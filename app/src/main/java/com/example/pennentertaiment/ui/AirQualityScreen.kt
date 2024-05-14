package com.example.pennentertaiment.ui.theme


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pennentertaiment.R
import com.example.pennentertaiment.model.City
import com.example.pennentertaiment.model.DailyForecast
import com.example.pennentertaiment.model.ForecastScreenData
import com.example.pennentertaiment.network.Resource
import com.example.pennentertaiment.ui.AirQualityScreenViewModel
import com.example.pennentertaiment.ui.DotsPulsing
import com.example.pennentertaiment.ui.utils.DAY
import com.example.pennentertaiment.utils.PM10
import com.example.pennentertaiment.utils.PM25
import com.example.pennentertaiment.utils.getMonthAndDay
import com.example.pennentertaiment.utils.o3
import com.google.accompanist.pager.HorizontalPagerIndicator
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun AirQualityScreen(
    viewModel: AirQualityScreenViewModel,
) {

    val airQualityData by viewModel.airQuality.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            when (airQualityData) {
                is Resource.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        DotsPulsing()
                    }
                }
                is Resource.Success -> {
                    SearchByCity(hint = stringResource(id = R.string.search_by_city), onValueChange = {
                        viewModel.getAirQualityByLocation(it)
                    })
                    ForecastContentByTabs(airQualityData.data)
                }
                is Resource.Error -> {
                    StationNotFound(airQualityData.message ?: stringResource(id = R.string.something_went_wrong)) {
                        viewModel.getAirQualityByGeolocation()
                    }
                }
            }
        }
    }
}

@Composable
fun StationNotFound(message: String, tryAgain: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.sad_dog),
            contentDescription = "something went wrong"
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = message,
            textAlign = TextAlign.Center,
            fontSize = 30.sp
        )
        Button(
            onClick = { tryAgain() }, modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(text = "Go back")
        }
    }
}

@Composable
fun SearchByCity(
    hint: String, onValueChange: (String) -> Unit
) {
    val searchByCityValue = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    TextField(
        value = searchByCityValue.value,
        onValueChange = { searchByCityValue.value = it },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent, RoundedCornerShape(5.dp)),
        placeholder = { Text(text = hint) },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            onValueChange(searchByCityValue.value)
            keyboardController?.hide()
            focusManager.clearFocus()
            searchByCityValue.value = ""
        }
        ))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ForecastContentByTabs(airQualityData: ForecastScreenData?) {
    val pagerState = rememberPagerState { 3 }
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf(DAY.YESTERDAY, DAY.TODAY, DAY.TOMORROW)
    val yesterday = airQualityData?.yesterday
    val today = airQualityData?.today
    val tomorrow = airQualityData?.tomorrow


    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                // Indicator for the selected tab
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = Color.Red
                )
            }
        ) {
            tabs.forEachIndexed { index, tabTitle ->
                Tab(
                    modifier = Modifier.padding(all = 16.dp),
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(
                                index
                            )
                        }
                    }
                ) {
                    Text(text = tabTitle.name)
                }
            }
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    pagerState.scrollToPage(
                        1
                    )
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            HorizontalPager(
                state = pagerState
            ) {
                val thisDay = when (it) {
                    0 -> yesterday
                    1 -> today
                    2 -> tomorrow
                    else -> {
                        today
                    }
                }
                DailyAirQualityScreen(
                    modifier = Modifier.align(Alignment.Center),
                    city = airQualityData?.city,
                    selected = thisDay
                )
            }

            HorizontalPagerIndicator(
                pagerState = pagerState, pageCount = 3, modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
            )

        }
    }
}

@Composable
fun DailyAirQualityScreen(
    modifier: Modifier,
    city: City?,
    selected: Map<String, DailyForecast?>?
) {
    Column(
        modifier = modifier
            .padding(top = 28.dp)
            .fillMaxHeight()
    ) {

        Text(
            text = selected?.values?.random()?.date?.getMonthAndDay() ?: "",
            fontSize = 30.sp,
            color = Color.Red,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Title("Station Name:")
        SubText(text = "${city?.name}")
        Title("Location (lat,lng):")
        SubText(text = "${city?.geo?.getOrNull(0) ?: 0.0}, ${city?.geo?.getOrNull(1) ?: 0.0}")
        Divider(modifier = Modifier.padding(16.dp))
        Title("Air Quality ")
        AirQualityTitle(title = PM25)
        AirQualityContent(airQualityText = selected?.get(PM25))
        AirQualityTitle(title = PM10)
        AirQualityContent(airQualityText = selected?.get(PM10))
        AirQualityTitle(title = "O3")
        AirQualityContent(airQualityText = selected?.get(o3))
    }
}

@Composable
fun Title(text: String) {
    Text(
        text = text,
        fontSize = 25.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SubText(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun AirQualityTitle(title: String) {
    Text(
        text = "$title:",
        fontSize = 20.sp,
        fontWeight = FontWeight.W700,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    )
}

@Composable
fun AirQualityContent(airQualityText: DailyForecast?) {
    Text(
        text = "Average: ${airQualityText?.average}",
        fontSize = 18.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    Text(
        text = "Max: ${airQualityText?.max}",
        fontSize = 18.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )

    Text(
        text = "Min: ${airQualityText?.min}",
        fontSize = 18.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}
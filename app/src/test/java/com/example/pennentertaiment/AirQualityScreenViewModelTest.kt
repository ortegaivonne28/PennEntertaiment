package com.example.pennentertaiment

import com.example.pennentertaiment.data.AirQualityRepository
import com.example.pennentertaiment.model.City
import com.example.pennentertaiment.model.Forecast
import com.example.pennentertaiment.model.ForecastData
import com.example.pennentertaiment.model.GetForecastResponse
import com.example.pennentertaiment.network.Resource
import com.example.pennentertaiment.ui.AirQualityScreenViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*


class AirQualityScreenViewModelTest {

    @MockK
    lateinit var repositoryMock: AirQualityRepository

    private lateinit var viewModel: AirQualityScreenViewModel

    val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `Get Air Quality By Geolocation Returns Loading`() = runTest {
        coEvery { repositoryMock.getAirQualityByLocation(0.0, 0.0) }
        viewModel = AirQualityScreenViewModel(repositoryMock)

        Assert.assertEquals(
            true,
            viewModel.airQuality.value is Resource.Loading
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Get Air Quality By Geolocation Returns Success`() = runTest {
        val success: Resource<GetForecastResponse> = Resource.Success(
            GetForecastResponse(
                ForecastData(
                    city = City("Austin"),
                    Forecast()
                )
            )
        )

        coEvery { repositoryMock.getAirQualityByLocation(0.0, 0.0) } returns success
        viewModel = AirQualityScreenViewModel(repositoryMock)
        viewModel.getAirQualityByGeolocation()

        advanceUntilIdle()
        val results = viewModel.airQuality.replayCache.last()

        Assert.assertEquals(
            true,
            results is Resource.Success
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Get Air Quality By Geolocation Returns Error`() = runTest {
        val success: Resource<GetForecastResponse> = Resource.Error("message")

        coEvery { repositoryMock.getAirQualityByLocation(0.0, 0.0) } returns success
        viewModel = AirQualityScreenViewModel(repositoryMock)
        viewModel.getAirQualityByGeolocation()

        advanceUntilIdle()
        val results = viewModel.airQuality.replayCache.last()

        Assert.assertEquals(
            true,
            results is Resource.Error
        )
    }

    @Test
    fun `Get Air Quality By City Returns Loading`() = runTest {
        val city = "Austin"
        coEvery { repositoryMock.getAirQualityByCity(city) }
        viewModel = AirQualityScreenViewModel(repositoryMock)

        Assert.assertEquals(
            true,
            viewModel.airQuality.value is Resource.Loading
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Get Air Quality By City Returns Success`() = runTest {
        val city = "Austin"
        val success: Resource<GetForecastResponse> = Resource.Success(
            GetForecastResponse(
                ForecastData(
                    city = City("Austin"),
                    Forecast()
                )
            )
        )

        coEvery { repositoryMock.getAirQualityByCity(city) } returns success
        viewModel = AirQualityScreenViewModel(repositoryMock)
        viewModel.getAirQualityByLocation(city)

        advanceUntilIdle()
        val results = viewModel.airQuality.replayCache.last()

        Assert.assertEquals(
            true,
            results is Resource.Success
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Get Air Quality By City Returns Error`() = runTest {
        val city = "Austin"
        val success: Resource<GetForecastResponse> = Resource.Error("message")

        coEvery { repositoryMock.getAirQualityByCity(city) } returns success
        viewModel = AirQualityScreenViewModel(repositoryMock)
        viewModel.getAirQualityByLocation(city)

        advanceUntilIdle()
        val results = viewModel.airQuality.replayCache.last()

        Assert.assertEquals(
            true,
            results is Resource.Error
        )
    }

}
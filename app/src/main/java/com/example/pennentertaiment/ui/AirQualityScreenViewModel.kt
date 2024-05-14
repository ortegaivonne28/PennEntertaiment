package com.example.pennentertaiment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennentertaiment.data.AirQualityRepository
import com.example.pennentertaiment.model.DailyForecast
import com.example.pennentertaiment.model.ForecastData
import com.example.pennentertaiment.model.ForecastData.Companion.mapForecastDataToScreenData
import com.example.pennentertaiment.model.ForecastScreenData
import com.example.pennentertaiment.network.Resource
import com.example.pennentertaiment.utils.PM10
import com.example.pennentertaiment.utils.PM25
import com.example.pennentertaiment.utils.o3
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AirQualityScreenViewModel @Inject constructor(private val repository: AirQualityRepository) :
    ViewModel() {

    var location: Pair<Double, Double> = Pair(0.0, 0.0)

    private val _airQuality = MutableStateFlow<Resource<ForecastScreenData>>(Resource.Loading())
    val airQuality: StateFlow<Resource<ForecastScreenData>> = _airQuality

    fun getAirQualityByGeolocation() {
        val lat = location.first
        val lng = location.second
        viewModelScope.launch {
            when (val resource = repository.getAirQualityByLocation(lat, lng)) {
                is Resource.Success -> {
                    resource.data?.forecastData?.let {
                        _airQuality.emit(Resource.Success(data = it.mapForecastDataToScreenData()))
                    }
                }
                is Resource.Loading -> {
                    _airQuality.emit(Resource.Loading())
                }
                is Resource.Error -> {
                    _airQuality.emit(Resource.Error(resource.message ?: "Something went wrong"))
                }
            }
        }
    }

    fun getAirQualityByLocation(city: String) {
        viewModelScope.launch {
            when (val resource = repository.getAirQualityByCity(city)) {
                is Resource.Success -> {
                    resource.data?.forecastData?.let {
                        _airQuality.emit(Resource.Success(data = it.mapForecastDataToScreenData()))
                    }
                }
                is Resource.Loading -> {
                    _airQuality.emit(Resource.Loading())
                }
                is Resource.Error -> {
                    _airQuality.emit(Resource.Error(resource.message ?: "Something went wrong"))
                }
            }
        }
    }
}
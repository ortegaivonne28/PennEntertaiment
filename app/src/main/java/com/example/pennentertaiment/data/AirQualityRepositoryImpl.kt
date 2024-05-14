package com.example.pennentertaiment.data

import com.example.pennentertaiment.model.GetForecastResponse
import com.example.pennentertaiment.network.ApiService
import com.example.pennentertaiment.network.Resource
import javax.inject.Inject

class AirQualityRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): AirQualityRepository, BaseRepo() {
    override suspend fun getAirQualityByLocation(lan: Double, lng: Double): Resource<GetForecastResponse> {
        return safeApiCall {
            apiService.getFeedByGeolocation(lan, lng)
        }
    }

    override suspend fun getAirQualityByCity(city: String): Resource<GetForecastResponse> {
        return safeApiCall {
            apiService.getFeedByLocation(city)
        }
    }
}
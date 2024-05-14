package com.example.pennentertaiment.data

import com.example.pennentertaiment.model.GetForecastResponse
import com.example.pennentertaiment.network.Resource

interface AirQualityRepository  {
    suspend fun getAirQualityByLocation(lan: Double, lng: Double): Resource<GetForecastResponse>
    suspend fun getAirQualityByCity(city: String): Resource<GetForecastResponse>
}
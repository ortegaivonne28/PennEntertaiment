package com.example.pennentertaiment.model

import com.google.gson.annotations.SerializedName

data class GetForecastResponse(
    @SerializedName("data")
    val forecastData: ForecastData? = null,
)
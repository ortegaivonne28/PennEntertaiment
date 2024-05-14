package com.example.pennentertaiment.model

data class ForecastScreenData(
    val city: City?,
    val today: Map<String,DailyForecast?>,
    val yesterday:Map<String,DailyForecast?>,
    val tomorrow: Map<String,DailyForecast?>
)
package com.example.pennentertaiment.model

import com.example.pennentertaiment.utils.PM10
import com.example.pennentertaiment.utils.PM25
import com.example.pennentertaiment.utils.o3
import com.google.gson.annotations.SerializedName
import java.time.LocalDate


data class ForecastData(
    @SerializedName("city")
    val city: City? = null,
    @SerializedName("forecast")
    val forecast: Forecast? = null,
    @SerializedName("aqi")
    val aqi: Int? = null,

    ) {
    companion object {
        fun ForecastData.mapForecastDataToScreenData(): ForecastScreenData {
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)
            val tomorrow = today.plusDays(1)

            return ForecastScreenData(
                this.city,
                today = mapOf(
                    Pair(
                        PM25,
                        this.forecast?.forecastContent?.dailyForecastPM25?.getForecastDataForDay(
                            today.toString()
                        )
                    ),
                    Pair(
                        PM10,
                        this.forecast?.forecastContent?.dailyForecastPM10?.getForecastDataForDay(
                            today.toString()
                        )
                    ),
                    Pair(
                        o3,
                        this.forecast?.forecastContent?.dailyForecastO3?.getForecastDataForDay(today.toString())
                    )
                ),
                yesterday = mapOf(
                    Pair(
                        PM25,
                        this.forecast?.forecastContent?.dailyForecastPM25?.getForecastDataForDay(
                            yesterday.toString()
                        )
                    ),
                    Pair(
                        PM10,
                        this.forecast?.forecastContent?.dailyForecastPM10?.getForecastDataForDay(
                            yesterday.toString()
                        )
                    ),
                    Pair(
                        o3,
                        this.forecast?.forecastContent?.dailyForecastO3?.getForecastDataForDay(
                            yesterday.toString()
                        )
                    )
                ),
                tomorrow = mapOf(
                    Pair(
                        PM25,
                        this.forecast?.forecastContent?.dailyForecastPM25?.getForecastDataForDay(
                            tomorrow.toString()
                        )
                    ),
                    Pair(
                        PM10,
                        this.forecast?.forecastContent?.dailyForecastPM10?.getForecastDataForDay(
                            tomorrow.toString()
                        )
                    ),
                    Pair(
                        o3,
                        this.forecast?.forecastContent?.dailyForecastO3?.getForecastDataForDay(
                            tomorrow.toString()
                        )
                    )
                )
            )
        }

        fun List<DailyForecast?>.getForecastDataForDay(day: String): DailyForecast? {
            return this.find { it?.date == day }
        }
    }
}

data class City(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("geo")
    val geo: List<Double>? = null
)


data class Forecast(
    @SerializedName("daily")
    val forecastContent: ForecastContent? = null,
)

data class ForecastContent(
    @SerializedName("pm25")
    val dailyForecastPM25: List<DailyForecast>? = null,
    @SerializedName("o3")
    val dailyForecastO3: List<DailyForecast>? = null,
    @SerializedName("pm10")
    val dailyForecastPM10: List<DailyForecast>? = null,
)

data class DailyForecast(
    @SerializedName("avg")
    val average: Int? = null,
    @SerializedName("day")
    val date: String? = null,
    @SerializedName("max")
    val max: Int? = null,
    @SerializedName("min")
    val min: Int? = null
)
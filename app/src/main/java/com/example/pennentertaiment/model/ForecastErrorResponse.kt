package com.example.pennentertaiment.model

import com.google.gson.annotations.SerializedName

data class ForecastErrorResponse(
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("status")
    val status: String? = null
)
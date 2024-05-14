package com.example.pennentertaiment.network


import com.example.pennentertaiment.model.GetForecastResponse
import com.example.pennentertaiment.utils.TOKEN


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    @GET("/feed/geo:{lat};{lng}/")
    suspend fun getFeedByGeolocation(
        @Path ("lat") lat: Double,
        @Path ("lng") lng: Double,
        @Query ("token") token: String = TOKEN,
    ): Response<GetForecastResponse>

    @GET("/feed/{city}/")
    suspend fun getFeedByLocation(
        @Path ("city") city: String,
        @Query ("token") token: String = TOKEN,
    ): Response<GetForecastResponse>

}
package com.example.pennentertaiment.di

import com.example.pennentertaiment.data.AirQualityRepository
import com.example.pennentertaiment.data.AirQualityRepositoryImpl
import com.example.pennentertaiment.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun bindAirQualityRepository(apiService: ApiService): AirQualityRepository {
        return AirQualityRepositoryImpl(apiService)
    }

}
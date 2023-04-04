package com.nitish.myweatherapp.domain.repository

import com.nitish.myweatherapp.data.network.model.ApiResponse
import com.nitish.myweatherapp.data.network.model.GeocodeResponseItem
import retrofit2.Response

interface WeatherRepository {

    suspend fun getWeather(
        cityName: String,
        appId: String
    ): Response<ApiResponse>

    suspend fun getCityName(
        latitude: Double,
        longitude: Double,
        limit: Int,
        appId: String
    ): Response<List<GeocodeResponseItem>>

}
package com.nitish.myweatherapp.domain.repository

import com.nitish.myweatherapp.data.network.client.WeatherApiClient
import com.nitish.myweatherapp.data.network.model.ApiResponse
import com.nitish.myweatherapp.data.network.model.GeocodeResponseItem
import retrofit2.Response

class WeatherRepositoryImpl(
    private val apiClient: WeatherApiClient
) : WeatherRepository {

    override suspend fun getWeather(cityName: String, appId: String): Response<ApiResponse> {
        return apiClient.getWeather(cityName = cityName, appId = appId)
    }

    override suspend fun getCityName(
        latitude: Double,
        longitude: Double,
        limit: Int,
        appId: String
    ): Response<List<GeocodeResponseItem>> {
        return apiClient.getCityName(
            latitude = latitude,
            longitude = longitude,
            limit = limit,
            appId = appId
        )
    }
}

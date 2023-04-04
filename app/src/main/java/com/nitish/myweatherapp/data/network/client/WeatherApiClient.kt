package com.nitish.myweatherapp.data.network.client

import com.nitish.myweatherapp.data.network.model.ApiResponse
import com.nitish.myweatherapp.data.network.model.GeocodeResponseItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiClient {

    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("q") cityName: String,
        @Query("appId") appId: String
    ): Response<ApiResponse>

    @GET("geo/1.0/reverse")
    suspend fun getCityName(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("limit") limit: Int,
        @Query("appId") appId: String
    ) : Response<List<GeocodeResponseItem>>
}
package com.nitish.myweatherapp.domain

import com.nitish.myweatherapp.data.network.model.WeatherItem

data class WeatherUiItem(
    val countryName: String,
    val temperature: String,
    val pressure: Int,
    val humidity: Int,
    val minTemp: Double,
    val maxTemp: Double,
    val clouds: Int,
    val rainLastHr: Double,
    val feelsLike: String,
    val weatherItems: List<WeatherItem?>? = emptyList()
)

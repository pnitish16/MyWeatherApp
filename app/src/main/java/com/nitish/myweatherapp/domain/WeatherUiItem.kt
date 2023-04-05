package com.nitish.myweatherapp.domain

data class WeatherUiItem(
    val city: String,
    val cityName: String,
    val temperature: String,
    val pressure: Int,
    val humidity: String,
    val minTemp: Int,
    val maxTemp: Int,
    val clouds: Int,
    val feelsLike: String,
    val weatherMain: String,
    val weatherId: Int,
    val weatherDesc: String,
    val weatherIcon: String
)

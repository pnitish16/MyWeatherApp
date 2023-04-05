package com.nitish.myweatherapp.data.network.mapper

import com.nitish.myweatherapp.data.network.model.ApiResponse
import com.nitish.myweatherapp.domain.WeatherUiItem

interface ApiMapper {

  fun mapApiUserItemToDomain(weatherDataItem: ApiResponse): WeatherUiItem
}
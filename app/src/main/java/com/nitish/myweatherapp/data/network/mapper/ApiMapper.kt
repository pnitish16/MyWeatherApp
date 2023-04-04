package com.nitish.myweatherapp.data.network.mapper

import com.nitish.myweatherapp.data.network.model.WeatherDataItem
import com.nitish.myweatherapp.domain.WeatherUiItem

interface ApiMapper {

  fun mapApiUserItemToDomain(weatherDataItem: WeatherDataItem): WeatherUiItem
}
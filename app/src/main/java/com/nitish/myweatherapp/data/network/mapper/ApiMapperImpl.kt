package com.nitish.myweatherapp.data.network.mapper

import com.nitish.myweatherapp.data.network.model.WeatherDataItem
import com.nitish.myweatherapp.data.network.model.WeatherItem
import com.nitish.myweatherapp.domain.WeatherUiItem


class ApiMapperImpl : ApiMapper {
    override fun mapApiUserItemToDomain(weatherDataItem: WeatherDataItem): WeatherUiItem {
        with(weatherDataItem) {

            val temperature = "${this.main?.temp ?: 0.0}\u00B0"
            return WeatherUiItem(
                this.sys?.country ?: "",
                temperature, this.main?.pressure ?: 0,
                this.main?.humidity ?: 0, this.main?.tempMin ?: 0.0,
                this.main?.tempMax ?: 0.0, this.clouds?.all ?: 0,
                this.rain?.oneHour ?: 0.0, this.main?.feelsLike ?: "",
                this.weather ?: emptyList<WeatherItem>()
            )
        }
    }

}
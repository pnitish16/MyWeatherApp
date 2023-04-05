package com.nitish.myweatherapp.data.network.mapper

import android.util.Log
import com.nitish.myweatherapp.data.network.model.ApiResponse
import com.nitish.myweatherapp.domain.WeatherUiItem
import com.nitish.myweatherapp.utils.Constants
import kotlin.math.ceil

class ApiMapperImpl : ApiMapper {
    override fun mapApiUserItemToDomain(weatherDataItem: ApiResponse): WeatherUiItem {
        with(weatherDataItem) {

            // temp parsing
            val tempCel = main?.temp ?: 0.0
            val temperature = "${ceil(tempCel).toInt()}\u00B0C"

            var weatherMain = ""
            var weatherId = 0
            var weatherDesc = ""
            var weatherIconPath = ""
            //weather details parsing
            if (weather?.isNotEmpty() == true) {
                weatherMain = weather.first()?.main ?: ""
                weatherId = weather.first()?.id ?: 0
                weatherDesc = weather.first()?.description ?: ""
                val weatherIcon = weather.first()?.icon ?: ""
                weatherIconPath = Constants.OPEN_WEATHER_IMAGE_BASE + weatherIcon + "@2x.png"
            }

            val feelsLikeValue = ceil(this.main?.feels_like?:0.0)
            Log.d("feelsLike", "$feelsLikeValue")
            val feelsLike = "Feels like ${feelsLikeValue.toInt()}"


            return WeatherUiItem(
                city = name ?: "",
                cityName = "${name ?: ""},${sys?.country ?: ""}",
                temperature = temperature,
                pressure = main?.pressure ?: 0,
                humidity = "Humidity : ${main?.humidity ?: 0}",
                minTemp = ceil(main?.temp_min ?: 0.0).toInt(),
                maxTemp = ceil(main?.tempMax ?: 0.0).toInt(),
                clouds = clouds?.all ?: 0,
                feelsLike = feelsLike,
                weatherMain = weatherMain,
                weatherId = weatherId,
                weatherDesc = weatherDesc,
                weatherIcon = weatherIconPath
            )
        }
    }

}
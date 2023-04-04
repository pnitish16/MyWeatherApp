package com.nitish.myweatherapp.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nitish.myweatherapp.data.network.mapper.ApiMapper
import com.nitish.myweatherapp.data.network.mapper.ApiMapperImpl
import com.nitish.myweatherapp.data.network.model.ApiResponse
import com.nitish.myweatherapp.domain.WeatherUiItem
import com.nitish.myweatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val apiMapper: ApiMapper
) : ViewModel() {

    val isLoading: LiveData<Boolean> get() = _isLoading
    private var _isLoading = MutableLiveData(false)

    private val _weatherUiItem =
        MutableSharedFlow<Result<WeatherUiItem>>(replay = 5)
    val weatherUiItem = _weatherUiItem.asSharedFlow()

    private val _cityNameResponse =
        MutableSharedFlow<Result<String>>()
    val cityNameResponse = _cityNameResponse.asSharedFlow()

    // api call for the getting the weather for the cityName
    fun getWeather(
        cityName: String,
        appId: String
    ) = viewModelScope.launch {
        repository.getWeather(cityName, appId).let { response ->
            if (response.isSuccessful) {
                response.body()?.let {
                    if (it.list?.isNotEmpty() == true) {
                        val weatherItem = it.list.first()
                        _weatherUiItem.emit(
                            Result.success(
                                apiMapper.mapApiUserItemToDomain(
                                    weatherItem!!
                                )
                            )
                        )
                    }
                }
            } else {
                _weatherUiItem.emit(
                    Result.failure(
                        java.lang.Exception(
                            response.body()?.message ?: "No Data found"
                        )
                    )
                )
            }
        }
    }

    // getting the cityName from the latitude and longitude of the user
    fun getCityName(
        latitude: Double,
        longitude: Double,
        limit: Int,
        appId: String
    ) = viewModelScope.launch {
        repository.getCityName(latitude, longitude, limit, appId).let { response ->
            if (response.isSuccessful) {
                response.body()?.let { geocodeResponse ->
                    if (geocodeResponse.isNotEmpty()) {
                        val cityName = geocodeResponse.first().name ?: ""
                        _cityNameResponse.emit(Result.success(cityName))
                    }
                }
            } else {
                _cityNameResponse.emit(Result.failure(java.lang.Exception("No data found")))
            }
        }
    }
}
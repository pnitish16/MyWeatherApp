package com.nitish.myweatherapp.ui.home.viewmodel

import android.content.Context
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
import com.nitish.myweatherapp.utils.ConnectivityUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val apiMapper: ApiMapper,
    private val context: Context
) : ViewModel() {

    val isLoading: LiveData<Boolean> get() = _isLoading
    private var _isLoading = MutableLiveData(false)

    private val _weatherUiItem =
        MutableSharedFlow<Result<WeatherUiItem>>()
    val weatherUiItem = _weatherUiItem.asSharedFlow()

    private val _cityNameResponse =
        MutableSharedFlow<Result<String>>()
    val cityNameResponse = _cityNameResponse.asSharedFlow()

    // api call for the getting the weather for the cityName
    fun getWeather(
        cityName: String,
        appId: String
    ) =
        viewModelScope.launch(exceptionHandler) {
            _isLoading.value = true
            repository.getWeather(cityName, appId).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.weather?.isNotEmpty() == true) {
                            _weatherUiItem.emit(
                                Result.success(
                                    apiMapper.mapApiUserItemToDomain(
                                        it
                                    )
                                )
                            )
                        }
                    }
                } else {
                    _weatherUiItem.emit(
                        Result.failure(
                            Exception(response.message()?:"No data found")
                        )
                    )
                }
            }
            _isLoading.value = false


        }

    // Operator for searching after delay of 500 ms
    private var searchJob: Job? = null
    fun searchDebounced(
        cityName: String,
        appId: String
    ) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            getWeather(cityName, appId)
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

    // due to brevity of time using in the same viewmodel instead of base class
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("exception", "exceptionHandler *== " + exception.cause)
        _isLoading.value = false
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (ConnectivityUtils.isConnectedToInternet(context)) {
                    _weatherUiItem.emit(
                        Result.failure(
                            java.lang.Exception(
                                "no internet connection"
                            )
                        )
                    )
                } else {
                    _weatherUiItem.emit(
                        Result.failure(
                            java.lang.Exception(
                                "no internet connection"
                            )
                        )
                    )
                }
            }
        }
    }
}
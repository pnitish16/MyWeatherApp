package com.nitish.myweatherapp.ui.home

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import com.nitish.myweatherapp.R
import com.nitish.myweatherapp.databinding.ActivityMainBinding
import com.nitish.myweatherapp.ui.home.viewmodel.WeatherViewModel
import com.nitish.myweatherapp.utils.Constants
import com.nitish.myweatherapp.utils.GPSLocationHelper
import com.nitish.myweatherapp.utils.widgets.Extensions.hideKeyboard
import com.nitish.myweatherapp.utils.widgets.SearchEditText
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    companion object {
        const val SPLASH_DISPLAY_LENGTH = 4000L
    }

    private val weatherViewModel by viewModel<WeatherViewModel>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        Handler(mainLooper).postDelayed({
            getGPSLocation()
        }, SPLASH_DISPLAY_LENGTH)


        binding.etSearchCity.setQueryTextChangeListener(object :
            SearchEditText.QueryTextListener {
            override fun onQueryTextSubmit(query: String?) {
                query?.let {
                    if (it.length > 2) {
                        weatherViewModel.getWeather(
                            query.toString(),
                            Constants.OPEN_WEATHER_API_KEY
                        )
                    }
                }
                hideKeyboard(binding.errorText)
            }

            override fun onQueryTextChange(newText: String?) {
                newText?.let {
                    weatherViewModel.getWeather(
                        it,
                        Constants.OPEN_WEATHER_API_KEY
                    )
                }
            }
        })
    }


    private fun setupObservers() {

        lifecycleScope.launch {
            weatherViewModel.weatherUiItem.collect {
                if (it.isSuccess) {
                    it.let {
                        it.getOrNull()?.let { weatherItem ->
                           binding.weatherItem = weatherItem
                            Log.d("WeatherItem", weatherItem.temperature)
                        }
                    }
                } else {
                    Log.d("ApiError", it.exceptionOrNull()?.message ?: "error")
                }
            }
        }

        lifecycleScope.launch {
            weatherViewModel.cityNameResponse.collect {
                it.getOrNull()?.let { cityName ->
                    Log.d("CityName", cityName)
                    weatherViewModel.getWeather(cityName, Constants.OPEN_WEATHER_API_KEY)
                }
            }
        }
    }


    private fun getGPSLocation() {
        Log.d("Location", "getGPSLocation")

        Dexter.withContext(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    GPSLocationHelper(this@MainActivity, false) {
                        Log.d("Location ", "getGPSLocation $it")
                        weatherViewModel.getCityName(
                            latitude = it?.latitude ?: 0.0,
                            longitude = it?.longitude ?: 0.0,
                            Constants.GEOCODE_LIMIT,
                            Constants.OPEN_WEATHER_API_KEY
                        )
                    }
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) { /* ... */
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: com.karumi.dexter.listener.PermissionRequest?,
                    p1: PermissionToken?
                ) {

                }
            }).check()
    }
}
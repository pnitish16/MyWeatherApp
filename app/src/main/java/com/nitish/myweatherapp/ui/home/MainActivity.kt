package com.nitish.myweatherapp.ui.home

import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
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
import com.nitish.myweatherapp.utils.SharedPrefsUtils
import com.nitish.myweatherapp.utils.widgets.Extensions.hideKeyboard
import com.nitish.myweatherapp.utils.widgets.SearchEditText
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.KoinJavaComponent.inject

class MainActivity : AppCompatActivity() {

    companion object {
        const val SPLASH_DISPLAY_LENGTH = 2000L
    }

    //injecting the viewmodel and databinding for the activity
    private val weatherViewModel by viewModel<WeatherViewModel>()
    private lateinit var binding: ActivityMainBinding
    private val sharedPrefsUtils: SharedPrefsUtils by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupViews()
        setupObservers()
    }


    // setup views for the activity
    private fun setupViews() {
        Handler(mainLooper).postDelayed({

            //checking if previously saved city weather
            sharedPrefsUtils.getString(Constants.SAVED_CITY, "")?.let {
                if (it.isNotEmpty()) {
                    weatherViewModel.getWeather(
                        it,
                        Constants.OPEN_WEATHER_API_KEY
                    )
                } else {
                    getGPSLocation()
                }
            }
        }, SPLASH_DISPLAY_LENGTH)


        // search city edittext query callback
        binding.etSearchCity.setQueryTextChangeListener(object :
            SearchEditText.QueryTextListener {
            override fun onQueryTextSubmit(query: String?) {
                query?.let {
                    if (it.length > 2) {
                        weatherViewModel.searchDebounced(
                            query.toString(),
                            Constants.OPEN_WEATHER_API_KEY
                        )
                    }
                }
                hideKeyboard(binding.errorText)
            }

            override fun onQueryTextChange(newText: String?) {
                newText?.let {
                    if (newText.isEmpty()) {
                        binding.weatherItem = null
                        binding.ivWeatherIcon.setImageResource(0)
                    } else {
                        if (newText.length > 2)
                            weatherViewModel.searchDebounced(
                                it,
                                Constants.OPEN_WEATHER_API_KEY
                            )
                    }
                }
            }
        })


        binding.ivGpsLocation.setOnClickListener {
            getGPSLocation()
        }
    }

    // observers for the data from the viewmodel
    private fun setupObservers() {

        lifecycleScope.launch {
            weatherViewModel.weatherUiItem.collect {
                if (it.isSuccess) {
                    it.let {
                        it.getOrNull()?.let { weatherItem ->
                            binding.apply {
                                this.weatherItem = weatherItem
                                errorText.visibility = View.GONE
                            }
                            sharedPrefsUtils.saveString(Constants.SAVED_CITY, weatherItem.cityName)
                        }
                    }
                } else {
                    binding.apply {
                        weatherItem = null
                        ivWeatherIcon.setImageResource(0)
                        errorText.visibility = View.VISIBLE
                    }
                }
            }
        }

        lifecycleScope.launch {
            weatherViewModel.cityNameResponse.collect {
                if (it.isSuccess) {
                    it.getOrNull()?.let { cityName ->
                        weatherViewModel.searchDebounced(cityName, Constants.OPEN_WEATHER_API_KEY)
                    }
                } else {
                    binding.apply {
                        weatherItem = null
                        ivWeatherIcon.setImageResource(0)
                        errorText.visibility = View.VISIBLE
                    }
                }
            }
        }

        weatherViewModel.isLoading.observe(this) {
            binding.loader.isVisible = it
        }
    }


    //Permission call to get the location of the user
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
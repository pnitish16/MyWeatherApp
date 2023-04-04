package com.nitish.myweatherapp.di

import com.nitish.myweatherapp.ui.home.viewmodel.WeatherViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module  {
    viewModel { WeatherViewModel(get(), get()) }
}

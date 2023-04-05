package com.nitish.myweatherapp.application

import android.app.Application
import com.nitish.myweatherapp.di.applicationModule
import com.nitish.myweatherapp.di.preferencesModule
import com.nitish.myweatherapp.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class WeatherApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    // initialising koin for dependency injection
    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@WeatherApplication)
            modules(listOf(applicationModule, presentationModule, preferencesModule))
        }
    }


}
package com.nitish.myweatherapp.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.common.util.SharedPreferencesUtils
import com.nitish.myweatherapp.utils.Constants.APP_NAME
import com.nitish.myweatherapp.utils.SharedPrefsUtils
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val preferencesModule = module {
    single { provideSettingsPreferences(androidApplication()) }
    single {
        SharedPrefsUtils(get())
    }
}

private fun provideSettingsPreferences(app: Application): SharedPreferences =
    app.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)



package com.nitish.myweatherapp.di

import com.nitish.myweatherapp.BuildConfig
import com.nitish.myweatherapp.data.network.client.WeatherApiClient
import com.nitish.myweatherapp.data.network.mapper.ApiMapper
import com.nitish.myweatherapp.data.network.mapper.ApiMapperImpl
import com.nitish.myweatherapp.domain.repository.WeatherRepository
import com.nitish.myweatherapp.domain.repository.WeatherRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL_NAME = "BASE_URL"
private const val BASE_URL = "https://api.openweathermap.org/"
private const val MAIN_DISPATCHER = "main_dispatcher"
private const val BACKGROUND_DISPATCHER = "background_dispatcher"

val applicationModule = module {
    single(named(BASE_URL_NAME)) {
        BASE_URL
    }

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single {
        val client = OkHttpClient().newBuilder()

        if (BuildConfig.DEBUG) {
            client.addInterceptor(get<HttpLoggingInterceptor>())
        }

        client.build()
    }

    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(get<String>(named(BASE_URL_NAME)))
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .client(get())
            .build()
    }

    single {
        get<Retrofit>().create(WeatherApiClient::class.java)
    }

    single<ApiMapper> {
        ApiMapperImpl()
    }

    single<WeatherRepository> {
        WeatherRepositoryImpl(
            get(),
        )
    }

    single { Picasso.get() }

    single(named(MAIN_DISPATCHER)) { Dispatchers.Main }

    single(named(BACKGROUND_DISPATCHER)) { Dispatchers.IO }

}
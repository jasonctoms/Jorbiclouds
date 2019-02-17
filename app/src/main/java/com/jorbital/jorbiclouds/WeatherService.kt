package com.jorbital.jorbiclouds

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherService {
    @GET("Search")
    fun searchLocationsAsync(
        @Query("q") q: String,
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("accuracy") accuracy: Double?,
        @Query("language") language: String
    ): Deferred<Response<LocationQuery>>

    @GET("{id}/forecast")
    fun getForecastAsync(
        @Path("id") id: String
    ): Deferred<Response<YrForecast>>
}

object WeatherFactory {
    private const val BASE_URL = "NOT IN SOURCE CONTROL"

    fun makeWeatherService(): WeatherService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build().create(WeatherService::class.java)
    }
}
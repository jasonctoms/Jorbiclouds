package com.jorbital.jorbiclouds

import com.squareup.moshi.Json
import java.util.*

data class LocationQuery(
    val totalResults: Int,
    val _embedded: ListOfLocations
)

data class ListOfLocations(
    val location: List<YrLocation>
)

data class YrLocation(
    val id: String,
    val name: String
)

data class YrForecast(
    val created: String,
    val update: String,
    val shortIntervals: List<YrInterval>,
    val longIntervals: List<YrInterval>
)

data class YrInterval(
    val symbol: YrSymbol,
    val precipitation: YrPrecipitation,
    val temperature: YrTemperature,
    val wind: YrWind,
    val pressure: YrPressure,
    val cloudCover: YrCloudCover,
    val humidity: YrHumidity,
    val dewPoint: YrDewPoint,
    val start: String,
    val end: String
)

data class YrSymbol(
    val n: Int,
    @field:Json(name = "var") val variant: String,
    val sunup: Boolean
)

data class YrPrecipitation(
    val min: Double,
    val max: Double,
    val value: Double
)

data class YrTemperature(
    val value: Double
)

data class YrWind(
    val direction: Int,
    val gust: Double,
    val speed: Double,
    val areaMaxSpeed: Double
)

data class YrPressure(
    val value: Int
)

data class YrCloudCover(
    val value: Int,
    val high: Int,
    val middle: Int,
    val low: Int,
    val fog: Int
)

data class YrHumidity(
    val value: Double
)

data class YrDewPoint(
    val value: Double
)

data class JorbicloudsDay(
    val date: String,
    val weatherEntries: MutableList<WeatherEntry>
)

data class WeatherEntry(
    val timeSpan: String,
    val icon: String,
    val temp: Double,
    val minPrecip: Double,
    val maxPrecip: Double,
    val windSpeed: Double,
    val windDirection: Int
)
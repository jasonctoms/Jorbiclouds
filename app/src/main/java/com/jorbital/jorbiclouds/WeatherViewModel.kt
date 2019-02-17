package com.jorbital.jorbiclouds

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class WeatherViewModel : ViewModel() {
    private lateinit var service: WeatherService

    private val selectedLocation: MutableLiveData<YrLocation> by lazy {
        MutableLiveData<YrLocation>().also {
            getLocation("", 59.91273, 10.74609, 1000.0, "en")
        }
    }

    private val listOfLocations: MutableLiveData<List<YrLocation>> = MutableLiveData()

    private val jorbicloudsDays: MutableLiveData<List<JorbicloudsDay>> = MutableLiveData()

    fun init() {
        service = WeatherFactory.makeWeatherService()
    }

    fun getSelectedLocation(): LiveData<YrLocation> {
        return selectedLocation
    }

    fun getLocation(q: String, lat: Double?, lon: Double?, accuracy: Double?, language: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val request = service.searchLocationsAsync(q, lat, lon, accuracy, language)
            val response = request.await()
            val result = response.body()
            if (result != null) {
                selectedLocation.postValue(result._embedded.location[0])
            }
        }
    }

    fun getListOfLocations(): LiveData<List<YrLocation>> {
        return listOfLocations
    }

    fun searchLocation(q: String, lat: Double?, lon: Double?, accuracy: Double?, language: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val request = service.searchLocationsAsync(q, lat, lon, accuracy, language)
            val response = request.await()
            val result = response.body()
            if (result != null) {
                listOfLocations.postValue(result._embedded.location)
            }
        }
    }

    fun getJorbicloudsDays(): LiveData<List<JorbicloudsDay>> {
        return jorbicloudsDays
    }

    fun getForecast(locationId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val request = service.getForecastAsync(locationId)
            val response = request.await()
            val result = response.body()
            if (result != null) {
                //this is a horrible terrible way to separate out the data into days, but
                //my brain refuses to think of the elegant solution today...
                val formatter = DateTimeFormatter.ofPattern("yyyy MMM dd")
                val todayDay = LocalDateTime.now().dayOfYear
                val today = JorbicloudsDay(LocalDateTime.now().format(formatter), mutableListOf())
                val tomorrowDay = LocalDateTime.now().plusDays(1).dayOfYear
                val tomorrow = JorbicloudsDay(LocalDateTime.now().plusDays(1).format(formatter), mutableListOf())
                val twoDaysFromNowDay = LocalDateTime.now().plusDays(2).dayOfYear
                val twoDaysFromNow = JorbicloudsDay(LocalDateTime.now().plusDays(2).format(formatter), mutableListOf())
                val threeDaysFromNowDay = LocalDateTime.now().plusDays(3).dayOfYear
                val threeDaysFromNow = JorbicloudsDay(LocalDateTime.now().plusDays(3).format(formatter), mutableListOf())
                result.longIntervals.forEach {
                    val start = ZonedDateTime.parse(it.start)
                    val end = ZonedDateTime.parse(it.end)
                    val entry = createEntry(start, end, it)
                    when {
                        start.dayOfYear == todayDay -> today.weatherEntries.add(entry)
                        start.dayOfYear == tomorrowDay -> tomorrow.weatherEntries.add(entry)
                        start.dayOfYear == twoDaysFromNowDay -> twoDaysFromNow.weatherEntries.add(entry)
                        start.dayOfYear == threeDaysFromNowDay -> threeDaysFromNow.weatherEntries.add(entry)
                    }
                }
                val days = listOf(today, tomorrow, twoDaysFromNow, threeDaysFromNow)
                jorbicloudsDays.postValue(days)
            }
        }
    }

    private fun createEntry(start: ZonedDateTime, end: ZonedDateTime, interval: YrInterval): WeatherEntry {
        val timeSpan = DecimalFormat("00").format(start.hour) + " - " + DecimalFormat("00").format(end.hour)
        var iconNumber = DecimalFormat("00").format(interval.symbol.n)
        if (interval.symbol.variant != null && interval.symbol.variant != "None") {
            iconNumber += when {
                interval.symbol.variant == "Sun" -> "d"
                interval.symbol.variant == "Moon" -> "n"
                else -> "m"
            }
        }
        return WeatherEntry(
            timeSpan,
            iconNumber,
            interval.temperature.value,
            interval.precipitation.min,
            interval.precipitation.max,
            interval.wind.speed,
            interval.wind.direction
        )
    }

}
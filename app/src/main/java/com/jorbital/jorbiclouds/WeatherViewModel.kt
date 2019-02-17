package com.jorbital.jorbiclouds

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private lateinit var service: WeatherService

    private val selectedLocation: MutableLiveData<YrLocation> by lazy {
        MutableLiveData<YrLocation>().also {
            getLocation("", 59.91273, 10.74609, 1000.0, "en")
        }
    }

    private val listOfLocations: MutableLiveData<List<YrLocation>> = MutableLiveData()

    fun init(){
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

    fun getListOfLocations(): LiveData<List<YrLocation>>{
        return listOfLocations
    }

    fun searchLocation(q: String, lat: Double?, lon: Double?, accuracy: Double?, language: String){
        GlobalScope.launch(Dispatchers.Main) {
            val request = service.searchLocationsAsync(q, lat, lon, accuracy, language)
            val response = request.await()
            val result = response.body()
            if (result != null) {
                listOfLocations.postValue(result._embedded.location)
            }
        }
    }

}
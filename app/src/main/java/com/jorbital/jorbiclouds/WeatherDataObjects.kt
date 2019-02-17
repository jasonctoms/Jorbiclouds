package com.jorbital.jorbiclouds

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
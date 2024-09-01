package com.luisreyes.weatherapp.data.repository.network.request

data class WeatherRequest (
    val lat: Double,
    val lon: Double,
    val apikey: String
)
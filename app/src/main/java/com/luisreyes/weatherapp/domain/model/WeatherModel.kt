package com.luisreyes.weatherapp.domain.model

data class WeatherModel(
    val cityName: String,
    val temperature: Double,
    val weatherDescription: String,
    val latitude: Double,
    val longitude: Double
)

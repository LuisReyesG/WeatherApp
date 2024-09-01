package com.luisreyes.weatherapp.domain.repository

import com.luisreyes.weatherapp.domain.model.WeatherModel

interface WeatherRepository {
    suspend fun getWeatherByCity(lat: Double, lon: Double, apikey: String): WeatherModel
}
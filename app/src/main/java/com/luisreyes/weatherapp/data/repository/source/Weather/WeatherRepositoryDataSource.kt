package com.luisreyes.weatherapp.data.repository.source.Weather

import com.luisreyes.weatherapp.domain.model.WeatherModel

interface WeatherRepositoryDataSource {
    suspend fun getWeatherByCity(lat: Double, lon: Double, apikey: String): WeatherModel
}
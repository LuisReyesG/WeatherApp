package com.luisreyes.weatherapp.data.repository.source.Weather

import com.luisreyes.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherDataSource: WeatherRepositoryDataSource
): WeatherRepository {
    override suspend fun getWeatherByCity(lat: Double, lon: Double, apikey: String) = weatherDataSource.getWeatherByCity(lat, lon, apikey)
}
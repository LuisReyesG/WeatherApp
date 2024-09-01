package com.luisreyes.weatherapp.domain.usecase

import com.luisreyes.weatherapp.domain.model.WeatherModel
import com.luisreyes.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double, apikey: String): WeatherModel {
      return repository.getWeatherByCity(lat, lon, apikey)
    }
}
package com.luisreyes.weatherapp.data.repository.source.Weather

import com.luisreyes.weatherapp.data.local.dao.CityWeatherDao
import com.luisreyes.weatherapp.data.mappers.toDomainModel
import com.luisreyes.weatherapp.data.mappers.toEntity
import com.luisreyes.weatherapp.data.repository.network.apis.WeatherApi
import com.luisreyes.weatherapp.data.repository.network.request.WeatherRequest
import com.luisreyes.weatherapp.domain.model.WeatherModel
import com.luisreyes.weatherapp.framework.utils.NoInternetException

class WeatherDataSourceImpl(
    private val weatherApi: WeatherApi,
    private val cityWeatherDao: CityWeatherDao,
): WeatherRepositoryDataSource {

    override suspend fun getWeatherByCity(lat: Double, lon: Double, apikey: String): WeatherModel {
        return try {
            val response = weatherApi.getWeatherByCity(lat, lon, apikey)
            val weatherModel = response.body()?.toDomainModel() ?: throw Exception("Error fetching weather data")
            cityWeatherDao.insertCityWeather(weatherModel.toEntity())
            weatherModel
        } catch (e: Exception) {
            cityWeatherDao.getCityWeather("WeatherRequest")?.toDomainModel() ?: throw NoInternetException("No internet connection and city not found in local database") // Devuelve el dato cacheado o lanza la excepción
        }
    }
}
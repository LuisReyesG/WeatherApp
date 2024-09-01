package com.luisreyes.weatherapp.data.mappers

import com.luisreyes.weatherapp.data.local.entities.CityWeatherEntity
import com.luisreyes.weatherapp.data.repository.network.responses.Weather.WeatherResponse
import com.luisreyes.weatherapp.domain.model.WeatherModel

fun WeatherResponse.toDomainModel(): WeatherModel {
    return WeatherModel(
        cityName = this.name,
        temperature = this.main.temp,
        weatherDescription = this.weather.firstOrNull()?.description ?: "No description",
        latitude = this.coord.lat,
        longitude = this.coord.lon
    )
}

fun CityWeatherEntity.toDomainModel(): WeatherModel {
    return WeatherModel(
        cityName = this.cityName,
        temperature = this.temperature,
        weatherDescription = this.description,
        latitude = 0.0, // Assuming you might want to store lat/lon in your DB later
        longitude = 0.0
    )
}

fun WeatherModel.toEntity(): CityWeatherEntity {
    return CityWeatherEntity(
        cityName = this.cityName,
        temperature = this.temperature,
        description = this.weatherDescription
    )
}
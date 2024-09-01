package com.luisreyes.weatherapp.data.mappers

import com.luisreyes.weatherapp.data.local.entities.CityWeatherEntity
import com.luisreyes.weatherapp.data.repository.network.responses.Weather.WeatherResponse
import com.luisreyes.weatherapp.domain.model.WeatherModel

fun WeatherResponse.toDomainModel(): WeatherModel {
    return WeatherModel(
        cityName = this.name.lowercase(),
        temperature = this.main.temp,
        weatherDescription = this.weather.firstOrNull()?.description ?: "No description",
        latitude = this.coord.lat,
        longitude = this.coord.lon
    )
}

fun CityWeatherEntity.toDomainModel(): WeatherModel {
    return WeatherModel(
        cityName = this.cityName.lowercase(),
        temperature = this.temperature,
        weatherDescription = this.description,
        latitude = this.lat,
        longitude = this.lng
    )
}

fun WeatherModel.toEntity(): CityWeatherEntity {
    return CityWeatherEntity(
        cityName = this.cityName.lowercase(),
        temperature = this.temperature,
        description = this.weatherDescription,
        lat = this.latitude,
        lng = this.longitude
    )
}
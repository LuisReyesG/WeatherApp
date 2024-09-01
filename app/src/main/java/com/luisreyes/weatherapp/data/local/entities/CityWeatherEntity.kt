package com.luisreyes.weatherapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city_weather")
data class CityWeatherEntity(
    @PrimaryKey val cityName: String,
    val temperature: Double,
    val description: String
)
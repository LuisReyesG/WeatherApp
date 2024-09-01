package com.luisreyes.weatherapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.luisreyes.weatherapp.data.local.entities.CityWeatherEntity

@Dao
interface CityWeatherDao {
    @Query("SELECT * FROM city_weather WHERE cityName = :cityName")
    suspend fun getCityWeather(cityName: String): CityWeatherEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCityWeather(cityWeather: CityWeatherEntity)
}
package com.luisreyes.weatherapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.luisreyes.weatherapp.data.local.dao.CityWeatherDao
import com.luisreyes.weatherapp.data.local.entities.CityWeatherEntity

@Database(entities = [CityWeatherEntity::class], version = 1, exportSchema = false)
abstract class CityWeatherDatabase : RoomDatabase() {
    abstract fun cityWeatherDao(): CityWeatherDao
}
package com.luisreyes.weatherapp.framework.di.modules

import android.content.Context
import androidx.room.Room
import com.luisreyes.weatherapp.data.local.CityWeatherDatabase
import com.luisreyes.weatherapp.data.local.dao.CityWeatherDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): CityWeatherDatabase {
        return Room.databaseBuilder(
            appContext,
            CityWeatherDatabase::class.java,
            "city_weather_database"
        ).build()
    }

    @Provides
    fun provideCityWeatherDao(db: CityWeatherDatabase): CityWeatherDao {
        return db.cityWeatherDao()
    }
}

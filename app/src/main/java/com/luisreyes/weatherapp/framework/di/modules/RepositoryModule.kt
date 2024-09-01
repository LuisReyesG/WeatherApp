package com.luisreyes.weatherapp.framework.di.modules


import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.luisreyes.weatherapp.data.local.dao.CityWeatherDao
import com.luisreyes.weatherapp.data.repository.network.apis.GeocodingServices
import com.luisreyes.weatherapp.data.repository.network.apis.WeatherApi
import com.luisreyes.weatherapp.data.repository.source.Geolocalitation.GeoDataSourceImpl
import com.luisreyes.weatherapp.data.repository.source.Geolocalitation.GeoRepositoryDataSource
import com.luisreyes.weatherapp.data.repository.source.Geolocalitation.GeoRepositoryImpl
import com.luisreyes.weatherapp.data.repository.source.Weather.WeatherDataSourceImpl
import com.luisreyes.weatherapp.data.repository.source.Weather.WeatherRepositoryDataSource
import com.luisreyes.weatherapp.data.repository.source.Weather.WeatherRepositoryImpl
import com.luisreyes.weatherapp.domain.repository.GeoRepository
import com.luisreyes.weatherapp.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideWeatherRepositoryDataSource(
        weatherApi: WeatherApi,
        cityWeatherDao: CityWeatherDao
    ): WeatherRepositoryDataSource {
        return WeatherDataSourceImpl(weatherApi, cityWeatherDao)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        weatherDataSource: WeatherRepositoryDataSource
    ): WeatherRepository {
        return WeatherRepositoryImpl(weatherDataSource)
    }

    @Provides
    @Singleton
    fun provideGeolocationRepositoryDataSource(
        geolocationApi: GeocodingServices,
    ): GeoRepositoryDataSource {
        return GeoDataSourceImpl(geolocationApi)
    }

    @Provides
    @Singleton
    fun provideGeolocationRepository(
        geoDataSource: GeoRepositoryDataSource
    ): GeoRepository {
        return GeoRepositoryImpl(geoDataSource)
    }

}

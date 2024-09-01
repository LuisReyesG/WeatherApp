package com.luisreyes.weatherapp.framework.di.modules

import com.luisreyes.weatherapp.BuildConfig
import com.luisreyes.weatherapp.data.repository.network.apis.GeocodingServices
import com.luisreyes.weatherapp.data.repository.network.apis.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Proveedor para Retrofit de Google Maps
    @Provides
    @Singleton
    @Named("GoogleMapsRetrofit")
    fun providesGoogleMapsRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl("https://maps.googleapis.com/maps/api/") // Base URL para Google Maps
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.API_KEY)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Singleton
    @Provides
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }

    @Singleton
    @Provides
    fun provideGeoApi(@Named("GoogleMapsRetrofit") retrofit: Retrofit): GeocodingServices {
        return retrofit.create(GeocodingServices::class.java)
    }
}
package com.luisreyes.weatherapp.data.repository.network.apis

import com.luisreyes.weatherapp.data.repository.network.request.WeatherRequest
import com.luisreyes.weatherapp.data.repository.network.responses.Weather.WeatherResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apikey: String
    ): Response<WeatherResponse>
}
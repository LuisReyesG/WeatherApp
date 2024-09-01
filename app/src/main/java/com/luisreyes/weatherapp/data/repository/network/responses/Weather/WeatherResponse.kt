package com.luisreyes.weatherapp.data.repository.network.responses.Weather

import com.luisreyes.weatherapp.data.model.Weather.Clouds
import com.luisreyes.weatherapp.data.model.Weather.Coord
import com.luisreyes.weatherapp.data.model.Weather.Main
import com.luisreyes.weatherapp.data.model.Weather.Sys
import com.luisreyes.weatherapp.data.model.Weather.Weather
import com.luisreyes.weatherapp.data.model.Weather.Wind

data class WeatherResponse(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
)

package com.luisreyes.weatherapp.presentation.viewModel

interface WeatherViewContract {
    fun ErrorSearchCityWeather(message: String?)
    fun ErrorGetCoordinates(message: String?)
    fun ErrorGetCityLocalData(s: String)
}
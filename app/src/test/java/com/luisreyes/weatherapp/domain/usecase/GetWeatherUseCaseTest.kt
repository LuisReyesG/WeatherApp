package com.luisreyes.weatherapp.domain.usecase

import android.view.Display.Mode
import com.luisreyes.weatherapp.domain.model.WeatherModel
import com.luisreyes.weatherapp.domain.repository.WeatherRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetWeatherUseCaseTest{

    @RelaxedMockK
    private lateinit var repository: WeatherRepository

    lateinit var useCase: GetWeatherUseCase

    @Before
    fun onBefore(){
        MockKAnnotations.init(this)
        useCase = GetWeatherUseCase(repository)
    }

    @Test
    fun `invoke should return a WeatherModel object`() = runBlocking{
        // Given
        val lat = 40.714224
        val lon = -73.961452
        val key = "key"
        coEvery { repository.getWeatherByCity(lat, lon, key) } returns WeatherModel("México", 25.00, "Clear", 19.25, 99.07)

        // When
        useCase(lat, lon, key)

        // Then
        coVerify { repository.getWeatherByCity(lat, lon, key)}
    }

}
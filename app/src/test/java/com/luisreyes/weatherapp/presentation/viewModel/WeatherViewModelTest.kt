package com.luisreyes.weatherapp.presentation.viewModel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.location.FusedLocationProviderClient
import com.luisreyes.weatherapp.data.local.dao.CityWeatherDao
import com.luisreyes.weatherapp.domain.model.WeatherModel
import com.luisreyes.weatherapp.domain.repository.GeoRepository
import com.luisreyes.weatherapp.domain.repository.WeatherRepository
import com.luisreyes.weatherapp.domain.usecase.GetGeolocalitationUseCase
import com.luisreyes.weatherapp.domain.usecase.GetWeatherUseCase
import com.luisreyes.weatherapp.framework.utils.NoInternetException
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @RelaxedMockK
    private lateinit var weatherRepository: WeatherRepository

    @RelaxedMockK
    private lateinit var geoRepository: GeoRepository

    private lateinit var viewModel: WeatherViewModel
    lateinit var weatherUSeCase: GetWeatherUseCase
    lateinit var geoUseCase: GetGeolocalitationUseCase
    lateinit var dao: CityWeatherDao
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var context: Context
    private val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)
        weatherUSeCase = GetWeatherUseCase(weatherRepository)
        geoUseCase = GetGeolocalitationUseCase(geoRepository)
        dao = mockk()
        fusedLocationProviderClient = mockk()
        context = mockk()
        viewModel =
            WeatherViewModel(weatherUSeCase, geoUseCase, dao, fusedLocationProviderClient, context)
    }

    @Test
    fun `initWeatherData should post success state when getWeather succeeds`() {
        // Given
        val expected = WeatherModel("México", 25.00, "Clear", 19.25, 99.07)
        coEvery { weatherRepository.getWeatherByCity(any(), any(), any()) } returns expected

        // When
        viewModel.searchCityWeather(40.714224, -73.961452, "key")

        // Then
        assertEquals(expected, viewModel.weatherLiveData.value)
    }

    @Test
    fun `initWeatherData should post error state when getWeather fails`() {
        // Given
        val exception = Exception("Error")
        coEvery { weatherRepository.getWeatherByCity(any(), any(), any()) } throws exception

        // When
        viewModel.searchCityWeather(40.714224, -73.961452, "key")

        // Then
        assertEquals(exception.message, viewModel.errorLiveData.value)
    }

    @Test
    fun `initWeatherData should post weather data when getWeather succeeds`() {
        // Given
        val expected = WeatherModel("México", 25.00, "Clear", 19.25, 99.07)
        coEvery { weatherRepository.getWeatherByCity(any(), any(), any()) } returns expected

        // When
        viewModel.searchCityWeather(40.714224, -73.961452, "key")

        // Then
        assertEquals(expected, viewModel.weatherLiveData.value)
    }
}

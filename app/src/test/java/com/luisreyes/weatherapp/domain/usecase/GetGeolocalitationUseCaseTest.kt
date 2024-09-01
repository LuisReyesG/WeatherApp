package com.luisreyes.weatherapp.domain.usecase

import com.luisreyes.weatherapp.data.model.Geo.Location
import com.luisreyes.weatherapp.domain.repository.GeoRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetGeolocalitationUseCaseTest{

    @RelaxedMockK
    private lateinit var repository: GeoRepository

    lateinit var useCase: GetGeolocalitationUseCase

    @Before
    fun onBefore(){
        MockKAnnotations.init(this)
        useCase = GetGeolocalitationUseCase(repository)
    }

    @Test
    fun `invoke should return a Location object`() = runBlocking{
        // Given
        val latlng = "40.714224,-73.961452"
        val key = "key"
        coEvery { repository.getCoordinates(latlng, key) } returns Location(40.714224, -73.961452)

        // When
        useCase(latlng, key)

        // Then
        coVerify { repository.getCoordinates(latlng, key)}
    }
}
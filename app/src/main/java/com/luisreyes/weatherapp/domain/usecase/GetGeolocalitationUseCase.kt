package com.luisreyes.weatherapp.domain.usecase

import android.util.Log
import com.luisreyes.weatherapp.data.model.Geo.Location
import com.luisreyes.weatherapp.data.repository.network.responses.Geocoding.GeocodingResponse
import com.luisreyes.weatherapp.domain.repository.GeoRepository
import javax.inject.Inject

class GetGeolocalitationUseCase @Inject constructor(
    private val repository: GeoRepository
){
    suspend operator fun invoke(latlng: String, key: String): Location? {
        Log.d("consumosgoogel", "getCoordinates3: " + latlng + " " + key)
        return repository.getCoordinates(latlng, key)
    }
}
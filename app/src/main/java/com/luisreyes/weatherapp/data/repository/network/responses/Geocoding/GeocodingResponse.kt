package com.luisreyes.weatherapp.data.repository.network.responses.Geocoding

import com.luisreyes.weatherapp.data.model.Geo.GeocodingResult

data class GeocodingResponse(
    val results: List<GeocodingResult>,
    val status: String
)

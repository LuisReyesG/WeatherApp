package com.luisreyes.weatherapp.domain.repository

import com.luisreyes.weatherapp.data.model.Geo.Location

interface GeoRepository {
    suspend fun getCoordinates(latlng: String, key: String): Location?
}
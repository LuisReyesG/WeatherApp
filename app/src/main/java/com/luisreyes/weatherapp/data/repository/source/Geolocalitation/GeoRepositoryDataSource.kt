package com.luisreyes.weatherapp.data.repository.source.Geolocalitation

import com.luisreyes.weatherapp.data.model.Geo.Location

interface GeoRepositoryDataSource {
    suspend fun getCoordinates(latlng: String, key: String): Location?
}
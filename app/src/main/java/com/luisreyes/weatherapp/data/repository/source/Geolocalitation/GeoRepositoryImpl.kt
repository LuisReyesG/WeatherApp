package com.luisreyes.weatherapp.data.repository.source.Geolocalitation

import android.util.Log
import com.luisreyes.weatherapp.data.model.Geo.Location
import com.luisreyes.weatherapp.domain.repository.GeoRepository
import javax.inject.Inject

class GeoRepositoryImpl @Inject constructor(
    private val geoDataSource: GeoRepositoryDataSource
): GeoRepository {
    override suspend fun getCoordinates(latlng: String, key: String): Location? {
        Log.d("consumosgoogel", "getCoordinates2: " + latlng + " " + key)
        return geoDataSource.getCoordinates(latlng, key)
    }

}
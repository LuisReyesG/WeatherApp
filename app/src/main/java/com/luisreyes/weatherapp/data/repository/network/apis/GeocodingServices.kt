package com.luisreyes.weatherapp.data.repository.network.apis

import com.luisreyes.weatherapp.data.repository.network.responses.Geocoding.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingServices {
    @GET("geocode/json")
    suspend fun getGeocoding(
        @Query("address") latlng: String,
        @Query("key") key: String
    ): GeocodingResponse
}
package com.luisreyes.weatherapp.data.repository.source.Geolocalitation

import android.util.Log
import com.luisreyes.weatherapp.data.model.Geo.Location
import com.luisreyes.weatherapp.data.repository.network.apis.GeocodingServices
import com.luisreyes.weatherapp.data.repository.network.responses.Geocoding.GeocodingResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import kotlin.math.log

class GeoDataSourceImpl(
    @Named("GoogleMapsRetrofit") private val geocodingServices: GeocodingServices
): GeoRepositoryDataSource {

    override suspend fun getCoordinates(city: String, apiKey: String): Location? {
        val response = geocodingServices.getGeocoding(city, apiKey)
        return response.results.firstOrNull()?.geometry?.location
    }
}
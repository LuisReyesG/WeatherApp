package com.luisreyes.weatherapp.presentation.viewModel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.luisreyes.weatherapp.domain.model.WeatherModel
import com.luisreyes.weatherapp.domain.usecase.GetGeolocalitationUseCase
import com.luisreyes.weatherapp.domain.usecase.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getgeoUseCase: GetGeolocalitationUseCase,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : ViewModel() {

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }

    private val _weatherLiveData = MutableLiveData<WeatherModel>()
    val weatherLiveData: LiveData<WeatherModel> get() = _weatherLiveData

    private val _locationData = MutableLiveData<Pair<Double?, Double?>>()
    val locationData: LiveData<Pair<Double?, Double?>> get() = _locationData

    var locationPermissionGranted = false
    private var lastKnownLocation: Location? = null

    fun checkLocationPermission(context: Context) {
        locationPermissionGranted = ContextCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
    }

    fun handlePermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            locationPermissionGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocation() {
        if (locationPermissionGranted) {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    lastKnownLocation = task.result
                    if (lastKnownLocation != null) {
                        _locationData.value = Pair(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
                        searchCityWeather(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude, "23dbe50d4c50ece6a42833fcc5a6e83b")
                    }
                }
            }
        }
    }



    //ApisConsumes
    fun getCoordinates(city: String, apikey: String) {
        Log.d("getCoordinates", "Fetching coordinates for: $city with API key: $apikey")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoding = getgeoUseCase(city, apikey)
                val lat = geocoding?.lat
                val lng = geocoding?.lng

                Log.d("getCoordinates", "Coordinates found: Lat = $lat, Lng = $lng")

                // Cambiar al hilo principal para actualizar LiveData
                withContext(Dispatchers.Main) {
                    _locationData.value = Pair(lat, lng)
                    Log.d("getCoordinates", "LocationData updated: $_locationData")
                }

                // Realizar la llamada para obtener el clima
                if (lat != null) {
                    if (lng != null) {
                        searchCityWeather(lat, lng, "23dbe50d4c50ece6a42833fcc5a6e83b")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("getCoordinates", "Failed to fetch coordinates: ${e.message}")
            }
        }
    }

    fun searchCityWeather(lat: Double, lon: Double, apikey: String) {
        Log.d("searchCityWeather: " , lat.toString() + " " + lon.toString() + " " + apikey)
        viewModelScope.launch {
            try {
                val weather = getWeatherUseCase(lat, lon, apikey)
                _weatherLiveData.value = weather
                Log.d("searchCityWeather: ", weather.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
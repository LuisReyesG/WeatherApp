package com.luisreyes.weatherapp.presentation.viewModel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.luisreyes.weatherapp.data.local.dao.CityWeatherDao
import com.luisreyes.weatherapp.data.mappers.toDomainModel
import com.luisreyes.weatherapp.domain.model.WeatherModel
import com.luisreyes.weatherapp.domain.usecase.GetGeolocalitationUseCase
import com.luisreyes.weatherapp.domain.usecase.GetWeatherUseCase
import com.luisreyes.weatherapp.framework.utils.NoInternetException
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.Normalizer
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getgeoUseCase: GetGeolocalitationUseCase,
    private val cityWeatherDao: CityWeatherDao,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }

    var viewContract: WeatherViewContract? = null

    private val _weatherLiveData = MutableLiveData<WeatherModel?>()
    val weatherLiveData: LiveData<WeatherModel?> get() = _weatherLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    private val _searchHistory = MutableLiveData<Pair<Double?, Double?>>()
    val searchHistory: LiveData<Pair<Double?, Double?>> get() = _searchHistory

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
                if (task.isSuccessful && task.result != null) {
                    lastKnownLocation = task.result
                    _locationData.value = Pair(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
                    searchCityWeather(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude, "23dbe50d4c50ece6a42833fcc5a6e83b")
                } else {
                    requestNewLocationData()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000 // 10 segundos
            fastestInterval = 5000 // 5 segundos
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        _locationData.value = Pair(location.latitude, location.longitude)
                        searchCityWeather(location.latitude, location.longitude, "23dbe50d4c50ece6a42833fcc5a6e83b")
                    }
                    fusedLocationProviderClient.removeLocationUpdates(this)
                }
            },
            Looper.getMainLooper()
        )
    }


    fun isInternetAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }




    //ApisConsumes
    fun getCoordinates(city: String, apikey: String) {
        // Verificar la conectividad
        if (!isInternetAvailable()) {
            getWeatherByCityLocalData(city) // Llama al método para obtener datos locales si no hay conexión
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoding = getgeoUseCase(city, apikey)
                val lat = geocoding?.lat
                val lng = geocoding?.lng

                // Cambiar al hilo principal para actualizar LiveData
                withContext(Dispatchers.Main) {
                    _locationData.value = Pair(lat, lng)
                }

                // Realizar la llamada para obtener el clima
                if (lat != null && lng != null) {
                    searchCityWeather(lat, lng, "23dbe50d4c50ece6a42833fcc5a6e83b")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                viewContract?.ErrorGetCoordinates(e.message)

            }
        }
    }


    fun searchCityWeather(lat: Double, lon: Double, apikey: String) {
        viewModelScope.launch {
            try {
                val weather = getWeatherUseCase(lat, lon, apikey)
                _weatherLiveData.value = weather
            } catch (e: Exception) {
                _weatherLiveData.postValue(null)
                _errorLiveData.postValue(e.message)
                viewContract?.ErrorSearchCityWeather(e.message)
            }
        }
    }

    fun getWeatherByCityLocalData(city: String) {
        viewModelScope.launch {
            val localWeather = cityWeatherDao.getCityWeather(city)?.toDomainModel()

            if (localWeather != null) {
                _weatherLiveData.value = localWeather
                val lat = localWeather.latitude
                val lng = localWeather.longitude

                withContext(Dispatchers.Main) {
                    _locationData.value = Pair(lat, lng)
                }

            } else {
                viewContract?.ErrorGetCityLocalData("No local data available")
            }
        }
    }
}
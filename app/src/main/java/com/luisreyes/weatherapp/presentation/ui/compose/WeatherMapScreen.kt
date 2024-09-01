package com.luisreyes.weatherapp.presentation.ui.compose

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.luisreyes.weatherapp.domain.model.WeatherModel
import com.luisreyes.weatherapp.presentation.viewModel.WeatherViewModel
import java.text.Normalizer


@Composable
fun WeatherMapScreen(
    weatherModel: WeatherModel?,
    onMapReady: (GoogleMap) -> Unit,
    mapProperties: MapProperties = MapProperties(),
    mapUiSettings: MapUiSettings = MapUiSettings(),
    viewModel: WeatherViewModel,

    ) {
    val weather by viewModel.weatherLiveData.observeAsState()
    val locationData by viewModel.locationData.observeAsState()
    val defaultLocation = LatLng(0.0, 0.0)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation , 15f)
    }
    var map by remember { mutableStateOf<GoogleMap?>(null) }

    val context = LocalContext.current
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activityNetwork = connectivityManager.activeNetworkInfo
    val isConnected = activityNetwork?.isConnectedOrConnecting == true

    LaunchedEffect(isConnected) {
        if (isConnected) {
            viewModel.checkLocationPermission(context)
            if (!viewModel.locationPermissionGranted) {
                viewModel.requestLocationPermission(context as Activity)
            } else {
                viewModel.getDeviceLocation()
            }
        }
    }

    locationData?.let { (lat, lng) ->
        if (lat != null && lng != null) {
            LaunchedEffect(lat, lng) {
                val newLocation = LatLng(lat, lng)
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(newLocation, 12f))
            }
        }
    }

    Scaffold() { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBox(viewModel)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    onMapLoaded = { map?.let { onMapReady(it) } },
                    properties = mapProperties,
                    uiSettings = mapUiSettings,
                ) {
                    weather?.let { weatherModel ->
                        val location = LatLng(weatherModel.latitude, weatherModel.longitude)
                        val markerState = rememberMarkerState( position = location)
                        Log.d("WeatherMapScreen: ", "WeatherModel: $weatherModel" + "\n" + "Location: $location")
                        Marker(
                            state = markerState,
                            title = weatherModel.cityName,
                            snippet = "${weatherModel.weatherDescription}, ${weatherModel.temperature}°C"
                        )

                        LaunchedEffect(location) {
                            markerState.position = location
                            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
                        }
                    }
                }
            }
            WeatherInfoTable(weather)
        }
    }
}


@Composable
fun SearchBox(
    viewModel: WeatherViewModel
) {
    var query by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = query,
        onValueChange = {
            query = it
            Log.d("SearchBox", "Query: $query")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text(text = "Search city...") },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
        },
        singleLine = true,
        keyboardActions = KeyboardActions(
            onSearch = {
                if (query.isNotEmpty()) {
                    Log.d("SearchBox", "Searching for: $query")
                    viewModel.getCoordinates(query, "AIzaSyDHkpmXPeW02-7uxqCKKCPZt1ai0k_v-a4")
                    keyboardController?.hide()
                }
            }
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        )
    )
}

@Composable
fun WeatherInfoTable(weatherModel: WeatherModel?) {
    weatherModel?.let { weather ->
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Text(text = "City Name: ")
                Text(text = weather.cityName.uppercase())
            }
            Row {
                Text(text = "Temperature: ")
                Text(text = "${weather.temperature}°C")
            }
            Row {
                Text(text = "Weather Description: ")
                Text(text = weather.weatherDescription)
            }
            Row {
                Text(text = "Latitude: ")
                Text(text = "${weather.latitude}")
            }
            Row {
                Text(text = "Longitude: ")
                Text(text = "${weather.longitude}")
            }
        }
    }
}

package com.luisreyes.weatherapp.presentation.ui.compose

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.luisreyes.weatherapp.R
import com.luisreyes.weatherapp.domain.model.WeatherModel
import com.luisreyes.weatherapp.presentation.viewModel.WeatherViewModel


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
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
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
            val newLocation = LatLng(lat, lng)
            LaunchedEffect(newLocation) {
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
                        val markerState = rememberMarkerState(position = location)
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
    val keyGoogle = stringResource(id = R.string.key_google_maps)

    OutlinedTextField(
        value = query,
        onValueChange = {
            query = it
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text(text = stringResource(id = R.string.label_search_key)) },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
        },
        singleLine = true,
        keyboardActions = KeyboardActions(
            onSearch = {
                if (query.isNotEmpty()) {
                    viewModel.getCoordinates(query, keyGoogle)
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
                Text(text = weather.weatherDescription, modifier = Modifier.padding(end = 8.dp))
                when (weather.weatherDescription.lowercase()) {
                    "clear sky" -> Image(painter = painterResource(id = R.drawable.ic_clear_sky), contentDescription = "Clear Sky", modifier = Modifier.size(24.dp))
                    "few clouds" -> Image(painter = painterResource(id = R.drawable.ic_few_clouds), contentDescription = "Few Clouds",  modifier = Modifier.size(24.dp))
                    "scattered clouds" -> Image(painter = painterResource(id = R.drawable.ic_few_clouds), contentDescription = "Scattered Clouds",  modifier = Modifier.size(24.dp))
                    "broken clouds" -> Image(painter = painterResource(id = R.drawable.ic_few_clouds), contentDescription = "Broken Clouds",  modifier = Modifier.size(24.dp))
                    "shower rain" -> Image(painter = painterResource(id = R.drawable.ic_rain), contentDescription = "Shower Rain",  modifier = Modifier.size(24.dp))
                    "rain" -> Image(painter = painterResource(id = R.drawable.ic_rain), contentDescription = "Rain",  modifier = Modifier.size(24.dp))
                    "thunderstorm" -> Image(painter = painterResource(id = R.drawable.ic_thunder_storm), contentDescription = "Thunderstorm",  modifier = Modifier.size(24.dp))
                    "snow" -> Image(painter = painterResource(id = R.drawable.ic_snow), contentDescription = "Snow",  modifier = Modifier.size(24.dp))
                    "mist" -> Image(painter = painterResource(id = R.drawable.ic_mist), contentDescription = "Mist",  modifier = Modifier.size(24.dp))
                    else -> Image(painter = painterResource(id = R.drawable.ic_few_clouds), contentDescription = "Cloudy",  modifier = Modifier.size(24.dp))
                }

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

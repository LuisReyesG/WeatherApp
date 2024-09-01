package com.luisreyes.weatherapp.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.maps.android.compose.GoogleMap
import com.luisreyes.weatherapp.databinding.ActivityMainBinding
import com.luisreyes.weatherapp.domain.model.WeatherModel
import com.luisreyes.weatherapp.presentation.ui.compose.WeatherMapScreen
import com.luisreyes.weatherapp.presentation.viewModel.WeatherViewContract
import com.luisreyes.weatherapp.presentation.viewModel.WeatherViewModel
import com.luisreyes.weatherapp.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), WeatherViewContract {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpCompose()
    }

    private fun setUpCompose() {
        Log.d(TAG, "setUpCompose: ")
        binding.composeView.apply {
            setContent {
                WeatherAppTheme {
                    // A surface container using the 'background' color from the theme
                    WeatherMapScreen(
                        weatherModel = viewModel.weatherLiveData.value,
                        onMapReady = { },
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    override fun ErrorSearchCityWeather(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun ErrorGetCoordinates(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}

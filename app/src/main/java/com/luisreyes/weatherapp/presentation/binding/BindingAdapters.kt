package com.luisreyes.weatherapp.presentation.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter

class BindingAdapters {

    @BindingAdapter("bind:weatherInfo")
    fun bindWeatherInfo(textView: TextView, weatherInfo: String) {
        weatherInfo?.let {
            textView.text = weatherInfo
        }

    }
}
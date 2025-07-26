package com.zerir.weathersnap.ui.screens

import androidx.lifecycle.ViewModel
import com.zerir.weathersnap.domain.model.Coordinates
import com.zerir.weathersnap.domain.model.Weather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class WeatherWithLocation(
    val weather: Weather,
    val coordinates: Coordinates,
    val defaultCelsius: Boolean,
)

@HiltViewModel
class SharedDataViewModel @Inject constructor() : ViewModel() {

    private val _currentWeatherData = MutableStateFlow<WeatherWithLocation?>(null)
    val currentWeatherData: StateFlow<WeatherWithLocation?> = _currentWeatherData.asStateFlow()

    fun setWeatherData(weather: Weather, coordinates: Coordinates, defaultCelsius: Boolean) {
        _currentWeatherData.value = WeatherWithLocation(weather, coordinates, defaultCelsius)
    }
}

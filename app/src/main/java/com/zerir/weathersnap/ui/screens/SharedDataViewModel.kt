package com.zerir.weathersnap.ui.screens

import androidx.lifecycle.ViewModel
import com.zerir.weathersnap.domain.model.Coordinates
import com.zerir.weathersnap.domain.model.Weather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SharedDataViewModel @Inject constructor() : ViewModel() {

    private val _currentWeatherData = MutableStateFlow<Pair<Weather, Coordinates>?>(null)
    val currentWeatherData: StateFlow<Pair<Weather, Coordinates>?> =
        _currentWeatherData.asStateFlow()

    fun setWeatherData(weather: Weather, coordinates: Coordinates) {
        _currentWeatherData.value = Pair(weather, coordinates)
    }

    fun clearWeatherData() {
        _currentWeatherData.value = null
    }

    fun hasWeatherData(): Boolean {
        return _currentWeatherData.value != null
    }

    fun getWeatherData(): Pair<Weather, Coordinates>? {
        return _currentWeatherData.value
    }
}
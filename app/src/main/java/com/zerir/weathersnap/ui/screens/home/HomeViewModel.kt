package com.zerir.weathersnap.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerir.weathersnap.domain.model.UiState
import com.zerir.weathersnap.domain.model.Weather
import com.zerir.weathersnap.domain.repository.LocationRepository
import com.zerir.weathersnap.domain.repository.WeatherRepository
import com.zerir.weathersnap.ui.state.LocationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository,
) : ViewModel() {

    private val _locationState = MutableStateFlow<LocationState?>(null)
    val locationState: StateFlow<LocationState?> = _locationState.asStateFlow()

    private val _weatherState = MutableStateFlow<UiState<Weather>?>(null)
    val weatherState: StateFlow<UiState<Weather>?> = _weatherState.asStateFlow()

    fun onPermissionGranted() {
        loadCurrentLocationWeather()
    }

    fun onPermissionDenied() {
        _locationState.value = LocationState.PermissionDenied
    }

    fun loadCurrentLocationWeather() {
        viewModelScope.launch {
            _locationState.value = LocationState.Loading
            when (val locationResult = locationRepository.getCurrentLocation()) {
                is UiState.Success -> {
                    val location = locationResult.data
                    _locationState.value = LocationState.Success(location)

                    loadWeather(location.latitude, location.longitude)
                }

                is UiState.Error -> {
                    if (locationResult.message.contains("permission", ignoreCase = true)) {
                        _locationState.value = LocationState.PermissionDenied
                    } else {
                        _locationState.value = LocationState.Error(locationResult.message)
                    }
                }

                UiState.Loading -> {}
            }
        }
    }

    fun loadWeather(latitude: Double = 37.7749, longitude: Double = -122.4194) {
        viewModelScope.launch {
            _weatherState.value = UiState.Loading
            val result = weatherRepository.getCurrentWeather(latitude, longitude)
            _weatherState.value = result
        }
    }
}
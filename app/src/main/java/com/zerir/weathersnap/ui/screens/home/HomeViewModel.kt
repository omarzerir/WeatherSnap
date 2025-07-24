package com.zerir.weathersnap.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerir.weathersnap.domain.model.UiState
import com.zerir.weathersnap.domain.model.Weather
import com.zerir.weathersnap.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _weatherState = MutableStateFlow<UiState<Weather>?>(null)
    val weatherState: StateFlow<UiState<Weather>?> = _weatherState.asStateFlow()

    fun loadWeather() {
        viewModelScope.launch {
            _weatherState.value = UiState.Loading
            // Test with San Francisco coordinates
            val result = weatherRepository.getCurrentWeather(37.7749, -122.4194)
            _weatherState.value = result
        }
    }
}
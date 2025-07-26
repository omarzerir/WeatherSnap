package com.zerir.weathersnap.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.zerir.weathersnap.domain.model.UiState
import com.zerir.weathersnap.domain.repository.ImageHistoryRepository
import com.zerir.weathersnap.domain.repository.LocationRepository
import com.zerir.weathersnap.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository,
    private val imageHistoryRepository: ImageHistoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        HomeState(
            imageHistory = imageHistoryRepository.getAllImagesPaginated().cachedIn(viewModelScope)
        )
    )
    val state: StateFlow<HomeState> = _state.asStateFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LocationPermissionGranted -> {
                updateState { copy(hasLocationPermission = true) }
                // Automatically load weather after permission granted
                onEvent(HomeEvent.LoadCurrentLocationWeather)
            }

            is HomeEvent.LocationPermissionDenied -> {
                updateState {
                    copy(
                        hasLocationPermission = false,
                        locationState = null,
                        weatherState = null,
                        canNavigateToCamera = false
                    )
                }
            }

            is HomeEvent.LoadCurrentLocationWeather -> {
                loadCurrentLocationWeather()
            }

            is HomeEvent.RefreshWeather -> {
                refreshWeather()
            }

            is HomeEvent.DeleteImage -> {
                deleteImage(imageId = event.imageId, filePath = event.filePath)
            }

            is HomeEvent.DeleteAllImages -> {
                deleteAllImages()
            }

            is HomeEvent.ToggleTemperatureUnit -> {
                updateState { copy(showCelsius = !showCelsius) }
            }
        }
    }

    private fun loadCurrentLocationWeather() {
        if (!state.value.hasLocationPermission) {
            updateState {
                copy(
                    locationState = UiState.Error("Location permission required"),
                    weatherState = null,
                    canNavigateToCamera = false
                )
            }
            return
        }

        viewModelScope.launch {
            // Start location loading
            updateState { copy(locationState = UiState.Loading) }

            when (val locationResult = locationRepository.getCurrentLocation()) {
                is UiState.Success -> {
                    val coordinates = locationResult.data
                    updateState { copy(locationState = UiState.Success(coordinates)) }

                    // Start weather loading
                    loadWeatherForCoordinates(coordinates.latitude, coordinates.longitude)
                }

                is UiState.Error -> {
                    updateState {
                        copy(
                            locationState = UiState.Error(locationResult.message),
                            weatherState = null,
                            canNavigateToCamera = false
                        )
                    }
                }

                is UiState.Loading -> {}
            }
        }
    }

    private fun loadWeatherForCoordinates(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            updateState { copy(weatherState = UiState.Loading) }

            when (val weatherResult = weatherRepository.getCurrentWeather(latitude, longitude)) {
                is UiState.Success -> {
                    updateState {
                        copy(
                            weatherState = UiState.Success(weatherResult.data),
                            lastUpdatedTime = getCurrentTimeString(),
                            isRefreshingWeather = false,
                            canNavigateToCamera = true // Weather + location both successful
                        )
                    }
                }

                is UiState.Error -> {
                    updateState {
                        copy(
                            weatherState = UiState.Error(weatherResult.message),
                            isRefreshingWeather = false,
                            canNavigateToCamera = false
                        )
                    }
                }

                is UiState.Loading -> {}
            }
        }
    }

    private fun refreshWeather() {
        val currentLocation = (state.value.locationState as? UiState.Success)?.data
        if (currentLocation != null) {
            updateState { copy(isRefreshingWeather = true) }
            loadWeatherForCoordinates(currentLocation.latitude, currentLocation.longitude)
        } else {
            // No location available - reload from scratch
            onEvent(HomeEvent.LoadCurrentLocationWeather)
        }
    }

    private fun deleteImage(imageId: String, filePath: String) {
        viewModelScope.launch {
            imageHistoryRepository.deleteImage(imageId = imageId, filePath = filePath)
        }
    }

    private fun deleteAllImages() {
        viewModelScope.launch {
            imageHistoryRepository.deleteAllImages()
        }
    }

    private fun updateState(update: HomeState.() -> HomeState) {
        _state.value = _state.value.update()
    }

    private fun getCurrentTimeString(): String {
        val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        return formatter.format(Date())
    }
}
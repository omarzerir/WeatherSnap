package com.zerir.weathersnap.ui.screens.home

sealed class HomeEvent {
    // Location permission events
    data object LocationPermissionGranted : HomeEvent()
    data object LocationPermissionDenied : HomeEvent()

    // Weather events
    data object LoadCurrentLocationWeather : HomeEvent()
    data object RefreshWeather : HomeEvent()
    data object ToggleTemperatureUnit : HomeEvent()

    // Image events
    data class DeleteImage(val imageId: String, val filePath: String) : HomeEvent()
    data object DeleteAllImages : HomeEvent()
}
package com.zerir.weathersnap.ui.screens.camera

import com.zerir.weathersnap.domain.model.Coordinates
import com.zerir.weathersnap.domain.model.Weather

sealed class CameraEvent {
    data object InitializeCamera : CameraEvent()
    data class CapturePhoto(
        val weather: Weather,
        val coordinates: Coordinates,
        val useCelsius: Boolean
    ) : CameraEvent()

    data object Reset : CameraEvent()
}

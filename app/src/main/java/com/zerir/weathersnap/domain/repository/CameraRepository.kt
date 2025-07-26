package com.zerir.weathersnap.domain.repository

import androidx.camera.core.ImageCapture
import com.zerir.weathersnap.domain.model.CapturedImage
import com.zerir.weathersnap.domain.model.Coordinates
import com.zerir.weathersnap.domain.model.UiState
import com.zerir.weathersnap.domain.model.Weather

interface CameraRepository {
    suspend fun capturePhotoWithWeatherOverlay(
        weather: Weather,
        coordinates: Coordinates,
        imageCapture: ImageCapture,
        useCelsius: Boolean,
    ): UiState<CapturedImage>
}
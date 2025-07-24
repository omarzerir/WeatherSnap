package com.zerir.weathersnap.data.repository

import androidx.camera.core.ImageCapture
import com.zerir.weathersnap.data.camera.CameraService
import com.zerir.weathersnap.domain.model.CapturedImage
import com.zerir.weathersnap.domain.model.Coordinates
import com.zerir.weathersnap.domain.model.UiState
import com.zerir.weathersnap.domain.model.Weather
import com.zerir.weathersnap.domain.repository.CameraRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraRepositoryImpl @Inject constructor(
    private val cameraService: CameraService
) : CameraRepository {

    override suspend fun capturePhotoWithWeatherOverlay(
        weather: Weather,
        coordinates: Coordinates,
        imageCapture: ImageCapture
    ): UiState<CapturedImage> {
        return try {
            // Capture photo with real CameraX
            val originalPhoto = cameraService.capturePhoto(imageCapture)

            // Overlay weather data on image
            val weatherOverlayPhoto =
                cameraService.capturePhotoWithWeatherOverlay(originalPhoto, weather)

            // Create captured image model
            val capturedImage = CapturedImage(
                imageFile = weatherOverlayPhoto,
                weather = weather,
                coordinates = coordinates,
                timestamp = System.currentTimeMillis()
            )

            UiState.Success(capturedImage)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Failed to capture photo with weather overlay")
        }
    }

    override suspend fun getAllCapturedImages(): UiState<List<CapturedImage>> {
        return try {
            UiState.Success(emptyList())
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Failed to load captured images")
        }
    }

    override suspend fun deleteCapturedImage(imageId: String): UiState<Unit> {
        return try {
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Failed to delete captured image")
        }
    }
}
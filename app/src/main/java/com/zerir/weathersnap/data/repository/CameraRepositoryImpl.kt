package com.zerir.weathersnap.data.repository

import androidx.camera.core.ImageCapture
import com.zerir.weathersnap.data.camera.CameraService
import com.zerir.weathersnap.domain.model.CapturedImage
import com.zerir.weathersnap.domain.model.Coordinates
import com.zerir.weathersnap.domain.model.UiState
import com.zerir.weathersnap.domain.model.Weather
import com.zerir.weathersnap.domain.repository.CameraRepository
import com.zerir.weathersnap.domain.repository.ImageHistoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraRepositoryImpl @Inject constructor(
    private val cameraService: CameraService,
    private val imageHistoryRepository: ImageHistoryRepository
) : CameraRepository {

    override suspend fun capturePhotoWithWeatherOverlay(
        weather: Weather,
        coordinates: Coordinates,
        imageCapture: ImageCapture,
        useCelsius: Boolean,
    ): UiState<CapturedImage> {
        return try {
            // 1. Capture photo with CameraX
            val originalPhoto = cameraService.capturePhoto(imageCapture)

            // 2. Apply weather overlay
            val weatherOverlayPhoto =
                cameraService.capturePhotoWithWeatherOverlay(originalPhoto, weather, useCelsius)

            // 3. Create domain model
            val capturedImage = CapturedImage(
                imageFile = weatherOverlayPhoto,
                weather = weather,
                coordinates = coordinates,
                timestamp = System.currentTimeMillis()
            )

            // 4. Save to history (DB + file)
            val saveResult = imageHistoryRepository.saveImage(capturedImage, useCelsius)
            if (saveResult is UiState.Error) {
                return UiState.Error(saveResult.message) // If DB save fails
            }

            // 5. Return success
            UiState.Success(capturedImage)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Failed to capture or save photo")
        }
    }
}

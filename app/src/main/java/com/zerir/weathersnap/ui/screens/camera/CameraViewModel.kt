package com.zerir.weathersnap.ui.screens.camera

import androidx.camera.core.ImageCapture
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerir.weathersnap.domain.model.Coordinates
import com.zerir.weathersnap.domain.model.UiState
import com.zerir.weathersnap.domain.model.Weather
import com.zerir.weathersnap.domain.repository.CameraRepository
import com.zerir.weathersnap.ui.state.CameraState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraRepository: CameraRepository
) : ViewModel() {

    private val _cameraState = MutableStateFlow<CameraState>(CameraState.Idle)
    val cameraState: StateFlow<CameraState> = _cameraState.asStateFlow()

    private var imageCapture: ImageCapture? = null

    fun setImageCapture(capture: ImageCapture) {
        imageCapture = capture
    }

    fun handleCameraPermissionDenied() {
        _cameraState.value = CameraState.PermissionDenied
    }

    fun handleCameraError(message: String) {
        _cameraState.value = CameraState.Error(message)
    }

    fun capturePhotoWithWeather(weather: Weather, coordinates: Coordinates) {
        val capture = imageCapture
        if (capture == null) {
            _cameraState.value = CameraState.Error("Camera not ready")
            return
        }

        viewModelScope.launch {
            _cameraState.value = CameraState.Loading

            when (val result =
                cameraRepository.capturePhotoWithWeatherOverlay(weather, coordinates, capture)) {
                is UiState.Success -> {
                    _cameraState.value = CameraState.Success(result.data)
                }

                is UiState.Error -> {
                    _cameraState.value = CameraState.Error(result.message)
                }

                is UiState.Loading -> {}
            }
        }
    }

    fun resetCameraState() {
        _cameraState.value = CameraState.Idle
    }
}
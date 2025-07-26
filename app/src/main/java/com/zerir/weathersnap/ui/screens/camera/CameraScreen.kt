package com.zerir.weathersnap.ui.screens.camera

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zerir.weathersnap.ui.components.CameraBottomControls
import com.zerir.weathersnap.ui.components.CameraOverlay
import com.zerir.weathersnap.ui.components.FullImageView
import com.zerir.weathersnap.ui.components.NoWeatherFallback
import com.zerir.weathersnap.ui.screens.SharedDataViewModel

@Composable
fun CameraScreen(
    sharedDataViewModel: SharedDataViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cameraViewModel: CameraViewModel = hiltViewModel()
    val cameraState by cameraViewModel.cameraState.collectAsState()
    val weatherData by sharedDataViewModel.currentWeatherData.collectAsState()

    var showCelsius by rememberSaveable { mutableStateOf(true) }

    var showFullImage by remember { mutableStateOf(false) }
    var fullImagePath by remember { mutableStateOf<String?>(null) }

    if (showFullImage && fullImagePath != null) {
        FullImageView(
            imagePath = fullImagePath,
            onClose = { showFullImage = false }
        )
        return
    }

    weatherData?.let { (weather, coordinates) ->
        Box(modifier = modifier.fillMaxSize()) {
            CameraPreview(
                onImageCaptureReady = { imageCapture ->
                    cameraViewModel.setImageCapture(imageCapture)
                },
                onError = { cameraViewModel.handleCameraError(it) }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                CameraOverlay(
                    weather,
                    showCelsius,
                    modifier = Modifier.padding(top = 32.dp)
                )

                CameraBottomControls(
                    modifier = Modifier.padding(bottom = 24.dp),
                    cameraState = cameraState,
                    onCapture = {
                        cameraViewModel.onEvent(
                            CameraEvent.CapturePhoto(weather, coordinates, showCelsius)
                        )
                    },
                    onRetry = { cameraViewModel.onEvent(CameraEvent.Reset) },
                    onImageClick = { path ->
                        fullImagePath = path
                        showFullImage = true
                    },
                    onBack = onNavigateBack,
                    showCelsius = showCelsius,
                    onToggleUnits = { showCelsius = it }
                )
            }
        }
    } ?: NoWeatherFallback(onNavigateBack)
}

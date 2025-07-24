package com.zerir.weathersnap.ui.screens.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.zerir.weathersnap.ui.screens.SharedDataViewModel
import com.zerir.weathersnap.ui.state.CameraState
import com.zerir.weathersnap.ui.state.getCapturedImageOrNull
import com.zerir.weathersnap.ui.state.getErrorMessageOrNull
import com.zerir.weathersnap.utils.hasCameraPermission
import com.zerir.weathersnap.utils.requestCameraPermission

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    sharedDataViewModel: SharedDataViewModel,
    onNavigateBack: () -> Unit
) {
    val cameraViewModel: CameraViewModel = hiltViewModel()
    val cameraState by cameraViewModel.cameraState.collectAsState()
    val weatherData by sharedDataViewModel.currentWeatherData.collectAsState()

    // State for full-screen image viewing
    var showFullImage by remember { mutableStateOf(false) }
    var fullImagePath by remember { mutableStateOf<String?>(null) }

    val cameraPermissionState = requestCameraPermission(
        onPermissionGranted = { /* Camera permission granted */ },
        onPermissionDenied = { cameraViewModel.handleCameraPermissionDenied() }
    )

    // Full-screen image overlay
    if (showFullImage && fullImagePath != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable { showFullImage = false }, // Tap to close
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = fullImagePath,
                contentDescription = "Full size captured photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            // Close button
            Button(
                onClick = { showFullImage = false },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Text("✕ Close")
            }
        }
    } else {
        // Normal camera UI
        weatherData?.let { (weather, coordinates) ->
            if (cameraPermissionState.hasCameraPermission()) {
                // Show camera UI with permission granted
                Box(modifier = Modifier.fillMaxSize()) {
                    // Camera Preview (Full Screen)
                    CameraPreview(
                        modifier = Modifier.fillMaxSize(),
                        onImageCaptureReady = { imageCapture ->
                            cameraViewModel.setImageCapture(imageCapture)
                        },
                        onError = { errorMessage ->
                            cameraViewModel.handleCameraError(errorMessage)
                        }
                    )

                    // Overlay UI Elements
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top: Weather Info
                        Text(
                            text = """
                                📍 ${weather.locationName}
                                🌡️ ${weather.temperatureCelsius}°C
                                ${weather.description}
                            """.trimIndent(),
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )

                        // Bottom: Camera Controls
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            when (cameraState) {
                                is CameraState.Idle -> {
                                    Button(
                                        onClick = {
                                            cameraViewModel.capturePhotoWithWeather(
                                                weather,
                                                coordinates
                                            )
                                        },
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    ) {
                                        Text("📸 Capture Weather Photo")
                                    }
                                }

                                is CameraState.Loading -> {
                                    CircularProgressIndicator(color = Color.White)
                                    Text(
                                        "Capturing photo...",
                                        color = Color.White,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }

                                is CameraState.Success -> {
                                    val capturedImage = cameraState.getCapturedImageOrNull()!!

                                    // Clickable thumbnail
                                    AsyncImage(
                                        model = capturedImage.imageFile.absolutePath,
                                        contentDescription = "Captured photo with weather (tap to view full size)",
                                        modifier = Modifier
                                            .size(200.dp)
                                            .padding(8.dp)
                                            .clickable {
                                                fullImagePath = capturedImage.imageFile.absolutePath
                                                showFullImage = true
                                            },
                                        contentScale = ContentScale.Crop
                                    )

                                    Text(
                                        text = "✅ Photo captured!\nTap image to view full size\n${capturedImage.imageFile.name}",
                                        color = Color.Green,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    Button(
                                        onClick = { cameraViewModel.resetCameraState() }
                                    ) {
                                        Text("📸 Capture Another")
                                    }
                                }

                                is CameraState.Error -> {
                                    Text(
                                        text = "❌ Error: ${cameraState.getErrorMessageOrNull()!!}",
                                        color = Color.Red,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Button(
                                        onClick = { cameraViewModel.resetCameraState() }
                                    ) {
                                        Text("🔄 Try Again")
                                    }
                                }

                                is CameraState.PermissionDenied -> {
                                    Text(
                                        text = "❌ Camera permission denied",
                                        color = Color.Red,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                            }

                            Button(
                                onClick = onNavigateBack,
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("⬅️ Back to Home")
                            }
                        }
                    }
                }
            } else {
                // Show permission request
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Camera permission is required to take photos")
                    Button(
                        onClick = { cameraPermissionState.launchMultiplePermissionRequest() }
                    ) {
                        Text("Grant Camera Permission")
                    }
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Back to Home")
                    }
                }
            }
        } ?: run {
            // No weather data fallback
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No weather data available.\nPlease get weather from Home screen first.",
                    textAlign = TextAlign.Center,
                    color = Color.Red
                )
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("⬅️ Back to Home")
                }
            }
        }
    }
}
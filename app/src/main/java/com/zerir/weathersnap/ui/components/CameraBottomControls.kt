package com.zerir.weathersnap.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zerir.weathersnap.ui.screens.camera.CameraState
import com.zerir.weathersnap.ui.screens.camera.getCapturedImageOrNull
import com.zerir.weathersnap.ui.screens.camera.getErrorMessageOrNull
import com.zerir.weathersnap.ui.theme.WeatherSnapTheme

@Composable
fun CameraBottomControls(
    cameraState: CameraState,
    showCelsius: Boolean,
    onToggleUnits: (Boolean) -> Unit,
    onCapture: () -> Unit,
    onRetry: () -> Unit,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        when (cameraState) {
            is CameraState.Idle -> {
                // Unit toggle row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Button(
                        onClick = { onToggleUnits(true) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showCelsius) MaterialTheme.colorScheme.inversePrimary
                            else MaterialTheme.colorScheme.primary
                        )
                    ) { Text("°C") }

                    Button(
                        onClick = { onToggleUnits(false) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!showCelsius) MaterialTheme.colorScheme.inversePrimary
                            else MaterialTheme.colorScheme.primary
                        )
                    ) { Text("°F") }
                }

                Spacer(Modifier.height(8.dp))

                Button(onClick = onCapture) {
                    Text("Capture Weather Photo")
                }
            }

            is CameraState.Loading -> {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Text(
                    "Capturing photo...",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            is CameraState.Success -> {
                val capturedImage = cameraState.getCapturedImageOrNull()!!
                AsyncImage(
                    model = capturedImage.imageFile.absolutePath,
                    contentDescription = "Captured photo",
                    modifier = Modifier
                        .size(64.dp)
                        .padding(8.dp)
                        .clickable { onImageClick(capturedImage.imageFile.absolutePath) },
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                Button(onClick = onRetry) { Text("Capture Another") }
            }

            is CameraState.Error -> {
                Text(
                    "Error: ${cameraState.getErrorMessageOrNull()}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Button(onClick = onRetry) { Text("Try Again") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CameraBottomControlsPreview() {
    WeatherSnapTheme {
        CameraBottomControls(
            cameraState = CameraState.Idle,
            showCelsius = true,
            onToggleUnits = {},
            onCapture = {},
            onRetry = {},
            onImageClick = {},
        )
    }
}

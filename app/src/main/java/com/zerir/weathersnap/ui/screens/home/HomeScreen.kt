package com.zerir.weathersnap.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.zerir.weathersnap.domain.model.UiState
import com.zerir.weathersnap.domain.model.getDataOrNull
import com.zerir.weathersnap.domain.model.getErrorMessageOrNull
import com.zerir.weathersnap.ui.state.LocationState
import com.zerir.weathersnap.utils.hasLocationPermission
import com.zerir.weathersnap.utils.requestLocationPermissions

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val weatherState by viewModel.weatherState.collectAsState()
    val locationState by viewModel.locationState.collectAsState()

    val permissionsState = requestLocationPermissions(
        onPermissionGranted = { viewModel.onPermissionGranted() },
        onPermissionDenied = { viewModel.onPermissionDenied() },
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Home Screen (Weather History)",
            textAlign = TextAlign.Center
        )

        Button(
            onClick = {
                if (permissionsState.hasLocationPermission()) {
                    viewModel.loadCurrentLocationWeather()
                } else {
                    permissionsState.launchMultiplePermissionRequest()
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Get Current Location Weather")
        }

        Button(
            onClick = { viewModel.loadWeather() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Test Weather API")
        }

        locationState?.let { state ->
            when (state) {
                is LocationState.Loading -> {
                    Text(
                        text = "Getting location...",
                        color = Color.Blue,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                is LocationState.Success -> {
                    Text(
                        text = "Coordinates: ${state.coordinates.latitude}, ${state.coordinates.longitude}",
                        color = Color.Green,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                is LocationState.Error -> {
                    Text(
                        text = "Location Error: ${state.message}",
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                is LocationState.PermissionDenied -> {
                    Text(
                        text = "Location permission denied",
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        weatherState?.let { state ->
            when (state) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }

                is UiState.Success -> {
                    val weather = state.getDataOrNull()!!
                    Text(
                        text = """
                        Location: ${weather.locationName}
                        Temperature: ${weather.temperatureCelsius}°C / ${weather.temperatureFahrenheit}°F
                        Condition: ${weather.description}
                        Humidity: ${weather.humidity}%
                        Wind: ${weather.windSpeedKph} km/h
                    """.trimIndent(),
                        modifier = Modifier.padding(16.dp)
                    )
                }

                is UiState.Error -> {
                    Text(
                        text = "Error: ${state.getErrorMessageOrNull()!!}",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        Button(
            onClick = onNavigateToCamera,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Capture Weather Photo")
        }

        Button(
            onClick = onNavigateToSettings,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Settings")
        }
    }
}
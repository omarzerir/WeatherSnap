package com.zerir.weathersnap.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.zerir.weathersnap.domain.model.Coordinates
import com.zerir.weathersnap.domain.model.Weather
import com.zerir.weathersnap.domain.model.UiState
import com.zerir.weathersnap.domain.model.getErrorMessageOrNull
import com.zerir.weathersnap.ui.theme.WeatherSnapTheme

@Composable
fun WeatherSection(
    locationState: UiState<Coordinates>?,
    weatherState: UiState<Weather>?,
    hasLocationPermission: Boolean,
    lastUpdated: String?,
    showCelsius: Boolean,
    onToggleUnits: () -> Unit,
    onRequestPermission: () -> Unit,
    onRetryWeather: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        when {
            !hasLocationPermission -> {
                WeatherPermissionCard(onRequestPermission)
            }

            locationState is UiState.Loading -> {
                WeatherLoadingCard("Loading location...")
            }

            weatherState is UiState.Loading -> {
                WeatherLoadingCard("Loading weather...")
            }

            weatherState is UiState.Success -> {
                WeatherSuccessCard(
                    weather = weatherState.data,
                    lastUpdated = lastUpdated ?: "Just now",
                    showCelsius = showCelsius,
                    onToggleUnits = onToggleUnits
                )
            }

            weatherState is UiState.Error || locationState is UiState.Error -> {
                val message = locationState?.getErrorMessageOrNull()
                        ?: weatherState?.getErrorMessageOrNull()
                WeatherErrorCard(
                    errorMessage = message ?: "Something went wrong",
                    onRetry = onRetryWeather
                )
            }

            else -> {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun WeatherPermissionCard(
    onRequestPermission: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Location permission required",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRequestPermission) {
                Text("Grant Permission")
            }
        }
    }
}

@Composable
fun WeatherLoadingCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(message)
        }
    }
}

@Composable
fun WeatherSuccessCard(
    weather: Weather,
    lastUpdated: String,
    showCelsius: Boolean,
    onToggleUnits: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Temperature & toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (showCelsius) "${weather.temperatureCelsius.toInt()}°C"
                    else "${weather.temperatureFahrenheit.toInt()}°F",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onToggleUnits) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Toggle Units",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            // Weather condition
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                SubcomposeAsyncImage(
                    model = weather.iconUrl,
                    contentDescription = weather.description,
                    modifier = Modifier.size(32.dp),
                    error = { Text("🌤️", fontSize = MaterialTheme.typography.bodyLarge.fontSize) }
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = weather.description,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(Modifier.height(6.dp))

            // Location
            Text(
                text = "📍 ${weather.locationName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(6.dp))

            // Humidity & Wind
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text("💧 ${weather.humidity}%", style = MaterialTheme.typography.bodySmall)
                Text(
                    "💨 ${weather.windSpeedKph.toInt()} km/h",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(4.dp))

            // Last updated
            Text(
                text = "Updated: $lastUpdated",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun WeatherErrorCard(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Weather failed: $errorMessage",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherSection_PermissionDenied_Preview() {
    WeatherSnapTheme {
        WeatherPermissionCard(onRequestPermission = {})
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherSection_Loading_Preview() {
    WeatherSnapTheme {
        WeatherLoadingCard("Loading weather...")
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherSuccessCardPreview() {
    WeatherSnapTheme {
        WeatherSuccessCard(
            weather = Weather(
                temperatureCelsius = 25.0,
                temperatureFahrenheit = 77.0,
                description = "Sunny",
                iconUrl = "//cdn.weatherapi.com/weather/64x64/day/113.png",
                windSpeedKph = 12.5,
                humidity = 42,
                locationName = "Seattle, WA"
            ),
            lastUpdated = "2 min ago",
            showCelsius = true,
            onToggleUnits = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherSection_Error_Preview() {
    WeatherSnapTheme {
        WeatherErrorCard(
            errorMessage = "Failed to fetch weather data.",
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherSection_Full_Preview() {
    WeatherSnapTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            WeatherPermissionCard(onRequestPermission = {})
            Spacer(modifier = Modifier.height(8.dp))
            WeatherLoadingCard("Loading weather...")
            Spacer(modifier = Modifier.height(8.dp))
            WeatherSuccessCard(
                weather = Weather(
                    temperatureCelsius = 28.7,
                    temperatureFahrenheit = 83.6,
                    description = "Partly Cloudy",
                    iconUrl = "//cdn.weatherapi.com/weather/64x64/day/116.png",
                    windSpeedKph = 12.0,
                    humidity = 55,
                    locationName = "Cairo, EG"
                ),
                lastUpdated = "5 min ago",
                showCelsius = true,
                onToggleUnits = {}
            )
            Spacer(modifier = Modifier.height(8.dp))
            WeatherErrorCard(
                errorMessage = "Location not found.",
                onRetry = {}
            )
        }
    }
}

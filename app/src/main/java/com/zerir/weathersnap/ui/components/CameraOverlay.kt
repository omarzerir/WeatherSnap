package com.zerir.weathersnap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zerir.weathersnap.domain.model.Weather
import com.zerir.weathersnap.ui.theme.WeatherSnapTheme

@Composable
fun CameraOverlay(
    weather: Weather,
    showCelsius: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x66000000))
            .padding(8.dp)
    ) {
        Text(
            text = "📍 ${weather.locationName}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (showCelsius) "🌡️ ${weather.temperatureCelsius}°C"
            else "🌡️ ${weather.temperatureFahrenheit}°F",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
        Text(
            text = weather.description,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CameraOverlayPreview() {
    WeatherSnapTheme {
        CameraOverlay(
            weather = Weather(
                temperatureCelsius = 26.0,
                temperatureFahrenheit = 78.0,
                description = "Sunny",
                iconUrl = "",
                windSpeedKph = 10.0,
                humidity = 50,
                locationName = "Cairo"
            ),
            showCelsius = true
        )
    }
}

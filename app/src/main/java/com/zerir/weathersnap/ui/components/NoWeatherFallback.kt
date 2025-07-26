package com.zerir.weathersnap.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zerir.weathersnap.ui.theme.WeatherSnapTheme

@Composable
fun NoWeatherFallback(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No weather data available.\nPlease get weather from Home screen first.",
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onNavigateBack,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("⬅️ Back to Home")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoWeatherFallbackPreview() {
    WeatherSnapTheme {
        NoWeatherFallback {}
    }
}

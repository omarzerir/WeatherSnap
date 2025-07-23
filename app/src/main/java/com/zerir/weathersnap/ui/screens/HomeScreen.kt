package com.zerir.weathersnap.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Home Screen\n(Weather History)",
            textAlign = TextAlign.Center
        )

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
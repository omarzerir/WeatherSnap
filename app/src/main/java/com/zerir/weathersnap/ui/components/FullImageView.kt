package com.zerir.weathersnap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zerir.weathersnap.ui.theme.WeatherSnapTheme

@Composable
fun FullImageView(
    imagePath: String?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { onClose() },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imagePath,
            contentDescription = "Full size photo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        Button(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(48.dp)
        ) {
            Text("✕ Close")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FullImageViewPreview() {
    WeatherSnapTheme {
        FullImageView(
            imagePath = "https://placekitten.com/800/600",
            onClose = {}
        )
    }
}

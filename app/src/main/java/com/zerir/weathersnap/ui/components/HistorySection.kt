package com.zerir.weathersnap.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import com.zerir.weathersnap.data.entity.CapturedImageEntity
import com.zerir.weathersnap.ui.theme.WeatherSnapTheme

@Composable
fun HistorySection(
    imageHistory: LazyPagingItems<CapturedImageEntity>,
    canNavigateToCamera: Boolean,
    onImageClick: (CapturedImageEntity) -> Unit,
    onDeleteImage: (CapturedImageEntity) -> Unit,
    onCaptureFirst: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (imageHistory.itemCount == 0) {
        EmptyHistoryState(
            showCaptureButton = canNavigateToCamera,
            onCapturePhotoClick = onCaptureFirst,
            modifier = modifier
        )
        return
    }

    // Grid with LazyColumn
    Column(modifier = modifier.fillMaxWidth()) {
        val rows = imageHistory.itemSnapshotList.items.chunked(2)
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (item in row) {
                    ImageHistoryCard(
                        imageEntity = item,
                        onImageClick = onImageClick,
                        onDeleteClick = { onDeleteImage(item) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun EmptyHistoryState(
    showCaptureButton: Boolean,
    onCapturePhotoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Camera icon
        Icon(
            imageVector = Icons.Outlined.Photo,
            contentDescription = "No photos yet",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = "No Weather Photos Yet",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        Text(
            text = "Capture your first weather photo to start building your weather history collection.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        if (showCaptureButton) {
            Spacer(modifier = Modifier.height(24.dp))

            // Call to action button
            Button(
                onClick = onCapturePhotoClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text("Take Your First Weather Photo")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyHistoryState_WithButton_Preview() {
    WeatherSnapTheme {
        EmptyHistoryState(
            showCaptureButton = true,
            onCapturePhotoClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyHistoryState_NoButton_Preview() {
    WeatherSnapTheme {
        EmptyHistoryState(
            showCaptureButton = false,
            onCapturePhotoClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HistorySection_Grid_Preview() {
    WeatherSnapTheme {
        HistoryGridPreview(
            images = demoImages()
        )
    }
}

@Composable
private fun HistoryGridPreview(
    images: List<CapturedImageEntity>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        modifier = modifier
    ) {
        items(images.size) { index ->
            val imageEntity = images[index]
            ImageHistoryCard(
                imageEntity = imageEntity,
                onImageClick = {},
                onDeleteClick = {}
            )
        }
    }
}

private fun demoImages(): List<CapturedImageEntity> =
    (1..6).map {
        CapturedImageEntity(
            id = it.toString(),
            filePath = "",
            fileName = "weather_${it}.jpg",
            locationName = "Location $it",
            latitude = 0.0,
            longitude = 0.0,
            temperatureCelsius = 20.0 + it,
            temperatureFahrenheit = 60.0 + it * 2,
            weatherDescription = "Sunny",
            humidity = 40 + it,
            windSpeedKph = 10.0 + it,
            iconUrl = "//cdn.weatherapi.com/weather/64x64/day/113.png",
            timestamp = System.currentTimeMillis(),
            fileSize = 1024L * it,
            isCelsius = true
        )
    }

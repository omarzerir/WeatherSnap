package com.zerir.weathersnap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.zerir.weathersnap.data.entity.CapturedImageEntity
import com.zerir.weathersnap.ui.theme.WeatherSnapTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ImageHistoryCard(
    imageEntity: CapturedImageEntity,
    onImageClick: (CapturedImageEntity) -> Unit,
    onDeleteClick: (CapturedImageEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onImageClick(imageEntity) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column {
            // Image with overlay delete button
            Box {
                SubcomposeAsyncImage(
                    model = imageEntity.filePath,
                    contentDescription = "Weather photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Downloading,
                                contentDescription = "Loading image",
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.errorContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BrokenImage,
                                contentDescription = "Failed to load image",
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                )

                IconButton(
                    onClick = { onDeleteClick(imageEntity) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete photo",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Info section
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                // Top Row: Temp + Weather Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text =
                            if(imageEntity.isCelsius) "${imageEntity.temperatureCelsius.toInt()}°C"
                            else "${imageEntity.temperatureFahrenheit.toInt()}°F",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SubcomposeAsyncImage(
                            model = imageEntity.iconUrl,
                            contentDescription = imageEntity.weatherDescription,
                            modifier = Modifier.size(20.dp),
                            error = { Text("🌤️", fontSize = 14.sp) }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = imageEntity.weatherDescription,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Location
                Text(
                    text = "📍 ${imageEntity.locationName}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Date
                Text(
                    text = formatTimestamp(imageEntity.timestamp),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
fun ImageHistoryCardPreview() {
    WeatherSnapTheme {
        ImageHistoryCard(
            imageEntity = CapturedImageEntity(
                id = "1",
                filePath = "",
                fileName = "weather_2025-01-24.jpg",
                locationName = "San Francisco, CA",
                latitude = 37.7749,
                longitude = -122.4194,
                temperatureCelsius = 22.5,
                temperatureFahrenheit = 72.5,
                weatherDescription = "Partly Cloudy",
                humidity = 65,
                windSpeedKph = 15.2,
                iconUrl = "//cdn.weatherapi.com/weather/64x64/day/116.png",
                timestamp = System.currentTimeMillis(),
                fileSize = 2048576L,
                isCelsius = false,
            ),
            onImageClick = {},
            onDeleteClick = {}
        )
    }
}

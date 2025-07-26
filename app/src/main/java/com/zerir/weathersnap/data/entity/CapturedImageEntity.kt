package com.zerir.weathersnap.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "captured_images")
data class CapturedImageEntity(
    @PrimaryKey
    val id: String,
    val filePath: String,
    val fileName: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val temperatureCelsius: Double,
    val temperatureFahrenheit: Double,
    val weatherDescription: String,
    val humidity: Int,
    val windSpeedKph: Double,
    val iconUrl: String,
    val timestamp: Long,
    val fileSize: Long,
    val isCelsius: Boolean,
)
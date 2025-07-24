package com.zerir.weathersnap.domain.model

import java.io.File

data class CapturedImage(
    val imageFile: File,
    val weather: Weather,
    val coordinates: Coordinates,
    val timestamp: Long = System.currentTimeMillis()
)
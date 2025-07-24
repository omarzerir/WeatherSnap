package com.zerir.weathersnap.domain.model

data class Weather(
    val temperatureCelsius: Double,
    val temperatureFahrenheit: Double,
    val description: String,
    val iconUrl: String,
    val windSpeedKph: Double,
    val humidity: Int,
    val locationName: String
)
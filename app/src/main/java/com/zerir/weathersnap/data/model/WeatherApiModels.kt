package com.zerir.weathersnap.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherApiResponse(
    val current: CurrentWeather,
    val location: Location
)

@JsonClass(generateAdapter = true)
data class CurrentWeather(
    @Json(name = "temp_c") val tempCelsius: Double,
    @Json(name = "temp_f") val tempFahrenheit: Double,
    val condition: Condition,
    @Json(name = "wind_kph") val windKph: Double,
    val humidity: Int
)

@JsonClass(generateAdapter = true)
data class Condition(
    val text: String,
    val icon: String
)

@JsonClass(generateAdapter = true)
data class Location(
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double
)
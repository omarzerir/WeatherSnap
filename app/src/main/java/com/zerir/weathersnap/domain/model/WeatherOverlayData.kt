package com.zerir.weathersnap.domain.model

data class WeatherOverlayData(
    val temperature: String,
    val temperatureUnit: String,
    val condition: String,
    val humidity: String,
    val windSpeed: String,
    val locationName: String,
    val iconUrl: String,
    val timestamp: String
) {
    companion object {
        fun fromWeather(
            weather: Weather,
            usesCelsius: Boolean = true
        ): WeatherOverlayData {
            val temp =
                if (usesCelsius) "${weather.temperatureCelsius.toInt()}"
                else "${weather.temperatureFahrenheit.toInt()}"
            val unit = if (usesCelsius) "°C" else "°F"

            return WeatherOverlayData(
                temperature = temp,
                temperatureUnit = unit,
                condition = weather.description,
                humidity = "${weather.humidity}%",
                windSpeed = "${weather.windSpeedKph} km/h",
                locationName = weather.locationName,
                iconUrl = weather.iconUrl,
                timestamp = java.text.SimpleDateFormat(
                    "MMM dd, yyyy HH:mm",
                    java.util.Locale.getDefault()
                ).format(java.util.Date())
            )
        }
    }
}
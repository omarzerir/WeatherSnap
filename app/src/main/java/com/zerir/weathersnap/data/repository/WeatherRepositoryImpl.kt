package com.zerir.weathersnap.data.repository

import com.zerir.weathersnap.data.remoteDatasource.WeatherApiService
import com.zerir.weathersnap.domain.model.UiState
import com.zerir.weathersnap.domain.model.Weather
import com.zerir.weathersnap.domain.repository.WeatherRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApiService: WeatherApiService
) : WeatherRepository {

    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): UiState<Weather> {
        return try {
            val coordinates = "$latitude,$longitude"
            val response = weatherApiService.getCurrentWeather(coordinates)

            val weather = Weather(
                temperatureCelsius = response.current.tempCelsius,
                temperatureFahrenheit = response.current.tempFahrenheit,
                description = response.current.condition.text,
                iconUrl = "https:${response.current.condition.icon}",
                windSpeedKph = response.current.windKph,
                humidity = response.current.humidity,
                locationName = response.location.name
            )

            UiState.Success(weather)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Failed to fetch weather data")
        }
    }
}
package com.zerir.weathersnap.domain.repository

import com.zerir.weathersnap.domain.model.UiState
import com.zerir.weathersnap.domain.model.Weather

interface WeatherRepository {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): UiState<Weather>
}
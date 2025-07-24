package com.zerir.weathersnap.domain.repository

import com.zerir.weathersnap.domain.model.Coordinates
import com.zerir.weathersnap.domain.model.UiState

interface LocationRepository {
    suspend fun getCurrentLocation(): UiState<Coordinates>
}
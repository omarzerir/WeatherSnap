package com.zerir.weathersnap.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun setShowCelsius(value: Boolean)
    suspend fun getShowCelsius(): Boolean
    val showCelsiusFlow: Flow<Boolean>
}

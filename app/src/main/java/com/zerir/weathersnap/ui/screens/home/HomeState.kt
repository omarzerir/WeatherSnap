package com.zerir.weathersnap.ui.screens.home

import androidx.paging.PagingData
import com.zerir.weathersnap.data.entity.CapturedImageEntity
import com.zerir.weathersnap.domain.model.Coordinates
import com.zerir.weathersnap.domain.model.UiState
import com.zerir.weathersnap.domain.model.Weather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class HomeState(
    val locationState: UiState<Coordinates>? = null,
    val weatherState: UiState<Weather>? = null,
    val lastUpdatedTime: String? = null,
    val imageHistory: Flow<PagingData<CapturedImageEntity>> = emptyFlow(),
    val isRefreshingWeather: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val canNavigateToCamera: Boolean = false,
    val showCelsius: Boolean = true,
    val defaultCelsius: Boolean = true,
)
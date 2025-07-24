package com.zerir.weathersnap.ui.state

import com.zerir.weathersnap.domain.model.Coordinates

sealed class LocationState {
    data object Loading : LocationState()
    data class Success(val coordinates: Coordinates) : LocationState()
    data class Error(val message: String) : LocationState()
    data object PermissionDenied : LocationState()
}
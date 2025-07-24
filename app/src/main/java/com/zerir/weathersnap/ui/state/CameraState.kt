package com.zerir.weathersnap.ui.state

import com.zerir.weathersnap.domain.model.CapturedImage

sealed class CameraState {
    data object Idle : CameraState()
    data object Loading : CameraState()
    data class Success(val capturedImage: CapturedImage) : CameraState()
    data class Error(val message: String) : CameraState()
    data object PermissionDenied : CameraState()
}

fun CameraState.getCapturedImageOrNull(): CapturedImage? = when (this) {
    is CameraState.Success -> capturedImage
    else -> null
}

fun CameraState.getErrorMessageOrNull(): String? = when (this) {
    is CameraState.Error -> message
    else -> null
}
package com.zerir.weathersnap.utils

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun requestLocationPermissions(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
): MultiplePermissionsState {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(permissionsState.allPermissionsGranted) {
        when {
            permissionsState.allPermissionsGranted -> {
                onPermissionGranted()
            }
            permissionsState.shouldShowRationale -> {
                onPermissionDenied()
            }
            !permissionsState.allPermissionsGranted -> {
                onPermissionDenied()
            }
        }
    }

    return permissionsState
}

@OptIn(ExperimentalPermissionsApi::class)
fun MultiplePermissionsState.hasLocationPermission(): Boolean {
    return this.allPermissionsGranted
}
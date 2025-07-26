package com.zerir.weathersnap.utils

import android.Manifest
import androidx.compose.runtime.*
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
class PermissionManager(
    private val permissionState: MultiplePermissionsState
) {
    // Internal callback storage (accessible for LaunchedEffect)
    internal var onGranted: (() -> Unit)? = null
    internal var onDenied: ((Boolean) -> Unit)? = null

    // Check permission state
    val isGranted: Boolean get() = permissionState.allPermissionsGranted
    val isPermanentlyDenied: Boolean
        get() = permissionState.permissions.any {
            it.isPermanentlyDenied()
        }

    // Request with optional callbacks
    fun request(
        onGranted: (() -> Unit)? = null,
        onDenied: ((permanently: Boolean) -> Unit)? = null
    ) {
        if (isGranted) {
            onGranted?.invoke()
            return
        }

        // Store callbacks
        this.onGranted = onGranted
        this.onDenied = onDenied

        permissionState.launchMultiplePermissionRequest()
    }

    // Listen to permission changes
    @Composable
    fun Listen(onPermissionChange: (granted: Boolean) -> Unit) {
        LaunchedEffect(isGranted) {
            onPermissionChange(isGranted)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberLocationPermissionManager(): PermissionManager {
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val manager = remember { PermissionManager(permissionState) }

    // Handle callbacks when permission state changes
    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (manager.onGranted != null || manager.onDenied != null) {
            if (permissionState.allPermissionsGranted) {
                manager.onGranted?.invoke()
            } else {
                manager.onDenied?.invoke(manager.isPermanentlyDenied)
            }
            // Clear callbacks
            manager.onGranted = null
            manager.onDenied = null
        }
    }

    return manager
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberCameraPermissionManager(): PermissionManager {
    val permissionState = rememberMultiplePermissionsState(
        listOf(Manifest.permission.CAMERA)
    )

    val manager = remember { PermissionManager(permissionState) }

    // Handle callbacks when permission state changes
    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (manager.onGranted != null || manager.onDenied != null) {
            if (permissionState.allPermissionsGranted) {
                manager.onGranted?.invoke()
            } else {
                manager.onDenied?.invoke(manager.isPermanentlyDenied)
            }
            // Clear callbacks
            manager.onGranted = null
            manager.onDenied = null
        }
    }

    return manager
}

@OptIn(ExperimentalPermissionsApi::class)
fun PermissionState.isPermanentlyDenied(): Boolean {
    return !status.isGranted && !status.shouldShowRationale
}
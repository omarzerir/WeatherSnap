package com.zerir.weathersnap.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

sealed class PermissionSettingsDialogData(
    val title: String,
    val message: String,
    val icon: ImageVector,
) {
    data object Location : PermissionSettingsDialogData(
        "Location Permission Required",
        "Location access has been permanently denied. Please enable location permission in your device settings.",
        Icons.Default.LocationOff
    )

    data object Camera : PermissionSettingsDialogData(
        "Camera Permission Required",
        "Camera access has been permanently denied. Please enable camera permission in your device settings.",
        Icons.Default.Camera
    )
}

@Composable
fun PermissionSettingsDialog(
    showDialog: Boolean,
    data: PermissionSettingsDialogData?,
    onDismiss: () -> Unit,
    onGoToSettings: () -> Unit
) {
    if (showDialog && data != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = data.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onGoToSettings
                ) {
                    Text(
                        text = "Open Settings",
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Cancel")
                }
            },
            icon = {
                Icon(
                    imageVector = data.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        )
    }
}
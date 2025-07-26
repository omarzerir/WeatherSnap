package com.zerir.weathersnap.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zerir.weathersnap.data.entity.CapturedImageEntity

@Composable
fun DeleteDialog(
    showDialog: Boolean,
    entity: CapturedImageEntity?,
    onDismiss: () -> Unit,
    onDelete: (entity: CapturedImageEntity) -> Unit
) {
    if (showDialog && entity != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Delete Photo",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete this photo?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { onDelete(entity) }
                ) {
                    Text(
                        text = "Delete",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(
                        "Cancel",
                        color = Color.Gray
                    )
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
            }
        )
    }
}
package com.capevi.app.ui.settings_composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LogoutConfirmationDialog(
    onLogout: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() }, // Dismiss when touching outside the dialog
        title = {
            Text(text = "Confirm Logout")
        },
        text = {
            Text(text = "Are you sure you want to log out?")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onLogout()
                    onDismiss() // Close the dialog
                },
            ) {
                androidx.compose.material3.Text("Log Out")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                androidx.compose.material3.Text("Cancel")
            }
        },
        modifier = Modifier.fillMaxWidth(),
    )
}

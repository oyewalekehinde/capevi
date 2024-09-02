package com.capevi.app.ui.settings_composable

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.capevi.navigation.Routes
import com.google.firebase.auth.FirebaseAuth

fun logout(
    context: Context,
    navController: NavController,
) {
    FirebaseAuth.getInstance().signOut()
    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
    navController.navigate(Routes.Login.route) {
        popUpTo(Routes.MainScreen.route) { inclusive = true }
    }
}

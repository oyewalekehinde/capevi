package com.capevi.shared.utils.photo_capture

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.capevi.viewmodels.SharedViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(
    ExperimentalPermissionsApi::class,
)
@Composable
fun mainScreen(
    navController: NavHostController,
    viewModel: SharedViewModel,
    isVideo: Boolean,
) {
    val cameraPermissionState =
        rememberPermissionState(
            android.Manifest.permission.CAMERA,
        )
    LaunchedEffect(Unit) {
        println("mainCameraScreen ViewModel instance: ${viewModel.hashCode()}")
    }
    mainContent(
        navController = navController,
        hasPermission = cameraPermissionState.status.isGranted,
        viewModel,
        isVideo,
        onRequestPermission = cameraPermissionState::launchPermissionRequest,
    )
}

@Composable
private fun mainContent(
    navController: NavHostController,
    hasPermission: Boolean,
    viewModel: SharedViewModel,
    isVideo: Boolean,
    onRequestPermission: () -> Unit,
) {
    if (hasPermission) {
        cameraScreen(navController, viewModel, isVideo)
    } else {
//        NoPermissionScreen(onRequestPermission)
    }
}

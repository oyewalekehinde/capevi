package com.capevi.app.ui.home_composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.capevi.app.ui.settings_composable.SettingsScreen
import com.capevi.navigation.Routes
import com.capevi.viewmodels.AuthViewModel
import com.capevi.viewmodels.CaseViewModel
import com.capevi.viewmodels.SharedViewModel

@Composable
fun MainInitalScreen(
    navController: NavHostController,
    caseViewModel: CaseViewModel,
    authViewModel: AuthViewModel,
    viewModel: SharedViewModel,
) {
    var selectedScreen by remember { mutableStateOf(Routes.Home.route) }
    val user by authViewModel.user.collectAsState()
    Scaffold(
        bottomBar = { BottomNavBar(selectedScreen, userModel = user!!) { selectedScreen = it } },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            when (selectedScreen) {
                Routes.Home.route -> homeScreen(navController, caseViewModel, authViewModel, viewModel)
                Routes.Profile.route -> SettingsScreen(user!!, navController, authViewModel)
            }
        }
    }
}

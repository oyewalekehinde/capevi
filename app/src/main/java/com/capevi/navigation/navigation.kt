package com.capevi.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.capevi.app.ui.MediaDisplayScreen
import com.capevi.app.ui.auth_composable.login
import com.capevi.app.ui.auth_composable.onboarding
import com.capevi.app.ui.auth_composable.signUp
import com.capevi.app.ui.case_composables.createCase
import com.capevi.app.ui.case_composables.seeAllCases
import com.capevi.app.ui.case_composables.viewCase
import com.capevi.app.ui.home_composables.MainInitalScreen
import com.capevi.app.ui.settings_composable.privacyPolicyScreen
import com.capevi.app.ui.settings_composable.termsOfUseScreen
import com.capevi.data.model.CaseModel
import com.capevi.shared.utils.RecordingScreen
import com.capevi.shared.utils.photo_capture.mainScreen
import com.capevi.viewmodels.AuthViewModel
import com.capevi.viewmodels.CaseViewModel
import com.capevi.viewmodels.FileViewModel
import com.capevi.viewmodels.SharedViewModel
import com.google.gson.reflect.TypeToken
import gson
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun navigation() {
    val authViewModel = hiltViewModel<AuthViewModel>()
    val viewModel = hiltViewModel<SharedViewModel>()
    val caseViewModel = hiltViewModel<CaseViewModel>()
    val fileViewModel = hiltViewModel<FileViewModel>()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.Onboarding.route) {
        composable(route = Routes.Onboarding.route) {
            onboarding(navController = navController)
        }
        composable(route = Routes.SignUp.route) {
            signUp(navController = navController, authViewModel)
        }
        composable(route = Routes.Login.route) {
            login(navController = navController, authViewModel)
        }
        composable(route = Routes.MainScreen.route) {
            MainInitalScreen(navController, caseViewModel, authViewModel, viewModel)
        }
        composable(route = Routes.Recording.route) {
            RecordingScreen(navController, viewModel)
        }
        composable(route = Routes.TermOfUse.route) {
            termsOfUseScreen(navController)
        }
        composable(route = Routes.PrivacyPolicy.route) {
            privacyPolicyScreen(navController)
        }

        composable(
            route = Routes.CreateCase.route,
            arguments = listOf(navArgument("case") { nullable = true }),
        ) { backStackEntry ->
            val caseModelJson = backStackEntry.arguments?.getString("case")

            val caseModel =
                caseModelJson?.let {
                    val decodedJson = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                    gson.fromJson(decodedJson, CaseModel::class.java)
                }
            createCase(navController = navController, viewModel, caseViewModel, fileViewModel, authViewModel, caseModel)
        }
        composable(
            route = Routes.CameraScreen.route + "/{isVideo}",
            arguments = listOf(navArgument("isVideo") { type = NavType.StringType }),
        ) { backStackEntry ->
            val value = backStackEntry.arguments?.getString("isVideo")
            mainScreen(
                navController = navController,
                viewModel,
                value == "true",
            )
        }
        composable(
            route = Routes.ViewCase.route + "/{case}",
            arguments = listOf(navArgument("case") { type = NavType.StringType }),
        ) { backStackEntry ->
            val caseModelJson = backStackEntry.arguments?.getString("case")

            val caseModel =
                caseModelJson?.let {
                    val decodedJson = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                    gson.fromJson(decodedJson, CaseModel::class.java)
                }
            viewCase(caseModel!!, navController, caseViewModel, authViewModel)
        }
        composable(
            route = Routes.SeeAllCases.route + "/{cases}",
            arguments = listOf(navArgument("cases") { type = NavType.StringType }),
        ) { backStackEntry ->
            val caseModelJson = backStackEntry.arguments?.getString("cases")
            val caseListType = object : TypeToken<List<CaseModel>>() {}.type
            val caseList: List<CaseModel>? =
                caseModelJson?.let {
                    val decodedJson = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
//                    gson.fromJson(decodedJson, CaseModel::class.java)
                    gson.fromJson(decodedJson, caseListType)
                }

//            val caseModel =
//                caseModelJson?.let {
//                    val decodedJson = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
//                    gson.fromJson(decodedJson, CaseModel::class.java)
//                }
            seeAllCases(navController, viewModel, caseList ?: emptyList())
        }

        composable(
            route = Routes.MediaScreen.route + "/{fileUri}",
            arguments = listOf(navArgument("fileUri") { type = NavType.StringType }),
        ) { backStackEntry ->
            val fileUriString = backStackEntry.arguments?.getString("fileUri")
            val fileUri =
                fileUriString?.let {
                    val decodedJson = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())

                    Uri.parse(decodedJson)
                }

            val file = File(fileUri?.path)

            MediaDisplayScreen(file = file)
        }
    }
}

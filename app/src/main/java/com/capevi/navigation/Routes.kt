package com.capevi.navigation

sealed class Routes(
    val route: String,
) {
    object Onboarding : Routes("onboarding")

    object SignUp : Routes("signIn")

    object Login : Routes("login")

    object Home : Routes("home")

    object MainScreen : Routes("mainScreen")

    object Recording : Routes("recording")

    object TermOfUse : Routes("termOfUse")

    object PrivacyPolicy : Routes("privacyPolicy")

    object CreateCase : Routes("createCase/{case}") {
        fun createRoute(case: String?) = "createCase/${case ?: "null"}"
    }

    object CameraScreen : Routes("cameraScreen")

    object ViewCase : Routes("viewCase")

    object SeeAllCases : Routes("seeAllCaseÏ")

    object MediaScreen : Routes("mediaScreen")

    object Profile : Routes("profile")
}

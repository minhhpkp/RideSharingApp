package com.ridesharingapp.driversideapp.navigation

sealed class Screen {
    object SignUpScreen : Screen()
    object TermsAndConditionsScreen : Screen()
    object LoginScreen : Screen()
    object ForgotPasswordScreen: Screen()
    object HomeScreen : Screen()
}

package com.ridesharingapp.passengersideapp.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

enum class Args {
    isFromSignUp,
    isFromLogin,
    email
}

sealed class Screen(
    route: String,
    arguments: List<NamedNavArgument> = listOf()
) : com.ridesharingapp.common.navigation.Screen(route, arguments) {
    object WelcomeScreen : Screen("welcome")
    object SignUpScreen : Screen(
        route = "signup",
        arguments = listOf(navArgument(name = Args.isFromLogin.name) { type = NavType.BoolType})
    )
    object TermsAndConditionsScreen : Screen("terms")
    object LoginScreen : Screen(
        route = "login",
        arguments = listOf(
            navArgument(name = Args.isFromSignUp.name) { type = NavType.BoolType },
            navArgument(name = Args.email.name) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    )
    object ForgotPasswordScreen : Screen("forgot_password")
    object HomeScreen : Screen("home")
}
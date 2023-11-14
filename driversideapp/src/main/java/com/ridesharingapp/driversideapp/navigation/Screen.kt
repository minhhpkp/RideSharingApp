package com.ridesharingapp.driversideapp.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

enum class Args() {
    isFromLogin,
    isFromSignUp,
    isUserCollision,
    vehicleType,
    licensePlate,
    email,
    username
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
            navArgument(name = Args.isUserCollision.name) {
                type = NavType.BoolType
                defaultValue = false
            },
            navArgument(name = Args.email.name) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
            navArgument(name = Args.vehicleType.name) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
            navArgument(name = Args.licensePlate.name) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    )
    object ForgotPasswordScreen : Screen("forgot_password")
    object HomeScreen : Screen("home")
    object UpdateRolesScreen: Screen(
        route = "updateRolesScreen",
        arguments = listOf(
            navArgument(name = Args.username.name) { type = NavType.StringType },
            navArgument(name = Args.vehicleType.name) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
            navArgument(name = Args.licensePlate.name) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    )
}
package com.ridesharingapp.passengersideapp.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ridesharingapp.common.data.forgotpassword.ForgotPasswordViewModel
import com.ridesharingapp.common.screens.ForgotPasswordScreen
import com.ridesharingapp.common.screens.TermsAndConditionsScreen
import com.ridesharingapp.passengersideapp.data.home.HomeViewModel
import com.ridesharingapp.passengersideapp.data.login.PassengerLoginViewModel
import com.ridesharingapp.passengersideapp.data.registration.SignUpViewModel
import com.ridesharingapp.passengersideapp.data.welcome.WelcomeViewModel
import com.ridesharingapp.passengersideapp.screens.HomeScreen
import com.ridesharingapp.passengersideapp.screens.PassengerLoginScreen
import com.ridesharingapp.passengersideapp.screens.SignUpScreen
import com.ridesharingapp.passengersideapp.screens.WelcomeScreen


@Composable
fun ScreenNavigation(auth: FirebaseAuth, db: FirebaseFirestore) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = if (auth.currentUser == null) Screen.WelcomeScreen.route
        else Screen.HomeScreen.route
    ) {
        composable(Screen.WelcomeScreen.route) {
            WelcomeScreen(
                welcomeViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return WelcomeViewModel(navController) as T
                        }
                    }
                )
            )
        }

        composable(
            route = Screen.LoginScreen.routeWithArgs,
            arguments = Screen.LoginScreen.arguments,
            content = loginContent(navController, auth, db)
        )

        composable(
            route = Screen.ForgotPasswordScreen.route,
            content = forgotPasswordContent(auth)
        )

        composable(
            route = Screen.SignUpScreen.routeWithArgs,
            arguments = Screen.SignUpScreen.arguments,
            content = signUpContent(navController, auth, db)
        )

        composable(
            route = Screen.TermsAndConditionsScreen.route,
            content = termsContent
        )

        composable(Screen.HomeScreen.route) {
            HomeScreen(
                homeViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return HomeViewModel(navController) as T
                        }
                    }
                )
            )
        }
    }
}

fun loginContent(
    navController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore
): @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit) {
    return { backStackEntry ->
        val isFromSignUp = backStackEntry.arguments?.getBoolean(Args.isFromSignUp.name)
            ?: throw Exception("The argument isFromSignUp passed to /login is null.")
        val initEmail = backStackEntry.arguments?.getString(Args.email.name)
        PassengerLoginScreen(
            passengerLoginViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return PassengerLoginViewModel(
                            navController,
                            auth,
                            db,
                            isFromSignUp,
                            initEmail
                        ) as T
                    }
                }
            )
        )
    }
}

fun forgotPasswordContent(
    auth: FirebaseAuth
): @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit) {
    return {
        ForgotPasswordScreen(
            forgotPasswordViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return ForgotPasswordViewModel(auth = auth) as T
                    }
                }
            )
        )
    }
}

fun signUpContent(
    navController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore
): @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit) = { backStackEntry ->
    val isFromLogin = backStackEntry.arguments?.getBoolean(Args.isFromLogin.name)
        ?: throw Exception("The argument isFromLogin passed to /signup is null")
    SignUpScreen(
        signUpViewModel = viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    // Create a SavedStateHandle for this ViewModel from extras
                    val savedStateHandle = extras.createSavedStateHandle()
                    return SignUpViewModel(
                        navController = navController,
                        auth = auth,
                        isFromLoginScreen = isFromLogin,
                        savedStateHandle = savedStateHandle,
                        db = db
                    ) as T
                }
            }
        )
    )
}

val termsContent: @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit) = {
    TermsAndConditionsScreen()
}

package com.ridesharingapp.driversideapp.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
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
import com.ridesharingapp.driversideapp.RideSharingApp
import com.ridesharingapp.driversideapp.data.home.HomeViewModel
import com.ridesharingapp.driversideapp.data.login.DriverLoginViewModel
import com.ridesharingapp.driversideapp.data.registration.SignUpViewModel
import com.ridesharingapp.driversideapp.data.updateroles.UpdateRolesViewModel
import com.ridesharingapp.driversideapp.data.welcome.WelcomeViewModel
import com.ridesharingapp.driversideapp.screens.DriverLoginScreen
import com.ridesharingapp.driversideapp.screens.HomeScreen
import com.ridesharingapp.driversideapp.screens.SignUpScreen
import com.ridesharingapp.driversideapp.screens.UpdateRolesScreen
import com.ridesharingapp.driversideapp.screens.WelcomeScreen

@Composable
fun ScreenNavigation(auth: FirebaseAuth, db: FirebaseFirestore) {
    val navController = rememberNavController()
    NavHost(
        navController = navController, 
        startDestination = if (auth.currentUser == null) Screen.WelcomeScreen.route
        else Screen.HomeScreen.route
    ) {
        composable(
            route = Screen.WelcomeScreen.route,
            content = welcomeContent(navController)
        )
        
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
            content = { TermsAndConditionsScreen() }
        )

        composable(
            route = Screen.UpdateRolesScreen.routeWithArgs,
            arguments = Screen.UpdateRolesScreen.arguments,
            content = updateRolesContent(navController, auth, db)
        )
        
        composable(
            route = Screen.HomeScreen.route,
            content = homeContent(navController)
        )
    }
}

fun welcomeContent(
    navController: NavHostController
): @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit) = {
    WelcomeScreen(welcomeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WelcomeViewModel(navController) as T
            }
        }
    ))
}

fun loginContent(
    navController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore
): @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit) = { backStackEntry ->
    val isFromSignUp = backStackEntry.arguments?.getBoolean(Args.isFromSignUp.name)
        ?: throw Exception("The argument isFromSignUp passed to /login is null.")
    val isUserCollision = backStackEntry.arguments?.getBoolean(Args.isUserCollision.name)!!
    val email = backStackEntry.arguments?.getString(Args.email.name)
    val vehicleType = backStackEntry.arguments?.getString(Args.vehicleType.name)
    val licensePlate = backStackEntry.arguments?.getString(Args.licensePlate.name)
    println("LoginNav $isUserCollision $email $vehicleType $licensePlate")

    DriverLoginScreen(
        driverLoginViewModel = viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DriverLoginViewModel(
                        navController = navController,
                        auth = auth,
                        db = db,
                        initEmail = email,
                        vehicleType = vehicleType,
                        licensePlate = licensePlate,
                        isFromSignUp = isFromSignUp,
                        userCollision = isUserCollision
                    ) as T
                }
            }
        )
    )
}

fun forgotPasswordContent(
    auth: FirebaseAuth
): @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit) = {
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

fun updateRolesContent(
    navController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore
): @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit) = { backStackEntry ->
    val vehicleType = backStackEntry.arguments?.getString(Args.vehicleType.name)
    val licensePlate = backStackEntry.arguments?.getString(Args.licensePlate.name)
    val name = backStackEntry.arguments?.getString(Args.username.name)!!
    UpdateRolesScreen(
        updateRolesViewModel = viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    val userDocumentReference =
                        db.collection("Users").document(auth.currentUser!!.uid)
                    // Get the Application object from extras
                    val application = checkNotNull(extras[APPLICATION_KEY])
                    return UpdateRolesViewModel(
                        navController,
                        auth,
                        userDocumentReference,
                        name,
                        vehicleType,
                        licensePlate,
                        application as RideSharingApp
                    ) as T
                }
            }
        )
    )
}

fun homeContent(
    navController: NavHostController
): @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit) = {
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
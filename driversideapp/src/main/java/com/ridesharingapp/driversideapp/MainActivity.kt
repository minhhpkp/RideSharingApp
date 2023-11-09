package com.ridesharingapp.driversideapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.ridesharingapp.common.data.forgotpassword.ForgotPasswordViewModel
import com.ridesharingapp.common.data.login.LoginViewModel
import com.ridesharingapp.common.data.registration.RegistrationViewModel
import com.ridesharingapp.common.navigation.AppRouter
import com.ridesharingapp.common.screens.ForgotPasswordScreen
import com.ridesharingapp.common.screens.LoginScreen
import com.ridesharingapp.common.screens.SignUpScreen
import com.ridesharingapp.common.screens.TermsAndConditionsScreen
import com.ridesharingapp.driversideapp.data.home.HomeViewModel
import com.ridesharingapp.driversideapp.navigation.Screen
import com.ridesharingapp.driversideapp.screens.HomeScreen

val appRouter = AppRouter<Screen>(Screen.SignUpScreen)

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setContent {
            RideSharingApp(auth)
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            currentUser.email?.let { Log.d("MainActivity.onStart", it) }
            appRouter.navigateTo(Screen.HomeScreen)
        }
    }
}

@Composable
fun RideSharingApp(auth: FirebaseAuth) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Crossfade(targetState = appRouter.currentScreen, label = "") {
            when (it.value) {
                is Screen.SignUpScreen -> {
                    SignUpScreen(
                        registrationViewModel = viewModel<RegistrationViewModel<Screen>>(
                            factory = object : ViewModelProvider.Factory {
                                @Suppress("UNCHECKED_CAST")
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return RegistrationViewModel(
                                        appRouter = appRouter,
                                        termsAndConditionScreen = Screen.TermsAndConditionsScreen,
                                        loginScreen = Screen.LoginScreen,
                                        authSuccessScreen = Screen.HomeScreen,
                                        auth = auth
                                    ) as T
                                }
                            }
                        )
                    )
                }
                is Screen.TermsAndConditionsScreen -> {
                    TermsAndConditionsScreen(
                        appRouter = appRouter,
                        signUpScreen = Screen.SignUpScreen
                    )
                }
                is Screen.LoginScreen -> {
                    LoginScreen(
                        loginViewModel = viewModel<LoginViewModel<Screen>>(
                            factory = object : ViewModelProvider.Factory {
                                @Suppress("UNCHECKED_CAST")
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return LoginViewModel(
                                        appRouter = appRouter,
                                        signUpScreen = Screen.SignUpScreen,
                                        authSuccessScreen = Screen.HomeScreen,
                                        auth = auth,
                                        forgotPasswordScreen = Screen.ForgotPasswordScreen
                                    ) as T
                                }
                            }
                        )
                    )
                }
                is Screen.ForgotPasswordScreen -> {
                    ForgotPasswordScreen(
                        forgotPasswordViewModel = viewModel<ForgotPasswordViewModel<Screen>>(
                            factory = object : ViewModelProvider.Factory {
                                @Suppress("UNCHECKED_CAST")
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return ForgotPasswordViewModel(auth, appRouter, Screen.LoginScreen) as T
                                }
                            }
                        )
                    )
                }
                is Screen.HomeScreen -> {
                    HomeScreen(
                        homeViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                @Suppress("UNCHECKED_CAST")
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return HomeViewModel(appRouter) as T
                                }
                            }
                        )
                    )
                }
            }
        }
    }
}
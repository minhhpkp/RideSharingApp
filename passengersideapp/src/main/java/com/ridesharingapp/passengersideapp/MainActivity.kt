package com.ridesharingapp.passengersideapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ridesharingapp.common.navigation.AppRouter
import com.ridesharingapp.common.screens.LoginScreen
import com.ridesharingapp.common.screens.SignUpScreen
import com.ridesharingapp.common.screens.TermsAndConditionsScreen
import com.ridesharingapp.passengersideapp.data.home.HomeViewModel
import com.ridesharingapp.passengersideapp.data.login.PassengerSideLoginViewModel
import com.ridesharingapp.passengersideapp.data.registration.PassengerSideRegistrationViewModel
import com.ridesharingapp.passengersideapp.navigation.Screen
import com.ridesharingapp.passengersideapp.screens.HomeScreen

val appRouter = AppRouter<Screen>(Screen.SignUpScreen)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RideSharingApp()
        }
    }
}

@Composable
fun RideSharingApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Crossfade(targetState = appRouter.currentScreen, label = "") {
            when (it.value) {
                is Screen.SignUpScreen -> {
                    SignUpScreen(
                        registrationViewModel = PassengerSideRegistrationViewModel(appRouter),
                        appRouter = appRouter,
                        termsAndConditionsScreen = Screen.TermsAndConditionsScreen,
                        loginScreen = Screen.LoginScreen
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
                        loginViewModel = PassengerSideLoginViewModel(appRouter),
                        appRouter = appRouter,
                        signUpScreen = Screen.SignUpScreen)
                }
                is Screen.HomeScreen -> {
                    HomeScreen(
                        homeViewModel = HomeViewModel(appRouter)
                    )
                }
            }
        }
    }
}
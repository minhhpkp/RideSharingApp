package com.ridesharingapp.passengersideapp

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
import com.google.firebase.auth.FirebaseAuth
import com.ridesharingapp.common.data.login.LoginViewModel
import com.ridesharingapp.common.data.registration.RegistrationViewModel
import com.ridesharingapp.common.navigation.AppRouter
import com.ridesharingapp.common.screens.LoginScreen
import com.ridesharingapp.common.screens.SignUpScreen
import com.ridesharingapp.common.screens.TermsAndConditionsScreen
import com.ridesharingapp.passengersideapp.data.home.HomeViewModel
import com.ridesharingapp.passengersideapp.navigation.Screen
import com.ridesharingapp.passengersideapp.screens.HomeScreen

val appRouter = AppRouter<Screen>(Screen.SignUpScreen)

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setContent {
            ScreenNavigation(auth = auth)
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
fun ScreenNavigation(auth: FirebaseAuth) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Crossfade(targetState = appRouter.currentScreen, label = "") {
            when (it.value) {
                is Screen.SignUpScreen -> {
                    SignUpScreen(
                        registrationViewModel = RegistrationViewModel(
                            appRouter = appRouter,
                            termsAndConditionScreen = Screen.TermsAndConditionsScreen,
                            loginScreen = Screen.LoginScreen,
                            authSuccessScreen = Screen.HomeScreen,
                            auth = auth
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
                        loginViewModel = LoginViewModel(
                            appRouter = appRouter,
                            signUpScreen = Screen.SignUpScreen,
                            authSuccessScreen = Screen.HomeScreen,
                            auth = auth
                        )
                    )
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
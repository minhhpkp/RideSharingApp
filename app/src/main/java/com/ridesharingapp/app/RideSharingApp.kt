package com.ridesharingapp.app

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ridesharingapp.navigation.AppRouter
import com.ridesharingapp.navigation.Screen
import com.ridesharingapp.screens.LoginScreen
import com.ridesharingapp.screens.SignUpScreen
import com.ridesharingapp.screens.TermsAndConditionsScreen

@Composable
fun RideSharingApp(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White
    ) {
        Crossfade(targetState = AppRouter.currentScreen, label = "") {
            when (it.value) {
                is Screen.SignUpScreen -> {
                    SignUpScreen()
                }
                is Screen.TermsAndConditionsScreen -> {
                    TermsAndConditionsScreen()
                }
                is Screen.LoginScreen -> {
                    LoginScreen()
                }
            }
        }
    }
}
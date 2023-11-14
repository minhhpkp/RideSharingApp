package com.ridesharingapp.passengersideapp.data.welcome

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.ridesharingapp.passengersideapp.navigation.Screen

class WelcomeViewModel(private val navController: NavHostController) : ViewModel() {
    fun onSignUpClicked() {
        navController.navigate(Screen.SignUpScreen.withArgs("false"))
    }
    fun onSignInClicked() {
        navController.navigate(Screen.LoginScreen.withArgs("false"))
    }
}
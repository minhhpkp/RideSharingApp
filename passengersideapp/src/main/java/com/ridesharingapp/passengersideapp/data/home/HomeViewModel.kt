package com.ridesharingapp.passengersideapp.data.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.ridesharingapp.passengersideapp.navigation.Screen

class HomeViewModel(private val navController: NavHostController) : ViewModel() {
    var signOutInProgress by mutableStateOf(false)

    fun signOut() {
        signOutInProgress = true
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signOut()

        firebaseAuth.addAuthStateListener{
            if (it.currentUser == null) {
                signOutInProgress = false
                navController.popBackStack(
                    route = Screen.HomeScreen.route,
                    inclusive = true
                )
                navController.navigate(Screen.WelcomeScreen.route)
            }
        }
    }
}
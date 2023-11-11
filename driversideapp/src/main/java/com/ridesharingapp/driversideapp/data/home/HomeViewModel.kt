package com.ridesharingapp.driversideapp.data.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.ridesharingapp.common.navigation.AppRouter
import com.ridesharingapp.driversideapp.navigation.Screen

class HomeViewModel(private val appRouter: AppRouter<Screen>) : ViewModel() {
    val homeUIState by mutableStateOf(HomeUIState())

    fun onEvent(event: HomeUIEvent) {
        when (event) {
            is HomeUIEvent.ContactPassenger -> {

            }
            is HomeUIEvent.PickUpPassenger -> {

            }
        }
    }
    fun signOut() {
        homeUIState.signOutInProgress.value = true
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signOut()

        firebaseAuth.addAuthStateListener{
            if (it.currentUser == null) {
                homeUIState.signOutInProgress.value = false
                appRouter.navigateTo(Screen.LoginScreen)
            }
        }
    }
}
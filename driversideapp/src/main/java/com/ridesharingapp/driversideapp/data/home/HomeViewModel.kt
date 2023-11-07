package com.ridesharingapp.driversideapp.data.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.ridesharingapp.common.navigation.AppRouter
import com.ridesharingapp.driversideapp.navigation.Screen

class HomeViewModel(private val appRouter: AppRouter<Screen>) : ViewModel() {
    var signOutInProgress by mutableStateOf(false)

    fun signOut() {
        signOutInProgress = true
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signOut()

        firebaseAuth.addAuthStateListener{
            if (it.currentUser == null) {
                signOutInProgress = false
                appRouter.navigateTo(Screen.LoginScreen)
            }
        }
    }
}
package com.ridesharingapp.data.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.ridesharingapp.navigation.AppRouter
import com.ridesharingapp.navigation.Screen

class HomeViewModel: ViewModel() {
    private var _tag = HomeViewModel::class.simpleName
    var signOutInProgress by mutableStateOf(false)

    fun signOut() {
        signOutInProgress = true
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signOut()

        firebaseAuth.addAuthStateListener{
            if (it.currentUser == null) {
                Log.d(_tag, "Signed out successfully")
                signOutInProgress = false
                AppRouter.navigateTo(Screen.LoginScreen)
            } else {
                Log.d(_tag, "Failed to sign out")
            }
        }
    }
}

package com.ridesharingapp.passengersideapp.data.login

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.ridesharingapp.common.data.login.LoginViewModel
import com.ridesharingapp.common.navigation.AppRouter
import com.ridesharingapp.passengersideapp.navigation.Screen

class PassengerSideLoginViewModel(private val appRouter: AppRouter<Screen>) : LoginViewModel() {
    private var loginInProgress = mutableStateOf(false)

    override fun isLoginInProgress(): MutableState<Boolean> {
        return loginInProgress
    }

    override fun onLoginButtonClick() {
        loginInProgress.value = true
        FirebaseAuth
            .getInstance()
            .signInWithEmailAndPassword(
                loginUIState.email,
                loginUIState.password
            )
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    loginInProgress.value = false
                    Log.d("Login", "Success")
                    appRouter.navigateTo(Screen.HomeScreen)
                }
            }
    }
}
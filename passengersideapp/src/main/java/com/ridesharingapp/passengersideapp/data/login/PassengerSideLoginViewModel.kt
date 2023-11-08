package com.ridesharingapp.passengersideapp.data.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.ridesharingapp.common.data.login.LoginViewModel
import com.ridesharingapp.common.navigation.AppRouter
import com.ridesharingapp.passengersideapp.navigation.Screen

class PassengerSideLoginViewModel(private val appRouter: AppRouter<Screen>) : LoginViewModel() {
    private var loginInProgress by mutableStateOf(false)
    private var loginFailed by mutableStateOf(false)

    override fun isLoginInProgress(): Boolean {
        return loginInProgress
    }

    override fun isLoginFailed(): Boolean {
        return loginFailed
    }

    override fun dismissFailureMessage() {
        loginFailed = false
    }

    override fun onLoginButtonClick() {
        loginInProgress = true
        FirebaseAuth
            .getInstance()
            .signInWithEmailAndPassword(
                loginUIState.email,
                loginUIState.password
            )
            .addOnCompleteListener{
                loginInProgress = false
                if (it.isSuccessful) {
                    Log.d("Login", "signInWithEmail:success")
                    appRouter.navigateTo(Screen.HomeScreen)
                } else {
                    Log.w("Login", "signInWithEmail:failure", it.exception)
                    loginFailed = true
                }
            }
    }
}
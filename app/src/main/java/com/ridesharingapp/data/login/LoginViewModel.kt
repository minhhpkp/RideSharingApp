package com.ridesharingapp.data.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.ridesharingapp.data.rules.Validator
import com.ridesharingapp.navigation.AppRouter
import com.ridesharingapp.navigation.Screen

class LoginViewModel : ViewModel() {
    val loginUIState by mutableStateOf(LoginUIState())
    var allValidationPassed by mutableStateOf(false)
    var loginInProgress by mutableStateOf(false)

    fun onEvent(event: LoginUIEvent) {
        when(event) {
            is LoginUIEvent.EmailChanged -> {
                loginUIState.email = event.email
                val emailValidationResult = Validator.validateEmail(event.email)
                loginUIState.emailErrorStatus.value = !emailValidationResult.status
            }
            is LoginUIEvent.PasswordChanged -> {
                loginUIState.password = event.password
                val passwordValidationResult = Validator.validatePassword(event.password)
                loginUIState.passwordErrorStatus.value = !passwordValidationResult.status
            }
            is LoginUIEvent.LoginButtonClicked -> {
                login()
            }
        }
        allValidationPassed =
            !loginUIState.emailErrorStatus.value
            && !loginUIState.passwordErrorStatus.value
    }

    private fun login() {
        loginInProgress = true
        FirebaseAuth
            .getInstance()
            .signInWithEmailAndPassword(
                loginUIState.email,
                loginUIState.password
            )
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    AppRouter.navigateTo(Screen.HomeScreen)
                }
                loginInProgress = false
            }
            .addOnFailureListener{
                Log.d(_tag, "Login Failed")
            }
    }

    private val _tag = LoginViewModel::class.simpleName
}
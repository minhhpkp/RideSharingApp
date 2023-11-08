package com.ridesharingapp.common.data.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.ridesharingapp.common.data.rules.Validator
import com.ridesharingapp.common.navigation.AppRouter

class LoginViewModel<Screen>(
    private val appRouter: AppRouter<Screen>,
    private val signUpScreen: Screen,
    private val authSuccessScreen: Screen,
    private val auth: FirebaseAuth
) : ViewModel() {
    val loginUIState by mutableStateOf(LoginUIState())
    private var allValidationPassed by mutableStateOf(false)
    private var loginInProgress by mutableStateOf(false)
    private var loginFailed by mutableStateOf(false)

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
                onEvent(LoginUIEvent.EmailChanged(loginUIState.email))
                onEvent(LoginUIEvent.PasswordChanged(loginUIState.password))
                if (allValidationPassed) onLoginButtonClick()
            }
            is LoginUIEvent.SignUpTextClicked -> {
                appRouter.navigateTo(signUpScreen)
            }
            is LoginUIEvent.BackButtonClicked -> {
                appRouter.navigateTo(signUpScreen)
            }
        }
        allValidationPassed = !loginUIState.emailErrorStatus.value
            && !loginUIState.passwordErrorStatus.value
    }

    fun isAllValidationPassed(): Boolean {
        return allValidationPassed
    }

    fun isLoginInProgress(): Boolean {
        return loginInProgress
    }

    fun isLoginFailed(): Boolean {
        return loginFailed
    }

    fun dismissFailureMessage() {
        loginFailed = false
    }

    private fun onLoginButtonClick() {
        loginInProgress = true
        auth.signInWithEmailAndPassword(
                loginUIState.email,
                loginUIState.password
            )
            .addOnCompleteListener{
                loginInProgress = false
                if (it.isSuccessful) {
                    Log.d("Login", "signInWithEmail:success")
                    appRouter.navigateTo(authSuccessScreen)
                } else {
                    Log.w("Login", "signInWithEmail:failure", it.exception)
                    loginFailed = true
                }
            }
    }
}
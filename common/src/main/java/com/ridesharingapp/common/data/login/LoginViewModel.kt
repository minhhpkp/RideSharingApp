package com.ridesharingapp.common.data.login

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ridesharingapp.common.data.rules.Validator

abstract class LoginViewModel : ViewModel() {
    val loginUIState by mutableStateOf(LoginUIState())
    private var allValidationPassed by mutableStateOf(false)

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
                onLoginButtonClick()
            }
        }
        allValidationPassed =
            !loginUIState.emailErrorStatus.value
                    && !loginUIState.passwordErrorStatus.value
    }

    fun isAllValidationPassed(): Boolean {
        return allValidationPassed
    }

    abstract fun isLoginInProgress(): MutableState<Boolean>

    abstract fun onLoginButtonClick()
}
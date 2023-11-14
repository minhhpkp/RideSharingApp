package com.ridesharingapp.common.data.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.ridesharingapp.common.data.rules.Validator
import com.ridesharingapp.common.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

open class LoginViewModel(
    private val navController: NavHostController,
    private val authSuccessScreen: Screen,
    private val forgotPasswordScreen: Screen,
    private val auth: FirebaseAuth,
    private val signUpScreen: Screen,
    private val isFromSignUpScreen: Boolean,
    private val startScreen: Screen,
    initEmail: String? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        LoginUIState(
            email = initEmail,
            emailErrorStatus = initEmail?.let { !Validator.validateEmail(it).status } ?: true
        )
    )
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()
    init {
        println("LoginViewModel created")
    }

    open fun onEvent(event: LoginUIEvent) {
        when(event) {
            is LoginUIEvent.EmailChanged -> {
                _uiState.update {
                    it.copy(
                        email = event.email,
                        emailErrorStatus = !Validator.validateEmail(event.email).status
                    )
                }
            }
            is LoginUIEvent.PasswordChanged -> {
                _uiState.update {
                    it.copy(
                        password = event.password,
                        passwordErrorStatus = !Validator.validatePassword(event.password).status
                    )
                }
            }
            is LoginUIEvent.LoginButtonClicked -> {
                if (allValidationPassed(_uiState.value)) onLoginButtonClick()
            }
            is LoginUIEvent.SignUpTextClicked -> {
                if (isFromSignUpScreen) {
                    navController.popBackStack()
                } else {
                    navController.navigate(signUpScreen.withArgs("true"))
                }
            }
            is LoginUIEvent.ForgotPasswordTextClicked -> {
                navController.navigate(forgotPasswordScreen.route)
            }
            is LoginUIEvent.LoginFailedChanged -> {
                _uiState.update { it.copy(loginFailed = event.loginFailed) }
            }
            is LoginUIEvent.LoginInProgressChanged -> {
                _uiState.update { it.copy(loginInProgress = event.loginInProgress) }
            }
        }
    }

    private fun onLoginButtonClick() {
        onEvent(LoginUIEvent.LoginInProgressChanged(true))
        auth.signInWithEmailAndPassword(
                _uiState.value.email!!,
                _uiState.value.password!!
            )
            .addOnCompleteListener{ task ->
                onEvent(LoginUIEvent.LoginInProgressChanged(false))
                if (task.isSuccessful) {
                    Log.d("Login", "signInWithEmail:success")
                    navController.popBackStack(
                        route = startScreen.route,
                        inclusive = true
                    )
                    navController.navigate(authSuccessScreen.route)
                } else {
                    Log.w("Login", "signInWithEmail:failure", task.exception)
                    onEvent(LoginUIEvent.LoginFailedChanged(true))
                }
            }
    }

    fun dismissFailureMessage() {
        _uiState.update { it.copy(loginFailed = false) }
    }

    fun allValidationPassed(uiState: LoginUIState): Boolean {
        return !uiState.emailErrorStatus && !uiState.passwordErrorStatus
    }
}
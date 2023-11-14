package com.ridesharingapp.common.data.login

sealed class LoginUIEvent {
    data class EmailChanged(val email: String) : LoginUIEvent()
    data class PasswordChanged(val password: String) : LoginUIEvent()
    object LoginButtonClicked : LoginUIEvent()
    object SignUpTextClicked : LoginUIEvent()
    object ForgotPasswordTextClicked : LoginUIEvent()

    data class LoginInProgressChanged(val loginInProgress: Boolean) : LoginUIEvent()
    data class LoginFailedChanged(val loginFailed: Boolean) : LoginUIEvent()
}

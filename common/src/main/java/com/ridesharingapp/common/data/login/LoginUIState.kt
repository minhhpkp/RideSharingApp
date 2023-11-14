package com.ridesharingapp.common.data.login

data class LoginUIState(
    var email: String? = null,
    var password: String? = null,
    var emailErrorStatus: Boolean = true,
    var passwordErrorStatus: Boolean = true,

    var loginInProgress: Boolean = false,
    var loginFailed: Boolean = false
)

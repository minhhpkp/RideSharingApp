package com.ridesharingapp.common.data.forgotpassword

data class ForgotPasswordUIState(
    var email: String? = null,
    var emailErrorStatus: Boolean = false
)
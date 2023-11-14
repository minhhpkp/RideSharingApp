package com.ridesharingapp.common.data.forgotpassword

data class ForgotPasswordUIState(
    var email: String? = null,
    var emailErrorStatus: Boolean = true,
    var sendingInProgress: Boolean = false,
    var sendingResult: Int = -1
)
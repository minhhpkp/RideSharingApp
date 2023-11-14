package com.ridesharingapp.common.data.forgotpassword

sealed class ForgotPasswordUIEvent {
    data class EmailChanged(val email: String) : ForgotPasswordUIEvent()
    object SendButtonClicked : ForgotPasswordUIEvent()
}

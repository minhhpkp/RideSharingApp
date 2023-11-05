package com.ridesharingapp.data.login

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class LoginUIState (
    var email: String = "",
    var password: String = "",
    val emailErrorStatus: MutableState<Boolean> = mutableStateOf(false),
    val passwordErrorStatus: MutableState<Boolean> = mutableStateOf(false)
)
package com.ridesharingapp.data.registration

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class RegistrationUIState(
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var password: String = "",

    val firstNameErrorStatus: MutableState<Boolean> = mutableStateOf(false),
    val lastNameErrorStatus: MutableState<Boolean> = mutableStateOf(false),
    val emailErrorStatus: MutableState<Boolean> = mutableStateOf(false),
    val passwordErrorStatus: MutableState<Boolean> = mutableStateOf(false),
    val termsConditionReadState: MutableState<Boolean> = mutableStateOf(false)
)
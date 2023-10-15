package com.ridesharingapp.data

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


class LoginViewModel: ViewModel() {
    private var registrationUIState by mutableStateOf(RegistrationUIState())

    fun onEvent(event: UIEvent) {
        when (event) {
            is UIEvent.FirstNameChanged -> {
                registrationUIState.firstName = event.firstName
            }
            is UIEvent.LastNameChanged -> {
                registrationUIState.lastName = event.lastName
            }
            is UIEvent.EmailChanged -> {
                registrationUIState.email = event.email
            }
            is UIEvent.PasswordChanged -> {
                registrationUIState.password = event.password
            }
        }
    }

    private var TAG = LoginViewModel::class.simpleName

    fun printState() {
        Log.d(TAG, "Inside printState")
        Log.d(TAG, registrationUIState.toString())
    }
}
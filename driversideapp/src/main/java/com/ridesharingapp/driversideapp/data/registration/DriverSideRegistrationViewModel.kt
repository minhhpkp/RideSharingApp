package com.ridesharingapp.driversideapp.data.registration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.ridesharingapp.common.data.registration.RegistrationViewModel
import com.ridesharingapp.common.navigation.AppRouter
import com.ridesharingapp.driversideapp.navigation.Screen

class DriverSideRegistrationViewModel(private val appRouter: AppRouter<Screen>) : RegistrationViewModel() {
    private var registrationInProgress by mutableStateOf(false)

    override fun isRegistrationInProgress(): Boolean {
        return registrationInProgress
    }

    override fun onRegisterButtonClick() {
        registrationInProgress = true
        FirebaseAuth
            .getInstance()
            .createUserWithEmailAndPassword(
                registrationUIState.email,
                registrationUIState.password
            )
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    registrationInProgress = false
                    appRouter.navigateTo(Screen.HomeScreen)
                }
            }
    }
}
package com.ridesharingapp.passengersideapp.data.registration

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.ridesharingapp.common.data.registration.RegistrationViewModel
import com.ridesharingapp.common.navigation.AppRouter
import com.ridesharingapp.passengersideapp.navigation.Screen

class PassengerSideRegistrationViewModel(private val appRouter: AppRouter<Screen>) : RegistrationViewModel() {
    private var registrationInProgress by mutableStateOf(false)
    private var registrationFailed by mutableStateOf(false)

    override fun isRegistrationInProgress(): Boolean {
        return registrationInProgress
    }

    override fun isRegistrationFailed(): Boolean {
        return registrationFailed
    }

    override fun dismissFailureMessage() {
        registrationFailed = false
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
                registrationInProgress = false
                if (it.isSuccessful) {
                    Log.d("SignUp", "createUserWithEmail:success")
                    appRouter.navigateTo(Screen.HomeScreen)
                } else {
                    Log.w("SignUp", "createUserWithEmail:failure", it.exception)
                    failureMessage = if (it.exception is FirebaseAuthUserCollisionException) {
                        com.ridesharingapp.common.R.string.email_already_in_use
                    } else {
                        com.ridesharingapp.common.R.string.failed_to_sign_up_please_try_again
                    }
                    registrationFailed = true
                }
            }
    }
}
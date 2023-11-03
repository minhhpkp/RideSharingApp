package com.ridesharingapp.data

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.ridesharingapp.data.rules.Validator
import com.ridesharingapp.navigation.AppRouter
import com.ridesharingapp.navigation.Screen


class RegistrationViewModel : ViewModel() {
    val registrationUIState by mutableStateOf(RegistrationUIState())
    var allValidationPassed by mutableStateOf(false)
    var registrationInProgress by mutableStateOf(false)

    fun onEvent(event: RegistrationUIEvent) {
        when (event) {
            is RegistrationUIEvent.FirstNameChanged -> {
                registrationUIState.firstName = event.firstName
                val firstNameResult = Validator.validateFirstName(registrationUIState.firstName)
                registrationUIState.firstNameErrorStatus.value = !firstNameResult.status
            }
            is RegistrationUIEvent.LastNameChanged -> {
                registrationUIState.lastName = event.lastName
                val lastNameResult = Validator.validateLastName(registrationUIState.lastName)
                registrationUIState.lastNameErrorStatus.value = !lastNameResult.status
            }
            is RegistrationUIEvent.EmailChanged -> {
                registrationUIState.email = event.email
                val emailResult = Validator.validateEmail(registrationUIState.email)
                registrationUIState.emailErrorStatus.value = !emailResult.status
            }
            is RegistrationUIEvent.PasswordChanged -> {
                registrationUIState.password = event.password
                val passwordResult = Validator.validatePassword(registrationUIState.password)
                registrationUIState.passwordErrorStatus.value = !passwordResult.status
            }
            is RegistrationUIEvent.TermsConditionChecked -> {
                registrationUIState.termsConditionReadState.value = event.checked
            }
            is RegistrationUIEvent.RegisterButtonClicked -> {
                signUp()
            }
        }
        allValidationPassed = (
            !registrationUIState.firstNameErrorStatus.value
            && !registrationUIState.lastNameErrorStatus.value
            && !registrationUIState.emailErrorStatus.value
            && !registrationUIState.passwordErrorStatus.value
            && registrationUIState.termsConditionReadState.value
        )
    }

    private fun signUp() {
        if (allValidationPassed) {
            createUserInFireBase(
                registrationUIState.email,
                registrationUIState.password
            )
        }
    }

    private fun createUserInFireBase(email: String, password: String) {
        registrationInProgress = true
        FirebaseAuth
            .getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                Log.d(_tag, "Inside_OnCompleteListener")
                Log.d(_tag, "isSuccessful = ${it.isSuccessful}")

                registrationInProgress = false
                if (it.isSuccessful) {
                    AppRouter.navigateTo(Screen.HomeScreen)
                }
            }
            .addOnFailureListener{
                Log.d(_tag, "Inside_OnFailureListener")
                Log.d(_tag, "Exception = ${it.message}")
                Log.d(_tag, "Exception = ${it.localizedMessage}")
            }
    }

    fun signOut() {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()

        firebaseAuth.addAuthStateListener{
            if (it.currentUser == null) {
                Log.d(_tag, "Signed out successfully")
                AppRouter.navigateTo(Screen.LoginScreen)
            } else {
                Log.d(_tag, "Failed to sign out")
            }
        }
    }

    private var _tag = RegistrationViewModel::class.simpleName
    /*private fun printState() {
        Log.d(_tag, "Inside printState")
        Log.d(_tag, registrationUIState.toString())
    }*/
}
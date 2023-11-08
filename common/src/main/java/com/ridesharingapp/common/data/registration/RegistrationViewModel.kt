package com.ridesharingapp.common.data.registration

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.ridesharingapp.common.R
import com.ridesharingapp.common.data.rules.Validator
import com.ridesharingapp.common.navigation.AppRouter

class RegistrationViewModel<Screen>(
    private val appRouter: AppRouter<Screen>,
    private val termsAndConditionScreen: Screen,
    private val loginScreen: Screen,
    private val authSuccessScreen: Screen,
    private val auth: FirebaseAuth
) : ViewModel() {
    val registrationUIState by mutableStateOf(RegistrationUIState())
    private var allValidationPassed by mutableStateOf(false)
    private var registrationInProgress by mutableStateOf(false)
    private var registrationFailed by mutableStateOf(false)
    var failureMessage = R.string.failed_to_sign_up_please_try_again

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
            is RegistrationUIEvent.TermsConditionChanged -> {
                registrationUIState.termsConditionReadState.value = event.checked
            }
            is RegistrationUIEvent.RegisterButtonClicked -> {
                onEvent(RegistrationUIEvent.FirstNameChanged(registrationUIState.firstName))
                onEvent(RegistrationUIEvent.LastNameChanged(registrationUIState.lastName))
                onEvent(RegistrationUIEvent.EmailChanged(registrationUIState.email))
                onEvent(RegistrationUIEvent.PasswordChanged(registrationUIState.password))
                onEvent(RegistrationUIEvent.TermsConditionChanged(registrationUIState.termsConditionReadState.value))
                if (allValidationPassed) onRegisterButtonClick()
            }
            is RegistrationUIEvent.TermsAndConditionsTextClicked -> {
                appRouter.navigateTo(termsAndConditionScreen)
            }
            is RegistrationUIEvent.LoginTextClicked -> {
                appRouter.navigateTo(loginScreen)
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

    fun isAllValidationPassed(): Boolean {
        return allValidationPassed
    }

    fun isRegistrationInProgress(): Boolean {
        return registrationInProgress
    }

    fun isRegistrationFailed(): Boolean {
        return registrationFailed
    }

    fun dismissFailureMessage() {
        registrationFailed = false
    }

    private fun onRegisterButtonClick() {
        registrationInProgress = true
        auth.createUserWithEmailAndPassword(
                registrationUIState.email,
                registrationUIState.password
            )
            .addOnCompleteListener{
                registrationInProgress = false
                if (it.isSuccessful) {
                    Log.d("SignUp", "createUserWithEmail:success")
                    appRouter.navigateTo(authSuccessScreen)
                } else {
                    Log.w("SignUp", "createUserWithEmail:failure", it.exception)
                    failureMessage = if (it.exception is FirebaseAuthUserCollisionException) {
                        R.string.email_already_in_use
                    } else {
                        R.string.failed_to_sign_up_please_try_again
                    }
                    registrationFailed = true
                }
            }
    }
}
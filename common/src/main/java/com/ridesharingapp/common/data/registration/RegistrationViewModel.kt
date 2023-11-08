package com.ridesharingapp.common.data.registration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ridesharingapp.common.R
import com.ridesharingapp.common.data.rules.Validator

abstract class RegistrationViewModel : ViewModel() {
    val registrationUIState by mutableStateOf(RegistrationUIState())
    private var allValidationPassed by mutableStateOf(false)
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
            is RegistrationUIEvent.TermsConditionChecked -> {
                registrationUIState.termsConditionReadState.value = event.checked
            }
            is RegistrationUIEvent.RegisterButtonClicked -> {
                onEvent(RegistrationUIEvent.FirstNameChanged(registrationUIState.firstName))
                onEvent(RegistrationUIEvent.LastNameChanged(registrationUIState.lastName))
                onEvent(RegistrationUIEvent.EmailChanged(registrationUIState.email))
                onEvent(RegistrationUIEvent.PasswordChanged(registrationUIState.password))
                onEvent(RegistrationUIEvent.TermsConditionChecked(registrationUIState.termsConditionReadState.value))
                if (allValidationPassed) onRegisterButtonClick()
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

    abstract fun isRegistrationInProgress(): Boolean

    abstract fun isRegistrationFailed(): Boolean

    abstract fun dismissFailureMessage()

    abstract fun onRegisterButtonClick()
}
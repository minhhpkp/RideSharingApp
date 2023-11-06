package com.ridesharingapp.common.data.registration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ridesharingapp.common.data.rules.Validator

abstract class RegistrationViewModel : ViewModel() {
    val registrationUIState by mutableStateOf(RegistrationUIState())
    private var allValidationPassed by mutableStateOf(false)

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
                onRegisterButtonClick()
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

    abstract fun onRegisterButtonClick()
}
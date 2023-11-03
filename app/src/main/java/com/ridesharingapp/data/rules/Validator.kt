package com.ridesharingapp.data.rules

object Validator {
    private val emailRegex: Regex =
        Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    fun validateFirstName(firstName: String): ValidationResult {
        return ValidationResult(
            firstName.isNotEmpty()
                    && firstName.length >= 2
                    && firstName[0].isUpperCase()
        )
    }

    fun validateLastName(lastName: String): ValidationResult {
        return ValidationResult(
            lastName.isNotEmpty()
                    && lastName.length >= 2
                    && lastName[0].isUpperCase()
        )
    }

    fun validateEmail(email: String): ValidationResult {
        return ValidationResult(
            email.isNotEmpty()
                    && email.matches(emailRegex)
        )
    }

    fun validatePassword(password: String): ValidationResult {
        return ValidationResult(
            password.isNotEmpty()
                    && password.length >= 8
        )
    }
}

data class ValidationResult(
    val status: Boolean = false
)
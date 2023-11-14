package com.ridesharingapp.common.data.rules

object Validator {
    private val _EMAIL_REGEX_: Regex =
        Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    private val _LICENSE_PLATE_REGEX_: Regex =
        Regex("^[0-9]{2}[A-Z]-[0-9]{3}\\.[0-9]{2}\$")
    fun validateName(firstName: String): ValidationResult {
        return ValidationResult(
            firstName.isNotEmpty()
                    && firstName.length >= 2
                    && firstName[0].isUpperCase()
        )
    }

    fun validateEmail(email: String): ValidationResult {
        return ValidationResult(
            email.isNotEmpty()
                    && email.matches(_EMAIL_REGEX_)
        )
    }

    fun validatePassword(password: String): ValidationResult {
        return ValidationResult(
            password.isNotEmpty()
                    && password.length >= 8
                    && password.length <= 50
        )
    }

    fun validateLicensePlate(licensePlate: String): ValidationResult {
        return ValidationResult(
            licensePlate.isNotEmpty()
                    && licensePlate.matches(_LICENSE_PLATE_REGEX_)
        )
    }

    fun validateVehicleType(vehicleType: String): ValidationResult {
        return ValidationResult(
            vehicleType.isNotEmpty()
                    && vehicleType.length > 1
                    && vehicleType.length <= 200
        )
    }
}

data class ValidationResult(
    val status: Boolean = false
)
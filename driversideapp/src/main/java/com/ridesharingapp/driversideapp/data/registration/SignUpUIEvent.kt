package com.ridesharingapp.driversideapp.data.registration

sealed class SignUpUIEvent{
    data class FirstNameChanged(val firstName: String) : SignUpUIEvent()
    data class LastNameChanged(val lastName: String) : SignUpUIEvent()
    data class LicensePlateChanged(val licensePlate: String) : SignUpUIEvent()
    data class VehicleTypeChanged(val vehicleType: String) : SignUpUIEvent()
    data class EmailChanged(val email: String) : SignUpUIEvent()
    data class PasswordChanged(val password: String) : SignUpUIEvent()
    data class TermsConditionChanged(val checked: Boolean) : SignUpUIEvent()

    object TermsAndConditionsTextClicked : SignUpUIEvent()
    object RegisterButtonClicked : SignUpUIEvent()
    object LoginTextClicked : SignUpUIEvent()
}
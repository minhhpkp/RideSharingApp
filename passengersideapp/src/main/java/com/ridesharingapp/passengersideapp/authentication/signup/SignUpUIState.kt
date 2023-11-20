package com.ridesharingapp.passengersideapp.authentication.signup

data class SignUpUIState(
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var password: String? = null,

    var firstNameErrorStatus: Boolean = true,
    var lastNameErrorStatus: Boolean = true,
    var emailErrorStatus: Boolean = true,
    var passwordErrorStatus: Boolean = true,
    var termsConditionReadState: Boolean = true,

    var registrationInProgress: Boolean = false,
    var registrationFailed: Boolean = false,

    var displayUserCollisionMsg: Boolean = false
)
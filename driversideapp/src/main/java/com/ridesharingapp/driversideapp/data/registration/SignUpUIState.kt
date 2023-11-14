package com.ridesharingapp.driversideapp.data.registration

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignUpUIState(
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var password: String? = null,
    var licensePlate: String? = null,
    var vehicleType: String? = null,

    var firstNameErrorStatus: Boolean = true,
    var lastNameErrorStatus: Boolean = true,
    var emailErrorStatus: Boolean = true,
    var passwordErrorStatus: Boolean = true,
    var termsConditionReadState: Boolean = false,
    var licensePlateErrorStatus: Boolean = true,
    var vehicleTypeErrorStatus: Boolean = true,

    var registrationInProgress: Boolean = false,
    var registrationFailed: Boolean = false
) : Parcelable
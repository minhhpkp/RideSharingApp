package com.ridesharingapp.driversideapp.data.updateroles

data class UpdateRolesUIState (
    var vehicleType: String? = null,
    var licensePlate: String? = null,
    var vehicleTypeErrorStatus: Boolean = true,
    var licensePlateErrorStatus: Boolean = true,

    val headerText: String = "",
    var result: Int = -1
)
package com.ridesharingapp.driversideapp.data.updateroles

sealed class UpdateRolesUIEvent {
    data class LicensePlateChanged(val licensePlate: String) : UpdateRolesUIEvent()
    data class VehicleTypeChanged(val vehicleType: String) : UpdateRolesUIEvent()
    object UpdateButtonClicked : UpdateRolesUIEvent()
}
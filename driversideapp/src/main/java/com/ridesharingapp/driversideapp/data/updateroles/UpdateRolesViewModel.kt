//package com.ridesharingapp.driversideapp.data.updateroles
//
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.navigation.NavHostController
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.DocumentReference
//import com.ridesharingapp.common.data.rules.Validator
//import com.ridesharingapp.driversideapp.RideSharingApp
//import com.ridesharingapp.driversideapp.navigation.Screen
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//
//class UpdateRolesViewModel(
//    private val navController: NavHostController,
//    private val auth: FirebaseAuth,
//    private val userDocumentReference: DocumentReference,
//    name: String,
//    vehicleType: String?,
//    licensePlate: String?,
//    private val app: RideSharingApp
//) : ViewModel() {
//    private val _uiState: MutableStateFlow<UpdateRolesUIState>
//    val uiState: StateFlow<UpdateRolesUIState>
//    init {
//        app.updateRolesInProgress = true
//        app.updateRolesSuccess = false
//        println("Update created $name $vehicleType $licensePlate")
//        val vehicleError =
//            if (vehicleType != null) !Validator.validateVehicleType(vehicleType).status else true
//        val licenseError =
//            if (licensePlate != null) !Validator.validateLicensePlate(licensePlate).status else true
//        val headerText = "Hello, $name. Complete your profile to continue."
//        _uiState = MutableStateFlow(
//            UpdateRolesUIState(
//                vehicleType = vehicleType,
//                licensePlate = licensePlate,
//                vehicleTypeErrorStatus = vehicleError,
//                licensePlateErrorStatus = licenseError,
//                headerText
//            )
//        )
//        uiState = _uiState.asStateFlow()
//    }
//
//    fun onEvent(event: UpdateRolesUIEvent) {
//        when (event) {
//            is UpdateRolesUIEvent.VehicleTypeChanged -> {
//                _uiState.update {
//                    it.copy(
//                        vehicleType = event.vehicleType,
//                        vehicleTypeErrorStatus = !Validator.validateVehicleType(event.vehicleType).status
//                    )
//                }
//            }
//            is UpdateRolesUIEvent.LicensePlateChanged -> {
//                _uiState.update {
//                    it.copy(
//                        licensePlate = event.licensePlate,
//                        licensePlateErrorStatus = !Validator.validateLicensePlate(event.licensePlate).status
//                    )
//                }
//            }
//            is UpdateRolesUIEvent.UpdateButtonClicked -> {
//                updateUserDocument()
//            }
//        }
//    }
//
//    private fun updateUserDocument() {
//        val updateDocument = hashMapOf<String, Any?>(
//            "Roles" to 2,
//            "Vehicle type" to _uiState.value.vehicleType,
//            "License plate" to _uiState.value.licensePlate,
//            "Rating" to null,
//            "Total completed rides" to 0
//        )
//
//        userDocumentReference.update(updateDocument)
//            .addOnSuccessListener {
//                app.updateRolesSuccess = true
//                Log.d("Update Roles", "Updated successfully")
//                _uiState.update {
//                    it.copy(result = com.ridesharingapp.common.R.string.update_role_success)
//                }
//            }
//            .addOnFailureListener {
//                Log.d("Update Roles", "Failed to update")
//                _uiState.update {
//                    it.copy(result = com.ridesharingapp.common.R.string.update_role_failed)
//                }
//            }
//    }
//
//    fun allValidationPassed(uiState: UpdateRolesUIState): Boolean {
//        return !uiState.vehicleTypeErrorStatus && !uiState.licensePlateErrorStatus
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        println("Update on clear ${app.updateRolesInProgress} ${app.updateRolesSuccess}")
//        if (!app.updateRolesSuccess) {
//            auth.signOut()
//        }
//        app.updateRolesInProgress = false
//        app.updateRolesSuccess = false
//    }
//
//    fun dismissResDialog() {
//        if (_uiState.value.result == com.ridesharingapp.common.R.string.update_role_success) {
//            navController.popBackStack(
//                route = Screen.WelcomeScreen.route,
//                inclusive = true
//            )
//            navController.navigate(Screen.HomeScreen.route)
//        }
//        _uiState.update { it.copy(result = -1) }
//    }
//}
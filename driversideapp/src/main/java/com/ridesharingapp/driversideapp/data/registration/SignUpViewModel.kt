package com.ridesharingapp.driversideapp.data.registration

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.ridesharingapp.common.data.rules.Validator
import com.ridesharingapp.driversideapp.navigation.Screen
import kotlinx.coroutines.flow.StateFlow

class SignUpViewModel(
    private val navController: NavHostController,
    private val auth: FirebaseAuth,
    private val isFromLoginScreen: Boolean,
    private val savedStateHandle: SavedStateHandle,
    private val db: FirebaseFirestore
) : ViewModel() {
    val uiState: StateFlow<SignUpUIState> = savedStateHandle.getStateFlow(
        key = "uiState",
        initialValue = SignUpUIState()
    )

    fun onEvent(event: SignUpUIEvent) {
        when(event) {
            is SignUpUIEvent.FirstNameChanged -> {
                savedStateHandle["uiState"] = savedStateHandle.get<SignUpUIState>("uiState")?.copy(
                    firstName = event.firstName,
                    firstNameErrorStatus = !Validator.validateName(event.firstName).status
                )
            }
            is SignUpUIEvent.LastNameChanged -> {
                savedStateHandle["uiState"] = savedStateHandle.get<SignUpUIState>("uiState")?.copy(
                    lastName = event.lastName,
                    lastNameErrorStatus = !Validator.validateName(event.lastName).status
                )
            }
            is SignUpUIEvent.LicensePlateChanged -> {
                savedStateHandle["uiState"] = savedStateHandle.get<SignUpUIState>("uiState")?.copy(
                    licensePlate = event.licensePlate,
                    licensePlateErrorStatus = !Validator.validateLicensePlate(event.licensePlate).status
                )
            }
            is SignUpUIEvent.VehicleTypeChanged -> {
                savedStateHandle["uiState"] = savedStateHandle.get<SignUpUIState>("uiState")?.copy(
                    vehicleType = event.vehicleType,
                    vehicleTypeErrorStatus = !Validator.validateVehicleType(event.vehicleType).status
                )
            }
            is SignUpUIEvent.EmailChanged -> {
                savedStateHandle["uiState"] = savedStateHandle.get<SignUpUIState>("uiState")?.copy(
                    email = event.email,
                    emailErrorStatus = !Validator.validateEmail(event.email).status
                )
            }
            is SignUpUIEvent.PasswordChanged -> {
                savedStateHandle["uiState"] = savedStateHandle.get<SignUpUIState>("uiState")?.copy(
                    password = event.password,
                    passwordErrorStatus = !Validator.validatePassword(event.password).status
                )
            }
            is SignUpUIEvent.TermsConditionChanged -> {
                savedStateHandle["uiState"] = savedStateHandle.get<SignUpUIState>("uiState")?.copy(
                    termsConditionReadState = event.checked
                )
            }
            is SignUpUIEvent.TermsAndConditionsTextClicked -> {
                navController.navigate(Screen.TermsAndConditionsScreen.route)
            }
            is SignUpUIEvent.LoginTextClicked -> {
                if (isFromLoginScreen) {
                    navController.popBackStack()
                } else {
                    navController.navigate(Screen.LoginScreen.withArgs("true"))
                }
            }
            is SignUpUIEvent.RegisterButtonClicked -> {
                val currentState = savedStateHandle.get<SignUpUIState>("uiState")
                if (allValidationPassed(currentState!!)) onRegisterButtonClick()
            }
        }
    }

    fun dismissFailureMessage() {
        savedStateHandle["uiState"] = savedStateHandle.get<SignUpUIState>("uiState")?.copy(
            registrationFailed = false
        )
    }

    private fun onRegisterButtonClick() {
        savedStateHandle.get<SignUpUIState>("uiState")?.let { currentState ->
            savedStateHandle["uiState"] = currentState.copy(
                registrationInProgress = true
            )
            auth.createUserWithEmailAndPassword(
                currentState.email!!,
                currentState.password!!
            )
                .addOnCompleteListener { task ->
                    savedStateHandle["uiState"] =
                        savedStateHandle.get<SignUpUIState>("uiState")?.copy(
                            registrationInProgress = false
                        )
                    // if the user has not been registered yet
                    if (task.isSuccessful) {
                        Log.d("SignUp", "createUserWithEmail:success")

                        // Add the user to the database
                        val user = hashMapOf(
                            "First name" to currentState.firstName!!,
                            "Last name" to currentState.lastName!!,
                            "Roles" to 1,
                            "Vehicle type" to currentState.vehicleType,
                            "License plate" to currentState.licensePlate,
                            "Rating" to null,
                            "Total completed rides" to 0
                        )
                        db.collection("Users")
                            .document(auth.currentUser!!.uid)
                            .set(user)
                            .addOnSuccessListener {
                                Log.d("Create user", "User document successfully added")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Create user", "Error writing user document", e)
                            }

                        navController.popBackStack(
                            route = Screen.WelcomeScreen.route,
                            inclusive = true
                        )
                        navController.navigate(Screen.HomeScreen.route)
                    } else if (task.exception is FirebaseAuthUserCollisionException) {
                        println("UserCollision SignUpToLogin ${currentState.email} ${currentState.vehicleType} ${currentState.licensePlate}")
                        navController.navigate(
                            Screen.LoginScreen.withArgs(
                                "true",
                                "true",
                                currentState.email!!,
                                currentState.vehicleType!!,
                                currentState.licensePlate!!
                            )
                        )
                    } else {
                        Log.w("SignUp", "createUserWithEmail:failure", task.exception)
                        savedStateHandle["uiState"] =
                            savedStateHandle.get<SignUpUIState>("uiState")?.copy(
                                registrationFailed = true
                            )
                    }
                }
        }
    }

    fun allValidationPassed(uiState: SignUpUIState): Boolean {
        return !uiState.firstNameErrorStatus && !uiState.lastNameErrorStatus
                && !uiState.licensePlateErrorStatus && !uiState.vehicleTypeErrorStatus
                && !uiState.emailErrorStatus && !uiState.passwordErrorStatus
                && uiState.termsConditionReadState
    }
}
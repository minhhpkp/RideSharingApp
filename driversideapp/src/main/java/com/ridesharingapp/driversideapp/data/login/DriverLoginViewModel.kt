package com.ridesharingapp.driversideapp.data.login

import android.util.Log
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ridesharingapp.common.data.login.LoginUIEvent
import com.ridesharingapp.common.data.login.LoginViewModel
import com.ridesharingapp.driversideapp.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DriverLoginViewModel(
    private val navController: NavHostController,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    initEmail: String? = null,
    private val vehicleType: String? = null,
    private val licensePlate: String? = null,
    isFromSignUp: Boolean,
    userCollision: Boolean
) : LoginViewModel(
    navController = navController,
    authSuccessScreen = if (userCollision) Screen.UpdateRolesScreen else Screen.HomeScreen,
    forgotPasswordScreen = Screen.ForgotPasswordScreen,
    auth = auth,
    signUpScreen = Screen.SignUpScreen,
    isFromSignUpScreen = isFromSignUp,
    startScreen = Screen.WelcomeScreen,
    initEmail = initEmail
) {
    private val _showError = MutableStateFlow(userCollision)
    val showError: StateFlow<Boolean> = _showError.asStateFlow()
    init {
        println("DriverLoginViewModel created $initEmail $vehicleType $licensePlate")
    }

    fun dismissUserCollisionMessage() {
        _showError.update { false }
    }

    override fun onEvent(event: LoginUIEvent) {
        when (event) {
            is LoginUIEvent.LoginButtonClicked -> {
                onLoginClicked()
            }
            else -> super.onEvent(event)
        }
    }

    private fun onLoginClicked() {
        onEvent(LoginUIEvent.LoginInProgressChanged(true))
        auth.signInWithEmailAndPassword(
            uiState.value.email!!,
            uiState.value.password!!
        )
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    Log.d("Login", "signInWithEmail:success")

                    db.collection("Users")
                        .document(auth.currentUser!!.uid)
                        .get()
                        .addOnSuccessListener { document ->
                            Log.d("Login", "User document get successfully")
                            onEvent(LoginUIEvent.LoginInProgressChanged(false))
                            when (document.get("Roles") as Long) {
                                // attempt to login with passenger role
                                0L -> {
                                    val name = document.getString("First name")!!
                                    navController.navigate(
                                        Screen.UpdateRolesScreen.withArgs(
                                            name,
                                            vehicleType,
                                            licensePlate
                                        )
                                    )
                                }
                                // attempt to login with driver/mixed role
                                1L, 2L -> {
                                    navController.popBackStack(
                                        route = Screen.WelcomeScreen.route,
                                        inclusive = true
                                    )
                                    navController.navigate(Screen.HomeScreen.route)
                                }
                                -1L -> throw Exception("Get Roles failed")
                                else -> throw Exception("Internal Server Error")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.w("Login", "User document get failed with ", e)
                            onEvent(LoginUIEvent.LoginInProgressChanged(false))
                            onEvent(LoginUIEvent.LoginFailedChanged(true))
                        }
                } else {
                    Log.w("Login", "signInWithEmail:failure", task.exception)
                    onEvent(LoginUIEvent.LoginInProgressChanged(false))
                    onEvent(LoginUIEvent.LoginFailedChanged(true))
                }
            }
    }
}
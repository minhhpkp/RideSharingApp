package com.ridesharingapp.passengersideapp.data.login

import android.util.Log
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ridesharingapp.common.data.login.LoginUIEvent
import com.ridesharingapp.common.data.login.LoginViewModel
import com.ridesharingapp.passengersideapp.R
import com.ridesharingapp.passengersideapp.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PassengerLoginViewModel(
    private val navController: NavHostController,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    isFromSignUp: Boolean,
    initEmail: String? = null
) : LoginViewModel(
    navController,
    authSuccessScreen = Screen.HomeScreen,
    forgotPasswordScreen = Screen.ForgotPasswordScreen,
    auth,
    signUpScreen = Screen.SignUpScreen,
    isFromSignUpScreen = isFromSignUp,
    startScreen = Screen.WelcomeScreen,
    initEmail = initEmail
) {
    private val _updateResult = MutableStateFlow(-1)
    val updateResult: StateFlow<Int> = _updateResult.asStateFlow()

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
                    val docRef = db.collection("Users")
                        .document(auth.currentUser!!.uid)


                    docRef.get()
                        .addOnSuccessListener { currentDoc ->
                            Log.d("Login", "User document get successfully")

                            when (currentDoc.get("Roles") as Long) {
                                // attempt to login with driver role
                                1L -> {
                                    val updateDoc = hashMapOf<String, Any?> (
                                        "Roles" to 2,
                                        "Reward points" to 0
                                    )
                                    docRef.update(updateDoc)
                                        .addOnSuccessListener {
                                            Log.d("Update Roles", "Updated successfully")
                                            onEvent(LoginUIEvent.LoginInProgressChanged(false))
                                            _updateResult.update {
                                                R.string.role_update_success
                                            }
                                        }
                                        .addOnFailureListener {
                                            Log.d("Update Roles", "Failed to update")
                                            onEvent(LoginUIEvent.LoginInProgressChanged(false))
                                            _updateResult.update {
                                                com.ridesharingapp.common.R.string.update_role_failed
                                            }
                                        }
                                }
                                // attempt to login with passenger/mixed role
                                0L, 2L -> {
                                    onEvent(LoginUIEvent.LoginInProgressChanged(false))
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

    fun dismissUpdateMsg() {
        if (_updateResult.value == R.string.role_update_success) {
            navController.popBackStack(
                route = Screen.WelcomeScreen.route,
                inclusive = true
            )
            navController.navigate(Screen.HomeScreen.route)
        }
        _updateResult.update { -1 }
    }
}
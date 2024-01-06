package com.ridesharingapp.common.usecases

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.ridesharingapp.common.ServiceResult
import com.ridesharingapp.common.domain.GrabLamUser
import com.ridesharingapp.common.keys.TYPE_PASSENGER
import com.ridesharingapp.common.services.AuthenticationService
import com.ridesharingapp.common.services.SignUpResult
import com.ridesharingapp.common.services.UserService
import kotlinx.coroutines.tasks.await

class SignUpUser(
    val authService: AuthenticationService,
    val userService: UserService
) {
    suspend fun signUpUser(email: String, password: String, username: String, type: Boolean)
    : ServiceResult<SignUpResult> {
        val authAttempt = authService.signUp(email, password)

        return if (authAttempt is ServiceResult.Value) {
            when (authAttempt.value) {
                is SignUpResult.Success -> {
                    updateUserDetails(
                        username,
                        authAttempt.value.uid,
                        type
                    )
                }
                else -> authAttempt
            }
        } else authAttempt
    }

    private suspend fun updateUserDetails(username: String, uid: String, type: Boolean)
    : ServiceResult<SignUpResult>  {
        val db = Firebase.firestore
        val userData = if (type == TYPE_PASSENGER) {
             hashMapOf(
                "Reward points" to 0
            )
        } else {
            hashMapOf(
                "Rating" to null,
                "Total rated rides" to 0
            )
        }
        db.collection("Users").document(uid)
            .set(userData)
            .addOnSuccessListener {
                Log.d("SignUpUser", "user data of type ${if (type) "driver" else "passenger"} added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("SignUpUser", "failed to add user data of type ${if (type) "driver" else "passenger"}", e)
            }
            .await()
        if (db.collection("Users").document(uid).get().await() == null) {
            return ServiceResult.Failure(Exception("Unable to add user data"))
        }

        return userService.initializeNewUser(
            GrabLamUser(
                userId = uid,
                username = username
            )
        ).let { updateResult ->
            when (updateResult) {
                is ServiceResult.Failure -> {
                    Log.w("SignUpUser", "updateUserDetails failed", updateResult.exception)
                    ServiceResult.Failure(updateResult.exception)
                }

                is ServiceResult.Value -> ServiceResult.Value(SignUpResult.Success(uid))
            }
        }
    }
}
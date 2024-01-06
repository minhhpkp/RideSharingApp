package com.ridesharingapp.common.usecases

import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.ridesharingapp.common.ServiceResult
import com.ridesharingapp.common.keys.TYPE_PASSENGER
import com.ridesharingapp.common.services.AuthenticationService
import com.ridesharingapp.common.services.LogInResult
import com.ridesharingapp.common.services.UserService
import kotlinx.coroutines.tasks.await

class LogInUser(
    val authService: AuthenticationService,
    val userService: UserService
) {

    suspend fun login(email: String, password: String, type: Boolean): ServiceResult<LogInResult> {
        val authAttempt = authService.login(email, password)

        return if (authAttempt is ServiceResult.Value) {
            when (authAttempt.value) {
                is LogInResult.Success -> getUserDetails(authAttempt.value.user.userId, type)
                else -> authAttempt
            }
        } else authAttempt
    }


    private suspend fun getUserDetails(uid: String, type: Boolean): ServiceResult<LogInResult> {
        val userDocRef = Firebase.firestore.collection("Users").document(uid)
        var snapshot = userDocRef.get().await()
        if (type == TYPE_PASSENGER) {
            if (!snapshot.exists() || !snapshot.contains("Reward points")) {
                val defaultValue = hashMapOf("Reward points" to 0)
                userDocRef.set(defaultValue, SetOptions.merge()).await()
                snapshot = userDocRef.get().await()
                if (!snapshot.exists() || !snapshot.contains("Reward points")) {
                    return ServiceResult.Failure(Exception("Unable to add user data"))
                }
            }
        } else {
            if (!snapshot.exists() || !snapshot.contains("Rating") || !snapshot.contains("Total completed rides")) {
                val defaultValue = hashMapOf(
                    "Rating" to null,
                    "Total completed rides" to 0
                )
                userDocRef.set(defaultValue, SetOptions.merge()).await()
                snapshot = userDocRef.get().await()
                if (!snapshot.exists() || !snapshot.contains("Rating") || !snapshot.contains("Total completed rides")) {
                    return ServiceResult.Failure(Exception("Unable to add user data"))
                }
            }
        }

        return userService.getUserById(uid).let { updateResult ->
            when (updateResult) {
                is ServiceResult.Failure -> ServiceResult.Failure(updateResult.exception)
                is ServiceResult.Value -> {
                    if (updateResult.value == null) ServiceResult.Failure(Exception("Null user in LogInUser"))
                    else ServiceResult.Value(LogInResult.Success(updateResult.value))
                }
            }
        }
    }
}
package com.ridesharingapp.driversideapp.data.usecase

import com.ridesharingapp.driversideapp.ServiceResult
import com.ridesharingapp.driversideapp.data.domain.GrabLamUser
import com.ridesharingapp.driversideapp.data.services.AuthorizationService
import com.ridesharingapp.driversideapp.data.services.SignUpResult
import com.ridesharingapp.driversideapp.data.services.UserService


class SignUpUser(
    val authService: AuthorizationService,
    val userService: UserService
) {

    suspend fun signUpUser(email: String, password: String, username: String): ServiceResult<SignUpResult> {
        val authAttempt = authService.signUp(email, password)

        return if (authAttempt is ServiceResult.Value) {
            when (authAttempt.value) {
                is SignUpResult.Success -> updateUserDetails(
                    username,
                    authAttempt.value.uid
                )
                else -> authAttempt
            }
        } else authAttempt
    }

    private suspend fun updateUserDetails(
        username: String,
        uid: String
    ): ServiceResult<SignUpResult> {
        return userService.initializeNewUser(
            GrabLamUser(
                userId = uid,
                username = username
            )
        ).let { updateResult ->
            when (updateResult) {
                is ServiceResult.Failure -> ServiceResult.Failure(updateResult.exception)
                is ServiceResult.Value -> ServiceResult.Value(SignUpResult.Success(uid))
            }
        }
    }
}
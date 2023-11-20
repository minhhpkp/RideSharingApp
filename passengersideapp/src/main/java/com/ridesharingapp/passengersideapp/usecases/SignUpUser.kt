package com.ridesharingapp.passengersideapp.usecases

import com.ridesharingapp.passengersideapp.ServiceResult
import com.ridesharingapp.passengersideapp.domain.AppUser
import com.ridesharingapp.passengersideapp.services.AuthenticationService
import com.ridesharingapp.passengersideapp.services.SignUpResult
import com.ridesharingapp.passengersideapp.services.UserService

class SignUpUser(
    val authService: AuthenticationService,
    val userService: UserService
) {
    suspend fun signUpUser(email: String, password: String, username: String)
    : ServiceResult<SignUpResult> {
        val authAttempt = authService.signUp(email, password)

        return if (authAttempt is ServiceResult.Value) {
            when (authAttempt.value) {
                is SignUpResult.Success -> {
                    updateUserDetails(
                        username,
                        authAttempt.value.uid
                    )
                }
                else -> authAttempt
            }
        } else authAttempt
    }

    private suspend fun updateUserDetails(username: String, uid: String)
    : ServiceResult<SignUpResult> = userService.initializeNewUser(
        AppUser(
            userId = uid,
            username = username
        )
    ).let { updateResult ->
        when (updateResult) {
            is ServiceResult.Failure -> {
                println("updateUserDetails ${updateResult.exception}")
                ServiceResult.Failure(updateResult.exception)
            }
            is ServiceResult.Value -> ServiceResult.Value(SignUpResult.Success(uid))
        }
    }
}
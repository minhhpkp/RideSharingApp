package com.ridesharingapp.driversideapp.data.usecase

import com.ridesharingapp.driversideapp.ServiceResult
import com.ridesharingapp.driversideapp.data.services.AuthorizationService
import com.ridesharingapp.driversideapp.data.services.LogInResult
import com.ridesharingapp.driversideapp.data.services.UserService


class LogInUser(
    val authService: AuthorizationService,
    val userService: UserService
) {

    suspend fun login(email: String, password: String): ServiceResult<LogInResult> {
        val authAttempt = authService.login(email, password)

       return if (authAttempt is ServiceResult.Value) {
            when (authAttempt.value) {
                is LogInResult.Success -> getUserDetails(authAttempt.value.user.userId)
                else -> authAttempt
            }
        } else authAttempt
    }


    private suspend fun getUserDetails(
        uid: String
    ): ServiceResult<LogInResult> {
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
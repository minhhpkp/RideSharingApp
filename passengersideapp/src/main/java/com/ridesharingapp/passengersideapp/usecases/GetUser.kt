package com.ridesharingapp.passengersideapp.usecases

import com.ridesharingapp.passengersideapp.ServiceResult
import com.ridesharingapp.passengersideapp.domain.AppUser
import com.ridesharingapp.passengersideapp.services.AuthenticationService
import com.ridesharingapp.passengersideapp.services.UserService

class GetUser(
    val authService: AuthenticationService,
    val userService: UserService
) {

    suspend fun getUser(): ServiceResult<AppUser?> {
        val getSession = authService.getSession()
        return when (getSession) {
            is ServiceResult.Failure -> getSession
            is ServiceResult.Value -> {
                if (getSession.value == null) getSession
                else getUserDetails(getSession.value.userId)
            }
        }
    }

    private suspend fun getUserDetails(uid: String): ServiceResult<AppUser?> {
        return userService.getUserById(uid).let { getDetailsResult ->
            when (getDetailsResult) {
                is ServiceResult.Failure -> ServiceResult.Failure(getDetailsResult.exception)
                is ServiceResult.Value -> ServiceResult.Value(getDetailsResult.value)
            }
        }
    }
}
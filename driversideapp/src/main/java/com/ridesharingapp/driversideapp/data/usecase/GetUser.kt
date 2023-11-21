package com.ridesharingapp.driversideapp.data.usecase

import com.ridesharingapp.driversideapp.data.domain.GrabLamUser
import com.ridesharingapp.driversideapp.ServiceResult
import com.ridesharingapp.driversideapp.data.services.AuthorizationService
import com.ridesharingapp.driversideapp.data.services.UserService

class GetUser(
    val authService: AuthorizationService,
    val userService: UserService
) {

    suspend fun getUser(): ServiceResult<GrabLamUser?> {
        val getSession = authService.getSession()
        return when (getSession) {
            is ServiceResult.Failure -> getSession
            is ServiceResult.Value -> {
                if (getSession.value == null) getSession
                else getUserDetails(getSession.value.userId)
            }
        }
    }

    private suspend fun getUserDetails(uid: String): ServiceResult<GrabLamUser?> {
        return userService.getUserById(uid).let { getDetailsResult ->
            when (getDetailsResult) {
                    is ServiceResult.Failure -> ServiceResult.Failure(getDetailsResult.exception)
                is ServiceResult.Value -> ServiceResult.Value(getDetailsResult.value)
            }
        }
    }
}
package com.ridesharingapp.driversideapp.data.usecase

import com.ridesharingapp.driversideapp.ServiceResult
import com.ridesharingapp.driversideapp.data.domain.GrabLamUser
import com.ridesharingapp.driversideapp.data.services.AuthorizationService
import com.ridesharingapp.driversideapp.data.services.UserService


class LogOutUser(
    val authService: AuthorizationService,
    val userService: UserService
) {

    suspend fun logout(user: GrabLamUser): ServiceResult<Unit> {
        authService.logout()
        userService.logOutUser(user)

        return ServiceResult.Value(Unit)
    }
}
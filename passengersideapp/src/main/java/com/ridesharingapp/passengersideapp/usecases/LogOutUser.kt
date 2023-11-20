package com.ridesharingapp.passengersideapp.usecases

import com.ridesharingapp.passengersideapp.ServiceResult
import com.ridesharingapp.passengersideapp.services.AuthenticationService
import com.ridesharingapp.passengersideapp.services.UserService

class LogOutUser(
    val authService: AuthenticationService,
    val userService: UserService
) {

    suspend fun logout(): ServiceResult<Unit> {
        authService.logout()
        userService.logOutUser()

        return ServiceResult.Value(Unit)
    }
}
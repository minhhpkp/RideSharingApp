package com.ridesharingapp.common.usecase

import com.ridesharingapp.common.ServiceResult
import com.ridesharingapp.common.domain.GrabLamUser
import com.ridesharingapp.common.services.AuthorizationService
import com.ridesharingapp.common.services.UserService


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
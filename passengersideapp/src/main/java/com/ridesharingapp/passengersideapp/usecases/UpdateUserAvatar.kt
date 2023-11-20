package com.ridesharingapp.passengersideapp.usecases

import com.ridesharingapp.passengersideapp.ServiceResult
import com.ridesharingapp.passengersideapp.domain.AppUser
import com.ridesharingapp.passengersideapp.services.PhotoService
import com.ridesharingapp.passengersideapp.services.UserService

class UpdateUserAvatar(
    val photoService: PhotoService,
    val userService: UserService
) {
    suspend fun updateAvatar(user: AppUser, uri: String): ServiceResult<String> {
        val updateAvatar = photoService.attemptUserAvatarUpdate(uri)
        return when (updateAvatar) {
            is ServiceResult.Failure -> updateAvatar
            is ServiceResult.Value -> updateUserPhoto(user, updateAvatar.value)
        }
    }

    private suspend fun updateUserPhoto(
        user: AppUser,
        newUrl: String
    ): ServiceResult<String> {
        return userService.updateUser(
            user.copy(
                avatarPhotoUrl = newUrl
            )
        ).let { updateResult ->
            when (updateResult) {
                is ServiceResult.Failure -> ServiceResult.Failure(updateResult.exception)
                is ServiceResult.Value -> ServiceResult.Value(newUrl)
            }
        }
    }
}
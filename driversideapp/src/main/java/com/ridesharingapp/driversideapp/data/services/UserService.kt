package com.ridesharingapp.driversideapp.data.services

import com.ridesharingapp.common.domain.GrabLamUser
import com.ridesharingapp.driversideapp.data.ServiceResult

interface UserService {
    suspend fun attemptSignUp(phoneNumber: String, userName: String): ServiceResult<SignUpResult>
    suspend fun attemptLogin(phoneNumber: String): ServiceResult<LogInResult>

    /**
     * A session is the period during which a user still has an authenticated connection to
     * the authorization services of the application. Sessions allow users to not have to
     * authenticate themselves every time they try to access a service.
     *
     * @return true if a session exists; else false
     */
    suspend fun getUser(): ServiceResult<GrabLamUser?>

    suspend fun getUserById(userId: String): ServiceResult<GrabLamUser?>
    suspend fun attemptLogout(): ServiceResult<Unit>
    suspend fun updateUser(user: GrabLamUser): ServiceResult<GrabLamUser?>

    suspend fun attemptUserAvatarUpdate(user: GrabLamUser, url: String): ServiceResult<String?>

    suspend fun attemptVehicleAvatarUpdate(user: GrabLamUser, url: String): ServiceResult<String?>
    suspend fun getPassengersLookingForRide(): ServiceResult<List<GrabLamUser>?>
}

enum class SignUpResult {
    SUCCESS,
    ALREADY_SIGNED_UP,
    INVALID_CREDENTIALS
}

enum class LogInResult {
    SUCCESS,
    INVALID_CREDENTIALS
}
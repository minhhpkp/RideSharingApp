package com.ridesharingapp.passengersideapp.services

import com.ridesharingapp.passengersideapp.ServiceResult
import com.ridesharingapp.passengersideapp.domain.AppUser

interface AuthenticationService {
    /**
     * @return uid if sign up is successful
     */
    suspend fun signUp(email: String, password: String): ServiceResult<SignUpResult>
    suspend fun login(email: String, password: String): ServiceResult<LogInResult>

    fun logout(): ServiceResult<Unit>

    /**
     * @return true if a user session is active, else null
     */
    suspend fun getSession(): ServiceResult<AppUser?>
}

sealed interface SignUpResult {
    data class Success(val uid: String) : SignUpResult
    object AlreadySignedUp : SignUpResult
    object InvalidCredentials : SignUpResult
}

sealed interface LogInResult {
    data class Success(val user: AppUser) : LogInResult
    object InvalidCredentials : LogInResult
}
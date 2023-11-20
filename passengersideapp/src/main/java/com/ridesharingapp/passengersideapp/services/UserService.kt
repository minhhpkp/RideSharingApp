package com.ridesharingapp.passengersideapp.services

import com.ridesharingapp.passengersideapp.ServiceResult
import com.ridesharingapp.passengersideapp.domain.AppUser

interface UserService {
    suspend fun getUserById(userId: String): ServiceResult<AppUser?>

    suspend fun updateUser(user: AppUser): ServiceResult<AppUser?>

    suspend fun initializeNewUser(user: AppUser): ServiceResult<AppUser?>

    suspend fun logOutUser(user: AppUser): Unit
}
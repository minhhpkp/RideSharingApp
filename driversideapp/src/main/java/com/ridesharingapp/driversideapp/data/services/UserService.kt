package com.ridesharingapp.driversideapp.data.services

import com.ridesharingapp.driversideapp.ServiceResult
import com.ridesharingapp.driversideapp.data.domain.GrabLamUser

interface UserService {

    suspend fun getUserById(userId: String): ServiceResult<GrabLamUser?>
    suspend fun updateUser(user: GrabLamUser): ServiceResult<GrabLamUser?>

    suspend fun initializeNewUser(user: GrabLamUser): ServiceResult<GrabLamUser?>

    suspend fun logOutUser(user: GrabLamUser): Unit
}
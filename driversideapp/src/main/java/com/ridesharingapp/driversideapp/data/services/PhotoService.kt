package com.ridesharingapp.driversideapp.data.services

import com.ridesharingapp.driversideapp.ServiceResult
interface PhotoService {
    suspend fun attemptUserAvatarUpdate(url: String): ServiceResult<String>
}
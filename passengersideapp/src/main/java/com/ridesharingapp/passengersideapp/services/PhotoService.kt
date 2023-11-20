package com.ridesharingapp.passengersideapp.services

import com.ridesharingapp.passengersideapp.ServiceResult

interface PhotoService {
    suspend fun attemptUserAvatarUpdate(url: String): ServiceResult<String>
}
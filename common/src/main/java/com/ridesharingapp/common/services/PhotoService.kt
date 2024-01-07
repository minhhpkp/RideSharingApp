package com.ridesharingapp.common.services

import com.ridesharingapp.common.ServiceResult
import com.ridesharingapp.common.domain.GrabLamUser

interface PhotoService {
    suspend fun attemptUserAvatarUpdate(url: String, user: GrabLamUser): ServiceResult<String>
}
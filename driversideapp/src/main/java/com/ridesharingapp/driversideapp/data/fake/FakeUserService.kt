package com.ridesharingapp.driversideapp.data.fake

import com.ridesharingapp.common.domain.GrabLamUser
import com.ridesharingapp.common.domain.Ride
import com.ridesharingapp.common.domain.RideStatus
import com.ridesharingapp.common.domain.UserStatus
import com.ridesharingapp.common.domain.UserType
import com.ridesharingapp.driversideapp.data.ServiceResult
import com.ridesharingapp.driversideapp.data.services.LogInResult
import com.ridesharingapp.driversideapp.data.services.RideService
import com.ridesharingapp.driversideapp.data.services.SignUpResult
import com.ridesharingapp.driversideapp.data.services.UserService

class FakeUserService : UserService {
    override suspend fun attemptSignUp(
        phoneNumber: String,
        userName: String
    ): ServiceResult<SignUpResult> {
        return ServiceResult.Success(SignUpResult.SUCCESS)
    }

    override suspend fun attemptLogin(phoneNumber: String): ServiceResult<LogInResult> {
        return ServiceResult.Success(LogInResult.SUCCESS)
    }

    override suspend fun getUser(): ServiceResult<GrabLamUser?> {
        return ServiceResult.Success(testUser)
    }

    override suspend fun getUserById(userId: String): ServiceResult<GrabLamUser?> {
        return ServiceResult.Success(testUser.copy(type = UserType.DRIVER.value,
            latitude = 51.0443,
            longitude = -113.06,
        ))
    }

    override suspend fun attemptLogout(): ServiceResult<Unit> {
        return ServiceResult.Success(Unit)
    }

    override suspend fun updateUser(user: GrabLamUser): ServiceResult<GrabLamUser?> {
        return ServiceResult.Success(testUser)
    }

    override suspend fun attemptUserAvatarUpdate(user: GrabLamUser, uri: String): ServiceResult<String?> {
        return ServiceResult.Success(uri)
    }

    override suspend fun attemptVehicleAvatarUpdate(user: GrabLamUser, url: String): ServiceResult<String?> {
        return ServiceResult.Success(url)
    }

    override suspend fun getPassengersLookingForRide(): ServiceResult<List<GrabLamUser>?> {
        return ServiceResult.Success(listOf(testUser.copy(
            latitude = 51.0543,
            longitude = -114.20,
        )))
        //return ServiceResult.Success(emptyList())

    }
}

private val testUser = GrabLamUser(
    "123456",
    "Saitama",
    UserType.DRIVER.value,
    UserStatus.INACTIVE.value,
    "https://static.wikia.nocookie.net/onepunchman/images/9/9b/Saitama_regular_face.jpg/revision/latest?cb=20200316015620",
    "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1d/Wild_Burros.jpg/1280px-Wild_Burros.jpg",
    "We ride tandem on a Donkey.",
    false,
    51.0443,
    -114.06,
    "Some time before",
    "Some time after"
)
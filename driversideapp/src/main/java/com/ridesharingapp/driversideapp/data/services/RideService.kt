package com.ridesharingapp.driversideapp.data.services

import com.ridesharingapp.driversideapp.data.ServiceResult
import com.ridesharingapp.common.domain.GrabLamUser
import com.ridesharingapp.common.domain.Ride
import kotlinx.coroutines.flow.Flow

interface RideService {
    suspend fun getRideIfInProgress(): ServiceResult<Ride?>
    suspend fun updateRide(ride: Ride): ServiceResult<Ride?>
    suspend fun createRide(
        passengerId: String,
        latitude: Double,
        longitude: Double,
        address: String
    ): ServiceResult<Ride>

    suspend fun cancelRide(ride: Ride): ServiceResult<Unit>
    suspend fun completeRide(value: Ride): ServiceResult<Unit>
    suspend fun getRideByPassengerId(passengerId: String): ServiceResult<Ride?>
}
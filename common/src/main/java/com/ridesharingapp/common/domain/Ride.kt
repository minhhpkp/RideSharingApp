package com.ridesharingapp.common.domain

data class Ride(
    val rideId: String = "",
    val status: String = RideStatus.SEARCHING_FOR_DRIVER.value,
    val destinationLatitude: Double = 0.0,
    val destinationLongitude: Double = 0.0,
    val destinationAddress: String = "",
    val pickUpLatitude: Double = 0.0,
    val pickUpLongitude: Double = 0.0,
    val pickUpAddress: String = "",
    val passengerId: String = "",
    val passengerLatitude: Double = 0.0,
    val passengerLongitude: Double = 0.0,
    val passengerName: String = "",
    val passengerAvatarUrl: String = "",
    val driverId: String? = null,
    val driverLatitude: Double? = null,
    val driverLongitude: Double? = null,
    val driverName: String? = null,
    val driverAvatarUrl: String? = null,
    val createdAt: String = "",
    val updatedAT: String = "",
    val totalMessages: Int = 0
)
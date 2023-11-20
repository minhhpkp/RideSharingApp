package com.ridesharingapp.passengersideapp.domain

data class AppUser(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val type: String = UserType.PASSENGER.value,
    val status: String = UserStatus.INACTIVE.value,
    val avatarPhotoUrl: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)
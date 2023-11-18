package com.ridesharingapp.common.domain

data class GrabLamUser(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val type: String = UserType.PASSENGER.value,
    val status: String = UserStatus.INACTIVE.value,
    val avatarPhotoUrl: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    //For convenience in Swift
}
package com.ridesharingapp.common.domain

enum class RideStatus(val value: String) {
    SEARCHING_FOR_DRIVER("SEARCHING_FOR_DRIVER"),
    PASSENGER_PICK_UP("PASSENGER_PICK_UP"),
    EN_ROUTE("EN_ROUTE"),
    ARRIVED("ARRIVED"),
    PENDING_RATING("PENDING_RATING")
}
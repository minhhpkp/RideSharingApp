package com.ridesharingapp.driversideapp.home

sealed interface HomeUiState {
    object SearchingForPassengers: HomeUiState
    data class PassengerPickUp(
        val passengerLat: Double,
        val passengerLon: Double,
        val driverLat: Double,
        val driverLon: Double,
        val destinationLat: Double,
        val destinationLon: Double,
        val destinationAddress: String,
        val passengerName: String,
        val passengerAvatar: String
    ): HomeUiState
    data class EnRoute(
        val driverLat: Double,
        val driverLon: Double,
        val destinationLat: Double,
        val destinationLon: Double,
        val destinationAddress: String,
        val passengerName: String,
        val passengerAvatar: String
    ): HomeUiState

    data class Arrived(
        val driverLat: Double,
        val driverLon: Double,
        val destinationLat: Double,
        val destinationLon: Double,
        val destinationAddress: String,
        val passengerName: String,
        val passengerAvatar: String
    ): HomeUiState

    //Signals something unexpected has happened
    object Error: HomeUiState
    object Loading: HomeUiState

    data class NewMessages(val totalMessages: Int) : HomeUiState
}
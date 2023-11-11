package com.ridesharingapp.driversideapp.data.home

sealed class HomeUIEvent {
    object ContactPassenger: HomeUIEvent()
    object PickUpPassenger: HomeUIEvent()
}

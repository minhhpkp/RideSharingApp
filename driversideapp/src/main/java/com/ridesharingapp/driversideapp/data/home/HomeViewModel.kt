package com.ridesharingapp.driversideapp.data.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.ridesharingapp.common.domain.GrabLamUser
import com.ridesharingapp.common.domain.Ride
import com.ridesharingapp.driversideapp.data.ServiceResult
import com.ridesharingapp.driversideapp.data.services.RideService
import com.ridesharingapp.driversideapp.navigation.Screen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChange


class HomeViewModel(
    val backstack: Backstack,
    val rideService: RideService
    ) : ViewModel() {
    var signOutInProgress by mutableStateOf(false)
    private val _driverModel = MutableStateFlow<GrabLamUser?>(null)
    private val _rideModel = MutableStateFlow<Ride?>(null)
    private val _mapIsReady = MutableStateFlow(false)


    private fun sendToLogin() {

    }

    fun mapIsReady() {
        _mapIsReady.value = true
    }

    fun handleError() {
        sendToLogin()
    }
}


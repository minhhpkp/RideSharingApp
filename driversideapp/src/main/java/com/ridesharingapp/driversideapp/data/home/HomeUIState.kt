package com.ridesharingapp.driversideapp.data.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.maps.android.compose.MapProperties

data class HomeUIState (
    val properties: MapProperties = MapProperties(),
    var signOutInProgress: MutableState<Boolean> = mutableStateOf(false)
)
package com.ridesharingapp.common.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class AppRouter<Screen>(firstScreen: Screen) {
    var currentScreen: MutableState<Screen> = mutableStateOf(firstScreen)
    fun navigateTo(destination: Screen) {
        currentScreen.value = destination
    }
}
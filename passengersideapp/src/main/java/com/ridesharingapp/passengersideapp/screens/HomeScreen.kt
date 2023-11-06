package com.ridesharingapp.passengersideapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ridesharingapp.common.components.ButtonComponent
import com.ridesharingapp.common.components.HeadingTextComponent
import com.ridesharingapp.passengersideapp.data.home.HomeViewModel

@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeadingTextComponent(value = "Home")

            ButtonComponent(
                labelValue = "Sign out",
                onClickAction = {
                    homeViewModel.signOut()
                },
                isEnabled = true
            )

            if (homeViewModel.signOutInProgress) {
                CircularProgressIndicator()
            }
        }
    }
}
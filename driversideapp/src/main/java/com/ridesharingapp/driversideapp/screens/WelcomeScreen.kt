package com.ridesharingapp.driversideapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ridesharingapp.common.components.ButtonComponent
import com.ridesharingapp.common.components.HeadingTextComponent
import com.ridesharingapp.driversideapp.data.welcome.WelcomeViewModel

@Composable
fun WelcomeScreen(welcomeViewModel: WelcomeViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeadingTextComponent(value = "Welcome")
        Spacer(modifier = Modifier.height(20.dp))
        ButtonComponent(labelValue = "Sign up", onClickAction = {
            welcomeViewModel.onSignUpClicked()
        })
        Spacer(modifier = Modifier.height(15.dp))
        ButtonComponent(labelValue = "Sign in", onClickAction = {
            welcomeViewModel.onSignInClicked()
        })
    }
}
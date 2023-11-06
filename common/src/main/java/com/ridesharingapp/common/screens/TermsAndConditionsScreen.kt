package com.ridesharingapp.common.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ridesharingapp.common.R
import com.ridesharingapp.common.components.HeadingTextComponent
import com.ridesharingapp.common.navigation.AppRouter
import com.ridesharingapp.common.navigation.SystemBackButtonHandler

@Composable
fun <Screen> TermsAndConditionsScreen(appRouter: AppRouter<Screen>, signUpScreen: Screen) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(16.dp)
    ) {
        HeadingTextComponent(value = stringResource(id = R.string.terms_header))
    }
    SystemBackButtonHandler {
        appRouter.navigateTo(signUpScreen)
    }
}
package com.ridesharingapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ridesharingapp.R
import com.ridesharingapp.components.HeadingTextComponent
import com.ridesharingapp.navigation.AppRouter
import com.ridesharingapp.navigation.Screen
import com.ridesharingapp.navigation.SystemBackButtonHandler

@Composable
fun TermsAndConditionsScreen() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(16.dp)
    ) {
        HeadingTextComponent(value = stringResource(id = R.string.terms_header))
    }
    SystemBackButtonHandler {
        AppRouter.navigateTo(Screen.SignUpScreen)
    }
}

@Preview
@Composable
fun TermsAndConditionScreenPreview() {
    TermsAndConditionsScreen()
}
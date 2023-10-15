package com.ridesharingapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ridesharingapp.R
import com.ridesharingapp.components.ButtonComponent
import com.ridesharingapp.components.CheckboxComponent
import com.ridesharingapp.components.DividerTextComponent
import com.ridesharingapp.components.HeadingTextComponent
import com.ridesharingapp.components.NormalTextComponent
import com.ridesharingapp.components.PasswordTextFieldComponent
import com.ridesharingapp.components.RegisterLoginRoutingText
import com.ridesharingapp.components.TermsAndConditionsText
import com.ridesharingapp.components.TextFieldComponent
import com.ridesharingapp.data.LoginViewModel
import com.ridesharingapp.data.UIEvent
import com.ridesharingapp.navigation.AppRouter
import com.ridesharingapp.navigation.Screen

@Composable
fun SignUpScreen(loginViewModel: LoginViewModel = viewModel()) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NormalTextComponent(value = stringResource(id = R.string.hello))
            HeadingTextComponent(value = stringResource(id = R.string.create_account))
            Spacer(modifier = Modifier.height(20.dp))
            TextFieldComponent(
                labelValue = stringResource(id = R.string.firstname),
                painterResource(id = R.drawable.profile),
                onTextChange = {
                    loginViewModel.onEvent(UIEvent.FirstNameChanged(it))
                    loginViewModel.printState()
                }
            )
            TextFieldComponent(
                labelValue = stringResource(id = R.string.last_name),
                painterResource = painterResource(id = R.drawable.profile),
                onTextChange = {
                    loginViewModel.onEvent(UIEvent.LastNameChanged(it))
                    loginViewModel.printState()
                }
            )
            TextFieldComponent(
                labelValue = stringResource(id = R.string.email),
                painterResource = painterResource(id = R.drawable.message),
                onTextChange = {
                    loginViewModel.onEvent(UIEvent.EmailChanged(it))
                    loginViewModel.printState()
                }
            )
            PasswordTextFieldComponent(
                labelValue = stringResource(id = R.string.password),
                painterResource = painterResource(id = R.drawable.lock),
                onTextChange = {
                    loginViewModel.onEvent(UIEvent.PasswordChanged(it))
                    loginViewModel.printState()
                }
            )
            CheckboxComponent(
                label = {
                    TermsAndConditionsText(onTextClickAction = {
                        AppRouter.navigateTo(Screen.TermsAndConditionsScreen)
                    })
                }
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                ButtonComponent(labelValue = stringResource(id = R.string.register))
                DividerTextComponent()
                RegisterLoginRoutingText(tryingToLogin = true, onTextClickAction = {
                    AppRouter.navigateTo(Screen.LoginScreen)
                })
            }
        }
    }
}

@Preview
@Composable
fun DefaultSignUpScreenPreview() {
    SignUpScreen()
}
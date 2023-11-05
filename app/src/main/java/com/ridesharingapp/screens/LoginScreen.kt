package com.ridesharingapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ridesharingapp.R
import com.ridesharingapp.components.ButtonComponent
import com.ridesharingapp.components.DividerTextComponent
import com.ridesharingapp.components.HeadingTextComponent
import com.ridesharingapp.components.NormalTextComponent
import com.ridesharingapp.components.PasswordTextFieldComponent
import com.ridesharingapp.components.RegisterLoginRoutingText
import com.ridesharingapp.components.TextFieldComponent
import com.ridesharingapp.components.UnderlinedClickableText
import com.ridesharingapp.data.login.LoginUIEvent
import com.ridesharingapp.data.login.LoginViewModel
import com.ridesharingapp.navigation.AppRouter
import com.ridesharingapp.navigation.Screen
import com.ridesharingapp.navigation.SystemBackButtonHandler

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel()) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(28.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                NormalTextComponent(value = stringResource(id = R.string.hello))
                HeadingTextComponent(value = stringResource(id = R.string.welcome))
                Spacer(modifier = Modifier.height(20.dp))
                TextFieldComponent(
                    labelValue = stringResource(id = R.string.email),
                    painterResource = painterResource(id = R.drawable.message),
                    onTextChange = {
                        loginViewModel.onEvent(LoginUIEvent.EmailChanged(it))
                    },
                    errorStatus = loginViewModel.loginUIState.emailErrorStatus.value
                )
                PasswordTextFieldComponent(
                    labelValue = stringResource(id = R.string.password),
                    painterResource = painterResource(id = R.drawable.lock),
                    onTextChange = {
                        loginViewModel.onEvent(LoginUIEvent.PasswordChanged(it))
                    },
                    errorStatus = loginViewModel.loginUIState.passwordErrorStatus.value
                )
                Spacer(modifier = Modifier.height(16.dp))
                UnderlinedClickableText(value = stringResource(id = R.string.forgot_password))

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    ButtonComponent(
                        labelValue = stringResource(id = R.string.login),
                        onClickAction = {
                            loginViewModel.onEvent(LoginUIEvent.LoginButtonClicked)
                        },
                        isEnabled = loginViewModel.allValidationPassed
                    )
                    DividerTextComponent()
                    RegisterLoginRoutingText(tryingToLogin = false, onTextClickAction = {
                        AppRouter.navigateTo(Screen.SignUpScreen)
                    })
                }
            }
        }
        if (loginViewModel.loginInProgress) {
            CircularProgressIndicator()
        }
    }

    SystemBackButtonHandler {
        AppRouter.navigateTo(Screen.SignUpScreen)
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
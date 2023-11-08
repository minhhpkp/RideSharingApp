package com.ridesharingapp.common.screens

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
import androidx.compose.ui.unit.dp
import com.ridesharingapp.common.R
import com.ridesharingapp.common.components.ButtonComponent
import com.ridesharingapp.common.components.DividerTextComponent
import com.ridesharingapp.common.components.ErrorText
import com.ridesharingapp.common.components.HeadingTextComponent
import com.ridesharingapp.common.components.NormalTextComponent
import com.ridesharingapp.common.components.PasswordTextFieldComponent
import com.ridesharingapp.common.components.RegisterLoginRoutingText
import com.ridesharingapp.common.components.TextFieldComponent
import com.ridesharingapp.common.components.UnderlinedClickableText
import com.ridesharingapp.common.components.UserAuthenticationFailedAlertDialog
import com.ridesharingapp.common.data.login.LoginUIEvent
import com.ridesharingapp.common.data.login.LoginViewModel
import com.ridesharingapp.common.navigation.AppRouter
import com.ridesharingapp.common.navigation.SystemBackButtonHandler

@Composable
fun <Screen> LoginScreen(
    loginViewModel: LoginViewModel,
    appRouter: AppRouter<Screen>,
    signUpScreen: Screen
) {
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
                    errorStatus = loginViewModel.loginUIState.emailErrorStatus.value,
                    isEmail = true
                )
                if (loginViewModel.loginUIState.emailErrorStatus.value) {
                    ErrorText(errorMessage = stringResource(id = R.string.incorrect_email_format))
                }

                PasswordTextFieldComponent(
                    labelValue = stringResource(id = R.string.password),
                    painterResource = painterResource(id = R.drawable.lock),
                    onTextChange = {
                        loginViewModel.onEvent(LoginUIEvent.PasswordChanged(it))
                    },
                    errorStatus = loginViewModel.loginUIState.passwordErrorStatus.value
                )
                if (loginViewModel.loginUIState.passwordErrorStatus.value) {
                    ErrorText(errorMessage = stringResource(id = R.string.password_format_error_message))
                }

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
                        isEnabled = loginViewModel.isAllValidationPassed()
                    )
                    DividerTextComponent()
                    RegisterLoginRoutingText(tryingToLogin = false, onTextClickAction = {
                        appRouter.navigateTo(signUpScreen)
                    })
                }
            }
        }
        if (loginViewModel.isLoginInProgress()) {
            CircularProgressIndicator()
        }
    }

    if (loginViewModel.isLoginFailed()) {
        UserAuthenticationFailedAlertDialog(
            dismiss = { loginViewModel.dismissFailureMessage() }
        )
    }

    SystemBackButtonHandler {
        appRouter.navigateTo(signUpScreen)
    }
}
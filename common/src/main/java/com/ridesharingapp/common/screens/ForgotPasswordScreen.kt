package com.ridesharingapp.common.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ridesharingapp.common.R
import com.ridesharingapp.common.components.ButtonComponent
import com.ridesharingapp.common.components.HeadingTextComponent
import com.ridesharingapp.common.components.TextFieldComponent
import com.ridesharingapp.common.data.forgotpassword.ForgotPasswordUIEvent
import com.ridesharingapp.common.data.forgotpassword.ForgotPasswordViewModel
import com.ridesharingapp.common.navigation.SystemBackButtonHandler

@Composable
fun <Screen> ForgotPasswordScreen(
    forgotPasswordViewModel: ForgotPasswordViewModel<Screen>
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
                HeadingTextComponent(value = "Reset your password here")
                Spacer(modifier = Modifier.height(20.dp))

                TextFieldComponent(
                    labelValue = "Enter your email address here",
                    painterResource = painterResource(id = R.drawable.message),
                    onTextChange = {
                        forgotPasswordViewModel.onEvent(ForgotPasswordUIEvent.EmailChanged(it))
                    },
                    errorStatus = forgotPasswordViewModel.showEmailFieldError(),
                    isEmail = true,
                    errorMessage = stringResource(R.string.incorrect_email_format)
                )

                Spacer(modifier = Modifier.height(20.dp))

                ButtonComponent(
                    labelValue = "Send reset email",
                    onClickAction = {
                        forgotPasswordViewModel.onEvent(ForgotPasswordUIEvent.SendButtonClicked)
                    },
                    isEnabled = forgotPasswordViewModel.sendEnabled()
                )
            }
        }

        if (forgotPasswordViewModel.isSendingInProgress()) {
            CircularProgressIndicator()
        }
    }

    if (forgotPasswordViewModel.showAlert()) {
        AlertDialog(
            onDismissRequest = { forgotPasswordViewModel.dismissAlert() },
            confirmButton = {
                TextButton(onClick = { forgotPasswordViewModel.dismissAlert() }) {
                    Text(text = "OK")
                }
            },
            title = {
                Text(
                    text = if (forgotPasswordViewModel.getSendingResultStringID() == R.string.reset_email_sent_successfully)
                        "Success" else "Failed"
                )
            },
            text = { Text(text = stringResource(forgotPasswordViewModel.getSendingResultStringID())) }
        )
    }

    SystemBackButtonHandler {
        forgotPasswordViewModel.onEvent(ForgotPasswordUIEvent.BackButtonClicked)
    }
}

@Preview
@Composable
fun ForgotPasswordScreenPreview() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeadingTextComponent(value = "Reset your password here")
            Spacer(modifier = Modifier.height(20.dp))

            TextFieldComponent(
                labelValue = "Enter your email address here",
                painterResource = painterResource(id = R.drawable.message),
                onTextChange = {

                },
                errorStatus = false,
                isEmail = true,
                errorMessage = stringResource(R.string.incorrect_email_format)
            )

            Spacer(modifier = Modifier.height(20.dp))

            ButtonComponent(
                labelValue = "Send reset email",
                onClickAction = {

                },
                isEnabled = true
            )
        }
    }
}
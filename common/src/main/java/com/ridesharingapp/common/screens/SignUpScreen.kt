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
import com.ridesharingapp.common.components.CheckboxComponent
import com.ridesharingapp.common.components.DividerTextComponent
import com.ridesharingapp.common.components.HeadingTextComponent
import com.ridesharingapp.common.components.NormalTextComponent
import com.ridesharingapp.common.components.PasswordTextFieldComponent
import com.ridesharingapp.common.components.RegisterLoginRoutingText
import com.ridesharingapp.common.components.TermsAndConditionsText
import com.ridesharingapp.common.components.TextFieldComponent
import com.ridesharingapp.common.components.UserAuthenticationFailedAlertDialog
import com.ridesharingapp.common.data.registration.RegistrationUIEvent
import com.ridesharingapp.common.data.registration.RegistrationViewModel

@Composable
fun <Screen> SignUpScreen(registrationViewModel: RegistrationViewModel<Screen>)
{
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
                HeadingTextComponent(value = stringResource(id = R.string.create_account))
                Spacer(modifier = Modifier.height(20.dp))

                TextFieldComponent(
                    labelValue = stringResource(id = R.string.firstname),
                    painterResource(id = R.drawable.profile),
                    onTextChange = {
                        registrationViewModel.onEvent(RegistrationUIEvent.FirstNameChanged(it))
                    },
                    errorStatus = registrationViewModel.registrationUIState.firstNameErrorStatus.value,
                    errorMessage = stringResource(R.string.name_format_error_message)
                )

                TextFieldComponent(
                    labelValue = stringResource(id = R.string.last_name),
                    painterResource = painterResource(id = R.drawable.profile),
                    onTextChange = {
                        registrationViewModel.onEvent(RegistrationUIEvent.LastNameChanged(it))
                    },
                    errorStatus = registrationViewModel.registrationUIState.lastNameErrorStatus.value,
                    errorMessage = stringResource(R.string.name_format_error_message)
                )

                TextFieldComponent(
                    labelValue = stringResource(id = R.string.email),
                    painterResource = painterResource(id = R.drawable.message),
                    onTextChange = {
                        registrationViewModel.onEvent(RegistrationUIEvent.EmailChanged(it))
                    },
                    errorStatus = registrationViewModel.registrationUIState.emailErrorStatus.value,
                    isEmail = true,
                    errorMessage = stringResource(R.string.incorrect_email_format)
                )

                PasswordTextFieldComponent(
                    labelValue = stringResource(id = R.string.password),
                    painterResource = painterResource(id = R.drawable.lock),
                    onTextChange = {
                        registrationViewModel.onEvent(RegistrationUIEvent.PasswordChanged(it))
                    },
                    errorStatus = registrationViewModel.registrationUIState.passwordErrorStatus.value
                )

                CheckboxComponent(
                    label = {
                        TermsAndConditionsText(onTextClickAction = {
                            registrationViewModel.onEvent(RegistrationUIEvent.TermsAndConditionsTextClicked)
                        })
                    },
                    onCheckedChange = {
                        registrationViewModel.onEvent(RegistrationUIEvent.TermsConditionChanged(it))
                    }
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    ButtonComponent(
                        labelValue = stringResource(id = R.string.register),
                        onClickAction = {
                            registrationViewModel.onEvent(RegistrationUIEvent.RegisterButtonClicked)
                        },
                        isEnabled = registrationViewModel.isAllValidationPassed()
                    )
                    DividerTextComponent()
                    RegisterLoginRoutingText(tryingToLogin = true, onTextClickAction = {
                        registrationViewModel.onEvent(RegistrationUIEvent.LoginTextClicked)
                    })
                }
            }
        }

        if (registrationViewModel.isRegistrationInProgress()) {
            CircularProgressIndicator()
        }
    }

    if (registrationViewModel.isRegistrationFailed()) {
        UserAuthenticationFailedAlertDialog(
            message = stringResource(registrationViewModel.failureMessage),
            dismiss = { registrationViewModel.dismissFailureMessage() }
        )
    }
}
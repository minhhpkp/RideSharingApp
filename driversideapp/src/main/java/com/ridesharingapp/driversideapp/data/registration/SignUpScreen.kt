package com.ridesharingapp.driversideapp.data.registration

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.ridesharingapp.common.screens.showError
import com.ridesharingapp.driversideapp.data.registration.SignUpUIEvent
import com.ridesharingapp.driversideapp.data.registration.SignUpViewModel

@Composable
fun SignUpScreen(signUpViewModel: SignUpViewModel) {
    val uiState by signUpViewModel.uiState.collectAsStateWithLifecycle()
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                NormalTextComponent(value = stringResource(id = R.string.hello))
                HeadingTextComponent(value = stringResource(id = R.string.create_account))
                Spacer(modifier = Modifier.height(20.dp))

                TextFieldComponent(
                    labelValue = stringResource(id = R.string.firstname),
                    painterResource(id = R.drawable.profile),
                    onTextChange = {
                        signUpViewModel.onEvent(SignUpUIEvent.FirstNameChanged(it))
                    },
                    textValue = uiState.firstName?:"",
                    errorStatus = showError(uiState.firstName, uiState.firstNameErrorStatus),
                    errorMessage = stringResource(R.string.name_format_error_message)
                )

                TextFieldComponent(
                    labelValue = stringResource(id = R.string.last_name),
                    painterResource = painterResource(id = R.drawable.profile),
                    onTextChange = {
                        signUpViewModel.onEvent(SignUpUIEvent.LastNameChanged(it))
                    },
                    textValue = uiState.lastName?:"",
                    errorStatus = showError(uiState.lastName, uiState.lastNameErrorStatus),
                    errorMessage = stringResource(R.string.name_format_error_message)
                )

                TextFieldComponent(
                    labelValue = stringResource(com.ridesharingapp.driversideapp.R.string.vehicle_type),
                    painterResource = painterResource(id = R.drawable.profile),
                    onTextChange = {
                        signUpViewModel.onEvent(SignUpUIEvent.VehicleTypeChanged(it))
                    },
                    textValue = uiState.vehicleType?:"",
                    errorStatus = showError(uiState.vehicleType, uiState.vehicleTypeErrorStatus),
                    errorMessage = stringResource(com.ridesharingapp.driversideapp.R.string.incorrect_vehicle_name_format)
                )

                TextFieldComponent(
                    labelValue = stringResource(com.ridesharingapp.driversideapp.R.string.license_plate),
                    painterResource = painterResource(id = R.drawable.profile),
                    onTextChange = {
                        signUpViewModel.onEvent(SignUpUIEvent.LicensePlateChanged(it))
                    },
                    textValue = uiState.licensePlate?:"",
                    errorStatus = showError(uiState.licensePlate, uiState.licensePlateErrorStatus),
                    errorMessage = stringResource(com.ridesharingapp.driversideapp.R.string.incorrect_license_plate_format)
                )

                TextFieldComponent(
                    labelValue = stringResource(id = R.string.email),
                    painterResource = painterResource(id = R.drawable.message),
                    onTextChange = {
                        signUpViewModel.onEvent(SignUpUIEvent.EmailChanged(it))
                    },
                    textValue = uiState.email?:"",
                    errorStatus = showError(uiState.email, uiState.emailErrorStatus),
                    isEmail = true,
                    errorMessage = stringResource(R.string.incorrect_email_format)
                )

                PasswordTextFieldComponent(
                    labelValue = stringResource(id = R.string.password),
                    painterResource = painterResource(id = R.drawable.lock),
                    onTextChange = {
                        signUpViewModel.onEvent(SignUpUIEvent.PasswordChanged(it))
                    },
                    password = uiState.password?:"",
                    errorStatus = showError(uiState.password, uiState.passwordErrorStatus)
                )

                CheckboxComponent(
                    label = {
                        TermsAndConditionsText(onTextClickAction = {
                            signUpViewModel.onEvent(SignUpUIEvent.TermsAndConditionsTextClicked)
                        })
                    },
                    onCheckedChange = {
                        signUpViewModel.onEvent(SignUpUIEvent.TermsConditionChanged(it))
                    }
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    ButtonComponent(
                        labelValue = stringResource(id = R.string.register),
                        onClickAction = {
                            signUpViewModel.onEvent(SignUpUIEvent.RegisterButtonClicked)
                        },
                        isEnabled = signUpViewModel.allValidationPassed(uiState)
                    )
                    DividerTextComponent()
                    RegisterLoginRoutingText(tryingToLogin = true, onTextClickAction = {
                        signUpViewModel.onEvent(SignUpUIEvent.LoginTextClicked)
                    })
                }
            }
        }

        if (uiState.registrationInProgress) {
            CircularProgressIndicator()
        }
    }

    if (uiState.registrationFailed) {
        UserAuthenticationFailedAlertDialog(
            message = stringResource(R.string.failed_to_sign_up_please_try_again),
            dismiss = { signUpViewModel.dismissFailureMessage() }
        )
    }
}
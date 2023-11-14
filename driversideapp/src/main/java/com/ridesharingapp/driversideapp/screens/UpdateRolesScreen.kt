package com.ridesharingapp.driversideapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.ridesharingapp.common.components.NormalTextComponent
import com.ridesharingapp.common.components.TextFieldComponent
import com.ridesharingapp.common.screens.showError
import com.ridesharingapp.driversideapp.data.updateroles.UpdateRolesUIEvent
import com.ridesharingapp.driversideapp.data.updateroles.UpdateRolesViewModel

@Composable
fun UpdateRolesScreen(updateRolesViewModel: UpdateRolesViewModel) {
    val uiState by updateRolesViewModel.uiState.collectAsStateWithLifecycle()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(28.dp)
        ) {
            Column {
                NormalTextComponent(value = uiState.headerText)
                Spacer(modifier = Modifier.height(20.dp))

                TextFieldComponent(
                    labelValue = stringResource(com.ridesharingapp.driversideapp.R.string.vehicle_type),
                    painterResource = painterResource(id = R.drawable.profile),
                    onTextChange = {
                        updateRolesViewModel.onEvent(UpdateRolesUIEvent.VehicleTypeChanged(it))
                    },
                    textValue = uiState.vehicleType?:"",
                    errorStatus = showError(uiState.vehicleType, uiState.vehicleTypeErrorStatus),
                    errorMessage = stringResource(com.ridesharingapp.driversideapp.R.string.incorrect_vehicle_name_format)
                )

                TextFieldComponent(
                    labelValue = stringResource(com.ridesharingapp.driversideapp.R.string.license_plate),
                    painterResource = painterResource(id = R.drawable.profile),
                    onTextChange = {
                        updateRolesViewModel.onEvent(UpdateRolesUIEvent.LicensePlateChanged(it))
                    },
                    textValue = uiState.licensePlate?:"",
                    errorStatus = showError(uiState.licensePlate, uiState.licensePlateErrorStatus),
                    errorMessage = stringResource(com.ridesharingapp.driversideapp.R.string.incorrect_license_plate_format)
                )

                Spacer(modifier = Modifier.height(20.dp))

                ButtonComponent(
                    labelValue = "Update Profile",
                    onClickAction = {
                        updateRolesViewModel.onEvent(UpdateRolesUIEvent.UpdateButtonClicked)
                    },
                    isEnabled = updateRolesViewModel.allValidationPassed(uiState)
                )
            }
        }

        if (uiState.result != -1) {
            AlertDialog(
                onDismissRequest = { updateRolesViewModel.dismissResDialog() },
                confirmButton = {
                    TextButton(onClick = { updateRolesViewModel.dismissResDialog() }) {
                        Text(text = "OK")
                    }
                },
                title = { Text(text = "Update result") },
                text = { Text(text = stringResource(id = uiState.result)) }
            )
        }
    }
}
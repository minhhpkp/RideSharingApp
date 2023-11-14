package com.ridesharingapp.passengersideapp.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ridesharingapp.common.screens.LoginScreen
import com.ridesharingapp.passengersideapp.data.login.PassengerLoginViewModel

@Composable
fun PassengerLoginScreen(passengerLoginViewModel: PassengerLoginViewModel) {
    LoginScreen(loginViewModel = passengerLoginViewModel)

    val updateResult by passengerLoginViewModel.updateResult.collectAsStateWithLifecycle()
    if (updateResult != -1) {
        AlertDialog(
            onDismissRequest = { passengerLoginViewModel.dismissUpdateMsg() },
            confirmButton = {
                TextButton(onClick = { passengerLoginViewModel.dismissUpdateMsg() }) {
                    Text(text = "OK")
                }
            },
            title = { Text(text = "Update result") },
            text = { Text(text = stringResource(id = updateResult)) }
        )
    }
}
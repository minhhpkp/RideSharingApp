package com.ridesharingapp.driversideapp.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ridesharingapp.common.screens.LoginScreen
import com.ridesharingapp.driversideapp.data.login.DriverLoginViewModel

@Composable
fun DriverLoginScreen(driverLoginViewModel: DriverLoginViewModel) {
    LoginScreen(loginViewModel = driverLoginViewModel)

    val showError by driverLoginViewModel.showError.collectAsStateWithLifecycle()
    if (showError) {
        AlertDialog(
            onDismissRequest = { driverLoginViewModel.dismissUserCollisionMessage() },
            confirmButton = {
                TextButton(onClick = { driverLoginViewModel.dismissUserCollisionMessage() }) {
                    Text(text = "OK")
                }
            },
            text = {
                Text("You have already had an account associated with this email address." +
                        " Please login to continue.")
            }
        )
    }
}
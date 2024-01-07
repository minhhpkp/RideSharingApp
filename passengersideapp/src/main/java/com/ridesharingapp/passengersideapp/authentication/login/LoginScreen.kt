package com.ridesharingapp.passengersideapp.authentication.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ridesharingapp.common.R
import com.ridesharingapp.common.style.color_primary
import com.ridesharingapp.common.style.color_white
import com.ridesharingapp.common.style.typography
import com.ridesharingapp.common.uicommon.AppHeader

@Composable
fun LoginScreen(
    viewModel: LoginViewModel
) {
    val clearingPrevLogin by viewModel.clearingPrevLogin.collectAsStateWithLifecycle()
    val loginInProcess by viewModel.loginInProcess.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    LoginScreen(
        showLoading = clearingPrevLogin || loginInProcess,
        email = email,
        updateEmail = { newEmail -> viewModel.updateEmail(newEmail) },
        password = password,
        updatePassword = { newPassword -> viewModel.updatePassword(newPassword) },
        handleLogin = { viewModel.handleLogin() },
        goToSignUp = { viewModel.goToSignup() }
    )
}

@Composable
fun LoginScreen(
    showLoading: Boolean,
    email: String,
    updateEmail: ((String) -> Unit),
    password: String,
    updatePassword: ((String) -> Unit),
    handleLogin: () -> Unit,
    goToSignUp: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color_white),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (showLoading) Arrangement.Center else Arrangement.Top
    )
    {
        if (showLoading) {
            Text(
                text = stringResource(id = R.string.loading),
                style = TextStyle(fontSize = 18.sp)
            )
            CircularProgressIndicator()
        } else {
            AppHeader(
                modifier = Modifier.padding(top = 64.dp),
                subtitleText = stringResource(id = R.string.need_a_ride)
            )

            EmailInputField(
                modifier = Modifier.padding(top = 16.dp),
                updateEmail = updateEmail,
                email = email
            )

            PasswordInputField(
                modifier = Modifier.padding(top = 16.dp),
                password = password,
                updatePassword = updatePassword
            )

            LoginContinueButton(
                modifier = Modifier.padding(top = 32.dp),
                handleLogin = { handleLogin() }
            )

            SignupText(
                modifier = Modifier.padding(top = 32.dp),
                goToSignUp = goToSignUp
            )
        }
    }
}

@Composable
fun EmailInputField(
    modifier: Modifier = Modifier,
    email: String,
    updateEmail: (String) -> Unit
) {

    OutlinedTextField(
        modifier = modifier,
        value = email,
        onValueChange = {
            updateEmail(it)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType =  KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        label = { Text(text = stringResource(id = R.string.email)) }
    )
}

@Composable
fun PasswordInputField(
    modifier: Modifier = Modifier,
    password: String,
    updatePassword: (String) -> Unit
) {
    val localFocusManager = LocalFocusManager.current
    var showPassword by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier,
        value = password,
        onValueChange = {
            updatePassword(it)
        },
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions{
            localFocusManager.clearFocus()
        },
        singleLine = true,
        label = { Text(text = stringResource(id = R.string.password)) },
        trailingIcon = {
            val image = if (showPassword)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            val description = if (showPassword) stringResource(id = R.string.hide_password) else stringResource(id = R.string.show_password)
            IconButton(onClick = { showPassword = !showPassword}){
                Icon(imageVector  = image, description)
            }
        }
    )
}

@Composable
fun LoginContinueButton(
    modifier: Modifier,
    handleLogin: () -> Unit
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color_primary,
            contentColor = color_white
        ),
        onClick = { handleLogin() },
    ) {
        Text(
            text = stringResource(id = R.string.string_continue),
            style = typography.button
        )
    }
}

@Composable
fun SignupText(
    modifier: Modifier,
    goToSignUp: () -> Unit
) {
    TextButton(
        modifier = modifier,
        onClick = { goToSignUp() }) {
        Text(
            style = typography.subtitle2,
            text = buildAnnotatedString {
                append(stringResource(id = R.string.no_account))
                append(" ")
                withStyle(
                    SpanStyle(
                        color = color_primary,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append(stringResource(id = R.string.sign_up))
                }
                append(" ")
                append(stringResource(id = R.string.here))
            }
        )
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        showLoading = false,
        email = "some@email.com",
        updateEmail = {},
        password = "123456",
        updatePassword = {},
        handleLogin = {},
        goToSignUp = {}
    )
}
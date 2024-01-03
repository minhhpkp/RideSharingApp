package com.ridesharingapp.passengersideapp.authentication.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
fun SignUpScreen(
    viewModel: SignUpViewModel
) {
    val showLoading by viewModel.showLoading.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()

    SignUpScreen(
        showLoading = showLoading,
        handleBackPress = { viewModel.handleBackPress() },
        name = name,
        updateName = { newName -> viewModel.updateName(newName) },
        email = email,
        updateEmail = { newEmail -> viewModel.updateEmail(newEmail) },
        password = password,
        updatePassword = { newPassword -> viewModel.updatePassword(newPassword) },
        handleSignUp = { viewModel.handleSignUp() }
    )
}

@Composable
fun SignUpScreen(
    showLoading: Boolean,
    handleBackPress: () -> Unit,
    name: String,
    updateName: (String) -> Unit,
    email: String,
    updateEmail: (String) -> Unit,
    password: String,
    updatePassword: (String) -> Unit,
    handleSignUp: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = color_white),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (showLoading) Arrangement.Center else Arrangement.Top
    ) {
        if (showLoading) {
            Text(
                text = stringResource(id = R.string.loading),
                style = TextStyle(fontSize = 18.sp)
            )
            CircularProgressIndicator()
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Icon(
                    modifier = Modifier.clickable { handleBackPress() },
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Back button",
                )
            }

            AppHeader(
                modifier = Modifier.padding(top = 64.dp),
                subtitleText = stringResource(id = R.string.sign_up_for_free)
            )

            UsernameInputField(
                modifier = Modifier.padding(top = 16.dp),
                name = name,
                updateName = updateName
            )

            EmailInputField(
                modifier = Modifier.padding(top = 16.dp),
                email = email,
                updateEmail = updateEmail
            )

            PasswordInputField(
                modifier = Modifier.padding(top = 16.dp),
                password = password,
                updatePassword = updatePassword
            )

            SignUpContinueButton(
                modifier = Modifier.padding(top = 32.dp),
                handleSignUp = handleSignUp
            )
        }
    }
}

@Composable
fun SignUpContinueButton(
    modifier: Modifier,
    handleSignUp: () -> Unit
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color_primary,
            contentColor = color_white
        ),
        onClick = { handleSignUp() },
    ) {
        Text(
            text = stringResource(id = R.string.string_continue),
            style = typography.button
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsernameInputField(
    modifier: Modifier = Modifier,
    name: String,
    updateName: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = name,
        onValueChange = {
            updateName(it)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType =  KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        label = { Text(text = stringResource(id = R.string.user_name)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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

@OptIn(ExperimentalMaterial3Api::class)
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

@Preview
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(
        showLoading = false,
        handleBackPress = {},
        name = "some name",
        updateName = {},
        email = "some@email.com",
        updateEmail = {},
        password = "123456",
        updatePassword = {}
    ) {}
}
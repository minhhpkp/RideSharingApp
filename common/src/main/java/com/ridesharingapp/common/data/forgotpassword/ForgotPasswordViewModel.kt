package com.ridesharingapp.common.data.forgotpassword

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ridesharingapp.common.R
import com.ridesharingapp.common.data.rules.Validator
import com.ridesharingapp.common.navigation.AppRouter
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ForgotPasswordViewModel<Screen>(
    private val auth: FirebaseAuth,
    private val appRouter: AppRouter<Screen>,
    private val loginScreen: Screen
) : ViewModel() {
    private val forgotPasswordUIState = MutableStateFlow(ForgotPasswordUIState())
    private var sendingInProgress by mutableStateOf(false)
    private var sendingResult by mutableIntStateOf(-1)

    fun showEmailFieldError(): Boolean {
        return if (forgotPasswordUIState.value.email == null) false
        else forgotPasswordUIState.value.emailErrorStatus
    }

    fun sendEnabled(): Boolean {
        return !forgotPasswordUIState.value.emailErrorStatus
    }

    fun isSendingInProgress(): Boolean {
        return sendingInProgress
    }

    fun dismissAlert() {
        sendingResult = -1
    }

    fun showAlert(): Boolean {
        return sendingResult != -1
    }

    fun getSendingResultStringID(): Int {
        if (sendingResult == -1) {
            throw Exception("Sending process has not started.")
        }
        return sendingResult
    }

    fun onEvent(event: ForgotPasswordUIEvent) {
        when (event) {
            is ForgotPasswordUIEvent.EmailChanged -> {
                forgotPasswordUIState.update {
                    it.copy(
                        email = event.email,
                        emailErrorStatus = !Validator.validateEmail(event.email).status
                    )
                }
            }
            is ForgotPasswordUIEvent.SendButtonClicked -> {
                if (sendEnabled()) {
                    onSendClickHandler()
                }
            }
            is ForgotPasswordUIEvent.BackButtonClicked -> {
                appRouter.navigateTo(loginScreen)
            }
        }
    }

    // Check if email is registered

    private fun onSendClickHandler() {
        forgotPasswordUIState.value.email?.let { email ->
            sendingInProgress = true
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    sendingInProgress = false
                    sendingResult = if (task.isSuccessful) {
                        Log.d("ForgotPassword_SendEmail:success", "Email sent.")
                        R.string.reset_email_sent_successfully
                    } else {
                        Log.w("ForgotPassword_SendEmail:failed", "Failed", task.exception)
                        R.string.failed_to_send_reset_email
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
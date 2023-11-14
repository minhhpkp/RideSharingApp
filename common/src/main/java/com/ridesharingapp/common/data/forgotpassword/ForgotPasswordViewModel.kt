package com.ridesharingapp.common.data.forgotpassword

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.ridesharingapp.common.R
import com.ridesharingapp.common.data.rules.Validator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ForgotPasswordViewModel(
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordUIState())
    val uiState: StateFlow<ForgotPasswordUIState> = _uiState.asStateFlow()

    fun onEvent(event: ForgotPasswordUIEvent) {
        when (event) {
            is ForgotPasswordUIEvent.EmailChanged -> {
                _uiState.update {
                    it.copy(
                        email = event.email,
                        emailErrorStatus = !Validator.validateEmail(event.email).status
                    )
                }
            }
            is ForgotPasswordUIEvent.SendButtonClicked -> {
                if (!_uiState.value.emailErrorStatus) {
                    onSendClickHandler()
                }
            }
        }
    }

    // Check if email is registered

    private fun onSendClickHandler() {
        _uiState.value.email?.let { email ->
            _uiState.update{ it.copy(sendingInProgress = true) }
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    _uiState.update { it.copy(sendingInProgress = false) }
                    _uiState.update {
                        it.copy(
                            sendingResult = if (task.isSuccessful) {
                                Log.d("ForgotPassword_SendEmail:success", "Email sent.")
                                R.string.reset_email_sent_successfully
                            } else {
                                Log.w("ForgotPassword_SendEmail:failed", "Failed", task.exception)
                                R.string.failed_to_send_reset_email
                            }
                        )
                    }
                }
        }
    }

    fun dismissAlert() {
        _uiState.update { it.copy(sendingResult = -1) }
    }
}
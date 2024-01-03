package com.ridesharingapp.passengersideapp.authentication.signup

import android.util.Log
import com.ridesharingapp.common.ServiceResult
import com.ridesharingapp.common.services.SignUpResult
import com.ridesharingapp.common.uicommon.ToastMessages
import com.ridesharingapp.common.usecases.SignUpUser
import com.ridesharingapp.passengersideapp.navigation.SplashKey
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.StateChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SignUpViewModel(
    private val backstack: Backstack,
    private val signUp: SignUpUser,
) : ScopedServices.Activated, CoroutineScope {
    internal var toastHandler: ((ToastMessages) -> Unit)? = null

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    fun updateEmail(input: String) {
        _email.update { input }
    }

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()
    fun updateName(input: String) {
        _name.update { input }
    }

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()
    fun updatePassword(input: String) {
        _password.update { input }
    }

    private val _showLoading = MutableStateFlow(false)
    val showLoading: StateFlow<Boolean> = _showLoading.asStateFlow()

    fun handleSignUp() = launch(Dispatchers.Main) {
        _showLoading.update { true }
        val signupAttempt = signUp.signUpUser(_email.value, _password.value, _name.value)
        _showLoading.update { false }
        when (signupAttempt) {
            is ServiceResult.Failure -> {
                Log.w("SignUpViewModel", "sign up failed", signupAttempt.exception)
                toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            }
            is ServiceResult.Value -> {
                when (signupAttempt.value) {
                    is SignUpResult.Success -> {
                        backstack.setHistory(
                            History.of(SplashKey()),
                            //Direction of navigation which is used for animation
                            StateChange.REPLACE
                        )
                    }
                    SignUpResult.InvalidCredentials -> toastHandler?.invoke(ToastMessages.INVALID_CREDENTIALS)
                    SignUpResult.AlreadySignedUp -> toastHandler?.invoke(ToastMessages.ACCOUNT_EXISTS)
                }
            }
        }
    }

    fun handleBackPress() {
        backstack.goBack()
    }
    override fun onServiceActive() = Unit

    override fun onServiceInactive() {
        canceller.cancel()
        toastHandler = null
    }

    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main
}
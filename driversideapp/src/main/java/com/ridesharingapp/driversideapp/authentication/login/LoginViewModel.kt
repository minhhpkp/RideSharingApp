package com.ridesharingapp.driversideapp.authentication.login

import android.util.Log
import com.ridesharingapp.common.ServiceResult
import com.ridesharingapp.common.keys.TYPE_DRIVER
import com.ridesharingapp.common.services.FirebaseAuthService
import com.ridesharingapp.common.services.LogInResult
import com.ridesharingapp.common.uicommon.ToastMessages
import com.ridesharingapp.common.usecases.LogInUser
import com.ridesharingapp.driversideapp.navigation.DriverHomeKey
import com.ridesharingapp.driversideapp.navigation.SignUpKey
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.StateChange
import io.getstream.chat.android.client.ChatClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


class LoginViewModel(
    private val backstack: Backstack,
    private val login: LogInUser,
    private val authService: FirebaseAuthService,
    private val client: ChatClient
) : ScopedServices.Activated, CoroutineScope {
    internal var toastHandler: ((ToastMessages) -> Unit)? = null

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    fun updateEmail(input: String) {
        _email.update { input }
    }

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun updatePassword(input: String) {
        _password.update { input }
    }

    private val _clearingPrevLogin = MutableStateFlow(false)
    val clearingPrevLogin: StateFlow<Boolean> = _clearingPrevLogin.asStateFlow()

    private val _loginInProcess = MutableStateFlow(false)
    val loginInProcess = _loginInProcess.asStateFlow()

    fun handleLogin() = launch(Dispatchers.Main) {
        Log.d("LoginViewModel", "login button clicked lv1")
        Log.d("LoginViewModel", "Thread = ${Thread.currentThread().name}")

        _loginInProcess.update { true }

        val loginAttempt = login.login(_email.value, _password.value, TYPE_DRIVER)

        _loginInProcess.update { false }

        when (loginAttempt) {
            is ServiceResult.Failure -> {
                Log.w("LoginViewModel", "login failed", loginAttempt.exception)
                toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            }

            is ServiceResult.Value -> {
                when (loginAttempt.value) {
                    is LogInResult.Success -> withContext(coroutineContext){
                        sendToDashboard()
                    }
                    is LogInResult.InvalidCredentials -> {
                        toastHandler?.invoke(ToastMessages.INVALID_CREDENTIALS)
                    }
                }
            }
        }
    }

    private fun sendToDashboard() {
        canceller.cancel()
        backstack.setHistory(
            History.of(DriverHomeKey()),
                //Direction of navigation which is used for animation
            StateChange.FORWARD
        )
    }

    fun goToSignup() {
        backstack.goTo(SignUpKey())
    }

    override fun onServiceActive() {
        Log.d("LoginViewModel", "onServiceActive")
        _clearingPrevLogin.update { true }
        // attempt to logout of firebase account
        val fireAuthUser = authService.auth.currentUser
        if (fireAuthUser != null) {
            Log.d("LoginViewModel", "onServiceActive:firebaseUser ${fireAuthUser.email}")
            authService.logout()
        }

        // attempt to disconnect the current stream user
        val user = client.getCurrentUser()
        if (user != null) {
            Log.d("LoginViewModel", "onServiceActive:chatClientUser ${user.name}")
            client.disconnect(flushPersistence = true).enqueue {
                _clearingPrevLogin.update { false }
                if (it.isError) {
                    Log.w(
                        "LoginViewModel",
                        it.error().message ?: "Error logging out",
                        it.error().cause
                    )
                } else {
                    Log.d("LoginViewModel", "clearing old user:disconnect chat client:success")
                }
            }
        } else {
            _clearingPrevLogin.update { false }
        }
    }
    override fun onServiceInactive() {
        toastHandler = null
    }

    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main
}
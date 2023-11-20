package com.ridesharingapp.passengersideapp.authentication.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ridesharingapp.passengersideapp.ServiceResult
import com.ridesharingapp.passengersideapp.domain.AppUser
import com.ridesharingapp.passengersideapp.navigation.PassengerDashboardKey
import com.ridesharingapp.passengersideapp.navigation.SignUpKey
import com.ridesharingapp.passengersideapp.services.LogInResult
import com.ridesharingapp.passengersideapp.uicommon.ToastMessages
import com.ridesharingapp.passengersideapp.usecases.LogInUser
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.StateChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LoginViewModel(
    private val backstack: Backstack,
    private val login: LogInUser
) : ScopedServices.Activated, CoroutineScope {
    internal var toastHandler: ((ToastMessages) -> Unit)? = null

    var email by mutableStateOf("")
        private set

    fun updateEmail(input: String) {
        email = input
    }

    var password by mutableStateOf("")
        private set

    fun updatePassword(input: String) {
        password = input
    }

    fun handleLogin() = launch(Dispatchers.Main) {
        val loginAttempt = login.login(email, password)
        when (loginAttempt) {
            is ServiceResult.Failure -> toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            is ServiceResult.Value -> {
                val result = loginAttempt.value
                when (result) {
                    is LogInResult.Success -> sendToDashboard(result.user)
                    LogInResult.InvalidCredentials -> toastHandler?.invoke(ToastMessages.INVALID_CREDENTIALS)
                }
            }
        }
    }

    private fun sendToDashboard(user: AppUser) {
         backstack.setHistory(
            History.of(PassengerDashboardKey()),
            //Direction of navigation which is used for animation
            StateChange.FORWARD
        )
    }

    fun goToSignup() {
        backstack.goTo(SignUpKey())
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
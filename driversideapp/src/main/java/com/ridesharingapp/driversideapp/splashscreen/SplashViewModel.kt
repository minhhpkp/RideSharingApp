package com.ridesharingapp.driversideapp.splashscreen

import android.util.Log
import com.ridesharingapp.common.ServiceResult
import com.ridesharingapp.common.domain.GrabLamUser
import com.ridesharingapp.common.usecases.GetUser
import com.ridesharingapp.driversideapp.navigation.DriverHomeKey
import com.ridesharingapp.driversideapp.navigation.LoginKey
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.StateChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class SplashViewModel(
    val backstack: Backstack,
    val getUser: GetUser
) : ScopedServices.Activated, CoroutineScope {
    private fun sendToLogin() {
        backstack.setHistory(
            History.of(LoginKey()),
            StateChange.FORWARD
        )
    }

    fun checkAuthState() = launch {
        val getUser = getUser.getUser()

        when (getUser) {
            //there's nothing else to do but send to the login page
            is ServiceResult.Failure -> {
                Log.e("SplashViewModel", "failed to get current user", getUser.exception)
                sendToLogin()
            }
            is ServiceResult.Value -> {
                if (getUser.value == null) {
                    Log.d("SplashViewModel", "checkAuthState: null user")
                    sendToLogin()
                }
                else {
                    Log.d("SplashViewModel", "get current user successfully")
                    sendToDashboard(getUser.value!!)
                }
            }
        }
    }

    private fun sendToDashboard(user: GrabLamUser) {
        Log.d("SplashViewMode", "logged in user: $user")
        backstack.setHistory(
            History.of((DriverHomeKey())),
            StateChange.FORWARD
        )
    }

    //Lifecycle method to Fetch things if necessary
    override fun onServiceActive() {
        checkAuthState()
    }

    //Tear down
    override fun onServiceInactive() {
        canceller.cancel()
    }

    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main
}
package com.ridesharingapp.driversideapp.settings

import android.net.Uri
import android.util.Log
import com.ridesharingapp.common.ServiceResult
import com.ridesharingapp.common.domain.GrabLamUser
import com.ridesharingapp.common.domain.UserType
import com.ridesharingapp.common.services.HistoryService
import com.ridesharingapp.common.uicommon.ToastMessages
import com.ridesharingapp.common.uicommon.history.HistoryKey
import com.ridesharingapp.common.usecases.GetUser
import com.ridesharingapp.common.usecases.LogOutUser
import com.ridesharingapp.common.usecases.UpdateUserAvatar
import com.ridesharingapp.driversideapp.navigation.LoginKey
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

class ProfileSettingsViewModel(
    private val backstack: Backstack,
    private val updateUserAvatar: UpdateUserAvatar,
    private val logUserOut: LogOutUser,
    private val historyService: HistoryService,
    private val getUser: GetUser,

) : ScopedServices.Activated, CoroutineScope {
    internal var toastHandler: ((ToastMessages) -> Unit)? = null

    private val _userModel = MutableStateFlow<GrabLamUser?>(null)
    val userModel: StateFlow<GrabLamUser?> get() = _userModel
    private val _profilePicUpdateInProgress = MutableStateFlow(false)
    val profilePicUpdateInProgress: StateFlow<Boolean> get() = _profilePicUpdateInProgress.asStateFlow()

    private val _rating: MutableStateFlow<Double?> = MutableStateFlow(null)
    val rating = _rating.asStateFlow()

    fun handleLogOut() = launch(Dispatchers.Main) {
        logUserOut.logout()
        Log.d(TAG, "Logout successfully")
        sendToLogin()
    }

    private fun getUser() = launch(Dispatchers.Main) {
        when (val getUser = getUser.getUser()) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                sendToLogin()
            }

            is ServiceResult.Value -> {
                if (getUser.value == null) sendToLogin()
                else {
                    _userModel.value = getUser.value
                    historyService.startListeningForRatingChanges(
                        driverId = _userModel.value!!.userId,
                        onSuccess = { newRating ->
                            _rating.update { newRating }
                        },
                        onError = {
                            Log.e(TAG, "Error getting new rating", it)
                            toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
                        }
                    )
                }
            }
        }
    }

    private fun sendToLogin() {
        backstack.setHistory(
            History.of(LoginKey()),
            StateChange.REPLACE
        )
    }

    override fun onServiceActive() {
        getUser()
    }

    override fun onServiceInactive() {
        historyService.stopListeningForRatingChanges()
        canceller.cancel()
        toastHandler = null
    }

    fun handleThumbnailUpdate(imageUri: Uri?) {
        launch(Dispatchers.Main) {
            if (imageUri != null) {
                Log.d("ProfileSettingsViewModel", imageUri.toString())
                _profilePicUpdateInProgress.update { true }
                val updateAttempt =
                    updateUserAvatar.updateAvatar(_userModel.value!!, imageUri.toString())
                when (updateAttempt) {
                    is ServiceResult.Failure -> toastHandler?.invoke(ToastMessages.SERVICE_ERROR)

                    is ServiceResult.Value -> {
                        _userModel.value = _userModel.value!!.copy(
                            avatarPhotoUrl = updateAttempt.value
                        )
                        toastHandler?.invoke(ToastMessages.UPDATE_SUCCESSFUL)
                    }
                }
                _profilePicUpdateInProgress.update { false }
            } else {
                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
            }
        }
    }

    fun seeHistory() {
        if (_userModel.value != null) {
            val userId = _userModel.value!!.userId
            val userType = UserType.DRIVER
            Log.d(TAG, "See history userId=${userId} type=${userType}")
            backstack.goTo(
                HistoryKey(
                    userId = userId,
                    userType = userType
                )
            )
        }
    }

    fun handleBackPress() {
        backstack.goBack()
    }

    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main

    companion object {
        val TAG = ProfileSettingsViewModel::class.simpleName
    }
}
package com.ridesharingapp.passengersideapp.profile.settings

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
import com.ridesharingapp.passengersideapp.navigation.LoginKey
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
    private val getUser: GetUser
) : ScopedServices.Activated, CoroutineScope {
    internal var toastHandler: ((ToastMessages) -> Unit)? = null

    private val _userModel = MutableStateFlow<GrabLamUser?>(null)
    val userModel: StateFlow<GrabLamUser?> get() = _userModel
    private val _profilePicUpdateInProgress = MutableStateFlow(false)
    val profilePicUpdateInProgress: StateFlow<Boolean> get() = _profilePicUpdateInProgress.asStateFlow()

    private val _earnedPoints: MutableStateFlow<Long> = MutableStateFlow(0)
    val earnedPoints = _earnedPoints.asStateFlow()

    fun handleLogOut() = launch(Dispatchers.Main) {
        logUserOut.logout()
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
                    historyService.startListeningForEarnedPointsChanges(
                        passengerId = _userModel.value!!.userId,
                        onSuccess = { newRewardPoints ->
                            if (newRewardPoints == null) {
                                Log.e(TAG, "newRewardPoints is null")
                                toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
                            } else {
                                _earnedPoints.update { newRewardPoints }
                            }
                        },
                        onError = {
                            Log.e(TAG, "Error getting new reward points", it)
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
        historyService.stopListeningForRewardPointsChanges()
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

    /*private suspend fun updateUser(user: GrabLamUser) {
        val updateAttempt = userService.updateUser(user)

        when (updateAttempt) {
            is ServiceResult.Failure -> toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            is ServiceResult.Value -> {
                if (updateAttempt.value == null) sendToLogin()
                else _userModel.value = updateAttempt.value
            }
        }
    }*/

    /*fun handleToggleUserType() = launch(Dispatchers.Main) {
        val oldModel = _userModel.value!!
        val newType = flipType(oldModel.type)

        updateUser(oldModel.copy(type = newType))
    }*/

    /*private fun flipType(oldType: String): String {
        return if (oldType == UserType.PASSENGER.value) UserType.DRIVER.value
        else UserType.PASSENGER.value
    }*/

    fun seeHistory() {
        if (_userModel.value != null) {
            val userId = _userModel.value!!.userId
            val userType = if (_userModel.value!!.type == UserType.PASSENGER.value) UserType.PASSENGER else UserType.DRIVER
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
        /*backstack.setHistory(
            History.of(PassengerDashboardKey()),
            //Direction of navigation which is used for animation
            StateChange.BACKWARD
        )*/
        backstack.goBack()
    }

    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main

    companion object {
        val TAG = ProfileSettingsViewModel::class.simpleName
    }
}
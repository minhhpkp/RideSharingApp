package com.ridesharingapp.common.uicommon.history

import android.util.Log
import com.ridesharingapp.common.domain.HistoryRide
import com.ridesharingapp.common.domain.UserType
import com.ridesharingapp.common.services.HistoryService
import com.ridesharingapp.common.uicommon.ToastMessages
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.ScopedServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.CoroutineContext

class HistoryViewModel(
    private val backstack: Backstack,
    private val userId: String,
    val type: UserType,
    private val historyService: HistoryService
):  ScopedServices.Activated, CoroutineScope {
    private val _history: MutableStateFlow<List<HistoryRide>> = MutableStateFlow(emptyList())
    val history = _history.asStateFlow()

    private val canceller = Job()
    internal var toastHandler: ((ToastMessages) -> Unit)? = null

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main

    fun handleBackPress() {
        backstack.goBack()
    }

    override fun onServiceActive() {
        historyService.startListeningForHistoryChanges(
            userId = userId,
            type = type,
            onSuccess = { newHistory ->
                if (newHistory == null) {
                    Log.e(TAG, "newHistory is null")
                    toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
                } else {
                    Log.d("HistoryViewModel", "onServiceActive: update successfully")
                    _history.update { newHistory }
                }
            },
            onError = {
                toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            }
        )
    }

    override fun onServiceInactive() {
        historyService.stopListeningForHistoryChanges()
        canceller.cancel()
        toastHandler = null
    }

    companion object {
        val TAG = HistoryViewModel::class.simpleName
    }
}
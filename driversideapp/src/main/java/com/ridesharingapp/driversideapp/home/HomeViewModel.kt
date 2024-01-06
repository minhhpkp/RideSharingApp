package com.ridesharingapp.driversideapp.home

import android.util.Log
import com.google.maps.model.LatLng
import com.ridesharingapp.common.ServiceResult
import com.ridesharingapp.common.domain.GrabLamUser
import com.ridesharingapp.common.domain.Ride
import com.ridesharingapp.common.domain.RideStatus
import com.ridesharingapp.common.services.RideService
import com.ridesharingapp.common.uicommon.ToastMessages
import com.ridesharingapp.common.uicommon.combineTuple
import com.ridesharingapp.common.usecases.GetUser
import com.ridesharingapp.driversideapp.navigation.ChatKey
import com.ridesharingapp.driversideapp.navigation.LoginKey
import com.ridesharingapp.driversideapp.navigation.ProfileSettingsKey
import com.ridesharingapp.driversideapp.navigation.SplashKey
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.StateChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class HomeViewModel(
    val backstack: Backstack,
    val getUser: GetUser,
    val rideService: RideService
    ) : ScopedServices.Activated, CoroutineScope {
    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main

    internal var toastHandler: ((ToastMessages) -> Unit)? = null

    private val _driverModel = MutableStateFlow<GrabLamUser?>(null)
    private val _rideModel: Flow<ServiceResult<Ride?>> = rideService.rideFlow()
//    private val _mapIsReady = MutableStateFlow(false)
    private val _currentMessagesCount = MutableStateFlow(0)

    /*
    Different UI states:
    1. User may never be null
    2. Ride may be null (If User.status is INACTIVE, then no need to try to fetch a ride)
    3. Ride may be not null, and in varying states:
        - SEARCHING_FOR_DRIVER
        - PASSENGER_PICK_UP
        - EN_ROUTE
        - ARRIVED
     */
    val uiState = combineTuple(_driverModel, _rideModel/*, _mapIsReady*/).map { (driver, rideResult/*, isMapReady*/) ->
        val isMapReady = true
        if (rideResult is ServiceResult.Failure) {
            Log.e(TAG, "error caught in ride model state flow", rideResult.exception)
            return@map HomeUiState.Error
        }
        val ride = (rideResult as ServiceResult.Value).value

        if (driver == null || !isMapReady) HomeUiState.Loading
        else {
            when {
                ride == null -> {
//                    getPassengerList()
                    HomeUiState.SearchingForPassengers
                }

                ride.status == RideStatus.PASSENGER_PICK_UP.value
                        && ride.driverLatitude != null
                        && ride.driverLongitude != null -> HomeUiState.PassengerPickUp(
                    passengerLat = ride.passengerLatitude,
                    passengerLon = ride.passengerLongitude,
                    driverLat = ride.driverLatitude!!,
                    driverLon = ride.driverLongitude!!,
                    destinationLat = ride.destinationLatitude,
                    destinationLon = ride.destinationLongitude,
                    destinationAddress = ride.destinationAddress,
                    passengerName = ride.passengerName,
                    passengerAvatar = ride.passengerAvatarUrl
                )

                ride.status == RideStatus.EN_ROUTE.value
                        && ride.driverLatitude != null
                        && ride.driverLongitude != null -> HomeUiState.EnRoute(
                    driverLat = ride.driverLatitude!!,
                    driverLon = ride.driverLongitude!!,
                    destinationLat = ride.destinationLatitude,
                    destinationLon = ride.destinationLongitude,
                    destinationAddress = ride.destinationAddress,
                    passengerName = ride.passengerName,
                    passengerAvatar = ride.passengerAvatarUrl
                )

                ride.status == RideStatus.ARRIVED.value
                        && ride.driverLatitude != null
                        && ride.driverLongitude != null -> HomeUiState.Arrived(
                    driverLat = ride.driverLatitude!!,
                    driverLon = ride.driverLongitude!!,
                    destinationLat = ride.destinationLatitude,
                    destinationLon = ride.destinationLongitude,
                    destinationAddress = ride.destinationAddress,
                    passengerName = ride.passengerName,
                    passengerAvatar = ride.passengerAvatarUrl
                )

                (ride.status == RideStatus.PASSENGER_PICK_UP.value
                        || ride.status == RideStatus.EN_ROUTE.value
                        || ride.status == RideStatus.ARRIVED.value)
                        && ride.driverLatitude != null
                        && ride.driverLongitude != null
                        && ride.totalMessages != _currentMessagesCount.value
                -> {
                    _currentMessagesCount.update { ride.totalMessages }
                    HomeUiState.NewMessages(ride.totalMessages)
                }

                ride.status == RideStatus.PENDING_RATING.value -> {
                    Log.d(TAG, "PENDING_RATING state reached")
                    HomeUiState.SearchingForPassengers
                }

                else -> {
                    Log.e(TAG, "unexpected state: driver=${driver}, ride=${ride}")
                    HomeUiState.Error
                }
            }
        }
    }

    //999 represents an impossible value, indicating we don't know the driver's location
    private val DEFAULT_LAT_OR_LON = 999.0
    private val _driverLocation = MutableStateFlow(LatLng(DEFAULT_LAT_OR_LON, DEFAULT_LAT_OR_LON))
    private var _passengerList = rideService.openRides()

    //I don't want a driver to be able to accept a ride unless we know their location first.
    val locationAwarePassengerList = combineTuple(_driverLocation, _passengerList).map {
//        if (it.first.lat == DEFAULT_LAT_OR_LON
//            || it.first.lng == DEFAULT_LAT_OR_LON
//        ) emptyList()
//        else {
            if (it.second is ServiceResult.Failure) {
                Log.e(TAG, "error caught in passenger list state flow", (it.second as ServiceResult.Failure<List<Ride>>).exception)
                handleError()
                emptyList()
            } else {
                val result = it.second as ServiceResult.Value
                result.value.map { ride ->
                    Pair(ride, _driverLocation.value)
                }
            }
//        }
    }

    /*fun mapIsReady() {
        _mapIsReady.value = true
    }*/

    fun getDriver() = launch(Dispatchers.Main) {
        val getUser = getUser.getUser()
        when (getUser) {
            is ServiceResult.Failure -> {
                Log.e(TAG, "failed to get driver", getUser.exception)
                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                sendToLogin()
            }
            is ServiceResult.Value -> {
                if (getUser.value == null) {
                    Log.w(TAG, "getDriver: null driver")
                    sendToLogin()
                }
                else {
                    Log.d(TAG, "getDriver: success")
                    getActiveRideIfItExists(getUser.value!!)
                }
            }
        }
    }

    /**
     * The Passenger model must always be the last model which is mutated from a null state. By
     * setting the other models first, we avoid the UI rapidly switching between different states
     * in a disorganized way.
     */
    private suspend fun getActiveRideIfItExists(user: GrabLamUser) {
        val result = rideService.getRideIfInProgress()

        when (result) {
            is ServiceResult.Failure -> {
                Log.e(TAG, "failed to get active ride", result.exception)
                toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
                sendToLogin()
            }
            is ServiceResult.Value -> {
                //if null, no active ride exists
                if (result.value == null) {
                    Log.d(TAG, "getActiveRideIfItExists: no active ride exists")
                    _driverModel.value = user
                    // old: don't need to call this here because the value null has been emitted in the
                    // ride model flow which causes the uiState to call this in its flow combining operation
                    // update: with the change in StreamRideService::observeOpenRides
                    // when the ride in open rides list is cancelled or taken, it will be automatically deleted from the list
                    // and we don't need to handle that in the flow operation
                    // thus we will remove the call to getPassengerList in the flow operation
                    // and add this back to here.
                    getPassengerList()
                } else {
                    Log.d(TAG, "getActiveRideIfItExists: active ride found")
                    observeRideModel(result.value!!, user)
                }
            }
        }
    }

    private suspend fun observeRideModel(rideId: String, user: GrabLamUser) {
        //The result of this call is handled inside the flowable assigned to _rideModel
        rideService.observeRideById(rideId)
        _driverModel.value = user
    }

    private suspend fun getPassengerList() {
        rideService.observeOpenRides()
    }

    fun handlePassengerItemClick(clickedRide: Ride) = launch(Dispatchers.Main) {
        //We must only proceed if driver LatLng are real values!
//        if (_driverLocation.value.lat != DEFAULT_LAT_OR_LON
//            && _driverLocation.value.lng != DEFAULT_LAT_OR_LON
//        ) {
            val result = rideService.connectDriverToRide(
                clickedRide.copy(
                    driverLatitude = _driverLocation.value.lat,
                    driverLongitude = _driverLocation.value.lng
                ), _driverModel.value!!
            )

            when (result) {
                is ServiceResult.Failure -> {
                    Log.e(TAG, "failed to connect driver to ride", result.exception)
                    toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
                }
                is ServiceResult.Value -> {
                    Log.d(TAG, "connect driver to ride successfully")
                    _passengerList = emptyFlow()
                    rideService.observeRideById(result.value)
                }
            }
//        } else {
//            toastHandler?.invoke(ToastMessages.UNABLE_TO_RETRIEVE_USER_COORDINATES)
//        }
    }

    fun goToProfile() {
        backstack.setHistory(
            History.of(ProfileSettingsKey()),
            StateChange.FORWARD
        )
    }

    /*fun updateDriverLocation(latLng: LatLng) = launch(Dispatchers.Main) {
        _driverLocation.value = latLng

        val currentRide = _rideModel.first()

        if (currentRide is ServiceResult.Value && currentRide.value != null) {
            val result = rideService.updateDriverLocation(
                currentRide.value!!,
                latLng.lat,
                latLng.lng
            )

            if (result is ServiceResult.Failure) {
                toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            }
        }
    }*/

    fun cancelRide() = launch(Dispatchers.Main) {
        when (val cancelRide = rideService.cancelRide()) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                Log.e("HomeViewModel", "cancelRide failure", cancelRide.exception)
                rideService.clearRideModel()
                sendToSplash()
            }

            //State should automatically be handled by the flow
            is ServiceResult.Value -> {
                getPassengerList()
                Log.d(TAG, "cancelRide successfully")
            }
        }
    }

    private fun sendToLogin() {
        backstack.setHistory(
            History.of(LoginKey()),
            StateChange.BACKWARD
        )
    }

    private fun sendToSplash() {
        backstack.setHistory(
            History.of(SplashKey()),
            StateChange.REPLACE
        )
    }


    override fun onServiceActive() {
        getDriver()

        // Simply remove the ride model when the status is pending rating
        launch(Dispatchers.Main) {
            _rideModel
                .distinctUntilChanged()
                .collect { rideModel ->
                    if (rideModel is ServiceResult.Value) {
                        val ride = rideModel.value
                        if (ride != null && ride.status == RideStatus.PENDING_RATING.value) {
                            rideService.clearRideModel()
                        }
                    }
                }
        }
    }

    override fun onServiceInactive() {
        canceller.cancel()
    }

    fun handleError() {
        sendToLogin()
    }

    fun completeRide() = launch(Dispatchers.Main) {
        val ride = _rideModel.first()

        if (ride is ServiceResult.Value && ride.value != null) {
            when (val completeRide = rideService.completeRide(ride.value!!)) {
                is ServiceResult.Failure -> {
                    toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                    Log.e("HomeViewModel", "completeRide: failure", completeRide.exception)
                    sendToSplash()
                }
                is ServiceResult.Value -> {
                    Log.d("HomeViewModel", "completeRide: success")
                    sendToSplash()
                }
            }
        } else {
            Log.e("HomeViewModel", "completeRide: unable to get current ride")
        }
    }

    fun advanceRide() = launch {
        val oldRideState = _rideModel.first()

        if (oldRideState is ServiceResult.Value && oldRideState.value != null) {
            val updateRide = rideService.advanceRide(
                oldRideState.value!!.rideId,
                advanceRideState(oldRideState.value!!.status)
            )

            when (updateRide) {
                is ServiceResult.Failure -> {
                    Log.e(TAG, "failed to advance ride", updateRide.exception)
                    toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
                }
                is ServiceResult.Value -> {
                    Log.d(TAG, "advanceRide, newState=${(_rideModel.first() as ServiceResult.Value).value?.status ?: "null ride"}")
                    Log.d(TAG, "advance ride successfully")
                }
            }
        }

    }

    private fun advanceRideState(status: String): String {
        return when (status) {
            RideStatus.SEARCHING_FOR_DRIVER.value -> RideStatus.PASSENGER_PICK_UP.value
            RideStatus.PASSENGER_PICK_UP.value -> RideStatus.EN_ROUTE.value
            else -> RideStatus.ARRIVED.value
        }
    }

    fun openChat() = launch(Dispatchers.Main) {
        val currentRide = _rideModel.first()

        if (currentRide is ServiceResult.Value && currentRide.value != null) {
            backstack.setHistory(
                History.of(ChatKey(currentRide.value!!.rideId)),
                StateChange.FORWARD
            )
        } else {
            Log.e(
                TAG, "failed to get current chat channel",
                if (currentRide is ServiceResult.Failure) currentRide.exception
                else Exception("null channel")
            )
        }
    }

    fun queryRidesAgain() = launch(Dispatchers.Main) {
        getPassengerList()
    }

//    fun saveRide() = launch(Dispatchers.IO) {
//        val ride = _rideModel.first()
//        if (ride is ServiceResult.Value && ride.value != null) {
//            rideService.saveRide(ride.value!!)
//        }
//    }

    companion object {
        val TAG = HomeViewModel::class.simpleName
    }
}


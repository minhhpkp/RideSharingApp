package com.ridesharingapp.passengersideapp.dashboard

import android.util.Log
import com.ridesharingapp.common.ServiceResult
import com.ridesharingapp.common.domain.GrabLamUser
import com.ridesharingapp.common.domain.Ride
import com.ridesharingapp.common.domain.RideStatus
import com.ridesharingapp.common.services.RideService
import com.ridesharingapp.common.uicommon.ToastMessages
import com.ridesharingapp.common.uicommon.combineTuple
import com.ridesharingapp.common.usecases.GetUser
import com.ridesharingapp.passengersideapp.navigation.ChatKey
import com.ridesharingapp.passengersideapp.navigation.LoginKey
import com.ridesharingapp.passengersideapp.navigation.ProfileSettingsKey
import com.ridesharingapp.passengersideapp.navigation.SplashKey
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.StateChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class PassengerDashboardViewModel(
    val backstack: Backstack,
    val getUser: GetUser,
    val rideService: RideService
//    , val googleService: com.ridesharingapp.common.google.GoogleService
) : ScopedServices.Activated, CoroutineScope {

    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main

    internal var toastHandler: ((ToastMessages) -> Unit)? = null

    private val _passengerModel = MutableStateFlow<GrabLamUser?>(null)
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
    val uiState = combineTuple(
        _passengerModel,
        _rideModel,
//        _mapIsReady
    ).map { (passenger, rideResult
//                                      , isMapReady
    ) ->
        val isMapReady = true
        if (rideResult is ServiceResult.Failure) {
            Log.e("PassengerDashboardViewModel", "error caught in ride model state flow", rideResult.exception)
            return@map PassengerDashboardUiState.Error
        }

        val ride = (rideResult as ServiceResult.Value).value

        //only publish state updates whe map is ready!
        if (passenger == null || !isMapReady) PassengerDashboardUiState.Loading
        else {
            when {
                ride == null -> PassengerDashboardUiState.RideInactive

                ride.driverId == null -> {
                    Log.d(TAG, "Current State: Searching for driver")
                    PassengerDashboardUiState.SearchingForDriver(
                        ride.passengerLatitude,
                        ride.passengerLongitude,
                        ride.destinationAddress
                    )
                }

                ride.status == RideStatus.PASSENGER_PICK_UP.value
                        && ride.driverLatitude != null
                        && ride.driverLongitude != null -> {
                    Log.d(TAG, "Current State: Driver has picked up the ride")
                    PassengerDashboardUiState.PassengerPickUp(
                        passengerLat = ride.passengerLatitude,
                        passengerLon = ride.passengerLongitude,
                        driverLat = ride.driverLatitude!!,
                        driverLon = ride.driverLongitude!!,
                        destinationLat = ride.destinationLatitude,
                        destinationLon = ride.destinationLongitude,
                        destinationAddress = ride.destinationAddress,
                        driverName = ride.driverName ?: "Error",
                        driverAvatar = ride.driverAvatarUrl ?: "",
                    )
                }
                ride.status == RideStatus.EN_ROUTE.value
                        && ride.driverLatitude != null
                        && ride.driverLongitude != null -> {
                    Log.d(TAG, "Current state: Driver is en route")
                    PassengerDashboardUiState.EnRoute(
                        passengerLat = ride.passengerLatitude,
                        passengerLon = ride.passengerLongitude,
                        driverName = ride.driverName ?: "Error",
                        destinationAddress = ride.destinationAddress,
                        destinationLat = ride.destinationLatitude,
                        destinationLon = ride.destinationLongitude,
                        driverAvatar = ride.driverAvatarUrl ?: "",
                        driverLat = ride.driverLatitude!!,
                        driverLon = ride.driverLongitude!!
                    )
                }

                ride.status == RideStatus.ARRIVED.value
                        && ride.driverLatitude != null
                        && ride.driverLongitude != null -> {
                    Log.d(TAG, "Current state: Driver has arrived")
                    PassengerDashboardUiState.Arrived(
                        passengerLat = ride.passengerLatitude,
                        passengerLon = ride.passengerLongitude,
                        driverName = ride.driverName ?: "Error",
                        destinationLat = ride.destinationLatitude,
                        destinationLon = ride.destinationLongitude,
                        destinationAddress = ride.destinationAddress,
                        driverAvatar = ride.driverAvatarUrl ?: "",
                    )
                }

                (ride.status == RideStatus.PASSENGER_PICK_UP.value
                        || ride.status == RideStatus.EN_ROUTE.value
                        || ride.status == RideStatus.ARRIVED.value)
                        && ride.driverLatitude != null
                        && ride.driverLongitude != null
                        && ride.totalMessages != _currentMessagesCount.value
                -> {
                    // this variable is used so that we only emit each new message event only once
                    _currentMessagesCount.update { ride.totalMessages }
                    PassengerDashboardUiState.NewMessages(ride.totalMessages)
                }

                else -> {
                    Log.e("PassengerDashboardViewModel", "Unexpected state: passenger=$passenger, ride=$ride")
                    PassengerDashboardUiState.Error
                }
            }
        }
    }

//    private val _autoCompleteList = MutableStateFlow<List<AutoCompleteModel>>(emptyList())
//    val autoCompleteList: StateFlow<List<AutoCompleteModel>> get() = _autoCompleteList

//    private var passengerLatLng = LatLng()

//    fun mapIsReady() {
//        _mapIsReady.value = true
//    }

    private fun getPassenger() = launch(Dispatchers.Main) {
        val getUser = getUser.getUser()
        when (getUser) {
            is ServiceResult.Failure -> {
                Log.e("PassengerDashboardViewModel", "getPassenger: failed", getUser.exception)
                toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
                sendToLogin()
            }
            is ServiceResult.Value -> {
                if (getUser.value == null) {
                    Log.d("PassengerDashboardViewModel", "getPassenger: null user")
                    sendToLogin()
                }
                else {
                    Log.d("PassengerDashboardViewModel", "getPassenger: success")
                    getActiveRideIfItExists(getUser.value!!)
                }
            }
        }
    }

    private suspend fun getActiveRideIfItExists(user: GrabLamUser) {
        val result = rideService.getRideIfInProgress()

        when (result) {
            is ServiceResult.Failure -> {
                Log.e("PassengerDashboardViewModel", "getActiveRideIfItExists: failed", result.exception)
                toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
                sendToLogin()
            }
            is ServiceResult.Value -> {
                //if null, no active ride exists
                if (result.value == null) {
                    Log.d("PassengerDashboardViewModel", "getActiveRideIfItExists: no current ride in progress")
                    _passengerModel.value = user
                }
                else {
                    Log.d("PassengerDashboardViewModel", "getActiveRideIfItExists: success")
                    observeRideModel(result.value!!, user)
                }
            }
        }
    }


    /**
     * The Passenger model must always be the last model which is mutated from a null state. By
     * setting the other models first, we avoid the UI rapidly switching between different states
     * in a disorganized way.
     */
    private suspend fun observeRideModel(rideId: String, user: GrabLamUser) {
        //The result of this call is handled inside the flowable assigned to _rideModel
        Log.d("PassengerDashboardViewModel", "started observe ride $rideId")
        rideService.observeRideById(rideId)
        _passengerModel.value = user
    }

    fun handleSearchItemClick(selectedPlace: AutoCompleteModel) = launch(Dispatchers.Main) {
//        val getCoordinates = googleService.getPlaceCoordinates(selectedPlace.prediction.placeId)

//        when (getCoordinates) {
//            is ServiceResult.Failure -> {
//                Log.e("PassengerDashboardViewModel", "handleSearchItemClick:getCoordinates failed", getCoordinates.exception)
//                toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
//            }
//            is ServiceResult.Value -> {
//                if (getCoordinates.value != null &&
//                    getCoordinates.value!!.place.latLng != null
//                ) {
                    attemptToCreateNewRide(
//                        getCoordinates.value!!,
                        21.032984273101047, 105.78499946607624,
                        selectedPlace.address)
//                } else toastHandler?.invoke(ToastMessages.UNABLE_TO_RETRIEVE_COORDINATES)
//            }
//        }
    }

    private suspend fun attemptToCreateNewRide(
//        response: FetchPlaceResponse,
        lat: Double,
        lng: Double,
        address: String) {
        val result = rideService.createRide(
//            destLat = response.place.latLng!!.latitude,
//            destLon = response.place.latLng!!.longitude,
            destLat = lat,
            destLon = lng,
            destinationAddress = address,
            passengerId = _passengerModel.value!!.userId,
            passengerAvatarUrl = _passengerModel.value!!.avatarPhotoUrl,
            passengerName = _passengerModel.value!!.username,
//            passengerLat = passengerLatLng.lat,
//            passengerLon = passengerLatLng.lng
            passengerLat = lat,
            passengerLon = lng
        )

        when (result) {
            is ServiceResult.Failure -> {
                Log.w("PassengerDashboardViewModel", "attemptToCreateNewRide failed", result.exception)
                toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            }
            is ServiceResult.Value -> {
//                _autoCompleteList.value = emptyList()
                Log.d(TAG, "attemptToCreateNewRide: success")
                observeRideModel(result.value, _passengerModel.value!!)
            }
        }
    }

/*    fun requestAutocompleteResults(query: String) = launch(Dispatchers.Main) {
        val autocompleteRequest = googleService.getAutocompleteResults(query)
        when (autocompleteRequest) {
            is ServiceResult.Failure -> {
                Log.e("PassengerDashboardViewModel", "requestAutocompleteResults failed", autocompleteRequest.exception)
                toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            }
            is ServiceResult.Value -> {
                _autoCompleteList.value = autocompleteRequest.value.map { prediction ->
                    AutoCompleteModel(
                        address = prediction.getFullText(null).toString(),
                        prediction = prediction
                    )
                }
            }
        }
    }*/

    fun cancelRide() = launch(Dispatchers.Main) {
        val cancelRide = rideService.cancelRide()
        when (cancelRide) {
            is ServiceResult.Failure -> {
                Log.e("PassengerDashboardViewModel", "cancelRide failed", cancelRide.exception)
                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                sendToSplash()
            }

            //State should automatically be handled by the flow
            is ServiceResult.Value -> {
                Log.d(TAG, "cancelRide: success")
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
        getPassenger()
    }

    override fun onServiceInactive() {
        canceller.cancel()
    }

    fun handleError() {
        sendToLogin()
    }

   /* fun updatePassengerLocation(latLng: LatLng) = launch(Dispatchers.Main) {
        passengerLatLng = latLng

        val currentRide = _rideModel.first()

        if (currentRide is ServiceResult.Value && currentRide.value != null) {
            val result = rideService.updatePassengerLocation(
                currentRide.value!!,
                latLng.lat,
                latLng.lng
            )

            if (result is ServiceResult.Failure) {
                Log.e("PassengerDashboardViewModel", "updatePassengerLocation", result.exception)
                toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            }
        } else {
            Log.e("PassengerDashboardViewModel", "updatePassengerLocation:currentRide is null")
        }
    }*/

    fun openChat() = launch(Dispatchers.Main) {
        val currentRide = _rideModel.first()

        if (currentRide is ServiceResult.Value && currentRide.value != null) {
            Log.d(TAG, "get current chat channel successfully")
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

    fun goToProfile() {
        //normally we would use backStack.goTo(...), but we always want to reload the state
        //of the dashboard
        backstack.setHistory(
            History.of(ProfileSettingsKey()),
            StateChange.FORWARD
        )
    }

    companion object {
        val TAG = PassengerDashboardViewModel::class.simpleName
    }
}
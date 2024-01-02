package com.ridesharingapp.common.services

import android.util.Log
import com.ridesharingapp.common.ServiceResult
import com.ridesharingapp.common.domain.GrabLamUser
import com.ridesharingapp.common.domain.Ride
import com.ridesharingapp.common.domain.RideStatus
import com.ridesharingapp.common.keys.FILTER_CREATED_AT
import com.ridesharingapp.common.keys.FILTER_UPDATED_AT
import com.ridesharingapp.common.keys.KEY_DEST_ADDRESS
import com.ridesharingapp.common.keys.KEY_DEST_LAT
import com.ridesharingapp.common.keys.KEY_DEST_LON
import com.ridesharingapp.common.keys.KEY_DRIVER_AVATAR_URL
import com.ridesharingapp.common.keys.KEY_DRIVER_ID
import com.ridesharingapp.common.keys.KEY_DRIVER_LAT
import com.ridesharingapp.common.keys.KEY_DRIVER_LON
import com.ridesharingapp.common.keys.KEY_DRIVER_NAME
import com.ridesharingapp.common.keys.KEY_PASSENGER_AVATAR_URL
import com.ridesharingapp.common.keys.KEY_PASSENGER_ID
import com.ridesharingapp.common.keys.KEY_PASSENGER_LAT
import com.ridesharingapp.common.keys.KEY_PASSENGER_LON
import com.ridesharingapp.common.keys.KEY_PASSENGER_NAME
import com.ridesharingapp.common.keys.KEY_STATUS
import com.ridesharingapp.common.keys.STREAM_CHANNEL_TYPE_LIVESTREAM
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.client.api.models.querysort.QuerySortByField
import io.getstream.chat.android.client.channel.ChannelClient
import io.getstream.chat.android.client.events.ChannelDeletedEvent
import io.getstream.chat.android.client.events.ChannelUpdatedByUserEvent
import io.getstream.chat.android.client.events.ChannelUpdatedEvent
import io.getstream.chat.android.client.events.ChatEvent
import io.getstream.chat.android.client.events.NewMessageEvent
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.utils.onErrorSuspend
import io.getstream.chat.android.client.utils.onSuccessSuspend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class StreamRideService(
    private val client: ChatClient
) : RideService {


    private val _rideModelUpdates: MutableStateFlow<ServiceResult<Ride?>> =
        MutableStateFlow(ServiceResult.Value(null))

    private val _openRides: MutableStateFlow<ServiceResult<List<Ride>>> =
        MutableStateFlow(ServiceResult.Value(emptyList()))

    override fun openRides(): Flow<ServiceResult<List<Ride>>> = _openRides
    override fun rideFlow(): Flow<ServiceResult<Ride?>> = _rideModelUpdates

    private var _lastMessageAt: Date? = null

    override suspend fun observeRideById(rideId: String) {
        withContext(Dispatchers.IO) {
            Log.d("StreamRideService", "started observe ride model $rideId")
            val channelClient = client.channel(
                cid = rideId
            )

            val currentUser = client.getCurrentUser()
            val currentID = if (currentUser == null) {
                Log.e("StreamRideService", "observeRideById: currentUser is null")
                ""
            } else {
                Log.d("StreamRideService", "observeRideById: currentUser is ${currentUser.name}")
                currentUser.id
            }
            val result = channelClient.addMembers(listOf(currentID)).await()

            if (result.isSuccess) {
                observeChannelEvents(channelClient)
                Log.d("StreamRideService", "observeRideById: add self successfully")

                result.onSuccessSuspend { channel ->
                    Log.d("StreamRideService", "observeRideById: emitted ride to rideModelUpdates flow")
                    _rideModelUpdates.emit(
                        ServiceResult.Value(
                            streamChannelToRide(channel)
                        )
                    )
                }

                result.onErrorSuspend {
                    _rideModelUpdates.emit(
                        result.error().let {
                            Log.w("StreamRideService", "observeRideById: observeChannelEvents failed", it.cause)
                            ServiceResult.Failure(Exception(it.cause))
                        }
                    )
                }

            } else {
                _rideModelUpdates.emit(
                    result.error().let {
                        Log.w("StreamRideService", "observeRideById: failed to add self", it.cause)
                        ServiceResult.Failure(Exception(it.cause))
                    }
                )
            }
        }
    }

    private suspend fun observeChannelEvents(channelClient: ChannelClient) = withContext(Dispatchers.IO) {
        channelClient.subscribe { event: ChatEvent ->
            when (event) {
                is ChannelDeletedEvent -> {
                    Log.d("StreamRideService", "observeChannelEvents: channel ${event.channelId} deleted event caught")
                    _rideModelUpdates.value = ServiceResult.Value(null)
                }

                is ChannelUpdatedEvent -> {
                    _rideModelUpdates.value =
                        ServiceResult.Value(streamChannelToRide(event.channel))
                    Log.d("StreamRideService", "observeChannelEvents: channel ${event.channelId} updated event caught")
                }

                is ChannelUpdatedByUserEvent -> {
                    _rideModelUpdates.value =
                        ServiceResult.Value(streamChannelToRide(event.channel))
                    Log.d("StreamRideService", "observeChannelEvents: channel ${event.channelId} updated by user event caught")
                }

                is NewMessageEvent -> {
                    Log.d("StreamRideService", "observeChannelEvents: channel ${event.channelId} new message event caught")
                    val currentRideModel = _rideModelUpdates.value

                    if (currentRideModel is ServiceResult.Value && currentRideModel.value != null) {
                        val newChannelClient = client.channel(
                            cid = event.cid
                        )

                        newChannelClient.create(emptyList(), mutableMapOf()).enqueue { result ->
                            if (result.isSuccess) {
                                val lastMessageAt = result.data().lastMessageAt
                                val hasMessage = if (lastMessageAt == _lastMessageAt) 0
                                else {
                                    _lastMessageAt = lastMessageAt
                                    1
                                }
                                _rideModelUpdates.value = ServiceResult.Value(
                                    currentRideModel.value.let {
                                        it.copy(
                                            totalMessages = it.totalMessages + hasMessage
                                        )
                                    }
                                )
                            } else {
                                Log.e("observeChannelEvents", "NewMessageEvent: failed to create new message on channel ${event.channelId}", result.error().cause)
                            }
                        }
                    }
                }

                else -> {
                    Log.d("StreamRideService", "observeChannelEvents: Unexpected event on channel ${channelClient.channelId}" + event.type)
                }
            }
        }
    }


    private fun streamChannelToRide(channel: Channel): Ride {
        val extraData = channel.extraData
        val destAddress: String? = extraData[KEY_DEST_ADDRESS] as String?
        val destLat: Double? = extraData[KEY_DEST_LAT] as Double?
        val destLon: Double? = extraData[KEY_DEST_LON] as Double?

        val driverId: String? = extraData[KEY_DRIVER_ID] as String?
        val driverLat: Double? = extraData[KEY_DRIVER_LAT] as Double?
        val driverLon: Double? = extraData[KEY_DRIVER_LON] as Double?
        val driverAvatar: String? = extraData[KEY_DRIVER_AVATAR_URL] as String?
        val driverName: String? = extraData[KEY_DRIVER_NAME] as String?

        val passengerId: String? = extraData[KEY_PASSENGER_ID] as String?
        val passengerLat: Double? = extraData[KEY_PASSENGER_LAT] as Double?
        val passengerLon: Double? = extraData[KEY_PASSENGER_LON] as Double?
        val passengerAvatar: String? =
            extraData[KEY_PASSENGER_AVATAR_URL] as String?
        val passengerName: String? = extraData[KEY_PASSENGER_NAME] as String?
        val status: String? = extraData[KEY_STATUS] as String?

        return Ride(
            rideId = channel.cid,
            status = status
                ?: RideStatus.SEARCHING_FOR_DRIVER.value,
            destinationLatitude = destLat ?: 999.0,
            destinationLongitude = destLon ?: 999.0,
            destinationAddress = destAddress ?: "",
            driverId = driverId,
            driverLatitude = driverLat,
            driverLongitude = driverLon,
            driverName = driverName,
            driverAvatarUrl = driverAvatar,
            passengerId = passengerId ?: "",
            passengerLatitude = passengerLat ?: 999.0,
            passengerLongitude = passengerLon ?: 999.0,
            passengerName = passengerName ?: "",
            passengerAvatarUrl = passengerAvatar ?: "",
            totalMessages = if (channel.lastMessageAt == null) 0 else 1
        )
    }

    override suspend fun observeOpenRides() {
        withContext(Dispatchers.IO) {
            Log.d("StreamRideService", "started observing open rides")
            val request = QueryChannelsRequest(
                filter = Filters.and(
                    Filters.eq(KEY_STATUS, RideStatus.SEARCHING_FOR_DRIVER.value)
                ),
                querySort = QuerySortByField.descByName(FILTER_CREATED_AT),
                limit = 10
            )

            val result = client.queryChannels(request).await()

            if (result.isSuccess) {
                Log.d("StreamRideService", "observeOpenRides: request open channels successfully")
                Log.d("StreamRideService", "open rides list: ${
                    result.data().map {channel -> channel.cid}
                }")
                _openRides.emit(
                    ServiceResult.Value(
                        result.data().map { channel ->
                            streamChannelToRide(channel)
                        }
                    )
                )
                result.data().forEach { channel ->
                    // delete the ride from the passenger list
                    // if it is cancelled or another driver has taken it
                    client.channel(channel.cid).let { channelClient ->
                        channelClient.subscribe { event ->
                            if (event is ChannelDeletedEvent) {
                                Log.d(
                                    "StreamRideService",
                                    "open ride channel ${channel.cid} being observed is deleted"
                                )
                                deleteFromOpenRidesList(channel.cid)
                            } else if (event is ChannelUpdatedEvent
                                && event.channel.extraData[KEY_STATUS] != null
                                && event.channel.extraData[KEY_STATUS] != RideStatus.SEARCHING_FOR_DRIVER
                            ) {
                                Log.d(
                                    "StreamRideService",
                                    "open ride channel ${channel.cid} has been taken"
                                )
                                deleteFromOpenRidesList(channel.cid)
                            } else if (
                                event is ChannelUpdatedByUserEvent
                                && event.channel.extraData[KEY_STATUS] != null
                                && event.channel.extraData[KEY_STATUS] != RideStatus.SEARCHING_FOR_DRIVER
                            ) {
                                Log.d(
                                    "StreamRideService",
                                    "open ride channel ${channel.cid} has been taken"
                                )
                                deleteFromOpenRidesList(channel.cid)
                            }
                        }
                    }
                }
            } else {
                Log.e("StreamRideService", "observeOpenRides: failed to request open channels", result.error().cause)
                _openRides.emit(ServiceResult.Failure(Exception(result.error().cause)))
            }
        }
    }

    private fun deleteFromOpenRidesList(cid: String) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                Log.d("StreamRideService", "channel $cid will be delete from the open rides list")
                if (_openRides.value is ServiceResult.Value) {
                    val currentList = (_openRides.value as ServiceResult.Value<List<Ride>>).value.toMutableList()
                    currentList.removeIf { it.rideId == cid }
                    Log.d("StreamRideService", "updated list of open rides: ${currentList.map{ride -> ride.rideId}}")
                    _openRides.emit(ServiceResult.Value(currentList))
                }
            }
        }
    }

    override suspend fun connectDriverToRide(
        ride: Ride,
        driver: GrabLamUser
    ): ServiceResult<String> =
        withContext(Dispatchers.IO) {
            val channelClient = client.channel(
                cid = ride.rideId
            )

            val addToChannel =
                channelClient.addMembers(listOf(client.getCurrentUser()?.id ?: "")).await()
            if (addToChannel.isSuccess) {
                Log.d("StreamRideService", "connectDriverToRide: add driver to passenger's channel ${ride.rideId} successfully")
                //note: we must check in the VM if driverLatLng are null!!
                val updateDetails = channelClient.updatePartial(
                    set = mutableMapOf(
                        KEY_STATUS to RideStatus.PASSENGER_PICK_UP.value,
                        KEY_DRIVER_ID to driver.userId,
                        KEY_DRIVER_NAME to driver.username,
                        KEY_DRIVER_LAT to ride.driverLatitude!!,
                        KEY_DRIVER_LON to ride.driverLongitude!!,
                        KEY_DRIVER_AVATAR_URL to driver.avatarPhotoUrl
                    )
                ).await()

                if (updateDetails.isSuccess) {
                    Log.d("StreamRideService", "connectDriverToRide: update channel ${ride.rideId} detail successfully")
                    ServiceResult.Value(channelClient.cid)
                } else {
                    Log.e("StreamRideService", "connectDriverToRide: failed to update channel ${ride.rideId} detail", updateDetails.error().cause)
                    ServiceResult.Failure(Exception(updateDetails.error().cause))
                }
            } else {
                Log.e("StreamRideService", "connectDriverToRide: failed to add driver ${ride.rideId} to channel", addToChannel.error().cause)
                ServiceResult.Failure(Exception(addToChannel.error().cause))
            }
        }

    override suspend fun getRideIfInProgress(): ServiceResult<String?> =
        withContext(Dispatchers.IO) {

            val currentUserId = client.getCurrentUser()?.id ?: ""
            val request = QueryChannelsRequest(
                filter = Filters.and(
                    Filters.`in`("members", currentUserId)
                ),
                querySort = QuerySortByField.descByName(FILTER_UPDATED_AT),
                limit = 1
            )

            val result = client.queryChannels(request).await()

            if (result.isSuccess) {
                if (result.data().isEmpty()) {
                    Log.d("StreamRideService", "get ride in progress successfully, result: no current ride in progress")
                    ServiceResult.Value(null)
                }
                else {
                    Log.d("StreamRideService", "get ride in progress successfully, rideId=${result.data().first().cid}")
                    result.data().first().let { channel ->
                        ServiceResult.Value(channel.cid)
                    }
                }
            } else {
                Log.e("StreamRideService", "failed to get ride in progress", result.error().cause)
                ServiceResult.Failure(Exception(result.error().cause))
            }
        }


    override suspend fun createRide(
        passengerId: String,
        passengerName: String,
        passengerLat: Double,
        passengerLon: Double,
        passengerAvatarUrl: String,
        destinationAddress: String,
        destLat: Double,
        destLon: Double
    ): ServiceResult<String> = withContext(Dispatchers.IO) {

        val channelId = generateUniqueId(6, ('A'..'Z') + ('0'..'9'))
        val result = client.createChannel(
            channelType = STREAM_CHANNEL_TYPE_LIVESTREAM,
            channelId = channelId,
            memberIds = listOf(passengerId),
            extraData = mutableMapOf(
                KEY_STATUS to RideStatus.SEARCHING_FOR_DRIVER,
                KEY_PASSENGER_ID to passengerId,
                KEY_PASSENGER_NAME to passengerName,
                KEY_PASSENGER_LAT to passengerLat,
                KEY_PASSENGER_LON to passengerLon,
                KEY_PASSENGER_AVATAR_URL to passengerAvatarUrl,
                KEY_DEST_ADDRESS to destinationAddress,
                KEY_DEST_LAT to destLat,
                KEY_DEST_LON to destLon
            )
        ).await()

        if (result.isSuccess) {
            Log.d("StreamRideService", "create ride successfully rideId=${result.data().cid}")
            ServiceResult.Value(result.data().cid)
        } else {
            Log.w("StreamRideService", "failed to create ride", result.error().cause)
            ServiceResult.Failure(Exception(result.error().cause))
        }
    }

    //Function below taken from https://github.com/GetStream/stream-draw-android
    private fun generateUniqueId(length: Int, allowedChars: List<Char>): String {
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    override suspend fun cancelRide(): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        //get ride in progress
        val currentUserId = client.getCurrentUser()?.id ?: ""
        val request = QueryChannelsRequest(
            filter = Filters.and(
                Filters.`in`("members", currentUserId)
            ),
            querySort = QuerySortByField.descByName(FILTER_UPDATED_AT),
            limit = 1
        )

        val result = client.queryChannels(request).await()

        if (result.isSuccess) {
            if (result.data().isEmpty()
            ) {
                Log.e("StreamRideService", "cancelRide: no current ride in progress")
                ServiceResult.Failure(Exception("Failed to retrieve channel for cancellation"))
            }
            else {
                Log.d("StreamRideService", "cancelRide: get ride in progress successfully, rideId=${result.data().first().cid}")
                val channelClient = client.channel(result.data().first().cid)

                if (channelClient.hide().await().isSuccess) {
                    Log.d("StreamRideService", "cancelRide: hide the channel ${channelClient.channelId} successfully")
                    val deleteResult = channelClient.delete().await()
//                    observeOpenRides()
                    if (deleteResult.isSuccess) {
                        Log.d("StreamRideService", "cancelRide: delete the channel ${channelClient.channelId} successfully")
                        _rideModelUpdates.emit(ServiceResult.Value(null))
                        ServiceResult.Value(Unit)
                    } else {
                        Log.e("StreamRideService", "cancelRide: failed to delete the channel", deleteResult.error().cause)
                        ServiceResult.Failure(Exception(deleteResult.error().cause))
                    }
                } else {
                    Log.e("StreamRideService", "cancelRide: failed to hide the channel")
                    ServiceResult.Failure(Exception("Unable to hide channel"))
                }
            }
        } else {
            Log.e("StreamRideService", "cancelRide: failed to get ride in progress", result.error().cause)
            ServiceResult.Failure(Exception(result.error().cause))
        }
    }

    override suspend fun completeRide(ride: Ride): ServiceResult<Unit> {
        val channelClient = client.channel(ride.rideId)
        channelClient.delete().await()
//        observeOpenRides()
        return ServiceResult.Value(Unit)
    }

    override suspend fun advanceRide(rideId: String, newState: String): ServiceResult<Unit> =
        withContext(Dispatchers.IO) {
            val advanceRide = client.updateChannelPartial(
                channelType = STREAM_CHANNEL_TYPE_LIVESTREAM,
                channelId = getChannelIdOnly(rideId),
                set = mutableMapOf(
                    KEY_STATUS to newState
                )
            ).await()

            if (advanceRide.isSuccess) {
                Log.d("StreamRideService", "advanceRide: success")
                ServiceResult.Value(Unit)
            } else {
                Log.e("StreamRideService", "advanceRide: failure", advanceRide.error().cause)
                ServiceResult.Failure(
                    Exception(advanceRide.error().cause)
                )
            }
        }

    override suspend fun updateDriverLocation(
        ride: Ride,
        lat: Double,
        lon: Double
    ): ServiceResult<Unit> =
        withContext(Dispatchers.IO) {
            val advanceRide = client.updateChannelPartial(
                channelType = STREAM_CHANNEL_TYPE_LIVESTREAM,
                channelId = getChannelIdOnly(ride.rideId),
                set = mutableMapOf(
                    KEY_DRIVER_LAT to lat,
                    KEY_DRIVER_LON to lon,
                )
            ).await()

            if (advanceRide.isSuccess) {
                Log.d("StreamRideService", "updateDriverLocation: success")

                //Unfortunately the update call only triggers remote clients for an update event,
                //it doesn't seem to trigger locally. This is a workaround to make sure the map
                //state is updated appropriately.
                val currentRideState = _rideModelUpdates.value
                if (currentRideState is ServiceResult.Value && currentRideState.value != null) {
                    _rideModelUpdates.value = ServiceResult.Value(
                        currentRideState.value.copy(
                            driverLatitude = lat,
                            driverLongitude = lon
                        )
                    )
                }

                ServiceResult.Value(Unit)
            } else {
                Log.e("StreamRideService", "updateDriverLocation: failure", advanceRide.error().cause)

                ServiceResult.Failure(
                    Exception(advanceRide.error().cause)
                )
            }
        }

    override suspend fun updatePassengerLocation(
        ride: Ride,
        lat: Double,
        lon: Double
    ): ServiceResult<Unit> =
        withContext(Dispatchers.IO) {
            val advanceRide = client.updateChannelPartial(
                channelType = STREAM_CHANNEL_TYPE_LIVESTREAM,
                channelId = getChannelIdOnly(ride.rideId),
                set = mutableMapOf(
                    KEY_PASSENGER_LAT to lat,
                    KEY_PASSENGER_LON to lon,
                )
            ).await()

            if (advanceRide.isSuccess) {
                Log.d("StreamRideService", "updatePassengerLocation: success")

                //Workaround, see above function
                val currentRideState = _rideModelUpdates.value
                if (currentRideState is ServiceResult.Value && currentRideState.value != null) {
                    _rideModelUpdates.value = ServiceResult.Value(
                        currentRideState.value.copy(
                            driverLatitude = lat,
                            driverLongitude = lon
                        )
                    )
                }

                ServiceResult.Value(Unit)
            } else {
                Log.e("StreamRideService", "updatePassengerLocation: failure", advanceRide.error().cause)

                ServiceResult.Failure(
                    Exception(advanceRide.error().cause)
                )
            }
        }

    //A cid will be passed in here in the format livestream:<channel id>. This splits it into
    //just the channel id.
    private fun getChannelIdOnly(cid: String): String = cid.split(":").last()

}
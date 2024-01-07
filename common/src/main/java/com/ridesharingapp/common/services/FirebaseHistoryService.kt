package com.ridesharingapp.common.services

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.ridesharingapp.common.domain.HistoryRide
import com.ridesharingapp.common.domain.UserType

class FirebaseHistoryService: HistoryService {
    private lateinit var _earnedPointsListener: ListenerRegistration
    private lateinit var _ratingListener: ListenerRegistration
    private lateinit var _historyListener: ListenerRegistration

    override fun startListeningForEarnedPointsChanges(
        passengerId: String,
        onSuccess: (Long?) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        _earnedPointsListener = Firebase.firestore.collection("Users").document(passengerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Listening for reward points failed", error)
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val rewardPoints = snapshot.get("Reward points") as Long
                    Log.d(TAG, "Passenger document changed, (updated) reward points: $rewardPoints")
                    onSuccess(rewardPoints)
                } else {
                    Log.w(TAG, "Unexpected event: Passenger document is null")
                    onSuccess(null)
                }
            }
    }

    override fun startListeningForRatingChanges(
        driverId: String,
        onSuccess: (Double?) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        _ratingListener = Firebase.firestore.collection("Users").document(driverId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Listening for rating failed", error)
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val rating = snapshot.getDouble("Rating")
                    Log.d(TAG, "Driver document changed, (update) rating: $rating")
                    onSuccess(rating)
                } else {
                    Log.w(TAG, "Unexpected event: Driver document is null")
                    onError(Exception("User document is null"))
                }
            }
    }

    override fun startListeningForHistoryChanges(
        userId: String, type: UserType,
        onSuccess: (List<HistoryRide>?) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val ridesCollectionRef = Firebase.firestore.collection("Rides")
        val query = ridesCollectionRef
            .whereEqualTo(if (type == UserType.PASSENGER) "Passenger" else "Driver", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(10)
        _historyListener = query.addSnapshotListener { value, error ->
            if (error != null) {
                Log.e(TAG, "Listening for history failed", error)
                onError(error)
                return@addSnapshotListener
            }

            val historyRides = ArrayList<HistoryRide>()
            for (doc in value!!) {
                val historyRide = HistoryRide(
                    passengerId = doc.getString("Passenger")!!,
                    driverId = doc.getString("Driver")!!,
                    destinationAddress = doc.getString("Destination address")!!,
                    destinationLatitude = doc.getDouble("Destination latitude")!!,
                    destinationLongitude = doc.getDouble("Destination longitude")!!,
                    pickUpAddress = doc.getString("Destination address")!!,
                    pickUpLatitude = doc.getDouble("Pick up latitude")!!,
                    pickUpLongitude = doc.getDouble("Pick up longitude")!!,
                    fee = doc.getDouble("Fee")!!,
                    earnedPoints = doc.get("Earned points")!! as Long,
                    rating = doc.getDouble("Rating"),
                    createdAt = doc.getTimestamp("createdAt")!!
                )
                historyRides.add(historyRide)
            }
            onSuccess(historyRides)
            Log.d(TAG, "Update history successfully")
        }
    }

    override fun stopListeningForRewardPointsChanges() {
        _earnedPointsListener.remove()
    }

    override fun stopListeningForRatingChanges() {
        _ratingListener.remove()
    }

    override fun stopListeningForHistoryChanges() {
        _historyListener.remove()
    }

    companion object {
        val TAG = FirebaseHistoryService::class.simpleName
    }
}
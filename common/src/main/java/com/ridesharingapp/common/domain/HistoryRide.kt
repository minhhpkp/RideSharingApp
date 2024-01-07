package com.ridesharingapp.common.domain

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import java.util.GregorianCalendar

@Parcelize
data class HistoryRide(
    val passengerId: String,
    val driverId: String,
    val destinationLatitude: Double,
    val destinationLongitude: Double,
    val destinationAddress: String = "",
    val pickUpLatitude: Double,
    val pickUpLongitude: Double,
    val pickUpAddress: String = "",
    val earnedPoints: Long,
    val fee: Double,
    val rating: Double?,
    val createdAt: Timestamp
) : Parcelable {
    companion object {
        fun getSampleRide() = HistoryRide(
            passengerId = "passengerId",
            driverId = "driverId",
            destinationAddress = "destination address",
            destinationLatitude = 10.0,
            destinationLongitude = 10.0,
            pickUpAddress = "pick up address",
            pickUpLongitude = 20.0,
            pickUpLatitude = 20.0,
            earnedPoints = 1,
            fee = 40.25,
            rating = 5.0,
            createdAt = Timestamp(GregorianCalendar(2024, 10, 5).time)
        )
    }
}

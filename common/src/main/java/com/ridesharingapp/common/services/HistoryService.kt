package com.ridesharingapp.common.services

import com.ridesharingapp.common.domain.HistoryRide
import com.ridesharingapp.common.domain.UserType

interface HistoryService {
    fun startListeningForEarnedPointsChanges(
        passengerId: String,
        onSuccess: (Long?) -> Unit,
        onError: (Throwable) -> Unit = {}
    )

    fun startListeningForRatingChanges(
        driverId: String,
        onSuccess: (Double?) -> Unit,
        onError: (Throwable) -> Unit = {}
    )

    fun startListeningForHistoryChanges(
        userId: String, type: UserType,
        onSuccess: (List<HistoryRide>?) -> Unit,
        onError: (Throwable) -> Unit = {}
    )

    fun stopListeningForRewardPointsChanges()

    fun stopListeningForRatingChanges()

    fun stopListeningForHistoryChanges()
}
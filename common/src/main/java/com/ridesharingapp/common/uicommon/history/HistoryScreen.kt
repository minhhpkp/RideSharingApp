package com.ridesharingapp.common.uicommon.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ridesharingapp.common.R
import com.ridesharingapp.common.domain.HistoryRide
import com.ridesharingapp.common.domain.UserType
import com.ridesharingapp.common.style.color_white
import com.ridesharingapp.common.style.typography
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val history by viewModel.history.collectAsStateWithLifecycle()
    HistoryScreen(
        history = history,
        handleBackPress = { viewModel.handleBackPress() },
        userType = viewModel.type
    )
}


@Composable
fun HistoryScreen(
    history: List<HistoryRide>,
    handleBackPress: () -> Unit,
    userType: UserType
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color_white)
            .padding(16.dp)
    ) {
        HistoryToolbar(handleBackPress = handleBackPress)

        if (history.isEmpty()) {
            Text(text = stringResource(R.string.empty_history_message))
        } else {

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                history.forEach {
                    RideCard(ride = it, userType = userType)
                }
            }
        }
    }
}

@Composable
fun RideCard(ride: HistoryRide, userType: UserType) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 8.dp,
    ) {
        val hourFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_car_marker),
                contentDescription = "Car icon"
            )
            Text(
                text = stringResource(
                    R.string.ride_card_title,
                    hourFormat.format(ride.createdAt.toDate()),
                    dateFormat.format(ride.createdAt.toDate())
                ),
                style = typography.h6,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(
                    id = R.string.ride_card_description,
                    ride.destinationAddress,
                    if (userType == UserType.PASSENGER) {
                        "Điểm thưởng nhận được: ${ride.earnedPoints}\n"
                    } else {
                        ""
                    },
                    ride.fee,
                    ride.rating?.let{"$it ⭐"} ?: "chưa được đánh giá."
                ),
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun HistoryToolbar(
    modifier: Modifier = Modifier,
    handleBackPress: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = if (enabled) modifier.clickable { handleBackPress() }
            else modifier,
            imageVector = Icons.Filled.KeyboardArrowLeft,
            contentDescription = stringResource(id = R.string.close_icon)
        )

        Spacer(modifier = modifier.width(4.dp))

        Text(
            text = stringResource(R.string.history),
            style = TextStyle(
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.poppins_medium))
            )
        )
    }
}

@Preview
@Composable
fun HistoryScreenPreview() {
    val sampleRide =  HistoryRide.getSampleRide()
    HistoryScreen(
        history = listOf(
            sampleRide,
            sampleRide,
            sampleRide,
            sampleRide,
            sampleRide,
            sampleRide,
        ),
        handleBackPress = {},
        userType = UserType.PASSENGER
    )
}

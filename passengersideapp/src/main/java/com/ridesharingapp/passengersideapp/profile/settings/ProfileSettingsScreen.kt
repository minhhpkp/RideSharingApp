package com.ridesharingapp.passengersideapp.profile.settings

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ridesharingapp.common.R
import com.ridesharingapp.common.domain.GrabLamUser
import com.ridesharingapp.common.style.color_black
import com.ridesharingapp.common.style.color_primary
import com.ridesharingapp.common.style.color_white
import com.ridesharingapp.common.style.typography
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ProfileSettingsScreen(
    viewModel: ProfileSettingsViewModel,
) {
    val profilePicUpdateInProgress by viewModel.profilePicUpdateInProgress.collectAsStateWithLifecycle()
    val user by viewModel.userModel.collectAsState()
    val rewardPoints by viewModel.earnedPoints.collectAsStateWithLifecycle()

    ProfileSettingsScreen(
        user = user,
        profilePicUpdateInProgress = profilePicUpdateInProgress,
        handleBackPress = { viewModel.handleBackPress() },
        handleLogOut = { viewModel.handleLogOut() },
        handleThumbnailUpdate = { imageUri ->
            viewModel.handleThumbnailUpdate(imageUri)
        },
        rewardPoints = rewardPoints,
        handleSeeHistoryClicked = { viewModel.seeHistory() }
//        handleToggleUserType = { viewModel.handleToggleUserType() }
    )
}

// stateless version of the screen for previewing
@Composable
fun ProfileSettingsScreen(
    user: GrabLamUser?,
    profilePicUpdateInProgress: Boolean,
    handleBackPress: (() -> Unit),
    handleLogOut: (() -> Unit),
    handleThumbnailUpdate: ((Uri?) -> Unit),
    rewardPoints: Long = 0,
    handleSeeHistoryClicked: () -> Unit = {}
//    handleToggleUserType: (() -> Unit)
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .background(color = if (profilePicUpdateInProgress) Color.Gray.copy(alpha = 0.5f) else color_white),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            ProfileToolbar(
                handleBackPress = handleBackPress,
                handleLogOut = handleLogOut,
                enabled = !profilePicUpdateInProgress
            )

            /*var driverSwitchState by rememberSaveable {
                mutableStateOf(false)
            }*/

            ProfileHeader(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(bottom = 64.dp),
                user = user,
                handleThumbnailUpdate = handleThumbnailUpdate,
                updateInProgress = profilePicUpdateInProgress
            )

           /* UserTypeState(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .wrapContentHeight()
                    .border(
                        width = 1.dp,
                        color_black.copy(alpha = 0.12f),
                        RoundedCornerShape(4.dp)
                    ),
                user = user,
                enabled = !profilePicUpdateInProgress,
                handleToggleUserType = handleToggleUserType
            )*/

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = stringResource(R.string.reward_points, rewardPoints),
                    style = typography.h5
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor =
                            if (!profilePicUpdateInProgress) color_primary
                            else color_black,
                        contentColor = color_white
                    ),
                    onClick = handleSeeHistoryClicked,
                    enabled = !profilePicUpdateInProgress,

                ) {
                    Text(
                        text = stringResource(R.string.see_history),
                        style = typography.button
                    )
                }
            }
        }
        if (profilePicUpdateInProgress) CircularProgressIndicator()
    }
}

@Composable
fun ProfileToolbar(
    modifier: Modifier = Modifier,
    handleBackPress: () -> Unit,
    handleLogOut: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = if (enabled) Modifier.clickable { handleBackPress() }
            else Modifier,
            imageVector = Icons.Filled.KeyboardArrowLeft,
            contentDescription = stringResource(id = R.string.close_icon)
        )

        TextButton(
            onClick = { handleLogOut() },
            enabled = enabled
        ) {
            Text(
                text = stringResource(id = R.string.log_out),
                style = typography.button.copy(
                    color = color_black,
                    fontWeight = FontWeight.Light
                )
            )
        }
    }
}

@Composable
fun ProfileHeader(
    modifier: Modifier,
    handleThumbnailUpdate: (Uri?) -> Unit,
    user: GrabLamUser?,
    updateInProgress: Boolean = false
) {

    //Note: You would want to do better null checking than this in a prod app
    if (user != null) Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileAvatar(
            modifier = Modifier,
            handleThumbnailUpdate = handleThumbnailUpdate,
            user = user,
            updateInProgress = updateInProgress
        )
        Text(
            modifier = Modifier
                .padding(start = 16.dp),
            text = if (updateInProgress) stringResource(id = R.string.loading) else user.username,
            style = typography.h2
        )
    }
}

@Composable
fun ProfileAvatar(
    modifier: Modifier,
    handleThumbnailUpdate: (Uri?) -> Unit,
    user: GrabLamUser,
    updateInProgress: Boolean = false
) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .padding(start = 16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        if (user.avatarPhotoUrl == "") Image(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .alpha(0.86f),
            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_account_circle_24),
            contentDescription = stringResource(id = R.string.user_avatar),
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(color_primary)
        ) else if (!updateInProgress) {
            GlideImage(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape),
                imageModel = { user.avatarPhotoUrl },
                loading = {
                    Box(modifier = Modifier.matchParentSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            )
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                handleThumbnailUpdate.invoke(it.data?.data)
            }
        )

        Icon(
            modifier = if (updateInProgress) Modifier else Modifier.clickable {
                launcher.launch(
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                )
            },
            imageVector = ImageVector.vectorResource(id = R.drawable.check_circle_24px),
            contentDescription = stringResource(id = R.string.edit_avatar),
            tint = Color.Unspecified
        )
    }
}

/*@Composable
fun UserTypeState(
    modifier: Modifier,
    handleToggleUserType: () -> Unit,
    user: GrabLamUser?,
    enabled: Boolean = true
) {
    if (user != null) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .wrapContentHeight(align = Alignment.Top)
                    .padding(top = 16.dp),
                text = if (user.type != UserType.PASSENGER.value) stringResource(id = R.string.driver)
                else stringResource(id = R.string.passenger),
                style = typography.h3
            )

            Switch(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(bottom = 16.dp)
                    .scale(1.5f),
                checked = user.type != UserType.PASSENGER.value,
                onCheckedChange = { handleToggleUserType() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = color_primary,
                    checkedTrackColor = color_primary
                ),
                enabled = enabled
            )
        }
    }
}*/

@Preview
@Composable
fun ProfileSettingsScreenPreview() {
    ProfileSettingsScreen(
        user = GrabLamUser(username = "Passenger"),
        profilePicUpdateInProgress = false,
        handleBackPress = {},
        handleLogOut = {},
        handleThumbnailUpdate = {},
        rewardPoints = 50,
        handleSeeHistoryClicked = {}
//        handleToggleUserType = {}
    )
}
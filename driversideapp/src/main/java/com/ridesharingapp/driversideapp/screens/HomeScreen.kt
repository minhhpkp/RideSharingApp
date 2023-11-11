package com.ridesharingapp.driversideapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.UiSettings
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.ridesharingapp.common.components.ButtonComponent
import com.ridesharingapp.common.components.HeadingTextComponent
import com.ridesharingapp.driversideapp.data.home.HomeViewModel

@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ) {
        val uiSettings = remember {
            MapUiSettings(myLocationButtonEnabled = true)
        }
        val driverState = LatLng(21.028511, 105.804817)
        val passengerState = LatLng(21.029162, 105.766857)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(driverState, 12f)
        }
        Column(modifier = Modifier.fillMaxSize()) {
            HeadingTextComponent(value = "Home")
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = homeViewModel.homeUIState.properties,
                uiSettings = uiSettings,
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = MarkerState(position = driverState),
                    title = "Tài xế đang ở đây",
                    snippet = "Marker in VietNam"
                )
                Marker(
                    state = MarkerState(position = passengerState),
                    title = "Khách đang ở đây",
                    snippet = "Marker in VietNam"
                )
            }

            ButtonComponent(
                labelValue = "Sign out",
                onClickAction = {
                    homeViewModel.signOut()
                },
                isEnabled = true
            )
            if (homeViewModel.homeUIState.signOutInProgress.value) {
                CircularProgressIndicator()
            }
        }
    }
}

//@Preview
//@Composable
//fun SimpleComposablePreview() {
//    val fakeHomeViewModel =
//        remember { HomeViewModel(appRouter) }
//
//    HomeScreen(homeViewModel = fakeHomeViewModel)
//}
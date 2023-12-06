package com.ridesharingapp.passengersideapp.splashscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.ridesharingapp.passengersideapp.MainActivity
import com.ridesharingapp.passengersideapp.R
import com.ridesharingapp.passengersideapp.RideSharingApp

class SplashFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val app = requireActivity().application as RideSharingApp
        app.service.showNotification("Bạn có tin nhắn mới","", com.ridesharingapp.common.R.drawable.message)
        return ComposeView(requireContext()).apply {
            // Dispose the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SplashScreen()
            }
        }
    }
}
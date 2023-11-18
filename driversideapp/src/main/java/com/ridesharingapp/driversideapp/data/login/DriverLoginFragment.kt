package com.ridesharingapp.driversideapp.data.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.ridesharingapp.driversideapp.R
import com.ridesharingapp.driversideapp.databinding.ActivityMainBinding
import com.ridesharingapp.driversideapp.databinding.FragmentHomeBinding
import com.zhuinden.simplestackextensions.fragmentsktx.backstack
import com.zhuinden.simplestackextensions.fragmentsktx.lookup

class DriverLoginFragment : Fragment() {
    private val viewModel by lazy {lookup<DriverLoginViewModel>()}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val backstack = backstack

        return ComposeView(requireContext()).apply {
            // Dispose the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DriverLoginScreen(driverLoginViewModel = viewModel)
            }
        }
    }
}
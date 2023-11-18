package com.ridesharingapp.driversideapp.data.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.libraries.places.api.Places
import com.ridesharingapp.driversideapp.BuildConfig
import com.ridesharingapp.driversideapp.R
import com.ridesharingapp.driversideapp.databinding.FragmentHomeBinding
import com.zhuinden.simplestackextensions.fragmentsktx.lookup

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
//
//class HomeFragment : Fragment(R.layout.fragment_home), OnMapReadyCallback {
//    val viewModel by lazy {lookup<HomeViewModel>() }
//    private var mapView: MapView? = null
//    private var googleMap: GoogleMap? = null
//    private var locationRequest: LocationRequest? = null
//    private lateinit var locationClient: FusedLocationProviderClient
//
//    lateinit var binding: FragmentHomeBinding
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return FragmentHomeBinding.inflate(inflater, container, false).root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        val binding = FragmentHomeBinding.bind(view)
//        Places.initialize(requireActivity().application, BuildConfig.MAPS_API_KEY)
//        binding.checkAgainBT.setOnClickListener {
//
//        }
//    }
//
//    private fun passengerPickUp(uiState: HomeUiState.PassengerPickUp) {
//        binding.apply {
//            rideLayout.visibility = View.VISIBLE
//            loadingView.loadingLayout.visibility = View.GONE
//            searchingLayout.visibility = View.GONE
//
//            passengerList.adapter = null
//
//            advanceLayout.advanceRideStateLayout.visibility = View.VISIBLE
//
//        }
//    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        this.googleMap = googleMap
//
//        if (ContextCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            Toast.makeText(
//                requireContext(),
//                R.string.permissions_required_to_use_this_app,
//                Toast.LENGTH_LONG
//            ).show()
//            viewModel.handleError()
//        } else {
//            googleMap.uiSettings.isZoomControlsEnabled = true
//            googleMap.isMyLocationEnabled = true
//            googleMap.uiSettings.setAllGesturesEnabled(true)
//
//            googleMap.setMinZoomPreference(11f)
//            viewModel.mapIsReady()
//        }
//    }
//
//    override fun onResume() {
//        mapView?.onResume()
//        super.onResume()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        mapView?.onPause()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mapView?.onDestroy()
//    }
//
//    override fun onLowMemory() {
//        super.onLowMemory()
//        mapView?.onLowMemory()
//    }
//}
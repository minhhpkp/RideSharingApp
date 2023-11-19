package com.ridesharingapp.driversideapp.data.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.maps.DirectionsApi
import com.google.maps.model.TravelMode
import com.ridesharingapp.driversideapp.BuildConfig
import com.ridesharingapp.driversideapp.R
import com.ridesharingapp.driversideapp.RideSharingApp
import com.ridesharingapp.driversideapp.databinding.FragmentHomeBinding
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import kotlinx.coroutines.launch


class HomeFragment : Fragment(R.layout.fragment_home), OnMapReadyCallback {
    val viewModel by lazy {lookup<HomeViewModel>() }
    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null
    private var locationRequest: LocationRequest? = null
    private lateinit var locationClient: FusedLocationProviderClient

    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentHomeBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHomeBinding.bind(view)
        Places.initialize(requireActivity().application, BuildConfig.MAPS_API_KEY)
        mapView = binding.mapLayout.mapView
        mapView?.onCreate(savedInstanceState)

//        requestPermission()

//        lifecycleScope.launch {
//            viewModel.uiState
//                //Only emit states when lifecycle of the fragment is started
//                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
//                .distinctUntilChanged()
//                .collect { uiState ->
//                    updateUi(uiState)
//                }
//        }
//
//        viewModel.toastHandler = {
//            handleToast(it)
//        }
//
//        //This button is reused in most states so we add the listener here
//        binding.mapLayout.cancelButton.setOnClickListener { viewModel.cancelRide() }
    }

//    private fun updateUi(uiState: HomeUiState) {
//        when (uiState) {
//            HomeUiState.Error -> viewModel.handleError()
//            HomeUiState.Loading -> {
//                binding.loadingView.loadingLayout.visibility = View.VISIBLE
//            }
//            is HomeUiState.SearchingForPassengers -> searchingForPassenger()
//            is HomeUiState.PassengerPickUp -> Unit //passengerPickUp(uiState)
//            is HomeUiState.EnRoute -> Unit //enRoute(uiState)
//            is HomeUiState.Arrived -> Unit //arrived(uiState)
//        }
//
//        updateMap(uiState)
//    }
//
//    private fun updateMap(
//        uiState: HomeUiState
//    ) {
//        if (googleMap != null) {
//            when (uiState) {
//                is HomeUiState.SearchingForPassengers -> Unit
//                is HomeUiState.PassengerPickUp -> {
//                    lifecycleScope.launch {
//                        val dirResult =
//                            DirectionsApi.newRequest((requireActivity().application as RideSharingApp).geoContext)
//                                .mode(TravelMode.DRIVING)
//                                .units(com.google.maps.model.Unit.METRIC)
//                                //Change this appropriately
//                                .region("ca")
//                                .origin(
//                                    com.google.maps.model.LatLng(
//                                        uiState.passengerLat,
//                                        uiState.passengerLon
//                                    )
//                                )
//                                .destination(
//                                    com.google.maps.model.LatLng(
//                                        uiState.driverLat,
//                                        uiState.driverLon
//                                    ).toString()
//                                )
//                                .await()
//
//                        if (dirResult.routes?.first() != null &&
//                            dirResult.routes.isNotEmpty() &&
//                            dirResult.routes.first().legs.isNotEmpty()
//                        ) {
//                            dirResult.routes.first().let { route ->
//                                route.legs.first().let { leg ->
//                                    binding.passengerDistance.text = buildString {
//                                        append(getString(R.string.passenger_is))
//                                        append(leg.distance.humanReadable)
//                                    }
//
//                                    leg.steps.forEach { step ->
//                                        googleMap!!.addPolyline(
//                                            PolylineOptions().apply {
//                                                clickable(false)
//                                                /*
//                                                Unfortunately the Directions API uses a different
//                                                LatLng from the Android Maps SDK, so we have to
//                                                convert it here.
//                                                 */
//                                                add(
//                                                    LatLng(
//                                                        step.startLocation.lat,
//                                                        step.startLocation.lng
//                                                    )
//                                                )
//
//                                                add(
//                                                    LatLng(
//                                                        step.endLocation.lat,
//                                                        step.endLocation.lng
//                                                    )
//                                                )
//
//                                                color(
//                                                    ContextCompat.getColor(
//                                                        requireContext(),
//                                                        R.color.color_primary
//                                                    )
//                                                )
//                                            }
//                                        )
//                                    }
//                                }
//                            }
//                        } else {
//                            Toast.makeText(
//                                requireContext(),
//                                R.string.unable_to_get_map_directions,
//                                Toast.LENGTH_LONG
//                            ).show()
//                        }
//
//                        googleMap!!.addMarker(
//                            MarkerOptions().apply {
//                                position(LatLng(uiState.passengerLat, uiState.passengerLon))
//                            }
//                        )
//                        googleMap!!.addMarker(
//                            MarkerOptions().apply {
//                                position(LatLng(uiState.driverLat, uiState.driverLon))
//                            }
//                        )
//
//
//                        googleMap!!.moveCamera(
//                            CameraUpdateFactory.newLatLngZoom(
//                                LatLng(uiState.passengerLat, uiState.passengerLon),
//                                14f
//                            )
//                        )
//                    }
//
//
//                }
//                is HomeUiState.EnRoute -> {
//                    lifecycleScope.launch {
//                        val dirResult =
//                            DirectionsApi.newRequest((requireActivity().application as RideSharingApp).geoContext)
//                                .mode(TravelMode.DRIVING)
//                                .units(com.google.maps.model.Unit.METRIC)
//                                //Change this appropriately
//                                .region("ca")
//                                .origin(
//                                    com.google.maps.model.LatLng(
//                                        uiState.driverLat,
//                                        uiState.driverLon
//                                    )
//                                )
//                                .destination(
//                                    com.google.maps.model.LatLng(
//                                        uiState.destinationLat,
//                                        uiState.destinationLon
//                                    ).toString()
//                                )
//                                .await()
//
//
////                        googleMap!!.addMarker(
////                            MarkerOptions().apply {
////                                position(LatLng(uiState.passengerLat, uiState.passengerLon))
////                            }
////                        )
////                        googleMap!!.addMarker(
////                            MarkerOptions().apply {
////                                position(LatLng(uiState.destinationLat, uiState.destinationLon))
////                            }
////                        )
////
////                        googleMap!!.moveCamera(
////                            CameraUpdateFactory.newLatLngZoom(
////                                LatLng(uiState.passengerLat, uiState.passengerLon),
////                                14f
////                            )
////                        )
//                    }
//
//                }
//                is HomeUiState.Arrived -> {
//                    googleMap!!.addMarker(
//                        MarkerOptions().apply {
//                            position(LatLng(uiState.destinationLat, uiState.destinationLon))
//                        }
//                    )
//
//                    googleMap!!.moveCamera(
//                        CameraUpdateFactory.newLatLngZoom(
//                            LatLng(uiState.destinationLat, uiState.destinationLon),
//                            14f
//                        )
//                    )
//                }
//
//
//                //do nothing
//                else -> Unit
//            }
//        }
//
//    }
//
//    private fun searchingForPassenger() {
//        binding.apply {
//            rideLayout.visibility = View.GONE
//            loadingView.loadingLayout.visibility = View.GONE
//            searchingLayout.visibility = View.VISIBLE
//
//
//            if (passengerList.adapter == null) {
//                passengerList.adapter = PassengerListAdapter().apply {
//                    handleItemClick = {
//                        viewModel.handlePassengerItemClick(it)
//                    }
//                }
//            }
//
//            //somewhat worried this could attach multiple observers
//            lifecycleScope.launch {
//                viewModel.locationAwarePassengerList
//                    //Only emit states when lifecycle of the fragment is started
//                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
//                    .distinctUntilChanged()
//                    .collect { models ->
//                        if (models.isNullOrEmpty()) {
//                            passengerList.visibility = View.GONE
//                            passengersLoadingLayout.visibility = View.VISIBLE
//                        } else {
//                            passengerList.visibility = View.VISIBLE
//                            passengersLoadingLayout.visibility = View.GONE
//
//                            (passengerList.adapter as PassengerListAdapter)
//                                .submitList(models)
//                        }
//                    }
//            }
//        }
//    }
//
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                requireContext(),
                R.string.permissions_required_to_use_this_app,
                Toast.LENGTH_LONG
            ).show()
            viewModel.handleError()
        } else {
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.setAllGesturesEnabled(true)

            googleMap.setMinZoomPreference(11f)
            viewModel.mapIsReady()
        }
    }
//
//    @SuppressLint("MissingPermission")
//    private fun requestLocation() {
//        //This function is a great introduction to programming with the Android SDK ;)
//        val locationManager = (requireActivity()
//            .getSystemService(Context.LOCATION_SERVICE) as LocationManager)
//
//        if (LocationManagerCompat.isLocationEnabled(locationManager)) {
//            if (ActivityCompat.checkSelfPermission(
//                    requireContext(),
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                    requireContext(),
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                requestPermission()
//            } else {
//                // Create the location request to start receiving updates
//                locationRequest = LocationRequest.Builder(
//                    Priority.PRIORITY_HIGH_ACCURACY,
//                    10000L
//                ).apply {
//                    //only update if user moved more than 10 meters
//                    setMinUpdateDistanceMeters(10f)
//                }.build()
//
//                //determine if device settings are configured properly
//                val locationSettingsRequest = LocationSettingsRequest.Builder().apply {
//                    addLocationRequest(locationRequest!!)
//                }.build()
//
//                LocationServices.getSettingsClient(requireContext())
//                    .checkLocationSettings(locationSettingsRequest).addOnCompleteListener { task ->
//                        if (task.isSuccessful) startRequestingLocationUpdates()
//                        else {
//                            Toast.makeText(
//                                requireContext(),
//                                R.string.system_settings_are_preventing_location_updates,
//                                Toast.LENGTH_LONG
//                            ).show()
//                            viewModel.handleError()
//                        }
//                    }
//            }
//        } else {
//            Toast.makeText(
//                requireContext(),
//                R.string.location_must_be_enabled,
//                Toast.LENGTH_LONG
//            ).show()
//            viewModel.handleError()
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun startRequestingLocationUpdates() {
//        locationClient
//            .lastLocation
//            .addOnCompleteListener { locationRequest ->
//                if (locationRequest.isSuccessful && locationRequest.result != null) {
//                    val location = locationRequest.result
//
//                    val lat = location.latitude
//                    val lon = location.longitude
//                    viewModel.updateDriverLocation(com.google.maps.model.LatLng(lat, lon))
//                } else {
//
//                    Log.d("PLACES", locationRequest.exception.toString())
//
//                    Toast.makeText(
//                        requireContext(),
//                        R.string.unable_to_retrieve_coordinates_user,
//                        Toast.LENGTH_LONG
//                    ).show()
//                    viewModel.handleError()
//                }
//            }
//    }

//    private fun requestPermission() {
//        if (ContextCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            //permission not granted
//            requestPermissionLauncher.launch(
//                Manifest.permission.ACCESS_FINE_LOCATION
//            )
//        } else {
//            //begin map initialization
//            mapView?.getMapAsync(this)
//
//            //get user location
//            requestLocation()
//        }
//    }
//
//    val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        if (isGranted) requestLocation()
//        else {
//            Toast.makeText(
//                requireContext(),
//                R.string.permissions_required_to_use_this_app,
//                Toast.LENGTH_LONG
//            ).show()
//            viewModel.handleError()
//        }
//    }


    override fun onResume() {
        mapView?.onResume()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}
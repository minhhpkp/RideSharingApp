package com.ridesharingapp.driversideapp

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.GeoApiContext
import com.ridesharingapp.driversideapp.data.services.RideService
import com.zhuinden.simplestack.GlobalServices

class RideSharingApp : Application() {
    lateinit var globalServices: GlobalServices
    lateinit var geoContext: GeoApiContext

    override fun onCreate() {
        super.onCreate()
        MapsInitializer.initialize(this)
        geoContext = GeoApiContext.Builder()
            .apiKey(BuildConfig.MAPS_API_KEY)
            .build()


        globalServices = GlobalServices.builder()
            .build()
    }
}
package com.ridesharingapp.driversideapp

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import com.google.maps.GeoApiContext
import com.ridesharingapp.driversideapp.data.fake.FakeRideService
import com.ridesharingapp.driversideapp.data.fake.FakeUserService
import com.ridesharingapp.driversideapp.data.services.RideService
import com.ridesharingapp.driversideapp.data.services.UserService
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.rebind

class RideSharingApp : Application() {
    lateinit var globalServices: GlobalServices
    lateinit var geoContext: GeoApiContext

    override fun onCreate() {
        super.onCreate()
        MapsInitializer.initialize(this)
        geoContext = GeoApiContext.Builder()
            .apiKey(BuildConfig.MAPS_API_KEY)
            .build()

        val fakeUser = FakeUserService()
        val fakeRideService = FakeRideService()

        globalServices = GlobalServices.builder()
            .add(fakeUser)
            .rebind<UserService>(fakeUser)
            .add(fakeRideService)
            .rebind<RideService>(fakeRideService)
            .build()
    }
}
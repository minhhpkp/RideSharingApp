package com.ridesharingapp.driversideapp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class RideSharingApp : Application() {
    var updateRolesInProgress: Boolean = false
    var updateRolesSuccess: Boolean = false
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        println("App on terminate $updateRolesInProgress $updateRolesSuccess")
        if (updateRolesInProgress && !updateRolesSuccess) {
            FirebaseAuth.getInstance().signOut()
        }
    }
}
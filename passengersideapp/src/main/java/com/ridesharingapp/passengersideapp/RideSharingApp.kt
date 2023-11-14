package com.ridesharingapp.passengersideapp

import android.app.Application
import com.google.firebase.FirebaseApp

class RideSharingApp : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}
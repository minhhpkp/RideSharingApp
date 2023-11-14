package com.ridesharingapp.passengersideapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.ridesharingapp.passengersideapp.navigation.ScreenNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val db = Firebase.firestore

        setContent {
            var loading by rememberSaveable {
                mutableStateOf(true)
            }
            rememberSaveable {
                if (auth.currentUser != null) {
                    println("${auth.currentUser!!.uid} ${auth.currentUser!!.email}")
                    db.collection("Users")
                        .document(auth.currentUser!!.uid)
                        .get().addOnSuccessListener { doc ->
                            println("MainAct: get user doc successfully")
                            val roles = doc.get("Roles")!! as Long
                            println("MainAct: roles = $roles")
                            // attempt to login with unauthorized roles
                            if (roles != 0L && roles != 2L) {
                                auth.signOut()
                                if (auth.currentUser != null) throw Exception("Cleanup unsuccessfully")
                            }
                            loading = false
                        }
                        .addOnFailureListener{
                            println("MainAct: failed to get user doc")
                            auth.signOut()
                            loading = false
                        }
                } else {
                    loading = false
                }
                0
            }
            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                ScreenNavigation(auth = auth, db = db)
            }
        }
    }
}
package com.ridesharingapp.driversideapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ridesharingapp.driversideapp.data.registration.SignUpScreen
import com.ridesharingapp.driversideapp.databinding.ActivityMainBinding
import com.ridesharingapp.driversideapp.navigation.DriverHomeKey
import com.ridesharingapp.driversideapp.navigation.LoginKey
import com.ridesharingapp.driversideapp.navigation.SignUpKey
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger
import com.zhuinden.simplestackextensions.navigatorktx.backstack
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider

class MainActivity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    private lateinit var fragmentStateChanger: DefaultFragmentStateChanger
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.container)

        Navigator.configure()
            .setStateChanger(SimpleStateChanger(this))
            .install(this, binding.container, History.single(DriverHomeKey()))
    }
    override fun onBackPressed() {
        if (!backstack.goBack()) {
            super.onBackPressed()
        }
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        fragmentStateChanger.handleStateChange(stateChange)
    }
}

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val auth = FirebaseAuth.getInstance()
//        val db = FirebaseFirestore.getInstance()
//
//        setContent {
//            var loading by rememberSaveable {
//                mutableStateOf(true)
//            }
//            rememberSaveable {
//                if (auth.currentUser != null) {
//                    println("${auth.currentUser!!.uid} ${auth.currentUser!!.email}")
//                    db.collection("Users")
//                        .document(auth.currentUser!!.uid)
//                        .get().addOnSuccessListener { doc ->
//                            println("MainAct: get user doc successfully")
//                            val roles = doc.get("Roles")!! as Long
//                            println("MainAct: roles = $roles")
//                            // attempt to login with unauthorized roles
//                            if (roles != 1L && roles != 2L) {
//                                auth.signOut()
//                                if (auth.currentUser != null) throw Exception("Cleanup unsuccessfully")
//                            }
//                            loading = false
//                        }
//                        .addOnFailureListener{
//                            println("MainAct: failed to get user doc")
//                            auth.signOut()
//                            loading = false
//                        }
//                } else {
//                    loading = false
//                }
//                0
//            }
//
//            if (loading) {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator()
//                }
//            } else {
//                ScreenNavigation(auth = auth, db = db)
//            }
//        }
//    }

package com.ridesharingapp.driversideapp.navigation

import androidx.fragment.app.Fragment
import com.ridesharingapp.driversideapp.data.login.DriverLoginFragment
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginKey(private val noArgsPlaceholder: String = ""): DefaultFragmentKey()  {
    override fun instantiateFragment(): Fragment = DriverLoginFragment()
}
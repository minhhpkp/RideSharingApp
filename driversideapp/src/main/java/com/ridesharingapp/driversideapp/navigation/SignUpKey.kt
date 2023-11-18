package com.ridesharingapp.driversideapp.navigation

import androidx.fragment.app.Fragment
import com.ridesharingapp.driversideapp.data.registration.SignUpFragment
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignUpKey(private val noArgsPlaceholder: String = ""): DefaultFragmentKey()  {
    override fun instantiateFragment(): Fragment = SignUpFragment()
}
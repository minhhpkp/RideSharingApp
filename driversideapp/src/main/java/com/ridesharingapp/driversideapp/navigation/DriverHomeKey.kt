package com.ridesharingapp.driversideapp.navigation

import androidx.fragment.app.Fragment
import com.ridesharingapp.driversideapp.data.home.HomeFragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

@Parcelize
data class DriverHomeKey(private val noArgsPlaceholder: String = "") : DefaultFragmentKey() {
    override fun instantiateFragment(): Fragment = HomeFragment.newInstance()

}

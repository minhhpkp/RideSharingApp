package com.ridesharingapp.driversideapp.navigation

import androidx.fragment.app.Fragment
import com.ridesharingapp.driversideapp.settings.ProfileSettingsFragment
import com.ridesharingapp.driversideapp.settings.ProfileSettingsViewModel
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileSettingsKey(private val noArgsPlaceholder: String = ""): DefaultFragmentKey(),
    DefaultServiceProvider.HasServices {
    override fun instantiateFragment(): Fragment = ProfileSettingsFragment()

    override fun getScopeTag(): String = toString()

    //How to create a scoped service
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(ProfileSettingsViewModel(backstack, lookup(), lookup(), lookup(), lookup()))
        }
    }
}
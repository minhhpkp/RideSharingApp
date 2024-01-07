package com.ridesharingapp.common.uicommon.history

import androidx.fragment.app.Fragment
import com.ridesharingapp.common.domain.UserType
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

@Parcelize
class HistoryKey(val userId: String, val userType: UserType): DefaultFragmentKey(),
    DefaultServiceProvider.HasServices {
    override fun instantiateFragment(): Fragment = HistoryFragment()

    override fun getScopeTag(): String = toString()

    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(HistoryViewModel(backstack, userId, userType, lookup()))
        }
    }
}
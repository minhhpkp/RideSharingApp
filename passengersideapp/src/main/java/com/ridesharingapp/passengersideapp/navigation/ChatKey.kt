package com.ridesharingapp.passengersideapp.navigation

import androidx.fragment.app.Fragment
import com.ridesharingapp.passengersideapp.chat.ChatFragment
import com.ridesharingapp.passengersideapp.chat.ChatViewModel
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatKey(val channelId: String): DefaultFragmentKey(),
    DefaultServiceProvider.HasServices {
    override fun instantiateFragment(): Fragment = ChatFragment()

    override fun getScopeTag(): String = toString()

    //How to create a scoped service
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            //add(SplashViewModel(lookup(), backstack))
            add(ChatViewModel(backstack, lookup(), lookup()))
        }
    }
}
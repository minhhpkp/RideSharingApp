package com.ridesharingapp.passengersideapp

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.GeoApiContext
import com.ridesharingapp.passengersideapp.services.AuthenticationService
import com.ridesharingapp.passengersideapp.services.FirebaseAuthService
import com.ridesharingapp.passengersideapp.services.StreamUserService
import com.ridesharingapp.passengersideapp.services.UserService
import com.ridesharingapp.passengersideapp.usecases.GetUser
import com.ridesharingapp.passengersideapp.usecases.LogInUser
import com.ridesharingapp.passengersideapp.usecases.SignUpUser
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.rebind
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.offline.model.message.attachments.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.configuration.Config
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory

class RideSharingApp : Application() {
    lateinit var globalServices: GlobalServices
    lateinit var geoContext: GeoApiContext

    override fun onCreate() {
        super.onCreate()

        val streamClient = configureStream()

        val firebaseAuthService = FirebaseAuthService(FirebaseAuth.getInstance())

        val streamUserService = StreamUserService(streamClient)

        val getUser = GetUser(firebaseAuthService, streamUserService)
        val signUpUser = SignUpUser(firebaseAuthService, streamUserService)
        val logInUser = LogInUser(firebaseAuthService, streamUserService)

        globalServices = GlobalServices.builder()
            .add(streamUserService)
            .rebind<UserService>(streamUserService)
            .add(firebaseAuthService)
            .rebind<AuthenticationService>(firebaseAuthService)
            .add(getUser)
            .add(signUpUser)
            .add(logInUser)
            .build()
    }

    private fun configureStream(): ChatClient {
        val logLevel = if (BuildConfig.DEBUG) ChatLogLevel.ALL else ChatLogLevel.NOTHING
        val pluginFactory = StreamOfflinePluginFactory(
            config = Config(
                backgroundSyncEnabled = true,
                userPresence = true,
                persistenceEnabled = true,
                uploadAttachmentsNetworkType = UploadAttachmentsNetworkType.NOT_ROAMING,
            ),
            appContext = this,
        )

        return ChatClient.Builder(BuildConfig.STREAM_API_KEY, this)
            .withPlugin(pluginFactory)
            .logLevel(logLevel)
            .build()
    }
}
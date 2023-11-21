package com.ridesharingapp.driversideapp

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.maps.GeoApiContext
import com.ridesharingapp.driversideapp.data.google.GoogleService
import com.ridesharingapp.driversideapp.data.services.AuthorizationService
import com.ridesharingapp.driversideapp.data.services.FirebaseAuthService
import com.ridesharingapp.driversideapp.data.services.FirebasePhotoService
import com.ridesharingapp.driversideapp.data.services.RideService
import com.ridesharingapp.driversideapp.data.services.StreamRideService
import com.ridesharingapp.driversideapp.data.services.StreamUserService
import com.ridesharingapp.driversideapp.data.services.UserService
import com.ridesharingapp.driversideapp.data.usecase.GetUser
import com.ridesharingapp.driversideapp.data.usecase.LogInUser
import com.ridesharingapp.driversideapp.data.usecase.LogOutUser
import com.ridesharingapp.driversideapp.data.usecase.SignUpUser
import com.ridesharingapp.driversideapp.data.usecase.UpdateUserAvatar
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
        MapsInitializer.initialize(this)
        geoContext = GeoApiContext.Builder()
            .apiKey(BuildConfig.MAPS_API_KEY)
            .build()
        val streamClient = configureStream()

        val firebaseAuthService = FirebaseAuthService(FirebaseAuth.getInstance())
        val firebaseStorageService = FirebasePhotoService(FirebaseStorage.getInstance(), this)

        val streamUserService = StreamUserService(streamClient)
        val streamRideService = StreamRideService(streamClient)

        val googleService = GoogleService(this, geoContext)

        /*
        Usecases:
        - In situations where multiple BE services must be coordinated in order to carry out a
        single function, a usecase is employed. However, In situations where a single call to a
        single BE service is required, there does not tend to be any benefit to adding usecases.
         */
        val getUser = GetUser(firebaseAuthService, streamUserService)
        val signUpUser = SignUpUser(firebaseAuthService, streamUserService)
        val logInUser = LogInUser(firebaseAuthService, streamUserService)
        val logOutUser = LogOutUser(firebaseAuthService, streamUserService)
        val updateUserAvatar = UpdateUserAvatar(firebaseStorageService, streamUserService)

        globalServices = GlobalServices.builder()
            .add(streamRideService)
            .rebind<RideService>(streamRideService)
            .add(streamUserService)
            .rebind<UserService>(streamUserService)
            .add(firebaseAuthService)
            .rebind<AuthorizationService>(firebaseAuthService)
            .add(googleService)
            .add(getUser)
            .add(signUpUser)
            .add(logInUser)
            .add(logOutUser)
            .add(updateUserAvatar)
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
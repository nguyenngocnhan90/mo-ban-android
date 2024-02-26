package com.moban

import android.app.Application
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.moban.constant.ServerURL
import com.moban.dagger.component.DaggerLinkmartNetComponent
import com.moban.dagger.component.DaggerNetComponent
import com.moban.dagger.component.LinkmartNetComponent
import com.moban.dagger.component.NetComponent
import com.moban.dagger.module.AppModule
import com.moban.dagger.module.LinkmartNetModule
import com.moban.dagger.module.NetModule
import com.moban.helper.CommonSharedStorage
import com.moban.helper.Configuration
import com.moban.helper.LHCache
import com.moban.helper.LocalStorage
import com.moban.network.service.LinkmartService
import com.moban.notification.NotificationOpenedHandler
import com.onesignal.BuildConfig
import com.onesignal.OneSignal
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import retrofit2.Retrofit

/**
 * Created by thangnguyen on 12/18/17.
 */
class LHApplication : Application() {
    companion object {
        lateinit var instance: LHApplication
            private set

        lateinit var config: Configuration
            private set

        lateinit var commonStorage: CommonSharedStorage
            private set
    }

    private var netComponent: NetComponent? = null
    private var fbManager: CallbackManager? = null
    private var linkmartNetComponent: LinkmartNetComponent? = null
    private var linkHubComponent: NetComponent? = null

    var lhCache = LHCache()
    var rootUrl = ServerURL.productionURL

    override fun onCreate() {
        super.onCreate()
        instance = this
        config = Configuration(applicationContext)
        commonStorage = CommonSharedStorage(applicationContext)

        initNetComponent()
        initLinkHubComponent()
        initLinkmartNetComponent()

        // Firebase
//        FirebaseApp.initializeApp(this)

        ViewPump.init(ViewPump.builder()
                .addInterceptor(
                        CalligraphyInterceptor(CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/WorkSans-Regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()
                        )
                ).build()
        )

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId("e7e4abd7-4d6a-4196-a7c9-a9d9e0143045")
        OneSignal.unsubscribeWhenNotificationsAreDisabled(true)
        OneSignal.addSubscriptionObserver { stateChange ->
            val userId = stateChange.to.userId
            if (!userId.isNullOrEmpty()) {
                LocalStorage.saveOneSignalId(userId)
            }
            val registrationId = stateChange.to.pushToken
            if (!registrationId.isNullOrEmpty()) {
                LocalStorage.saveGoogleRegistrationId(registrationId)
            }
        }
        OneSignal.setNotificationOpenedHandler(NotificationOpenedHandler())

//        if (!BuildConfig.DEBUG) {
//            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
//        }

        // Fresco
        val config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .build()
        Fresco.initialize(this, config)

        //Facebook
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))
        FacebookSdk.sdkInitialize(this)
    }

    fun getNetComponent(): NetComponent? {
        return netComponent
    }

    fun getFbManager(): CallbackManager? {
        if (fbManager == null) {
            fbManager = CallbackManager.Factory.create()
        }
        return fbManager
    }

    fun initNetComponent() {
        rootUrl = if (BuildConfig.DEBUG) ServerURL.stagingURL else ServerURL.productionURL
        netComponent = DaggerNetComponent.builder()
                .appModule(AppModule(this))
                .netModule(NetModule(rootUrl + "api/", this))
                .build()
    }

    fun initLinkHubComponent() {
        linkHubComponent = DaggerNetComponent.builder()
                .appModule(AppModule(this))
                .netModule(NetModule(ServerURL.secondaryProjectUrl + "api/", this))
                .build()
    }

    fun getLinkmartNetComponent(): LinkmartNetComponent? {
        return linkmartNetComponent
    }

    fun getLinkHubNetComponent(): NetComponent? {
        return linkHubComponent
    }

    fun initLinkmartNetComponent() {
        linkmartNetComponent = DaggerLinkmartNetComponent.builder()
                .appModule(AppModule(this))
                .linkmartNetModule(LinkmartNetModule(ServerURL.linkmartURL))
                .build()
    }

    fun getLinkmartService(): LinkmartService? {
        instance.initLinkmartNetComponent()
        val retrofit: Retrofit? = instance.getLinkmartNetComponent()?.retrofit()
        return retrofit?.create(LinkmartService::class.java)
    }
}

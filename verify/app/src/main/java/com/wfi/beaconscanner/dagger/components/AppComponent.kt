package com.wfi.beaconscanner.dagger.components

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.wfi.beaconscanner.API.LoggingService
import com.wfi.beaconscanner.dagger.modules.*
import com.wfi.beaconscanner.events.RxBus
import com.wfi.beaconscanner.utils.PreferencesHelper
import com.wfi.beaconscanner.utils.RatingHelper
import dagger.Component
import io.realm.Realm
import javax.inject.Singleton

/**
 * Created by nisha on 05/10/2016.
 */
@Singleton
@Component(modules = arrayOf(
        ContextModule::class,
        DatabaseModule::class,
        NetworkModule::class,
        EventModule::class,
        PreferencesModule::class,
        AnalyticsModule::class
))

interface AppComponent {
    fun context(): Context
    fun realm(): Realm
    fun loggingService(): LoggingService
    fun rxBus(): RxBus
    fun prefs(): PreferencesHelper
    fun rating(): RatingHelper
    fun tracker(): FirebaseAnalytics
}

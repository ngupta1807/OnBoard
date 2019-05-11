package com.wfi.beaconscanner

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.wfi.beaconscanner.dagger.components.ActivityComponent
import com.wfi.beaconscanner.dagger.components.DaggerActivityComponent
import com.wfi.beaconscanner.dagger.components.DaggerAppComponent
import com.wfi.beaconscanner.dagger.modules.*
import com.wfi.beaconscanner.utils.RatingHelper
import io.realm.Realm
import io.realm.RealmConfiguration
import javax.inject.Inject

/**
 * Created by nisha.
 */

class AppSingleton : Application() {
    companion object {
       lateinit var activityComponent: ActivityComponent
       lateinit var mcon : Context
    }

    @Inject lateinit var ratingHelper: RatingHelper

    override fun onCreate() {
        super.onCreate()
        val appComponent = DaggerAppComponent.builder()
                .contextModule(ContextModule(this))
                .networkModule(NetworkModule())
                .databaseModule(DatabaseModule())
                .eventModule(EventModule())
                .build()

        mcon = this.baseContext

        activityComponent = DaggerActivityComponent.builder()
                .appComponent(appComponent)
                .bluetoothModule(BluetoothModule())
                .build()

        activityComponent.inject(this)
        MultiDex.install(this)
        Realm.init(this)
        registerActivityLifecycleCallbacks(MyLifecycleHandler())
        val realmConfig = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()

        Realm.setDefaultConfiguration(realmConfig)

        ratingHelper.incrementAppOpens()
    }

}

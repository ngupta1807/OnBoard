package com.wfi.beaconscanner.dagger.modules

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by nisha on 06/04/2017.
 */

@Module
class AnalyticsModule {

    @Provides
    @Singleton
    fun providesFirebaseAnalytics(ctx: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(ctx)
    }
}

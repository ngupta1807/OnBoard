package com.wfi.beaconscanner.dagger.modules

import android.content.Context
import com.wfi.beaconscanner.utils.PreferencesHelper
import com.wfi.beaconscanner.utils.RatingHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by nisha on 03/04/2017.
 */

@Module
class PreferencesModule {

    @Provides
    @Singleton
    fun providesPreferencesHelper(ctx: Context): PreferencesHelper {
        return PreferencesHelper(ctx)
    }

    @Provides
    @Singleton
    fun providesRatingHelper(ctx: Context): RatingHelper {
        return RatingHelper(ctx)
    }
}

package com.wfi.beaconscanner.dagger.modules

import com.wfi.beaconscanner.events.RxBus
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by nisha on 05/10/2016.
 */

@Module
class EventModule {
    @Provides
    @Singleton
    fun providesRxBus(): RxBus {
        return RxBus()
    }
}

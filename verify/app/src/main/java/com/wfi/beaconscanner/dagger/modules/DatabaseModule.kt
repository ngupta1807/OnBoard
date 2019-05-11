package com.wfi.beaconscanner.dagger.modules

import dagger.Module
import dagger.Provides
import io.realm.Realm

/**
 * Created by nisha on 05/10/2016.
 */
@Module
class DatabaseModule {
    @Provides
    fun providesRealm(): Realm {
        return Realm.getDefaultInstance()
    }
}

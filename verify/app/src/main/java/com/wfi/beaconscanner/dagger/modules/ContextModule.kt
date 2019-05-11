package com.wfi.beaconscanner.dagger.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ContextModule(private val context: Application) {

    @Provides
    @Singleton
    fun providesContext(): Context {
        return context
    }
}

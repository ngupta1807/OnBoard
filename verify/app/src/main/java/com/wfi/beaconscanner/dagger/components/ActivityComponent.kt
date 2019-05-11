package com.wfi.beaconscanner.dagger.components

import com.wfi.beaconscanner.AppSingleton
import com.wfi.beaconscanner.dagger.PerActivity
import com.wfi.beaconscanner.dagger.modules.BluetoothModule
import com.wfi.beaconscanner.features.beaconList.BeaconListActivity
import com.wfi.beaconscanner.features.beaconList.ControlsBottomSheetDialog
import com.wfi.beaconscanner.features.blockedList.BlockedActivity
import com.wfi.beaconscanner.features.settings.SettingsActivity
import com.wfi.beaconscanner.features.startapp.AddToken

import dagger.Component
import org.altbeacon.beacon.BeaconManager

/**
 * Created by nisha on 08/10/2016.
 */
@PerActivity
@Component(
        dependencies = arrayOf(AppComponent::class),
        modules = arrayOf(BluetoothModule::class)
)
interface ActivityComponent {
    fun providesBeaconManager() : BeaconManager

    fun inject(app: AppSingleton)
    fun inject(activity: AddToken)
    fun inject(activity: BeaconListActivity)
    fun inject(activity: SettingsActivity)
    fun inject(activity: BlockedActivity)

    fun inject(bs: ControlsBottomSheetDialog)
}

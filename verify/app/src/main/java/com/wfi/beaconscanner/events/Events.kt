package com.wfi.beaconscanner.events

import android.bluetooth.BluetoothAdapter
import com.wfi.beaconscanner.features.beaconList.BeaconListActivity
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.Region

/**
 * Created by nisha on 05/10/2016.
 */

class Events {
    class RangeBeacon(var beacons: Collection<Beacon>, var region: Region)

    class BluetoothState(var state: Int) {
        fun getBluetoothState() : BeaconListActivity.BluetoothState  {
            return when (state) {
                BluetoothAdapter.STATE_OFF -> BeaconListActivity.BluetoothState.STATE_OFF
                BluetoothAdapter.STATE_TURNING_OFF -> BeaconListActivity.BluetoothState.STATE_TURNING_OFF
                BluetoothAdapter.STATE_ON -> BeaconListActivity.BluetoothState.STATE_ON
                BluetoothAdapter.STATE_TURNING_ON -> BeaconListActivity.BluetoothState.STATE_TURNING_ON
                else -> BeaconListActivity.BluetoothState.STATE_OFF
            }
        }
    }
}

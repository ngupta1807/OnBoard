package com.wfi.beaconscanner.models

import com.google.gson.annotations.SerializedName

/**
 * Created by nisha.
 */

data class LoggingRequest(@SerializedName("sendtime") val sendtime: String,
                          @SerializedName("mode") val mode: String,
                          @SerializedName("device_id") val device_id: String,
                          @SerializedName("beacons") val beacons: List<BeaconSaved>)
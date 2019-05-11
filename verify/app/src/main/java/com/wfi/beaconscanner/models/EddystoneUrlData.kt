package com.wfi.beaconscanner.models

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

/**
 * Created by nisha.
 */

open class EddystoneUrlData() : RealmObject() {
    @SerializedName("url") var url: String? = null

    constructor(u: String) : this() {
        url = u
    }

    fun clone(): EddystoneUrlData {
        val ret = EddystoneUrlData()

        ret.url = url

        return ret
    }
}
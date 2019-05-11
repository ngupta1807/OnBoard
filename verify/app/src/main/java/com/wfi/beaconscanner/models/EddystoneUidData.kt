package com.wfi.beaconscanner.models

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

/**
 * Created by nisha.
 */

open class EddystoneUidData() : RealmObject() {
    @SerializedName("namespaceId") var namespaceId: String? = null
    @SerializedName("instanceId") var instanceId: String? = null

    constructor(nameId: String, instId: String) : this() {
        namespaceId = nameId
        instanceId = instId
    }

    fun clone(): EddystoneUidData {
        val ret = EddystoneUidData()

        ret.namespaceId = namespaceId
        ret.instanceId = instanceId

        return ret
    }
}
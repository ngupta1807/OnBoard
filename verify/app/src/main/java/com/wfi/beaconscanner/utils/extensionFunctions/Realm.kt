package com.wfi.beaconscanner.utils.extensionFunctions

import com.wfi.beaconscanner.models.BeaconSaved
import io.realm.Case
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

/**
 * Created by nisha.
 */

fun Realm.getScannedBeacons(blocked: Boolean = false) : RealmResults<BeaconSaved> {
    return this.where(BeaconSaved::class.java)
            .equalTo("isBlocked", blocked)
            .sort(arrayOf("lastMinuteSeen", "lastSeen"), arrayOf(Sort.DESCENDING, Sort.DESCENDING))
            .findAll()
}

fun Realm.getScannedBeaconsName(name:String) : RealmResults<BeaconSaved> {
    return this.where(BeaconSaved::class.java)
            .equalTo("isBlocked", false)
            .contains("beaconName", name, Case.INSENSITIVE)
            .findAllAsync()
}

fun Realm.getBeaconsScannedAfter(timestamp: Long) : RealmResults<BeaconSaved> {
    return this.where(BeaconSaved::class.java)
            .greaterThan("lastSeen", timestamp)
            .equalTo("isBlocked", false)
            .findAllAsync()
}

fun Realm.clearScannedBeacons(blocked: Boolean = false) {
    this.executeTransactionAsync { tRealm ->
        tRealm.where(BeaconSaved::class.java)
                .equalTo("isBlocked", blocked)
                .findAll().deleteAllFromRealm()
    }
}

fun Realm.clearOldScannedBeacons(timestamp: Long) {
    this.executeTransactionAsync { tRealm ->
        tRealm.where(BeaconSaved::class.java)
                .equalTo("isBlocked", false)
                .lessThan("lastSeen", timestamp)
                .findAll().deleteAllFromRealm()
    }
}

fun Realm.getBeaconWithId(hashcode: Int) : BeaconSaved? {
    return this.where(BeaconSaved::class.java).equalTo("hashcode", hashcode).findFirst()
}
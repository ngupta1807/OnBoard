package com.wfi.beaconscanner.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.wfi.beaconscanner.utils.RuuviParser
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor
import java.util.*

/**
 * Created by nisha.
 */

open class BeaconSaved() : RealmObject(), Parcelable {

    @PrimaryKey
    @SerializedName("hashcode") var hashcode: Int = 0// hashcode()
    @SerializedName("beaconName") var beaconName: String = ""// beaconName()
    @SerializedName("sStatus") var sStatus: String = "" // hashcode()
    @SerializedName("beaconType") var beaconType: String = "" // Eddystone, altBeacon, iBeacon
    @SerializedName("beaconAddress") var beaconAddress: String? = null // MAC address of the bluetooth emitter
    @SerializedName("manufacturer") var manufacturer: Int = 0
    @SerializedName("txPower") var txPower: Int = 0
    @SerializedName("rssi") var rssi: Int = 0
    @SerializedName("distance") var distance: Double = 0.toDouble()
    @SerializedName("lastSeen") var lastSeen: Long = 0
    @SerializedName("lastMinuteSeen") var lastMinuteSeen: Long = 0

    /**
     * Specialized field for every beacon type
     */
    @SerializedName("ibeaconData") var ibeaconData: IbeaconData? = null
    @SerializedName("eddystoneUrlData") var eddystoneUrlData: EddystoneUrlData? = null
    @SerializedName("eddystoneUidData") var eddystoneUidData: EddystoneUidData? = null
    @SerializedName("telemetryData") var telemetryData: TelemetryData? = null
    @SerializedName("ruuviData") var ruuviData: RuuviData? = null
    //internal lateinit var mDbHelper: DbAdapter
    /**
     * Fields for the app logic
     */
    var isBlocked: Boolean = false

    constructor(parcel: Parcel) : this() {
        hashcode = parcel.readInt()
        beaconName = parcel.readString()
        beaconType = parcel.readString()
        beaconAddress = parcel.readString()
        manufacturer = parcel.readInt()
        txPower = parcel.readInt()
        rssi = parcel.readInt()
        distance = parcel.readDouble()
        lastSeen = parcel.readLong()
        lastMinuteSeen = parcel.readLong()
    }

    constructor(beacon: Beacon, sstatus: String) : this() {
        // Common fields to every beacons
        hashcode = beacon.hashCode()
        lastSeen = Date().time
        lastMinuteSeen = Date().time / 1000 / 60
        beaconAddress = beacon.bluetoothAddress
        manufacturer = beacon.manufacturer
        rssi = beacon.rssi
        txPower = beacon.txPower
        distance = beacon.distance
        sStatus= sstatus

        if (beacon.serviceUuid == 0xFEAA) { // This is an Eddystone format

            // Do we have telemetry data?
            if (beacon.extraDataFields.size >= 5) {
                telemetryData = TelemetryData(beacon.extraDataFields[0],
                        beacon.extraDataFields[1],
                        TelemetryData.getTemperatureFromTlmField(beacon.extraDataFields[2].toFloat()),
                        beacon.extraDataFields[3],
                        beacon.extraDataFields[4])
            }

            when (beacon.beaconTypeCode) {
                0x00 -> { // This is a Eddystone-UID frame
                    beaconType = BeaconSaved.TYPE_EDDYSTONE_UID
                    eddystoneUidData = EddystoneUidData(beacon.id1.toString(), beacon.id2.toString())
                }
                0x10 -> { // This is a Eddystone-URL frame
                    beaconType = BeaconSaved.TYPE_EDDYSTONE_URL
                    val url = UrlBeaconUrlCompressor.uncompress(beacon.id1.toByteArray())
                    eddystoneUrlData = EddystoneUrlData(url)

                    if (url?.startsWith("https://ruu.vi/#") == true) { // This is a RuuviTag
                        val hash = url.split("#").get(1)

                        // We manually set the hashcode of the RuuviTag so it only appears once per address
                        hashcode = beaconAddress?.hashCode() ?: -1
                        beaconType = BeaconSaved.TYPE_RUUVITAG
                        val ruuviParser = RuuviParser(hash)

                        ruuviData = RuuviData(ruuviParser.humidity, ruuviParser.airPressure, ruuviParser.temp)
                    }
                }
            }
        } else { // This is an iBeacon or ALTBeacon
            beaconType = if (beacon.beaconTypeCode == 0xBEAC) BeaconSaved.TYPE_ALTBEACON else BeaconSaved.TYPE_IBEACON // 0x4c000215 is iBeacon
            ibeaconData = IbeaconData(beacon.id1.toString(), beacon.id2.toString(), beacon.id3.toString())
        }
        beaconName=""
    }

    fun getData() : BeaconSaved {
        val ret = BeaconSaved()
        ret.hashcode = hashcode
        ret.beaconName = beaconName
        ret.beaconType = beaconType
        ret.beaconAddress = beaconAddress
        ret.manufacturer = manufacturer
        ret.txPower = txPower
        ret.rssi = rssi
        ret.distance = distance
        ret.lastSeen = lastSeen
        ret.lastMinuteSeen = lastMinuteSeen
        ret.ibeaconData = ibeaconData?.clone()
        ret.eddystoneUrlData = eddystoneUrlData?.clone()
        ret.eddystoneUidData = eddystoneUidData?.clone()
        ret.telemetryData = telemetryData?.clone()
        ret.ruuviData = ruuviData?.clone()
        ret.sStatus = sStatus

        return ret
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(hashcode)
        parcel.writeString(beaconName)
        parcel.writeString(beaconType)
        parcel.writeString(beaconAddress)
        parcel.writeInt(manufacturer)
        parcel.writeInt(txPower)
        parcel.writeInt(rssi)
        parcel.writeDouble(distance)
        parcel.writeLong(lastSeen)
        parcel.writeLong(lastMinuteSeen)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BeaconSaved> {
        @Ignore const val TYPE_EDDYSTONE_UID = "eddystone_uid"
        @Ignore const val TYPE_OFFLINE = "eddystone_uid_offline"
        @Ignore const val TYPE_EDDYSTONE_URL = "eddystone_url"
        @Ignore const val TYPE_ALTBEACON = "altbeacon"
        @Ignore const val TYPE_IBEACON = "ibeacon"
        @Ignore const val TYPE_RUUVITAG = "ruuvitag"

        override fun createFromParcel(parcel: Parcel): BeaconSaved {
            return BeaconSaved(parcel)
        }

        override fun newArray(size: Int): Array<BeaconSaved?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        var str =
                "{\"beaconAddress\":\"$beaconAddress\"," +
                "\"beaconType\":\"$beaconType\"," +
                "\"distance\":$distance," +
                "\"hashcode\":$hashcode," +
                "\"beaconName\":$beaconName," +
                "\"isBlocked\":$isBlocked," +
                "\"lastMinuteSeen\":$lastMinuteSeen," +
                "\"lastSeen\":$lastSeen," +
                "\"manufacturer\":$manufacturer," +
                "\"rssi\":$rssi," +
                "\"sStatus\":$sStatus," +
                "\"batteryMilliVolts\":${telemetryData?.batteryMilliVolts}," +
                "\"txPower\":$txPower"
        ;

        if (eddystoneUidData != null) {
            if(eddystoneUidData?.instanceId!!.startsWith("0x"))
                str += ",\"instanceId\":\"${eddystoneUidData?.instanceId!!.substring(2,eddystoneUidData?.instanceId!!.length)}\""

            else
                str += ",\"instanceId\":\"${eddystoneUidData?.instanceId}\""
        }else{
            str += ",\"instanceId\":\"\""
        }
        return str + "}"
    }
}

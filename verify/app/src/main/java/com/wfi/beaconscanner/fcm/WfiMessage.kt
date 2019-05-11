package com.wfi.beaconscanner.fcm

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.wfi.beaconscanner.features.startapp.AddToken
import com.wfi.beaconscanner.service.DeviceUpdate
import com.wfi.beaconscanner.utils.Keys
import com.wfi.beaconscanner.utils.SharedPreferencesManager
import org.json.JSONObject


/**
 * Created by nisha used to handle notifications from server.
 */
class WfiMessage() : FirebaseMessagingService(){
    private lateinit var spm: SharedPreferencesManager
    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        val json = p0?.data
        val body = json?.get("body")
        spm = SharedPreferencesManager(this)
        try {
            if(body.toString().contains("Deleted") ){
                spm.saveStringValues(Keys.CHECK,"");
                spm.saveStringValues(Keys.DeviceStatus, "Deleted")
                stopService(Intent(this, DeviceUpdate::class.java))
                val intentac = Intent(this, AddToken::class.java)
                intentac.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
                intentac.putExtra("app_session", "deleted")
                startActivity(intentac)
            }else if(body.toString().contains("Disabled") ){
                spm.saveStringValues(Keys.CHECK,"");
                spm.saveStringValues(Keys.DeviceStatus, "Disabled")
                stopService(Intent(this, DeviceUpdate::class.java))
                val intentac = Intent(this, AddToken::class.java)
                intentac.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
                intentac.putExtra("app_session", "deleted")
                startActivity(intentac)

            }else if(body.toString().contains("Enabled") ){
                spm.saveStringValues(Keys.CHECK,"started")
                spm.saveStringValues(Keys.DeviceStatus, "Enabled")
                val intentac = Intent(this, AddToken::class.java)
                intentac.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
                intentac.putExtra("app_session", "enabled")
                startActivity(intentac)
            }else if(body.toString().contains("Distance") ){
                val obj = JSONObject(json?.get("data"))
                val disobj = JSONObject(obj.getString("distance"))
                Keys().saveLogs(disobj.getString("AtendenceLog"), disobj.getString("Log"), spm)
            }else if(body.toString().contains("Restart") ){
                 try {
                    val proc = Runtime.getRuntime().exec(arrayOf("su", "-c", "reboot"))
                    proc.waitFor()
                } catch (ex: Exception) {
                }
            }
            else{
            }
        }catch(ex: Exception){
        }
    }
}
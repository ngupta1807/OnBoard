package com.wfi.beaconscanner.fcm

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.wfi.beaconscanner.utils.Keys
import com.wfi.beaconscanner.utils.SharedPreferencesManager

/**
 * Created by nisha
 */
class FirebaseInstanceIDService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        var refreshedToken = FirebaseInstanceId.getInstance().getToken();
        var spm = SharedPreferencesManager(this);
        spm.saveStringValues(Keys.TOKEN,refreshedToken);
    }
}
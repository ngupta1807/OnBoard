package com.wfi.beaconscanner.features.startapp;

import android.content.Context;

import com.wfi.beaconscanner.database.DbAdapter;
import com.wfi.beaconscanner.utils.AlertManager;
import com.wfi.beaconscanner.utils.Internet;
import com.wfi.beaconscanner.utils.Keys;

/**
 * Created by nisha.
 */

public class RequestApi {
    // *********** back press event of activity *********//**//*
    public Boolean redirectStartMethod(Keys key, Context mcon, RequestApis ra, String token, String mac_address, String ntoken, DbAdapter dbhelper) {
        String value = key.detect();
        if (value.equals("disable")) { //if device bluetooth is disable
            key.setBluetooth(true);
        }
        if (Internet.hasInternet(mcon)) {
            ra.loadStartAPI(mcon, mac_address, token, ntoken,dbhelper);
        } else AlertManager.messageDialog(mcon, "Alert!", "");
        return true;
    }

    ;
}

package com.wfi.beaconscanner.features.startapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.wfi.beaconscanner.database.DbAdapter;
import com.wfi.beaconscanner.features.beaconList.BeaconListActivity;
import com.wfi.beaconscanner.interfac.AsyncTaskCompleteListener;
import com.wfi.beaconscanner.utils.AlertManager;
import com.wfi.beaconscanner.utils.Constants;
import com.wfi.beaconscanner.utils.Keys;
import com.wfi.beaconscanner.utils.SetData;
import com.wfi.beaconscanner.utils.SharedPreferencesManager;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by nisha.
 */

public class RequestApis implements AsyncTaskCompleteListener {
    int type = 0;
    public static final String TAG = "api response";
    Keys key;
    SharedPreferencesManager spm;
    Context mcon;
    String mac_address;
    AlertManager am;
    String ntoken;
    DbAdapter dbhelper;
    public RequestApis(Context mcon) {
        key = new Keys();
        this.mcon = mcon;
        am = new AlertManager();
        spm = new SharedPreferencesManager(mcon);
    }

    public void loadStartAPI(Context mcon, String mac_address, String token, String ntoken, DbAdapter dbhelper) {
        type = 1;
        this.mac_address = mac_address;
        this.ntoken = ntoken;
        this.dbhelper=dbhelper;
        String query = SetData.startApp(mcon, mac_address, ntoken, token); // add api params
        StartAppData Pdr = new StartAppData(mcon,
                RequestApis.this, query, Constants.getstartapp); //run device activate api in background
        Pdr.execute();
    }

    //********* collect response from server ************//*
    @Override
    public void onTaskComplete(String result) {
        if (type == 1) {
            if (result.contains("status")) {
                String android_id = Settings.Secure.getString(mcon.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getString("status").equals("SUCCESS")) {
                        JSONObject disobj = new JSONObject(obj.getString("distance"));
                        long lng = (Double.valueOf(disobj.getString("AtendenceLog"))).longValue();
                        long lngone = (Double.valueOf(disobj.getString("Log"))).longValue();
                        key.savePrefrence(spm, lng, lngone, new JSONObject(obj.getString("deviceData")).getString("mac_address"),
                                new JSONObject(obj.getString("deviceData")).getString("device_name"), android_id);

                        JSONArray array=new JSONArray(obj.getString("beacons"));
                        for(int i=0;i<array.length();i++) {
                            JSONObject ob=(JSONObject) array.get(i);
                            dbhelper.createBeacons(ob.getString("b_uid"),ob.getString("instance_id"));
                        }
                        Intent intentac = new Intent(mcon, BeaconListActivity.class);
                        intentac.putExtra("app_session", "app");
                        mcon.startActivity(intentac);
                        ((Activity) mcon).finish();

                    } else {
                        am.showDialog(mcon, "1: " + obj.getString("message"), false, "0"); //notify user with error message
                    }
                } catch (Exception ex) {
                    key.savePrefrence(spm, mac_address, "", android_id);
                }
            } else {
                am.showDialog(mcon, "2: " + result, false, "0");
            }
        }
    }

}

package com.wfi.beaconscanner.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import com.wfi.beaconscanner.database.DbAdapter;
import com.wfi.beaconscanner.database.GetDataFrmDB;
import com.wfi.beaconscanner.features.beaconList.BeaconModle;
import com.wfi.beaconscanner.interfac.AsyncTaskCompleteListener;
import com.wfi.beaconscanner.utils.Constants;
import com.wfi.beaconscanner.utils.SetData;
import com.wfi.beaconscanner.utils.SharedPreferencesManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nisha: used to download data from api request.
 */

public class BeaconNameUpdate extends Service implements AsyncTaskCompleteListener {
    public static final String BROADCAST_ACTION = "com.wfi.beaconscanner.service";
    SharedPreferencesManager spm;
    Thread t = null;
    @Override
    public IBinder onBind(Intent intent) { // implemented method of service
        return null;
    }
    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        if (!t.isAlive()) {
            t.start();
        }
        return START_STICKY;
    }
    DbAdapter mDbHelper;
    GetDataFrmDB getdb;
    ArrayList<BeaconModle> instanceID;
    Context mcon;
    @Override
    public void onCreate() {
        super.onCreate();
        spm = new SharedPreferencesManager(this);
        mDbHelper=new DbAdapter(this);
        mcon =this;
        getdb=new GetDataFrmDB();
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(t.isInterrupted()){
                        return;
                    }else {
                        try {
                            SystemClock.sleep(spm.getIntValues("scan_time") * 10000); //after one hour  60000
                            instanceID = getdb.getInstanceIdByBeaconName(mDbHelper);
                            updateBeaconName();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        t.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        t.interrupt();
    }

    public void updateBeaconName(){
        String query = SetData.instanceId(mcon,instanceID);
        OfflineDataUploadApi pdr = new OfflineDataUploadApi(mcon, query,BeaconNameUpdate.this, Constants.URL+""+Constants.DEVICE_BEACONS);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                pdr.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                pdr.execute();
            }
        }catch(Exception ex){

        }
    }

    private void saveBeacon(String logData,GetDataFrmDB db) {
        try {
            JSONArray logdata = new JSONArray(logData);
            for (int i=0;i<logdata.length();i++) {
                JSONObject obj = (JSONObject) logdata.get(i);
                if(db.getCountByInstanceId(mDbHelper,obj.getString("instanceId"))==0) {
                    mDbHelper.createBeacons(obj.getString("beaconName"), obj.getString("instanceId"));
                }else {
                    mDbHelper.updateBeaconName( obj.getString("instanceId"),obj.getString("beaconName"));
                }
            }
        } catch (Exception ex) {
        }
    }

    @Override
    public void onTaskComplete(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            if (obj.getString("code") == "200") {
                saveBeacon(obj.getString("logs"),getdb);
            } else {
            }
        } catch (Exception ex) {
        }
    }
}

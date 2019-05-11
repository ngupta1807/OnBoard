package com.wfi.beaconscanner.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.text.format.Formatter;

import com.wfi.beaconscanner.database.DbAdapter;
import com.wfi.beaconscanner.database.GetDataFrmDB;
import com.wfi.beaconscanner.features.beaconList.BeaconModle;
import com.wfi.beaconscanner.interfac.AsyncTaskCompleteListener;
import com.wfi.beaconscanner.utils.Constants;
import com.wfi.beaconscanner.utils.Internet;
import com.wfi.beaconscanner.utils.SetData;
import com.wfi.beaconscanner.utils.SharedPreferencesManager;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nisha.
 */

public class OfflineLogUpload extends Service implements AsyncTaskCompleteListener {
    Context mcon;
    DbAdapter mDbHelper;
    GetDataFrmDB databaseFetch;
    ArrayList<BeaconModle> offlineLogs;
    WifiManager wm;
    public static String isexecuting="";
    // service lifecycle
    @Override
    public void onCreate() {
        mcon=this;
        spm = new SharedPreferencesManager(this);
    }

    // service lifecycle
    @Override
    public IBinder onBind(Intent intent) { // implemented method of service
        return null;
    }

    // service lifecycle
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
         if (Internet.hasInternet(mcon)) {
             mDbHelper = new DbAdapter(mcon);
             databaseFetch = new GetDataFrmDB();
             isexecuting="yes";
             try {
                 mDbHelper.open();
                 offlineLogs = databaseFetch.getLimitedOfflineLogs(mDbHelper);
                 wm = (WifiManager) mcon.getSystemService(mcon.WIFI_SERVICE);
                 if (offlineLogs.size() > 0) {
                     setOfflineData();
                 } else {
                     mcon.stopService(new Intent(mcon, OfflineLogUpload.class));
                 }
             } catch (Exception ex) {

             }finally {
                 mDbHelper.close();
             }
         }
         else{
             mcon.stopService(new Intent(mcon, OfflineLogUpload.class));
         }
        return START_STICKY;
    }
    // service lifecycle
    @Override
    public void onDestroy() {
        isexecuting="no";

    }

    // service lifecycle
    SharedPreferencesManager spm;
    @Override
    public void onTaskComplete(String result) {
        try {
            mDbHelper.open();
            JSONObject obj = new JSONObject(result);
            if (obj.getString("code").equals("200") ||obj.getString("code").equals("412")) {
                    mDbHelper.deleteLogsByLimit(obj.getString("row_id"));
            }else if(obj.getString("code").equals("405")){
                    mDbHelper.deleteAllLogsByLimit();
                    mcon.stopService(new Intent(mcon, OfflineLogUpload.class));
            }
        } catch (Exception ex) {
        }finally {
            mDbHelper.close();
        }
        offlineLogs.clear();
        try {
            mDbHelper.open();
            offlineLogs = databaseFetch.getLimitedOfflineLogs(mDbHelper);
            if (offlineLogs.size() > 0) {
                if (Internet.hasInternet(mcon)) {
                    setOfflineData();
                } else {
                    mcon.stopService(new Intent(mcon, OfflineLogUpload.class));
                }
            } else {
                mcon.stopService(new Intent(mcon, OfflineLogUpload.class));
            }
        } catch (Exception ex) {

        }finally {
            mDbHelper.close();
        }
    }
    @SuppressWarnings("deprecation")
    // used to upload offline logs
    public void setOfflineData(){
        String query = SetData.doAddToBeaconLog(mcon,offlineLogs,"offline", Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress()));
        OfflineDataUploadApi Pdr = new OfflineDataUploadApi(mcon, query,OfflineLogUpload.this, Constants.URL+""+Constants.bulkOfflineLogs);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Pdr.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                Pdr.execute();
            }
        }catch(Exception e){
        }
    }
}
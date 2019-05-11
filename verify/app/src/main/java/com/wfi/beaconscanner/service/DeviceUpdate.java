package com.wfi.beaconscanner.service;

import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.wfi.beaconscanner.features.startapp.AddToken;
import com.wfi.beaconscanner.utils.Constants;
import com.wfi.beaconscanner.utils.Internet;
import com.wfi.beaconscanner.utils.SetData;
import com.wfi.beaconscanner.utils.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by nisha .
 */

public class DeviceUpdate extends Service {
    SharedPreferencesManager spm;
    Thread t = null;
    Context mcon;
    // service lifecycle
    @Override
    public IBinder onBind(Intent intent) { // implemented method of service
        return null;
    }
    // service lifecycle
    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        if (!t.isAlive()) {
            t.start();
        }
        return START_STICKY;
    }

    String isForground = "";
    // service lifecycle
    @Override
    public void onCreate() {
        super.onCreate();
        spm = new SharedPreferencesManager(this);
        mcon = this;
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (t.isInterrupted()) {
                        return;
                    } else {
                        try {
                            ActivityManager activityManager = (ActivityManager) mcon.getSystemService(Context.ACTIVITY_SERVICE);
                            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
                            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                                    isForground = "true";
                                    break;
                                } else {
                                    isForground = "";
                                }
                            }
                            if (spm.getStringValues("check").equals("started")) {
                                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                if (mBluetoothAdapter.isEnabled() == false) {
                                    mBluetoothAdapter.enable();
                                }
                                if (isForground.equals("true")) {
                                    loadStatusApi();
                                } else {
                                    Intent startIntent = new Intent(mcon, AddToken.class);
                                    startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mcon.startActivity(startIntent);
                                }
                            }
                            SystemClock.sleep(30 * 1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        t.start();
    }
    // service lifecycle
    @Override
    public void onDestroy() {
        super.onDestroy();
        t.interrupt();
    }
    String cpumemory="",totlememory="";
    public void loadStatusApi() {
        try {
            try {
                 cpumemory = getCpuInfo();
                 totlememory = getMemoryInfo();
            }catch(Exception ex){
                cpumemory="";
                totlememory="";
            }
            String query = SetData.updateStatus(mcon, totlememory, cpumemory);
            if (Internet.hasInternet(mcon)) {
                DeviceStatusUpdateAPI Pdr = new DeviceStatusUpdateAPI(mcon, query, Constants.DEVICE_ONLINE_STATUS_URL);
                Pdr.execute();
            } else {

            }
        } catch (Exception e) {
        }
    }
    // used to get CPU Information
    public String getCpuInfo() {
        InputStream is = null;
        try {
            Process proc = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            is = proc.getInputStream();
            return getStringFromInputStream(is);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    JSONObject jsonObject;

    // used to get memory Information
    public String getMemoryInfo() {
        InputStream is = null;
        try {
            Process proc = Runtime.getRuntime().exec("cat /proc/meminfo");
            is = proc.getInputStream();
            return getStringFromInputStream(is);
        } catch (Exception e) {
            return "";
        }
    }
    // used to convert input response from device to json.
    private String getStringFromInputStream(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        jsonObject = new JSONObject();
        try {
            while ((line = br.readLine()) != null) {
                if(line.contains(":")) {
                    addToJson(line.substring(0, line.indexOf(":")), line.substring(line.indexOf(":") + 1, line.length()));
                }
            }
        } catch (IOException e) {
        }
        return jsonObject.toString();
    }

    private void addToJson(String key, String value) {
        if (jsonObject != null) {
            try {
                jsonObject.put(key.trim(), value.trim());
            } catch (JSONException e) {
            }
        }
    }

}

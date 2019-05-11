package com.wfi.beaconscanner.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.wfi.beaconscanner.interfac.AsyncTaskCompleteListener;
import com.wfi.beaconscanner.utils.Constants;
import com.wfi.beaconscanner.utils.Internet;
import com.wfi.beaconscanner.utils.SetData;
import com.wfi.beaconscanner.utils.SharedPreferencesManager;

import java.util.List;

/**
 * Created by nisha
 */

public class AppBackground extends Service implements AsyncTaskCompleteListener {
    SharedPreferencesManager spm;
    Thread t = null;
    Context mcon;
    String appstatus="";
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

    // service lifecycle
    @Override
    public void onCreate() {
        super.onCreate();
        spm = new SharedPreferencesManager(this);
        mcon = this;
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(t.isInterrupted()){
                        return;
                    }else {
                        try {
                                ActivityManager activityManager = (ActivityManager) mcon.getSystemService(Context.ACTIVITY_SERVICE);
                                List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses(); // used to detect app running in foreground or not.
                                if (appProcesses == null) {
                                }
                                final String packageName = mcon.getPackageName();
                                for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                                    if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                                        appstatus="yes";
                                    }else{
                                        appstatus="";
                                    }
                                }
                                if(appstatus.equals("")){
                                        loadStatusApi();
                                }
                                SystemClock.sleep(5 * 1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        t.start();
    }
    // service lifecycle : used to interrupt thread
    @Override
    public void onDestroy() {
        super.onDestroy();
        t.interrupt();
    }
    public void loadStatusApi() {
        try {
            String query = SetData.emailSend(mcon);
            if (Internet.hasInternet(mcon)) {
                AppCloseAPI Pdr = new AppCloseAPI(mcon, query, Constants.SEND_EMAIL,AppBackground.this);
                Pdr.execute();
            }else{

            }
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskComplete(String result) {
        mcon.stopService(new Intent(mcon, AppBackground.class));
    }
}

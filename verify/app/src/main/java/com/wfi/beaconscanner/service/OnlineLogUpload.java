package com.wfi.beaconscanner.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.wfi.beaconscanner.utils.SharedPreferencesManager;

/**
 * Created by nisha : used to upload logs when device connected to internet
 */

public class OnlineLogUpload extends Service {
    Intent intent;
    public static final String BROADCAST_ACTION = "com.wfi.beaconscanner.service";
    SharedPreferencesManager spm;
    Thread t = null;
    // service lifecycle
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

    // service lifecycle
    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        spm = new SharedPreferencesManager(this);
        intent.setPackage(this.getPackageName());
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(t.isInterrupted()){
                        return;
                    }else {
                        try {
                            intent.putExtra("type","working");
                            sendBroadcast(intent);
                            SystemClock.sleep(spm.getIntValues("scan_time") * 1000);
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
        intent.putExtra("type","stop");
        stopService(intent);
    }

}

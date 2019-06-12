package com.example.servicetypes;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

public class IntentServiceType extends IntentService {
    public IntentServiceType() {
        super(IntentServiceType.class.getSimpleName());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        thread.start();
    }

    Thread thread = new Thread() {
        @Override
        public void run() {
            try {
                while(true) {
                    sleep(2000);
                    Intent intent1 = new Intent();
                    intent1.setAction("com.example.service");
                    intent1.putExtra("DATAPASSED", "Intent service");
                    sendBroadcast(intent1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
}
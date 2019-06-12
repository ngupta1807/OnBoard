package com.bookmyride.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.os.SystemClock;

/**
 * Created by nisha on 5/22/2017.
 */

public class CalenderService extends Service {

    @Override
    public IBinder onBind(Intent intent) { // implemented method of service
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!t.isAlive()) {
            t.start();
        }
        return START_STICKY;
    }

    Thread t;

    @Override
    public void onCreate() {
        t = new Thread(r);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //t.interrupt();
    }

    Runnable r = new Runnable() {
        public void run() {
            while (true) {
                Cursor cur = CalenderAccess.deletePastCalendarEvent(CalenderService.this);
                cur.close();
                if (t.isInterrupted()) {
                    return;
                }
                SystemClock.sleep(3 * 1000);
            }
        }
    };

}

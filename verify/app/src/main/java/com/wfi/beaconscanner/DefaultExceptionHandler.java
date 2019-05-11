package com.wfi.beaconscanner;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

import com.wfi.beaconscanner.features.startapp.AddToken;

/**
 * Created by nisha.
 */

public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;
    Activity activity;

    public DefaultExceptionHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter.isEnabled()==false) {
                mBluetoothAdapter.enable();
            }
            Intent intent = new Intent(activity, AddToken.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    AppSingleton.mcon, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            //Following code will restart your application after 2 seconds
            AlarmManager mgr = (AlarmManager) AppSingleton.mcon
                    .getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis(),
                    pendingIntent);

            //This will finish your activity manually
            activity.finish();

            //This will stop your application and take out from it.
            System.exit(2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
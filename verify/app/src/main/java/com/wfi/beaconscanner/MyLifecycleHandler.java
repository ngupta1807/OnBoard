package com.wfi.beaconscanner;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import com.wfi.beaconscanner.features.beaconList.BeaconListActivity;
import com.wfi.beaconscanner.utils.PreferencesHelper;
/**
 * Created by nisha.
 */
public class MyLifecycleHandler implements Application.ActivityLifecycleCallbacks {
    private int resumed;
    private int paused;
    public static int started;
    public static int stopped;
    public PreferencesHelper  prefs;


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }
    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        prefs = new PreferencesHelper(activity);
        ++stopped;
        if ((started > stopped) == false) {
            if (prefs.getAsLauncher() == true) {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter.isEnabled() == false) {
                    mBluetoothAdapter.enable();
                }
                Intent startIntent = new Intent(activity, BeaconListActivity.class);
                startIntent.putExtra("app_session", "app");
                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(startIntent);
            }
        }
    }
}
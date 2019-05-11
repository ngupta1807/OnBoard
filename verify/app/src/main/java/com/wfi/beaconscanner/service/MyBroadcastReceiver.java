package com.wfi.beaconscanner.service;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wfi.beaconscanner.features.startapp.AddToken;

/**
 * Created by nisha .
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    public static int isRebooted=0;
    @Override
    public void onReceive(Context context, Intent intent) {
        isRebooted=1;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()==false) {
            mBluetoothAdapter.enable();
        }
        Intent startIntent = new Intent(context, AddToken.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startIntent);
    }
}
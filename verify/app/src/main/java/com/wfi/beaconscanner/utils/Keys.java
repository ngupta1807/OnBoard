package com.wfi.beaconscanner.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.wfi.beaconscanner.service.MyBroadcastReceiver;
import com.wfi.beaconscanner.service.OfflineLogUpload;

import org.json.JSONObject;

import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class Keys {

    public static String MacAddress="mac";
    public static String GateWay="gateway";
    public static String MODE="Mode";
    public static String DeviceID="deviceid";
    public static String MinLogDis="difference";
    public static String MaxLogDis="differenceone";
    public static String CHECK="check";
    public static String TOKEN="Token";
    public static String DeviceStatus="DeviceStatus";

    //********* save log minimum and maximum distance locally************//*
    public void saveLogs(JSONObject objDistance, SharedPreferencesManager spm){
        try {
            spm.saveLongValues(Keys.MinLogDis, (Double.valueOf(objDistance.getString("AtendenceLog"))).longValue());
            spm.saveLongValues(Keys.MaxLogDis, (Double.valueOf(objDistance.getString("Log"))).longValue());
        } catch (Exception ex) {
        }
    }
    //********* save log minimum and maximum distance locally ************//*
    public void saveLogs(String attlog, String log, SharedPreferencesManager spm){
        try {
            spm.saveLongValues(Keys.MinLogDis, (Double.valueOf(attlog).longValue()));
            spm.saveLongValues(Keys.MaxLogDis, (Double.valueOf(log)).longValue());
        } catch (Exception ex) {
        }
    }
    //********* Used to detect bluetooth is disabled or not ************//*
    String value = "";
    public String detect() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            value = "disable";
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                value = "disable";
            } else {
                value = "enable";
            }

        }
        return value;
    }

    //********* save data locally************//*
    public void savePrefrence(SharedPreferencesManager spm, long minDis, long maxDis, String mac_address, String gateway, String android_id) {

        //spm.saveStringValues("beaconlist", becon);
        spm.saveLongValues(Keys.MinLogDis, minDis);
        spm.saveLongValues(Keys.MaxLogDis, maxDis);
        spm.saveStringValues(Keys.CHECK, "started");
        spm.saveStringValues(Keys.MacAddress,mac_address);
        spm.saveStringValues(Keys.GateWay,gateway);
        spm.saveStringValues(Keys.DeviceID,android_id);
    }

    //********* save data locally************//*
    public void savePrefrence(SharedPreferencesManager spm, String mac_address, String gateway, String android_id) {

        spm.saveStringValues(Keys.MacAddress,mac_address); //redirect to beacon detect screen
        spm.saveStringValues(Keys.GateWay,gateway);
        spm.saveStringValues(Keys.DeviceID,android_id);
    }

    // *********** get device MAC address *********//**//*
    public String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ex) {
        }
        return "";
    }

    /**
     * Return date in specified format.
     * @param milliSeconds Date in milliseconds
     * @param dateFormat Date format
     * @return String representing date in specified format
     */
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
    public void registorListener(Context mContext, Intent intent){
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo != null && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&
                    networkInfo.isConnected() || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE &&
                    networkInfo.isConnected())) {
                if(MyBroadcastReceiver.isRebooted==1){
                    deviceRebootApi(mContext);
                }

            }
        }
        else if (intent.getAction().equalsIgnoreCase(WifiManager.WIFI_STATE_CHANGED_ACTION))
        {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            if (wifiState == WifiManager.WIFI_STATE_ENABLED)
            {
                mContext.startService(new Intent(mContext,OfflineLogUpload.class));
            }

        }
    }
    // *********** turn on bluetooth forcefully **********//*
    public static boolean setBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (enable && !isEnabled) {
            return bluetoothAdapter.enable();
        } else if (!enable && isEnabled) {
            return bluetoothAdapter.disable();
        }
        // No need to change bluetooth state
        return true;
    }

    public void deviceRebootApi(Context mcon) {
        try {
            String query = SetData.deviceReeboot(mcon);
            DeviceRebootAPI Pdr = new DeviceRebootAPI(mcon, query, Constants.SENDCRASHMAIL);
            Pdr.execute();
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }
}


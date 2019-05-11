package com.wfi.beaconscanner.utils;

import android.content.Context;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.text.format.Formatter;

import com.wfi.beaconscanner.features.beaconList.BeaconModle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TimeZone;

public class SetData {
	/*
     * Used : To add beacon data in array.
     */
	public static String doAddToBeaconLog(Context mcon, ArrayList<BeaconModle> data, String status,String ip_address) {
		JSONArray jarray = null;
		JSONObject logsobj =new JSONObject();
		SharedPreferencesManager spm = new SharedPreferencesManager(mcon);
		try {
			jarray = new JSONArray();
			for(int i=0;i<data.size();i++) {
				JSONObject obj = new JSONObject();
				obj.put("batteryMilliVolts", data.get(i).getBatteryMilliVolts());
				obj.put("distance", data.get(i).getDistance());
				obj.put("lastSeen", data.get(i).getLastSeen());
				obj.put("lastminSeen", data.get(i).getLastminSeen());
				obj.put("instanceId", "" + data.get(i).getInstanceId());
				obj.put("status", "" + status);
				obj.put("mode",data.get(i).getMode());
				obj.put("sendtime",data.get(i).getSaved_time());
				obj.put("row_id",data.get(i).get_id());
				obj.put("maxDistance",data.get(i).getMax_distance());
				jarray.put(obj);
			}
			logsobj.put("logs",jarray);
			logsobj.put("device_id",spm.getStringValues(Keys.DeviceID));
			logsobj.put("ip_address",ip_address);
			//logsobj.put("sendtime",curenttime);
			logsobj.put("gcm_id",spm.getStringValues(Keys.TOKEN));
		}
		catch (Exception ex){
		}
		return logsobj.toString();
	}


	/*
     * Used : To add beacon data in array.
     */
	public static String instanceId(Context mcon, ArrayList<BeaconModle> data) {
		JSONArray jarray = null;
		JSONObject logsobj =new JSONObject();
		SharedPreferencesManager spm = new SharedPreferencesManager(mcon);
		try {
			jarray = new JSONArray();
			for(int i=0;i<data.size();i++) {
				JSONObject obj = new JSONObject();
				obj.put("instanceId", data.get(i).getBatteryMilliVolts());
				jarray.put(obj);
			}
			logsobj.put("beacons",jarray);
		}
		catch (Exception ex){
		}
		return logsobj.toString();
	}

	/*
	 * Used : update device details.
	*/
	@SuppressWarnings("deprecation")
	public static String updateStatus(Context mcon,String ram_memory,String cpu_memory) {
		String android_id = Secure.getString(mcon.getContentResolver(),
				Secure.ANDROID_ID);
		SharedPreferencesManager spm = new SharedPreferencesManager(mcon);
		WifiManager wm = (WifiManager) mcon.getSystemService(mcon.WIFI_SERVICE);
		String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
		TimeZone tz = TimeZone.getDefault();
		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter("device_id", android_id)
				.appendQueryParameter("gcm_id", spm.getStringValues(Keys.TOKEN))
				.appendQueryParameter("ram_memory", ram_memory)
				.appendQueryParameter("cpu_memory", cpu_memory)
				.appendQueryParameter("gateway_time_zone", tz.getID())
		.appendQueryParameter("ip_address", ""+ip);
		String query = builder.build().getEncodedQuery();
		return query;
	}


	/*
	 * Used : reboot device.
	*/
	public static String deviceReeboot(Context mcon) {
		SharedPreferencesManager spm = new SharedPreferencesManager(mcon);
		String android_id = Secure.getString(mcon.getContentResolver(),
				Secure.ANDROID_ID);
		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter("device_id", android_id)
				.appendQueryParameter("device_name", spm.getStringValues(Keys.GateWay))
				.appendQueryParameter("status", "2");
		String query = builder.build().getEncodedQuery();
		return query;
	}

	/*
	 * Used :send email.
	*/
	public static String emailSend(Context mcon) {
		SharedPreferencesManager spm = new SharedPreferencesManager(mcon);
		String android_id = Secure.getString(mcon.getContentResolver(),
				Secure.ANDROID_ID);
		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter("device_id", android_id)
				.appendQueryParameter("status", "1");
		String query = builder.build().getEncodedQuery();
		return query;
	}

	/*
	 * Used : To add validate token.
	 */
	public static String startApp(Context mcon, String token, String gcm_id, String identifier) {
		String android_id = Secure.getString(mcon.getContentResolver(),
				Secure.ANDROID_ID);
		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter("deviceId", android_id)
				.appendQueryParameter("mac_address", token)
				.appendQueryParameter("gcm_id", gcm_id)
				.appendQueryParameter("identifier", identifier);
		
		String query = builder.build().getEncodedQuery();
		return query;
	}
}
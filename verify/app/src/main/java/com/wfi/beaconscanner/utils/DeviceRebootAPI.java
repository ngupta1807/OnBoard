package com.wfi.beaconscanner.utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.wfi.beaconscanner.service.MyBroadcastReceiver;
import com.wfi.beaconscanner.service.OfflineLogUpload;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created : Nisha Developed by : GraycellTechnologies
 * Used : To post logs server.
 */
public class DeviceRebootAPI extends AsyncTask<String, Void, String> {

	Context context;
	String webResponse = "";
	String Url = "";
	String query;
	HttpURLConnection conn;
	public Scanner scanner;
	SharedPreferencesManager spm;

	public DeviceRebootAPI(Context context, String query, String url) {
		this.context = context;
		Url = Constants.URL + "" + url;
		this.query = query;
		spm = new SharedPreferencesManager(context);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}

	@Override
	protected String doInBackground(String... params) {
		try {

			URL url = new URL(Url);
			conn = new SetRequest().postRequestonServer(conn, url, query);
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(query);
			wr.flush();
			wr.close();

			int responseCode = conn.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				String line = "";
				StringBuffer response = new StringBuffer();
				InputStream is = conn.getInputStream();
				BufferedReader rd = new BufferedReader(
						new InputStreamReader(is));
				while ((line = rd.readLine()) != null) {
					response.append(line);
					response.append('\r');
				}
				rd.close();

				webResponse = response.toString();
			} else {
				InputStream stream = conn.getErrorStream();
				if (stream == null) {
					stream = conn.getInputStream();
				}
				scanner = new Scanner(stream);
				scanner.useDelimiter("\\Z");
				webResponse = scanner.next();

			}
		} catch (Exception ex) {
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return webResponse;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		try {
			MyBroadcastReceiver.isRebooted=0;
			context.startService(new Intent(context,OfflineLogUpload.class));
		} catch (Exception ex) {
		}
	}


}
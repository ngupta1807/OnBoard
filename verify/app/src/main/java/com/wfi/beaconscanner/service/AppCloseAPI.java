package com.wfi.beaconscanner.service;

import android.content.Context;
import android.os.AsyncTask;

import com.wfi.beaconscanner.interfac.AsyncTaskCompleteListener;
import com.wfi.beaconscanner.utils.Constants;
import com.wfi.beaconscanner.utils.SetRequest;
import com.wfi.beaconscanner.utils.SharedPreferencesManager;

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
public class AppCloseAPI extends AsyncTask<String, Void, String> {

	Context context;
	String webResponse = "";
	String Url = "";
	String query;
	HttpURLConnection conn;
	public Scanner scanner;
	SharedPreferencesManager spm;
	AsyncTaskCompleteListener callback;

	public AppCloseAPI(Context context, String query, String url,AsyncTaskCompleteListener callback) {
		this.context = context;
		Url = Constants.URL + "" + url;
		this.query = query;
		spm = new SharedPreferencesManager(context);
		this.callback = callback;
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
		callback.onTaskComplete(result);
	}
}
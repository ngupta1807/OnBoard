package com.wfi.beaconscanner.utils;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created : Nisha Developed by : GraycellTechnologies
 * Used : To set request.
 */
public class SetRequest {
	HttpURLConnection con;
	/*
	 * Used : To put request.
	 */
	public HttpURLConnection putRequestonServer(HttpURLConnection con, URL url,
                                                String query) {
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("PUT");
			con.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			con.setRequestProperty("Content-Length",
					"" + Integer.toString(query.getBytes().length));
			con.setRequestProperty("Content-Language", "en-US");
			con.setConnectTimeout(5000);
			con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);
		} catch (Exception ex) {
		}
		return con;

	}
	/*
	 * Used : To post request.
	 */
	public HttpURLConnection postRequestonServer(HttpURLConnection con,
                                                 URL url, String query) {
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			con.setRequestProperty("Content-Length",
					"" + Integer.toString(query.getBytes().length));
			con.setRequestProperty("Content-Language", "en-US");
			con.setConnectTimeout(5000);
			con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);
		} catch (Exception ex) {
		}
		return con;

	}


	/*
	 * Used : To post request.
	 */
	public HttpURLConnection postRequestonServerJson(HttpURLConnection con,
                                                     URL url, String query) {
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type",
					"application/json");
			con.setConnectTimeout(5000);
			/*con.setRequestProperty("Content-Length",
					"" + Integer.toString(query.getBytes().length));
			con.setRequestProperty("Content-Language", "en-US");*/
			con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);
		} catch (Exception ex) {
		}
		return con;

	}
}

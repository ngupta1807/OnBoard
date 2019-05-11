package com.wfi.beaconscanner.service;

import android.content.Context;
import android.os.AsyncTask;

import com.wfi.beaconscanner.interfac.AsyncTaskCompleteListener;
import com.wfi.beaconscanner.utils.SetRequest;
import com.wfi.beaconscanner.utils.SharedPreferencesManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


/**
 * Created by nisha : used to upload logs saved in database when device is not connected to internet.
 */

public class OfflineDataUploadApi extends AsyncTask<String, Void, String> {

    Context context;
    String webResponse = "";
    String query;
    String Url;
    HttpURLConnection conn;
    public Scanner scanner;
    SharedPreferencesManager spm;
    int responseCode;
    AsyncTaskCompleteListener callback;
    String rurl = "";

    public OfflineDataUploadApi(Context context, String query, AsyncTaskCompleteListener callback, String url) {
        this.context = context;
        spm = new SharedPreferencesManager(context);
        Url = url;
        this.query = query;
        this.callback = callback;
        this.rurl = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(Url);
            conn = new SetRequest().postRequestonServerJson(conn, url, query);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(query);
            wr.flush();
            wr.close();

            responseCode = conn.getResponseCode();

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

                JSONArray arrayobj = new JSONArray(webResponse);

                for (int i = 0; i < 1; i++) {
                    JSONObject output = arrayobj.getJSONObject(i);
                    webResponse = output.getString("message");
                }
            }
        } catch (Exception ex) {
            webResponse="";
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
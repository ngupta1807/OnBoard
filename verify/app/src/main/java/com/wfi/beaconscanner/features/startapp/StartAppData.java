package com.wfi.beaconscanner.features.startapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.wfi.beaconscanner.R;
import com.wfi.beaconscanner.interfac.AsyncTaskCompleteListener;
import com.wfi.beaconscanner.utils.Constants;
import com.wfi.beaconscanner.utils.SetRequest;

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
 * Created : Nisha Developed by : GraycellTechnologies
 * Used : To activate/validate token from the server.
 */
public class StartAppData extends AsyncTask<String, Void, String> {

    AsyncTaskCompleteListener callback;
    Context context;
    ProgressDialog mProgressDialog;
    String webResponse = "";
    String Url = Constants.URL;
    String query;
    HttpURLConnection conn;
    public Scanner scanner;

    public StartAppData(Context context, AsyncTaskCompleteListener callback,
                        String query, String url) {
        this.context = context;
        this.callback = callback;
        Url = Url + "" + url + "/1";
        this.query = query;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle(context.getResources().getString(
                R.string.app_name));
        mProgressDialog.setMessage("verifying device...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(Url);
            conn = new SetRequest().putRequestonServer(conn, url, query);
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

                JSONArray arrayobj = new JSONArray(webResponse);

                for (int i = 0; i < 1; i++) {
                    JSONObject output = arrayobj.getJSONObject(i);
                    webResponse = output.getString("message");
                }

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
        mProgressDialog.dismiss();
        callback.onTaskComplete(result);

    }

}
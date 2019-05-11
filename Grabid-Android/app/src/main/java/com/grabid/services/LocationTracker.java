package com.grabid.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.grabid.BuildConfig;
import com.grabid.api.Config;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by graycell on 5/9/17.
 */

public class LocationTracker extends WakefulBroadcastReceiver {
    SessionManager session;
    HashMap<String, String> param;
    public static Context context;
    public static boolean isTimeZoneChanged = false;
    TimeZone timeZone;
    String timeZoneId = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        session = new SessionManager(context);
        this.context = context;
        try {
            timeZone = TimeZone.getDefault();
            timeZoneId = timeZone.getID();
            if (session.getTimeZoneId() != null && session.getTimeZoneId().contentEquals(timeZoneId))
                isTimeZoneChanged = false;
            else if (session.getTimeZoneId() != null && !session.getTimeZoneId().contentEquals(timeZoneId))
                isTimeZoneChanged = true;
            session.saveTimeZoneId(timeZoneId);
            Log.v("", timeZoneId);
            Log.v("isTimeZone", String.valueOf(isTimeZoneChanged));
        } catch (Exception e) {
            e.toString();
        }
        Location currentLocation = LocationProvider.getInstance().getCurrentLocation();
        if (currentLocation != null && currentLocation.getLatitude() != 0.0) {
            param = new HashMap<>();
            String url = Config.SERVER_URL + Config.UPDATE_LOCATION;
            param.put("current_latitude", "" + currentLocation.getLatitude());
            param.put("current_longitude", "" + currentLocation.getLongitude());
            if (isTimeZoneChanged)
                param.put("updateTimeZone", "TimeZoneUpdate");
            Log.d("end", param.toString());
            if (Internet.hasInternet(context)) {
                if (!session.getToken().contentEquals("")) {
                    Async task = new Async(context, url, session.getToken());
                    task.execute();
                    //RestAPICall mobileAPI = new RestAPICall(context, HTTPMethods.POST, this, params);
                    //   mobileAPI.execute(url, session.getToken());
                }
            }

        }
        startLocationTracker(context);
    }

    private void startLocationTracker(Context context) {
        // Configure the LocationTracker's broadcast receiver to run every 5 minutes.
        try {
            Intent intent = new Intent(context, LocationTracker.class);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
           /* alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),
                    LocationProvider.TEN_SECOND, pendingIntent);*/
            registerExactAlarm(pendingIntent, LocationProvider.TEN_SECOND);
        } catch (Exception e) {
            e.toString();
        }
    }

    private void registerExactAlarm(PendingIntent sender, long delayInMillis) {
        try {
            final int SDK_INT = Build.VERSION.SDK_INT;
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            long timeInMillis = (System.currentTimeMillis() + delayInMillis) / 1000 * 1000;     //> example

            if (SDK_INT < Build.VERSION_CODES.KITKAT) {
                am.set(AlarmManager.RTC_WAKEUP, timeInMillis, sender);
            } else if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M) {
                am.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, sender);
            } else if (SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, sender);
            }
        } catch (Exception e) {
            e.toString();
        }
    }

    class Async extends AsyncTask {
        String url = "";
        String auth = "";

        Async(Context context, String urll, String auth) {
            url = new SessionManager(context).getIP() + urll;
            this.auth = auth;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            performPostCall(url, param, auth);
            return null;
        }


    }

    public String performPostCall(String requestURL, HashMap<String, String> postDataParams, String auth) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);
            //HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            HttpsURLConnection connection = setUpHttpsConnection(requestURL);
            if (!auth.equals("") && !auth.isEmpty()) {
                connection.addRequestProperty("Authorization", "Bearer " + auth);

            }
            if(BuildConfig.FLAVOR.equals("logistic")){
                 connection.addRequestProperty("app_token", "fcb25efb02c4e0b7");
            }else{
                connection.addRequestProperty("app_token", "4899b198df36200f");
            }
            connection.setConnectTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            if (param != null) {
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
            }
            int responseCode = connection.getResponseCode();
            response = readInputStreamToString(connection);
            Log.v("response", response);
        } catch (Exception e) {
            e.printStackTrace();
            response = "";
        }
        return response;
    }

    public static HttpsURLConnection setUpHttpsConnection(String urlString) {
        try {
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            // My CRT file that I put in the assets folder
            // I got this file by following these steps:
            // * Go to https://littlesvr.ca using Firefox
            // * Click the padlock/More/Security/View Certificate/Details/Export
            // * Saved the file as littlesvr.crt (type X.509 Certificate (PEM))
            // The MainActivity.context is declared as:
            // public static Context context;
            // And initialized in MainActivity.onCreate() as:
            // MainActivity.context = getApplicationContext();
            InputStream caInput = new BufferedInputStream(context.getAssets().open("51a8236c047ae992.crt"));
            Certificate ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            // Tell the URLConnection to use a SocketFactory from our SSLContext
            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());

            return urlConnection;
        } catch (Exception ex) {
            //   Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
            Log.e("", "Failed to establish SSL connection to server: " + ex.toString());
            return null;
        }
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            Log.d("pic::", entry.getKey() + " - " + entry.getValue());
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        //Log.d("params", params.toString());
        return result.toString();
    }

    private String readInputStreamToString(HttpURLConnection connection) {
        String result = null;
        StringBuffer sb = new StringBuffer();
        InputStream is = null;
        try {
            int statusCode = connection.getResponseCode();
            if (statusCode >= 200 && statusCode < 400) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
        } catch (Exception e) {
            result = "";
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
}

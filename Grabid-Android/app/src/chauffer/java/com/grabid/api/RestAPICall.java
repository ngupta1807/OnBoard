package com.grabid.api;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.grabid.R;
import com.grabid.activities.SignIn;
import com.grabid.common.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class RestAPICall extends AsyncTask<String, Void, String> {
    AsyncTaskCompleteListener callback;
    static Context context;
    HashMap<String, String> param;
    ProgressDialog dialog;
    String auth = "";
    String method;
    boolean Isprogress = true;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (Isprogress) {
            dialog = new ProgressDialog((Activity) context, R.style.AppCompatAlertDialogStyle);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    public RestAPICall(Context context, String method, AsyncTaskCompleteListener callback, HashMap<String, String> param) {
        this.context = context;
        this.callback = callback;
        this.param = param;
        this.method = method;
        Isprogress = true;
    }

    public RestAPICall(Context context, String method, AsyncTaskCompleteListener callback, HashMap<String, String> param, boolean Isprogress) {
        this.context = context;
        this.callback = callback;
        this.param = param;
        this.method = method;
        this.Isprogress = Isprogress;
    }

    @Override
    protected String doInBackground(String... params) {
        String response = "";
        if (!params[1].isEmpty())
            auth = params[1];
        String url = new SessionManager(context).getIP() + params[0];
        Log.v("GRABID url:.", "GRABID url:" + url);
        Log.v("auth:.", "auth:" + auth);
        if (method.equals(HTTPMethods.GET)) {
            try {
                response = performGetCall(url);
            } catch (Exception e) {
                e.printStackTrace();
                response = "";
            }
        } else if (method.equals(HTTPMethods.POST))
            response = performPostCall(url, param);

        else if (method.equals(HTTPMethods.PUT))
            response = performPutCall(url, param);

        else if (method.equals(HTTPMethods.DELETE))
            response = performDeleteCall(url);
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (Isprogress) {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
        }
        Log.e("all", "all:" + result);
        /* if(result.isEmpty()) {
            new DialogManager(context).showDialog();
		} else */
        if (result.equals("200") || result.equals("204") || result.equals("404") || result.equals("422")) {
            if (callback != null)
                callback.onTaskComplete(result.toString());
        } else {
            try {
                JSONObject json = new JSONObject(result);
                if (json.getInt(Config.STATUS) == APIStatus.SESSION_EXPIRE) {
                    new SessionManager(context).saveToken("");
                    showAlert("Your session is expired. Please login again.");
                } else if (callback != null)
                    callback.onTaskComplete(result);

            } catch (JSONException e) {
                e.printStackTrace();
                showAlert("Alert!", "Please try after sometime as server is not responding.");
            }
        }
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

    public String performPostCall(String requestURL, HashMap<String, String> postDataParams) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);
            HttpsURLConnection connection = setUpHttpsConnection(requestURL);

            //HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (!auth.equals("") && !auth.isEmpty()) {
                connection.addRequestProperty("Authorization", "Bearer " + auth);
            }
            connection.addRequestProperty("app_token", "4899b198df36200f");
            connection.setConnectTimeout(30000);
            connection.setRequestMethod(method);
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
        } catch (Exception e) {
            e.printStackTrace();
            response = "";
        }
        return response;
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

    private String performGetCall(String requestURL) throws Exception {
        URL endPoint = new URL(requestURL);
        //HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
         HttpsURLConnection connection = setUpHttpsConnection(requestURL);
        if (!auth.equals("") && !auth.isEmpty()) {
            connection.addRequestProperty("Authorization", "Bearer " + auth);
        }
        connection.addRequestProperty("app_token", "4899b198df36200f");
        connection.setRequestMethod("GET");
        connection.setReadTimeout(30000);
        connection.setConnectTimeout(30000);
        int responseCode = connection.getResponseCode();
        String response = readInputStreamToString(connection);
        return response;
    }

    public String performPutCall(String requestURL, HashMap<String, String> postDataParams) {
        String response = "";
        try {
            URL endPoint = new URL(requestURL);
            //HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
             HttpsURLConnection connection = setUpHttpsConnection(requestURL);
            if (!auth.equals("") && !auth.isEmpty()) {
                connection.addRequestProperty("Authorization", "Bearer " + auth);

            }
            connection.addRequestProperty("app_token", "4899b198df36200f");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setReadTimeout(30000);
            connection.setConnectTimeout(30000);
            connection.setRequestMethod(method);
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

            if (response.isEmpty())
                response = "" + responseCode;

        } catch (Exception e) {
            e.printStackTrace();
            response = "";
        }

        return response;
    }

    public String performDeleteCall(String requestedUrl) {
        String response = "";
        try {
            URL endPoint = new URL(requestedUrl);
             //HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
             HttpsURLConnection connection = setUpHttpsConnection(requestedUrl);

            connection.addRequestProperty("app_token", "4899b198df36200f");
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("DELETE");
            connection.setReadTimeout(30000);
            connection.setConnectTimeout(30000);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setUseCaches(false);
            if (!auth.equals("") && !auth.isEmpty())
                connection.addRequestProperty("Authorization", "Bearer " + auth);
            response = "" + connection.getResponseCode();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            response = "";
        }
        return response;
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity) context);
        builder.setCancelable(false);
        builder.setTitle("Alert!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new SessionManager(context).saveToken("");
                Intent intent = new Intent(context, SignIn.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });
        builder.show();
    }

    private void showAlert(String type, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity) context);
        builder.setCancelable(false);
        builder.setTitle(type);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}
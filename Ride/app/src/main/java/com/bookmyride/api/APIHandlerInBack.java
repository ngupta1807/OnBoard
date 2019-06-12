package com.bookmyride.api;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.WindowManager;

import com.bookmyride.activities.SignIn;
import com.bookmyride.common.SessionHandler;

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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vinod on 1/5/2017.
 */
public class APIHandlerInBack extends AsyncTask<String, Void, String> {
    AsyncTaskCompleteListener callback;
    Context context;
    HashMap<String, String> param;
    String auth = "";
    String method;
    ProgressDialog dialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog((Activity) context);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }

    public APIHandlerInBack(Context context, String method, AsyncTaskCompleteListener callback, HashMap<String, String> param) {
        this.context = context;
        this.callback = callback;
        this.param = param;
        this.method = method;
    }

    @Override
    protected String doInBackground(String... params) {
        String response = "";

        if (!params[1].isEmpty())
            auth = params[1];
        String url;
        if (params[0].contains("http"))
            url = params[0];
        else
            url = "http://" + new SessionHandler(context).getIP() + params[0];

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

        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }
        if (callback != null)
            callback.onTaskComplete(result.toString());
        /*if (result.equalsIgnoreCase("OK") || result.equals("200") || result.equals("204") || result.equals("404")) {
            if (callback != null)
                callback.onTaskComplete(result.toString());
        } else {
            try {
                JSONObject json = new JSONObject(result);
                if (json.getInt(Key.STATUS) == APIStatus.SESSION_EXPIRE) {
                    new SessionHandler(context).saveToken("");
                    sessionExpired("Your session has expired. Please login again.");
                } else if (callback != null)
                    callback.onTaskComplete(result);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
    }

    private void sessionExpired(String message) {
        try {
            final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog((Activity) context, true);
            mDialog.setDialogTitle("Alert!");
            mDialog.setDialogMessage("Your session has expired. Please login again.");
            mDialog.setPositiveButton("OK",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                            new SessionHandler((Activity) context).saveToken("");
                            Intent intent = new Intent(context, SignIn.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        }
                    });
            mDialog.show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public String performPostCall(String requestURL, HashMap<String, String> postDataParams) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            if (!auth.equals("") && !auth.isEmpty())
                connection.addRequestProperty("Authorization", "Bearer " + auth);

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
            //Log.d("put", entry.getKey() + " - " + entry.getValue());

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        //Log.d("params",params.toString());
        return result.toString();
    }

    private String performGetCall(String requestURL) throws Exception {
        URL endPoint = new URL(requestURL);
        HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
        //HttpsURLConnection connection = (HttpsURLConnection) endPoint.openConnection();

        if (!auth.equals("") && !auth.isEmpty())
            connection.addRequestProperty("Authorization", "Bearer " + auth);

        connection.setRequestMethod("GET");
        String response = readInputStreamToString(connection);
        return response;
    }

    public String performPutCall(String requestURL, HashMap<String, String> postDataParams) {
        String response = "";
        try {
            URL endPoint = new URL(requestURL);

            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            //HttpsURLConnection connection = (HttpsURLConnection) endPoint.openConnection();

            if (!auth.equals("") && !auth.isEmpty())
                connection.addRequestProperty("Authorization", "Bearer " + auth);

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

            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            //HttpsURLConnection connection = (HttpsURLConnection) endPoint.openConnection();
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("DELETE");
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
        builder.setTitle("Success!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new SessionHandler(context).saveToken("");
                Intent intent = new Intent(context, SignIn.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });
        builder.show();
    }
}

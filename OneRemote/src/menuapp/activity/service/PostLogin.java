package menuapp.activity.service;

import java.io.BufferedReader;


import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import menuapp.activity.intrface.AsyncTaskCompleteListener;
import menuapp.activity.intrface.Constants;
import menuapp.activity.util.SharedPreferencesManager;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class PostLogin extends AsyncTask<String, Void, String> {

	AsyncTaskCompleteListener callback;
	Context context;
	ProgressDialog mProgressDialog;
	String webResponse = "";
	String Url = Constants.URL;
	String query;
	HttpsURLConnection connection;
	public Scanner scanner;
	SharedPreferencesManager spm;
	int code = 0;

	public PostLogin(Context context, AsyncTaskCompleteListener callback,
			String query, String url) {
		this.context = context;
		this.callback = callback;
		Url = Url + "" + url;
		this.query = query;
		spm = new SharedPreferencesManager(context);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage("Please Wait...");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}

	@Override
	protected String doInBackground(String... params) {
		try {

			URL url = new URL(Url);
			System.out.println("Url:.." + Url);
			connection =  new SetRequest().postRequestonServer(connection, url, query,
					context);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(query);
			wr.flush();
			wr.close();
			System.out.println("query:.." + query);
			int responseCode = 0;
			try {
				responseCode = connection.getResponseCode();
			} catch (Exception ex) {
				responseCode = connection.getResponseCode();
			}
			code = responseCode;
			if (responseCode == HttpsURLConnection.HTTP_OK) {
				String line = "";
				StringBuffer response = new StringBuffer();
				InputStream is = connection.getInputStream();
				BufferedReader rd = new BufferedReader(
						new InputStreamReader(is));
				while ((line = rd.readLine()) != null) {
					response.append(line);
					response.append('\r');
				}
				rd.close();
				spm.saveStringValues(Constants.Token,
						connection.getHeaderField("access-token"));
				spm.saveStringValues(Constants.Client,
						connection.getHeaderField("client"));
				spm.saveStringValues(Constants.Expiry,
						connection.getHeaderField("expiry"));
				spm.saveStringValues(Constants.Uid, connection.getHeaderField("uid"));

				webResponse = response.toString();
			} else {
				InputStream stream = connection.getErrorStream();
				if (stream == null) {
					stream = connection.getInputStream();
				}
				scanner = new Scanner(stream);
				scanner.useDelimiter("\\Z");
				webResponse = scanner.next();
				System.out.println("webResponse:.." + webResponse);
			}

		} catch (Exception ex) {
			System.out.println("Error in login." + ex.getMessage());
			webResponse="error";
		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
		return webResponse;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		try {
			mProgressDialog.dismiss();
			callback.onTaskComplete(result, code);

		} catch (Exception ex) {
			System.out.println("Error in login complition:..."
					+ ex.getMessage());
		}
	}

}
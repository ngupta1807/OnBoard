package menuapp.activity.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import menuapp.activity.intrface.AsyncTaskCompleteListener;
import menuapp.activity.intrface.Constants;
import menuapp.activity.util.SharedPreferencesManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class DeleteLogout extends AsyncTask<String, Void, String> {

	AsyncTaskCompleteListener callback;
	Context context;
	ProgressDialog mProgressDialog;
	String webResponse = "";
	String Url = Constants.URL;
	HttpsURLConnection conn;
	public Scanner scanner;
	SharedPreferencesManager spm;
	int code = 0;

	public DeleteLogout(Context context, AsyncTaskCompleteListener callback,
			String url) {
		this.context = context;
		this.callback = callback;
		Url = Url + "" + url;
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
			System.out.println("Url:.." + Url);

			URL url = new URL(Url);
			conn = new SetRequest().deleteRequestonServer(conn, url, context);
			int responseCode = conn.getResponseCode();
			code = responseCode;
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
				spm.saveStringValues(Constants.Token, "0");
				spm.saveStringValues(Constants.Client, "0");
				spm.saveStringValues(Constants.Expiry, "0");
				spm.saveStringValues(Constants.Uid, "0");

				webResponse = response.toString();
			} else {
				InputStream stream = conn.getErrorStream();
				if (stream == null) {
					stream = conn.getInputStream();
				}
				scanner = new Scanner(stream);
				scanner.useDelimiter("\\Z");
				webResponse = scanner.next();
				System.out.println("webResponse:.." + webResponse);
			}

		} catch (Exception ex) {
			System.out.println("Error in login." + ex.getMessage());
			webResponse = "error";
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
			mProgressDialog.dismiss();
			callback.onTaskComplete(result, code);

		} catch (Exception ex) {
			System.out.println("Error in login complition:..."
					+ ex.getMessage());
		}
	}

}
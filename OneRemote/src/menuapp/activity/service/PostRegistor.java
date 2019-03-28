package menuapp.activity.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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

public class PostRegistor extends AsyncTask<String, Void, String> {

	AsyncTaskCompleteListener callback;
	Context context;
	ProgressDialog mProgressDialog;
	String webResponse = "";
	String Url = Constants.URL;
	String query;
	HttpsURLConnection conn;
	public Scanner scanner;
	SharedPreferencesManager spm;
	int code = 0;

	public PostRegistor(Context context, AsyncTaskCompleteListener callback,
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
			conn = new SetRequest().postRequestonServer(conn, url, query,
					context);
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(query);
			wr.flush();
			wr.close();
			System.out.println("query:.." + query);
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
				spm.saveStringValues(Constants.Token,
						conn.getHeaderField("access-token"));
				spm.saveStringValues(Constants.Client,
						conn.getHeaderField("client"));
				spm.saveStringValues(Constants.Expiry,
						conn.getHeaderField("expiry"));
				spm.saveStringValues(Constants.Uid, conn.getHeaderField("uid"));

				webResponse = response.toString();
			} else {
				InputStream stream = conn.getErrorStream();
				if (stream == null) {
					stream = conn.getInputStream();
				}
				scanner = new Scanner(stream);
				scanner.useDelimiter("\\Z");
				webResponse = scanner.next();

				/*
				 * JSONArray arrayobj = new JSONArray(webResponse);
				 * 
				 * for (int i = 0; i < 1; i++) { JSONObject output =
				 * arrayobj.getJSONObject(i); webResponse =
				 * output.getString("message"); }
				 */
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
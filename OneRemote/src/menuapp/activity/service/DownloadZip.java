package menuapp.activity.service;

import java.io.File;

import menuapp.activity.intrface.AsyncTaskCompleteListener;
import menuapp.activity.util.CustomAlertDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class DownloadZip extends AsyncTask<String, Void, String> {
	Context con;
	ProgressDialog mProgressDialog;
	AsyncTaskCompleteListener callback;
	String Url = "";
	String webServiceResponse;
	String name = "";
	int code = 0;

	public DownloadZip(Context context, AsyncTaskCompleteListener callback,
			String url, String name) {
		con = context;
		this.callback = callback;
		Url = Url + "" + url;
		this.name = name;
	}

	@Override
	protected void onPreExecute() {
		showProgress();
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			System.out.println("URL:.." + Url);
			String response = downloadAllAssets(Url);
			webServiceResponse = response;
		} catch (Exception e) {
			webServiceResponse = "error";
		}
		return webServiceResponse;
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
		dismissProgress();
		if (result == null) {
			return;
		}

	}

	protected void showProgress() {
		mProgressDialog = new ProgressDialog(con);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}

	protected void dismissProgress() {
		// You can't be too careful.
		if (mProgressDialog != null && mProgressDialog.isShowing()
				&& mProgressDialog.getWindow() != null) {
			try {
				mProgressDialog.dismiss();
			} catch (IllegalArgumentException ignore) {
				;
			}
		}
		mProgressDialog = null;
	}

	private String downloadAllAssets(String url) {
		File appDir = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "OrSave");

		System.out.println("Dir:.." + appDir.getAbsolutePath());
		if (!appDir.exists() && !appDir.isDirectory()) {
			if (appDir.mkdirs()) {
				Log.i("CreateDir", "App dir created");
			} else {
				Log.i("CreateDir", "Unable to create app dir!");
			}
		} else {
			Log.i("CreateDir", "App dir already exists");
		}

		File zipFile = new File(appDir.getPath() + "/" + name + ".zip");
		System.out.println("path:." + zipFile.getAbsolutePath());
		try {
			code = DownloadFile.download(url, zipFile, appDir, con);

			return "done";
			// unzipFile( zipFile, outputDir );
		} catch (Exception ex) {
			System.out.println("" + ex.getMessage());
			return "error";
		}

	}

	/*
	 * protected void unzipFile(File zipFile, File destination) { DecompressZip
	 * decomp = new DecompressZip(zipFile.getPath(), destination.getPath() +
	 * File.separator); decomp.unzip(); }
	 */

}
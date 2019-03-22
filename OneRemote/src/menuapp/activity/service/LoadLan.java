package menuapp.activity.service;

import java.io.BufferedReader;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import menuapp.activity.R;
import menuapp.activity.intrface.AsyncTaskCompleteListener;
import menuapp.activity.intrface.AsyncTaskCompleteListenerLoadLan;
import menuapp.activity.intrface.Constants;
import menuapp.activity.util.CustomAlertDialog;
import menuapp.activity.util.SharedPreferencesManager;
import menuapp.activity.util.model.LanModel;

import menuapp.upnp.ssdp.SSDPConstants;
import menuapp.upnp.ssdp.SSDPSearchMsg;
import menuapp.upnp.ssdp.SSDPSocket;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Html;
import android.util.Log;

public class LoadLan extends AsyncTask<String, Void, String> {

	AsyncTaskCompleteListenerLoadLan callback;
	Context context;
	ProgressDialog mProgressDialog;
	String webResponse = "";
	SharedPreferencesManager spm;
	int code = 0;
	ArrayList<LanModel> landata = new ArrayList<LanModel>();

	public LoadLan(Context context, AsyncTaskCompleteListenerLoadLan callback) {
		this.context = context;
		this.callback = callback;
		spm = new SharedPreferencesManager(context);
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiManager.MulticastLock multicastLock = wm
				.createMulticastLock("multicastLock");
		multicastLock.setReferenceCounted(true);
		multicastLock.acquire();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = new ProgressDialog(context,R.style.dialog);
		mProgressDialog.setMessage(context.getString(R.string.app_text));
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}

	SSDPSocket sock = null;

	@SuppressWarnings("finally")
	@Override
	protected String doInBackground(String... params) {

		webResponse = searchSSDP();

		/*
		 * String res=""; long startTime = System.currentTimeMillis(); //fetch
		 * starting time do { System.out.println("do"); res=searchSSDP(); }
		 * while (res.equals("done")
		 * ||(System.currentTimeMillis()-startTime)<20000);
		 */

		return webResponse;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		try {
			sock.close();
			mProgressDialog.dismiss();
			if (webResponse.equals("error")) {
				landata = new ArrayList<LanModel>();
				callback.onTaskComplete(result, landata);
			} else {
				
				callback.onTaskComplete(result, landata);
			}

		} catch (Exception ex) {
			System.out.println("Error in login complition:..."
					+ ex.getMessage());
		}
	}

	public String searchSSDP() {
		final SSDPSearchMsg searchContentDirectory = new SSDPSearchMsg(
				SSDPConstants.ST_ContentDirectory);
		final SSDPSearchMsg searchAVTransport = new SSDPSearchMsg(
				SSDPConstants.ST_AVTransport);
		final SSDPSearchMsg searchProduct = new SSDPSearchMsg(
				SSDPConstants.ST_Product);
		/*
		 * final SSDPSearchMsg searchEntity = new SSDPSearchMsg(
		 * SSDPConstants.ST_Entity);
		 */

		try {
			sock = new SSDPSocket();
			for (int i = 0; i < 1; i++) {
				sock.send(searchContentDirectory.toString());
				sock.send(searchAVTransport.toString());
				//sock.send(searchProduct.toString());
				// sock.send(searchEntity.toString());
			}
			long startTime = System.currentTimeMillis(); // fetch starting time
			while (true && (System.currentTimeMillis() - startTime) < 20000) {

				DatagramPacket dp = sock.receive();
				String c = new String(dp.getData());
				System.out.println(c);
				LanModel lm = new LanModel();
				
				try {
					String ar[] = c.split("LOCATION: ");

					String ar2[] = ar[1].split(".xml");
					
					lm.setKEY_host(ar2[0]+".xml");
					System.out.println("ar[1]"+ar[1]);
					System.out.println("ar2[0]"+ar2[0]);
					
				} catch (Exception ex) {
					try {
						String ar[] = c.split("Location: ");

						String ar2[] = ar[1].split(".xml");
						
						lm.setKEY_host(ar2[0]+".xml");
						System.out.println("ar[1]"+ar[1]);
						System.out.println("ar2[0]"+ar2[0]);
						
					} catch (Exception e) {
						lm.setKEY_host("");
					}
				}
				
				try {
					String setserver[] = c.split("Server: ");
					lm.setKEY_name(setserver[1].split("\r\n")[0].trim());
				} catch (Exception ex) {
					try {
						String setserver[] = c.split("SERVER: ");
						lm.setKEY_name(setserver[1].split("\r\n")[0].trim());
					} catch (Exception e) {
						lm.setKEY_name("");
					}
				}
				try {
					String setlocation[] = c.split("LOCATION: ");
					lm.setKEY_url(setlocation[1].split(".xml")[0].trim()+".xml");
				} catch (Exception ex) {
					try {
						String setlocation[] = c.split("Location: ");
						lm.setKEY_url(setlocation[1].split(".xml")[0].trim()+".xml");
					} catch (Exception e) {
						lm.setKEY_url("");
					}
				}
				try {
					String setname[] = c.split("MYNAME: ");
					lm.setKEY_ip(setname[1].split("\r\n")[0].trim());
				} catch (Exception ex) {
					try {
						String setname[] = c.split("Myname: ");
						lm.setKEY_ip(setname[1].split("\r\n")[0].trim());
					} catch (Exception e) {
						lm.setKEY_ip("");
					}
				}

				//lm.setKEY_name(c);

				landata.add(lm);
				webResponse = landata.toString();
			}
		} catch (Exception e) {
			try {
				// sock.close();
				webResponse = "done";
			} catch (Exception ex) {

			}
		} finally {
			try {
				// sock.close();
				System.out.println("Done");
				webResponse = "done";

			} catch (Exception e) {

			}
		}
		return webResponse;

	}
}
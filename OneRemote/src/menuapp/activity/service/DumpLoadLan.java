/*package menuapp.activity.service;

import java.util.ArrayList;
import menuapp.activity.intrface.AsyncTaskCompleteListenerLoadLan;
import menuapp.activity.util.SharedPreferencesManager;
import menuapp.activity.util.model.LanModel;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;

public class DumpLoadLan extends AsyncTask<String, Void, String> {

	AsyncTaskCompleteListenerLoadLan callback;
	Context context;
	ProgressDialog mProgressDialog;
	String webResponse = "";
	SharedPreferencesManager spm;
	int code = 0;
	ArrayList<LanModel> landata = new ArrayList<LanModel>();

	public DumpLoadLan(Context context, AsyncTaskCompleteListenerLoadLan callback) {
		this.context = context;
		this.callback = callback;

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

	@SuppressWarnings("finally")
	@Override
	protected String doInBackground(String... params) {

		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wifi != null) {
			MulticastLock mcLock = wifi.createMulticastLock("mylock");
			mcLock.acquire();
		}

		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiManager.MulticastLock multicastLock = wm
				.createMulticastLock("multicastLock");
		multicastLock.setReferenceCounted(true);
		multicastLock.acquire();

		webResponse = SendMSearchMessage();
		System.out.println("webResponse:.." + webResponse);
		return webResponse;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		System.out.println("result:.." + result);
		try {
			mProgressDialog.dismiss();
			callback.onTaskComplete(result, landata);

		} catch (Exception ex) {
			System.out.println("Error in login complition:..."
					+ ex.getMessage());
		}
	}

	public String SendMSearchMessage() {
		String c = "";
		
		 * SSDPSearchMsg searchContentDirectory = new SSDPSearchMsg(
		 * SSDPConstants.ST_ContentDirectory); SSDPSearchMsg searchAVTransport =
		 * new SSDPSearchMsg( SSDPConstants.ST_AVTransport); SSDPSearchMsg
		 * searchProduct = new SSDPSearchMsg( SSDPConstants.ST_Product);
		 * 
		 * SSDPSocket sock; try { sock = new SSDPSocket(); for (int i = 0; i <
		 * 2; i++) { sock.send(searchContentDirectory.toString());
		 * sock.send(searchAVTransport.toString());
		 * sock.send(searchProduct.toString()); }
		 * 
		 * while (true) { DatagramPacket dp = sock.receive(); // Here, I only
		 * receive the LanModel lm = new LanModel(); // sent above c = new
		 * String(dp.getData()); lm.setKEY_name(c);
		 * System.out.println("in while:.."+c); landata.add(lm); }
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * System.out.println("c:.."+c); c = ""; }
		 
		return c;
	}
}*/
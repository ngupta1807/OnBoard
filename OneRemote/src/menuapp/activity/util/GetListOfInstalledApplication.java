package menuapp.activity.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import menuapp.activity.intrface.AsyncTaskCompletePackageListener;
import menuapp.activity.util.model.PackageItem;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.ViewGroup;
import android.view.Window;

public class GetListOfInstalledApplication extends
		AsyncTask<String, Void, String> {
	ProgressDialog mProgressDialog;
	static Context context;
	AsyncTaskCompletePackageListener callback;
	ArrayList<PackageItem> data = new ArrayList<PackageItem>();

	public GetListOfInstalledApplication(Context context,
			AsyncTaskCompletePackageListener callback) {
		this.context = context;
		this.callback = callback;

	}

	public GetListOfInstalledApplication(Context context) {
		this.context = context;

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// Create progress dialog
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mProgressDialog.getWindow().setLayout(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		mProgressDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}

	@Override
	protected String doInBackground(String... place) {

		PackageManager appInfo = context.getPackageManager();
		List<ApplicationInfo> listInfo = appInfo.getInstalledApplications(PackageManager.GET_META_DATA);
		Collections.sort(listInfo, new ApplicationInfo.DisplayNameComparator(
				appInfo));

		for (int index = 0; index < listInfo.size(); index++) {
			try {
				ApplicationInfo content = listInfo.get(index);
				if ((content.flags != ApplicationInfo.FLAG_SYSTEM)
						&& content.enabled) {
					if (content.icon != 0) {
						PackageItem item = new PackageItem();
						item.setName(context.getPackageManager()
								.getApplicationLabel(content).toString());
						item.setPackageName(content.packageName);

						item.setPackagePath(""
								+ appInfo
										.getLaunchIntentForPackage(content.packageName));

						data.add(item);
					}
				}
			} catch (Exception e) {

			}
		}
		return "";
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		mProgressDialog.dismiss();
		callback.onTaskComplete(data);
	}

	public ArrayList<PackageItem> getdata() {
		ArrayList<PackageItem> newdata = new ArrayList<PackageItem>();

		PackageManager appInfo = context.getPackageManager();
		List<ApplicationInfo> listInfo = appInfo.getInstalledApplications(0);
		Collections.sort(listInfo, new ApplicationInfo.DisplayNameComparator(
				appInfo));

		for (int index = 0; index < listInfo.size(); index++) {
			try {
				ApplicationInfo content = listInfo.get(index);
				if ((content.flags != ApplicationInfo.FLAG_SYSTEM)
						&& content.enabled) {
					if (content.icon != 0) {
						PackageItem item = new PackageItem();
						item.setName(context.getPackageManager()
								.getApplicationLabel(content).toString());
						item.setPackageName(content.packageName);

						newdata.add(item);
					}
				}
			} catch (Exception e) {

			}
		}
		return newdata;
	}

	public String getPackagename(String name) {
		String data = "";

		PackageManager appInfo = context.getPackageManager();
		List<ApplicationInfo> listInfo = appInfo.getInstalledApplications(0);
		Collections.sort(listInfo, new ApplicationInfo.DisplayNameComparator(
				appInfo));

		for (int index = 0; index < listInfo.size(); index++) {
			try {
				ApplicationInfo content = listInfo.get(index);
				if ((content.flags != ApplicationInfo.FLAG_SYSTEM)
						&& content.enabled) {
					if (content.icon != 0) {

						if (name.equals(context.getPackageManager()
								.getApplicationLabel(content).toString())) {
							data = "" + content.packageName;
						}

					}
				}
			} catch (Exception e) {

			}
		}
		return data;
	}

	public boolean openApp(String packageName) {
		System.out.println("packageName:."+packageName);
	    PackageManager manager = context.getPackageManager();
	    try {
	        Intent i = manager.getLaunchIntentForPackage(packageName);
	        if (i == null) {
	        	System.out.println("package null");
	        	//i = context.getPackageManager().getLaunchIntentForPackage(packageName);
	        	
	            return false;
	            //throw new PackageManager.NameNotFoundException();
	        }
	        else{
		        i.addCategory(Intent.CATEGORY_LAUNCHER);
		        
		        context.startActivity(i);
		        return true;
	        }
	       
	    } catch (Exception e) {
	        return false;
	    }
	}
}

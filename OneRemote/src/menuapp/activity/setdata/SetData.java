package menuapp.activity.setdata;



import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings.Secure;

public class SetData {
	public static String reg(Context mcon, String email, String username,
			String password, String fname, String lname, int gender,
			String number, String majorvalue, String minorvalue, String pvalue) {
		String android_id = Secure.getString(mcon.getContentResolver(),
				Secure.ANDROID_ID);
		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter("username", username)
				.appendQueryParameter("email", email)
				.appendQueryParameter("user_type", "5")
				.appendQueryParameter("device", android_id)
				.appendQueryParameter("firstName", fname)
				.appendQueryParameter("lastName", lname)
				.appendQueryParameter("sex", "" + gender)
				.appendQueryParameter("phone", number)
				.appendQueryParameter("major_value", majorvalue)
				.appendQueryParameter("minor_value", "" + minorvalue)
				.appendQueryParameter("pvalue", pvalue)
				.appendQueryParameter("password_hash", password);
		String query = builder.build().getEncodedQuery();
		return query;
	}

	
	
	public static String updateprofile(Context mcon, String username,
			String fname, String lname, int gender, String number,
			String majorvalue, String minorvalue, String pvalue) {
		String android_id = Secure.getString(mcon.getContentResolver(),
				Secure.ANDROID_ID);
		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter("username", username)
				.appendQueryParameter("user_type", "5")
				.appendQueryParameter("device", android_id)
				.appendQueryParameter("firstName", fname)
				.appendQueryParameter("lastName", lname)
				.appendQueryParameter("sex", "" + gender)
				.appendQueryParameter("phone", number)
				.appendQueryParameter("major_value", majorvalue)
				.appendQueryParameter("minor_value", "" + minorvalue)
				.appendQueryParameter("pvalue", pvalue);
		String query = builder.build().getEncodedQuery();
		return query;
	}

	public static String changepassword(Context mcon, String o_pwd, String pwd) {
		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter("password", o_pwd)
				/*.appendQueryParameter(
						"id",
						spm.getStringValues(mcon.getResources().getString(
								R.string._ID)))*/
				.appendQueryParameter("newPassword", pwd);
		String query = builder.build().getEncodedQuery();
		return query;
	}

	public static String login(Context mcon, String username, String password) {

		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter("email", username)
				.appendQueryParameter("password", password);
		String query = builder.build().getEncodedQuery();
		

		return query;
	}
	
	

	
	public static String reg(Context mcon, String email, String password,String c_pwd) {

		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter("email", email)
				.appendQueryParameter("password", password)
				.appendQueryParameter("password_confirmation", c_pwd);
		String query = builder.build().getEncodedQuery();

		return query;
	}
	


/*	public static String logout(Context mcon) {
		SharedPreferencesManager spm = new SharedPreferencesManager(mcon);
		System.out.println("access_token"
				+ spm.getStringValues(mcon.getResources().getString(
						R.string.SESSION)));
		Uri.Builder builder = new Uri.Builder().appendQueryParameter(
				"access_token",
				spm.getStringValues(mcon.getResources().getString(
						R.string.SESSION)));
		String query = builder.build().getEncodedQuery();

		return query;
	}*/

/*	public static String setnotification(Context mcon) {

		SharedPreferencesManager spm = new SharedPreferencesManager(mcon);

		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter(
						"userId",
						spm.getStringValues(mcon.getResources().getString(
								R.string._ID)));
		String query = builder.build().getEncodedQuery();

		return query;
	}*/

/*	public static String deletenotification(Context mcon, String task_id) {

		SharedPreferencesManager spm = new SharedPreferencesManager(mcon);

		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter(
						"userId",
						spm.getStringValues(mcon.getResources().getString(
								R.string._ID))).appendQueryParameter("taskId",
						task_id);
		String query = builder.build().getEncodedQuery();

		return query;
	}

	public static String taskList(Context mcon, String status) {

		SharedPreferencesManager spm = new SharedPreferencesManager(mcon);
		System.out.println("userID"
				+ spm.getStringValues(mcon.getResources().getString(
						R.string._ID)));
		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter(
						"id",
						spm.getStringValues(mcon.getResources().getString(
								R.string._ID))).appendQueryParameter("status",
						"" + status);

		
		 * Uri.Builder builder = new Uri.Builder().appendQueryParameter( "id",
		 * "4");
		 

		String query = builder.build().getEncodedQuery();

		return query;
	}

	public static String dashboardtaskList(Context mcon, String status) {

		SharedPreferencesManager spm = new SharedPreferencesManager(mcon);
		System.out.println("userID"
				+ spm.getStringValues(mcon.getResources().getString(
						R.string._ID)));
		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter(
						"userId",
						spm.getStringValues(mcon.getResources().getString(
								R.string._ID))).appendQueryParameter(
						"tasktype", "" + status);

		String query = builder.build().getEncodedQuery();

		return query;
	}

	public static String statusUpdate(Context mcon, String status, String id) {
		int val = 0;
		SharedPreferencesManager spm = new SharedPreferencesManager(mcon);
		if (status.equals("In Progess")) {
			val = 3;
		} else if (status.equals("Pending")) {
			val = 1;
		} else {
			val = 2;
		}
		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter(
						"userId",
						spm.getStringValues(mcon.getResources().getString(
								R.string._ID)))
				.appendQueryParameter("taskId", id)
				.appendQueryParameter("status", "" + val);
		String query = builder.build().getEncodedQuery();

		return query;
	}

	public static String forgotpassword(Context mcon, String email) {

		Uri.Builder builder = new Uri.Builder().appendQueryParameter("email",
				email);
		String query = builder.build().getEncodedQuery();

		return query;
	}

	public static String searchTask(Context mcon, String filter, String status) {

		SharedPreferencesManager spm = new SharedPreferencesManager(mcon);

		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter(
						"userId",
						spm.getStringValues(mcon.getResources().getString(
								R.string._ID)))
				.appendQueryParameter("keyword", filter)
				.appendQueryParameter("status", "" + status);
		String query = builder.build().getEncodedQuery();

		return query;
	}

	
	 * public static String searchallTask(Context mcon,String filter,String
	 * status) {
	 * 
	 * SharedPreferencesManager spm = new SharedPreferencesManager(mcon);
	 * 
	 * Uri.Builder builder = new Uri.Builder() .appendQueryParameter("userId",
	 * spm.getStringValues(mcon.getResources().getString(R.string._ID)))
	 * .appendQueryParameter("keyword",filter) .appendQueryParameter("status",
	 * "" + status); String query = builder.build().getEncodedQuery();
	 * 
	 * return query; }
	 

	public static String addLogs(Context mcon, String hour, String min,
			String desc, String date, String task_id) {
		System.out.println("Start Time textview 1:" + hour);
		System.out.println("End Time textview 2:" + min);

		SharedPreferencesManager spm = new SharedPreferencesManager(mcon);

		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter(
						"user_id",
						spm.getStringValues(mcon.getResources().getString(
								R.string._ID)))
				.appendQueryParameter("start_time", "" + hour)
				.appendQueryParameter("end_time", "" + min)
				.appendQueryParameter("description", "" + desc)

				.appendQueryParameter("date", "" + date)
				.appendQueryParameter("task_id", "" + task_id);

		String query = builder.build().getEncodedQuery();

		return query;
	}

	public static String editLogs(Context mcon, String hour, String min,
			String desc, String date, String _id) {

		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter("start_time", "" + hour)
				.appendQueryParameter("end_time", "" + min)
				.appendQueryParameter("description", "" + desc)
				.appendQueryParameter("date", "" + date)
				.appendQueryParameter("id", "" + _id);

		String query = builder.build().getEncodedQuery();

		return query;
	}

	public static String deleteLogs(Context mcon, String _id) {

		Uri.Builder builder = new Uri.Builder().appendQueryParameter("id", ""
				+ _id);
		String query = builder.build().getEncodedQuery();

		return query;
	}

	public static String setlogs(Context mcon, String task_id) {

		SharedPreferencesManager spm = new SharedPreferencesManager(mcon);

		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter(
						"userId",
						spm.getStringValues(mcon.getResources().getString(
								R.string._ID))).appendQueryParameter("taskId",
						task_id);

		String query = builder.build().getEncodedQuery();

		return query;
	}

	public static String addmsg(Context mcon, String task_id, String msg) {

		SharedPreferencesManager spm = new SharedPreferencesManager(mcon);

		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter(
						"user_id",
						spm.getStringValues(mcon.getResources().getString(
								R.string._ID)))
				.appendQueryParameter("task_id", task_id)
				.appendQueryParameter("message", msg);

		String query = builder.build().getEncodedQuery();

		return query;
	}*/
	
	public static String putmsg(Context mcon,String msg) {
	
		Uri.Builder builder = new Uri.Builder().appendQueryParameter("message", msg);

		String query = builder.build().getEncodedQuery();

		return query;
	}

}

package menuapp.activity.util;

import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetReachability 
{
	private Context _context;

	public InternetReachability(Context context)
	{
		this._context = context;
	}

	public boolean isConnected()
	{
		ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) 
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) 
				for (int i = 0; i < info.length; i++) 
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}

		}
		return false;
	}

}

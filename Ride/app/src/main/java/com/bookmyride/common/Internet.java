package com.bookmyride.common;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by vinod on 10/26/2016.
 */
public class Internet {
    public static boolean hasInternet(Context mContext) {
        /*ConnectivityManager connectivityManager =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }*/
        return isNetworkConnected(mContext);
    }
    private static boolean isNetworkConnected(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}

package com.sample.app.util


import android.content.Context
import android.net.ConnectivityManager

/**
 * The type Network status cheker.
 */
object NetworkStatusCheker {

    /**
     * Checking internet state.
     *
     * @param context the context
     * @return true if internet is enabled else false
     *
     */
    fun checkNetConnection(context: Context): Boolean {
        //intialize ConnectivityManager
        val miManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //intialize NetworkInfo
        val miInfo = miManager.activeNetworkInfo
        var networkStatus = false
        if (miInfo != null && miInfo.type == ConnectivityManager.TYPE_WIFI) {
            networkStatus = true
        } else if (miInfo != null && miInfo.type == ConnectivityManager.TYPE_MOBILE) {
            networkStatus = true
        } else if (miInfo != null && miInfo.isConnectedOrConnecting) {
            networkStatus = true
        }
        return networkStatus
    }
}

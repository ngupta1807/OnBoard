package com.tipbox.app.util

/**
 * NetworkStatusCheker.java
 * This Class is used to check the network(Wifi,GPS, Simcard) States
 *
 * @category Contus
 * @package com.compass.com.utils
 * @version 1.0
 * @author Contus Team <developers></developers>@contus.in>
 * @copyright Copyright (C) 2015 Contus. All rights reserved.
 * @license http://www.apache.org/licenses/LICENSE-2.0
 */

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

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
     * A more concise way of checking whether a network interface is available is as follows.
     * The method getActiveNetworkInfo() returns a NetworkInfo instance representing the first
     * connected network interface it can find, or null if none of the interfaces is connected
     * (meaning that an internet connection is not available)
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
/**
 * Instantiates a new network status cheker.
 */
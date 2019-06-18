/**
 * @category ContusMessanger
 * @package com.contusfly
 * @version 1.0
 * @author ContusTeam <developers></developers>@contus.in>
 * @copyright Copyright (C) 2015 <Contus>. All rights reserved.
 * @license http://www.apache.org/licenses/LICENSE-2.0
</Contus> */
package com.tipbox.app.util

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.tipbox.app.restclient.RestClient


/**
 * The Class MApplication.
 */
class MApplication : Application() {

    /**
     * Gets rest client.
     *
     * @return the rest client
     */
    private var restClient: RestClient? = null
    private var mPreferences: SharedPreferences? = null
    private var mEditor: SharedPreferences.Editor? = null
    private var preferences: SharedPreferences? = null
    public var mInstance: MApplication? = null
    override fun onCreate() {
        super.onCreate()
        mInstance = this
        restClient = RestClient(this)
        mPreferences = getSharedPreferences(
            getString(com.tipbox.app.R.string.app_name),
            Context.MODE_PRIVATE
        )
        mEditor = mPreferences!!.edit()
        preferences = PreferenceManager.getDefaultSharedPreferences(this)

    }

    /**
     * Gets rest client.
     *
     * @return the rest client
     */
    fun getRestClient(): RestClient? {
        return restClient
    }

    /**
     * Gets the string from preference.
     * @param key the key
     * @return the string from preference
     */
    fun getStringFromPreference(key: String): String {
        return mPreferences!!.getString(key, "")
    }

    /**
     * Store string in preference.
     *
     * @param key   the key
     * @param value the value
     */
    fun storeStringInPreference(key: String, value: String) {
        mEditor!!.putString(key, value)
        mEditor!!.commit()
    }

    /**
     * Store boolean in preference.
     *
     * @param key   the key
     * @param value the value
     */
    fun storeBooleanInPreference(key: String, value: Boolean) {
        mEditor!!.putBoolean(key, value)
        mEditor!!.commit()
    }

    /**
     * Gets the boolean from preference.
     *
     * @param key the key
     * @return the boolean from preference
     */
    fun getBooleanFromPreference(key: String): Boolean {
        return mPreferences!!.getBoolean(key, false)
    }

    fun getRegToken(con: Context): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(con)
        val sentToken = sharedPreferences
            .getBoolean(ServerUrls.SET_TOKEN, false)
        val token = sharedPreferences
            .getString(ServerUrls.SET_TOKEN, "")
        Log.d("device Token", token)
        return if (sentToken && !"".equals(token!!, ignoreCase = true)) token else ""
    }
}

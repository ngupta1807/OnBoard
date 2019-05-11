package com.wfi.beaconscanner.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.wfi.beaconscanner.R

/**
 * Created by nisha.
 */

class PreferencesHelper(ctx: Context) {
    companion object {
        private val SHARED_PREF_KEY = "shared_pref"
        private val SCAN_ON_OPEN_KEY = "scanOnOpenKey"
        private val SCANNING_STATE_KEY = "scanningState"
        private val SCAN_DELAY_KEY = "scanDelay"
        private val PREVENT_SLEEP_KEY = "preventSleep"
        private val PREVENT_LAUNCHER = "launcher"
        private val LOGGING_ENABLED_KEY = "loggingEnabled"
        private val LOGGING_ENDPOINT_KEY = "loggingEndpoint"
        private val LOGGING_DEVICE_NAME_KEY = "loggingDeviceName"
        private val LAST_LOGGING_CALL = "lastLoggingCall"
        private val MODE_ENABLED_KEY = "mode"
    }

    private val prefs: SharedPreferences
    private val ressources: Resources

    init {
        prefs = ctx.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        ressources = ctx.resources
    }

    var isScanOnOpen: Boolean
        get() = prefs.getBoolean(SCAN_ON_OPEN_KEY, false)
        set(status) = prefs.edit().putBoolean(SCAN_ON_OPEN_KEY, status).apply()

    var preventSleep: Boolean
        get() = prefs.getBoolean(PREVENT_SLEEP_KEY, false)
        set(status) = prefs.edit().putBoolean(PREVENT_SLEEP_KEY, status).apply()

    var asLauncher: Boolean
        get() = prefs.getBoolean(PREVENT_LAUNCHER, false)
        set(status) = prefs.edit().putBoolean(PREVENT_LAUNCHER, status).apply()

    fun setScanningState(state: Boolean) = prefs.edit().putBoolean(SCANNING_STATE_KEY, state).apply()

    fun wasScanning() = prefs.getBoolean(SCANNING_STATE_KEY, false)

    fun getScanDelayIdx() = prefs.getInt(SCAN_DELAY_KEY, 0)

    fun getScanDelay() : Long {
        val idx = getScanDelayIdx()
        val scansDelays = ressources.getIntArray(R.array.scan_delays)

        if (idx < scansDelays.size) {
            return scansDelays[idx].toLong()
        }
        return 0L
    }

    var isLoggingEnabled: Boolean
        get() = prefs.getBoolean(LOGGING_ENABLED_KEY, false)
        set(status) = prefs.edit().putBoolean(LOGGING_ENABLED_KEY, status).apply()

    var isModeEnabled: String
        get() = prefs.getString(MODE_ENABLED_KEY, null)
        set(status) = prefs.edit().putString(MODE_ENABLED_KEY, status).apply()

    var loggingEndpoint: String?
        get() = prefs.getString(LOGGING_ENDPOINT_KEY, null)
        set(endpoint) = prefs.edit().putString(LOGGING_ENDPOINT_KEY, endpoint).apply()

    var loggingDeviceName: String?
        get() = prefs.getString(LOGGING_DEVICE_NAME_KEY, null)
        set(name) = prefs.edit().putString(LOGGING_DEVICE_NAME_KEY, name).apply()

    var lasLoggingCall: Long
        get() = prefs.getLong(LAST_LOGGING_CALL, 0)
        set(timestamp) = prefs.edit().putLong(LAST_LOGGING_CALL, timestamp).apply()

}

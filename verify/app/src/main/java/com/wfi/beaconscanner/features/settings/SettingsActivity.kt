package com.wfi.beaconscanner.features.settings

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.ScrollView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnCheckedChanged
import butterknife.OnClick
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.google.firebase.analytics.FirebaseAnalytics
import com.wfi.beaconscanner.R
import com.wfi.beaconscanner.service.OnlineLogUpload
import com.wfi.beaconscanner.utils.Keys
import com.wfi.beaconscanner.utils.PreferencesHelper
import com.wfi.beaconscanner.utils.SharedPreferencesManager
import com.wfi.beaconscanner.utils.extensionFunctions.component
import javax.inject.Inject


class SettingsActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SettingsActivity"
    }

    @Inject lateinit var prefs: PreferencesHelper
    @Inject lateinit var tracker: FirebaseAnalytics
    @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
    @BindView(R.id.content) lateinit var content: ScrollView
    @BindView(R.id.scan_open) lateinit var scanOpen: SwitchCompat
    @BindView(R.id.prevent_sleep) lateinit var preventSleep: SwitchCompat
    @BindView(R.id.as_launcher) lateinit var asLauncher: SwitchCompat
    @BindView(R.id.mac_address) lateinit var settings: TextView
    @BindView(R.id.gateway) lateinit var gateway: TextView
    @BindView(R.id.min_distance) lateinit var min_distance: TextView
    @BindView(R.id.max_distance) lateinit var max_distance: TextView
    @BindView(R.id.device_id) lateinit var device_id: TextView
    @BindView(R.id.version) lateinit var version: TextView
    @BindView(R.id.upload_frequency) lateinit var scan_time: TextView
    private lateinit var spm: SharedPreferencesManager
    private lateinit var ressources: Resources
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ButterKnife.bind(this)
        component().inject(this)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        spm = SharedPreferencesManager(this)
        ressources = this.resources
        if(spm.getIntValues("scan_time")==1){
            scan_time.text = "No delay"
        }else {
            scan_time.text = "Every " + spm.getIntValues("scan_time") + " seconds"
        }

        if (spm.getStringValues(Keys.MODE).equals("")) {
            spm.saveStringValues(Keys.MODE, "1");
            prefs.isModeEnabled = "1";
            scanOpen.setText("Live")
            scanOpen.isChecked = true
            //startService(Intent(this, OnlineDeviceUpdate::class.java) )
        } else if (spm.getStringValues(Keys.MODE).equals("1")) {
            prefs.isModeEnabled = "1";
            scanOpen.setText("Live")
            scanOpen.isChecked = true
        } else {
            prefs.isModeEnabled = "2";
            scanOpen.setText("Test")
            scanOpen.isChecked = false
        }


        preventSleep.isChecked = prefs.preventSleep
        asLauncher.isChecked = prefs.asLauncher
        gateway.setText(spm.getStringValues(Keys.GateWay))
        settings.setText(spm.getStringValues(Keys.MacAddress))
        min_distance.setText("" + spm.getLongValues(Keys.MinLogDis))
        max_distance.setText("" + spm.getLongValues(Keys.MaxLogDis))
        device_id.setText(spm.getStringValues(Keys.DeviceID))
        version.setText(R.string.app_version)
    }


    @OnCheckedChanged(R.id.scan_open)
    fun onScanOpenChanged(status: Boolean) {
        val b = Bundle()
        b.putBoolean("status", status)
        if (status == true) {
            spm.saveStringValues(Keys.MODE, "1");
            prefs.isModeEnabled = "1";
            scanOpen.setText("Live")
            //startService(Intent(this, OnlineDeviceUpdate::class.java))
        } else {
            spm.saveStringValues(Keys.MODE, "2");
            prefs.isModeEnabled = "2";
            scanOpen.setText("Test")
            //stopService(Intent(this, OnlineDeviceUpdate::class.java))
        }
    }

    @OnClick(R.id.upload_delays_container)
    fun onFreqDelayClicked() {
        MaterialDialog.Builder(this)
                .theme(Theme.LIGHT)
                .title("Log upload duration")
                .items(R.array.upload_delays_names)
                .itemsCallbackSingleChoice(loadtimerIndex(spm.getIntValues("scan_time"))) { _, _, which, text ->
                    if(which==0){
                        stopService(Intent(this, OnlineLogUpload::class.java))
                        spm.saveIntValues("scan_time", loadtimer(which))
                    }else {
                        startService(Intent(this, OnlineLogUpload::class.java))
                        spm.saveIntValues("scan_time", loadtimer(which))
                    }
                    scan_time.text = "$text"
                    true
                }
                .positiveText(R.string.choose)
                .show()
    }

    fun loadtimer(status: Int): Int {
        val uploadDelays = ressources.getIntArray(R.array.upload_delays)
        if (status < uploadDelays.size) {
            return uploadDelays[status]
        }
        return 2
    }

    fun loadtimerIndex(timer: Int): Int {
        val uploadDelays = ressources.getIntArray(R.array.upload_delays)
        return uploadDelays.indexOf(timer)
    }

    @OnCheckedChanged(R.id.prevent_sleep)
    fun onPreventSleepChanged(status: Boolean) {
        val b = Bundle()

        b.putBoolean("status", status)
        tracker.logEvent("prevent_sleep_changed", b)
        prefs.preventSleep = status
    }

    @OnCheckedChanged(R.id.as_launcher)
    fun onLauncherChanged(status: Boolean) {
        val b = Bundle()

        b.putBoolean("status", status)
        tracker.logEvent("prevent_sleep_changed", b)
        prefs.asLauncher = status
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

package com.wfi.beaconscanner.features.beaconList

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import com.wfi.beaconscanner.R
import com.wfi.beaconscanner.database.DbAdapter
import com.wfi.beaconscanner.database.GetDataFrmDB
import com.wfi.beaconscanner.events.RxBus
import com.wfi.beaconscanner.features.settings.SettingsActivity
import com.wfi.beaconscanner.models.BeaconSaved
import com.wfi.beaconscanner.service.AppBackground
import com.wfi.beaconscanner.service.DeviceUpdate
import com.wfi.beaconscanner.service.OfflineLogUpload
import com.wfi.beaconscanner.service.OnlineLogUpload
import com.wfi.beaconscanner.utils.*
import com.wfi.beaconscanner.utils.extensionFunctions.component
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import io.realm.RealmResults
import org.altbeacon.beacon.BeaconConsumer
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

/**
 * Created by nisha.
 */
class BeaconListActivity : AppCompatActivity(), BeaconListContract.View, BeaconConsumer, EasyPermissions.PermissionCallbacks {
    // *********** Declare Used Variables *********//*
    companion object {
        val coarseLocationPermission = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
        val RC_COARSE_LOCATION = 1
        val RC_SETTINGS_SCREEN = 2
    }

    enum class BluetoothState(val bgColor: Int, val text: Int) {
        STATE_OFF(R.color.bluetoothDisabled, R.string.bluetooth_disabled),
        STATE_TURNING_OFF(R.color.bluetoothTurningOff, R.string.turning_bluetooth_off),
        STATE_ON(R.color.bluetoothTurningOn, R.string.bluetooth_enabled),
        STATE_TURNING_ON(R.color.bluetoothTurningOn, R.string.turning_bluetooth_on)
    }

    @Inject lateinit var bluetoothState: BluetoothManager
    @Inject lateinit var rxBus: RxBus
    @Inject lateinit var realm: Realm
    @Inject lateinit var prefs: PreferencesHelper
    @Inject lateinit var ratingHelper: RatingHelper
    @Inject lateinit var tracker: FirebaseAnalytics

    @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
    @BindView(R.id.progress_1) lateinit var progressOne: ProgressBar
    @BindView(R.id.progress_2) lateinit var progressTwo: ProgressBar
    @BindView(R.id.activity_main) lateinit var rootView: CoordinatorLayout
    @BindView(R.id.bluetooth_state) lateinit var bluetoothStateTv: TextView
    @BindView(R.id.static_text) lateinit var statictext: TextView
    @BindView(R.id.search_text) lateinit var search_text: EditText
    @BindView(R.id.empty_view) lateinit var emptyView: RelativeLayout
    @BindView(R.id.beacons_rv) lateinit var beaconsRv: RecyclerView

    @BindView(R.id.bottom_sheet) lateinit var bottomSheet: NestedScrollView
    @BindView(R.id.rating_step_1) lateinit var ratingStep1: ConstraintLayout
    @BindView(R.id.rating_step_2) lateinit var ratingStep2: ConstraintLayout
    @BindView(R.id.scan_fab) lateinit var scanFab: FloatingActionButton

    private var dialog: MaterialDialog? = null
    private var menu: Menu? = null
    private var bsBehavior: BottomSheetBehavior<NestedScrollView>? = null
    lateinit  var presenter: BeaconListContract.Presenter
    private lateinit var deviceid: String
    private lateinit var mcon: Context
    private lateinit var spm: SharedPreferencesManager
    internal lateinit var mDbHelper: DbAdapter
    private lateinit var logupload: Intent
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(intent.getStringExtra("type").equals("working")) {
                    presenter.logToWebhookIfNeeded()
            }
        }
    }
    /**
     * Every Activity created is passed through a sequence of method calls.
     * onCreate() is the first of these calls.
     **/
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppThemeNoActionBar)
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        component().inject(this)
        mcon = this;
        toolbar.inflateMenu(R.menu.main_menu)
        setSupportActionBar(toolbar)
        mDbHelper = DbAdapter(this)
        mDbHelper.open()
        logupload = Intent(this, OnlineLogUpload::class.java)
        beaconsRv.setHasFixedSize(true)
        startService(logupload)
        beaconsRv.setLayoutManager(LinearLayoutManagerWithSmoothScroller(this))
        beaconsRv.smoothScrollToPosition(0)
        registorReceiver()

        search_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                presenter.filterList(p0.toString().trim())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        spm = SharedPreferencesManager(this)

        bsBehavior = BottomSheetBehavior.from(bottomSheet)

        // Hide the bottomSheet uppon creation
        bsBehavior?.state = BottomSheetBehavior.STATE_HIDDEN



        runOnUiThread {
            presenter = BeaconListPresenter(this, rxBus, prefs, realm, bluetoothState, ratingHelper, tracker, mcon, mDbHelper, statictext,GetDataFrmDB(),logupload)
        }

        deviceid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        prefs.loggingDeviceName = deviceid

        prefs.loggingEndpoint =Constants.URL+Constants.bulkOnlineLogs

        prefs.isLoggingEnabled = true

        prefs.isScanOnOpen = true

        prefs.preventSleep = true

        startService(Intent(this, DeviceUpdate::class.java))
        startService(Intent(this, AppBackground::class.java))

        if (spm.getStringValues(Keys.MODE).equals("") || spm.getStringValues(Keys.MODE).equals("1")) {
            spm.saveStringValues(Keys.MODE, "1")
            prefs.isModeEnabled = "1"
        } else {
        }
    }
    // used to registor broadcast receiver
    fun registorReceiver() {
        val filters = IntentFilter()
        filters.addAction("android.net.wifi.WIFI_STATE_CHANGED")
        filters.addAction("android.net.wifi.STATE_CHANGE")
        filters.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        super.registerReceiver(myReceiver, filters)

        registerReceiver(broadcastReceiver,  IntentFilter(
                OnlineLogUpload.BROADCAST_ACTION))

    }


    //The BroadcastReceiver that listens for bluetooth broadcasts
    private val myReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Keys().registorListener(context, intent)
        }
    }
    // activity life cycle
    override fun onResume() {
        super.onResume()
        startService(Intent(this, OfflineLogUpload::class.java))
        presenter.setBeaconManager(component().providesBeaconManager())
        presenter.start()

        if(!search_text.getText().toString().equals("")) {
            presenter.filterList(search_text.getText().toString().trim());
        }
    }
    // activity interface : check for device screen on/off
    override fun keepScreenOn(status: Boolean) {
            if (status) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // activity interface : used to set list adapter
    override fun setAdapter(beaconResults: RealmResults<BeaconSaved>) {
        beaconsRv.adapter = BeaconsRecyclerViewAdapter(beaconResults, this, object : BeaconsRecyclerViewAdapter.OnControlsOpen {
            override fun onOpenControls(beacon: BeaconSaved) {
                val bsDialog = ControlsBottomSheetDialog.newInstance(beacon)
                bsDialog.show(supportFragmentManager, bsDialog.tag)
            }
        })
    }
    // activity interface : used to display empty view
    override fun showEmptyView(show: Boolean) {
        emptyView.visibility = if (show) View.VISIBLE else View.GONE
        beaconsRv.visibility = if (show) View.GONE else View.VISIBLE
    }
    // activity interface : used to update bluetooth state
    override fun updateBluetoothState(state: BluetoothState, isEnabled: Boolean) {
        bluetoothStateTv.visibility = View.VISIBLE
        bluetoothStateTv.setBackgroundColor(ContextCompat.getColor(this, state.bgColor))
        bluetoothStateTv.text = getString(state.text)

        menu?.getItem(1)?.setIcon(if (isEnabled) R.drawable.ic_bluetooth_white_24dp else R.drawable.ic_bluetooth_disabled_white_24dp)

        // If the bluetooth is ON, we don't warn the user
        if (state == BluetoothState.STATE_ON) {
            bluetoothStateTv.visibility = View.GONE
        }
    }

    /* Permissions methods */
    override fun hasCoarseLocationPermission() = EasyPermissions.hasPermissions(this, *coarseLocationPermission)

    override fun hasSomePermissionPermanentlyDenied(perms: List<String>) = EasyPermissions.somePermissionPermanentlyDenied(this, perms)

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode == RC_COARSE_LOCATION) {
            presenter.onLocationPermissionGranted()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, permList: List<String>) {
        if (requestCode == RC_COARSE_LOCATION) {
            presenter.onLocationPermissionDenied(requestCode, permList)
        }
    }

    override fun showEnablePermissionSnackbar() {
        Snackbar.make(rootView, getString(R.string.enable_permission_from_settings), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.enable)) { _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + packageName))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

                    startActivityForResult(intent, RC_SETTINGS_SCREEN)
                }.show()
    }

    override fun askForCoarseLocationPermission() = ActivityCompat.requestPermissions(this, coarseLocationPermission, RC_COARSE_LOCATION)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /* ==== end of permission methods ==== */

    @OnClick(R.id.scan_fab)
    fun toggleScan() = presenter.toggleScan()
    // activity interface : used to detect bluetooth
    override fun showBluetoothNotEnabledError() {
        Snackbar.make(rootView, getString(R.string.enable_bluetooth_to_start_scanning), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.enable)) { _ ->
                    presenter.onBluetoothToggle()
                }
                .show()
    }
    // activity interface : used to display error message
    override fun showGenericError(msg: String) {
        Snackbar.make(rootView, msg, Snackbar.LENGTH_LONG).show()
    }

    override fun showLoggingError() = Snackbar.make(rootView, getString(R.string.logging_error_please_check), Snackbar.LENGTH_LONG).show()

    // activity interface : used to change scanning button shown in bottom right of scanning screen
    override fun showScanningState(state: Boolean) {
        toolbar.title = getString(if (state) R.string.scanning_for_beacons else R.string.app_name)
        progressOne.visibility = if (state) View.VISIBLE else View.GONE
        progressTwo.visibility = if (state) View.VISIBLE else View.GONE

        scanFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, if (state) R.color.colorPauseFab else R.color.colorAccent))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val anim = AnimatedVectorDrawableCompat.create(this, if (state) R.drawable.play_to_pause else R.drawable.pause_to_play) as AnimatedVectorDrawableCompat

            scanFab.setImageDrawable(anim)
            anim.start()
        } else {
            scanFab.setImageDrawable(AppCompatResources.getDrawable(this, if (state) R.drawable.pause_icon else R.drawable.play_icon))
        }
    }

    override fun onBeaconServiceConnect() = presenter.onBeaconServiceConnect()
    // activity interface : used to display rating , not used in current functionality.
    override fun showRating(step: Int, show: Boolean) {
        if (!show) {
            bsBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            return
        }

        when (step) {
            RatingHelper.STEP_ONE -> {
                bsBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                ratingStep1.visibility = View.VISIBLE
                ratingStep2.visibility = View.GONE
            }
            RatingHelper.STEP_TWO -> {
                bsBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                ratingStep1.visibility = View.GONE
                ratingStep2.visibility = View.VISIBLE
            }
        }
    }
    // activity interface : used to redirect to store page , not used in current functionality .
    override fun redirectToStorePage() {
        val appPackageName = packageName // getPackageName() from Context or Activity object

        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)))
        } catch (anfe: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)))
        }
    }

    @OnClick(R.id.positive_btn_step_1, R.id.negative_btn_step_1, R.id.positive_btn_step_2, R.id.negative_btn_step_2)
    fun onRatingInteraction(view: View) {
        val step = when (view.id) {
            R.id.positive_btn_step_1, R.id.negative_btn_step_1 -> RatingHelper.STEP_ONE
            R.id.positive_btn_step_2, R.id.negative_btn_step_2 -> RatingHelper.STEP_TWO
            else -> RatingHelper.STEP_ONE
        }
        val answer = when (view.id) {
            R.id.positive_btn_step_1, R.id.positive_btn_step_2 -> true
            else -> false
        }

        presenter.onRatingInteraction(step, answer)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        this.menu = menu
        return true
    }

    override fun showClearDialog() {
        dialog = MaterialDialog.Builder(this)
                .theme(Theme.LIGHT)
                .title(R.string.delete_all)
                .content(R.string.are_you_sure_delete_all)
                .autoDismiss(true)
                .onPositive { _, _ ->
                    presenter.onClearAccepted()
                }
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .build()
        dialog?.show()
    }
    // activity interface : used to redirect in setting screen
    override fun startSettingsActivity() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_bluetooth -> {
                presenter.onBluetoothToggle()
            }
            R.id.action_clear -> {
                presenter.onClearClicked()
            }
            R.id.action_settings -> {
                presenter.onSettingsClicked()
            }

            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
    // activity lifecycle : used to upload offline logs available in database.
    override fun onPause() {
        stopService(Intent(this, OfflineLogUpload::class.java))
        presenter.stop()
        super.onPause()
    }
    // activity lifecycle : used to handle when user press back button of device.
    override fun onBackPressed() {
        if (toolbar.title.equals("Scanning")) {
            if (prefs.asLauncher == false) {
                showDialog(this, "Clossing the app will stop scanning.", true, "1");
            }
        } else {
            super.onBackPressed()
        }
    }
    // activity lifecycle : used to close all open variables.
    override fun onDestroy() {
        dialog?.dismiss()
        presenter.clear()
        stopService(logupload)
        stopService(Intent(this, OfflineLogUpload::class.java))
        unregisterReceiver(myReceiver)
        unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }
    // used to display dialog
    fun showDialog(mcon: Context, message: String, isCancelable: Boolean, closeapp: String) {
        val builder = AlertDialog.Builder(mcon)
        builder.setCancelable(isCancelable)
        builder.setTitle(Html.fromHtml("<font color='#ff0000'>Alert!</font>"))
        builder.setMessage(Html.fromHtml("<font color='#000000'>$message</font>"))
        builder.setNegativeButton("Ok") { dialog, which ->
            dialog.dismiss()
            finish()
        }
        builder.show()
    }

    // activity lifecycle
    override fun onStop() {
        super.onStop()
    }
}

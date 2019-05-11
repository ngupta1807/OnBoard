package com.wfi.beaconscanner.features.startapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.wfi.beaconscanner.DefaultExceptionHandler
import com.wfi.beaconscanner.R
import com.wfi.beaconscanner.database.DbAdapter
import com.wfi.beaconscanner.features.beaconList.BeaconListActivity
import com.wfi.beaconscanner.utils.AlertManager
import com.wfi.beaconscanner.utils.Keys
import com.wfi.beaconscanner.utils.SharedPreferencesManager

/**
 * Created by nisha.
 */
class AddToken : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // *********** Declare Used Variables *********//*
    private lateinit var spm: SharedPreferencesManager
    private lateinit var ra: RequestApis
    private lateinit var rto: RequestApi
    private lateinit var key: Keys
    private lateinit var am: AlertManager
    private lateinit var mac_address: String
    private lateinit var ntoken: String
    private var PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    private var resultCode = 0
    internal lateinit var mDbHelper: DbAdapter
    internal lateinit var mcon: Context
    @BindView(R.id.token) lateinit var token: EditText
    @BindView(R.id.fillview) lateinit var fillview: RelativeLayout
    @BindView(R.id.emptyview) lateinit var emptyview: RelativeLayout
    @OnClick(R.id.start)
    fun click() {
        if (token.getText().toString().equals("")) {
            am.showDialog(this, "Please enter token", true, "0");
        }else {
            ntoken = spm.getStringValues(Keys.TOKEN);
            if (rto.redirectStartMethod(key, this, ra, token.getText().toString().trim(), mac_address, ntoken, mDbHelper) == false) {
                finish();
            }
        }
    }
    /**
     * Every Activity created is passed through a sequence of method calls.
     * onCreate() is the first of these calls.
     **/
    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        setTheme(R.style.AppThemeNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startapp)
        ButterKnife.bind(this);
        initialiseScreen();
        Thread.setDefaultUncaughtExceptionHandler(DefaultExceptionHandler(this));
    }
    // used to initialize screen and other parameter used.
    fun initialiseScreen() {
        key = Keys()
        ra = RequestApis(this)
        am = AlertManager()
        spm = SharedPreferencesManager(this)
        rto = RequestApi()
        mDbHelper = DbAdapter(this)
        mDbHelper.open()
        mcon=this
        mac_address = key.getMACAddress("wlan0"); //get device mac address in case of wifi
        // *********** used to check app status *********//**//*

        if (spm.getStringValues(Keys.CHECK).equals("started")) {
            try{
            if(getIntent().getStringExtra("app_session").equals("Restart")) {
                fillview.visibility = View.GONE
                emptyview.visibility = View.VISIBLE
                restartApp()
            }else {
                val intentac = Intent(this, BeaconListActivity::class.java)
                intentac.putExtra("app_session", "app")
                startActivity(intentac)
                finish();
            }
            }catch(ex: Exception){
                val intentac = Intent(this, BeaconListActivity::class.java)
                intentac.putExtra("app_session", "app")
                startActivity(intentac)
                finish();
            }
        }
        try {
            if (getIntent().getStringExtra("app_session").equals("deleted")) {
                am.showDialog(this, "Your Gateway has been " + spm.getStringValues(Keys.DeviceStatus) + " from our portal. Please request support to add it for your location.", true, "0");
            }
        } catch (e: Exception) {

        }
        checkPlayServices()
    }
    // used to redirect user on scanning screen.
    fun restartApp() {
        if(spm.getStringValues(Keys.GateWay).equals("")) {
            fillview.visibility = View.VISIBLE
            emptyview.visibility = View.GONE
        }else{
            val intentac = Intent(this, BeaconListActivity::class.java)
            intentac.putExtra("app_session", "app")
            startActivity(intentac)
            finish();
        }
    }

    // used to check play service is installed in device or not
    fun checkPlayServices(): Boolean {

        val apiAvailability = GoogleApiAvailability.getInstance()
        resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
            }
            return false;
        }
        return true;
    }
    // activity lifecycle
    override fun onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
    }


    override fun onConnected(p0: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
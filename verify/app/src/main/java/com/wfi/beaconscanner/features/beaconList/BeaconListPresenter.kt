package com.wfi.beaconscanner.features.beaconList

import android.app.ActivityManager
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Build
import android.os.RemoteException
import android.text.format.Formatter
import android.widget.TextView
import com.google.firebase.analytics.FirebaseAnalytics
import com.wfi.beaconscanner.database.DbAdapter
import com.wfi.beaconscanner.database.GetDataFrmDB
import com.wfi.beaconscanner.events.Events
import com.wfi.beaconscanner.events.RxBus
import com.wfi.beaconscanner.features.startapp.AddToken
import com.wfi.beaconscanner.interfac.PostDataCompleteListener
import com.wfi.beaconscanner.models.BeaconSaved
import com.wfi.beaconscanner.service.DeviceUpdate
import com.wfi.beaconscanner.utils.*
import com.wfi.beaconscanner.utils.extensionFunctions.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.realm.Realm
import io.realm.RealmResults
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.Region
import org.json.JSONArray
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by nisha.
 */

class BeaconListPresenter(val view: BeaconListContract.View,
                          val rxBus: RxBus,
                          val prefs: PreferencesHelper,
                          val realm: Realm,
                          val bluetoothState: BluetoothManager,
                          val ratingHelper: RatingHelper,
                          val tracker: FirebaseAnalytics, val mcon: Context, var mDbHelper: DbAdapter, var statictext: TextView, var db: GetDataFrmDB, var logupload: Intent) : BeaconListContract.Presenter, PostDataCompleteListener {

    // *********** Declare Used Variables *********//*
    private val TAG = "BeaconListPresenter"
    private lateinit var beaconResults: RealmResults<BeaconSaved>
    private var bluetoothStateDisposable: Disposable? = null
    private var rangeDisposable: Disposable? = null
    private var beaconManager: BeaconManager? = null
    private var numberOfScansSinceLog = 0
    private var numberOfScansPerLog = 0
    private var loggingRequests = CompositeDisposable()
    var wm: WifiManager? = null


    override fun setBeaconManager(bm: BeaconManager) {
        beaconManager = bm
    }

    override fun start() {
        bluetoothStateDisposable = bluetoothState.asFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { e ->
                    if (e is Events.BluetoothState) {
                        view.updateBluetoothState(e.getBluetoothState(), bluetoothState.isEnabled)

                        if (e.getBluetoothState() == BeaconListActivity.BluetoothState.STATE_OFF) {
                            stopScan()
                        }else if((e.getBluetoothState() == BeaconListActivity.BluetoothState.STATE_ON) ){
                            startScan()
                        }
                    }
                }

                beaconResults = realm.getScannedBeacons()
                view.setAdapter(beaconResults)
                beaconResults.addChangeListener { results ->
                    if (results.isLoaded) {
                        view.showEmptyView(results.size == 0)
                    }
                }

        // Show the tutorial if needed
        /*if (!prefs.hasSeenTutorial()) {
            prefs.setHasSeenTutorial(view.showTutorial())
        }*/
        if (prefs.isScanOnOpen || prefs.wasScanning()) {  // Start scanning if the scan on open is activated or if we were previously scanning
            startScan()
        }
    }

    override fun filterList(query: String) {
        if (query.equals("")) {
            beaconResults = realm.getScannedBeacons()
            view.setAdapter(beaconResults)
        } else {
            beaconResults = realm.getScannedBeaconsName(query)
            beaconResults.addChangeListener { results ->
                if (results.isLoaded) {
                    if (results.isEmpty() == true) {
                        beaconResults = realm.getScannedBeacons()
                        view.setAdapter(beaconResults)
                    } else {
                        view.setAdapter(beaconResults)
                    }

                }
            }
        }
    }

    override fun toggleScan() {
        if (!isScanning()) {
            tracker.logEvent("start_scanning_clicked", null)
            return startScan()
        }
        tracker.logEvent("stop_scanning_clicked", null)
        stopScan()
    }
    override fun startScan() {
        spm = SharedPreferencesManager(mcon)
        mcon.startService(logupload)
        if (!view.hasCoarseLocationPermission()) {
            return view.askForCoarseLocationPermission()
        }

        if (!(beaconManager?.isBound(view) ?: false)) {
            beaconManager?.bind(view)
        }
        view.keepScreenOn(true)
        view.showScanningState(true)
        rangeDisposable?.dispose() // clear the previous subscription if any
        if (!bluetoothState.isEnabled || beaconManager == null) {
            return view.showBluetoothNotEnabledError()
        }

        rangeDisposable = rxBus.asFlowable() // Listen for range events
                .observeOn(AndroidSchedulers.mainThread()) // We use this so we use the realm on the good thread & we can make UI changes
                .filter({ e -> e is Events.RangeBeacon && e.beacons.isNotEmpty() })
                .subscribe({ e ->
                    e as Events.RangeBeacon
                    storeBeaconsAround(e.beacons, "0")
                }, { err ->
                    view.showGenericError(err.message ?: "generic error :")
                })

    }

    override fun onBeaconServiceConnect() {
        wm = mcon.getSystemService(WIFI_SERVICE) as WifiManager
        beaconManager?.addRangeNotifier { beacons, region -> rxBus.send(Events.RangeBeacon(beacons, region)) }
        try {
            beaconManager?.startRangingBeaconsInRegion(Region("com.wfi.beaconscanner", null, null, null))
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun onLocationPermissionGranted() {
        tracker.logEvent("permission_granted", null)
        startScan()
    }

    override fun onLocationPermissionDenied(requestCode: Int, permList: List<String>) {
        tracker.logEvent("permission_denied")

        // If the user refused the permission, we just disabled the scan on open
        prefs.isScanOnOpen = false
        if (view.hasSomePermissionPermanentlyDenied(permList)) {
            tracker.logEvent("permission_denied_permanently")
            view.showEnablePermissionSnackbar()
        }
    }

    override fun onRatingInteraction(step: Int, answer: Boolean) {
        if (!answer) { // The user answered "no" to any question
            return view.showRating(step, false)
        }

        when (step) {
            RatingHelper.STEP_ONE -> view.showRating(RatingHelper.STEP_TWO)
            RatingHelper.STEP_TWO -> {
                ratingHelper.setPopupSeen()
                view.redirectToStorePage()
                view.showRating(step, false)
            }
        }
    }

    override fun storeBeaconsAround(beacons: Collection<Beacon>, sstatus: String) {
        realm.executeTransactionAsync({ tRealm ->

            for (b: Beacon in beacons) {
                val beacon = BeaconSaved(b, sstatus) // Create a new object
                var beaconname=""
                try {
                    if(beacon.eddystoneUidData?.clone()?.instanceId!!.startsWith("0x")) {
                        beaconname = db.getBeaconNameByInstanceId(mDbHelper, beacon.eddystoneUidData?.clone()!!.instanceId!!.substring(2, beacon.eddystoneUidData?.clone()!!.instanceId!!.length))
                    }else {
                        beaconname = db.getBeaconNameByInstanceId(mDbHelper, beacon.eddystoneUidData?.clone()!!.instanceId)
                    }
                } catch(e:Exception){
                }
                val res = tRealm.getBeaconWithId(beacon.hashcode) // See if we scanned this beacon before
                res?.let {
                     // If we did, update the beacon logic fields
                        beacon.isBlocked = it.isBlocked
                    }
                try {
                    if (!beaconname.contentEquals(""))
                        beacon.beaconName = "\"" + beaconname + "\""
                    else
                        beacon.beaconName = "\"" + "" + "\""
                } catch(e:Exception){
                }
                tRealm.copyToRealmOrUpdate(beacon)
                tracker.logBeaconScanned(beacon.manufacturer, beacon.beaconType, beacon.distance)
            }
        }, null, { error: Throwable? ->
            view.showGenericError(error?.message ?: "generic:"+error?.message)
        })
    }


    var savedTime = ""
    var currentTime = ""
    var sec = 0L
    private lateinit var spm: SharedPreferencesManager
    override fun logToWebhookIfNeeded() {
        if (numberOfScansPerLog == 0) {
            verifyLogUpload()
        }
        if (!savedTime.equals("")) {
            numberOfScansPerLog++
            verifyLogUpload()
            try {
                val beaconToLog = realm.getBeaconsScannedAfter(prefs.lasLoggingCall)
                beaconToLog.addChangeListener {resp ->
                if (resp.isLoaded && resp.isNotEmpty()) { // && secChanged.equals("true")
                    prefs.lasLoggingCall = Date().time
                    resp.map { it.getData() }
                    if (Internet.hasInternet(mcon)) {
                        saveData(resp.toList(),doAddToBeaconLog(currentTime, prefs.isModeEnabled, prefs.loggingDeviceName ?: "", resp.toList(), "online"))
                    } else {
                        saveOfflineData(resp.toList(), "Offline", db,currentTime,sec)
                        statictext.text = "Receiving offline at "+ DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.getDefault()).format(Date(System.currentTimeMillis()))
                    }

                } else {
                    statictext.text = "No Beacon Found "
                    numberOfScansSinceLog = 0
                }

                realm.clearOldScannedBeacons(Date().time-(1000*30))
                view.setAdapter(beaconResults)
                beaconToLog.removeAllChangeListeners()
            }
           }catch(ex:Exception){
           }
        }
    }
    //********* upload response to send to server ************//*
    fun saveData(toList: List<BeaconSaved>,logs: JSONObject) {
        val Pdr = PostData(mcon, "" + logs, this, prefs.loggingEndpoint ?: "",toList)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Pdr.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                Pdr.execute();
            }
        }catch(ex: Exception){

        }
    }
    var ip_adress=""
    // add log data in json
    fun doAddToBeaconLog(ctime: String, mode: String, deviceid: String, toList: List<BeaconSaved>, status: String): JSONObject {
        try{
            ip_adress=Formatter.formatIpAddress(wm?.getConnectionInfo()!!.getIpAddress());
        }catch (ex:Exception){
            ip_adress=""
        }

        val obj = JSONObject()
        obj.put("maxDistance",spm.getLongValues(Keys.MaxLogDis))
        obj.put("sendtime", ctime)
        obj.put("mode", mode)
        obj.put("device_id", deviceid)
        obj.put("android_status", status)
        obj.put("gcm_id", spm.getStringValues(Keys.TOKEN))
        obj.put("beacons", toList)
        obj.put("ip_address",ip_adress )
        return obj
    }
    //********* collect response from server ************//*
    override fun onTaskComplete(result: String,dataSend:List<BeaconSaved>) {
        try {
            val obj = JSONObject(result)
            if (obj.getString("code") == "200") {
                statictext.text = "Log Status : Receiving online at " +DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.getDefault()).format(Date(System.currentTimeMillis()))
                saveInDataBase(obj.getString("logs"),db)
                Keys().saveLogs(obj.getString("AtendenceLog"), obj.getString("Log"), spm)
            } else {
                    try {
                    if (obj.getString("error").equals("Device not found")) {
                        mcon.stopService(Intent(mcon, DeviceUpdate::class.java))
                        spm.saveStringValues(Keys.CHECK, "")
                        spm.saveStringValues(Keys.DeviceStatus, "Deleted")
                        val am = mcon.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                        val taskInfo = am.getRunningTasks(1)
                        if(!taskInfo.get(0).topActivity.getClassName().equals("com.wfi.beaconscanner.features.startapp.AddToken")) {
                            val intentac = Intent(mcon, AddToken::class.java)
                            intentac.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intentac.putExtra("app_session", "deleted")
                            mcon.startActivity(intentac)
                        }
                    } else {
                        saveBeacon(obj.getString("logs"), db)
                        statictext.text = "Log Status : No log uploaded "
                    }
                }catch (ex: Exception) {
                        saveBeacon(obj.getString("logs"), db)
                        statictext.text = "Log Status : No log uploaded "
                }
            }
        } catch (ex: Exception) {
            saveOfflineData(dataSend, "Offline", db,currentTime,sec)
            statictext.text = "Log Status : Receiving offline at " +DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.getDefault()).format(Date(System.currentTimeMillis()))
        }
    }

    // used to find current datetime.
    fun verifyLogUpload(){
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val format = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss")
        val c = Calendar.getInstance()
        val ctime = df.format(c.time)
        if(savedTime.equals("")){
            savedTime = df.format(c.time)
        }
        val date1 = format.parse(ctime)
        val date2 = format.parse(savedTime)
        val difference = Math.abs(date2.time - date1.time)
        val spantime=spm.getIntValues("scan_time")
        sec = TimeUnit.MILLISECONDS.toSeconds(difference)
        currentTime =(c.timeInMillis/1000).toString()
        savedTime = df.format(c.time)
    }
    // used to save beacon data in database
    private fun saveInDataBase(logData: String, db:GetDataFrmDB) {
        try {
            val logdata = JSONArray(logData)
            for (i in 0 until logdata.length()) {
                val obj = logdata.get(i) as JSONObject
                if(db.getCountByInstanceId(mDbHelper,obj.getString("instanceId"))==0) {
                    mDbHelper.createBeacons(obj.getString("beaconName"), obj.getString("instanceId"))
                }else {
                    mDbHelper.updateBeaconName( obj.getString("instanceId"),obj.getString("beaconName"))
                }
            }
        } catch (ex: Exception) {
            println("" + ex.message)
        }
    }
    // used to save beacon data in database.
    private fun saveBeacon(logData: String, db:GetDataFrmDB) {
        try {
            val logdata = JSONArray(logData)
            for (i in 0 until logdata.length()) {
                val obj = logdata.get(i) as JSONObject
                if(db.getCountByInstanceId(mDbHelper,obj.getString("instanceId"))==0) {
                    mDbHelper.createBeacons(obj.getString("beaconName"), obj.getString("instanceId"))
                }else {
                    mDbHelper.updateBeaconName( obj.getString("instanceId"),obj.getString("beaconName"))
            }
            }
        } catch (ex: Exception) {
            println("" + ex.message)
        }
    }
    // activity interface : used to stop scanning
    override fun stopScan() {
        unbindBeaconManager()
        rangeDisposable?.dispose()
        mcon.stopService(logupload)
        view.showScanningState(false)
        view.keepScreenOn(false)
    }


    override fun onBluetoothToggle() {
        bluetoothState.toggle()
        tracker.logEvent("action_bluetooth")
    }

    override fun onSettingsClicked() {
        tracker.logEvent("action_settings")
        view.startSettingsActivity()
    }

    override fun onClearClicked() {
        tracker.logEvent("action_clear")
        view.showClearDialog()
    }

    override fun onClearAccepted() {
        tracker.logEvent("action_clear_accepted")
        realm.clearScannedBeacons()
    }

    fun isScanning() = !(rangeDisposable?.isDisposed ?: true)
    // activity lifecycle
    override fun stop() {
        prefs.setScanningState(isScanning())
        unbindBeaconManager()
        beaconResults.removeAllChangeListeners()
        loggingRequests.clear()
        bluetoothStateDisposable?.dispose()
        rangeDisposable?.dispose()
        view.keepScreenOn(false)
    }

    fun unbindBeaconManager() {
        if (beaconManager?.isBound(view) == true) {
            beaconManager?.unbind(view)
        }
    }


    override fun clear() {
        realm.close()
    }
    // used to save offline data in database.
    fun saveOfflineData(toList: List<BeaconSaved>, status: String, db: GetDataFrmDB, currentTime: String,sec:Long) {
        try {
            for (i in 0 until toList.size) {
                val tdm = toList.get(i)
                var instanceID=""
                if(tdm.eddystoneUidData?.clone()?.instanceId!!.startsWith("0x"))
                    instanceID = tdm.eddystoneUidData?.clone()?.instanceId!!.substring(2,tdm.eddystoneUidData?.clone()?.instanceId!!.length)
                else
                    instanceID = tdm.eddystoneUidData?.clone()?.instanceId.toString()

                mDbHelper.createLogs(tdm.distance.toString(), ""+spm.getLongValues(Keys.MaxLogDis), "Offline", prefs.isModeEnabled,
                        tdm.lastSeen.toString(),
                        instanceID, tdm.telemetryData?.clone()?.batteryMilliVolts.toString(), tdm.lastMinuteSeen.toString(),currentTime)
                if(db.getCountByInstanceId(mDbHelper,instanceID)==0) {
                    mDbHelper.createBeacons(tdm.beaconName, instanceID)
                }else {
                    mDbHelper.updateBeaconName(instanceID,tdm.beaconName)
                }
            }
        } catch (e: Exception) {
        }

    }
}



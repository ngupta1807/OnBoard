package com.bookmyride.services;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.api.ServiceHandlerInBack;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.fcm.NotificationFilters;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by vinod on 3/23/2017.
 */
public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public static String serverDateTime = "";
    private static final String TAG = "LocationService";

    private boolean currentlyProcessingLocation = false;
    private GoogleApiClient googleApiClient;

    double latitude;
    double longitude;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    private BroadcastReceiver tineZoneReceiver;
    public static IntentFilter intentFilter;

    static {
        intentFilter = new IntentFilter();
        //intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        //intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        serverDateTime = getCurrentDateTime();
        mLocationRequest = createLocationRequest();

        this.tineZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                        action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                    if (api != null) {
                        if (api.getStatus() == AsyncTask.Status.RUNNING)
                            api.cancel(true);
                    }
                    isTimeZoneChanged = true;
                }
            }
        };
        // Registers the receiver so that your service will listen for
        // broadcasts
        this.registerReceiver(this.tineZoneReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // if we are currently trying to get a location and the alarm manager has called this again,
        // no need to start processing a new location.
        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;
            startTracking();
        }

        return START_STICKY;
    }

    private void startTracking() {
        Log.d(TAG, "startTracking");
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int isGooglePlayServicesAvailable = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (isGooglePlayServicesAvailable == ConnectionResult.SUCCESS) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            Log.e(TAG, "unable to connect to google play services.");
        }
    }

    @Override
    public void onDestroy() {
        //stopLocationUpdates();
        this.unregisterReceiver(tineZoneReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            this.mLastLocation = location;
            // we have our desired accuracy of 500 meters so lets quit this service,
            // onDestroy will be called and stop our location uodates
            Log.e("Loc_service", location.getAccuracy() + ":" + location.getLatitude() + ":" + location.getLongitude());
            //if (location.getAccuracy() < 1000.0f) {
                //stopLocationUpdates();
                if (api != null) {
                    if (api.getStatus() == AsyncTask.Status.FINISHED) {
                        doUpdateGEOCoordinates("" + location.getLatitude(), "" + location.getLongitude());
                        updateGPSCoordinates();
                    }
                } else {
                    doUpdateGEOCoordinates("" + location.getLatitude(), "" + location.getLongitude());
                    updateGPSCoordinates();
                }
            //}
        }
    }

    public void updateGPSCoordinates() {
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
        }
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (mLastLocation != null) {
            longitude = mLastLocation.getLongitude();
        }
        return longitude;
    }

    private void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        Log.d(TAG, "Connection method has been called");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                            if (mLastLocation != null) {
                                Log.e("lat-lng", mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude());
                                if (mLastLocation.getAccuracy() < 500.0f) {
                                    //stopLocationUpdates();
                                    doUpdateGEOCoordinates("" + mLastLocation.getLatitude(), "" + mLastLocation.getLongitude());
                                    updateGPSCoordinates();
                                }
                                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, LocationService.this);
                            } else
                                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, LocationService.this);
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        googleApiClient, locationRequest, this);
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        }*/
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");

        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "GoogleApiClient connection has been suspend");
    }

    public static boolean isTimeZoneChanged = true;
    ServiceHandlerInBack api = null;

    private void doUpdateGEOCoordinates(final String latitude, final String longitude) {
        HashMap<String, String> params = new HashMap<>();
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        if (isTimeZoneChanged)
            params.put("updateTimeZone", "TimeZoneUpdate");
        Log.e("LOC_PARAM", params.toString());
        api = new ServiceHandlerInBack(getApplicationContext(), HTTPMethods.POST, new AsyncTaskCompleteListener() {
            @Override
            public void onTaskComplete(String result) {
                api = null;
                Log.e("LOC_RES", result);
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.getInt(Key.STATUS) == APIStatus.SESSION_EXPIRE) {
                        new SessionHandler(LocationService.this).saveToken("");
                        stopService(new Intent(LocationService.this, LocationService.class));
                    } else if (json.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                        isTimeZoneChanged = false;
                        JSONObject dataObj = json.getJSONObject(Key.DATA);
                        //update server date time
                        serverDateTime = dataObj.get("server_time").toString();
                        //update counts
                        Intent counts = new Intent(NotificationFilters.AVAILABLE_RIDE);
                        counts.putExtra("noOfRides", dataObj.get("available_booking").toString());
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(counts);
                        //update markers
                        Intent bookings = new Intent(NotificationFilters.AVAILABLE_BOOKINGS);
                        bookings.putExtra("bookings_list", dataObj.get("available_bookingList").toString());
                        bookings.putExtra("server_time", dataObj.get("server_time").toString());
                        bookings.putExtra("server_timezone", dataObj.get("server_timezone").toString());
                        bookings.putExtra("server_timezone_offset", dataObj.get("server_timezone_offset").toString());
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(bookings);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    stopService(new Intent(getApplicationContext(), LocationService.class));
                }
            }
        }, params);
        String authToken = new SessionHandler(LocationService.this).getToken();
        if (!authToken.equals("")) {
            api.execute(Config.UPDATE_DRIVER_GEO, new SessionHandler(getApplicationContext()).getToken());
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopLocationUpdates();
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        /*mLocationRequest.setInterval(8000);
        //mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setSmallestDisplacement(0.1f);*/
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }
}
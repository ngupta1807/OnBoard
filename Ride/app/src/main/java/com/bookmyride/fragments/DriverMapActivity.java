package com.bookmyride.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.bookmyride.R;
import com.bookmyride.activities.DriverHome;
import com.bookmyride.activities.RideDetail;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.api.ServiceHandlerInBack;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.fcm.AlarmReceiver;
import com.bookmyride.fcm.NotificationFilters;
import com.bookmyride.fcm.NotificationUtils;
import com.bookmyride.models.DriverCategory;
import com.bookmyride.services.RouteService;
import com.bookmyride.util.Bookings;
import com.bookmyride.util.LatLngInterpolator;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vinod on 1/19/2017.
 */
public class DriverMapActivity extends Fragment implements
        AsyncTaskCompleteListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    MapView mMapView;
    GoogleMap googleMap;

    double latitude, longitude;
    SessionHandler session;
    TextView goOffline;
    private Circle mCircle;
    private BroadcastReceiver mReceiver;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    ImageView gpsBtn;
    CardView layFleet;
    TextView fleetName, brand, model_number;
    boolean hasFleet;
    ImageView icon;
    private ClusterManager<Bookings> mClusterManager;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Timer timer = new Timer();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DriverHome.mTitle.setText("ONLINE:");
        DriverHome.availableRides.setVisibility(View.VISIBLE);
        DriverHome.availableRides.setText("Available\nRides: " + DriverHome.noOfRides);
        /*DriverHome.availableRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPreBooking();
            }
        });*/
        View view = inflater.inflate(R.layout.frag_driver_map, null);

        init(view);

        if (getArguments().containsKey("CategoryID"))
            categoryId = getArguments().getString("CategoryID");

        if (getArguments().containsKey("hasFleet")) {
            hasFleet = getArguments().getBoolean("hasFleet", false);
        }
        if (hasFleet)
            appendFleeData();
        setTitle();
        initMap(view, savedInstanceState);
        return view;
    }

    //Timer updateTimer = new Timer();
    double distance;
    double radiansBetweenAnnotations;
    String msg = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGoogleAPIClient();
    }

    private void init(View view) {
        session = new SessionHandler(getActivity());
        session.saveOnlineStatus(true);
        goOffline = (TextView) view.findViewById(R.id.done);
        goOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goOffline();
            }
        });
        gpsBtn = (ImageView) view.findViewById(R.id.gps);
        layFleet = (CardView) view.findViewById(R.id.lay_fleet);
        fleetName = (TextView) view.findViewById(R.id.fleet_name);
        brand = (TextView) view.findViewById(R.id.brand);
        model_number = (TextView) view.findViewById(R.id.name_number);
        icon = (ImageView) view.findViewById(R.id.icon);
        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLastLocation != null) {
                    LatLng coordinate = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    //Store these lat lng values somewhere. These should be constant.
                    CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                            coordinate, googleMap.getCameraPosition().zoom);
                    googleMap.animateCamera(location);
                }
            }
        });

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NotificationFilters.REQUEST_CANCELLED)) {
                    if (session.getUserType().equals("4")) {
                        try {
                            JSONObject obj = new JSONObject(intent.getStringExtra("rideData"));
                            msg = obj.getString("message");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    /*if (msg.contains("accepted by another"))
                                        showToast(msg);
                                    else*/
                                    showPopup(getActivity(), msg);
                                }
                            }, 500);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (intent.getAction().equals(NotificationFilters.AVAILABLE_BOOKINGS)) {
                    if (session.getUserType().equals("4")) {
                        String ridesData = intent.getStringExtra("bookings_list");
                        String serverDateTime = intent.getStringExtra("server_time");
                        String serverTimeZone = intent.getStringExtra("server_timezone");
                        String serverTimeZoneOffset = intent.getStringExtra("server_timezone_offset");
                        //handlingTimeZone(serverTimeZoneOffset, serverTimeZone);
                        handlingPool(ridesData);
                    }
                } else if (intent.getAction().equals(NotificationFilters.LOCATION_CHANGED)) {

                    double lat = intent.getDoubleExtra("lat", 0.0);
                    double lng = intent.getDoubleExtra("lng", 0.0);

                    //Place current location marker
                    LatLng latLng = new LatLng(lat, lng);
                    if (googleMap != null) {
                        float zoomLevel = googleMap.getCameraPosition().zoom;
                        if (zoomLevel < 10)
                            zoomLevel = 12.2f;
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
                        googleMap.moveCamera(cameraUpdate);

                        Location newLoc = new Location("");
                        newLoc.setLatitude(lat);
                        newLoc.setLongitude(lng);
                        mLastLocation = newLoc;

                        if (mCircle == null || mCurrLocationMarker == null) {
                            drawMarkerWithCircle(latLng);
                        } else {
                            updateMarkerWithCircle(latLng);
                        }
                    }
                }
            }
        };
        /* if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkLocationPermission())
                initializeMap();
        } else
            initializeMap(); */
    }

    boolean isDateTimePromptShowing = false;

    private String getTimezoneOffset() {
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());
        String offset = String.format("%02d:%02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
        offset = (offsetInMillis >= 0 ? "+" : "-") + offset;
        return offset;
    }

    private void openDateTimeSetting() {
        startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void handlingPool(String ridesData) {
        try {
            JSONArray outerArray = new JSONArray(ridesData);
            //googleMap.clear();
            if (outerArray.length() > 0) {
                distance = outerArray.length() / 2.0;
                radiansBetweenAnnotations = (Config.PI * 2) / outerArray.length();
            }
            if (mClusterManager != null)
                mClusterManager.clearItems();

            for (int i = 0; i < outerArray.length(); i++) {
                String puAddress, puLat, puLng;
                JSONObject innerObj = outerArray.getJSONObject(i);
                String pickup = innerObj.get("pickUp").toString();
                JSONObject pickObj = new JSONObject(pickup);
                puAddress = pickObj.getString("address");
                puLat = pickObj.getString("lat");
                puLng = pickObj.getString("lng");

                JSONObject markerData = new JSONObject();
                markerData.put("id", innerObj.get("id").toString());
                markerData.put("address", puAddress);

                double lat = Double.parseDouble(puLat);
                double lng = Double.parseDouble(puLng);
                final LatLng latLng = new LatLng(lat, lng);
                double heading = radiansBetweenAnnotations * i;

                final LatLng updatedLatLng = calculateCoordinateFrom(
                        latLng, distance, heading);

                mClusterManager.addItem(new Bookings(updatedLatLng,
                        markerData.toString(), puAddress));
            }
            mClusterManager.cluster();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private LatLng calculateCoordinateFrom(LatLng latLng, double distanceInMetres, double bearingInRadians) {
        double oldLat = latLng.latitude * Config.PI / 180;
        double oldLng = latLng.longitude * Config.PI / 180;

        double distanceComparedToEarth = distanceInMetres / 6378100;

        double resultLatitudeInRadians = Math.asin(Math.sin(oldLat)
                * Math.cos(distanceComparedToEarth)
                + Math.cos(oldLat)
                * Math.sin(distanceComparedToEarth)
                * Math.cos(bearingInRadians));
        double resultLongitudeInRadians = oldLng
                + Math.atan2(Math.sin(bearingInRadians)
                        * Math.sin(distanceComparedToEarth)
                        * Math.cos(oldLat),
                Math.cos(distanceComparedToEarth)
                        - Math.sin(oldLat)
                        * Math.sin(resultLatitudeInRadians));

        double newLat = resultLatitudeInRadians * 180 / Config.PI;
        double newLng = resultLongitudeInRadians * 180 / Config.PI;
        LatLng result = new LatLng(newLat, newLng);
        return result;
    }

    private void appendFleeData() {
        brand.setText(getArguments().getString("selectedModel"));
        model_number.setText(getArguments().getString("selectedNumber"));
        layFleet.setVisibility(View.VISIBLE);
        String vehicleNum = getArguments().getString("selectedNumber");
        String vehicleId = getArguments().getString("selectedVehicleId");
        session.saveDriverDategory(categoryId, vehicleId, vehicleNum);

        if (categoryId.equals("1")) {
            fleetName.setText("TAXI");
            fleetName.setBackgroundColor(Color.parseColor("#f9d214"));
            icon.setImageResource(R.drawable.taxi);
        } else if (categoryId.equals("2")) {
            fleetName.setText("ECONOMY");
            fleetName.setBackgroundColor(Color.parseColor("#78ad2c"));
            icon.setImageResource(R.drawable.economy);
        } else if (categoryId.equals("3")) {
            fleetName.setText("PREMIUM");
            fleetName.setBackgroundColor(Color.parseColor("#D42C15"));
            icon.setImageResource(R.drawable.premium);
        } else if (categoryId.equals("4")) {
            fleetName.setText("MOTOR BIKE");
            fleetName.setBackgroundColor(Color.parseColor("#0066b0"));
            icon.setImageResource(R.drawable.motor_bike);
        }
    }

    private void setTitle() {
        if (categoryId.equals("1"))
            DriverHome.mTitle.setText("ONLINE: TAXI");
        else if (categoryId.equals("2"))
            DriverHome.mTitle.setText("ONLINE: ECONOMY");
        else if (categoryId.equals("3"))
            DriverHome.mTitle.setText("ONLINE: PREMIUM");
        else if (categoryId.equals("4"))
            DriverHome.mTitle.setText("ONLINE: MOTOR BIKE");
    }

    String categoryId = "";

    private void initMap(View rootView, Bundle savedInstanceState) {
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                LatLng currentPos = new LatLng(latitude, longitude);
                if (mCircle == null || mCurrLocationMarker == null) {
                    drawMarkerWithCircle(currentPos);
                } else {
                    updateMarkerWithCircle(currentPos);
                }

                //Display markers for available rides
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        try {
                            if (marker.getTitle().length() > 0) {
                                JSONObject obj = new JSONObject(marker.getTitle());
                                startActivity(new Intent(getActivity(), RideDetail.class)
                                        .putExtra("booking_id", obj.getString("id")));
                                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                // Setting a custom info window adapter for the google map
                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    // Use default InfoWindow frame
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    // Defines the contents of the InfoWindow
                    @Override
                    public View getInfoContents(Marker marker) {
                        View view = null;
                        try {
                            if (marker.getTitle().length() > 0) {
                                view = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);
                                JSONObject obj = new JSONObject(marker.getTitle());
                                TextView title = (TextView) view.findViewById(R.id.title);
                                TextView address = (TextView) view.findViewById(R.id.address);
                                title.setText("RIDE_ID: " + obj.getString("id"));
                                address.setText(obj.getString("address"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        return view;
                    }
                });
                try {
                    mClusterManager = new ClusterManager<Bookings>(getActivity(), googleMap);
                    googleMap.setOnCameraIdleListener(mClusterManager);
                    //googleMap.setOnMarkerClickListener(mClusterManager);
                    mClusterManager.setRenderer(new ClusterRenderer(getActivity(), googleMap, mClusterManager));
                    mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<Bookings>() {
                        @Override
                        public boolean onClusterClick(Cluster<Bookings> cluster) {
                            Log.e("cluster", "Cluster Click");
                            return false;
                        }
                    });
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    double prevLat = 0.0, prevLng = 0.0;

    private void updateMarkerWithCircle(LatLng position) {
        //mCurrLocationMarker.setPosition(position);
        Location currLoc = new Location("curr");
        currLoc.setLatitude(position.latitude);
        currLoc.setLongitude(position.longitude);
        if (prevLat == 0.0 && prevLng == 0.0) {
            prevLat = position.latitude;
            prevLng = position.longitude;
        }
        Location prevLoc = new Location("prev");
        prevLoc.setLatitude(prevLat);
        prevLoc.setLongitude(prevLng);
        animateMarker(prevLoc, currLoc, mCurrLocationMarker);
        //animateCar(currLoc, mCurrLocationMarker);
        mCircle.setCenter(position);
        prevLat = position.latitude;
        prevLng = position.longitude;
    }

    private void drawMarkerWithCircle(LatLng position) {
        try {
            double radiusInMeters = 5000.0;
            int strokeColor = getResources().getColor(R.color.driver_color);
            int shadeColor = getResources().getColor(R.color.driver_color_tr);

            CircleOptions circleOptions = new CircleOptions().center(position)
                    .radius(radiusInMeters)
                    .fillColor(shadeColor)
                    .strokeColor(strokeColor)
                    .strokeWidth(3);
            mCircle = googleMap.addCircle(circleOptions);
            int resource;
            if (categoryId.equals("4"))
                resource = R.drawable.motor_bike_map;
            else if (categoryId.equals("1"))
                resource = R.drawable.taxi_map;
            else if (categoryId.equals("3"))
                resource = R.drawable.premium_map;
            else if (categoryId.equals("2"))
                resource = R.drawable.economy_map;
            else
                resource = R.drawable.carmove;
            MarkerOptions markerOptions = new MarkerOptions().position(position)
                    .icon(BitmapDescriptorFactory.fromResource(resource))
                    .title("")
                    .anchor(0.5f, 0.5f).flat(true);
            mCurrLocationMarker = googleMap.addMarker(markerOptions);

            /*float zoomLevel = googleMap.getCameraPosition().zoom;
            if(zoomLevel < 10)
                zoomLevel = 12.2f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, zoomLevel);
            googleMap.animateCamera(cameraUpdate);*/
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void goOffline() {
        type = 0;
        HashMap<String, String> params = new HashMap<>();
        params.put(Key.STATUS, "0");
        params.put("driverCategory_id", categoryId);
        if (hasFleet) {
            params.put(Key.VEHICLE_NUM, getArguments().getString("selectedNumber"));
            params.put(Key.VEHICLE_ID, getArguments().getString("selectedVehicleId"));
        } else {
            params.put(Key.VEHICLE_NUM, "0");
            params.put(Key.VEHICLE_ID, "0");
        }
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.PUT, this, params);
            apiHandler.execute(Config.DRIVER_AVAILABLITY + "0", session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                if (noActionDialog != null && noActionDialog.isShowing())
                    noActionDialog.dismiss();
                session.saveOnlineStatus(false);
                session.saveDriverDategory("", "", "");
                stopAlarm();
                Intent intent = new Intent(getActivity(), DriverHome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                /*if (!NotificationUtils.isAppIsInBackground(getActivity())) {
                    DriverHome.fm.popBackStack();
                }*/
            } else {
                Alert("Alert!", outJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(getActivity(), true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
        mDialog.show();
    }

    AlertDialog dateTimeDialog;

    private void dateTimePrompt(String title, String message) {
        dateTimeDialog = new AlertDialog(getActivity(), false);
        dateTimeDialog.setDialogTitle(title);
        dateTimeDialog.setDialogMessage(message);
        dateTimeDialog.setPositiveButton("Cancel",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dateTimeDialog.dismiss();
                        isDateTimePromptShowing = false;
                    }
                });
        dateTimeDialog.setNegativeButton("Open Settings",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isDateTimePromptShowing = false;
                        dateTimeDialog.dismiss();
                        openDateTimeSetting();
                    }
                });
        dateTimeDialog.show();
        isDateTimePromptShowing = true;
    }

    int type;

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
        if (mGoogleApiClient != null)
            if (!mGoogleApiClient.isConnected())
                mGoogleApiClient.connect();
        //timerCount = 0;
        //checkUserAction();
        startAlarm();
        if (session.isDriverEngage()) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    goOfflineInBack();
                }
            }, 15 * 60 * 1000);
            showNoActionPrompt();
            session.saveDriverDialogOpen(false);
        }
    }

    Toast mToast;

    private void startAlarm() {
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(getActivity(),
                1, intent, 0);

        // Schedule the alarm for half an hour!
        long interval = 30 * 60 * 1000;
        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        assert am != null;
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + interval,
                interval, sender);
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(getActivity(), "Alarm Registered.", Toast.LENGTH_LONG);
        //mToast.show();
    }

    private void stopAlarm() {
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(getActivity(),
                1, intent, 0);

        // And cancel the alarm.
        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        assert am != null;
        am.cancel(sender);
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(getActivity(), "Alarm Stopped.", Toast.LENGTH_LONG);
        //mToast.show();
    }

    //boolean userActionPerformed = false;

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver,
                new IntentFilter(NotificationFilters.REQUEST_CANCELLED));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver,
                new IntentFilter(NotificationFilters.AVAILABLE_BOOKINGS));

        /*LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver,
                new IntentFilter(NotificationFilters.LOCATION_CHANGED));*/

        NotificationUtils.clearNotifications(getActivity());
    }

    @Override
    public void onPause() {
        stopAlarm();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        //getActivity().stopService(new Intent(getActivity(), RouteService.class));
        super.onPause();
    }

    //int timerCount = 0;

    /*private void checkUserAction() {
        updateTimer.schedule(new TimerTask() {
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timerCount++;
                        Log.e("timerCount", "" + timerCount);
                        if (timerCount >= 15 && !userActionPerformed
                                && noActionDialog != null && noActionDialog.isShowing()) {
                            noActionDialog.dismiss();
                            userActionPerformed = false;
                            goOffline();
                        } else if (timerCount >= 30) {
                            timerCount = 0;
                            if (noActionDialog != null && noActionDialog.isShowing())
                                noActionDialog.dismiss();
                            if (NotificationUtils.isAppIsInBackground(getActivity())) {
                                createNotification("Ride24:7", "You haven't engaged with RIDE24:7 for 30 minutes. Click on GO OFFLINE if you don't want to get any rides now.");
                                playNotificationSound();
                            } else
                                showNoActionPrompt();
                        }
                    }
                });
            }
        }, 0, 60000);
    }*/

    AlertDialog noActionDialog;

    private void showNoActionPrompt() {
        noActionDialog = new AlertDialog(getActivity(), true);
        noActionDialog.setDialogTitle("Alert!");
        noActionDialog.setDialogMessage("You haven't engaged with BookMyRide for 30 minutes. Click on GO OFFLINE if you don't want to get any rides now.");
        noActionDialog.setNegativeButton("STAY ONLINE",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //timerCount = 0;
                        //userActionPerformed = true;
                        //startAlarm();
                        if (timer != null)
                            timer.cancel();
                        noActionDialog.dismiss();
                    }
                });
        noActionDialog.setPositiveButton("GO OFFLINE",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //userActionPerformed = true;
                        if (timer != null)
                            timer.cancel();
                        noActionDialog.dismiss();
                        goOffline();
                    }
                });
        try {
            noActionDialog.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        //if (updateTimer != null)
        //    updateTimer.cancel();
        stopAlarm();
        DriverHome.availableRides.setVisibility(View.GONE);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        getActivity().stopService(new Intent(getActivity(), RouteService.class));
        super.onDestroy();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        /*if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }*/
                        googleMap.setMyLocationEnabled(false);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        //initializeMap();
                    }
                } else {
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                    getFragmentManager().popBackStack();
                }
                return;
            }
        }
    }

    public static void animateMarker(final Location destination, final Marker marker) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        marker.setRotation(computeRotation(v, startRotation, destination.getBearing()));

                    } catch (Exception ex) {
                        // I don't care atm..
                    }
                }
            });

            valueAnimator.start();
        }
    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }


    public void animateMarker(final Location prevLoc, final Location currLoc,
                              final Marker marker) {
        if (marker != null && currLoc != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(currLoc.getLatitude(), currLoc.getLongitude());

            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        //marker.setRotation(computeRotation(v, startRotation, currLoc.getBearing()));
                        marker.setFlat(true);
                        float bearing = (float) bearingBetweenLocations(
                                new LatLng(prevLoc.getLatitude(), prevLoc.getLongitude())
                                , new LatLng(currLoc.getLatitude(), currLoc.getLongitude()));
                        rotateMarker(marker, bearing);
                    } catch (Exception ex) {
                        // I don't care atm..
                    }
                }
            });

            valueAnimator.start();
        }
    }

    private float whichWayToTurn(float currentDirection, float targetDirection) {
        float diff = targetDirection - currentDirection;
        if (Math.abs(diff) == 0) {
            return 0;
        }
        if (diff > 180) {
            return -1;
        } else {
            return 1;
        }
    }

    boolean isMarkerRotating = false;

    private double bearingBetweenLocations(LatLng oldLatLng, LatLng newLatLng) {

        double PI = 3.14159;
        double lat1 = oldLatLng.latitude * PI / 180;
        double long1 = oldLatLng.longitude * PI / 180;
        double lat2 = newLatLng.latitude * PI / 180;
        double long2 = newLatLng.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    private void rotateMarker(final Marker marker, final float toRotation) {
        if (!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 2000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    float bearing = -rot > 180 ? rot / 2 : rot;

                    marker.setRotation(bearing);
                    //Toast.makeText(getApplicationContext(), "Rotation:->"+bearing, Toast.LENGTH_SHORT).show();
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }

    private void animateCar(final Location destination, final Marker marker) {

        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            // final float startRotation = marker.getRotation();
            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);

                        marker.setRotation(getBearing(startPosition, new LatLng(destination.getLatitude(), destination.getLongitude())));
                    } catch (Exception ex) {
                        //I don't care atm..
                    }
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                }
            });
            valueAnimator.start();
        }
    }

    //Method for finding bearing between two points
    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    public class ClusterRenderer extends DefaultClusterRenderer<Bookings> {
        public ClusterRenderer(Context context, GoogleMap map, ClusterManager<Bookings> clusterManager) {
            super(context, map, clusterManager);
        }

        /*@Override
        protected void onBeforeClusterRendered(Cluster<Bookings> cluster, MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker_icon2));
        }*/
        @Override
        protected void onBeforeClusterItemRendered(Bookings item, MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.available_ride));
            markerOptions.snippet(item.getSnippet());
            markerOptions.title(item.getTitle());
            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }

    public static void showPopup(final Activity ctx, String msg) {
        final Dialog mDialog = new Dialog(ctx, R.style.rideDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_payment);
        TextView title = (TextView) mDialog.findViewById(R.id.txt_msg);
        TextView message = (TextView) mDialog.findViewById(R.id.msg);
        RelativeLayout Rl_ok = (RelativeLayout) mDialog.findViewById(R.id.ok);

        message.setText(msg);
        title.setText("Alert!");
        Rl_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
        /*if(getIntent().hasExtra("end"))
            dismissDialog();*/
    }
    /*Ending the updates for the location service*/

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null)
            if (mGoogleApiClient.isConnected())
                mGoogleApiClient.disconnect();
        mMapView.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        settingRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("googleapi", "Connection Suspended!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("googleapi", "Connection Failed!");
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), 90000);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Current Location", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /*Method to get the enable location settings dialog*/
    public void settingRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(6000);
        mLocationRequest.setFastestInterval(16);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case 1000:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.e("location", "Location Service not Enabled");
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    public void getLocation() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
            } else {
            /*Getting the location after aquiring location service*/
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (mLastLocation != null && googleMap != null) {
            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            float zoomLevel = googleMap.getCameraPosition().zoom;
            if (zoomLevel < 10)
                zoomLevel = 12.2f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
            googleMap.moveCamera(cameraUpdate);
            if (mCircle == null || mCurrLocationMarker == null) {
                drawMarkerWithCircle(latLng);
            } else {
                updateMarkerWithCircle(latLng);
            }
        } else {
                /*if there is no last known location. Which means the device has no data for the loction currently.
                * So we will get the current location.
                * For this we'll implement Location Listener and override onLocationChanged*/
            Log.i("Current Location", "No data for location found");

            if (!mGoogleApiClient.isConnected())
                mGoogleApiClient.connect();
            if (mGoogleApiClient.isConnected())
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /* When Location changes, this method get called. */
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        Log.e("locationChanged", "" + location.getLatitude() + "" + location.getLongitude());
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (googleMap != null) {
            float zoomLevel = googleMap.getCameraPosition().zoom;
            if (zoomLevel < 10)
                zoomLevel = 12.2f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
            googleMap.moveCamera(cameraUpdate);
            if (mCircle == null || mCurrLocationMarker == null) {
                drawMarkerWithCircle(latLng);
            } else {
                updateMarkerWithCircle(latLng);
            }

        }
    }

    protected synchronized void buildGoogleApiClient() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int isGooglePlayServicesAvailable = googleApiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (isGooglePlayServicesAvailable == ConnectionResult.SUCCESS) {

            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            /*if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }*/
        } else {
            //Log.e(TAG, "unable to connect to google play services.");
        }
    }

    private void initGoogleAPIClient() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            }
        } else {
            buildGoogleApiClient();
        }
    }

    private void goOfflineInBack() {
        DriverCategory dc = session.getOnlineDriverData();
        HashMap<String, String> params = new HashMap<>();
        params.put(Key.STATUS, "0");
        params.put("driverCategory_id", dc.getDriverCateogry());
        params.put(Key.VEHICLE_NUM, dc.getVehicleNum());
        params.put(Key.VEHICLE_ID, dc.getVehicleId());

        if (Internet.hasInternet(getContext())) {
            ServiceHandlerInBack apiHandler = new ServiceHandlerInBack(getActivity(), HTTPMethods.PUT, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    try {
                        JSONObject outJson = new JSONObject(result);
                        if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                            if (noActionDialog != null && noActionDialog.isShowing())
                                noActionDialog.dismiss();
                            session.saveOnlineStatus(false);
                            session.saveDriverDategory("", "", "");
                            stopAlarm();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    displayDriverMap(new Bundle());
                                }
                            });
                        } else {
                            Alert("Alert!", outJson.getString(Key.MESSAGE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, params);
            apiHandler.execute(Config.DRIVER_AVAILABLITY + "0", session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void displayDriverMap(Bundle bundle) {
        Fragment fragment = new com.bookmyride.fragments.DriverDashboard();
        fragment.setArguments(bundle);
        FragmentTransaction ft = DriverHome.fm.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commitAllowingStateLoss();
    }
}

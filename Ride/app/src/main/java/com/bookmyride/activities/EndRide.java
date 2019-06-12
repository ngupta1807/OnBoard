package com.bookmyride.activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.bookmyride.R;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIHandlerInBack;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.api.ServiceHandlerInBack;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.fragments.AcceptPayment;
import com.bookmyride.map.DirectionsJSONParser;
import com.bookmyride.models.WaitingInfo;
import com.bookmyride.util.LatLngInterpolator;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by vinod on 1/16/2017.
 */
public class EndRide extends AppCompatActivity implements View.OnTouchListener,
        SeekBar.OnSeekBarChangeListener, OnMapReadyCallback,
        AsyncTaskCompleteListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    SeekBar sliderSeekBar;
    ShimmerButton slider;
    Shimmer shimmer;
    //TrackGPS gps;
    double latitude, longitude;
    GoogleMap googleMap;
    String bookingId, passengerName, passengerPhone, driverCategory;
    SessionHandler session;
    TextView startWait, stopWait;
    TextView waitTime;
    RelativeLayout timerLay;
    private int mins;
    private int secs;

    private Handler customHandler = new Handler();
    private boolean waitingStatus = false;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    private long startTime = 0L;
    private LatLng fromPosition;
    private LatLng toPosition;
    TextView rideId, name/*, mobile*/, pmtStatus;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    String bookingType, paymentStatus, operatorPhone = "", bookedBy = "", cardValid = "";
    LinearLayout layContact;
    boolean isDiscount = false;
    CardView cardInfo;
    TextView doAddres;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_ride);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initGoogleAPIClient();

        init();
        driverCategory = getIntent().getStringExtra("driverCategory");
        isDiscount = getIntent().getBooleanExtra("is_discount", false);
        updateSliderUI();
        /*if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }*/
        bookingId = getIntent().getStringExtra("bookingId");
        passengerName = getIntent().getStringExtra("passenger_name");
        passengerPhone = getIntent().getStringExtra("passenger_phone");
        //driverCategory = getIntent().getStringExtra("driverCategory");
        rideId.setText("RIDE_ID: " + bookingId);
        name.setText("Passenger: " + passengerName);
        //mobile.setText("");
        String dropOff = getIntent().getStringExtra("dropOff");
        try {
            JSONObject dropObj = new JSONObject(dropOff);
            doAddress = dropObj.getString("address");
            doLat = dropObj.getString("lat");
            doLng = dropObj.getString("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        doAddres.setText(doAddress);
        String pickup = getIntent().getStringExtra("pickUp");
        try {
            JSONObject pickObj = new JSONObject(pickup);
            puAddress = pickObj.getString("address");
            puLat = pickObj.getString("lat");
            puLng = pickObj.getString("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        paymentStatus = getIntent().getStringExtra("payment_status");
        bookingType = getIntent().getStringExtra("type");
        if (paymentStatus.equals("1")) {
            float scale = getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (10 * scale + 0.5f);
            pmtStatus.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
            pmtStatus.setVisibility(View.VISIBLE);
        } else pmtStatus.setVisibility(View.GONE);

        WaitingInfo waitingInfo = session.getWaitingInfo();
        updatedTime = waitingInfo.getUpdatedTime();
        timeInMilliseconds = waitingInfo.getTimeInMilliseconds();
        startTime = waitingInfo.getStartTime();
        timeSwapBuff = waitingInfo.getTimeSwapBuff();
        mins = waitingInfo.getMins();
        secs = waitingInfo.getSecs();
        waitTime.setText(mins + ":" + String.format("%02d", secs));
        if (secs > 0 || mins > 0)
            timerLay.setVisibility(View.VISIBLE);

        getDriverGEO();
    }

    private void getDriverGEO() {
        type = 0;
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this, null);
            apiHandler.execute(Config.DRIVER_GEO + bookingId + "?expand=drivergeo,passenger", session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void init() {
        session = new SessionHandler(this);
        //gps = new TrackGPS(this);
        shimmer = new Shimmer();
        sliderSeekBar = (SeekBar) findViewById(R.id.end_Trip_seek);
        slider = (ShimmerButton) findViewById(R.id.end_Trip_slider_button);
        shimmer.start(slider);
        sliderSeekBar.setOnSeekBarChangeListener(this);
        rideId = (TextView) findViewById(R.id.ride_id);
        name = (TextView) findViewById(R.id.name);
        cardInfo = (CardView) findViewById(R.id.lay_info);
        //mobile = (TextView) findViewById(R.id.mobile);
        pmtStatus = (TextView) findViewById(R.id.payment_status);
        startWait = (TextView) findViewById(R.id.start_wait);
        startWait.setOnClickListener(this);
        stopWait = (TextView) findViewById(R.id.stop_wait);
        stopWait.setOnClickListener(this);
        doAddres = (TextView) findViewById(R.id.do_address);
        waitTime = (TextView) findViewById(R.id.timerValue);
        timerLay = (RelativeLayout) findViewById(R.id.layout_timer);
        layContact = (LinearLayout) findViewById(R.id.lay_contact);
        layContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showRideDialog();
                if (operatorPhone.equals("") && operatorPhone.equals("null")) {
                    Alert("Alert!", "Not a valid phone number.");
                } else
                    callPassenger();
            }
        });
        cardInfo.setCardElevation(5);
        cardInfo.setMaxCardElevation(6);
        cardInfo.setRadius(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_wait:
                stopWait.setVisibility(View.VISIBLE);
                startWait.setVisibility(View.GONE);
                timerLay.setVisibility(View.VISIBLE);
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
                waitingStatus = true;
                break;
            case R.id.stop_wait:
                startWait.setVisibility(View.VISIBLE);
                stopWait.setVisibility(View.GONE);
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);
                waitingStatus = false;
        }
    }

    private Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            secs = (int) (updatedTime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            if (mins >= 60) {
                mins = 00;
            }
            waitTime.setText(mins + ":" + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }
    };

    public String getSeconds(String time) {
        String[] units = time.split(":"); //will break the string up into an array
        int minutes = Integer.parseInt(units[0]); //first element
        int seconds = Integer.parseInt(units[1]); //second element
        int duration = 60 * minutes + seconds; //add up our values
        return "" + duration;
    }

    private void drawRoute() {
        if (!doLat.equals("") && !doLng.equals("")) {
            fromPosition = new LatLng(Double.parseDouble(puLat), Double.parseDouble(puLng));
            toPosition = new LatLng(Double.parseDouble(doLat), Double.parseDouble(doLng));
            if (fromPosition != null && toPosition != null) {
                googleMap.addMarker(new MarkerOptions()
                        .position(fromPosition)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot)));
                googleMap.addMarker(new MarkerOptions()
                        .position(toPosition)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_dot)));
                String url = getDirectionsUrl(fromPosition, null, toPosition);
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);
            }
        }
    }

    private void initializeMap() {
        if (googleMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            try {
                mapFragment.getMapAsync(this);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Move the camera to last position with a zoom level
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        /*} else {
            Alert("Alert!", getResources().getString(R.string.alert_gpsEnable));
        }*/

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //buildGoogleApiClient();
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } else {
            //buildGoogleApiClient();
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        drawRoute();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        /*if (i > 15) {
            seekBar.setThumb(getResources().getDrawable(R.drawable.slider_taxi));
        }*/
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        slider.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getProgress() > 90) {
            sliderSeekBar.setProgress(2);
            getDistance();
        } else {
            sliderSeekBar.setProgress(2);
        }
        updateSliderUI();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    private void updateSliderUI() {
        if (driverCategory.equals("1"))
            sliderSeekBar.setThumb(getResources().getDrawable(R.drawable.slider_taxi));
        else if (driverCategory.equals("2"))
            sliderSeekBar.setThumb(getResources().getDrawable(R.drawable.slider_economy));
        else if (driverCategory.equals("3"))
            sliderSeekBar.setThumb(getResources().getDrawable(R.drawable.slider_premium));
        else if (driverCategory.equals("4"))
            sliderSeekBar.setThumb(getResources().getDrawable(R.drawable.slider_motor_bike));
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    String distance;
    String duration, destination;

    private void handleResponse(String result) {
        try {
            Log.v("result","result:"+result);
            JSONObject outJson = new JSONObject(result);
            if (outJson.get(Key.STATUS).toString().equalsIgnoreCase("ok")) {
                JSONObject distanceObj = outJson.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
                distance = distanceObj.getJSONObject("distance").getString("value");
                duration = distanceObj.getJSONObject("duration").getString("value");
                destination = distanceObj.getString("end_address");
                endRide();
            } else if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                if (type == 0) {
                    JSONObject dataObj = outJson.getJSONObject(Key.DATA);
                    paymentStatus = dataObj.get("paymentStatus").toString();
                    if (dataObj.has("operatorPhone"))
                        operatorPhone = dataObj.get("operatorPhone").toString();
                    if (paymentStatus.equals("1")) {
                        float scale = getResources().getDisplayMetrics().density;
                        int dpAsPixels = (int) (10 * scale + 0.5f);
                        pmtStatus.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                        pmtStatus.setVisibility(View.VISIBLE);
                    } else pmtStatus.setVisibility(View.GONE);

                    if (dataObj.has("paymentDetail")) {
                        String paymentInfo = dataObj.get("paymentDetail").toString();
                        if (!paymentInfo.equals("") && !paymentInfo.equals("null")) {
                            JSONObject payObj = new JSONObject(paymentInfo);
                            if (payObj.has("isCardValid"))
                                cardValid = payObj.get("isCardValid").toString();
                            else cardValid = "";
                        }
                    }

                    if (cardValid.equals("0")) {
                        pmtStatus.setText("Credit Card Invalid");
                        pmtStatus.setBackgroundResource(R.drawable.rounded_red);
                        float scale = getResources().getDisplayMetrics().density;
                        int dpAsPixels = (int) (10 * scale + 0.5f);
                        pmtStatus.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                        pmtStatus.setVisibility(View.VISIBLE);
                    } else pmtStatus.setVisibility(View.GONE);

                    if (dataObj.has("bookedBy"))
                        bookedBy = dataObj.get("bookedBy").toString();

                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkLocationPermission())
                            initializeMap();
                    } else
                        initializeMap();
                } else if (type == 1) {
                    Error("Success!", "Thanks for using BookMyRide.");
                } else if (type == 3) {
                    Alert("Success!", "Message has sent to driver.");
                } else if (bookingType.equals("1") && paymentStatus.equals("1")) {
                    Success("Success!", "Your ride is complete. Fixed price has been pre-paid to operator on booking.");
                } else {
                    //proceedAfterEnding();
                    paymentStatus = outJson.getJSONObject(Key.DATA).get("paymentStatus").toString();
                    String paymentMessage = outJson.getJSONObject(Key.DATA).getString("payment_message");
                    if (paymentStatus.equals("1")) {
                        //Success("Success!", "Your ride is complete. Fixed price has been pre-paid to operator on booking.");
                        proceedAfterEnding();
                    } else if (bookedBy.equals("0")) {
                        paymentFailed("Alert!", paymentMessage);
                    } else {
                        proceedAfterEnding();
                    }
                    /*else if (paymentStatus.equals("0")) {
                        paymentFailureAlert("Alert!", paymentMessage);
                    } else {
                        JSONObject fareDetail = outJson.getJSONObject(Key.DATA).getJSONObject("fareDetail");
                        if (bookingType.equals("1") && (paymentStatus.equals("0") || paymentStatus.equals("null"))) {
                            //Intent intent = new Intent(EndRide.this, FareBreakdown.class);
                            Intent intent = new Intent(EndRide.this, AcceptPayment.class);
                            intent.putExtra("type", bookingType);
                            intent.putExtra("bookingID", getIntent().getStringExtra("bookingId"));
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            session.saveWaitingInfo(0L, 0L, 0L, 0L, 0, 0);
                            EndRide.this.finish();
                            //payByCash();
                        } else {
                            Intent intent = new Intent(EndRide.this, Waiting.class);
                            intent.putExtra("bookingID", bookingId);
                            intent.putExtra("total", fareDetail.getString("currency") + fareDetail.getString("total"));
                            intent.putExtra("duration", duration);
                            intent.putExtra("distance", distance);
                            intent.putExtra("waitTime", waitSec);
                            intent.putExtra("driverCategory", driverCategory);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            session.saveWaitingInfo(0L, 0L, 0L, 0L, 0, 0);
                            EndRide.this.finish();
                        }
                    }*/
                }
            } else if (outJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                Error("Alert!", outJson.getString(Key.MESSAGE));
            } else {
                Error("Alert!", outJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void paymentFailureAlert(String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(EndRide.this, false);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setCancelOnTouchOutside(false);
        mDialog.setNegativeButton("Alternate Payment", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent = new Intent(EndRide.this, AcceptPayment.class);
                intent.putExtra("type", bookingType);
                intent.putExtra("bookingID", getIntent().getStringExtra("bookingId"));
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                session.saveWaitingInfo(0L, 0L, 0L, 0L, 0, 0);
                EndRide.this.finish();
            }
        });
        if (bookedBy.equals("0")) {
            mDialog.setPositiveButton("Accept by Passenger", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    Intent intent = new Intent(EndRide.this, Waiting.class);
                    intent.putExtra("bookingID", bookingId);
                    intent.putExtra("total", "");
                    intent.putExtra("duration", duration);
                    intent.putExtra("distance", distance);
                    intent.putExtra("waitTime", waitSec);
                    intent.putExtra("driverCategory", driverCategory);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    session.saveWaitingInfo(0L, 0L, 0L, 0L, 0, 0);
                    EndRide.this.finish();
                }
            });
        }
        mDialog.show();
    }

    private void paymentFailed(String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(EndRide.this, false);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setCancelOnTouchOutside(false);
        mDialog.setNegativeButton("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                proceedAfterEnding();
            }
        });
        mDialog.show();
    }

    private void Error(final String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(EndRide.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (title.equals("Success!")) {
                    Intent intent = new Intent(getApplicationContext(), DriverHome.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    EndRide.this.finish();
                }
            }
        });
        mDialog.show();
    }

    private void proceedAfterEnding() {
        /*if (driverCategory.equals("2") ||
                driverCategory.equals("3") ||
                driverCategory.equals("4")) {*/
        Intent intent = new Intent(EndRide.this, FareBreakdown.class);
        intent.putExtra("type", bookingType);
        intent.putExtra("bookingID", getIntent().getStringExtra("bookingId"));
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        session.saveWaitingInfo(0L, 0L, 0L, 0L, 0, 0);
        EndRide.this.finish();
        /*} else {
            Intent intent = new Intent(getApplicationContext(), DriverHome.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            EndRide.this.finish();
        }*/
    }

    private void Success(String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(EndRide.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                proceedAfterEnding();
            }
        });
        mDialog.show();
    }

    int type;
    String pickUp, dropOff;
    String puLat, puLng, doLat, doLng, puAddress, doAddress;

    private void getDistance() {
        pickUp = getIntent().getStringExtra("pickUp");
        dropOff = getIntent().getStringExtra("dropOff");
        try {
            JSONObject obj = new JSONObject(pickUp);
            puLat = obj.get("lat").toString();
            puLng = obj.get("lng").toString();
            puAddress = obj.getString("address");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject obj = new JSONObject(dropOff);
            doLat = obj.get("lat").toString();
            doLng = obj.get("lng").toString();
            doAddress = obj.getString("address");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        type = 5;
        if (Internet.hasInternet(this)) {
            String source = "" + puLat + "," + puLng;
            String destination = "&destination=" + latitude + "," + longitude;
            String endPoint = Config.DISTANCE_BY_GOOGLE + source + destination;
            APIHandlerInBack apiHandler = new APIHandlerInBack(this, HTTPMethods.GET, this, null);
            apiHandler.execute(Config.DISTANCE_BY_GOOGLE + source + destination, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    String waitSec = "0";

    private void Alert(String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(EndRide.this, true);
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

    private void prompt(String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(EndRide.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setNegativeButton("Proceed with Google",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        openGoogleMap(doLat, doLng);
                    }
                });
        mDialog.setPositiveButton("Proceed with Waze",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        openWazeApp(doLat, doLng);
                    }
                });
        mDialog.show();
    }

    private void endRide() {
        JSONObject drop = new JSONObject();
        try {
            drop.put("address", destination);
            drop.put("lat", latitude);
            drop.put("lng", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!waitTime.getText().toString().equals("0:00")) {
            waitSec = getSeconds(waitTime.getText().toString());
        }
        if (getIntent().getStringExtra("driverCategory").equals("1")
                /*&& bookingType.equals("1")*/ && !paymentStatus.equals("1") && !isDiscount) {
            startActivity(new Intent(this, MeterReading.class)
                    .putExtra("waitTime", waitSec)
                    .putExtra("dropOff", drop.toString())
                    .putExtra("distance", distance)
                    .putExtra("type", bookingType)
                    .putExtra("payment_status", paymentStatus)
                    .putExtra("duration", duration)
                    .putExtra("bookedBy", bookedBy)
                    .putExtra("driverCategory", driverCategory)
                    .putExtra("bookingId", bookingId));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            session.saveWaitingInfo(0L, 0L, 0L, 0L, 0, 0);
            EndRide.this.finish();
        } else {
            HashMap<String, String> jsonParams = new HashMap<String, String>();
            jsonParams.put("status", "5");
            jsonParams.put("waitTime", waitSec);
            jsonParams.put("dropOff", drop.toString());
            jsonParams.put("distance", distance);
            jsonParams.put("duration", duration);
            if (Internet.hasInternet(this)) {
                APIHandler apiHandler = new APIHandler(this, HTTPMethods.PUT, this, jsonParams);
                apiHandler.execute(Config.BOOKING_STATUS + bookingId, session.getToken());
            } else
                Error("Alert!", getResources().getString(R.string.no_internet));
        }
    }

    Marker carMarker;
    double prevLat = 0.0, prevLng = 0.0;
    int i = 0;
    final Handler h = new Handler();

    private void updateLocation() {
        h.postDelayed(locationRunnable, 10000);
    }

    double dLat = 0.0;
    double dLng = 0.0;
    Runnable locationRunnable = new Runnable() {
        @Override
        public void run() {
            ServiceHandlerInBack handler = new ServiceHandlerInBack(EndRide.this, HTTPMethods.GET, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        if (obj.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                            JSONObject innerObj = obj.getJSONObject(Key.DATA).getJSONObject("drivergeo");
                            String currentLatLng = innerObj.getString("currentLocation");
                            if (currentLatLng != null && !currentLatLng.equals("")) {
                                JSONObject driverGEO = new JSONObject(currentLatLng);
                                dLat = Double.parseDouble(driverGEO.getString("latitude"));
                                dLng = Double.parseDouble(driverGEO.getString("longitude"));

                                Location currLoc = new Location("curr");
                                currLoc.setLatitude(dLat);
                                currLoc.setLongitude(dLng);
                                int resource;
                                if (carMarker == null) {
                                    if (driverCategory.equals("4"))
                                        resource = R.drawable.motor_bike_nav;
                                    else if (driverCategory.equals("1"))
                                        resource = R.drawable.taxi_nav;
                                    else if (driverCategory.equals("3"))
                                        resource = R.drawable.premium_nav;
                                    else if (driverCategory.equals("2"))
                                        resource = R.drawable.economy_nav;
                                    else resource = R.drawable.taxi_nav;

                                    carMarker = googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(dLat, dLng))
                                            .anchor(0.5f, 0.5f).flat(true)
                                            .icon(BitmapDescriptorFactory.fromResource(resource)));
                                }
                                if (prevLat == 0.0 && prevLng == 0.0) {
                                    prevLat = dLat;
                                    prevLng = dLat;
                                }
                                Location prevLoc = new Location("prev");
                                prevLoc.setLatitude(prevLat);
                                prevLoc.setLongitude(prevLng);
                                animateMarker(prevLoc, currLoc, carMarker);
                                prevLat = dLat;
                                prevLng = dLng;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
            handler.execute(Config.DRIVER_GEO + bookingId + "?expand=drivergeo", session.getToken());
            h.postDelayed(this, 10000);
        }
    };

    @Override
    protected void onDestroy() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() &&
                mLocationRequest != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        session.saveWaitingInfo(updatedTime, timeInMilliseconds, startTime, timeSwapBuff, mins, secs);
        //h.removeCallbacks(locationRunnable);
        if (googleMap != null) {
            googleMap.clear();
            googleMap = null;
        }
        super.onDestroy();
    }

    public void animateMarker(final Location prevLoc, final Location currLoc, final Marker marker) {
        if (marker != null && currLoc != null) {

            /*final LatLng endPosition = new LatLng(currLoc.getLatitude(), currLoc.getLongitude());

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = new ValueAnimator();
            //final LatLng startPosition = marker.getPosition();
            final LatLng startPosition = new LatLng(prevLoc.getLatitude(), prevLoc.getLongitude());
            final float startRotation = marker.getRotation();
            final float angle = 180 - Math.abs(Math.abs(startRotation - currLoc.getBearing()) - 180);
            final float right = whichWayToTurn(startRotation, currLoc.getBearing());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        float rotation = startRotation + right * v * angle;
                        marker.setPosition(newPosition);
                        marker.setFlat(true);
                        //marker.setRotation((float) rotation);
                        //Toast.makeText(getApplicationContext(), ""+prevLoc.bearingTo(currLoc), Toast.LENGTH_LONG).show();
                        //marker.setRotation(prevLoc.bearingTo(currLoc));
                        float bearing = (float) bearingBetweenLocations(
                                new LatLng(prevLoc.getLatitude(), prevLoc.getLongitude())
                                , new LatLng(currLoc.getLatitude(), currLoc.getLongitude()));
                        rotateMarker(marker, bearing);
                    } catch (Exception ex) {
                        // I don't care atm..
                        ex.printStackTrace();
                    }
                }
            });
            valueAnimator.setFloatValues(0, 1);
            valueAnimator.setDuration(3000);
            valueAnimator.start();*/
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

    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

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

    public static void animateMarker(final Location destination, final Marker marker) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(1000); // duration 1 second
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


    public void onCVClick(View view) {
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        /*mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(16);
        mLocationRequest.setSmallestDisplacement(0.1f);*/
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    float zoomLevel = 15;

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        mLastLocation = location;
        Log.e("end_loc", location.getAccuracy() + "<>" + location.getSpeed());
        //if (location.getAccuracy() < 100.0 && location.getSpeed() < 6.95) {
            //Place current location marker
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (googleMap != null) {
                if (mCurrLocationMarker == null) {
                    drawMarker(latLng);
                } else {
                    updateMarker(latLng);
                    zoomLevel = googleMap.getCameraPosition().zoom;
                    if (zoomLevel < 15)
                        zoomLevel = 15;
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng)
                            .zoom(zoomLevel)
                            .bearing(15)
                            .tilt(10)
                            .build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    googleMap.animateCamera(cameraUpdate);
                }
            }
            if (line != null) {
                double tolerance = 50;
                boolean isLocationOnPath = PolyUtil.isLocationOnEdge(latLng, line.getPoints(), true, tolerance);
                //Toast.makeText(getApplicationContext(), "OnRoute: "+isLocationOnPath, Toast.LENGTH_SHORT).show();
                if (!isLocationOnPath) {
                    //Toast.makeText(getApplicationContext(), "updating Route", Toast.LENGTH_SHORT).show();
                    String url = getDirectionsUrl(fromPosition, latLng, toPosition);
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);
                }
            }
        //}
    }

    private void drawMarker(LatLng position) {
        int resource;
        if (driverCategory.equals("4"))
            resource = R.drawable.motor_bike_nav;
        else if (driverCategory.equals("1"))
            resource = R.drawable.taxi_nav;
        else if (driverCategory.equals("3"))
            resource = R.drawable.premium_nav;
        else if (driverCategory.equals("2"))
            resource = R.drawable.economy_nav;
        else
            resource = R.drawable.taxi_nav;
        MarkerOptions markerOptions = new MarkerOptions().position(position)
                .icon(BitmapDescriptorFactory.fromResource(resource))
                .anchor(0.5f, 0.5f).flat(true);
        mCurrLocationMarker = googleMap.addMarker(markerOptions);

        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 12);
        //googleMap.animateCamera(cameraUpdate);
    }

    private void updateMarker(LatLng position) {
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
        //animateMarker(currLoc, mCurrLocationMarker);
        prevLat = position.latitude;
        prevLng = position.longitude;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
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

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            //buildGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(false);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                        //initializeMap();
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng wayPoint, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters;
        if (wayPoint != null) {
            String wayPoints = "waypoints=optimize:true|" + wayPoint.latitude + "," + wayPoint.longitude + "|";
            parameters = str_origin + "&" + wayPoints + "&" + str_dest + "&" + sensor;
        } else {
            parameters = str_origin + "&" + str_dest + "&" + sensor;
        }
        String mode = "mode=driving&transit_routing_preference=less_driving&";
        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + mode + parameters + "&key=" + Config.PLACE_API;
        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to download data from Google Directions URL
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    Polyline line;

    /**
     * A class to parse the Google Directions in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            if (line != null) {
                line.remove();
            }
            try {
                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                        //Log.e("poly:", j + ":" + lat + "-" + lng);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.geodesic(true);
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(Color.RED);
                }
                // Drawing polyline in the Google Map for the i-th route
                line = googleMap.addPolyline(lineOptions);

                LatLngBounds.Builder bc = new LatLngBounds.Builder();
                for (LatLng item : points) {
                    bc.include(item);
                }
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() &&
                mLocationRequest != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    protected void onStart() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void showRideDialog() {
        final Dialog mDialog = new Dialog(EndRide.this, R.style.rideDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list);
        mDialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("Select Contact Type");

        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mDialog.dismiss();
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDialog.dismiss();
            }
        });

        ListView dialog_ListView = (ListView) mDialog.findViewById(R.id.list);
        ArrayAdapter<String>
                adapter = new ArrayAdapter<>(this,
                R.layout.simple_list_item, R.id.textItem, getContactType());

        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("SMS")) {
                    showMessageDialog();
                } else if (parent.getItemAtPosition(position).toString().equals("CALL")) {
                    callPassenger();
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void callPassenger() {
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            if (!checkCallPhonePermission() || !checkReadStatePermission()) {
                requestPermission();
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + operatorPhone));
                startActivity(callIntent);
            }
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + operatorPhone));
            startActivity(callIntent);
        }
    }

    private boolean checkCallPhonePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkReadStatePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }

    final int PERMISSION_REQUEST_CODE = 111;

    private void showMessageDialog() {
        LayoutInflater inflater = LayoutInflater.from(EndRide.this);
        View subView = inflater.inflate(R.layout.message_dialog, null);
        final EditText subEditText = (EditText) subView.findViewById(R.id.bid_price);
        TextView proceed = (TextView) subView.findViewById(R.id.send);
        TextView cancel = (TextView) subView.findViewById(R.id.cancel);
        AlertDialog.Builder builder = new AlertDialog.Builder(EndRide.this);
        builder.setView(subView);
        final AlertDialog alertDialog = builder.create();
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subEditText.getText().toString().length() > 0) {
                    alertDialog.dismiss();
                    sendMessageToDriver(subEditText.getText().toString());
                } else
                    Alert("Alert!", "Please enter message.");
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void sendMessageToDriver(String message) {
        type = 3;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("msgFrom", session.getUserID());
        params.put("msgTo", passengerPhone);
        params.put("booking_id", bookingId);
        params.put("msg", message);
        //Log.e("param", params.toString());
        APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, params);
        apiHandler.execute(Config.SEND_MESSAGE_DRIVER, session.getToken());
    }

    public String[] getContactType() {
        String[] gateway = getResources().getStringArray(R.array.contact_via);
        return gateway;
    }

    private void payByCash() {
        type = 1;
        HashMap<String, String> requestParam = new HashMap<>();
        requestParam.put("bookingId", bookingId);
        requestParam.put("gateway", "cash");
        requestParam.put("tip", "0");
        requestParam.put("isSavedCard", "0");
        if (Internet.hasInternet(this)) {
            APIHandler apiCall = new APIHandler(this, HTTPMethods.POST, this, requestParam);
            apiCall.execute(Config.ACCEPT_BY_CASH, session.getToken());
        } else
            Error("Alert!", getResources().getString(R.string.no_internet));
    }

    public void openGoogleMap(View view) {
        boolean isAppInstalled = appInstalledOrNot("com.waze");
        if (isAppInstalled)
            prompt("Alert!", "Please choose preferred navigation app:");
        else
            openGoogleMap(doLat, doLng);
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            PackageInfo appInfo = pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void openGoogleMap(String doLat, String doLng) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=" + doLat + "," + doLng));
        intent.setPackage("com.google.android.apps.maps");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // Launch Waze to look for Navigation
    private void openWazeApp(String doLat, String doLng) {
        try {
            //String url = "geo: " + doLat + "," + doLng;
            String url = "https://waze.com/ul?ll=" + doLat + "," + doLng + "&navigate=yes";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
            startActivity(intent);
        }
    }

    //Initializing Google Play Services
    private void initGoogleAPIClient() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            }
        } else {
            buildGoogleApiClient();
        }
    }
}
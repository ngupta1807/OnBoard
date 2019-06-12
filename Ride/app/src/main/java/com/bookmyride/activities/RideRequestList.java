package com.bookmyride.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bookmyride.api.APIHandlerInBack;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.bookmyride.R;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.api.ServiceHandlerInBack;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.fcm.NotificationFilters;
import com.bookmyride.fcm.NotificationUtils;
import com.bookmyride.map.DirectionsJSONParser;
import com.bookmyride.services.LocationService;
import com.bookmyride.services.TrackGPS;
import com.bookmyride.views.AlertDialog;
import com.google.api.client.util.IOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

/**
 * Created by vinod on 1/19/2017.
 */
public class RideRequestList extends AppCompatActivity implements AsyncTaskCompleteListener {
    String bookingId, pickUP, dropOff, driverCategory;
    SessionHandler session;
    private BroadcastReceiver mReceiver;
    String bookingType, driverID = "";
    ImageView cross;
    TextView rideDistance, pickupDistance;
    String rideInfo;
    TextView title, rideID, message, puAddress, doAddress, puDate, puTime, header, price;
    RelativeLayout layASAP, layPUDate;
    LinearLayout accept, reject;
    TrackGPS gps;

    @Override
    protected void onStart() {
        super.onStart();
        freeMemory();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_later_request);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.e("createRide", "createRide");
        init();
        if (gps.canGetLocation()) {
            currentLat = "" + gps.getLatitude();
            currentLng = "" + gps.getLongitude();
        }
        if (getIntent().hasExtra("rideData")) {
            rideInfo = getIntent().getStringExtra("rideData");
            getIntent().removeExtra("rideData");
            session.saveRideData("");
            try {
                JSONObject rideData = new JSONObject(rideInfo);
                bookingType = rideData.getString("type");

                if (rideData.has("driver_id"))
                    driverID = rideData.getString("driver_id");
                /*if (bookingType.equals("1") && driverID.equals(""))
                    appendRideLater(rideData);
                else if (bookingType.equals("1") && !driverID.equals(""))
                    showMessage();
                else
                    appendData(rideData);*/
                appendRideLater(rideData);
                if (bookingType.equals("1") && !driverID.equals(""))
                    showMessage();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showMessage() {
        Intent intent = new Intent(this, NotificationDialogs.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("add_extra", "");
        intent.putExtra("rideData", rideInfo);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        RideRequestList.this.finish();
    }

    String msg = "";
    String cancelledRideID = "";

    private void init() {
        session = new SessionHandler(this);
        gps = new TrackGPS(this);
        cross = (ImageView) findViewById(R.id.cross);
        title = (TextView) findViewById(R.id.title);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RideRequestList.this.finish();
            }
        });
        mCircleView = (CircleProgressView) findViewById(R.id.timer);
        rideID = (TextView) findViewById(R.id.ride_id);
        message = (TextView) findViewById(R.id.message);
        puAddress = (TextView) findViewById(R.id.pu_address);
        doAddress = (TextView) findViewById(R.id.do_address);
        accept = (LinearLayout) findViewById(R.id.accept);
        reject = (LinearLayout) findViewById(R.id.reject);
        puDate = (TextView) findViewById(R.id.pu_date);
        puTime = (TextView) findViewById(R.id.pu_time);
        rideDistance = (TextView) findViewById(R.id.ride_distance);
        pickupDistance = (TextView) findViewById(R.id.pickup_distance);
        header = (TextView) findViewById(R.id.header);
        price = (TextView) findViewById(R.id.price);
        layASAP = (RelativeLayout) findViewById(R.id.lay_asap);
        layPUDate = (RelativeLayout) findViewById(R.id.lay_pudate);
        mCircleView.setEnabled(false);
        mCircleView.setFocusable(false);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 1;
                acceptRejectRide("1");
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 2;
                acceptRejectRide("2");
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
                            cancelledRideID = obj.getString("bookingId");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mCircleView != null && mHandler != null) {
                                        mCircleView.stopSpinning();
                                        mHandler.removeCallbacks(mRunnable);
                                    }
                                    startActivity(new Intent(getApplicationContext(), RideCancelledDialog.class)
                                            .putExtra("message", msg));
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    RideRequestList.this.finish();

                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (intent.getAction().equals(NotificationFilters.LOCATION_CHANGED)) {

                    double lat = intent.getDoubleExtra("lat", 0.0);
                    double lng = intent.getDoubleExtra("lng", 0.0);
                    currentLat = "" + lat;
                    currentLng = "" + lng;
                    if (pickupDistance.equals("")) {
                        double puDist = getRideDistance(lat, lng,
                                Double.parseDouble(puLat),
                                Double.parseDouble(puLng));
                        String dist = new DecimalFormat("##.##").format(puDist);
                        pickupDistance.setText("Pickup: " + dist + " km");
                    }
                    //Place current location marker
                    LatLng latLng = new LatLng(lat, lng);
                    if (googleMap != null && line == null) {
                        float zoomLevel = googleMap.getCameraPosition().zoom;
                        if (zoomLevel < 10)
                            zoomLevel = 12.2f;
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
                        googleMap.moveCamera(cameraUpdate);
                        drawRoute();
                    }
                }
            }
        };
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    private long getTimerValue(String createdDateTime, String serverTime) {
        String currentDateTime = getCurrentDateTime();
        // Custom date format
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(createdDateTime);
            //d2 = format.parse(currentDateTime);
            //d2 = format.parse(serverTime);
            d2 = format.parse(LocationService.serverDateTime);
            // Get msec from each, and subtract.
            long diff = d2.getTime() - d1.getTime();
            long diffSeconds = diff / 1000;
            long diffMinutes = diff / (60 * 1000);
            long diffHours = diff / (60 * 60 * 1000);
            return diffSeconds;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void initTimer(int seconds) {
        this.seconds = seconds;
        mCircleView.setEnabled(false);
        mCircleView.setFocusable(false);
        mCircleView.setMaxValue(seconds);
        mCircleView.setValueAnimated(0);
        mCircleView.setAutoTextSize(true);
        mCircleView.setTextScale(0.6f);
        mCircleView.setInnerContourColor(getResources().getColor(R.color.white));
        mCircleView.setOuterContourColor(getResources().getColor(R.color.white));
        mCircleView.setFillCircleColor(getResources().getColor(R.color.title_color));
        mCircleView.setTextColor(getResources().getColor(R.color.white));
        mHandler = new Handler(Looper.myLooper());
        mHandler.post(mRunnable);
    }

    Handler mHandler;
    CircleProgressView mCircleView;
    String statusInfo = "";
    JSONObject ride;
    private void appendRideLater(JSONObject ride) {
        this.ride=ride;
        cross.setVisibility(View.GONE);
        mCircleView.setEnabled(false);
        mCircleView.setFocusable(false);
        try {
            bookingId = ride.getString("bookingId");
            pickUP = ride.getString("pickUp");
            dropOff = ride.getString("dropOff");
            driverCategory = ride.getString("driverCategory_id");
            if (ride.has(Key.ASAP))
                statusInfo = ride.getString(Key.ASAP);

            JSONObject puAddressObj = new JSONObject(ride.getString("pickUp"));
            puLat = puAddressObj.get("lat").toString();
            puLng = puAddressObj.get("lng").toString();
            puAddress.setText(puAddressObj.getString("address"));
            if (!currentLat.equalsIgnoreCase("") && !currentLng.equalsIgnoreCase("")) {
                getDistance(Double.parseDouble(currentLat),
                        Double.parseDouble(currentLng),
                        Double.parseDouble(puLat),
                        Double.parseDouble(puLng),"Pickup");
            }
            if (ride.has("dropOff")) {
                try {
                    JSONObject doAddressObj = new JSONObject(ride.getString("dropOff"));
                    doAddress.setText(doAddressObj.getString("address"));
                    doLat = doAddressObj.get("lat").toString();
                    doLng = doAddressObj.get("lng").toString();
                    doAddress.setVisibility(View.VISIBLE);
                    getDistance(Double.parseDouble(puLat),
                            Double.parseDouble(puLng),
                            Double.parseDouble(doLat),
                            Double.parseDouble(doLng),"Ride");
                }
                catch (Exception ex){

                }
            } else doAddress.setVisibility(View.GONE);

            if (statusInfo.equalsIgnoreCase("1") || bookingType.equals("0")) {
                //header.setText("");
                if (bookingType.equals("1"))
                    layASAP.setVisibility(View.VISIBLE);
                long seconds = getTimerValue(ride.get("created_at").toString(), ride.get("server_time").toString());
                if (seconds >= 30) {
                    //initTimer(30);
                    openPreBooking();
                } else if (seconds <= 30) {
                    int sec = 30;
                    if (seconds > 0)
                        sec = 30 - (int) seconds;
                    initMap();
                    initTimer(sec);
                }
            } else {
                layPUDate.setBackgroundColor(Color.parseColor("#79AD2E"));
                title.setText("New Pre-Booked Request");
                header.setText("Pre-Booked Request");
                layASAP.setVisibility(View.GONE);
                long seconds = getTimerValue(ride.get("created_at").toString(), ride.get("server_time").toString());
                if (seconds >= 60) {
                    openPreBooking();
                    //initTimer(60);
                } else if (seconds <= 60) {
                    int sec = 60;
                    if (seconds > 0)
                        sec = 60 - (int) seconds;
                    initMap();
                    initTimer(sec);
                }
            }

            rideID.setText("RIDE_ID: " + bookingId);
            if (ride.has("price"))
                price.setText("$" + ride.get("price").toString());
            String puDateTime = ride.getString("pickUpDate");
            //puDate.setText(puDateTime);
            if (ride.has("distance"))
                rideDistance.setText("Ride: " + ride.get("distance").toString() + " km");
            if (!puDateTime.equals("null") && !puDateTime.equals("")) {
                String day = puDateTime.substring(0, puDateTime.indexOf(" "));
                String time = puDateTime.substring(puDateTime.indexOf(" ") + 1, puDateTime.length());
                puDate.setText(day);
                puTime.setText(time);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //getDistance();
    }

    private void openPreBooking() {
        Intent intent = new Intent(this, DriverHome.class);
        intent.putExtra("pre", "2");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        RideRequestList.this.finish();
    }

    private void goHome() {
        /*Intent intent = new Intent(this, DriverHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);*/
        RideRequestList.this.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    GoogleMap googleMap;

    private void initMap() {
        if (googleMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.ride_map);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    Log.e("Map", "Initialize");
                    googleMap = map;
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                            new LatLng(Double.parseDouble(puLat), Double.parseDouble(puLng)), 12);
                    googleMap.moveCamera(cameraUpdate);
                    drawRoute();
                }
            });
        }
    }

    String puLat = "", puLng = "", doLat = "", doLng = "", currentLat = "", currentLng = "";

    private void drawRoute() {
        if (!doLat.equals("") && !doLng.equals("")
                && !puLat.equals("") && !puLng.equals("")) {
            LatLng fromPosition = new LatLng(Double.parseDouble(puLat), Double.parseDouble(puLng));
            LatLng toPosition = new LatLng(Double.parseDouble(doLat), Double.parseDouble(doLng));
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

    int type;

    private void acceptRejectRide(String status) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("status", status);
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.PUT, this, jsonParams);
            apiHandler.execute(Config.ACCEPT_BOOKING + bookingId, session.getToken());
        } else
            Error("Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    String distance = "";

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.get(Key.STATUS).toString().equalsIgnoreCase("ok")) {
                JSONObject distanceObj = outJson.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
                distance = distanceObj.getJSONObject("distance").getString("text");
                if (distance.contains(" m")) {
                    String dist = distance.substring(0, distance.indexOf(" "));
                    double distVal = Double.parseDouble(dist);
                    double actDist = distVal / 1000;
                    if (pickupDistance != null) {
                        pickupDistance.setText("Pickup: " + actDist + " km");
                    }
                } else {
                    if (pickupDistance != null) {
                        pickupDistance.setText("Pickup: " + distance);
                    }
                }
                //duration = distanceObj.getJSONObject("duration").getString("value");
            } else if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                if (type == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mCircleView != null && mHandler != null) {
                                mCircleView.stopSpinning();
                                mHandler.removeCallbacks(mRunnable);
                            }
                        }
                    });
                    if (bookingType.equals("1")) {
                        if (statusInfo.equalsIgnoreCase("1")) {
                            openDriverArrived(outJson);
                        } else {
                            msg = outJson.getString(Key.MESSAGE);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(getApplicationContext(), RideCancelledDialog.class)
                                                    .putExtra("message", msg).putExtra("title", "Success!"));
                                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                            RideRequestList.this.finish();
                                        }
                                    }, 100);
                                }
                            });
                        }
                    } else {
                        openDriverArrived(outJson);
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mCircleView != null && mHandler != null) {
                                mCircleView.stopSpinning();
                                mHandler.removeCallbacks(mRunnable);
                            }
                            goHome();
                        }
                    });
                    //RideRequestList.this.finish();
                    //goHome();
                }
            } else if (outJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                Alert("Alert!", outJson.getString(Key.MESSAGE));
            } else {
                Error("Alert!", outJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Success(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(RideRequestList.this, true);
        mDialog.setDialogTitle(title);
        if (statusInfo.equalsIgnoreCase("1"))
            mDialog.setDialogMessage("Thanks for accepting new booking request. Click on OK to get booking details.");
        else mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                goHome();
            }
        });
        mDialog.show();
    }

    private void openDriverArrived(JSONObject outerJson) {
        try {
            JSONObject datObj = outerJson.getJSONObject(Key.DATA);
            startActivity(new Intent(RideRequestList.this, DriverArrived.class)
                    //startActivity(new Intent(RideRequestList.this, StartRiding.class)
                    .putExtra("bookingId", bookingId)
                    .putExtra("pickUp", pickUP)
                    .putExtra("type", bookingType)
                    .putExtra("payment_status", "0")
                    .putExtra("driverCategory", driverCategory)
                    .putExtra("passenger_name", datObj.getString("name"))
                    .putExtra("passenger_phone", datObj.getString("phone"))
                    .putExtra("dropOff", dropOff));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            RideRequestList.this.finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Error(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(RideRequestList.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(RideRequestList.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                RideRequestList.this.finish();
            }
        });
        mDialog.show();
    }

    int count = 0, seconds;
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (count < seconds) {
                count++;
                mCircleView.setText(String.valueOf(Math.abs(seconds - count)) + "sec");
                mCircleView.setTextMode(TextMode.TEXT);
                mCircleView.setValueAnimated(count, 500);
                mHandler.postDelayed(this, 1000);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCircleView != null && mHandler != null) {
                            mCircleView.stopSpinning();
                            mHandler.removeCallbacks(mRunnable);
                        }
                        //mHandler.removeCallbacks(mRunnable);
                        goHome();
                    }
                });

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(NotificationFilters.REQUEST_CANCELLED));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(NotificationFilters.LOCATION_CHANGED));
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        googleMap = null;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
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
            String waypoints = "waypoints=optimize:true|" + wayPoint.latitude + "," + wayPoint.longitude + "|";
            parameters = str_origin + "&" + waypoints + "&" + str_dest + "&" + sensor;
        } else {
            parameters = str_origin + "&" + str_dest + "&" + sensor;
        }
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + Config.PLACE_API;
        //Log.e("endPoint:", url);
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
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
            }
            if (googleMap != null) {
                // Drawing polyline in the Google Map for the i-th route
                try {
                    line = googleMap.addPolyline(lineOptions);
                }catch (Exception ex){

                }
                LatLngBounds.Builder bc = new LatLngBounds.Builder();
                for (LatLng item : points) {
                    bc.include(item);
                }
                try {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    private void getDistance() {
        if (Internet.hasInternet(this)) {
            String source = "" + currentLat + "," + currentLng;
            String destination = "&destination=" + puLat + "," + puLng;
            ServiceHandlerInBack apiHandler = new ServiceHandlerInBack(this, HTTPMethods.GET, this, null);
            apiHandler.execute(Config.DISTANCE_BY_GOOGLE + source + destination, session.getToken());
        } else
            Error("Alert!", getResources().getString(R.string.no_internet));
    }

    //Getting distance in kilometers (km)
    public static double getRideDistance(
            double lat1, double lng1, double lat2, double lng2) {
        int r = 6371; // average radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c;
        return d;
    }



    String dist="";
    public void getDistance(final double lat1, final double lon1, final double lat2, final double lon2, final String type){

        if (Internet.hasInternet(this)) {
            String source = "" + lat1 + "," + lon1;
            String destination = "&destination=" + lat2 + "," + lon2;
            String endPoint = Config.DISTANCE_BY_GOOGLE + source + destination;
            APIHandlerInBack apiHandler = new APIHandlerInBack(this, HTTPMethods.GET, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    JSONObject outJson = null;
                    try {
                        outJson = new JSONObject(result);
                        if (outJson.get(Key.STATUS).toString().equalsIgnoreCase("ok")) {
                            JSONObject distanceObj = outJson.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
                            distance = distanceObj.getJSONObject("distance").getString("text");
                            if (distance.contains("km")) {
                                  dist = distance.substring(0, distance.indexOf(" "));
                                  if(type.equals("Pickup")) {
                                      pickupDistance.setText("Pickup: " + dist + " km");
                                  }else {
                                      rideDistance.setText("Ride: " + dist + " km");
                                  }





                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                }, null);
            apiHandler.execute(Config.DISTANCE_BY_GOOGLE + source + destination, session.getToken());
        } else
            Error("Alert!", getResources().getString(R.string.no_internet));

            //duration = distanceObj.getJSONObject("duration").getString("value");
        }



    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

   /* public class getData extends AsyncTask<String,String,String>{
        double lat1;
        double lon1;
        double lat2;
        double lon2;
        getData(double lat1,  double lon1,  double lat2,  double lon2){
            lat1=this.lat1;
            lon1=this.lon1;
            lat2=this.lat2;
            lon2=this.lon2;
        }

        @Override
        protected String doInBackground(String... strings) {
            String val=getDistance(lat1,lat1,lat1,lat1);
            return val;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pickupDistance.setText("Pickup: " + s + " km");
            if (ride.has("dropOff")) {
                try {
                    JSONObject doAddressObj = new JSONObject(ride.getString("dropOff"));
                    doAddress.setText(doAddressObj.getString("address"));
                    doLat = doAddressObj.get("lat").toString();
                    doLng = doAddressObj.get("lng").toString();
                    doAddress.setVisibility(View.VISIBLE);
                    *//*String val=getDistance(Double.parseDouble(puLat),
                        Double.parseDouble(puLng),
                         Double.parseDouble(doLat),
                        Double.parseDouble(doLng));*//*
                    rideDistance.setText("Ride: " + pickupDistance.getText().toString() + " km");
                   *//*String dist = new DecimalFormat("##.##").format(rideDist);
                rideDistance.setText("Ride: " + dist + " km");*//*
                }
                catch (Exception ex){

                }
            } else doAddress.setVisibility(View.GONE);
        }
    }*/

}


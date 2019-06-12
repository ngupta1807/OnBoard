package com.bookmyride.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.bookmyride.api.APIHandlerInBack;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.fcm.NotificationFilters;
import com.bookmyride.fcm.NotificationUtils;
import com.bookmyride.map.DirectionsJSONParser;
import com.bookmyride.services.TrackGPS;
import com.bookmyride.views.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
public class MultipleRideRequest extends AppCompatActivity implements AsyncTaskCompleteListener {
    private LinearLayout listView;
    LayoutInflater inflater;
    String bookingId, pickUP, dropOff, driverCategory;
    SessionHandler session;
    private BroadcastReceiver mReceiver;
    String bookingType, driverID = "";
    ImageView cross;
    TrackGPS gps;
    TextView rideDistance, pickupDistance;
    String rideInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_request_list);
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
                if (bookingType.equals("1") && !driverID.equals(""))
                    showMessage();
                else
                    appendData(rideData);
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
        MultipleRideRequest.this.finish();
    }

    String msg = "";

    private void init() {
        session = new SessionHandler(this);
        gps = new TrackGPS(this);
        cross = (ImageView) findViewById(R.id.cross);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultipleRideRequest.this.finish();
            }
        });
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listView = (LinearLayout) findViewById(R.id.requestList);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NotificationFilters.REQUEST_CANCELLED)) {
                    if (session.getUserType().equals("4")) {
                        try {
                            JSONObject obj = new JSONObject(intent.getStringExtra("rideData"));
                            msg = obj.getString("message");
                            if (mCircleView != null && mHandler != null) {
                                mCircleView.stopSpinning();
                                mHandler.removeCallbacks(mRunnable);
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(getApplicationContext(), RideCancelledDialog.class)
                                            .putExtra("message", msg));
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    MultipleRideRequest.this.finish();
                                }
                            }, 500);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } /*else if (intent.getAction().equals(NotificationFilters.INCOMING_RIDE_REQUEST)) {
                    if (session.getUserType().equals("4")) {
                        try {
                            JSONObject rideData = new JSONObject(intent.getStringExtra("rideData"));
                            appendData(rideData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }*/
            }
        };
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    private long getTimerValue(String createdDateTime) {
        String currentDateTime = getCurrentDateTime();
        // Custom date format
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(createdDateTime);
            d2 = format.parse(currentDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Get msec from each, and subtract.
        long diff = d2.getTime() - d1.getTime();
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        return diffSeconds;
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
        mHandler = new Handler();
        mHandler.post(mRunnable);
    }

    Handler mHandler;
    CircleProgressView mCircleView;
    String statusInfo = "";

    private void openPreBooking() {
        Intent intent = new Intent(this, DriverHome.class);
        intent.putExtra("pre", "2");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        MultipleRideRequest.this.finish();
    }

    private void goHome() {
        Intent intent = new Intent(this, DriverHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        MultipleRideRequest.this.finish();
    }

    GoogleMap googleMap;

    private void initMap() {
        if (googleMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    googleMap = map;
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                            new LatLng(Double.parseDouble(currentLat), Double.parseDouble(currentLng)), 12);
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

    private void appendData(JSONObject ride) {
        View rideView = inflater.inflate(R.layout.ride_request, null);
        mCircleView = (CircleProgressView) rideView.findViewById(R.id.timer);
        TextView rideID = (TextView) rideView.findViewById(R.id.ride_id);
        //TextView message = (TextView) rideView.findViewById(R.id.message);
        TextView puAddress = (TextView) rideView.findViewById(R.id.pu_address);
        TextView doAddress = (TextView) rideView.findViewById(R.id.do_address);
        LinearLayout accept = (LinearLayout) rideView.findViewById(R.id.accept);
        LinearLayout reject = (LinearLayout) rideView.findViewById(R.id.reject);
        TextView puDate = (TextView) rideView.findViewById(R.id.pu_date);
        TextView puTime = (TextView) rideView.findViewById(R.id.pu_time);
        rideDistance = (TextView) rideView.findViewById(R.id.ride_distance);
        pickupDistance = (TextView) rideView.findViewById(R.id.pickup_distance);
        mCircleView.setEnabled(false);
        mCircleView.setFocusable(false);
        //initTimer(30);
        try {
            bookingId = ride.getString("bookingId");
            pickUP = ride.getString("pickUp");
            dropOff = ride.getString("dropOff");
            driverCategory = ride.getString("driverCategory_id");
            JSONObject puAddressObj = new JSONObject(ride.getString("pickUp"));
            puLat = puAddressObj.get("lat").toString();
            puLng = puAddressObj.get("lng").toString();
            puAddress.setText(puAddressObj.getString("address"));

            long seconds = getTimerValue(ride.get("created_at").toString());
            if (seconds >= 30) {
                openPreBooking();
            } else if (seconds <= 30) {
                int sec = 30;
                if (seconds > 0)
                    sec = 30 - (int) seconds;
                initTimer(sec);
                if (seconds > 5)
                    initMap();
            }
            /*long seconds = getTimerValue(ride.get("created_at").toString());
            if(seconds <= 30){
                initTimer((int)seconds);
            }*/

            if (ride.has("distance"))
                rideDistance.setText("Ride: " + ride.get("distance").toString() + "km");
            /*if(ride.has("dropOff")){
                JSONObject doAddressObj = new JSONObject(ride.getString("dropOff"));
                doAddress.setText(doAddressObj.getString("address"));
                doAddress.setVisibility(View.VISIBLE);
            } else doAddress.setVisibility(View.GONE);*/
            if (ride.has("dropOff")) {
                JSONObject doAddressObj = new JSONObject(ride.getString("dropOff"));
                doAddress.setText(doAddressObj.getString("address"));
                doLat = doAddressObj.get("lat").toString();
                doLng = doAddressObj.get("lng").toString();
                doAddress.setVisibility(View.VISIBLE);
                if (!doLat.equals("") && !doLat.equals("null")
                        && !doLng.equals("") && !doLng.equals("null")) {
                    double rideDist = getRideDistance(Double.parseDouble(puLat),
                            Double.parseDouble(puLng),
                            Double.parseDouble(doLat),
                            Double.parseDouble(doLng));
                    //rideDistance.setText("Ride: " + rideDist + "km");
                    String dist = new DecimalFormat("##.##").format(rideDist);
                    rideDistance.setText("Ride: " + dist + "km");
                }
            } else doAddress.setVisibility(View.GONE);
            //message.setText("New booking request");
            rideID.setText("RIDE ID: " + bookingId);
            String puDateTime = ride.getString("pickUpDate");
            //puDate.setText(puDateTime);
            if (!puDateTime.equals("null") && !puDateTime.equals("")) {
                String day = puDateTime.substring(0, puDateTime.indexOf(" "));
                String time = puDateTime.substring(puDateTime.indexOf(" ") + 1, puDateTime.length());
                puDate.setText(day);
                puTime.setText(time);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initMap();
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
        listView.addView(rideView);
        getDistance();
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
                    if (mCircleView != null && mHandler != null) {
                        mCircleView.stopSpinning();
                        mHandler.removeCallbacks(mRunnable);
                    }
                    if (bookingType.equals("1")) {
                        if (statusInfo.equalsIgnoreCase("1")) {
                            openDriverArrived(outJson);
                        } else
                            Success("Success!", outJson.getString(Key.MESSAGE), outJson);
                    } else {
                        JSONObject datObj = outJson.getJSONObject(Key.DATA);
                        startActivity(new Intent(MultipleRideRequest.this, DriverArrived.class)
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
                        MultipleRideRequest.this.finish();
                    }
                } else {
                    if (mCircleView != null && mHandler != null) {
                        mCircleView.stopSpinning();
                        mHandler.removeCallbacks(mRunnable);
                    }
                    //RideRequestList.this.finish();
                    goHome();
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

    private void Success(String title, String message, final JSONObject outerJson) {
        final AlertDialog mDialog = new AlertDialog(MultipleRideRequest.this, true);
        mDialog.setDialogTitle(title);
        if (statusInfo.equalsIgnoreCase("1"))
            mDialog.setDialogMessage("Thanks for accepting new booking request. Click on OK to get booking details.");
        else mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                /*if(statusInfo.equalsIgnoreCase("1")){
                    openDriverArrived(outerJson);
                } else {*/
                Intent intent2;
                if (session.getUserType().equals("4"))
                    intent2 = new Intent(getApplicationContext(), DriverHome.class);
                else
                    intent2 = new Intent(getApplicationContext(), PassengerHome.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                MultipleRideRequest.this.finish();
                //}
            }
        });
        mDialog.show();
    }

    private void openDriverArrived(JSONObject outerJson) {
        try {
            JSONObject datObj = outerJson.getJSONObject(Key.DATA);
            startActivity(new Intent(MultipleRideRequest.this, DriverArrived.class)
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
            MultipleRideRequest.this.finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Error(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(MultipleRideRequest.this, true);
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
        final AlertDialog mDialog = new AlertDialog(MultipleRideRequest.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                MultipleRideRequest.this.finish();
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
                mHandler.removeCallbacks(this);
                //RideRequestList.this.finish();
                //type = 2;
                //acceptRejectRide("2");
                goHome();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(NotificationFilters.REQUEST_CANCELLED));
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
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
                line = googleMap.addPolyline(lineOptions);
                LatLngBounds.Builder bc = new LatLngBounds.Builder();
                for (LatLng item : points) {
                    bc.include(item);
                }
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
            }
        }
    }

    private void getDistance() {
        if (Internet.hasInternet(this)) {
            String source = "" + currentLat + "," + currentLng;
            String destination = "&destination=" + puLat + "," + puLng;
            String endPoint = Config.DISTANCE_BY_GOOGLE + source + destination;
            APIHandlerInBack apiHandler = new APIHandlerInBack(this, HTTPMethods.GET, this, null);
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

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}

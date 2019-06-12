package com.bookmyride.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.bookmyride.map.GMapV2GetRouteDirection;
import com.bookmyride.services.TrackGPS;
import com.bookmyride.util.ImageLoader;
import com.bookmyride.views.AlertDialog;
import com.bookmyride.views.RideDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Vinod on 2017-02-01.
 */
public class GuaranteeDriverInfo extends AppCompatActivity implements View.OnClickListener,
        OnMapReadyCallback, AsyncTaskCompleteListener {
    String driverLat = "", driverLong = "",
            puLat = "", puLong = "", puLocation = "";
    private GoogleMap googleMap;
    private LatLng fromPosition;
    private LatLng toPosition;
    TrackGPS gps;
    SessionHandler session;
    String status = "";
    boolean isNext = false;
    String bookingID = "0", driverID, dist, duration;
    TextView driverName, address, distance, fare, extra, bookNow, skipDriver;
    ImageView driverImg;
    ImageLoader imgLoader;
    int guaranteedFeePerKm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guarantee_driver_info);
        init();
        getPickupInfo(getIntent().getStringExtra("pickUp"));
        getSkipDriver();
    }

    private void init() {
        gps = new TrackGPS(this);
        session = new SessionHandler(this);
        imgLoader = new ImageLoader(this);
        driverName = (TextView) findViewById(R.id.driver_name);
        address = (TextView) findViewById(R.id.address);
        distance = (TextView) findViewById(R.id.distance);
        fare = (TextView) findViewById(R.id.fare);
        extra = (TextView) findViewById(R.id.extra);
        bookNow = (TextView) findViewById(R.id.book_now);
        bookNow.setOnClickListener(this);
        skipDriver = (TextView) findViewById(R.id.skip_driver);
        skipDriver.setOnClickListener(this);
        driverImg = (ImageView) findViewById(R.id.driver_img);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.skip_driver:
                if (skipDriver.getText().toString().equalsIgnoreCase("CANCEL"))
                    showCancelRideDialog();
                else
                    getSkipDriver();
                break;
            case R.id.book_now:
                bookDriver();
                break;
        }
    }

    public void getPickupInfo(String pickup) {
        try {
            JSONObject jobj = new JSONObject(pickup);
            puLat = jobj.getString("lat");
            puLong = jobj.getString("lng");
            puLocation = jobj.getString("address");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    RideDialog mdialog;

    public void showCancelRideDialog() {
        mdialog = new RideDialog(GuaranteeDriverInfo.this, false, true);
        mdialog.setDialogTitle(getResources().getString(R.string.cancel_ride));
        mdialog.setDialogMessage(getResources().getString(R.string.cancel_ride_msg));
        mdialog.setNegativeButton(getResources().getString(R.string.timer_label_alert_yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdialog.dismiss();
                type = 3;
                if (Internet.hasInternet(GuaranteeDriverInfo.this)) {
                    APIHandler mRequest = new APIHandler(GuaranteeDriverInfo.this, HTTPMethods.PUT, GuaranteeDriverInfo.this, null);
                    mRequest.execute(Config.CANCEL_BOOKING + bookingID, session.getToken());
                } else {
                    Alert("Alert!", getResources().getString(R.string.no_internet));
                }
            }
        });
        mdialog.setPositiveButton(getResources().getString(R.string.timer_label_alert_no), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdialog.dismiss();
            }
        });
        mdialog.show();
    }

    public class GetRouteTask extends AsyncTask<String, Void, String> {

        String response = "";
        GMapV2GetRouteDirection v2GetRouteDirection = new GMapV2GetRouteDirection();
        Document document;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {
            //Get All Route values
            document = v2GetRouteDirection.getDocument(toPosition, fromPosition, GMapV2GetRouteDirection.MODE_DRIVING);
            response = "Success";
            return response;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result.equalsIgnoreCase("Success")) {
                googleMap.clear();
                try {
                    if (status.equals("4")) {
                        ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
                        PolylineOptions rectLine = new PolylineOptions().width(18).color(getResources().getColor(R.color.red_color));
                        for (int i = 0; i < directionPoint.size(); i++) {
                            rectLine.add(directionPoint.get(i));
                        }
                        // Adding route on the map
                        googleMap.addPolyline(rectLine);
                    }
                    googleMap.addMarker(new MarkerOptions()
                            .position(fromPosition)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot)));
                    googleMap.addMarker(new MarkerOptions()
                            .position(toPosition)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_dot)));
                    googleMap.addMarker(new MarkerOptions()
                            .position(fromPosition)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.carmove)));

                    //Show path in
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(fromPosition);
                    builder.include(toPosition);
                    LatLngBounds bounds = builder.build();
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 162));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap Map) {
        googleMap = Map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(false);
        double Dlatitude = gps.getLatitude();
        double Dlongitude = gps.getLongitude();
        // Move the camera to last position with a zoom level
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Dlatitude, Dlongitude)).zoom(15).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //set marker for driver location.
        if (!driverLat.equals("") && !driverLong.equals("")) {
            fromPosition = new LatLng(Double.parseDouble(puLat), Double.parseDouble(puLong));
            toPosition = new LatLng(Double.parseDouble(driverLat), Double.parseDouble(driverLong));
            if (fromPosition != null && toPosition != null) {
                googleMap.addMarker(new MarkerOptions()
                        .position(fromPosition)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot)));
                googleMap.addMarker(new MarkerOptions()
                        .position(toPosition)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_dot)));
                googleMap.addMarker(new MarkerOptions()
                        .position(fromPosition)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.carmove)));
                LatLngBounds.Builder bc = new LatLngBounds.Builder();
                bc.include(fromPosition);
                bc.include(toPosition);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
            }
        }
    }

    private void initializeMap() {
        if (googleMap == null) {
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            if (googleMap == null) {
                //Toast.makeText(TrackDetail.this, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void Alert(String title, String alert) {
        final AlertDialog mDialog = new AlertDialog(GuaranteeDriverInfo.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    double extraFare;

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.get(Key.STATUS).toString().equalsIgnoreCase("ok")) {
                JSONObject distanceObj = outJson.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
                dist = distanceObj.getJSONObject("distance").getString("value");
                duration = distanceObj.getJSONObject("duration").getString("value");
                distance.setText("Distance: " + dist);
                extraFare = Double.parseDouble(dist) * guaranteedFeePerKm;
                extra.setText("Extra Fare: " + extraFare);
                initializeMap();
            } else if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                if (type == 1) {
                    JSONArray dataArray = outJson.getJSONArray(Key.DATA);
                    //for(int i =0; i<dataArray.length(); i++) {
                    JSONObject obj = dataArray.getJSONObject(0);
                    bookingID = obj.get("booking_id").toString();
                    driverID = obj.get("id").toString();
                    isNext = obj.getBoolean("isNext");
                    if (!driverImg.equals("") && !driverImg.equals("null"))
                        imgLoader.DisplayImage(obj.getString("image"), driverImg);
                    String driverLoc = obj.get("currentLocation").toString();
                    if (!driverLoc.equals("") && !driverLoc.equals("null")) {
                        JSONObject driverGEO = new JSONObject(driverLoc);
                        driverLat = driverGEO.get("latitude").toString();
                        driverLong = driverGEO.get("longitude").toString();
                    }
                    driverName.setText("Driver Name: " + obj.getString("fullName"));
                    //address.setText(obj.get("id").toString());
                    address.setVisibility(View.GONE);
                    distance.setText("Distance: " + obj.get("distance").toString());
                    JSONObject fares = obj.getJSONObject("fares");
                    guaranteedFeePerKm = fares.getInt("guaranteedFeePerKm");
                    String aprxFare = //"Min Bill KM: " + fares.get("minBillKm").toString()+"\n"+
                            "Min Bill Fare: " + fares.get("minBillFare").toString() + "\n" +
                                    "Fare Per KM: " + fares.get("farePerKm").toString() + "\n" +
                                    //"Fare Per Min: " + fares.get("farePerMin").toString()+"\n"+
                                    "Wait Fare Per Min: " + fares.get("waitFarePerMin").toString() + "\n" +
                                    //"Wait Time Free: " + fares.get("waitTimeFree").toString()+"\n"+
                                    "Guarantee Fee Per KM: " + fares.get("guaranteedFeePerKm").toString();
                    fare.setText(aprxFare);
                    extra.setText("");
                    extra.setVisibility(View.GONE);
                    //}
                    if (!isNext) {
                        skipDriver.setText("CANCEL");
                    }
                    getDistance();
                } else if (type == 2) {
                    Intent intent = new Intent(this, BookingTimer.class);
                    intent.putExtra("pickUp", getIntent().getStringExtra("pickUp"));
                    if (getIntent().hasExtra("dropOff"))
                        intent.putExtra("dropOff", getIntent().getStringExtra("dropOff"));
                    intent.putExtra("CategoryID", getIntent().getStringExtra("CategoryID"));
                    intent.putExtra("type", getIntent().getStringExtra("type"));
                    intent.putExtra("puDate", getIntent().getStringExtra("puDate"));
                    intent.putExtra("UserID", session.getUserID());
                    intent.putExtra("RideID", bookingID);
                    intent.putExtra("Response_time", "30");
                    intent.putExtra("Next_driver_availability", "30");
                    intent.putExtra("Message", "No driver available");
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    GuaranteeDriverInfo.this.finish();
                } else if (type == 3) {
                    final RideDialog mDialog = new RideDialog(GuaranteeDriverInfo.this, false, true);
                    mDialog.setDialogTitle("");
                    mDialog.setDialogMessage(outJson.getString(Key.MESSAGE));
                    mDialog.setPositiveButton(getResources().getString(R.string.timer_label_alert_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                            GuaranteeDriverInfo.this.finish();
                        }
                    });
                    mDialog.show();
                }
            } else if (outJson.getInt(Key.STATUS) == APIStatus.CREATED) {
                if (type == 2) {
                    Intent intent = new Intent(this, BookingTimer.class);
                    intent.putExtra("pickUp", getIntent().getStringExtra("pickUp"));
                    if (getIntent().hasExtra("dropOff"))
                        intent.putExtra("dropOff", getIntent().getStringExtra("dropOff"));
                    intent.putExtra("CategoryID", getIntent().getStringExtra("CategoryID"));
                    intent.putExtra("type", getIntent().getStringExtra("type"));
                    intent.putExtra("puDate", getIntent().getStringExtra("puDate"));
                    intent.putExtra("UserID", session.getUserID());
                    intent.putExtra("RideID", bookingID);
                    intent.putExtra("Response_time", "30");
                    intent.putExtra("Next_driver_availability", "30");
                    intent.putExtra("Message", "No driver available");
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    GuaranteeDriverInfo.this.finish();
                }
            } else {
                Alert("Alert!", outJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    int type;

    private void getSkipDriver() {
        type = 1;
        HashMap<String, String> param = new HashMap<>();
        param.put("pickUpDate", getIntent().getStringExtra("puDate"));
        param.put("pickUp", getIntent().getStringExtra("pickUp"));
        if (getIntent().hasExtra("dropOff"))
            param.put("dropOff", getIntent().getStringExtra("dropOff"));
        param.put("driverCategory_id", getIntent().getStringExtra("CategoryID"));
        if (bookingID.equals("0"))
            param.put("isSkipDriver", "0");
        else
            param.put("isSkipDriver", driverID);
        param.put("type", getIntent().getStringExtra("type"));

        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.PUT, this, param);
            apiHandler.execute(Config.GUARANTEE_DRIVER + bookingID, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void bookDriver() {
        type = 2;
        HashMap<String, String> param = new HashMap<>();
        param.put("booking_id", bookingID);
        param.put("driver_id", driverID);
        param.put("distance", dist);
        param.put("extraFare", "" + extraFare);
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.PUT, this, param);
            apiHandler.execute(Config.GUARANTEE_REQUEST + bookingID, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void getDistance() {
        type = 3;
        if (Internet.hasInternet(this)) {
            String source = "" + driverLat + "," + driverLong;
            String destination = "&destination=" + puLat + "," + puLong;
            String endPoint = Config.DISTANCE_BY_GOOGLE + source + destination;
            APIHandlerInBack apiHandler = new APIHandlerInBack(this, HTTPMethods.GET, this, null);
            apiHandler.execute(Config.DISTANCE_BY_GOOGLE + source + destination, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }
}

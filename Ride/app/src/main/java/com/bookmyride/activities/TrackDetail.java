package com.bookmyride.activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import com.bookmyride.util.ImageLoader;
import com.bookmyride.util.LatLngInterpolator;

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
 * Created by vinod on 2017-01-21.
 */
public class TrackDetail extends AppCompatActivity implements OnMapReadyCallback,
        AsyncTaskCompleteListener {
    String driverLat = "", driverLong = "",
            userLat = "", userLong = "";
    private GoogleMap googleMap;
    private LatLng fromPosition;
    private LatLng toPosition;
    private MarkerOptions markerOptions;
    //TrackGPS gps;
    SessionHandler session;
    LinearLayout callDriver, cancelTrip;
    TextView done, driverName, carModel, carNo;
    public static TrackDetail trackDetail;
    String status, bookingID, driverId, driverCategory;
    ImageView panic, driverImg, driverCarImg;
    RatingBar driverRating;
    ImageLoader imgLoader;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.track_detail);
        init();
        status = getIntent().getStringExtra("status");
        bookingID = getIntent().getStringExtra("bookingID");
        driverCategory = getIntent().getStringExtra("driverCategory");
        getDriverGEO();
    }

    float zoomLevel = 15;
    String msg = "";

    private void init() {
        trackDetail = TrackDetail.this;
        //gps = new TrackGPS(this);
        session = new SessionHandler(this);
        markerOptions = new MarkerOptions();
        imgLoader = new ImageLoader(this);
        driverName = (TextView) findViewById(R.id.driver_name);
        carModel = (TextView) findViewById(R.id.driver_carmodel);
        carNo = (TextView) findViewById(R.id.driver_carNo);
        driverImg = (ImageView) findViewById(R.id.driver_img);
        driverCarImg = (ImageView) findViewById(R.id.driver_carimg);
        driverRating = (RatingBar) findViewById(R.id.rating);
        done = (TextView) findViewById(R.id.track_your_ride_done_textview);
        callDriver = (LinearLayout) findViewById(R.id.lay_contact_driver);
        cancelTrip = (LinearLayout) findViewById(R.id.lay_cancel);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrackDetail.this.finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        callDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRideDialog();
            }
        });
        cancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCancelRideDialog();
            }
        });
        panic = (ImageView) findViewById(R.id.panic_image);
        panic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emergencyPrompt();
                //Alert("", "InProgress");
            }
        });
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NotificationFilters.REQUEST_CANCELLED)) {
                    if (session.getUserType().equals("3")) {
                        try {
                            JSONObject obj = new JSONObject(intent.getStringExtra("rideData"));
                            msg = obj.getString("message");
                            final String bookingId = obj.getString("bookingId");
                            new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Utils.showPopup(TrackDetail.this, msg);
                                    recallAlert("Alert!", msg, bookingId);
                                }
                            }, 500);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (intent.getAction().equals(NotificationFilters.UPDATE_STATUS)) {
                    if (session.getUserType().equals("3")) {
                        try {
                            JSONObject obj = new JSONObject(intent.getStringExtra("rideData"));
                            status = obj.getString("status");
                            bookingID = obj.getString("bookingId");
                            driverCategory = obj.getString("driverCategory_id");
                            String msg = obj.getString("message");
                            getDriverGEO();
                            if (status.equals("5") && obj.getString("paymentStatus").equals("0")) {
                                paymentFailureAlert("Alert!", obj.getString("payment_message"));
                            }
                            final View layout = getLayoutInflater().inflate(R.layout.customtoast, null);
                            TextView message = (TextView) layout.findViewById(R.id.message);
                            message.setText(msg);
                            TextView title = (TextView) layout.findViewById(R.id.title);
                            title.setText(getResources().getString(R.string.app_name));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    final Toast toast = new Toast(getApplicationContext());
                                    toast.setDuration(Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
                                    toast.setView(layout);
                                    toast.show();
                                }
                            }, 300);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (intent.getAction().equals(NotificationFilters.PAYMENT_REQEUST)) {
                    if (session.getUserType().equals("3")) {
                        String status = "";
                        String msg = "";
                        try {
                            JSONObject obj = new JSONObject(intent.getStringExtra("rideData"));
                            if (obj.has("status"))
                                status = obj.getString("status");
                            if (obj.has("message"))
                                msg = obj.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (status.equals("202")) {
                            final View layout = getLayoutInflater().inflate(R.layout.customtoast, null);
                            TextView message = (TextView) layout.findViewById(R.id.message);
                            if (msg.equals(""))
                                message.setText("Payment Successfully done.");
                            else
                                message.setText(msg);
                            TextView title = (TextView) layout.findViewById(R.id.title);
                            title.setText(getResources().getString(R.string.app_name));

                            final Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
                            toast.setView(layout);
                            toast.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(getApplicationContext(), FareBreakdown.class);
                                    intent.putExtra("bookingID", bookingID);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    session.saveWaitingInfo(0L, 0L, 0L, 0L, 0, 0);
                                    TrackDetail.this.finish();
                                }
                            }, 300);
                        }
                    }
                } else if (intent.getAction().equals(NotificationFilters.LOCATION_CHANGED)) {
                    double lat = intent.getDoubleExtra("lat", 0.0);
                    double lng = intent.getDoubleExtra("lng", 0.0);
                    //latitude = lat;
                    //longitude = lng;
                    // Move the camera to last position with a zoom level
                    if (googleMap != null) {
                        zoomLevel = googleMap.getCameraPosition().zoom;
                        if (zoomLevel < 15)
                            zoomLevel = 15;
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(zoomLevel).build();
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }
            }
        };
    }

    private void paymentFailureAlert(String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(TrackDetail.this, false);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setCancelOnTouchOutside(false);
        mDialog.setPositiveButton("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent3 = new Intent(TrackDetail.this, FareBreakup.class);
                intent3.putExtra("status", status);
                intent3.putExtra("bookingID", bookingID);
                intent3.putExtra("driverCategory", driverCategory);
                intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent3);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                TrackDetail.this.finish();
            }
        });
        /*mDialog.setPositiveButton("", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });*/
        mDialog.show();
    }

    private void cancelFailureAlert(String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(TrackDetail.this, false);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setCancelOnTouchOutside(false);
        mDialog.setNegativeButton("Use Another Card", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                startActivityForResult(new Intent(TrackDetail.this, MyCard.class)
                        .putExtra("isBack", ""), 0);
            }
        });
        mDialog.setPositiveButton("Ignore", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    Marker carMarker;

    @Override
    public void onBackPressed() {
    }

    public final boolean containsDigit(String s) {
        boolean containsDigit = false;
        if (s != null && !s.isEmpty()) {
            for (char c : s.toCharArray()) {
                if (containsDigit = Character.isDigit(c)) {
                    break;
                }
            }
        }
        return containsDigit;
    }

    private int resource() {
        int rideStatus = 0;
        if (containsDigit(status))
            rideStatus = Integer.parseInt(status);

        int resource;
        switch (driverCategory) {
            case "1":
                if (rideStatus > 3)
                    resource = R.drawable.taxi_map_booked;
                else
                    resource = R.drawable.taxi_nav;
                break;
            case "2":
                if (rideStatus > 3)
                    resource = R.drawable.economy_map_booked;
                else
                    resource = R.drawable.economy_nav;
                break;
            case "3":
                if (rideStatus > 3)
                    resource = R.drawable.premium_map_booked;
                else
                    resource = R.drawable.premium_nav;
                break;
            case "4":
                if (rideStatus > 3)
                    resource = R.drawable.motor_bike_map_booked;
                else
                    resource = R.drawable.motor_bike_nav;
                break;
            default:
                resource = R.drawable.taxi_nav;
                break;
        }
        return resource;
    }

    @Override
    public void onMapReady(GoogleMap Map) {
        googleMap = Map;
        // Changing map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Enable / Disable zooming controls
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        // Enable / Disable my location button
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Enable / Disable Compass icon
        googleMap.getUiSettings().setCompassEnabled(false);
        // Enable / Disable Rotate gesture
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        // Enable / Disable zooming functionality
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        // Showing / hiding your current location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(false);
        //set marker for driver location.

        if (!driverLat.equals("") && !driverLong.equals("")) {

            double dist = distance(Double.parseDouble(userLat),
                    Double.parseDouble(userLong),
                    Double.parseDouble(driverLat),
                    Double.parseDouble(driverLong), "K");
            //Log.d("dist", "" + dist);
            //if(dist > 2){
            fromPosition = new LatLng(Double.parseDouble(userLat), Double.parseDouble(userLong));
            toPosition = new LatLng(Double.parseDouble(driverLat), Double.parseDouble(driverLong));

            if (fromPosition != null && toPosition != null) {
                if (status.equals("4") || status.equals("3")) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(fromPosition)
                            .anchor(0.5f, 0.5f).flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot)));
                    googleMap.addMarker(new MarkerOptions()
                            .position(toPosition)
                            .anchor(0.5f, 0.5f).flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_dot)));
                    carMarker = googleMap.addMarker(new MarkerOptions()
                            .position(fromPosition)
                            .anchor(0.5f, 0.5f).flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(resource())));
                } else {
                    googleMap.addMarker(new MarkerOptions()
                            .position(fromPosition)
                            .anchor(0.5f, 0.5f).flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_dot)));
                    googleMap.addMarker(new MarkerOptions()
                            .position(toPosition)
                            .anchor(0.5f, 0.5f).flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot)));
                    carMarker = googleMap.addMarker(new MarkerOptions()
                            .position(toPosition)
                            .anchor(0.5f, 0.5f).flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(resource())));

                }
                if (!status.equals("3")) {
                    String url = getDirectionsUrl(fromPosition, null, toPosition);
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);
                }
            }
        }
        //}
    }

    public double distance(double lat1, double lon1, double lat2, double lon2, String sr) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (sr.equals("K")) {
            dist = dist * 1.609344;
        } else if (sr.equals("N")) {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    public double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private void initializeMap() {
        if (googleMap == null) {
            //googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.track_your_ride_mapview)).getMap();
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.track_your_ride_mapview);
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
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(TrackDetail.this, true);
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

    private void recallAlert(String title, String alert, final String bookingID) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(TrackDetail.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent = new Intent(TrackDetail.this, RideDetail.class);
                intent.putExtra("booking_id", bookingID);
                startActivity(intent);
                TrackDetail.this.finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        mDialog.show();
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                if (type == 0) {
                    JSONObject dataObj = outJson.getJSONObject(Key.DATA);
                    JSONObject pickupGeo = dataObj.getJSONObject("pickUp");
                    userLat = pickupGeo.getString("lat");
                    userLong = pickupGeo.getString("lng");
                    if (status.equals("3") || status.equals("4")) {
                        JSONObject dropOffGeo = dataObj.getJSONObject("dropOff");
                        driverLat = dropOffGeo.getString("lat");
                        driverLong = dropOffGeo.getString("lng");
                    } else {
                        String driverLatLng = dataObj.get("drivergeo").toString();
                        if (driverLatLng != null && !driverLatLng.equals("null")) {
                            JSONObject driverGeo = new JSONObject(driverLatLng);
                            JSONObject currentLatLnt = new JSONObject(driverGeo.getString("currentLocation"));
                            driverLat = currentLatLnt.getString("latitude");
                            driverLong = currentLatLnt.getString("longitude");
                        }
                    }
                    if (dataObj.has("drivergeo")) {
                        String driverLatLng = dataObj.get("drivergeo").toString();
                        if (driverLatLng != null && !driverLatLng.equals("null")) {
                            JSONObject driverGeo = new JSONObject(driverLatLng);
                            if (!driverImg.equals("") && !driverImg.equals("null"))
                                imgLoader.DisplayImage(driverGeo.getString("image"), driverImg);
                            driverName.setText(driverGeo.getString("fullName"));
                            driverNumber = driverGeo.getString("dial_code") + driverGeo.getString("phone");
                            JSONObject vehicleInfo = new JSONObject(driverGeo.getString("vehicleDetail"));
                            if (!driverCarImg.equals("") && !driverCarImg.equals("null"))
                                imgLoader.DisplayImage(vehicleInfo.getString("icon"), driverCarImg);
                            carModel.setText(vehicleInfo.getString("vehicle_type"));
                            carNo.setText(vehicleInfo.getString("registerationNumber"));
                            driverRating.setRating(Float.parseFloat(vehicleInfo.get("rating").toString()));
                            if (status.equals("3") && driverLat.equals("") && driverLong.equals("")) {
                                JSONObject currentLatLnt = new JSONObject(driverGeo.getString("currentLocation"));
                                driverLat = currentLatLnt.getString("latitude");
                                driverLong = currentLatLnt.getString("longitude");
                            }
                            if (dataObj.has("vehicle_details")) {
                                String vehicleDetail = dataObj.get("vehicle_details").toString();
                                if (vehicleDetail != null && !vehicleDetail.equals("null")) {
                                    JSONObject vehicleInfo2 = new JSONObject(vehicleDetail);
                                    String isFleet2 = vehicleInfo2.getString("isFleetSelected");
                                    if (isFleet2.equals("1"))
                                        carNo.setText(vehicleInfo2.getString("vehicle_num"));
                                    else
                                        carNo.setText(vehicleInfo2.getString("registerationNumber"));
                                }
                            }
                        }
                    }
                    if (googleMap != null) {
                        googleMap.clear();
                        googleMap = null;
                    }
                    initializeMap();
                } else if (type == 1) {
                    Alert("Success!", "Message has sent to driver.");
                }
            } else {
                Alert("Alert!", outJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    int type = 0;

    private void getDriverGEO() {
        type = 0;
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this, null);
            apiHandler.execute(Config.DRIVER_GEO + bookingID + "?expand=drivergeo,passenger", session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    com.bookmyride.views.AlertDialog mDialog;

    private void showCancelRideDialog() {
        mDialog = new com.bookmyride.views.AlertDialog(TrackDetail.this, false);
        mDialog.setDialogTitle("Alert!");
        mDialog.setDialogMessage(getResources().getString(R.string.cancel_ride_alert));
        mDialog.setNegativeButton("CHECK FEES", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (Internet.hasInternet(TrackDetail.this)) {
                    //cancelRide();
                    getCancellationCharges();
                } else {
                    Alert("Alert!", getResources().getString(R.string.no_internet));
                }
            }
        });
        mDialog.setPositiveButton("BACK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void getCancellationCharges() {
        APIHandler mRequest = new APIHandler(TrackDetail.this, HTTPMethods.PUT, new AsyncTaskCompleteListener() {
            @Override
            public void onTaskComplete(String result) {
                try {
                    JSONObject outJson = new JSONObject(result);
                    if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                        //String cancellationCharges = outJson.getJSONObject(Key.DATA).getString("cancellationCharges");
                        //String msg = "You might be charged $" + cancellationCharges + " as ride cancellation fee.";
                        cancellationPrompt(outJson.getString(Key.MESSAGE), "CANCEL & PAY");
                    } else if (outJson.getInt(Key.STATUS) == APIStatus.GUARANTEE_AVAILABLE) {
                        cancellationPrompt(outJson.getString(Key.MESSAGE), "CANCEL RIDE");
                    } else
                        Alert("Alert!", outJson.getString(Key.MESSAGE));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        mRequest.execute(Config.BOOKING_CHARGES + bookingID, session.getToken());
    }

    private void cancellationPrompt(String msg, String action) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(TrackDetail.this, true);
        mDialog.setDialogTitle("Alert!");
        mDialog.setDialogMessage(msg);
        mDialog.setNegativeButton(action, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                cancelRide();
            }
        });
        mDialog.setPositiveButton("BACK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void emergencyPrompt() {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(TrackDetail.this, true);
        mDialog.setDialogTitle("Alert!");
        mDialog.setDialogMessage("Do you want to call emergency services?");
        mDialog.setNegativeButton("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                doMakeCall();
            }
        });
        mDialog.setPositiveButton("No", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void cancelRide() {
        APIHandler mRequest = new APIHandler(TrackDetail.this, HTTPMethods.PUT, new AsyncTaskCompleteListener() {
            @Override
            public void onTaskComplete(String result) {
                try {
                    JSONObject outJson = new JSONObject(result);
                    if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS ||
                            outJson.getInt(Key.STATUS) == APIStatus.GUARANTEE_AVAILABLE) {
                        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(TrackDetail.this, true);
                        mDialog.setDialogTitle("Success!");
                        mDialog.setDialogMessage(outJson.getString(Key.MESSAGE));
                        mDialog.setPositiveButton(getResources().getString(R.string.timer_label_alert_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                TrackDetail.this.finish();
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        });
                        mDialog.show();
                    } else if (outJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                        cancelFailureAlert("Alert!", outJson.getString(Key.MESSAGE));
                    } else
                        Alert("Alert!", outJson.getString(Key.MESSAGE));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        mRequest.execute(Config.CANCEL_BOOKING + bookingID, session.getToken());
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    if (callType == 0)
                        callIntent.setData(Uri.parse("tel:" + driverNumber));
                    else
                        callIntent.setData(Uri.parse("tel:" + "000"));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);
                }
                break;
        }
    }

    final int PERMISSION_REQUEST_CODE = 111;

    private void doMakeCall() {
        callType = 1;
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            if (!checkCallPhonePermission() || !checkReadStatePermission()) {
                requestPermission();
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + "000"));
                startActivity(callIntent);
            }
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + "000"));
            startActivity(callIntent);
        }
    }

    String driverNumber = "";
    int callType = 0;

    private void callDriver() {
        callType = 0;
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            if (!checkCallPhonePermission() || !checkReadStatePermission()) {
                requestPermission();
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + driverNumber));
                startActivity(callIntent);
            }
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + driverNumber));
            startActivity(callIntent);
        }
    }

    private void getTwillioNumber() {
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                            JSONObject dataObj = object.getJSONObject(Key.DATA);
                            driverNumber = dataObj.getString("twilio_number");
                            callDriver();
                        } else if (object.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                            Alert("Alert!", object.getString(Key.MESSAGE));
                        } else {
                            Alert("Alert!", object.getString(Key.MESSAGE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
            apiHandler.execute(Config.GET_TWILLIO_NUMBER + bookingID, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void showMessageDialog() {
        LayoutInflater inflater = LayoutInflater.from(TrackDetail.this);
        View subView = inflater.inflate(R.layout.message_dialog, null);
        final EditText subEditText = (EditText) subView.findViewById(R.id.bid_price);
        TextView proceed = (TextView) subView.findViewById(R.id.send);
        TextView cancel = (TextView) subView.findViewById(R.id.cancel);
        AlertDialog.Builder builder = new AlertDialog.Builder(TrackDetail.this);
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
        type = 1;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("msgFrom", session.getUserID());
        params.put("msgTo", driverNumber);
        params.put("booking_id", bookingID);
        params.put("msg", message);
        //Log.e("param", params.toString());
        APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, params);
        apiHandler.execute(Config.SEND_MESSAGE_DRIVER, session.getToken());
    }

    public String[] getContactType() {
        String[] gateway = getResources().getStringArray(R.array.contact_via);
        return gateway;
    }

    public void showRideDialog() {
        final Dialog mDialog = new Dialog(TrackDetail.this, R.style.rideDialog);
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
                    //callDriver();
                    getTwillioNumber();
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    double prevLat = 0.0, prevLng = 0.0;
    final Handler h = new Handler();
    double dLat = 0.0;
    double dLng = 0.0;

    private void updateLocation() {
        h.postDelayed(locationRunnable, 5000);
    }

    Runnable locationRunnable = new Runnable() {
        @Override
        public void run() {
            ServiceHandlerInBack handler = new ServiceHandlerInBack(TrackDetail.this, HTTPMethods.GET, new AsyncTaskCompleteListener() {
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
                                if (carMarker == null) {
                                    carMarker = googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(dLat, dLng))
                                            .anchor(0.5f, 0.5f).flat(true)
                                            .icon(BitmapDescriptorFactory.fromResource(resource())));
                                }

                                if (prevLat == 0.0 && prevLng == 0.0) {
                                    prevLat = dLat;
                                    prevLng = dLat;
                                }
                                Location prevLoc = new Location("prev");
                                prevLoc.setLatitude(prevLat);
                                prevLoc.setLongitude(prevLng);

                                LatLng updatedLatLng = new LatLng(dLat, dLng);
                                zoomLevel = googleMap.getCameraPosition().zoom;
                                if (zoomLevel < 15)
                                    zoomLevel = 15;
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(updatedLatLng)
                                        .zoom(zoomLevel)
                                        .bearing(15)
                                        .tilt(10)
                                        .build();
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                                googleMap.animateCamera(cameraUpdate);

                                animateMarker(prevLoc, currLoc, carMarker);
                                //animateMarker(currLoc, carMarker);
                                prevLat = dLat;
                                prevLng = dLng;

                                if (line != null) {
                                    LatLng latLng = new LatLng(dLat, dLng);
                                    double tolerance = 50;
                                    boolean isLocationOnPath = PolyUtil.isLocationOnEdge(latLng, line.getPoints(), true, tolerance);
                                    //Toast.makeText(getApplicationContext(), "OnRoute: "+isLocationOnPath, Toast.LENGTH_SHORT).show();
                                    if (!isLocationOnPath) {
                                        //Toast.makeText(getApplicationContext(), "updating Route", Toast.LENGTH_SHORT).show();
                                        String url = getDirectionsUrl(fromPosition, latLng, toPosition);
                                        DownloadTask downloadTask = new DownloadTask();
                                        downloadTask.execute(url);
                                    }
                                    //updateRoute(line.getPoints());
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
            handler.execute(Config.DRIVER_GEO + bookingID + "?expand=drivergeo", session.getToken());
            h.postDelayed(this, 5000);
        }
    };

    @Override
    protected void onDestroy() {
        h.removeCallbacks(locationRunnable);
        if (mReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
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

    public void animateMarker(final Location prevLoc, final Location currLoc, final Marker marker) {
        if (marker != null && currLoc != null) {
            final LatLng endPosition = new LatLng(currLoc.getLatitude(), currLoc.getLongitude());

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
            valueAnimator.setDuration(300);
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
                if (!status.equals("3")) {
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
                        lineOptions.geodesic(true);
                        lineOptions.addAll(points);
                        lineOptions.width(10);
                        lineOptions.color(Color.RED);
                    }
                    // Drawing polyline in the Google Map for the i-th route
                    //line = googleMap.addPolyline(lineOptions);
                    if (points != null) {
                        if (points.size() != 0)
                            line = googleMap.addPolyline(lineOptions);
                        LatLngBounds.Builder bc = new LatLngBounds.Builder();
                        for (LatLng item : points) {
                            bc.include(item);
                        }
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
                    }
                }
                if (status.equals("4")) {
                    cancelTrip.setVisibility(View.GONE);
                }
                updateLocation();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    Polyline line;

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(NotificationFilters.REQUEST_CANCELLED));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(NotificationFilters.UPDATE_STATUS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(NotificationFilters.PAYMENT_REQEUST));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(NotificationFilters.LOCATION_CHANGED));
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        h.removeCallbacks(locationRunnable);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    protected void onStart() {
        //startService(new Intent(this, RouteService.class));
        super.onStart();
    }
}
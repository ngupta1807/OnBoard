package com.grabid.fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.LatLngInterpolator;
import com.grabid.common.SessionManager;
import com.grabid.models.UserInfo;
import com.grabid.util.DirectionsJSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.R.attr.label;
import static com.grabid.R.id.mapView;


/**
 * Created by vinod on 10/14/2016.
 */
public class TrackDelivery extends Fragment implements AsyncTaskCompleteListener, View.OnClickListener {
    SessionManager session;
    String type, deliveryID;
    String incomingType = "";
    UserInfo userInfo;
    MapView mMapView;
    private GoogleMap googleMap;
    String incoming_delivery_type = "";
    String pick_up_date, dropp_off_date;
    String TITLE = "Alert!";
    public boolean isMarkerRotating;
    JSONArray route;
    ImageView mWazeIcon;
    Marker transietmarker;
    Handler h = new Handler();
    int delay = 10000; //10 seconds
    Runnable runnable;
    boolean IsFirst = true;
    int typee = 0;
    public static boolean isCancelled = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.edit.setBackgroundResource(R.drawable.edit_top);
        View view = inflater.inflate(R.layout.fragment_map_track_delivery, null);
        initMap(view, savedInstanceState);
        init(view);
        isCancelled = false;
        return view;
    }

    private void init(View view) {
        session = new SessionManager(getActivity());
        userInfo = session.getUserDetails();
        mWazeIcon = (ImageView) view.findViewById(R.id.wazeicon);
        mWazeIcon.setOnClickListener(this);

    }

    private void appendData() {
        typee = 0;
        String url = Config.SERVER_URL + Config.DELIVREY_ROUTE + "?id=" + deliveryID;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage(TITLE, getResources().getString(R.string.no_internet));

    }

    private void appendupdateData() {
        if (!isCancelled) {
            typee = 0;
            String url = Config.SERVER_URL + Config.DELIVREY_ROUTE + "?id=" + deliveryID;
            try {
                if (Internet.hasInternet(getActivity())) {
                    RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null, false);
                    mobileAPI.execute(url, session.getToken());
                } else
                    showMessage(TITLE, getResources().getString(R.string.no_internet));
            } catch (Exception e) {
                e.toString();
            }
        }
    }

    private void showMessage(String title, String message) {
        AlertManager.messageDialog(getActivity(), title, message);
    }

    private void initMap(View rootView, Bundle savedInstanceState) {
        mMapView = (MapView) rootView.findViewById(mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        // needed to get the map to display immediately
        isMarkerRotating = false;
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

            }
        });
    }

    @Override
    public void onTaskComplete(String result) {
        Log.d(Config.TAG, result);
        handleResponse(result);
    }

    String pick_latitude, pick_longitude, drop_latitude, drop_longitude, delivery_name, current_lat, current_lon, previous_latitude, previus_longitude;
    JSONArray path;

    private void handleResponse(String result) {
        if (!isCancelled) {
            if (typee == 0) {
                try {
                    JSONObject outJson = new JSONObject(result);
                    JSONObject dataJson = outJson.getJSONObject(Config.DATA);
                    Log.v("dataJson", "dataJson::" + dataJson);
                    delivery_name = dataJson.getJSONObject(Config.DELIVERY).getString(Config.ITEM_TITLE);
                    pick_latitude = dataJson.getJSONObject(Config.PICK_UP_DETAIL).getString(Config.LATITUDE);
                    pick_longitude = dataJson.getJSONObject(Config.PICK_UP_DETAIL).getString(Config.LONGITUDE);
                    drop_latitude = dataJson.getJSONObject(Config.DROP_OFF_DETAIL).getString(Config.LATITUDE);
                    drop_longitude = dataJson.getJSONObject(Config.DROP_OFF_DETAIL).getString(Config.LONGITUDE);

                    pick_up_date = dataJson.getJSONObject(Config.DELIVERY).getString(Config.ITEM_PICK_UP);
                    dropp_off_date = dataJson.getJSONObject(Config.DELIVERY).getString(Config.ITEM_DROP_OFF);

                    try {
                        current_lat = dataJson.getJSONObject(Config.CURRENT_DETAIL).getString(Config.LATITUDE);
                        current_lon = dataJson.getJSONObject(Config.CURRENT_DETAIL).getString(Config.LONGITUDE);
                        previous_latitude = dataJson.getJSONObject(Config.PREVIOUSDETAIL).getString(Config.LATITUDE);
                        previus_longitude = dataJson.getJSONObject(Config.PREVIOUSDETAIL).getString(Config.LONGITUDE);
                    } catch (Exception e) {
                        e.toString();
                    }
                    route = dataJson.getJSONArray(Config.ROUTE_LOCATION);
                    Log.v("route", "route" + route);
                    path = new JSONArray();
                    path.put(dataJson.getJSONObject(Config.PICK_UP_DETAIL));

                    for (int i = 0; i < route.length(); i++) {
                        JSONObject point = (JSONObject) route.get(i);
                        path.put(point);
                    }
                    path.put(dataJson.getJSONObject(Config.DROP_OFF_DETAIL));
                    Log.v("path", "path" + path);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                googleMap.setMyLocationEnabled(true);
                Custominfowindowadpater infoadapter = new Custominfowindowadpater();
                googleMap.setInfoWindowAdapter(infoadapter);
                Log.v("incoming_delivery_type", "incoming_delivery_type" + incoming_delivery_type);
                Log.v("incomingType", "incomingType" + incomingType);
                if (incomingType.equals("shipper") || incomingType.equals("driver")) {
//            if (incoming_delivery_type.equals(getString(R.string.completed_del))) { //track route
                    if (incoming_delivery_type.equals("3")) {
                        try {
                            if (pick_latitude.equals("0.0") && pick_longitude.equals("0.0") && drop_latitude.equals("0.0") && drop_longitude.equals("0.0")) {
                                Toast.makeText(getActivity(), "No Route Found", Toast.LENGTH_SHORT).show();
                            } else if (route != null && route.length() > 1) {
                       /* LatLng origin = new LatLng(Double.parseDouble(pick_latitude), Double.parseDouble(pick_longitude));
                        googleMap.addMarker(new MarkerOptions().position(origin).title("pickupp1-" + delivery_name).snippet(pick_up_date).icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_pointer)));
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(origin).zoom(18).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        LatLng dest = new LatLng(Double.parseDouble(drop_latitude), Double.parseDouble(drop_longitude));
                        googleMap.addMarker(new MarkerOptions().position(dest).title(delivery_name).snippet(dropp_off_date).icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_off_pointer)));
                        ArrayList<LatLng> selectedLine = new ArrayList<LatLng>();
                        selectedLine.clear();
                        points = new ArrayList();
                        lineOptions = new PolylineOptions();
                        for (int j = 0; j < path.length(); j++) {
                            try {
                                JSONObject point = (JSONObject) path.get(j);
                                double lat = Double.parseDouble(point.getString("latitude"));
                                double lng = Double.parseDouble(point.getString("longitude"));
                                LatLng position = new LatLng(lat, lng);
                                points.add(position);
                            } catch (Exception ex) {
                                Log.v("error:", "error:" + ex.getMessage());
                            }
                        }
                        lineOptions.addAll(points);
                        lineOptions.width(6);
                        selectedLine.addAll(points);
                        lineOptions.color(Color.RED);
                        googleMap.addPolyline(lineOptions);*/
                                // drawPolyLineOnMap(points);
                                DrawPath();
                            } else {
                                DrawPath();
                            }
                        } catch (Exception e) {
                            e.toString();
                        }
                    } else if (incoming_delivery_type.equals("2")) {
                        try {
                            if (pick_latitude.equals("0.0") && pick_longitude.equals("0.0") && drop_latitude.equals("0.0") && drop_longitude.equals("0.0")) {
                                Toast.makeText(getActivity(), "No Route Found", Toast.LENGTH_SHORT).show();
                            } else if (route != null && route.length() > 1) {
                        /*LatLng origin = new LatLng(Double.parseDouble(pick_latitude), Double.parseDouble(pick_longitude));
                        googleMap.addMarker(new MarkerOptions().position(origin).title("pickupp1-" + delivery_name).snippet(pick_up_date).icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_pointer)));
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(origin).zoom(18).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        LatLng dest = new LatLng(Double.parseDouble(drop_latitude), Double.parseDouble(drop_longitude));
                        googleMap.addMarker(new MarkerOptions().position(dest).title(delivery_name).icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_off_pointer)));
                        LatLng current = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lon));
                        Marker marker = googleMap.addMarker(new MarkerOptions().position(current).title("nosnippet" + delivery_name).icon(BitmapDescriptorFactory.fromResource(R.drawable.truckn)).flat(true));
                        float bearing = (float) bearingBetweenLocations(current, dest);
                        rotateMarker(marker, bearing);
                        ArrayList<LatLng> selectedLine = new ArrayList<LatLng>();
                        selectedLine.clear();
                        points = new ArrayList();
                        lineOptions = new PolylineOptions();
                        for (int j = 0; j < path.length(); j++) {
                            try {
                                JSONObject point = (JSONObject) path.get(j);
                                double lat = Double.parseDouble(point.getString("latitude"));
                                double lng = Double.parseDouble(point.getString("longitude"));
                                LatLng position = new LatLng(lat, lng);
                                points.add(position);
                            } catch (Exception ex) {
                                Log.v("error:", "error:" + ex.getMessage());
                            }
                        }
                        lineOptions.addAll(points);
                        lineOptions.width(6);
                        selectedLine.addAll(points);
                        lineOptions.color(Color.RED);
                        googleMap.addPolyline(lineOptions);*/
                                // drawPolyLineOnMap(points);
                                DrawPath();
                            } else {
                                DrawPath();
                            }
                        } catch (Exception e) {
                            e.toString();
                        }
                    }
            /*else if (incoming_delivery_type.equals("2")) {
                LatLng currentPos = new LatLng(Double.parseDouble(pick_latitude), Double.parseDouble(pick_longitude));
                googleMap.addMarker(new MarkerOptions().position(currentPos).title("pickupp1-" + delivery_name).snippet(pick_up_date).icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_pointer)));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(currentPos).zoom(6).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } */
                    else if (incoming_delivery_type.equals("1")) {
                        try {
                            if (incomingType.equals("driver")) {
                                if (pick_latitude.equals("0.0") && pick_longitude.equals("0.0") && drop_latitude.equals("0.0") && drop_longitude.equals("0.0")) {
                                    Toast.makeText(getActivity(), "No Route Found", Toast.LENGTH_SHORT).show();
                                } else {
                                    DrawPath();
                                }
                            } else {
                                LatLng currentPos = new LatLng(Double.parseDouble(pick_latitude), Double.parseDouble(pick_longitude));
                                googleMap.addMarker(new MarkerOptions().position(currentPos).title("pickupp1-" + delivery_name).snippet("Not Picked up Yet").icon(BitmapDescriptorFactory.fromResource(R.drawable.green_pointer)));
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(currentPos).zoom(15).build();
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }
                        } catch (Exception e) {
                            e.toString();
                        }
                    }
                } else {
                    if (pick_latitude.equals("0.0") && pick_longitude.equals("0.0") && drop_latitude.equals("0.0") && drop_longitude.equals("0.0")) {
                        Toast.makeText(getActivity(), "No Route Found", Toast.LENGTH_SHORT).show();
                    } else {
                        DrawPath();
                    }
                }
                if (mMapView != null) {
                    try {
                        View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                        ImageView img = (ImageView) locationButton;
                        img.setImageResource(R.drawable.compass);
                    } catch (Exception e) {
                        e.toString();
                    }
                }
            }
            if (typee == 1) {
                JSONObject outJson = null;
                try {
                    outJson = new JSONObject(result);
                    JSONObject dataJson = outJson.getJSONObject(Config.DATA);
                    try {
                        current_lat = dataJson.getJSONObject(Config.CURRENT_DETAIL).getString(Config.LATITUDE);
                        current_lon = dataJson.getJSONObject(Config.CURRENT_DETAIL).getString(Config.LONGITUDE);
                        final Location location = new Location("GPS");
                        location.setLatitude(Double.parseDouble(current_lat));
                        location.setLongitude(Double.parseDouble(current_lon));
                        if (transietmarker != null)
                            animateMarker(location, transietmarker);
                    } catch (Exception e) {
                        e.toString();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    public String getaddress(LatLng latlongi) {
        Geocoder geocoder;
        String address;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latlongi.latitude, latlongi.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0);
        } catch (Exception ex) {
            address = "";
        }

        return address;
    }

    public void drawPolyLineOnMap(List<LatLng> list) {
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.RED);
        polyOptions.width(6);
        polyOptions.addAll(list);

        googleMap.clear();
        googleMap.addPolyline(polyOptions);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : list) {
            builder.include(latLng);
        }

        final LatLngBounds bounds = builder.build();

        //BOUND_PADDING is an int to specify padding of bound.. try 100.
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 10);
        googleMap.animateCamera(cu);
    }

    public void DrawPath() {
        if (incoming_delivery_type.equals("2")) {
            if (googleMap != null) {
                googleMap.clear();
            }

            LatLng current = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lon));
            transietmarker = googleMap.addMarker(new MarkerOptions().position(current).title("nosnippet" + getaddress(current)).icon(BitmapDescriptorFactory.fromResource(R.drawable.car_green)).flat(true));
            LatLng dest = new LatLng(Double.parseDouble(drop_latitude), Double.parseDouble(drop_longitude));
            googleMap.addMarker(new MarkerOptions().position(dest).title(getaddress(dest)).icon(BitmapDescriptorFactory.fromResource(R.drawable.red_pointer)));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(current).zoom(15).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            if (previus_longitude != null && !previus_longitude.contentEquals("null")) {
                try {
                    LatLng previous = new LatLng(Double.parseDouble(previous_latitude), Double.parseDouble(previus_longitude));
                    float bearing = (float) bearingBetweenLocations(previous, dest);
                    // float previousbearing = Bearing.getBearing();
                    // if (previousbearing != bearing)
                    rotateMarker(transietmarker, bearing);
                    //Bearing.setBearing(bearing);
                } catch (Exception e) {
                    e.toString();
                }
            } else {
                try {
                    float bearing = (float) bearingBetweenLocations(current, dest);
                    //  float previousbearing = Bearing.getBearing();
                    // if (previousbearing != bearing)
                    rotateMarker(transietmarker, bearing);
                    //  rotateMarker(transietmarker, bearing);
                    //  Bearing.setBearing(bearing);
                } catch (Exception e) {
                    e.toString();
                }
            }


// Getting URL to the Google Directions API
            String url = getDirectionsUrl(current, dest);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
            if (incomingType.equals("shipper")) {

            }


        } else {
            LatLng origin = new LatLng(Double.parseDouble(pick_latitude), Double.parseDouble(pick_longitude));
            googleMap.addMarker(new MarkerOptions().position(origin).title("pickupp1-" + getaddress(origin)).icon(BitmapDescriptorFactory.fromResource(R.drawable.green_pointer)));
            LatLng dest = new LatLng(Double.parseDouble(drop_latitude), Double.parseDouble(drop_longitude));
            googleMap.addMarker(new MarkerOptions().position(dest).title(getaddress(dest)).icon(BitmapDescriptorFactory.fromResource(R.drawable.red_pointer)));

            CameraPosition cameraPosition = new CameraPosition.Builder().target(origin).zoom(15).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            downloadTask.execute(url);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wazeicon: {
                showGrabidDialog();

            }
        }
    }

    public void showGrabidDialog() {
        final Dialog mDialog = new Dialog(getActivity(), R.style.GrabidDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_navigation);
        mDialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);

        title.setText("Please choose preferred navigation app:");

        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                try {
                    dialog.dismiss();
                } catch (Exception e) {

                }
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                dialog.dismiss();
            }
        });

        final ListView dialog_ListView = (ListView) mDialog.findViewById(R.id.list);
        ArrayAdapter<String> adapter = null;
        adapter = new ArrayAdapter<>(getActivity(),
                R.layout.dialog_textview, R.id.textItem, getBuildList());

        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.v("", String.valueOf(position));
                if (position == 2)
                    mDialog.dismiss();
                else if (!drop_latitude.equals("0.0") && !drop_longitude.equals("0.0"))
                    openApp(position, drop_latitude, drop_longitude);

                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public String[] getBuildList() {
        return getActivity().getResources().getStringArray(R.array.nav_type);
    }

    private void openApp(int pos, String doLat, String doLng) {
        if (pos == 0) {
           /* Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=loc:" + doLat + "," + doLng));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Only if initiating from a Broadcast Receiver
            String mapsPackageName = "com.google.android.apps.maps";
            // if (Utility.isPackageExisted(context, mapsPackageName)) {
            i.setClassName(mapsPackageName, "com.google.android.maps.MapsActivity");
            i.setPackage(mapsPackageName);
            startActivity(i);*/
            //  }
            //   context.startActivity(i);
            boolean isAppInstalled = checkAppInstall("com.google.android.apps.maps");
            if (isAppInstalled) {
                String uri = "http://maps.google.com/maps?q=loc:" + doLat + "," + doLng + " (" + label + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                String mapsPackageName = "com.google.android.apps.maps";
                intent.setClassName(mapsPackageName, "com.google.android.maps.MapsActivity");
                intent.setPackage(mapsPackageName);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(),
                        "The GoogleMaps app is not currently installed on your device. Please install it and try again. ", Toast.LENGTH_LONG).show();
            }
        } else {
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
    }

    private boolean checkAppInstall(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    class Custominfowindowadpater implements GoogleMap.InfoWindowAdapter {
        private final View mymarkerview;

        Custominfowindowadpater() {
            mymarkerview = getActivity().getLayoutInflater()
                    .inflate(R.layout.markerlayout, null);

        }

        public View getInfoWindow(Marker marker) {
            render(marker, mymarkerview);
            return mymarkerview;
        }

        public View getInfoContents(Marker marker) {
            return null;
        }

        private void render(Marker marker, View view) {
            try {
                Log.v("", "required values");
                TextView type = (TextView) view.findViewById(R.id.delivery_type);
                TextView date = (TextView) view.findViewById(R.id.delivery_value);
                ImageView markerback = (ImageView) view.findViewById(R.id.markerback);
                if (marker.getTitle().startsWith("nosnippet")) {
                    return;
                } else if (marker.getTitle().startsWith("pickupp1-")) {
                    markerback.setImageResource(R.drawable.pickup_tooltip);
                    String string = marker.getTitle();
                    String[] parts = string.split("-");
                    String part1 = parts[0];
                    String part2 = parts[1];
                    type.setText(part2);
                } else {
                    markerback.setImageResource(R.drawable.drop_off_tooltip);
                    type.setText(marker.getTitle());
                }
                try {
                    if (marker.getSnippet() != null) {
                        date.setText(marker.getSnippet());
                    } else {
                        date.setText("");
                    }
                } catch (Exception e) {
                    e.toString();
                }

            } catch (Exception e) {
                e.toString();
            }

            // Add the code to set the required values
            // for each element in your custominfowindow layout file
        }
    }

    @Override
    public void onStart() {
        deliveryID = getArguments().getString("deliveryID");
        incomingType = getArguments().getString("incoming_type");
        incoming_delivery_type = getArguments().getString("incoming_delivery_type");

        if (incomingType.equals("shipper")) {
            HomeActivity.title.setText(getResources().getString(R.string.track));
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
        } else if (incomingType.equals("driver")) {
            if (incoming_delivery_type.equals("3"))
                HomeActivity.title.setText(getResources().getString(R.string.track));
            else
                HomeActivity.title.setText(getResources().getString(R.string.propose));
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
        }
        Log.v("deliveryID", "deliveryID:" + deliveryID);
        if (incoming_delivery_type.equals("2")) {
            //    if (!isCancelled) {

            h.postDelayed(new Runnable() {
                public void run() {
                    //do something
                    // if (!IsFirst) {
                    // getLocation();
                    if (!isCancelled) {
                        appendupdateData();
                        runnable = this;
                        h.postDelayed(runnable, delay);
                        //  }
                    }
                }
            }, delay);

        }
        // }
        if (incoming_delivery_type.equals("3"))
            mWazeIcon.setVisibility(View.GONE);
        appendData();


        super.onStart();
    }

    public void getLocation() {
        typee = 1;
        String url = Config.SERVER_URL + Config.DELIVREY_ROUTE + "?id=" + deliveryID;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null, false);
            mobileAPI.execute(url, session.getToken());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        try {
            if (incoming_delivery_type.equals("2")) {
                h.removeCallbacks(runnable);
                h.removeCallbacksAndMessages(null);
                if (!IsFirst) {

                }
            }
        } catch (Exception e) {
            e.toString();
        }
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        isCancelled = true;
        //  HomeActivity.edit.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isCancelled = true;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

// Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

// Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

// Sensor enabled
        String sensor = "sensor=false";

// Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

// Output format
        String output = "json";

// Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&alternatives=true";

        //String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?types=geocode&key=AIzaSyD6WMCZAgjZjaDz3z8bylRo8y4sJ37p7To&input=chandi";

        Log.v("url", "url:.." + url);
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
            Log.d("Exception ", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
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
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
// doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.v("data", "data:" + result);

            ParserTask parserTask = new ParserTask();

// Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    PolylineOptions lineOptions = new PolylineOptions();
    List<LatLng> points = null;

    /**
     * A class to parse the Google Places in JSON format
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
            try {
                ArrayList<LatLng> selectedLine = new ArrayList<LatLng>();
                selectedLine.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                points = new ArrayList();
                lineOptions = new PolylineOptions();
                int minIndex = DirectionsJSONParser.distance.indexOf(Collections.min(DirectionsJSONParser.distance));
                List<HashMap<String, String>> pathmap = result.get(minIndex);  //Fetching min distance route
                Log.v("result", "result:" + result.get(minIndex));
               /* if (route != null && route.length() > 1) {
                    for (int j = 0; j < path.length(); j++) {
                        try {
                            JSONObject point = (JSONObject) path.get(j);
                            double lat = Double.parseDouble(point.getString("latitude"));
                            double lng = Double.parseDouble(point.getString("longitude"));
                            LatLng position = new LatLng(lat, lng);
                            points.add(position);
                        } catch (Exception ex) {
                            Log.v("error:", "error:" + ex.getMessage());
                        }
                    }
                } else {*/
                for (int j = 0; j < pathmap.size(); j++) {
                    HashMap<String, String> point = pathmap.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                //}
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(6);
                selectedLine.addAll(points);
                lineOptions.color(Color.RED);
                googleMap.addPolyline(lineOptions);
                IsFirst = false;
            } catch (Exception e) {
                e.toString();
            }
        }
    }
}

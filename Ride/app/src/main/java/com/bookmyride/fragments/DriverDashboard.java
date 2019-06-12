package com.bookmyride.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.bookmyride.R;
import com.bookmyride.activities.AddBankDetail;
import com.bookmyride.activities.BeginRide;
import com.bookmyride.activities.DriverArrived;
import com.bookmyride.activities.DriverHome;
import com.bookmyride.activities.DriverTypes;
import com.bookmyride.activities.EndRide;
import com.bookmyride.activities.NotificationDialogs;
import com.bookmyride.activities.RideRequestList;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.fcm.NotificationFilters;
import com.bookmyride.util.ImageLoader;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class DriverDashboard extends Fragment implements AsyncTaskCompleteListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    MapView mMapView;
    private GoogleMap googleMap;
    //TrackGPS gps;
    double latitude, longitude;
    SessionHandler session;
    ImageView driverImg;
    TextView userName, carNo, goOnline;
    ImageLoader imgLoader;
    RatingBar rating;
    LinearLayout layBottom;
    TextView lastEarning, todayTip, totalEarning, totalTips, todayRides, lastRide;
    boolean hasFleet = false;
    ImageView gpsBtn;
    String fleetID = "null";
    RelativeLayout rateOverlay;
    private BroadcastReceiver mReceiver;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.driver_home, null);
        init(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGoogleAPIClient();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMap(view, savedInstanceState);
    }

    private void init(View view) {
        session = new SessionHandler(getActivity());
        //gps = new TrackGPS(getActivity());
        imgLoader = new ImageLoader(getActivity());
        userName = (TextView) view.findViewById(R.id.user_name);
        carNo = (TextView) view.findViewById(R.id.car_no);
        goOnline = (TextView) view.findViewById(R.id.go_online);
        goOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hasBackAccount();
                //goOnline();
            }
        });
        driverImg = (ImageView) view.findViewById(R.id.driver_img);
        rating = (RatingBar) view.findViewById(R.id.rating);
        layBottom = (LinearLayout) view.findViewById(R.id.lay_bottom);
        rateOverlay = (RelativeLayout) view.findViewById(R.id.rating_overlay);
        rateOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        layBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        gpsBtn = (ImageView) view.findViewById(R.id.gps);
        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if () {
                LatLng coordinate = new LatLng(latitude, longitude); //Store these lat lng values somewhere. These should be constant.
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                        coordinate, googleMap.getCameraPosition().zoom);
                googleMap.animateCamera(location);
                //}
            }
        });

        lastEarning = (TextView) view.findViewById(R.id.last_trip_earning);
        todayTip = (TextView) view.findViewById(R.id.today_tip);
        totalEarning = (TextView) view.findViewById(R.id.estimated_net);
        totalTips = (TextView) view.findViewById(R.id.tip);
        todayRides = (TextView) view.findViewById(R.id.today_rides);
        lastRide = (TextView) view.findViewById(R.id.last_ride);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NotificationFilters.LOCATION_CHANGED)) {
                    double lat = intent.getDoubleExtra("lat", 0.0);
                    double lng = intent.getDoubleExtra("lng", 0.0);
                    latitude = lat;
                    longitude = lng;
                    //Place current location marker
                    LatLng latLng = new LatLng(lat, lng);
                    if (googleMap != null) {
                        float zoomLevel = googleMap.getCameraPosition().zoom;
                        if (zoomLevel < 10)
                            zoomLevel = 12.2f;
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
                        googleMap.moveCamera(cameraUpdate);
                    }
                }
            }
        };
    }

    private void getProfile() {
        type = 0;
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET, this, null);
            apiHandler.execute(Config.GET_PROFILE + session.getUserID() + "?expand=profile,carModel,rating,userDashboard,booking", session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void appendData(JSONObject dataObj) {
        try {
            if (dataObj.has("fleet_id"))
                fleetID = dataObj.getString("fleet_id");
            session.saveReferralCode(dataObj.getString("referralCode"));
            userName.setText(dataObj.getString("fullName"));
            final String imgUrl = dataObj.getString(Key.IMAGE);
            Log.e("imgUrl", imgUrl);
            if (!imgUrl.equals("") && !imgUrl.equals("null")) {
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imgLoader.DisplayImage(imgUrl, driverImg);
                    }
                }, 1000);
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imgLoader.DisplayImage(imgUrl, DriverHome.userImg);
                    }
                }, 1000);
            } else {
                driverImg.setBackgroundResource(R.drawable.driver_icon);
                DriverHome.userImg.setBackgroundResource(R.drawable.driver_icon);
            }
            JSONObject profileObj = dataObj.getJSONObject(Key.PROFILE);
            rating.setRating(Float.parseFloat(profileObj.get("rating").toString()));

            /* String address = profileObj.getString(Key.ADDRESS);
            JSONObject addressObj = new JSONObject(address);
            carCategory.setText("Address: "+addressObj.getString("address")+", "+ addressObj.getString("city")+"\n"+
            addressObj.getString("state")+", "+ addressObj.getString("country")); */
            if (dataObj.has(Key.IS_PREMIUM)) {
                if (dataObj.get(Key.IS_PREMIUM).toString().equals("1"))
                    hasFleet = true;
                else
                    hasFleet = false;
            }
            String vehicle = profileObj.get("vehicleDetail").toString();
            if (hasFleet) {
                if (fleetID.equals("null"))
                    carNo.setText("Available for Fleets\nCurrently fleets are not assigned to you." +
                            " Please contact with administrator.");
                else
                    carNo.setText("Available for Fleets");
            } else if (!vehicle.equals("") && !vehicle.equals("null")) {
                JSONArray vehicleArray = new JSONArray(vehicle);
                String vehicleInfo = "";
                for (int i = 0; i < vehicleArray.length(); i++) {
                    JSONObject vehicleObj = vehicleArray.getJSONObject(i);
                    vehicleInfo += vehicleObj.getString("vehicle_type") +/* ": " + vehicleObj.getString("registerationNumber") +*/ ", ";
                }
                carNo.setText(vehicleInfo.substring(0, vehicleInfo.length() - 2).trim());
            }

            JSONObject earning = dataObj.getJSONObject("userDashboard");
            String currency = earning.getString("currency");
            if (!earning.get("lastRideEarning").toString().equals("")
                    && !earning.get("lastRideEarning").toString().equals("null")
                    && !earning.get("lastRideTip").toString().equals("")
                    && !earning.get("lastRideTip").toString().equals("null")) {
                float todayEarn = Float.valueOf(earning.get("lastRideEarning").toString());
                float todayTp = Float.valueOf(earning.get("lastRideTip").toString());
                float lastRideTotal = todayEarn + todayTp;
                lastEarning.setText("" + currency + lastRideTotal);
            } else {
                lastEarning.setText("" + currency + "0.0");
            }
            //lastEarning.setText(currency+earning.get("todayEarning").toString());
            //todayEarning.setText(currency+earning.get("todayEarning").toString());
            todayTip.setText(earning.get("todayRideWithTip").toString() + " Tips");
            totalEarning.setText(currency + earning.get("todayEarning").toString());
            totalTips.setText(currency + earning.get("todayTip").toString());
            todayRides.setText(earning.get("todayRide").toString() + " Trips");

            if (!earning.get("lastPickupDate").toString().equals("") &&
                    !earning.get("lastPickupDate").toString().equals("null")) {
                String last_ride = getLatRide(Long.parseLong(earning.get("lastPickupDate").toString()));
                lastRide.setText(last_ride);
            }

            String isOnline = dataObj.getString("isOnline");
            JSONObject isOnlineObj = new JSONObject(isOnline);
            String status = isOnlineObj.get("status").toString();
            String driverCat = isOnlineObj.get("driverCategory_id").toString();
            if ((status.equals("") || status.equals("0")) && !session.getRideData().equals("")) {
                showRideRequest(getActivity().getApplicationContext(), session.getRideData());
                session.saveRideData("");
            } else if (status.equals("1") && dataObj.get("booking").toString().equals("null")) {
                session.saveLastCategory(driverCat);
                Bundle bundle = new Bundle();
                bundle.putString("CategoryID", driverCat);
                //if(hasFleet) {
                if (isOnlineObj.has("vehicle_id")) {
                    if (!isOnlineObj.getString("vehicle_id").equals("0")) {
                        bundle.putBoolean("hasFleet", true);
                        bundle.putString("selectedModel", isOnlineObj.getString("vehicle_model"));
                        bundle.putString("selectedNumber", isOnlineObj.getString("vehicle_num"));
                        bundle.putString("selectedVehicleId", isOnlineObj.getString("vehicle_id"));
                    } else {
                        bundle.putBoolean("hasFleet", false);
                    }
                }
                displayDriverMap(bundle);
            } else if (dataObj.has("booking")) {
                if (!dataObj.get("booking").toString().equals("null")) {
                    JSONObject bookingObj = dataObj.getJSONObject("booking");
                    onRideAlert(bookingObj.toString(),
                            "One of your ride is in progress. Please click on track to track the ride.");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String response;

    private void initMap(View rootView, Bundle savedInstanceState) {
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                /*try {
                    // Customise map styling via JSON file
                    boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.mapstyle));

                    if (!success) {
                        Log.e("style", "Style parsing failed.");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e("style", "Can't find style. Error: ", e);
                }*/
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                //latitude = gps.getLatitude();
                //longitude = gps.getLongitude();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latitude, longitude))      // Sets the center of the map to location user
                        .zoom(12f)             // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        googleMap.clear();
                    }
                });
            }
        });
    }

    String status = "";

    public void showRideRequest(Context context, String rideInfo) {
        try {
            JSONObject rideData = new JSONObject(rideInfo);
            if (rideData.has("status"))
                status = rideData.getString("status");
            Log.e("r_data", rideData.toString());
            String bookingType = rideData.getString("type");
            if (rideData.has("driver_id")) {
                String driver_id = rideData.getString("driver_id");
                if (bookingType.equals("1") && !driver_id.equals("")) {
                    startActivity(new Intent(context, NotificationDialogs.class)
                            .putExtra("rideData", rideInfo));
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    if (status.equalsIgnoreCase("") || status.equalsIgnoreCase("0")) {
                        startActivity(new Intent(context, RideRequestList.class)
                                .putExtra("rideData", rideInfo));
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
            } else {
                if (status.equalsIgnoreCase("") || status.equalsIgnoreCase("0")) {
                    startActivity(new Intent(context, RideRequestList.class)
                            .putExtra("rideData", rideInfo));
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
        if (mGoogleApiClient != null)
            if (!mGoogleApiClient.isConnected())
                mGoogleApiClient.connect();
        getProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        /*LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver,
                new IntentFilter(NotificationFilters.LOCATION_CHANGED));*/
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    int type = 0;

    private void hasBackAccount() {
        type = 2;
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET, this, null);
            apiHandler.execute(Config.CHECK_BACK_ACCOUNT, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void showRideDialog(String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(getActivity(), false);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setCancelOnTouchOutside(false);
        mDialog.setPositiveButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.setNegativeButton("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                startActivityForResult(new Intent(getActivity(), AddBankDetail.class)
                        .putExtra("isBack", ""), SAVE_CARD);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        mDialog.show();
    }

    int SAVE_CARD = 151;

    private void goOnline() {
        Intent i = new Intent(getActivity(), DriverTypes.class);
        i.putExtra("hasFleet", hasFleet);
        i.putExtra("fleetID", fleetID);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        /* type = 1;
        HashMap<String, String> params = new HashMap<>();
        params.put(Key.STATUS, "1");
        params.put("driverCategory_id", );
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.PUT, this, params);
            apiHandler.execute(Config.DRIVER_AVAILABLITY+"1", session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet)); */
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
        response = result;
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                if (type == 1) {
                    Intent i = new Intent(getActivity(), DriverTypes.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (type == 2) {
                    goOnline();
                } else if (type == 0) {
                    JSONObject dataObj = outJson.getJSONObject(Key.DATA);
                    session.saveDriverData(
                            dataObj.getString(Key.ID),
                            "4",
                            dataObj.getString(Key.USERNAME),
                            dataObj.getString(Key.IMAGE),
                            dataObj.getString(Key.EMAIL),
                            dataObj.getString(Key.TOKEN),
                            dataObj.getString(Key.FIRST_NAME),
                            dataObj.getString(Key.LAST_NAME),
                            dataObj.getString(Key.PHONE),
                            dataObj.getString(Key.DIAL_CODE),
                            dataObj.get(Key.PROFILE).toString(),
                            dataObj.get(Key.IS_ONLINE).toString(),
                            dataObj.has(Key.IS_CUSTOMER) ? dataObj.getString(Key.IS_CUSTOMER) : "0");
                    appendData(outJson.getJSONObject(Key.DATA));
                }
            } else if (outJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                if (type == 2) {
                    showRideDialog("Alert!", outJson.getString(Key.MESSAGE));
                } else
                    Alert("Alert!", outJson.getString(Key.MESSAGE));
            } else {
                Alert("Alert!", outJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    private void displayDriverMap(Bundle bundle) {
        Fragment fragment = new com.bookmyride.fragments.DriverMapActivity();
        fragment.setArguments(bundle);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commitAllowingStateLoss();
    }

    private void onRideAlert(final String rideData, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(getActivity(), false);
        mDialog.setDialogTitle("BookMyRide");
        mDialog.setDialogMessage(message);
        mDialog.setCancelOnTouchOutside(false);
        mDialog.setPositiveButton("Track", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                try {
                    JSONObject rideObj = new JSONObject(rideData);
                    String status = rideObj.get("status").toString();
                    String bookingID = rideObj.get("id").toString();

                    if (status.equals("1")) {
                        startActivity(new Intent(getActivity(), DriverArrived.class)
                                //startActivity(new Intent(getActivity(), StartRiding.class)
                                .putExtra("bookingId", bookingID)
                                .putExtra("passenger_name", rideObj.getString("name"))
                                .putExtra("passenger_phone", rideObj.getString("phone"))
                                .putExtra("type", rideObj.getString("type"))
                                .putExtra("payment_status", rideObj.getString("paymentStatus"))
                                .putExtra("pickUp", rideObj.getString("pickUp"))
                                .putExtra("is_discount", rideObj.get("is_discount").toString().equals("1") ? true : false)
                                .putExtra("driverCategory", rideObj.getString("driverCategory_id"))
                                .putExtra("dropOff", rideObj.getString("dropOff")));
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else if (status.equals("12")) {
                        startActivity(new Intent(getActivity(), DriverArrived.class)
                                .putExtra("bookingId", bookingID)
                                .putExtra("passenger_name", rideObj.getString("name"))
                                .putExtra("passenger_phone", rideObj.getString("phone"))
                                .putExtra("type", rideObj.getString("type"))
                                .putExtra("payment_status", rideObj.getString("paymentStatus"))
                                .putExtra("pickUp", rideObj.getString("pickUp"))
                                .putExtra("is_discount", rideObj.get("is_discount").toString().equals("1") ? true : false)
                                .putExtra("driverCategory", rideObj.getString("driverCategory_id"))
                                .putExtra("dropOff", rideObj.getString("dropOff")));
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else if (status.equals("3")) {
                        startActivity(new Intent(getActivity(), BeginRide.class)
                                .putExtra("bookingId", bookingID)
                                .putExtra("passenger_name", rideObj.getString("name"))
                                .putExtra("passenger_phone", rideObj.getString("phone"))
                                .putExtra("type", rideObj.getString("type"))
                                .putExtra("payment_status", rideObj.getString("paymentStatus"))
                                .putExtra("pickUp", rideObj.getString("pickUp"))
                                .putExtra("is_discount", rideObj.get("is_discount").toString().equals("1") ? true : false)
                                .putExtra("driverCategory", rideObj.getString("driverCategory_id"))
                                .putExtra("dropOff", rideObj.getString("dropOff")));
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else if (status.equals("4")) {
                        startActivity(new Intent(getActivity(), EndRide.class)
                                .putExtra("bookingId", bookingID)
                                .putExtra("passenger_name", rideObj.getString("name"))
                                .putExtra("passenger_phone", rideObj.getString("phone"))
                                .putExtra("type", rideObj.getString("type"))
                                .putExtra("payment_status", rideObj.getString("paymentStatus"))
                                .putExtra("pickUp", rideObj.getString("pickUp"))
                                .putExtra("is_discount", rideObj.get("is_discount").toString().equals("1") ? true : false)
                                .putExtra("driverCategory", rideObj.getString("driverCategory_id"))
                                .putExtra("dropOff", rideObj.getString("dropOff")));
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else if (status.equals("5")) {
                        String cardStatus = rideObj.getString("cardStatus");
                        String cardDetail = rideObj.get("cardDetail").toString();
                        startActivity(new Intent(getActivity(), AcceptPayment.class)
                                .putExtra("bookingID", bookingID)
                                .putExtra("cardStatus", cardStatus)
                                .putExtra("cardDetail", cardDetail)
                                .putExtra("type", rideObj.getString("type")));
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        /*mDialog.setNegativeButton("Cancel",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });*/
        mDialog.show();
    }

    private String getLatRide(long unixSeconds) {
        System.out.println("unix:" + unixSeconds);
        //long unixSeconds = 1372339860;
        Date date = new Date(unixSeconds * 1000L); // *1000 is to convert seconds to milliseconds
        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = simpleDateFormat.format(date);
        System.out.println("format:" + formattedDate);
        return formattedDate;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null)
            if (mGoogleApiClient.isConnected())
                mGoogleApiClient.disconnect();
        mMapView.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        mLocationRequest.setInterval(10000);    // 10 seconds, in milliseconds
        mLocationRequest.setFastestInterval(1000);   // 1 second, in milliseconds
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
                //final LocationSettingsStates state = result.getLocationSettingsStates();
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
            case 151:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        goOnline();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.e("action", "No Action");
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    Location mLastLocation;

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
            if (mLastLocation != null && googleMap != null) {
                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                float zoomLevel = googleMap.getCameraPosition().zoom;
                if (zoomLevel < 10)
                    zoomLevel = 12.2f;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
                googleMap.moveCamera(cameraUpdate);
            } else {
                /*if there is no last known location. Which means the device has no data for the location currently.
                * So we will get the current location.
                * For this we'll implement Location Listener and override onLocationChanged*/
                Log.i("Current Location", "No data for location found");

                if (!mGoogleApiClient.isConnected())
                    mGoogleApiClient.connect();

                if (mGoogleApiClient.isConnected())
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /*When Location changes, this method get called. */
    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (googleMap != null) {
            float zoomLevel = googleMap.getCameraPosition().zoom;
            if (zoomLevel < 10)
                zoomLevel = 12.2f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
            googleMap.moveCamera(cameraUpdate);
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
}

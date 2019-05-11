package com.grabid.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.adapters.ListMapAdapter;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.LatLngInterpolator;
import com.grabid.common.SessionManager;
import com.grabid.models.CompanyInfo;
import com.grabid.models.HomeData;
import com.grabid.models.UserInfo;
import com.grabid.services.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.grabid.R.id.container;

/**
 * Created by vinod on 10/14/2016.
 */
public class HomeMap extends Fragment implements View.OnClickListener,
        AsyncTaskCompleteListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
    TextView freight, appliance, vehicle, livestock;
    ListView list;
    TextView listAll, listFreight, listAppliance, listVehicle, listLivestock, all, takeNow, active, future;
    LinearLayout layTop;
    MapView mMapView;
    private GoogleMap googleMap;
    SessionManager session;
    ValueAnimator mAnimator;
    ImageView readToBid, upDown;
    RelativeLayout layBid, layMap;
    LinearLayout layList;
    GPSTracker gps;
    double latitude, longitude;
    ListMapAdapter adapter;
    TextView noData;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private static final String TAG = HomeMap.class.getSimpleName();
    boolean mFirstUpdate = true, mNotification = true;
    int LOCATION_PERMISSION_CODE = 101;
    ImageView mRefreshIcon;
    Marker mCurrentMarker;
    static final float COORDINATE_OFFSET = 0.00002f;
    CompanyInfo companyinfo;
    //    private int firstVisibleItem, visibleItemCount, totalItemCount;
    int page = 1;
    int totalCount = 1;
    boolean IsLocationUpdate = false;
    String[] permissionsRequired = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION};
    int REQUEST_PERMISSION_SETTING = 200;
    UserInfo userInfo;

    //  HashMap<String, String> vehicleValues = new HashMap<>();
    //   String vehicleTypeID = "";
    //  ArrayList mvehicleArray, mVehicleId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId().requestEmail().requestIdToken("845250573232-e00i4gde6t0mp1t558pvto11r2bgc1s0.apps.googleusercontent.com")
                .build();
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .addApi(LocationServices.API).addConnectionCallbacks(this).addConnectionCallbacks(this)
                    .build();
            // mGoogleApiClient.connect();
            Log.v("oncreate", "oncreate");
        } catch (Exception e) {
            e.toString();
        }
        createLocationRequest();


    }

    public void UpdateDesign() {
        HomeActivity.edit.setVisibility(View.VISIBLE);
        //   HomeActivity.edit.setBackgroundResource(R.drawable.listing_icon);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("oncreateview", "oncreate");
        HomeActivity.edit.setVisibility(View.VISIBLE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.edit.setTag("map");
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.title.setText(getResources().getString(R.string.home));
        HomeActivity.edit.setBackgroundResource(R.drawable.listing_icon);
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        HomeActivity.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (userInfo.getIsprofileCompleted().equals("0"))
                        showProfileDialog();
                    else if (userInfo.getAdminApprovalStatus().equals("0") || userInfo.getVerifiedStatus().equals("0"))
                        showEmailConfirmDialog();
                    else {
                        if (HomeActivity.edit.getTag().equals("map")) {
                            HomeActivity.edit.setTag("list");
                            HomeActivity.edit.setBackgroundResource(R.drawable.location_icon);
                            layMap.setVisibility(View.GONE);
                            layList.setVisibility(View.VISIBLE);
                            getData(Id, subId);

                        } else {
                            HomeActivity.edit.setTag("map");
                            HomeActivity.edit.setBackgroundResource(R.drawable.listing_icon);
                            layList.setVisibility(View.GONE);
                            layMap.setVisibility(View.VISIBLE);
                            getData("", "");
                        }
                    }
                } catch (Exception e) {
                    e.toString();
                }
            }
        });
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        init(rootView);
        initMap(rootView, savedInstanceState);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            if (!(latitude == 0.0) || !(longitude == 0.0))
                mFirstUpdate = false;
        }
        getData("");
        updateUI(1);
        updateSubUI(1);
        return rootView;
    }

    public void settingsrequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                            startLocationUpdates();
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().

                            status.startResolutionForResult((HomeActivity) getActivity(), 9);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.v("", "");
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                startLocationUpdates();
            }
        } else if (requestCode == 9) {
            try {
                if (resultCode == -1)
                    if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
                        startLocationUpdates();
                //settingsrequest();
            } catch (Exception e) {
                e.toString();
            }

        }
    }


    public void animateCamera() {
        LatLng currentPos = new LatLng(latitude, longitude);
        //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(currentPos).zoom(14).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

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
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(false);
                animateCamera();
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        try {
                            if (userInfo.getIsprofileCompleted().equals("0"))
                                showProfileDialog();
                            else if (userInfo.getAdminApprovalStatus().equals("0") || userInfo.getVerifiedStatus().equals("0"))
                                showEmailConfirmDialog();
                            else {
                                JSONObject obj = new JSONObject(marker.getTitle());
                                String backStateName = this.getClass().getName();
                                if (!obj.getString("id").contentEquals("null")) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("delivery_id", obj.getString("id"));
                                    bundle.putSerializable("incoming_type", "home");
                                    //                          bundle.putSerializable("incoming_type", "map");
                                    Fragment fragment = new DeliveryInfo();
                                    fragment.setArguments(bundle);
                                    getActivity().getFragmentManager().beginTransaction().replace(container, fragment, backStateName)
                                            .addToBackStack(null)
                                            .commitAllowingStateLoss();
                                }
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
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    // Defines the contents of the InfoWindow
                    @Override
                    public View getInfoContents(Marker arg0) {
                        View v = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);
                        try {
                            JSONObject obj = new JSONObject(arg0.getTitle());
                            TextView tvLat = (TextView) v.findViewById(R.id.title);
                            TextView tvLng = (TextView) v.findViewById(R.id.address);
                            tvLat.setText(obj.getString("title"));
                            tvLng.setText(commaCount(obj.getString("address")));
                            if (obj.getString("id").contentEquals("null")) {
                                tvLng.setVisibility(View.GONE);
                                ImageView img = (ImageView) v.findViewById(R.id.info);
                                img.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return v;
                    }
                });
            }
        });
    }

    private String commaCount(String input) {
        String output = "";
        if (input.contains(", ")) {
            String addArray[] = input.split(", ");
            if (addArray.length > 2) {
                output = addArray[addArray.length - 3] + ", " + addArray[addArray.length - 2] + ", " + addArray[addArray.length - 1];
            } else {
                output = input;
            }
        } else {
            output = input;
        }
        return output;
    }

    private void init(View rootView) {
        session = new SessionManager(getActivity());
        companyinfo = session.getCompanyInfo();
        userInfo = session.getUserDetails();
        gps = new GPSTracker(getActivity());
        freight = (TextView) rootView.findViewById(R.id.freight);
        appliance = (TextView) rootView.findViewById(R.id.appliance);
        vehicle = (TextView) rootView.findViewById(R.id.vehicle);
        livestock = (TextView) rootView.findViewById(R.id.live_stock);
        upDown = (ImageView) rootView.findViewById(R.id.up_down);
        mRefreshIcon = (ImageView) rootView.findViewById(R.id.refresh);
        upDown.setOnClickListener(this);
        layTop = (LinearLayout) rootView.findViewById(R.id.lay_top);
        freight.setOnClickListener(this);
        appliance.setOnClickListener(this);
        vehicle.setOnClickListener(this);
        livestock.setOnClickListener(this);
        listAll = (TextView) rootView.findViewById(R.id.all);
        listFreight = (TextView) rootView.findViewById(R.id.list_freight);
        listAppliance = (TextView) rootView.findViewById(R.id.list_appliance);
        listVehicle = (TextView) rootView.findViewById(R.id.list_vehicle);
        listLivestock = (TextView) rootView.findViewById(R.id.list_live_stock);
        listAll.setOnClickListener(this);
        listFreight.setOnClickListener(this);
        listAppliance.setOnClickListener(this);
        listVehicle.setOnClickListener(this);
        listLivestock.setOnClickListener(this);
        all = (TextView) rootView.findViewById(R.id.all_next);
        takeNow = (TextView) rootView.findViewById(R.id.take_now);
        active = (TextView) rootView.findViewById(R.id.active);
        future = (TextView) rootView.findViewById(R.id.future);
        all.setOnClickListener(this);
        takeNow.setOnClickListener(this);
        active.setOnClickListener(this);
        future.setOnClickListener(this);
        list = (ListView) rootView.findViewById(R.id.list);
        list.setSmoothScrollbarEnabled(true);
        adapter = new ListMapAdapter(getActivity(), data);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (userInfo.getIsprofileCompleted().equals("0"))
                    showProfileDialog();
                else if (userInfo.getAdminApprovalStatus().equals("0") || userInfo.getVerifiedStatus().equals("0"))
                    showEmailConfirmDialog();
                else {
                    String backStateName = this.getClass().getName();
                    Bundle bundle = new Bundle();
                    bundle.putString("delivery_id", data.get(i).getId());
                    bundle.putSerializable("incoming_type", "home");
                    Fragment fragment = new DeliveryInfo();
                    fragment.setArguments(bundle);
                    getActivity().getFragmentManager().beginTransaction().replace(container, fragment, backStateName)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                }
            }
        });
        noData = (TextView) rootView.findViewById(R.id.no_data);
        layList = (LinearLayout) rootView.findViewById(R.id.lay_list);
        layMap = (RelativeLayout) rootView.findViewById(R.id.lay_map);
        layBid = (RelativeLayout) rootView.findViewById(R.id.lay_bid);
        layBid.setOnClickListener(this);
        readToBid = (ImageView) rootView.findViewById(R.id.on_off);
        mRefreshIcon.setOnClickListener(this);
        readToBid.setOnClickListener(this);
        layTop.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        layTop.getViewTreeObserver().removeOnPreDrawListener(this);
                        layTop.setVisibility(View.GONE);
                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(
                                0, View.MeasureSpec.UNSPECIFIED);
                        final int heightSpec = View.MeasureSpec
                                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        layTop.measure(widthSpec, heightSpec);
                        mAnimator = slideAnimator(0, layTop.getMeasuredHeight());
                        return true;
                    }
                });
        if (session.getAvailability().equals("1")) {
            readToBid.setImageResource(R.drawable.on_btn);
            layBid.setBackgroundColor(getResources().getColor(R.color.seagreen));
        } else {
            readToBid.setImageResource(R.drawable.off_btn);
            layBid.setBackgroundColor(getActivity().getResources().getColor(R.color.darkblue));
        }

       /* try {
            JSONArray vehicleArray = new JSONArray(companyinfo.getVehiclesInfo());
            for (int i = 0; i < vehicleArray.length(); i++) {
                JSONObject jobj = vehicleArray.getJSONObject(i);
                vehicleValues.put(jobj.getString("vehicle_type"), jobj.getString("vehicle_type_string"));
            }
            mvehicleArray = new ArrayList();
            mVehicleId = new ArrayList();
            for (Map.Entry<String, String> entry : vehicleValues.entrySet()) {
                mVehicleId.add(entry.getKey());
                mvehicleArray.add(entry.getValue());

            }
            Log.v("", vehicleValues.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
*/

    }

    public void checkNotificationPermission() {
        try {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getActivity());
            boolean areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();
            //String appPushEnabled = String.valueOf(areNotificationsEnabled);
            if (!areNotificationsEnabled) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getActivity().getResources().getString(R.string.sendyounotification));
                builder.setMessage(getActivity().getResources().getString(R.string.enablenoti));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        try {
                            Intent intentP = new Intent();
                            intentP.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                            intentP.setData(uri);
                            //  intentP.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            //intentP.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(intentP, 1);
                       /* Intent intent = new Intent();
                        intent.setClassName("com.android.settings", "com.android.settings.Settings$AppNotificationSettingsActivity");
                        intent.putExtra("app_package", getActivity().getPackageName());
                        intent.putExtra("app_uid", getActivity().getApplicationInfo().uid);
                        startActivity(intent);*/
                        } catch (Exception e) {
                            e.toString();
                        }
                    }
                });
                builder.setNegativeButton("Don’t Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            }
        } catch (Exception e) {
            e.toString();
        }
    }

    private void showProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Success!");
        builder.setMessage(getActivity().getResources().getString(R.string.profileactivate));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("profileup", "1");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showEmailConfirmDialog() {
        String message = "";
        if (userInfo.getAdminApprovalStatus().equals("0") && userInfo.getVerifiedStatus().equals("0"))
            message = getActivity().getResources().getString(R.string.verifyemailandaccount);
        else if (userInfo.getAdminApprovalStatus().equals("0"))
            message = getActivity().getResources().getString(R.string.verifyadmin);
        else if (userInfo.getVerifiedStatus().equals("0"))
            message = getActivity().getResources().getString(R.string.verifyemail);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

        /*if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        if (IsLocationUpdate)
            stopLocationUpdates();

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("onstart", "oncreate");
        mGoogleApiClient.connect();
      /*  final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                if (!IsLocationUpdate)
                    startLocationUpdates();
            }
        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null)
            mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null)
            mMapView.onLowMemory();
    }

    @Override
    public void onTaskComplete(String result) {
        Log.d(Config.DATA, result);
        handleResponse(result);
    }

    ArrayList<HomeData> data = new ArrayList<>();

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                if (type == 1) {
                    JSONArray dataArray = outJson.getJSONArray(Config.DATA);
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject obj = dataArray.getJSONObject(i);
                        HomeData homeData = new HomeData();
                        homeData.setId(obj.getString(Keys.KEY_ID));
                        try {
                            homeData.setJob_ID(obj.getString(Keys.KEY_JOB_ID));
                        }catch (Exception ex){
                            homeData.setJob_ID("");
                        }

                        homeData.setLiftequipement(obj.getString(Keys.LIFT_EQUIPMENT));
                        homeData.setDeliveryStatus(obj.getString(Keys.DELIVERY_STATUS));
                        homeData.setStatus(obj.getString(Keys.STATUS));
                        homeData.setAuctionBid(obj.getString(Keys.AUCTION_BID));
                        homeData.setDeliveryStatusType(obj.getString(Keys.DELIVERY_STATUS_TYPE));
                        homeData.setDeliveryTypeId(obj.getString(Keys.DELIVERY_TYPE_ID));
                        homeData.setDistance(obj.getString(Keys.DISTANCE));
                        homeData.setDriverDistance(obj.getString(Keys.DRIVER_DISTANCE));
                        homeData.setGeoZone(obj.getString(Keys.GEO_ZONE));
                        homeData.setItemDeliveryTitle(obj.getString(Keys.ITEM_DELIVERY_TITLE));
                        homeData.setItemMoreDetails(obj.getString(Keys.ITEM_MORE));
                        homeData.setPickupAddress(obj.getString(Keys.PICKUP_ADDRESS));
                        try {
                            homeData.setPick_up_city(obj.getString(Keys.PICKUP_CITY));
                        }catch(Exception ex){
                            homeData.setPick_up_city("null");
                        }
                        try {
                            homeData.setPickUpState(obj.getString(Keys.PICKUP_STATE));
                        }catch(Exception ex){
                            homeData.setPickUpState("null");
                        }
                        homeData.setPickupLatitude(obj.getString(Keys.PICKUP_LATITUDE) + i);
                        homeData.setPickupLongitude(obj.getString(Keys.PICKUP_LONGITUDE) + i);
                        homeData.setRadius(obj.getString(Keys.RADIUS));
                        homeData.setMaximumOpeningBid(obj.getString(Keys.MAX_AUCTION_BID));
                        homeData.setFixedOffer(obj.getString(Keys.FIXED_OFFER));
                        homeData.setPickupDay(obj.getString(Keys.PICKUP_DATE));
                        homeData.setDropoffDay(obj.getString(Keys.DROPOFF_DATE));
                        homeData.setItemPhoto(obj.getString(Keys.ITEM_PHOTO));
                        data.add(homeData);
                    }
                    adapter.notifyDataSetChanged();
                    addMarkersOnMap();
                    if (data.size() == 0) {
                        list.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                    } else {
                        noData.setVisibility(View.GONE);
                        list.setVisibility(View.VISIBLE);
                    }
                    getAvailability();
                } else if (type == 2) {
                    String availableStatus = outJson.getJSONObject(Config.DATA).getJSONObject(Config.USER).getString(Keys.AVAILABLE_STATUS);
                    session.saveAvailability(availableStatus);
                    if (availableStatus.equals("1")) {
                        readToBid.setImageResource(R.drawable.on_btn);
                        layBid.setBackgroundColor(getResources().getColor(R.color.seagreen));
                    } else {
                        readToBid.setImageResource(R.drawable.off_btn);
                        layBid.setBackgroundColor(getActivity().getResources().getColor(R.color.darkblue));
                    }
                } else if (type == 3) {
                    String availableStatus = outJson.getJSONObject(Config.DATA).getJSONObject(Config.USER).getString(Keys.AVAILABLE_STATUS);
                    session.saveAvailability(availableStatus);
                    if (availableStatus.equals("1")) {
                        readToBid.setImageResource(R.drawable.on_btn);
                        layBid.setBackgroundColor(getResources().getColor(R.color.seagreen));
                    } else {
                        readToBid.setImageResource(R.drawable.off_btn);
                        layBid.setBackgroundColor(getActivity().getResources().getColor(R.color.darkblue));
                    }
                    if (!userInfo.getIsprofileCompleted().equals("0")) {
                        if (userInfo.getAdminApprovalStatus().equals("0") || userInfo.getVerifiedStatus().equals("0"))
                            getUserProfileData();
                    } else {
                        if (mNotification) {
                            mNotification = false;
                            checkNotificationPermission();
                        }
                    }
                } else if (type == 4) {
                    UserData(outJson);
                    if (mNotification) {
                        mNotification = false;
                        checkNotificationPermission();
                    }
                }


            } else {
                AlertManager.messageDialog(getActivity(), "Alert!", outJson.getString(Config.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            list.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
    }

    public void getUserProfileData() {
        type = 4;
        String url = Config.SERVER_URL + Config.USER + Config.PROFILE_VIEW + userInfo.getId();
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void UserData(JSONObject outterJson) {
        try {
            JSONObject dataObj = outterJson.getJSONObject(Config.DATA);
            JSONObject UserInfo = dataObj.getJSONObject(Config.USER);
            JSONObject driverProfile = dataObj.getJSONObject(Config.DRIVER_PROFILE);
            JSONObject company = new JSONObject();
            JSONArray vehicle = new JSONArray();
            if (dataObj.get(Config.COMPANY) instanceof JSONObject) {
                company = dataObj.getJSONObject(Config.COMPANY);
                vehicle = company.getJSONArray(Config.VHICLE);
            }
            String sRating = "";
            String dRating = "";
            String AdminApprovalStatus = "";
            String EmailVerified = "";
            try {
                sRating = UserInfo.getString(Keys.SHIPPER_RATING);
                dRating = UserInfo.getString(Keys.DRIVER_RATING);
                AdminApprovalStatus = UserInfo.getString(Keys.ADMIN_APPROVAL_STATUS);
                EmailVerified = UserInfo.getString(Keys.VERIFIED_STATUS);
            } catch (Exception e) {
                e.toString();
            }
            String creditcard = dataObj.optString(Keys.CREDIT_CARD);
            String bankDetail = dataObj.optString(Keys.BANK_DETAIL);

            new SessionManager(getActivity()).saveUserDate(UserInfo.getString(Keys.KEY_ID),
                    UserInfo.getString(Config.USER_NAME),
                    UserInfo.getString(Keys.KEY_EMAIL),
                    dataObj.getString(Keys.KEY_IMAGE),
                    UserInfo.getString(Config.TOKEN),
                    UserInfo.toString(), driverProfile.toString(),
                    dataObj.getString(Keys.DRIVER_LIMAGE),
                    company.toString(), vehicle.toString(), sRating, dRating, dataObj.optString(Keys.CREDIT_CARD), dataObj.optString(Keys.BANK_DETAIL), AdminApprovalStatus, EmailVerified, false);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    String oldUi = "";

    public void ChangeUi(String id, String oldUi) {
        try {
            if (id.contentEquals(oldUi)) {
                return;
            }
        } catch (Exception e) {
            e.toString();
        }
        switch (id) {
            case "1": {
                freight.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.freight_active, 0, 0);
                freight.setTextColor(getResources().getColor(R.color.filer_text_color_selected));
                freight.setBackgroundColor(getResources().getColor(R.color.filer_background_color_selected));
                break;
            }
            case "2": {
                appliance.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.appliances_active, 0, 0);
                appliance.setTextColor(getResources().getColor(R.color.filer_text_color_selected));
                appliance.setBackgroundColor(getResources().getColor(R.color.filer_background_color_selected));
                break;
            }
            case "3": {
                vehicle.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.vehicles_active, 0, 0);
                vehicle.setTextColor(getResources().getColor(R.color.filer_text_color_selected));
                vehicle.setBackgroundColor(getResources().getColor(R.color.filer_background_color_selected));
                break;
            }
            case "4": {
                livestock.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.livestock_active, 0, 0);
                livestock.setTextColor(getResources().getColor(R.color.filer_text_color_selected));
                livestock.setBackgroundColor(getResources().getColor(R.color.filer_background_color_selected));
                break;
            }
        }
        if (!oldUi.contentEquals("")) {
            if (oldUi.contentEquals("1")) {
                freight.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.freight, 0, 0);
                freight.setTextColor(getResources().getColor(R.color.filer_text_color_unselected));
                freight.setBackgroundColor(getResources().getColor(R.color.filer_background_color_unselected));
            } else if (oldUi.contentEquals("2")) {
                appliance.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.appliances, 0, 0);
                appliance.setTextColor(getResources().getColor(R.color.filer_text_color_unselected));
                appliance.setBackgroundColor(getResources().getColor(R.color.filer_background_color_unselected));
            } else if (oldUi.contentEquals("3")) {
                vehicle.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.vehicles, 0, 0);
                vehicle.setTextColor(getResources().getColor(R.color.filer_text_color_unselected));
                vehicle.setBackgroundColor(getResources().getColor(R.color.filer_background_color_unselected));
            } else if (oldUi.contentEquals("4")) {
                livestock.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.livestock, 0, 0);
                livestock.setTextColor(getResources().getColor(R.color.filer_text_color_unselected));
                livestock.setBackgroundColor(getResources().getColor(R.color.filer_background_color_unselected));
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.freight:
                getData("1", "");
                ChangeUi("1", oldUi);
                oldUi = "1";
                //   collapse();
                break;
            case R.id.appliance:
                getData("2", "");
                //collapse();
                ChangeUi("2", oldUi);
                oldUi = "2";
                break;
            case R.id.vehicle:
                getData("3", "");
                //collapse();
                ChangeUi("3", oldUi);
                oldUi = "3";
                break;
            case R.id.live_stock:
                getData("4", "");
                // collapse();
                ChangeUi("4", oldUi);
                oldUi = "4";
                break;
            case R.id.all:
                Id = "";
                getData("");
                updateUI(1);
                break;
            case R.id.list_freight:
                Id = "1";
                getData("1");
                updateUI(2);
                break;
            case R.id.list_appliance:
                Id = "2";
                getData("2");
                updateUI(3);
                break;
            case R.id.list_vehicle:
                Id = "3";
                getData("3");
                updateUI(4);
                break;
            case R.id.list_live_stock:
                Id = "4";
                getData("4");
                updateUI(5);
                break;
            case R.id.up_down:
                if (layTop.getVisibility() == View.GONE) {
                    expand();
                } else {
                    collapse();
                }
                break;
            case R.id.on_off:
                setAvailability();
               /* try {
                    if (!session.getAvailability().contentEquals("")) {
                        int size = mvehicleArray.size();
                        if (mvehicleArray != null && mvehicleArray.size() > 1) {
                            if (session.getAvailability().contentEquals("0"))
                                showVehicleDialog();
                            else
                                setAvailability("0", "0");
                        } else {
                            if (mVehicleId != null && mVehicleId.size() > 0)
                                if (session.getAvailability().contentEquals("0"))
                                    setAvailability("1", (String) mVehicleId.get(0));
                                else
                                    setAvailability("0", "0");
                        }
                    }
                } catch (Exception e) {
                    e.toString();
                }*/

                break;
            case R.id.lay_bid:
                break;

            case R.id.all_next:
                subId = "";
                getData(Id);
                updateSubUI(1);
                break;
            case R.id.take_now:
                subId = "1";
                getData(Id);
                updateSubUI(2);
                break;
            case R.id.active:
                subId = "2";
                getData(Id);
                updateSubUI(3);
                break;
            case R.id.future:
                subId = "3";
                getData(Id);
                updateSubUI(4);
                break;
            case R.id.refresh:
                Fragment fragment = new HomeMap();
                String backStateName = "com.grabid.activities.HomeActivity";
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();

                break;
        }

    }

    /*public void showVehicleDialog() {
        final Dialog mDialog = new Dialog(getActivity(), R.style.GrabidDialog);
        //  mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list_new);
        mDialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        ImageView close = (ImageView) mDialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog.isShowing())
                    mDialog.dismiss();
            }
        });
        title.setText("Select Vehicle");

        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        ListView dialog_ListView = (ListView) mDialog.findViewById(R.id.list);
        ArrayAdapter<String> adapter = null;


        adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.dialog_textview, R.id.textItem, mvehicleArray);

        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String val = parent.getItemAtPosition(position).toString();
                for (Map.Entry<String, String> entry : vehicleValues.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (value.contentEquals(val)) {
                        vehicleTypeID = key;
                        Log.v("", vehicleTypeID);
                        setAvailability("1", vehicleTypeID);
                    }
                }
                Log.v("", vehicleTypeID);
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }*/

    private void expand() {
        layTop.setVisibility(View.VISIBLE);
        upDown.setImageResource((R.drawable.collapse_arrow_white));
        mAnimator.start();
    }

    private void collapse() {
        int finalHeight = layTop.getHeight();
        upDown.setImageResource((R.drawable.collapse_arrow));
        ValueAnimator mAnimator = slideAnimator(finalHeight, 0);
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                layTop.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = layTop.getLayoutParams();
                layoutParams.height = value;
                layTop.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    String subId = "", Id = "";

    private void getData(String id) {
        data.clear();
        type = 1;
        String url = Config.SERVER_URL + Config.DELIVERY_HOME;
        //if(!id.equals(""))
        //  url = url + "?delivery_type_id=" + id + "&delivery_status_type=" + subId;
        Log.d(Config.TAG, url);
        HashMap<String, String> params = new HashMap<>();
        params.put("driver_lat", "" + latitude);
        params.put("driver_lng", "" + longitude);
        params.put("delivery_type_id", "" + id);
        params.put("delivery_status_type", "" + subId);
        Log.d(Config.TAG, params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void getData(String id, String subId) {
        data.clear();
        type = 1;
        String url = Config.SERVER_URL + Config.DELIVERY_HOME;
        //if(!id.equals(""))
        //  url = url + "?delivery_type_id=" + id + "&delivery_status_type=" + subId;
        Log.d(Config.TAG, url);
        HashMap<String, String> params = new HashMap<>();
        params.put("driver_lat", "" + latitude);
        params.put("driver_lng", "" + longitude);
        params.put("delivery_type_id", "" + id);
        params.put("delivery_status_type", "" + subId);
        Log.d(Config.TAG, params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }


    int type;

    private void setAvailability() {
        type = 2;
        String url = Config.SERVER_URL + Config.AVAILABILITY_CHANGE;
        Log.d(Config.TAG, url);
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    /*private void setAvailability(String availability, String typeid) {
        type = 2;
        String url = Config.SERVER_URL + Config.AVAILABILITY_CHANGE;
        HashMap<String, String> params = new HashMap<>();
        params.put("availability_status", availability);
        params.put("vehicle_type_id", "" + typeid);
        Log.d(Config.TAG, url);
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }*/

    private void getAvailability() {
        type = 3;
        String url = Config.SERVER_URL + Config.AVAILABILITY;
        Log.d(Config.TAG, url);
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void updateUI(int type) {
        if (type == 1) {
            listAll.setBackgroundResource(R.drawable.border_back);
            listAll.setTextColor(Color.WHITE);
            listFreight.setBackgroundResource(R.drawable.border_black);
            listFreight.setTextColor(Color.BLACK);
            listAppliance.setBackgroundResource(R.drawable.border_black);
            listAppliance.setTextColor(Color.BLACK);
            listVehicle.setBackgroundResource(R.drawable.border_black);
            listVehicle.setTextColor(Color.BLACK);
            listLivestock.setBackgroundResource(R.drawable.border_black);
            listLivestock.setTextColor(Color.BLACK);
        } else if (type == 2) {
            listAll.setBackgroundResource(R.drawable.border_black);
            listAll.setTextColor(Color.BLACK);
            listFreight.setBackgroundResource(R.drawable.border_back);
            listFreight.setTextColor(Color.WHITE);
            listAppliance.setBackgroundResource(R.drawable.border_black);
            listAppliance.setTextColor(Color.BLACK);
            listVehicle.setBackgroundResource(R.drawable.border_black);
            listVehicle.setTextColor(Color.BLACK);
            listLivestock.setBackgroundResource(R.drawable.border_black);
            listLivestock.setTextColor(Color.BLACK);
        } else if (type == 3) {
            listAll.setBackgroundResource(R.drawable.border_black);
            listAll.setTextColor(Color.BLACK);
            listFreight.setBackgroundResource(R.drawable.border_black);
            listFreight.setTextColor(Color.BLACK);
            listAppliance.setBackgroundResource(R.drawable.border_back);
            listAppliance.setTextColor(Color.WHITE);
            listVehicle.setBackgroundResource(R.drawable.border_black);
            listVehicle.setTextColor(Color.BLACK);
            listLivestock.setBackgroundResource(R.drawable.border_black);
            listLivestock.setTextColor(Color.BLACK);
        } else if (type == 4) {
            listAll.setBackgroundResource(R.drawable.border_black);
            listAll.setTextColor(Color.BLACK);
            listFreight.setBackgroundResource(R.drawable.border_black);
            listFreight.setTextColor(Color.BLACK);
            listAppliance.setBackgroundResource(R.drawable.border_black);
            listAppliance.setTextColor(Color.BLACK);
            listVehicle.setBackgroundResource(R.drawable.border_back);
            listVehicle.setTextColor(Color.WHITE);
            listLivestock.setBackgroundResource(R.drawable.border_black);
            listLivestock.setTextColor(Color.BLACK);
        } else if (type == 5) {
            listAll.setBackgroundResource(R.drawable.border_black);
            listAll.setTextColor(Color.BLACK);
            listFreight.setBackgroundResource(R.drawable.border_black);
            listFreight.setTextColor(Color.BLACK);
            listAppliance.setBackgroundResource(R.drawable.border_black);
            listAppliance.setTextColor(Color.BLACK);
            listVehicle.setBackgroundResource(R.drawable.border_black);
            listVehicle.setTextColor(Color.BLACK);
            listLivestock.setBackgroundResource(R.drawable.border_back);
            listLivestock.setTextColor(Color.WHITE);
        }
    }

    private void updateSubUI(int type) {
        if (type == 1) {
            all.setBackgroundResource(R.drawable.border_back);
            all.setTextColor(Color.WHITE);
            takeNow.setBackgroundResource(R.drawable.border_black);
            takeNow.setTextColor(Color.BLACK);
            active.setBackgroundResource(R.drawable.border_black);
            active.setTextColor(Color.BLACK);
            future.setBackgroundResource(R.drawable.border_black);
            future.setTextColor(Color.BLACK);
        } else if (type == 2) {
            all.setBackgroundResource(R.drawable.border_black);
            all.setTextColor(Color.BLACK);
            takeNow.setBackgroundResource(R.drawable.border_back);
            takeNow.setTextColor(Color.WHITE);
            active.setBackgroundResource(R.drawable.border_black);
            active.setTextColor(Color.BLACK);
            future.setBackgroundResource(R.drawable.border_black);
            future.setTextColor(Color.BLACK);
        } else if (type == 3) {
            all.setBackgroundResource(R.drawable.border_black);
            all.setTextColor(Color.BLACK);
            takeNow.setBackgroundResource(R.drawable.border_black);
            takeNow.setTextColor(Color.BLACK);
            active.setBackgroundResource(R.drawable.border_back);
            active.setTextColor(Color.WHITE);
            future.setBackgroundResource(R.drawable.border_black);
            future.setTextColor(Color.BLACK);
        } else if (type == 4) {
            all.setBackgroundResource(R.drawable.border_black);
            all.setTextColor(Color.BLACK);
            takeNow.setBackgroundResource(R.drawable.border_black);
            takeNow.setTextColor(Color.BLACK);
            active.setBackgroundResource(R.drawable.border_black);
            active.setTextColor(Color.BLACK);
            future.setBackgroundResource(R.drawable.border_back);
            future.setTextColor(Color.WHITE);
        }
    }

    private void addMarkersOnMap() {
        try {
//            if (googleMap != null)
            googleMap.clear();
            addCurrentLocationMarker();
            for (int i = 1; i <= data.size(); i++) {
                HomeData homeData = data.get(i - 1);
                JSONObject markerData = new JSONObject();
                String address="";

                if ( homeData.getPickUpState() != null && !homeData.getPickUpState().equals("null") && !homeData.getPickUpState().equals("")){
                    if (homeData.getPick_up_city()!= null && !homeData.getPick_up_city().equals("")) {
                        address =homeData.getPick_up_city() +" , "+homeData.getPickUpState();
                    }
                    else{
                        address = homeData.getPickUpState();
                    }
                }
                else if ( homeData.getPickupAddress() != null) {
                    String ar[]=homeData.getPickupAddress().split(",");

                    if (ar.length >= 3) {
                        address =ar[ar.length-3]+" , "+ ar[ar.length-2];
                    }
                    else if(ar.length ==2){
                        address =ar[ar.length-2];
                    }
                    else{
                        address = homeData.getPickupAddress();
                    }
                }

                try {
                    markerData.put("id", homeData.getId());
                    markerData.put("title", homeData.getItemDeliveryTitle());
                    markerData.put("address", address);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                double lat = Double.parseDouble(homeData.getPickupLatitude());
                double lng = Double.parseDouble(homeData.getPickupLongitude());

          /*  LatLng location = new LatLng(Double.valueOf(homeData.getPickupLatitude()),
                    Double.valueOf(homeData.getPickupLongitude()));*/
                LatLng location = new LatLng((lat + i * COORDINATE_OFFSET),
                        (lng + i * COORDINATE_OFFSET));
                MarkerOptions marker = new MarkerOptions().position(location).
                        title(markerData.toString());
                if (homeData.getDeliveryStatusType().equals("1"))
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.purple_pointer));
                else if (homeData.getDeliveryStatusType().equals("2"))
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_pointer));
                else if (homeData.getDeliveryStatusType().equals("3"))
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_pointer));
                else
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_pointer));
                googleMap.addMarker(marker);
            }
        } catch (Exception e) {
            e.toString();
        }
    }

    public void addCurrentLocationMarker() {
        if (latitude != 0.0 && longitude != 0.0) {
            JSONObject markerData = new JSONObject();
            try {
                markerData.put("id", "null");
                markerData.put("title", "i am here");
                markerData.put("address", "lat" + String.valueOf(latitude) + " lon" + String.valueOf(longitude) + "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            LatLng location = new LatLng(latitude, longitude);
            MarkerOptions markerOptions = new MarkerOptions().position(location).title(markerData.toString());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_pointer)).flat(true);
            mCurrentMarker = googleMap.addMarker(markerOptions);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        settingsrequest();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("onConnectionFailed", "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            if (mFirstUpdate) {
                mFirstUpdate = false;
                getData("");
                animateCamera();
            }
            if (session.getAvailability().equals("1")) {
                try {
                    if (!mFirstUpdate) {
                        //  animateCamera();
                        if (mCurrentMarker != null) {
                            animateMarker(location, mCurrentMarker);
                        }
                    }
                } catch (Exception e) {
                    e.toString();
                }
            }
            Log.v("", "");

        } catch (Exception e) {
            e.toString();
        }
    }


    //Location Marker

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

    protected void stopLocationUpdates() {
        try {
            IsLocationUpdate = false;
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } catch (Exception e) {
            e.toString();
        }
        Log.d(TAG, "Location update stopped .......................");
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {

        LocationUpdates();
        /*PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");*/
    }

    /*  public void LocationUpdates() {
          if (Build.VERSION.SDK_INT >= 23) {
              if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                      != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                      != PackageManager.PERMISSION_GRANTED) {
                  ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                          android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
              } else if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                      != PackageManager.PERMISSION_GRANTED) {
                  ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
              } else if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                      != PackageManager.PERMISSION_GRANTED) {
                  ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
              } else {
                  PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                          mGoogleApiClient, mLocationRequest, this);
                  Log.d(TAG, "Location update started ..............: ");
              }
          } else {
              PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                      mGoogleApiClient, mLocationRequest, this);
              Log.d(TAG, "Location update started ..............: ");
          }
      }*/
  /*  public void LocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity(), permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissionsRequired[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissionsRequired[1])
                        ) {
                    //Show Information about why you need the permission

                } else if (new SessionManager(getActivity()).getLocation()) {
                    //Previously Permission Request was cancelled with 'Dont Ask Again',
                    // Redirect to Settings after showing Information about why you need the permission

                } else {
                    //just request the permission

                }
            } else {
                //You already have the permission, just go ahead.
                //proceedAfterPermission();
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    IsLocationUpdate = true;
                    PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                            mGoogleApiClient, mLocationRequest, this);
                    Log.d(TAG, "Location update started ..............: ");
                }
            }
        } else {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                IsLocationUpdate = true;
                PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
                Log.d(TAG, "Location update started ..............: ");
            }
        }
    }*/
    public void LocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity(), permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissionsRequired[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissionsRequired[1])
                        ) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getActivity().getResources().getString(R.string.accesslocation));
                    builder.setMessage(getActivity().getResources().getString(R.string.needpermision));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(getActivity(), permissionsRequired, LOCATION_PERMISSION_CODE);
                        }
                    });
                    builder.setNegativeButton("Don’t Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else if (new SessionManager(getActivity()).getLocation()) {
                    //Previously Permission Request was cancelled with 'Dont Ask Again',
                    // Redirect to Settings after showing Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getActivity().getResources().getString(R.string.accesslocation));
                    builder.setMessage(getActivity().getResources().getString(R.string.needpermision));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            // sentToSettings = true;

                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                            intent.setData(uri);
                            getActivity().startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.granytlocationpermission), Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNegativeButton("Don’t Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    //just request the permission
                    ActivityCompat.requestPermissions(getActivity(), permissionsRequired, LOCATION_PERMISSION_CODE);
                }
                new SessionManager(getActivity()).setLocation(true);

            } else {
                //You already have the permission, just go ahead.
                //proceedAfterPermission();
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    IsLocationUpdate = true;
                    PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                            mGoogleApiClient, mLocationRequest, this);
                    Log.d(TAG, "Location update started ..............: ");
                }
            }
        } else {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                IsLocationUpdate = true;
                PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
                Log.d(TAG, "Location update started ..............: ");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
       /* if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationUpdates();
            }*/
        if (requestCode == LOCATION_PERMISSION_CODE) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                LocationUpdates();
            }
        }
    }
}
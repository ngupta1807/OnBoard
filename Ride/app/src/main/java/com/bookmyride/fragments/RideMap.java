package com.bookmyride.fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.bookmyride.R;
import com.bookmyride.activities.BookingTimer;
import com.bookmyride.activities.EstimateRide;
import com.bookmyride.activities.FareBreakup;
import com.bookmyride.activities.GuaranteeDriverInfo;
import com.bookmyride.activities.MyCard;
import com.bookmyride.activities.PassengerSignup;
import com.bookmyride.activities.RideDetail;
import com.bookmyride.activities.SearchOnMap;
import com.bookmyride.activities.SignIn;
import com.bookmyride.activities.TrackDetail;
import com.bookmyride.adapters.BookMyRideAdapter;
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
import com.bookmyride.models.Cars;
import com.bookmyride.models.Drivers;

import com.bookmyride.services.RouteService;
import com.bookmyride.util.LatLngInterpolator;
import com.bookmyride.views.HorizontalListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RideMap extends Fragment implements View.OnClickListener, AsyncTaskCompleteListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private MapStyleOptions mapStyle;
    MapView mMapView;
    private GoogleMap googleMap;
    //TrackGPS gps;
    //double latitude, longitude;
    SessionHandler session;
    LinearLayout layPickup, layDropoff;
    TextView pickupLocation, dropOffLocation, estimateRide, requestRide;
    HorizontalListView listView;
    BookMyRideAdapter adapter;
    CardView layGuarantee;
    RelativeLayout tryNow, cancel;
    ImageView iconGPS;
    String categoryId = "1";
    ArrayList<Cars> carData = new ArrayList<>();
    ArrayList<Drivers> availableDrivers = new ArrayList<>();
    ImageView doAddDelete, puAddDelete;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGoogleAPIClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null);
        init(view);
        getActivity().startService(new Intent(getActivity(), RouteService.class));
        /*if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }*/
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        //getAvailableDrivers();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMap(view, savedInstanceState);
        //checkServiceAvailable();
        getProfile();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
        if (mGoogleApiClient != null)
            if (!mGoogleApiClient.isConnected())
                mGoogleApiClient.connect();
        layGuarantee.setVisibility(View.GONE);
    }

    public void updateAddDeleteUI() {
        if (pickupLocation.getText().toString().trim().equals(""))
            puAddDelete.setImageResource(R.drawable.pickup_add);
        else
            puAddDelete.setImageResource(R.drawable.dropoff_close);
        if (dropOffLocation.getText().toString().trim().equals(""))
            doAddDelete.setImageResource(R.drawable.pickup_add);
        else
            doAddDelete.setImageResource(R.drawable.dropoff_close);
    }

    int type = 3;
    CameraUpdate cameraUpdate;
    String event = "";

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pu_delete:
                if (pickupLocation.getText().toString().trim().equals("")) {
                    type = 0;
                    startActivityForResult(new Intent(getActivity(), SearchOnMap.class), placeRequestCode);
                    getActivity().overridePendingTransition(R.anim.slideup, R.anim.slidedown);
                } else {
                    pickupLocation.setText("");
                }
                updateAddDeleteUI();
                break;
            case R.id.do_delete:
                if (dropOffLocation.getText().toString().trim().equals("")) {
                    type = 1;
                    startActivityForResult(new Intent(getActivity(), SearchOnMap.class), placeRequestCode);
                    getActivity().overridePendingTransition(R.anim.slideup, R.anim.slidedown);
                } else {
                    dropOffLocation.setText("");
                }
                updateAddDeleteUI();
                break;
            case R.id.lay_try_now:
                showGuaranteeDriver();
                //bookRide("0");
                break;
            case R.id.lay_cancel:
                layGuarantee.setVisibility(View.GONE);
                break;
            case R.id.icon_gps:
                //LatLng latLang = new LatLng(gps.getLatitude(), gps.getLongitude());
                LatLng latLang = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLang, 15f);
                googleMap.animateCamera(cameraUpdate);
                break;
            case R.id.request_ride:
                if (pickupLocation.getText().toString().trim().equals("")) {
                    Alert("Alert!", "Please enter pick up location.");
                } else if (dropOffLocation.getText().toString().trim().equals("")) {
                    Alert("Alert!", "Please enter drop off location.");
                } else requestDialog();
                break;
            case R.id.lay_drop:
                type = 1;
                startActivityForResult(new Intent(getActivity(), SearchOnMap.class), placeRequestCode);
                getActivity().overridePendingTransition(R.anim.slideup, R.anim.slidedown);
                break;
            case R.id.lay_pickup:
                type = 0;
                startActivityForResult(new Intent(getActivity(), SearchOnMap.class), placeRequestCode);
                getActivity().overridePendingTransition(R.anim.slideup, R.anim.slidedown);
                break;
            case R.id.estimate:
                event = "estimate";
                if (pickupLocation.getText().toString().trim().equals("")) {
                    Alert("Alert!", "Please enter pick up location.");
                } else if (dropOffLocation.getText().toString().trim().equals("")) {
                    Alert("Alert!", "Please enter drop off location.");
                } else getDistance();
                break;
        }
    }

    private int placeRequestCode = 200;
    private int SAVE_CARD = 51;
    private int UPDATE_CARD = 52;

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private void init(View view) {
        updatable = true;
        session = new SessionHandler(getActivity());
        mapStyle = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.mapstyle);
        //gps = new TrackGPS(getActivity());
        iconGPS = (ImageView) view.findViewById(R.id.icon_gps);
        iconGPS.setOnClickListener(this);
        tryNow = (RelativeLayout) view.findViewById(R.id.lay_try_now);
        tryNow.setOnClickListener(this);
        cancel = (RelativeLayout) view.findViewById(R.id.lay_cancel);
        cancel.setOnClickListener(this);
        layGuarantee = (CardView) view.findViewById(R.id.lay_guarantee);
        pickupLocation = (TextView) view.findViewById(R.id.pickup);
        dropOffLocation = (TextView) view.findViewById(R.id.dropoff);
        puAddDelete = (ImageView) view.findViewById(R.id.pu_delete);
        puAddDelete.setOnClickListener(this);
        doAddDelete = (ImageView) view.findViewById(R.id.do_delete);
        doAddDelete.setOnClickListener(this);
        layPickup = (LinearLayout) view.findViewById(R.id.lay_pickup);
        layPickup.setOnClickListener(this);
        layDropoff = (LinearLayout) view.findViewById(R.id.lay_drop);
        layDropoff.setOnClickListener(this);
        estimateRide = (TextView) view.findViewById(R.id.estimate);
        estimateRide.setOnClickListener(this);
        requestRide = (TextView) view.findViewById(R.id.request_ride);
        requestRide.setOnClickListener(this);
        listView = (HorizontalListView) view.findViewById(R.id.cat_list);
        adapter = new BookMyRideAdapter(getActivity(), carData);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                String id = carData.get(pos).getId();
                categoryId = id;
                int selectedIndex = getIndex(id);
                Cars car = new Cars();
                car.setDefault(true);
                car.setIcon(carData.get(pos).getIcon());
                car.setId(carData.get(pos).getId());
                car.setSelectedIcon(carData.get(pos).getSelectedIcon());
                car.setName(carData.get(pos).getName());
                carData.set(selectedIndex, car);
                adapter.notifyDataSetChanged();
                isUIEvent = true;
                getAvailableDrivers();
            }
        });

    }

    private void getAddress(String latitude, String longitude) {
        String endPoint = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" +
                latitude + "," + longitude + "&key=" + Config.PLACE_API;
        //mLastLocation = newLoc;
        ServiceHandlerInBack mHandler = new ServiceHandlerInBack(getActivity(), HTTPMethods.GET, new AsyncTaskCompleteListener() {
            @Override
            public void onTaskComplete(String result) {
                try {
                    JSONObject outerJson = new JSONObject(result);
                    if (outerJson.get(Key.STATUS).toString().equalsIgnoreCase("ok")) {
                        JSONArray resultArray = outerJson.getJSONArray("results");
                        JSONObject addressComponent = resultArray.getJSONObject(0);
                        String address = addressComponent.getString("formatted_address");
                        puLocation = address;
                        pickupLocation.setText(puLocation.replace("null", ""));
                        updateAddDeleteUI();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        mHandler.execute(endPoint, "");
    }

    boolean isUIEvent = false;
    boolean isBooking = false;

    private int getIndex(String id) {
        int selectedIndex = -1;
        for (int i = 0; i < carData.size(); i++) {
            if (carData.get(i).getId().equals(id))
                selectedIndex = i;
            else {
                Cars car = new Cars();
                car.setDefault(false);
                car.setIcon(carData.get(i).getIcon());
                car.setId(carData.get(i).getId());
                car.setSelectedIcon(carData.get(i).getSelectedIcon());
                car.setName(carData.get(i).getName());
                carData.set(i, car);
            }
        }
        return selectedIndex;
    }

    private void getDistance() {
        type = 5;
        if (Internet.hasInternet(getActivity())) {
            String source = "" + puLat + "," + puLng;
            String destination = "&destination=" + doLat + "," + doLng;
            String endPoint = Config.DISTANCE_BY_GOOGLE + source + destination;
            APIHandlerInBack apiHandler = new APIHandlerInBack(getActivity(), HTTPMethods.GET, this, null);
            apiHandler.execute(Config.DISTANCE_BY_GOOGLE + source + destination, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void checkServiceAvailable() {
        type = 3;
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET, this, null);
            apiHandler.execute(Config.SERVICE_AVAILABLES, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void getProfile() {
        type = 10;
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET, this, null);
            apiHandler.execute(Config.GET_PROFILE + session.getUserID() + "?expand=profile,booking", session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    APIHandler apiHandler;
    ServiceHandlerInBack apiHandlerBack;

    private void getAvailableDrivers() {
        if (isUIEvent) {
            if (apiHandler != null) {
                if (apiHandler.getStatus() == AsyncTask.Status.RUNNING) {
                    apiHandler.cancel(true);
                    // My AsyncTask is currently doing work in doInBackground()
                }
            }
            if (apiHandlerBack != null) {
                if (apiHandlerBack.getStatus() == AsyncTask.Status.RUNNING) {
                    apiHandlerBack.cancel(true);
                    // My AsyncTask is currently doing work in doInBackground()
                }
            }
        }
        type = 4;
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("lat", "" + mLastLocation.getLatitude());
            params.put("lng", "" + mLastLocation.getLongitude());
            params.put("driverCategory", "" + categoryId);
            if (Internet.hasInternet(getActivity())) {
                if (isUIEvent) {
                    apiHandler = new APIHandler(getActivity(), HTTPMethods.POST, this, params);
                    apiHandler.execute(Config.SERVICE_AVAILABLES, session.getToken());
                } else {
                    apiHandlerBack = new ServiceHandlerInBack(getActivity(), HTTPMethods.POST, this, params);
                    apiHandlerBack.execute(Config.SERVICE_AVAILABLES, session.getToken());
                }
            } else
                Alert("Alert!", getResources().getString(R.string.no_internet));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void setCurrentLocation() {
        if (mLastLocation != null) {
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.locator_pin))
                    .anchor(0.5f, 0.5f).flat(true);
            if (googleMap != null) {
                //googleMap.clear();
                googleMap.addMarker(markerOptions);
            }
        }
    }

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
                googleMap.setMyLocationEnabled(false);
                //googleMap.setMapStyle(mapStyle);

                //latitude = gps.getLatitude();
                //longitude = gps.getLongitude();
                setCurrentLocation();
                /*CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latitude, longitude))      // Sets the center of the map to location user
                        .zoom(13f)             // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                puLat = "" + latitude;
                puLng = "" + longitude;
                getAddress("" + latitude, "" + longitude);   */
            }
        });
    }

    @Override
    public void onResume() {
        driverHandler.postDelayed(updateDriver, 10000);
        super.onResume();
        mMapView.onResume();
        /*LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver,
                new IntentFilter(NotificationFilters.LOCATION_CHANGED));*/
    }

    @Override
    public void onPause() {
        super.onPause();
        driverHandler.removeCallbacks(updateDriver);
        mMapView.onPause();
        //LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        driverHandler.removeCallbacks(updateDriver);
        mMapView.onDestroy();
        //LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    Handler driverHandler = new Handler();
    Runnable updateDriver = new Runnable() {
        @Override
        public void run() {
            isUIEvent = false;
            getAvailableDrivers();
            driverHandler.postDelayed(this, 10000);
        }
    };

    int bookingType = 2;
    /*private void requestDialog(){
            final RideDialog mDialog = new RideDialog(getActivity(), true, true);
            mDialog.setDialogTitle("Ride24:7");
            //mDialog.showCross(true);
            mDialog.setDialogMessage("Do you want to request now or later?");
            mDialog.setPositiveButton(getResources().getString(R.string.book_now), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    bookingType = 0;
                    bookRide("0");
                }
            });
        mDialog.setNegativeButton(getResources().getString(R.string.book_later), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                bookingType = 1;
                //showDateTimePicker();
                simpleClicked();
            }
        });
            mDialog.show();
    }*/

    Dialog mDialog;
    String strNote = "";

    public void requestDialog() {
        mDialog = new Dialog(getActivity(), R.style.rideDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_note);
        mDialog.setCanceledOnTouchOutside(false);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        TextView message = (TextView) mDialog.findViewById(R.id.msg);
        final TextView note = (TextView) mDialog.findViewById(R.id.note);
        TextView now = (TextView) mDialog.findViewById(R.id.now);
        TextView later = (TextView) mDialog.findViewById(R.id.later);
        ImageView cross = (ImageView) mDialog.findViewById(R.id.cross);
        title.setText("BookMyRide");
        message.setText("Do you want to request now or later?");
        now.setText("Now");
        later.setText("Later");

        now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mDialog.dismiss();
                event = "";
                strNote = note.getText().toString();
                bookingType = 0;
                if (session.isCardExist()) {
                    mDialog.dismiss();
                    getDistance();
                    //bookRide("" + bookingType, "", "");
                } else
                    isCardSaved();
            }
        });

        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                event = "";
                strNote = note.getText().toString();
                bookingType = 1;
                simpleClicked();
            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

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

        mDialog.show();
    }

    private void isCardSaved() {
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    try {
                        JSONObject outerJson = new JSONObject(result);
                        if (outerJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                            if (mDialog != null && mDialog.isShowing())
                                mDialog.dismiss();
                            session.saveCardExist(true);
                            getDistance();
                            //bookRide("" + bookingType, "", "");
                        } else if (outerJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                            session.saveCardExist(false);
                            showRideDialog("Alert!", outerJson.getString(Key.MESSAGE));
                        } else {
                            session.saveCardExist(false);
                            Alert("Alert!", outerJson.getString(Key.MESSAGE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
            apiHandler.execute(Config.CHECK_AVAILABLE_CARD + session.getUserID(), session.getToken());
        } else
            Alert("Alert!", getActivity().getResources().getString(R.string.no_internet));
    }

    private void showRideDialog(String title, String message) {
        final com.bookmyride.views.AlertDialog dialog = new com.bookmyride.views.AlertDialog(getActivity(), false);
        dialog.setDialogTitle(title);
        dialog.setDialogMessage(message);
        dialog.setCancelOnTouchOutside(false);
        dialog.setPositiveButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mDialog != null && mDialog.isShowing())
                    mDialog.dismiss();
            }
        });
        dialog.setNegativeButton("Save Card", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivityForResult(new Intent(getActivity(), MyCard.class)
                        .putExtra("isBack", ""), SAVE_CARD);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        dialog.show();
    }

    private void showCardExpiryDialog(String title, String message) {
        final com.bookmyride.views.AlertDialog dialog = new com.bookmyride.views.AlertDialog(getActivity(), false);
        dialog.setDialogTitle(title);
        dialog.setDialogMessage(message);
        dialog.setCancelOnTouchOutside(false);
        dialog.setPositiveButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mDialog != null && mDialog.isShowing())
                    mDialog.dismiss();
            }
        });
        dialog.setNegativeButton("Update Card", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivityForResult(new Intent(getActivity(), MyCard.class)
                        .putExtra("isBack", ""), UPDATE_CARD);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        dialog.show();
    }

    com.bookmyride.views.AlertDialog noEmailDialog;

    private void noEmailFound(String title, String message) {
        noEmailDialog = new com.bookmyride.views.AlertDialog(getActivity(), false);
        noEmailDialog.setDialogTitle(title);
        noEmailDialog.setDialogMessage(message);
        noEmailDialog.setCancelOnTouchOutside(false);
        noEmailDialog.setPositiveButton("Update Profile", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //noEmailDialog.dismiss();
                startActivityForResult(new Intent(getActivity(), PassengerSignup.class)
                        .putExtra("profile", "")
                        .putExtra("isBack", ""), 1001);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        noEmailDialog.show();
    }

    String bookingDataTime = "";

    private void bookRide(String type, String distance, String duration) {
        HashMap<String, String> requestParam = new HashMap<>();
        JSONObject pickup = new JSONObject();
        try {
            pickup.put("address", puLocation);
            pickup.put("lat", puLat);
            pickup.put("lng", puLng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (bookingType == 0)
            requestParam.put("pickUpDate", getCurrentDateTime());
        else
            requestParam.put("pickUpDate", bookingDataTime);
        requestParam.put("pickUp", pickup.toString());
        JSONObject dropoff = new JSONObject();
        try {
            dropoff.put("address", doLocation);
            dropoff.put("lat", doLat);
            dropoff.put("lng", doLng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!doLocation.equals("")) {
            requestParam.put("dropOff", dropoff.toString());
            requestParam.put("distance", distance);
            requestParam.put("duration", duration);
        }
        requestParam.put("type", type);
        requestParam.put("driverCategory_id", categoryId);
        if (layGuarantee.getVisibility() == View.VISIBLE)
            requestParam.put("is_guarantee", "1");
        else
            requestParam.put("is_guarantee", "0");

        requestParam.put("note", strNote);

        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.POST, this, requestParam);
            apiHandler.execute(Config.BOOKING_REQUEST, session.getToken());
        } else
            Alert("Alert!", getActivity().getResources().getString(R.string.no_internet));
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentDateAndTime = sdf.format(new Date());
        return currentDateAndTime;
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void Alert(String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(getActivity(), true);
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

    private void paymentPendingPrompt(final String rideID, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(getActivity(), true);
        mDialog.setDialogTitle("Alert!");
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        startActivity(new Intent(getActivity(), RideDetail.class)
                                .putExtra("booking_id", rideID));
                    }
                });
        mDialog.show();
    }

    private void handleResponse(String result) {
        Log.e("map_res", result);
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.get(Key.STATUS).toString().equalsIgnoreCase("ok")) {
                JSONObject distanceObj = outJson.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
                String distance = distanceObj.getJSONObject("distance").getString("value");
                String duration = distanceObj.getJSONObject("duration").getString("value");
                if (!event.equals("estimate")) {
                    bookRide("" + bookingType, distance, duration);
                } else {
                    Intent intent = new Intent(getActivity(), EstimateRide.class);
                    intent.putExtra("pickup", puLocation);
                    intent.putExtra("dropoff", doLocation);
                    intent.putExtra("distance", distance);
                    intent.putExtra("duration", duration);
                    intent.putExtra("puLat", "" + puLat);
                    intent.putExtra("puLng", "" + puLng);
                    intent.putExtra("doLat", "" + doLat);
                    intent.putExtra("doLng", "" + doLng);
                    intent.putExtra("category", "" + categoryId);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                layGuarantee.setVisibility(View.GONE);
            } else if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                if (type == 10) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkServiceAvailable();
                        }
                    }, 500);
                    JSONObject dataObj = outJson.getJSONObject(Key.DATA);
                    session.saveReferralCode(dataObj.getString("referralCode"));
                    if (dataObj.getString("email").equalsIgnoreCase("") ||
                            dataObj.getString("email").equalsIgnoreCase("null")) {
                        noEmailFound("Alert!", "Please update your profile.");
                    } else {
                        if (dataObj.has("profileData")) {
                            if (dataObj.get("profileData") instanceof JSONObject) {
                                JSONObject profileData = dataObj.getJSONObject("profileData");
                                String address = profileData.getString("address");
                                session.saveCardAddress(address);
                            } else session.saveCardAddress("");
                        } else session.saveCardAddress("");
                        if (dataObj.has("booking")) {
                            if (!dataObj.get("booking").toString().equals("null")) {
                                JSONObject bookingObj = dataObj.getJSONObject("booking");
                                onRideAlert(bookingObj.toString(), "One of your ride is in progress. Please click on track to track the ride.");
                            }
                        }
                        if (dataObj.has("cardExpire")) {
                            boolean isCardExpire = dataObj.getBoolean("cardExpire");
                            if (isCardExpire) {
                                showCardExpiryDialog("Alert!", dataObj.getString("cardExpireMessage"));
                            }
                        }
                    }
                } else if (type == 3) {
                    if (googleMap != null)
                        googleMap.clear();
                    availableDrivers.clear();
                    driverHandler.postDelayed(updateDriver, 200);
                    carData.clear();
                    JSONObject dataObj = outJson.getJSONObject(Key.DATA);
                    JSONArray itemArray = dataObj.getJSONArray(Key.ITEMS);
                    for (int i = 0; i < itemArray.length(); i++) {
                        JSONObject itemObj = itemArray.getJSONObject(i);
                        Cars cars = new Cars();
                        cars.setId(itemObj.get(Key.ID).toString());
                        cars.setName(itemObj.getString(Key.NAME));
                        cars.setIcon(itemObj.getString(Key.ICON));
                        cars.setSelectedIcon(itemObj.getString(Key.SELECTED_ICON));
                        cars.setDefault(itemObj.get(Key.IS_DEFAULT).toString().equals("1") ? true : false);
                        carData.add(cars);
                    }
                    adapter.notifyDataSetChanged();
                    layGuarantee.setVisibility(View.GONE);
                } else if (type == 4) {
                    //googleMap.clear();
                    //if(isUIEvent)
                    availableDrivers.clear();
                    //JSONObject dataObj = outJson.getJSONObject(Key.DATA);
                    JSONArray itemArray;
                    if (outJson.get(Key.DATA) instanceof JSONArray) {
                        itemArray = outJson.getJSONArray(Key.DATA);
                    } else {
                        itemArray = outJson.getJSONObject(Key.DATA).getJSONArray(Key.ITEMS);
                    }
                    for (int i = 0; i < itemArray.length(); i++) {
                        JSONObject itemObj = itemArray.getJSONObject(i);
                        Drivers driver = new Drivers();
                        driver.setId(itemObj.get(Key.ID).toString());
                        driver.setFullName(itemObj.getString("fullName"));
                        JSONObject locationObj = new JSONObject(itemObj.getString("currentLocation"));
                        driver.setCurrentLat(locationObj.getString("latitude"));
                        driver.setCurrentLng(locationObj.getString("longitude"));
                        driver.setPrevLat(locationObj.has("pre_latitude") ? locationObj.getString("pre_latitude") : "0.0");
                        driver.setPrevLng(locationObj.has("pre_longitude") ? locationObj.getString("pre_longitude") : "0.0");
                        driver.setUserType(itemObj.getString(Key.USER_TYPE));
                        if (isUIEvent) {
                            googleMap.clear();
                            visibleMarkers.clear();
                        }
                        availableDrivers.add(driver);
                    }
                    layGuarantee.setVisibility(View.GONE);
                    addMarkersOnMap();
                }
            } else if (outJson.getInt(Key.STATUS) == APIStatus.CREATED) {
                //if (bookingType == 0) {
                JSONArray dataArray = outJson.getJSONArray(Key.DATA);
                JSONObject obj = dataArray.getJSONObject(0);
                String userId = obj.get(Key.USER_ID).toString();
                String rideId = obj.get(Key.ID).toString();
                showBookingTimer(userId, rideId);
                /*} else {
                    Alert("Alert!", outJson.getString(Key.MESSAGE));
                }*/
                layGuarantee.setVisibility(View.GONE);
            } else if (outJson.getInt(Key.STATUS) == APIStatus.GUARANTEE_AVAILABLE) {
                googleMap.clear();
                setCurrentLocation();
                layGuarantee.setVisibility(View.VISIBLE);
            } else if (outJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                layGuarantee.setVisibility(View.GONE);
                JSONArray jsonArray = outJson.getJSONArray(Key.DATA);
                JSONObject innerObj = jsonArray.getJSONObject(0);
                String field = innerObj.getString("field");
                String message = innerObj.getString("message");
                //Alert("Alert!", outJson.getJSONArray(Key.DATA).getJSONObject(0).getString(Key.MESSAGE));
                if (field.equalsIgnoreCase("payment")) {
                    String rideId = innerObj.getString("rideId");
                    paymentPendingPrompt(rideId, message);
                } else
                    Alert("Alert!", message);
            } else if (outJson.getInt(Key.STATUS) == APIStatus.ACCEPTED) {
                googleMap.clear();
                setCurrentLocation();
                layGuarantee.setVisibility(View.GONE);
                if (isUIEvent || bookingType == 0 || bookingType == 1) {
                    isUIEvent = false;
                    bookingType = 2;
                    Alert("Alert!", outJson.getString(Key.MESSAGE));
                }
            } else if (outJson.getInt(Key.STATUS) == APIStatus.SESSION_EXPIRE) {
                layGuarantee.setVisibility(View.GONE);
                driverHandler.removeCallbacks(updateDriver);
                sessionExpired(outJson.getString(Key.MESSAGE));
            } else {
                layGuarantee.setVisibility(View.GONE);
                Alert("Alert!", outJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            layGuarantee.setVisibility(View.GONE);
        }
    }

    String oldLat = "", oldLng = "";

    HashMap<String, Marker> visibleMarkers = new HashMap<String, Marker>();

    private void addMarkersOnMap() {
        //googleMap.clear();
        setCurrentLocation();
        //googleMap.clear();
        Log.e("vis:", "" + availableDrivers.size());
        for (int i = 0; i < availableDrivers.size(); i++) {
            if (visibleMarkers.containsKey(availableDrivers.get(i).getId())) {
                Log.e("id:", "" + availableDrivers.get(i).getId());
                if (!availableDrivers.get(i).getPrevLat().equals(oldLat) &&
                        !availableDrivers.get(i).getPrevLng().equals(oldLng)) {
                    oldLat = availableDrivers.get(i).getPrevLat();
                    oldLng = availableDrivers.get(i).getPrevLng();
                    Location prevLoc = new Location("previous");
                    prevLoc.setLatitude(Double.parseDouble(availableDrivers.get(i).getPrevLat()));
                    prevLoc.setLongitude(Double.parseDouble(availableDrivers.get(i).getPrevLng()));
                    Location currLoc = new Location("current");
                    currLoc.setLatitude(Double.parseDouble(availableDrivers.get(i).getCurrentLat()));
                    currLoc.setLongitude(Double.parseDouble(availableDrivers.get(i).getCurrentLng()));
                    animateMarker(prevLoc, currLoc, visibleMarkers.get(availableDrivers.get(i).getId()));
                }
            } else {
                /*if(isMarkerAvailable(availableDrivers.get(i).getId())){
                    Marker marker = visibleMarkers.get(availableDrivers.get(i).getId());
                    marker.remove();
                    visibleMarkers.remove(availableDrivers.get(i).getId());
                }*/
                visibleMarkers.put(availableDrivers.get(i).getId(), createMarker(Double.parseDouble(availableDrivers.get(i).getCurrentLat()),
                        Double.parseDouble(availableDrivers.get(i).getCurrentLng())));
            }
        }
    }

    String puLat, puLng, puLocation, doLat, doLng, doLocation = "";
    boolean updatable = true;

    protected Marker createMarker(double latitude, double longitude) {
        Log.e("add", "marker");
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
        return googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f).flat(true)
                .title("")
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(resource)));
    }

    private void showBookingTimer(String userID, String rideID) {
        JSONObject pickup = new JSONObject();
        try {
            pickup.put("address", puLocation);
            pickup.put("lat", puLat);
            pickup.put("lng", puLng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject dropOff = new JSONObject();
        try {
            dropOff.put("address", doLocation);
            dropOff.put("lat", doLat);
            dropOff.put("lng", doLng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(getActivity(), BookingTimer.class);
        intent.putExtra("pickUp", pickup.toString());
        if (!doLocation.equals(""))
            intent.putExtra("dropOff", dropOff.toString());
        intent.putExtra("CategoryID", categoryId);
        intent.putExtra("type", "" + bookingType);
        if (bookingType == 0)
            intent.putExtra("puDate", getCurrentDateTime());
        else
            intent.putExtra("puDate", bookingDataTime);
        intent.putExtra("UserID", userID);
        intent.putExtra("RideID", rideID);
        if (bookingType == 0)
            intent.putExtra("Response_time", "30");
        else
            intent.putExtra("Response_time", "60");
        intent.putExtra("Next_driver_availability", "30");
        intent.putExtra("Message", "This booking request has not yet been accepted by available drivers. We will continue looking for an available driver and will notify you as soon as we have found one. Please cancel the booking if you no longer need this ride*. *Please note the BookMyRide cancellation policy.");
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private void showGuaranteeDriver() {
        JSONObject pickup = new JSONObject();
        try {
            pickup.put("address", puLocation);
            pickup.put("lat", puLat);
            pickup.put("lng", puLng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject dropOff = new JSONObject();
        try {
            dropOff.put("address", doLocation);
            dropOff.put("lat", doLat);
            dropOff.put("lng", doLng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(getActivity(), GuaranteeDriverInfo.class);
        intent.putExtra("pickUp", pickup.toString());
        if (!doLocation.equals(""))
            intent.putExtra("dropOff", dropOff.toString());
        intent.putExtra("CategoryID", categoryId);
        intent.putExtra("type", "0");
        intent.putExtra("puDate", getCurrentDateTime());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    public void simpleClicked() {
        final Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.HOUR, 1);
        calendar.add(Calendar.MINUTE, 11);
        final Date defaultDate = calendar.getTime();

        singleBuilder = new SingleDateAndTimePickerDialog.Builder(getActivity())
                .bottomSheet()
                .curved()
                .backgroundColor(Color.BLACK)
                .mainColor(Color.parseColor("#e0e0e0"))
                .minutesStep(1)
                .mustBeOnFuture()
                .defaultDate(defaultDate)
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                        //Alert("Date", simpleDateFormat.format(date));
                        bookingDataTime = simpleDateFormat.format(date);
                        if (findTimeDistance(bookingDataTime) >= 11) {
                            if (session.isCardExist()) {
                                if (mDialog != null && mDialog.isShowing())
                                    mDialog.dismiss();
                                getDistance();
                                //bookRide("" + bookingType, "", "");

                                //bookRide("1", "", "");
                            } else
                                isCardSaved();
                            //bookRide("1");
                        } else
                            Alert("BookMyRide", "The pre-booked ride time must be at least 10 minutes from current time.");
                        //singleText.setText(simpleDateFormat.format(date));
                    }
                });
        singleBuilder.display();
    }

    SingleDateAndTimePickerDialog.Builder singleBuilder;
    SimpleDateFormat simpleDateFormat;

    private int findTimeDistance(String bookingDataTime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            String currentDateTime = format.format(cal.getTime());
            Log.e("currentDateTime", "" + currentDateTime);
            Log.e("bookingDataTime", "" + bookingDataTime);
            Date Date1 = format.parse(currentDateTime);
            Date Date2 = format.parse(bookingDataTime);
            long mills = Date2.getTime() - Date1.getTime();
            int Hours = (int) (mills / (1000 * 60 * 60));
            int Mins = (int) (mills / (1000 * 60)) % 60;

            String diff = Hours + ":" + Mins; // updated value every1 second
            Log.e("diff", diff);
            return Mins;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void onRideAlert(final String rideData, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(getActivity(), false);
        mDialog.setDialogTitle("Alert!");
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton("Track",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        try {
                            JSONObject rideObj = new JSONObject(rideData);
                            String status = rideObj.get("status").toString();
                            String bookingID = rideObj.get("id").toString();

                            if (status.equalsIgnoreCase("1") ||
                                    status.equalsIgnoreCase("3") ||
                                    status.equalsIgnoreCase("4")) {
                                Intent intent2 = new Intent(getActivity(), TrackDetail.class);
                                intent2.putExtra("status", status);
                                intent2.putExtra("driverCategory", rideObj.getString("driverCategory_id"));
                                intent2.putExtra("bookingID", bookingID);
                                startActivity(intent2);
                                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            } else if (status.equalsIgnoreCase("5") ||
                                    status.equalsIgnoreCase("6")) {
                                Intent intent3 = new Intent(getActivity(), FareBreakup.class);
                                intent3.putExtra("status", status);
                                intent3.putExtra("bookingID", bookingID);
                                startActivity(intent3);
                                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        mDialog.show();
    }

    private void moveDriversMarker(final Location prevLoc, final Location currLoc, final Marker mrk) {
        new Thread(new Runnable() {
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        double lng = 78.486671;
                        double lat = 17.385044;
                        animateMarker(prevLoc, currLoc, mrk);
                    }
                });
            }
        }).start();
    }

    public void animateMarker(final Location prevLoc, final Location currLoc, final Marker marker) {
        if (marker != null && currLoc != null) {
            final LatLng endPosition = new LatLng(currLoc.getLatitude(), currLoc.getLongitude());
            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = new ValueAnimator();
            //final LatLng startPosition = marker.getPosition();
            final LatLng startPosition = new LatLng(prevLoc.getLatitude(), prevLoc.getLongitude());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        marker.setFlat(true);
                        float bearing = (float) bearingBetweenLocations(
                                startPosition, endPosition);
                        rotateMarker(marker, bearing);
                    } catch (Exception ex) {
                        // I don't care atm..
                        ex.printStackTrace();
                    }
                }
            });
            valueAnimator.setFloatValues(0, 1);
            valueAnimator.setDuration(3000);
            valueAnimator.start();
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

    private void sessionExpired(String message) {
        try {
            final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(getActivity(), true);
            mDialog.setDialogTitle("Alert!");
            mDialog.setDialogMessage("Your session has been expired. Please login again.");
            mDialog.setPositiveButton(getResources().getString(R.string.ok),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                            new SessionHandler(getActivity()).saveToken("");
                            Intent intent = new Intent(getActivity(), SignIn.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            getActivity().startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            getActivity().finish();
                        }
                    });
            mDialog.show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
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
        mLocationRequest.setFastestInterval(5000);   // 5 second, in milliseconds
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
            case 1001:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (noEmailDialog != null && noEmailDialog.isShowing())
                            noEmailDialog.dismiss();
                        getProfile();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                    default:
                        break;
                }
                break;
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
            case 200:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        if (type == 0) {
                            updatable = false;
                            pickupLocation.setText(data.getStringExtra("Selected_Location"));
                            puLat = data.getStringExtra("Selected_Latitude");
                            puLng = data.getStringExtra("Selected_Longitude");
                            puLocation = data.getStringExtra("Selected_Location");
                            //CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(puLat), Double.parseDouble(puLng))).zoom(17).build();
                            //googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        } else if (type == 1) {
                            dropOffLocation.setText(data.getStringExtra("Selected_Location"));
                            doLat = data.getStringExtra("Selected_Latitude");
                            doLng = data.getStringExtra("Selected_Longitude");
                            doLocation = data.getStringExtra("Selected_Location");
                            // Move the camera to last position with a zoom level
                            //CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(doLat), Double.parseDouble(doLng))).zoom(17).build();
                            //googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                        updateAddDeleteUI();
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

    Location mLastLocation;

    public void getLocation() throws NullPointerException {
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
                zoomLevel = 15f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
            googleMap.moveCamera(cameraUpdate);
            if (updatable) {
                puLat = "" + mLastLocation.getLatitude();
                puLng = "" + mLastLocation.getLongitude();
                getAddress("" + mLastLocation.getLatitude(), "" + mLastLocation.getLongitude());
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


    /*When Location changes, this method get called. */
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (googleMap != null) {
            float zoomLevel = googleMap.getCameraPosition().zoom;
            if (zoomLevel < 10)
                zoomLevel = 15f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
            googleMap.moveCamera(cameraUpdate);
            if (updatable) {
                puLat = "" + location.getLatitude();
                puLng = "" + location.getLongitude();
                getAddress("" + location.getLatitude(), "" + location.getLongitude());
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
}

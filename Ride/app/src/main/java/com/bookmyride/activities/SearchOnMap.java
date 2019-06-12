package com.bookmyride.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.bookmyride.R;
import com.bookmyride.adapters.PlaceAutoCompleteAdapter;
import com.bookmyride.api.Config;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.fcm.NotificationFilters;
import com.bookmyride.views.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by vinod on 1/18/2017.
 */
public class SearchOnMap extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        LocationListener {
    String address;
    AutoCompleteTextView inputs;
    TextView done, titleDone;
    //TrackGPS gps;
    double latitude, longitude;
    GoogleMap googleMap;
    private PlaceAutoCompleteAdapter mAdapter;
    /*private static final LatLngBounds BOUNDS = new LatLngBounds(new LatLng(-57.965341647205726, 144.9987719580531),
            new LatLng(72.77492067739843, -9.998857788741589));*/
    private static final LatLngBounds BOUNDS = new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));
    protected GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    protected LatLng start;
    ImageView gpsBtn, cross;
    BroadcastReceiver mReceiver;
    SessionHandler session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_search);
        initGoogleAPIClient();
        init();
        /*if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }*/
        initializeMap();
        mAdapter = new PlaceAutoCompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS, null);
        inputs.setAdapter(mAdapter);
    }

    public boolean isValidLatLng(double lat, double lng) {
        if (lat < -90 || lat > 90) {
            return false;
        } else if (lng < -180 || lng > 180) {
            return false;
        }
        return true;
    }

    public void onBack(View view) {
        onBackPressed();
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(SearchOnMap.this, true);
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

    private void init() {
        session = new SessionHandler(this);
        inputs = (AutoCompleteTextView) findViewById(R.id.inputs);
        done = (TextView) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isValidLatLng(puLat, puLng)) {
                    Alert("Alert!", "Please select location.");
                } else {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Selected_Latitude", "" + puLat);
                    returnIntent.putExtra("Selected_Longitude", "" + puLng);
                    returnIntent.putExtra("Selected_Location", inputs.getText().toString());
                    setResult(RESULT_OK, returnIntent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });
        cross = (ImageView) findViewById(R.id.cross);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputs.setText("");
                puLat = -91;
                puLng = -181;
                googleMap.clear();
            }
        });
        titleDone = (TextView) findViewById(R.id.title_done);
        titleDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isValidLatLng(puLat, puLng)) {
                    Alert("Alert!", "Please select location.");
                } else {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Selected_Latitude", "" + puLat);
                    returnIntent.putExtra("Selected_Longitude", "" + puLng);
                    returnIntent.putExtra("Selected_Location", inputs.getText().toString());
                    setResult(RESULT_OK, returnIntent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });

        gpsBtn = (ImageView) findViewById(R.id.gps);
        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (gps != null) {
                LatLng coordinate = new LatLng(latitude, longitude); //Store these lat lng values somewhere. These should be constant.
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                        coordinate, googleMap.getCameraPosition().zoom);
                googleMap.animateCamera(location);
                //}
            }
        });

        inputs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.i(Config.TAG, "Autocomplete item selected: " + item.description);

                /*
                Issue a request to the Places Geo Data API to retrieve a Place object with additional
                details about the place.
                */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e(Config.TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);

                        start = place.getLatLng();
                        puLat = start.latitude;
                        puLng = start.longitude;
                        getAddress(puLat, puLng);
                        googleMap.addMarker(new MarkerOptions().position(start).title(puCity + ", " + puCountry).
                                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(start).zoom(15).build();
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                });
            }
        });
        inputs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int startNum, int before, int count) {
                if (start != null) {
                    start = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
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
                                    showPopup(msg);
                                }
                            }, 500);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
    }

    String msg = "";

    private void initializeMap() {
        if (googleMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.setMyLocationEnabled(false);
        /*LatLng currentPos = new LatLng(latitude, longitude);
        //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(currentPos).zoom(12f).bearing(0).tilt(0).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                googleMap.clear();
                puLat = point.latitude;
                puLng = point.longitude;
                try {
                    new GetLocationAsync(point.latitude, point.longitude).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                googleMap.addMarker(new MarkerOptions().position(point).title(address).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setSmallestDisplacement(0.1f);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(Config.TAG, connectionResult.toString());
    }

    class GetLocationAsync extends AsyncTask<String, Void, String> {
        Geocoder geocoder;
        List<Address> yourAddresses;
        double LATITUDE, LONGITUDE;

        public GetLocationAsync(double latitude, double longitude) {
            LATITUDE = latitude;
            LONGITUDE = longitude;
        }

        @Override
        protected void onPreExecute() {
            //Address.setText(" Getting location ");
        }

        @Override
        protected String doInBackground(String... params) {
            String address = "";
            try {
                geocoder = new Geocoder(SearchOnMap.this, Locale.getDefault());
                yourAddresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);

                if (yourAddresses.size() > 0) {
                    String yourAddress = yourAddresses.get(0).getAddressLine(0) + " "
                            + yourAddresses.get(0).getAddressLine(1);
                    String yourCity = yourAddresses.get(0).getLocality();
                    String yourCountry = yourAddresses.get(0).getCountryName();
                    address = yourAddress + ", " + yourCity + ", " + yourCountry;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return address;
        }

        @Override
        protected void onPostExecute(String result) {
            address = result;
            inputs.setText(address.replace("null", ""));
        }
    }

    double puLat = -91, puLng = -181;
    String puCity, puState, puCountry;

    private void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                // Here are some results you can geocode
                String ZIP = "";
                String city = "";
                String state = "";
                String country = "";

                if (addresses.get(0).getPostalCode() != null) {
                    ZIP = addresses.get(0).getPostalCode();
                    Log.d("ZIP", ZIP);
                }

                if (addresses.get(0).getLocality() != null) {
                    city = addresses.get(0).getLocality();
                    Log.d("city", city);
                }

                if (addresses.get(0).getAdminArea() != null) {
                    state = addresses.get(0).getAdminArea();
                    Log.d("state", state);
                }

                if (addresses.get(0).getCountryName() != null) {
                    country = addresses.get(0).getCountryName();
                    Log.d("country", country);
                }

                puCity = city;
                if (state.equals(""))
                    puState = city;
                else
                    puState = state;
                puCountry = country;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng currentPos = new LatLng(latitude, longitude);
        //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));
        //CameraPosition cameraPosition = new CameraPosition.Builder().target(currentPos).zoom(12f).bearing(0).tilt(0).build();
        if (googleMap != null) {
            float zoomLevel = googleMap.getCameraPosition().zoom;
            if (zoomLevel < 10)
                zoomLevel = 15f;
            CameraUpdate loc = CameraUpdateFactory.newLatLngZoom(
                    currentPos, zoomLevel);
            //googleMap.animateCamera(loc);
            googleMap.moveCamera(loc);
            //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            stopLocationUpdates();
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

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(NotificationFilters.REQUEST_CANCELLED));
    }

    private void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() &&
                mLocationRequest != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        //mGoogleApiClient.connect();
    }

    private void goHome() {
        Intent intent;
        if (session.getUserType().equals("3"))
            intent = new Intent(getApplicationContext(), PassengerHome.class);
        else
            intent = new Intent(getApplicationContext(), DriverHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        SearchOnMap.this.finish();
    }

    public void showPopup(String msg) {
        final Dialog mDialog = new Dialog(this, R.style.rideDialog);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
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
                goHome();
            }
        });
        mDialog.show();
        /*if(getIntent().hasExtra("end"))
            dismissDialog();*/
    }
}

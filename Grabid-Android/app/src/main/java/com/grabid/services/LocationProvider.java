package com.grabid.services;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.grabid.common.SessionManager;


/**
 * Created by graycell on 5/9/17.
 */

public class LocationProvider {

    private static LocationProvider instance = null;
    private static Activity context;

    public static final int ONE_MINUTE = 1000 * 60;
    public static final int FIVE_MINUTES = ONE_MINUTE * 5;
    public static final int THIRT_SECOND = 1000 * 30;
    public static final int TEN_SECOND = 1000 * 10;
    private static Location currentLocation;
    private static int LOCATION_PERMISSION_CODE = 101;
    static String[] permissionsRequired = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION};
    static int REQUEST_PERMISSION_SETTING = 200;

    private LocationProvider() {

    }

    public static LocationProvider getInstance() {
        if (instance == null) {
            instance = new LocationProvider();
        }

        return instance;
    }

    public void configureIfNeeded(Activity ctx) {
        if (context == null) {
            context = ctx;
            configureLocationUpdates();
        }
    }

    private void configureLocationUpdates() {
        final LocationRequest locationRequest = createLocationRequest();
        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                startLocationUpdates(googleApiClient, locationRequest);
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });
        googleApiClient.registerConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {

            }
        });

        googleApiClient.connect();
    }

    private static LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(FIVE_MINUTES);
        return locationRequest;
    }

    private static void startLocationUpdates(GoogleApiClient client, LocationRequest request) {
        LocationUpdates(client, request);

    }

    public static void LocationUpdates(GoogleApiClient client, LocationRequest request) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(context, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(context, permissionsRequired[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(context, permissionsRequired[1])
                        ) {
                    //Show Information about why you need the permission
                   /* AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("GRABiD would like to access your Location.");
                    builder.setMessage("GRABiD needs Location permissions.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(context, permissionsRequired, LOCATION_PERMISSION_CODE);
                        }
                    });
                    builder.setNegativeButton("Don’t Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();*/
                } else if (new SessionManager(context).getLocation()) {
                    //Previously Permission Request was cancelled with 'Dont Ask Again',
                    // Redirect to Settings after showing Information about why you need the permission
                   /* AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("GRABiD would like to access your Location.");
                    builder.setMessage("GRABiD needs Location permissions.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            // sentToSettings = true;

                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                            intent.setData(uri);
                            context.startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            Toast.makeText(context, "Go to Permissions to Grant Location Permission", Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNegativeButton("Don’t Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();*/
                } else {
                    //just request the permission
                    //  ActivityCompat.requestPermissions(context, permissionsRequired, LOCATION_PERMISSION_CODE);
                }
                //    new SessionManager(context).setLocation(true);

            } else {
                //You already have the permission, just go ahead.
                //proceedAfterPermission();
                LocationServices.FusedLocationApi.requestLocationUpdates(client, request, new com.google.android.gms.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        currentLocation = location;
                    }
                });
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, request, new com.google.android.gms.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    currentLocation = location;
                }
            });
        }
    }

  /*  public static void LocationUpdates(GoogleApiClient client, LocationRequest request) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
              *//*  ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);*//*
            } else if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                //  ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            } else if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                //   ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(client, request, new com.google.android.gms.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        currentLocation = location;
                    }
                });
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, request, new com.google.android.gms.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    currentLocation = location;
                }
            });
        }
    }*/

    public Location getCurrentLocation() {
        return currentLocation;
    }
}

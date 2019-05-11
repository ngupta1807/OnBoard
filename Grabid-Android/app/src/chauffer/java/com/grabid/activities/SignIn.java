package com.grabid.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.grabid.BuildConfig;
import com.grabid.R;
import com.grabid.adapters.CountryCodeAdapter;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.services.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by vinod on 10/14/2016.
 */
public class SignIn extends AppCompatActivity implements AsyncTaskCompleteListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener, AdapterView.OnItemSelectedListener {
    EditText userName, password;
    GPSTracker gps;
    double latitude, longitude;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private static final String TAG = SignIn.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;
    private LocationManager locationManager;
    private String provider;
    private LocationRequest mLocationRequest;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    int LOCATION_PERMISSION_CODE = 101;
    TimeZone timeZone;
    String timeZoneId = "";
    String idd;
    String device_type = "Android";
    private boolean sentToSettings = false;
    String[] permissionsRequired = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION};
    int REQUEST_PERMISSION_SETTING = 200;
    boolean IsLocationUpdate = false;
    String[] countrycode = {"+61", "+91"};
    public static String countrycodestr = "";
    Spinner countrypicker;
    int[] flags = {R.drawable.au, R.drawable.in};
    int check = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            e.toString();
        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        init();
        initIp();

        try {
            timeZone = TimeZone.getDefault();
            timeZoneId = timeZone.getID();
            Log.v("", timeZoneId);
        } catch (Exception e) {
            e.toString();
        }
        createLocationRequest();

    }

    public void initIp() {
        //ipAddress = getLocalIpAddress();
        idd = Build.ID;
        checkReferral(device_type, idd);
    }

    public void checkReferral(String deviceType, String version) {
        type = 4;
        String url;
        HashMap<String, String> params = new HashMap<>();
        //  params.put("ip", ip);
        params.put("device_type", deviceType);
        params.put("version", version);
        Log.d(Config.TAG, params.toString());
        if (Internet.hasInternet(SignIn.this)) {
            RestAPICall apiCall = new RestAPICall(this, HTTPMethods.POST, this, params);
            url = Config.SERVER_URL + Config.SHAREREFERRAL;
            apiCall.execute(url, "");
        } else {
            showMessage(getResources().getString(R.string.no_internet));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("", "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
       /* final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                if (!IsLocationUpdate)
                    startLocationUpdates();
            }
        }*/
        try {
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
            }
        } catch (Exception e) {
            e.toString();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        if (IsLocationUpdate)
            stopLocationUpdates();

    }

    @Override
    protected void onPause() {
        super.onPause();


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
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(SignIn.this, 9);
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

    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions", "publish_stream", "email", "user_groups", "read_stream", "user_about_me", "offline_access");
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    private void initFacebook() {
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                Log.v("", "");
            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                Log.v("", "");
                //displayMessage(newProfile);
                //Log.d("new_Profile",newProfile.getName());
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                //Log.d("FB_token:",loginResult.getAccessToken().getToken());
                AccessToken token = loginResult.getAccessToken();
                String facebook_id = loginResult.getAccessToken().getUserId();
                String appid = loginResult.getAccessToken().getApplicationId();
                loginWithFacebook(loginResult.getAccessToken().getToken());
                //getUserFbProfile(token);
            }

            @Override
            public void onCancel() {
                try {
                    Toast.makeText(SignIn.this, "Login Cancelled", Toast.LENGTH_SHORT).show();
                    //   AccessToken token = AccessToken.getCurrentAccessToken();
                    //  if (token != null) {
                    // String tokenvalue = AccessToken.getCurrentAccessToken().getToken();
                    //    Log.v("", tokenvalue);
                    //   loginWithFacebook(tokenvalue);
                    //  }
                } catch (Exception e) {
                    e.toString();
                }
                //getUserFbProfile(token);
            }

            @Override
            public void onError(FacebookException error) {
                try {
                    Toast.makeText(SignIn.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    if (error instanceof FacebookAuthorizationException) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut();
                        }
                    }
                } catch (Exception e) {
                    e.toString();
                }
            }
        });
    }

    private void getUserFbProfile(final AccessToken loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("LoginActivity", response.toString());
                        String first_name = object.optString("first_name");
                        String last_name = object.optString("last_name");
                        String email = object.optString("email");
                        String id = object.optString("id");
                        Log.v("", email);
                        //loginWithFacebook(loginResult.getToken(), first_name, last_name, email, id);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void init() {
        gps = new GPSTracker(SignIn.this);
        userName = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        countrypicker = (Spinner) findViewById(R.id.countrycode);
        countrypicker.setOnItemSelectedListener(SignIn.this);
        CountryCodeAdapter customAdapter = new CountryCodeAdapter(SignIn.this, flags, countrycode);
        countrypicker.setAdapter(customAdapter);
        initFacebook();

     /*   GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId().requestEmail().requestIdToken("758894980513-55kiq82g0f2shlkod4f4f07vmtdapbfq.apps.googleusercontent.com")
                .build();
*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestId().requestEmail().requestIdToken(getString(R.string.clientID))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(LocationServices.API).addConnectionCallbacks(this).addConnectionCallbacks(this)
                .build();
        // mGoogleApiClient.connect();
    }

    public void openSignup(View view) {
        startActivity(new Intent(getApplicationContext(), SignUP.class));
        //SignIn.this.finish();
    }

    public void openForgotPassword(View view) {
        startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
        //SignIn.this.finish();
    }

    public void doFacebookLogin(View view) {
        LoginManager.getInstance().logInWithReadPermissions(SignIn.this, Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));
    }

    public void doGooglePlusLogin(View view) {
        //Toast.makeText(SignIn.this,"in progress...",Toast.LENGTH_SHORT).show();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        try {
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(SignIn.this, "You haven't installed google+ on your device", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean isValid() {
        if (userName.getText().toString().trim().length() == 0 || password.getText().toString().trim().length() == 0) {
            showMessage(getResources().getString(R.string.usernameemailandpassword));
            return false;
        } else if (userName.getText().toString().trim().length() == 0) {
            showMessage(getResources().getString(R.string.usernamepassword));
            return false;
        } else if (password.getText().toString().trim().length() == 0) {
            showMessage(getResources().getString(R.string.passwordcheck));
            return false;
        }
        return true;
    }

    /* public void loginWithGoogle(String id, String email, String firstName, String lastName) {
         type = 3;
         String url = Config.SERVER_URL + Config.GOOGLE_LOGIN;
         HashMap<String, String> params = new HashMap<>();
         params.put("google_id", id);
         params.put("email", email);
         params.put("first_name", firstName);
         params.put("last_name", lastName);
         RestAPICall apiCall = new RestAPICall(this, HTTPMethods.POST, this, params);
         apiCall.execute(url, "");
     }*/
    public void loginWithGoogle(String id, String email, String firstName, String lastName, String accessToken) {
        if (gps.canGetLocation() && (latitude == 0.0 && longitude == 0.0)) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }
        if (latitude == 0.0 && longitude == 0.0) {
            // showMessage(getString(R.string.latlongerror));
            if (!IsLocationUpdate)
                LocationUpdates();
            else
                showMessage(getString(R.string.latlongerror));
        } else {
            type = 3;
//            String url = Config.SERVER_URL + Config.GOOGLE_LOGIN;
            String url = Config.SERVER_URL + Config.SOCIAL_LOGIN;
            HashMap<String, String> params = new HashMap<>();
            //   params.put("google_id", id);
            params.put("token", accessToken);
            //   params.put("email", email);
            //  params.put("first_name", firstName);
            //   params.put("last_name", lastName);
            params.put("device_registration_id", new SessionManager(this).getGCMKey());
            params.put("device", "android");
            params.put("type", "" + "google");
            params.put("current_latitude", "" + latitude);
            params.put("current_longitude", "" + longitude);
            params.put("timezone", timeZoneId);
            params.put("updateTimeZone", "TimeZoneUpdate");
            if (!countrycodestr.contentEquals(""))
                params.put("country_code", countrycodestr);
            if (BuildConfig.logistic)
                params.put("app_id", "1");
            else
                params.put("app_id", "2");
            if (Internet.hasInternet(SignIn.this)) {
                RestAPICall apiCall = new RestAPICall(this, HTTPMethods.POST, this, params);
                apiCall.execute(url, "");
            } else {
                showMessage(getResources().getString(R.string.no_internet));
            }
        }
    }

    public void loginWithFacebook(String accessToken) {
        if (gps.canGetLocation() && (latitude == 0.0 && longitude == 0.0)) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }
        if (latitude == 0.0 && longitude == 0.0) {
            //showMessage(getString(R.string.latlongerror));
            if (!IsLocationUpdate)
                LocationUpdates();
            else
                showMessage(getString(R.string.latlongerror));
        } else {
            type = 2;
//            String url = Config.SERVER_URL + Config.FACEBOOK_LOGIN;
            String url = Config.SERVER_URL + Config.SOCIAL_LOGIN;
            HashMap<String, String> params = new HashMap<>();
            params.put("token", accessToken);
            params.put("device_registration_id", new SessionManager(this).getGCMKey());
            params.put("device", "android");
            params.put("current_latitude", "" + latitude);
            params.put("current_longitude", "" + longitude);
            params.put("type", "" + "facebook");
            params.put("timezone", timeZoneId);
            params.put("updateTimeZone", "TimeZoneUpdate");
            if (!countrycodestr.contentEquals(""))
                params.put("country_code", countrycodestr);
            if (BuildConfig.logistic)
                params.put("app_id", "1");
            else
                params.put("app_id", "2");
            if (Internet.hasInternet(SignIn.this)) {
                RestAPICall apiCall = new RestAPICall(this, HTTPMethods.POST, this, params);
                apiCall.execute(url, "");
            } else {
                showMessage(getResources().getString(R.string.no_internet));
            }
        }
    }

    /*  params.put("facebook_id", id);
           params.put("first_name", firtname);
           params.put("last_name", lastname);
           params.put("email", email);*/
    int type;

    public void loginUser(View view) {
        if (gps.canGetLocation() && (latitude == 0.0 && longitude == 0.0)) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }
        if (latitude == 0.0 && longitude == 0.0) {
            // showMessage(getString(R.string.latlongerror));
            if (!IsLocationUpdate)
                LocationUpdates();
            else
                showMessage(getString(R.string.latlongerror));
        } else {
            type = 1;
            if (isValid()) {
                String key = new SessionManager(this).getGCMKey();
                String url = Config.SERVER_URL + Config.LOGIN;
                HashMap<String, String> params = new HashMap<>();
                params.put("username", userName.getText().toString());
                params.put("password_hash", password.getText().toString());
                params.put("device_registration_id", new SessionManager(this).getGCMKey());
                params.put("device_os", "ANDROID");
                params.put("current_latitude", "" + latitude);
                params.put("current_longitude", "" + longitude);
                params.put("timezone", timeZoneId);
                params.put("updateTimeZone", "TimeZoneUpdate");
                if (!countrycodestr.contentEquals(""))
                    params.put("country_code", countrycodestr);
                if (BuildConfig.logistic)
                    params.put("app_id", "1");
                else
                    params.put("app_id", "2");
                if (Internet.hasInternet(SignIn.this)) {
                    RestAPICall apiCall = new RestAPICall(this, HTTPMethods.POST, this, params);
                    apiCall.execute(url, "");
                } else {
                    showMessage(getResources().getString(R.string.no_internet));
                }
            }
        }
    }

    @Override
    public void onTaskComplete(String result) {
        Response(result);
    }

    private void Response(String result) {
        try {
            if ((type == 4)) {
                try {
                    JSONObject outJson = new JSONObject(result);
                    String message = outJson.optString(Config.MESSAGE);
                    if (message.contentEquals("")) {
                        String referral_code = outJson.optJSONObject(Config.DATA).optString("referral_code");
                        // referralCode.setText(referral_code);
                        Intent intent = new Intent(getApplicationContext(), SignUP.class);
                        intent.putExtra("referaal", referral_code);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.toString();
                }
            } else {
                JSONObject outterJson = new JSONObject(result);
                if (outterJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                    if (type == 1) {
                        UserData(outterJson);
                    } else if (type == 2 || type == 3) {
                        JSONObject ChildJson = outterJson.optJSONObject(Config.DATA);
                        UserData(outterJson);
                    }
                } else if (outterJson.getInt(Config.STATUS) == APIStatus.UNPROCESSABLE) {
                    if (type == 2 || type == 3) {
                        String message = null;
                        try {
                            JSONArray jsonArray = outterJson.optJSONArray(Config.DATA);
                            JSONObject childJson = jsonArray.optJSONObject(0);
                            String field = childJson.optString("field");
                            if (field != null && !field.isEmpty()) {
                                if (!field.contentEquals("token")) {
                                    JSONObject userInfo = childJson.optJSONObject("message");
                                    String value = userInfo.toString();
                                    if (userInfo.has("verified_status")) {
                                        userInfo.put("auth_key", "");
                                        messageDialog("Success!", "Thanks for the details. Please click on OK to complete your profile.", userInfo.toString());
                                    }
                                } else {
                                    String alertmessage = childJson.optString("message");
                                    showMessage(alertmessage);
                                }
                            }
                        } catch (Exception e) {
                            e.toString();
                        }
                    } else if (type == 1) {
                        try {
                            String message = null;
                            JSONArray jsonArray = outterJson.optJSONArray(Config.DATA);
                            if (jsonArray.length() > 0) {
                                message = outterJson.optJSONArray(Config.DATA).optJSONObject(0).optString(Config.MESSAGE);
                            } else if (message == null) {
                                message = outterJson.optString("message");
                                Log.v("", message);
                            }
                            if (!message.contentEquals("423"))
                                showMessage(message);
                            else
                                countrypicker.performClick();
                        } catch (Exception e) {
                            e.toString();
                        }
                    }
                } else if (outterJson.getInt(Config.STATUS) == APIStatus.REVIEW) {
                    try {
                        String message = null;
                        JSONArray jsonArray = outterJson.optJSONArray(Config.DATA);
                        if (jsonArray.length() > 0) {
                            message = outterJson.optJSONArray(Config.DATA).optJSONObject(0).optString(Config.MESSAGE);
                        } else if (message == null) {
                            message = outterJson.optString("message");
                            Log.v("", message);
                        }
                        showMessage(message);
                    } catch (Exception e) {
                        e.toString();
                    }
                } else {
                    try {
                        String message = null;
                        JSONArray jsonArray = outterJson.optJSONArray(Config.DATA);
                        if (jsonArray.length() > 0) {
                            message = outterJson.optJSONArray(Config.DATA).optJSONObject(0).optString(Config.MESSAGE);
                        } else if (message == null) {
                            message = outterJson.optString("message");
                            Log.v("", message);
                        }
                        showMessage(message);
                    } catch (Exception e) {
                        e.toString();
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            String userpaymentmode = dataObj.getString(Keys.USER_PAYMENT_MODES);
            new SessionManager(this).saveUserDate(UserInfo.getString(Keys.KEY_ID),
                    UserInfo.getString(Config.USER_NAME),
                    UserInfo.getString(Keys.KEY_EMAIL),
                    dataObj.getString(Keys.KEY_IMAGE),
                    UserInfo.getString(Config.TOKEN),
                    UserInfo.toString(), driverProfile.toString(),
                    dataObj.getString(Keys.DRIVER_LIMAGE),
                    company.toString(), vehicle.toString(), sRating, dRating, dataObj.optString(Keys.CREDIT_CARD), dataObj.optString(Keys.BANK_DETAIL), AdminApprovalStatus, EmailVerified, userpaymentmode, true);
            new SessionManager(this).saveCount("");
            goToHome();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message) {
        AlertManager.messageDialog(this, "Alert!", message);
    }

    private void goToHome() {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        SignIn.this.finish();
    }

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    public boolean checkOS() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return true;
        else
            return false;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        List<String> permissionsNeeded = new ArrayList<String>();
        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");
        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("Read Contacts");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Storage");
        if (!addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("Camera");
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("Phone");

        if (permissionsList.size() > 0) {

            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(SignIn.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
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
        } else if (requestCode == RC_SIGN_IN) {
            //Log.d("res", data.toString());
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result != null)
                SignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void SignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String givenname = acct.getGivenName();
            String familyname = acct.getFamilyName();
            String personName = acct.getDisplayName();
            String personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();
            String idToken = acct.getIdToken();
                /*Log.e(TAG, "Name: " + personName + ", token: " + acct.getIdToken()
                        + ", Image: " + personPhotoUrl +
                        ", Email:" + email);*/
            loginWithGoogle(acct.getId(), acct.getEmail(), givenname, familyname, idToken);

        } else {
            Log.v("", "");
            // Signed out, show unauthenticated UI.
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    public void messageDialog(String title, String message, final String userInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), SignUP.class);
                intent.putExtra("userInfo", userInfo);
                startActivity(intent);
                //  SignIn.this.finish();
            }
        });
        Dialog d = builder.create();
        d.show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        settingsrequest();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.v("", "");
    }

    protected void stopLocationUpdates() {
        try {
            IsLocationUpdate = false;
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
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
    }

    /* public void LocationUpdates() {
         if (Build.VERSION.SDK_INT >= 23) {
             if (ContextCompat.checkSelfPermission(SignIn.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                     != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(SignIn.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                     != PackageManager.PERMISSION_GRANTED) {
                 PermissionDialog();
                *//* ActivityCompat.requestPermissions(SignIn.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);*//*
            } else if (ContextCompat.checkSelfPermission(SignIn.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                PermissionDialog();
                //  ActivityCompat.requestPermissions(SignIn.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            } else if (ContextCompat.checkSelfPermission(SignIn.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                PermissionDialog();
                // ActivityCompat.requestPermissions(SignIn.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
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
    }
*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
            } /*else if (ActivityCompat.shouldShowRequestPermissionRationale(SignIn.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(SignIn.this, permissionsRequired[1])
                    ) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(SignIn.this, permissionsRequired, LOCATION_PERMISSION_CODE);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                // Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }*/
        }
          /*  if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationUpdates();
            }*/

    }

    /* @Override
     protected void onPostResume() {
         super.onPostResume();
         if (sentToSettings) {
             if (ActivityCompat.checkSelfPermission(SignIn.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                 //Got Permission
                 startLocationUpdates();
             }
         }
     }
 */
    public void LocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(SignIn.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(SignIn.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(SignIn.this, permissionsRequired[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(SignIn.this, permissionsRequired[1])
                        ) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
                    builder.setTitle("GRABiD Chauffeur would like to access your Location.");
                    builder.setMessage("GRABiD Chauffeur needs Location permissions.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(SignIn.this, permissionsRequired, LOCATION_PERMISSION_CODE);
                        }
                    });
                    builder.setNegativeButton("Don’t Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else if (new SessionManager(this).getLocation()) {
                    //Previously Permission Request was cancelled with 'Dont Ask Again',
                    // Redirect to Settings after showing Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
                    builder.setTitle("GRABiD Chauffeur would like to access your Location.");
                    builder.setMessage("GRABiD Chauffeur needs Location permissions.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            sentToSettings = true;
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            Toast.makeText(getBaseContext(), "Go to Permissions to Grant Location", Toast.LENGTH_LONG).show();
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
                    ActivityCompat.requestPermissions(SignIn.this, permissionsRequired, LOCATION_PERMISSION_CODE);
                }
                new SessionManager(this).setLocation(true);

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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (++check > 1)
            countrycodestr = countrycode[i];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}


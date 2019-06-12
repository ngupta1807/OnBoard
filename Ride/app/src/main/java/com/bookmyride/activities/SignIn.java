package com.bookmyride.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
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
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.model.Address;
import com.google.api.services.people.v1.model.Birthday;
import com.google.api.services.people.v1.model.CoverPhoto;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;
import com.bookmyride.R;
import com.bookmyride.activities.register.SelectUser;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.views.AlertDialog;
import com.bookmyride.views.RideDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vinod on 2017-01-07.
 */
public class SignIn extends AppCompatActivity implements View.OnClickListener,
        AsyncTaskCompleteListener, GoogleApiClient.OnConnectionFailedListener {
    double latitude, longitude;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private static final String TAG = SignIn.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;

    String socialToken = "", socialType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_signin);
        init();
        if (checkOS()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkAndRequestPermissions())
                    getReferralCode();
            }
        } else {
            getReferralCode();
        }
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

            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                //displayMessage(newProfile);
                //Log.d("new_Profile", newProfile.getName());
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                type = 1;
                socialToken = loginResult.getAccessToken().getToken();
                socialType = "facebook";
                loginBySocial("");
                //LoginManager.getInstance().logOut();
            }

            @Override
            public void onCancel() {
                AccessToken token = AccessToken.getCurrentAccessToken();
                getUserFbProfile(token);
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SignIn.this, error.getMessage(), Toast.LENGTH_LONG).show();
                if (error instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
            }
        });
    }

    private void getUserFbProfile(AccessToken loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        //Log.v("LoginActivity", response.toString());
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "last_name,first_name,id,name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void init() {
        initFacebook();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(SignIn.this.getResources().getString(R.string.server_client_id))
                .requestEmail().build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onClick(View view) {

    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    public void openHomeScreen(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        SignIn.this.finish();
    }

    public void openSignup(View view) {
        startActivity(new Intent(getApplicationContext(), SelectUser.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        //SignIn.this.finish();
    }

    public void openForgotPassword(View view) {
        startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        //SignIn.this.finish();
    }

    public void doFacebookLogin(View view) {
        if (Internet.hasInternet(this))
            LoginManager.getInstance().logInWithReadPermissions(SignIn.this,
                    Arrays.asList("email", "public_profile", "user_birthday"));
        else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    public void doGooglePlusLogin(View view) {
        if (Internet.hasInternet(this)) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    public void loginBySocial(String userType) {
        HashMap<String, String> params = new HashMap<>();
        params.put("device", "android");
        params.put("type", socialType);
        params.put("token", socialToken);
        params.put("device_token", new SessionHandler(this).getGCMKey());
        params.put("latitude", "" + latitude);
        params.put("longitude", "" + longitude);
        if (!userType.equals(""))
            params.put("userType", userType);
        APIHandler apiCall = new APIHandler(this, HTTPMethods.POST, this, params);
        apiCall.execute(Config.SOCIAL_LOGIN, "");
    }

    int type;

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outerJson = new JSONObject(result);
            if (outerJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                if (type == 3) {
                    JSONObject dataObj = outerJson.getJSONObject(Key.DATA);
                    String referralCode = dataObj.getString("referral_code");
                    new SessionHandler(this).saveSessionCode(referralCode);
                    startActivity(new Intent(getApplicationContext(), SelectUser.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    JSONObject dataObj = outerJson.getJSONObject(Key.DATA);
                    new SessionHandler(this).saveCardExist(false);
                    new SessionHandler(this).saveOnlineStatus(false);
                    new SessionHandler(this).saveReferralCode(dataObj.getString("referralCode"));
                    new SessionHandler(this).saveUserActualType(dataObj.getString(Key.USER_TYPE));
                    if (dataObj.getString(Key.USER_TYPE).equals("5")) {
                        loginResponse = new JSONObject(result);
                        showProceedDialog(outerJson, outerJson.getString(Key.MESSAGE));
                    } else {
                        if (dataObj.getString(Key.USER_TYPE).equals("4"))
                            proceedAsDriver(outerJson);
                        else proceedAsPassenger(outerJson);
                    }
                }
            } else if (outerJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                if (type == 3) {

                } else {
                    JSONObject msgObj = outerJson.getJSONArray(Key.DATA).getJSONObject(0).getJSONObject(Key.MESSAGE);
                    if (type == 1) {
                        LoginManager.getInstance().logOut();
                        startActivity(new Intent(getApplicationContext(), SelectUser.class)
                                .putExtra("social_data", msgObj.toString())
                                .putExtra("type", "facebook"));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else if (type == 2) {
                        startActivity(new Intent(getApplicationContext(), SelectUser.class)
                                .putExtra("social_data", msgObj.toString())
                                .putExtra("type", "google"));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
                //Alert("Alert!", outerJson.getJSONArray(Key.DATA).getJSONObject(0).getString(Key.MESSAGE));
            } else if (outerJson.getInt(Key.STATUS) == APIStatus.ACCEPTED) {
                Alert("Alert!", outerJson.getJSONArray(Key.DATA).getJSONObject(0).getString(Key.MESSAGE));
            } else if (outerJson.getInt(Key.STATUS) == APIStatus.GUARANTEE_AVAILABLE) {
                loginDialog("Alert!", outerJson.getJSONArray(Key.DATA).getJSONObject(0).getString(Key.MESSAGE));
            } else {
                Alert("Alert!", outerJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void goToHome(String userType) {
        new SessionHandler(this).saveUserType(userType);
        Intent intent;
        if (userType.equals("3"))
            intent = new Intent(getApplicationContext(), PassengerHome.class);
        else
            intent = new Intent(getApplicationContext(), DriverHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        SignIn.this.finish();
    }

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    public boolean checkOS() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return true;
        else
            return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*@Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }*/
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String personName = acct.getDisplayName();
            String personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();
            new requestUserInfoAsync(this, acct).execute();
            Log.e(TAG, "Name: " + personName + ", token: " + acct.getIdToken()
                    + ", Image: " + personPhotoUrl +
                    ", Email:" + email);
            type = 2;
            socialToken = acct.getIdToken();
            socialType = "google";
            loginBySocial("");
        } else {
            Log.v("::","er:"+result.getStatus());
            // Signed out, show unauthenticated UI.
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void Alert(String title, String message) {
        final RideDialog mDialog = new RideDialog(SignIn.this, false, true);
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


    private void loginDialog(String title, String message) {
        final RideDialog mDialog = new RideDialog(SignIn.this, true, true);
        mDialog.setDialogTitle(title);
        //mDialog.showCross(true);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton("As Driver", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                loginBySocial("4");

            }
        });
        mDialog.setNegativeButton("As Passenger", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                loginBySocial("3");
            }
        });
        mDialog.show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkAndRequestPermissions() {
        int permissionReadContact = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int phoneStatePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int writepermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int locationFinePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int locationCoarsePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int callpermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int readCalender = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        int writeCalendar = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionReadContact != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }

        if (phoneStatePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (locationFinePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (locationCoarsePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (callpermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (readCalender != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CALENDAR);
        }
        if (writeCalendar != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_CALENDAR);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    if (permissions[i].equals(Manifest.permission.READ_CONTACTS)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("msg", "read_contact granted");
                        }
                    } else if (permissions[i].equals(Manifest.permission.READ_PHONE_STATE)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("msg", "read phone state granted");
                        }
                    } else if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("msg", "write external storage granted");
                        }
                    } else if (permissions[i].equals(Manifest.permission.CALL_PHONE)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("msg", "call granted");
                        }
                    } else if (permissions[i].equals(Manifest.permission.CAMERA)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("msg", "camera granted");
                        }
                    } else if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("msg", "location fine granted");
                        }
                    } else if (permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("msg", "location coarse granted");
                        }
                    } else if (permissions[i].equals(Manifest.permission.READ_CALENDAR)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("msg", "Read Calendar granted");
                        }
                    } else if (permissions[i].equals(Manifest.permission.WRITE_CALENDAR)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("msg", "Write Calendar granted");
                        }
                    }
                }
                getReferralCode();
            }
        }
    }

    private void getReferralCode() {
        type = 3;
        if (Internet.hasInternet(this)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("device_type", "Android");
            params.put("version", "" + Build.ID);
            APIHandler apiCall = new APIHandler(this, HTTPMethods.POST, this, params);
            apiCall.execute(Config.SHARE_CODE, "");
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void showProceedDialog(final JSONObject outJson, String message) {
        final AlertDialog mDialog = new AlertDialog(SignIn.this, false);
        mDialog.setDialogTitle("Success!");
        if (message.equals(""))
            message = "Please select if you would like to proceed as a driver or passenger.";
        mDialog.setDialogMessage(message);
        mDialog.setCancelOnTouchOutside(false);
        mDialog.setPositiveButton(getResources().getString(R.string.proceed_as_driver), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                loggedInAs("driver");
                //proceedAsDriver(outJson);
            }
        });
        mDialog.setNegativeButton(getResources().getString(R.string.proceed_as_passenger), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                loggedInAs("passenger");
                //proceedAsPassenger(outJson);
            }
        });
        mDialog.show();
    }

    private void proceedAsDriver(final JSONObject outJson) {
        try {
            JSONObject dataObj = outJson.getJSONObject(Key.DATA);
            new SessionHandler(getApplicationContext()).saveDriverData(
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
            goToHome("4");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void proceedAsPassenger(final JSONObject outJson) {
        try {
            JSONObject dataObj = outJson.getJSONObject(Key.DATA);
            new SessionHandler(getApplicationContext()).savePassengerData(
                    dataObj.getString(Key.ID),
                    "3",
                    dataObj.getString(Key.USERNAME),
                    dataObj.getString(Key.IMAGE),
                    dataObj.getString(Key.EMAIL),
                    dataObj.getString(Key.TOKEN),
                    dataObj.getString(Key.FIRST_NAME),
                    dataObj.getString(Key.LAST_NAME),
                    dataObj.getString(Key.PHONE),
                    dataObj.getString(Key.DIAL_CODE));
            goToHome("3");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    JSONObject loginResponse = null;

    private void loggedInAs(final String userType) {
        String token = "";
        try {
            token = loginResponse.getJSONObject(Key.DATA).getString(Key.TOKEN);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (Internet.hasInternet(this)) {
            APIHandler apiCall = new APIHandler(this, HTTPMethods.GET, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    try {
                        JSONObject outerJson = new JSONObject(result);
                        if (outerJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                            if (userType.equalsIgnoreCase("driver"))
                                proceedAsDriver(loginResponse);
                            else proceedAsPassenger(loginResponse);
                        } else if (outerJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                            Alert("Alert!", outerJson.getString(Key.MESSAGE));
                            //showRideDialog("Alert!", outerJson.getString(Key.MESSAGE));
                        } else
                            Alert("Alert!", outerJson.getString(Key.MESSAGE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
            apiCall.execute(Config.ACTIVATE_PROFILE + userType, token);
        } else Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private static class requestUserInfoAsync extends AsyncTask<Void, Void, Void> {

        // Global instance of the HTTP transport.
        private static HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
        // Global instance of the JSON factory.
        private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

        private Context context;
        private GoogleSignInAccount acct;

        private String birthdayText;
        private String addressText;
        private String cover;

        public requestUserInfoAsync(Context context, GoogleSignInAccount acct) {
            this.context = context;
            this.acct = acct;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // On worker thread
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                    context, Collections.singleton(Scopes.PROFILE)
            );
            credential.setSelectedAccount(new android.accounts.Account(acct.getEmail(), "com.google"));
            //credential.setSelectedAccount(new Account(acct.getEmail(), "com.google"));
            People service = new People.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(context.getString(R.string.app_name) /* whatever you like */)
                    .build();

            // All the person details
            try {
              /*  PeopleService service = new PeopleService.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                        .setApplicationName("Google Sign In Quickstart")
                        .build();

                ListConnectionsResponse connectionsResponse = service
                        .people()
                        .connections()
                        .list("people/me")
                        .setPersonFields("names,emailAddresses")
                        .execute();
                */






                Person meProfile = service.people().get("people/me").set("personFields","names,addresses,birthdays,genders,phoneNumbers,photos").execute();
               /* ListConnectionsResponse birthdays = service
                        .people()
                        .connections()
                        .list("people/me")
                        .set("personFields","names,addresses,birthdays,genders,phoneNumbers,photos")
                        .execute();*/



                List<Birthday> birthdays = meProfile.getBirthdays();
                if (birthdays != null && birthdays.size() > 0) {
                    Birthday birthday = birthdays.get(0);

                    // DateFormat.getDateInstance(DateFormat.FULL).format(birthdayDate)
                    birthdayText = "";
                    try {
                        if (birthday.getDate().getYear() != null) {
                            birthdayText += birthday.getDate().getYear() + " ";
                        }
                        birthdayText += birthday.getDate().getMonth() + " " + birthday.getDate().getDay();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                List<Address> addresses = meProfile.getAddresses();
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    addressText = address.getFormattedValue();
                }

                List<CoverPhoto> coverPhotos = meProfile.getCoverPhotos();
                if (coverPhotos != null && coverPhotos.size() > 0) {
                    CoverPhoto coverPhoto = coverPhotos.get(0);
                    cover = coverPhoto.getUrl();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.e("TagTag", "birthday: " + birthdayText);
            Log.e("TagTag", "address: " + addressText);
            Log.e("TagTag", "cover: " + cover);
        }
    }
}
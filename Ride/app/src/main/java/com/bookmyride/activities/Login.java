package com.bookmyride.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;

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
import com.bookmyride.fcm.NotificationFilters;
import com.bookmyride.services.LocationService;
import com.bookmyride.services.RouteService;
import com.bookmyride.services.TrackGPS;
import com.bookmyride.util.AsteriskPasswordTransformationMethod;
import com.bookmyride.views.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by vinod on 1/10/2017.
 * This class accept user credentials as an input and logged in a user to the app.
 */
public class Login extends AppCompatActivity implements AsyncTaskCompleteListener {
    EditText userName, password;
    double latitude, longitude;
    private BroadcastReceiver mReceiver;
    TrackGPS gps;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        if (checkOS())
            checkPermissions();

        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(this, RouteService.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(NotificationFilters.LOCATION_CHANGED));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        //stopService(new Intent(this, RouteService.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        //stopService(new Intent(this, RouteService.class));
    }

    public void openForgotPassword(View view) {
        startActivity(new Intent(this, ForgotPassword.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void openRegistration(View view) {
        startActivity(new Intent(this, SelectUser.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void init() {
        gps = new TrackGPS(this);
        userName = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        //password.addTextChangedListener(new PasswordTextWatcher(password, getApplicationContext()));
        password.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NotificationFilters.LOCATION_CHANGED)) {
                    double lat = intent.getDoubleExtra("lat", 0.0);
                    double lng = intent.getDoubleExtra("lng", 0.0);
                    latitude = lat;
                    longitude = lng;
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        onBack();
        //super.onBackPressed();
    }

    public void onBack() {
        startActivity(new Intent(this, SignIn.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Login.this.finish();
    }

    /**
     * Open home screen.
     *
     * @param view the view
     */
    public void openHomeScreen(View view) {
        if (Internet.hasInternet(this))
            loginUser();
        else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    /* Call web service (API) for logged in a user to app
    * Parameter's info
    * @param username, password : valid user name and password which user chose while registration
    * @param device_token : GCM/FCM key for push notification
    * @param device : default set as 'android'
    * @param latitude, longitude : User current location coordinates obtained by fused api
    * */
    public void loginUser() {
        if (isValid()) {
            if (Internet.hasInternet(this)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("username", userName.getText().toString());
                params.put("password", password.getText().toString());
                params.put("device_token", new SessionHandler(this).getGCMKey());
                params.put("device", "android");
                params.put("latitude", "" + latitude);
                params.put("longitude", "" + longitude);
                //params.put("active_profile", "" + longitude);
                //Log.d(Config.TAG, params.toString());
                APIHandler apiCall = new APIHandler(this, HTTPMethods.POST, this, params);
                apiCall.execute(Config.LOGIN_URL + "?expand=profile", "");
            } else
                Alert("Alert!", getResources().getString(R.string.no_internet));
        }
    }

    /* Get web service response and pass the response for parsing */
    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    JSONObject loginResponse = null;

    /* Accept web service response, parsing response and save data to app session */
    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS
                    || outJson.getInt(Key.STATUS) == APIStatus.ACCEPTED) {
                JSONObject dataObj = outJson.getJSONObject(Key.DATA);
                new SessionHandler(this).saveCardExist(false);
                new SessionHandler(this).saveOnlineStatus(false);
                new SessionHandler(this).saveReferralCode(dataObj.getString("referralCode"));
                new SessionHandler(this).saveUserActualType(dataObj.getString(Key.USER_TYPE));
                if (dataObj.getString(Key.USER_TYPE).equals("5")) {
                    loginResponse = new JSONObject(result);
                    showProceedDialog(outJson, outJson.getString(Key.MESSAGE));
                } else {
                    if (dataObj.getString(Key.USER_TYPE).equals("4"))
                        proceedAsDriver(outJson);
                    else proceedAsPassenger(outJson);
                }

            } else if (outJson.getInt(Key.STATUS) == APIStatus.CREATED) {
                Alert("Alert!", outJson.getJSONArray(Key.DATA).getJSONObject(0).getString(Key.MESSAGE));
            } else if (outJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                Alert("Alert!", outJson.getJSONArray(Key.DATA).getJSONObject(0).getString(Key.MESSAGE));
            } else {
                Alert("Alert!", outJson.getJSONArray(Key.DATA).getJSONObject(0).getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

            if (outJson.getInt(Key.STATUS) == APIStatus.ACCEPTED) {
                startActivity(new Intent(getApplicationContext(), ChangePassword.class).putExtra("isTemp", password.getText().toString()));
                Login.this.finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else goToHome("4");
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
            if (outJson.getInt(Key.STATUS) == APIStatus.ACCEPTED) {
                startActivity(new Intent(getApplicationContext(), ChangePassword.class).putExtra("isTemp", password.getText().toString()));
                Login.this.finish();
            } else goToHome("3");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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

    private void showProceedDialog(final JSONObject outJson, String message) {
        final AlertDialog mDialog = new AlertDialog(Login.this, false);
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

    private void showRideDialog(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(Login.this, false);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setCancelOnTouchOutside(false);
        mDialog.setPositiveButton("Ignore", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.setNegativeButton(getResources().getString(R.string.proceed_as_passenger), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                proceedAsPassenger(loginResponse);
            }
        });
        mDialog.show();
    }

    private void goToHome(String userType) {
        LocationService.isTimeZoneChanged = true;
        new SessionHandler(this).saveUserType(userType);
        Intent intent;
        if (userType.equals("3"))
            intent = new Intent(getApplicationContext(), PassengerHome.class);
        else
            intent = new Intent(getApplicationContext(), DriverHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Login.this.finish();
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
            /*if(permissionsNeeded.size() > 0) {
                // Need Rationale
				String message = "You need to grant access to " + permissionsNeeded.get(0);
				for (int i = 1; i < permissionsNeeded.size(); i++)
					message = message + ", " + permissionsNeeded.get(i);
				showMessageOKCancel(message,
						new DialogInterface.OnClickListener() {
							@TargetApi(Build.VERSION_CODES.M)
							@Override
							public void onClick(DialogInterface dialog, int which) {
								requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
										REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
							}
						});
				return;
			}*/
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

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(Login.this, true);
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

    private boolean isValid() {
        if (userName.getText().toString().trim().length() == 0) {
            Alert("Oops !!!", "Please enter username.");
            return false;
        } else if (password.getText().toString().trim().length() < 6) {
            Alert("Oops !!!", "Please enter password. Password should be minimum at least 6 character.");
            return false;
        }
        return true;
    }
}
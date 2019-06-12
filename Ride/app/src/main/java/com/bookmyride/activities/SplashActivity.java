package com.bookmyride.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.bookmyride.R;
import com.bookmyride.api.Config;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.fcm.AlarmReceiver;
import com.bookmyride.fcm.NotificationFilters;
import com.bookmyride.fcm.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class SplashActivity extends AppCompatActivity {
    SessionHandler mSession;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public void fullScreenCall() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        fullScreenCall();
        init();
        if (AlarmReceiver.timer != null) {
            AlarmReceiver.timer.cancel();
        }
        /*if(getIntent().hasExtra("rideData")) {
                showCancelledMessage();
        }*/
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    if (mSession.getUserType().equals("4")) {
                        String status = "";
                        String msg = "";
                        String type = "";
                        String confirmation = "";
                        try {
                            JSONObject obj = new JSONObject(intent.getStringExtra("rideData"));
                            if (obj.has("status"))
                                status = obj.getString("status");
                            if (obj.has("message"))
                                msg = obj.getString("message");
                            if (obj.has("type"))
                                type = obj.getString("type");
                            if (obj.has("confirmation"))
                                confirmation = obj.getString("confirmation");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (status.equals("202") || status.equals("204")) {
                            final View layout = getLayoutInflater().inflate(R.layout.customtoast, null);
                            TextView message = (TextView) layout.findViewById(R.id.message);
                            message.setText(msg);
                            TextView title = (TextView) layout.findViewById(R.id.title);
                            title.setText(getResources().getString(R.string.app_name));
                            final Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
                            toast.setView(layout);
                            toast.show();
                        } else if (status.equals("302")) {
                            startActivity(new Intent(context, NotificationDialogs.class)
                                    .putExtra("rideData", intent.getSerializableExtra("rideData")));
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        } else if (type.equals("1") && confirmation.equals("driver")) {
                            //Handle confirmation notification by driver
                            startActivity(new Intent(context, Confirmation.class)
                                    .putExtra("rideData", intent.getSerializableExtra("rideData")));
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        } else /*if (type.equals("1"))*/ {
                            try {
                                JSONObject rideData = new JSONObject(intent.getStringExtra("rideData"));
                                String bookingType = rideData.getString("type");
                                String driver_id = "";
                                if (rideData.has("rideData"))
                                    driver_id = rideData.getString("driver_id");
                                if (bookingType.equals("1") && !driver_id.equals("")) {
                                    startActivity(new Intent(context, NotificationDialogs.class)
                                            .putExtra("rideData", intent.getSerializableExtra("rideData")));
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                } else if (rideData.has("driverDetail")) {
                                    String driverDetail = rideData.get("driverDetail").toString();
                                    if (!driverDetail.equals("null") && !driverDetail.equals("")) {
                                        try {
                                            JSONObject obj = new JSONObject(driverDetail);
                                            String driverID = obj.getString("id");
                                            startActivity(new Intent(context, RideCancelledDialog.class)
                                                    .putExtra("message", rideData.getString("message")));
                                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        if (status.equalsIgnoreCase("") || status.equalsIgnoreCase("0")) {
                                            startActivity(new Intent(context, RideRequestList.class)
                                                    .putExtra("rideData", intent.getSerializableExtra("rideData")));
                                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        }
                                    }
                                } else {
                                    if (status.equalsIgnoreCase("") || status.equalsIgnoreCase("0")) {
                                        startActivity(new Intent(context, RideRequestList.class)
                                                .putExtra("rideData", intent.getSerializableExtra("rideData")));
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        String paymentStatus = "";
                        String status = "";
                        String type = "";
                        String bookingID = "";
                        String driverCat = "";
                        String confirmation = "";
                        String asap = "";
                        try {
                            JSONObject obj = new JSONObject(intent.getStringExtra("rideData"));
                            if (obj.has("status"))
                                status = obj.getString("status");
                            if (obj.has("type")) {
                                type = obj.getString("type");
                            }
                            if (obj.has("bookingId"))
                                bookingID = obj.getString("bookingId");
                            if (obj.has("driverCategory_id"))
                                driverCat = obj.getString("driverCategory_id");
                            if (obj.has("confirmation"))
                                confirmation = obj.getString("confirmation");
                            if (obj.has("asap_pickup"))
                                asap = obj.getString("asap_pickup");
                            if (type.equalsIgnoreCase("1") && confirmation.equals("passenger")) {
                                //Handle ride Confirmation notification by passenger
                                startActivity(new Intent(context, Confirmation.class)
                                        .putExtra("rideData", intent.getSerializableExtra("rideData")));
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            } else if (type.equalsIgnoreCase("0") && !status.equals("204")) {
                                if (BookingTimer.timer != null) {
                                    BookingTimer.timer.finish();
                                }
                                Intent trackIntent = new Intent(SplashActivity.this, TrackDetail.class);
                                trackIntent.putExtra("status", status);
                                trackIntent.putExtra("bookingID", bookingID);
                                trackIntent.putExtra("driverCategory", driverCat);
                                trackIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(trackIntent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            } else if (type.equalsIgnoreCase("1") && asap.equals("1") && !status.equals("204")) {
                                if (BookingTimer.timer != null) {
                                    BookingTimer.timer.finish();
                                }
                                Intent trackIntent = new Intent(SplashActivity.this, TrackDetail.class);
                                trackIntent.putExtra("status", status);
                                trackIntent.putExtra("bookingID", bookingID);
                                trackIntent.putExtra("driverCategory", driverCat);
                                trackIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(trackIntent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            } else if (type.equalsIgnoreCase("1") && asap.equals("0") && !status.equals("204")) {
                                if (BookingTimer.timer != null) {
                                    BookingTimer.timer.finish();
                                }
                                startActivity(new Intent(getApplicationContext(), RideCancelledDialog.class)
                                        .putExtra("message", obj.getString("message")));
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                return;
                            } else if (status.equalsIgnoreCase("3") || status.equalsIgnoreCase("4")) {
                                Intent intent2 = new Intent(SplashActivity.this, TrackDetail.class);
                                intent2.putExtra("status", status);
                                intent2.putExtra("bookingID", bookingID);
                                intent2.putExtra("driverCategory", driverCat);
                                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent2);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            } else if (status.equalsIgnoreCase("5") || status.equalsIgnoreCase("6")) {
                                Intent intent3 = new Intent(SplashActivity.this, FareBreakup.class);
                                intent3.putExtra("status", status);
                                intent3.putExtra("bookingID", bookingID);
                                intent3.putExtra("driverCategory", driverCat);
                                intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent3);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            } else if (status.equals("10")) {
                                paymentStatus = obj.get("paymentStatus").toString();
                                if (paymentStatus.equals("0")) {
                                    Intent intent3 = new Intent(SplashActivity.this, FareBreakup.class);
                                    intent3.putExtra("status", status);
                                    intent3.putExtra("bookingID", bookingID);
                                    intent3.putExtra("driverCategory", driverCat);
                                    intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent3);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                } else {
                                    Intent intent4 = new Intent(SplashActivity.this, FareBreakdown.class);
                                    intent4.putExtra("status", status);
                                    intent4.putExtra("bookingID", bookingID);
                                    if (obj.has("paymentStatus"))
                                        intent4.putExtra("type", obj.get("paymentStatus").toString());
                                    intent4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent4);
                                }
                            }
                            /*startActivity(new Intent(context, NotificationDialogs.class)
                                    .putExtra("rideData", intent.getSerializableExtra("rideData")));*/
                            if (confirmation.equals("")) {
                                final View layout = getLayoutInflater().inflate(R.layout.customtoast, null);
                                TextView message = (TextView) layout.findViewById(R.id.message);
                                message.setText(obj.getString("message"));
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
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //}
                    }
                } else if (intent.getAction().equals(NotificationFilters.AVAILABLE_BOOKINGS)) {
                    if (mSession.getUserType().equals("4")) {
                        String ridesData = intent.getStringExtra("bookings_list");
                        String serverDateTime = intent.getStringExtra("server_time");
                        String serverTimeZone = intent.getStringExtra("server_timezone");
                        String serverTimeZoneOffset = intent.getStringExtra("server_timezone_offset");
                        //handlingTimeZone(serverDateTime, serverTimeZoneOffset, serverTimeZone);
                    }
                }
            }
            //proceed();
        };

    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    private String getOffset() {
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        return "" + mGMTOffset;
    }

    private void handlingTimeZone(String serverdateTime, String serverTimeZoneOffset, String serverTimeZone) {
        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = calendar.getTimeZone();
        String deviceTimeZone = timeZone.getID();
        String deviceTimeZoneOffset = getOffset();
        Log.e("deviceoffset", deviceTimeZoneOffset);
        Log.e("serverTimeZoneOffset", serverTimeZoneOffset);
        if (!deviceTimeZoneOffset.startsWith(serverTimeZoneOffset) &&
                !NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            showToast("Please check if your time zone is correct.\n" +
                    "Server Time Zone: " + serverTimeZone + "\n" +
                    "Server Time: " + serverdateTime + "\n" +
                    "Device Time Zone: " + timeZone.getID() + "\n" +
                    "Device Time: " + getCurrentDateTime());
        }
        /*    //Log.e("equal", "true");
            dateTimePrompt("Alert!", "Your device time zone is not accurate." +
                    "\nServer Time Zone: " + serverTimeZone +
                    "\nDevice Time Zone: " + deviceTimeZone +
                    "\nPlease change it.");
        } else if (deviceTimeZoneOffset.startsWith(serverTimeZoneOffset) && isDateTimePromptShowing) {
            if (dateTimeDialog != null && dateTimeDialog.isShowing())
                dateTimeDialog.dismiss();
            isDateTimePromptShowing = false;
        }*/
    }

    Toast toast;

    private void showToast(String msg) {
        final View layout = getLayoutInflater().inflate(R.layout.customtoast, null);
        TextView message = (TextView) layout.findViewById(R.id.message);
        message.setText(msg);
        TextView title = (TextView) layout.findViewById(R.id.title);
        title.setText(getResources().getString(R.string.app_name));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isToastShown()) {
                    toast = new Toast(SplashActivity.this);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
                    toast.setView(layout);
                    toast.show();
                }
            }
        }, 300);
    }

    private boolean isToastShown() {
        boolean isShown = false;
        if (toast != null) {
            try {
                if (toast.getView().isShown())
                    isShown = true;
                else
                    isShown = false;
            } catch (Exception e) {         // invisible if exception
                e.printStackTrace();
                isShown = false;
            }
        }
        return isShown;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isGPSEnable())
            proceed();
        else
            showAlert("Location Service", "GPS is not active. Do you want to enable GPS?");
    }

    private void init() {
        mSession = new SessionHandler(this);
    }

    AsyncTask splash = null;

    @SuppressLint("StaticFieldLeak")
    private void proceed() {
        mSession.saveSessionCode("");
        splash = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (mSession.getToken().equals("")) {
                    startActivity(new Intent(getApplicationContext(), SignIn.class));
                } else if (mSession.getUserType().equals("4")) {
                    Intent intent = new Intent(getApplicationContext(), DriverHome.class);
                    if (getIntent().hasExtra("rideData"))
                        intent.putExtra("rideData", getIntent().getStringExtra("rideData"));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), PassengerHome.class);
                    if (getIntent().hasExtra("rideData"))
                        intent.putExtra("rideData", getIntent().getStringExtra("rideData"));
                    if (getIntent().hasExtra("bookingId"))
                        intent.putExtra("bookingId", getIntent().getStringExtra("bookingId"));
                    startActivity(intent);
                }
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                SplashActivity.this.finish();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                for (int i = 0; i < 4; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(NotificationFilters.AVAILABLE_BOOKINGS));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        if (splash != null) {
            if (splash.getStatus() == AsyncTask.Status.RUNNING)
                splash.cancel(true);
        }
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void showAlert(String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(SplashActivity.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setNegativeButton(getResources().getString(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 1);
                mDialog.dismiss();

            }
        });
        mDialog.setPositiveButton(getResources().getString(R.string.no), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplashActivity.this.finish();
                mDialog.dismiss();

            }
        });
        mDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            switch (requestCode) {
                case 1:
                    if (isGPSEnable())
                        proceed();
                    break;
            }
        }
    }

    private boolean isGPSEnable() {
        LocationManager locationManager;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGPSEnabled;
    }

    private void showCancelledMessage() {
        Intent intent = new Intent(this, NotificationDialogs.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("rideData", getIntent().getStringExtra("rideData"));
        startActivity(intent);
    }
}
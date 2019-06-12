package com.bookmyride.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bookmyride.R;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.fcm.NotificationFilters;
import com.bookmyride.fcm.NotificationUtils;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vinod on 2017-01-07.
 */
public class Waiting extends AppCompatActivity implements AsyncTaskCompleteListener {
    TextView home, checkStatus;
    SessionHandler session;
    BroadcastReceiver mReceiver;
    String bookingID;
    RelativeLayout layFare;
    ImageView icon;
    TextView totalFare, distance, duration, waitTime, ok;
    String driverCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        init();
        if (getIntent().hasExtra("bookingID"))
            bookingID = getIntent().getStringExtra("bookingID");
        else
            bookingID = session.getCurrentBookingId();
        //if(getIntent().hasExtra("total"))
        //    appendData();
        getRideDetail();
    }

    private void getRideDetail() {
        type = 0;
        String endPoint = Config.BOOKING_LIST + "/" + bookingID;
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this, null);
            apiHandler.execute(endPoint, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void appendData(String total, String dur, String dist, String wait_time) {
        layFare.setVisibility(View.VISIBLE);
        driverCategory = getIntent().getStringExtra("driverCategory");
        if (driverCategory.equals("1"))
            icon.setImageResource(R.drawable.fair_taxi_circle_back);
        else if (driverCategory.equals("2"))
            icon.setImageResource(R.drawable.fair_economy_circle_back);
        else if (driverCategory.equals("3"))
            icon.setImageResource(R.drawable.fair_premium_circle_back);
        else if (driverCategory.equals("4"))
            icon.setImageResource(R.drawable.fair_motor_circle_back);

        /* totalFare.setText(getIntent().getStringExtra("total"));
        duration.setText(getDurationString(Integer.parseInt(getIntent().getStringExtra("duration"))));
        distance.setText(meterToKM(getIntent().getStringExtra("distance")));
        waitTime.setText(getIntent().getStringExtra("waitTime")+ " sec"); */
        totalFare.setText(total);
        duration.setText(secondsToMin(dur));
        distance.setText(meterToKM(dist));
        waitTime.setText(wait_time + " Sec");
    }

    private String meterToKM(String meters) {
        double actMeters = Double.parseDouble(meters);
        double km = actMeters / 1000;
        return km + " KM";
    }

    private String getDurationString(int seconds) {
        //int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;
        return /*twoDigitString(hours) + " : " + */twoDigitString(minutes) + ":" + twoDigitString(seconds);
    }

    private String twoDigitString(int number) {
        if (number == 0) {
            return "00";
        }
        if (number / 10 == 0) {
            return "0" + number;
        }
        return String.valueOf(number);
    }

    private void init() {
        session = new SessionHandler(this);
        home = (TextView) findViewById(R.id.home);
        icon = (ImageView) findViewById(R.id.icon);
        checkStatus = (TextView) findViewById(R.id.checkstatus);
        layFare = (RelativeLayout) findViewById(R.id.lay_fare);
        totalFare = (TextView) findViewById(R.id.total_fare);
        distance = (TextView) findViewById(R.id.distance);
        duration = (TextView) findViewById(R.id.duration);
        waitTime = (TextView) findViewById(R.id.wait_time);
        ok = (TextView) findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layFare.setVisibility(View.GONE);
            }
        });
        checkStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPaymentStatus();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHome();
            }
        });
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NotificationFilters.PAYMENT_REQEUST)) {
                    if (session.getUserType().equals("4")) {
                        String status = "";
                        String msg = "";
                        try {
                            JSONObject obj = new JSONObject(intent.getStringExtra("rideData"));
                            if (obj.has("status"))
                                status = obj.getString("status");
                            if (obj.has("message"))
                                msg = obj.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (status.equals("202")) {
                            final View layout = getLayoutInflater().inflate(R.layout.customtoast, null);
                            TextView message = (TextView) layout.findViewById(R.id.message);
                            if (msg.equals(""))
                                message.setText("Payment Successfully done.");
                            else
                                message.setText(msg);
                            TextView title = (TextView) layout.findViewById(R.id.title);
                            title.setText(getResources().getString(R.string.app_name));

                            final Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
                            toast.setView(layout);
                            toast.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    /*Intent intent2 = new Intent(getApplicationContext(), RatingToPassenger.class);
                                    intent2.putExtra("bookingId", bookingID);
                                    intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent2);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);*/
                                    Intent intent = new Intent(getApplicationContext(), FareBreakdown.class);
                                    //intent.putExtra("type", bookingType);
                                    intent.putExtra("bookingID", bookingID);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    session.saveWaitingInfo(0L, 0L, 0L, 0L, 0, 0);
                                    Waiting.this.finish();
                                }
                            }, 300);
                        }
                    }
                }
            }
        };
    }

    public void onBack(View view) {
        goToHome();
    }

    private void goToHome() {
        String userType = new SessionHandler(this).getUserType();
        Intent intent;
        if (userType.equals("3"))
            intent = new Intent(getApplicationContext(), PassengerHome.class);
        else
            intent = new Intent(getApplicationContext(), DriverHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Waiting.this.finish();
    }

    int type = 0;

    public void checkPaymentStatus() {
        type = 1;
        if (Internet.hasInternet(this)) {
            APIHandler apiCall = new APIHandler(this, HTTPMethods.GET, this, null);
            apiCall.execute(Config.PAYMENT_BY_CARD + "/" + bookingID, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    String status = "";

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                if (type == 0) {
                    JSONObject obj = outJson.getJSONObject("data");
                    String fareDetail = obj.getString("fareDetail");
                    if (!fareDetail.equals("") && !fareDetail.equals("null")) {
                        JSONObject fareObj = new JSONObject(fareDetail);
                        String currency = fareObj.get("currency").toString();
                        String waitTime = obj.get("waitTime").toString();
                        String duration = obj.getString("duration");
                        String distance = obj.get("distance").toString();
                        String totalAmount = currency + fareObj.getString("total");
                        appendData(totalAmount, duration, distance, waitTime);
                    }
                } else {
                    JSONArray dataArray = outJson.getJSONArray(Key.DATA);
                    status = dataArray.get(0).toString();
                    if (status.equals("1")) {
                        Alert("Success!", outJson.getString(Key.MESSAGE));
                    } else {
                        Alert("Alert!", outJson.getString(Key.MESSAGE));
                    }
                }
            } else {
                Alert("Alert!", outJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String secondsToMin(String seconds) {
        /*double actMeters = Double.parseDouble(seconds);
        double min = actMeters / 60;
        String duration = new DecimalFormat("##.##").format(min);
        return duration + " Min";*/
        int second = Integer.parseInt(seconds);
        int hours = second / 3600;
        int minutes = (second % 3600) / 60;
        int secs = second % 60;

        //return String.format("%02d:%02d:%02d", hours, minutes, secs);
        if(hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        else
            return String.format("%02d:%02d", minutes, secs) + " Min";
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(Waiting.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (status.equals("1")) {
                    goHome();
                }
            }
        });
        mDialog.show();
    }

    private void goHome() {
        Intent intent = new Intent(getApplicationContext(), DriverHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Waiting.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(NotificationFilters.PAYMENT_REQEUST));

        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
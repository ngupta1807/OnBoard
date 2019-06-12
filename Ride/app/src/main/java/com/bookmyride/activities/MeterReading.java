package com.bookmyride.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
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
import com.bookmyride.fragments.AcceptPayment;
import com.bookmyride.views.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * Created by vinod on 1/20/2017.
 */
public class MeterReading extends AppCompatActivity implements AsyncTaskCompleteListener {
    EditText meterReading;
    SessionHandler session;
    TextView submit;
    RelativeLayout layFare;
    TextView totalFare, distance, duration, waitTime, ok;
    BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meter_reading);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        init();
    }

    public void onBack(View view) {
        endRide(1);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    String bookingID = "";
    String bookingType = "";
    String driverCategory = "";
    String waitSec = "";
    String dist, dur, bookedBy = "";

    private void init() {
        session = new SessionHandler(this);

        bookingID = getIntent().getStringExtra("bookingId");
        bookingType = getIntent().getStringExtra("type");
        driverCategory = getIntent().getStringExtra("driverCategory");
        waitSec = getIntent().getStringExtra("waitTime");
        dist = getIntent().getStringExtra("distance");
        dur = getIntent().getStringExtra("duration");
        bookedBy = getIntent().getStringExtra("bookedBy");

        meterReading = (EditText) findViewById(R.id.meter_reading);
        meterReading.addTextChangedListener(new TextWatcher() {
            DecimalFormat dec = new DecimalFormat("0.00");

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().matches("^\\$(\\d{1,3}(\\,\\d{3})*|(\\d+))(\\.\\d{2})?$")) {
                    String userInput = "" + s.toString().replaceAll("[^\\d]", "");
                    if (userInput.length() > 0) {
                        Float in = Float.parseFloat(userInput);
                        float percen = in / 100;
                        meterReading.setText("$" + dec.format(percen));
                        meterReading.setSelection(meterReading.getText().length());
                    }
                }
            }
        });
        submit = (TextView) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Float.parseFloat(meterReading.getText().toString().substring(1)) > 0) {
                    String msg = "You have entered "
                            + meterReading.getText().toString()
                            + " as your total taxi metre cost. Click on OK to confirm or BACK to change the total.";
                    prompt("Alert!", msg);
                } else Alert("Alert!", "Please enter total fare.");
            }
        });
        layFare = (RelativeLayout) findViewById(R.id.lay_fare);
        totalFare = (TextView) findViewById(R.id.total_fare);
        distance = (TextView) findViewById(R.id.distance);
        duration = (TextView) findViewById(R.id.duration);
        waitTime = (TextView) findViewById(R.id.wait_time);
        ok = (TextView) findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (getIntent().getStringExtra("type").equals("1")) {
                    intent = new Intent(MeterReading.this, FareBreakdown.class);
                    intent.putExtra("bookingID", getIntent().getStringExtra("bookingId"));
                } else {
                    intent = new Intent(MeterReading.this, Waiting.class);
                    intent.putExtra("driverCategory", getIntent().getStringExtra("driverCategory"));
                    intent.putExtra("bookingId", getIntent().getStringExtra("bookingId"));
                }
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                MeterReading.this.finish();
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
                                    intent.putExtra("type", bookingType);
                                    intent.putExtra("bookingID", bookingID);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    session.saveWaitingInfo(0L, 0L, 0L, 0L, 0, 0);
                                    MeterReading.this.finish();
                                }
                            }, 300);
                        }
                    }
                }
            }
        };
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(MeterReading.this, true);
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

    private void success(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(MeterReading.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        proceedAlternatePayment();
                    }
                });
        mDialog.show();
    }

    private void prompt(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(MeterReading.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setNegativeButton(getResources().getString(R.string.ok),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        endRide(0);
                    }
                });
        mDialog.setPositiveButton("BACK",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();
                    }
                });
        mDialog.show();
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

    private void endRide(int type) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("status", "5");
        jsonParams.put("waitTime", getIntent().getStringExtra("waitTime"));
        jsonParams.put("dropOff", getIntent().getStringExtra("dropOff"));
        jsonParams.put("distance", getIntent().getStringExtra("distance"));
        jsonParams.put("duration", getIntent().getStringExtra("duration"));
        if (type == 0) {
            String reading = meterReading.getText().toString();
            reading = reading.substring(1, reading.length());
            jsonParams.put("meterFare", reading);
        }
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.PUT, this, jsonParams);
            apiHandler.execute(Config.BOOKING_STATUS + getIntent().getStringExtra("bookingId"), session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void proceedAlternatePayment() {
        Intent intent = new Intent(MeterReading.this, FareBreakdown.class);
        intent.putExtra("type", bookingType);
        intent.putExtra("bookingID", getIntent().getStringExtra("bookingId"));
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        session.saveWaitingInfo(0L, 0L, 0L, 0L, 0, 0);
        MeterReading.this.finish();
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                JSONObject datObj = outJson.getJSONObject(Key.DATA);
                String paymentStatus = datObj.getString("paymentStatus");
                String paymentMessage = datObj.getString("payment_message");

                String checkTaxiOperator = datObj.getString("checkTaxiOperator");
                String cardStatus = datObj.getString("cardStatus");
                String cardDetail = datObj.get("cardDetail").toString();
                if (paymentStatus.equals("1")) {
                    proceedAlternatePayment();
                } else if (checkTaxiOperator.equals("1")) {
                    Intent intent = new Intent(MeterReading.this, AcceptPayment.class);
                    intent.putExtra("type", bookingType);
                    intent.putExtra("cardStatus", cardStatus);
                    intent.putExtra("cardDetail", cardDetail);
                    intent.putExtra("bookingID", getIntent().getStringExtra("bookingId"));
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    MeterReading.this.finish();
                } else {
                    success("Alert!", paymentMessage);
                }
                /*else if (paymentStatus.equals("0") && !paymentMessage.equals("") && !checkTaxiOperator.equals("1")) {
                    paymentFailureAlert("Alert!", paymentMessage);
                } else {
                    JSONObject fareDetail = outJson.getJSONObject(Key.DATA).getJSONObject("fareDetail");
                    layFare.setVisibility(View.VISIBLE);
                    totalFare.setText(fareDetail.getString("currency")+fareDetail.getString("total"));
                    duration.setText(getIntent().getStringExtra("duration"));
                    distance.setText(getIntent().getStringExtra("distance"));
                    waitTime.setText(getIntent().getStringExtra("waitTime"));
                    Intent intent;
                    if (getIntent().getStringExtra("type").equals("1")) {
                        //intent = new Intent(MeterReading.this, FareBreakdown.class);
                        intent = new Intent(MeterReading.this, AcceptPayment.class);
                        intent.putExtra("type", bookingType);
                        intent.putExtra("cardStatus", cardStatus);
                        intent.putExtra("cardDetail", cardDetail);
                        intent.putExtra("bookingID", getIntent().getStringExtra("bookingId"));
                    } else {
                        intent = new Intent(MeterReading.this, Waiting.class);
                        intent.putExtra("driverCategory", getIntent().getStringExtra("driverCategory"));
                        intent.putExtra("bookingId", getIntent().getStringExtra("bookingId"));
                        intent.putExtra("total", fareDetail.getString("currency") + fareDetail.getString("total"));
                        intent.putExtra("duration", getIntent().getStringExtra("duration"));
                        intent.putExtra("distance", getIntent().getStringExtra("distance"));
                        intent.putExtra("waitTime", getIntent().getStringExtra("waitTime"));
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    MeterReading.this.finish();
                }*/
            } else {
                Alert("Alert!", outJson.getJSONArray(Key.DATA).getJSONObject(0).getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Success(String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(MeterReading.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), DriverHome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                MeterReading.this.finish();
            }
        });
        mDialog.show();
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
        MeterReading.this.finish();
    }

    private void paymentFailureAlert(String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(MeterReading.this, false);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setCancelOnTouchOutside(false);
        mDialog.setNegativeButton("Alternate Payment", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent = new Intent(MeterReading.this, AcceptPayment.class);
                intent.putExtra("type", bookingType);
                intent.putExtra("bookingID", getIntent().getStringExtra("bookingId"));
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                session.saveWaitingInfo(0L, 0L, 0L, 0L, 0, 0);
                MeterReading.this.finish();
            }
        });
        if (bookedBy.equals("0")) {
            mDialog.setPositiveButton("Accept by Passenger", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    Intent intent = new Intent(MeterReading.this, Waiting.class);
                    intent.putExtra("bookingID", bookingID);
                    intent.putExtra("total", "");
                    intent.putExtra("duration", dur);
                    intent.putExtra("distance", dist);
                    intent.putExtra("waitTime", waitSec);
                    intent.putExtra("driverCategory", driverCategory);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    session.saveWaitingInfo(0L, 0L, 0L, 0L, 0, 0);
                    MeterReading.this.finish();
                }
            });
        }
        mDialog.show();
    }
}

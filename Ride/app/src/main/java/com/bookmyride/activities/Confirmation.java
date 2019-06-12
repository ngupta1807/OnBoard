package com.bookmyride.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;
import com.bookmyride.R;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by vinod on 11/10/2017.
 */
public class Confirmation extends AppCompatActivity implements View.OnClickListener {
    TextView title, message, confirm, cancel, time, puAddress, doAddress, rideID;
    SessionHandler session;
    String bookingID;
    String userType = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_confirmation);
        init();
        if (getIntent().hasExtra("rideData"))
            appendData();
    }

    private void init() {
        session = new SessionHandler(this);
        userType = session.getUserType();
        confirm = (TextView) findViewById(R.id.confirm);
        confirm.setOnClickListener(this);
        message = (TextView) findViewById(R.id.msg);
        title = (TextView) findViewById(R.id.title);
        time = (TextView) findViewById(R.id.time);
        puAddress = (TextView) findViewById(R.id.pu_address);
        doAddress = (TextView) findViewById(R.id.do_address);
        rideID = (TextView) findViewById(R.id.ride_id);
        cancel = (TextView) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
    }

    private String msg, status = "";
    String pickUP, dropOff, driverCategory, bookingType, pickUpDate = "";

    private void appendData() {
        Intent intent = getIntent();
        try {
            JSONObject obj = new JSONObject(intent.getStringExtra("rideData"));
            msg = obj.getString("message");
            if (obj.has("status"))
                status = obj.getString("status");
            if (obj.has("bookingId"))
                bookingID = obj.getString("bookingId");
            if (obj.has("pickUp"))
                pickUP = obj.getString("pickUp");
            if (obj.has("dropOff"))
                dropOff = obj.getString("dropOff");
            if (obj.has("driverCategory_id"))
                driverCategory = obj.getString("driverCategory_id");
            if (obj.has("type"))
                bookingType = obj.getString("type");
            if (obj.has("pickUpDate"))
                pickUpDate = obj.getString("pickUpDate");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        title.setText("Confirmation!");
        rideID.setText("RIDE_ID: "+ bookingID);
        message.setText(msg);
        String puAddres = "";
        String doAddres = "";
        try {
            JSONObject jobj = new JSONObject(pickUP);
            puAddres = jobj.getString("address");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jobj = new JSONObject(dropOff);
            if (jobj.getString("address").equals("") || jobj.getString("address").equals("null"))
                doAddres = "Not Selected";
            else
                doAddres = jobj.getString("address");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String pickDT = "<b>Pick Up Date and Time:</b> " + pickUpDate;
        String pickAddress = "<b>Pick Up Location:</b> " + puAddres;
        String dropAddress = "<b>Drop Off Location:</b> " + doAddres;
        time.setText(pickUpDate);
        puAddress.setText(puAddres);
        doAddress.setText(doAddres);
        if (userType.equals("4")) {
            confirm.setText("OK");
            cancel.setText("CANCEL");
        }
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                showCancelRideDialog();
                break;
            case R.id.confirm:
                confirm();
                break;
        }
    }

    com.bookmyride.views.AlertDialog mDialog;

    private void showCancelRideDialog() {
        mDialog = new com.bookmyride.views.AlertDialog(Confirmation.this, false);
        mDialog.setDialogTitle("Alert!");
        mDialog.setDialogMessage(getResources().getString(R.string.cancel_ride_alert));
        mDialog.setNegativeButton("CHECK FEES", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (Internet.hasInternet(Confirmation.this)) {
                    getCancellationCharges();
                } else {
                    Alert("Alert!", getResources().getString(R.string.no_internet));
                }
            }
        });
        mDialog.setPositiveButton("BACK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void Alert(String title, String alert) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(Confirmation.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void getCancellationCharges() {
        APIHandler mRequest = new APIHandler(Confirmation.this, HTTPMethods.PUT, new AsyncTaskCompleteListener() {
            @Override
            public void onTaskComplete(String result) {
                try {
                    JSONObject outJson = new JSONObject(result);
                    if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                        //String cancellationCharges = outJson.getJSONObject(Key.DATA).getString("cancellationCharges");
                        //String msg = "You might be charged $" + cancellationCharges + " as ride cancellation fee.";
                        cancellationPrompt(outJson.getString(Key.MESSAGE), "CANCEL & PAY");
                    } else if (outJson.getInt(Key.STATUS) == APIStatus.GUARANTEE_AVAILABLE) {
                        cancellationPrompt(outJson.getString(Key.MESSAGE), "CANCEL RIDE");
                    } else
                        Alert("Alert!", outJson.getString(Key.MESSAGE));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        mRequest.execute(Config.BOOKING_CHARGES + bookingID, session.getToken());
    }

    private void cancellationPrompt(String msg, String action) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(Confirmation.this, true);
        mDialog.setDialogTitle("Alert!");
        mDialog.setDialogMessage(msg);
        mDialog.setNegativeButton(action, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (userType.equals("4"))
                    declinedByDriver();
                else if (userType.equals("3"))
                    cancelRide();
            }
        });
        mDialog.setPositiveButton("BACK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void cancelRide() {
        if (Internet.hasInternet(Confirmation.this)) {
            APIHandler mRequest = new APIHandler(Confirmation.this, HTTPMethods.PUT, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    try {
                        JSONObject outJson = new JSONObject(result);
                        if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS ||
                                outJson.getInt(Key.STATUS) == APIStatus.GUARANTEE_AVAILABLE) {
                            final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(Confirmation.this, true);
                            mDialog.setDialogTitle("Success!");
                            mDialog.setDialogMessage(outJson.getString(Key.MESSAGE));
                            mDialog.setPositiveButton(getResources().getString(R.string.timer_label_alert_ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Confirmation.this.finish();
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                }
                            });
                            mDialog.show();
                        } else
                            Alert("Alert!", outJson.getString(Key.MESSAGE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
            mRequest.execute(Config.CANCEL_BOOKING + bookingID, session.getToken());
        } else {
            Alert("Alert!", getResources().getString(R.string.no_internet));
        }
    }

    private void declinedByDriver() {
        if (Internet.hasInternet(Confirmation.this)) {
            APIHandler mRequest = new APIHandler(Confirmation.this, HTTPMethods.PUT, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    try {
                        JSONObject outJson = new JSONObject(result);
                        if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS ||
                                outJson.getInt(Key.STATUS) == APIStatus.GUARANTEE_AVAILABLE) {
                            final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(Confirmation.this, true);
                            mDialog.setDialogTitle("Alert!");
                            mDialog.setDialogMessage(outJson.getString(Key.MESSAGE));
                            mDialog.setPositiveButton(getResources().getString(R.string.timer_label_alert_ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Confirmation.this.finish();
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                }
                            });
                            mDialog.show();
                        } else
                            Alert("Alert!", outJson.getString(Key.MESSAGE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
            mRequest.execute(Config.DECLINED_BY_DRIVER + bookingID, session.getToken());
        } else {
            Alert("Alert!", getResources().getString(R.string.no_internet));
        }
    }

    private void confirm() {
        HashMap<String, String> params = new HashMap<>();
        params.put("booking_id", bookingID);
        if (userType.equals("4"))
            params.put("confirmed_by", "driver");
        else if (userType.equals("3"))
            params.put("confirmed_by", "passenger");
        if (Internet.hasInternet(Confirmation.this)) {
            APIHandler mRequest = new APIHandler(Confirmation.this, HTTPMethods.POST, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    try {
                        JSONObject outJson = new JSONObject(result);
                        if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                            final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(Confirmation.this, true);
                            mDialog.setDialogTitle("Success!");
                            mDialog.setDialogMessage(outJson.getString(Key.MESSAGE));
                            mDialog.setPositiveButton(getResources().getString(R.string.timer_label_alert_ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Confirmation.this.finish();
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                }
                            });
                            mDialog.show();
                        } else
                            Alert("Alert!", outJson.getString(Key.MESSAGE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, params);
            mRequest.execute(Config.CONFIRM_BOOKING, session.getToken());
        } else {
            Alert("Alert!", getResources().getString(R.string.no_internet));
        }
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
        Confirmation.this.finish();
    }
}
package com.bookmyride.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bookmyride.views.AlertDialog;
import com.bookmyride.views.RideDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

public class BookingTimer extends AppCompatActivity implements AsyncTaskCompleteListener {

    CircleProgressView mCircleView;
    int seconds = 0;
    private String rideID = "";
    private String userID = "";
    private String sNext_driver_availability = "";
    private String sMessage = "";
    private SessionHandler sessionManager;
    private ImageView Iv_cancelRide;
    private TextView Tv_message;
    private LinearLayout Ll_cancelRide_message;
    private Toast toast;
    private RideDialog mdialog;
    Handler mHandler;
    int count = 0;
    @SuppressLint("StaticFieldLeak")
    public static BookingTimer timer;
    BroadcastReceiver updateReceiver;
    APIHandler mRequest;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                Iv_cancelRide.setVisibility(View.VISIBLE);
                Ll_cancelRide_message.setVisibility(View.GONE);
                final RideDialog mDialog = new RideDialog(BookingTimer.this, false, true);
                mDialog.setDialogTitle("Success!");
                mDialog.setDialogMessage(outJson.getString(Key.MESSAGE));
                mDialog.setPositiveButton(getResources().getString(R.string.timer_label_alert_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        BookingTimer.this.finish();
                    }
                });
                mDialog.show();
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BookingTimer.this.finish();
                    }
                }, 3000);*/
            } else {
                Iv_cancelRide.setVisibility(View.VISIBLE);
                Ll_cancelRide_message.setVisibility(View.GONE);
                Alert("Alert!", outJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_timer);
        timer = BookingTimer.this;
        initialize();

        // Receiving the data from broadcast
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.app.pushNotification.GuaranteeOptionList");
        filter.addAction("com.app.pushNotification.GuaranteeDriver_Decline");
        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals("com.app.pushNotification.GuaranteeOptionList")) {
                    mHandler.removeCallbacks(mRunnable);
                    if (mRequest != null) {
                        mRequest.cancel(true);
                    }
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else if (intent.getAction().equals("com.app.pushNotification.GuaranteeDriver_Decline")) {
                    mHandler.removeCallbacks(mRunnable);
                    if (mRequest != null) {
                        mRequest.cancel(true);
                    }

                    String sMessage = intent.getStringExtra("message");
                    rideID = intent.getStringExtra("RideId");

                    if (sNext_driver_availability.equalsIgnoreCase("1")) {
                        final RideDialog mDialog = new RideDialog(BookingTimer.this, false, true);
                        mDialog.setDialogTitle(getResources().getString(R.string.timer_label_alert_sorry));
                        mDialog.setDialogMessage(sMessage);
                        mDialog.setNegativeButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();

                                if (Internet.hasInternet(BookingTimer.this)) {
                                    GuaranteeJobRequest(Config.guarantee_job_find_driver_url);
                                } else {
                                    Alert("Alert!", getResources().getString(R.string.no_internet));
                                }
                            }
                        });
                        mDialog.setPositiveButton(getResources().getString(R.string.action_cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();

                                if (Internet.hasInternet(BookingTimer.this)) {
                                    GuaranteeDeleteRideRequest(Config.delete_ride_url);
                                } else {
                                    AlertInternetCheck();
                                }
                            }
                        });
                        mDialog.show();
                    } else {
                        final RideDialog mDialog = new RideDialog(BookingTimer.this, false, true);
                        mDialog.setDialogTitle(getResources().getString(R.string.timer_label_alert_sorry));
                        mDialog.setDialogMessage(sMessage);
                        mDialog.setPositiveButton(getResources().getString(R.string.timer_label_alert_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                if (Internet.hasInternet(BookingTimer.this)) {
                                    GuaranteeDeleteRideRequest(Config.delete_ride_url);
                                } else {
                                    AlertInternetCheck();
                                }
                            }
                        });
                        mDialog.show();
                    }
                }
            }
        };
        registerReceiver(updateReceiver, filter);

        Iv_cancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdialog = new RideDialog(BookingTimer.this, false, true);
                mdialog.setDialogTitle(getResources().getString(R.string.cancel_ride));
                mdialog.setDialogMessage(getResources().getString(R.string.cancel_ride_msg));
                mdialog.setNegativeButton(getResources().getString(R.string.timer_label_alert_yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mdialog.dismiss();
                        if (Internet.hasInternet(BookingTimer.this)) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            mCircleView.stopSpinning();
                            cancelRide();
                        } else {
                            toast = Toast.makeText(getBaseContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
                mdialog.setPositiveButton(getResources().getString(R.string.timer_label_alert_no), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mdialog.dismiss();
                    }
                });
                mdialog.show();
            }
        });
    }

    /* Initialization of views and components */
    private void initialize() {
        sessionManager = new SessionHandler(this);
        Iv_cancelRide = (ImageView) findViewById(R.id.guarantee_timer_cancel_ride_image);
        Ll_cancelRide_message = (LinearLayout) findViewById(R.id.guarantee_timer_cancel_ride_layout);
        mCircleView = (CircleProgressView) findViewById(R.id.guarantee_timer_circleView);
        Tv_message = (TextView) findViewById(R.id.guarantee_timer_page_message_textView);

        mCircleView.setEnabled(false);
        mCircleView.setFocusable(false);

        /*HashMap<String, String> userDetail = sessionManager.getUserDetails();
        userID = userDetail.get(SessionManager.KEY_USERID);*/

        Intent intent = getIntent();
        seconds = Integer.parseInt(intent.getStringExtra("Response_time")) + 1;
        rideID = intent.getStringExtra("RideID");
        userID = intent.getStringExtra("UserID");
        sNext_driver_availability = intent.getStringExtra("Next_driver_availability");
        sMessage = intent.getStringExtra("Message");

        Tv_message.setText(getResources().getString(R.string.timer_label));

        //value setting
        mCircleView.setMaxValue(seconds);
        mCircleView.setValueAnimated(0);

        //show unit
        // mCircleView.setUnit("");
        // mCircleView.setShowUnit(true);

        //text sizes
        mCircleView.setTextSize(50);
        // mCircleView.setUnitSize(40); // if i set the text size i also have to set the unit size

        // enable auto text size, previous values are overwritten
        mCircleView.setAutoTextSize(true);

        //if you want the calculated text sizes to be bigger/smaller you can do so via
        //mCircleView.setUnitScale(0.9f);
        mCircleView.setTextScale(0.6f);

        //colors of text and unit can be set via
        mCircleView.setInnerContourColor(getResources().getColor(R.color.white));
        mCircleView.setOuterContourColor(getResources().getColor(R.color.white));
        mCircleView.setFillCircleColor(getResources().getColor(R.color.title_color));
        mCircleView.setTextColor(getResources().getColor(R.color.white));


        System.out.println("Seconds--------------- " + seconds);

        mHandler = new Handler();
        mHandler.post(mRunnable);
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (count < seconds) {
                count++;
                mCircleView.setText(String.valueOf(Math.abs(seconds - count)) + "sec");
                mCircleView.setTextMode(TextMode.TEXT);
                mCircleView.setValueAnimated(count, 500);
                mHandler.postDelayed(this, 1000);
            } else {
                mHandler.removeCallbacks(this);

                if (mRequest != null) {
                    mRequest.cancel(true);
                }

                if (sNext_driver_availability.equalsIgnoreCase("1")) {
                    final RideDialog mDialog = new RideDialog(BookingTimer.this, false, true);
                    mDialog.setDialogTitle(getResources().getString(R.string.timer_label_alert_sorry));
                    mDialog.setDialogMessage(sMessage);
                    mDialog.setNegativeButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();

                            if (Internet.hasInternet(BookingTimer.this)) {
                                GuaranteeJobRequest(Config.guarantee_job_find_driver_url);
                            } else {
                                Alert("Alert!", getResources().getString(R.string.no_internet));
                            }
                        }
                    });
                    mDialog.setPositiveButton(getResources().getString(R.string.action_cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();

                            if (Internet.hasInternet(BookingTimer.this)) {
                                GuaranteeDeleteRideRequest(Config.delete_ride_url);
                            } else {
                                AlertInternetCheck();
                            }
                        }
                    });
                    mDialog.show();
                } else {
                    if (getIntent().hasExtra("is_guarantee")) {
                        showRideDialog();
                    } else {
                        final RideDialog mDialog = new RideDialog(BookingTimer.this, false, true);
                        mDialog.setDialogTitle("Alert!");
                        mDialog.setDialogMessage(sMessage);
                        mDialog.setPositiveButton(getResources().getString(R.string.timer_label_alert_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                BookingTimer.this.finish();
                                /*if (Internet.hasInternet(BookingTimer.this)) {
                                    GuaranteeDeleteRideRequest(Config.delete_ride_url);
                                } else {
                                    AlertInternetCheck();
                                }*/
                            }
                        });
                        mDialog.show();
                    }
                }
            }
        }
    };

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {
        final AlertDialog mDialog = new AlertDialog(BookingTimer.this, true);
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

    //--------------Alert Internet Method-----------
    private void AlertInternetCheck() {
        final RideDialog mDialog = new RideDialog(BookingTimer.this, false, true);
        mDialog.setDialogTitle(getResources().getString(R.string.alert_label_title));
        mDialog.setDialogMessage(getResources().getString(R.string.no_internet));
        mDialog.setPositiveButton(getResources().getString(R.string.action_retry), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();

                if (Internet.hasInternet(BookingTimer.this)) {
                    GuaranteeDeleteRideRequest(Config.delete_ride_url);
                } else {
                    AlertInternetCheck();
                }
            }
        });
        mDialog.show();
    }

    //-------------------Guarantee Job Post Request----------------
    private void GuaranteeJobRequest(String Url) {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", userID);
        jsonParams.put("ride_id", rideID);

        mRequest = new APIHandler(BookingTimer.this, HTTPMethods.POST, new AsyncTaskCompleteListener() {
            @Override
            public void onTaskComplete(String result) {
                String sResponse_time = "", sMessage = "", sNext_driver_availability = "";

                try {
                    JSONObject object = new JSONObject(result);
                    if (object.length() > 0) {
                        String status = object.getString("status");
                        if (status.equalsIgnoreCase("1")) {
                            JSONObject response_Object = object.getJSONObject("response");
                            if (response_Object.length() > 0) {
                                sResponse_time = response_Object.getString("response_time");
                                rideID = response_Object.getString("ride_id");
                                userID = response_Object.getString("user_id");
                                sMessage = response_Object.getString("message");
                                sNext_driver_availability = response_Object.getString("next_driver_availability");


                                if (sNext_driver_availability.equalsIgnoreCase("1")) {

                                    seconds = Integer.parseInt(sResponse_time) + 1;

                                    System.out.println("------------seconds------------" + seconds);

                                    //value setting
                                    mCircleView.setMaxValue(seconds);
                                    mCircleView.setValueAnimated(0);

                                    count = 0;

                                    mHandler = new Handler();
                                    mHandler.post(mRunnable);
                                } else {
                                    seconds = Integer.parseInt(sResponse_time) + 1;

                                    System.out.println("------------seconds else------------" + seconds);

                                    //value setting
                                    mCircleView.setMaxValue(seconds);
                                    mCircleView.setValueAnimated(0);

                                    count = 0;

                                    mHandler = new Handler();
                                    mHandler.post(mRunnable);
                                }

                                /*else {
                                    final PkDialog mDialog = new PkDialog(BookingTimer.this);
                                    mDialog.setDialogTitle(getResources().getString(R.string.alert_label_title));
                                    mDialog.setDialogMessage(sMessage);
                                    mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mDialog.dismiss();
                                            cd = new ConnectionDetector(BookingTimer.this);
                                            isInternetPresent = cd.isConnectingToInternet();
                                            if (isInternetPresent) {
                                                GuaranteeDeleteRideRequest(Iconstant.delete_ride_url);
                                            } else {
                                                AlertInternetCheck();
                                            }
                                        }
                                    });
                                    mDialog.show();
                                }*/
                            }
                        } else {
                            String response_value = object.getString("response");
                            Alert("Alert!", response_value);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, jsonParams);
    }

    //-------------------Guarantee Timer Delete Ride Post Request----------------
    private void GuaranteeDeleteRideRequest(String Url) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", userID);
        jsonParams.put("ride_id", rideID);

        mRequest = new APIHandler(BookingTimer.this, HTTPMethods.POST, new AsyncTaskCompleteListener() {
            @Override
            public void onTaskComplete(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.length() > 0) {
                        String status = object.getString("status");
                        String response_value = object.getString("response");
                        if (status.equalsIgnoreCase("1")) {
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("com.refresh_homePage_guaranteeJob_layout");
                            sendBroadcast(broadcastIntent);
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            final RideDialog mDialog = new RideDialog(BookingTimer.this, false, true);
                            mDialog.setDialogTitle(getResources().getString(R.string.alert_label_title));
                            mDialog.setDialogMessage(response_value);
                            mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent broadcastIntent = new Intent();
                                    broadcastIntent.setAction("com.refresh_homePage_guaranteeJob_layout");
                                    sendBroadcast(broadcastIntent);

                                    mDialog.dismiss();
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                }
                            });
                            mDialog.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, jsonParams);
    }

    //-------------------Delete Ride Post Request----------------
    private void cancelRide() {
        Iv_cancelRide.setVisibility(View.GONE);
        Ll_cancelRide_message.setVisibility(View.VISIBLE);
        mRequest = new APIHandler(BookingTimer.this, HTTPMethods.PUT, this, null);
        mRequest.execute(Config.CANCEL_BOOKING + rideID, sessionManager.getToken());
    }

    @Override
    public void onDestroy() {
        if (mdialog != null) {
            mdialog.dismiss();
        }
        mHandler.removeCallbacks(mRunnable);
        unregisterReceiver(updateReceiver);
        super.onDestroy();
    }

    private void showRideDialog() {
        final RideDialog mDialog = new RideDialog(BookingTimer.this, false, true);
        mDialog.setDialogTitle("");
        mDialog.setDialogMessage("No Driver Found. There is guarantee driver available. You will have to pay extra.\nDo you want to find?");
        mDialog.setNegativeButton(getResources().getString(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent = new Intent(BookingTimer.this, GuaranteeDriverInfo.class);
                intent.putExtra("pickUp", getIntent().getStringExtra("pickUp"));
                if (getIntent().hasExtra("dropOff"))
                    intent.putExtra("dropOff", getIntent().getStringExtra("dropOff"));
                intent.putExtra("CategoryID", getIntent().getStringExtra("CategoryID"));
                intent.putExtra("type", getIntent().getStringExtra("type"));
                intent.putExtra("puDate", getIntent().getStringExtra("puDate"));
                intent.putExtra("UserID", userID);
                intent.putExtra("RideID", rideID);
                startActivity(intent);
                BookingTimer.this.finish();
            }
        });
        mDialog.setPositiveButton(getResources().getString(R.string.no), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                BookingTimer.this.finish();
            }
        });
        mDialog.show();
    }
}
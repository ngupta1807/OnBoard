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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bookmyride.util.ImageLoader;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * Created by Vinod on 2017-01-07.
 */
public class FareBreakup extends AppCompatActivity implements AsyncTaskCompleteListener, View.OnClickListener {
    private TextView pickup, dropOff, fare, waitCharges, bookingId, discount;
    SessionHandler session;
    TextView total, apply, duration, waiting, distance, processFee, subTotal, tipAmount;
    CheckBox useWallet, useTip;
    LinearLayout layAvailTip, layRemoveTip;
    RelativeLayout layTip;
    EditText tip;
    TextView driverName, carName, carNumber/*, rideDateTime*/;
    String bookingID;
    //ImageView driverImg;
    ImageLoader imgLoader;
    TextView walletAmount, payableAmount, balancedWallet, payByPaypal, payByCard, payByWallet, checkStatus;
    String availableWalletAmount, billAmount;
    ImageView icon;
    LinearLayout layWW, laySPA, layDiscount;
    BroadcastReceiver mReceiver;
    TextView acceptPayment;
    LinearLayout layBottom;
    RelativeLayout layPaidAmount, layWalletUsed;
    TextView walletUsed, paidAmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fare_breakup);
        init();
        if (getIntent().hasExtra("bookingID")) {
            bookingID = getIntent().getStringExtra("bookingID");
            //status = getIntent().getStringExtra("status");
        } else {
            bookingID = session.getCurrentBookingId();
        }
        getEstimate();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.accept_payment:
                Intent intent;
                if (bookedBy.equals("0")) {
                    if (session.getUserType().equals("3")) {
                        intent = new Intent(getApplicationContext(), RatingToDriver.class);
                        intent.putExtra("isFavourite", isFavourite);
                        intent.putExtra("bookingID", bookingID);
                    } else {
                        intent = new Intent(getApplicationContext(), RatingToPassenger.class);
                        intent.putExtra("bookingId", bookingID);
                    }
                } else {
                    if (session.getUserType().equals("3"))
                        intent = new Intent(getApplicationContext(), PassengerHome.class);
                    else
                        intent = new Intent(getApplicationContext(), DriverHome.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                FareBreakup.this.finish();
                break;
            case R.id.check_staus:
                checkPaymentStatus();
                break;
            case R.id.pay_by_paypal:
                type = 2;
                //payByPaypal();
                proceedPrompt(2, "Are you sure to proceed for payment?");
                break;
            case R.id.proceed:
                type = 3;
                //payByWallet();
                proceedPrompt(3, "Are you sure to proceed for payment?");
                break;
            case R.id.pay_by_card:
                type = 1;
                //payByCard();
                proceedPrompt(1, "Are you sure to proceed for payment?");
                break;
            case R.id.apply:
                if (tip.getText().toString().length() > 0) {
                    layAvailTip.setVisibility(View.VISIBLE);
                    tipAmount.setText(currency + tip.getText().toString());
                    tip.setText("");
                    layTip.setVisibility(View.GONE);
                    useTip.setChecked(true);
                    if (useWallet.isChecked()) {
                        refreshPayableAmount();
                    }
                } else
                    Error("Alert!", "Please enter tip amount.");
                break;
            case R.id.lay_remove_tip:
                layAvailTip.setVisibility(View.GONE);
                tipAmount.setText("");
                tip.setText("");
                useTip.setChecked(false);
                if (useWallet.isChecked()) {
                    refreshPayableAmount();
                }
                break;
        }
    }

    private void payByWallet() {
        if (useTip.isChecked() && tipAmount.getText().toString().length() == 0) {
            type = 50;
            Error("Alert!", "Please enter tip amount.");
            return;
        }
        HashMap<String, String> requestParam = new HashMap<>();
        requestParam.put("bookingId", bookingID);
        if (useTip.isChecked()) {
            String tipAmt = tipAmount.getText().toString();
            String actTipAmt = tipAmt.substring(1, tipAmt.length());
            //double amt = Double.parseDouble(actTipAmt);
            requestParam.put("tip", actTipAmt);
        } else
            requestParam.put("tip", "0");
        requestParam.put("gateway", "wallet");
        requestParam.put("isSavedCard", "0");
        if (useWallet.isChecked())
            requestParam.put("use_wallet", "1");
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, requestParam);
            apiHandler.execute(Config.PAYMENT_BY_CARD, session.getToken());
        } else
            Error("Alert!", getResources().getString(R.string.no_internet));
    }

    private void payByCard() {
        if (useTip.isChecked() && tipAmount.getText().toString().length() == 0) {
            type = 50;
            Error("Alert!", "Please enter tip amount.");
            return;
        }
        HashMap<String, String> requestParam = new HashMap<>();
        requestParam.put("bookingId", bookingID);
        if (useTip.isChecked()) {
            String tipAmt = tipAmount.getText().toString();
            String actTipAmt = tipAmt.substring(1, tipAmt.length());
            //double amt = Double.parseDouble(actTipAmt);
            requestParam.put("tip", actTipAmt);
        } else
            requestParam.put("tip", "0");
        //requestParam.put("gateway", "westpac");
        requestParam.put("gateway", "pinpay");
        requestParam.put("isSavedCard", "1");
        if (useWallet.isChecked())
            requestParam.put("use_wallet", "1");
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, requestParam);
            apiHandler.execute(Config.PAYMENT_BY_CARD, session.getToken());
        } else
            Error("Alert!", getResources().getString(R.string.no_internet));
    }

    private void payByPaypal() {
        if (useTip.isChecked() && tipAmount.getText().toString().length() == 0) {
            type = 50;
            Error("Alert!", "Please enter tip amount.");
            return;
        }
        HashMap<String, String> requestParam = new HashMap<>();
        requestParam.put("bookingId", bookingID);
        if (useTip.isChecked()) {
            String tipAmt = tipAmount.getText().toString();
            String actTipAmt = tipAmt.substring(1, tipAmt.length());
            //double amt = Double.parseDouble(actTipAmt);
            requestParam.put("tip", actTipAmt);
        } else
            requestParam.put("tip", "0");
        requestParam.put("gateway", "paypalCheckout");
        requestParam.put("isSavedCard", "1");
        if (useWallet.isChecked())
            requestParam.put("use_wallet", "1");
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, requestParam);
            apiHandler.execute(Config.PAYMENT_BY_CARD, session.getToken());
        } else
            Error("Alert!", getResources().getString(R.string.no_internet));
    }

    String currency;
    String driverCategory = "";
    String bookedBy = "";
    boolean isFavourite = false;

    private void appendData(JSONObject data) {
        try {
            if (data.has("isFavorite"))
                isFavourite = data.get("isFavorite").toString().equals("1") ? true : false;
            else isFavourite = false;
            bookedBy = data.get("bookedBy").toString();
            //rideDateTime.setText(data.getString("pickUpDate"));
            bookingId.setText("Ride ID: " + data.get("id").toString());
            driverCategory = data.get("driverCategory_id").toString();
            String pick = data.getString(Key.PICKUP);
            if (!pick.equals("null") && !pick.equals("")) {
                JSONObject pu = new JSONObject(pick);
                pickup.setText(pu.getString(Key.ADDRESS));
            }
            String drop = data.getString(Key.DROPOFF);
            if (drop != null && !drop.equals("null")) {
                JSONObject doff = new JSONObject(drop);
                dropOff.setText(doff.getString(Key.ADDRESS));
            }

            if (driverCategory.equals("1")) {
                layWW.setVisibility(View.GONE);
                laySPA.setVisibility(View.GONE);
                layDiscount.setVisibility(View.GONE);
            } else {
                laySPA.setVisibility(View.VISIBLE);
                if (driverCategory.equals("1"))
                    layDiscount.setVisibility(View.GONE);
                else layDiscount.setVisibility(View.VISIBLE);
            }

            if (driverCategory.equals("1")) {
                icon.setImageResource(R.drawable.estimate_taxi);
            } else if (driverCategory.equals("2")) {
                icon.setImageResource(R.drawable.estimate_economy);
            } else if (driverCategory.equals("3")) {
                icon.setImageResource(R.drawable.estimate_premium);
            } else if (driverCategory.equals("4")) {
                icon.setImageResource(R.drawable.estimate_motor_bike);
            }

            JSONObject fairObject = new JSONObject(data.getString("fareDetail"));
            currency = fairObject.getString("currency");
            billAmount = fairObject.getString("total");
            total.setText(currency + getTotal(fairObject.getString("total")));
            //baseFare.setText(currency + fairObject.getString("fare"));
            waiting.setText(data.getString("waitTime") + " Sec");

            if (fairObject.has("waitTimeCharges")) {
                if (!data.getString("waitTime").equals("0") &&
                        !data.getString("waitTime").equals("null") &&
                        !data.getString("waitTime").equals("") &&
                        !driverCategory.equals("1")) {
                    waitCharges.setText(currency + fairObject.getString("waitTimeCharges"));
                    layWW.setVisibility(View.VISIBLE);
                } else {
                    layWW.setVisibility(View.GONE);
                    waitCharges.setText(currency + "0");
                }
            } else {
                layWW.setVisibility(View.GONE);
                waitCharges.setText(currency + "0");
            }

            if (data.getString("duration").contains("-"))
                duration.setText(secondsToMin(data.getString("duration").substring(1)));
            else
                duration.setText(secondsToMin(data.getString("duration")));

            if (!fairObject.get("discount").toString().equals("") &&
                    !fairObject.get("discount").toString().equals("null")) {
                String disc = fairObject.get("discount").toString();
                if (Double.parseDouble(disc) > 0)
                    layDiscount.setVisibility(View.VISIBLE);
                else layDiscount.setVisibility(View.GONE);
                discount.setText(currency + disc);
                String actFare = fairObject.getString("fare");
                double actualFare = Double.parseDouble(actFare) + Double.parseDouble(disc);
                //fare.setText("$" + actualFare);
                fare.setText("$" + getTotal(actFare));
            } else {
                layDiscount.setVisibility(View.GONE);
                fare.setText(currency + fairObject.getString("fare"));
            }

            String paymentDetail = data.getString("payment_amount_detail");
            if (!paymentDetail.equals("") && !paymentDetail.equals("null")) {
                JSONObject paymentObj = new JSONObject(paymentDetail);
                String walletAmountUsed = paymentObj.get("Wallet_amt_used").toString();
                if (!walletAmountUsed.equals("null")) {
                    double walletAmt = Double.parseDouble(walletAmountUsed);
                    if (walletAmt >= 1) {
                        layWalletUsed.setVisibility(View.VISIBLE);
                        walletUsed.setText(currency + getTotal(walletAmountUsed));
                    } else {
                        layWalletUsed.setVisibility(View.GONE);
                    }
                } else {
                    layWalletUsed.setVisibility(View.GONE);
                }

                String amountUsed = paymentObj.get("amount").toString();
                if (!amountUsed.equals("null") && !amountUsed.equals("") &&
                        (layWalletUsed.getVisibility() == View.VISIBLE
                                || layDiscount.getVisibility() == View.VISIBLE)) {
                    layPaidAmount.setVisibility(View.VISIBLE);
                    paidAmt.setText(currency + getTotal(amountUsed));
                } else {
                    layPaidAmount.setVisibility(View.GONE);
                }
                //ride.setPaidVia(paymentObj.get("payment_gateway").toString());
            }

            distance.setText(meterToKM(data.get("distance").toString()));
            processFee.setText(currency + fairObject.getString("processFee"));
            //serviceTax.setText(fairObject.getString("serviceTax")+"%");
            subTotal.setText(currency + fairObject.getString("subTotal"));
            availableWalletAmount = data.get("walletBalance").toString();
            if (Double.parseDouble(availableWalletAmount) == 0) {
                useWallet.setVisibility(View.GONE);
            } else useWallet.setVisibility(View.GONE);
            walletAmount.setText(currency + data.get("walletBalance").toString());
            String driverGEO = data.get("drivergeo").toString();
            if (driverGEO != null) {
                JSONObject driverObj = new JSONObject(driverGEO);
                //imgLoader.DisplayImage(driverObj.getString("image"), driverImg);
                driverName.setText(driverObj.getString("fullName"));
                String vehicleGEO = driverObj.get("vehicleDetail").toString();
                if (vehicleGEO != null) {
                    JSONObject vehicleObj = new JSONObject(vehicleGEO);
                    carName.setText(vehicleObj.getString("vehicle_type"));
                    carNumber.setText(vehicleObj.getString("registerationNumber"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getTotal(String totalAmount) {
        if (totalAmount.contains(".")) {
            if (totalAmount.startsWith("."))
                totalAmount = "0" + totalAmount;
            double actMeters = Double.parseDouble(totalAmount);
            totalAmount = String.format("%.2f", actMeters);
        }
        return totalAmount;
    }

    private String secondsToMin(String seconds) {
        int second = Integer.parseInt(seconds);
        int hours = second / 3600;
        int minutes = (second % 3600) / 60;
        int secs = second % 60;

        //return String.format("%02d:%02d:%02d", hours, minutes, secs);
        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        else
            return String.format("%02d:%02d", minutes, secs) + " Min";
    }

    private String meterToKM(String meters) {
        double actMeters = Double.parseDouble(meters);
        double km = actMeters / 1000;
        String dist = String.format("%.2f", km);
        return dist + " KM";
    }

    private void init() {
        imgLoader = new ImageLoader(this);
        session = new SessionHandler(this);
        bookingId = (TextView) findViewById(R.id.ride_id);
        pickup = (TextView) findViewById(R.id.pickup_address);
        dropOff = (TextView) findViewById(R.id.do_address);
        icon = (ImageView) findViewById(R.id.icon);
        fare = (TextView) findViewById(R.id.fare);
        waitCharges = (TextView) findViewById(R.id.wait_charges);
        total = (TextView) findViewById(R.id.total);
        apply = (TextView) findViewById(R.id.apply);
        apply.setOnClickListener(this);

        layWW = (LinearLayout) findViewById(R.id.lay_ww);
        laySPA = (LinearLayout) findViewById(R.id.lay_spa);
        layDiscount = (LinearLayout) findViewById(R.id.lay_discount);
        layBottom = (LinearLayout) findViewById(R.id.fare_breakup_bottom_layout);
        acceptPayment = (TextView) findViewById(R.id.accept_payment);
        acceptPayment.setOnClickListener(this);

        layWalletUsed = (RelativeLayout) findViewById(R.id.lay_wallet_used);
        layPaidAmount = (RelativeLayout) findViewById(R.id.lay_paid);

        walletUsed = (TextView) findViewById(R.id.wallet_used);
        paidAmt = (TextView) findViewById(R.id.total_paid);

        discount = (TextView) findViewById(R.id.discount);
        walletAmount = (TextView) findViewById(R.id.wallet_amount);
        payableAmount = (TextView) findViewById(R.id.payable_amount);
        balancedWallet = (TextView) findViewById(R.id.balanced_amount);
        //driverImg = (ImageView) findViewById(R.id.driver_img);
        driverName = (TextView) findViewById(R.id.driver_name);
        carName = (TextView) findViewById(R.id.car_number);
        carNumber = (TextView) findViewById(R.id.car_name);
        //rideDateTime = (TextView) findViewById(R.id.ride_date);
        //baseFare = (TextView) findViewById(R.id.base_fare);
        duration = (TextView) findViewById(R.id.duration);
        waiting = (TextView) findViewById(R.id.waiting);
        distance = (TextView) findViewById(R.id.distance);
        //serviceTax = (TextView) findViewById(R.id.service_tx);
        processFee = (TextView) findViewById(R.id.process_fee);
        subTotal = (TextView) findViewById(R.id.subtotal);
        useWallet = (CheckBox) findViewById(R.id.use_wallet);
        useTip = (CheckBox) findViewById(R.id.fare_breakup_tip_checkBox);
        layTip = (RelativeLayout) findViewById(R.id.fare_breakup_tip_layout);
        layRemoveTip = (LinearLayout) findViewById(R.id.lay_remove_tip);
        layRemoveTip.setOnClickListener(this);
        layAvailTip = (LinearLayout) findViewById(R.id.fare_breakup_tip_amount_layout);
        checkStatus = (TextView) findViewById(R.id.check_staus);
        checkStatus.setOnClickListener(this);
        payByPaypal = (TextView) findViewById(R.id.pay_by_paypal);
        payByPaypal.setOnClickListener(this);
        payByCard = (TextView) findViewById(R.id.pay_by_card);
        payByCard.setOnClickListener(this);
        payByWallet = (TextView) findViewById(R.id.proceed);
        payByWallet.setOnClickListener(this);
        tip = (EditText) findViewById(R.id.tip);
        tipAmount = (TextView) findViewById(R.id.tip_amount);
        useTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    layTip.setVisibility(View.VISIBLE);
                } else {
                    layTip.setVisibility(View.GONE);
                    layAvailTip.setVisibility(View.GONE);
                    tipAmount.setText("");
                    tip.setText("");
                    if (useWallet.isChecked())
                        refreshPayableAmount();
                }
            }
        });
        useWallet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    refreshPayableAmount();
                } else {
                    payableAmount.setVisibility(View.GONE);
                    payableAmount.setText("");
                    balancedWallet.setVisibility(View.GONE);
                    balancedWallet.setText("");
                    payByWallet.setVisibility(View.GONE);
                    payByCard.setVisibility(View.VISIBLE);
                    payByPaypal.setVisibility(View.VISIBLE);
                    if (useWallet.isChecked()) {
                        refreshPayableAmount();
                    }
                }
            }
        });
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NotificationFilters.PAYMENT_REQEUST)) {
                    if (session.getUserType().equals("3")) {
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
                                    acceptPayment.setVisibility(View.VISIBLE);
                                    layBottom.setVisibility(View.GONE);
                                }
                            }, 300);
                        }
                    }
                }
            }
        };
    }

    private void refreshPayableAmount() {
        double payableMoney = Double.parseDouble(billAmount);
        double walletAmount = Double.parseDouble(availableWalletAmount);
        double totalPayableAmount;
        double balancedAmt;
        double tipAmt;
        if (!tipAmount.getText().toString().equals("")) {
            String tip = tipAmount.getText().toString();
            String actTipAmt = tip.substring(1, tip.length());
            tipAmt = Double.parseDouble(actTipAmt);
            payableMoney = payableMoney + tipAmt;
        }
        if (walletAmount > payableMoney) {
            totalPayableAmount = 0.0;
            balancedAmt = walletAmount - payableMoney;
            payByCard.setVisibility(View.GONE);
            payByPaypal.setVisibility(View.GONE);
            payByWallet.setVisibility(View.VISIBLE);
        } else {
            balancedAmt = 0.0;
            totalPayableAmount = payableMoney - walletAmount;
            payByWallet.setVisibility(View.GONE);
            payByCard.setVisibility(View.VISIBLE);
            payByPaypal.setVisibility(View.VISIBLE);
        }
        payableAmount.setVisibility(View.VISIBLE);
        balancedWallet.setVisibility(View.VISIBLE);
        DecimalFormat two = new DecimalFormat("0.00");
        payableAmount.setText("Total Payable Amount: " + currency + two.format(totalPayableAmount));
        balancedWallet.setText("Available Wallet Amount After use: " + currency + two.format(balancedAmt));
    }

    @Override
    public void onBackPressed() {
        if (acceptPayment.getVisibility() == View.GONE)
            backDialog("Alert!", "You will have to make ride payment. Do you want to quit the screen?");
    }

    public void onBack(View view) {
        onBackPressed();
    }

    public void getEstimate() {
        type = 0;
        if (Internet.hasInternet(this)) {
            APIHandler apiCall = new APIHandler(this, HTTPMethods.GET, this, null);
            apiCall.execute(Config.ESTIMATE_RIDE + "/" + bookingID + "?expand=drivergeo", session.getToken());
        } else
            Error("Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    int type;

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                if (type == 4) {
                    JSONArray dataArray = outJson.getJSONArray(Key.DATA);
                    String status = dataArray.get(0).toString();
                    if (status.equals("1")) {
                        success("Success!", outJson.getString(Key.MESSAGE));
                    } else {
                        Error("Alert!", outJson.getString(Key.MESSAGE));
                    }
                } else if (type == 3) {
                    success("Success!", "Payment Received. Thanks for using BookMyRide.");
                } else if (type == 2) {
                    JSONObject dataObj = outJson.getJSONObject(Key.DATA);
                    proceedDialog("Success!", outJson.getString(Key.MESSAGE), dataObj.getString("url"));
                } else if (type == 0)
                    appendData(outJson.getJSONObject(Key.DATA));
                else {
                    success("Success!", "Payment Received. Thanks for using BookMyRide.");
                }
            } else if (outJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                // Alert("","Payment Received. Thanks for using Ride24:7.");
                startActivityForResult(new Intent(FareBreakup.this, MyCard.class), 501);
            } else {
                Error("Alert!", outJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(FareBreakup.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (type == 1 || type == 3) {
                    Intent intent = new Intent(getApplicationContext(), RatingToDriver.class);
                    intent.putExtra("bookingID", bookingID);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    FareBreakup.this.finish();
                }
            }
        });
        mDialog.show();
    }

    private void success(String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(FareBreakup.this, false);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setCancelOnTouchOutside(false);
        /*mDialog.setNegativeButton("Rate Driver", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent2 = new Intent(getApplicationContext(), RatingToDriver.class);
                intent2.putExtra("bookingID", bookingID);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                FareBreakup.this.finish();
            }
        });*/
        mDialog.setPositiveButton("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (getIntent().hasExtra("isBack")) {
                    FareBreakup.this.finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Intent intent = new Intent(getApplicationContext(), PassengerHome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    FareBreakup.this.finish();
                }
            }
        });
        mDialog.show();
    }

    private void Error(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(FareBreakup.this, true);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 501) {
                payByCard();
            }
        }
    }

    private void proceedDialog(String title, String message, final String endPoint) {
        final AlertDialog mDialog = new AlertDialog(FareBreakup.this, true);
        mDialog.setDialogTitle(title);
        //mDialog.showCross(true);
        mDialog.setDialogMessage(message);
        mDialog.setNegativeButton("Proceed", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                startActivity(new Intent(FareBreakup.this, PaymentBrowser.class)
                        .putExtra("endPoint", endPoint).putExtra("bookingID", bookingID));
            }
        });
        mDialog.setPositiveButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void backDialog(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(FareBreakup.this, true);
        mDialog.setDialogTitle(title);
        //mDialog.showCross(true);
        mDialog.setDialogMessage(message);
        mDialog.setNegativeButton("Quit", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                FareBreakup.this.finish();
            }
        });
        mDialog.setPositiveButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }


    private void proceedPrompt(final int type, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(FareBreakup.this, true);
        mDialog.setDialogTitle("BookMyRide");
        mDialog.setDialogMessage(message);
        mDialog.setNegativeButton(getResources().getString(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (type == 1) {
                    startActivityForResult(new Intent(FareBreakup.this, MyCard.class), 501);
                    //payByCard();
                } else if (type == 2) {
                    payByPaypal();
                } else if (type == 3) {
                    payByWallet();
                }
            }
        });
        mDialog.setPositiveButton(getResources().getString(R.string.no), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public void checkPaymentStatus() {
        type = 4;
        if (Internet.hasInternet(this)) {
            APIHandler apiCall = new APIHandler(this, HTTPMethods.GET, this, null);
            apiCall.execute(Config.PAYMENT_BY_CARD + "/" + bookingID, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
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
package com.bookmyride.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.bookmyride.fragments.AcceptPaymentByCard;
import com.bookmyride.util.ImageLoader;
import com.bookmyride.views.RideDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Vinod on 2017-01-07.
 */
public class FareBreakdown extends AppCompatActivity implements AsyncTaskCompleteListener, View.OnClickListener {
    private TextView pickup, dropOff, fare, waitCharges, waiting, bookingId;
    SessionHandler session;
    TextView total, apply, duration, processFee, distance, tipAmount;
    CheckBox useTip;
    LinearLayout layAvailTip, layRemoveTip;
    RelativeLayout layTip;
    EditText tip;
    TextView name, discount /*, rideDateTime*/;
    TextView driverName, carName, carNumber/*, rideDateTime*/;
    String bookingID;
    //ImageView driverImg;
    ImageLoader imgLoader;
    TextView walletAmount, payableAmount, acceptByPaypal, acceptByCard, acceptPayment;
    String availableWalletAmount, billAmount;
    LinearLayout layGST, layWait, laySPA, layDiscount, layDriver;
    String paymentType = "";
    ImageView icon;
    RelativeLayout layPass, layPaidAmount, layWalletUsed;
    TextView walletUsed, paidAmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fare_breakdown);
        init();
        if (getIntent().hasExtra("bookingID")) {
            bookingID = getIntent().getStringExtra("bookingID");
            //status = getIntent().getStringExtra("status");
        } else {
            bookingID = session.getCurrentBookingId();
        }
        if (getIntent().hasExtra("type"))
            paymentType = getIntent().getStringExtra("type");
        getEstimate();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.accept_payment:
                if (paymentType.equals("cash")) {
                    acceptPayment(1);
                } else if (paymentType.equals("credit_card")) {
                    acceptPayment(2);
                } else if (paymentType.equals("paypal")) {
                    acceptPayment(3);
                } else if (paymentType.equals("terminal")) {
                    //showDialog();
                    payByTerminal("");
                } else if (paymentType.equals("0") || paymentType.equals("1")) {
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
                    FareBreakdown.this.finish();
                }
                //startActivityForResult(new Intent(this, AcceptPayment.class),21);
                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                //showRideDialog();
                break;
            case R.id.pay_by_paypal:
                type = 2;
                payByPaypal();
                break;
            case R.id.proceed:
                type = 3;
                payByWallet();
                break;
            case R.id.pay_by_card:
                type = 1;
                payByCard();
                break;
            case R.id.apply:
                if (tip.getText().toString().length() > 0) {
                    layAvailTip.setVisibility(View.VISIBLE);
                    tipAmount.setText(currency + tip.getText().toString());
                    tip.setText("");
                    layTip.setVisibility(View.GONE);
                    useTip.setChecked(true);
                } else
                    Error("Alert!", "Please enter tip amount.");
                break;
            case R.id.lay_remove_tip:
                layAvailTip.setVisibility(View.GONE);
                tipAmount.setText("");
                tip.setText("");
                useTip.setChecked(false);
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
        type = 104;
        HashMap<String, String> requestParam = new HashMap<>();
        requestParam.put("bookingId", bookingID);
        if (useTip.isChecked()) {
            String tipAmt = tipAmount.getText().toString();
            String actTipAmt = tipAmt.substring(1, tipAmt.length());
            //double amt = Double.parseDouble(actTipAmt);
            requestParam.put("tip", actTipAmt);
        } else
            requestParam.put("tip", "0");
        requestParam.put("gateway", "card");
        requestParam.put("isSavedCard", "1");
        requestParam.put("payBySavedCard", "1");
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
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, requestParam);
            apiHandler.execute(Config.PAYMENT_BY_CARD, session.getToken());
        } else
            Error("Alert!", getResources().getString(R.string.no_internet));
    }

    String currency;

    private void payByTerminal(String transactionId) {
        if (useTip.isChecked() && tipAmount.getText().toString().length() == 0) {
            type = 50;
            Error("Alert!", "Please enter tip amount.");
            return;
        }
        type = 100;
        HashMap<String, String> requestParam = new HashMap<>();
        requestParam.put("bookingId", bookingID);
        if (useTip.isChecked()) {
            String tipAmt = tipAmount.getText().toString();
            String actTipAmt = tipAmt.substring(1, tipAmt.length());
            //double amt = Double.parseDouble(actTipAmt);
            requestParam.put("tip", actTipAmt);
        } else
            requestParam.put("tip", "0");
        requestParam.put("gateway", "terminal");
        requestParam.put("transId", transactionId);
        requestParam.put("isSavedCard", "1");
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, requestParam);
            apiHandler.execute(Config.PAYMENT_BY_CARD, session.getToken());
        } else
            Error("Alert!", getResources().getString(R.string.no_internet));
    }

    String driverCategory = "";
    String bookedBy = "";
    boolean isFavourite = false;

    private void appendData(JSONObject data) {
        try {
            if (data.has("isFavorite"))
                isFavourite = data.get("isFavorite").toString().equals("1") ? true : false;
            else isFavourite = false;
            //rideDateTime.setText(data.getString("pickUpDate"));
            bookedBy = data.get("bookedBy").toString();

            if (paymentType.equals("null") || paymentType.equals(""))
                paymentType = data.get("paymentStatus").toString();
            bookingId.setText("Ride ID: " + data.get("id").toString());
            driverCategory = data.get("driverCategory_id").toString();

            if (paymentType.equals("0") || paymentType.equals("1"))
                acceptPayment.setText("CLOSE");

            if (driverCategory.equals("1") && paymentType.equals("cash")) {
                layGST.setVisibility(View.GONE);
                layWait.setVisibility(View.GONE);
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
            JSONObject fairObject = new JSONObject(data.getString("fareDetail"));
            currency = fairObject.getString("currency");
            billAmount = fairObject.getString("total");
            waiting.setText(data.getString("waitTime") + " Sec");
            if (paymentType.equals("cash"))
                total.setText(currency + getTotal(fairObject.getString("fare")));
            else
                total.setText(currency + getTotal(fairObject.getString("total")));
            //baseFare.setText(currency + fairObject.getString("fare"));

            if (fairObject.has("waitTimeCharges")) {
                if (!data.getString("waitTime").equals("0") &&
                        !data.getString("waitTime").equals("null") &&
                        !data.getString("waitTime").equals("") &&
                        !driverCategory.equals("1")) {
                    waitCharges.setText(currency + fairObject.getString("waitTimeCharges"));
                    layWait.setVisibility(View.VISIBLE);
                } else {
                    layWait.setVisibility(View.GONE);
                    waitCharges.setText(currency + "0");
                }
            } else {
                layWait.setVisibility(View.GONE);
                waitCharges.setText(currency + "0");
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

            if (data.getString("duration").startsWith("-"))
                duration.setText(secondsToMin(data.getString("duration").substring(1)));
            else
                duration.setText(secondsToMin(data.getString("duration")));

            //waiting.setText(data.getString("waitTime")+" Sec");

            /*if (fairObject.has("waitTimeCharges"))
                waitCharges.setText(currency + fairObject.getString("waitTimeCharges"));
            else
                waitCharges.setText(currency + "0");*/

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

            distance.setText(meterToKM(data.get("distance").toString()));
            processFee.setText(currency + fairObject.getString("processFee"));
            //serviceTax.setText(fairObject.getString("serviceTax")+"%");
            //subTotal.setText(currency + fairObject.getString("subTotal"));
            availableWalletAmount = data.get("walletBalance").toString();
            walletAmount.setText(currency + data.get("walletBalance").toString());

            if (!session.getUserType().equals("3")) {
                layDriver.setVisibility(View.GONE);
                layPass.setVisibility(View.VISIBLE);
                String passenger = data.get("passanger").toString();
                if (passenger != null) {
                    JSONObject passengerObj = new JSONObject(passenger);
                    passengerID = passengerObj.get("id").toString();
                    name.setText(passengerObj.getString("fullName"));
                }
            } else {
                layPass.setVisibility(View.GONE);
                layDriver.setVisibility(View.VISIBLE);
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
                if (data.has("vehicle_details")) {
                    String vehicleDetail = data.get("vehicle_details").toString();
                    if (vehicleDetail != null && !vehicleDetail.equals("null")) {
                        JSONObject vehicleInfo2 = new JSONObject(vehicleDetail);
                        String isFleet2 = vehicleInfo2.getString("isFleetSelected");
                        if (isFleet2.equals("1"))
                            carNumber.setText(vehicleInfo2.getString("vehicle_num"));
                        else
                            carNumber.setText(vehicleInfo2.getString("registerationNumber"));
                    }
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

    String passengerID;

    private String meterToKM(String meters) {
        double actMeters = Double.parseDouble(meters);
        double km = actMeters / 1000;
        String dist = String.format("%.2f", km);
        return dist + " KM";
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

    private void init() {
        imgLoader = new ImageLoader(this);
        session = new SessionHandler(this);
        bookingId = (TextView) findViewById(R.id.ride_id);
        pickup = (TextView) findViewById(R.id.pickup_address);
        dropOff = (TextView) findViewById(R.id.do_address);
        fare = (TextView) findViewById(R.id.fare);
        waitCharges = (TextView) findViewById(R.id.wait_charges);
        total = (TextView) findViewById(R.id.total);
        apply = (TextView) findViewById(R.id.apply);
        icon = (ImageView) findViewById(R.id.icon);
        apply.setOnClickListener(this);
        walletAmount = (TextView) findViewById(R.id.wallet_amount);
        payableAmount = (TextView) findViewById(R.id.payable_amount);
        name = (TextView) findViewById(R.id.name);
        driverName = (TextView) findViewById(R.id.driver_name);
        carName = (TextView) findViewById(R.id.car_number);
        carNumber = (TextView) findViewById(R.id.car_name);
        duration = (TextView) findViewById(R.id.duration);
        waiting = (TextView) findViewById(R.id.waiting);
        discount = (TextView) findViewById(R.id.discount);
        distance = (TextView) findViewById(R.id.distance);
        //serviceTax = (TextView) findViewById(R.id.service_tx);
        processFee = (TextView) findViewById(R.id.process_fee);
        //subTotal = (TextView) findViewById(R.id.subtotal);
        useTip = (CheckBox) findViewById(R.id.fare_breakup_tip_checkBox);
        layTip = (RelativeLayout) findViewById(R.id.fare_breakup_tip_layout);
        layWalletUsed = (RelativeLayout) findViewById(R.id.lay_wallet_used);
        layPaidAmount = (RelativeLayout) findViewById(R.id.lay_paid);

        walletUsed = (TextView) findViewById(R.id.wallet_used);
        paidAmt = (TextView) findViewById(R.id.total_paid);

        layGST = (LinearLayout) findViewById(R.id.lay_gst);
        layWait = (LinearLayout) findViewById(R.id.lay_ww);
        laySPA = (LinearLayout) findViewById(R.id.lay_spa);
        layDiscount = (LinearLayout) findViewById(R.id.lay_discount);
        layDriver = (LinearLayout) findViewById(R.id.lay_driver);
        layPass = (RelativeLayout) findViewById(R.id.lay_pass);

        layRemoveTip = (LinearLayout) findViewById(R.id.lay_remove_tip);
        layRemoveTip.setOnClickListener(this);
        layAvailTip = (LinearLayout) findViewById(R.id.fare_breakup_tip_amount_layout);
        acceptByPaypal = (TextView) findViewById(R.id.by_paypal);
        acceptByPaypal.setOnClickListener(this);
        acceptByCard = (TextView) findViewById(R.id.by_card);
        acceptByCard.setOnClickListener(this);
        acceptPayment = (TextView) findViewById(R.id.accept_payment);
        acceptPayment.setOnClickListener(this);
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
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!acceptPayment.getText().toString().equals("CLOSE"))
            backDialog("Alert!", "You will have to make ride payment. Do you want to quit the screen?");
    }

    public void onBack(View view) {
        onBackPressed();
    }

    public void getEstimate() {
        type = 0;
        if (Internet.hasInternet(this)) {
            APIHandler apiCall = new APIHandler(this, HTTPMethods.GET, this, null);
            apiCall.execute(Config.ESTIMATE_RIDE + "/" + bookingID + "?expand=passanger,drivergeo", session.getToken());
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
                if (type == 100) {
                    goHome();
                    //Success("Success!","Your payment method has been saved successfully.");
                } else if (type == 101) {
                    goHome();
                    //Success("Success!","Your payment method has been saved successfully.");
                } else if (type == 102) {
                    JSONObject dataObj = outJson.getJSONObject(Key.DATA);
                    String cardNumber = dataObj.getString("number");
                    String firstName = dataObj.getString("first_name");
                    String lastName = dataObj.getString("last_name");
                    String fullName = firstName + " " + lastName;
                    String msg = "Hi " + firstName + ", your card  number '" + cardNumber + "' and name on Card '" + fullName + "' is saved on BookMyRide. Do you want to pay using this card or use other card?";
                    cardDialog("Alert!", msg, "");
                } else if (type == 103) {
                    JSONObject dataObj = outJson.getJSONObject(Key.DATA);
                    proceedDialog("Success!", outJson.getString(Key.MESSAGE), dataObj.getString("url"));
                } else if (type == 104) {
                    Success("Success!", "Payment Received. Thanks for using BookMyRide.");
                } else if (type == 0)
                    appendData(outJson.getJSONObject(Key.DATA));
            } else if (outJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                // Alert("","Payment Received. Thanks for using Ride24:7.");
                String tip;
                if (useTip.isChecked()) {
                    String tipAmt = tipAmount.getText().toString();
                    String actTipAmt = tipAmt.substring(1, tipAmt.length());
                    //double amt = Double.parseDouble(actTipAmt);
                    tip = actTipAmt;
                } else
                    tip = "0";
                startActivityForResult(new Intent(FareBreakdown.this, AcceptPaymentByCard.class)
                        .putExtra("isBack", "").putExtra("bookingID", bookingID)
                        .putExtra("tip", tip), 20);
            } else {
                Error("Alert!", outJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Alert(String title, String message) {
        final RideDialog mDialog = new RideDialog(FareBreakdown.this, false, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (type == 1 || type == 3) {
                    Intent intent = new Intent(getApplicationContext(), RatingToDriver.class);
                    intent.putExtra("bookingID", bookingID);
                    intent.putExtra("isFavourite", isFavourite);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    FareBreakdown.this.finish();
                }
            }
        });
        mDialog.show();
    }

    private void Error(final String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(FareBreakdown.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (title.equals("Success!")) {
                    Intent intent = new Intent(getApplicationContext(), DriverHome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    FareBreakdown.this.finish();
                }
            }
        });
        mDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 20) {
                Success("Success!", "Payment Received. Thanks for using BookMyRide. Do you want to rate driver?");
            } else if (requestCode == 21) {
                if (data.getStringExtra("type").equals("cash")) {
                    acceptPayment(1);
                } else if (data.getStringExtra("type").equals("credit_card")) {
                    acceptPayment(2);
                } else if (data.getStringExtra("type").equals("paypal")) {
                    acceptPayment(3);
                } else if (data.getStringExtra("type").equals("terminal")) {
                    showDialog();
                }
            }
        }
    }

    private void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(FareBreakdown.this);
        View subView = inflater.inflate(R.layout.receipt_dialog, null);
        final EditText subEditText = (EditText) subView.findViewById(R.id.bid_price);
        TextView proceed = (TextView) subView.findViewById(R.id.send);
        TextView cancel = (TextView) subView.findViewById(R.id.cancel);
        AlertDialog.Builder builder = new AlertDialog.Builder(FareBreakdown.this);
        builder.setView(subView);
        final AlertDialog alertDialog = builder.create();
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                payByTerminal(subEditText.getText().toString());
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void proceedDialog(String title, String message, final String endPoint) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(FareBreakdown.this, true);
        mDialog.setDialogTitle(title);
        //mDialog.showCross(true);
        mDialog.setDialogMessage(message);
        mDialog.setNegativeButton("Proceed", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                startActivity(new Intent(FareBreakdown.this, PaymentBrowser.class)
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

    private void cardDialog(String title, String message, final String endPoint) {
        final RideDialog mDialog = new RideDialog(FareBreakdown.this, false, true);
        mDialog.setDialogTitle(title);
        //mDialog.showCross(true);
        mDialog.setDialogMessage(message);
        mDialog.setNegativeButton("Use This", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                payByCard();
            }
        });
        mDialog.setPositiveButton("Use Other", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                String tip;
                if (useTip.isChecked()) {
                    String tipAmt = tipAmount.getText().toString();
                    String actTipAmt = tipAmt.substring(1, tipAmt.length());
                    //double amt = Double.parseDouble(actTipAmt);
                    tip = actTipAmt;
                } else
                    tip = "0";
                startActivityForResult(new Intent(FareBreakdown.this, AcceptPaymentByCard.class)
                        .putExtra("isBack", "").putExtra("bookingID", bookingID)
                        .putExtra("tip", tip), 20);
            }
        });
        mDialog.show();
    }

    private void backDialog(String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(FareBreakdown.this, true);
        mDialog.setDialogTitle(title);
        //mDialog.showCross(true);
        mDialog.setDialogMessage(message);
        mDialog.setNegativeButton("Quit", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                FareBreakdown.this.finish();
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

    /* type
     1 = cash
     2 = credit card
     3 = paypal */
    private void acceptPayment(int type) {
        if (type == 1) {
            if (useTip.isChecked() && tipAmount.getText().toString().length() == 0) {
                type = 50;
                Error("Alert!", "Please enter tip amount.");
                return;
            }
            this.type = 101;
            HashMap<String, String> requestParam = new HashMap<>();
            requestParam.put("bookingId", bookingID);
            requestParam.put("gateway", "cash");
            if (useTip.isChecked()) {
                String tipAmt = tipAmount.getText().toString();
                String actTipAmt = tipAmt.substring(1, tipAmt.length());
                //double amt = Double.parseDouble(actTipAmt);
                requestParam.put("tip", actTipAmt);
            } else
                requestParam.put("tip", "0");
            requestParam.put("isSavedCard", "0");
            if (Internet.hasInternet(this)) {
                APIHandler apiCall = new APIHandler(this, HTTPMethods.POST, this, requestParam);
                apiCall.execute(Config.ACCEPT_BY_CASH, session.getToken());
            } else
                Error("Alert!", getResources().getString(R.string.no_internet));
        } else if (type == 2) {
            this.type = 102;
            if (Internet.hasInternet(this)) {
                APIHandler apiCall = new APIHandler(this, HTTPMethods.GET, this, null);
                apiCall.execute(Config.CHECK_AVAILABLE_CARD + passengerID, session.getToken());
            } else
                Error("Alert!", getResources().getString(R.string.no_internet));
        } else if (type == 3) {
            this.type = 103;
            payByPaypal();
            //Error("Alert!","In-Progress.");
        }
    }

    public void showRideDialog() {
        final Dialog mDialog = new Dialog(FareBreakdown.this, R.style.rideDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list);
        mDialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("Accept payment using");

        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mDialog.dismiss();
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDialog.dismiss();
            }
        });

        ListView dialog_ListView = (ListView) mDialog.findViewById(R.id.list);
        ArrayAdapter<String>
                adapter = new ArrayAdapter<>(this,
                R.layout.simple_list_item, R.id.textItem, getPaymentType());

        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("CASH")) {
                    acceptPayment(1);
                } else if (parent.getItemAtPosition(position).toString().equals("CREDIT CARD")) {
                    acceptPayment(2);
                } else if (parent.getItemAtPosition(position).toString().equals("PAYPAL")) {
                    acceptPayment(3);
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public String[] getPaymentType() {
        String[] gateway = getResources().getStringArray(R.array.payment_type);
        return gateway;
    }

    private void Success(String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(FareBreakdown.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                goHome();
            }
        });
        mDialog.show();
    }

    private void goHome() {
        Intent intent = new Intent(getApplicationContext(), DriverHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        FareBreakdown.this.finish();
    }
}
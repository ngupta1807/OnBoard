package com.bookmyride.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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
import com.bookmyride.fragments.AcceptPayment;
import com.bookmyride.map.GMapV2GetRouteDirection;
import com.bookmyride.models.Ride;
import com.bookmyride.util.Utils;
import com.bookmyride.views.AlertDialog;
import com.bookmyride.views.RideDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vinod on 1/25/2017.
 */
public class RideDetail extends AppCompatActivity implements View.OnClickListener,
        OnMapReadyCallback {
    TextView title, rideID, puAddress, doAddress, puDate, doDate, puTime, doTime, distance, timeTaken,
            waitTime, totalBill, totalBillPaid, tip, payment;
    LinearLayout layCancelRide, layTrackRide, layPayment, layEmailInvoice, layReportIssue,
            layDDW, layAccept, layDecline, layDrop, layRateDriver, layRateRider;
    GoogleMap googleMap;
    SessionHandler session;
    String status, statusInfo, bookingID, puInfo, doInfo, driverCat, custName, custPhone, puDateTime, cardStatus, cardDetail;
    boolean isDiscount;
    TextView rideStatus, walletUsed, paidAmount;
    RelativeLayout layBill, layWalletUsed, layTip, layPaymentMode, layCardUsed;
    ImageView icon;
    String bookingType, paymentStatus;
    TextView txtDuration, txtDistance, bill, paidVia, cardNumber,
            ccBill, ccPaidVia, ccCardNumber;
    LinearLayout layRideInfo, layFareInfo, layCancelCharges, layRefund, layRefundReason;
    TextView baseFare, processFee, waitCharges, line, discount, refundAmount, refundReason, refundReasn;
    Timer updateTimer = new Timer();
    RelativeLayout layPrepaid, layDiscount;
    TextView asapStatus;
    private BroadcastReceiver mReceiver;
    boolean isDriverFavourited = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_detail);
        init();
        if (getIntent().hasExtra("rideDetail")) {
            Ride rideDetail = (Ride) getIntent().getSerializableExtra("rideDetail");
            appendData(rideDetail);
        } else if (getIntent().hasExtra("booking_id")) {
            getRidesInfo(getIntent().getStringExtra("booking_id"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(NotificationFilters.REQUEST_CANCELLED));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onPause();
    }

    private void initMap() {
        if (googleMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    String msg = "";

    private void init() {
        session = new SessionHandler(this);
        icon = (ImageView) findViewById(R.id.icon);
        title = (TextView) findViewById(R.id.title);
        rideID = (TextView) findViewById(R.id.ride_id);
        puAddress = (TextView) findViewById(R.id.pu_address);
        doAddress = (TextView) findViewById(R.id.do_address);
        puDate = (TextView) findViewById(R.id.pu_date);
        doDate = (TextView) findViewById(R.id.do_date);
        puTime = (TextView) findViewById(R.id.pu_time);
        doTime = (TextView) findViewById(R.id.do_time);
        txtDistance = (TextView) findViewById(R.id.text_distance);
        txtDuration = (TextView) findViewById(R.id.text_duration);
        bill = (TextView) findViewById(R.id.bill);
        distance = (TextView) findViewById(R.id.distance);
        timeTaken = (TextView) findViewById(R.id.time_taken);
        waitTime = (TextView) findViewById(R.id.wait_time);
        totalBill = (TextView) findViewById(R.id.total_bill);
        totalBillPaid = (TextView) findViewById(R.id.total_paid);
        tip = (TextView) findViewById(R.id.tip);
        payment = (TextView) findViewById(R.id.payment);
        rideStatus = (TextView) findViewById(R.id.status);
        walletUsed = (TextView) findViewById(R.id.wallet_used);
        paidAmount = (TextView) findViewById(R.id.paid_amt);
        paidVia = (TextView) findViewById(R.id.paid_via);
        cardNumber = (TextView) findViewById(R.id.card_number);
        asapStatus = (TextView) findViewById(R.id.asap_status);

        ccBill = (TextView) findViewById(R.id.cc_paid);
        ccPaidVia = (TextView) findViewById(R.id.cc_paid_via);
        ccCardNumber = (TextView) findViewById(R.id.cc_card_number);
        refundAmount = (TextView) findViewById(R.id.refund_amount);
        refundReason = (TextView) findViewById(R.id.refund_reason);
        refundReasn = (TextView) findViewById(R.id.refund_reasn);

        layRideInfo = (LinearLayout) findViewById(R.id.lay_ride_info);
        layCancelCharges = (LinearLayout) findViewById(R.id.lay_cancel_charge);
        layRefund = (LinearLayout) findViewById(R.id.lay_refund);
        layRefundReason = (LinearLayout) findViewById(R.id.lay_refund_reason);
        layCancelRide = (LinearLayout) findViewById(R.id.lay_cancel);
        layTrackRide = (LinearLayout) findViewById(R.id.lay_track);
        layPayment = (LinearLayout) findViewById(R.id.lay_payment);
        layEmailInvoice = (LinearLayout) findViewById(R.id.lay_email_invoice);
        layReportIssue = (LinearLayout) findViewById(R.id.lay_report_issue);
        layBill = (RelativeLayout) findViewById(R.id.lay_bill);
        layDDW = (LinearLayout) findViewById(R.id.lay_ddw);
        layDrop = (LinearLayout) findViewById(R.id.lay_drop);
        layRateDriver = (LinearLayout) findViewById(R.id.lay_rate_driver);
        layRateRider = (LinearLayout) findViewById(R.id.lay_rate_rider);

        layPrepaid = (RelativeLayout) findViewById(R.id.lay_prepaid);
        layDiscount = (RelativeLayout) findViewById(R.id.lay_discount);

        layWalletUsed = (RelativeLayout) findViewById(R.id.lay_wallet_used);
        layTip = (RelativeLayout) findViewById(R.id.lay_tip);
        layPaymentMode = (RelativeLayout) findViewById(R.id.lay_payment_mode);
        layAccept = (LinearLayout) findViewById(R.id.lay_accept);
        layDecline = (LinearLayout) findViewById(R.id.lay_decline);
        layCardUsed = (RelativeLayout) findViewById(R.id.lay_card_info);

        layFareInfo = (LinearLayout) findViewById(R.id.lay_fare);

        discount = (TextView) findViewById(R.id.discount);
        baseFare = (TextView) findViewById(R.id.base_fare);
        processFee = (TextView) findViewById(R.id.process_fee);
        waitCharges = (TextView) findViewById(R.id.wait_charges);
        //gst = (TextView) findViewById(R.id.gst);
        line = (TextView) findViewById(R.id.line);

        layCancelRide.setOnClickListener(this);
        layTrackRide.setOnClickListener(this);
        layPayment.setOnClickListener(this);
        layEmailInvoice.setOnClickListener(this);
        layReportIssue.setOnClickListener(this);
        layAccept.setOnClickListener(this);
        layDecline.setOnClickListener(this);
        layRateRider.setOnClickListener(this);
        layRateDriver.setOnClickListener(this);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NotificationFilters.REQUEST_CANCELLED)) {
                    //if (session.getUserType().equals("4")) {
                    try {
                        JSONObject obj = new JSONObject(intent.getStringExtra("rideData"));
                        msg = obj.getString("message");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showPopup(RideDetail.this, msg);
                            }
                        }, 500);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //}
                }
            }
        };
    }

    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 101) {
                layRateDriver.setVisibility(View.GONE);
                layReportIssue.setVisibility(View.VISIBLE);
                layEmailInvoice.setVisibility(View.VISIBLE);
            } else if (requestCode == 102) {
                layRateRider.setVisibility(View.GONE);
                layReportIssue.setVisibility(View.VISIBLE);
                //layEmailInvoice.setVisibility(View.VISIBLE);
            }
        } else if (resultCode == RESULT_CANCELED) {

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lay_rate_driver:
                startActivityForResult(new Intent(this, RatingToDriver.class)
                        .putExtra("isFavourite", isDriverFavourited)
                        .putExtra("bookingID", bookingID).putExtra("is_back", ""), 101);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.lay_rate_rider:
                startActivityForResult(new Intent(this, RatingToPassenger.class)
                        .putExtra("bookingId", bookingID).putExtra("is_back", ""), 102);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.lay_cancel:
                showCancelRideDialog();
                /*showRideDialog(0, getResources().getString(R.string.cancel_trip)
                        , getResources().getString(R.string.cancel_ride_alert));*/
                break;
            case R.id.lay_track:
                trackDriver();
                break;
            case R.id.lay_payment:
                if (session.getUserType().equals("3")) {
                    Intent intent3 = new Intent(RideDetail.this, FareBreakup.class);
                    intent3.putExtra("status", status);
                    intent3.putExtra("bookingID", bookingID);
                    intent3.putExtra("isBack", "");
                    intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent3);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    RideDetail.this.finish();
                } else if (session.getUserType().equals("4")) {
                    if (bookingType.equals("1") && paymentStatus.equals("0")) {
                        //Intent intent3 = new Intent(RideDetail.this, FareBreakdown.class);
                        Intent intent3 = new Intent(RideDetail.this, AcceptPayment.class);
                        intent3.putExtra("bookingID", bookingID);
                        intent3.putExtra("type", bookingType);
                        intent3.putExtra("cardStatus", cardStatus);
                        intent3.putExtra("cardDetail", cardDetail);
                        intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent3);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else
                        requestPayment();
                }
                break;
            case R.id.lay_email_invoice:
                showRideDialog(1, "Generate Invoice", "Please confirm an invoice should be sent to you by email?");
                break;
            case R.id.lay_report_issue:
                startActivity(new Intent(this, ReportIssue.class).putExtra("bookingID", bookingID));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.lay_accept:
                acceptRejectRide("1");
                break;
            case R.id.lay_decline:
                acceptRejectRide("2");
                break;
        }
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(RideDetail.this, true);
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

    private void Success(String title, String message, final JSONObject outJson) {
        final RideDialog mDialog = new RideDialog(RideDetail.this, false, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (statusInfo.equals("1")) {
                    try {
                        JSONObject datObj = outJson.getJSONObject(Key.DATA);
                        startActivity(new Intent(RideDetail.this, DriverArrived.class)
                                //startActivity(new Intent(RideDetail.this, StartRiding.class)
                                .putExtra("bookingId", bookingID)
                                .putExtra("pickUp", puInfo)
                                .putExtra("type", bookingType)
                                .putExtra("payment_status", "0")
                                .putExtra("driverCategory", driverCat)
                                .putExtra("passenger_name", datObj.getString("name"))
                                .putExtra("passenger_phone", datObj.getString("phone"))
                                .putExtra("dropOff", doInfo));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        RideDetail.this.finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else RideDetail.this.finish();
            }
        });
        mDialog.show();
    }

    private void appendData(Ride ride) {
        paymentStatus = ride.getPaymentStatus();
        bookingType = ride.getBookingType();
        bookingID = ride.getId();
        status = ride.getStatusId();
        puInfo = ride.getPuInfo();
        doInfo = ride.getDoInfo();
        custName = ride.getpName();
        custPhone = ride.getpPhone();
        driverCat = ride.getDriverCategory();
        puAddress.setText(ride.getPickup());
        isDiscount = ride.isDiscount();
        statusInfo = ride.getStatusInfo();
        puDateTime = ride.getPuDate();
        isDriverFavourited = ride.isDriverFavourited();

        cardStatus = ride.getCardStatus();
        cardDetail = ride.getCardDetail();

        setColor(status);


        /*if (!status.equals("10") && paymentStatus.equals("1") && statusInfo.equals("1")) {
            if (!status.equals("7"))
                layPrepaid.setVisibility(View.VISIBLE);
        } else*/
        if (!status.equals("10") /*&& paymentStatus.equals("0")*/ && statusInfo.equals("1")) {
            layPrepaid.setVisibility(View.VISIBLE);
            asapStatus.setText("AS SOON AS POSSIBLE");
        } else layPrepaid.setVisibility(View.GONE);

        rideID.setText("RIDE_ID: " + bookingID);

        if (ride.getDropoff().equals(""))
            doAddress.setText("Not Selected");
        else
            doAddress.setText(ride.getDropoff());
        int ride_status = Integer.parseInt(ride.getStatusId());
        if (bookingType.equals("1") && paymentStatus.equals("0") && ride_status < 5) {
            txtDuration.setText("Estimated Time");
            bill.setText("ESTIMATED FARE");
        }

        if (!ride.getPuDate().equals("0") && !ride.getPuDate().equals("null")) {
            String date = ride.getPuDate();
            String day = date.substring(0, date.indexOf(" "));
            String time = date.substring(date.indexOf(" "), date.length());
            puDate.setText("Pick Up Time: " + day);
            puTime.setText(time);
        }

        if (!ride.getDoDate().equals("0") && !ride.getDoDate().equals("null")) {
            String date = ride.getDoDate();
            String day = date.substring(0, date.indexOf(" "));
            String time = date.substring(date.indexOf(" "), date.length());
            doDate.setText("Drop Off Time: " + day);
            doTime.setText(time);
            layDrop.setVisibility(View.VISIBLE);
        } else layDrop.setVisibility(View.GONE);

        if (!ride.getDistance().equals("") && !ride.getDistance().equals("null"))
            distance.setText(meterToKM(ride.getDistance()));

        if (ride.getDuration().contains("-"))
            timeTaken.setText(secondsToMin(ride.getDuration().substring(1)));
        else
            timeTaken.setText(secondsToMin(ride.getDuration()));

        if (!ride.getWaitTime().equals("0") && !ride.getWaitTime().contains(":")) {
            waitTime.setText("00:" + ride.getWaitTime());
        } else
            waitTime.setText(ride.getWaitTime());

        //Display fare summary
        if (!ride.getFareInfo().equals("null") && !ride.getFareInfo().equals("")) {
            layFareInfo.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            try {
                JSONObject fareObj = new JSONObject(ride.getFareInfo());
                String currency = fareObj.get("currency").toString();
                /*if (bookingType.equals("1") && !ride.getDiscountAmt().equals("0")) {
                    //baseFare.setText(currency + fareObj.get("normalFare").toString());
                    baseFare.setText(currency + fareObj.get("fare").toString());
                } else */
                if (bookingType.equals("1") && Integer.parseInt(ride.getStatusId()) < 5 &&
                        !ride.getDiscountAmt().equals("0")) {
                    baseFare.setText(currency + getTotal(fareObj.get("normalFare").toString()));
                } else
                    baseFare.setText(currency + getTotal(fareObj.get("fare").toString()));
                processFee.setText(currency + fareObj.get("processFee").toString());
                waitCharges.setText(currency + fareObj.get("waitTimeCharges").toString());
                //gst.setText(fareObj.get("serviceTax").toString()+"%");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            layFareInfo.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }

        if (!ride.getDiscountAmt().equals("0") && !ride.getDiscountAmt().equals("") &&
                !ride.getDiscountAmt().equals("null")) {
            layDiscount.setVisibility(View.VISIBLE);
            if (ride.getDiscountType().equals("1"))
                discount.setText("$" + ride.getDiscountAmt() + " /" + " $ ");
            else if (ride.getDiscountType().equals("2"))
                discount.setText("$" + ride.getDiscountAmt() + " /" + " % ");
            else
                discount.setText("$" + ride.getDiscountAmt());
            String total = ride.getTotalBill();
            String disc = ride.getDiscountAmt();
            double actualFare = Double.parseDouble(total) + Double.parseDouble(disc);
            //totalBill.setText("$" + actualFare);
            totalBill.setText("$" + getTotal(total));
        } else {
            layDiscount.setVisibility(View.GONE);
            totalBill.setText("$" + getTotal(ride.getTotalBill()));
        }

        if (paymentStatus.equals("0")) {
            totalBillPaid.setText("Pending");
            paidAmount.setText("PAYMENT");
        } else {
            totalBillPaid.setText("$" + getTotal(ride.getTotalPaid()));
            paidAmount.setText("PAID AMOUNT");
            layPaymentMode.setVisibility(View.VISIBLE);
            paidVia.setText(ride.getPaidVia());
        }

        if (!ride.getCardNumber().equals("") && paymentStatus.equals("1")) {
            layCardUsed.setVisibility(View.VISIBLE);
            cardNumber.setText(ride.getCardNumber());
        }

        if (!ride.getUsedWalletAmount().equals("") &&
                !ride.getUsedWalletAmount().equals("0") &&
                !ride.getUsedWalletAmount().equals("null")) {
            walletUsed.setText("$" + ride.getUsedWalletAmount());
            layWalletUsed.setVisibility(View.VISIBLE);
        }
        String tp = ride.getTipAmount();
        tp = tp.substring(1, tp.length());
        tp.trim();
        if (Double.parseDouble(tp) > 0) {
            layTip.setVisibility(View.VISIBLE);
            tip.setText(ride.getTipAmount());
        }
        puLat = ride.getPuLat();
        puLng = ride.getPuLng();
        doLat = ride.getDoLat();
        doLng = ride.getDoLng();

        if (ride.getDistance().equals("") && ride.getDuration().equals(""))
            layDDW.setVisibility(View.GONE);
        else
            layDDW.setVisibility(View.VISIBLE);

        if (ride.getTotalBill().equals("") && ride.getTotalPaid().equals(""))
            layBill.setVisibility(View.GONE);
        else
            layBill.setVisibility(View.VISIBLE);

        if (ride.getDriverCategory().equals("1")) {
            icon.setImageResource(R.drawable.estimate_taxi);
        } else if (ride.getDriverCategory().equals("2")) {
            icon.setImageResource(R.drawable.estimate_economy);
        } else if (ride.getDriverCategory().equals("3")) {
            icon.setImageResource(R.drawable.estimate_premium);
        } else if (ride.getDriverCategory().equals("4")) {
            icon.setImageResource(R.drawable.estimate_motor_bike);
        }

        if (session.getUserType().equals("3")) {
            if (ride.getStatusId().equals("5") && paymentStatus.equals("0")) {
                payment.setText("MAKE PAYMENT");
                layPayment.setVisibility(View.VISIBLE);
                rideStatus.setText("AWAITING PAYMENT");
            } else if (ride.getStatusId().equals("10")) {
                if (paymentStatus.equals("0")) {
                    payment.setText("MAKE PAYMENT");
                    layPayment.setVisibility(View.VISIBLE);
                    rideStatus.setText("AWAITING PAYMENT");
                } else {
                    rideStatus.setText("COMPLETED");
                    if (ride.getDriverRating() <= 0) {
                        layRateDriver.setVisibility(View.VISIBLE);
                        layReportIssue.setVisibility(View.VISIBLE);
                    } else {
                        layEmailInvoice.setVisibility(View.VISIBLE);
                        layReportIssue.setVisibility(View.VISIBLE);
                    }
                    double refundAmt = Double.parseDouble(ride.getRefunAmount());
                    if (refundAmt > 0) {
                        layRefund.setVisibility(View.VISIBLE);
                        refundAmount.setText("$" + ride.getRefunAmount());
                    }
                    if (!ride.getRefundReason().equals("") && !ride.getRefundReason().equals("null")) {
                        if (ride.getRefundReason().length() > 25)
                            refundReason.setText(ride.getRefundReason());
                        else
                            refundReasn.setText(ride.getRefundReason());
                        layRefundReason.setVisibility(View.VISIBLE);
                    } else layRefundReason.setVisibility(View.GONE);
                }
            } else if (ride.getStatusId().equals("0")) {
                layCancelRide.setVisibility(View.VISIBLE);
                rideStatus.setText("REQUESTED");
                /*if (ride.getBookingType().equals("0")) {
                    layRideInfo.setVisibility(View.GONE);
                }*/
                if (ride.getDropoff().equals(""))
                    layRideInfo.setVisibility(View.GONE);
            } else if (ride.getStatusId().equals("1")) {
                layCancelRide.setVisibility(View.VISIBLE);
                layTrackRide.setVisibility(View.VISIBLE);
                rideStatus.setText("BOOKED");
                if (ride.getDropoff().equals(""))
                    layRideInfo.setVisibility(View.GONE);
            } else if (ride.getStatusId().equals("12")) {
                layCancelRide.setVisibility(View.VISIBLE);
                layTrackRide.setVisibility(View.VISIBLE);
                rideStatus.setText("START RIDE");
            } else if (ride.getStatusId().equals("3")) {
                layTrackRide.setVisibility(View.VISIBLE);
                rideStatus.setText("ARRIVED");
                if (ride.getDropoff().equals(""))
                    layRideInfo.setVisibility(View.GONE);
            } else if (ride.getStatusId().equals("4")) {
                layTrackRide.setVisibility(View.VISIBLE);
                rideStatus.setText("BEGIN");
            } else if (ride.getStatusId().equals("7")) {
                rideStatus.setText("CANCELLED");
                layRideInfo.setVisibility(View.GONE);
                if (paymentStatus.equals("1")) {
                    ccBill.setText("$" + ride.getTotalPaid());
                    ccPaidVia.setText(ride.getPaidVia());
                    ccCardNumber.setText(ride.getCardNumber());
                    layCancelCharges.setVisibility(View.VISIBLE);
                }
                double refundAmt = Double.parseDouble(ride.getRefunAmount());
                if (refundAmt > 0) {
                    layRefund.setVisibility(View.VISIBLE);
                    refundAmount.setText("$" + ride.getRefunAmount());
                }
                if (!ride.getRefundReason().equals("") && !ride.getRefundReason().equals("null")) {
                    if (ride.getRefundReason().length() > 25)
                        refundReason.setText(ride.getRefundReason());
                    else
                        refundReasn.setText(ride.getRefundReason());
                    layRefundReason.setVisibility(View.VISIBLE);
                } else layRefundReason.setVisibility(View.GONE);
                //rideStatus.setTextColor(Color.parseColor("#D42C15"));
            } else if (ride.getStatusId().equals("5") || ride.getStatusId().equals("6")) {
                if (paymentStatus.equals("1")) {
                    layEmailInvoice.setVisibility(View.GONE);
                    layReportIssue.setVisibility(View.VISIBLE);
                    rideStatus.setText("FINISHED");
                } else {
                    payment.setText("MAKE PAYMENT");
                    layPayment.setVisibility(View.VISIBLE);
                    rideStatus.setText("AWAITING PAYMENT");
                }
            }
        } else if (session.getUserType().equals("4")) {
            if (ride.getStatusId().equals("0") && bookingType.equals("0")) {
                layAccept.setVisibility(View.VISIBLE);
                rideStatus.setText("REQUESTED");
                if (ride.getDropoff().equals(""))
                    layRideInfo.setVisibility(View.GONE);
                layDecline.setVisibility(View.VISIBLE);
            } else if (ride.getStatusId().equals("0") && bookingType.equals("1")) {
                layAccept.setVisibility(View.VISIBLE);
                rideStatus.setText("RIDE REQUEST");
                layDecline.setVisibility(View.VISIBLE);
                if (ride.getDropoff().equals(""))
                    layRideInfo.setVisibility(View.GONE);
            } else if (ride.getStatusId().equals("1")) {
                //if(isTrackable(puDateTime))
                trackRide();
                if (statusInfo.equals("0"))
                    layCancelRide.setVisibility(View.VISIBLE);
                //layTrackRide.setVisibility(View.VISIBLE);
                rideStatus.setText("ACCEPTED");
                if (ride.getDropoff().equals(""))
                    layRideInfo.setVisibility(View.GONE);
            } else if (ride.getStatusId().equals("12")) {
                layTrackRide.setVisibility(View.VISIBLE);
                if (bookingType.equals("0"))
                    layRideInfo.setVisibility(View.GONE);
                rideStatus.setText("START RIDE");
            } else if (ride.getStatusId().equals("3")) {
                layTrackRide.setVisibility(View.VISIBLE);
                rideStatus.setText("BEGAN RIDE");
            } else if (ride.getStatusId().equals("4")) {
                layTrackRide.setVisibility(View.VISIBLE);
                rideStatus.setText("END RIDE");
            } else if (ride.getStatusId().equals("5") || ride.getStatusId().equals("6")) {
                if (paymentStatus.equals("0") && bookingType.equals("1")) {
                    payment.setText("ACCEPT PAYMENT");
                    layPayment.setVisibility(View.VISIBLE);
                    rideStatus.setText("AWAITING PAYMENT");
                } else if (paymentStatus.equals("1")) {
                    layEmailInvoice.setVisibility(View.GONE);
                    layReportIssue.setVisibility(View.VISIBLE);
                    rideStatus.setText("FINISHED");
                } else {
                    payment.setText("REQUEST PAYMENT");
                    layPayment.setVisibility(View.VISIBLE);
                    rideStatus.setText("AWAITING PAYMENT");
                }
            } else if (ride.getStatusId().equals("7")) {
                rideStatus.setText("CANCELLED");
                layRideInfo.setVisibility(View.GONE);
                if (paymentStatus.equals("1")) {
                    ccBill.setText("$" + ride.getTotalPaid());
                    ccPaidVia.setText(ride.getPaidVia());
                    ccCardNumber.setText(ride.getCardNumber());
                    layCancelCharges.setVisibility(View.VISIBLE);
                }
                double refundAmt = Double.parseDouble(ride.getRefunAmount());
                if (refundAmt > 0) {
                    layRefund.setVisibility(View.VISIBLE);
                    refundAmount.setText("$" + ride.getRefunAmount());
                }
                if (!ride.getRefundReason().equals("") && !ride.getRefundReason().equals("null")) {
                    if (ride.getRefundReason().length() > 25)
                        refundReason.setText(ride.getRefundReason());
                    else
                        refundReasn.setText(ride.getRefundReason());
                    layRefundReason.setVisibility(View.VISIBLE);
                } else layRefundReason.setVisibility(View.GONE);
                //rideStatus.setTextColor(Color.parseColor("#D42C15"));
            } else if (ride.getStatusId().equals("10")) {
                if (paymentStatus.equals("0")) {
                    payment.setText("PAYMENT IN-PROGRESS");
                    layPayment.setVisibility(View.VISIBLE);
                    layPayment.setOnClickListener(null);
                    rideStatus.setText("AWAITING PAYMENT");
                } else {
                    //layEmailInvoice.setVisibility(View.GONE);
                    //layReportIssue.setVisibility(View.VISIBLE);
                    rideStatus.setText("COMPLETED");
                    if (ride.getPassengerRating() <= 0) {
                        layRateRider.setVisibility(View.VISIBLE);
                        layReportIssue.setVisibility(View.VISIBLE);
                    } else {
                        layReportIssue.setVisibility(View.VISIBLE);
                        //layEmailInvoice.setVisibility(View.VISIBLE);
                    }
                    double refundAmt = Double.parseDouble(ride.getRefunAmount());
                    if (refundAmt > 0) {
                        layRefund.setVisibility(View.VISIBLE);
                        refundAmount.setText("$" + ride.getRefunAmount());
                    }
                    if (!ride.getRefundReason().equals("") && !ride.getRefundReason().equals("null")) {
                        if (ride.getRefundReason().length() > 25)
                            refundReason.setText(ride.getRefundReason());
                        else
                            refundReasn.setText(ride.getRefundReason());
                        layRefundReason.setVisibility(View.VISIBLE);
                    } else layRefundReason.setVisibility(View.GONE);
                }
            }
        }
        initMap();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.setMyLocationEnabled(false);
        drawRoute();
    }

    LatLng fromPosition;
    LatLng toPosition;
    String puLat = "", puLng = "", doLat = "", doLng = "";

    private void drawRoute() {
        if (!doLat.equals("") && !doLng.equals("")) {
            fromPosition = new LatLng(Double.parseDouble(puLat), Double.parseDouble(puLng));
            toPosition = new LatLng(Double.parseDouble(doLat), Double.parseDouble(doLng));
            if (fromPosition != null && toPosition != null) {
                GetRouteTask routeAsyncTask = new GetRouteTask();
                routeAsyncTask.execute();
            }
        } else {
            fromPosition = new LatLng(Double.parseDouble(puLat), Double.parseDouble(puLng));
            googleMap.addMarker(new MarkerOptions()
                    .position(fromPosition)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot)));
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                    fromPosition, 12.2f);
            googleMap.animateCamera(location);
        }
    }

    public class GetRouteTask extends AsyncTask<String, Void, String> {
        String response = "";
        GMapV2GetRouteDirection v2GetRouteDirection = new GMapV2GetRouteDirection();
        Document document;

        @Override
        protected String doInBackground(String... urls) {
            //Get All Route values
            document = v2GetRouteDirection.getDocument(toPosition, fromPosition, GMapV2GetRouteDirection.MODE_DRIVING);
            response = "Success";
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equalsIgnoreCase("Success")) {
                googleMap.clear();
                try {
                    ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
                    PolylineOptions rectLine = new PolylineOptions().width(15).color(getResources().getColor(R.color.red_color));
                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }
                    // Adding route on the map
                    googleMap.addPolyline(rectLine);
                    googleMap.addMarker(new MarkerOptions()
                            .position(fromPosition)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot)));
                    googleMap.addMarker(new MarkerOptions()
                            .position(toPosition)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_dot)));
                    //Show path in
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(fromPosition);
                    builder.include(toPosition);
                    LatLngBounds bounds = builder.build();
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 162));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    AlertDialog mDialog;

    private void showRideDialog(final int type, String title, String message) {
        mDialog = new AlertDialog(RideDetail.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setNegativeButton(getResources().getString(R.string.timer_label_alert_yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (Internet.hasInternet(RideDetail.this)) {
                    if (type == 0)
                        cancelRide();
                    else if (type == 1)
                        sendInvoiceToPassenger();
                } else {
                    Alert("Alert!", getResources().getString(R.string.no_internet));
                }
            }
        });
        mDialog.setPositiveButton(getResources().getString(R.string.timer_label_alert_no), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void sendInvoiceToPassenger() {
        HashMap<String, String> params = new HashMap<>();
        params.put("booking_id", bookingID);
        APIHandler mRequest = new APIHandler(this, HTTPMethods.POST, new AsyncTaskCompleteListener() {
            @Override
            public void onTaskComplete(String result) {
                try {
                    JSONObject outerJson = new JSONObject(result);
                    if (outerJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                        Alert("Success!", outerJson.getString(Key.MESSAGE));
                    } else
                        Alert("Alert!", outerJson.getString(Key.MESSAGE));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
        mRequest.execute(Config.SEND_INVOICE, session.getToken());
    }

    private void cancelRide() {
        APIHandler mRequest = new APIHandler(RideDetail.this, HTTPMethods.PUT, new AsyncTaskCompleteListener() {
            @Override
            public void onTaskComplete(String result) {
                try {
                    JSONObject outJson = new JSONObject(result);
                    if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS ||
                            outJson.getInt(Key.STATUS) == APIStatus.GUARANTEE_AVAILABLE) {
                        final RideDialog mDialog = new RideDialog(RideDetail.this, false, true);
                        mDialog.setDialogTitle("Success!");
                        mDialog.setDialogMessage(outJson.getString(Key.MESSAGE));
                        mDialog.setPositiveButton(getResources().getString(R.string.timer_label_alert_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                RideDetail.this.finish();
                            }
                        });
                        mDialog.show();
                    } else if (outJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                        cancelFailureAlert("Alert!", outJson.getString(Key.MESSAGE));
                    } else
                        Alert("Alert!", outJson.getString(Key.MESSAGE));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        mRequest.execute(Config.CANCEL_BOOKING + bookingID, session.getToken());
    }

    private void requestPayment() {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("status", "6");
        APIHandler mRequest = new APIHandler(RideDetail.this, HTTPMethods.PUT, new AsyncTaskCompleteListener() {
            @Override
            public void onTaskComplete(String result) {
                try {
                    JSONObject outJson = new JSONObject(result);
                    if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                        Intent intent3 = new Intent(RideDetail.this, Waiting.class);
                        intent3.putExtra("status", status);
                        intent3.putExtra("bookingID", bookingID);
                        intent3.putExtra("driverCategory", driverCat);
                        intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent3);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else
                        Alert("Alert!", outJson.getString(Key.MESSAGE));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, jsonParams);
        mRequest.execute(Config.BOOKING_STATUS + bookingID, session.getToken());
    }

    private void trackDriver() {
        if (session.getUserType().equals("3")) {
            Intent trackIntent = new Intent(RideDetail.this, TrackDetail.class);
            trackIntent.putExtra("status", status);
            trackIntent.putExtra("driverCategory", driverCat);
            trackIntent.putExtra("bookingID", bookingID);
            startActivity(trackIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            RideDetail.this.finish();
        } else if (session.getUserType().equals("4")) {
            if (status.equals("1")) {
                startActivity(new Intent(RideDetail.this, DriverArrived.class)
                        //startActivity(new Intent(RideDetail.this, StartRiding.class)
                        .putExtra("bookingId", bookingID)
                        .putExtra("pickUp", puInfo)
                        .putExtra("type", bookingType)
                        .putExtra("payment_status", paymentStatus)
                        .putExtra("passenger_name", custName)
                        .putExtra("passenger_phone", custPhone)
                        .putExtra("driverCategory", driverCat)
                        .putExtra("is_discount", isDiscount)
                        .putExtra("dropOff", doInfo));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                RideDetail.this.finish();
            } else if (status.equals("12")) {
                startActivity(new Intent(RideDetail.this, DriverArrived.class)
                        .putExtra("bookingId", bookingID)
                        .putExtra("pickUp", puInfo)
                        .putExtra("type", bookingType)
                        .putExtra("payment_status", paymentStatus)
                        .putExtra("passenger_name", custName)
                        .putExtra("passenger_phone", custPhone)
                        .putExtra("driverCategory", driverCat)
                        .putExtra("is_discount", isDiscount)
                        .putExtra("dropOff", doInfo));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                RideDetail.this.finish();
            } else if (status.equals("3")) {
                startActivity(new Intent(RideDetail.this, BeginRide.class)
                        .putExtra("bookingId", bookingID)
                        .putExtra("pickUp", puInfo)
                        .putExtra("type", bookingType)
                        .putExtra("payment_status", paymentStatus)
                        .putExtra("passenger_name", custName)
                        .putExtra("passenger_phone", custPhone)
                        .putExtra("driverCategory", driverCat)
                        .putExtra("is_discount", isDiscount)
                        .putExtra("dropOff", doInfo));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                RideDetail.this.finish();
            } else if (status.equals("4")) {
                startActivity(new Intent(RideDetail.this, EndRide.class)
                        .putExtra("bookingId", bookingID)
                        .putExtra("pickUp", puInfo)
                        .putExtra("type", bookingType)
                        .putExtra("passenger_name", custName)
                        .putExtra("passenger_phone", custPhone)
                        .putExtra("payment_status", paymentStatus)
                        .putExtra("driverCategory", driverCat)
                        .putExtra("is_discount", isDiscount)
                        .putExtra("dropOff", doInfo));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                RideDetail.this.finish();
            } else if (status.equals("5")) {
                Intent intent3 = new Intent(RideDetail.this, AcceptPayment.class);
                intent3.putExtra("bookingID", bookingID);
                intent3.putExtra("type", bookingType);
                intent3.putExtra("cardStatus", cardStatus);
                intent3.putExtra("cardDetail", cardDetail);
                startActivity(intent3);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                RideDetail.this.finish();
            }
        }
    }

    private void acceptRejectRide(final String status) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("status", status);
        if (status.equals("1"))
            jsonParams.put("donotSendNotification", "1");
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(RideDetail.this, HTTPMethods.PUT, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    try {
                        JSONObject outJson = new JSONObject(result);
                        if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                            if (status.equals("1")) {
                                if (bookingType.equals("1")) {
                                    Success("Success!", outJson.getString(Key.MESSAGE), outJson);
                                } else {
                                    JSONObject datObj = outJson.getJSONObject(Key.DATA);
                                    startActivity(new Intent(RideDetail.this, DriverArrived.class)
                                            //startActivity(new Intent(RideDetail.this, StartRiding.class)
                                            .putExtra("bookingId", bookingID)
                                            .putExtra("pickUp", puInfo)
                                            .putExtra("type", bookingType)
                                            .putExtra("payment_status", "0")
                                            .putExtra("driverCategory", driverCat)
                                            .putExtra("passenger_name", datObj.getString("name"))
                                            .putExtra("passenger_phone", datObj.getString("phone"))
                                            .putExtra("dropOff", doInfo));
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    RideDetail.this.finish();
                                }
                            } else
                                Error("Success!", outJson.getString(Key.MESSAGE));
                        } else {
                            Error("Alert!", outJson.getString(Key.MESSAGE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, jsonParams);
            apiHandler.execute(Config.ACCEPT_BOOKING + bookingID, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void getRidesInfo(String id) {
        String endPoint = Config.BOOKING_LIST + "/" + id + "?expand=drivergeo,passanger";
        Log.e("endPoint",endPoint);
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    try {
                        JSONObject outJson = new JSONObject(result);
                        if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                            JSONObject obj = outJson.getJSONObject(Key.DATA);
                            String pick = obj.getString(Key.PICKUP);
                            Ride ride = new Ride();
                            if (!pick.equals("null") && !pick.equals("")) {
                                JSONObject pickup = new JSONObject(pick);
                                ride.setPuInfo(pickup.toString());
                                ride.setPickup(pickup.getString(Key.ADDRESS));
                                ride.setPuLat(pickup.getString("lat"));
                                ride.setPuLng(pickup.getString("lng"));
                            }
                            String drop = obj.getString(Key.DROPOFF);
                            if (drop != null && !drop.equals("null")) {
                                JSONObject dropOff = new JSONObject(drop);
                                ride.setDoInfo(dropOff.toString());
                                ride.setDropoff(dropOff.getString(Key.ADDRESS));
                                ride.setDoLat(dropOff.getString("lat"));
                                ride.setDoLng(dropOff.getString("lng"));
                            }
                            if (obj.has("isFavorite"))
                                ride.setDriverFavourited(obj.get("isFavorite").equals("1") ? true : false);
                            else ride.setDriverFavourited(false);

                            if (obj.has("refundAmount"))
                                ride.setRefunAmount(obj.get("refundAmount").toString());
                            else ride.setRefunAmount("");

                            if (obj.has("refundReason"))
                                ride.setRefundReason(obj.getString("refundReason"));
                            else ride.setRefundReason("");

                            ride.setCardStatus(obj.get("cardStatus").toString());
                            ride.setCardDetail(obj.get("cardDetail").toString());
                            ride.setDriverCategory(obj.getString("driverCategory_id"));
                            ride.setPuDate(obj.getString("pickUpDate"));
                            ride.setId(obj.getString(Key.ID));
                            ride.setStatus(obj.getString("statusText"));
                            ride.setPaymentStatus(obj.get("paymentStatus").toString());
                            ride.setBookingType(obj.getString("type"));
                            ride.setStatusId(obj.get("status").toString());
                            ride.setDoDate(obj.getString("dropOffDate"));
                            ride.setDuration(obj.getString("duration"));
                            ride.setStatusInfo(obj.get(Key.ASAP).toString());
                            //ride.setDiscount(obj.get("is_discount").toString().equals("1")?true:false);
                            ride.setWaitTime(obj.get("waitTime").toString());
                            String fareDetail = obj.getString("fareDetail");
                            if (!fareDetail.equals("") && !fareDetail.equals("null")) {
                                JSONObject fareObj = new JSONObject(fareDetail);
                                ride.setFareInfo(fareObj.toString());
                                String currency;
                                if (fareObj.has("currency"))
                                    currency = fareObj.get("currency").toString();
                                else
                                    currency = "$";
                                if (!obj.get("status").toString().equals("7")) {
                                    ride.setDistance(obj.get("distance").toString());
                                    if (fareObj.has("discount"))
                                        ride.setDiscountAmt(fareObj.get("discount").toString());
                                    if (fareObj.has("discountType"))
                                        ride.setDiscountType(fareObj.get("discountType").toString());
                                    if (fareObj.has("waitTimeCharges"))
                                        ride.setWaitingCharge(fareObj.getString("waitTimeCharges"));
                                    if (fareObj.has("tip")) {
                                        String total = fareObj.getString("total");
                                        String tp = fareObj.getString("tip");
                                        double totalPaid = Double.parseDouble(total) + Double.parseDouble(tp);
                                        ride.setTotalBill(fareObj.getString("total"));
                                        ride.setTotalPaid(currency + totalPaid);
                                        ride.setTipAmount(currency + fareObj.getString("tip"));
                                    } else {
                                        ride.setTotalBill(fareObj.getString("total"));
                                        ride.setTotalPaid(currency + fareObj.getString("total"));
                                    }
                                }
                            }
                            String paymentDetail = obj.getString("payment_amount_detail");
                            if (!paymentDetail.equals("") && !paymentDetail.equals("null")) {
                                JSONObject paymentObj = new JSONObject(paymentDetail);
                                ride.setUsedWalletAmount(paymentObj.get("Wallet_amt_used").toString());
                                ride.setTotalPaid(paymentObj.get("amount").toString());
                                ride.setPaidVia(paymentObj.get("payment_gateway").toString());
                            }
                            String paymentInfo = obj.get("paymentDetail").toString();
                            if (!paymentInfo.equals("") && !paymentInfo.equals("null")) {
                                JSONObject payObj = new JSONObject(paymentInfo);
                                if (payObj.has("card"))
                                    ride.setCardNumber(payObj.getString("card"));
                            }
                            String passengerInfo = obj.getString("passanger");
                            if (!passengerInfo.equals("") && !passengerInfo.equals("null")) {
                                JSONObject passengerObj = new JSONObject(passengerInfo);
                                ride.setpName(passengerObj.getString("fullName"));
                                if (passengerObj.getString("dial_code").equals("null"))
                                    ride.setpPhone(passengerObj.getString("phone"));
                                else
                                    ride.setpPhone(passengerObj.getString("dial_code") + passengerObj.getString("phone"));
                            }

                            String rating = obj.get("rating").toString();
                            if (!rating.equals("") && !rating.equals("null")) {
                                JSONObject ratingObj = new JSONObject(rating);
                                ride.setDriverRating(ratingObj.getDouble("rateToDriver"));
                                ride.setPassengerRating(ratingObj.getDouble("rateToCustomer"));
                            }
                            appendData(ride);
                        } else {
                            Alert("Alert!", outJson.getString(Key.MESSAGE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
            apiHandler.execute(endPoint, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void Error(String title, String message) {
        final RideDialog mDialog = new RideDialog(RideDetail.this, false, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                RideDetail.this.finish();
            }
        });
        mDialog.show();
    }

    private void setColor(String status) {
        switch (status) {
            case "0":
                rideStatus.setBackgroundColor(Color.parseColor("#f6b803"));
                break;
            case "1":
                rideStatus.setBackgroundColor(Color.parseColor("#79ad2e"));
                break;
            case "3":
                rideStatus.setBackgroundColor(Color.parseColor("#0066b0"));
                break;
            case "4":
                rideStatus.setBackgroundColor(Color.parseColor("#e46c0b"));
                break;
            case "5":
                rideStatus.setBackgroundColor(Color.parseColor("#990066"));
                break;
            case "6":
                rideStatus.setBackgroundColor(Color.parseColor("#000066"));
                break;
            case "7":
                rideStatus.setBackgroundColor(Color.parseColor("#d52c15"));
                break;
            case "10":
                if (paymentStatus.equals("0"))
                    rideStatus.setBackgroundColor(Color.parseColor("#990066"));
                else
                    rideStatus.setBackgroundColor(Color.parseColor("#006666"));
                break;
        }
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentDateAndTime = sdf.format(new Date());
        return currentDateAndTime;
    }

    private void isTrackable(String pickupDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentDate = getCurrentDateTime();
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(currentDate);
            d2 = format.parse(pickupDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Get msec from each, and subtract.
        long diff = d2.getTime() - d1.getTime();
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);

        Log.e("Diff-min", diffMinutes + " minutes");
        Log.e("Diff-hour", diffHours + " hours");

        if (diffHours <= 0 && diffMinutes <= 15) {
            updateTimer.cancel();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    layTrackRide.setVisibility(View.VISIBLE);
                    layCancelRide.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void trackRide() {
        updateTimer.schedule(new TimerTask() {
            public void run() {
                isTrackable(puDateTime);
            }
        }, 0, 30000);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();

        if (updateTimer != null)
            updateTimer.cancel();
    }

    private void showCancelRideDialog() {
        mDialog = new AlertDialog(RideDetail.this, false);
        mDialog.setDialogTitle("Alert!");
        mDialog.setDialogMessage(getResources().getString(R.string.cancel_ride_alert));
        mDialog.setNegativeButton("CHECK FEES", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (Internet.hasInternet(RideDetail.this)) {
                    //cancelRide();
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

    private void getCancellationCharges() {
        APIHandler mRequest = new APIHandler(RideDetail.this, HTTPMethods.PUT, new AsyncTaskCompleteListener() {
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
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(RideDetail.this, true);
        mDialog.setDialogTitle("Alert!");
        mDialog.setDialogMessage(msg);
        mDialog.setNegativeButton(action, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (session.getUserType().equals("3"))
                    cancelRide();
                else if (session.getUserType().equals("4"))
                    declinedByDriver();
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

    private void declinedByDriver() {
        if (Internet.hasInternet(RideDetail.this)) {
            APIHandler mRequest = new APIHandler(RideDetail.this, HTTPMethods.PUT, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    try {
                        JSONObject outJson = new JSONObject(result);
                        if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS ||
                                outJson.getInt(Key.STATUS) == APIStatus.GUARANTEE_AVAILABLE) {
                            final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(RideDetail.this, true);
                            mDialog.setDialogTitle("Success!");
                            mDialog.setDialogMessage(outJson.getString(Key.MESSAGE));
                            mDialog.setPositiveButton(getResources().getString(R.string.timer_label_alert_ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    RideDetail.this.finish();
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

    private void cancelFailureAlert(String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(RideDetail.this, false);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setCancelOnTouchOutside(false);
        mDialog.setNegativeButton("Use Another Card", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                startActivityForResult(new Intent(RideDetail.this, MyCard.class)
                        .putExtra("isBack", ""), 0);
            }
        });
        mDialog.setPositiveButton("Ignore", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    /*private String getTotal(String totalAmount) {
        if (totalAmount.contains(".")) {
            if (totalAmount.startsWith("."))
                totalAmount = "0" + totalAmount;
            String counts = totalAmount.substring(totalAmount.indexOf(".") + 1, totalAmount.length());
            Log.e("counts", ""+counts);
            int count = counts.length();
            Log.e("count", ""+count);
            if (count > 3)
                totalAmount = totalAmount.substring(0, totalAmount.indexOf(".") + 3);
            else if (count > 2)
                totalAmount = totalAmount.substring(0, totalAmount.indexOf(".") + 2);
        }
        return totalAmount;
    }*/
    private String getTotal(String totalAmount) {
        if (totalAmount.contains(".")) {
            if (totalAmount.startsWith("."))
                totalAmount = "0" + totalAmount;
            double actTotal = Double.parseDouble(totalAmount);
            totalAmount = String.format("%.2f", actTotal);
        }
        return totalAmount;
    }

}
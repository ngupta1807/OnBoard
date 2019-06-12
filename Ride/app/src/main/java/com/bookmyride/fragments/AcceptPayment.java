package com.bookmyride.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bookmyride.R;
import com.bookmyride.activities.DriverHome;
import com.bookmyride.activities.FareBreakdown;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by vinod on 4/18/2017.
 */
public class AcceptPayment extends AppCompatActivity implements AsyncTaskCompleteListener {
    SessionHandler session;
    RelativeLayout paypal;
    CardView layCreditCard;
    TextView ccInfo;
    BroadcastReceiver mReceiver;
    RelativeLayout layId, layWalletAmtUsed, layRideFare, layDiscount;
    TextView rideID, walletAmtUsed, rideFare, discount, cashAmt, terminalAmt, cardAmt;
    LinearLayout cash, terminal;

    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_type);
        init();
        getEstimate();
    }

    private void init() {
        session = new SessionHandler(this);
        bookingID = getIntent().getStringExtra("bookingID");
        bookingType = getIntent().getStringExtra("type");
        cash = (LinearLayout) findViewById(R.id.cash);
        terminal = (LinearLayout) findViewById(R.id.terminal);
        layCreditCard = (CardView) findViewById(R.id.cv_creditCard);
        paypal = (RelativeLayout) findViewById(R.id.paypal);
        cash.setOnClickListener(onCashListener);
        terminal.setOnClickListener(onTerminalListener);
        layCreditCard.setOnClickListener(onCardListener);
        paypal.setOnClickListener(onPaypalListener);
        ccInfo = (TextView) findViewById(R.id.cc_info);

        layId = (RelativeLayout) findViewById(R.id.lay_id);
        layWalletAmtUsed = (RelativeLayout) findViewById(R.id.lay_wallet_used);
        layRideFare = (RelativeLayout) findViewById(R.id.lay_fare);
        layDiscount = (RelativeLayout) findViewById(R.id.lay_discount);
        rideID = (TextView) findViewById(R.id.ride_id);
        walletAmtUsed = (TextView) findViewById(R.id.wallet_amt_used);
        rideFare = (TextView) findViewById(R.id.ride_bill);
        discount = (TextView) findViewById(R.id.discount);

        cashAmt = (TextView) findViewById(R.id.cash_amt);
        terminalAmt = (TextView) findViewById(R.id.terminal_amt);
        cardAmt = (TextView) findViewById(R.id.card_amt);

        if (getIntent().hasExtra("cardStatus")) {
            String cardStatus = getIntent().getStringExtra("cardStatus");
            if (cardStatus.equals("0") || cardStatus.equals("") || cardStatus.equals("null")) {
                layCreditCard.setVisibility(View.GONE);
            } else {
                String cardInfo = getIntent().getStringExtra("cardDetail");
                if (cardInfo != null && !cardInfo.equals("null") && !cardInfo.equals("")) {
                    try {
                        JSONObject cardJson = new JSONObject(cardInfo);
                        layCreditCard.setVisibility(View.VISIBLE);
                        //ccInfo.setText("This passenger has credit card having number " + cardJson.getString("number") + ". We are accept credit card.");
                        ccInfo.setText("Click here if passenger wants to pay using saved credit card " + cardJson.getString("number"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    layCreditCard.setVisibility(View.GONE);
                }
            }
        } else {
            layCreditCard.setVisibility(View.GONE);
        }

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
                                message.setText("Payment successfully done.");
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
                                    Intent intent = new Intent(getApplicationContext(), FareBreakdown.class);
                                    intent.putExtra("type", bookingType);
                                    intent.putExtra("bookingID", bookingID);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    session.saveWaitingInfo(0L, 0L, 0L, 0L, 0, 0);
                                    AcceptPayment.this.finish();
                                }
                            }, 300);
                        }
                    }
                }
            }
        };
    }

    public View.OnClickListener onTerminalListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //displaySelectedScreen("terminal");
            showDialog();
        }
    };
    public View.OnClickListener onCashListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            proceedPrompt(1, "Do you want to proceed for payment using cash?");
            //acceptPayment();
        }
    };
    public View.OnClickListener onCardListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            proceedPrompt(2, "Are you sure you want to proceed with payment using the saved credit card on the system?");
            //displaySelectedScreen("credit_card");
        }
    };
    public View.OnClickListener onPaypalListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            displaySelectedScreen("paypal");
        }
    };

    private void displaySelectedScreen(String type) {
        Intent intent = new Intent(AcceptPayment.this, FareBreakdown.class);
        intent.putExtra("type", type);
        intent.putExtra("bookingID", getIntent().getStringExtra("bookingID"));
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    String bookingID, bookingType;
    int type = 100;

    private void payByTerminal(String transactionId) {
        type = 100;
        HashMap<String, String> requestParam = new HashMap<>();
        requestParam.put("bookingId", bookingID);
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

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                if (type == 100 || type == 101 || type == 102) {
                    displaySelectedScreen(bookingType);
                } else if (type == 0) {
                    appendData(outJson.getJSONObject(Key.DATA));
                }
            } else if (outJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                if (outJson.get(Key.DATA) instanceof JSONArray) {
                    JSONArray jArray = outJson.getJSONArray(Key.DATA);
                    JSONObject innerObj = jArray.getJSONObject(0);
                    Error("Alert!", innerObj.getString(Key.MESSAGE));
                } else {
                    paymentFailure("Alert!", outJson.getString(Key.MESSAGE));
                }
            } else if (outJson.getInt(Key.STATUS) == APIStatus.DELETE_ERROR) {
                if (outJson.get(Key.DATA) instanceof JSONArray) {
                    JSONArray jArray = outJson.getJSONArray(Key.DATA);
                    JSONObject innerObj = jArray.getJSONObject(0);
                    Error("Alert!", innerObj.getString(Key.MESSAGE));
                } else {
                    paymentFailure("Alert!", outJson.getString(Key.MESSAGE));
                }
            } else {
                Error("Alert!", outJson.getString(Key.MESSAGE));
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

    double walletAmtused = 0;

    private void appendData(JSONObject data) {
        try {
            JSONObject fairObject = new JSONObject(data.getString("fareDetail"));
            String currency = fairObject.getString("currency");
            rideFare.setText(currency + getTotal(fairObject.getString("total")));
            rideID.setText(data.get("id").toString());

            String paymentDetail = data.getString("payment_amount_detail");
            if (!paymentDetail.equals("") && !paymentDetail.equals("null")) {
                JSONObject paymentObj = new JSONObject(paymentDetail);
                String walletAmountUsed = paymentObj.get("Wallet_amt_used").toString();
                if (!walletAmountUsed.equals("null")) {
                    walletAmtused = Double.parseDouble(getTotal(walletAmountUsed));
                    if (walletAmtused > 0) {
                        layWalletAmtUsed.setVisibility(View.VISIBLE);
                        walletAmtUsed.setText(currency + walletAmountUsed);
                    } else {
                        layWalletAmtUsed.setVisibility(View.GONE);
                    }
                } else {
                    layWalletAmtUsed.setVisibility(View.GONE);
                }
            } else {
                layWalletAmtUsed.setVisibility(View.GONE);
            }

            if (!fairObject.get("discount").toString().equals("") &&
                    !fairObject.get("discount").toString().equals("null")) {
                String disc = fairObject.get("discount").toString();
                if (Double.parseDouble(disc) > 0)
                    layDiscount.setVisibility(View.VISIBLE);
                else layDiscount.setVisibility(View.GONE);
                discount.setText(currency + disc);
            } else {
                layDiscount.setVisibility(View.GONE);
            }

            String payViaCash = data.get("payViaCash").toString();
            String payViaTerminal = data.get("PayViaTerminal").toString();
            String payViaCard = data.get("payViaCard").toString();
            if (!payViaCash.equals("") && !payViaCash.equals("null")) {
                double payableCashAmt = Double.parseDouble(getTotal(payViaCash));
                double actualPayableAmt = payableCashAmt - walletAmtused;
                if (actualPayableAmt >= 0)
                    cashAmt.setText(currency + getTotal("" + actualPayableAmt));
                else cashAmt.setText(currency + "0");
            }
            if (!payViaTerminal.equals("") && !payViaTerminal.equals("null")) {
                double payableTerminalAmt = Double.parseDouble(getTotal(payViaTerminal));
                double actualPayableAmt = payableTerminalAmt - walletAmtused;
                if (actualPayableAmt >= 0)
                    terminalAmt.setText(currency + getTotal("" + actualPayableAmt));
                else terminalAmt.setText(currency + "0");
            }
            if (!payViaCard.equals("") && !payViaCard.equals("null")) {
                double payableCardAmt = Double.parseDouble(getTotal(payViaCard));
                double actualPayableAmt = payableCardAmt - walletAmtused;
                if (actualPayableAmt >= 0)
                    cardAmt.setText(currency + getTotal("" + actualPayableAmt));
                else cardAmt.setText(currency + "0");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            layWalletAmtUsed.setVisibility(View.GONE);
        }
    }

    private void Error(final String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(AcceptPayment.this, true);
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
                    AcceptPayment.this.finish();
                }
            }
        });
        mDialog.show();
    }

    private void paymentFailure(final String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(AcceptPayment.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent = new Intent(AcceptPayment.this, FareBreakdown.class);
                intent.putExtra("type", bookingType);
                intent.putExtra("bookingID", bookingID);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                AcceptPayment.this.finish();
            }
        });
        mDialog.show();
    }

    private void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(AcceptPayment.this);
        View subView = inflater.inflate(R.layout.receipt_dialog, null);
        final EditText subEditText = (EditText) subView.findViewById(R.id.bid_price);
        TextView proceed = (TextView) subView.findViewById(R.id.send);
        TextView cancel = (TextView) subView.findViewById(R.id.cancel);
        AlertDialog.Builder builder = new AlertDialog.Builder(AcceptPayment.this);
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

    public void getEstimate() {
        type = 0;
        if (Internet.hasInternet(this)) {
            APIHandler apiCall = new APIHandler(this, HTTPMethods.GET, this, null);
            apiCall.execute(Config.ESTIMATE_RIDE + "/" + bookingID + "?expand=passanger,drivergeo", session.getToken());
        } else
            Error("Alert!", getResources().getString(R.string.no_internet));
    }

    private void acceptPayment() {
        this.type = 101;
        HashMap<String, String> requestParam = new HashMap<>();
        requestParam.put("bookingId", bookingID);
        requestParam.put("gateway", "cash");
        requestParam.put("tip", "0");
        requestParam.put("isSavedCard", "0");
        if (Internet.hasInternet(this)) {
            APIHandler apiCall = new APIHandler(this, HTTPMethods.POST, this, requestParam);
            apiCall.execute(Config.ACCEPT_BY_CASH, session.getToken());
        } else
            Error("Alert!", getResources().getString(R.string.no_internet));
    }

    private void payByCard() {
        this.type = 102;
        HashMap<String, String> requestParam = new HashMap<>();
        requestParam.put("bookingId", bookingID);
        requestParam.put("tip", "0");
        requestParam.put("gateway", "pinpay");
        requestParam.put("isSavedCard", "1");
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, requestParam);
            apiHandler.execute(Config.PAYMENT_BY_CARD, session.getToken());
        } else
            Error("Alert!", getResources().getString(R.string.no_internet));
    }

    private void proceedPrompt(final int type, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(AcceptPayment.this, true);
        mDialog.setDialogTitle("BookMyRide");
        mDialog.setDialogMessage(message);
        mDialog.setNegativeButton(getResources().getString(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (type == 1) {
                    acceptPayment();
                } else if (type == 2) {
                    payByCard();
                } else if (type == 3) {
                    //payByWallet();
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

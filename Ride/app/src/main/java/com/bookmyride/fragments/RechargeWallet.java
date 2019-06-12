package com.bookmyride.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.activities.MyCard;
import com.bookmyride.activities.PaymentBrowser;
import com.bookmyride.activities.WalletHistory;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.views.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by vinod on 4/18/2017.
 */
public class RechargeWallet extends Fragment implements AsyncTaskCompleteListener {
    SessionHandler session;
    TextView availableAmount, minAmount, midAmount, maxAmount, addByCard, addByPaypal;
    EditText amount;
    String minimumAmt, mediumAmt, maximumAmt;
    TextView viewHistory;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_money, null);
        init(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getWalletAmount();
    }

    private void init(View view) {
        minimumAmt = "10";
        mediumAmt = "50";
        maximumAmt = "100";
        session = new SessionHandler(getActivity());
        viewHistory = (TextView) view.findViewById(R.id.view_history);
        viewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), WalletHistory.class));
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        availableAmount = (TextView) view.findViewById(R.id.current_amt);
        minAmount = (TextView) view.findViewById(R.id.min_amt);
        midAmount = (TextView) view.findViewById(R.id.mid_amt);
        maxAmount = (TextView) view.findViewById(R.id.max_amt);
        addByCard = (TextView) view.findViewById(R.id.by_card);
        addByCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!amount.getText().toString().equals(""))
                    rechargeWallet(2);
                else
                    Alert("Oops!!!", "Please enter amount.");
            }
        });
        addByPaypal = (TextView) view.findViewById(R.id.by_paypal);
        addByPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!amount.getText().toString().equals(""))
                    rechargeWallet(3);
                else
                    Alert("Oops!!!", "Please enter amount.");
            }
        });
        amount = (EditText) view.findViewById(R.id.input_amt);
        //amount.addTextChangedListener(EditorWatcher);
        minAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount.setText(minimumAmt);
                /*minAmount.setBackgroundColor(0xFF009788);
                midAmount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                maxAmount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                minAmount.setTextColor(0xFFFFFFFF);
                midAmount.setTextColor(0xFF4E4E4E);
                maxAmount.setTextColor(0xFF4E4E4E);*/
                amount.setSelection(amount.getText().length());
            }
        });

        midAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount.setText(mediumAmt);
                /*midAmount.setBackgroundColor(0xFF009788);
                minAmount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                maxAmount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                minAmount.setTextColor(0xFF4E4E4E);
                midAmount.setTextColor(0xFFFFFFFF);
                maxAmount.setTextColor(0xFF4E4E4E);*/
                amount.setSelection(amount.getText().length());
            }
        });

        maxAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount.setText(maximumAmt);
                /*maxAmount.setBackgroundColor(0xFF009788);
                minAmount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                midAmount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                minAmount.setTextColor(0xFF4E4E4E);
                midAmount.setTextColor(0xFF4E4E4E);
                maxAmount.setTextColor(0xFFFFFFFF);*/
                amount.setSelection(amount.getText().length());
            }
        });
    }

    int type;

    private void rechargeWallet(int pos) {
        type = pos;
        HashMap<String, String> requestParam = new HashMap<>();
        requestParam.put(Key.AMOUNT, amount.getText().toString());
        if (pos == 2) {
            //requestParam.put(Key.GATEWAY, "westpac");
            requestParam.put(Key.GATEWAY, "pinpay");
        } else if (pos == 3)
            requestParam.put(Key.GATEWAY, "paypalCheckout");
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.POST, this, requestParam);
            apiHandler.execute(Config.RECHARGE_WALLET, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                amount.setText("");
                if (type == 1) {
                    JSONObject innerObj = outJson.getJSONObject(Key.DATA);
                    availableAmount.setText("$" + innerObj.get(Key.AVAILABLE_AMOUNT).toString());
                } else if (type == 2) {
                    JSONObject innerObj = outJson.getJSONObject(Key.DATA);
                    availableAmount.setText("$" + innerObj.get(Key.AVAILABLE_AMOUNT).toString());
                    Alert("Success!", outJson.getString(Key.MESSAGE));
                } else if (type == 3) {
                    JSONObject dataObj = outJson.getJSONObject(Key.DATA);
                    proceedDialog("Success!", outJson.getString(Key.MESSAGE), dataObj.getString("url"));
                }
            } else if (outJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                noCardAlert("Alert!", outJson.getString(Key.MESSAGE));
            } else
                Alert("Alert!", outJson.getString(Key.MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void proceedDialog(String title, String message, final String endPoint) {
        final AlertDialog mDialog = new AlertDialog(getActivity(), true);
        mDialog.setDialogTitle(title);
        //mDialog.showCross(true);
        mDialog.setDialogMessage(message);
        mDialog.setNegativeButton("Proceed", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                startActivity(new Intent(getActivity(), PaymentBrowser.class)
                        .putExtra("endPoint", endPoint).putExtra("bookingID", ""));
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

    private void getWalletAmount() {
        type = 1;
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET, this, null);
            apiHandler.execute(Config.GET_WALLET, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void Alert(final String title, String message) {
        final AlertDialog mDialog = new AlertDialog(getActivity(), true);
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

    private void noCardAlert(final String title, String message) {
        final AlertDialog mDialog = new AlertDialog(getActivity(), true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                startActivityForResult(new Intent(getActivity(), MyCard.class)
                        .putExtra("isBack", ""), 0);

            }
        });
        mDialog.show();
    }
}
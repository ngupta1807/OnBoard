package com.grabid.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.models.PaymentHistory;

import java.util.HashMap;

/**
 * Created by graycell on 4/9/17.
 */

public class WalletTransactionDetails extends Fragment implements View.OnClickListener,
        AsyncTaskCompleteListener {
    View view;
    TextView mShipmentId, mDate, mTotalAmount, mAmountPay, mReferral, mAmountPayStatus, mReferralh, mdeliveryName, mPaymentVia;
    RelativeLayout mWalletReferral, mTotalAmountRel, mPaymenyviaRlt;
    SessionManager session;
    PaymentHistory PaymentCard;
    String incoming_type;
    TextView mPaymentvh;


    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.transactiondetails, null);
        init(view);
        initTopBar();
        if (getArguments().containsKey("data")) {
            incoming_type = getArguments().getString("incoming_type");
            if (incoming_type.contentEquals("bank") || incoming_type.contentEquals("card")) {
                HashMap<String, PaymentHistory> map = (HashMap<String, PaymentHistory>) getArguments().getSerializable("data");
                PaymentCard = map.get("data");
            }
            appendData();
            //  getData();
            Log.v("", "");
        }

        return view;

    }

    public void getData() {
        String url = Config.SERVER_URL + Config.DRIVERTRANSACTIONS;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage("Alert!", getActivity().getResources().getString(R.string.no_internet));
    }

    private void appendData() {
        try {

            if (incoming_type != null && incoming_type.contentEquals("bank")) {
                if (PaymentCard.getPayable() == 0) {
                    mAmountPayStatus.setText("Total Amount Payable");
                    mAmountPay.setText("$" + PaymentCard.getPayToDriverAmount());
                } else {
                    mAmountPayStatus.setText("Total Amount Paid");
                    mAmountPay.setText("$" + PaymentCard.getPayToDriverAmount());
                }
                mShipmentId.setText(PaymentCard.getDeliveryId());
                try {
                    SpannableString text = new SpannableString(PaymentCard.getDate());
                    text.setSpan(new ForegroundColorSpan(getActivity().getResources().getColor(R.color.litegray)), 0, 10, 0);
                    mDate.setText(text, TextView.BufferType.SPANNABLE);
                } catch (Exception e) {
                    e.toString();
                }
                // mDate.setText(PaymentCard.getDate());
                //  mTotalAmount.setText("$" + PaymentCard.getAmount());
                mdeliveryName.setText(PaymentCard.getDeliveryTitle());
                mTotalAmountRel.setVisibility(View.GONE);
                mPaymentvh.setText("Payment Type");
               /* if (PaymentCard.getCommision_amount() != null && !PaymentCard.getCommision_amount().contentEquals("")) {
                    mReferralh.setText("Commision");
                    mReferral.setText("$" + PaymentCard.getCommision_amount());
                    mWalletReferral.setVisibility(View.GONE);

                } else*/
                mWalletReferral.setVisibility(View.GONE);


            } else if (incoming_type != null && incoming_type.contentEquals("card")) {
                mShipmentId.setText(PaymentCard.getDeliveryId());
                try {
                    SpannableString text = new SpannableString(PaymentCard.getDate());
                    text.setSpan(new ForegroundColorSpan(getActivity().getResources().getColor(R.color.litegray)), 0, 10, 0);
                    mDate.setText(text, TextView.BufferType.SPANNABLE);
                } catch (Exception e) {
                    e.toString();
                }
                // mDate.setText(PaymentCard.getDate());
                mTotalAmount.setText("$" + PaymentCard.getAmount());
//                mAmountPayStatus.setText("Amount Charged");
                mAmountPayStatus.setText("Total Amount Paid");
                mAmountPay.setText("$" + PaymentCard.getAmount());
                mdeliveryName.setText(PaymentCard.getDeliveryTitle());
                try {
                    if (PaymentCard.getChargedAmount() != null && !PaymentCard.getChargedAmount().contentEquals(PaymentCard.getAmount())) {
                        Double diff = Double.parseDouble(PaymentCard.getAmount()) - Double.parseDouble(PaymentCard.getChargedAmount());
                        double valueRounded = Double.parseDouble(PaymentCard.getAmount()) - Double.parseDouble(PaymentCard.getChargedAmount());
                        try {
                            valueRounded = Math.round(diff * 100D) / 100D;
                        } catch (Exception e) {
                            e.toString();
                        }
                        mReferralh.setText("Wallet Amount Used");
                        mReferral.setText("$" + String.valueOf(valueRounded));
                        mAmountPay.setText("$" + PaymentCard.getChargedAmount());
                    } else {
                        mWalletReferral.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.toString();
                }
            }
            if (PaymentCard.getPaymentMethodName() != null) {
                mPaymenyviaRlt.setVisibility(View.VISIBLE);
                mPaymentVia.setText(PaymentCard.getPaymentMethodName());
            } else
                mPaymenyviaRlt.setVisibility(View.GONE);
        } catch (Exception e) {
            e.toString();
        }


    }

    public boolean CheckdecimalLenght(Double d) {
        try {
            String[] splitter = d.toString().split("\\.");
            splitter[0].length();   // Before Decimal Count
            int decimalLength = splitter[1].length();  // After Decimal Count

            // if (decimalLength == 2)
            // valid
//else
            // invalid
        } catch (Exception e) {
            e.toString();
        }
        return false;
    }

    private void showMessage(String title, String message) {
        AlertManager.messageDialog(getActivity(), title, message);
    }

    private void init(View view) {
        session = new SessionManager(getActivity());
        mShipmentId = (TextView) view.findViewById(R.id.shipment_id);
        mDate = (TextView) view.findViewById(R.id.date);
        mTotalAmount = (TextView) view.findViewById(R.id.totalamount);
        mAmountPay = (TextView) view.findViewById(R.id.amount_pay);
        mReferral = (TextView) view.findViewById(R.id.referral);
        mWalletReferral = (RelativeLayout) view.findViewById(R.id.walletreferral);
        mTotalAmountRel = (RelativeLayout) view.findViewById(R.id.totalamountrel);
        mAmountPayStatus = (TextView) view.findViewById(R.id.amountpay_status);
        mReferralh = (TextView) view.findViewById(R.id.referralh);
        mdeliveryName = (TextView) view.findViewById(R.id.delivery_name);
        mPaymentVia = (TextView) view.findViewById(R.id.paymentvia);
        mPaymenyviaRlt = (RelativeLayout) view.findViewById(R.id.rltpaymentvia);
        mPaymentvh = (TextView) view.findViewById(R.id.headingpv);
    }

    private void initTopBar() {
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.title.setText(getResources().getString(R.string.transactiondetails));
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onTaskComplete(String result) {
        Log.d("data", result);
        handleResponse(result);
    }

    private void handleResponse(String result) {
        Log.v("", result.toString());

    }
}
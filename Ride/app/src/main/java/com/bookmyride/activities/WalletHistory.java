package com.bookmyride.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.adapters.TransactionAdapter;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.api.OnLoadMoreListener;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.models.Payment;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vinod on 6/14/2017.
 */
public class WalletHistory extends AppCompatActivity implements View.OnClickListener,
        AsyncTaskCompleteListener {
    ListView mList;
    LinearLayout rechargeHistory, paymentHistory, layTop;
    TextView rechargeView, paymentView;
    SessionHandler session;
    TextView noData;
    TransactionAdapter adapter;
    ArrayList<Payment> data = new ArrayList<Payment>();

    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_history);
        init();
        if (session.getUserType().equals("4")) {
            layTop.setVisibility(View.GONE);
        }
        updateUI(1);
    }

    /* Initialization of views*/
    private void init() {
        session = new SessionHandler(this);
        mList = (ListView) findViewById(R.id.list);
        adapter = new TransactionAdapter(this, data, 2, loadMore);
        mList.setAdapter(adapter);
        layTop = (LinearLayout) findViewById(R.id.lay_top);
        rechargeHistory = (LinearLayout) findViewById(R.id.recharge_history);
        rechargeHistory.setOnClickListener(this);
        paymentHistory = (LinearLayout) findViewById(R.id.payment_history);
        paymentHistory.setOnClickListener(this);
        rechargeView = (TextView) findViewById(R.id.h_view);
        paymentView = (TextView) findViewById(R.id.d_view);
        noData = (TextView) findViewById(R.id.no_data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recharge_history:
                updateUI(1);
                break;
            case R.id.payment_history:
                updateUI(2);
                break;
        }
    }

    int selectedType;
    int pageCount, currentPage = 0;

    private void updateUI(int type) {
        selectedType = type;
        if (type == 1) {
            currentPage = 0;
            getHistory("1");
            rechargeView.setBackgroundResource(R.color.driver_color);
            paymentView.setBackgroundResource(R.color.white);
        } else if (type == 2) {
            currentPage = 0;
            getHistory("2");
            rechargeView.setBackgroundResource(R.color.white);
            paymentView.setBackgroundResource(R.color.driver_color);
        }
    }

    public static boolean hasMore = false;
    private OnLoadMoreListener loadMore = new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (hasMore)
                getHistory("" + selectedType);
        }
    };

    private void getHistory(String type) {
        String endPoint = Config.WALLET_HISTORY + "?type=" + type + "&page=" + (++currentPage);
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this, null);
            apiHandler.execute(endPoint, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outerObject = new JSONObject(result);
            if (outerObject.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                if (data.size() > 0 && !hasMore)
                    data.clear();
                if (selectedType == 1) {
                    JSONArray array = outerObject.getJSONObject(Key.DATA).getJSONArray("transaction");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject innerObj = array.getJSONObject(i);
                        Payment payment = new Payment();
                        payment.setDate(innerObj.getString("created_at"));
                        payment.setAmount("$" + innerObj.getString("amount"));
                        payment.setBookingID(innerObj.getString("invoice_id"));
                        payment.setGateway(innerObj.getString("gateway"));
                        data.add(payment);
                    }
                } else if (selectedType == 2) {
                    JSONArray array = outerObject.getJSONObject(Key.DATA).getJSONArray("transaction");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject innerObj = array.getJSONObject(i);
                        String paymentInfo = innerObj.get("payment_amount_detail").toString();
                        Payment payment = new Payment();
                        if (!paymentInfo.equals("null") && !paymentInfo.equals("")) {
                            JSONObject paymentObj = new JSONObject(paymentInfo);
                            payment.setAmount("$" + paymentObj.get("amount").toString());
                            payment.setGateway(paymentObj.get("payment_gateway").toString());
                        }
                        payment.setDate(innerObj.getString("created_at"));
                        payment.setBookingID(innerObj.getString("id"));
                        data.add(payment);
                    }
                }
                adapter.notifyDataSetChanged();
                if (data.size() > 0) {
                    noData.setVisibility(View.GONE);
                    mList.setVisibility(View.VISIBLE);
                } else {
                    mList.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                }
                pageCount = outerObject.getJSONObject(Key.DATA).getJSONObject("_meta").getInt("pageCount");
                currentPage = outerObject.getJSONObject(Key.DATA).getJSONObject("_meta").getInt("currentPage");

                if (currentPage < pageCount)
                    hasMore = true;
                else hasMore = false;
            } else {
                Alert("Alert!", outerObject.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(this, true);
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
}

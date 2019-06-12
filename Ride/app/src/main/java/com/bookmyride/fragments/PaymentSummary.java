package com.bookmyride.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.adapters.PaymentAdapter;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.models.Payment;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PaymentSummary extends Fragment implements AsyncTaskCompleteListener, View.OnClickListener {
    LinearLayout all, pending, credit;
    TextView allView, pendingView, creditView;
    ListView rideList;
    SessionHandler session;
    PaymentAdapter adapter;
    ArrayList<Payment> allData = new ArrayList<>();
    ArrayList<Payment> pendingData = new ArrayList<>();
    ArrayList<Payment> creditData = new ArrayList<>();
    int selectedType;
    TextView noData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_summary, null);
        init(view);
        getPaymentList();
        return view;
    }

    /*@Override
    public void onStart() {
        super.onStart();
        updateUI(1);
        getRides();
    }*/

    /*@Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("RIDE247 WALLET");
    }*/

    private void init(View view) {
        session = new SessionHandler(getActivity());
        all = (LinearLayout) view.findViewById(R.id.all);
        all.setOnClickListener(this);
        pending = (LinearLayout) view.findViewById(R.id.pending);
        pending.setOnClickListener(this);
        credit = (LinearLayout) view.findViewById(R.id.credit);
        credit.setOnClickListener(this);
        rideList = (ListView) view.findViewById(R.id.rides_list);
        noData = (TextView) view.findViewById(R.id.no_data);
        allView = (TextView) view.findViewById(R.id.a_view);
        pendingView = (TextView) view.findViewById(R.id.p_view);
        creditView = (TextView) view.findViewById(R.id.c_view);
    }

    private void setListAdapter(final ArrayList<Payment> paymentData) {
        adapter = new PaymentAdapter(getActivity(), paymentData, null);
        rideList.setAdapter(adapter);
        rideList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Payment selected = paymentData.get(position);

            }
        });
        adapter.notifyDataSetChanged();
        if (paymentData.size() > 0) {
            noData.setVisibility(View.GONE);
            rideList.setVisibility(View.VISIBLE);
        } else {
            rideList.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.all:
                updateUI(1);
                break;
            case R.id.pending:
                updateUI(3);
                break;
            case R.id.credit:
                updateUI(2);
                break;
        }
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                JSONArray items = outJson.getJSONObject(Key.DATA).getJSONArray(Key.ITEMS);
                if (items.length() > 0) {
                    allData.clear();
                    pendingData.clear();
                    creditData.clear();
                }
                for (int i = 0; i < items.length(); i++) {
                    JSONObject obj = items.getJSONObject(i);
                    Payment payment = new Payment();
                    payment.setAmount("$" + obj.getString(Key.AMOUNT));
                    payment.setType(obj.getString("booking_id"));
                    //payment.setCurrency(obj.getString(Key));
                    payment.setDate(obj.getString(Key.CREATED_AT));
                    allData.add(payment);
                    if (obj.getString(Key.STATUS).equals("0"))
                        creditData.add(payment);
                    else if (obj.getString(Key.STATUS).equals("1"))
                        pendingData.add(payment);
                }
                updateUI(1);
            } else {
                Alert("Alert!", outJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getPaymentList() {
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET, this, null);
            apiHandler.execute(Config.WALLET + "/rideearning", session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void Alert(String title, String message) {
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

    private void updateUI(int type) {
        selectedType = type;
        if (type == 1) {
            allView.setBackgroundResource(R.color.driver_color);
            pendingView.setBackgroundResource(R.color.white);
            creditView.setBackgroundResource(R.color.white);
            setListAdapter(allData);
        } else if (type == 2) {
            allView.setBackgroundResource(R.color.white);
            pendingView.setBackgroundResource(R.color.white);
            creditView.setBackgroundResource(R.color.driver_color);
            setListAdapter(creditData);
        } else if (type == 3) {
            allView.setBackgroundResource(R.color.white);
            pendingView.setBackgroundResource(R.color.driver_color);
            creditView.setBackgroundResource(R.color.white);
            setListAdapter(pendingData);
        }
    }
}
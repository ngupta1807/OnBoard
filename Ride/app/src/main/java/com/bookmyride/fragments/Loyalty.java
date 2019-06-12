package com.bookmyride.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.bookmyride.views.RideDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Loyalty extends Fragment implements AsyncTaskCompleteListener {
    SessionHandler session;
    TextView amount, cashOut;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loyalty, null);
        init(view);
        getLoyaltyAmount();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("LOYALTY");
    }

    private void init(View view) {
        session = new SessionHandler(getActivity());
        amount = (TextView) view.findViewById(R.id.amount);
        cashOut = (TextView) view.findViewById(R.id.redeem);
        cashOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cashOutLoyalty();
            }
        });
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if(outJson.getInt(Key.STATUS) == APIStatus.SUCCESS)
                if(type == 0){
                    JSONArray dataArray = outJson.getJSONObject(Key.DATA).getJSONArray(Key.ITEMS);
                    JSONObject dataObj = dataArray.getJSONObject(0);
                    amount.setText("$0.00");
                } else if(type == 1) {
                    Alert("Success!", outJson.getString(Key.MESSAGE));
                }
            else
                Alert("Alert!", outJson.getString(Key.MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void Alert(String title, String message) {
        final RideDialog mDialog = new RideDialog(getActivity(), false, true);
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
    int type;
    private void getLoyaltyAmount() {
        type = 0;
        if(Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET,this, null);
            apiHandler.execute(Config.CARD_TYPES, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }
    private void cashOutLoyalty() {
        type = 1;
        if(Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET,this, null);
            apiHandler.execute(Config.CARD_TYPES, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

}
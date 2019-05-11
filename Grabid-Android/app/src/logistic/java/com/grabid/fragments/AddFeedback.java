package com.grabid.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.BackPressed;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by vinod on 11/10/2016.
 */
public class AddFeedback extends Fragment implements View.OnClickListener,
        AsyncTaskCompleteListener, BackPressed {
    String deliveryID;
    EditText inputs;
    RatingBar rating;
    TextView submit;
    SessionManager session;
    String incomingType = "";
    String backStack = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.title.setText(getResources().getString(R.string.feedback));
        HomeActivity.markread.setVisibility(View.GONE);
        View view = inflater.inflate(R.layout.add_feedback, null);
        init(view);
        incomingType = getArguments().getString("incoming_type");
        deliveryID = getArguments().getString("delivery_id");
        backStack = getArguments().getString("backstack");
        if (incomingType != null && incomingType.equals("shipper")) {
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
            HomeActivity.title.setTextColor(getResources().getColor(R.color.white));
            HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.seagreen));
        } else {
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
            HomeActivity.title.setTextColor(getResources().getColor(R.color.black));
            HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        }
        return view;
    }

    private void init(View view) {
        session = new SessionManager(getActivity());
        inputs = (EditText) view.findViewById(R.id.input);
        rating = (RatingBar) view.findViewById(R.id.rating);
        submit = (TextView) view.findViewById(R.id.submit);
        submit.setOnClickListener(this);
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    @Override
    public void onClick(View view) {
        if (isReadyForUpload())
            uploadFeedback();
    }

    private void uploadFeedback() {
        String url;
        //  if (incomingType.equals("feedback"))
        if (incomingType.equals("shipper"))
            url = Config.SERVER_URL + Config.FEEDBACKS;
        else
            url = Config.SERVER_URL + Config.FEEDBACK_TO_SHIPPER;
        HashMap<String, String> params = new HashMap<>();
        params.put(Keys.DELIVERY_ID, deliveryID);
        params.put(Keys.RATING, "" + rating.getRating());
        params.put(Keys.FEEDBACK, inputs.getText().toString());
        Log.d(Config.TAG, params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage(TITLE, getResources().getString(R.string.no_internet));
    }

    private boolean isReadyForUpload() {
        /*if(inputs.getText().toString().length() == 0){
            showMessage(TITLE, "Please enter your feedback.");
            return false;
        } else*/
        if (rating.getRating() == 0) {
            showMessage(TITLE, getActivity().getResources().getString(R.string.giveyourating));
            return false;
        }
        return true;
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            Log.d(Config.TAG, outJson.toString());
            if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                if (incomingType.equals("shipper"))
                    showSuccessMessage(getResources().getString(R.string.feedbacksender));
                else
                    showSuccessMessage(getResources().getString(R.string.feedbackcarrier));
            } else if (outJson.getInt(Config.STATUS) == APIStatus.UNPROCESSABLE) {
                if (outJson.getString(Config.MESSAGE).contentEquals("")) {
                    try {
                        JSONArray dataObj = outJson.optJSONArray(Config.DATA);
                        JSONObject messageJson = dataObj.optJSONObject(0);
                        String message = messageJson.optString("message");
                        showMessage("Error", message.toString());
                    } catch (Exception e) {
                        e.toString();
                    }

                } else
                    showMessage("Error", outJson.getString(Config.MESSAGE));
            } else {
                showMessage("Error", getResources().getString(R.string.no_response));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    String TITLE = "Alert!";

    private void showMessage(String title, String message) {
        AlertManager.messageDialog(getActivity(), title, message);
    }

    private void showSuccessMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Success!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().startActivity(new Intent(getActivity(), HomeActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                getActivity().finish();
                /*Fragment fragment = new GrabidMapFragment();
                String backStateName = "com.grabid.activities.HomeActivity";
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();*/
            }
        });
        builder.show();
    }

    @Override
    public boolean onBackPressed() {
        if (backStack != null && backStack.contentEquals("listingpage")) {
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            intent.putExtra("signaturetype", "1");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
            return true;
        }
        return false;
    }
}
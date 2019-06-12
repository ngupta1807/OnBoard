package com.bookmyride.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.activities.DriverHome;
import com.bookmyride.activities.PassengerHome;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.util.AsteriskPasswordTransformationMethod;
import com.bookmyride.views.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by vinod on 1/9/2017.
 */
public class ChangePassword extends Fragment implements AsyncTaskCompleteListener {
    EditText currentPassword, newPassword, confirmPassword;
    SessionHandler session;
    TextView submit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_password, null);
        init(view);
        return view;
    }

    /* @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("CHANGE PASSWORD");
    } */

    private void init(View view) {
        session = new SessionHandler(getActivity());
        submit = (TextView) view.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    if (Internet.hasInternet(getActivity()))
                        changePassword();
                    else
                        Alert("Alert!", getResources().getString(R.string.no_internet));
                }
            }
        });
        currentPassword = (EditText) view.findViewById(R.id.password);
        currentPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        newPassword = (EditText) view.findViewById(R.id.new_password);
        newPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        confirmPassword = (EditText) view.findViewById(R.id.confirm_password);
        confirmPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());
    }

    private void showMessage(String message) {
        Alert("Oops !!!", message);
    }

    private boolean isValid() {
        if (currentPassword.getText().toString().trim().length() == 0) {
            showMessage("Please enter your current password.");
            return false;
        } else if (newPassword.getText().toString().trim().length() == 0) {
            showMessage("Please enter your new password.");
            return false;
        } else if (newPassword.getText().toString().trim().length() < 6) {
            showMessage("Please enter your new 6 digit password.");
            return false;
        } else if (confirmPassword.getText().toString().trim().length() == 0) {
            showMessage("Please reenter your new password.");
            return false;
        } else if (confirmPassword.getText().toString().trim().length() < 6) {
            showMessage("Please reenter your new 6 digit password.");
            return false;
        } else if (!newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
            showMessage("New password and confirm password does not match.");
            return false;
        }
        return true;
    }

    private void Alert(final String title, String message) {
        final AlertDialog mDialog = new AlertDialog(getActivity(), true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (title.contains("Success")) {
                    if (session.getUserType().equals("4")) {
                        DriverHome.mTitle.setText("DASHBOARD");
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, new DriverDashboard());
                        ft.commit();
                    } else if (session.getUserType().equals("3")) {
                        PassengerHome.mTitle.setText("RIDE247");
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, new RideMap());
                        ft.commit();
                    }
                }
            }
        });
        mDialog.show();
    }

    public void changePassword() {
        if (isValid()) {
            HashMap<String, String> params = new HashMap<>();
            params.put("password", currentPassword.getText().toString());
            params.put("newPassword", newPassword.getText().toString());
            APIHandler apiCall = new APIHandler(getActivity(), HTTPMethods.POST, this, params);
            apiCall.execute(Config.CHANGE_PASSWORD, session.getToken());
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
                JSONObject dataObj = outJson.getJSONObject(Key.DATA);
                session.saveToken(dataObj.getString(Key.TOKEN));
                Alert("Success!", "You have successfully changed your password.");
            } else if (outJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                String message = outJson.getJSONArray(Key.DATA).getJSONObject(0).getString(Key.MESSAGE);
                showMessage(message);
            } else {
                String message = outJson.getString(Key.MESSAGE);
                showMessage(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

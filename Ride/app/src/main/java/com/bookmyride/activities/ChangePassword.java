package com.bookmyride.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import com.bookmyride.R;
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
public class ChangePassword extends AppCompatActivity implements AsyncTaskCompleteListener {
    EditText currentPassword, newPassword, confirmPassword;
    SessionHandler session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        init();
        updateUI();
    }

    @Override
    public void onBackPressed() {
        if (!getIntent().hasExtra("isTemp")) {
            super.onBackPressed();
        } else {
            Alert("Alert!", "You are logged in with temp password. Please choose your new password.");
        }
    }

    private void updateUI() {
        if (getIntent().hasExtra("isTemp")) {
            currentPassword.setVisibility(View.GONE);
        } else currentPassword.setVisibility(View.VISIBLE);
    }

    public void changePassword(View view) {
        if (Internet.hasInternet(this))
            changePassword();
        else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    public void onBack(View view) {
        onBackPressed();
    }

    private void init() {
        session = new SessionHandler(this);
        currentPassword = (EditText) findViewById(R.id.password);
        currentPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        newPassword = (EditText) findViewById(R.id.new_password);
        newPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        confirmPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());
    }

    private boolean isValid() {
        if (currentPassword.getText().toString().trim().length() == 0
                && !getIntent().hasExtra("isTemp")) {
            Alert("Oops !!!", "Please enter your current password.");
            return false;
        } else if (newPassword.getText().toString().trim().length() == 0) {
            Alert("Oops !!!", "Please enter your new password.");
            return false;
        } else if (newPassword.getText().toString().trim().length() < 6) {
            Alert("Oops !!!", "Please enter your new 6 digit password.");
            return false;
        } else if (confirmPassword.getText().toString().trim().length() == 0) {
            Alert("Oops !!!", "Please reenter your new password.");
            return false;
        } else if (confirmPassword.getText().toString().trim().length() < 6) {
            Alert("Oops !!!", "Please reenter your new 6 digit password.");
            return false;
        } else if (!newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
            Alert("Oops !!!", "New password and confirm password does not match.");
            return false;
        }
        return true;
    }

    private void Alert(final String title, String message) {
        final AlertDialog mDialog = new AlertDialog(ChangePassword.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (title.equals("Success!") && getIntent().hasExtra("isTemp")) {
                    goToHome(session.getUserType());
                }
            }
        });
        mDialog.show();
    }

    private void goToHome(String userType) {
        new SessionHandler(this).saveUserType(userType);
        Intent intent;
        if (userType.equals("3"))
            intent = new Intent(getApplicationContext(), PassengerHome.class);
        else
            intent = new Intent(getApplicationContext(), DriverHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        ChangePassword.this.finish();
    }

    public void changePassword() {
        if (isValid()) {
            HashMap<String, String> params = new HashMap<>();
            if (getIntent().hasExtra("isTemp"))
                params.put("password", getIntent().getStringExtra("isTemp"));
            else
                params.put("password", currentPassword.getText().toString());
            params.put("newPassword", newPassword.getText().toString());
            APIHandler apiCall = new APIHandler(this, HTTPMethods.POST, this, params);
            apiCall.execute(Config.CHANGE_PASSWORD, session.getToken());
        }
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outerJson = new JSONObject(result);
            if (outerJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                JSONObject dataObj = outerJson.getJSONObject(Key.DATA);
                session.saveToken(dataObj.getString(Key.TOKEN));
                Alert("Success!", "Your password has been changed successfully.");
            } else if (outerJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                String message = outerJson.getJSONArray(Key.DATA).getJSONObject(0).getString(Key.MESSAGE);
                Alert("Alert!", message);
            } else {
                String message = outerJson.getString(Key.MESSAGE);
                Alert("Alert!", message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

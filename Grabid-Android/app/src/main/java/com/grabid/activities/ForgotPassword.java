package com.grabid.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.grabid.BuildConfig;
import com.grabid.R;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by vinod on 10/24/2016.
 */
public class ForgotPassword extends AppCompatActivity implements AsyncTaskCompleteListener,
        View.OnClickListener {
    EditText email;
    ImageView nav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            e.toString();
        }
        setContentView(R.layout.activity_forgot_password);
        init();
    }

    private void init() {
        email = (EditText) findViewById(R.id.email);
        nav = (ImageView) findViewById(R.id.nav);
        nav.setOnClickListener(this);
    }

    private boolean isValid() {
        if (email.getText().toString().trim().length() == 0) {
            showMessage("Alert!", getResources().getString(R.string.enteremail));
            return false;
        } else if (email.getText().toString().contains("@") && !Utils.isEmailValid(email.getText().toString().trim())) {
            showMessage("Alert!", getResources().getString(R.string.validemail));
            return false;
        }
        return true;
    }

    private void showMessage(String title, String message) {
        AlertManager.messageDialog(this, title, message);
    }

    public void forgotPassword(View view) {
        if (isValid()) {
            String url = Config.SERVER_URL + Config.FORGOT_PASSWORD;
            HashMap<String, String> params = new HashMap<>();
            params.put("email", email.getText().toString());
            if (BuildConfig.logistic)
                params.put("app_id", "1");
            else
                params.put("app_id", "2");
            if (Internet.hasInternet(ForgotPassword.this)) {
                RestAPICall apiCall = new RestAPICall(this, HTTPMethods.POST, this, params);
                apiCall.execute(url, "");
            } else {
                showMessage("Alert!", getResources().getString(R.string.no_internet));
            }
        }
    }

    public void Success(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ForgotPassword.this.finish();
            }
        });
        Dialog d = builder.create();
        d.show();
    }
    /*@Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), SignIn.class));
        ForgotPassword.this.finish();
    }*/

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outerJson = new JSONObject(result);
            if (outerJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                email.setText("");
                Success("Success!", outerJson.getString(Config.MESSAGE));
            } else {
                showMessage("Alert!", outerJson.getJSONArray(Config.DATA).getJSONObject(0).getString(Config.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.nav) {
            onBackPressed();
        }
    }
}

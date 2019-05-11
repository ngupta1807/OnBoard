package com.grabid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by vinod on 10/14/2016.
 */
public class ChangePassword extends Fragment implements View.OnClickListener,
        AsyncTaskCompleteListener {
    EditText currentPass, newPass, confirmPass;
    TextView changePassword;
    SessionManager session;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.title.setText("Change Password");
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        View view = inflater.inflate(R.layout.change_passwrod, null);
        init(view);
        return view;
    }

    private void init(View view) {
        session = new SessionManager(getActivity());
        changePassword = (TextView) view.findViewById(R.id.change);
        changePassword.setOnClickListener(this);
        currentPass = (EditText) view.findViewById(R.id.current);
        newPass = (EditText) view.findViewById(R.id.new_pass);
        confirmPass = (EditText) view.findViewById(R.id.confirm);
    }

    private boolean isValid() {
        if (currentPass.getText().toString().trim().length() == 0 &&
                newPass.getText().toString().trim().length() == 0
                && confirmPass.getText().toString().trim().length() == 0) {
            showMessage(getActivity().getString(R.string.completeallfield));
            return false;
        }
        if (currentPass.getText().toString().trim().length() == 0) {
            showMessage(getActivity().getString(R.string.currentpassword));
            return false;
        } else if (newPass.getText().toString().trim().length() == 0) {
            showMessage(getActivity().getString(R.string.newpassword));
            return false;
        } else if (newPass.getText().toString().trim().length() < 6) {
            showMessage(getActivity().getString(R.string.newpasswordmax));
            return false;
        } else if (confirmPass.getText().toString().trim().length() < 6) {
            showMessage(getActivity().getString(R.string.confirmpassword));
            return false;
        } else if (!newPass.getText().toString().equals(confirmPass.getText().toString())) {
            showMessage(getActivity().getString(R.string.notmatchpassword));
            return false;
        }
        return true;
    }

    public void changePassword() {
        if (isValid()) {
            String url = Config.SERVER_URL + Config.CHANGE_PASSWORD;
            HashMap<String, String> params = new HashMap<>();
            params.put("password", currentPass.getText().toString());
            params.put("newPassword", newPass.getText().toString());
            Log.d(Config.TAG, params.toString());
            if (Internet.hasInternet(getActivity())) {
                RestAPICall apiCall = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
                apiCall.execute(url, session.getToken());
            } else {
                AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
            }
        }
    }

    @Override
    public void onTaskComplete(String result) {
        Log.d(Config.TAG, result);
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outterJson = new JSONObject(result);
            if (outterJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                JSONObject dataObj = outterJson.getJSONObject(Config.DATA);
                session.saveToken(dataObj.getString(Config.TOKEN));
                messageDialog("Success!", "Your password has been changed successfully.");
            } else if (outterJson.getInt(Config.STATUS) == APIStatus.UNPROCESSABLE) {
                String message = outterJson.optString("message");
                //  String message = outterJson.getJSONArray(Config.DATA).getJSONObject(0).getString(Config.MESSAGE);
                AlertManager.messageDialog(getActivity(), "Alert!", message);
            } else {
                String message = outterJson.getString(Config.MESSAGE);
                AlertManager.messageDialog(getActivity(), "Alert!", message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.change)
            changePassword();
    }

    private void showMessage(String message) {
        AlertManager.messageDialog(getActivity(), "Alert!", message);
    }

    public void messageDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getActivity().getFragmentManager().popBackStack();
            }
        });
        Dialog d = builder.create();
        d.show();
    }
}
package com.bookmyride.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.activities.Login;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.views.AlertDialog;
import com.bookmyride.views.RideDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class Settings extends Fragment {
    SessionHandler session;
    TextView deactivate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, null);
        init(view);
        return view;
    }

    private void init(View view) {
        session = new SessionHandler(getActivity());
        deactivate = (TextView) view.findViewById(R.id.deactivate);
        deactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitDialog();
            }
        });
    }

    private void Alert(final String title, String message) {
        final AlertDialog mDialog = new AlertDialog(getActivity(), true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if(title.equalsIgnoreCase("Success!")){
                    Intent intent = new Intent(getActivity(), Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        mDialog.show();
    }

    private void quitDialog() {
        final RideDialog mDialog = new RideDialog(getActivity(), false, true);
        mDialog.setDialogTitle("DEACTIVATE PROFILE");
        mDialog.setDialogMessage(getResources().getString(R.string.del_profile_msg));
        mDialog.setPositiveButton(getResources().getString(R.string.profile_lable_logout_yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.PUT, new AsyncTaskCompleteListener() {
                    @Override
                    public void onTaskComplete(String result) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            if(obj.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                                Alert("Success!",obj.getString(Key.MESSAGE));
                            } else
                                Alert("Alert!",obj.getString(Key.MESSAGE));
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, null);
                apiHandler.execute(Config.DEACTIVATE + session.getUserID(), session.getToken());
            }
        });
        mDialog.setNegativeButton(getResources().getString(R.string.profile_lable_logout_no), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }
}
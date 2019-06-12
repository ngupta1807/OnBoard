package com.bookmyride.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.activities.CountryList;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.common.Validator;
import com.bookmyride.views.AlertDialog;
import com.bookmyride.views.RideDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EmergencyContact extends Fragment implements AsyncTaskCompleteListener {
    SessionHandler session;
    TextView save, dialCode, delete;
    EditText name, phone, email;
    RelativeLayout overLay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.emergency_contact, null);
        init(view);
        getEmergencyInfo();
        return view;
    }

    /*@Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("EMERGENCY");
    }*/

    private void init(View view) {
        session = new SessionHandler(getActivity());
        overLay = (RelativeLayout) view.findViewById(R.id.lay_overlay);
        overLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        name = (EditText) view.findViewById(R.id.name);
        email = (EditText) view.findViewById(R.id.email);
        phone = (EditText) view.findViewById(R.id.mobile_no);
        dialCode = (TextView) view.findViewById(R.id.dial_code);
        dialCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), CountryList.class), 2);
            }
        });
        save = (TextView) view.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (save.getText().toString().equals("Edit")) {
                    overLay.setVisibility(View.GONE);
                    save.setText("Update");
                } else {
                    if (isValid())
                        saveContact();
                }
            }
        });
        delete = (TextView) view.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog();
            }
        });
    }

    private boolean isValid() {
        if (name.getText().length() == 0) {
            Alert("Oops !!!", "Please enter name.");
            return false;
        } else if (dialCode.getText().length() == 0) {
            Alert("Oops !!!", "Please select country code.");
            return false;
        } else if (phone.getText().length() == 0) {
            Alert("Oops !!!", "Please enter valid mobile number.");
            return false;
        } else if (phone.getText().length() < 9) {
            Alert("Oops !!!", "Please enter valid mobile number.");
            return false;
        } else if (email.getText().length() == 0) {
            Alert("Oops !!!", "Please enter email.");
            return false;
        } else if (!Validator.isEmailValid(email.getText().toString())) {
            Alert("Oops !!!", "Invalid Email! Please enter valid email.");
            return false;
        }
        return true;
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outerJson = new JSONObject(result);
            if (outerJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                if (type == 0) {
                    JSONObject dataObj = outerJson.getJSONObject(Key.DATA);
                    name.append(dataObj.getString(Key.NAME));
                    dialCode.append(dataObj.getString(Key.DIAL_CODE));
                    phone.append(dataObj.getString(Key.MOBILE));
                    email.append(dataObj.getString(Key.EMAIL));
                    save.setText("Edit");
                    overLay.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.VISIBLE);
                } else if (type == 1) {
                    overLay.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.VISIBLE);
                    save.setText("Edit");
                    Alert("Success!", outerJson.getString(Key.MESSAGE));
                }
            } else if (outerJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                delete.setVisibility(View.GONE);
                //Alert("Alert!", outerJson.getString(Key.MESSAGE));
            } else
                Alert("Alert!", outerJson.getString(Key.MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
            delete.setVisibility(View.GONE);
        }
    }

    String code;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2) {
                code = result.getStringExtra("code");
                dialCode.setText(code);
            }
        } else {
        }
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

    int type;

    private void getEmergencyInfo() {
        type = 0;
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET, this, null);
            apiHandler.execute(Config.EMERGENCY_CONTACT, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void saveContact() {
        type = 1;
        HashMap<String, String> params = new HashMap<>();
        params.put("name", name.getText().toString());
        params.put("email", email.getText().toString());
        params.put("mobile", phone.getText().toString());
        params.put("dial_code", dialCode.getText().toString());
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.POST, this, params);
            apiHandler.execute(Config.EMERGENCY_CONTACT, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void deleteDialog() {
        final RideDialog mDialog = new RideDialog(getActivity(), false, true);
        mDialog.setDialogTitle("Emergency Contact");
        mDialog.setDialogMessage("Are you sure you want to delete your emergency contact?");
        mDialog.setNegativeButton(getResources().getString(R.string.profile_lable_logout_yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                final APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.DELETE, new AsyncTaskCompleteListener() {
                    @Override
                    public void onTaskComplete(String result) {
                        if (result.equals(String.valueOf(APIStatus.DELETE_SUCCESS))) {
                            name.setText("");
                            dialCode.setText("");
                            phone.setText("");
                            email.setText("");
                            delete.setVisibility(View.GONE);
                            save.setText("SAVE");
                            overLay.setVisibility(View.GONE);
                            Alert("Success!", "You have successfully deleted your emergency contact.");
                        } else
                            Alert("Alert!", "Delete error. Please try after sometime.");
                    }
                }, null);
                apiHandler.execute(Config.EMERGENCY_CONTACT + "/" + session.getUserID(), session.getToken());
            }
        });
        mDialog.setPositiveButton(getResources().getString(R.string.profile_lable_logout_no), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }
}
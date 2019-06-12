package com.bookmyride.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.Internet;
import com.bookmyride.common.Validator;
import com.bookmyride.views.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Vinod on 2017-01-07.
 */
public class ForgotPassword extends AppCompatActivity implements AsyncTaskCompleteListener {
    EditText email, userType, dialCode, mobile;
    String usrType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        init();
    }

    private void init() {
        email = (EditText) findViewById(R.id.email);
        userType = (EditText) findViewById(R.id.user_type);
        userType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRideDialog();
            }
        });
        dialCode = (EditText) findViewById(R.id.dial_code);
        mobile = (EditText) findViewById(R.id.phone);
        dialCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), CountryList.class), 2);
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                Log.e("beforeTextChanged", arg0.toString());
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                Log.e("afterTextChanged", arg0.toString());
                if (arg0.length() == 1) {
                    new AsyncTask<Void, Void, Void>() {
                        protected Void doInBackground(Void... params) {
                            return null;
                        }

                        protected void onPostExecute(Void result) {
                            dialCode.setText("");
                            mobile.setText("");
                        }
                    }.execute();
                }
            }
        });
        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                //email.setText("");
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (arg0.length() == 1) {
                    new AsyncTask<Void, Void, Void>() {
                        protected Void doInBackground(Void... params) {
                            return null;
                        }

                        protected void onPostExecute(Void result) {
                            email.setText("");
                        }
                    }.execute();
                }
            }
        });
    }

    String code = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2) {
                code = result.getStringExtra("code");
                dialCode.setText(code);
                email.setText("");
                mobile.requestFocus();
                mobile.setCursorVisible(true);
                mobile.setFocusableInTouchMode(true);
            }
        }
    }

    Dialog mDialog;

    public void showRideDialog() {
        mDialog = new Dialog(ForgotPassword.this, R.style.rideDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list);
        mDialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("Select User Type");

        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        ListView dialog_ListView = (ListView) mDialog.findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.simple_list_item, R.id.textItem, getUserTypeList());

        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String type = parent.getItemAtPosition(position).toString();
                userType.setText(type);
                if (type.equalsIgnoreCase("Passenger"))
                    usrType = "3";
                else if (type.equalsIgnoreCase("Driver"))
                    usrType = "4";
                else if (type.equalsIgnoreCase("Both"))
                    usrType = "5";
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public String[] getUserTypeList() {
        String[] gateway = getResources().getStringArray(R.array.userType);
        return gateway;
    }

    public void onBack(View view) {
        onBackPressed();
    }

    private boolean isValid() {
        if (userType.getText().toString().equals("")) {
            Alert("Oops !!!", "Please select user type.");
            return false;
        } else if (email.getText().toString().trim().length() == 0 &&
                mobile.getText().toString().trim().length() == 0) {
            Alert("Oops !!!", "Please enter your email or phone.");
            return false;
        } else if (email.getText().toString().trim().length() > 0 &&
                !Validator.isEmailValid(email.getText().toString().trim())) {
            Alert("Oops !!!", "Please enter your valid email.");
            return false;
        } else if (mobile.getText().toString().trim().length() > 0 &&
                email.getText().toString().equals("")) {
            if (dialCode.getText().toString().equals("")) {
                Alert("Oops !!!", "Please select dial code.");
                return false;
            } else if (mobile.getText().toString().trim().length() < 9) {
                Alert("Oops !!!", "Please enter your valid phone number.");
                return false;
            }
        }
        return true;
    }

    public void forgotPassword(View view) {
        if (isValid()) {
            HashMap<String, String> params = new HashMap<>();
            if (!email.getText().toString().equals(""))
                params.put("email", email.getText().toString());
            else params.put("email", dialCode.getText().toString() + mobile.getText().toString());
            params.put("user_type", usrType);
            if (Internet.hasInternet(this)) {
                APIHandler apiCall = new APIHandler(this, HTTPMethods.POST, this, params);
                apiCall.execute(Config.FORGOT_PASSWORD, "");
            } else
                Alert("Alert!", getResources().getString(R.string.no_internet));
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
                Alert("Success!", outJson.getString(Key.MESSAGE));
                email.setText("");
            } else {
                Alert("Alert!", outJson.getJSONArray(Key.DATA).getJSONObject(0).getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Alert(final String title, String message) {
        final AlertDialog mDialog = new AlertDialog(ForgotPassword.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (title.equals("Success!")) {
                    ForgotPassword.this.finish();
                }
            }
        });
        mDialog.show();
    }
}

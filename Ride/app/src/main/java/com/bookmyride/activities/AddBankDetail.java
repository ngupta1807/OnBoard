package com.bookmyride.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.bookmyride.views.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AddBankDetail extends AppCompatActivity implements AsyncTaskCompleteListener {
    SessionHandler session;
    EditText accountNumber, holderName, bankName, bsb;
    TextView save;
    LinearLayout layInfo;
    //private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_bank_detail);
        init();
        getBankDetail();
    }

    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        AddBankDetail.this.finish();
        //super.onBackPressed();

    }

   /*@Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(NotificationFilters.REQUEST_CANCELLED));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onPause();
    }*/

    private void init() {
        session = new SessionHandler(this);
        accountNumber = (EditText) findViewById(R.id.ac_number);
        holderName = (EditText) findViewById(R.id.ac_name);
        holderName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        bankName = (EditText) findViewById(R.id.bank_name);
        bankName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        bsb = (EditText) findViewById(R.id.bsb);
        save = (TextView) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (save.getText().toString().equals("Edit")) {
                    //overLay.setVisibility(View.GONE);
                    updateInputs();
                    save.setText("Update");
                } else {
                    if (isValidate())
                        saveBankDetail();
                }
            }
        });
        layInfo = (LinearLayout) findViewById(R.id.lay_info);

        holderName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    holderName.requestFocus();
                    holderName.setFocusable(true);
                    holderName.setCursorVisible(true);
                    holderName.setFocusableInTouchMode(true);
                    holderName.setSelection(holderName.getText().length());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(holderName, InputMethodManager.SHOW_FORCED);
                }
                return true; // return is important...
            }
        });

        accountNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    accountNumber.requestFocus();
                    accountNumber.setFocusable(true);
                    accountNumber.setCursorVisible(true);
                    accountNumber.setFocusableInTouchMode(true);
                    accountNumber.setSelection(accountNumber.getText().length());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(accountNumber, InputMethodManager.SHOW_FORCED);
                }
                return true; // return is important...
            }
        });

        bankName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    bankName.requestFocus();
                    bankName.setFocusable(true);
                    bankName.setCursorVisible(true);
                    bankName.setFocusableInTouchMode(true);
                    bankName.setSelection(bankName.getText().length());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(bankName, InputMethodManager.SHOW_FORCED);
                }
                return true; // return is important...
            }
        });

        bsb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    bsb.requestFocus();
                    bsb.setFocusable(true);
                    bsb.setCursorVisible(true);
                    bsb.setFocusableInTouchMode(true);
                    bsb.setSelection(bsb.getText().length());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(bsb, InputMethodManager.SHOW_FORCED);
                }
                return true; // return is important...
            }
        });
        /*mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NotificationFilters.REQUEST_CANCELLED)) {
                    //if (session.getUserType().equals("4")) {
                    try {
                        JSONObject obj = new JSONObject(intent.getStringExtra("rideData"));
                        msg = obj.getString("message");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showPopup(msg);
                            }
                        }, 500);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //}
                }
            }
        };*/
    }

    String msg = "";

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                if (type == 3) {
                    JSONObject innerObj = outJson.getJSONObject(Key.DATA);
                    accountNumber.setText(innerObj.getString(Key.NUMBER));
                    holderName.setText(innerObj.getString(Key.ACCOUNT_OWNER_NAME));
                    bankName.setText(innerObj.getString(Key.NAME));
                    bsb.setText(innerObj.getString(Key.ROUTING));
                    save.setText("Edit");
                    disableInputs();
                    //overLay.setVisibility(View.VISIBLE);
                } else if (type == 4) {
                    save.setText("Edit");
                    disableInputs();
                    //overLay.setVisibility(View.VISIBLE);
                    Alert("Success!", outJson.getString(Key.MESSAGE));
                }
            } else
                Alert("Alert!", outJson.getString(Key.MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    int type = 0;

    private void getBankDetail() {
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this, null);
            apiHandler.execute(Config.BANK_DETAIL + "/0", session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void Alert(final String title, String message) {
        final AlertDialog mDialog = new AlertDialog(AddBankDetail.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (title.equals("Success!") && getIntent().hasExtra("isBack")) {
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    AddBankDetail.this.finish();
                }
            }
        });
        mDialog.show();
    }

    private boolean isValidate() {
        if (holderName.getText().toString().trim().isEmpty()) {
            Alert("Oops !!!", "Please enter account holder name.");
            return false;
        } else if (accountNumber.getText().toString().length() == 0 || accountNumber.getText().toString().length() < 6) {
            Alert("Oops !!!", "Please enter valid bank account number.");
            return false;
        } else if (bankName.getText().toString().length() == 0) {
            Alert("Oops !!!", "Please enter bank name.");
            return false;
        } else if (bsb.getText().toString().length() == 0 || bsb.getText().toString().length() < 6) {
            Alert("Oops !!!", "Please enter valid 6 digit BSB number.");
            return false;
        }
        return true;
    }

    private void saveBankDetail() {
        type = 4;
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put(Key.NAME, bankName.getText().toString());
        jsonParams.put(Key.ACCOUNT_OWNER_NAME, holderName.getText().toString());
        jsonParams.put(Key.NUMBER, accountNumber.getText().toString());
        jsonParams.put(Key.ROUTING, bsb.getText().toString());
        APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, jsonParams);
        apiHandler.execute(Config.BANK_DETAIL, session.getToken());
    }

    private void updateInputs() {
        holderName.setEnabled(true);
        accountNumber.setEnabled(true);
        bankName.setEnabled(true);
        bsb.setEnabled(true);
    }

    private void disableInputs() {
        accountNumber.setEnabled(false);
        accountNumber.setCursorVisible(false);
        holderName.setEnabled(false);
        holderName.setCursorVisible(false);
        bankName.setEnabled(false);
        bankName.setCursorVisible(false);
        bsb.setEnabled(false);
        bsb.setCursorVisible(false);
    }

    private void goHome() {
        Intent intent;
        if (session.getUserType().equals("3"))
            intent = new Intent(getApplicationContext(), PassengerHome.class);
        else
            intent = new Intent(getApplicationContext(), DriverHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        AddBankDetail.this.finish();
    }

    public void showPopup(String msg) {
        final Dialog mDialog = new Dialog(this, R.style.rideDialog);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_payment);
        TextView title = (TextView) mDialog.findViewById(R.id.txt_msg);
        TextView message = (TextView) mDialog.findViewById(R.id.msg);
        RelativeLayout Rl_ok = (RelativeLayout) mDialog.findViewById(R.id.ok);

        message.setText(msg);
        title.setText("Alert!");
        Rl_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                goHome();
            }
        });
        mDialog.show();
        /*if(getIntent().hasExtra("end"))
            dismissDialog();*/
    }
}
package com.bookmyride.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
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
import com.bookmyride.common.SessionHandler;
import com.bookmyride.models.CardType;
import com.bookmyride.util.AsteriskPasswordTransformationMethod;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by vinod on 4/18/2017.
 */
public class AcceptPaymentByCard extends AppCompatActivity implements AsyncTaskCompleteListener {
    SessionHandler session;
    EditText nameOnCard, cardNumber, cvv, gateway;
    TextView expiry, done, cardType, title;
    static final int DATE_DIALOG_ID = 1;
    private int mYear;
    private int mMonth;
    private int mDay;
    CheckBox useFuture;

    public void onBack(View view) {
        //onBackPressed();
        AcceptPaymentByCard.this.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accept_pay_creditcard);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        init();
    }

    int count = 0;

    private void init() {
        session = new SessionHandler(this);
        title = (TextView) findViewById(R.id.title);
        nameOnCard = (EditText) findViewById(R.id.name_on_card);
        cardNumber = (EditText) findViewById(R.id.card_number);
        cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (count <= cardNumber.getText().toString().length()
                        && (cardNumber.getText().toString().length() == 4
                        || cardNumber.getText().toString().length() == 9
                        || cardNumber.getText().toString().length() == 14)) {
                    cardNumber.setText(cardNumber.getText().toString() + " ");
                    int pos = cardNumber.getText().length();
                    cardNumber.setSelection(pos);
                } else if (count >= cardNumber.getText().toString().length()
                        && (cardNumber.getText().toString().length() == 4
                        || cardNumber.getText().toString().length() == 9
                        || cardNumber.getText().toString().length() == 14)) {
                    cardNumber.setText(cardNumber.getText().toString().substring(0, cardNumber.getText().toString().length() - 1));
                    int pos = cardNumber.getText().length();
                    cardNumber.setSelection(pos);
                }
                count = cardNumber.getText().toString().length();
            }
        });
        cvv = (EditText) findViewById(R.id.card_cvv);
        cvv.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        //cvv.addTextChangedListener(new AsteriskPasswordTransformationMethod());
        expiry = (TextView) findViewById(R.id.card_expiry);
        expiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        cardType = (TextView) findViewById(R.id.card_type);
        cardType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRideDialog(1);
            }
        });
        gateway = (EditText) findViewById(R.id.gateway);
        gateway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRideDialog(2);
            }
        });
        done = (TextView) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidate())
                    saveCreditCard();
            }
        });
        useFuture = (CheckBox) findViewById(R.id.use_future);
        if (Internet.hasInternet(this)) {
            doGetCardTypes();
        } else {
            Alert("Alert!", getResources().getString(R.string.no_internet));
        }
    }

    private void saveCreditCard() {
        type = 1;
        String exp = expiry.getText().toString();
        String expiryMonth = exp.substring(0, exp.indexOf("/"));
        String expiryYear = exp.substring(exp.indexOf("/") + 1, exp.length());
        String name = nameOnCard.getText().toString();
        String fName;
        String lName;
        if (name.contains(" ")) {
            fName = name.substring(0, name.indexOf(" "));
            lName = name.substring(name.indexOf(" ") + 1, name.length());
        } else {
            fName = name;
            lName = ".";
        }
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put(Key.TYPE, cardType.getText().toString());
        jsonParams.put(Key.FIRST_NAME, fName);
        jsonParams.put(Key.LAST_NAME, lName);
        jsonParams.put(Key.NUMBER, cardNumber.getText().toString());
        jsonParams.put(Key.CARD_EXPIRY_MONTH, expiryMonth);
        jsonParams.put(Key.CARD_EXPIRY_YEAR, expiryYear);
        jsonParams.put(Key.CARD_CVV, cvv.getText().toString());
        //jsonParams.put(Key.GATEWAY, gateway.getText().toString());
        jsonParams.put(Key.GATEWAY, "card");
        jsonParams.put("bookingId", getIntent().getStringExtra("bookingID"));
        jsonParams.put("tip", getIntent().getStringExtra("tip"));
        jsonParams.put("isSavedCard", useFuture.isChecked() ? "1" : "0");
        //jsonParams.put("user_id",getIntent().getStringExtra("user_id"));
        APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, jsonParams);
        apiHandler.execute(Config.PAYMENT_BY_CARD, session.getToken());
    }

    private void doGetCardTypes() {
        type = 3;
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this, null);
            apiHandler.execute(Config.CARD_TYPES, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private boolean isValidate() {
        if (cardType.getText().toString().trim().isEmpty()
                || nameOnCard.getText().toString().trim().isEmpty()
                || cardNumber.getText().toString().trim().isEmpty()
                || expiry.getText().toString().trim().isEmpty()
                || cvv.getText().toString().trim().isEmpty()/*
                || gateway.getText().toString().trim().isEmpty()*/) {
            Alert("Oops !!!", "Please complete all fields.");
            return false;
        } else if (cardNumber.getText().toString().length() < 16) {
            Alert("Oops !!!", "Invalid card number. Please enter valid 16 digit card number.");
            return false;
        } else if (cvv.getText().toString().length() < 3) {
            Alert("Oops !!!", "Invalid CVV. Please enter valid 3 digit CVV number.");
            return false;
        } else if (isCardExpiryDateValid(expiry.getText().toString())) {
            Alert("Oops !!!", "Please select valid card expiry date.");
            return false;
        }
        return true;
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(this, true);
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

    private boolean isCardExpiryDateValid(String input) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy");
        simpleDateFormat.setLenient(false);
        Date expiry = null;
        try {
            expiry = simpleDateFormat.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        boolean expired = expiry.before(new Date());
        return expired;
    }

    Dialog mDialog;

    public void showRideDialog(final int type) {
        mDialog = new Dialog(this, R.style.rideDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list);
        mDialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        if (type == 1)
            title.setText("Select ChargeMe Type");
        else
            title.setText("Select Gateway");

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
        ArrayAdapter<String> adapter;
        if (type == 1)
            adapter = new ArrayAdapter<>(this,
                    R.layout.simple_list_item, R.id.textItem, getCardTypeList());
        else
            adapter = new ArrayAdapter<>(this,
                    R.layout.simple_list_item, R.id.textItem, getGatewayList());

        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (type == 1)
                    cardType.setText(parent.getItemAtPosition(position).toString());
                else
                    gateway.setText(parent.getItemAtPosition(position).toString());
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    int type = 0;
    ArrayList<CardType> cardData = new ArrayList<CardType>();

    public String[] getCardTypeList() {
        String[] listContent = new String[cardData.size()];
        for (int i = 0; i < cardData.size(); i++) {
            listContent[i] = cardData.get(i).getName();
        }
        return listContent;
    }

    public String[] getGatewayList() {
        String[] gateway = getResources().getStringArray(R.array.gateway);
        return gateway;
    }

    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDate();
        }
    };

    public Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                DatePickerDialog datePickerDialog = this.customDatePicker();
                return datePickerDialog;
        }
        return null;
    }

    protected void updateDate() {
        int localMonth = (mMonth + 1);
        String monthString = localMonth < 10 ? "0" + localMonth : Integer
                .toString(localMonth);
        String localYear = Integer.toString(mYear).substring(2);
        expiry.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(monthString).append("/").append(localYear).append(" "));
        this.showDialog(DATE_DIALOG_ID);
    }

    private DatePickerDialog customDatePicker() {
        DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListener,
                mYear, mMonth, mDay);
        try {

            Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField
                            .get(dpd);
                    Field datePickerFields[] = datePickerDialogField.getType()
                            .getDeclaredFields();
                    for (Field datePickerField : datePickerFields) {
                        if ("mDayPicker".equals(datePickerField.getName())
                                || "mDaySpinner".equals(datePickerField
                                .getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = new Object();
                            dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpd;
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                if (type == 0) {
                    JSONArray dataArray = outJson.getJSONObject(Key.DATA).getJSONArray(Key.ITEMS);
                    JSONObject dataObj = dataArray.getJSONObject(0);
                    cardType.setText(dataObj.getString(Key.TYPE));
                    nameOnCard.setText(dataObj.getString("first_name") + " " + dataObj.getString("last_name"));
                    cardNumber.setText(dataObj.getString(Key.NUMBER));
                    expiry.setText(dataObj.getString("expire_month") + "/" + dataObj.getString("expire_year"));
                    gateway.setText(dataObj.getString(Key.GATEWAY));
                } else if (type == 1) {
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    onBackPressed();
                    finish();
                    //Alert("Success!", outJson.getString(Key.MESSAGE));
                } else {
                    if (cardData.size() > 0)
                        cardData.clear();
                    JSONArray arr = outJson.getJSONArray(Key.DATA);
                    for (int i = 0; i < arr.length(); i++) {
                        CardType cardType = new CardType();
                        //cardType.setId(cardTypeObject.getString(Key.ID));
                        cardType.setName("" + arr.get(i));
                        //cardType.setIcon(cardTypeObject.getString(Key.ICON));
                        cardData.add(cardType);
                    }
                }
            } else
                Alert("Alert!", outJson.getString(Key.MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
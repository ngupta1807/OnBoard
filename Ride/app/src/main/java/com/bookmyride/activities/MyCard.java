package com.bookmyride.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.bookmyride.R;
import com.bookmyride.adapters.PlaceAutoCompleteAdapter;
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class MyCard extends AppCompatActivity implements AsyncTaskCompleteListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    SessionHandler session;
    EditText nameOnCard, cardNumber, cvv, gateway;
    TextView expiry, save, cardType, title;
    EditText /*address_1,*/ address_2, city, state, country, postalCode;
    LinearLayout layAddress;
    static final int DATE_DIALOG_ID = 1;
    private int mYear;
    private int mMonth;
    private int mDay;
    AutoCompleteTextView address;
    private PlaceAutoCompleteAdapter adapter;
    private static final LatLngBounds BOUNDS = new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        init();
        if (!session.getCardAddress().equals("") && !session.getCardAddress().equals("null")) {
            appendAddress();
        }
    }

    private void appendAddress() {
        try {
            JSONObject addressObj = new JSONObject(session.getCardAddress());
            //address_1.setText(addressObj.getString("address"));
            address.setText(addressObj.getString("address"));
            address_2.setText("");
            city.setText(addressObj.getString("city"));
            state.setText("");
            country.setText(addressObj.getString("country"));
            postalCode.setText(addressObj.getString("postalcode"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onBack(View view) {
        onBackPressed();
    }

    int count = 0;

    private void init() {
        session = new SessionHandler(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        title = (TextView) findViewById(R.id.title);
        layAddress = (LinearLayout) findViewById(R.id.lay_address);
        nameOnCard = (EditText) findViewById(R.id.name_on_card);
        nameOnCard.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
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
        address = (AutoCompleteTextView) findViewById(R.id.address);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        address.setTypeface(font);
        adapter = new PlaceAutoCompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS, null);
        address.setAdapter(adapter);
        //address_1 = (EditText) findViewById(R.id.address_1);
        address_2 = (EditText) findViewById(R.id.address_2);
        city = (EditText) findViewById(R.id.city);
        state = (EditText) findViewById(R.id.state);
        country = (EditText) findViewById(R.id.country);
        postalCode = (EditText) findViewById(R.id.postal_code);
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
        save = (TextView) findViewById(R.id.done);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (save.getText().toString().equals("Edit")) {
                    layAddress.setVisibility(View.VISIBLE);
                    cardNumber.setText("");
                    save.setText("Update");
                } else {
                    if (isValidate())
                        saveCreditCard();
                }
            }
        });
        address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PlaceAutoCompleteAdapter.PlaceAutocomplete item = adapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);

                /*
                Issue a request to the Places Geo Data API to retrieve a Place object with additional
                details about the place.
                */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e(Config.TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);
                        double latitude = place.getLatLng().latitude;
                        double longitude = place.getLatLng().longitude;
                        getAddress(latitude, longitude);
                    }
                });
            }
        });
        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int startNum, int before, int count) {
                /*if (start != null) {
                    start = null;
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if (Internet.hasInternet(this)) {
            doGetCardTypes();
        } else {
            Alert("Alert!", getResources().getString(R.string.no_internet));
        }
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
                if (type == 0) {
                    JSONArray dataArray = outJson.getJSONObject(Key.DATA).getJSONArray(Key.ITEMS);
                    JSONObject dataObj = dataArray.getJSONObject(0);
                    cardType.setText(dataObj.getString(Key.TYPE));
                    nameOnCard.setText(dataObj.getString("first_name") + " " + dataObj.getString("last_name"));
                    cardNumber.setText(dataObj.getString(Key.NUMBER));
                    expiry.setText(dataObj.getString("expire_month") + "/" + dataObj.getString("expire_year"));
                    gateway.setText(dataObj.getString(Key.GATEWAY));
                    //layAddress.setVisibility(View.GONE);
                    if (dataObj.has(Key.ADDRESS_LINE_1) && !dataObj.getString(Key.ADDRESS_LINE_1).equals("null"))
                        address.setText(dataObj.getString(Key.ADDRESS_LINE_1));
                    if (dataObj.has(Key.ADDRESS_LINE_2) && !dataObj.getString(Key.ADDRESS_LINE_2).equals("null"))
                        address_2.setText(dataObj.getString(Key.ADDRESS_LINE_2));
                    if (dataObj.has(Key.ADDRESS_CITY) && !dataObj.getString(Key.ADDRESS_CITY).equals("null"))
                        city.setText(dataObj.getString(Key.ADDRESS_CITY));
                    if (dataObj.has(Key.ADDRESS_STATE) && !dataObj.getString(Key.ADDRESS_STATE).equals("null"))
                        state.setText(dataObj.getString(Key.ADDRESS_STATE));
                    if (dataObj.has(Key.ADDRESS_COUNTRY) && !dataObj.getString(Key.ADDRESS_COUNTRY).equals("null"))
                        country.setText(dataObj.getString(Key.ADDRESS_COUNTRY));
                    if (dataObj.has(Key.ADDRESS_POSTAL_CODE) && !dataObj.getString(Key.ADDRESS_POSTAL_CODE).equals("null"))
                        postalCode.setText(dataObj.getString(Key.ADDRESS_POSTAL_CODE));
                    save.setText("Update");
                } else if (type == 1) {
                    save.setText("Update");
                    String number = cardNumber.getText().toString();
                    String output = "XXXX-XXXX-XXXX-" + number.substring(number.length() - 4);
                    cardNumber.setText(output);
                    success("Success!", outJson.getString(Key.MESSAGE));
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
                    if (Internet.hasInternet(this)) {
                        getSavedCard();
                    } else {
                        Alert("Alert!", getResources().getString(R.string.no_internet));
                    }
                }
            } else if (outJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                Alert("Alert!", outJson.getJSONArray(Key.DATA).getJSONObject(0).getString(Key.MESSAGE));
            } else {
                Alert("Alert!", outJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ArrayList<CardType> cardData = new ArrayList<CardType>();
    int type = 0;

    private void getSavedCard() {
        type = 0;
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this, null);
            apiHandler.execute(Config.CREDIT_CARD, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void Alert(final String title, String message) {
        final AlertDialog mDialog = new AlertDialog(MyCard.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (title.equals("Success!") && type == 1 && getIntent().hasExtra("isBack")) {
                    MyCard.this.finish();
                }
            }
        });
        mDialog.show();
    }
    private void success(final String title, String message) {
        final AlertDialog mDialog = new AlertDialog(MyCard.this, false);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setCancelOnTouchOutside(false);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                MyCard.this.finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        mDialog.show();
    }
    private boolean isValidate() {
        if (cardType.getText().toString().trim().isEmpty()
                || nameOnCard.getText().toString().trim().isEmpty()
                || cardNumber.getText().toString().trim().isEmpty()
                || expiry.getText().toString().trim().isEmpty()
                || cvv.getText().toString().trim().isEmpty()
                /*|| gateway.getText().toString().trim().isEmpty()*/) {
            Alert("Oops !!!", "Please complete all fields.");
            return false;
        } else if (!nameOnCard.getText().toString().contains(" ")) {
            Alert("Oops !!!", "Please enter full name of Card Holder.");
            return false;
        } else if (cardNumber.getText().toString().length() < 16) {
            Alert("Oops !!!", "Invalid card number. Please enter valid 16 digit card number.");
            return false;
        } else if (cardNumber.getText().toString().contains("xxxx")) {
            Alert("Oops !!!", "Invalid card number. Please enter valid 16 digit card number.");
            return false;
        } else if (cvv.getText().toString().length() < 3) {
            Alert("Oops !!!", "Invalid CVV. Please enter valid 3 digit CVV number.");
            return false;
        } else if (isCardExpiryDateValid(expiry.getText().toString())) {
            Alert("Oops !!!", "Please select valid card expiry date.");
            return false;
        } else if (address.getText().toString().length() <= 0) {
            Alert("Oops !!!", "Please enter address.");
            return false;
        } else if (city.getText().toString().length() <= 0) {
            Alert("Oops !!!", "Please enter city.");
            return false;
        } else if (country.getText().toString().length() <= 0) {
            Alert("Oops !!!", "Please enter country.");
            return false;
        }
        return true;
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

    /* private void showDatePicker() {
        MonthYearPickerDialog pd = new MonthYearPickerDialog();
        pd.setListener(onDateListen);
        pd.show(getFragmentManager(), "MonthYearPickerDialog");
    }

    DatePickerDialog.OnDateSetListener onDateListen = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            String  m;
            if(monthOfYear < 10)
                m = "0" + monthOfYear;
            else
                m = String.valueOf(monthOfYear);
            expiry.setText(m + "/"+ String.valueOf(year));
        }
    };*/

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
        jsonParams.put(Key.NUMBER, cardNumber.getText().toString().replace(" ", "").trim());
        //Westpac params
        /*jsonParams.put(Key.CARD_EXPIRY_MONTH, expiryMonth);
        jsonParams.put(Key.CARD_EXPIRY_YEAR, expiryYear);
        jsonParams.put(Key.CARD_CVV, cvv.getText().toString());
        jsonParams.put(Key.FIRST_NAME, fName);
        jsonParams.put(Key.LAST_NAME, lName);*/
        //Pin Payment params
        jsonParams.put(Key.NAME, nameOnCard.getText().toString());
        jsonParams.put(Key.EXPIRY_MONTH, expiryMonth);
        jsonParams.put(Key.EXPIRY_YEAR, expiryYear);
        jsonParams.put(Key.CVC, cvv.getText().toString());
        jsonParams.put(Key.GATEWAY, "pinpay"/*"westpac"gateway.getText().toString()*/);
        jsonParams.put(Key.EMAIL, session.getPassengerData().getEmail());
        jsonParams.put(Key.ADDRESS_LINE_1, address.getText().toString());
        jsonParams.put(Key.ADDRESS_LINE_2, address_2.getText().toString());
        jsonParams.put(Key.ADDRESS_CITY, city.getText().toString());
        jsonParams.put(Key.ADDRESS_COUNTRY, country.getText().toString());
        jsonParams.put(Key.ADDRESS_STATE, state.getText().toString());
        jsonParams.put(Key.ADDRESS_POSTAL_CODE, postalCode.getText().toString());
        APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, jsonParams);
        apiHandler.execute(Config.CREDIT_CARD, session.getToken());
    }

    private void doGetCardTypes() {
        type = 3;
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this, null);
            apiHandler.execute(Config.CARD_TYPES, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    Dialog mDialog;

    public void showRideDialog(final int type) {
        mDialog = new Dialog(MyCard.this, R.style.rideDialog);
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

    @Override
    protected Dialog onCreateDialog(int id) {
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
        showDialog(DATE_DIALOG_ID);
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
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(Config.TAG, connectionResult.toString());
    }

    private void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                // Here are some results you can geocode
                String ZIP = "";
                String city = "";
                String state = "";
                String postalCode = "";
                String country = "";

                if (addresses.get(0).getLocality() != null) {
                    city = addresses.get(0).getLocality();
                    this.city.setText(city);
                    Log.d("city", city);
                }

                if (addresses.get(0).getAdminArea() != null) {
                    state = addresses.get(0).getAdminArea();
                    this.state.setText(state);
                    Log.d("state", state);
                }

                if (addresses.get(0).getPostalCode() != null) {
                    postalCode = addresses.get(0).getPostalCode();
                    this.postalCode.setText(postalCode);
                    Log.d("state", postalCode);
                }

                if (addresses.get(0).getCountryName() != null) {
                    country = addresses.get(0).getCountryName();
                    this.country.setText(country);
                    Log.d("country", country);
                    Log.d("country_code", addresses.get(0).getCountryCode());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        MyCard.this.finish();
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
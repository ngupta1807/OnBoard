package com.bookmyride.activities.register;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import com.bookmyride.activities.CountryList;
import com.bookmyride.adapters.PlaceAutoCompleteAdapter;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.models.Profile;
import com.bookmyride.util.AsteriskPasswordTransformationMethod;
import com.bookmyride.views.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Vinod on 2017-01-07.
 */
public class Address extends AppCompatActivity implements
        View.OnClickListener, AsyncTaskCompleteListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    TextView next, sendOTP;
    EditText city, state, postalCode, dialCode, mobile, otp, country;
    SessionHandler session;
    String countryName, countryID, countryCode;
    String preSelectedCountryCode = "", preSelectedNumber = "";
    TextView title;
    double latitude, longitude;
    SimpleDateFormat simpleDateFormat;
    AutoCompleteTextView address;
    private PlaceAutoCompleteAdapter adapter;
    private static final LatLngBounds BOUNDS = new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        init();
        if (!Location.addressSelectedData.equals(""))
            setData();
        else {
            state.setText(session.getLocation().getLocation());
        }
        if (getIntent().hasExtra("upgrade")) {
            appendData();
        }
    }

    private void appendData() {
        //disableControls();
        Profile profile = session.getPassengerData();
        dialCode.setText(profile.getDialCode());
        mobile.setText(profile.getPhone());

        preSelectedNumber = profile.getPhone();
        preSelectedCountryCode = profile.getDialCode();

        sendOTP.setVisibility(View.GONE);
        otp.setVisibility(View.GONE);

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

    private void disableControls() {
        mobile.setFocusable(false);
        mobile.setFocusableInTouchMode(false);
        mobile.setClickable(false);
        dialCode.setOnClickListener(null);
    }

    private void setData() {
        title.setText("Profile");
        try {
            JSONObject addressObject = new JSONObject(Location.addressSelectedData);
            country.setText(addressObject.getString(Key.COUNTRY));
            preSelectedCountryCode = addressObject.getString(Key.DIAL_CODE);
            countryCode = addressObject.getString(Key.DIAL_CODE);
            dialCode.setText(addressObject.getString(Key.DIAL_CODE));
            address.setText(addressObject.getString(Key.ADDRESS));
            city.setText(addressObject.getString(Key.CITY));
            state.setText(addressObject.getString(Key.STATE));
            postalCode.setText(addressObject.getString(Key.POSTALCODE));
            mobile.setText(addressObject.getString(Key.PHONE));
            preSelectedNumber = addressObject.getString(Key.PHONE);

            String lat = addressObject.getString(Key.LATITUDE);
            if (!lat.equals("") && !lat.equals("null"))
                latitude = Double.parseDouble(lat);

            String lng = addressObject.getString(Key.LONGITUDE);
            if (!lng.equals("") && !lng.equals("null"))
                longitude = Double.parseDouble(lng);

            /*if(!state.getText().toString().equals("") &&
                    !country.getText().toString().equals("")) {

                LatLng addressLatLng =
                getLocationFromAddress(
                        address.getText().toString() + ", " + city.getText().toString() + ", " +
                                state.getText().toString() + ", " + country.getText().toString());

                if(addressLatLng == null){
                    Alert("Alert!","Address is not correct. Please fill valid address.");
                }
            }*/
            if (mobile.getText().toString().equalsIgnoreCase(preSelectedNumber)
                    && dialCode.getText().toString().equalsIgnoreCase(preSelectedCountryCode)) {
                sendOTP.setVisibility(View.GONE);
                otp.setVisibility(View.GONE);
            } else {
                sendOTP.setVisibility(View.VISIBLE);
                otp.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    private boolean isValid() {
        if (address.getText().toString().equals("")) {
            Alert("Oops !!!", "Please enter driver address.");
            address.requestFocus();
            return false;
        } else if (city.getText().toString().equals("")) {
            Alert("Oops !!!", "Please enter driver city.");
            city.requestFocus();
            return false;
        } else if (state.getText().toString().equals("")) {
            Alert("Oops !!!", "Please enter State / Province / Region.");
            state.requestFocus();
            return false;
        } else if (postalCode.getText().toString().equals("")) {
            Alert("Oops !!!", "Please enter postal code.");
            postalCode.requestFocus();
            return false;
        } else if (country.getText().toString().equals("")) {
            Alert("Oops !!!", "Please select country.");
            return false;
        } else if (mobile.getText().toString().equals("")) {
            Alert("Oops !!!", "Please enter phone number.");
            mobile.requestFocus();
            return false;
        } else if (mobile.getText().toString().length() < 9) {
            Alert("Oops !!!", "Invalid phone number! Please enter valid phone number.");
            mobile.requestFocus();
            return false;
        } else if (otp.getText().toString().equals("") && otp.getVisibility() == View.VISIBLE) {
            Alert("Oops !!!", "Please enter OTP.");
            otp.requestFocus();
            return false;
        }
        return true;
    }

    private void init() {
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        session = new SessionHandler(this);
        next = (TextView) findViewById(R.id.continu);
        next.setOnClickListener(this);
        sendOTP = (TextView) findViewById(R.id.send_otp);
        sendOTP.setOnClickListener(this);
        country = (EditText) findViewById(R.id.country);
        country.setOnClickListener(this);
        title = (TextView) findViewById(R.id.signin_header_Tv);
        address = (AutoCompleteTextView) findViewById(R.id.address);
       /* Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf");
        address.setTypeface(font);*/
        adapter = new PlaceAutoCompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS, null);
        address.setAdapter(adapter);
        city = (EditText) findViewById(R.id.city);
        state = (EditText) findViewById(R.id.state);
        postalCode = (EditText) findViewById(R.id.postal_code);
        mobile = (EditText) findViewById(R.id.mobile);
        otp = (EditText) findViewById(R.id.otp);
        otp.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        dialCode = (EditText) findViewById(R.id.dial_code);
        city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                city.setText(capitalizeFirstLetter(city.getText().toString()));
            }
        });
        state.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                state.setText(capitalizeFirstLetter(state.getText().toString()));
            }
        });
        mobile.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (mobile.getText().toString().equalsIgnoreCase(preSelectedNumber)
                        && dialCode.getText().toString().equalsIgnoreCase(countryCode)) {
                    sendOTP.setVisibility(View.GONE);
                    otp.setVisibility(View.GONE);
                } else {
                    sendOTP.setVisibility(View.VISIBLE);
                    otp.setVisibility(View.VISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
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
                        latitude = place.getLatLng().latitude;
                        longitude = place.getLatLng().longitude;
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2) {
                countryName = data.getStringExtra("name");
                countryID = data.getStringExtra("id");
                countryCode = data.getStringExtra("code");
                country.setText(countryName);
                dialCode.setText(countryCode);
                if (!preSelectedCountryCode.equalsIgnoreCase(countryCode)
                        || !preSelectedNumber.equalsIgnoreCase(mobile.getText().toString())) {
                    sendOTP.setVisibility(View.VISIBLE);
                    otp.setVisibility(View.VISIBLE);
                } else {
                    sendOTP.setVisibility(View.GONE);
                    otp.setVisibility(View.GONE);
                }
                mobile.requestFocus();
                mobile.setCursorVisible(true);
                mobile.setFocusableInTouchMode(true);
                mobile.setSelection(mobile.getText().length());
                /*getLocationFromAddress(
                        address.getText().toString()+", "+ city.getText().toString() + ", " +
                        state.getText().toString()+", " + countryName);*/
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.continu:
                if (isValid()) {
                    if (otp.getVisibility() == View.VISIBLE)
                        verifyOtp();
                    else {
                        session.saveAddress(address.getText().toString(),
                                city.getText().toString(),
                                state.getText().toString(),
                                postalCode.getText().toString(),
                                country.getText().toString(),
                                countryID,
                                countryCode,
                                mobile.getText().toString(),
                                otp.getText().toString(), "" + latitude, "" + longitude);
                        //session.getAddress();
                        Intent intent = new Intent(getApplicationContext(), VehicleInfo.class);
                        if (getIntent().hasExtra("social_data")) {
                            intent.putExtra("social_data", getIntent().getStringExtra("social_data"));
                            intent.putExtra("type", getIntent().getStringExtra("type"));
                        }
                        if (getIntent().hasExtra("upgrade"))
                            intent.putExtra("upgrade", getIntent().getStringExtra("upgrade"));
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
                break;
            case R.id.country:
                startActivityForResult(new Intent(getApplicationContext(), CountryList.class), 2);
                break;
            case R.id.send_otp:
                if (country.getText().toString().equals("")
                        && dialCode.getText().toString().equals("")) {
                    Alert("Oops !!!", "Please select driver country.");
                } else if (mobile.getText().toString().equals(""))
                    Alert("Oops !!!", "Please enter your mobile number.");
                else if (mobile.getText().toString().length() < 9)
                    Alert("Oops !!!", "Invalid phone number! Please enter valid phone number.");
                else {
                    getOtp();
                }
                break;
        }
    }

    int type;

    private void getOtp() {
        String mobileNo = mobile.getText().toString();
        //if(mobile.getText().toString().length() < 10)
        //mobileNo = "0" + mobileNo;
        type = 0;
        if (Internet.hasInternet(this)) {
            HashMap<String, String> jsonParams = new HashMap<String, String>();
            //jsonParams.put("dail_code", countryCode);
            jsonParams.put("mobile", countryCode + mobileNo.trim());
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, jsonParams);
            apiHandler.execute(Config.SEND_OTP, "");
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void verifyOtp() {
        String mobileNo = mobile.getText().toString();
        //if(mobile.getText().toString().length() < 10)
        //mobileNo = "0" + mobileNo;
        type = 1;
        if (Internet.hasInternet(this)) {
            HashMap<String, String> jsonParams = new HashMap<String, String>();
            jsonParams.put("dynamicCode", otp.getText().toString());
            jsonParams.put("mobile", countryCode + mobileNo);
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, jsonParams);
            apiHandler.execute(Config.SEND_OTP, "");
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    public void onBack(View view) {
        onBackPressed();
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(Address.this, true);
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

    private void handleResponse(String result) {
        try {
            JSONObject outerJson = new JSONObject(result);
            if (outerJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                //otp.setText(outerJson.getString("otp"));
                if (type == 0) {
                    Alert("Success!", "Your one-time password (OTP) has been sent to the mobile number provided. Please enter it into the OTP field on your screen.");
                    otp.requestFocus();
                    otp.setCursorVisible(true);
                    otp.setFocusableInTouchMode(true);
                } else if (type == 1) {
                    session.saveAddress(address.getText().toString(),
                            city.getText().toString(),
                            state.getText().toString(),
                            postalCode.getText().toString(),
                            country.getText().toString(),
                            countryID,
                            countryCode,
                            mobile.getText().toString(),
                            otp.getText().toString(), "" + latitude, "" + longitude);

                    Intent intent = new Intent(getApplicationContext(), VehicleInfo.class);
                    if (getIntent().hasExtra("social_data")) {
                        intent.putExtra("social_data", getIntent().getStringExtra("social_data"));
                        intent.putExtra("type", getIntent().getStringExtra("type"));
                    }
                    if (getIntent().hasExtra("upgrade"))
                        intent.putExtra("upgrade", getIntent().getStringExtra("upgrade"));
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            } else if (outerJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                Alert("Alert!", outerJson.getJSONArray(Key.DATA).getJSONObject(0).getString(Key.MESSAGE));
            } else
                Alert("Alert!", outerJson.getString(Key.MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<android.location.Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            if (address.size() > 0) {
                android.location.Address location = address.get(0);
                location.getLatitude();
                location.getLongitude();
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                p1 = new LatLng(location.getLatitude(), location.getLongitude());
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return p1;
    }

    private void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<android.location.Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                // Here are some results you can geocode
                String ZIP = "";
                String city = "";
                String state = "";
                String postalCode = "";
                String country = "";

                if (addresses.get(0).getPostalCode() != null) {
                    ZIP = addresses.get(0).getPostalCode();
                    Log.d("ZIP", ZIP);
                }

                if (addresses.get(0).getLocality() != null) {
                    city = addresses.get(0).getLocality();
                    this.city.setText(city);
                    Log.d("city", city);
                }

                if (addresses.get(0).getAdminArea() != null) {
                    state = addresses.get(0).getAdminArea();
                    Log.d("state", state);
                }

                if (addresses.get(0).getPostalCode() != null) {
                    postalCode = addresses.get(0).getPostalCode();
                    this.postalCode.setText(postalCode);
                    Log.d("state", state);
                }

                if (addresses.get(0).getCountryName() != null) {
                    country = addresses.get(0).getCountryName();
                    //this.country.setText(country);
                    Log.d("country", country);
                    Log.d("country_code", addresses.get(0).getCountryCode());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.grabid.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.grabid.BuildConfig;
import com.grabid.R;
import com.grabid.adapters.CountryCodeAdapter;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.common.Utils;
import com.grabid.services.GPSTracker;
import com.grabid.views.MySpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by vinod on 10/14/2016.
 */
public class SignUP extends AppCompatActivity implements
        View.OnClickListener, AsyncTaskCompleteListener,
        AdapterView.OnItemSelectedListener {
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    EditText firstName, lastName, password, confirmPassword, phone, otpEdt, referralCode;
    TextView title, cCode, otpTxt, done, mDoB;
    ImageView nav;
    boolean socialLogin = false;
    String google_id = "", facebook_id = "";
    MySpinner countrypicker;
    String[] countrycode = {"+61", "+91"};
    public static String countrycodestr = "";
    int[] flags = {R.drawable.au, R.drawable.in};
    TextView mOtp;
    private static final String TAG = SignIn.class.getSimpleName();
    int type = 0;
    int check = 0;
    GPSTracker gps;
    double latitude, longitude;
    String timeZoneId = "";
    TimeZone timeZone;
    CheckBox accept;
    TextView mTerms;
    RadioButton mMale, mFemale;
    String mGender = "1";
    RadioGroup mGenderRadio;
    int mAge = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            e.toString();
        }
        setContentView(R.layout.activity_register);
        init();
        try {
            if (getIntent().hasExtra("userInfo"))
                appendData(getIntent().getStringExtra("userInfo"));
            if (getIntent().hasExtra("referaal"))
                appendReferral(getIntent().getStringExtra("referaal"));

        } catch (Exception e) {
            e.toString();
        }
    }

    public void appendReferral(String referral_code) {
        referralCode.setText(referral_code);
    }


   /* public void CCclick(View view) {
        countrypicker.performClick();
    }*/


    public int lengthAbn(String str) {
        int characters = 0;
        for (int i = 0, length = str.length(); i < length; i++) {
            if (str.charAt(i) != ' ') {
                characters++;
            }
        }
        return characters;
    }

    int phoneCount = 0;

    private void init() {
        gps = new GPSTracker(SignUP.this);
        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        phone = (EditText) findViewById(R.id.phone);
        nav = (ImageView) findViewById(R.id.nav);
        cCode = (TextView) findViewById(R.id.cc);
        mOtp = (TextView) findViewById(R.id.generateotp);
        otpEdt = (EditText) findViewById(R.id.otpedt);
        done = (TextView) findViewById(R.id.done);
        referralCode = (EditText) findViewById(R.id.referal);
        mDoB = (TextView) findViewById(R.id.dateofbirth);
        mGenderRadio = (RadioGroup) findViewById(R.id.radiogender);
        nav.setOnClickListener(this);
        done.setOnClickListener(this);
        mOtp.setOnClickListener(this);
        cCode.setOnClickListener(this);
        mDoB.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title);
        countrypicker = (MySpinner) findViewById(R.id.countrycode);
        countrypicker.setOnItemSelectedListener(this);
        CountryCodeAdapter customAdapter = new CountryCodeAdapter(SignUP.this, flags, countrycode);
        countrypicker.setAdapter(customAdapter);
        otpTxt = (TextView) findViewById(R.id.otptxt);
        accept = (CheckBox) findViewById(R.id.accept);
        mTerms = (TextView) findViewById(R.id.btn_terms);
        mMale = (RadioButton) findViewById(R.id.radioMale);
        mFemale = (RadioButton) findViewById(R.id.radioFemale);
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "fonts/Raleway_SemiBold.ttf");
        mMale.setTypeface(typeface);
        mFemale.setTypeface(typeface);
        firstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (firstName.getText().toString().length() > 0)
                    firstName.setText(makeFirstLetterCapitel(firstName.getText().toString()));
            }
        });
        lastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (lastName.getText().toString().length() > 0)
                    lastName.setText(makeFirstLetterCapitel(lastName.getText().toString()));
            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (phoneCount <= phone.getText().toString().length()
                        && (phone.getText().toString().length() == 4
                        || phone.getText().toString().length() == 8)) {
                    phone.setText(phone.getText().toString() + " ");
                    int pos = phone.getText().length();
                    phone.setSelection(pos);
                } else if (phoneCount >= phone.getText().toString().length()
                        && (phone.getText().toString().length() == 4
                        || phone.getText().toString().length() == 8)) {
                    phone.setText(phone.getText().toString().substring(0, phone.getText().toString().length() - 1));
                    int pos = phone.getText().length();
                    phone.setSelection(pos);
                }
                phoneCount = phone.getText().toString().length();
            }
        });
        try {
            timeZone = TimeZone.getDefault();
            timeZoneId = timeZone.getID();
            Log.v("", timeZoneId);
        } catch (Exception e) {
            e.toString();
        }


    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date

        // Create the DatePickerDialog instance
        DatePickerDialog datePicker = new DatePickerDialog(SignUP.this,
                datePickerListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePicker.setCancelable(false);
        //datePicker.setTitle("Select the date");
        datePicker.show();
    }

    // Listener
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            mDoB.setText(formattedDate(selectedYear, selectedMonth + 1, selectedDay));
        }
    };

    private String formattedDate(int year, int month, int day) {
        String date = String.format("%d-%02d-%02d", year, month, day);
        mAge = Utils.getAge(year, month, day);
        return date;
    }


    SpannableString spannableString;

    @Override
    protected void onStart() {
        super.onStart();
        String str = getResources().getString(R.string.termsandcondition);
        mTerms.setText(str, TextView.BufferType.SPANNABLE);

        ClickableSpan termsOfServicesClick = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUP.this, TermsAndConditions.class);
                i.putExtra("type", "1");
                startActivity(i);
                try {
                    if (spannableString != null)
                        Selection.removeSelection(spannableString);
                } catch (Exception e) {
                    e.toString();
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#00DC88"));

            }
        };

        ClickableSpan privacyPolicyClick = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUP.this, TermsAndConditions.class);
                i.putExtra("type", "2");
                startActivity(i);
                try {
                    if (spannableString != null)
                        Selection.removeSelection(spannableString);
                } catch (Exception e) {
                    e.toString();
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#00DC88"));
            }
        };
        makeLinks(mTerms, new String[]{"Terms & Conditions", "Privacy Policy"}, new ClickableSpan[]{
                termsOfServicesClick, privacyPolicyClick
        });


    }

    public void makeLinks(TextView textView, String[] links, ClickableSpan[] clickableSpans) {
        try {
            spannableString = new SpannableString(textView.getText());
            for (int i = 0; i < links.length; i++) {
                ClickableSpan clickableSpan = clickableSpans[i];
                String link = links[i];


                int startIndexOfLink = textView.getText().toString().indexOf(link);
                spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length(),
                        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            textView.setMovementMethod(CustomLinkMovementMethod.getInstance());
            textView.setText(spannableString, TextView.BufferType.SPANNABLE);
            textView.setHighlightColor(Color.TRANSPARENT);
        } catch (Exception e) {
            e.toString();
        }
    }

    public class CustomLinkMovementMethod extends LinkMovementMethod {

        // 2. Copy this method from LinkMovementMethod.
        @Override
        public boolean onTouchEvent(TextView widget, Spannable buffer,
                                    MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget);
                        Selection.removeSelection(buffer); // 3. Add this line.
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        Selection.setSelection(buffer,
                                buffer.getSpanStart(link[0]),
                                buffer.getSpanEnd(link[0]));
                    }

                    return true;
                } else {
                    Selection.removeSelection(buffer);
                }
            }

            return super.onTouchEvent(widget, buffer, event);
        }
    }

    public void sendOtp(String mobile) {
        try {
            mobile = mobile.replaceAll("\\s+", "");
            type = 4;
            String url;
            HashMap<String, String> params = new HashMap<>();
            params.put("mobile", mobile);
            params.put("app_id", "1");
            Log.d(Config.TAG, params.toString());
            RestAPICall apiCall = new RestAPICall(this, HTTPMethods.POST, this, params);
            url = Config.SERVER_URL + Config.OTP;
            apiCall.execute(url, "");
        } catch (Exception e) {
            e.toString();
        }

    }

    public void verifyOtp(String mobile, String code) {
        try {
            mobile = mobile.replaceAll("\\s+", "");
            type = 5;
            String url;
            HashMap<String, String> params = new HashMap<>();
            params.put("mobile", mobile);
            params.put("dynamicCode", code);
            params.put("app_id", "1");
            Log.d(Config.TAG, params.toString());
            RestAPICall apiCall = new RestAPICall(this, HTTPMethods.POST, this, params);
            url = Config.SERVER_URL + Config.OTP;
            apiCall.execute(url, "");
        } catch (Exception e) {
            e.toString();
        }

    }

    String authKey = "", VerifiedStatus = "";

    private void appendData(String userData) {
        try {
            JSONObject userInfo = new JSONObject(userData);
            firstName.setText(userInfo.getString("first_name"));
            lastName.setText(userInfo.getString("last_name"));
            authKey = userInfo.getString("auth_key");
            socialLogin = true;
            VerifiedStatus = userInfo.getString("verified_status");
            title.setText("Sign Up");
            if (userInfo.has("sex")) {
                if (userInfo.getString("sex") != null && userInfo.getString("sex").contentEquals("1"))
                    mMale.setChecked(true);
                if (userInfo.getString("sex") != null && userInfo.getString("sex").contentEquals("0"))
                    mFemale.setChecked(true);
            }
            if (userInfo.has("birthday")) {
                if (userInfo.getString("birthday") != null && !userInfo.getString("birthday").contentEquals("") && !userInfo.getString("birthday").contentEquals("null")) {
                    String date = userInfo.getString("birthday").trim();
                    String[] items1 = date.split("/");
                    String month = items1[0];
                    String date1 = items1[1];
                    String year = items1[2];
                    mDoB.setText(year + "-" + month + "-" + date1);
                    Log.e("date", month + ":" + date1 + ":" + year);
                    mAge = Utils.getAge(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(date1));
                    Log.e("age", year + "-" + month + "-" + date1);
                    Log.e("age", "" + mAge);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Boolean value = false;

    public void onSignUp() {
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }
        type = 0;

        if (mFemale.isChecked())
            mGender = "2";
        else
            mGender = "1";
        String dob = mDoB.getText().toString();
        // String mobile = phone.getText().toString().replaceAll("\\s+", "");
        String url;
        HashMap<String, String> params = new HashMap<>();
        params.put("first_name", firstName.getText().toString());
        params.put("last_name", lastName.getText().toString());
        params.put("dob", mDoB.getText().toString());
        params.put("gender", mGender);
        params.put("password_hash", password.getText().toString());
        params.put("confirm_password", confirmPassword.getText().toString());
        params.put("mobile", countrycodestr + phone.getText().toString().replaceAll("\\s+", ""));
        params.put("used_refer_code", referralCode.getText().toString());
        params.put("device_registration_id", new SessionManager(this).getGCMKey());
        params.put("device_os", "ANDROID");
        params.put("timezone", timeZoneId);
        params.put("updateTimeZone", "TimeZoneUpdate");
        if (BuildConfig.logistic)
            params.put("app_id", "1");
        else
            params.put("app_id", "2");
        if (latitude != 0.0) {
            params.put("current_latitude", "" + latitude);
            params.put("current_longitude", "" + longitude);
        }

        if (getIntent().hasExtra("userInfo")) {
            try {
                JSONObject userInfo = new JSONObject(getIntent().getStringExtra("userInfo"));
                if (!userInfo.getString("verified_status").contentEquals(""))
                    params.put("verified_status", VerifiedStatus);
                if (userInfo.has("google_id")) {
                    if (!userInfo.getString("google_id").contentEquals("")) {
                        params.put("google_id", userInfo.getString("google_id"));
                    }
                }
                if (userInfo.has("facebook_id")) {
                    if (!userInfo.getString("facebook_id").contentEquals("")) {
                        params.put("facebook_id", userInfo.getString("facebook_id"));
                    }
                }
                if (userInfo.has("email")) {
                    params.put("email", userInfo.getString("email"));
                }
            } catch (Exception e) {
                e.toString();
            }

        }
        Log.d(Config.TAG, params.toString());
        if (Internet.hasInternet(SignUP.this)) {
            RestAPICall apiCall = new RestAPICall(this, HTTPMethods.POST, this, params);
            url = Config.SERVER_URL + Config.CREATE_USER;
            apiCall.execute(url, "");

        } else {
            showMessage(getResources().getString(R.string.no_internet));
        }

    }


    public String makeFirstLetterCapitel(String input) {
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }


    public boolean isValidChar(CharSequence seq) {
        int len = seq.length();
        for (int i = 0; i < len; i++) {
            char c = seq.charAt(i);
            if ('0' <= c && c <= '9') continue;
            if ('a' <= c && c <= 'z') continue;
            if ('A' <= c && c <= 'Z') continue;
            if (c == '.') continue;
            if (c == '_') continue;
            if (c == '-') continue;
            return false;
        }
        return true;
    }

    private boolean isValid() {
        if (firstName.getText().toString().trim().length() == 0 && lastName.getText().toString().trim().length() == 0
                && phone.getText().toString().trim().length() == 0 && otpEdt.getText().toString().trim().length() == 0
                && password.getText().toString().trim().length() == 0) {
            showMessage(getResources().getString(R.string.completeallfield));
            return false;
        }
        if (firstName.getText().toString().trim().length() == 0) {
            showMessage(getResources().getString(R.string.enterfirstname));
            return false;
        } else if (lastName.getText().toString().trim().length() == 0) {
            showMessage(getResources().getString(R.string.enterlastname));
            return false;
        } else if (phone.getText().toString().trim().length() == 0) {
            showMessage(getResources().getString(R.string.emptymobileno));
            return false;
        } else if (cCode.getText().toString().trim().length() == 0) {
            showMessage(getResources().getString(R.string.countrycode));
            return false;
        } else if (lengthAbn(phone.getText().toString()) < 9) {
            showMessage(getResources().getString(R.string.validmobileno));
            return false;
        } else if (otpEdt.getText().toString().trim().length() == 0) {
            showMessage(getResources().getString(R.string.enterotp));
            return false;
        } else if (password.getText().toString().trim().length() == 0 ||
                password.getText().toString().trim().length() < 6) {
            showMessage(getResources().getString(R.string.enterpassword));
            return false;
        } else if (confirmPassword.getText().toString().trim().length() == 0 ||
                confirmPassword.getText().toString().trim().length() < 6) {
            showMessage(getResources().getString(R.string.enterconformpassword));
            return false;
        } else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            showMessage(getResources().getString(R.string.paswordnotmatch));
            return false;
        } /*else if (mDoB.getText().toString().trim().length() == 0) {
            showMessage("Please select your date of birth.");
            return false;
        } else if (mGenderRadio.getCheckedRadioButtonId() == -1) {
            showMessage("Please select your gender.");
            return false;
        }*/
        if (mDoB.getText().toString().trim().length() > 0) {
            if (mAge < 18 || mAge >= 90) {
                showMessage(getResources().getString(R.string.userage));
                return false;
            }
        }

        if (!accept.isChecked()) {
            showMessage(getResources().getString(R.string.accepttermsandconditions));
            return false;
        }
        if (!(cCode.getText().toString().trim().length() == 0)) {
            if (countrycodestr.contentEquals(""))
                countrycodestr = cCode.getText().toString();
        }
        return true;
    }

    private void handleResponse(String result) {
        try {
            if (type == 4) {
                JSONObject outJson = new JSONObject(result);
                Log.d(Config.TAG, outJson.toString());
                if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                    showMessage("Success!", outJson.getString(Config.MESSAGE));
                } else if (outJson.getInt(Config.STATUS) == APIStatus.UNPROCESSABLE) {
                    showMessage(outJson.getJSONArray(Config.DATA).getJSONObject(0).getString(Config.MESSAGE));
                }
            } else if (type == 5) {
                JSONObject outJson = new JSONObject(result);
                Log.d(Config.TAG, outJson.toString());
                if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                    onSignUp();
                } else if (outJson.getInt(Config.STATUS) == APIStatus.UNPROCESSABLE) {
                    showMessage(outJson.getJSONArray(Config.DATA).getJSONObject(0).getString(Config.MESSAGE));
                }
            } else if (type == 0) {
                JSONObject outJson = new JSONObject(result);
                Log.d(Config.TAG, outJson.toString());
                if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                    UserData(outJson);
                    //   messageDialog("Success!", outJson.getString(Config.MESSAGE));
                } else if (outJson.getInt(Config.STATUS) == APIStatus.UNPROCESSABLE) {
                    showMessage(outJson.getJSONObject(Config.DATA).getJSONArray(Config.USER).getJSONObject(0).getString(Config.MESSAGE));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void UserData(JSONObject outterJson) {
        try {
            JSONObject dataObj = outterJson.getJSONObject(Config.DATA);
            JSONObject UserInfo = dataObj.getJSONObject(Config.USER);
            JSONObject driverProfile = dataObj.getJSONObject(Config.DRIVER_PROFILE);
            JSONObject company = new JSONObject();
            JSONArray vehicle = new JSONArray();
            if (dataObj.get(Config.COMPANY) instanceof JSONObject) {
                company = dataObj.getJSONObject(Config.COMPANY);
                vehicle = company.getJSONArray(Config.VHICLE);
            }
            String sRating = "";
            String dRating = "";
            String AdminApprovalStatus = "";
            String EmailVerified = "";
            try {
                sRating = UserInfo.getString(Keys.SHIPPER_RATING);
                dRating = UserInfo.getString(Keys.DRIVER_RATING);
                AdminApprovalStatus = UserInfo.getString(Keys.ADMIN_APPROVAL_STATUS);
                EmailVerified = UserInfo.getString(Keys.VERIFIED_STATUS);
            } catch (Exception e) {
                e.toString();
            }
            String creditcard = dataObj.optString(Keys.CREDIT_CARD);
            String bankDetail = dataObj.optString(Keys.BANK_DETAIL);

            new SessionManager(this).saveUserDate(UserInfo.getString(Keys.KEY_ID),
                    UserInfo.getString(Config.USER_NAME),
                    UserInfo.getString(Keys.KEY_EMAIL),
                    dataObj.getString(Keys.KEY_IMAGE),
                    UserInfo.getString(Config.TOKEN),
                    UserInfo.toString(), driverProfile.toString(),
                    dataObj.getString(Keys.DRIVER_LIMAGE),
                    company.toString(), vehicle.toString(), sRating, dRating, dataObj.optString(Keys.CREDIT_CARD), dataObj.optString(Keys.BANK_DETAIL), AdminApprovalStatus, EmailVerified, false);
            new SessionManager(this).saveCount("");
            goToHome();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void goToHome() {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        SignUP.this.finish();
    }


    public void messageDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUP.this);
        builder.setTitle(title);
        if (message.equals(""))
            message = "Congratulations you have signed up to GRABiD! Please check your email and confirm your address. Thanks";
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                openLoginScreen();
                new SessionManager(getApplicationContext()).saveTempUserData("");
                new SessionManager(getApplicationContext()).saveTempToken("");

            }
        });
        Dialog d = builder.create();
        d.show();
    }

    private void openLoginScreen() {
        Intent intent = new Intent(getApplicationContext(), SignIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }


    private void showMessage(String message) {
        AlertManager.messageDialog(this, "Alert!", message);
    }

    private void showMessage(String title, String message) {
        AlertManager.messageDialog(this, title, message);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav:
                onBackPressed();
                break;

            case R.id.generateotp:
                if (cCode.getText().toString().trim().length() == 0) {
                    showMessage("Please select country code.");
                    return;
                } else if (phone.getText().toString().trim().length() == 0) {
                    showMessage("Empty field! Please enter your valid mobile number.");
                    return;
                } else if (lengthAbn(phone.getText().toString()) < 9) {
                    showMessage("Please enter valid mobile number.");
                    return;
                } else {
                    if (!(cCode.getText().toString().trim().length() == 0)) {
                        if (countrycodestr.contentEquals(""))
                            countrycodestr = cCode.getText().toString();
                    }
                    sendOtp(countrycodestr + phone.getText().toString());
                }
                break;
            case R.id.done:
                if (isValid())
                    verifyOtp(countrycodestr + phone.getText().toString(), otpEdt.getText().toString());
                break;
            case R.id.cc:
                countrypicker.performClick();
                break;
            case R.id.dateofbirth:
                showDatePicker();
                break;
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //   if (++check > 1) {
        countrycodestr = countrycode[position];
        Log.v("tag", countrycode[position]);
        cCode.setText(countrycodestr);
        //  }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
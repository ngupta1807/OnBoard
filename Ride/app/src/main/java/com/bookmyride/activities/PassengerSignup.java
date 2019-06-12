package com.bookmyride.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.IntentCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.bookmyride.common.Validator;
import com.bookmyride.fcm.NotificationFilters;
import com.bookmyride.imageCrop.Constants;
import com.bookmyride.imageCrop.ImageCropActivity;
import com.bookmyride.imageCrop.PicModeSelectDialogFragment;
import com.bookmyride.models.Profile;
import com.bookmyride.services.RouteService;
import com.bookmyride.util.AsteriskPasswordTransformationMethod;
import com.bookmyride.util.FileOperation;
import com.bookmyride.util.ImageLoader;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by vinod on 2017-01-08.
 */
public class PassengerSignup extends AppCompatActivity implements
        View.OnClickListener, AsyncTaskCompleteListener,
        PicModeSelectDialogFragment.IPicModeSelectListener {
    TextView picProfile, getOTP, btnTerms;
    EditText firstName, lastName, userName, email, password, confirmPassword, mobile, referral;
    String countryName, countryID, code, image = "", identifier = "", socialType = "";
    ImageView editImgProfile, ImgProfile;
    ImageLoader imgLoader;
    SessionHandler session;
    RelativeLayout layImgProfile;
    EditText otp, countryCode;
    TextView title, submit;
    String preSelectedCode = "", preSelectedNumber = "";
    //TrackGPS gps;
    double latitude, longitude;
    CheckBox termCheck;
    LinearLayout layTerm;
    TextInputLayout ilPassword, ilPassword2, ilOTP;
    ScrollView mScroll;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_signup);
        init();
        if (getIntent().hasExtra("social_data")) {
            appendData(getIntent().getStringExtra("social_data"));
            socialType = getIntent().getStringExtra("type");
        }
        if (getIntent().hasExtra("profile")) {
            getUserProfile();
        }
        deleteFiles(getFile("profile_photo.jpg"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(this, RouteService.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(NotificationFilters.LOCATION_CHANGED));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        //stopService(new Intent(this, RouteService.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        //stopService(new Intent(this, RouteService.class));
    }

    private String getFile(String fileName) {
        File mFileTemp;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), fileName);
        } else {
            mFileTemp = new File(getFilesDir(), fileName);
        }
        return mFileTemp.getPath();
    }

    private void appendData(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            email.setText(obj.getString("email"));
            firstName.setText(obj.getString("firstName"));
            lastName.setText(obj.getString("lastName"));
            image = obj.getString("image");
            identifier = obj.getString("identifier");
            if (image.equals("") || image.equals("null")) {
                picProfile.setVisibility(View.VISIBLE);
                layImgProfile.setVisibility(View.GONE);
            } else {
                picProfile.setVisibility(View.GONE);
                layImgProfile.setVisibility(View.VISIBLE);
                imgLoader.DisplayImage(obj.getString("image"), ImgProfile);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.dial_code)
            startActivityForResult(new Intent(getApplicationContext(), CountryList.class), 2);
        else if (view.getId() == R.id.btn_terms)
            startActivity(new Intent(this, TermsConditions.class));
        else if (view.getId() == R.id.submit) {
            if (otp.getVisibility() == View.VISIBLE && otp.getText().toString().length() >= 5) {
                if (isValid())
                    verifyOtp();
            } else if (submit.getText().toString().equals("Edit")) {
                enableControlsForUpdate();
                submit.setText("Update");
            } else if (submit.getText().toString().equals("Update")) {
                updateInfo();
            } else createNewUser();
        } else if (view.getId() == R.id.profile_pic) {
            ImageCropActivity.TEMP_PHOTO_FILE_NAME = "profile_photo.jpg";
            showAddProfilePicDialog();
        } else if (view.getId() == R.id.edit_img_profile) {
            ImageCropActivity.TEMP_PHOTO_FILE_NAME = "profile_photo.jpg";
            showAddProfilePicDialog();
        } else if (view.getId() == R.id.send_otp) {
            if (countryCode.getText().toString().equals("")) {
                Alert("Alert!", "Please select country code.");
            } else if (mobile.getText().toString().equals(""))
                Alert("Alert!", "Please enter your mobile number.");
            else if (mobile.getText().toString().length() < 9)
                Alert("Alert!", "Invalid mobile number! Please enter your valid mobile number.");
            else {
                getOtp();
            }
        } else if (view.getId() == R.id.verify_otp) {
            if (countryCode.getText().toString().equals("")) {
                Alert("Alert!", "Please select country code.");
            } else if (mobile.getText().toString().equals(""))
                Alert("Alert!", "Please enter your mobile number.");
            else if (mobile.getText().toString().length() < 9)
                Alert("Alert!", "Invalid mobile number! Please enter your valid mobile number.");
            else {
                verifyOtp();
            }
        }
    }

    private void getOtp() {
        String mobileNo = mobile.getText().toString();
        otp.setText("");
        if (Internet.hasInternet(this)) {
            HashMap<String, String> jsonParams = new HashMap<String, String>();
            jsonParams.put("mobile", code + mobileNo.trim());
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    try {
                        JSONObject outerJson = new JSONObject(result);
                        if (outerJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                            Alert("Success!", "Your one-time password (OTP) has been sent to the mobile number provided. Please enter it into the OTP field on your screen.");
                        } else if (outerJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                            JSONArray arr = outerJson.getJSONArray(Key.DATA);
                            JSONObject innerObj = arr.getJSONObject(0);
                            Alert("Alert!!!", innerObj.getString(Key.MESSAGE));
                        } else {
                            Alert("Alert!!!", outerJson.getString(Key.MESSAGE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, jsonParams);
            apiHandler.execute(Config.SEND_OTP, "");
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void showAddProfilePicDialog() {
        PicModeSelectDialogFragment dialogFragment = new PicModeSelectDialogFragment();
        dialogFragment.setiPicModeSelectListener(this);
        dialogFragment.show(getFragmentManager(), "picModeSelector");
    }

    private void init() {
        session = new SessionHandler(this);
        imgLoader = new ImageLoader(this);
        title = (TextView) findViewById(R.id.title);
        mScroll = (ScrollView) findViewById(R.id.scroll);
        submit = (TextView) findViewById(R.id.submit);
        submit.setOnClickListener(this);
        getOTP = (TextView) findViewById(R.id.send_otp);
        getOTP.setOnClickListener(this);
        layTerm = (LinearLayout) findViewById(R.id.lay_term);
        otp = (EditText) findViewById(R.id.otp);
        ilPassword = (TextInputLayout) findViewById(R.id.il_password);
        ilPassword2 = (TextInputLayout) findViewById(R.id.il_password2);
        ilOTP = (TextInputLayout) findViewById(R.id.il_otp);
        otp.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        picProfile = (TextView) findViewById(R.id.profile_pic);
        picProfile.setOnClickListener(this);
        ImgProfile = (ImageView) findViewById(R.id.img_profile);
        editImgProfile = (ImageView) findViewById(R.id.edit_img_profile);
        editImgProfile.setOnClickListener(this);
        layImgProfile = (RelativeLayout) findViewById(R.id.lay_proflie_img);
        countryCode = (EditText) findViewById(R.id.dial_code);
        countryCode.setOnClickListener(this);
        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        userName = (EditText) findViewById(R.id.user_name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        password.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        confirmPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        mobile = (EditText) findViewById(R.id.mobile);
        termCheck = (CheckBox) findViewById(R.id.accept);
        btnTerms = (TextView) findViewById(R.id.btn_terms);
        btnTerms.setOnClickListener(this);
        mobile.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (mobile.getText().toString().equalsIgnoreCase(preSelectedNumber)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getOTP.setVisibility(View.GONE);
                            ilOTP.setVisibility(View.GONE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getOTP.setVisibility(View.VISIBLE);
                            ilOTP.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        referral = (EditText) findViewById(R.id.referral);
        if (!session.getSessionCode().equals("") && !session.getSessionCode().equals("null")) {
            referral.setText(session.getSessionCode());
            referral.setFocusable(false);
            referral.setFocusableInTouchMode(false);
            referral.setClickable(false);
        } else {
            referral.setFocusable(true);
            referral.setFocusableInTouchMode(true);
            referral.setClickable(true);
        }
        focusLoose();
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NotificationFilters.LOCATION_CHANGED)) {
                    double lat = intent.getDoubleExtra("lat", 0.0);
                    double lng = intent.getDoubleExtra("lng", 0.0);
                    latitude = lat;
                    longitude = lng;
                }
            }
        };
    }

    boolean flagEmail = false;

    private void focusLoose() {
        userName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false && userName.getText().length() > 0) {
                    validateUserName();
                }
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (flagEmail) {
                    if (hasFocus == false && email.getText().length() > 0)
                        validateEmail();
                }
            }
        });
        /*mobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false && mobile.getText().length() > 0) {
                    validateUserName();
                }
            }
        });*/
    }

    public static final int REQUEST_CODE_UPDATE_PIC = 0x1;

    @SuppressLint("WrongConstant")
    public void openSignIn() {
        Intent intent = new Intent(getApplicationContext(), SignIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showCroppedImage(String mImagePath) {
        if (mImagePath != null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(mImagePath);
            session.saveProfileImgPath(mImagePath);
            ImgProfile.setImageBitmap(myBitmap);
            layImgProfile.setVisibility(View.VISIBLE);
            picProfile.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2) {
                countryName = result.getStringExtra("name");
                countryID = result.getStringExtra("id");
                code = result.getStringExtra("code");
                countryCode.setText(code);
                mobile.requestFocus();
                mobile.setCursorVisible(true);
                mobile.setFocusableInTouchMode(true);
                if (preSelectedCode.equalsIgnoreCase(code)) {
                    getOTP.setVisibility(View.GONE);
                    otp.setVisibility(View.GONE);
                } else {
                    getOTP.setVisibility(View.VISIBLE);
                    otp.setVisibility(View.VISIBLE);
                }
            } else if (requestCode == REQUEST_CODE_UPDATE_PIC) {
                String imagePath = result.getStringExtra(Constants.IntentExtras.IMAGE_PATH);
                showCroppedImage(imagePath);
            }
        } else {
            if (requestCode == REQUEST_CODE_UPDATE_PIC) {
                String errorMsg = result.getStringExtra(ImageCropActivity.ERROR_MSG);
                //Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        session.saveSessionCode("");
        PassengerSignup.this.finish();
    }

    public boolean isValid() {
        if (firstName.getText().length() == 0) {
            firstName.requestFocus();
            focusOnView(firstName);
            Alert("Oops !!!", "Please enter first name.");
            return false;
        } else if (lastName.getText().length() == 0) {
            lastName.requestFocus();
            focusOnView(lastName);
            Alert("Oops !!!", "Please enter last name.");
            return false;
        } else if (userName.getText().length() == 0) {
            userName.requestFocus();
            focusOnView(userName);
            Alert("Oops !!!", "Please enter user name.");
            return false;
        } else if (userName.getText().toString().trim().contains(" ")) {
            userName.requestFocus();
            focusOnView(userName);
            Alert("Oops !!!", "Please enter valid passenger username without spaces.");
            return false;
        } else if (email.getText().length() == 0) {
            email.requestFocus();
            focusOnView(email);
            Alert("Oops !!!", "Please enter valid email address.");
            return false;
        } else if (!Validator.isEmailValid(email.getText().toString())) {
            email.requestFocus();
            focusOnView(email);
            Alert("Oops !!!", "Invalid email. Please enter valid email address.");
            return false;
        } else if ((password.getText().length() == 0 || password.getText().length() < 6)
                && !submit.getText().toString().equalsIgnoreCase("Update")
                && !getIntent().hasExtra("upgrade")) {
            password.requestFocus();
            focusOnView(password);
            Alert("Oops !!!", "Please enter password. Password should be minimum at least 6 character.");
            return false;
        } else if ((confirmPassword.getText().length() == 0 || confirmPassword.getText().length() < 6)
                && !submit.getText().toString().equalsIgnoreCase("Update")
                && !getIntent().hasExtra("upgrade")) {
            confirmPassword.requestFocus();
            focusOnView(confirmPassword);
            Alert("Oops !!!", "Please enter confirm password. Password should be minimum at least 6 character.");
            return false;
        } else if (!password.getText().toString().equals(confirmPassword.getText().toString())
                && !submit.getText().toString().equalsIgnoreCase("Update")
                && !getIntent().hasExtra("upgrade")) {
            confirmPassword.requestFocus();
            focusOnView(confirmPassword);
            Alert("Oops !!!", "Password and confirm password does not match.");
            return false;
        } else if (countryCode.getText().length() == 0) {
            Alert("Oops !!!", "Please enter country code.");
            return false;
        } else if (mobile.getText().length() == 0) {
            mobile.requestFocus();
            focusOnView(mobile);
            Alert("Oops !!!", "Please enter mobile number.");
            return false;
        } else if (mobile.getText().length() < 9) {
            mobile.requestFocus();
            focusOnView(mobile);
            Alert("Oops !!!", "Invalid mobile number! Please enter valid mobile number.");
            return false;
        } else if (ilOTP.getVisibility() == View.VISIBLE && otp.getText().length() == 0) {
            otp.requestFocus();
            focusOnView(otp);
            Alert("Oops !!!", "Please enter OTP.");
            return false;
        } else if (layTerm.getVisibility() == View.VISIBLE && !termCheck.isChecked()) {
            Alert("Alert!", "Please accept terms & conditions.");
            return false;
        }
        return true;
    }

    private void focusOnView(final EditText input) {
        mScroll.post(new Runnable() {
            @Override
            public void run() {
                mScroll.scrollTo(0, input.getBottom());
            }
        });
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(PassengerSignup.this, true);
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

    private void success(String title, String message, final JSONObject json) {
        final AlertDialog mDialog = new AlertDialog(PassengerSignup.this, true);
        mDialog.setDialogTitle(title);
        if (message.equals(""))
            message = "Thanks, Your account with BookMyRide has been created successfully.";
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                //if(submit.getText().toString().equals("UPDATE")) {
                proceedAsPassenger(json);
                //} else openSignIn();
            }
        });
        mDialog.show();
    }

    @SuppressLint("WrongConstant")
    private void goToHome(String userType) {
        new SessionHandler(this).saveUserType(userType);
        Intent intent;
        if (userType.equals("3"))
            intent = new Intent(getApplicationContext(), PassengerHome.class);
        else
            intent = new Intent(getApplicationContext(), DriverHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        PassengerSignup.this.finish();
    }

    private void validateUserName() {
        type = "userName";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", userName.getText().toString().trim());
        params.put("firstName", firstName.getText().toString());
        params.put("lastName", lastName.getText().toString());
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, params);
            apiHandler.execute(Config.VALIDATE_USER, "");
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void disableControls() {
        userName.setFocusable(false);
        email.setFocusable(false);
        firstName.setFocusable(false);
        firstName.setFocusableInTouchMode(false);
        firstName.setClickable(false);
        lastName.setFocusable(false);
        lastName.setFocusableInTouchMode(false);
        lastName.setClickable(false);
        mobile.setFocusable(false);
        mobile.setFocusableInTouchMode(false);
        mobile.setClickable(false);
        countryCode.setOnClickListener(null);
        editImgProfile.setEnabled(false);
    }

    private void enableControlsForUpdate() {
        userName.setFocusable(false);
        if (email.getText().toString().equals("")) {
            email.setFocusable(true);
            email.setFocusableInTouchMode(true);
            email.setClickable(true);
        } else
            email.setFocusable(false);
        firstName.setFocusable(true);
        firstName.setFocusableInTouchMode(true);
        firstName.setClickable(true);
        lastName.setFocusable(true);
        lastName.setFocusableInTouchMode(true);
        lastName.setClickable(true);
        mobile.setFocusable(true);
        mobile.setFocusableInTouchMode(true);
        mobile.setClickable(true);
        countryCode.setOnClickListener(this);
        editImgProfile.setEnabled(true);
    }

    int resType;

    private void getUserProfile() {
        if (getIntent().hasExtra("upgrade")) {
            enableControlsForUpdate();
            //title.setText("PROFILE");
            //submit.setText("Edit");
        } else {
            title.setText("PROFILE");
            /*submit.setText("Edit");
            disableControls();*/
            submit.setText("Update");
            enableControlsForUpdate();
        }
        Profile profile = session.getPassengerData();
        if (profile.getImage().equals("")
                || profile.getImage().equals("null")) {
            picProfile.setVisibility(View.VISIBLE);
            layImgProfile.setVisibility(View.GONE);
        } else {
            picProfile.setVisibility(View.GONE);
            layImgProfile.setVisibility(View.VISIBLE);
            imgLoader.DisplayImage(profile.getImage(), ImgProfile);
        }
        firstName.setText(profile.getFirstName());
        lastName.setText(profile.getLastName());
        userName.setText(profile.getUserName());
        email.setText(profile.getEmail());
        countryCode.setText(profile.getDialCode());
        mobile.setText(profile.getPhone());
        preSelectedCode = profile.getDialCode();
        code = profile.getDialCode();
        preSelectedNumber = profile.getPhone();
        ilPassword.setVisibility(View.GONE);
        ilPassword2.setVisibility(View.GONE);
        getOTP.setVisibility(View.GONE);
        ilOTP.setVisibility(View.GONE);
        referral.setVisibility(View.GONE);
        layTerm.setVisibility(View.GONE);

        /*resType = 0;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", userName.getText().toString().trim());
        params.put("firstName", firstName.getText().toString());
        params.put("lastName", lastName.getText().toString());
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this, null);
            apiHandler.execute(Config.GET_PROFILE + "0", session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
        */
    }

    String type = "";

    private void validateEmail() {
        type = "email";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", userName.getText().toString().trim());
        params.put("firstName", firstName.getText().toString());
        params.put("lastName", lastName.getText().toString());
        params.put("email", email.getText().toString());
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, params);
            apiHandler.execute(Config.VALIDATE_USER, "");
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    public void updateInfo() {
        if (isValid()) {
            resType = 2;
            HashMap<String, String> jsonParams = new HashMap<>();
            if (!session.getProfileImgPath().equals("")) {
                try {
                    profileImg = FileOperation.encodeFileToBase64Binary(session.getProfileImgPath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                jsonParams.put(Key.IMAGE, profileImg);
            }
            jsonParams.put(Key.PHONE, mobile.getText().toString());
            jsonParams.put(Key.FIRST_NAME, firstName.getText().toString());
            jsonParams.put(Key.LAST_NAME, lastName.getText().toString());
            jsonParams.put(Key.DIAL_CODE, countryCode.getText().toString());
            jsonParams.put(Key.USERNAME, userName.getText().toString());
            jsonParams.put(Key.EMAIL, email.getText().toString());
            if (session.isBothTypeUser())
                jsonParams.put(Key.USER_TYPE, "5");
            else
                jsonParams.put(Key.USER_TYPE, "3");
            if (Internet.hasInternet(this)) {
                APIHandler apiHandler = new APIHandler(this, HTTPMethods.PUT, this, jsonParams);
                apiHandler.execute(Config.CREATE_USER + "/" + session.getUserID(), session.getToken());
            } else
                Alert("Alert!", getResources().getString(R.string.no_internet));
        }
    }

    String profileImg = "";

    public void createNewUser() {
        if (isValid()) {
            HashMap<String, String> jsonParams = new HashMap<>();
            if (!session.getProfileImgPath().equals("") && !getIntent().hasExtra("social_data")) {
                try {
                    profileImg = FileOperation.encodeFileToBase64Binary(session.getProfileImgPath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                jsonParams.put(Key.pProfileImg, profileImg);
            }/* else if (!session.getPassengerData().getImage().equals("") &&
                    !session.getPassengerData().getImage().equals("null")) {
                jsonParams.put(Key.pProfileImg, session.getPassengerData().getImage());
            }*/
            jsonParams.put(Key.FIRST_NAME, firstName.getText().toString());
            jsonParams.put(Key.LAST_NAME, lastName.getText().toString());
            jsonParams.put(Key.USERNAME, userName.getText().toString().trim());
            jsonParams.put(Key.EMAIL, email.getText().toString());
            jsonParams.put(Key.PASSWORD_HASH, password.getText().toString());
            jsonParams.put(Key.DIAL_CODE, countryCode.getText().toString());
            jsonParams.put(Key.PHONE, mobile.getText().toString());
            if (getIntent().hasExtra("upgrade"))
                jsonParams.put(Key.USER_TYPE, "5");
            else
                jsonParams.put(Key.USER_TYPE, "3");
            jsonParams.put(Key.DYNAMIC_CODE, otp.getText().toString());
            jsonParams.put(Key.REFER_BY, referral.getText().toString());
            jsonParams.put(Key.DEVICE_TOKEN, new SessionHandler(this).getGCMKey());
            jsonParams.put(Key.DEVICE, "android");
            jsonParams.put(Key.LATITUDE, "" + latitude);
            jsonParams.put(Key.LONGITUDE, "" + longitude);
            if (!identifier.equals("")) {
                jsonParams.put(Key.TYPE, socialType);
                jsonParams.put(Key.IS_SOCIAL, "1");
                jsonParams.put(Key.IDENTIFIER, identifier);
                jsonParams.put(Key.pProfileImg, image);
            }
            if (Internet.hasInternet(this)) {
                APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, jsonParams);
                if (getIntent().hasExtra("upgrade"))
                    apiHandler.execute(Config.UPGRADE_USER, session.getToken());
                else
                    apiHandler.execute(Config.CREATE_USER, "");
            } else
                Alert("Alert!", getResources().getString(R.string.no_internet));
        }
    }

    private void deleteFiles(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("file Deleted :" + path);
            } else {
                System.out.println("file not Deleted :" + path);
            }
        }
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outerJson = new JSONObject(result);
            if (Integer.parseInt(outerJson.getString(Key.STATUS)) == APIStatus.SUCCESS) {
                if (type.equals("")) {
                    /*if(resType == 0){
                        resType = 1;
                        title.setText("PROFILE");
                        submit.setText("UPDATE");
                        JSONObject dataObj = outerJson.getJSONObject(Key.DATA);
                        firstName.append(dataObj.getString(Key.FIRST_NAME));
                        lastName.setText(dataObj.getString(Key.LAST_NAME));
                        userName.setText(dataObj.getString(Key.USERNAME));
                        email.setText(dataObj.getString(Key.EMAIL));
                        countryCode.setText(dataObj.getString(Key.DIAL_CODE));
                        mobile.setText(dataObj.getString(Key.PHONE));
                        if(dataObj.getString(Key.pProfileImg).equals("")
                                || dataObj.getString(Key.pProfileImg).equals("null")){
                            picProfile.setVisibility(View.VISIBLE);
                            layImgProfile.setVisibility(View.GONE);
                        } else {
                            picProfile.setVisibility(View.GONE);
                            layImgProfile.setVisibility(View.VISIBLE);
                            imgLoader.DisplayImage(dataObj.getString(Key.pProfileImg), ImgProfile);
                        }
                        password.setVisibility(View.GONE);
                        confirmPassword.setVisibility(View.GONE);
                        getOTP.setVisibility(View.GONE);
                        otp.setVisibility(View.GONE);
                        referral.setVisibility(View.GONE);
                    } else */
                    if (resType == 2) {
                        deleteFiles(session.getProfileImgPath());
                        session.saveProfileImgPath("");
                        success("Success!", "Your profile has been updated successfully.", outerJson);
                    } else {
                        deleteFiles(session.getProfileImgPath());
                        session.saveProfileImgPath("");
                        JSONObject dataObj;
                        if (outerJson.get(Key.DATA) instanceof JSONArray)
                            dataObj = outerJson.getJSONArray(Key.DATA).getJSONObject(0);
                        else
                            dataObj = outerJson.getJSONObject(Key.DATA);
                        session.saveUserActualType(dataObj.getString(Key.USER_TYPE));
                        if (dataObj.getString(Key.USER_TYPE).equals("5")) {
                            loginResponse = new JSONObject(result);
                            showProceedDialog(outerJson, outerJson.getString(Key.MESSAGE));
                        } else
                            success("Success!", outerJson.getString(Key.MESSAGE), outerJson);
                    }
                } else
                    type = "";
            } else if (Integer.parseInt(outerJson.getString(Key.STATUS)) == APIStatus.UNPROCESSABLE) {
                JSONArray arr = outerJson.getJSONArray(Key.DATA);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject innerObj = arr.getJSONObject(i);
                    String field = innerObj.getString("field");
                    if (field.equals("email") || field.equals("username")) {
                        if (type.equals("email") && field.equals("email")) {
                            email.requestFocus();
                            email.setSelection(email.getText().length());
                            Alert("Oops !!!", innerObj.getString(Key.MESSAGE));
                            return;
                        } else if (type.equals("userName") && field.equals("username")) {
                            email.clearFocus();
                            userName.requestFocus();
                            userName.setSelection(userName.getText().length());
                            Alert("Oops !!!", innerObj.getString(Key.MESSAGE));
                            return;
                        } else if (type.equals("") && field.equals("email")) {
                            email.requestFocus();
                            email.setSelection(email.getText().length());
                            Alert("Oops !!!", innerObj.getString(Key.MESSAGE));
                            return;
                        } else if (type.equals("") && field.equals("username")) {
                            userName.requestFocus();
                            userName.setSelection(userName.getText().length());
                            Alert("Oops !!!", innerObj.getString(Key.MESSAGE));
                            return;
                        } else if (type.equals("userName")) {
                            email.setFocusableInTouchMode(true);
                            email.requestFocus();
                            flagEmail = true;
                            return;
                        }
                    } else {
                        Alert("Alert!", innerObj.getString(Key.MESSAGE));
                        break;
                    }
                }

                type = "";
                //Alert("Oops !!!", outJson.getJSONArray(Key.DATA).getJSONObject(0).getString(Key.MESSAGE));
            } else {
                Alert("Alert!", outerJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    JSONObject loginResponse = null;

    private void loggedInAs(final String userType) {
        if (Internet.hasInternet(this)) {
            APIHandler apiCall = new APIHandler(this, HTTPMethods.GET, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    try {
                        JSONObject outerJson = new JSONObject(result);
                        if (outerJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                            proceedAsPassenger(loginResponse);
                        } else if (outerJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                            Alert("Alert!", outerJson.getString(Key.MESSAGE));
                            //showRideDialog("Alert!", outerJson.getString(Key.MESSAGE));
                        } else
                            Alert("Alert!", outerJson.getString(Key.MESSAGE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
            //apiCall.execute(Config.ACTIVATE_PROFILE + userType, token);
            apiCall.execute(Config.ACTIVATE_PROFILE + userType, session.getToken());
        } else Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void proceedAsPassenger(final JSONObject outJson) {
        try {
            JSONObject dataObj;
            if (outJson.get(Key.DATA) instanceof JSONArray)
                dataObj = outJson.getJSONArray(Key.DATA).getJSONObject(0);
            else
                dataObj = outJson.getJSONObject(Key.DATA);

            String IMG_URL = "";
            if (dataObj.has(Key.IMAGE))
                IMG_URL = dataObj.getString(Key.IMAGE);
            new SessionHandler(getApplicationContext()).savePassengerData(
                    dataObj.getString(Key.ID),
                    "3",
                    dataObj.getString(Key.USERNAME),
                    IMG_URL,
                    dataObj.getString(Key.EMAIL),
                    dataObj.getString(Key.TOKEN),
                    dataObj.getString(Key.FIRST_NAME),
                    dataObj.getString(Key.LAST_NAME),
                    dataObj.getString(Key.PHONE),
                    dataObj.getString(Key.DIAL_CODE));
            new ImageLoader(this).clearCache();
            if (getIntent().hasExtra("isBack")) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                PassengerSignup.this.finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else goToHome("3");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void showProceedDialog(final JSONObject outJson, String message) {
        final AlertDialog mDialog = new AlertDialog(PassengerSignup.this, false);
        mDialog.setDialogTitle("BookMyRide");
        mDialog.setDialogMessage(message + " Please select if you would like to proceed as a driver or passenger.");
        mDialog.setCancelOnTouchOutside(false);
        mDialog.setPositiveButton(getResources().getString(R.string.proceed_as_driver), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                //proceedAsDriver(outJson);
                goToHome("4");
            }
        });
        mDialog.setNegativeButton(getResources().getString(R.string.proceed_as_passenger), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                //proceedAsPassenger(outJson);
                loggedInAs("passenger");
            }
        });
        mDialog.show();
    }

    private void verifyOtp() {
        String mobileNo = mobile.getText().toString();
        //if(mobile.getText().toString().length() < 10)
        //mobileNo = "0" + mobileNo;
        if (Internet.hasInternet(this)) {
            HashMap<String, String> jsonParams = new HashMap<String, String>();
            jsonParams.put("dynamicCode", otp.getText().toString());
            jsonParams.put("mobile", code + mobileNo);
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    try {
                        JSONObject outerJson = new JSONObject(result);
                        if (outerJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                            //Alert("Success!", "You are successfully verified.");
                            otp.setVisibility(View.GONE);
                            if (submit.getText().toString().equals("Update")) {
                                updateInfo();
                            } else createNewUser();
                        } else if (outerJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                            JSONArray arr = outerJson.getJSONArray(Key.DATA);
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject innerObj = arr.getJSONObject(i);
                                if (innerObj.getString("field").equals("dynamicCode"))
                                    Alert("Alert!", innerObj.getString(Key.MESSAGE));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, jsonParams);
            apiHandler.execute(Config.SEND_OTP, "");
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public void onPicModeSelected(String mode) {
        String action = mode.equalsIgnoreCase(Constants.PicModes.CAMERA) ? Constants.IntentExtras.ACTION_CAMERA : Constants.IntentExtras.ACTION_GALLERY;
        actionProfilePic(action);
    }

    private void actionProfilePic(String action) {
        Intent intent = new Intent(this, ImageCropActivity.class);
        intent.putExtra("ACTION", action);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_PIC);
    }
}

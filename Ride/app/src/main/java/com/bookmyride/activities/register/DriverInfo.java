package com.bookmyride.activities.register;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.DatePicker;
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
import com.bookmyride.imageCrop.Constants;
import com.bookmyride.imageCrop.ImageCropActivity;
import com.bookmyride.imageCrop.PicModeSelectDialogFragment;
import com.bookmyride.models.Profile;
import com.bookmyride.util.AsteriskPasswordTransformationMethod;
import com.bookmyride.util.ImageLoader;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by Vinod on 2017-01-07.
 */
public class DriverInfo extends AppCompatActivity
        implements View.OnClickListener, AsyncTaskCompleteListener,
        PicModeSelectDialogFragment.IPicModeSelectListener {
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    TextView picLicence, picProfile, next, licenceExpiry;
    RelativeLayout layImgLicence, layImgProfile;
    ScrollView scroll;
    LinearLayout layTaxi;
    SessionHandler session;
    public static final int REQUEST_CODE_UPDATE_PIC = 0x1;
    EditText firstName, lastName, userName, email, password, confirmPassword, licenceNumber;
    int imgType;
    ImageView editImgLicence, editImgProfile, ImgLicence, ImgProfile, back;
    ImageLoader imgLoader;
    TextView title;
    TextInputLayout ilPassword, ilPassword2;
    ScrollView mScroll;

    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_info);
        init();
        if (!Location.driverSelectedData.equals(""))
            setData();
        if (getIntent().hasExtra("upgrade")) {
            appendData();
        }
        if (getIntent().hasExtra("social_data")) {
            appendData(getIntent().getStringExtra("social_data"));
        }
        deleteFiles(session.getLicenceImgPath());
        deleteFiles(session.getProfileImgPath());
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

    private void disableControls() {
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
        editImgProfile.setEnabled(true);
    }

    private void appendData() {
        disableControls();
        Profile profile = session.getPassengerData();
        firstName.setText(profile.getFirstName());
        lastName.setText(profile.getLastName());
        userName.setText(profile.getUserName());
        email.setText(profile.getEmail());
        ilPassword.setVisibility(View.GONE);
        ilPassword2.setVisibility(View.GONE);
        licenceNumber.setText("");
        licenceExpiry.setText("");

        hasLIcenceImgUrl = false;
        picLicence.setVisibility(View.VISIBLE);
        layImgLicence.setVisibility(View.GONE);

        if (!profile.getImage().equals("") && !profile.getImage().equals("null")) {
            hasProfileImgUrl = true;
            picProfile.setVisibility(View.GONE);
            layImgProfile.setVisibility(View.VISIBLE);
            imgLoader.DisplayImage(profile.getImage(), ImgProfile);
        } else {
            hasProfileImgUrl = false;
            picProfile.setVisibility(View.VISIBLE);
            layImgProfile.setVisibility(View.GONE);
        }
    }

    private void appendData(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            email.setText(obj.getString("email"));
            firstName.setText(obj.getString("firstName"));
            lastName.setText(obj.getString("lastName"));
            if (obj.getString("image").equals("") || obj.getString("image").equals("null")) {
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

    boolean flagEmail = false;
    boolean hasLIcenceImgUrl = false, hasProfileImgUrl = false;

    private void setData() {
        title.setText("Profile");
        try {
            JSONObject driverobject = new JSONObject(Location.driverSelectedData);
            firstName.setText(driverobject.getString(Key.FIRST_NAME));
            lastName.setText(driverobject.getString(Key.LAST_NAME));
            userName.setText(driverobject.getString(Key.USERNAME));
            userName.setFocusable(false);
            email.setText(driverobject.getString(Key.EMAIL));
            email.setFocusable(false);
            ilPassword.setVisibility(View.GONE);
            ilPassword2.setVisibility(View.GONE);
            licenceNumber.setText(driverobject.getString(Key.LICENCE_NUMBER));
            licenceExpiry.setText(driverobject.getString(Key.LICENCE_EXPIRY));
            if (!driverobject.getString(Key.LICENCE_IMAGE).equals("") &&
                    !driverobject.getString(Key.LICENCE_IMAGE).equals("null")) {
                hasLIcenceImgUrl = true;
                picLicence.setVisibility(View.GONE);
                layImgLicence.setVisibility(View.VISIBLE);
                imgLoader.DisplayImage(driverobject.getString(Key.LICENCE_IMAGE), ImgLicence);
            } else {
                hasLIcenceImgUrl = false;
                picLicence.setVisibility(View.VISIBLE);
                layImgLicence.setVisibility(View.GONE);
            }
            if (!driverobject.getString(Key.IMAGE).equals("") &&
                    !driverobject.getString(Key.IMAGE).equals("null")) {
                hasProfileImgUrl = true;
                picProfile.setVisibility(View.GONE);
                layImgProfile.setVisibility(View.VISIBLE);
                imgLoader.DisplayImage(driverobject.getString(Key.IMAGE), ImgProfile);
            } else {
                hasProfileImgUrl = false;
                picProfile.setVisibility(View.VISIBLE);
                layImgProfile.setVisibility(View.GONE);
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

    private void init() {
        session = new SessionHandler(this);
        imgLoader = new ImageLoader(this);
        mScroll = (ScrollView) findViewById(R.id.scroll);
        picLicence = (TextView) findViewById(R.id.licence_pic);
        picLicence.setOnClickListener(this);
        picProfile = (TextView) findViewById(R.id.profile_pic);
        picProfile.setOnClickListener(this);
        title = (TextView) findViewById(R.id.signin_header_Tv);
        layImgLicence = (RelativeLayout) findViewById(R.id.lay_img);
        layImgProfile = (RelativeLayout) findViewById(R.id.lay_proflie_img);
        editImgLicence = (ImageView) findViewById(R.id.edit_img);
        editImgLicence.setOnClickListener(this);
        editImgProfile = (ImageView) findViewById(R.id.edit_img_profile);
        editImgProfile.setOnClickListener(this);
        ImgLicence = (ImageView) findViewById(R.id.img);
        ImgProfile = (ImageView) findViewById(R.id.img_profile);
        scroll = (ScrollView) findViewById(R.id.scroll);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        layTaxi = (LinearLayout) findViewById(R.id.lay_taxi);
        next = (TextView) findViewById(R.id.continu);
        next.setOnClickListener(this);
        ilPassword = (TextInputLayout) findViewById(R.id.il_password);
        ilPassword2 = (TextInputLayout) findViewById(R.id.il_password2);

        firstName = (EditText) findViewById(R.id.f_name);
        lastName = (EditText) findViewById(R.id.l_name);
        userName = (EditText) findViewById(R.id.user_name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirm);
        password.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        confirmPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        licenceNumber = (EditText) findViewById(R.id.licence_number);
        licenceExpiry = (TextView) findViewById(R.id.licence_expiry);
        licenceExpiry.setOnClickListener(this);
        firstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                firstName.setText(capitalizeFirstLetter(firstName.getText().toString()));
            }
        });
        lastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                lastName.setText(capitalizeFirstLetter(lastName.getText().toString()));
            }
        });
        focusLoose();
    }

    private void focusLoose() {
        userName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false && userName.getText().toString().length() > 0) {
                    validateUserName();
                }
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (flagEmail) {
                    if (hasFocus == false && email.getText().toString().length() > 0)
                        validateEmail();
                }
            }
        });
    }

    private void validateUserName() {
        type = "userName";
        com.bookmyride.models.Location loc = session.getLocation();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", userName.getText().toString().trim());
        params.put("firstName", firstName.getText().toString());
        params.put("lastName", lastName.getText().toString());
        if (loc.getIsPassenger().equals("1"))
            params.put(Key.USER_TYPE, "5");
        else params.put(Key.USER_TYPE, "4");
        params.put(Key.LOCATION_ID, loc.getLocationID());
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, params);
            apiHandler.execute(Config.VALIDATE_USER, "");
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    String type = "";

    private void validateEmail() {
        type = "email";
        com.bookmyride.models.Location loc = session.getLocation();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", userName.getText().toString().trim());
        params.put("firstName", firstName.getText().toString());
        params.put("lastName", lastName.getText().toString());
        params.put("email", email.getText().toString());
        params.put(Key.LOCATION_ID, loc.getLocationID());
        if (loc.getIsPassenger().equals("1"))
            params.put(Key.USER_TYPE, "5");
        else params.put(Key.USER_TYPE, "4");
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, params);
            apiHandler.execute(Config.VALIDATE_USER, "");
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
        DatePickerDialog datePicker = new DatePickerDialog(DriverInfo.this,
                datePickerListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.setCancelable(false);
        datePicker.setTitle("Select the date");
        datePicker.show();
    }

    // Listener
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            String year1 = String.valueOf(selectedYear);
            String month1 = String.valueOf(selectedMonth + 1);
            String day1 = String.valueOf(selectedDay);
            //licenceExpry.setText(day1 + "/" + month1 + "/" + year1);
            licenceExpiry.setText(year1 + "-" + month1 + "-" + day1);
        }
    };

    private void showAddProfilePicDialog() {
        PicModeSelectDialogFragment dialogFragment = new PicModeSelectDialogFragment();
        dialogFragment.setiPicModeSelectListener(this);
        dialogFragment.show(getFragmentManager(), "picModeSelector");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.continu:
                if (isValid()) {
                    session.saveProfile(firstName.getText().toString(),
                            lastName.getText().toString(),
                            userName.getText().toString().trim(),
                            email.getText().toString(),
                            password.getText().toString(),
                            licenceNumber.getText().toString(),
                            licenceExpiry.getText().toString());
                    Intent intent = new Intent(getApplicationContext(), Address.class);
                    if (getIntent().hasExtra("social_data")) {
                        intent.putExtra("social_data", getIntent().getStringExtra("social_data"));
                        intent.putExtra("type", getIntent().getStringExtra("type"));
                    }
                    if (getIntent().hasExtra("upgrade"))
                        intent.putExtra("upgrade", getIntent().getStringExtra("upgrade"));
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                break;
            case R.id.licence_pic:
                ImageCropActivity.TEMP_PHOTO_FILE_NAME = "ride_licence_photo.jpg";
                imgType = 0;
                showAddProfilePicDialog();
                break;
            case R.id.profile_pic:
                ImageCropActivity.TEMP_PHOTO_FILE_NAME = "ride_profile_photo.jpg";
                imgType = 1;
                showAddProfilePicDialog();
                break;
            case R.id.edit_img:
                ImageCropActivity.TEMP_PHOTO_FILE_NAME = "ride_licence_photo.jpg";
                imgType = 0;
                showAddProfilePicDialog();
                break;
            case R.id.edit_img_profile:
                ImageCropActivity.TEMP_PHOTO_FILE_NAME = "ride_profile_photo.jpg";
                imgType = 1;
                showAddProfilePicDialog();
                break;
            case R.id.back:
                onBackPressed();
                break;
            case R.id.licence_expiry:
                showDatePicker();
                break;
        }
    }

    public boolean isValid() {
        if (firstName.getText().toString().equals("")) {
            Alert("Oops !!!", "Please enter driver first name.");
            firstName.requestFocus();
            focusOnView(firstName);
            return false;
        } else if (lastName.getText().toString().equals("")) {
            Alert("Oops !!!", "Please enter driver last name.");
            lastName.requestFocus();
            focusOnView(lastName);
            return false;
        } else if (userName.getText().toString().equals("")) {
            Alert("Oops !!!", "Please enter driver username.");
            userName.requestFocus();
            focusOnView(userName);
            return false;
        } else if (userName.getText().toString().trim().contains(" ")) {
            Alert("Oops !!!", "Please enter valid driver username without spaces.");
            userName.requestFocus();
            focusOnView(userName);
            return false;
        } else if (email.getText().toString().equals("")) {
            Alert("Oops !!!", "Please enter driver valid email.");
            email.requestFocus();
            focusOnView(email);
            return false;
        } else if (!isEmailValid(email.getText().toString().trim())) {
            Alert("Oops !!!", "Invalid email. Please enter driver valid email.");
            email.requestFocus();
            focusOnView(email);
            return false;
        } else if ((password.getText().toString().equals("") || password.getText().toString().length() < 6)
                && Location.driverSelectedData.equals("") && !getIntent().hasExtra("upgrade")) {
            Alert("Oops !!!", "Please enter password. Password should be minimum of at least 6 character.");
            password.requestFocus();
            focusOnView(password);
            return false;
        } else if ((confirmPassword.getText().toString().equals("") || confirmPassword.getText().toString().length() < 6)
                && Location.driverSelectedData.equals("") && !getIntent().hasExtra("upgrade")) {
            Alert("Oops !!!", "Please enter confirm password. Password should be minimum of at least 6 character.");
            confirmPassword.requestFocus();
            focusOnView(confirmPassword);
            return false;
        } else if (!password.getText().toString().equals(confirmPassword.getText().toString())
                && Location.driverSelectedData.equals("") && !getIntent().hasExtra("upgrade")) {
            Alert("Oops !!!", "Confirm Password does not match.");
            confirmPassword.requestFocus();
            focusOnView(confirmPassword);
            return false;
        } else if (licenceNumber.getText().toString().length() < 5) {
            Alert("Oops !!!", "Please enter valid driver's licence number.");
            licenceNumber.requestFocus();
            focusOnView(licenceNumber);
            return false;
        } else if (licenceExpiry.getText().toString().length() <= 0) {
            Alert("Oops !!!", "Please choose driver licence expiry date.");
            return false;
        } else if (!Validator.isValidDate(licenceExpiry.getText().toString())) {
            Alert("Oops !!!", "Invalid Date. Please choose valid driver licence expiry date.");
            return false;
        } else if (!hasLIcenceImgUrl && !hasImage(ImgLicence)) {
            Alert("Oops !!!", "Please choose driver's licence image.");
            return false;
        } else if (!hasProfileImgUrl && !hasImage(ImgProfile)) {
            Alert("Oops !!!", "Please choose driver's profile image.");
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

    public static boolean isEmailValid(String str_newEmail) {
        return str_newEmail.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }

        return hasImage;
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (Integer.parseInt(outJson.getString(Key.STATUS)) != 422) {
                if (type.equals("")) {
                    session.saveProfile(firstName.getText().toString(),
                            lastName.getText().toString(),
                            userName.getText().toString().trim(),
                            email.getText().toString(),
                            password.getText().toString(),
                            licenceNumber.getText().toString(),
                            licenceExpiry.getText().toString());
                    Intent i = new Intent(getApplicationContext(), Address.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    type = "";
                }
            } else if (Integer.parseInt(outJson.getString(Key.STATUS)) == APIStatus.UNPROCESSABLE) {
                JSONArray arr = outJson.getJSONArray(Key.DATA);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject innerObj = arr.getJSONObject(i);
                    String field = innerObj.getString("field");

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
                }
                type = "";
                //Alert("Oops !!!", outJson.getJSONArray(Key.DATA).getJSONObject(0).getString(Key.MESSAGE));
            } else
                Alert("Alert!", outJson.getString(Key.MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(DriverInfo.this, true);
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

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == REQUEST_CODE_UPDATE_PIC) {
            if (resultCode == RESULT_OK) {
                String imagePath = result.getStringExtra(Constants.IntentExtras.IMAGE_PATH);
                showCroppedImage(imagePath);
            } else if (resultCode == RESULT_CANCELED) {

            } else {
                String errorMsg = result.getStringExtra(ImageCropActivity.ERROR_MSG);
                //Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                Alert("Alert!", errorMsg);
            }
        }
    }

    @Override
    public void onPicModeSelected(String mode) {
        String action = mode.equalsIgnoreCase(Constants.PicModes.CAMERA) ? Constants.IntentExtras.ACTION_CAMERA : Constants.IntentExtras.ACTION_GALLERY;
        actionProfilePic(action);
    }

    private void actionProfilePic(String action) {
        Intent intent = new Intent(this, ImageCropActivity.class);
        intent.putExtra("ACTION", action);
        if (imgType == 0)
            intent.putExtra("rect", "");
        startActivityForResult(intent, REQUEST_CODE_UPDATE_PIC);
    }

    private void showCroppedImage(String mImagePath) {
        if (mImagePath != null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(mImagePath);
            if (imgType == 0) {
                session.saveLicenceImgPath(mImagePath);
                ImgLicence.setImageBitmap(myBitmap);
                layImgLicence.setVisibility(View.VISIBLE);
                picLicence.setVisibility(View.GONE);
            } else if (imgType == 1) {
                session.saveProfileImgPath(mImagePath);
                ImgProfile.setImageBitmap(myBitmap);
                layImgProfile.setVisibility(View.VISIBLE);
                picProfile.setVisibility(View.GONE);
            }
        }
    }
}
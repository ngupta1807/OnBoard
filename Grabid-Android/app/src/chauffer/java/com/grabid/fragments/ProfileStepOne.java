package com.grabid.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
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
import com.grabid.imageCrop.Constants;
import com.grabid.imageCrop.ImageCropActivity;
import com.grabid.imageCrop.PicModeSelectDialogFragment;
import com.grabid.models.UserInfo;
import com.grabid.views.MySpinner;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by vinod on 10/14/2016.
 */
public class ProfileStepOne extends Fragment implements View.OnClickListener,
        PicModeSelectDialogFragment.IPicModeSelectListener, AdapterView.OnItemSelectedListener, AsyncTaskCompleteListener {
    EditText firstName, lastName, userName, email, phone, referralCode, otpEdt;
    //LinearLayout multiVehicle;
    SessionManager session;
    UserInfo userInfo;
    TextView submit, cancel, done, mSaveExit;
    TextView pic;
    ImageView cCode;
    RelativeLayout layImg;
    ImageView img, editImg;
    ScrollView scroll;
    //RadioGroup multiple;
    //  RadioGroup owner;
    EditText vehicle_owner, vehicle_count;
    RelativeLayout unEditable, mEditable;
    View view;
    public static boolean Iseditable = false;
    public static String countrycodestr = "";
    int[] flags = {R.drawable.au, R.drawable.in};
    MySpinner countrypicker;
    String[] countrycode = {"+61", "+91"};
    int check = 0;
    public boolean IsMultipleSelected = false;
    RadioGroup mradioGroup;
    String profilePath = "";
    boolean Isappenddata = true;
    boolean IsclearCheck = false;
    ProgressBar mProgress;
    TextView mGenerateOtp, mOtptxt, mDoB;
    int type = 0;
    LinearLayout mcClinear;
    //RadioButton mMyes, mMno;
    //  RadioButton mOyes,mOno ;
    RadioButton mMale, mFemale;
    String mGender = "1";
    RadioGroup mGenderRadio;
    int mAge = 500;
    boolean IsSave = false;
    LinearLayout mPay_Mode;
    String mPayment_mode = "";
    CheckBox mCheckcash, mCheckCredit, mCheckCab;
    boolean IsStepOne = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        HomeActivity.title.setText(getResources().getString(R.string.profile));
        view = inflater.inflate(R.layout.profile, null);
        unEditable = (RelativeLayout) view.findViewById(R.id.relativeuneditable);
        mEditable = (RelativeLayout) view.findViewById(R.id.relativeditable);
        session = new SessionManager(getActivity());
        userInfo = session.getUserDetails();
        if (session.getUserDetails().getIslaststep().equals("0")) {
            IsStepOne = false;
        }
        else IsStepOne = true;
        if (!IsStepOne) {
            mEditable.setVisibility(View.VISIBLE);
            unEditable.setVisibility(View.GONE);
            init(view);
            appendData(view);
            HomeActivity.edit.setVisibility(View.GONE);
        } else {
            initunEditable(view);
            appendDataunEditable(view);
        }
        if (!IsStepOne)
            ProfileStepOne.Iseditable = false;
        else
            ProfileStepOne.Iseditable = true;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setLayout(boolean isEditable) {
        if (isEditable) {
            init(view);
            if (IsStepOne)
                if (Isappenddata)
                    appendData(view);
            mEditable.setVisibility(View.VISIBLE);
            unEditable.setVisibility(View.GONE);
            check = 0;

        } else {
            initunEditable(view);
            appendDataunEditable(view);
            mEditable.setVisibility(View.GONE);
            unEditable.setVisibility(View.VISIBLE);
        }
    }


    private void initunEditable(final View view) {
        HomeActivity.edit.setVisibility(View.VISIBLE);
        HomeActivity.edit.setBackgroundResource(R.drawable.edit_top);
        ProfileStepOne.Iseditable = true;
        session = new SessionManager(getActivity());
        firstName = (EditText) view.findViewById(R.id.first_nameun);
        lastName = (EditText) view.findViewById(R.id.last_nameun);
        userName = (EditText) view.findViewById(R.id.usernameun);
        email = (EditText) view.findViewById(R.id.emailun);
        phone = (EditText) view.findViewById(R.id.phoneun);
        referralCode = (EditText) view.findViewById(R.id.referalun);
        //    multiVehicle = (LinearLayout) view.findViewById(R.id.lay_multivehicle);
        mProgress = (ProgressBar) view.findViewById(R.id.progressun);
        submit = (TextView) view.findViewById(R.id.submitun);
        submit.setOnClickListener(this);
        mSaveExit = (TextView) view.findViewById(R.id.saveexit);
        mSaveExit.setOnClickListener(this);
        img = (ImageView) view.findViewById(R.id.imgun);
        vehicle_owner = (EditText) view.findViewById(R.id.vehicle_owner);
        vehicle_count = (EditText) view.findViewById(R.id.vehicle_count);
        cCode = (ImageView) view.findViewById(R.id.cc);
        img.setOnClickListener(this);
        mGenerateOtp = (TextView) view.findViewById(R.id.generateotp);
        otpEdt = (EditText) view.findViewById(R.id.otpedt);
        mOtptxt = (TextView) view.findViewById(R.id.otptxt);
        mPay_Mode = (LinearLayout) view.findViewById(R.id.pay_mode);
        mCheckcash = (CheckBox) view.findViewById(R.id.radiocash);
        mCheckCredit = (CheckBox) view.findViewById(R.id.radiocredicard);
        mCheckCab = (CheckBox) view.findViewById(R.id.radiocabcharge);
        //  mGenerateOtp.setVisibility(View.GONE);
        // mOtptxt.setVisibility(View.GONE);
        //   otpEdt.setVisibility(View.GONE);
        //  mOyes = view.findViewById(R.id.o_yes);
        //  mMyes = view.findViewById(R.id.m_yes);
        //  mOno = view.findViewById(R.id.o_no);
        //   mMno = view.findViewById(R.id.m_no);
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
        mDoB = view.findViewById(R.id.dateofbirth);
        mMale = view.findViewById(R.id.radioMale);
        mFemale = view.findViewById(R.id.radioFemale);
        mGenderRadio = view.findViewById(R.id.radiogender);
    }

    public String makeFirstLetterCapitel(String input) {
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

    private void appendDataunEditable(View view) {
        userInfo = session.getUserDetails();
        firstName.setText(userInfo.getFirstName());
        lastName.setText(userInfo.getLaseName());
        userName.setText(userInfo.getUserName());
        email.setText(userInfo.getEmail());
        try {
            String mobile = userInfo.getMobile();
            Log.v("mobile", "profile mobile" + mobile);
            mobile = mobile.replaceAll("\\s+", "");

          /*  String newMobile = "";
            if (mobile.startsWith("+61") || mobile.startsWith("+91")) {
                if (mobile.substring(3, 4).equals("0")) {
                    newMobile = mobile.replace(mobile.substring(3, 4), "");
                } else {
                    newMobile = mobile;
                }
                phone.setText(newMobile.substring(0, 3) + " " + newMobile.substring(3, newMobile.length()));
            } else {
                phone.setText(userInfo.getMobile());
            }*/

            String firstCharacter = mobile.substring(0, 1);
            if (firstCharacter != null && firstCharacter.contentEquals("0")) {
                String s = mobile;
                mobile = s.replaceFirst("^0*", "");
                if (s.isEmpty()) s = "0";
                //  countrypicker.setSelection(0);
                //  cCode.setText("+61");
                //  phone.setText(mobile);
                phone.setText("+61" + mobile);

            } else if (mobile.length() > 3) {
                String first = mobile.substring(0, 3);
                String last = mobile.substring(3, mobile.length());
                // String last = mobile.substring(mobile.length() - 1, mobile.length());
                if (first.contentEquals("+91")) {
                    //   countrypicker.setSelection(1);
                    //  cCode.setText(first);
                    StringBuilder str;
                    str = new StringBuilder(last);
                    if (last.startsWith("0"))
                        str = str.deleteCharAt(0);
                    last = str.toString();
                    String one = last.substring(0, 4);
                    String two = last.substring(4, 7);
                    String three = last.substring(7, last.length());
                    phone.setText("+91 " + one + " " + two + " " + three);
                } else if (first.contentEquals("+61")) {
                    //     countrypicker.setSelection(0);
                    //   cCode.setText(first);
                    StringBuilder str;
                    str = new StringBuilder(last);
                    if (last.startsWith("0"))
                        str = str.deleteCharAt(0);
                    last = str.toString();
                    String one = last.substring(0, 4);
                    String two = last.substring(4, 7);
                    String three = last.substring(7, last.length());
                    phone.setText("+61 " + one + " " + two + " " + three);
                } else {
                    // countrypicker.setSelection(0);
                    //   cCode.setText("+61");
                    phone.setText(userInfo.getMobile());
                }
                Log.v("", last);
            } else {
                // countrypicker.setSelection(0);
                //    cCode.setText("+61");
                phone.setText(userInfo.getMobile());
            }
        } catch (Exception e) {
            Log.v("mobile", "profile mobile" + e.getMessage());
        }

        referralCode.setText(userInfo.getReferCode());
        //  isOwner = userInfo.getIsOwner().equals("1") ? true : false;
        //  hasMultiVehicle = userInfo.getHasMultipleVehicle().equals("1") ? false : true;
        /*if (isOwner) {
            vehicle_owner.setText("Yes");
        } else {
            vehicle_owner.setText("No");
        }*/
       /* if (hasMultiVehicle) {
            vehicle_count.setText("Multiple Vehicle");
        } else {
            vehicle_count.setText("Single Vehicle");
        }*/
        try {
            if (userInfo.getProfileImage() != null && !userInfo.getProfileImage().contentEquals("") && !userInfo.getProfileImage().contentEquals("null")) {
                img.setVisibility(View.VISIBLE);
                //pic.setVisibility(View.GONE);
                // new ImageLoader(getActivity()).DisplayImage(userInfo.getProfileImage(), img);
                Picasso.with(getActivity()).load(userInfo.getProfileImage()).into(img);
            } else {
                //pic.setVisibility(View.VISIBLE);
                //   img.setVisibility(View.GONE);
                img.setVisibility(View.VISIBLE);
                img.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.defaultimg));
                mProgress.setVisibility(View.GONE);

            }
        } catch (Exception e) {
            e.toString();
        }
    }

    int phoneCount = 0;

    private void init(View view) {
        //  HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.edit.setBackgroundResource(R.drawable.edit_top_grey);
        ProfileStepOne.Iseditable = false;
        session = new SessionManager(getActivity());
        firstName = (EditText) view.findViewById(R.id.first_name);
        lastName = (EditText) view.findViewById(R.id.last_name);
        userName = (EditText) view.findViewById(R.id.username);
       /* if (!IsStepOne) {
            userName.setClickable(true);
            userName.setEnabled(true);
            userName.setFocusable(true);
        } else {
            userName.setClickable(false);
            userName.setEnabled(false);
            userName.setFocusable(false);
        }*/
        email = (EditText) view.findViewById(R.id.email);
        phone = (EditText) view.findViewById(R.id.phone);
        referralCode = (EditText) view.findViewById(R.id.referal);
        //multiVehicle = (LinearLayout) view.findViewById(R.id.lay_multivehicle);
        submit = (TextView) view.findViewById(R.id.submit);
        submit.setOnClickListener(this);
        mSaveExit = (TextView) view.findViewById(R.id.saveexit);
        mSaveExit.setOnClickListener(this);
        layImg = (RelativeLayout) view.findViewById(R.id.lay_img);
        img = (ImageView) view.findViewById(R.id.img);
        pic = (TextView) view.findViewById(R.id.pic);
        mGenerateOtp = (TextView) view.findViewById(R.id.generateotp);
        otpEdt = (EditText) view.findViewById(R.id.otpedt);
        mOtptxt = (TextView) view.findViewById(R.id.otptxt);
        mPay_Mode = (LinearLayout) view.findViewById(R.id.pay_mode);
        mCheckcash = (CheckBox) view.findViewById(R.id.radiocash);
        mCheckCredit = (CheckBox) view.findViewById(R.id.radiocredicard);
        mCheckCab = (CheckBox) view.findViewById(R.id.radiocabcharge);
        //  otpEdt.setTransformationMethod(new AsteriskStarTransformationMethod());
        pic.setOnClickListener(this);
        mGenerateOtp.setOnClickListener(this);
        editImg = (ImageView) view.findViewById(R.id.edit_img);
        editImg.setOnClickListener(this);
        //   owner = (RadioGroup) view.findViewById(R.id.o_group);
        //   owner.setOnCheckedChangeListener(OwnerListener);
        //multiple = (RadioGroup) view.findViewById(R.id.m_group);
        //multiple.setOnCheckedChangeListener(MultiVehicleListener);
        mProgress = (ProgressBar) view.findViewById(R.id.progress);
        cCode = (ImageView) view.findViewById(R.id.cc);
        cCode.setOnClickListener(this);
        mcClinear = (LinearLayout) view.findViewById(R.id.cclinear);
        mcClinear.setOnClickListener(this);
        countrypicker = (MySpinner) view.findViewById(R.id.countrycode);
        countrypicker.setOnItemSelectedListener(ProfileStepOne.this);
        mDoB = view.findViewById(R.id.dateofbirth);
        mDoB.setOnClickListener(this);
        mMale = (RadioButton) view.findViewById(R.id.radioMale);
        mFemale = (RadioButton) view.findViewById(R.id.radioFemale);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Raleway_SemiBold.ttf");
        mMale.setTypeface(typeface);
        mFemale.setTypeface(typeface);
        mGenderRadio = view.findViewById(R.id.radiogender);
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
        CountryCodeAdapter customAdapter = new CountryCodeAdapter(getActivity(), flags, countrycode);
        countrypicker.setAdapter(customAdapter);


        /*phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (phoneCount <= phone.getText().toString().length()
                            && (phone.getText().toString().length() == 3
                            || phone.getText().toString().length() == 6
                            || phone.getText().toString().length() == 9
                    )) {
                        if(phone.getText().toString().length() == 11){
                            phone.setText(phone.getText().toString() + "");
                        }else {
                            phone.setText(phone.getText().toString() + " ");
                        }
                        int pos = phone.getText().length();
                        phone.setSelection(pos);
                    } else if (phoneCount >= phone.getText().toString().length()
                            && (phone.getText().toString().length() == 3
                            || phone.getText().toString().length() == 6
                            || phone.getText().toString().length() == 9
                    )) {
                            phone.setText(phone.getText().toString().substring(0, phone.getText().toString().length() - 1));
                    }
                    int pos = phone.getText().length();
                    phone.setSelection(pos);
                    phoneCount = phone.getText().toString().length();
                }catch(Exception ex){
                    Log.v("error","phone er;;"+ex.getMessage());
                }
            }
        });*/
    }

    /*public void RadioDialog(Context ctx, String title, String message, final boolean IsFirst) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mMyes.setChecked(true);
               *//* if (IsFirst)
                    mOyes.setChecked(true);
                else
                    mMyes.setChecked(true);*//*

            }
        });
        Dialog d = builder.create();
        d.show();
    }*/

    /*RadioGroup.OnCheckedChangeListener OwnerListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            if (checkedId == R.id.o_yes) {
              //  isOwner = true;
                multiVehicle.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.o_no) {
               // isOwner = false;
                multiVehicle.setVisibility(View.GONE);
                hasMultiVehicle = false;
                if (userInfo.getHasMultipleVehicle().equals("1") ? false : true) {
                    if (!Isappenddata)
//                        AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.removeVehicleALertSingle));
                        RadioDialog(getActivity(), "Alert!", getResources().getString(R.string.removeVehicleALertSingle), true);
                }
            }

            if (IsMultipleSelected) {
                if (mradioGroup != null) {
                    IsclearCheck = true;
                    mradioGroup.clearCheck();
                    //  RadioButton buttonyes = (RadioButton) mradioGroup.findViewById(R.id.m_yes);
                    //  buttonyes.setChecked(false);
                }
            }

        }
    };*/
    //boolean hasMultiVehicle = false;
    //boolean isOwner = false;
    /*RadioGroup.OnCheckedChangeListener MultiVehicleListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            if (checkedId == R.id.m_yes) {
                //if (isOwner)
                hasMultiVehicle = true;
            } else if (checkedId == R.id.m_no) {
                hasMultiVehicle = false;
                if (userInfo.getHasMultipleVehicle().equals("1") ? false : true) {
                    if (!Isappenddata)
                        if (!IsclearCheck)
//                            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.removeVehicleALertSingle));
                            RadioDialog(getActivity(), "Alert!", getResources().getString(R.string.removeVehicleALertSingle), false);
                        else {
                            IsclearCheck = false;
                        }
                }
            }
            IsMultipleSelected = true;
            mradioGroup = radioGroup;
            IsclearCheck = false;


        }
    };
*/
    public void removeVehicleAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog d = builder.create();
        d.show();
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

    public void sendOtp(String mobile) {
        try {
            mobile = mobile.replaceAll("\\s+", "");
            type = 4;
            String url;
            HashMap<String, String> params = new HashMap<>();
            params.put("mobile", mobile);
            params.put("app_id", "2");
            Log.d(Config.TAG, params.toString());
            RestAPICall apiCall = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            url = Config.SERVER_URL + Config.OTP;
            apiCall.execute(url, "");
        } catch (Exception e) {
            e.toString();
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.generateotp) {
            if (cCode.getDrawable() == null) {
                showMessage("Please select country code.");
                return;
            }
            /*if (cCode.getText().toString().trim().length() == 0) {
                showMessage("Please select country code.");
                return;
            }*/
            else if (phone.getText().toString().trim().length() == 0) {
                showMessage("Empty field! Please enter your valid phone number.");
                return;
            } else if (lengthAbn(phone.getText().toString()) < 9) {
                showMessage("Please enter valid phone number.");
                return;
            } else {
                /*if (!(cCode.getText().toString().trim().length() == 0)) {
                    if (countrycodestr.contentEquals(""))
                        countrycodestr = cCode.getText().toString();
                }*/
                if (!(cCode.getDrawable() == null)) {
                    if (countrycodestr.contentEquals("")) {
                        if (((String) cCode.getTag()).contentEquals("in"))
                            countrycodestr = "+91";
                        else
                            countrycodestr = "+61";
                    }
                    //countrycodestr = cCode.getText().toString();
                }
                sendOtp(countrycodestr + phone.getText().toString());
            }
        }
        if (view.getId() == R.id.cc) {
            countrypicker.performClick();
        }
        if (view.getId() == R.id.cclinear) {
            countrypicker.performClick();
        }
        if (view.getId() == R.id.submit) {
            showCompanyProfile(false);
        }
        if (view.getId() == R.id.saveexit) {
            showCompanyProfile(true);
        }
        if (view.getId() == R.id.submitun) {
            showCompanyProfile(false);
        }
        if (view.getId() == R.id.dateofbirth) {
            showDatePicker();
        }
        if (view.getId() == R.id.pic) {
            ImageCropActivity.TEMP_PHOTO_FILE_NAME = "profile_photo.jpg";
            showAddProfilePicDialog();
        } else if (view.getId() == R.id.edit_img) {
            ImageCropActivity.TEMP_PHOTO_FILE_NAME = "profile_photo.jpg";
            showAddProfilePicDialog();
        }
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date

        // Create the DatePickerDialog instance
        DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                datePickerListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePicker.setCancelable(false);
        //  datePicker.setTitle("Select the date");
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

   /* public void checkuserValid() {
        String url;
        HashMap<String, String> params = new HashMap<>();
        params.put("username", userName.getText().toString());
        params.put("email", email.getText().toString());
        params.put("used_refer_code", referralCode.getText().toString());
        Log.d(Config.TAG, params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall apiCall = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            if (!authKey.contentEquals("")) {
                url = Config.SERVER_URL + Config.VALIDATE_UPDATE_USER;
                apiCall.execute(url, authKey);
            } else {
                url = Config.SERVER_URL + Config.VALIDATE_NEW_USER;
                apiCall.execute(url, "");
            }
        } else {
            showMessage(getResources().getString(R.string.no_internet));
        }
    }*/

    public void SaveCapturedImagePath(String path) {
      /*  SharedPreferences myPrefs = getActivity().getSharedPreferences(Config.PREF_NAME, 0);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putString("profile_photo", path);
        prefsEditor.commit();*/
        // ProfileModel.setProfileimg(path);
        profilePath = path;
    }

    private void showMessage(String message) {
        AlertManager.messageDialog(getActivity(), "Alert!", message);
    }

    public void setmPay_Mode() {
        mPayment_mode = "";
        if (mCheckcash.isChecked())
            mPayment_mode = "1";
        if (mCheckCredit.isChecked()) {
            if (!mPayment_mode.contentEquals(""))
                mPayment_mode = mPayment_mode + ",2";
            else
                mPayment_mode = "2";
        }
        if (mCheckCab.isChecked()) {
            if (!mPayment_mode.contentEquals(""))
                mPayment_mode = mPayment_mode + ",3";
            else
                mPayment_mode = "3";
        }
    }

    private boolean isUserValid(Boolean check) {
        setmPay_Mode();
        if(check==true) {
            if (!IsStepOne) {
                if (userName.getText().toString().trim().length() > 0) {
                    if (!isValidChar(userName.getText().toString())) {
                        showMessage(getActivity().getResources().getString(R.string.usernamevalid));
                        return false;
                    }
                }
                if (mGenerateOtp.getVisibility() == View.VISIBLE) {
                    if (otpEdt.getText().toString().trim().length() == 0) {
                        showMessage(getActivity().getResources().getString(R.string.enterotp));
                        return false;
                    }
                }
                if (email.getText().toString().trim().length() > 0) {
                    if (!Utils.isEmailValid(email.getText().toString().trim())) {
                        showMessage(getActivity().getResources().getString(R.string.invalidemail));
                        return false;
                    }
                }
                else if (phone.getText().toString().trim().length() > 0) {
                    if (lengthAbn(phone.getText().toString()) < 9) {
                        showMessage(getActivity().getResources().getString(R.string.validmobileno));
                        return false;
                    }
                }
                 else if (mDoB.getText().toString().trim().length() > 0) {
                        if ((mAge < 18 || mAge >= 90) && mAge != 500) {
                            showMessage(getActivity().getResources().getString(R.string.userage));
                            return false;
                        }
                }
                else if (mPayment_mode.contentEquals("")) {
                    showMessage(getActivity().getResources().getString(R.string.paymentoption));
                    return false;
                }
                }
            }
            else{
                if (firstName.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.enterfirstname));
                    return false;
                } else if (lastName.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.enterlastname));
                    return false;

                }
                if (userName.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.enterusername));
                    return false;
                } else if (!isValidChar(userName.getText().toString())) {
                    showMessage(getActivity().getResources().getString(R.string.usernamevalid));
                    return false;
                }
                if (mGenerateOtp.getVisibility() == View.VISIBLE) {
                    if (otpEdt.getText().toString().trim().length() == 0) {
                        showMessage(getActivity().getResources().getString(R.string.enterotp));
                        return false;
                    }
                }
                if (email.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.emptyemail));
                    return false;
                } else if (!Utils.isEmailValid(email.getText().toString().trim())) {
                    showMessage(getActivity().getResources().getString(R.string.invalidemail));
                    return false;
                } else if (cCode.getDrawable() == null) {
                    showMessage(getActivity().getResources().getString(R.string.countrycode));
                    return false;
                } else if (phone.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.emptymobileno));
                    return false;
                } else if (lengthAbn(phone.getText().toString()) < 9) {
                    showMessage(getActivity().getResources().getString(R.string.validmobileno));
                    return false;
                }
                if (mDoB.getText().toString().trim().length() > 0) {
                    if ((mAge < 18 || mAge >= 90) && mAge != 500) {
                        showMessage(getActivity().getResources().getString(R.string.userage));
                        return false;
                    }
                }
                if (mPayment_mode.contentEquals("")) {
                    showMessage(getActivity().getResources().getString(R.string.paymentoption));
                    return false;
                }
                if (!(cCode.getDrawable() == null)) {
                    if (countrycodestr.contentEquals("")) {
                        if (((String) cCode.getTag()).contentEquals("in"))
                            countrycodestr = "+91";
                        else
                            countrycodestr = "+61";
                    }
                }
            }
        return true;
    }


    public void UpdateDesign(boolean IsLayout) {
        Log.v("updatedesign", "updatedesign");
        HomeActivity.title.setText("Profile");
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.filter.setVisibility(View.GONE);
        if (!IsLayout) {
            if (ProfileStepOne.Iseditable)
                HomeActivity.edit.setBackgroundResource(R.drawable.edit_top);
            else
                HomeActivity.edit.setBackgroundResource(R.drawable.edit_top_grey);
        }
        if (IsLayout)
            setLayout(ProfileStepOne.Iseditable);
        HomeActivity.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("", "");
                setLayout(ProfileStepOne.Iseditable);
            }
        });


    }

    private void appendData(View view) {
        Isappenddata = true;
        userInfo = session.getUserDetails();
        firstName.setText(userInfo.getFirstName());
        lastName.setText(userInfo.getLaseName());
        if (userInfo.getUser_payment_modes() != null && !userInfo.getUser_payment_modes().contentEquals("")) {
            if (userInfo.getUser_payment_modes().contains("1"))
                mCheckcash.setChecked(true);
            if (userInfo.getUser_payment_modes().contains("2"))
                mCheckCredit.setChecked(true);
            if (userInfo.getUser_payment_modes().contains("3"))
                mCheckCab.setChecked(true);
        }
        String mobile = userInfo.getMobile();
        mobile = mobile.replaceAll("\\s+", "");
        if (mobile != null && !mobile.contentEquals("")) {
            try {
                String firstCharacter = mobile.substring(0, 1);
                if (firstCharacter != null && firstCharacter.contentEquals("0")) {
                    String s = mobile;
                    mobile = s.replaceFirst("^0*", "");
                    if (s.isEmpty()) s = "0";
                    countrypicker.setSelection(0);
                    //    cCode.setText("+61");
                    cCode.setImageDrawable(getResources().getDrawable(R.drawable.au));
                    cCode.setTag("au");
                    phone.setText(mobile);
                    countrycodestr = "+61";
                } else if (mobile.length() > 3) {
                    String first = mobile.substring(0, 3);
                    String last = mobile.substring(3, mobile.length());
                    // String last = mobile.substring(mobile.length() - 1, mobile.length());
                    if (first.contentEquals("+91")) {
                        countrypicker.setSelection(1);
                        //  cCode.setText(first);
                        cCode.setImageDrawable(getResources().getDrawable(R.drawable.in));
                        cCode.setTag("in");
                        String one = last.substring(0, 4);
                        String two = last.substring(4, 7);
                        String three = last.substring(7, last.length());
                        phone.setText(one + " " + two + " " + three);
                        countrycodestr = "+91";
                        //phone.setText(last);
                    } else if (first.contentEquals("+61")) {
                        countrypicker.setSelection(0);
                        // cCode.setText(first);
                        cCode.setImageDrawable(getResources().getDrawable(R.drawable.au));
                        cCode.setTag("au");
                        String one = last.substring(0, 4);
                        String two = last.substring(4, 7);
                        String three = last.substring(7, last.length());
                        phone.setText(one + " " + two + " " + three);
                        countrycodestr = "+61";
                        //  phone.setText(last);
                    } else {
                        // countrypicker.setSelection(0);
                        //   cCode.setText("+61");
                        phone.setText(userInfo.getMobile());
                    }
                    Log.v("", last);
                } else {
                    // countrypicker.setSelection(0);
                    //    cCode.setText("+61");
                    phone.setText(userInfo.getMobile());
                }
            } catch (Exception e) {
                e.toString();
            }
        }
        if (userInfo.getDob() != null && !userInfo.getDob().contentEquals("null") && !userInfo.getDob().equals("0"))
            mDoB.setText(userInfo.getDob());
        if (userInfo.getDob() != null && !userInfo.getGender().equals("0")) {
            if (userInfo.getGender().equals("1"))
                mMale.setChecked(true);
            else if (userInfo.getGender().equals("2"))
                mFemale.setChecked(true);
        }
        if (!userInfo.getVerifiedStatus().equals("0")) {
            email.setText(userInfo.getEmail());
        }
       // if (IsStepOne) {
        if(!userInfo.getUserName().equals("null"))
            userName.setText(userInfo.getUserName());
        if(!userInfo.getEmail().equals("null"))
            email.setText(userInfo.getEmail());
            referralCode.setText(userInfo.getReferCode());
            profilePath = userInfo.getProfileImage();
            try {
                if (userInfo.getProfileImage() != null && !userInfo.getProfileImage().contentEquals("") && !userInfo.getProfileImage().contentEquals("null")) {
                    layImg.setVisibility(View.VISIBLE);
                    pic.setVisibility(View.GONE);
                    String profileimg = userInfo.getProfileImage();
//                    new ImageLoader(getActivity()).DisplayImage(userInfo.getProfileImage(), img);
                    Picasso.with(getActivity()).load(userInfo.getProfileImage()).into(img);
                } else {
                    layImg.setVisibility(View.VISIBLE);
                    pic.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    img.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.defaultimg));
                }
            } catch (Exception e) {
                e.toString();
            }
            Isappenddata = false;

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
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
                    try {
                        if (phone.getText().toString().length() > 0) {
                            /*if (!(cCode.getText().toString().trim().length() == 0)) {
                                if (countrycodestr.contentEquals(""))
                                    countrycodestr = cCode.getText().toString();
                            }*/
                            if (!(cCode.getDrawable() == null)) {
                                if (countrycodestr.contentEquals("")) {
                                    if (((String) cCode.getTag()).contentEquals("in"))
                                        countrycodestr = "+91";
                                    else
                                        countrycodestr = "+61";
                                }
                                //countrycodestr = cCode.getText().toString();
                            }

                            if (!userInfo.getMobile().contentEquals(countrycodestr + phone.getText().toString().replaceAll("\\s+", ""))) {
                                mGenerateOtp.setVisibility(View.VISIBLE);
                                mOtptxt.setVisibility(View.VISIBLE);
                                otpEdt.setVisibility(View.VISIBLE);
                            } else {
                                mGenerateOtp.setVisibility(View.GONE);
                                mOtptxt.setVisibility(View.GONE);
                                otpEdt.setVisibility(View.GONE);
                            }
                        }

                    } catch (Exception e) {
                        e.toString();
                    }
                } catch (Exception e) {
                    e.toString();
                }
            }
        });
    }

    public int lengthAbn(String str) {
        int characters = 0;
        for (int i = 0, length = str.length(); i < length; i++) {
            if (str.charAt(i) != ' ') {
                characters++;
            }
        }
        return characters;
    }

    public void profileCompany() {
        if (mMale.isChecked())
            mGender = "1";
        else
            mGender = "2";
        Bundle bundle = new Bundle();
        bundle.putString("firstName", firstName.getText().toString());
        bundle.putString("lastName", lastName.getText().toString());
        bundle.putString("userName", userName.getText().toString());
        bundle.putString("email", email.getText().toString());
        if (!ProfileStepOne.Iseditable)
//            bundle.putString("phone", cCode.getText().toString() + phone.getText().toString().replaceAll("\\s+", ""));
            bundle.putString("phone", countrycodestr + phone.getText().toString().replaceAll("\\s+", ""));
            //  bundle.putString("phone", phone.getText().toString());
        else
            bundle.putString("phone", userInfo.getMobile());
        bundle.putString("referralCode", referralCode.getText().toString());
        // bundle.putString("hasMultiVehicle", hasMultiVehicle ? "2" : "1");
        // bundle.putString("isOwner", isOwner ? "1" : "0");
        bundle.putString("email", email.getText().toString());
        bundle.putString("profile_img", profilePath);
        if (!ProfileStepOne.Iseditable)
            bundle.putString("gender", mGender);
        else if (userInfo.getGender() != null && !userInfo.getGender().contentEquals("null") && !userInfo.getGender().equals("0"))
            bundle.putString("gender", userInfo.getGender());
        else
            bundle.putString("gender", mGender);
        if (!ProfileStepOne.Iseditable)
            bundle.putString("dob", mDoB.getText().toString());
        else if (userInfo.getDob() != null && !userInfo.getDob().contentEquals("null") && !userInfo.getDob().equals("0"))
            bundle.putString("dob", userInfo.getDob());
        else
            bundle.putString("dob", mDoB.getText().toString());
        Bundle userInfoo = new Bundle();
        userInfoo.putBundle("userInfo", bundle);
        Log.v("userInfo", "userInfo1" + userInfo);
        String backStateName = this.getClass().getName();
        Fragment fragment;
        /*if (!IsStepOne)
            fragment = new VehicleOwnerProfile();
        else*/
        fragment = new ProfileStepTwo();
        fragment.setArguments(userInfoo);
        getActivity().getFragmentManager().beginTransaction().add(R.id.container, fragment, backStateName)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private void showCompanyProfile(boolean IsSave) {
        this.IsSave = IsSave;
        if (!ProfileStepOne.Iseditable) {
            if (isUserValid(IsSave)) {
                if (mGenerateOtp.getVisibility() == View.VISIBLE) {
                    verifyOtp(countrycodestr + phone.getText().toString(), otpEdt.getText().toString());
                } else
                    validateUser();
                //profileCompany();
            }
        } else {
            //profileCompany();
            saveProfileData();
        }

    }

    public void saveProfileData() {
        type = 6;
        HashMap<String, String> params = new HashMap<>();
        String profile_pic = "";
        try {
            String profileimg = session.getProfileImage();
            // profile_pic = encodeFileToBase64Binary(session.getProfileImage());
            if (!profilePath.contentEquals("") && !profilePath.contains("http"))
                profile_pic = Utils.encodeFileToBase64Binary(profilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mFemale.isChecked())
            mGender = "2";
        else
            mGender = "1";

        params.put("first_name", firstName.getText().toString());
        params.put("last_name", lastName.getText().toString());
        if (!ProfileStepOne.Iseditable)
            params.put("username", userName.getText().toString());
        else
            params.put("username", userInfo.getUserName());
        params.put("email", email.getText().toString());
        if (!ProfileStepOne.Iseditable)
            params.put("pay_mode_id", mPayment_mode);
        else
            params.put("pay_mode_id", userInfo.getUser_payment_modes());
        if (!ProfileStepOne.Iseditable)
            params.put("mobile", countrycodestr + phone.getText().toString().replaceAll("\\s+", ""));
            //  bundle.putString("phone", phone.getText().toString());
        else
            params.put("mobile", userInfo.getMobile());
        params.put("used_refer_code", referralCode.getText().toString());
        // params.put("vehicle_qty_type", hasMultiVehicle ? "2" : "1");
        //  params.put("is_owner", userInfo.getString("isOwner"));
        if (!ProfileStepOne.Iseditable)
            params.put("gender", mGender);
        else if (userInfo.getGender() != null && !userInfo.getGender().contentEquals("null") && !userInfo.getGender().equals("0"))
            params.put("gender", userInfo.getGender());
        else
            params.put("gender", mGender);
        if (!ProfileStepOne.Iseditable)
            params.put("dob", mDoB.getText().toString());
        else if (userInfo.getDob() != null && !userInfo.getDob().contentEquals("null") && !userInfo.getDob().equals("0"))
            params.put("dob", userInfo.getDob());
        else
            params.put("dob", mDoB.getText().toString());
        if (!profile_pic.equals(""))
            params.put("profile_image", profile_pic);
        String url = Config.SERVER_URL + Config.PROFILE_DATA;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall apiCall = new RestAPICall(getActivity(), HTTPMethods.PUT, this, params);
            apiCall.execute(url, session.getToken());
        } else {
            showMessage(getResources().getString(R.string.no_internet));
        }
    }

    public void validateUser() {
        type = 0;
        String url;
        HashMap<String, String> params = new HashMap<>();
        params.put("username", userName.getText().toString());
        params.put("email", email.getText().toString());
        // params.put("used_refer_code", referralCode.getText().toString());
        Log.d(Config.TAG, params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall apiCall = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            if (!session.getToken().contentEquals("")) {
                url = Config.SERVER_URL + Config.VALIDATE_UPDATE_USER;
                apiCall.execute(url, session.getToken());
            } else {
                url = Config.SERVER_URL + Config.VALIDATE_NEW_USER;
                apiCall.execute(url, "");
            }
        } else {
            showMessage(getResources().getString(R.string.no_internet));
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
            params.put("app_id", "2");
            Log.d(Config.TAG, params.toString());
            RestAPICall apiCall = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            url = Config.SERVER_URL + Config.OTP;
            apiCall.execute(url, "");
        } catch (Exception e) {
            e.toString();
        }

    }

    @Override
    public void onPicModeSelected(String mode) {
        String action = mode.equalsIgnoreCase(Constants.PicModes.CAMERA) ? Constants.IntentExtras.ACTION_CAMERA : Constants.IntentExtras.ACTION_GALLERY;
        actionProfilePic(action);
    }

    public static final int REQUEST_CODE_UPDATE_PIC = 0x1;

    private void actionProfilePic(String action) {
        Intent intent = new Intent(getActivity(), ImageCropActivity.class);
        intent.putExtra("ACTION", action);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_PIC);
    }

    private void showCroppedImage(String mImagePath) {
        if (mImagePath != null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(mImagePath);
            SaveCapturedImagePath(mImagePath);
            img.setImageBitmap(myBitmap);
            layImg.setVisibility(View.VISIBLE);
            pic.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_UPDATE_PIC) {
                String imagePath = result.getStringExtra(Constants.IntentExtras.IMAGE_PATH);
                showCroppedImage(imagePath);
            }
        } else {
            if (requestCode == REQUEST_CODE_UPDATE_PIC) {
                String errorMsg = result.getStringExtra(ImageCropActivity.ERROR_MSG);
                // Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showAddProfilePicDialog() {
        PicModeSelectDialogFragment dialogFragment = new PicModeSelectDialogFragment();
        dialogFragment.setiPicModeSelectListener(this);
        dialogFragment.show(getFragmentManager(), "picModeSelector");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (++check > 1) {
            countrycodestr = countrycode[position];
            Log.v("tag", countrycode[position]);
            //  cCode.setText(countrycodestr);
            if (position == 0) {
                cCode.setImageDrawable(getResources().getDrawable(R.drawable.au));
                cCode.setTag("au");
            } else {
                cCode.setImageDrawable(getResources().getDrawable(R.drawable.in));
                cCode.setTag("in");
            }

            if (!userInfo.getMobile().contentEquals(countrycodestr + phone.getText().toString().replaceAll("\\s+", ""))) {
                mGenerateOtp.setVisibility(View.VISIBLE);
                mOtptxt.setVisibility(View.VISIBLE);
                otpEdt.setVisibility(View.VISIBLE);
            } else {
                mGenerateOtp.setVisibility(View.GONE);
                mOtptxt.setVisibility(View.GONE);
                otpEdt.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void showMessage(String title, String message) {
        AlertManager.messageDialog(getActivity(), title, message);
    }

    public void showSaveMessage(String message) {
        if (IsSave) {
            getActivity().startActivity(new Intent(getActivity(), HomeActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            getActivity().finish();
        } else {
            String backStateName = this.getClass().getName();
            Fragment fragment;
           /* if (userInfo.getIsprofileCompleted().equals("0"))
                fragment = new VehicleOwnerProfile();
            else*/
            fragment = new ProfileStepTwo();
            getActivity().getFragmentManager().beginTransaction().add(R.id.container, fragment, backStateName)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
     /*   AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Success!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();


            }
        });
        Dialog d = builder.create();
        d.show();*/
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
                    validateUser();
                    // profileCompany();
                } else if (outJson.getInt(Config.STATUS) == APIStatus.UNPROCESSABLE) {
                    showMessage(outJson.getJSONArray(Config.DATA).getJSONObject(0).getString(Config.MESSAGE));
                }
            } else if (type == 0) {
                JSONObject outJson = new JSONObject(result);
                Log.d(Config.TAG, outJson.toString());
                if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                    //profileCompany();
                    saveProfileData();
                } else {
                    showMessage(outJson.getJSONArray(Config.DATA).getJSONObject(0).getString(Config.MESSAGE));
                }

            } else if (type == 6) {
                JSONObject outJson = new JSONObject(result);
                Log.d(Config.TAG, outJson.toString());
                if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                    JSONObject dataObj = outJson.getJSONObject(Config.DATA);
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
                    String userpaymentmode = dataObj.getString(Keys.USER_PAYMENT_MODES);
                    session.saveUserDate(UserInfo.getString(Keys.KEY_ID),
                            UserInfo.getString(Config.USER_NAME),
                            UserInfo.getString(Keys.KEY_EMAIL),
                            dataObj.getString(Keys.KEY_IMAGE),
                            UserInfo.getString(Config.TOKEN),
                            UserInfo.toString(), driverProfile.toString(),
                            dataObj.getString(Keys.DRIVER_LIMAGE),
                            company.toString(), vehicle.toString(), sRating, dRating, dataObj.optString(Keys.CREDIT_CARD), dataObj.optString(Keys.BANK_DETAIL), AdminApprovalStatus, EmailVerified, userpaymentmode, false);
                    File fileProfile = new File(session.getProfileImage());
                    Utils.deleteDirectory(fileProfile);
                    SharedPreferences myPrefs = getActivity().getSharedPreferences(Config.PREF_NAME, 0);
                    SharedPreferences.Editor prefsEditor = myPrefs.edit();
                    prefsEditor.putString("profile_photo", "");
                    prefsEditor.commit();
                    String message = outJson.optString(Config.MESSAGE);
                    if (message != null && !message.contentEquals(""))
                        showSaveMessage(message);
                    else
                        showSaveMessage("Your Profile has been updated successfully.");
                } else if (outJson.getInt(Config.STATUS) == APIStatus.UNPROCESSABLE) {
                    try {
                        boolean Ismessage = false;
                        if (!Ismessage) {
                            String message = outJson.optString(Config.MESSAGE);
                            if (message != null && !message.contentEquals("")) {
                                AlertManager.messageDialog(getActivity(), "Alert!", message);
                                Ismessage = true;
                            } else
                                Ismessage = false;
                        }
                        if (!Ismessage) {
                            if (outJson.optJSONObject("data").has("user")) {
                                try {
                                    JSONArray jsonArray = outJson.optJSONObject("data").optJSONArray("user");
                                    JSONObject jsonObj = jsonArray.optJSONObject(0);
                                    if (jsonObj.has(Config.MESSAGE)) {
                                        AlertManager.messageDialog(getActivity(), "Alert!", jsonObj.optString(Config.MESSAGE));
                                        Ismessage = true;
                                    } else
                                        Ismessage = false;

                                } catch (Exception e) {
                                    e.toString();
                                }
                            }
                        }
                        if (!Ismessage) {
                            if (outJson.optJSONObject("data").has("vehicle")) {
                                try {
                                    JSONArray jsonArray = outJson.optJSONObject("data").optJSONArray("vehicle");
                                    JSONObject jsonObj = jsonArray.optJSONObject(0);
                                    if (jsonObj.has(Config.MESSAGE)) {
                                        AlertManager.messageDialog(getActivity(), "Alert!", jsonObj.optString(Config.MESSAGE));
                                        Ismessage = true;
                                    } else
                                        Ismessage = false;

                                } catch (Exception e) {
                                    e.toString();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.toString();
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
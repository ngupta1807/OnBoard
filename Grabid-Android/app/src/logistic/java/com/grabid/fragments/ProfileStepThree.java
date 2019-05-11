package com.grabid.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.imageCrop.Constants;
import com.grabid.imageCrop.ImageCropActivity;
import com.grabid.imageCrop.PicModeSelectDialogFragment;
import com.grabid.models.Category;
import com.grabid.models.DriverInfo;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by vinod on 10/25/2016.
 */
public class ProfileStepThree extends Fragment implements AsyncTaskCompleteListener,
        View.OnClickListener, PicModeSelectDialogFragment.IPicModeSelectListener {
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_CHOOSE_PHOTO = 2;
    TextView licenceType, expiry, submit, btn_licence_image, mSaveExit, btn_medicare_img, btn_nationalimg,saveexit;
    EditText licenceNumber, mMedicareNumber;
    DriverInfo driverInfo;
    SessionManager session;
    int type;
    RelativeLayout licence_layout, mMedLayout, mNatlayout;
    int CAMERA_PERMISSION_CODE = 115;
    int STORAGE_PERMISSION_CODE = 116;
    ScrollView scroll;
    ImageView img, mMedicareCardImage, mNationalImage;
    String profile_pic = "";
    String licence_pic = "";
    String medicarePic = "";
    String nationalPic = "";
    String file_name;
    File sdIconStorageDir;
    ImageView edit_img, mededit_img, natedit_img;
    boolean IsEdit = false;
    String driverPath = "";
    String medicareImagePath = "";
    String nationalImagePath = "";
    String datetime = "";
    int photo_type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
        //  HomeActivity.edit.setVisibility(View.GONE);
        //   HomeActivity.edit.setVisibility(View.VISIBLE);
        //  HomeActivity.edit.setBackgroundResource(R.drawable.pencil_icon);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        View view = inflater.inflate(R.layout.driver_info, null);
        init(view);
        type = 1;
        getLicenceTypes();

        return view;
    }

    public void Editable(boolean Edit) {
        if (Edit) {
            HomeActivity.edit.setVisibility(View.VISIBLE);
            if (!ProfileStepOne.Iseditable) {
                HomeActivity.edit.setVisibility(View.GONE);
            } else {
                HomeActivity.edit.setBackgroundResource(R.drawable.edit_top_grey);
                HomeActivity.edit.setVisibility(View.VISIBLE);
                IsEdit = false;
            }
            licenceType.setFocusable(true);
            //   licenceType.setFocusableInTouchMode(true);
            //licenceType.setClickable(true);
            licenceType.setEnabled(true);
            expiry.setFocusable(true);
            expiry.setFocusableInTouchMode(true);
            expiry.setClickable(true);
            licenceNumber.setFocusable(true);
            licenceNumber.setFocusableInTouchMode(true);
            licenceNumber.setClickable(true);
            mMedicareNumber.setFocusable(true);
            mMedicareNumber.setFocusableInTouchMode(true);
            mMedicareNumber.setClickable(true);
            edit_img.setFocusable(true);
            edit_img.setFocusableInTouchMode(true);
            edit_img.setClickable(true);
            mededit_img.setFocusable(true);
            mededit_img.setFocusableInTouchMode(true);
            mededit_img.setClickable(true);
            natedit_img.setFocusable(true);
            natedit_img.setFocusableInTouchMode(true);
            natedit_img.setClickable(true);
            btn_licence_image.setClickable(true);
            btn_medicare_img.setClickable(true);
            btn_nationalimg.setClickable(true);

        } else {
            if (!ProfileStepOne.Iseditable) {
                HomeActivity.edit.setVisibility(View.GONE);
            } else {
                HomeActivity.edit.setBackgroundResource(R.drawable.edit_top);
                HomeActivity.edit.setVisibility(View.VISIBLE);
                IsEdit = true;
            }
            licenceType.setFocusable(false);
            //  licenceType.setFocusableInTouchMode(false);
            //licenceType.setClickable(false);
            licenceType.setEnabled(false);
            expiry.setFocusable(false);
            expiry.setFocusableInTouchMode(false);
            expiry.setClickable(false);
            licenceNumber.setFocusable(false);
            licenceNumber.setFocusableInTouchMode(false);
            licenceNumber.setClickable(false);
            mMedicareNumber.setFocusable(false);
            mMedicareNumber.setFocusableInTouchMode(false);
            mMedicareNumber.setClickable(false);
            edit_img.setFocusable(false);
            edit_img.setFocusableInTouchMode(false);
            edit_img.setClickable(false);
            mededit_img.setFocusable(false);
            mededit_img.setFocusableInTouchMode(false);
            mededit_img.setClickable(false);
            natedit_img.setFocusable(false);
            natedit_img.setFocusableInTouchMode(false);
            natedit_img.setClickable(false);
            btn_licence_image.setClickable(false);
            btn_medicare_img.setClickable(false);
            btn_nationalimg.setClickable(false);

        }
    }

    public void UpdateDesign() {
        Log.v("updatedesign", "updatedesign");
        HomeActivity.title.setText("Carrier's Details");
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
        HomeActivity.filter.setVisibility(View.GONE);
        if (session.getUserDetails().getIsprofileCompleted().equals("0"))
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        if (!ProfileStepOne.Iseditable)
            IsEdit = true;
        else
            IsEdit = false;
        if (session.getUserDetails().getAdminApprovalStatus().equals("0") && session.getUserDetails().getVerifiedStatus().equals("0")){
            IsEdit = true;
        }
        Editable(IsEdit);
        HomeActivity.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable(IsEdit);
            }
        });
    }

    private String getTypesData() {
        String selectedTypes = "";
        for (int i = 0; i < licenceTypeData.size(); i++) {
            Category cat = licenceTypeData.get(i);
            if (idBuilder.contains(cat.getId())) {
                selectedTypes += cat.getName() + ", ";
                cat.setSelected(true);
            } else {
                cat.setSelected(false);
            }
            licenceTypeData.set(i, cat);
        }
        return selectedTypes.substring(0, selectedTypes.length() - 2);
    }

    private void appendData() {
        driverInfo = session.getDriverInfo();
        idBuilder = driverInfo.getLicenceTypeId();
        try {
            Log.v("licenceTypeId", "licenceTypeId::" + idBuilder);
            licenceType.setText(getTypesData());
        }catch (Exception ex){

        }

        if(!driverInfo.getLicenceValidTill().equals("null"))
            expiry.setText(driverInfo.getLicenceValidTill());
        if(!driverInfo.getLicenceNumber().equals("null"))
            licenceNumber.setText(driverInfo.getLicenceNumber());
        if(!driverInfo.getMedicareNumber().equals("null"))
            mMedicareNumber.setText(driverInfo.getMedicareNumber());
        driverPath = session.getLicenceImage();

        try {
            if (driverInfo.getLicenceImage() != null && !driverInfo.getLicenceImage().equals("") && !driverInfo.getLicenceImage().contentEquals("null")) {
                licence_layout.setVisibility(View.VISIBLE);
                btn_licence_image.setVisibility(View.GONE);
                //new ImageLoader(getActivity()).DisplayImage(driverInfo.getLicenceImage(), img);
                Picasso.with(getActivity()).load(driverInfo.getLicenceImage()).into(img);
            } else {
                btn_licence_image.setVisibility(View.VISIBLE);
                licence_layout.setVisibility(View.GONE);
            /*File extStore = Environment.getExternalStorageDirectory();
            File picFile = new File(extStore.getAbsolutePath() + "/GRABiD/licence_pic.png");
            if (picFile.exists()) {
                licence_layout.setVisibility(View.VISIBLE);
                btn_licence_image.setVisibility(View.GONE);
                Bitmap bitmap = BitmapFactory.decodeFile(picFile.getAbsolutePath());
                img.setImageBitmap(bitmap);

            } else {
                btn_licence_image.setVisibility(View.VISIBLE);
                licence_layout.setVisibility(View.GONE);
            }*/
            }
            if (driverInfo.getMedicareImage() != null && !driverInfo.getMedicareImage().equals("") && !driverInfo.getMedicareImage().contentEquals("null")) {
                mMedLayout.setVisibility(View.VISIBLE);
                btn_medicare_img.setVisibility(View.GONE);
                Picasso.with(getActivity()).load(driverInfo.getMedicareImage()).into(mMedicareCardImage);
            } else {
                btn_medicare_img.setVisibility(View.VISIBLE);
                mMedLayout.setVisibility(View.GONE);
            }
            if (driverInfo.getNationalImage() != null && !driverInfo.getNationalImage().equals("") && !driverInfo.getNationalImage().contentEquals("null")) {
                mNatlayout.setVisibility(View.VISIBLE);
                btn_nationalimg.setVisibility(View.GONE);
                Picasso.with(getActivity()).load(driverInfo.getNationalImage()).into(mNationalImage);
            } else {
                btn_nationalimg.setVisibility(View.VISIBLE);
                mNatlayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.toString();
        }
    }

    private String getLicenceType(String id) {
        for (Category info : licenceTypeData) {
            if (info.getId().equalsIgnoreCase(id)) {
                return info.getName();
            }
        }
        return "";
    }

    private void init(View view) {
        session = new SessionManager(getActivity());
        licenceType = (TextView) view.findViewById(R.id.licence_type);
        licenceType.setOnClickListener(this);
        expiry = (TextView) view.findViewById(R.id.licence_valid_till);
        expiry.setOnClickListener(this);
        licenceNumber = (EditText) view.findViewById(R.id.licence_no);
        submit = (TextView) view.findViewById(R.id.submit);
        submit.setOnClickListener(this);
        //  mSaveExit = (TextView) view.findViewById(R.id.saveexit);
        // mSaveExit.setOnClickListener(this);
        btn_licence_image = (TextView) view.findViewById(R.id.btn_licence_image);
        btn_licence_image.setOnClickListener(this);
        btn_medicare_img = (TextView) view.findViewById(R.id.btn_medicare_image);
        btn_medicare_img.setOnClickListener(this);
        btn_nationalimg = (TextView) view.findViewById(R.id.btn_nationalpolice_image);
        btn_nationalimg.setOnClickListener(this);
        licence_layout = (RelativeLayout) view.findViewById(R.id.licence_layout);
        mMedLayout = view.findViewById(R.id.medicare_layout);
        mNatlayout = view.findViewById(R.id.national_layout);
        licenceNumber = (EditText) view.findViewById(R.id.licence_no);
        mMedicareNumber = view.findViewById(R.id.medicarecard_no);
        scroll = (ScrollView) view.findViewById(R.id.scrollView);
        img = (ImageView) view.findViewById(R.id.img);
        mMedicareCardImage = view.findViewById(R.id.mimg);
        mNationalImage = view.findViewById(R.id.nimg);
        edit_img = (ImageView) view.findViewById(R.id.edit_img);
        saveexit = (TextView) view.findViewById(R.id.saveexit);
        edit_img.setOnClickListener(this);
        saveexit.setOnClickListener(this);
        mededit_img = view.findViewById(R.id.medit_img);
        mededit_img.setOnClickListener(this);
        natedit_img = view.findViewById(R.id.nedit_img);
        natedit_img.setOnClickListener(this);
    }

    private String encodeFileToBase64Binary(String path) throws IOException, FileNotFoundException {
        File file = new File(path);
        byte[] bytes = loadFile(file);
        byte[] encoded = org.apache.commons.codec.binary.Base64.encodeBase64(bytes);
        String encodedString = new String(encoded);
        return encodedString;
    }

    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }

    public void saveDriverInfo(boolean value) {
        if (isValid(value)) {
            type = 2;
            HashMap<String, String> params = new HashMap<>();
            params.put("licence_type_id", idBuilder);
            if( expiry.getText().toString().startsWith("Select")){
                params.put("licence_valid_till", "");
            }else {
                params.put("licence_valid_till", expiry.getText().toString());
            }
            params.put("licence_number", licenceNumber.getText().toString());
            params.put("medicare_card_number", mMedicareNumber.getText().toString());

            try {
                if (!driverPath.equals("") && !driverPath.contains("http"))
                    licence_pic = encodeFileToBase64Binary(driverPath);
                if (!medicareImagePath.equals("") && !medicareImagePath.contains("http"))
                    medicarePic = encodeFileToBase64Binary(medicareImagePath);
                if (!nationalImagePath.equals("") && !nationalImagePath.contains("http"))
                    nationalPic = encodeFileToBase64Binary(nationalImagePath);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!licence_pic.equals(""))
                params.put("licence_image", licence_pic);
            if (!medicarePic.equals(""))
                params.put("medicare_card_image", medicarePic);
            if (!nationalPic.equals(""))
                params.put("police_check_image", nationalPic);
            String url = Config.SERVER_URL + Config.DRIVER_DATA;
            //  Log.d("profile pic:", "profile pic:" + profile_pic);
            //  Log.d("licence pic:", "licence pic:" + licence_pic);
            if (Internet.hasInternet(getActivity())) {
                RestAPICall apiCall = new RestAPICall(getActivity(), HTTPMethods.PUT, this, params);
                apiCall.execute(url, session.getToken());
            } else {
                showMessage(getResources().getString(R.string.no_internet));
            }
        }

    }

    private boolean isValid(Boolean check) {
        if(check==true) {

        }else {
            if (idBuilder.equals("") && licenceNumber.getText().toString().length() == 0
                    && expiry.getText().toString().equalsIgnoreCase("Select Licence Valid till")) {
                showMessage(getActivity().getResources().getString(R.string.completeallfield));
                return false;
            }
            if (idBuilder.equals("")) {
                showMessage(getActivity().getResources().getString(R.string.selectlicensetype));
                return false;
            } else if (licenceNumber.getText().toString().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.enterlicensenumber));
                return false;
            } else if (expiry.getText().toString().equalsIgnoreCase("Select Licence Valid till")) {
                showMessage(getActivity().getResources().getString(R.string.chooselicenseexpirydate));
                return false;
            }
            SimpleDateFormat pusdf = new SimpleDateFormat("yyyy-MM-dd hh:mm aa", Locale.US);
            try {
                Date license = pusdf.parse(datetime);
                if (license.before(new Date())) {
                    showMessage(getActivity().getResources().getString(R.string.expirydategreaterthencurrentdate));
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (licence_layout.getVisibility() == View.GONE) {
                showMessage(getActivity().getResources().getString(R.string.chooselicensecarrierimage));
                return false;
            }
            if (mMedicareNumber.getText().toString().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.choosemedicarecardnumber));
                return false;
            }
            if (mMedLayout.getVisibility() == View.GONE) {
                showMessage(getActivity().getResources().getString(R.string.choosemedicarecardimage));
                return false;
            }
        }
        return true;
    }

    private void showMessage(String message) {
        AlertManager.messageDialog(getActivity(), "Alert!", message);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outterJson = new JSONObject(result);
            if (Integer.parseInt(outterJson.getString(Config.STATUS)) == APIStatus.SUCCESS) {
                if (type == 1) {
                    JSONArray outterArray = outterJson.getJSONArray(Config.DATA);
                    for (int i = 0; i < outterArray.length(); i++) {
                        JSONObject innerJson = outterArray.getJSONObject(i);
                        Category cat = new Category();
                        cat.setName(innerJson.getString(Keys.KEY_NAME));
                        cat.setId(innerJson.get(Keys.KEY_ID).toString());
                        cat.setSelected(false);
                        licenceTypeData.add(cat);
                    }
                    //if (!session.getUserDetails().getIsprofileCompleted().equals("0"))
                        appendData();
                } else if (type == 2) {
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

                    session.saveUserDate(UserInfo.getString(Keys.KEY_ID),
                            UserInfo.getString(Config.USER_NAME),
                            UserInfo.getString(Keys.KEY_EMAIL),
                            dataObj.getString(Keys.KEY_IMAGE),
                            UserInfo.getString(Config.TOKEN),
                            UserInfo.toString(), driverProfile.toString(),
                            dataObj.getString(Keys.DRIVER_LIMAGE),
                            company.toString(), vehicle.toString(), sRating, dRating, dataObj.optString(Keys.CREDIT_CARD), dataObj.optString(Keys.BANK_DETAIL), AdminApprovalStatus, EmailVerified, false);
                    File fileProfile = new File(session.getProfileImage());
                    File fileLicence = new File(session.getLicenceImage());
                    deleteDirectory(fileProfile);
                    deleteDirectory(fileLicence);
                    SharedPreferences myPrefs = getActivity().getSharedPreferences(Config.PREF_NAME, 0);
                    SharedPreferences.Editor prefsEditor = myPrefs.edit();
                    prefsEditor.putString("profile_photo", "");
                    prefsEditor.putString("licence_photo", "");
                    prefsEditor.commit();
                    String message = outterJson.optString(Config.MESSAGE);
                    if (message != null && !message.contentEquals("")) {
                        if(isSave.equals("true")){
                            getActivity().startActivity(new Intent(getActivity(), HomeActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            getActivity().finish();
                        }else {
                            showSuccessMessage(message);
                        }
                    }
                    else {
                        if(isSave.equals("true")){
                            getActivity().startActivity(new Intent(getActivity(), HomeActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            getActivity().finish();
                        }else {
                            showSuccessMessage("Your Profile has been updated successfully.");
                        }
                    }

                }
            } else if (Integer.parseInt(outterJson.getString(Config.STATUS)) == APIStatus.UNPROCESSABLE) {
                //JSONArray userInfo = outterJson.get
                try {
                    boolean Ismessage = false;
                    if (!Ismessage) {
                        String message = outterJson.optString(Config.MESSAGE);
                        if (message != null && !message.contentEquals("")) {
                            AlertManager.messageDialog(getActivity(), "Alert!", message);
                            Ismessage = true;
                        } else
                            Ismessage = false;
                    }
                    if (!Ismessage) {
                        if (outterJson.optJSONObject("data").has("user")) {
                            try {
                                JSONArray jsonArray = outterJson.optJSONObject("data").optJSONArray("user");
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
                        if (outterJson.optJSONObject("data").has("driver")) {
                            try {
                                JSONArray jsonArray = outterJson.optJSONObject("data").optJSONArray("driver");
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
                        if (outterJson.optJSONObject("data").has("company")) {
                            try {
                                JSONArray jsonArray = outterJson.optJSONObject("data").optJSONArray("company");
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
                        if (outterJson.optJSONObject("data").has("vehicle")) {
                            try {
                                JSONArray jsonArray = outterJson.optJSONObject("data").optJSONArray("vehicle");
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

              /*  try {
                    String message = null;
                    //  JSONArray jsonArray = outterJson.optJSONArray(Config.DATA);
                    JSONObject jobj = outterJson.optJSONObject(Config.DATA);
                    JSONArray jsonArray = jobj.optJSONArray("vehicle");
                    if (jsonArray.length() > 0) {
                        JSONObject childjson = (JSONObject) jsonArray.get(0);
                        message = childjson.optString(Config.MESSAGE);
                        // message = outterJson.optJSONArray("data").optJSONObject(0).optString(Config.MESSAGE);
                    } else if (message == null) {
                        message = outterJson.optString("message");
                        Log.v("", message);
                    }
                    showMessage(message);
                } catch (Exception e) {
                    e.toString();
                }*/
                // AlertManager.messageDialog(getActivity(), "Alert!", outterJson.getJSONArray("data").getJSONObject(0).getString(Config.MESSAGE));
            } else {
                try {
                    String message = null;
                    JSONArray jsonArray = outterJson.optJSONArray(Config.DATA);
                    if (jsonArray.length() > 0) {
                        message = outterJson.optJSONArray("data").optJSONObject(0).optString(Config.MESSAGE);
                    } else if (message == null) {
                        message = outterJson.optString("message");
                        Log.v("", message);
                    }
                    showMessage(message);
                } catch (Exception e) {
                    e.toString();
                }
                //AlertManager.messageDialog(getActivity(), "Alert!", outterJson.getJSONArray("data").getJSONObject(0).getString(Config.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void goToHome() {
        startActivity(new Intent(getActivity(), HomeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    ArrayList<Category> licenceTypeData = new ArrayList<>();

    @Override
    public void onTaskComplete(String result) {
        Log.d(Config.TAG, result);
        handleResponse(result);
    }

    private void getLicenceTypes() {
        String url = Config.SERVER_URL + Config.LICENCE_TYPE;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, "");
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    public String[] getCountryList() {
        String[] listContent = new String[licenceTypeData.size()];
        for (int i = 0; i < licenceTypeData.size(); i++) {
            listContent[i] = licenceTypeData.get(i).getName();
        }
        return listContent;
    }

    public String getLicenceId(String countryName) {
        for (int i = 0; i < licenceTypeData.size(); i++) {
            if (licenceTypeData.get(i).getName().equalsIgnoreCase(countryName))
                return licenceTypeData.get(i).getId();
        }
        return "";
    }
    String isSave="";
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.licence_type) {
            //showGrabidDialog();
            showLicenceDialog();
        } else if (view.getId() == R.id.licence_valid_till) {
            showDatePicker();
        } else if (view.getId() == R.id.submit) {
            isSave="false";
            saveDriverInfo(false);
        }
        else if (view.getId() == R.id.saveexit) {
            isSave="true";
            saveDriverInfo(true);
        }  else if (view.getId() == R.id.btn_licence_image) {
            photo_type = 0;
            ImageCropActivity.TEMP_PHOTO_FILE_NAME = "licence_photo.jpg";
            showAddProfilePicDialog();
        } else if (view.getId() == R.id.btn_medicare_image) {
            photo_type = 1;
            ImageCropActivity.TEMP_PHOTO_FILE_NAME = "medicare_photo.jpg";
            showAddProfilePicDialog();
        } else if (view.getId() == R.id.btn_nationalpolice_image) {
            photo_type = 2;
            ImageCropActivity.TEMP_PHOTO_FILE_NAME = "national_photo.jpg";
            showAddProfilePicDialog();
        } else if (view.getId() == R.id.edit_img) {
            photo_type = 0;
            ImageCropActivity.TEMP_PHOTO_FILE_NAME = "licence_photo.jpg";
            showAddProfilePicDialog();
        } else if (view.getId() == R.id.medit_img) {
            photo_type = 1;
            ImageCropActivity.TEMP_PHOTO_FILE_NAME = "medicare_photo.jpg";
            showAddProfilePicDialog();
        } else if (view.getId() == R.id.nedit_img) {
            photo_type = 2;
            ImageCropActivity.TEMP_PHOTO_FILE_NAME = "national_photo.jpg";
            showAddProfilePicDialog();
        }


    }

    public void SaveCapturedImagePath(String path) {
     /*   SharedPreferences myPrefs = getActivity().getSharedPreferences(Config.PREF_NAME, 0);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putString("licence_photo", path);
        prefsEditor.commit();*/
        if (photo_type == 0)
            driverPath = path;
        else if (photo_type == 1)
            medicareImagePath = path;
        else
            nationalImagePath = path;
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date

        // Create the DatePickerDialog instance
        DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
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

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker datePicker, int selectedYear,
                              int selectedMonth, int selectedDay) {
            expiry.setText(formattedDate(selectedYear, selectedMonth + 1, selectedDay));
            Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                    datePicker.getMonth(),
                    datePicker.getDayOfMonth());

            datetime = "";
            datetime = datetime + calendar.get(Calendar.YEAR) + "-" +
                    String.format("%02d", (calendar.get(Calendar.MONTH) + 1)) + "-" +
                    String.format("%02d", (calendar.get(Calendar.DAY_OF_MONTH)));

            datetime = datetime + " " + getFormattedTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        }
    };

    private String getFormattedTime(int hourOfDay, int mnts) {
        String format;
        if (hourOfDay == 0) {
            hourOfDay += 12;
            format = "AM";
        } else if (hourOfDay == 12) {
            format = "PM";
        } else if (hourOfDay > 12) {
            hourOfDay -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        String actual = String.format("%02d:%02d", hourOfDay, mnts);
        return actual + " " + format;
    }

    private String formattedDate(int year, int month, int day) {
        String date = String.format("%d-%02d-%02d", year, month, day);
        return date;
    }

    public void messageDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog d = builder.create();
        d.show();
    }

    private void showSuccessMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Success!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().startActivity(new Intent(getActivity(), HomeActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                getActivity().finish();
                /*Fragment fragment = new HomeMap();
                String backStateName = "com.grabid.activities.HomeActivity";
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();*/
            }
        });
        builder.show();
    }

    Dialog mDialog;

    public void showLicenceDialog() {
        mDialog = new Dialog(getActivity(), R.style.GrabidDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list_new);
        mDialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        ImageView close = (ImageView) mDialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (mDialog.isShowing())
                    mDialog.dismiss();*/
                done();
            }
        });

        title.setText("Select licence type");

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
        CategoryAdapter catAdapter = new CategoryAdapter(getActivity());
        dialog_ListView.setAdapter(catAdapter);
        mDialog.show();
    }

    JSONArray licenceArray = new JSONArray();
    String idBuilder = "";

    private class CategoryAdapter extends BaseAdapter {
        Context ctx;

        CategoryAdapter(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public int getCount() {
            return licenceTypeData.size();
        }

        @Override
        public Object getItem(int i) {
            return licenceTypeData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.simple_list_item_check, null);
            Category category = (Category) getItem(position);
            final TextView name = (TextView) convertView.findViewById(R.id.textItem);
            final CheckBox check = (CheckBox) convertView.findViewById(R.id.check);
            final TextView done = (TextView) convertView.findViewById(R.id.done);
            name.setTag(position);

            if (position == licenceTypeData.size() - 1) {
                done.setVisibility(View.VISIBLE);
            } else done.setVisibility(View.GONE);

            name.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (check.isChecked())
                        check.setChecked(false);
                    else
                        check.setChecked(true);
                    Category country = licenceTypeData.get((int) name.getTag());
                    country.setSelected(check.isChecked());
                    licenceTypeData.set((int) name.getTag(), country);
                    //notifyDataSetChanged();
                }
            });
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    Category country = licenceTypeData.get((int) name.getTag());
                    country.setSelected(isChecked);
                    licenceTypeData.set((int) name.getTag(), country);

                }
            });
            done.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    done();
                }
            });
            name.setText(category.getName());
            check.setChecked(category.isSelected());
            return convertView;
        }
    }

    public void done() {
        String builder = "";
        idBuilder = "";
        try {
            for (int i = 0; i < licenceTypeData.size(); i++) {
                if (licenceTypeData.get(i).isSelected()) {
                    builder += licenceTypeData.get(i).getName() + ", ";
                    idBuilder += licenceTypeData.get(i).getId() + ",";
                    JSONObject object = new JSONObject();
                    object.put("name", licenceTypeData.get(i).getName());
                    object.put("id", licenceTypeData.get(i).getId());
                    licenceArray.put(object);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (builder.equals("")) {
            showMessage("Please select carrier licence type.");
        } else {
            licenceType.setText(builder.substring(0, builder.length() - 2));
            if (mDialog != null && mDialog.isShowing())
                mDialog.dismiss();
        }
        Log.e("selected IDs", idBuilder);
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
        intent.putExtra("rect", "");
        startActivityForResult(intent, REQUEST_CODE_UPDATE_PIC);
    }

    private void showCroppedImage(String mImagePath) {
        if (mImagePath != null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(mImagePath);
            SaveCapturedImagePath(mImagePath);
            if (photo_type == 0) {
                img.setImageBitmap(myBitmap);
                licence_layout.setVisibility(View.VISIBLE);
                btn_licence_image.setVisibility(View.GONE);
            } else if (photo_type == 1) {
                mMedicareCardImage.setImageBitmap(myBitmap);
                mMedLayout.setVisibility(View.VISIBLE);
                btn_medicare_img.setVisibility(View.GONE);
            } else if (photo_type == 2) {
                mNationalImage.setImageBitmap(myBitmap);
                mNatlayout.setVisibility(View.VISIBLE);
                btn_nationalimg.setVisibility(View.GONE);
            }
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
}

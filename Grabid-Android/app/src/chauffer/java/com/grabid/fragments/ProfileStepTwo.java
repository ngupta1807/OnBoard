package com.grabid.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.activities.VehicleListProfileCompany;
import com.grabid.adapters.MultipleImages;
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
import com.grabid.models.Category;
import com.grabid.models.CompanyInfo;
import com.grabid.models.UserInfo;
import com.grabid.models.VehicleInfo;
import com.grabid.util.FileOperation;
import com.grabid.util.PlaceDetailsJSONParser;
import com.grabid.util.PlaceJSONParser;
import com.grabid.views.BoldAutoCompleteTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by vinod on 10/14/2016.
 */
public class ProfileStepTwo extends Fragment implements View.OnClickListener,
        AsyncTaskCompleteListener, AdapterView.OnItemSelectedListener, PicModeSelectDialogFragment.IPicModeSelectListener {
    TextView country, state, submit, saveexit;
    //ImageView cCode;
    SessionManager session;
    CompanyInfo info;
    ArrayList<VehicleInfo> vehicleInfo;
    String countryID = "";
    String stateID = "";
    String vehicleTypeID = "";
    //  HashMap<Integer, String> vehicleTypeID = new HashMap<>();
    EditText companyName, abnNumber, suburb, officeNumber, totalVehicle, postalCode;
    // EditText contactPerson,phone;
    TextView addMoreVehicle, txt_vehicle_qty;
    LinearLayout layVehicles;
    ArrayList<HashMap<String, String>> vehicleTypeData = new ArrayList<>();
    int type;
    ArrayList<HashMap<String, String>> countryData = new ArrayList<>();
    ArrayList<HashMap<String, String>> stateData = new ArrayList<>();
    public static boolean IsEdit = false;
    boolean IsvehicleEditable = true;
    int totalvehiclein;
    //AutoComplete textvar
    BoldAutoCompleteTextView street;
    ParserTask placesParserTask;
    ParserTask placeDetailsParserTask;
    DownloadTask placesDownloadTask;
    DownloadTask placeDetailsDownloadTask;
    final int PLACES = 0;
    final int PLACES_DETAILS = 1;
    // MySpinner countrypicker;
    // String[] countrycode = {"+61", "+91"};
    // public static String countrycodestr = "";
    //int[] flags = {R.drawable.au, R.drawable.in};
    int check = 0;
    TextView pic;
    String LogoPath = "";
    ImageView img, editImg;
    RelativeLayout layImg;
    // LinearLayout mcClinear;
    RadioButton mRegisteryes, mRegisterNo;
    RadioGroup mGstRadio;
    String mGst = "";
    String regpaperImgPath = "", inscimagepath = "";
    List<List<String>> regPapers = new ArrayList<>();
    List<List<String>> insCertificate = new ArrayList<>();
    //   List<String> list = new ArrayList<>();
    MultipleImages imagesAdapter, mInscAdapter;
    int mImagetype;
    String encoded_image = null;
    JSONArray mTemp = new JSONArray();
    int img_pos;
    boolean IsStepTwo = false;
    UserInfo userInfo;
    boolean IsSave = false;
    String company_Logo = "";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        //  HomeActivity.edit.setVisibility(View.VISIBLE);
        //    HomeActivity.edit.setBackgroundResource(R.drawable.pencil_icon);
        container.clearDisappearingChildren();
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        Log.v("OnCreate", "OnCreate");
        View view = inflater.inflate(R.layout.profile_company, null);
        view.setOnClickListener(this);
        vehicleIndex = 0;
        vehicleJSONArray = new JSONArray();
        session = new SessionManager(getActivity());
        if (session.getUserDetails().getIslaststep().equals("1"))
            IsStepTwo = false;
        else
            IsStepTwo = true;
        init(view);
        //if (IsStepTwo)
            appendData();
        return view;
    }

    public void Editable(boolean Edit) {
        if (Edit) {
            HomeActivity.edit.setVisibility(View.VISIBLE);
            if (!ProfileStepOne.Iseditable) {
                HomeActivity.edit.setVisibility(View.GONE);
                HomeActivity.edit.setTag(R.drawable.edit_top_grey);
            } else {
                HomeActivity.edit.setBackgroundResource(R.drawable.edit_top_grey);
                HomeActivity.edit.setVisibility(View.VISIBLE);
                HomeActivity.edit.setTag(R.drawable.edit_top_grey);
                IsEdit = false;
            }
            companyName.setFocusable(true);
            companyName.setFocusableInTouchMode(true);
            companyName.setClickable(true);
            abnNumber.setFocusable(true);
            abnNumber.setFocusableInTouchMode(true);
            abnNumber.setClickable(true);
            //  contactPerson.setFocusable(true);
            //   contactPerson.setFocusableInTouchMode(true);
            //  contactPerson.setClickable(true);
            street.setFocusable(true);
            street.setFocusableInTouchMode(true);
            street.setClickable(true);
            suburb.setFocusable(true);
            suburb.setFocusableInTouchMode(true);
            suburb.setClickable(true);
            //  phone.setFocusable(true);
            //  phone.setFocusableInTouchMode(true);
            //    phone.setClickable(true);
            officeNumber.setFocusable(true);
            officeNumber.setFocusableInTouchMode(true);
            officeNumber.setClickable(true);
            // cCode.setFocusableInTouchMode(true);
            // cCode.setClickable(true);
            // mcClinear.setFocusableInTouchMode(true);
            //  mcClinear.setClickable(true);
            //  countrypicker.setFocusableInTouchMode(true);
            // countrypicker.setClickable(true);
            //   addMoreVehicle.setFocusable(true);
            //  addMoreVehicle.setFocusableInTouchMode(true);
            // addMoreVehicle.setClickable(true);
            // addMoreVehicle.setVisibility(View.VISIBLE);
            totalVehicle.setFocusable(true);
            totalVehicle.setFocusableInTouchMode(true);
            totalVehicle.setClickable(true);
            country.setFocusable(true);
            country.setFocusableInTouchMode(true);
            country.setClickable(true);
            state.setFocusable(true);
            state.setFocusableInTouchMode(true);
            state.setClickable(true);
            editImg.setFocusable(true);
            editImg.setFocusableInTouchMode(true);
            editImg.setClickable(true);
            pic.setClickable(true);
            mRegisteryes.setEnabled(true);
            mRegisterNo.setEnabled(true);
            postalCode.setFocusable(true);
            postalCode.setFocusableInTouchMode(true);
            postalCode.setClickable(true);

            for (int i = 0; i < layVehicles.getChildCount(); i++) {
                try {
                    View viewone = layVehicles.getChildAt(i);
                    final TextView type = (TextView) viewone.findViewById(R.id.vehicle_type);
                    final EditText regdNo = (EditText) viewone.findViewById(R.id.vehicle_regd_no);
                    RadioButton myes = viewone.findViewById(R.id.m_yes);
                    RadioButton mNo = viewone.findViewById(R.id.m_no);
                    final RadioButton freightyes = viewone.findViewById(R.id.freight_yes);
                    final RadioButton freightNo = viewone.findViewById(R.id.freight_no);
                    final EditText price = viewone.findViewById(R.id.f_price);

                    type.setEnabled(true);
                    type.setClickable(true);
                    regdNo.setEnabled(true);
                    regdNo.setClickable(true);
                    myes.setEnabled(true);
                    mNo.setEnabled(true);
                    freightyes.setEnabled(true);
                    freightNo.setEnabled(true);
                    price.setEnabled(true);
                    price.setClickable(true);

                } catch (Exception e) {
                    e.toString();
                }
            }


        } else {
            if (!ProfileStepOne.Iseditable) {
                HomeActivity.edit.setVisibility(View.GONE);
                HomeActivity.edit.setTag(R.drawable.edit_top_grey);
            } else {
                HomeActivity.edit.setBackgroundResource(R.drawable.edit_top);
                HomeActivity.edit.setVisibility(View.VISIBLE);
                HomeActivity.edit.setTag(R.drawable.edit_top);
                IsEdit = true;
            }
            companyName.setFocusable(false);
            companyName.setFocusableInTouchMode(false);
            companyName.setClickable(false);
            abnNumber.setFocusable(false);
            abnNumber.setFocusableInTouchMode(false);
            abnNumber.setClickable(false);
            //   contactPerson.setFocusable(false);
            //   contactPerson.setFocusableInTouchMode(false);
            //   contactPerson.setClickable(false);
            street.setFocusable(false);
            street.setFocusableInTouchMode(false);
            street.setClickable(false);
            suburb.setFocusable(false);
            suburb.setFocusableInTouchMode(false);
            suburb.setClickable(false);
            //   phone.setFocusable(false);
            //  phone.setFocusableInTouchMode(false);
            //    phone.setClickable(false);
            officeNumber.setFocusable(false);
            officeNumber.setFocusableInTouchMode(false);
            officeNumber.setClickable(false);
            // addMoreVehicle.setVisibility(View.GONE);
            // addMoreVehicle.setFocusable(false);
            //   addMoreVehicle.setFocusableInTouchMode(false);
            //  addMoreVehicle.setClickable(false);
            totalVehicle.setFocusable(false);
            totalVehicle.setFocusableInTouchMode(false);
            totalVehicle.setClickable(false);
            country.setFocusable(false);
            country.setFocusableInTouchMode(false);
            country.setClickable(false);
            state.setFocusable(false);
            state.setFocusableInTouchMode(false);
            state.setClickable(false);
            //  cCode.setFocusableInTouchMode(false);
            //  cCode.setClickable(false);
            //   mcClinear.setFocusableInTouchMode(false);
            // mcClinear.setClickable(false);
            editImg.setFocusable(false);
            editImg.setFocusableInTouchMode(false);
            editImg.setClickable(false);
            pic.setClickable(false);
            mRegisteryes.setEnabled(false);
            mRegisterNo.setEnabled(false);
            postalCode.setFocusable(false);
            postalCode.setFocusableInTouchMode(false);
            postalCode.setClickable(false);
            //countrypicker.setFocusableInTouchMode(true);
            // countrypicker.setClickable(true);


            for (int i = 0; i < layVehicles.getChildCount(); i++) {
                try {
                    View viewone = layVehicles.getChildAt(i);
                    final TextView type = (TextView) viewone.findViewById(R.id.vehicle_type);
                    final EditText regdNo = (EditText) viewone.findViewById(R.id.vehicle_regd_no);
                    RadioButton myes = viewone.findViewById(R.id.m_yes);
                    RadioButton mNo = viewone.findViewById(R.id.m_no);
                    final RadioButton freightyes = viewone.findViewById(R.id.freight_yes);
                    final RadioButton freightNo = viewone.findViewById(R.id.freight_no);
                    final EditText price = viewone.findViewById(R.id.f_price);
                    RecyclerView recyclerView = viewone.findViewById(R.id.rvhl);
                    type.setEnabled(false);
                    type.setClickable(false);
                    regdNo.setEnabled(false);
                    regdNo.setClickable(false);
                    myes.setEnabled(false);
                    mNo.setEnabled(false);
                    freightyes.setEnabled(false);
                    freightNo.setEnabled(false);
                    price.setEnabled(false);
                    price.setClickable(false);
                    try {
                        int item_Count = recyclerView.getAdapter().getItemCount();
                        MultipleImages Adapter = (MultipleImages) recyclerView.getAdapter();
                        for (int j = 1; j <= item_Count; j++) {
                            if (j == item_Count) {
                                RecyclerView.ViewHolder viewholder = recyclerView.findViewHolderForAdapterPosition(j - 1);
                                viewholder.itemView.setEnabled(false);
                            }
                        }

                        Log.v("", "");
                    } catch (Exception e) {
                        e.toString();
                    }
                } catch (Exception e) {
                    e.toString();
                }
            }

        }

    }

    public void setEditTextMaxLength(final EditText editText, int length) {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(length);
            editText.setFilters(FilterArray);
        } catch (Exception e) {
            e.toString();
        }
    }

    int abnCount = 0;
    int officeCount = 0;
    int phoneCount = 0;

    private void init(View view) {
        session = new SessionManager(getActivity());
        info = session.getCompanyInfo();
        country = (TextView) view.findViewById(R.id.country);
        country.setOnClickListener(this);
        state = (TextView) view.findViewById(R.id.state);
        state.setOnClickListener(this);

        companyName = (EditText) view.findViewById(R.id.company_name);
        abnNumber = (EditText) view.findViewById(R.id.abn_number);
        setEditTextMaxLength(abnNumber, 14);
        //  contactPerson = (EditText) view.findViewById(R.id.company_contact_person);
        street = (BoldAutoCompleteTextView) view.findViewById(R.id.company_street);
        suburb = (EditText) view.findViewById(R.id.suburb);
        //   phone = (EditText) view.findViewById(R.id.phone);
        officeNumber = (EditText) view.findViewById(R.id.office_number);
        totalVehicle = (EditText) view.findViewById(R.id.vehicle_qty);
        pic = (TextView) view.findViewById(R.id.pic);
        pic.setOnClickListener(this);
        txt_vehicle_qty = (TextView) view.findViewById(R.id.txt_vehicle_qty);
        submit = (TextView) view.findViewById(R.id.next);
        submit.setOnClickListener(this);
        saveexit = (TextView) view.findViewById(R.id.saveexit);
        saveexit.setOnClickListener(this);
        addMoreVehicle = (TextView) view.findViewById(R.id.add_more_vehicle);
        addMoreVehicle.setOnClickListener(this);
        //  mcClinear = (LinearLayout) view.findViewById(R.id.cclinear);
        //mcClinear.setOnClickListener(this);
        //vehicleType.setOnClickListener(this);
        //vehicleRegdNo.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        layVehicles = (LinearLayout) view.findViewById(R.id.lay_vehicles);
        postalCode = (EditText) view.findViewById(R.id.postal_code);
        layImg = (RelativeLayout) view.findViewById(R.id.lay_img);
        img = (ImageView) view.findViewById(R.id.img);
        editImg = (ImageView) view.findViewById(R.id.edit_img);
        editImg.setOnClickListener(this);
        mRegisteryes = (RadioButton) view.findViewById(R.id.radioyes);
        mRegisterNo = (RadioButton) view.findViewById(R.id.radiono);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Raleway_SemiBold.ttf");
        mRegisteryes.setTypeface(typeface);
        mRegisterNo.setTypeface(typeface);
        mGstRadio = view.findViewById(R.id.radiogst);
        // addVehicle();
        // cCode = (ImageView) view.findViewById(R.id.cc);
        //   cCode.setOnClickListener(this);
        companyName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (companyName.getText().toString().length() > 0)
                    companyName.setText(makeFirstLetterCapitel(companyName.getText().toString()));
            }
        });

       /* contactPerson.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (contactPerson.getText().toString().length() > 0)
                    contactPerson.setText(makeFirstLetterCapitel(contactPerson.getText().toString()));
            }
        });*/
        street.setThreshold(1);
        street.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                placesDownloadTask = new DownloadTask(PLACES);
                String url = getAutoCompleteUrl(s.toString());
                placesDownloadTask.execute(url);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        street.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long id) {
                try {
                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(street.getWindowToken(), 0);
                } catch (Exception e) {
                    e.toString();
                }
                try {
                    if (street.isPopupShowing()) {
                        street.setDropDownHeight(0);
                    }
                } catch (Exception e) {
                    e.toString();
                }
                ListView lv = (ListView) arg0;
                SimpleAdapter adapter = (SimpleAdapter) lv.getAdapter();
                HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);
                placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);
                //Log.d("ref", hm.get("reference"));
                String url = getPlaceDetailsUrl(hm.get("reference"));
                //Log.d("url", url);
                placeDetailsDownloadTask.execute(url);
            }
        });
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
                } catch (Exception e) {
                    e.toString();
                }
            }
        });*/
        abnNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (abnCount <= abnNumber.getText().toString().length()
                        && (abnNumber.getText().toString().length() == 2
                        || abnNumber.getText().toString().length() == 6
                        || abnNumber.getText().toString().length() == 10)) {
                    abnNumber.setText(abnNumber.getText().toString() + " ");
                    int pos = abnNumber.getText().length();
                    abnNumber.setSelection(pos);
                } else if (abnCount >= abnNumber.getText().toString().length()
                        && (abnNumber.getText().toString().length() == 2
                        || abnNumber.getText().toString().length() == 6
                        || abnNumber.getText().toString().length() == 10)) {
                    abnNumber.setText(abnNumber.getText().toString().substring(0, abnNumber.getText().toString().length() - 1));
                    int pos = abnNumber.getText().length();
                    abnNumber.setSelection(pos);
                }
                abnCount = abnNumber.getText().toString().length();
            }
        });
        officeNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (officeCount <= officeNumber.getText().toString().length()
                        && (officeNumber.getText().toString().length() == 2
                        || officeNumber.getText().toString().length() == 7
                        || officeNumber.getText().toString().length() == 12
                )) {
                    officeNumber.setText(officeNumber.getText().toString() + " ");
                    int pos = officeNumber.getText().length();
                    officeNumber.setSelection(pos);
                } else if (officeCount >= officeNumber.getText().toString().length()
                        && (officeNumber.getText().toString().length() == 2
                        || officeNumber.getText().toString().length() == 7
                        || officeNumber.getText().toString().length() == 12
                )) {
                    officeNumber.setText(officeNumber.getText().toString().substring(0, officeNumber.getText().toString().length() - 1));
                    int pos = officeNumber.getText().length();
                    officeNumber.setSelection(pos);
                }
                officeCount = officeNumber.getText().toString().length();

            }
        });
        totalVehicle.addTextChangedListener(new TextWatcher() {
            String beforetxt="";
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int len = s.length();
                    if (s.length() >= 1) {
                        if(totalVehicle.getText().toString().equals("0")) {
                            Log.v("value","value:"+s);
                        }
                        else {
                            if (!checkZero(totalVehicle.getText().toString().toString())) {
                                showMessage("please enter vehicle more than zero");
                            } else if (vehicleJSONArray != null && vehicleIndex >= Integer.parseInt(s.toString())) {
                                int val = Integer.parseInt(s.toString());
                                removeVehicleAlert(getResources().getString(R.string.removeVehicleALert), vehicleIndex + 1);
                            }
                        }
                    }else if(s.length()==0){
                        showMessage("please enter vehicle more than zero");
                        totalVehicle.setText("0");
                        totalVehicle.setSelection(totalVehicle.getText().length());
                    }
                } catch (Exception e) {
                    e.toString();
                }
            }

            public Boolean checkZero(String val) {
                try {
                    if (val.matches("[0]+"))
                        return false;

                } catch (Exception e) {
                    e.toString();
                }
                return true;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                beforetxt = s.toString();
                Log.v("", "");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = totalVehicle.getText().toString();
              /*  if (addMoreVehicle.getText().toString().equals("SAV")) {
                    showMessage("please click on save button first ");
                    //totalVehicle.setText(String.valueOf(vehicleJSONArray.length() + 1));
                } else*/

              /*  if (totalVehicle.getText().toString() != null && !totalVehicle.getText().toString().contentEquals("")) {
                    if (vehicleJSONArray.length() > Integer.parseInt(totalVehicle.getText().toString())) {
                        showMessage("You cannot write value less than added vehicles ");
                        totalVehicle.setText(String.valueOf(vehicleJSONArray.length()));
                    }

                }*/

            }
        });
        //countrypicker = (MySpinner) view.findViewById(R.id.countrycode);
        // countrypicker.setOnItemSelectedListener(ProfileStepTwo.this);
        // CountryCodeAdapter customAdapter = new CountryCodeAdapter(getActivity(), flags, countrycode);
        //  countrypicker.setAdapter(customAdapter);
    }

    public String makeFirstLetterCapitel(String input) {
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    public void showSaveMessage(String message) {
        if (IsSave) {
            getActivity().startActivity(new Intent(getActivity(), HomeActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            getActivity().finish();
        } else {
            Editable(false);
            String backStateName = this.getClass().getName();
            Fragment fragment = new ProfileStepThree();
            getActivity().getFragmentManager().beginTransaction().add(R.id.container, fragment, backStateName)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (type == 7) {
                if (Integer.parseInt(outJson.getString(Config.STATUS)) == APIStatus.UNPROCESSABLE) {
                    AlertManager.messageDialog(getActivity(), "Alert!", outJson.getJSONArray("data").getJSONObject(0).getString(Config.MESSAGE));

                }
            } else {
                if (type == 5) {
                    if (Integer.parseInt(outJson.getString(Config.STATUS)) == APIStatus.SUCCESS) {
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

                        String message = outJson.optString(Config.MESSAGE);
                        if (message != null && !message.contentEquals(""))
                            showSaveMessage(message);
                        else
                            showSaveMessage("Your Profile has been updated successfully.");
                    } else if (Integer.parseInt(outJson.getString(Config.STATUS)) == APIStatus.UNPROCESSABLE) {
                        //JSONArray userInfo = outterJson.get
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
                                if (outJson.optJSONObject("data").has("company")) {
                                    try {
                                        JSONArray jsonArray = outJson.optJSONObject("data").optJSONArray("company");
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

                } else {
                    if (Integer.parseInt(outJson.getString(Config.STATUS)) == APIStatus.SUCCESS) {
                        if (type == 4) {
                            validate();
                        } else {
                            if (type == 1)
                                countryData.clear();
                            else if (type == 2)
                                stateData.clear();
                            else if (type == 3) {
                                vehicleTypeData.clear();
                                suitableVehicleData.clear();
                            }

                            JSONArray outArray = outJson.getJSONArray("data");

                            for (int i = 0; i < outArray.length(); i++) {
                                JSONObject innerJson = outArray.getJSONObject(i);
                                HashMap<String, String> map = new HashMap<>();
                                map.put(Keys.KEY_ID, innerJson.get(Keys.KEY_ID).toString());
                                map.put(Keys.KEY_NAME, innerJson.getString(Keys.KEY_NAME));
                                if (type == 1)
                                    countryData.add(map);
                                else if (type == 2) {
                                    if (innerJson.getString(Keys.KEY_NAME).equals(state.getText().toString()))
                                        this.stateID = innerJson.get(Keys.KEY_ID).toString();
                                    stateData.add(map);
                                } else if (type == 3) {
                                    vehicleTypeData.add(map);
                                    if (type == 3) {
                                        Category cat = new Category();
                                        cat.setName(innerJson.getString(Keys.KEY_NAME));
                                        cat.setId(innerJson.get(Keys.KEY_ID).toString());
                                        cat.setSelected(false);
                                        suitableVehicleData.add(cat);
                                    }
                                }
                            }
                            Log.v("type", "type" + type);
                            if (type == 1) {
                                if (!info.equals("")) {
                                    doGetStates(info.getCountryID());
                                } else {
                                    getVehicleTypes();
                                }
                            } else if (type == 2) {
                                Log.v("type", "type" + type);
                                if (!info.equals("")) {
                                    getVehicleTypes();
                                }
                            } else if (info.equals("")) {
                                if (type == 3)
                                    showVehicleDialog(0, (Integer) viewType.getTag());
                            }
                        }
                    } else {
                        if (type == 4)
                            AlertManager.messageDialog(getActivity(), "Alert!", outJson.optString(Config.MESSAGE));
                        else
                            AlertManager.messageDialog(getActivity(), "Alert!", outJson.getJSONArray("data").getJSONObject(0).getString(Config.MESSAGE));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message) {
        AlertManager.messageDialog(getActivity(), "Alert!", message);
    }

    public void AddPhotos(int imagetype, int id) {
        this.mImagetype = imagetype;
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US).format(new Date());
        ImageCropActivity.TEMP_PHOTO_FILE_NAME = "vehregpap" + timeStamp + ".jpg";
        showAddProfilePicDialog();
        img_pos = id;

    }

    private void showAddProfilePicDialog() {
        PicModeSelectDialogFragment dialogFragment = new PicModeSelectDialogFragment();
        dialogFragment.setiPicModeSelectListener(this);
        dialogFragment.show(getFragmentManager(), "picModeSelector");
    }

    @Override
    public void onClick(View view) {
       /* if (view.getId() == R.id.cclinear) {
            countrypicker.performClick();
        }
        if (view.getId() == R.id.cc) {
            countrypicker.performClick();
        }*/
        if (view.getId() == R.id.pic) {
            mImagetype = 2;
            ImageCropActivity.TEMP_PHOTO_FILE_NAME = "logo_photo.jpg";
            showAddProfilePicDialog();
        }
        if (view.getId() == R.id.edit_img) {
            mImagetype = 2;
            ImageCropActivity.TEMP_PHOTO_FILE_NAME = "logo_photo.jpg";
            showAddProfilePicDialog();
        }
        if (view.getId() == R.id.country) {
            showGrabidDialog(1);
        }
        if (view.getId() == R.id.state) {
            showGrabidDialog(2);
        }
        if (view.getId() == R.id.saveexit) {
            showVehicleInsuranceDetail(true);
        }
        if (view.getId() == R.id.next) {
            showVehicleInsuranceDetail(false);
        } else if (view.getId() == R.id.add_more_vehicle) {
            if (totalVehicle.getText().toString().trim().length() == 0) {
                showMessage("Please enter total number of vehicles.");
                return;
            }
            //   if (addMoreVehicle.getText().toString().equals("ADD")) {
            String vehicleQty = totalVehicle.getText().toString();
            int noOfVehicle = Integer.parseInt(vehicleQty);
            Log.d("ind add", "" + vehicleIndex);
            if (vehicleIndex < (noOfVehicle - 1)) {
                // addMoreVehicle.setText("SAV");
                //addMoreVehicle.setBackgroundResource(R.drawable.add_btn_tick);
                ++vehicleIndex;
                addVehicle();
            } else
                showMessage("You cannot add more vehicles than the total number of vehicles entered.");
           /* } else if (addMoreVehicle.getText().toString().equals("SAV")) {
                Log.d("ind save", "" + vehicleIndex);
                TextView vType = (TextView) layVehicles.getChildAt(vehicleIndex).findViewById(R.id.vehicle_type);
                EditText vRegdNo = (EditText) layVehicles.getChildAt(vehicleIndex).findViewById(R.id.vehicle_regd_no);
                if (vRegdNo.getText().toString().equals(""))
                    showMessage("Please Enter vehicle registration number.");
                else if (vType.getText().toString().equals(""))
                    showMessage("Please select vehicle type.");
                else {
                    addMoreVehicle.setText("ADD");
                    addMoreVehicle.setBackgroundResource(R.drawable.add_btn);
                    appendVehicleToJson(vehicleTypeID, vRegdNo.getText().toString(), String.valueOf(vehicleIndex));
                    vehicleIndex = vehicleJSONArray.length() - 1;
                    IsvehicleEditable = true;
                }
            }*/
        }
    }

    public void showGrabidDialog(final int type) {
        final Dialog mDialog = new Dialog(getActivity(), R.style.GrabidDialog);
        //  mDialog.setCancelable(false);
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
                if (mDialog.isShowing())
                    mDialog.dismiss();
            }
        });


        if (type == 1)
            title.setText("Select Country");
        else if (type == 2)
            title.setText("Select State");
        else if (type == 3)
            title.setText("Select Vehicle Type");

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
        ArrayAdapter<String> adapter = null;

        if (type == 1)
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.dialog_textview, R.id.textItem, getCountryList());
        else if (type == 2)
            adapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.dialog_textview, R.id.textItem, getStateList());
        else if (type == 3)
            adapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.dialog_textview, R.id.textItem, getVehicleTypeList());

        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (type == 1) {
                    country.setText(parent.getItemAtPosition(position).toString());
                    state.setText("");
                    countryID = getCountryId(parent.getItemAtPosition(position).toString());
                    doGetStates(countryID);
                } else if (type == 2) {
                    state.setText(parent.getItemAtPosition(position).toString());
                    stateID = getStateId(parent.getItemAtPosition(position).toString());
                } else if (type == 3) {
                    //vehicleType.setText(parent.getItemAtPosition(position).toString());
                    vehicleTypeID = getVehicleTypeId(parent.getItemAtPosition(position).toString());
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public String[] getStateList() {
        String[] listContent = new String[stateData.size()];
        for (int i = 0; i < stateData.size(); i++) {
            listContent[i] = stateData.get(i).get(Keys.KEY_NAME);
        }
        return listContent;
    }

    private void doGetStates(String country_id) {
        type = 2;
        String url = Config.SERVER_URL + Config.STATE + "/" + country_id;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, "");
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    public String getCountryId(String countryName) {
        for (int i = 0; i < countryData.size(); i++) {
            if (countryData.get(i).get(Keys.KEY_NAME).equalsIgnoreCase(countryName))
                return countryData.get(i).get(Keys.KEY_ID);
        }
        return "";
    }

    public String getStateId(String stateName) {
        for (int i = 0; i < stateData.size(); i++) {
            if (stateData.get(i).get(Keys.KEY_NAME).equalsIgnoreCase(stateName))
                return stateData.get(i).get(Keys.KEY_ID);
        }
        return "";
    }

    public String[] getCountryList() {
        String[] listContent = new String[countryData.size()];
        for (int i = 0; i < countryData.size(); i++) {
            listContent[i] = countryData.get(i).get(Keys.KEY_NAME);
        }
        return listContent;
    }

    private void getVehicleTypes() {
        type = 3;
        String url = Config.SERVER_URL + Config.SUITABLE_VEHICLE;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    int vehicleIndex;
    TextView viewType;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.v("", "");
        }
    }

    public void UpdateDesign() {
        Log.v("updatedesign", "updatedesign");
        HomeActivity.title.setText(getActivity().getResources().getString(R.string.profile));
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
        HomeActivity.filter.setVisibility(View.GONE);
        if (!IsStepTwo)
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        if (!ProfileStepOne.Iseditable)
            IsEdit = true;
        else
            IsEdit = false;
        if (session.getUserDetails().getAdminApprovalStatus().equals("0") && session.getUserDetails().getVerifiedStatus().equals("0")){
            IsEdit = true;
        }
        Editable(IsEdit);
        //  IsvehicleEditable = false;
        HomeActivity.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable(IsEdit);
            }
        });
    }

    public void validateVehicleApi(String regdno, String isowner) {
        type = 7;
        HashMap<String, String> params = new HashMap<>();
        String url = Config.SERVER_URL + Config.VALIDATE_VEHICLE_API;
        params.put("registration_number", regdno);
        params.put("is_owner", isowner);
        Log.d("end", params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage(getActivity().getResources().getString(R.string.no_internet));
    }

    public void validateVehicle() {
        type = 4;
        HashMap<String, String> params = new HashMap<>();
        String url = Config.SERVER_URL + Config.VALIDATE_VEHICLE;
        params.put("registration_number", vehicleJSONArray.toString());
        Log.d("end", params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage(getActivity().getResources().getString(R.string.no_internet));
    }

    private void addVehicle() {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.vehicle, null);
        view.setTag("" + vehicleIndex);
        view.setId(vehicleIndex);
        final RadioButton myes = view.findViewById(R.id.m_yes);
        final RadioButton mNo = view.findViewById(R.id.m_no);
        final TextView type = (TextView) view.findViewById(R.id.vehicle_type);
        final TextView regpaperstxt = (TextView) view.findViewById(R.id.regpapers);
        final EditText regdNo = (EditText) view.findViewById(R.id.vehicle_regd_no);
        final RecyclerView imagesRecyclerView = view.findViewById(R.id.rvhl);
        final RecyclerView insRecyclerView = view.findViewById(R.id.rvhlincr);
        final RadioButton freightyes = view.findViewById(R.id.freight_yes);
        final RadioButton freightNo = view.findViewById(R.id.freight_no);
        final LinearLayout freightdl = (LinearLayout) view.findViewById(R.id.freightdollarlinear);


        regPapers.add(vehicleIndex, new ArrayList<String>());
        insCertificate.add(vehicleIndex, new ArrayList<String>());
        imagesAdapter = new MultipleImages(regPapers.get(vehicleIndex), getActivity(), this, 0, view.getId(), "rpaper");
        mInscAdapter = new MultipleImages(insCertificate.get(vehicleIndex), getActivity(), this, 1, view.getId(), "certificate");
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager horizontalLayoutManagerinsc = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        imagesRecyclerView.setLayoutManager(horizontalLayoutManager);
        imagesRecyclerView.setAdapter(imagesAdapter);
        insRecyclerView.setLayoutManager(horizontalLayoutManagerinsc);
        insRecyclerView.setAdapter(mInscAdapter);
        type.setTag(vehicleIndex);

        regdNo.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        imagesRecyclerView.setVisibility(View.GONE);
        regpaperstxt.setVisibility(View.GONE);
        freightdl.setVisibility(View.GONE);

        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vieww) {
                Log.v("Click Done", "Click Done");

                if (regdNo.hasFocus())
                    if (vehicleTypeData.size() > 0)
                        if (regdNo.getText().toString().length() > 0)
                            if (isUniqueNumber(regdNo.getText().toString(), view.getId()))
                                validateVehicleApi(regdNo.getText().toString(), myes.isChecked() ? "1" : "0");
                            else
                                AlertManager.messageDialog(getActivity(), "Alert!", "Please enter unique Vehicle Registration Number.");

                if (vehicleTypeData.size() > 0) {
                    viewType = type;
                    showVehicleDialog(0, (Integer) type.getTag());
                } else getVehicleTypes();
                // addMoreVehicle.setVisibility(View.VISIBLE);
            }
        });
        myes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    regpaperstxt.setVisibility(View.VISIBLE);
                    imagesRecyclerView.setVisibility(View.VISIBLE);
                    if (regdNo.getText().toString().length() > 0)
                        if (isUniqueNumber(regdNo.getText().toString(), view.getId()))
                            validateVehicleApi(regdNo.getText().toString(), "1");
                        else
                            AlertManager.messageDialog(getActivity(), "Alert!", "Please enter unique Vehicle Registration Number.");
                }
            }
        });
        mNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    regpaperstxt.setVisibility(View.GONE);
                    imagesRecyclerView.setVisibility(View.GONE);
                    if (regdNo.getText().toString().length() > 0)
                        if (isUniqueNumber(regdNo.getText().toString(), view.getId()))
                            validateVehicleApi(regdNo.getText().toString(), "0");
                        else
                            AlertManager.messageDialog(getActivity(), "Alert!", "Please enter unique Vehicle Registration Number.");
                }
            }
        });
        freightyes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    freightdl.setVisibility(View.VISIBLE);
                }
            }
        });
        freightNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    freightdl.setVisibility(View.GONE);
                }
            }
        });
        regdNo.setImeOptions(EditorInfo.IME_ACTION_DONE);
        regdNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == 12) {
                    if (regdNo.getText().toString().length() > 0)
                        if (isUniqueNumber(regdNo.getText().toString(), view.getId()))
                            validateVehicleApi(regdNo.getText().toString(), myes.isChecked() ? "1" : "0");
                        else
                            AlertManager.messageDialog(getActivity(), "Alert!", "Please enter unique Vehicle Registration Number.");

                }
                return false;
            }
        });

        layVehicles.addView(view);
    }


    //String value = "";
    String textChange = "";
    String idBuilder = "";

    private void appendData() {
        doGetCountries();
        info = session.getCompanyInfo();
        vehicleInfo = session.getVehicleInfo();
        countryID = info.getCountryID();
        stateID = info.getStateId();
        companyName.setText(info.getName());
        abnNumber.setText(info.getAbnNumber());
        if(!info.getAddress().equals("null"))
        street.setText(info.getAddress());
        if(!info.getSuburb().equals("null"))
        suburb.setText(info.getSuburb());
        //phone.setText(info.getCompanyMobile());
        try {
            if (info.getCompany_logo() != null && !info.getCompany_logo().contentEquals("") && !info.getCompany_logo().contentEquals("null")) {
                layImg.setVisibility(View.VISIBLE);
                pic.setVisibility(View.GONE);
                Picasso.with(getActivity()).load(info.getCompany_logo()).into(img);
            } else {
                layImg.setVisibility(View.GONE);
                pic.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.toString();
        }
        if (info.getPostalCode() != null && !info.getPostalCode().contentEquals("null")) {
            postalCode.setText(info.getPostalCode());
        }
        try {
            if (session.getUserDetails().getRegistergst() != null && !session.getUserDetails().getRegistergst().contentEquals("null"))
                if (session.getUserDetails().getRegistergst().contentEquals("1"))
                    mRegisteryes.setChecked(true);
                else if (session.getUserDetails().getRegistergst().contentEquals("0"))
                    mRegisterNo.setChecked(true);
        } catch (Exception e) {
            e.toString();
        }
        if(!info.getVehicleInfleet().equals("0")) {
            totalVehicle.setText(info.getVehicleInfleet());
        }
        userInfo = session.getUserDetails();
        if (!IsStepTwo) {
            try {
                if (Integer.parseInt(totalVehicle.getText().toString()) == 0) {
                    addMoreVehicle.setVisibility(View.VISIBLE);
                    totalVehicle.setVisibility(View.VISIBLE);
                    txt_vehicle_qty.setVisibility(View.VISIBLE);
                    addVehicle();
                }
            }catch(Exception ex){
                addMoreVehicle.setVisibility(View.VISIBLE);
                totalVehicle.setVisibility(View.VISIBLE);
                txt_vehicle_qty.setVisibility(View.VISIBLE);
                addVehicle();
            }
            doGetCountries();
        }


        totalvehiclein = Integer.parseInt(info.getVehicleInfleet());
        if(!info.getCountryName().equals("null"))
        country.setText(info.getCountryName());
        if(!info.getStateName().equals("null"))
        state.setText(info.getStateName());
        countryID = info.getCountryID();
        stateID = info.getStateId();
        try {
            if (info.getOfficeNumber().contentEquals("null"))
                officeNumber.setText("");
            else
                officeNumber.setText(info.getOfficeNumber());
        } catch (Exception ex) {
            Log.v("", "");
        }
        /*try {
            if (info.getOfficeNumber() != null && info.getOfficeNumber().length() > 4) {
                String officeNumberstr = info.getOfficeNumber();
                NumberFormatter addLineNumberFormatter = new NumberFormatter(new WeakReference<EditText>(officeNumber));
                officeNumber.addTextChangedListener(addLineNumberFormatter);
                officeNumber.setText(info.getOfficeNumber());
            } else
                officeNumber.setText(info.getOfficeNumber());
        } catch (Exception e) {
            e.toString();
        }*/
        addMoreVehicle.setVisibility(View.VISIBLE);
        totalVehicle.setVisibility(View.VISIBLE);
        txt_vehicle_qty.setVisibility(View.VISIBLE);
        //  value = userInfo.getHasMultipleVehicle();
       /* if (value.equals("2")) {
            addMoreVehicle.setVisibility(View.VISIBLE);
            totalVehicle.setVisibility(View.VISIBLE);
            txt_vehicle_qty.setVisibility(View.VISIBLE);
        } else {
            addMoreVehicle.setVisibility(View.GONE);
            totalVehicle.setVisibility(View.GONE);
            txt_vehicle_qty.setVisibility(View.GONE);
            totalVehicle.setText("1");
        }*/

        try {
            vehicleArray = new JSONArray(info.getVehiclesInfo());
            for (int i = 0; i < vehicleArray.length(); i++) {
                vehicleIndex = vehicleArray.length() - 1;
                JSONObject jobj = vehicleArray.getJSONObject(i);
                final View view = getActivity().getLayoutInflater().inflate(R.layout.vehicle, null);
                view.setTag("" + vehicleIndex);
                view.setId(i);
                final TextView type = (TextView) view.findViewById(R.id.vehicle_type);
                final EditText regdNo = (EditText) view.findViewById(R.id.vehicle_regd_no);
                TextView _id = (TextView) view.findViewById(R.id._id);
                _id.setText("" + i);
                _id.setId(i);
                type.setTag(i);
                final RadioButton myes = view.findViewById(R.id.m_yes);
                final RadioButton mNo = view.findViewById(R.id.m_no);
                final RadioButton freightyes = view.findViewById(R.id.freight_yes);
                final RadioButton freightNo = view.findViewById(R.id.freight_no);
                final LinearLayout freightdl = (LinearLayout) view.findViewById(R.id.freightdollarlinear);
                final TextView regpaperstxt = (TextView) view.findViewById(R.id.regpapers);
                final EditText price = view.findViewById(R.id.f_price);


                // idBuilder = jobj.getString("vehicle_type_string");
                //   type.setText(getTypesData());
                type.setText(jobj.getString("vehicle_type_string"));
                regdNo.setText(jobj.getString("registration_number"));
                // vehicleTypeID.put(i, jobj.getString("vehicle_type"));
                String Is_Owner = jobj.optString("is_owner");
                String Is_freight = jobj.optString("is_freight_insurance_cover");
                final RecyclerView imagesRecyclerView = view.findViewById(R.id.rvhl);
                RecyclerView insRecyclerView = view.findViewById(R.id.rvhlincr);
                ArrayList<String> vehiclereg = new ArrayList();
                if (Is_Owner != null && Is_Owner.contentEquals("1")) {
                    JSONArray jsonArray = jobj.optJSONArray("registration_papers_img");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        for (int q = 0; q < jsonArray.length(); q++) {
                            JSONObject childJson = jsonArray.getJSONObject(q);
                            vehiclereg.add(childJson.getString("image"));
                        }
                    }
                }
                regPapers.add(i, vehiclereg);
                ArrayList<String> inscert = new ArrayList<>();
                JSONArray jsonArrayin = jobj.optJSONArray("insurance_certificate_img");
                if (jsonArrayin != null && jsonArrayin.length() > 0) {
                    for (int q = 0; q < jsonArrayin.length(); q++) {
                        JSONObject childJson = jsonArrayin.getJSONObject(q);
                        inscert.add(childJson.getString("image"));
                    }
                }
                insCertificate.add(i, inscert);
                imagesAdapter = new MultipleImages(regPapers.get(i), getActivity(), this, 0, view.getId(), "rpaper");
                mInscAdapter = new MultipleImages(insCertificate.get(i), getActivity(), this, 1, view.getId(), "certificate");
                LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                LinearLayoutManager horizontalLayoutManagerinsc = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                imagesRecyclerView.setLayoutManager(horizontalLayoutManager);
                imagesRecyclerView.setAdapter(imagesAdapter);
                insRecyclerView.setLayoutManager(horizontalLayoutManagerinsc);
                insRecyclerView.setAdapter(mInscAdapter);

                if (Is_freight != null && Is_freight.contentEquals("1")) {
                    freightyes.setChecked(true);
                    freightdl.setVisibility(View.VISIBLE);
                    price.setText(jobj.optString("freight_insurance_cover_amount"));
                } else if (Is_freight != null && Is_freight.contentEquals("0")) {
                    freightNo.setChecked(true);
                    freightdl.setVisibility(View.GONE);
                } else {
                    freightdl.setVisibility(View.GONE);
                }
                if (Is_Owner != null && !Is_Owner.contentEquals("null") && !Is_Owner.contentEquals("")) {
                    if (Is_Owner.contentEquals("1")) {
                        imagesRecyclerView.setVisibility(View.VISIBLE);
                        regpaperstxt.setVisibility(View.VISIBLE);
                        myes.setChecked(true);
                    } else if (Is_Owner.contentEquals("0")) {
                        imagesRecyclerView.setVisibility(View.GONE);
                        regpaperstxt.setVisibility(View.GONE);
                        mNo.setChecked(true);
                    }
                }
                myes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            regpaperstxt.setVisibility(View.VISIBLE);
                            imagesRecyclerView.setVisibility(View.VISIBLE);
                            if (regdNo.getText().toString().length() > 0)
                                if (isUniqueNumber(regdNo.getText().toString(), view.getId()))
                                    validateVehicleApi(regdNo.getText().toString(), "1");
                                else
                                    AlertManager.messageDialog(getActivity(), "Alert!", "Please enter unique Vehicle Registration Number.");

                        }
                    }
                });
                mNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            regpaperstxt.setVisibility(View.GONE);
                            imagesRecyclerView.setVisibility(View.GONE);
                            if (regdNo.getText().toString().length() > 0)
                                if (isUniqueNumber(regdNo.getText().toString(), view.getId()))
                                    validateVehicleApi(regdNo.getText().toString(), "0");
                                else
                                    AlertManager.messageDialog(getActivity(), "Alert!", "Please enter unique Vehicle Registration Number.");

                        }
                    }
                });
                freightyes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            freightdl.setVisibility(View.VISIBLE);
                        }
                    }
                });
                freightNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            freightdl.setVisibility(View.GONE);
                        }
                    }
                });
                regdNo.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                regdNo.setImeOptions(EditorInfo.IME_ACTION_DONE);
                regdNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == 12) {
                            if (regdNo.getText().toString().length() > 0)
                                if (isUniqueNumber(regdNo.getText().toString(), view.getId()))
                                    validateVehicleApi(regdNo.getText().toString(), myes.isChecked() ? "1" : "0");
                                else
                                    AlertManager.messageDialog(getActivity(), "Alert!", "Please enter unique Vehicle Registration Number.");

                        }
                        return false;
                    }
                });


                type.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment instance = getActivity().getFragmentManager().findFragmentById(R.id.container);
                        if (instance instanceof ProfileStepTwo) {
                            if (regdNo.hasFocus())
                                if (vehicleTypeData.size() > 0)
                                    if (regdNo.getText().toString().length() > 0)
                                        if (isUniqueNumber(regdNo.getText().toString(), view.getId()))
                                            validateVehicleApi(regdNo.getText().toString(), myes.isChecked() ? "1" : "0");
                                        else
                                            AlertManager.messageDialog(getActivity(), "Alert!", "Please enter unique Vehicle Registration Number.");

                            if (vehicleTypeData.size() > 0) {
                                viewType = type;
                                showVehicleDialog(1, (Integer) type.getTag());
                                //  addMoreVehicle.setVisibility(View.VISIBLE);
                            } else getVehicleTypes();

                           /* if (IsvehicleEditable) {
                                IsvehicleEditable = false;
                                viewType = type;
                                //  type.setText("");
                                LinearLayout vwParentRow = (LinearLayout) v.getParent();
                                TextView _id = (TextView) vwParentRow.getChildAt(0);
                                EditText _name = (EditText) vwParentRow.getChildAt(2);

                                // vehicleJSONArray = RemoveJSONArray(vehicleJSONArray, Integer.parseInt(_id.getText().toString()));
                                addMoreVehicle.setText("SAV");
                                addMoreVehicle.setBackgroundResource(R.drawable.add_btn_tick);
                                Log.v("_name", "_name" + _name.getText().toString());
                                vehicleIndex = Integer.parseInt(_id.getText().toString());
                                regdNo.setText(_name.getText().toString());
                                if (vehicleTypeData.size() > 0)
                                    showVehicleDialog(1, (Integer) type.getTag());
                                else getVehicleTypes();
                                addMoreVehicle.setVisibility(View.VISIBLE);
                            } else {
                                showMessage("Please click on save button first");
                            }*/
                        }
                    }
                });
                //  appendVehicleToJson(jobj.getString("vehicle_type"), jobj.getString("registration_number"), "");
                layVehicles.addView(view);


            }

         /*   for (int i = 0; i < layVehicles.getChildCount(); i++) {
                View viewone = layVehicles.getChildAt(i);
                final TextView type = (TextView) viewone.findViewById(R.id.vehicle_type);
                final EditText regdNo = (EditText) viewone.findViewById(R.id.vehicle_regd_no);
                type.setEnabled(false);
                type.setClickable(false);
                regdNo.setEnabled(false);
                regdNo.setClickable(false);
            }*/

        } catch (JSONException e) {
            e.printStackTrace();
        }
       /* try {
            if (!value.equals("2")) {
                if (vehicleIndex > 0)
                    remove_vehicle(1);
                // int len = vehicleJSONArray.length();
                //if (vehicleJSONArray != null && vehicleJSONArray.length() > 1) {

                // }
            }
        } catch (Exception e) {
            e.toString();
        }*/
    }

    public boolean isUniqueNumber(String regno, int id) {
        for (int i = 0; i < layVehicles.getChildCount(); i++) {
            if (i == id)
                continue;
            View viewone = layVehicles.getChildAt(i);
            final EditText regdNo = (EditText) viewone.findViewById(R.id.vehicle_regd_no);
            String regdnostr = regdNo.getText().toString();
            if (regdnostr != null && regdnostr.contentEquals(regno))
                return false;
        }
        return true;
    }
    /*private String getTypesData() {
        String selectedTypes = "";
        for (int i = 0; i < suitableVehicleData.size(); i++) {
            Category cat = suitableVehicleData.get(i);
            if (idBuilder.contains(cat.getId())) {
                selectedTypes += cat.getName() + ", ";
                cat.setSelected(true);
            } else {
                cat.setSelected(false);
            }
            suitableVehicleData.set(i, cat);
        }
        return selectedTypes.substring(0, selectedTypes.length() - 2);
    }*/

    public void removeVehicleAlert(String message, final int val) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                remove_vehicle(Integer.parseInt(totalVehicle.getText().toString()));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                totalVehicle.setText(String.valueOf(val));
            }
        });
        Dialog d = builder.create();
        d.show();
    }

    public void remove_vehicle(int totalqty) {
        int j = 0;
        // int len = vehicleJSONArray.length();
        try {
            for (int i = vehicleIndex; i >= totalqty; i--) {
                View view = layVehicles.findViewById(i);
                layVehicles.removeView(view);
                regPapers.remove(i);
                insCertificate.remove(i);
                j++;
                Log.v("", "");
            }
            if (vehicleJSONArray != null) {
                for (int i = vehicleJSONArray.length(); i >= totalqty; i--) {
                    vehicleJSONArray.remove(i);
                }
            }
            vehicleIndex = vehicleIndex - j;
            Log.v("", "");

        } catch (Exception e) {
            e.toString();
        }
    }

    public static JSONArray RemoveJSONArray(JSONArray jarray, int pos) {
        JSONArray Njarray = new JSONArray();
        try {
            for (int i = 0; i < jarray.length(); i++) {
                if (i != pos)
                    Njarray.put(jarray.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Njarray;
    }

    private void doGetCountries() {
        type = 1;
        String url = Config.SERVER_URL + Config.COUNTRY;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, "");
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void showVehicleInsuranceDetail(boolean IsSave) {
        this.IsSave = IsSave;
        Log.e("vehicleIndex", "vehicleIndex" + vehicleIndex);

        if (isValid(IsSave)) {
            if (totalVehicle.getText().toString().trim().length() > 0) {
                if ((IsAlladded(IsSave)))
                    AsyncImageBase64();
            }else{
                saveCompanyData();
            }
        }
    }

    Bundle bundle;

    public boolean isAlphaNumeric(String s) {
        String pattern = "^[a-zA-Z0-9]*$";
        return s.matches(pattern);
    }


    public boolean IsAlladded(boolean istrue) {
        for (int i = 0; i < layVehicles.getChildCount(); i++) {
            try {
                final View viewone = layVehicles.getChildAt(i);
                final TextView type = (TextView) viewone.findViewById(R.id.vehicle_type);
                final EditText regdNo = (EditText) viewone.findViewById(R.id.vehicle_regd_no);
                RadioGroup mRadio = viewone.findViewById(R.id.m_group);
                RadioGroup mRadioFreight = viewone.findViewById(R.id.freight_group);

                final RadioButton myes = viewone.findViewById(R.id.m_yes);
                final RadioButton freightyes = viewone.findViewById(R.id.freight_yes);
                final EditText price = viewone.findViewById(R.id.f_price);

                if (regdNo.getText().toString().equals("")) {
                    regdNo.requestFocus();
                    showMessage(getActivity().getResources().getString(R.string.entervehicleno));
                    return false;
                } else if (!isAlphaNumeric(regdNo.getText().toString())) {
                    regdNo.requestFocus();
                    showMessage(getActivity().getResources().getString(R.string.vehiclenoalphanumeric));
                    return false;
                } else if (!isUniqueNumber(regdNo.getText().toString(), viewone.getId())) {
                    showMessage(getActivity().getResources().getString(R.string.uniqueregno));
                    return false;
                } else if (type.getText().toString().equals("")) {
                    showMessage(getActivity().getResources().getString(R.string.entervehicleno));
                    return false;
                } else if (mRadio.getCheckedRadioButtonId() == -1) {
                    showMessage(getActivity().getResources().getString(R.string.nominate) + " " + regdNo.getText().toString());
                    return false;
                }
                if(istrue==false) {
                    if (myes.isChecked()) {
                        if (!(regPapers.get(i).size() > 0)) {
                            showMessage(getActivity().getResources().getString(R.string.addimagesforregpaper) + " " + regdNo.getText().toString());
                            return false;
                        }
                    }
                    if (!(insCertificate.get(i).size() > 0)) {
                        showMessage(getActivity().getResources().getString(R.string.addimagesforinspaper) + " " + regdNo.getText().toString());
                        return false;

                    }
                   /* if (mRadioFreight.getCheckedRadioButtonId() == -1) {
                        showMessage("Please select freight insurance option.");
                        return false;
                    }*/
                }
                if (freightyes.isChecked()) {
                    if (price.getText().toString().trim().length() == 0) {
                        showMessage(getActivity().getResources().getString(R.string.selectfreightinscover));
                        return false;
                    }
                }

            } catch (Exception e) {
                e.toString();
            }
        }
        try {
            if (layVehicles.getChildCount() < Integer.parseInt(totalVehicle.getText().toString())) {
                showMessage(getActivity().getResources().getString(R.string.enterequalvehicle));
                return false;
            } else if (Integer.parseInt(totalVehicle.getText().toString()) < layVehicles.getChildCount()) {
                showMessage(getActivity().getResources().getString(R.string.totalqtyshouldnotless));
                return false;
            }
        } catch (Exception e) {
            e.toString();
        }
        return true;
    }

    public void validate() {
        //if (isValid()) {
        // Log.v("vehicleArray:", "vehicleArray:" + vehicleJSONArray.toString());
        //    if (IsAlladded()) {
        //  if (saveJsonData()) {
        bundle = new Bundle();
        bundle.putString("country_id", countryID);
        bundle.putString("state_id", stateID);
        bundle.putString("companyName", companyName.getText().toString());
        bundle.putString("abnNumber", abnNumber.getText().toString());
        bundle.putString("register_for_gst", mGst);
        //  bundle.putString("contactPerson", contactPerson.getText().toString());
        bundle.putString("street", street.getText().toString());
        bundle.putString("suburb", suburb.getText().toString());
        //bundle.putString("phone", countrycodestr + phone.getText().toString());
        bundle.putString("officeNumber", officeNumber.getText().toString());
        bundle.putString("postalCode", postalCode.getText().toString());


        if(vehicleJSONArray.toString().length()==2){
            bundle.putString("vehicleRegdNo", "");
        }else {
            bundle.putString("vehicleRegdNo", vehicleJSONArray.toString());
        }
        bundle.putString("totalVehicles", "" + totalVehicle.getText().toString());
        bundle.putString("company_logo", LogoPath);
        String backStateName = this.getClass().getName();
        Bundle info = new Bundle();
        info.putBundle("userInfo", this.getArguments().getBundle("userInfo"));
        info.putBundle("ownerInfo", bundle);
        Editable(false);
        Fragment fragment = new ProfileStepThree();
        fragment.setArguments(info);
        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.add(R.id.container, fragment, backStateName);
        // ft.hide(this);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
           /* } else {
                showMessage("Total qty should not be less then added vehicles");
            }*/
        // }
        // }
    }

    ProgressDialog dialog;

    public void AsyncImageBase64() {
        mTemp = new JSONArray();
        encoded_image = "";
        new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(getActivity());
                dialog.setMessage("Please wait...");
                dialog.setCancelable(false);
                dialog.show();
                vehicleJSONArray = new JSONArray();

            }

            @Override
            protected JSONArray doInBackground(Void... params) {
                JSONArray array = new JSONArray();
                JSONArray insc = new JSONArray();
                try {
                    try {
                        for (int j = 0; j < layVehicles.getChildCount(); j++) {
                            View viewone = layVehicles.getChildAt(j);
                            final TextView type = (TextView) viewone.findViewById(R.id.vehicle_type);
                            final EditText regdNo = (EditText) viewone.findViewById(R.id.vehicle_regd_no);
                            RadioButton myes = viewone.findViewById(R.id.m_yes);
                            RadioButton mNo = viewone.findViewById(R.id.m_no);
                            final RadioButton freightyes = viewone.findViewById(R.id.freight_yes);
                            final RadioButton freightNo = viewone.findViewById(R.id.freight_no);
                            final EditText price = viewone.findViewById(R.id.f_price);

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("vehicle_type", getVehicleTypeId(type.getText().toString()));
                            jsonObject.put("registration_number", regdNo.getText().toString());
                            jsonObject.put("is_owner", myes.isChecked() ? "1" : "0");
                            if (freightyes.isChecked())
                                jsonObject.put("is_freight_insurance_cover", "1");
                            else if (freightNo.isChecked())
                                jsonObject.put("is_freight_insurance_cover", "0");
                            else
                                jsonObject.put("is_freight_insurance_cover", "");

                            if (freightyes.isChecked())
                                jsonObject.put("freight_insurance_cover_amount", price.getText().toString());
                            array = new JSONArray();
                            if (myes.isChecked()) {
                                if (regPapers.get(j).size() > 0) {
                                    for (int i = 0; i < regPapers.get(j).size(); i++) {
                                        if (regPapers.get(j).get(i).toString().contains("http")) {
                                            if (regPapers.get(j).get(i).toString().contains(".pdf")) {
                                                Log.v("", "" + regPapers.get(j).get(i).toString());
                                                JSONObject Jobj = new JSONObject();
                                                Jobj.put("image", "");
                                                array.put(i, Jobj);
                                            } else {
                                                Log.v("", "" + regPapers.get(j).get(i).toString());
                                                URL newurl = new URL(regPapers.get(j).get(i).toString());

//                    URL newurl = new URL("http://demo.grabidnow.com/frontend/web/images/uploads/delivery/2a847c6e78ee338e3d678287e0d1e890.jpg");
                                                //   img_value = new URL(obj.getString("item_photo"));
                                                HttpURLConnection connection = (HttpURLConnection) newurl.openConnection();
                                                connection.setDoInput(true);
                                                connection.connect();
                                                InputStream input = connection.getInputStream();
                                                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                                                encoded_image = Utils.encodeTobase64(myBitmap);
                                                Log.v("", encoded_image.toString());
                                                JSONObject Jobj = new JSONObject();
                                                Jobj.put("image", encoded_image);
                                                array.put(i, Jobj);
                                            }
                                        } else {
                                            encoded_image = FileOperation.encodeFileToBase64Binary(regPapers.get(j).get(i).toString());
                                            Log.v("", encoded_image.toString());
                                            JSONObject Jobj = new JSONObject();
                                            Jobj.put("image", encoded_image);
                                            array.put(i, Jobj);
                                        }

                                    }
                                }
                            }
                            jsonObject.put("registration_papers_img", array);
                            insc = new JSONArray();
                            if (insCertificate.get(j).size() > 0) {

                                for (int i = 0; i < insCertificate.get(j).size(); i++) {
                                    if (insCertificate.get(j).get(i).toString().contains("http")) {
                                        if (insCertificate.get(j).get(i).toString().contains(".pdf")) {
                                            JSONObject Jobj = new JSONObject();
                                            Jobj.put("image", "");
                                            insc.put(i, Jobj);
                                        } else {
                                            Log.v("", "");
                                            URL newurl = new URL(insCertificate.get(j).get(i).toString());

//                                          URL newurl = new URL("http://demo.grabidnow.com/frontend/web/images/uploads/delivery/2a847c6e78ee338e3d678287e0d1e890.jpg");
                                            //   img_value = new URL(obj.getString("item_photo"));
                                            HttpURLConnection connection = (HttpURLConnection) newurl.openConnection();
                                            connection.setDoInput(true);
                                            connection.connect();
                                            InputStream input = connection.getInputStream();
                                            Bitmap myBitmap = BitmapFactory.decodeStream(input);
                                            encoded_image = Utils.encodeTobase64(myBitmap);
                                            Log.v("", encoded_image.toString());
                                            JSONObject Jobj = new JSONObject();
                                            Jobj.put("image", encoded_image);
                                            insc.put(i, Jobj);
                                        }
                                    } else {
                                        encoded_image = FileOperation.encodeFileToBase64Binary(insCertificate.get(j).get(i).toString());
                                        Log.v("", encoded_image.toString());
                                        JSONObject Jobj = new JSONObject();
                                        Jobj.put("image", encoded_image);
                                        insc.put(i, Jobj);
                                    }
                                }

                            }
                            jsonObject.put("insurance_certificate_img", insc);
                            //jsonObject.put("is_freight_insurance_cover", "1");
                            //jsonObject.put("freight_insurance_cover_amount", "100");
                            vehicleJSONArray.put(jsonObject);

                        }

                           /* if (insCertificate.get(index).size() > 0) {
                                for (int i = 0; i < insCertificate.get(index).size(); i++) {
                                    encoded_image = FileOperation.encodeFileToBase64Binary(insCertificate.get(index).get(i).toString());
                                    Log.v("", encoded_image.toString());
                                    JSONObject Jobj = new JSONObject();
                                    Jobj.put("image", encoded_image);
                                    array.put(i, Jobj);
                                }
                            }*/


                         /*   */

                    } catch (Exception ex) {
                        ex.toString();
                    }

                } catch (Exception e) {
                    e.toString();
                }
                return vehicleJSONArray;
            }

            @Override
            protected void onPostExecute(JSONArray aVoid) {
                // update the UI (this is executed on UI thread)
                super.onPostExecute(aVoid);
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();

                saveCompanyData();

                // CompleteDelivery(deliveryId, aVoid.toString());
            }
        }.execute();
    }

    public void saveCompanyData() {
        type = 5;
        HashMap<String, String> params = new HashMap<>();
        String url = Config.SERVER_URL + Config.COMPANY_DATA;
        params.put("country_id", countryID);
        params.put("state_id", stateID);
        params.put("name", companyName.getText().toString());
        params.put("abn_number", abnNumber.getText().toString());
        params.put("register_for_gst", mGst);
        //  params.put("contact_person", contactPerson.getText().toString());
        params.put("address", street.getText().toString());
        params.put("suburb", suburb.getText().toString());
        params.put("postal_code", postalCode.getText().toString());
        //params.put("company_mobile", countrycodestr + phone.getText().toString());
        params.put("office_number", officeNumber.getText().toString());
        if(vehicleJSONArray.toString().length()==2){
            params.put("vehicle_detail", "");
        }else {
            params.put("vehicle_detail", vehicleJSONArray.toString());
        }
        params.put("vehicle_in_fleet", totalVehicle.getText().toString());
        if (!LogoPath.contentEquals("") && !LogoPath.contains("http"))
            try {
                company_Logo = Utils.encodeFileToBase64Binary(LogoPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (!company_Logo.contentEquals(""))
            params.put("company_logo", company_Logo);

        try {
            JSONArray dobj=new JSONArray();
            if(MultipleImages.deletedPDF.length==0){
                params.put("DeletedPDF", "");
            }else {
                for (int i = 0; i < MultipleImages.deletedPDF.length; i++) {
                    dobj.put(MultipleImages.deletedPDF[i]);
                }
                params.put("DeletedPDF", dobj.toString());
            }
        }catch (Exception ex){
            params.put("DeletedPDF", "");
        }

        Log.d("end", params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage(getActivity().getResources().getString(R.string.no_internet));

    }

    public boolean saveJsonData() {
        vehicleJSONArray = new JSONArray();
        for (int i = 0; i < layVehicles.getChildCount(); i++) {
            try {
                View viewone = layVehicles.getChildAt(i);
                final TextView type = (TextView) viewone.findViewById(R.id.vehicle_type);
                final EditText regdNo = (EditText) viewone.findViewById(R.id.vehicle_regd_no);
                RadioButton myes = viewone.findViewById(R.id.m_yes);
                RadioButton mNo = viewone.findViewById(R.id.m_no);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("vehicle_type", getVehicleTypeId(type.getText().toString()));
                jsonObject.put("registration_number", regdNo.getText().toString());
                jsonObject.put("is_owner", myes.isChecked() ? "1" : "0");
                vehicleJSONArray.put(jsonObject);
                Log.v("", "");
            } catch (Exception e) {
                e.toString();
            }
        }
        Log.v("", "");
        try {
            if (vehicleJSONArray.length() < Integer.parseInt(totalVehicle.getText().toString())) {
                showMessage("Please enter vehicles equal to total vehicles.");
                return false;
            } else if (Integer.parseInt(totalVehicle.getText().toString()) < vehicleJSONArray.length()) {
                showMessage("Total qty should not be less then added vehicles");
                return false;
            } else
                return true;


        } catch (Exception e) {
            e.toString();
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == 13 && resultCode == -1) {
            try {
                String val = result.getStringExtra("value");
                viewType.setText(result.getStringExtra("strvalue"));
                vehicleTypeID = val;
                Log.v("val", vehicleTypeID);
            } catch (Exception e) {
                e.toString();
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_UPDATE_PIC) {
                String imagePath = result.getStringExtra(Constants.IntentExtras.IMAGE_PATH);
                showCroppedImage(imagePath);
            }
        } else {
            if (requestCode == REQUEST_CODE_UPDATE_PIC) {
                //  String errorMsg = result.getStringExtra(ImageCropActivity.ERROR_MSG);
                // Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showCroppedImage(String mImagePath) {
        if (mImagePath != null) {
            if (mImagetype == 0) {
                regpaperImgPath = mImagePath;
                Bitmap myBitmap = BitmapFactory.decodeFile(mImagePath);
                // mImage.setImageBitmap(myBitmap);
                // mImage.setVisibility(View.VISIBLE);
                regPapers.get(img_pos).add(regpaperImgPath);
                // list.add(regpaperImgPath);
                imagesAdapter.notifyDataSetChanged();
            } else if (mImagetype == 1) {
                inscimagepath = mImagePath;
                insCertificate.get(img_pos).add(inscimagepath);
                mInscAdapter.notifyDataSetChanged();
            } else {
                Bitmap myBitmap = BitmapFactory.decodeFile(mImagePath);
                SaveCapturedImagePath(mImagePath);
                img.setImageBitmap(myBitmap);
                layImg.setVisibility(View.VISIBLE);
                pic.setVisibility(View.GONE);
            }
        }
    }

    public void SaveCapturedImagePath(String path) {
      /*  SharedPreferences myPrefs = getActivity().getSharedPreferences(Config.PREF_NAME, 0);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putString("profile_photo", path);
        prefsEditor.commit();*/
        // ProfileModel.setProfileimg(path);
        LogoPath = path;
    }

    ArrayList<Category> suitableVehicleData = new ArrayList<Category>();
    String suitableIdBuilder = "";
    JSONArray suitableArray = new JSONArray();

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

  /*  private class CategoryAdapter extends BaseAdapter {
        Context ctx;
        int index;

        CategoryAdapter(Context ctx, int index) {
            this.ctx = ctx;
            this.index = index;

        }

        @Override
        public int getCount() {
            return suitableVehicleData.size();
        }

        @Override
        public Object getItem(int i) {
            return suitableVehicleData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.simple_list_item_check, null);
            Category category = (Category) getItem(position);
            final TextView name = (TextView) convertView.findViewById(R.id.textItem);
            final CheckBox check = (CheckBox) convertView.findViewById(R.id.check);
            TextView done = (TextView) convertView.findViewById(R.id.done);
            name.setTag(position);

            if (position == suitableVehicleData.size() - 1) {
                done.setVisibility(View.VISIBLE);
            } else done.setVisibility(View.GONE);

            name.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (check.isChecked())
                        check.setChecked(false);
                    else
                        check.setChecked(true);
                    check.setChecked(true);
                    Category country = suitableVehicleData.get((int) name.getTag());
                    country.setSelected(check.isChecked());
                    suitableVehicleData.set((int) name.getTag(), country);
                }
                //notifyDataSetChanged();

            });
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    Category country = suitableVehicleData.get((int) name.getTag());
                    country.setSelected(isChecked);
                    suitableVehicleData.set((int) name.getTag(), country);
                }
            });
            done.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String builder = "";
                    try {
                        suitableIdBuilder = "";
                        for (int i = 0; i < suitableVehicleData.size(); i++) {
                            if (suitableVehicleData.get(i).isSelected()) {
                                builder += suitableVehicleData.get(i).getName() + ", ";
                                suitableIdBuilder += suitableVehicleData.get(i).getId() + ",";
                                JSONObject object = new JSONObject();
                                object.put("name", suitableVehicleData.get(i).getName());
                                object.put("id", suitableVehicleData.get(i).getId());
                                suitableArray.put(object);
                            }
                        }
                        if (!suitableIdBuilder.equals("")) {
                            try {
                                String val = suitableIdBuilder.substring(0, suitableIdBuilder.length() - 1);
                                vehicleTypeID.put(index, val);
                            } catch (Exception e) {
                                e.toString();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (builder.equals("")) {
                        showMessage("Please select driver category");
                    } else {
                        viewType.setText(builder.substring(0, builder.length() - 2));
                        if (mDialog != null && mDialog.isShowing())
                            mDialog.dismiss();
                    }
                }
            });
            name.setText(category.getName());
            check.setChecked(category.isSelected());
            return convertView;
        }
    }*/

    boolean isRemovable = false;
    JSONArray vehicleArray = new JSONArray();
    JSONArray vehicleJSONArray = new JSONArray();

    private void appendVehicleToJson(String vehicleTypeID, String vehicleRegdNo, String pos) {
        try {
            if (pos.contentEquals("")) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("vehicle_type", vehicleTypeID);
                jsonObject.put("registration_number", vehicleRegdNo);
                vehicleJSONArray.put(jsonObject);
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("vehicle_type", vehicleTypeID);
                jsonObject.put("registration_number", vehicleRegdNo);
                vehicleJSONArray.put(Integer.parseInt(pos), jsonObject);
            }
            //  vehicleJSONArray.put()
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Vehicle_data", vehicleJSONArray.toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("resume", "resume");
    }

    Dialog mDialog;

    public void showVehicleDialog(final int val, final int index) {
        Intent intent = new Intent(getActivity(), VehicleListProfileCompany.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("array", suitableVehicleData);
        bundle.putInt("index", index);
        if (viewType.getText().toString().length() > 0)
            bundle.putString("vehicletypeid", getVehicleTypeId(viewType.getText().toString()));
        intent.putExtras(bundle);
        startActivityForResult(intent, 13);

    }

    /*public void showVehicleDialog(final int val, int index) {
        mDialog = new Dialog(getActivity(), R.style.GrabidDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list_new);
        mDialog.setCanceledOnTouchOutside(true);
        //  mDialog.setCancelable(false);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("Select Vehicle Type");
        ImageView img = (ImageView) mDialog.findViewById(R.id.close);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (val == 1) {
                    addMoreVehicle.setText("ADD");
                    addMoreVehicle.setBackgroundResource(R.drawable.add_btn);
                    vehicleIndex = vehicleJSONArray.length() - 1;
                    IsvehicleEditable = true;
                }
                if (mDialog.isShowing())
                    mDialog.dismiss();
            }
        });

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
        //CategoryAdapter catAdapter = new CategoryAdapter(getActivity(), index);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.dialog_textview, R.id.textItem, getVehicleTypeList());

        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                viewType.setText(parent.getItemAtPosition(position).toString());
                vehicleTypeID = getVehicleTypeId(parent.getItemAtPosition(position).toString());
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }*/

    public String[] getVehicleTypeList() {
        String[] listContent = new String[vehicleTypeData.size()];
        for (int i = 0; i < vehicleTypeData.size(); i++) {
            listContent[i] = vehicleTypeData.get(i).get(Keys.KEY_NAME);
        }
        return listContent;
    }

    public String getVehicleTypeId(String stateName) {
        for (int i = 0; i < vehicleTypeData.size(); i++) {
            Log.v("outside", "outside" + vehicleTypeData.get(i).get(Keys.KEY_NAME));
            if (vehicleTypeData.get(i).get(Keys.KEY_NAME).equalsIgnoreCase(stateName)) {
                Log.v("inside", "inside" + vehicleTypeData.get(i).get(Keys.KEY_NAME));
                return vehicleTypeData.get(i).get(Keys.KEY_ID);
            }
        }
        return "";
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

    private boolean isValid(Boolean check) {
        if(check==true) {
            if (!IsStepTwo) {
                if (abnNumber.getText().toString().trim().length() > 0) {
                    if (lengthAbn(abnNumber.getText().toString()) < 11) {
                        showMessage(getActivity().getResources().getString(R.string.digitabnnumber));
                        return false;
                    }
                }else if (postalCode.getText().toString().trim().length() > 0) {
                        if (postalCode.getText().toString().trim().length() < 4) {
                            showMessage(getActivity().getResources().getString(R.string.validpostalcode));
                            return false;
                        }
                }
                if (officeNumber.getText().toString() != null && !officeNumber.getText().toString().contentEquals("")) {
                    if (lengthAbn(officeNumber.getText().toString()) < 10) {
                        showMessage(getActivity().getResources().getString(R.string.tendigitofficeno));
                        return false;
                    }
                } else if (stateID.contentEquals("")) {
                    stateID = info.getStateId();
                    countryID = info.getCountryID();
                    Log.v("", stateID.toString());
                }
                if (mRegisteryes.isChecked())
                    mGst = "1";
                else
                    mGst = "0";
            }
        }else {
            if (abnNumber.getText().toString().trim().length() == 0 && mGstRadio.getCheckedRadioButtonId() == -1 &&
                    street.getText().toString().trim().length() == 0 && suburb.getText().toString().trim().length() == 0
                    && country.getText().toString().trim().length() == 0 && state.getText().toString().trim().length() == 0
                    && postalCode.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.completeallfield));
                return false;

            }
            if (abnNumber.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.enterabnnumber));
                return false;
            } else if (lengthAbn(abnNumber.getText().toString()) < 11) {
                showMessage(getActivity().getResources().getString(R.string.digitabnnumber));
                return false;
            } else if (mGstRadio.getCheckedRadioButtonId() == -1) {
                showMessage(getActivity().getResources().getString(R.string.selectregisterforgst));
                return false;
            } else if (street.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.companystreetandno));
                return false;
            } else if (suburb.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.entersuburb));
                return false;
            } else if (postalCode.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.postalcode));
                return false;
            } else if (postalCode.getText().toString().trim().length() < 4) {
                showMessage(getActivity().getResources().getString(R.string.validpostalcode));
                return false;
            } else if (country.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.selectcountry));
                return false;
            } else if (state.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.selectstate));
                return false;
            }
            if (officeNumber.getText().toString() != null && !officeNumber.getText().toString().contentEquals("")) {
                if (lengthAbn(officeNumber.getText().toString()) < 10) {
                    showMessage(getActivity().getResources().getString(R.string.tendigitofficeno));
                    return false;
                }
            }
            if (totalVehicle.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.totalnoofvehicle));
                return false;
            } else if (stateID.contentEquals("")) {
                stateID = info.getStateId();
                countryID = info.getCountryID();
                Log.v("", stateID.toString());

            }
            if (mRegisteryes.isChecked())
                mGst = "1";
            else
                mGst = "0";
        }
        return true;
    }

    //AutoComplete url
    private String getAutoCompleteUrl(String place) {
        String key = getActivity().getResources().getString(R.string.searckey);
        String url = "";
        try {
            String input = "input=" + URLEncoder.encode(place, "utf-8");
            String types = "types=geocode";
            String sensor = "sensor=false";
            String compp = "components=country:au|country:in";
            String parameters = input + "&" + types + "&" + sensor + "&" + key + "&" + compp;
            String output = "json";
            url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;
        } catch (Exception ex) {
            Log.v("error", "error" + ex.getMessage());
            url = "";
        }
        Log.v("url", "url:" + url);
        return url;
    }

    private String getPlaceDetailsUrl(String ref) {
        String url = "";
        try {
            String key = getActivity().getResources().getString(R.string.searckey);
            String reference = "reference=" + URLEncoder.encode(ref, "utf-8");
            String sensor = "sensor=false";
            String compp = "components=country:au|country:in";
            String parameters = reference + "&" + sensor + "&" + key + "&" + compp;
            String output = "json";
            url = "https://maps.googleapis.com/maps/api/place/details/" + output + "?" + parameters;
        } catch (Exception ex) {
            Log.v("error", "error" + ex.getMessage());
            url = "";
        }
        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {
        private int downloadType = 0;

        public DownloadTask(int type) {
            this.downloadType = type;
        }

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            switch (downloadType) {
                case PLACES:
                    placesParserTask = new ParserTask(PLACES);
                    placesParserTask.execute(result);
                    break;
                case PLACES_DETAILS:
                    placeDetailsParserTask = new ParserTask(PLACES_DETAILS);
                    placeDetailsParserTask.execute(result);
            }
        }
    }


    double dLat, dLng;
    String dCity, dState, dCountry;

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        int parserType = 0;

        public ParserTask(int type) {
            this.parserType = type;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<HashMap<String, String>> list = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                switch (parserType) {
                    case PLACES:
                        PlaceJSONParser placeJsonParser = new PlaceJSONParser();
                        list = placeJsonParser.parse(jObject);
                        break;
                    case PLACES_DETAILS:
                        PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
                        list = placeDetailsJsonParser.parse(jObject);
                }
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            switch (parserType) {
                case PLACES:
                    try {
                        String[] from = new String[]{"description"};
                        int[] to = new int[]{android.R.id.text1};
                        SimpleAdapter adapter = new SimpleAdapter(getActivity(), result, android.R.layout.simple_list_item_1, from, to);
                        Log.v("from", "from" + from);
                        street.setAdapter(adapter);
                        street.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (hasFocus) {
                                    //    if (!IsEdit) {
                                    try {
                                        street.setDropDownHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                                        street.showDropDown();
                                    } catch (Exception e) {
                                        e.toString();
                                    }
                                }
                                //   }

                            }
                        });

                        street.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                // if (!IsEdit) {
                                try {
                                    street.setDropDownHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                                    street.showDropDown();
                                } catch (Exception e) {
                                    e.toString();
                                }
                                //  }
                                return false;
                            }
                        });
                        try {
                            street.showDropDown();
                        } catch (Exception e) {
                            e.toString();
                        }


                    } catch (Exception ex) {

                    }
                    break;
                case PLACES_DETAILS:
                    try {
                        HashMap<String, String> hm = result.get(0);
                        dLat = Double.parseDouble(hm.get("lat"));
                        dLng = Double.parseDouble(hm.get("lng"));
                        getAddress(dLat, dLng);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
            }
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        /*if (++check > 1) {
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
        }*/


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                // Here are some results you can geocode
                String city = "";
                String state = "";
                String country = "";
                String postalCodestr = "";

                if (addresses.get(0).getLocality() != null) {
                    city = addresses.get(0).getLocality();
                    Log.d("city", city);
                }

                if (addresses.get(0).getAdminArea() != null) {
                    state = addresses.get(0).getAdminArea();
                    Log.d("state", state);
                }

                if (addresses.get(0).getCountryName() != null) {
                    country = addresses.get(0).getCountryName();
                    Log.d("country", country);
                }
                if (addresses.get(0).getPostalCode() != null) {
                    postalCodestr = addresses.get(0).getPostalCode();
                    Log.d("country", country);
                }
                dCity = city;
                if (state.equals(""))
                    dState = city;
                else
                    dState = state;
                dCountry = country;
                this.suburb.setText(dCity);
                this.state.setText(dState);
                this.country.setText(dCountry);
                countryID = getCountryId(dCountry);
                doGetStates(countryID);
                stateID = getStateId(dState);
                if (postalCodestr != null && !postalCodestr.contentEquals("null") && !postalCodestr.contentEquals("")) {
                    postalCode.setText(postalCodestr);
                }
                Log.v("", "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
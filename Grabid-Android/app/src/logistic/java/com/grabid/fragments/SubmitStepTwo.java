package com.grabid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.adapters.CountryCodeAdapter;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.common.Utils;
import com.grabid.models.Delivery;
import com.grabid.util.PlaceDetailsJSONParser;
import com.grabid.util.PlaceJSONParser;
import com.grabid.views.BoldAutoCompleteTextView;
import com.grabid.views.CustomDatePicker;
import com.grabid.views.MySpinner;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.grabid.common.AlertManager.messageDialog;

/**
 * Created by vinod on 10/14/2016.
 */
public class SubmitStepTwo extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, AsyncTaskCompleteListener {
    final int PLACES = 0;
    final int PLACES_DETAILS = 1;
    int searchType = 0;
    TextView submit, puDateTimeChoose, doDateTimeChoose, puDateTime, doEndDateTime, puEndDateTime, doDateTime, auctionStart, auctionEnd;
    SessionManager session;
    EditText deliveryTitle, puContactPerson, puMobile, doContactPerson,
            doMobile;
    EditText maxOpeningBid, fixedPrice;
    RadioGroup auctionBid;
    LinearLayout auctionYes, auctionNo, layPUEndDate, layDOEndDate;
    BoldAutoCompleteTextView puAddress, doAddress;
    ParserTask placesParserTask;
    ParserTask placeDetailsParserTask;
    DownloadTask placesDownloadTask;
    DownloadTask placeDetailsDownloadTask;
    Delivery delivery = null;
    TextView pu_cCode, do_cCode;
    MySpinner pu_countrypicker, do_countrypicker;
    String[] countrycode = {"+61", "+91"};
    public static String pu_countrycodestr = "";
    public static String do_countrycodestr = "";
    int[] flags = {R.drawable.au, R.drawable.in};
    int checkpu = 0, checkdo = 0;
    String duration = "", mPickDropdiff = "", mPickupDiff = "";
    boolean IsClicked = false;
    boolean mCreditCardDetail = true;


    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.seagreen));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.text_color_white));
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        View view = inflater.inflate(R.layout.shipment_address, null);
        init(view);
        if (getArguments().containsKey("home"))
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_white);
        else
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);

        if (getArguments().containsKey("data"))
            appendData(view);
        getDuration(false);
        return view;
    }

    public void UpdateDesign() {
        if (getArguments().containsKey("home"))
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_white);
        else
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments().containsKey("home"))
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_white);
        else
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
    }

    private void appendData(View view) {
        HashMap<String, Delivery> map = (HashMap<String, Delivery>) getArguments().getSerializable("data");
        Delivery delivery = map.get("data");
        Log.d("Edit", delivery.getDeliveryTypeID());
        Log.d("Edit", delivery.getDeliveryTypeSubID());
        puCity = delivery.getPickupCity();
        puState = delivery.getPickupState();
        puCountry = delivery.getPickupCountry();
        doCity = delivery.getDropoffCity();
        doState = delivery.getDropoffState();
        doCountry = delivery.getDropoffCountry();
        Log.v("data", "data pu 1:" + delivery.getPickupCity() + ":" + delivery.getPickupState() + ":" + delivery.getPickupCountry());
        Log.v("data", "data do 2:" + delivery.getDropoffCity() + ":" + delivery.getDropoffState() + ":" + delivery.getDropoffCountry());
        this.delivery = delivery;
        deliveryTitle.setText(delivery.getTitle());
        puContactPerson.setText(delivery.getPuContactPerson());
        puMobile.setText(delivery.getPuMobile());
        String pu_mobile = delivery.getPuMobile();
        if (pu_mobile != null && !pu_mobile.contentEquals("")) {
            try {
                String firstCharacter = pu_mobile.substring(0, 1);
                if (firstCharacter != null && firstCharacter.contentEquals("0")) {
                    String s = pu_mobile;
                    pu_mobile = s.replaceFirst("^0*", "");
                    if (s.isEmpty()) s = "0";
                    pu_countrypicker.setSelection(0);
                    pu_cCode.setText("+61");
                    puMobile.setText(pu_mobile);
                } else if (pu_mobile.length() > 3) {
                    String first = pu_mobile.substring(0, 3);
                    String last = pu_mobile.substring(3, pu_mobile.length());
                    // String last = mobile.substring(mobile.length() - 1, mobile.length());
                    if (first.contentEquals("+91")) {
                        pu_countrypicker.setSelection(1);
                        pu_cCode.setText(first);
                        puMobile.setText(last);
                    } else if (first.contentEquals("+61")) {
                        pu_countrypicker.setSelection(0);
                        pu_cCode.setText(first);
                        puMobile.setText(last);
                    } else {
                        // countrypicker.setSelection(0);
                        //   cCode.setText("+61");
                        puMobile.setText(delivery.getPuMobile());
                    }
                    Log.v("", last);
                } else {
                    // countrypicker.setSelection(0);
                    //    cCode.setText("+61");
                    puMobile.setText(delivery.getPuMobile());
                }
            } catch (Exception e) {
                e.toString();
            }
        }
        /*if (pu_mobile != null && !pu_mobile.contentEquals("")) {
            try {
                if (pu_mobile.length() > 3) {
                    String first = pu_mobile.substring(0, 3);
                    String last = pu_mobile.substring(3, pu_mobile.length());
                    // String last = mobile.substring(mobile.length() - 1, mobile.length());
                    if (first.contentEquals("+91")) {
                        pu_countrypicker.setSelection(1);
                        pu_cCode.setText(first);
                        puMobile.setText(last);
                    } else if (first.contentEquals("+61")) {
                        pu_countrypicker.setSelection(0);
                        pu_cCode.setText(first);
                        puMobile.setText(last);
                    } else {
                        pu_countrypicker.setSelection(0);
                        pu_cCode.setText("+61");
                        puMobile.setText(pu_mobile);
                    }
                    Log.v("", last);
                } else {
                    pu_countrypicker.setSelection(0);
                    pu_cCode.setText("+61");
                    puMobile.setText(delivery.getPuMobile());
                }
            } catch (Exception e) {
                e.toString();
            }
        }*/


        setDateTime(delivery.getPickUpDate(), 1);
        setDateTime(delivery.getDropOffDate(), 2);
        doContactPerson.setText(delivery.getDoContactPerson());
        doMobile.setText(delivery.getDoMobile());

        String do_mobile = delivery.getDoMobile();
        if (do_mobile != null && !do_mobile.contentEquals("")) {
            try {
                String firstCharacter = do_mobile.substring(0, 1);
                if (firstCharacter != null && firstCharacter.contentEquals("0")) {
                    String s = do_mobile;
                    do_mobile = s.replaceFirst("^0*", "");
                    if (s.isEmpty()) s = "0";
                    do_countrypicker.setSelection(0);
                    do_cCode.setText("+61");
                    doMobile.setText(do_mobile);
                } else if (do_mobile.length() > 3) {
                    String first = do_mobile.substring(0, 3);
                    String last = do_mobile.substring(3, do_mobile.length());
                    // String last = mobile.substring(mobile.length() - 1, mobile.length());
                    if (first.contentEquals("+91")) {
                        do_countrypicker.setSelection(1);
                        do_cCode.setText(first);
                        doMobile.setText(last);
                    } else if (first.contentEquals("+61")) {
                        do_countrypicker.setSelection(0);
                        do_cCode.setText(first);
                        doMobile.setText(last);
                    } else {
                        // countrypicker.setSelection(0);
                        //   cCode.setText("+61");
                        doMobile.setText(delivery.getDoMobile());
                    }
                    Log.v("", last);
                } else {
                    // countrypicker.setSelection(0);
                    //    cCode.setText("+61");
                    doMobile.setText(delivery.getDoMobile());
                }
            } catch (Exception e) {
                e.toString();
            }
        }
       /* if (do_mobile != null && !do_mobile.contentEquals("")) {
            try {
                if (do_mobile.length() > 3) {
                    String first = do_mobile.substring(0, 3);
                    String last = do_mobile.substring(3, do_mobile.length());
                    // String last = mobile.substring(mobile.length() - 1, mobile.length());
                    if (first.contentEquals("+91")) {
                        do_countrypicker.setSelection(1);
                        do_cCode.setText(first);
                        doMobile.setText(last);
                    } else if (first.contentEquals("+61")) {
                        do_countrypicker.setSelection(0);
                        do_cCode.setText(first);
                        doMobile.setText(last);
                    } else {
                        do_countrypicker.setSelection(0);
                        do_cCode.setText("+61");
                        doMobile.setText(pu_mobile);
                    }
                    Log.v("", last);
                } else {
                    do_countrypicker.setSelection(0);
                    do_cCode.setText("+61");
                    doMobile.setText(delivery.getPuMobile());
                }
            } catch (Exception e) {
                e.toString();
            }
        }*/


        puAddress.setText(delivery.getPickUpAddress());
        doAddress.setText(delivery.getDropoffAdress());
        if (delivery.getAuctionBid().equals("1")) {
            RadioButton btn = (RadioButton) view.findViewById(R.id.o_yes);
            btn.setChecked(true);
            auctionNo.setVisibility(View.GONE);
            auctionYes.setVisibility(View.VISIBLE);
            //auctionStart.setText(delivery.getAuctionStart());
            // auctionEnd.setText(delivery.getAuctionEnd());
            setDateTime(delivery.getAuctionStart(), 3);
            setDateTime(delivery.getAuctionEnd(), 4);
            maxOpeningBid.setText(delivery.getMaxOpeningBid());
        } else {
            RadioButton btn = (RadioButton) view.findViewById(R.id.o_no);
            btn.setChecked(true);
            auctionYes.setVisibility(View.GONE);
            auctionNo.setVisibility(View.VISIBLE);
            fixedPrice.setText(delivery.getFixedOffer());
        }

        puDateType = delivery.getPickupDateType();
        SetPickDropOption(puDateTimeChoose, puDateType, layPUEndDate, true);
        doDateType = delivery.getDropoffDateType();
        SetPickDropOption(doDateTimeChoose, doDateType, layDOEndDate, false);
        String pkupdatetime = delivery.getPickupEndDate();
        String endpickupdate = delivery.getDropOffEndDate();

        puEndDateTime.setText(delivery.getPickupEndDate());
        doEndDateTime.setText(delivery.getDropOffEndDate());
        fillData(delivery);
    }

    public void fillData(Delivery delivery) {
        try {
            if (delivery.getPuLat() != null)
                puLat = Double.parseDouble(delivery.getPuLat());
            if (delivery.getPuLng() != null)
                puLlng = Double.parseDouble(delivery.getPuLng());
            if (delivery.getDoLat() != null)
                doLat = Double.parseDouble(delivery.getDoLat());
            if (delivery.getDoLng() != null)
                doLng = Double.parseDouble(delivery.getDoLng());

        } catch (Exception e) {
            e.toString();
        }
    }

    private void SetPickDropOption(TextView type, String value, LinearLayout layout, Boolean pickup) {
        try {
            /*if (value.equalsIgnoreCase("1")) {
                layout.setVisibility(View.GONE);
                type.setText("After");
            }
            if (value.equalsIgnoreCase("2")) {
                layout.setVisibility(View.GONE);
                type.setText("Before");
            }
            if (value.equalsIgnoreCase("3")) {
                layout.setVisibility(View.VISIBLE);
                type.setText("Between");
            } else if (value.equalsIgnoreCase("4")) {
                layout.setVisibility(View.VISIBLE);
                type.setText("On");
                if (pickup) {
                    setDateTime(delivery.getPickupEndDate(), 5);
                } else {
                    setDateTime(delivery.getDropOffEndDate(), 6);
                }
            }*/
            if (value.equalsIgnoreCase("2")) {
                layout.setVisibility(View.GONE);
                type.setText("After");
            }
            if (value.equalsIgnoreCase("3")) {
                layout.setVisibility(View.GONE);
                type.setText("Before");
            }
            if (value.equalsIgnoreCase("4")) {
                layout.setVisibility(View.VISIBLE);
                type.setText("Between");
                if (pickup) {
                    setDateTime(delivery.getPickupEndDate(), 5);
                } else {
                    setDateTime(delivery.getDropOffEndDate(), 6);
                }
            } else if (value.equalsIgnoreCase("1")) {
                layout.setVisibility(View.GONE);
                type.setText("On");
            }
        } catch (Exception ex) {
            Log.v("eror", "error:" + ex.getMessage());
        }
    }

    int phoneCount = 0;
    int puphoneCount = 0;

    private void init(View view) {
        session = new SessionManager(getActivity());
        deliveryTitle = (EditText) view.findViewById(R.id.title);
        puContactPerson = (EditText) view.findViewById(R.id.pu_contact);
        puMobile = (EditText) view.findViewById(R.id.pu_mobile);
        puAddress = (BoldAutoCompleteTextView) view.findViewById(R.id.pu_address);
        puDateTime = (TextView) view.findViewById(R.id.pu_date);
        puDateTime.setOnClickListener(this);
        doContactPerson = (EditText) view.findViewById(R.id.do_contact);
        doAddress = (BoldAutoCompleteTextView) view.findViewById(R.id.do_address);
        doMobile = (EditText) view.findViewById(R.id.do_mobile);
        doDateTime = (TextView) view.findViewById(R.id.do_date);
        doDateTime.setOnClickListener(this);

        puDateTimeChoose = (TextView) view.findViewById(R.id.pu_date_choose);
        puDateTimeChoose.setOnClickListener(this);
        doDateTimeChoose = (TextView) view.findViewById(R.id.do_date_choose);
        doDateTimeChoose.setOnClickListener(this);
        puEndDateTime = (TextView) view.findViewById(R.id.pu_date_end);
        puEndDateTime.setOnClickListener(this);
        doEndDateTime = (TextView) view.findViewById(R.id.do_date_end);
        doEndDateTime.setOnClickListener(this);

        auctionStart = (TextView) view.findViewById(R.id.a_start_time);
        auctionStart.setOnClickListener(this);
        auctionEnd = (TextView) view.findViewById(R.id.a_end_time);
        auctionEnd.setOnClickListener(this);
        maxOpeningBid = (EditText) view.findViewById(R.id.max_opening_bid);
        fixedPrice = (EditText) view.findViewById(R.id.fix_price);
        auctionBid = (RadioGroup) view.findViewById(R.id.auction_group);
        auctionBid.setOnCheckedChangeListener(AuctionBidListener);
        submit = (TextView) view.findViewById(R.id.submit);
        submit.setOnClickListener(this);
        auctionYes = (LinearLayout) view.findViewById(R.id.lay_auction_yes);
        auctionNo = (LinearLayout) view.findViewById(R.id.lay_auction_no);

        layPUEndDate = (LinearLayout) view.findViewById(R.id.lay_pu_end_date);
        layDOEndDate = (LinearLayout) view.findViewById(R.id.lay_do_end_date);

        puAddress.setThreshold(1);

        puAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchType = 0;
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
        puAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long id) {
                try {
                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(puAddress.getWindowToken(), 0);
                } catch (Exception e) {
                    e.toString();
                }
                try {
                    if (puAddress.isPopupShowing()) {
                        puAddress.setDropDownHeight(0);
                    }
                } catch (Exception e) {
                    e.toString();
                }
                searchType = 0;
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
        doAddress.setThreshold(1);
        doAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchType = 1;
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
        doAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long id) {
                try {
                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(doAddress.getWindowToken(), 0);
                } catch (Exception e) {
                    e.toString();
                }
                try {
                    if (doAddress.isPopupShowing()) {
                        doAddress.setDropDownHeight(0);
                    }
                } catch (Exception e) {
                    e.toString();
                }
                searchType = 1;
                ListView lv = (ListView) arg0;
                SimpleAdapter adapter = (SimpleAdapter) lv.getAdapter();
                HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);
                placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);
                String url = getPlaceDetailsUrl(hm.get("reference"));
                placeDetailsDownloadTask.execute(url);
            }
        });

        deliveryTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (deliveryTitle.getText().toString().length() > 0)
                    deliveryTitle.setText(makeFirstLetterCapitel(deliveryTitle.getText().toString()));
            }
        });
        puContactPerson.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (puContactPerson.getText().toString().length() > 0)
                    puContactPerson.setText(makeFirstLetterCapitel(puContactPerson.getText().toString()));
            }
        });
        doContactPerson.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (doContactPerson.getText().toString().length() > 0)
                    doContactPerson.setText(makeFirstLetterCapitel(doContactPerson.getText().toString()));
            }
        });


        pu_cCode = (TextView) view.findViewById(R.id.pu_cc);
        pu_cCode.setOnClickListener(this);

        do_cCode = (TextView) view.findViewById(R.id.do_cc);
        do_cCode.setOnClickListener(this);

        pu_countrypicker = (MySpinner) view.findViewById(R.id.pu_countrycode);
        pu_countrypicker.setOnItemSelectedListener(SubmitStepTwo.this);

        do_countrypicker = (MySpinner) view.findViewById(R.id.do_countrycode);
        do_countrypicker.setOnItemSelectedListener(SubmitStepTwo.this);
        CountryCodeAdapter customAdapter = new CountryCodeAdapter(getActivity(), flags, countrycode);
        pu_countrypicker.setAdapter(customAdapter);
        do_countrypicker.setAdapter(customAdapter);

        doMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (doMobile.getText().toString().trim().startsWith("03") || doMobile.getText().toString().trim().startsWith("02")
                        || doMobile.getText().toString().trim().startsWith("07") || doMobile.getText().toString().trim().startsWith("08")) {
                    if (phoneCount <= doMobile.getText().toString().length()
                            && (doMobile.getText().toString().length() == 2
                            || doMobile.getText().toString().length() == 7

                    )) {
                        doMobile.setText(doMobile.getText().toString() + " ");
                        int pos = doMobile.getText().length();
                        doMobile.setSelection(pos);
                    } else if (phoneCount >= doMobile.getText().toString().length()
                            && (doMobile.getText().toString().length() == 2
                            || doMobile.getText().toString().length() == 7

                    )) {
                        doMobile.setText(doMobile.getText().toString().substring(0, doMobile.getText().toString().length() - 1));
                        int pos = doMobile.getText().length();
                        doMobile.setSelection(pos);
                    }
                    phoneCount = doMobile.getText().toString().length();
                } else {
                    //04
                    if (phoneCount <= doMobile.getText().toString().length()
                            && (doMobile.getText().toString().length() == 4
                            || doMobile.getText().toString().length() == 8

                    )) {
                        doMobile.setText(doMobile.getText().toString() + " ");
                        int pos = doMobile.getText().length();
                        doMobile.setSelection(pos);
                    } else if (phoneCount >= doMobile.getText().toString().length()
                            && (doMobile.getText().toString().length() == 4
                            || doMobile.getText().toString().length() == 8

                    )) {
                        doMobile.setText(doMobile.getText().toString().substring(0, doMobile.getText().toString().length() - 1));
                        int pos = doMobile.getText().length();
                        doMobile.setSelection(pos);
                    }
                    phoneCount = doMobile.getText().toString().length();
                }
            }
        });

        puMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (puMobile.getText().toString().trim().startsWith("03") || puMobile.getText().toString().trim().startsWith("02")
                        || puMobile.getText().toString().trim().startsWith("07") || puMobile.getText().toString().trim().startsWith("08")) {
                    //                    03 / 02 / 07 / 08
                    if (puphoneCount <= puMobile.getText().toString().length()
                            && (puMobile.getText().toString().length() == 2
                            || puMobile.getText().toString().length() == 7

                    )) {
                        puMobile.setText(puMobile.getText().toString() + " ");
                        int pos = puMobile.getText().length();
                        puMobile.setSelection(pos);
                    } else if (puphoneCount >= puMobile.getText().toString().length()
                            && (puMobile.getText().toString().length() == 2
                            || puMobile.getText().toString().length() == 7

                    )) {
                        puMobile.setText(puMobile.getText().toString().substring(0, puMobile.getText().toString().length() - 1));
                        int pos = puMobile.getText().length();
                        puMobile.setSelection(pos);
                    }
                    puphoneCount = puMobile.getText().toString().length();
                } else {
                    //04
                    if (puphoneCount <= puMobile.getText().toString().length()
                            && (puMobile.getText().toString().length() == 4
                            || puMobile.getText().toString().length() == 8

                    )) {
                        puMobile.setText(puMobile.getText().toString() + " ");
                        int pos = puMobile.getText().length();
                        puMobile.setSelection(pos);
                    } else if (puphoneCount >= puMobile.getText().toString().length()
                            && (puMobile.getText().toString().length() == 4
                            || puMobile.getText().toString().length() == 8

                    )) {
                        puMobile.setText(puMobile.getText().toString().substring(0, puMobile.getText().toString().length() - 1));
                        int pos = puMobile.getText().length();
                        puMobile.setSelection(pos);
                    }
                    puphoneCount = puMobile.getText().toString().length();
                }
            }
        });
    }

    public void AddExtra() {
        session.saveShipmentAddress(deliveryTitle.getText().toString(),
                puContactPerson.getText().toString(), pu_countrycodestr + puMobile.getText().toString(),
                puAddress.getText().toString(), puDateTime.getText().toString(),
                puDateType, puEndDateTime.getText().toString(),
                doContactPerson.getText().toString(), do_countrycodestr + doMobile.getText().toString(),
                doAddress.getText().toString(), doDateTime.getText().toString(),
                doDateType, doEndDateTime.getText().toString(),
                hasAuctionBid, maxOpeningBid.getText().toString(),
                auctionStart.getText().toString(), auctionEnd.getText().toString(),
                fixedPrice.getText().toString(), "" + puLat, "" + puLlng, "" + doLat, "" + doLng, puCity,
                puState, puCountry, doCity, doState, doCountry, puDateTimeChoose.getText().toString(), doDateTimeChoose.getText().toString());
        String backStateName = this.getClass().getName();
        Fragment fragment = new SubmitStepThree();
        Bundle bundle = new Bundle();
        if (delivery != null) {
            HashMap<String, Delivery> data = new HashMap<String, Delivery>();
            data.put("data", delivery);
            bundle.putSerializable("data", data);
        }
        fragment.setArguments(bundle);
        getActivity().getFragmentManager().beginTransaction().add(R.id.container, fragment, backStateName)
                .addToBackStack(null)
                //.commit();
                .commitAllowingStateLoss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pu_date_end:
                showDateTimePicker(5);
                break;
            case R.id.do_date_end:
                showDateTimePicker(6);
                break;
            case R.id.pu_date_choose:
                showGrabidDialog(1);
                break;
            case R.id.do_date_choose:
                showGrabidDialog(2);
                break;
            case R.id.submit:
                if (isValid())
                    AddExtra();
                break;
            case R.id.pu_date:
                showDateTimePicker(1);
                break;
            case R.id.do_date:
                showDateTimePicker(2);
                break;
            case R.id.a_start_time:
                showDateTimePicker(3);
                break;
            case R.id.a_end_time:
                showDateTimePicker(4);
                break;
            case R.id.pu_cc:
                pu_countrypicker.performClick();
                break;
            case R.id.do_cc:
                do_countrypicker.performClick();
                break;
        }
    }

    private void showMessage(String message) {
        AlertManager.messageDialog(getActivity(), "Alert!", message);
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


    private boolean isValid() {
        try {
            if ((deliveryTitle.getText().toString().trim().length() == 0) && puContactPerson.getText().toString().trim().length() == 0
                    && puMobile.getText().toString().trim().length() == 0 && pu_cCode.getText().toString().trim().length() == 0
                    && puAddress.getText().toString().trim().length() == 0 && puDateTimeChoose.getText().toString().trim().length() == 0
                    && puDateTime.getText().toString().trim().length() == 0 && doContactPerson.getText().toString().trim().length() == 0
                    && doMobile.getText().toString().trim().length() == 0 && doAddress.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.completeallfield));
                return false;
            }
            if (deliveryTitle.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.deliveryrefname));
                return false;
            } else if (puContactPerson.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.pperson));
                return false;
            } else if (puMobile.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.ppersonnumber));
                return false;
            } else if (lengthAbn(puMobile.getText().toString()) < 9) {
                showMessage(getActivity().getResources().getString(R.string.validpicknumber));
                return false;
            } else if (pu_cCode.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.pickupcc));
                return false;
            } else if (puAddress.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.pickupaddress));
                return false;
            } else if (TextUtils.isEmpty(puCountry)) {
                showMessage(getActivity().getResources().getString(R.string.validpickaddress));
                return false;
            } else if (puDateTimeChoose.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.pickdatetimeype));
                return false;
            } else if (puDateTime.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.pickupdatetime));
                return false;
            } /*else if (puDate.before(new Date())) {
                showMessage("Pickup day must be greater than current day/time");
                return false;
            }*/
            if (!mPickupDiff.contentEquals("")) {
                try {
                    int durationint = Integer.parseInt(mPickupDiff);
                    if ((puDate.getTime() - new Date().getTime()) < durationint * 60 * 1000) {
                        showMessage("Pick Up time must be at least " + mPickupDiff + " minutes later than current time. Please change accordingly.");
                        return false;
                    }
                } catch (Exception e) {
                    e.toString();
                }
            } else {
                getDuration(true);
                return false;
            }
            if (puDateTimeChoose.getText().toString().equalsIgnoreCase("between")) {
                if (puEndDateTime.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.pickupenddatetime));
                    return false;
                } else if (puendate.before(puDate) || puendate.equals(puDate)) {
                    showMessage(getActivity().getResources().getString(R.string.pickupdategreatstartdate));
                    return false;
                }
            }

            if (doContactPerson.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.dropoffcontactpersonname));
                return false;
            } else if (doMobile.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.dropoffpersonnumber));
                return false;
            } else if (lengthAbn(doMobile.getText().toString()) < 9) {
                showMessage(getActivity().getResources().getString(R.string.validdropoffnumber));
                return false;
            } else if (do_cCode.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.dropcc));
                return false;
            } else if (doAddress.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.dropoffaddress));
                return false;
            } else if (TextUtils.isEmpty(doCountry)) {
                showMessage(getActivity().getResources().getString(R.string.validdropaddress));
                return false;
            } else if (doDateTimeChoose.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.dropdaytimetype));
                return false;
            } else if (doDateTime.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.selectdropdate));
                return false;
            } else if (duDate.before(new Date())) {
                showMessage(getActivity().getResources().getString(R.string.dropmustgreatercurrent));
                return false;
            }
            if (doDateTimeChoose.getText().toString().equalsIgnoreCase("between")) {
                if (doEndDateTime.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.dropenddaytime));
                    return false;
                } else if (doenddate.before(duDate) || doenddate.equals(duDate)) {
                    showMessage(getActivity().getResources().getString(R.string.droptimemustgreaterstarttime));
                    return false;
                }
            }

            /*if (duDate.before(puDate) || duDate.equals(puDate)) {
                showMessage("Drop off day / time must be later than pick up day / time");
                return false;
            }
            */
            if (!mPickDropdiff.contentEquals("")) {
                try {
                    int durationint = Integer.parseInt(mPickDropdiff);
                    if ((duDate.getTime() - puDate.getTime()) < durationint * 60 * 1000) {
                        showMessage("Drop off time must be at least " + mPickDropdiff + " minutes later than pick up time. Please change accordingly.");
                        return false;
                    }
                } catch (Exception e) {
                    e.toString();
                }
            } else {
                getDuration(true);
                return false;
            }


            if (puDateTimeChoose.getText().toString().equalsIgnoreCase("between")) {
               /* if (duDate.before(puendate) || duDate.equals(puendate)) {
                    showMessage("Drop off day / time must be later than pick up end day / time");
                    return false;
                }
               */
                if (!mPickDropdiff.contentEquals("")) {
                    try {
                        int durationint = Integer.parseInt(mPickDropdiff);
                        if ((duDate.getTime() - puendate.getTime()) < durationint * 60 * 1000) {
                            showMessage("Drop off time must be at least " + mPickDropdiff + " minutes later than pick up end time. Please change accordingly.");
                            return false;
                        }
                    } catch (Exception e) {
                        e.toString();
                    }
                }
            }
            if (auctionBid.getCheckedRadioButtonId() == -1) {
                showMessage(getActivity().getResources().getString(R.string.auction_bid));
                return false;
            }
            try {
                if (auctionBid.getCheckedRadioButtonId() == R.id.o_yes) {

                    if (maxOpeningBid.getText().toString().trim().length() == 0) {
                        showMessage(getActivity().getResources().getString(R.string.maximumopeningbid));
                        return false;
                    } else if (!checkZero(maxOpeningBid.getText().toString())) {
                        showMessage(getActivity().getResources().getString(R.string.validmaximumopeningbid));
                        return false;
                    } else if (auctionStart.getText().toString().trim().length() == 0) {
                        showMessage(getActivity().getResources().getString(R.string.auctionstartdatetime));
                        return false;
                    } else if (auStDate.before(new Date())) {
                        showMessage(getActivity().getResources().getString(R.string.auctionstartmustbegreatercurrent));
                        return false;
                    } else if (auctionEnd.getText().toString().trim().length() == 0) {
                        showMessage(getActivity().getResources().getString(R.string.auctionendtime));
                        return false;
                    } else if (auStDate.after(auenDate) || auStDate.equals(auenDate)) {
                        showMessage(getActivity().getResources().getString(R.string.auctionendgreaterthenauctionstart));
                        return false;
                    } else if (auStDate.after(puDate) || auStDate.equals(puDate)) {
                        showMessage(getActivity().getResources().getString(R.string.althenpickuptime));
                        return false;
                    } else if (Utils.calculateDifference(puDate, auenDate) < 60) {
                        showMessage(getActivity().getResources().getString(R.string.onehourlater));
                        return false;
                    } else if (auenDate.after(puDate) || auenDate.equals(puDate)) {
                        showMessage(getActivity().getResources().getString(R.string.alpickuptime));
                        return false;
                    }
                    if (!duration.contentEquals("")) {
                        try {
                            int durationint = Integer.parseInt(duration);
                            if ((auenDate.getTime() - auStDate.getTime()) < durationint * 60 * 1000) {
                                showMessage("Auction length of time must be at least " + duration + " minutes. Please change the end time to at least " + duration + " minutes from the start time.");
                                return false;
                            }
                        } catch (Exception e) {
                            e.toString();
                        }
                    } else {
                        getDuration(true);
                        return false;
                    }

                }
              /*  if (!duration.contentEquals("")) {
                    if (!mCreditCardDetail) {
                        AlertManager.BankmessageDialog((HomeActivity) getActivity(), "Alert!", getActivity().getResources().getString(R.string.creditcarddecline), this.getClass().getName(), "5");
                        return false;
                    }
                } else {
                    getDuration(true);
                    return false;
                }*/
            } catch (Exception e) {
                e.toString();
            }
            if (auctionBid.getCheckedRadioButtonId() == R.id.o_no) {
                if (fixedPrice.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.fixedprice));
                    return false;
                }
                if (!checkZero(fixedPrice.getText().toString())) {
                    showMessage(getActivity().getResources().getString(R.string.validfixedprice));
                    return false;
                }
            }
            if (!(pu_cCode.getText().toString().trim().length() == 0)) {
                if (pu_countrycodestr.contentEquals(""))
                    pu_countrycodestr = pu_cCode.getText().toString();
            }
            if (!(do_cCode.getText().toString().trim().length() == 0)) {
                if (do_countrycodestr.contentEquals(""))
                    do_countrycodestr = do_cCode.getText().toString();
            }
        } catch (
                Exception e)

        {
            e.toString();
        }
        return true;
    }

    public Boolean checkZero(String val) {
        if (val.matches("[0]+"))
            return false;
        return true;
    }

    RadioGroup.OnCheckedChangeListener AuctionBidListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            if (checkedId == R.id.o_yes) {
                hasAuctionBid = true;
                auctionNo.setVisibility(View.GONE);
                auctionYes.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.o_no) {
                hasAuctionBid = false;
                auctionYes.setVisibility(View.GONE);
                auctionNo.setVisibility(View.VISIBLE);
            }
        }
    };
    boolean hasAuctionBid = false;

    private void showDateTimePicker(final int type) {
        final View dialogView = View.inflate(getActivity(), R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        try {
            TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
            timePicker.setIs24HourView(false);
            Calendar now = Calendar.getInstance();
            Calendar tmp = (Calendar) now.clone();
            tmp.add(Calendar.HOUR_OF_DAY, 0);
            tmp.add(Calendar.MINUTE, 10);
            Calendar nowPlus10Minutes = tmp;
            timePicker.setCurrentHour(nowPlus10Minutes.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(nowPlus10Minutes.get(Calendar.MINUTE));
            Log.v("", "");
        } catch (Exception e) {
            e.toString();
        }

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDatePicker datePicker = (CustomDatePicker) dialogView.findViewById(R.id.date_picker);

                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
                timePicker.setIs24HourView(false);
                int hour, min;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hour = timePicker.getHour();
                    min = timePicker.getMinute();
                } else {
                    hour = timePicker.getCurrentHour();
                    min = timePicker.getCurrentMinute();
                }

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        hour,
                        min);

                String datetime = "";
                datetime = datetime + calendar.get(Calendar.YEAR) + "-" +
                        String.format("%02d", (calendar.get(Calendar.MONTH) + 1)) + "-" +
                        String.format("%02d", (calendar.get(Calendar.DAY_OF_MONTH)));

                datetime = datetime + " " + getFormattedTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

                setDateTime(datetime, type);
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

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

    private String getFormattedTimeFromApi(int hourOfDay, int mnts) {
        String ampm = "";
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

    Date puDate, duDate, auStDate, auenDate, doenddate, puendate;

    private void setDateTime(String datetime, int type) {
        switch (type) {
            case 1:
                puDateTime.setText(datetime);
                SimpleDateFormat pusdf = new SimpleDateFormat("yyyy-MM-dd hh:mm aa", Locale.US);
                try {
                    puDate = pusdf.parse(puDateTime.getText().toString());
                } catch (Exception ex) {
                    ex.toString();
                }
                break;
            case 2:
                doDateTime.setText(datetime);
                SimpleDateFormat dusdf = new SimpleDateFormat("yyyy-MM-dd hh:mm aa", Locale.US);
                try {
                    duDate = dusdf.parse(doDateTime.getText().toString());
                } catch (Exception ex) {
                    ex.toString();
                }
                break;
            case 3:
                auctionStart.setText(datetime);
                SimpleDateFormat auctionstartsdf = new SimpleDateFormat("yyyy-MM-dd hh:mm aa", Locale.US);
                try {
                    auStDate = auctionstartsdf.parse(auctionStart.getText().toString());
                } catch (Exception ex) {
                    ex.toString();
                }
                break;
            case 4:
                auctionEnd.setText(datetime);
                SimpleDateFormat auctionendsdf = new SimpleDateFormat("yyyy-MM-dd hh:mm aa", Locale.US);
                try {
                    auenDate = auctionendsdf.parse(auctionEnd.getText().toString());
                } catch (Exception ex) {
                    ex.toString();
                }
                break;
            case 5:
                puEndDateTime.setText(datetime);
                SimpleDateFormat puendf = new SimpleDateFormat("yyyy-MM-dd hh:mm aa", Locale.US);
                try {
                    puendate = puendf.parse(puEndDateTime.getText().toString());
                } catch (Exception ex) {
                    ex.toString();
                }
                break;
            case 6:
                doEndDateTime.setText(datetime);
                SimpleDateFormat doendf = new SimpleDateFormat("yyyy-MM-dd hh:mm aa", Locale.US);
                try {
                    doenddate = doendf.parse(doEndDateTime.getText().toString());
                } catch (Exception ex) {
                    ex.toString();
                }
                break;
        }
    }

    private void getDuration(boolean IsClicked) {
        this.IsClicked = IsClicked;
        String url = Config.SERVER_URL + Config.DURATION;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    //String key = "key=AIzaSyDxBqk-VhEklzJyUW3cQ1PE9tsbFQQPYWo";
    // Fetches all places from GooglePlaces AutoComplete Web Service

    private String getAutoCompleteUrl(String place) {
        String key = getActivity().getResources().getString(R.string.searckey);
        ;
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

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);

    }

    private void handleResponse(String result) {
        try {
            JSONObject outterJson = new JSONObject(result);
            JSONObject dataObj = outterJson.getJSONObject(Config.DATA);
            duration = dataObj.optString(Keys.DURATION);
            mCreditCardDetail = dataObj.optBoolean(Keys.CREDITCARDDETAIL);
            mPickDropdiff = dataObj.optString(Keys.DROP_DIFFERENCE);
            mPickupDiff = dataObj.optString(Keys.PICK_UP_DIFFERENCE);
            if (IsClicked) {
                if (isValid())
                    AddExtra();
            }
        } catch (Exception e) {
            e.toString();
        }
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

    double puLat, puLlng, doLat, doLng;
    String puCity, puState, puCountry, doCity, doState, doCountry;

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
                        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);
                        Log.v("from", "from" + from);
                        if (searchType == 0) {
                            doAddress.clearFocus();
                            puAddress.setAdapter(adapter);
                            puAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (hasFocus) {
                                        try {
                                            puAddress.setDropDownHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                                            puAddress.showDropDown();
                                        } catch (Exception e) {
                                            e.toString();
                                        }
                                    }

                                }
                            });

                            puAddress.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    try {
                                        puAddress.setDropDownHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                                        puAddress.showDropDown();
                                    } catch (Exception e) {
                                        e.toString();
                                    }
                                    return false;
                                }
                            });
                            try {
                                puAddress.showDropDown();
                            } catch (Exception e) {
                                e.toString();

                            }
                        } else if (searchType == 1) {
                            puAddress.clearFocus();
                            doAddress.setAdapter(adapter);
                            doAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (hasFocus) {
                                        try {
                                            doAddress.setDropDownHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                                            doAddress.showDropDown();
                                        } catch (Exception e) {
                                            e.toString();
                                        }
                                    }
                                }
                            });

                            doAddress.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    try {
                                        doAddress.setDropDownHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                                        doAddress.showDropDown();
                                    } catch (Exception e) {
                                        e.toString();
                                    }
                                    return false;
                                }
                            });
                            try {
                                doAddress.showDropDown();
                            } catch (Exception e) {
                                e.toString();
                            }


                        }
                    } catch (Exception ex) {

                    }
                    break;
                case PLACES_DETAILS:
                    try {
                        HashMap<String, String> hm = result.get(0);
                        if (searchType == 0) {
                            puLat = Double.parseDouble(hm.get("lat"));
                            puLlng = Double.parseDouble(hm.get("lng"));
                            getAddress(puLat, puLlng);
                        } else if (searchType == 1) {
                            doLat = Double.parseDouble(hm.get("lat"));
                            doLng = Double.parseDouble(hm.get("lng"));
                            getAddress(doLat, doLng);
                        }
                    } catch (Exception ex) {

                    }
                    break;
            }
        }

    }

    String puDateType, doDateType;

    public void showGrabidDialog(final int type) {
        final Dialog mDialog = new Dialog(getActivity(), R.style.GrabidDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list_new);
        mDialog.setCanceledOnTouchOutside(true);
        final TextView title = (TextView) mDialog.findViewById(R.id.title);
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
            title.setText("Please Select");
        else if (type == 2)
            title.setText("Please Select");

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
            adapter = new ArrayAdapter<>(getActivity(),
                    R.layout.dialog_textview, R.id.textItem, getPickUpList());
        else
            adapter = new ArrayAdapter<>(getActivity(),
                    R.layout.dialog_textview, R.id.textItem, getDropOffList());

        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String value = parent.getItemAtPosition(position).toString();
                if (type == 1) {
                    puDateTimeChoose.setText(value);
                    if (value.equalsIgnoreCase("between")) {
                        layPUEndDate.setVisibility(View.VISIBLE);
                    } else {
                        layPUEndDate.setVisibility(View.GONE);
                    }
                    if (value.equalsIgnoreCase("after"))
                        puDateType = "2";
                    if (value.equalsIgnoreCase("before"))
                        puDateType = "3";
                    if (value.equalsIgnoreCase("between"))
                        puDateType = "4";
                    else if (value.equalsIgnoreCase("on"))
                        puDateType = "1";
                    puDateTime.setText("");
                    puEndDateTime.setText("");
                } else if (type == 2) {
                    doDateTimeChoose.setText(value);
                    if (value.equalsIgnoreCase("between")) {
                        layDOEndDate.setVisibility(View.VISIBLE);
                    } else {
                        layDOEndDate.setVisibility(View.GONE);
                    }
                    if (value.equalsIgnoreCase("before"))
                        doDateType = "3";
                    if (value.equalsIgnoreCase("between"))
                        doDateType = "4";
                    else if (value.equalsIgnoreCase("on"))
                        doDateType = "1";
                    doDateTime.setText("");
                    doEndDateTime.setText("");
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public String[] getPickUpList() {
        return getActivity().getResources().getStringArray(R.array.choose_pu_date_time);
    }

    public String[] getDropOffList() {
        return getActivity().getResources().getStringArray(R.array.choose_do_date_time);
    }

    public String makeFirstLetterCapitel(String input) {
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

    private void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                // Here are some results you can geocode
                String ZIP = "";
                String city = "";
                String state = "";
                String country = "";

                if (addresses.get(0).getPostalCode() != null) {
                    ZIP = addresses.get(0).getPostalCode();
                    Log.d("ZIP", ZIP);
                }

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
                if (searchType == 1) {
                    doCity = city;
                    if (state.equals(""))
                        doState = city;
                    else
                        doState = state;
                    doCountry = country;
                } else {
                    puCity = city;
                    if (state.equals(""))
                        puState = city;
                    else
                        puState = state;
                    puCountry = country;
                    Log.v("pucountry", puCountry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.v("onItemSelected", "Id: " + parent.getId());
        switch (parent.getId()) {
            case R.id.pu_countrycode:
                if (!getArguments().containsKey("data")) {
                    pu_countrycodestr = countrycode[position];
                    pu_cCode.setText(pu_countrycodestr);
                } else if (++checkpu > 1) {
                    Log.v("tag", countrycode[position]);
                    pu_countrycodestr = countrycode[position];
                    pu_cCode.setText(pu_countrycodestr);
                }
                break;
            case R.id.do_countrycode:
                if (!getArguments().containsKey("data")) {
                    do_countrycodestr = countrycode[position];
                    do_cCode.setText(do_countrycodestr);
                } else if (++checkdo > 1) {
                    Log.v("tag", countrycode[position]);
                    do_countrycodestr = countrycode[position];
                    do_cCode.setText(do_countrycodestr);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
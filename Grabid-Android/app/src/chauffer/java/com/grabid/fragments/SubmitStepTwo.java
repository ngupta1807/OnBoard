package com.grabid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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
import android.widget.TimePicker;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.activities.FavoriteGroupSelectionList;
import com.grabid.activities.VehicleListSubmitMultiple;
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
import com.grabid.models.Category;
import com.grabid.models.Delivery;
import com.grabid.models.Jobs;
import com.grabid.models.PreviewField;
import com.grabid.util.PlaceDetailsJSONParser;
import com.grabid.util.PlaceJSONParser;
import com.grabid.views.BoldAutoCompleteTextView;
import com.grabid.views.CustomDatePicker;
import com.grabid.views.MySpinner;

import org.json.JSONArray;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.grabid.R.color.top_bar_title_color;

/**
 * Created by vinod on 10/14/2016.
 */
public class SubmitStepTwo extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, AsyncTaskCompleteListener {
    final int PLACES = 0;
    final int PLACES_DETAILS = 1;
    int searchType = 0;
    TextView submit, suitableVehicle, puDateTimeChoose, doDateTimeChoose, puDateTime, doEndDateTime, puEndDateTime, doDateTime, auctionStart, auctionEnd;
    SessionManager session;
    EditText deliveryTitle, puContactPerson, puMobile;
    EditText maxOpeningBid, fixedPrice;
    RadioGroup auctionBid;
    LinearLayout auctionYes, auctionNo, layPUEndDate, layDOEndDate;
    BoldAutoCompleteTextView puAddress, doAddress;
    ParserTask placesParserTask;
    ParserTask placeDetailsParserTask;
    DownloadTask placesDownloadTask;
    DownloadTask placeDetailsDownloadTask;
    Delivery delivery = null;
    //TextView pu_cCode;
    MySpinner pu_countrypicker;
    String[] countrycode = {"+61", "+91"};
    public static String pu_countrycodestr = "";
    int[] flags = {R.drawable.au, R.drawable.in};
    int checkpu = 0;
    int type;
    EditText geo, puSpecialRestriction;
    RadioGroup geoZone;
    TextView mAuctionBidTxt;
    RelativeLayout mRelativeAuction;
    String vehicleTypeID = "";
    ImageView pu_cCode;
    LinearLayout mcClinear;
    String user_Group = "";
    String duration = "", mPickupDiff = "";
    boolean IsClicked = false;
    boolean mCreditCardDetail = true;
    ArrayList<Category> favouritegroupsdata = new ArrayList<>();
    String mUser_Group = "";
    boolean hasGroups = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.title.setText(getResources().getString(R.string.submitjobs));
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.seagreen));
        HomeActivity.title.setTextColor(getResources().getColor(top_bar_title_color));
        if (getArguments().containsKey("home"))
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_white);
        else
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
        View view = inflater.inflate(R.layout.shipment_address, null);
        init(view);
        if (getArguments().containsKey("data"))
            appendData(view);
        getSuitableVehicle();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments().containsKey("home"))
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_white);
        else
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
    }

    @Override
    public void onStop() {
        super.onStop();
        //  HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
    }

    private void getDuration(boolean IsClicked) {
        this.IsClicked = IsClicked;
        type = 5;
        String url = Config.SERVER_URL + Config.DURATION;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void getSuitableVehicle() {
        type = 2;
        String url = Config.SERVER_URL + Config.SUITABLE_VEHICLE;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    public void showFavouriteSelect() {
        //   user_Group = "";
        final Dialog mDialog = new Dialog(getActivity());
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //   mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.favouriteselectiondialog);
        mDialog.setCanceledOnTouchOutside(true);
        TextView selectgroup = mDialog.findViewById(R.id.selectgroup);
        TextView cancel = mDialog.findViewById(R.id.cancel);
        final CheckBox check1 = mDialog.findViewById(R.id.check1);
        final CheckBox check2 = mDialog.findViewById(R.id.check2);
        final CheckBox check3 = mDialog.findViewById(R.id.check3);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway_SemiBold.ttf");
        check1.setTypeface(face);
        check2.setTypeface(face);
        check3.setTypeface(face);
        if (user_Group != null && !user_Group.contentEquals("")) {
            if (user_Group.contentEquals("1")) {
                check1.setChecked(true);
                check2.setChecked(false);
                check3.setChecked(false);
            } else if (user_Group.contentEquals("2")) {
                check1.setChecked(false);
                check2.setChecked(true);
                check3.setChecked(false);
            } else {
                check1.setChecked(false);
                check2.setChecked(false);
                check3.setChecked(true);
            }
        }
        check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    check2.setChecked(false);
                    check3.setChecked(false);
                }
            }
        });
        check2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    check1.setChecked(false);
                    check3.setChecked(false);
                }
            }
        });
        check3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    check1.setChecked(false);
                    check2.setChecked(false);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        selectgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check1.isChecked()) {
                    mDialog.dismiss();
                    user_Group = "1";
                    // submitJob();
                    getfavourites();
                } else if (check2.isChecked()) {
                    mDialog.dismiss();
                    user_Group = "2";
                    // submitJob();
                    getfavourites();
                } else if (check3.isChecked()) {
                    mDialog.dismiss();
                    user_Group = "3";
                    submitJob("sendall");
                } else
                    showMessage(getResources().getString(R.string.pleasetick));

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
        mDialog.show();
    }


    public void UpdateDesign() {
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.title.setText(getResources().getString(R.string.submitjobs));
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.seagreen));
        HomeActivity.title.setTextColor(getResources().getColor(top_bar_title_color));
        if (getArguments().containsKey("home"))
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_white);
        else
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
    }

    private void appendData(View view) {
        HashMap<String, Delivery> map = (HashMap<String, Delivery>) getArguments().getSerializable("data");
        Delivery delivery = map.get("data");
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
        try {
            suitableVehicle.setText(delivery.getSuitableVehicles().replaceAll("(?!\\s)\\W", "$0 "));
        } catch (Exception e) {
            e.toString();
        }

        vehicleTypeID = delivery.getSuitabelVehicle();
        puContactPerson.setText(delivery.getPuContactPerson());
        puMobile.setText(delivery.getPuMobile());
        String pu_mobile = delivery.getPuMobile();
        puSpecialRestriction.setText(delivery.getPickupSpecialRestriction());
        if (pu_mobile != null && !pu_mobile.contentEquals("")) {
            try {
                String firstCharacter = pu_mobile.substring(0, 1);
                if (firstCharacter != null && firstCharacter.contentEquals("0")) {
                    String s = pu_mobile;
                    pu_mobile = s.replaceFirst("^0*", "");
                    if (s.isEmpty()) s = "0";
                    pu_countrypicker.setSelection(0);
                    pu_cCode.setImageDrawable(getResources().getDrawable(R.drawable.au));
                    pu_cCode.setTag("au");
                    // pu_cCode.setText("+61");
                    puMobile.setText(pu_mobile);
                } else if (pu_mobile.length() > 3) {
                    String first = pu_mobile.substring(0, 3);
                    String last = pu_mobile.substring(3, pu_mobile.length());
                    // String last = mobile.substring(mobile.length() - 1, mobile.length());
                    if (first.contentEquals("+91")) {
                        pu_countrypicker.setSelection(1);
                        pu_cCode.setImageDrawable(getResources().getDrawable(R.drawable.in));
                        pu_cCode.setTag("in");
                        //  pu_cCode.setText(first);
                        puMobile.setText(last);
                    } else if (first.contentEquals("+61")) {
                        pu_countrypicker.setSelection(0);
                        //  pu_cCode.setText(first);
                        pu_cCode.setImageDrawable(getResources().getDrawable(R.drawable.au));
                        pu_cCode.setTag("au");
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
        //   setDateTime(delivery.getDropOffDate(), 2);
        puAddress.setText(delivery.getPickUpAddress());
        doAddress.setText(delivery.getDropoffAdress());
        if (session.getJobs().getAsap().equals("1")) {
            //autofillDate();
            setDateTime(delivery.getPickUpDate(), 1);
            mAuctionBidTxt.setVisibility(View.GONE);
            mRelativeAuction.setVisibility(View.GONE);
            auctionNo.setVisibility(View.VISIBLE);
            if (delivery.getAuctionBid().equals("3")) {
                RadioButton btn = (RadioButton) view.findViewById(R.id.o_no);
                btn.setChecked(true);
                auctionYes.setVisibility(View.GONE);
                auctionNo.setVisibility(View.VISIBLE);
                fixedPrice.setText(delivery.getFixedOffer());
            }
        } else {
            setDateTime(delivery.getPickUpDate(), 1);
            mAuctionBidTxt.setVisibility(View.VISIBLE);
            mRelativeAuction.setVisibility(View.VISIBLE);
            if (delivery.getAuctionBid().equals("2")) {
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
        }
        fillData(delivery);
        if (delivery.getGeo().equals("1")) {
            RadioButton btn = (RadioButton) view.findViewById(R.id.yes);
            btn.setChecked(true);
            isGEOZoneEnable = true;
            geo.setVisibility(View.VISIBLE);
            geo.setText(delivery.getRadius());
        } else {
            RadioButton btn = (RadioButton) view.findViewById(R.id.no);
            btn.setChecked(true);
            isGEOZoneEnable = false;
            geo.setVisibility(View.GONE);
        }
        mUser_Group = delivery.getFav_user_id();
        user_Group = delivery.getUser_Group();
       /* if (session.getCurrentScreen().equals("edit") || session.getCurrentScreen().equals("editRelisting")) {
            submit.setText("UPDATE TRANSFER");
        } else
            submit.setText("SUBMIT TRANSFER");*/
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
        doAddress = (BoldAutoCompleteTextView) view.findViewById(R.id.do_address);

        doDateTime = (TextView) view.findViewById(R.id.do_date);
        doDateTime.setOnClickListener(this);
        mcClinear = (LinearLayout) view.findViewById(R.id.cclinear);
        mcClinear.setOnClickListener(this);
        suitableVehicle = (TextView) view.findViewById(R.id.suitable_vehicle);
        suitableVehicle.setOnClickListener(this);
        puDateTimeChoose = (TextView) view.findViewById(R.id.pu_date_choose);
        puDateTimeChoose.setOnClickListener(this);
        doDateTimeChoose = (TextView) view.findViewById(R.id.do_date_choose);
        doDateTimeChoose.setOnClickListener(this);
        puEndDateTime = (TextView) view.findViewById(R.id.pu_date_end);
        puEndDateTime.setOnClickListener(this);
        doEndDateTime = (TextView) view.findViewById(R.id.do_date_end);
        doEndDateTime.setOnClickListener(this);
        mAuctionBidTxt = (TextView) view.findViewById(R.id.auctionbidtxt);
        mRelativeAuction = (RelativeLayout) view.findViewById(R.id.auctionbidrelative);
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
        puSpecialRestriction = (EditText) view.findViewById(R.id.pu_special_restriction);
        layPUEndDate = (LinearLayout) view.findViewById(R.id.lay_pu_end_date);
        layDOEndDate = (LinearLayout) view.findViewById(R.id.lay_do_end_date);
        geoZone = (RadioGroup) view.findViewById(R.id.geo_zone);
        geoZone.setOnCheckedChangeListener(GEOZoneListener);
        geo = (EditText) view.findViewById(R.id.geoRadius);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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


        pu_cCode = (ImageView) view.findViewById(R.id.pu_cc);
        pu_cCode.setOnClickListener(this);
        pu_countrypicker = (MySpinner) view.findViewById(R.id.pu_countrycode);
        pu_countrypicker.setOnItemSelectedListener(SubmitStepTwo.this);
        CountryCodeAdapter customAdapter = new CountryCodeAdapter(getActivity(), flags, countrycode);
        pu_countrypicker.setAdapter(customAdapter);


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
        if (session.getJobs().getAsap().equals("1"))
            autofillDate();
        else {
            mAuctionBidTxt.setVisibility(View.VISIBLE);
            mRelativeAuction.setVisibility(View.VISIBLE);
        }

    }

    public void autofillDate() {
        Calendar now = Calendar.getInstance();
        Calendar tmp = (Calendar) now.clone();
        tmp.add(Calendar.HOUR_OF_DAY, 0);
        tmp.add(Calendar.MINUTE, 10);
        Calendar nowPlus10Minutes = tmp;
        String datetime = "";
        datetime = datetime + nowPlus10Minutes.get(Calendar.YEAR) + "-" +
                String.format("%02d", (nowPlus10Minutes.get(Calendar.MONTH) + 1)) + "-" +
                String.format("%02d", (nowPlus10Minutes.get(Calendar.DAY_OF_MONTH)));
        datetime = datetime + " " + getFormattedTime(nowPlus10Minutes.get(Calendar.HOUR_OF_DAY), nowPlus10Minutes.get(Calendar.MINUTE));
        setDateTime(datetime, 1);
        mAuctionBidTxt.setVisibility(View.GONE);
        mRelativeAuction.setVisibility(View.GONE);
        auctionNo.setVisibility(View.VISIBLE);

    }

    boolean isGEOZoneEnable = false;
    RadioGroup.OnCheckedChangeListener GEOZoneListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            if (checkedId == R.id.yes) {
                isGEOZoneEnable = true;
                geo.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.no) {
                isGEOZoneEnable = false;
                geo.setVisibility(View.GONE);
            }
        }
    };


    public void submitJob(String user_Groupp) {
        HashMap<String, String> params = new HashMap<>();
        com.grabid.models.ShipmentAddress address = session.getShipmentAddress();
        Jobs jobs = session.getJobs();
        params.put(Keys.USER_GROUP, user_Group);
        params.put(Keys.ITEM_DELIVERY_TITLE, deliveryTitle.getText().toString());
        params.put(Keys.ITEM_TYPE, jobs.getJobType());
        params.put(Keys.ITEM_QTY, jobs.getTotalqty());
        params.put(Keys.PICK_UP_TIME_TYPE, jobs.getAsap());
        params.put(Keys.DELIVERY_TYPE_ID_ROUND_TRIP, jobs.getRoundTrip());
        //params.put(Keys.KEY_USER_ID,session.getUserDetails().getId());
        params.put(Keys.job_PICK_UP_DATE_TIME, puDateTime.getText().toString());
        params.put(Keys.PICK_UP_MOBILE_VIRTUAL, pu_countrycodestr + puMobile.getText().toString());
        params.put(Keys.PICKUP_ADDRESS, puAddress.getText().toString());
        params.put(Keys.PICKUP_COUNTRY, puCountry);
        params.put(Keys.PICKUP_STATE, puState);
        params.put(Keys.PICKUP_CITY, puCity);
        params.put(Keys.PICKUP_CONTACT_PERSON, puContactPerson.getText().toString());
        params.put(Keys.PICKUP_MOBILE, pu_countrycodestr + puMobile.getText().toString());
        params.put(Keys.DROPOFF_ADDRESS, doAddress.getText().toString());
        params.put(Keys.DROPOFF_COUNTRY, doCountry);
        params.put(Keys.DROPOFF_STATE, doState);
        params.put(Keys.DROPOFF_CITY, doCity);
        params.put(Keys.SUITABLE_VEHICLE, vehicleTypeID);
        params.put(Keys.PICKUP_SPECIAL_RESTRICTION, puSpecialRestriction.getText().toString());
        params.put(Keys.AUCTION_BID, hasAuctionBid ? "2" : "3");
        if (jobs.getJobType() != null && jobs.getJobType().contentEquals("2"))
            params.put(Keys.PAYMENT_MODE, jobs.getPaymentmode());

        if (hasAuctionBid) {
            params.put(Keys.BID_DATE_TIME1, auctionStart.getText().toString());
            params.put(Keys.BID_DATE_TIME2, auctionEnd.getText().toString());
            params.put(Keys.MAX_AUCTION_BID, maxOpeningBid.getText().toString());
        } else {
            params.put(Keys.FIXED_OFFER, fixedPrice.getText().toString());
        }
        if (user_Groupp != null && !user_Groupp.contentEquals("sendall"))
            params.put(Keys.FAVOURITE_USER_GROUP_IDS, user_Groupp);
        params.put(Keys.PICKUP_LATITUDE, "" + puLat);
        params.put(Keys.PICKUP_LONGITUDE, "" + puLlng);
        params.put(Keys.DROPOFF_LATITUDE, "" + doLat);
        params.put(Keys.DROPOFF_LONGITUDE, "" + doLng);
        if (isGEOZoneEnable) {
            params.put(Keys.GEO_ZONE, "1");
            params.put(Keys.RADIUS, geo.getText().toString());
        } else
            params.put(Keys.GEO_ZONE, "2");
        String url = "";
        if (submit.getText().toString().contains("UPDATE")) {
            if (session.getCurrentScreen().equals("editRelisting"))
                url = Config.SERVER_URL + Config.JOB + "/relist" + "?id=" + delivery.getId();
            else {
                url = Config.SERVER_URL + Config.JOBS + "/1";
                params.put(Keys.ID, delivery.getId());
            }
        } else
            url = Config.SERVER_URL + Config.JOBS;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall apiCall;
            if (submit.getText().toString().contains("UPDATE")) {
                type = 3;
                apiCall = new RestAPICall(getActivity(), HTTPMethods.PUT, this, params);
            } else {
                type = 1;
                apiCall = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            }
            // apiCall.execute(url, session.getToken());

        } else {
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
        }
        PreviewField.setSuiotableVehicles(suitableVehicle.getText().toString());
        if (session.getCurrentScreen().equals("edit") || session.getCurrentScreen().equals("editRelisting")) {
            PreviewDelivery(params, delivery.getId());
        } else {
            session.saveCurrentScreen("copy");
            PreviewDelivery(params, "");
        }
        // PreviewDelivery(params);
    }

    public void getfavourites() {
        Intent intent = new Intent(getActivity(), FavoriteGroupSelectionList.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("IsPageNation", false);
        bundle.putString("data", mUser_Group);
        intent.putExtras(bundle);
        startActivityForResult(intent, 16);
        /*if (favouritegroupsdata.size() > 0)
            showMultipleGroupDialog();
        else {
            type = 15;
            String url;
            url = Config.SERVER_URL + Config.GET_FAVOURITE_GROUP_LIST;
            if (Internet.hasInternet(getActivity())) {
                RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
                mobileAPI.execute(url, session.getToken());
            } else
                AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
        }*/
    }


    public void PreviewDelivery(HashMap<String, String> params, String id) {
        String backStateName = this.getClass().getName();
        Bundle bundle = new Bundle();
        bundle.putSerializable("hashmap", params);
        bundle.putString("id", id);
        Fragment fragment = new PreviewTransfer();
        fragment.setArguments(bundle);
        getActivity().getFragmentManager().beginTransaction().add(R.id.container, fragment, backStateName)
                .addToBackStack(null)
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
                getValidDetail();
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
            case R.id.cclinear:
                pu_countrypicker.performClick();
                break;
            case R.id.suitable_vehicle:
                //showVehicleTypeDialog();
                showVehicleDialog(0);
                break;
        }
    }
    private void getValidDetail() {
        type=10;
        Jobs jobs = session.getJobs();
        HashMap<String, String> params = new HashMap<>();
        params.put("item_qty", jobs.getTotalqty());
        params.put("suitable_vehicle",vehicleTypeID);
        String url = Config.SERVER_URL + Config.SEATCHECK;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST , this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
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
            if (deliveryTitle.getText().toString().trim().length() == 0 &&
                    vehicleTypeID.equals("") && puContactPerson.getText().toString().trim().length() == 0
                    && puMobile.getText().toString().trim().length() == 0 && puAddress.getText().toString().trim().length() == 0
                    && puDateTime.getText().toString().trim().length() == 0 && doAddress.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.completeallfield));
                return false;
            }
            if (deliveryTitle.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.transfertitlev));
                return false;
            } else if (vehicleTypeID.equals("")) {
                showMessage(getActivity().getResources().getString(R.string.selectsuitablevehicle));
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
            } else if (pu_cCode.getDrawable() == null) {
                showMessage(getActivity().getResources().getString(R.string.countrycode));
                return false;
            }  /*else if (pu_cCode.getText().toString().trim().length() == 0) {
                showMessage("Please select pick up country code.");
                return false;
            }*/ else if (puAddress.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.pickupaddress));
                return false;
            } else if (TextUtils.isEmpty(puCountry)) {
                showMessage(getActivity().getResources().getString(R.string.validpickaddress));
                return false;
            } else if (puDateTime.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.pickupdatetime));
                return false;
            }/* else if (puDate.before(new Date())) {
                showMessage("Pickup day must be greater than current day/time");
                return false;
            } else*/
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
            if (doAddress.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.dropoffaddress));
                return false;
            }/* else if (puSpecialRestriction.getText().toString().trim().length() == 0) {
                showMessage("Please enter PickUp/Drop Off Notes.");
                return false;
            }*/ else if (TextUtils.isEmpty(doCountry)) {
                showMessage(getActivity().getResources().getString(R.string.validdropaddress));
                return false;
            }
            if (!session.getJobs().getAsap().equals("1")) {
                if (auctionBid.getCheckedRadioButtonId() == -1) {
                    showMessage(getActivity().getResources().getString(R.string.auctionbid));
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

                    /*if (!mCreditCardDetail) {
                        AlertManager.BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", getActivity().getResources().getString(R.string.creditcarddecline), this.getClass().getName(), "5");
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
            } else {
                if (fixedPrice.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.fixedprice));
                    return false;
                }
                if (!checkZero(fixedPrice.getText().toString())) {
                    showMessage(getActivity().getResources().getString(R.string.validfixedprice));
                    return false;
                }
            }
            if (geoZone.getCheckedRadioButtonId() == -1) {
                showMessage(getActivity().getResources().getString(R.string.geozone));
                return false;
            } else if (geoZone.getCheckedRadioButtonId() == R.id.yes) {
                if (geo.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.georadius));
                    return false;
                }
            }
           /* if (!(pu_cCode.getText().toString().trim().length() == 0)) {
                if (pu_countrycodestr.contentEquals(""))
                    pu_countrycodestr = pu_cCode.getText().toString();
            }*/
            if (!(pu_cCode.getDrawable() == null)) {
                if (pu_countrycodestr.contentEquals("")) {
                    if (((String) pu_cCode.getTag()).contentEquals("in"))
                        pu_countrycodestr = "+91";
                    else
                        pu_countrycodestr = "+61";
                }
                //countrycodestr = cCode.getText().toString();
            }

        } catch (Exception e) {
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

    //String key = "key=AIzaSyDbjEJYaObHTyUL_zkn4V0XeexYAISu_z4";
    // Fetches all places from GooglePlaces AutoComplete Web Service

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

    Dialog mDialog;

    public void showVehicleDialog(final int index) {
        Intent intent = new Intent(getActivity(), VehicleListSubmitMultiple.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("array", suitableVehicleData);
        bundle.putInt("index", index);
        if (!vehicleTypeID.equals("")) {
            try {
                bundle.putString("vehicletypeid", vehicleTypeID);
            } catch (Exception e) {
                e.toString();
            }
        }
        intent.putExtras(bundle);
        startActivityForResult(intent, 14);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == 14 && resultCode == -1) {
            try {
                String val = result.getStringExtra("value");
                suitableVehicle.setText(result.getStringExtra("strvalue"));
                vehicleTypeID = val;
                Log.v("val", vehicleTypeID);
            } catch (Exception e) {
                e.toString();
            }
        }
        if (requestCode == 16 && resultCode == -1) {
            try {
                String val = result.getStringExtra("value");
                hasGroups = result.getBooleanExtra("hasGroups", true);
                mUser_Group = val;
                if (hasGroups) {
                    if (mUser_Group.contentEquals(""))
                        mUser_Group = "all";
                    submitJob(mUser_Group);
                } else
                    showMessage(getResources().getString(R.string.nofavouritefroup));
            } catch (Exception e) {
                e.toString();
            }
        }
    }

   /* public void showVehicleTypeDialog() {
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
                if (mDialog.isShowing())
                    mDialog.dismiss();
            }
        });

        title.setText("Select suitable vehicle");

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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.dialog_textview, R.id.textItem, getVehicleTypeList());
        //  CategoryAdapter catAdapter = new CategoryAdapter(getActivity());
        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                suitableVehicle.setText(parent.getItemAtPosition(position).toString());
                vehicleTypeID = getVehicleTypeId(parent.getItemAtPosition(position).toString());
                mDialog.dismiss();
            }
        });

        mDialog.show();
    }*/

    public String getVehicleTypeId(String stateName) {
        for (int i = 0; i < vehicleTypeData.size(); i++) {
            if (vehicleTypeData.get(i).get(Keys.KEY_NAME).equalsIgnoreCase(stateName))
                return vehicleTypeData.get(i).get(Keys.KEY_ID);
        }
        return "";
    }

    public String[] getVehicleTypeList() {
        String[] listContent = new String[vehicleTypeData.size()];
        for (int i = 0; i < vehicleTypeData.size(); i++) {
            listContent[i] = vehicleTypeData.get(i).get(Keys.KEY_NAME);
        }
        return listContent;
    }


    @Override
    public void onTaskComplete(String result) {
        Log.d(Config.TAG, result);
        handleResponse(result);
    }


    ArrayList<HashMap<String, String>> vehicleTypeData = new ArrayList<>();
    ArrayList<Category> suitableVehicleData = new ArrayList<Category>();

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (Integer.parseInt(outJson.getString(Config.STATUS)) == APIStatus.SUCCESS) {
                Log.v("type", "Type" + type);
                if (type == 15) {
                    favouritegroupsdata.clear();
                    //  JSONObject dataObj = outJson.getJSONObject(Config.DATA);
                    JSONArray favouriteArray = outJson.getJSONArray(Config.DATA);
                    if (favouriteArray.length() < 1) {
                        AlertManager.messageDialog(getActivity(), "Alert!", "No favourite groups available for you at that time. Please first create favourite groups before submit tranfser under that selected Favourite Group/s option.");
                    } else {
                        for (int i = 0; i < favouriteArray.length(); i++) {
                            JSONObject carrierObj = favouriteArray.getJSONObject(i);
                            Category cat = new Category();
                            cat.setName(carrierObj.getString(Keys.NAME));
                            cat.setId(carrierObj.getString(Keys.ID));
                            cat.setSelected(false);
                            favouritegroupsdata.add(cat);
                        }
                        if (getArguments().containsKey("data")) {
                            try {
                                if (delivery.getFav_user_id().length() > 0) {
                                    String fav_user[] = delivery.getFav_user_id().split(",");
                                    for (int j = 0; j < fav_user.length; j++) {
                                        for (int i = 0; i < favouriteArray.length(); i++) {
                                            JSONObject innerJson = favouriteArray.getJSONObject(i);
                                            Category cat = new Category();
                                            if (fav_user[j].equals(innerJson.get(Keys.KEY_ID).toString())) {
                                                cat.setName(innerJson.getString(Keys.KEY_NAME));
                                                cat.setId(innerJson.get(Keys.KEY_ID).toString());
                                                cat.setSelected(true);
                                                // suitableSelectedVehicleData.add(cat);
                                                favouritegroupsdata.set((int) i, cat);
                                            }

                                        }
                                    }

                                }
                            } catch (Exception e) {
                                e.toString();
                            }
                        }
                        showMultipleGroupDialog();
                    }
                }
                if (type == 1)
                    showSuccessMessage("Your Transfer successfully submitted.");
                if (type == 3)
                    showSuccessMessage("Your Transfer successfully updated.");
                if (type == 5) {
                    JSONObject dataObj = outJson.getJSONObject(Config.DATA);
                    duration = dataObj.optString(Keys.DURATION);
                    mCreditCardDetail = dataObj.optBoolean(Keys.CREDITCARDDETAIL);
                    mPickupDiff = dataObj.optString(Keys.PICK_UP_DIFFERENCE);
                    if (IsClicked) {
                        if (isValid())
                            showFavouriteSelect();
                    }
                }
                if (type == 2) {
                    vehicleTypeData.clear();
                    JSONArray outArray = outJson.getJSONArray("data");
                    for (int i = 0; i < outArray.length(); i++) {
                        JSONObject innerJson = outArray.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<>();
                        map.put(Keys.KEY_ID, innerJson.get(Keys.KEY_ID).toString());
                        map.put(Keys.KEY_NAME, innerJson.getString(Keys.KEY_NAME));
                        vehicleTypeData.add(map);
                        Category cat = new Category();
                        cat.setName(innerJson.getString(Keys.KEY_NAME));
                        cat.setId(innerJson.get(Keys.KEY_ID).toString());
                        cat.setSelected(false);
                        suitableVehicleData.add(cat);
                    }
                    getDuration(false);
                }
                else if (type==10){
                    if (isValid())
                        showFavouriteSelect();
                }
            } else if (Integer.parseInt(outJson.getString(Config.STATUS)) == APIStatus.UNPROCESSABLE) {
                try {
                    if (type == 1 || type == 3) {
                        String message = outJson.getString(Config.MESSAGE);
                        JSONObject inJson = outJson.optJSONObject("data");
                        if (inJson != null && inJson.has("type")) {
                            try {
                                String inJsonType = inJson.optString("type");
                                AlertManager.BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", outJson.getString(Config.MESSAGE), this.getClass().getName(), inJsonType);
                            } catch (Exception e) {
                                e.toString();
                            }
                        } else if (message != null && !message.contentEquals("")) {
                            showMessage(message);
                        } else
                            showMessage(outJson.getJSONArray("data").getJSONObject(0).getString(Config.MESSAGE));
                    } else if(type==10) {
                        try {
                            showMessage(outJson.getString("message").toString());
                        } catch (Exception e) {
                            e.toString();
                        }
                    }
                    else if (!outJson.getString("message").contentEquals(""))
                        showMessage(outJson.getString("message"));
                    else
                        showMessage(outJson.getJSONArray("data").getJSONObject(0).getString(Config.MESSAGE));
                } catch (Exception e) {
                    e.toString();
                }
            } else if (outJson.getInt(Config.STATUS) == APIStatus.INVALID_CARD) {
                try {
                    AlertManager.BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", outJson.getString(Config.MESSAGE), this.getClass().getName(), "5");
                } catch (Exception e) {
                    e.toString();
                }
            } else
                showMessage(outJson.getJSONArray("data").getJSONObject(0).getString(Config.MESSAGE));
        } catch (Exception e) {
            e.toString();
        }
    }

    private void showSuccessMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Success!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //String name = getFragmentManager().getBackStackEntryAt(0).getName();
                //getFragmentManager().popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Fragment fragment = new HomeMap();
                String backStateName = "com.grabid.activities.HomeActivity";
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        });
        builder.show();
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
                    Log.v("tag", countrycode[position]);
                    pu_countrycodestr = countrycode[position];
                    //  pu_cCode.setText(pu_countrycodestr);
                    if (position == 0) {
                        pu_cCode.setImageDrawable(getResources().getDrawable(R.drawable.au));
                        pu_cCode.setTag("au");
                    } else {
                        pu_cCode.setImageDrawable(getResources().getDrawable(R.drawable.in));
                        pu_cCode.setTag("in");
                    }
                } else if (++checkpu > 1) {
                    Log.v("tag", countrycode[position]);
                    pu_countrycodestr = countrycode[position];
                    //  pu_cCode.setText(pu_countrycodestr);
                    if (position == 0) {
                        pu_cCode.setImageDrawable(getResources().getDrawable(R.drawable.au));
                        pu_cCode.setTag("au");
                    } else {
                        pu_cCode.setImageDrawable(getResources().getDrawable(R.drawable.in));
                        pu_cCode.setTag("in");
                    }
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    Dialog mDialog1;

    public void showMultipleGroupDialog() {
        mDialog1 = new Dialog(getActivity(), R.style.GrabidDialog);
        // mDialog.setCancelable(false);
        mDialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog1.setContentView(R.layout.fragment_list_new);
        mDialog1.setCanceledOnTouchOutside(false);
        TextView title = (TextView) mDialog1.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        ImageView close = (ImageView) mDialog1.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog1 != null && mDialog1.isShowing())
                    mDialog1.dismiss();
                // done();


            }
        });
        title.setText("Choose which favourite group this user belongs to");
        mDialog1.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        mDialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        ListView dialog_ListView = (ListView) mDialog1.findViewById(R.id.list);
        CategoryAdapterGroups catAdapter = new CategoryAdapterGroups(getActivity());
        dialog_ListView.setAdapter(catAdapter);
        dialog_ListView.setSmoothScrollbarEnabled(true);

        dialog_ListView.setItemsCanFocus(true);

        mDialog1.show();
    }

    private class CategoryAdapterGroups extends BaseAdapter {
        Context ctx;

        CategoryAdapterGroups(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public int getCount() {
            return favouritegroupsdata.size();
        }

        @Override
        public Object getItem(int i) {
            return favouritegroupsdata.get(i);
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
            final TextView done = (TextView) convertView.findViewById(R.id.done);
            name.setTag(position);
            if (position == favouritegroupsdata.size() - 1)
                done.setVisibility(View.VISIBLE);

            name.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (check.isChecked())
                        check.setChecked(false);
                    else
                        check.setChecked(true);
                    check.setChecked(true);
                    Category country = new Category();
                    country = favouritegroupsdata.get((int) name.getTag());
                    country.setSelected(check.isChecked());
                    favouritegroupsdata.set((int) name.getTag(), country);
                }
                //notifyDataSetChanged();

            });
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    Category country = new Category();
                    country = favouritegroupsdata.get((int) name.getTag());
                    country.setSelected(isChecked);
                    favouritegroupsdata.set((int) name.getTag(), country);
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

    String suitableIdBuilderr = "";

    public void done() {
        mDialog1.dismiss();
        String builder = "";
        suitableIdBuilderr = "";
        for (int i = 0; i < favouritegroupsdata.size(); i++) {
            if (favouritegroupsdata.get(i).isSelected()) {
                builder += favouritegroupsdata.get(i).getName() + ", ";
                suitableIdBuilderr += favouritegroupsdata.get(i).getId() + ",";
            }
        }
        builder = suitableIdBuilderr.substring(0, suitableIdBuilderr.length() - 1);
        submitJob(builder);

    }
}
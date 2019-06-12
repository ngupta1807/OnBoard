package com.bookmyride.activities.register;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.activities.DriverHome;
import com.bookmyride.activities.PassengerHome;
import com.bookmyride.activities.SignIn;
import com.bookmyride.activities.TermsConditions;
import com.bookmyride.activities.VehicleModelManufacture;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.models.*;
import com.bookmyride.models.Address;
import com.bookmyride.util.FileOperation;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Vinod on 2017-01-07.
 */
public class VehicleInfo extends AppCompatActivity implements View.OnClickListener, AsyncTaskCompleteListener {
    TextView next, taxiVehicleType, taxiVehicleMaker, taxiVehicleModel, taxiMakeYear,
            hireVehicleType, hireVehicleMaker, hireVehicleModel, hireMakeYear,
            shareVehicleType, shareVehicleMaker, shareVehicleModel, shareMakeYear,
            bikeMakeYear;
    EditText taxiRegistrationNumber, hireRegistrationNumber, shareRegistrationNumber,
            bikeRegistrationNumber, referralCode, bikeVehicleType, bikeVehicleMaker, bikeVehicleModel;
    SessionHandler session;
    ImageView taxiAC, hireAC, shareAC;
    LinearLayout layTaxi, layHire, layShare, layBike, layTerm;
    boolean hasTaxiAC = true, hasHireAC = true, hasShareAC = true, hasBikeAC;
    JSONArray categoryData;
    TextView btnTerms, btnUseSame;
    TextView btnUseSameTaxi, btnUseSameEconomy, btnUseSamePremium, btnUseSameBike;
    CheckBox termCheck, useSame;
    CheckBox useSameTaxi, useSameEconomy, useSamePremium, useSameBike;
    String image = "", identifier = "", socialType = "";
    TextView title, taxiFleet, economyFleet, premiumFleet, bikeFleet;
    LinearLayout layTaxiInfo, layEconomyInfo, layPremiumInfo, layBikeInfo;
    LinearLayout layTaxiFleet, layEconomyFleet, layPremiumFleet, layBikeFleet;
    EditText taxiMakerOther, taxiModelOther, shareMakerOther, shareModelOther, hireMakerOther, hireModelOther;
    TextView taxiModelLine, shareModelLine, hireModelLine;
    ScrollView mScroll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_info);
        init();
        updateUI();
        getVehicleType("");
        if (!Location.vehicalSelectedData.equals("") && !Location.vehicalSelectedData.equals("null"))
            setData();
        if (getIntent().hasExtra("social_data")) {
            socialType = getIntent().getStringExtra("type");
            getSocialData(getIntent().getStringExtra("social_data"));
        }
    }

    private void getSocialData(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            image = obj.getString("image");
            identifier = obj.getString("identifier");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean categoryExists(JSONArray jsonArray, String nameToFind) {
        //Log.d("exist",""+jsonArray.toString().contains("\"name\":\""+nameToFind+"\""));
        return jsonArray.toString().contains("\"name\":\"" + nameToFind + "\"");
    }

    private void setData() {
        title.setText("Profile");
        if (Location.isPremium.equals("1")) {
            useSame.setChecked(true);
            hideAllCategories();
        } else {
            useSame.setChecked(false);
            updateUI();
            try {
                JSONArray vehicleObject = new JSONArray(Location.vehicalSelectedData);
                //Log.i("vehicle",vehicleObject.toString());
                //Log.i("categoryData",categoryData.toString());
                for (int i = 0; i < vehicleObject.length(); i++) {
                    //JSONObject obj = (JSONObject)vehicleObject.get(i);
                    JSONObject obj = vehicleObject.getJSONObject(i);
                    if (obj.getString(Key.VEHICLE_TYPE).trim().equalsIgnoreCase("Taxi") &&
                            categoryExists(categoryData, "Taxi")) {

                        layTaxi.setVisibility(View.VISIBLE);

                        if (obj.getString(Key.IS_FLEET_SELECTED).equals("1")) {
                            useSameTaxi.setChecked(true);
                            showHideTaxiInfo(false);
                        } else {
                            useSameTaxi.setChecked(false);
                            showHideTaxiInfo(true);
                        }
                        if (!useSameTaxi.isChecked()) {
                            taxiVehicleType.setText(obj.getString(Key.TYPE_NAME));
                            taxiVehicleMaker.setText(obj.getString(Key.MAKER_NAME));
                            if (obj.getString(Key.MAKER_NAME).contains("Other Manufacturer")) {
                                taxiMakerOther.setVisibility(View.VISIBLE);
                                taxiMakerOther.setText(obj.getString(Key.MAKER_NAME_OTHER));
                                taxiVehicleModel.setVisibility(View.GONE);
                                taxiModelLine.setVisibility(View.GONE);
                                taxiModelOther.setVisibility(View.VISIBLE);
                                taxiModelOther.setText(obj.getString(Key.MODEL_NAME_OTHER));
                            } else {
                                taxiMakerOther.setVisibility(View.GONE);
                                taxiMakerOther.setText("");
                                taxiVehicleModel.setVisibility(View.VISIBLE);
                                taxiModelLine.setVisibility(View.VISIBLE);
                                taxiModelOther.setVisibility(View.GONE);
                                taxiModelOther.setText("");
                                taxiVehicleModel.setText(obj.getString(Key.MODEL_NAME));
                                if (obj.getString(Key.MODEL_NAME).contains("Other Model")) {
                                    taxiModelOther.setVisibility(View.VISIBLE);
                                    taxiModelOther.setText(obj.getString(Key.MODEL_NAME_OTHER));
                                } else {
                                    taxiModelOther.setVisibility(View.GONE);
                                    taxiModelOther.setText("");
                                }
                            }
                            taxiMakeYear.setText(obj.getString(Key.YEAR));
                            taxiRegistrationNumber.setText(obj.getString(Key.REGISTRATION_NUMBER));
                            taxiVehicleID = obj.getString(Key.TYPE);
                            taxiMakerID = obj.getString(Key.MAKER);
                            taxiModelID = obj.getString(Key.MODEL);
                            taxiVehicleName = obj.getString(Key.TYPE_NAME);
                            taxiMakerName = obj.getString(Key.MAKER_NAME);
                            taxiModelName = obj.getString(Key.MODEL_NAME);
                            taxiModelYear = obj.getString(Key.YEAR);
                            taxiAC.setTag(obj.getString(Key.AC));
                            if (taxiAC.getTag().toString().equals("1") || taxiAC.getTag().toString().equals("Yes")) {
                                hasTaxiAC = true;
                                taxiAC.setTag("1");
                                taxiAC.setImageDrawable(ContextCompat.getDrawable(VehicleInfo.this, R.drawable.on_button));
                            } else {
                                hasTaxiAC = false;
                                taxiAC.setTag("0");
                                taxiAC.setImageDrawable(ContextCompat.getDrawable(VehicleInfo.this, R.drawable.off_button));
                            }
                        } else {
                            taxiFleetID = obj.getString(Key.FLEET_ID);
                            taxiFleet.setText(obj.getString(Key.FLEET_NAME));
                        }
                    } else if (obj.getString(Key.VEHICLE_TYPE).trim().equalsIgnoreCase("Economy") &&
                            categoryExists(categoryData, "Economy")) {

                        layShare.setVisibility(View.VISIBLE);

                        if (obj.getString(Key.IS_FLEET_SELECTED).equals("1")) {
                            useSameEconomy.setChecked(true);
                            showHideEconomyInfo(false);
                        } else {
                            useSameEconomy.setChecked(false);
                            showHideEconomyInfo(true);
                        }

                        if (!useSameEconomy.isChecked()) {
                            shareVehicleType.setText(obj.getString(Key.TYPE_NAME));
                            shareVehicleMaker.setText(obj.getString(Key.MAKER_NAME));
                            if (obj.getString(Key.MAKER_NAME).contains("Other Manufacturer")) {
                                shareMakerOther.setVisibility(View.VISIBLE);
                                shareMakerOther.setText(obj.getString(Key.MAKER_NAME_OTHER));
                                shareVehicleModel.setVisibility(View.GONE);
                                shareModelLine.setVisibility(View.GONE);
                                shareModelOther.setVisibility(View.VISIBLE);
                                shareModelOther.setText(obj.getString(Key.MODEL_NAME_OTHER));
                            } else {
                                shareMakerOther.setVisibility(View.GONE);
                                shareMakerOther.setText("");
                                shareVehicleModel.setVisibility(View.VISIBLE);
                                shareModelLine.setVisibility(View.VISIBLE);
                                shareModelOther.setVisibility(View.GONE);
                                shareModelOther.setText("");
                                shareVehicleModel.setText(obj.getString(Key.MODEL_NAME));
                                if (obj.getString(Key.MODEL_NAME).contains("Other Model")) {
                                    shareModelOther.setVisibility(View.VISIBLE);
                                    shareModelOther.setText(obj.getString(Key.MODEL_NAME_OTHER));
                                } else {
                                    shareModelOther.setVisibility(View.GONE);
                                    shareModelOther.setText("");
                                }
                            }

                            shareMakeYear.setText(obj.getString(Key.YEAR));
                            shareRegistrationNumber.setText(obj.getString(Key.REGISTRATION_NUMBER));

                            shareVehicleID = obj.getString(Key.TYPE);
                            shareMakerID = obj.getString(Key.MAKER);
                            shareModelID = obj.getString(Key.MODEL);
                            shareVehicleName = obj.getString(Key.TYPE_NAME);
                            shareMakerName = obj.getString(Key.MAKER_NAME);
                            shareModelName = obj.getString(Key.MODEL_NAME);
                            shareModelYear = obj.getString(Key.YEAR);

                            shareAC.setTag(obj.getString(Key.AC));
                            if (shareAC.getTag().toString().equals("1") || shareAC.getTag().toString().equals("Yes")) {
                                hasShareAC = true;
                                shareAC.setTag("1");
                                shareAC.setImageDrawable(ContextCompat.getDrawable(VehicleInfo.this, R.drawable.on_button));
                            } else {
                                hasShareAC = false;
                                shareAC.setTag("0");
                                shareAC.setImageDrawable(ContextCompat.getDrawable(VehicleInfo.this, R.drawable.off_button));
                            }
                        } else {
                            economyFleetID = obj.getString(Key.FLEET_ID);
                            economyFleet.setText(obj.getString(Key.FLEET_NAME));
                        }
                    } else if (obj.getString(Key.VEHICLE_TYPE).trim().equalsIgnoreCase("Premium") &&
                            categoryExists(categoryData, "Premium")) {

                        layHire.setVisibility(View.VISIBLE);

                        if (obj.getString(Key.IS_FLEET_SELECTED).equals("1")) {
                            useSamePremium.setChecked(true);
                            showHidePremiumInfo(false);
                        } else {
                            useSamePremium.setChecked(false);
                            showHidePremiumInfo(true);
                        }
                        if (!useSamePremium.isChecked()) {
                            hireVehicleType.setText(obj.getString(Key.TYPE_NAME));
                            hireVehicleMaker.setText(obj.getString(Key.MAKER_NAME));
                            if (obj.getString(Key.MAKER_NAME).contains("Other Manufacturer")) {
                                hireMakerOther.setVisibility(View.VISIBLE);
                                hireMakerOther.setText(obj.getString(Key.MAKER_NAME_OTHER));
                                hireVehicleModel.setVisibility(View.GONE);
                                hireModelLine.setVisibility(View.GONE);
                                hireModelOther.setVisibility(View.VISIBLE);
                                hireModelOther.setText(obj.getString(Key.MODEL_NAME_OTHER));
                            } else {
                                hireMakerOther.setVisibility(View.GONE);
                                hireMakerOther.setText("");
                                hireVehicleModel.setVisibility(View.VISIBLE);
                                hireModelLine.setVisibility(View.VISIBLE);
                                hireModelOther.setVisibility(View.GONE);
                                hireModelOther.setText("");
                                hireVehicleModel.setText(obj.getString(Key.MODEL_NAME));
                                if (obj.getString(Key.MODEL_NAME).contains("Other Model")) {
                                    hireModelOther.setVisibility(View.VISIBLE);
                                    hireModelOther.setText(obj.getString(Key.MODEL_NAME_OTHER));
                                } else {
                                    hireModelOther.setVisibility(View.GONE);
                                    hireModelOther.setText("");
                                }
                            }

                            hireMakeYear.setText(obj.getString(Key.YEAR));
                            hireRegistrationNumber.setText(obj.getString(Key.REGISTRATION_NUMBER));

                            hireVehicleID = obj.getString(Key.TYPE);
                            hireMakerID = obj.getString(Key.MAKER);
                            hireModelID = obj.getString(Key.MODEL);
                            hireVehicleName = obj.getString(Key.TYPE_NAME);
                            hireMakerName = obj.getString(Key.MAKER_NAME);
                            hireModelName = obj.getString(Key.MODEL_NAME);
                            hireModelYear = obj.getString(Key.YEAR);

                            hireAC = (ImageView) findViewById(R.id.hire_push_switch);
                            hireAC.setTag(obj.getString(Key.AC));
                            if (hireAC.getTag().toString().equals("1") || hireAC.getTag().toString().equals("Yes")) {
                                hasHireAC = true;
                                hireAC.setTag("1");
                                hireAC.setImageDrawable(ContextCompat.getDrawable(VehicleInfo.this, R.drawable.on_button));
                            } else {
                                hasHireAC = false;
                                hireAC.setTag("0");
                                hireAC.setImageDrawable(ContextCompat.getDrawable(VehicleInfo.this, R.drawable.off_button));
                            }
                        } else {
                            premiumFleetID = obj.getString(Key.FLEET_ID);
                            premiumFleet.setText(obj.getString(Key.FLEET_NAME));
                        }
                    } else if (obj.getString(Key.VEHICLE_TYPE).trim().equalsIgnoreCase("Motor Bike") &&
                            categoryExists(categoryData, "Motor Bike")) {
                        layBike.setVisibility(View.VISIBLE);
                        if (obj.getString(Key.IS_FLEET_SELECTED).equals("1")) {
                            useSameBike.setChecked(true);
                            showHideBikeInfo(false);
                        } else {
                            useSameBike.setChecked(false);
                            showHideBikeInfo(true);
                        }
                        if (!useSameBike.isChecked()) {
                            bikeVehicleType.setText(obj.getString(Key.TYPE_NAME));
                            bikeVehicleMaker.setText(obj.getString(Key.MAKER_NAME));
                            bikeVehicleModel.setText(obj.getString(Key.MODEL_NAME));
                            bikeMakeYear.setText(obj.getString(Key.YEAR));
                            bikeRegistrationNumber.setText(obj.getString(Key.REGISTRATION_NUMBER));

                            bikeVehicleID = obj.getString(Key.TYPE);
                            bikeMakerID = obj.getString(Key.MAKER);
                            bikeModelID = obj.getString(Key.MODEL);
                            bikeVehicleName = obj.getString(Key.TYPE_NAME);
                            bikeMakerName = obj.getString(Key.MAKER_NAME);
                            bikeModelName = obj.getString(Key.MODEL_NAME);
                            bikeModelYear = obj.getString(Key.YEAR);
                        } else {
                            bikeFleetID = obj.getString(Key.FLEET_ID);
                            bikeFleet.setText(obj.getString(Key.FLEET_NAME));
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        //referralCode.setText(Location.selectedRefferelCode);
        referralCode.setVisibility(View.GONE);
        layTerm.setVisibility(View.GONE);
        next.setText("Update");
    }

    private void hideAllCategories() {
        layTaxi.setVisibility(View.GONE);
        layHire.setVisibility(View.GONE);
        layShare.setVisibility(View.GONE);
        layBike.setVisibility(View.GONE);
    }

    public void onBack(View view) {
        onBackPressed();
    }

    private void updateUI() {
        try {
            categoryData = new JSONArray(session.getLocation().getCategory());
            for (int i = 0; i < categoryData.length(); i++) {
                if (categoryData.getJSONObject(i).getString("name").equals("Taxi")) {
                    taxiVehicleType.setTag(categoryData.getJSONObject(i).getString("id"));
                    layTaxi.setVisibility(View.VISIBLE);
                }
                if (categoryData.getJSONObject(i).getString("name").equals("Premium")) {
                    hireVehicleType.setTag(categoryData.getJSONObject(i).getString("id"));
                    layHire.setVisibility(View.VISIBLE);
                }
                if (categoryData.getJSONObject(i).getString("name").equals("Economy")) {
                    shareVehicleType.setTag(categoryData.getJSONObject(i).getString("id"));
                    layShare.setVisibility(View.VISIBLE);
                }
                if (categoryData.getJSONObject(i).getString("name").equals("Motor Bike")) {
                    bikeVehicleType.setTag(categoryData.getJSONObject(i).getString("id"));
                    layBike.setVisibility(View.VISIBLE);
                }
                categoryID = categoryData.getJSONObject(i).getString("id") + ",";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        session = new SessionHandler(this);
        categoryData = new JSONArray();
        //categoryID = session.getLocation().getCategoryID();
        next = (TextView) findViewById(R.id.continu);
        next.setOnClickListener(this);
        termCheck = (CheckBox) findViewById(R.id.accept);
        useSame = (CheckBox) findViewById(R.id.use_same);

        mScroll = (ScrollView) findViewById(R.id.scroll);

        useSameTaxi = (CheckBox) findViewById(R.id.use_same_taxi);
        useSameEconomy = (CheckBox) findViewById(R.id.use_same_economy);
        useSamePremium = (CheckBox) findViewById(R.id.use_same_premium);
        useSameBike = (CheckBox) findViewById(R.id.use_same_bike);

        btnTerms = (TextView) findViewById(R.id.btn_terms);
        btnTerms.setOnClickListener(this);
        btnUseSame = (TextView) findViewById(R.id.btn_use_same);
        btnUseSame.setOnClickListener(this);

        btnUseSameTaxi = (TextView) findViewById(R.id.btn_use_same_taxi);
        btnUseSameTaxi.setOnClickListener(this);
        btnUseSameEconomy = (TextView) findViewById(R.id.btn_use_same_economy);
        btnUseSameEconomy.setOnClickListener(this);
        btnUseSamePremium = (TextView) findViewById(R.id.btn_use_same_premium);
        btnUseSamePremium.setOnClickListener(this);
        btnUseSameBike = (TextView) findViewById(R.id.btn_use_same_bike);
        btnUseSameBike.setOnClickListener(this);

        layTaxiInfo = (LinearLayout) findViewById(R.id.lay_taxi_info);
        layEconomyInfo = (LinearLayout) findViewById(R.id.lay_economy_info);
        layPremiumInfo = (LinearLayout) findViewById(R.id.lay_premium_info);
        layBikeInfo = (LinearLayout) findViewById(R.id.lay_bike_info);

        layTaxiFleet = (LinearLayout) findViewById(R.id.lay_taxi_fleet);
        layEconomyFleet = (LinearLayout) findViewById(R.id.lay_economy_fleet);
        layPremiumFleet = (LinearLayout) findViewById(R.id.lay_premium_fleet);
        layBikeFleet = (LinearLayout) findViewById(R.id.lay_bike_fleet);

        taxiFleet = (TextView) findViewById(R.id.taxi_fleet_type);
        taxiFleet.setOnClickListener(this);
        economyFleet = (TextView) findViewById(R.id.economy_fleet_type);
        economyFleet.setOnClickListener(this);
        premiumFleet = (TextView) findViewById(R.id.premium_fleet_type);
        premiumFleet.setOnClickListener(this);
        bikeFleet = (TextView) findViewById(R.id.bike_fleet_type);
        bikeFleet.setOnClickListener(this);

        title = (TextView) findViewById(R.id.signin_header_Tv);
        layTaxi = (LinearLayout) findViewById(R.id.lay_taxi);
        layHire = (LinearLayout) findViewById(R.id.lay_hire_car);
        layShare = (LinearLayout) findViewById(R.id.lay_ride_share);
        layBike = (LinearLayout) findViewById(R.id.lay_motor_bike);
        layTerm = (LinearLayout) findViewById(R.id.lay_term);

        //For Taxi Type
        taxiVehicleType = (TextView) findViewById(R.id.taxi_vehicle_type);
        taxiVehicleType.setOnClickListener(this);
        taxiVehicleMaker = (TextView) findViewById(R.id.taxi_maker);
        taxiVehicleMaker.setOnClickListener(this);
        taxiMakerOther = (EditText) findViewById(R.id.taxi_maker_other);
        taxiVehicleModel = (TextView) findViewById(R.id.taxi_model);
        taxiVehicleModel.setOnClickListener(this);
        taxiModelLine = (TextView) findViewById(R.id.v_taxi_model);
        taxiModelOther = (EditText) findViewById(R.id.taxi_model_other);
        taxiMakeYear = (TextView) findViewById(R.id.taxi_make_year);
        taxiMakeYear.setOnClickListener(this);
        taxiRegistrationNumber = (EditText) findViewById(R.id.taxi_registration_number);
        taxiRegistrationNumber.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        taxiAC = (ImageView) findViewById(R.id.taxi_push_switch);
        taxiAC.setTag("1");
        taxiAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (taxiAC.getTag().toString().equals("1")) {
                    hasTaxiAC = false;
                    taxiAC.setTag("0");
                    taxiAC.setImageDrawable(ContextCompat.getDrawable(VehicleInfo.this, R.drawable.off_button));
                } else {
                    hasTaxiAC = true;
                    taxiAC.setTag("1");
                    taxiAC.setImageDrawable(ContextCompat.getDrawable(VehicleInfo.this, R.drawable.on_button));
                }
            }
        });
        //For Hire Car Type
        hireVehicleType = (TextView) findViewById(R.id.hire_vehicle_type);
        hireVehicleType.setOnClickListener(this);
        hireVehicleMaker = (TextView) findViewById(R.id.hire_maker);
        hireVehicleMaker.setOnClickListener(this);
        hireMakerOther = (EditText) findViewById(R.id.hire_maker_other);
        hireVehicleModel = (TextView) findViewById(R.id.hire_model);
        hireVehicleModel.setOnClickListener(this);
        hireModelLine = (TextView) findViewById(R.id.v_hire_model);
        hireModelOther = (EditText) findViewById(R.id.hire_model_other);
        hireMakeYear = (TextView) findViewById(R.id.hire_make_year);
        hireMakeYear.setOnClickListener(this);
        hireRegistrationNumber = (EditText) findViewById(R.id.hire_registration_number);
        hireRegistrationNumber.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        hireAC = (ImageView) findViewById(R.id.hire_push_switch);
        hireAC.setTag("1");
        hireAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hireAC.getTag().toString().equals("1")) {
                    hasHireAC = false;
                    hireAC.setTag("0");
                    hireAC.setImageDrawable(ContextCompat.getDrawable(VehicleInfo.this, R.drawable.off_button));
                } else {
                    hasHireAC = true;
                    hireAC.setTag("1");
                    hireAC.setImageDrawable(ContextCompat.getDrawable(VehicleInfo.this, R.drawable.on_button));
                }
            }
        });
        //for Share Ride
        shareVehicleType = (TextView) findViewById(R.id.ride_vehicle_type);
        shareVehicleType.setOnClickListener(this);
        shareVehicleMaker = (TextView) findViewById(R.id.ride_maker);
        shareVehicleMaker.setOnClickListener(this);
        shareMakerOther = (EditText) findViewById(R.id.ride_maker_other);
        shareVehicleModel = (TextView) findViewById(R.id.ride_model);
        shareVehicleModel.setOnClickListener(this);
        shareModelLine = (TextView) findViewById(R.id.v_ride_model);
        shareModelOther = (EditText) findViewById(R.id.ride_model_other);
        shareMakeYear = (TextView) findViewById(R.id.ride_make_year);
        shareMakeYear.setOnClickListener(this);
        shareRegistrationNumber = (EditText) findViewById(R.id.ride_registration_number);
        shareRegistrationNumber.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        shareAC = (ImageView) findViewById(R.id.ride_push_switch);
        shareAC.setTag("1");
        shareAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shareAC.getTag().toString().equals("1")) {
                    hasShareAC = false;
                    shareAC.setTag("0");
                    shareAC.setImageDrawable(ContextCompat.getDrawable(VehicleInfo.this, R.drawable.off_button));
                } else {
                    hasShareAC = true;
                    shareAC.setTag("1");
                    shareAC.setImageDrawable(ContextCompat.getDrawable(VehicleInfo.this, R.drawable.on_button));
                }
            }
        });

        //for Motor Bike
        bikeVehicleType = (EditText) findViewById(R.id.bike_vehicle_type);
        //bikeVehicleType.setOnClickListener(this);
        bikeVehicleMaker = (EditText) findViewById(R.id.bike_maker);
        //bikeVehicleMaker.setOnClickListener(this);
        bikeVehicleModel = (EditText) findViewById(R.id.bike_model);
        //bikeVehicleModel.setOnClickListener(this);
        bikeMakeYear = (TextView) findViewById(R.id.bike_make_year);
        bikeMakeYear.setOnClickListener(this);
        bikeRegistrationNumber = (EditText) findViewById(R.id.bike_registration_number);
        bikeRegistrationNumber.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        referralCode = (EditText) findViewById(R.id.referral_code);

        if (!session.getSessionCode().equals("") && !session.getSessionCode().equals("null")) {
            referralCode.setText(session.getSessionCode());
            referralCode.setFocusable(false);
            referralCode.setFocusableInTouchMode(false);
            referralCode.setClickable(false);
        } else {
            referralCode.setFocusable(true);
            referralCode.setFocusableInTouchMode(true);
            referralCode.setClickable(true);
        }

        useSame.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    hideAllCategories();
                } else {
                    updateUI();
                }
            }
        });

        useSameTaxi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    showHideTaxiInfo(true);
                } else {
                    showHideTaxiInfo(false);
                }
            }
        });
        useSameEconomy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    showHideEconomyInfo(true);
                } else {
                    showHideEconomyInfo(false);
                }
            }
        });
        useSamePremium.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    showHidePremiumInfo(true);
                } else {
                    showHidePremiumInfo(false);
                }
            }
        });
        useSameBike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    showHideBikeInfo(true);
                } else {
                    showHideBikeInfo(false);
                }
            }
        });
    }

    private boolean isValid() {
        if (!useSame.isChecked()) {
            try {
                categoryData = new JSONArray(session.getLocation().getCategory());
                for (int i = 0; i < categoryData.length(); i++) {
                    if (categoryData.getJSONObject(i).getString("name").equals("Taxi")) {
                        if (!useSameTaxi.isChecked()) {
                            if (taxiVehicleType.getText().toString().equals("")) {
                                Alert("Oops !!!", "Please select vehicle type for Taxi.");
                                return false;
                            } else if (taxiVehicleMaker.getText().toString().equals("")) {
                                Alert("Oops !!!", "Please select Vehicle Manufacturer for Taxi.");
                                return false;
                            } else if (taxiMakerOther.getVisibility() == View.VISIBLE &&
                                    taxiMakerOther.getText().toString().equals("")) {
                                taxiMakerOther.requestFocus();
                                focusOnView(taxiMakerOther);
                                Alert("Oops !!!", "Please enter Vehicle Manufacturer for Taxi.");
                                return false;
                            } else if (taxiVehicleModel.getVisibility() == View.VISIBLE &&
                                    taxiVehicleModel.getText().toString().equals("")) {
                                Alert("Oops !!!", "Please select Vehicle Model for Taxi.");
                                return false;
                            } else if (taxiModelOther.getVisibility() == View.VISIBLE &&
                                    taxiModelOther.getText().toString().equals("")) {
                                taxiModelOther.requestFocus();
                                focusOnView(taxiModelOther);
                                Alert("Oops !!!", "Please enter Vehicle Model for Taxi.");
                                return false;
                            } else if (taxiMakeYear.getText().toString().equals("")) {
                                Alert("Oops !!!", "Please select Year of Manufacturer for Taxi.");
                                return false;
                            } else if (taxiRegistrationNumber.getText().toString().equals("")) {
                                taxiRegistrationNumber.requestFocus();
                                focusOnView(taxiRegistrationNumber);
                                Alert("Oops !!!", "Please enter Vehicle Registration Number for Taxi.");
                                return false;
                            }
                        } else if (useSameTaxi.isChecked()) {
                            if (taxiFleet.getText().toString().equals("")) {
                                Alert("Oops !!!", "Please select fleet type for Taxi.");
                                return false;
                            }
                        }
                    } else if (categoryData.getJSONObject(i).getString("name").equals("Economy")) {
                        if (!useSameEconomy.isChecked()) {
                            if (shareVehicleType.getText().toString().equals("")) {
                                Alert("Oops !!!", "Please select vehicle type for Economy.");
                                return false;
                            } else if (shareVehicleMaker.getText().toString().equals("")) {
                                Alert("Oops !!!", "Please select Vehicle Manufacturer Economy.");
                                return false;
                            } else if (shareMakerOther.getVisibility() == View.VISIBLE &&
                                    shareMakerOther.getText().toString().equals("")) {
                                shareMakerOther.requestFocus();
                                focusOnView(shareMakerOther);
                                Alert("Oops !!!", "Please enter Vehicle Manufacturer Economy.");
                                return false;
                            } else if (shareVehicleModel.getVisibility() == View.VISIBLE &&
                                    shareVehicleModel.getText().toString().equals("")) {
                                Alert("Oops !!!", "Please select Vehicle Model for Economy.");
                                return false;
                            } else if (shareModelOther.getVisibility() == View.VISIBLE &&
                                    shareModelOther.getText().toString().equals("")) {
                                shareModelOther.requestFocus();
                                focusOnView(shareModelOther);
                                Alert("Oops !!!", "Please enter Vehicle Model for Economy.");
                                return false;
                            } else if (shareMakeYear.getText().toString().equals("")) {
                                Alert("Oops !!!", "Please select Year of Manufacturer for Economy.");
                                return false;
                            } else if (shareRegistrationNumber.getText().toString().equals("")) {
                                shareRegistrationNumber.requestFocus();
                                focusOnView(shareRegistrationNumber);
                                Alert("Oops !!!", "Please enter Vehicle Registration Number for Economy.");
                                return false;
                            }
                        } else if (useSameEconomy.isChecked()) {
                            if (economyFleet.getText().toString().equals("")) {
                                Alert("Oops !!!", "Please select fleet type for Economy.");
                                return false;
                            }
                        }
                    } else if (categoryData.getJSONObject(i).getString("name").equals("Premium")) {
                        if (!useSamePremium.isChecked()) {
                            if (hireVehicleType.getText().toString().equals("")) {
                                Alert("Oops !!!", "Please select vehicle type for Premium.");
                                return false;
                            } else if (hireVehicleMaker.getText().toString().equals("")) {
                                Alert("Oops !!!", "Please select Vehicle Manufacturer for Premium.");
                                return false;
                            } else if (hireMakerOther.getVisibility() == View.VISIBLE &&
                                    hireMakerOther.getText().toString().equals("")) {
                                hireMakerOther.requestFocus();
                                focusOnView(hireMakerOther);
                                Alert("Oops !!!", "Please enter Vehicle Manufacturer for Premium.");
                                return false;
                            } else if (hireVehicleModel.getVisibility() == View.VISIBLE &&
                                    hireVehicleModel.getText().toString().equals("")) {
                                Alert("Oops !!!", "Please select Vehicle Model for Premium.");
                                return false;
                            } else if (hireModelOther.getVisibility() == View.VISIBLE &&
                                    hireModelOther.getText().toString().equals("")) {
                                hireModelOther.requestFocus();
                                focusOnView(hireModelOther);
                                Alert("Oops !!!", "Please enter Vehicle Model for Premium.");
                                return false;
                            } else if (hireMakeYear.getText().toString().equals("")) {
                                Alert("Oops !!!", "Please select Year of Manufacturer for Premium.");
                                return false;
                            } else if (hireRegistrationNumber.getText().toString().equals("")) {
                                hireRegistrationNumber.requestFocus();
                                focusOnView(hireRegistrationNumber);
                                Alert("Oops !!!", "Please enter Vehicle Registration Number for Premium.");
                                return false;
                            }
                        } else if (useSamePremium.isChecked()) {
                            if (premiumFleet.getText().toString().equals("")) {
                                Alert("Oops !!!", "Please select fleet type for Premium.");
                                return false;
                            }
                        }
                    } else if (categoryData.getJSONObject(i).getString("name").equals("Motor Bike")) {
                        if (!useSameBike.isChecked()) {
                            if (bikeVehicleType.getText().toString().equals("")) {
                                bikeVehicleType.requestFocus();
                                focusOnView(bikeVehicleType);
                                Alert("Oops !!!", "Please enter vehicle type for Motor Bike.");
                                return false;
                            } else if (bikeVehicleMaker.getText().toString().equals("")) {
                                bikeVehicleMaker.requestFocus();
                                focusOnView(bikeVehicleMaker);
                                Alert("Oops !!!", "Please enter Vehicle Manufacturer Motor Bike.");
                                return false;
                            } else if (bikeVehicleModel.getText().toString().equals("")) {
                                bikeVehicleModel.requestFocus();
                                focusOnView(bikeVehicleModel);
                                Alert("Oops !!!", "Please enter Vehicle Model for Motor Bike.");
                                return false;
                            } else if (bikeMakeYear.getText().toString().equals("")) {
                                Alert("Oops !!!", "Please select Year of Manufacturer for Motor Bike.");
                                return false;
                            } else if (bikeRegistrationNumber.getText().toString().equals("")) {
                                bikeRegistrationNumber.requestFocus();
                                focusOnView(bikeRegistrationNumber);
                                Alert("Oops !!!", "Please enter Vehicle Registration Number for Motor Bike.");
                                return false;
                            }
                        } else if (useSameBike.isChecked()) {
                            if (bikeFleet.getText().toString().equals("")) {
                                Alert("Oops !!!", "Please select fleet type for Motor Bike.");
                                return false;
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!termCheck.isChecked() && Location.vehicalSelectedData.equals("")) {
                Alert("Alert!", "Please accept terms & conditions.");
                return false;
            }
        } else if (!termCheck.isChecked() && Location.vehicalSelectedData.equals("")) {
            Alert("Alert!", "Please accept terms & conditions.");
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 101) {
                if (data.getStringExtra("category").equals("1")) {
                    taxiVehicleMaker.setText(data.getStringExtra("name"));
                    if (data.getStringExtra("name").equals("Other Manufacturer")) {
                        taxiMakerOther.setVisibility(View.VISIBLE);
                        taxiMakerOther.requestFocus();
                        taxiVehicleModel.setVisibility(View.GONE);
                        taxiModelLine.setVisibility(View.GONE);
                        taxiModelID = "0";
                        taxiModelName = "0";
                        taxiModelOther.setVisibility(View.VISIBLE);
                    } else {
                        taxiMakerOther.setVisibility(View.GONE);
                        taxiVehicleModel.setVisibility(View.VISIBLE);
                        taxiModelLine.setVisibility(View.VISIBLE);
                        taxiModelOther.setVisibility(View.GONE);
                    }
                    taxiVehicleModel.setText("");
                    taxiMakeYear.setText("");
                    taxiMakerID = data.getStringExtra("id");
                    taxiMakerName = data.getStringExtra("name");
                } else if (data.getStringExtra("category").equals("2")) {
                    hireVehicleMaker.setText(data.getStringExtra("name"));
                    if (data.getStringExtra("name").equals("Other Manufacturer")) {
                        hireMakerOther.setVisibility(View.VISIBLE);
                        hireMakerOther.requestFocus();
                        hireVehicleModel.setVisibility(View.GONE);
                        hireModelLine.setVisibility(View.GONE);
                        hireModelOther.setVisibility(View.VISIBLE);
                        hireModelID = "0";
                        hireModelName = "0";
                    } else {
                        hireMakerOther.setVisibility(View.GONE);
                        hireVehicleModel.setVisibility(View.VISIBLE);
                        hireModelLine.setVisibility(View.VISIBLE);
                        hireModelOther.setVisibility(View.GONE);
                    }
                    hireVehicleModel.setText("");
                    hireMakeYear.setText("");
                    hireMakerID = data.getStringExtra("id");
                    hireMakerName = data.getStringExtra("name");
                } else if (data.getStringExtra("category").equals("3")) {
                    shareVehicleMaker.setText(data.getStringExtra("name"));
                    if (data.getStringExtra("name").equals("Other Manufacturer")) {
                        shareMakerOther.setVisibility(View.VISIBLE);
                        shareMakerOther.requestFocus();
                        shareVehicleModel.setVisibility(View.GONE);
                        shareModelLine.setVisibility(View.GONE);
                        shareModelOther.setVisibility(View.VISIBLE);
                        shareModelName = "0";
                        shareModelID = "0";
                    } else {
                        shareMakerOther.setVisibility(View.GONE);
                        shareVehicleModel.setVisibility(View.VISIBLE);
                        shareModelLine.setVisibility(View.VISIBLE);
                        shareModelOther.setVisibility(View.GONE);
                    }
                    shareVehicleModel.setText("");
                    shareMakeYear.setText("");
                    shareMakerID = data.getStringExtra("id");
                    shareMakerName = data.getStringExtra("name");
                }
            } else if (requestCode == 102) {
                if (data.getStringExtra("category").equals("1")) {
                    taxiVehicleModel.setText(data.getStringExtra("name"));
                    if (data.getStringExtra("name").equals("Other Model")) {
                        taxiModelOther.setVisibility(View.VISIBLE);
                        taxiModelOther.requestFocus();
                    } else
                        taxiModelOther.setVisibility(View.GONE);
                    taxiMakeYear.setText("");
                    taxiModelID = data.getStringExtra("id");
                    taxiModelName = data.getStringExtra("name");
                } else if (data.getStringExtra("category").equals("2")) {
                    hireVehicleModel.setText(data.getStringExtra("name"));
                    if (data.getStringExtra("name").equals("Other Model")) {
                        hireModelOther.setVisibility(View.VISIBLE);
                        hireModelOther.requestFocus();
                    } else
                        hireModelOther.setVisibility(View.GONE);
                    hireMakeYear.setText("");
                    hireModelID = data.getStringExtra("id");
                    hireModelName = data.getStringExtra("name");
                } else if (data.getStringExtra("category").equals("3")) {
                    shareVehicleModel.setText(data.getStringExtra("name"));
                    if (data.getStringExtra("name").equals("Other Model")) {
                        shareModelOther.setVisibility(View.VISIBLE);
                        shareModelOther.requestFocus();
                    } else
                        shareModelOther.setVisibility(View.GONE);
                    shareMakeYear.setText("");
                    shareModelID = data.getStringExtra("id");
                    shareModelName = data.getStringExtra("name");
                }
            }
        }
    }

    int selectedFleet;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.taxi_fleet_type:
                selectedFleet = 1;
                /*if (fleetData.size() > 0)
                    showRideDialog(1);
                else */getFleets("1");
                break;
            case R.id.economy_fleet_type:
                selectedFleet = 2;
                /*if (fleetData.size() > 0)
                    showRideDialog(2);
                else */getFleets("2");
                break;
            case R.id.premium_fleet_type:
                selectedFleet = 3;
                /*if (fleetData.size() > 0)
                    showRideDialog(3);
                else */getFleets("3");
                break;
            case R.id.bike_fleet_type:
                selectedFleet = 4;
                /*if (fleetData.size() > 0)
                    showRideDialog(4);
                else */getFleets("4");
                break;
            case R.id.btn_terms:
                startActivity(new Intent(this, TermsConditions.class));
                break;
            case R.id.btn_use_same:
                if (useSame.isChecked()) {
                    useSame.setChecked(false);
                    updateUI();
                } else {
                    useSame.setChecked(true);
                    hideAllCategories();
                }
                break;
            case R.id.btn_use_same_taxi:
                if (useSameTaxi.isChecked()) {
                    useSameTaxi.setChecked(false);
                    showHideTaxiInfo(true);
                } else {
                    useSameTaxi.setChecked(true);
                    showHideTaxiInfo(false);
                }
                break;
            case R.id.btn_use_same_economy:
                if (useSameEconomy.isChecked()) {
                    useSameEconomy.setChecked(false);
                    showHideEconomyInfo(true);
                } else {
                    useSameEconomy.setChecked(true);
                    showHideEconomyInfo(false);
                }
                break;
            case R.id.btn_use_same_premium:
                if (useSamePremium.isChecked()) {
                    useSamePremium.setChecked(false);
                    showHidePremiumInfo(true);
                } else {
                    useSamePremium.setChecked(true);
                    showHidePremiumInfo(false);
                }
                break;
            case R.id.btn_use_same_bike:
                if (useSameBike.isChecked()) {
                    useSameBike.setChecked(false);
                    showHideBikeInfo(true);
                } else {
                    useSameBike.setChecked(true);
                    showHideBikeInfo(false);
                }
                break;
            case R.id.continu:
                if (isValid()) {
                    registerNewUser();
                }
                break;

            //for Taxi Category
            case R.id.taxi_vehicle_type:
                showRideDialog(1, 1, taxiVehicleType, taxiVehicleMaker, taxiVehicleModel, taxiMakeYear);
                break;
            case R.id.taxi_maker:
                if (taxiVehicleType.getText().length() > 0) {
                    startActivityForResult(new Intent(this, VehicleModelManufacture.class)
                            .putExtra("endPoint", Config.MAKER_LIST)
                            .putExtra("title", "Select Manufacturer")
                            .putExtra("type", "1")
                            .putExtra("category", "1"), 101);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    /*if (getMakerList().length > 0)
                        showRideDialog(2, 1, taxiVehicleType, taxiVehicleMaker, taxiVehicleModel, taxiMakeYear);
                    else Alert("Alert!", "No data found. Please select other vehicle type.");*/
                } else
                    Alert("Alert!", "Please select taxi vehicle type first.");
                break;
            case R.id.taxi_model:
                if (taxiVehicleMaker.getText().length() > 0) {
                    startActivityForResult(new Intent(this, VehicleModelManufacture.class)
                            .putExtra("endPoint", Config.MODEL_LIST + "?type_id=" + taxiVehicleID + "&brand_id=" + taxiMakerID)
                            .putExtra("title", "Select Model")
                            .putExtra("type", "2")
                            .putExtra("category", "1"), 102);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    /*if(getModelList().length > 0)
                        showRideDialog(3, 1, taxiVehicleType, taxiVehicleMaker, taxiVehicleModel, taxiMakeYear);
                    else Alert("Alert!", "No data found. Please select other vehicle manufacturer.");*/
                } else
                    Alert("Alert!", "Please select taxi manufacturer first.");
                break;
            case R.id.taxi_make_year:
                showRideDialog(4, 1, taxiVehicleType, taxiVehicleMaker, taxiVehicleModel, taxiMakeYear);
                break;

            //for Hire Car Category
            case R.id.hire_vehicle_type:
                showRideDialog(1, 2, hireVehicleType, hireVehicleMaker, hireVehicleModel, hireMakeYear);
                break;
            case R.id.hire_maker:
                if (hireVehicleType.getText().length() > 0) {
                    startActivityForResult(new Intent(this, VehicleModelManufacture.class)
                            .putExtra("endPoint", Config.MAKER_LIST)
                            .putExtra("title", "Select Manufacturer")
                            .putExtra("type", "1")
                            .putExtra("category", "2"), 101);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    /*if(getMakerList().length > 0)
                        showRideDialog(2, 2, hireVehicleType, hireVehicleMaker, hireVehicleModel, hireMakeYear);
                    else Alert("Alert!", "No data found. Please select other vehicle type.");*/
                } else
                    Alert("Alert!", "Please select premium vehicle type first.");
                break;
            case R.id.hire_model:
                if (hireVehicleMaker.getText().length() > 0) {
                    startActivityForResult(new Intent(this, VehicleModelManufacture.class)
                            .putExtra("endPoint", Config.MODEL_LIST + "?type_id=" + hireVehicleID + "&brand_id=" + hireMakerID)
                            .putExtra("title", "Select Model")
                            .putExtra("type", "2")
                            .putExtra("category", "2"), 102);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    /*if(getModelList().length > 0)
                        showRideDialog(3, 2, hireVehicleType, hireVehicleMaker, hireVehicleModel, hireMakeYear);
                    else Alert("Alert!", "No data found. Please select other vehicle manufacturer.");*/
                } else
                    Alert("Alert!", "Please select premium manufacturer first.");
                break;
            case R.id.hire_make_year:
                showRideDialog(4, 2, hireVehicleType, hireVehicleMaker, hireVehicleModel, hireMakeYear);
                break;

            //for Ride Share Category
            case R.id.ride_vehicle_type:
                showRideDialog(1, 3, shareVehicleType, shareVehicleMaker, shareVehicleModel, shareMakeYear);
                break;
            case R.id.ride_maker:
                if (shareVehicleType.getText().length() > 0) {
                    startActivityForResult(new Intent(this, VehicleModelManufacture.class)
                            .putExtra("endPoint", Config.MAKER_LIST)
                            .putExtra("title", "Select Manufacturer")
                            .putExtra("type", "1")
                            .putExtra("category", "3"), 101);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    /*if (getMakerList().length > 0)
                        showRideDialog(2, 3, shareVehicleType, shareVehicleMaker, shareVehicleModel, shareMakeYear);
                    else Alert("Alert!", "No data found. Please select other vehicle type.");*/
                } else
                    Alert("Alert!", "Please select economy vehicle type first.");
                break;
            case R.id.ride_model:
                if (shareVehicleMaker.getText().length() > 0) {
                    startActivityForResult(new Intent(this, VehicleModelManufacture.class)
                            .putExtra("endPoint", Config.MODEL_LIST + "?type_id=" + shareVehicleID + "&brand_id=" + shareMakerID)
                            .putExtra("title", "Select Model")
                            .putExtra("type", "2")
                            .putExtra("category", "3"), 102);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    /*if(getModelList().length > 0)
                        showRideDialog(3, 3, shareVehicleType, shareVehicleMaker, shareVehicleModel, shareMakeYear);
                    else Alert("Alert!", "No data found. Please select other vehicle manufacturer.");*/
                } else
                    Alert("Alert!", "Please select economy manufacturer first.");
                break;
            case R.id.ride_make_year:
                showRideDialog(4, 3, shareVehicleType, shareVehicleMaker, shareVehicleModel, shareMakeYear);
                break;

            // for Motor Bike Category
            case R.id.bike_vehicle_type:
                showRideDialog(1, 4, bikeVehicleType, bikeVehicleMaker, bikeVehicleModel, bikeMakeYear);
                break;
            case R.id.bike_maker:
                if (bikeVehicleType.getText().length() > 0) {
                    if (getMakerList().length > 0)
                        showRideDialog(2, 4, bikeVehicleType, bikeVehicleMaker, bikeVehicleModel, bikeMakeYear);
                    else Alert("Alert!", "No data found. Please select other vehicle type.");
                } else
                    Alert("Alert!", "Please select motor bike vehicle type first.");
                break;
            case R.id.bike_model:
                if (bikeVehicleMaker.getText().length() > 0) {
                    if (getModelList().length > 0)
                        showRideDialog(3, 4, bikeVehicleType, bikeVehicleMaker, bikeVehicleModel, bikeMakeYear);
                    else
                        Alert("Alert!", "No data found. Please select other vehicle manufacturer.");
                } else
                    Alert("Alert!", "Please select motor bike manufacturer first.");
                break;
            case R.id.bike_make_year:
                showRideDialog(4, 4, bikeVehicleType, bikeVehicleMaker, bikeVehicleModel, bikeMakeYear);
                break;
        }
    }

    private void showHideTaxiInfo(boolean visible) {
        if (visible) {
            layTaxiInfo.setVisibility(View.VISIBLE);
            layTaxiFleet.setVisibility(View.GONE);
        } else {
            layTaxiInfo.setVisibility(View.GONE);
            layTaxiFleet.setVisibility(View.VISIBLE);
            /*if(fleetData.size() == 0)
                getFleets("");*/
        }
    }

    private void showHideEconomyInfo(boolean visible) {
        if (visible) {
            layEconomyInfo.setVisibility(View.VISIBLE);
            layEconomyFleet.setVisibility(View.GONE);
        } else {
            layEconomyInfo.setVisibility(View.GONE);
            layEconomyFleet.setVisibility(View.VISIBLE);
        }
    }

    private void showHidePremiumInfo(boolean visible) {
        if (visible) {
            layPremiumInfo.setVisibility(View.VISIBLE);
            layPremiumFleet.setVisibility(View.GONE);
        } else {
            layPremiumInfo.setVisibility(View.GONE);
            layPremiumFleet.setVisibility(View.VISIBLE);
        }
    }

    private void showHideBikeInfo(boolean visible) {
        if (visible) {
            layBikeInfo.setVisibility(View.VISIBLE);
            layBikeFleet.setVisibility(View.GONE);
        } else {
            layBikeInfo.setVisibility(View.GONE);
            layBikeFleet.setVisibility(View.VISIBLE);
        }
    }

    public void showRideDialog(final int type) {
        final Dialog mDialog = new Dialog(VehicleInfo.this, R.style.rideDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list);
        mDialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);

        title.setText("Select Fleet Type");

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

        adapter = new ArrayAdapter<>(this,
                R.layout.simple_list_item, R.id.textItem, getFleetList());

        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (type == 1) {
                    taxiFleet.setText(parent.getItemAtPosition(position).toString());
                    taxiFleetID = getFleetId(taxiFleet.getText().toString());
                } else if (type == 2) {
                    economyFleet.setText(parent.getItemAtPosition(position).toString());
                    economyFleetID = getFleetId(economyFleet.getText().toString());
                } else if (type == 3) {
                    premiumFleet.setText(parent.getItemAtPosition(position).toString());
                    premiumFleetID = getFleetId(premiumFleet.getText().toString());
                } else if (type == 4) {
                    bikeFleet.setText(parent.getItemAtPosition(position).toString());
                    bikeFleetID = getFleetId(bikeFleet.getText().toString());
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public void showRideDialog(final int type, final int category, final TextView vehicleType, final TextView vehicleMaker,
                               final TextView vehicleModel, final TextView makeYear) {
        final Dialog mDialog = new Dialog(VehicleInfo.this, R.style.rideDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list);
        mDialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);

        if (type == 1)
            title.setText("Select Vehicle Type");
        else if (type == 2)
            title.setText("Select Vehicle Manufacturer");
        else if (type == 3)
            title.setText("Select Vehicle Model");
        else if (type == 4)
            title.setText("Select year of manufacture");

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
            adapter = new ArrayAdapter<>(this,
                    R.layout.simple_list_item, R.id.textItem, getVehicleList());
        else if (type == 2)
            adapter = new ArrayAdapter<>(this,
                    R.layout.simple_list_item, R.id.textItem, getMakerList());
        else if (type == 3)
            adapter = new ArrayAdapter<>(this,
                    R.layout.simple_list_item, R.id.textItem, getModelList());
        else if (type == 4)
            adapter = new ArrayAdapter<>(this,
                    R.layout.simple_list_item, R.id.textItem, getMakeYearList());

        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (type == 1) {
                    vehicleType.setText(parent.getItemAtPosition(position).toString());
                    vehicleMaker.setText("");
                    vehicleModel.setText("");
                    makeYear.setText("");
                    if (category == 1) {
                        taxiVehicleID = getVehicleId(parent.getItemAtPosition(position).toString());
                        taxiVehicleName = parent.getItemAtPosition(position).toString();
                    } else if (category == 2) {
                        hireVehicleID = getVehicleId(parent.getItemAtPosition(position).toString());
                        hireVehicleName = vehicleType.getText().toString();
                    } else if (category == 3) {
                        shareVehicleID = getVehicleId(parent.getItemAtPosition(position).toString());
                        shareVehicleName = vehicleType.getText().toString();
                    } else if (category == 4) {
                        bikeVehicleID = getVehicleId(parent.getItemAtPosition(position).toString());
                        bikeVehicleName = vehicleType.getText().toString();
                    }
                    //getVehicleMaker();
                } else if (type == 2) {
                    vehicleMaker.setText(parent.getItemAtPosition(position).toString());
                    vehicleModel.setText("");
                    makeYear.setText("");
                    if (category == 1) {
                        taxiMakerID = getMakerId(parent.getItemAtPosition(position).toString());
                        taxiMakerName = vehicleMaker.getText().toString();
                    } else if (category == 2) {
                        hireMakerID = getMakerId(parent.getItemAtPosition(position).toString());
                        hireMakerName = vehicleMaker.getText().toString();
                    } else if (category == 3) {
                        shareMakerID = getMakerId(parent.getItemAtPosition(position).toString());
                        shareMakerName = vehicleMaker.getText().toString();
                    } else if (category == 4) {
                        bikeMakerID = getMakerId(parent.getItemAtPosition(position).toString());
                        bikeMakerName = vehicleMaker.getText().toString();
                    }
                    //getVehicleModel();
                } else if (type == 3) {
                    vehicleModel.setText(parent.getItemAtPosition(position).toString());
                    makeYear.setText("");
                    if (category == 1) {
                        taxiModelID = getModelId(parent.getItemAtPosition(position).toString());
                        taxiModelName = vehicleModel.getText().toString();
                    } else if (category == 2) {
                        hireModelID = getModelId(parent.getItemAtPosition(position).toString());
                        hireModelName = vehicleModel.getText().toString();
                    } else if (category == 3) {
                        shareModelID = getModelId(parent.getItemAtPosition(position).toString());
                        shareModelName = vehicleModel.getText().toString();
                    } else if (category == 4) {
                        bikeModelID = getModelId(parent.getItemAtPosition(position).toString());
                        bikeModelName = vehicleModel.getText().toString();
                    }
                    //getMakeYear();
                } else if (type == 4) {
                    makeYear.setText(parent.getItemAtPosition(position).toString());
                    if (category == 1)
                        taxiModelYear = parent.getItemAtPosition(position).toString();
                    else if (category == 2)
                        hireModelYear = parent.getItemAtPosition(position).toString();
                    else if (category == 3)
                        shareModelYear = parent.getItemAtPosition(position).toString();
                    else if (category == 4)
                        bikeModelYear = parent.getItemAtPosition(position).toString();
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public String[] getVehicleList() {
        String[] listContent = new String[vehicleData.size()];
        for (int i = 0; i < vehicleData.size(); i++) {
            listContent[i] = vehicleData.get(i).get(Key.TYPE);
        }
        return listContent;
    }

    public String[] getFleetList() {
        String[] listContent = new String[fleetData.size()];
        for (int i = 0; i < fleetData.size(); i++) {
            listContent[i] = fleetData.get(i).getName();
        }
        return listContent;
    }

    public String getFleetId(String name) {
        for (int i = 0; i < fleetData.size(); i++) {
            if (fleetData.get(i).getName().equalsIgnoreCase(name))
                return "" + fleetData.get(i).getId();
        }
        return "";
    }

    public String getVehicleId(String countryName) {
        for (int i = 0; i < vehicleData.size(); i++) {
            if (vehicleData.get(i).get(Key.TYPE).equalsIgnoreCase(countryName))
                return vehicleData.get(i).get(Key.ID);
        }
        return "";
    }

    public String getMakerId(String stateName) {
        for (int i = 0; i < makerData.size(); i++) {
            if (makerData.get(i).get(Key.NAME).equalsIgnoreCase(stateName))
                return makerData.get(i).get(Key.ID);
        }
        return "";
    }

    public String[] getMakerList() {
        String[] listContent = new String[makerData.size()];
        for (int i = 0; i < makerData.size(); i++) {
            listContent[i] = makerData.get(i).get(Key.NAME);
        }
        return listContent;
    }

    public String getModelId(String stateName) {
        for (int i = 0; i < modelData.size(); i++) {
            if (modelData.get(i).get(Key.NAME).equalsIgnoreCase(stateName))
                return modelData.get(i).get(Key.ID);
        }
        return "";
    }

    public String[] getModelList() {
        String[] listContent = new String[modelData.size()];
        for (int i = 0; i < modelData.size(); i++) {
            listContent[i] = modelData.get(i).get(Key.NAME);
        }
        return listContent;
    }

    public String[] getMakeYearList() {
        createYearList();
        String[] listContent = new String[makeYearData.size()];
        for (int i = 0; i < makeYearData.size(); i++) {
            listContent[i] = makeYearData.get(i);
        }
        return listContent;
    }

    int type;
    ArrayList<HashMap<String, String>> vehicleData = new ArrayList<>();
    ArrayList<HashMap<String, String>> makerData = new ArrayList<>();
    ArrayList<HashMap<String, String>> modelData = new ArrayList<>();
    ArrayList<Fleet> fleetData = new ArrayList<>();
    ArrayList<String> makeYearData = new ArrayList<String>();
    String categoryID, taxiVehicleID, taxiModelID, taxiMakerID, taxiModelYear,
            hireVehicleID, hireModelID, hireMakerID, hireModelYear,
            shareVehicleID, shareModelID, shareMakerID, shareModelYear,
            bikeVehicleID = "0", bikeModelID = "0", bikeMakerID = "0", bikeModelYear,
            taxiVehicleName, hireVehicleName, shareVehicleName, bikeVehicleName;
    String taxiMakerName, hireMakerName, shareMakerName, bikeMakerName, taxiModelName,
            hireModelName, bikeModelName, shareModelName;

    String taxiFleetID = "0", economyFleetID = "0", premiumFleetID = "0", bikeFleetID = "0";

    private void getVehicleType(String categoryId) {
        type = 1;
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("category_id", categoryId);
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this, null);
            apiHandler.execute(Config.VEHICLE_LIST, "");
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void getVehicleMaker() {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("category_id", categoryID.substring(0, categoryID.indexOf(",")));
        type = 2;
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this, null);
            apiHandler.execute(Config.MAKER_LIST, "");
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void getFleets(String categoryId) {
        type = 11;
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this, null);
            apiHandler.execute(Config.AVAILABLE_FLEET + "?id=" + categoryId, "");
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
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

    private void handleResponse(String result) {
        try {
            JSONObject outerJson = new JSONObject(result);
            if (Integer.parseInt(outerJson.getString(Key.STATUS)) == APIStatus.SUCCESS) {
                if (type == 11) {
                    fleetData.clear();
                    JSONObject data = outerJson.getJSONObject(Key.DATA);
                    JSONArray items = data.getJSONArray(Key.ITEMS);
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject fleetObj = items.getJSONObject(i);
                        Fleet fleet = new Fleet();
                        fleet.setId(fleetObj.getInt("id"));
                        fleet.setName(fleetObj.getString("name"));
                        fleet.setStatus(fleetObj.getInt("status"));
                        fleetData.add(fleet);
                    }
                    showRideDialog(selectedFleet);
                } else if (type == 10) {
                    deleteFiles(session.getTaxiImgPath());
                    deleteFiles(session.getLicenceImgPath());
                    deleteFiles(session.getProfileImgPath());
                    session.saveTaxiImgPath("");
                    session.saveLicenceImgPath("");
                    session.saveProfileImgPath("");
                    session.clearAddress();
                    session.clearLocation();
                    session.clearProfile();
                    JSONObject dataObj;
                    if (outerJson.get(Key.DATA) instanceof JSONArray) {
                        dataObj = outerJson.getJSONArray(Key.DATA).getJSONObject(0);
                    } else
                        dataObj = outerJson.getJSONObject(Key.DATA);
                    if (!Location.vehicalSelectedData.equals("")) {
                        session.saveDriverData(
                                dataObj.getString(Key.ID),
                                dataObj.getString(Key.USER_TYPE),
                                dataObj.getString(Key.USERNAME),
                                dataObj.getString(Key.IMAGE),
                                dataObj.getString(Key.EMAIL),
                                dataObj.getString(Key.TOKEN),
                                dataObj.getString(Key.FIRST_NAME),
                                dataObj.getString(Key.LAST_NAME),
                                dataObj.getString(Key.PHONE),
                                dataObj.getString(Key.DIAL_CODE),
                                "", "", dataObj.has(Key.IS_CUSTOMER) ? dataObj.getString(Key.IS_CUSTOMER) : "0");
                    }
                    session.saveUserActualType(dataObj.getString(Key.USER_TYPE));
                    if (dataObj.getString(Key.USER_TYPE).equals("5") && getIntent().hasExtra("upgrade"))
                        showProceedDialog(outerJson, outerJson.getString(Key.MESSAGE));
                    else
                        Success("Success!", outerJson.getString(Key.MESSAGE));
                } else {
                    if (type == 1)
                        vehicleData.clear();
                    else if (type == 2)
                        makerData.clear();
                    else if (type == 3)
                        modelData.clear();
                    else if (type == 4)
                        makeYearData.clear();

                    //JSONObject innerObj = outerJson.getJSONObject(Key.DATA);
                    JSONArray outerArray = outerJson.getJSONArray(Key.DATA);

                    for (int i = 0; i < outerArray.length(); i++) {
                        if (type == 4) {
                            makeYearData.add(outerArray.get(i).toString());
                        } else {
                            JSONObject innerJson = outerArray.getJSONObject(i);
                            if (type == 1) {
                                HashMap<String, String> map = new HashMap<>();
                                map.put(Key.ID, innerJson.get(Key.ID).toString());
                                map.put(Key.TYPE, innerJson.getString(Key.TYPE));
                                vehicleData.add(map);
                            } else if (type == 2) {
                                HashMap<String, String> map = new HashMap<>();
                                map.put(Key.ID, innerJson.get(Key.ID).toString());
                                map.put(Key.NAME, innerJson.getString(Key.NAME));
                                makerData.add(map);
                            } else if (type == 3) {
                                HashMap<String, String> map = new HashMap<>();
                                map.put(Key.ID, innerJson.get(Key.ID).toString());
                                map.put(Key.NAME, innerJson.getString(Key.NAME));
                                modelData.add(map);
                            }
                        }
                    }
                    hideSoftKeyboard();
                    /*if(vehicleType == 1){
                        showRideDialog(1, 1 ,taxiVehicleType, taxiVehicleMaker, taxiVehicleModel, taxiMakeYear);
                    } else if(vehicleType == 2){
                        showRideDialog(1, 2 ,taxiVehicleType, taxiVehicleMaker, taxiVehicleModel, taxiMakeYear);
                    } else if(vehicleType == 3){
                        showRideDialog(1, 3 ,taxiVehicleType, taxiVehicleMaker, taxiVehicleModel, taxiMakeYear);
                    } else if(vehicleType == 4){
                        showRideDialog(1, 4 ,taxiVehicleType, taxiVehicleMaker, taxiVehicleModel, taxiMakeYear);
                    }*/
                }
            } else
                Alert("Alert!", outerJson.getJSONArray("data").getJSONObject(0).getString(Key.MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(VehicleInfo.this, true);
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

    @SuppressLint("WrongConstant")
    public void proceed() {
        Intent intent;
        if (Location.driverSelectedData.equals("")) {
            session.saveToken("");
            intent = new Intent(getApplicationContext(), SignIn.class);
        } else
            intent = new Intent(getApplicationContext(), DriverHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        VehicleInfo.this.finish();
    }

    String token = "";

    private void showProceedDialog(final JSONObject outJson, String message) {
        try {
            token = outJson.getJSONObject(Key.DATA).getString(Key.TOKEN);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final AlertDialog mDialog = new AlertDialog(VehicleInfo.this, false);
        mDialog.setDialogTitle("Success!");
        mDialog.setDialogMessage(message + " Please select if you would like to proceed as a driver or passenger.");
        mDialog.setCancelOnTouchOutside(false);
        mDialog.setPositiveButton(getResources().getString(R.string.proceed_as_driver), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                proceed();
            }
        });
        mDialog.setNegativeButton(getResources().getString(R.string.proceed_as_passenger), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                loggedInAs("passenger", token);
            }
        });
        mDialog.show();
    }

    @SuppressLint("WrongConstant")
    private void proceedAsPassenger(String userType) {
        new SessionHandler(this).saveUserType(userType);
        Intent intent;
        if (userType.equals("3"))
            intent = new Intent(getApplicationContext(), PassengerHome.class);
        else
            intent = new Intent(getApplicationContext(), DriverHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        VehicleInfo.this.finish();
    }

    private void loggedInAs(final String userType, String token) {
        if (Internet.hasInternet(this)) {
            APIHandler apiCall = new APIHandler(this, HTTPMethods.GET, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    try {
                        JSONObject outerJson = new JSONObject(result);
                        if (outerJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                            proceedAsPassenger("3");
                        } else if (outerJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                            Alert("Alert!", outerJson.getString(Key.MESSAGE));
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

    private void Success(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(VehicleInfo.this, true);
        mDialog.setDialogTitle(title);
        if (message.equals("")) {
            if (Location.vehicalSelectedData.equals(""))
                message = "Thanks, Your account with BookMyRide has been created successfully.";
            else
                message = "Your profile has been updated successfully.";
        }
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                proceed();
            }
        });
        mDialog.show();
    }

    String accreditationImg = "";
    String licenceImg = "";
    String profileImg = "";

    private void registerNewUser() {
        categoryID = "";
        type = 10;
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        if (!session.getTaxiImgPath().equals("") && categoryExists(categoryData, "Taxi")) {
            try {
                accreditationImg = FileOperation.encodeFileToBase64Binary(session.getTaxiImgPath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            jsonParams.put(Key.pTaxiAccreditation, accreditationImg);
        }/* else {
            jsonParams.put(Key.pTaxiAccreditation, "");
        }*/

        if (!session.getLicenceImgPath().equals("")) {
            try {
                licenceImg = FileOperation.encodeFileToBase64Binary(session.getLicenceImgPath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            jsonParams.put(Key.pLicenceImg, licenceImg);
        } //else jsonParams.put(Key.pLicenceImg, "");

        if (!session.getProfileImgPath().equals("") && !getIntent().hasExtra("social_data")) {
            try {
                profileImg = FileOperation.encodeFileToBase64Binary(session.getProfileImgPath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            jsonParams.put(Key.pProfileImg, profileImg);
        } //else jsonParams.put(Key.pProfileImg, "");

        //vehicle data
        JSONArray jsonArray = new JSONArray();
        //if(!useSame.isChecked()) {
        try {
            categoryData = new JSONArray(session.getLocation().getCategory());
            for (int i = 0; i < categoryData.length(); i++) {
                // Taxi Category data
                if (categoryData.getJSONObject(i).getString("name").equals("Taxi")) {
                    try {
                        JSONObject taxiObj = new JSONObject();
                        if (!useSameTaxi.isChecked()) {
                            taxiObj.put(Key.CATEGORY, categoryData.getJSONObject(i).getString("id"));
                            taxiObj.put(Key.VEHICLE_TYPE, "Taxi");
                            taxiObj.put(Key.TYPE, taxiVehicleID);
                            taxiObj.put(Key.TYPE_NAME, taxiVehicleName);
                            taxiObj.put(Key.MAKER, taxiMakerID);
                            taxiObj.put(Key.MAKER_NAME, taxiMakerName);
                            taxiObj.put(Key.MAKER_NAME_OTHER, taxiMakerOther.getText().toString());
                            taxiObj.put(Key.MODEL, taxiModelID);
                            taxiObj.put(Key.MODEL_NAME, taxiModelName);
                            taxiObj.put(Key.MODEL_NAME_OTHER, taxiModelOther.getText().toString());
                            taxiObj.put(Key.YEAR, taxiModelYear);
                            taxiObj.put(Key.FLEET_ID, "0");
                            taxiObj.put(Key.REGISTRATION_NUMBER, taxiRegistrationNumber.getText().toString());
                            taxiObj.put(Key.AC, hasTaxiAC ? "1" : "0");
                            taxiObj.put(Key.FLEET_NAME, "");
                        } else {
                            taxiObj.put(Key.FLEET_ID, taxiFleetID);
                            taxiObj.put(Key.FLEET_NAME, taxiFleet.getText().toString());
                            taxiObj.put(Key.CATEGORY, categoryData.getJSONObject(i).getString("id"));
                            taxiObj.put(Key.VEHICLE_TYPE, "Taxi");
                            taxiObj.put(Key.TYPE, "0");
                            taxiObj.put(Key.TYPE_NAME, "0");
                            taxiObj.put(Key.MAKER, "0");
                            taxiObj.put(Key.MAKER_NAME, "0");
                            taxiObj.put(Key.MAKER_NAME_OTHER, "0");
                            taxiObj.put(Key.MODEL, "0");
                            taxiObj.put(Key.MODEL_NAME, "0");
                            taxiObj.put(Key.MODEL_NAME_OTHER, "0");
                            taxiObj.put(Key.YEAR, "0");
                            taxiObj.put(Key.REGISTRATION_NUMBER, "0");
                            taxiObj.put(Key.AC, "0");
                        }
                        taxiObj.put(Key.IS_FLEET_SELECTED, useSameTaxi.isChecked() ? "1" : "0");
                        jsonArray.put(taxiObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // Ride Share/Economy Category data
                else if (categoryData.getJSONObject(i).getString("name").equals("Economy")) {
                    try {
                        JSONObject rideObj = new JSONObject();
                        if (!useSameEconomy.isChecked()) {
                            rideObj.put(Key.CATEGORY, categoryData.getJSONObject(i).getString("id"));
                            rideObj.put(Key.VEHICLE_TYPE, "Economy");
                            rideObj.put(Key.TYPE, shareVehicleID);
                            rideObj.put(Key.TYPE_NAME, shareVehicleName);
                            rideObj.put(Key.MAKER, shareMakerID);
                            rideObj.put(Key.MAKER_NAME, shareMakerName);
                            rideObj.put(Key.MAKER_NAME_OTHER, shareMakerOther.getText().toString());
                            rideObj.put(Key.MODEL, shareModelID);
                            rideObj.put(Key.MODEL_NAME, shareModelName);
                            rideObj.put(Key.MODEL_NAME_OTHER, shareModelOther.getText().toString());
                            rideObj.put(Key.YEAR, shareModelYear);
                            rideObj.put(Key.REGISTRATION_NUMBER, shareRegistrationNumber.getText().toString());
                            rideObj.put(Key.AC, hasShareAC ? "1" : "0");
                            rideObj.put(Key.FLEET_ID, "0");
                            rideObj.put(Key.FLEET_NAME, "");
                        } else {
                            rideObj.put(Key.FLEET_ID, economyFleetID);
                            rideObj.put(Key.FLEET_NAME, economyFleet.getText().toString());
                            rideObj.put(Key.CATEGORY, categoryData.getJSONObject(i).getString("id"));
                            rideObj.put(Key.VEHICLE_TYPE, "Economy");
                            rideObj.put(Key.TYPE, "0");
                            rideObj.put(Key.TYPE_NAME, "0");
                            rideObj.put(Key.MAKER, "0");
                            rideObj.put(Key.MAKER_NAME, "0");
                            rideObj.put(Key.MAKER_NAME_OTHER, "0");
                            rideObj.put(Key.MODEL, "0");
                            rideObj.put(Key.MODEL_NAME, "0");
                            rideObj.put(Key.MODEL_NAME_OTHER, "0");
                            rideObj.put(Key.YEAR, "0");
                            rideObj.put(Key.REGISTRATION_NUMBER, "0");
                            rideObj.put(Key.AC, "0");
                        }
                        rideObj.put(Key.IS_FLEET_SELECTED, useSameEconomy.isChecked() ? "1" : "0");
                        jsonArray.put(rideObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // Hire Taxi/Premium Category data
                else if (categoryData.getJSONObject(i).getString("name").equals("Premium")) {
                    try {
                        JSONObject hireObj = new JSONObject();
                        if (!useSamePremium.isChecked()) {
                            hireObj.put(Key.CATEGORY, categoryData.getJSONObject(i).getString("id"));
                            hireObj.put(Key.TYPE, hireVehicleID);
                            hireObj.put(Key.VEHICLE_TYPE, "Premium");
                            hireObj.put(Key.TYPE_NAME, hireVehicleName);
                            hireObj.put(Key.MAKER, hireMakerID);
                            hireObj.put(Key.MAKER_NAME, hireMakerName);
                            hireObj.put(Key.MAKER_NAME_OTHER, hireMakerOther.getText().toString());
                            hireObj.put(Key.MODEL, hireModelID);
                            hireObj.put(Key.MODEL_NAME, hireModelName);
                            hireObj.put(Key.MODEL_NAME_OTHER, hireModelOther.getText().toString());
                            hireObj.put(Key.YEAR, hireModelYear);
                            hireObj.put(Key.REGISTRATION_NUMBER, hireRegistrationNumber.getText().toString());
                            hireObj.put(Key.AC, hasHireAC ? "1" : "0");
                            hireObj.put(Key.FLEET_ID, "0");
                            hireObj.put(Key.FLEET_NAME, "");
                        } else {
                            hireObj.put(Key.FLEET_ID, premiumFleetID);
                            hireObj.put(Key.FLEET_NAME, premiumFleet.getText().toString());
                            hireObj.put(Key.CATEGORY, categoryData.getJSONObject(i).getString("id"));
                            hireObj.put(Key.TYPE, "0");
                            hireObj.put(Key.VEHICLE_TYPE, "Premium");
                            hireObj.put(Key.TYPE_NAME, "0");
                            hireObj.put(Key.MAKER, "0");
                            hireObj.put(Key.MAKER_NAME, "0");
                            hireObj.put(Key.MAKER_NAME_OTHER, "0");
                            hireObj.put(Key.MODEL, "0");
                            hireObj.put(Key.MODEL_NAME, "0");
                            hireObj.put(Key.MODEL_NAME_OTHER, "0");
                            hireObj.put(Key.YEAR, "0");
                            hireObj.put(Key.REGISTRATION_NUMBER, "0");
                            hireObj.put(Key.AC, "0");
                        }
                        hireObj.put(Key.IS_FLEET_SELECTED, useSamePremium.isChecked() ? "1" : "0");
                        jsonArray.put(hireObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // Motor bike Category Data
                else if (categoryData.getJSONObject(i).getString("name").equals("Motor Bike")) {
                    try {
                        JSONObject rideObj = new JSONObject();
                        if (!useSameBike.isChecked()) {
                            rideObj.put(Key.CATEGORY, categoryData.getJSONObject(i).getString("id"));
                            rideObj.put(Key.VEHICLE_TYPE, "Motor Bike");
                            rideObj.put(Key.TYPE, bikeVehicleID);
                            //rideObj.put(Key.TYPE_NAME, bikeVehicleName);
                            rideObj.put(Key.TYPE_NAME, bikeVehicleType.getText().toString());
                            rideObj.put(Key.MAKER, bikeMakerID);
                            //rideObj.put(Key.MAKER_NAME, bikeMakerName);
                            rideObj.put(Key.MAKER_NAME, bikeVehicleMaker.getText().toString());
                            rideObj.put(Key.MAKER_NAME_OTHER, "0");
                            rideObj.put(Key.MODEL, bikeModelID);
                            //rideObj.put(Key.MODEL_NAME, bikeModelName);
                            rideObj.put(Key.MODEL_NAME, bikeVehicleModel.getText().toString());
                            rideObj.put(Key.MODEL_NAME_OTHER, "0");
                            rideObj.put(Key.YEAR, bikeModelYear);
                            rideObj.put(Key.REGISTRATION_NUMBER, bikeRegistrationNumber.getText().toString());
                            rideObj.put(Key.AC, "0");
                            rideObj.put(Key.FLEET_ID, "0");
                            rideObj.put(Key.FLEET_NAME, "");

                        } else {
                            rideObj.put(Key.FLEET_ID, bikeFleetID);
                            rideObj.put(Key.FLEET_NAME, bikeFleet.getText().toString());
                            rideObj.put(Key.CATEGORY, categoryData.getJSONObject(i).getString("id"));
                            rideObj.put(Key.VEHICLE_TYPE, "Motor Bike");
                            rideObj.put(Key.TYPE, "0");
                            rideObj.put(Key.TYPE_NAME, "0");
                            rideObj.put(Key.MAKER, "0");
                            rideObj.put(Key.MAKER_NAME, "0");
                            rideObj.put(Key.MAKER_NAME_OTHER, "0");
                            rideObj.put(Key.MODEL, "0");
                            rideObj.put(Key.MODEL_NAME, "0");
                            rideObj.put(Key.MODEL_NAME_OTHER, "0");
                            rideObj.put(Key.YEAR, "0");
                            rideObj.put(Key.REGISTRATION_NUMBER, "0");
                            rideObj.put(Key.AC, "0");
                        }
                        rideObj.put(Key.IS_FLEET_SELECTED, useSameBike.isChecked() ? "1" : "0");
                        jsonArray.put(rideObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                categoryID += categoryData.getJSONObject(i).getString("id") + ",";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*} else {
            categoryID = getCategoryID();
        }*/

        if (useSame.isChecked()) {
            jsonParams.put(Key.IS_PREMIUM, useSame.isChecked() ? "1" : "0");
        } else {
            jsonParams.put(Key.IS_PREMIUM, "0");
            jsonParams.put(Key.pVehicleDetail, jsonArray.toString());
        }

        // address
        Address address = session.getAddress();
        JSONObject addressObject = new JSONObject();
        try {
            addressObject.put(Key.ADDRESS, address.getAddress());
            addressObject.put(Key.STATE, address.getState());
            addressObject.put(Key.POSTALCODE, address.getPostalCode());
            addressObject.put(Key.COUNTRY, address.getCountry());
            addressObject.put(Key.CITY, address.getCity());
            addressObject.put(Key.LATITUDE, address.getLatitude());
            addressObject.put(Key.LONGITUDE, address.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //location
        com.bookmyride.models.Location loc = session.getLocation();
        jsonParams.put(Key.REFER_BY, referralCode.getText().toString());
        jsonParams.put(Key.IS_CUSTOMER, loc.getIsPassenger());
        if (categoryExists(categoryData, "Taxi"))
            jsonParams.put(Key.pTaxiAccreditationExpiry, loc.getExpiry());
        else
            jsonParams.put(Key.pTaxiAccreditationExpiry, "");
        jsonParams.put(Key.LOCATION_ID, loc.getLocationID());

        //Address object
        jsonParams.put(Key.pAddress, addressObject.toString());

        //profile
        Profile profile = session.getProfile();
        jsonParams.put(Key.pLicenceExpiry, profile.getLicenceExpiry());
        jsonParams.put(Key.pLicenceNumber, profile.getLicenceNumber());
        if (getIntent().hasExtra("upgrade") || session.isBothTypeUser() || loc.getIsPassenger().equals("1"))
            jsonParams.put(Key.USER_TYPE, "5");
        else
            jsonParams.put(Key.USER_TYPE, "4");
        jsonParams.put(Key.FIRST_NAME, profile.getFirstName());
        jsonParams.put(Key.LAST_NAME, profile.getLastName());
        jsonParams.put(Key.USERNAME, profile.getUserName());
        jsonParams.put(Key.EMAIL, profile.getEmail());
        jsonParams.put(Key.PHONE, address.getMobile());
        jsonParams.put(Key.DIAL_CODE, address.getDialCode());

        if (Location.driverSelectedData.equals(""))
            jsonParams.put(Key.PASSWORD_HASH, profile.getPassword());

        jsonParams.put(Key.DRIVER_CATEGORY, "[" + categoryID.substring(0, categoryID.length() - 1) + "]");

        //Appending Social Data
        if (getIntent().hasExtra("social_data")) {
            jsonParams.put(Key.TYPE, socialType);
            jsonParams.put(Key.IDENTIFIER, identifier);
            jsonParams.put(Key.IS_SOCIAL, "1");
            jsonParams.put(Key.pProfileImg, image);
        }
        jsonParams.put("device_token", new SessionHandler(this).getGCMKey());
        jsonParams.put("device", "android");

        if (Internet.hasInternet(this)) {
            APIHandler apiHandler;
            if (!Location.driverSelectedData.equals("")) {
                apiHandler = new APIHandler(this, HTTPMethods.PUT, this, jsonParams);
                apiHandler.execute(Config.CREATE_USER + "/" + session.getUserID(), session.getToken());
            } else {
                apiHandler = new APIHandler(this, HTTPMethods.POST, this, jsonParams);
                if (getIntent().hasExtra("upgrade"))
                    apiHandler.execute(Config.UPGRADE_USER, session.getToken());
                else
                    apiHandler.execute(Config.CREATE_USER, "");
            }
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private String getCategoryID() {
        String categoryID = "";
        try {
            categoryData = new JSONArray(session.getLocation().getCategory());
            for (int i = 0; i < categoryData.length(); i++) {
                categoryID += categoryData.getJSONObject(i).getString("id") + ",";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return categoryID;
    }

    private void createYearList() {
        int currentYear, startYear, endYear;
        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        startYear = currentYear - 14;
        endYear = startYear + 14;
        makeYearData.clear();
        /*for(int i = startYear; i <= endYear; i++){
            makeYearData.add(""+i);
        }*/
        for (int i = endYear; i >= startYear; i--) {
            makeYearData.add("" + i);
        }
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void focusOnView(final EditText input) {
        mScroll.post(new Runnable() {
            @Override
            public void run() {
                mScroll.scrollTo(0, input.getBottom());
            }
        });
    }
}
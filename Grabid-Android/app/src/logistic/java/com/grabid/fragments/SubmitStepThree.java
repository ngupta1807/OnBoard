package com.grabid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.FavoriteGroupSelectionList;
import com.grabid.activities.HomeActivity;
import com.grabid.activities.VehicleList;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.models.Category;
import com.grabid.models.Delivery;
import com.grabid.models.PreviewField;
import com.grabid.models.Shipment;
import com.grabid.util.StorePath;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by vinod on 10/14/2016.
 */
public class SubmitStepThree extends Fragment implements View.OnClickListener,
        AsyncTaskCompleteListener {
    TextView suitableVehicle, puBuildType, puLiftEquipment, doBuildType, doCall, doAppoint, doLiftEquipment,
            puInduction, doInduction, specailPermit, puLiftCategory, txtPULiftCategory,
            doLiftCategory, txtDOLiftCategory, freightDelType, txtSplPermit, mPickComTitle, mdropComTitle;
    RadioGroup geoZone;
    TextView submit;
    SessionManager session;
    ScrollView scroll;
    EditText geo, puSpecialRestriction, doSpecialRestriction, specialPermitDetail, mPickUpComVal, mdropoffComVal;
    String puBuild, puLift, doBuild, doLift, doCallRequire, doAppointment, deliveryID = "",
            doInduct, puInduct, specialPermit;
    int type, liftType, liftSubType;
    Delivery delivery;
    ProgressDialog dialog;
    int cattype;
    String user_Group = "";
    ArrayList<Category> favouritegroupsdata = new ArrayList<>();
    String mUser_Group = "";
    boolean hasGroups = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        View view = inflater.inflate(R.layout.submit_shipment, null);
        init(view);
        if (getArguments().containsKey("data")) {
            HashMap<String, Delivery> map = (HashMap<String, Delivery>) getArguments().getSerializable("data");
            delivery = map.get("data");
            appendData(view);
        }
        getSuitableVehicle();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
    }

    public void UpdateDesign() {
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.seagreen));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.text_color_white));
        HomeActivity.title.setText(getResources().getString(R.string.submitdelivery));
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
    }

    private void appendData(View view) {
        if (delivery.getPuBuildType().equals("1")) {
            puBuild = "1";
            puBuildType.setText("Commercial");
            mPickComTitle.setVisibility(View.VISIBLE);
            mPickUpComVal.setVisibility(View.VISIBLE);
            if (delivery.getPickUpComName() != null && !delivery.getPickUpComName().contentEquals("null"))
                mPickUpComVal.setText(delivery.getPickUpComName());

        } else if (delivery.getPuBuildType().equals("2")) {
            puBuild = "2";
            puBuildType.setText("Residential");
            mPickComTitle.setVisibility(View.GONE);
            mPickUpComVal.setVisibility(View.GONE);

        }

        if (delivery.getDoBuildType().equals("1")) {
            doBuild = "1";
            doBuildType.setText("Commercial");
            mdropComTitle.setVisibility(View.VISIBLE);
            mdropoffComVal.setVisibility(View.VISIBLE);
            if (delivery.getDropOffComName() != null && !delivery.getDropOffComName().contentEquals("null"))
                mdropoffComVal.setText(delivery.getDropOffComName());
        } else if (delivery.getDoBuildType().equals("2")) {
            doBuild = "2";
            doBuildType.setText("Residential");
            mdropComTitle.setVisibility(View.GONE);
            mdropoffComVal.setVisibility(View.GONE);
        }

        if (delivery.getDoCall().equals("1")) {
            doCallRequire = "1";
            doCall.setText("YES");
        } else {
            doCallRequire = "0";
            doCall.setText("NO");
        }

        if (delivery.getDoAppoint().equals("1")) {
            doAppointment = "1";
            doAppoint.setText("YES");
        } else {
            doAppointment = "0";
            doAppoint.setText("NO");
        }

        if (delivery.getDoLiftEquipment().equals("1")) {
            doLift = "1";
            doLiftEquipment.setText("Available on site");
        } else if (delivery.getDoLiftEquipment().equals("0")) {
            doLift = "0";
            doLiftEquipment.setText("Not available on site");
        } else {
            doLift = "2";
            doLiftEquipment.setText("Manual Handling");
        }

        try {
            if (delivery.getPickupinductionRequire().equals("1")) {
                puInduction.setText("YES");
                puInduct = "1";
            } else {
                puInduction.setText("YES");
                puInduct = "0";
            }
        } catch (Exception ex) {

        }
        try {
            if (delivery.getDropoffinductionRequire().equals("1")) {
                doInduction.setText("YES");
                doInduct = "1";
            } else {
                doInduction.setText("YES");
                doInduct = "0";
            }
        } catch (Exception ex) {

        }
        puSpecialRestriction.setText(delivery.getPickupSpecialRestriction());
        doSpecialRestriction.setText(delivery.getDropoffSpecialRestriction());
        try {

            if (delivery.getSpecialPermit().equals("1")) {
                specialPermit = "1";
                specailPermit.setText("Yes");
                txtSplPermit.setVisibility(View.VISIBLE);
                specialPermitDetail.setVisibility(View.VISIBLE);
                specialPermitDetail.setText(delivery.getSpecialPermitDetail());
            } else {
                specialPermit = "0";
                specailPermit.setText("NO");
                txtSplPermit.setVisibility(View.GONE);
                specialPermitDetail.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            ex.toString();
        }
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
        deliveryID = delivery.getId();
        mUser_Group = delivery.getFav_user_id();
        user_Group = delivery.getUser_Group();
    }

    private void init(View view) {
        session = new SessionManager(getActivity());
        suitableVehicle = (TextView) view.findViewById(R.id.suitable_vehicle);
        suitableVehicle.setOnClickListener(this);
        freightDelType = (TextView) view.findViewById(R.id.freight_del_type);
        freightDelType.setOnClickListener(this);
        puBuildType = (TextView) view.findViewById(R.id.pu_build_type);
        puBuildType.setOnClickListener(this);
        puInduction = (TextView) view.findViewById(R.id.pu_induction_require);
        puInduction.setOnClickListener(this);
        doInduction = (TextView) view.findViewById(R.id.do_induction_require);
        doInduction.setOnClickListener(this);
        specailPermit = (TextView) view.findViewById(R.id.special_permit);
        specailPermit.setOnClickListener(this);
        mPickComTitle = (TextView) view.findViewById(R.id.pu_build_type_comtitle);
        mPickUpComVal = (EditText) view.findViewById(R.id.pu_build_type_comname);
        mdropComTitle = (TextView) view.findViewById(R.id.do_build_type_comtitle);
        mdropoffComVal = (EditText) view.findViewById(R.id.do_build_type_comname);
        puLiftCategory = (TextView) view.findViewById(R.id.pu_lift_equip_category);
        puLiftCategory.setOnClickListener(this);
        doLiftCategory = (TextView) view.findViewById(R.id.do_lift_equip_category);
        doLiftCategory.setOnClickListener(this);
        txtPULiftCategory = (TextView) view.findViewById(R.id.txt_pu_lift_equip_category);
        txtDOLiftCategory = (TextView) view.findViewById(R.id.txt_do_lift_equip_category);

        puLiftEquipment = (TextView) view.findViewById(R.id.pu_lift_equip);
        puLiftEquipment.setOnClickListener(this);
        doBuildType = (TextView) view.findViewById(R.id.do_build_type);
        doBuildType.setOnClickListener(this);
        doCall = (TextView) view.findViewById(R.id.do_call);
        doCall.setOnClickListener(this);
        doAppoint = (TextView) view.findViewById(R.id.do_appointment);
        doAppoint.setOnClickListener(this);
        doLiftEquipment = (TextView) view.findViewById(R.id.do_lift_equip);
        doLiftEquipment.setOnClickListener(this);
        geoZone = (RadioGroup) view.findViewById(R.id.geo_zone);
        geoZone.setOnCheckedChangeListener(GEOZoneListener);
        submit = (TextView) view.findViewById(R.id.submit);
        submit.setOnClickListener(this);
        scroll = (ScrollView) view.findViewById(R.id.scrollView);
        geo = (EditText) view.findViewById(R.id.geoRadius);
        puSpecialRestriction = (EditText) view.findViewById(R.id.pu_special_restriction);
        doSpecialRestriction = (EditText) view.findViewById(R.id.do_special_restriction);
        txtSplPermit = (TextView) view.findViewById(R.id.txt_spl_prmt);
        specialPermitDetail = (EditText) view.findViewById(R.id.special_permit_detail);
    }

    public void getfavourites() {
        Intent intent = new Intent(getActivity(), FavoriteGroupSelectionList.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("IsPageNation", false);
        bundle.putString("data", mUser_Group);
        intent.putExtras(bundle);
        startActivityForResult(intent, 16);
    }

    @Override
    public void onTaskComplete(String result) {
        Log.d(Config.TAG, result);
        handleResponse(result);
    }

    String alreadydone = "";
    String PuDoBuilder = "";
    ArrayList<Category> suitableVehicleData = new ArrayList<Category>();
    ArrayList<Category> suitableSelectedVehicleData = new ArrayList<Category>();
    ArrayList<Category> puLiftCategoryData = new ArrayList<Category>();
    ArrayList<Category> doLiftCategoryData = new ArrayList<Category>();

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (Integer.parseInt(outJson.getString(Config.STATUS)) == APIStatus.SUCCESS) {
                Log.v("type", "Type" + type);
                Log.v("lifttype", "liftType" + liftType);
                if (type == 15) {
                    favouritegroupsdata.clear();
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
                if (type == 1) {
                    if (liftType == 1) {
                        puLiftCategoryData.clear();
                        if (liftSubType == 1)
                            txtPULiftCategory.setText("Lifting Equipment Available On-site");
                        else if (liftSubType == 2)
                            txtPULiftCategory.setText("Lifting Equipment Needed by Driver");
                        txtPULiftCategory.setVisibility(View.VISIBLE);
                        puLiftCategory.setVisibility(View.VISIBLE);
                    } else if (liftType == 2) {
                        doLiftCategoryData.clear();
                        if (liftSubType == 1)
                            txtDOLiftCategory.setText("Lifting Equipment Available On-site");
                        else if (liftSubType == 2)
                            txtDOLiftCategory.setText("Lifting Equipment Needed by Driver");
                        txtDOLiftCategory.setVisibility(View.VISIBLE);
                        doLiftCategory.setVisibility(View.VISIBLE);
                    }
                    JSONArray outArray = outJson.getJSONArray("data");
                    for (int i = 0; i < outArray.length(); i++) {
                        JSONObject innerJson = outArray.getJSONObject(i);
                        if (type == 1) {
                            Category cat = new Category();
                            cat.setName(innerJson.getString(Keys.KEY_NAME));
                            cat.setId(innerJson.get(Keys.KEY_ID).toString());
                            cat.setSelected(false);
                            if (liftType == 1)
                                puLiftCategoryData.add(cat);
                            else
                                doLiftCategoryData.add(cat);
                        }
                    }
                try {
                    Log.v("puData", "puData:" + puData);
                    if (!puData.equals("")) {
                        String builder = "";
                        PuDoBuilder = "";
                        int length = 0;
                        String pudoData[];
                        if (puData.contains(",")) {
                            pudoData = puData.replace(" ", "").split(",");
                            length = puData.length();
                        } else {
                            pudoData = (puData + ",").split(",");
                            length = 1;
                        }
                        for (int j = 0; j < length; j++) {
                            try {
                                JSONArray outArrayData = outJson.getJSONArray("data");
                                Log.v("outArrayData", "outArrayData:" + outArrayData.toString());

                                for (int i = 0; i < outArrayData.length(); i++) {
                                    JSONObject innerJson = outArrayData.getJSONObject(i);
                                    Log.v("pudoData", "pudoData in for loop" + pudoData[j].toString());
                                    Log.v("LifCategoryData", "LifCategoryData  in for loop" + innerJson.getString(Keys.KEY_ID).toString());
                                    if (pudoData[j].toString().equals(innerJson.getString(Keys.KEY_ID).toString())) {
                                        Log.v("PuDoBuilder", "PuDoBuilder  in for loop" + PuDoBuilder.indexOf(pudoData[j].toString() + ","));
                                        if (PuDoBuilder.indexOf(pudoData[j].toString() + ",") < 0) {
                                            Log.v("Data", "Data: exists");
                                            builder += innerJson.getString(Keys.KEY_NAME) + ", ";
                                            if (liftType == 1) {
                                                puLiftIdBuilder += innerJson.getString(Keys.KEY_ID).toString() + ",";
                                                PuDoBuilder += innerJson.getString(Keys.KEY_ID).toString() + ",";
                                            } else {
                                                doLiftIdBuilder += innerJson.getString(Keys.KEY_ID).toString() + ",";
                                                PuDoBuilder += innerJson.getString(Keys.KEY_ID).toString() + ",";
                                            }
                                            JSONObject object = new JSONObject();
                                            object.put("name", innerJson.getString(Keys.KEY_NAME));
                                            object.put("id", innerJson.getString(Keys.KEY_ID).toString());
                                            if (liftType == 1) {
                                                puLiftArray.put(object);
                                                Category country = puLiftCategoryData.get((int) i);
                                                country.setSelected(true);
                                                puLiftCategoryData.set((int) i, country);
                                                //builder += puLiftCategoryData.get(i).getName() + ", ";
                                            } else {
                                                doLiftArray.put(object);
                                                Category country = doLiftCategoryData.get((int) i);
                                                country.setSelected(true);
                                                doLiftCategoryData.set((int) i, country);
                                                //builder += doLiftCategoryData.get(i).getName() + ", ";
                                            }
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                Log.v("Error", "Erro data seting:" + ex.getMessage());
                            }
                            if (liftType == 1) {
                                if (!builder.contentEquals(""))
                                    puLiftCategory.setText(builder.substring(0, builder.length() - 2));
                            } else if (liftType == 2) {
                                if (!builder.contentEquals(""))
                                    doLiftCategory.setText(builder.substring(0, builder.length() - 2));
                            }
                        }

                        if (!alreadydone.equals("done")) {
                            alreadydone = "done";
                            dropOffViewSet();
                        }
                    }
                }catch(Exception ex){

                }
                } else if (type == 2) {
                    suitableVehicleData.clear();
                    suitableSelectedVehicleData.clear();
                    JSONArray outArray = outJson.getJSONArray("data");
                    for (int i = 0; i < outArray.length(); i++) {
                        JSONObject innerJson = outArray.getJSONObject(i);
                        Category cat = new Category();
                        cat.setName(innerJson.getString(Keys.KEY_NAME));
                        cat.setId(innerJson.get(Keys.KEY_ID).toString());
                        cat.setVehicletype(innerJson.get(Keys.VEHICLE_TYPE).toString());
                        cat.setSelected(false);
                        suitableVehicleData.add(cat);
                    }

                    if (getArguments().containsKey("data")) {
                        try {
                            if (delivery.getSuitabelVehicle().length() > 0) {
                                String selectedVehical[] = delivery.getSuitabelVehicle().split(",");
                                Log.v("selectedVehical", "selectedVehical" + delivery.getSuitabelVehicle());
                                for (int j = 0; j < selectedVehical.length; j++) {
                                    for (int i = 0; i < outArray.length(); i++) {
                                        JSONObject innerJson = outArray.getJSONObject(i);
                                        Category cat = new Category();
                                        if (selectedVehical[j].equals(innerJson.get(Keys.KEY_ID).toString())) {
                                            cat.setName(innerJson.getString(Keys.KEY_NAME));
                                            cat.setId(innerJson.get(Keys.KEY_ID).toString());
                                            cat.setSelected(true);
                                            suitableSelectedVehicleData.add(cat);
                                            suitableVehicleData.set((int) i, cat);
                                        }

                                    }
                                }
                            }
                            String builder = "";
                            try {
                                for (int i = 0; i < suitableSelectedVehicleData.size(); i++) {
                                    builder += suitableSelectedVehicleData.get(i).getName() + ",";
                                    suitableIdBuilder += suitableSelectedVehicleData.get(i).getId() + ",";
                                    JSONObject object = new JSONObject();
                                    object.put("name", suitableSelectedVehicleData.get(i).getName());
                                    object.put("id", suitableSelectedVehicleData.get(i).getId());
                                    suitableArray.put(object);
                                }
                                pickUpView();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            suitableVehicle.setText(builder.substring(0, builder.length() - 2));
                        } catch (Exception ex) {
                            Log.v("getSuitabelVehicle", "getSuitabelVehicle:" + ex.getMessage());
                        }
                    }
                } else if (type == 3) {
                    showSuccessMessage("Delivery successfully submitted.");
                } else if (type == 4) {
                    showSuccessMessage("Delivery successfully updated.");
                }
            } else if (Integer.parseInt(outJson.getString(Config.STATUS)) == APIStatus.UNPROCESSABLE) {
                try {
                    if (type == 3 || type == 4) {
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
                    } else if (!outJson.getString("message").contentEquals(""))
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void pickUpView() {
        Log.v("pick", "pick" + delivery.getPickUpLiftEquiAvailableIds());
        Log.v("pick", "pick" + delivery.getPickUpLiftEquiNeededIds());
        if (delivery.getPuLiftEquipment().equals("1")) {
            puLift = "1";
            puLiftEquipment.setText("Available on site");
            puLiftEquipment.setVisibility(View.VISIBLE);
            txtPULiftCategory.setVisibility(View.VISIBLE);
            puLiftCategory.setVisibility(View.VISIBLE);
            txtPULiftCategory.setText("Lifting Equipment Available On-site");
            liftType = 1;
            type = 1;
            getLiftEquipment(delivery.getPickUpLiftEquiAvailableIds());
        } else if (delivery.getPuLiftEquipment().equals("0")) {
            puLift = "0";
            liftType = 1;
            type = 1;
            puLiftEquipment.setText("Not available on site");
            puLiftEquipment.setVisibility(View.VISIBLE);
            txtPULiftCategory.setVisibility(View.VISIBLE);
            puLiftCategory.setVisibility(View.VISIBLE);
            txtPULiftCategory.setText("Lifting Equipment Needed by Driver");
            getLiftEquipment(delivery.getPickUpLiftEquiNeededIds());
        } else {
            puLift = "2";
            liftType = 1;
            type = 1;
            puLiftEquipment.setText("Manual Handling");
            puLiftEquipment.setVisibility(View.VISIBLE);
            txtPULiftCategory.setVisibility(View.GONE);
            puLiftCategory.setVisibility(View.GONE);
            dropOffViewSet();
        }
    }

    private void dropOffViewSet() {
        Log.v("DropView", "DropView:" + delivery.getDropOffLiftEquiAvailableIds());
        Log.v("DropView", "DropView:" + delivery.getDropOffLiftEquiNeededIds());
        Log.v("DropView", "DropView:" + delivery.getDoLiftEquipment());
        if (delivery.getDoLiftEquipment().equals("1")) {
            doLift = "1";
            liftType = 2;
            type = 1;
            doLiftEquipment.setText("Available on site");
            doLiftEquipment.setVisibility(View.VISIBLE);
            txtDOLiftCategory.setVisibility(View.VISIBLE);
            doLiftCategory.setVisibility(View.VISIBLE);
            txtDOLiftCategory.setText("Lifting Equipment Available On-site");
            getLiftEquipment(delivery.getDropOffLiftEquiAvailableIds());
        } else if (delivery.getDoLiftEquipment().equals("0")) {
            doLift = "0";
            liftType = 2;
            type = 1;
            doLiftEquipment.setText("Not available on site");
            doLiftEquipment.setVisibility(View.VISIBLE);
            txtDOLiftCategory.setVisibility(View.VISIBLE);
            doLiftCategory.setVisibility(View.VISIBLE);
            txtDOLiftCategory.setText("Lifting Equipment Needed by Driver");
            getLiftEquipment(delivery.getDropOffLiftEquiNeededIds());
        } else {
            doLift = "2";
            liftType = 2;
            type = 1;
            doLiftEquipment.setText("Manual Handling");
            doLiftEquipment.setVisibility(View.VISIBLE);
            txtDOLiftCategory.setVisibility(View.GONE);
            doLiftCategory.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3 && resultCode == -1) {
            try {
                String val = data.getStringExtra("value");
                suitableIdBuilder = data.getStringExtra("suitableidbuilder");
                suitableVehicle.setText(data.getStringExtra("strvalue"));
            } catch (Exception e) {
                e.toString();
            }
        }
        if (requestCode == 16 && resultCode == -1) {
            try {
                String val = data.getStringExtra("value");
                hasGroups = data.getBooleanExtra("hasGroups", true);
                mUser_Group = val;
                if (hasGroups) {
                    if (mUser_Group.contentEquals(""))
                        mUser_Group = "all";
                    submitShipment(mUser_Group);
                } else
                    showMessage(getResources().getString(R.string.nofavouritefroup));
            } catch (Exception e) {
                e.toString();
            }
        }

    }

    public void showVehicleDialog(final int index) {
        Intent intent = new Intent(getActivity(), VehicleList.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("array", suitableVehicleData);
        bundle.putInt("index", index);
        bundle.putString("topcolor", "1");
        if (!suitableIdBuilder.equals("")) {
            try {
                String val = suitableIdBuilder.substring(0, suitableIdBuilder.length() - 1);
                bundle.putString("vehicletypeid", val);
            } catch (Exception e) {
                e.toString();
            }
        }
        intent.putExtras(bundle);
        startActivityForResult(intent, 3);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.freight_del_type:
                showGrabidDialog(3, 0);
                break;
            case R.id.suitable_vehicle:
                showVehicleDialog(0);
                break;
            case R.id.submit:
                if (isValid())
                    showFavouriteSelect();
                break;
            case R.id.pu_build_type:
                showGrabidDialog(1, 1);
                break;
            case R.id.pu_lift_equip:
                showGrabidDialog(2, 1);
                break;
            case R.id.do_build_type:
                showGrabidDialog(1, 2);
                break;
            case R.id.do_lift_equip:

                showGrabidDialog(2, 2);
                break;
            case R.id.do_call:
                showSelctionDialog(1);
                break;
            case R.id.do_appointment:
                showSelctionDialog(2);
                break;
            case R.id.pu_induction_require:
                showSelctionDialog(3);
                break;
            case R.id.do_induction_require:
                showSelctionDialog(4);
                break;
            case R.id.special_permit:
                showSelctionDialog(5);
                break;
            case R.id.pu_lift_equip_category:
                showLiftEquipmentDialog(1);
                break;
            case R.id.do_lift_equip_category:
                showLiftEquipmentDialog(2);
                break;
        }
    }

    private void showMessage(String message) {
        AlertManager.messageDialog(getActivity(), "Alert!", message);
    }

    private void showSuccessMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Success!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Fragment fragment = new HomeMap();
                String backStateName = "com.grabid.activities.HomeActivity";
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        });
        builder.show();
    }

    private boolean isValid() {
        if (suitableVehicle.getText().toString().trim().length() == 0 && puBuildType.getText().toString().trim().length() == 0
                && puInduction.getText().toString().trim().length() == 0 && doBuildType.getText().toString().trim().length() == 0
                && doInduction.getText().toString().trim().length() == 0 && doCall.getText().toString().trim().length() == 0
                && doAppoint.getText().toString().trim().length() == 0 && doLiftEquipment.getText().toString().trim().length() == 0) {
            showMessage(getActivity().getResources().getString(R.string.completeallfield));
            return false;
        }
        if (suitableVehicle.getText().toString().trim().length() == 0) {
            showMessage(getActivity().getResources().getString(R.string.selectsuitablevehicle));
            return false;
        }
        if (puBuildType.getText().toString().trim().length() == 0) {
            showMessage(getActivity().getResources().getString(R.string.selectbuildtype));
            return false;
        }
        if (puBuild != null && puBuild.equals("1")) {
            if (mPickUpComVal.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.pickupbuildname));
                return false;
            }
        }
        if (puInduction.getText().toString().trim().length() == 0) {
            showMessage(getActivity().getResources().getString(R.string.pickupindreq));
            return false;
        } else if (puLiftEquipment.getText().toString().trim().length() < 4) {
            showMessage(getActivity().getResources().getString(R.string.pickupleq));
            return false;
        }
        if (puLift != null && puLift.equals("1")) {
            if (puLiftIdBuilder.contentEquals("")) {
                showMessage(getActivity().getResources().getString(R.string.pickuplavid));
                return false;
            }
        } else if (puLift != null && puLift.equals("0")) {
            if (puLiftIdBuilder.contentEquals("")) {
                showMessage(getActivity().getResources().getString(R.string.pickuplneid));
                return false;
            }
        }
        if (doBuildType.getText().toString().trim().length() == 0) {
            showMessage(getActivity().getResources().getString(R.string.dropofbuildtype));
            return false;
        }
        if (doBuild != null && doBuild.equals("1")) {
            if (mdropoffComVal.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.dropoffcom));
                return false;
            }
        }
        if (doInduction.getText().toString().trim().length() == 0) {
            showMessage(getActivity().getResources().getString(R.string.dropoffindreq));
            return false;
        } else if (doCall.getText().toString().trim().length() == 0) {
            showMessage(getActivity().getResources().getString(R.string.dropoffcalreq));
            return false;
        } else if (doAppoint.getText().toString().trim().length() == 0) {
            showMessage(getActivity().getResources().getString(R.string.dropoffaporeq));
            return false;
        } else if (doLiftEquipment.getText().toString().trim().length() == 0) {
            showMessage(getActivity().getResources().getString(R.string.dropoffliftequip));
            return false;
        }
        if (doLift != null && doLift.equals("1")) {
            if (doLiftIdBuilder.contentEquals("")) {
                showMessage(getActivity().getResources().getString(R.string.dropoffliftavaids));
                return false;
            }
        } else if (doLift != null && doLift.equals("0")) {
            if (doLiftIdBuilder.contentEquals("")) {
                showMessage(getActivity().getResources().getString(R.string.dropoffliftneededid));
                return false;
            }
        }
        if (specailPermit.getText().toString().trim().length() == 0) {
            showMessage(getActivity().getResources().getString(R.string.special_permit));
            return false;
        } else if (specialPermit.equals("1") && specialPermitDetail.getText().toString().trim().length() == 0) {
            showMessage(getActivity().getResources().getString(R.string.specialpermitdetail));
            return false;
        } else if (geoZone.getCheckedRadioButtonId() == -1) {
            showMessage(getActivity().getResources().getString(R.string.geozone));
            return false;
        } else if (geoZone.getCheckedRadioButtonId() == R.id.yes) {
            if (geo.getText().toString().trim().length() == 0) {
                showMessage(getActivity().getResources().getString(R.string.georadius));
                return false;
            }
        }
        return true;
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

    public void showSelctionDialog(final int type) {
        final Dialog mDialog = new Dialog(getActivity(), R.style.GrabidDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_choose_new);
        mDialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("        Please select        ");
        TextView YES = (TextView) mDialog.findViewById(R.id.yes);
        TextView NO = (TextView) mDialog.findViewById(R.id.no);
        ImageView close = (ImageView) mDialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog.isShowing())
                    mDialog.dismiss();
            }
        });
        YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == 1) {
                    doCallRequire = "1";
                    doCall.setText("YES");
                } else if (type == 2) {
                    doAppointment = "1";
                    doAppoint.setText("YES");
                } else if (type == 3) {
                    puInduct = "1";
                    puInduction.setText("YES");
                } else if (type == 4) {
                    doInduct = "1";
                    doInduction.setText("YES");
                } else if (type == 5) {
                    specialPermit = "1";
                    specailPermit.setText("YES");
                    txtSplPermit.setVisibility(View.VISIBLE);
                    specialPermitDetail.setVisibility(View.VISIBLE);
                }
                mDialog.dismiss();
            }
        });
        NO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == 1) {
                    doCallRequire = "0";
                    doCall.setText("NO");
                } else if (type == 2) {
                    doAppointment = "0";
                    doAppoint.setText("NO");
                } else if (type == 3) {
                    puInduct = "0";
                    puInduction.setText("NO");
                } else if (type == 4) {
                    doInduct = "0";
                    doInduction.setText("NO");
                } else if (type == 5) {
                    specialPermit = "0";
                    specailPermit.setText("NO");
                    txtSplPermit.setVisibility(View.GONE);
                    specialPermitDetail.setVisibility(View.GONE);
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
        ;
    }

    public void showFavouriteSelect() {
        final Dialog mDialog = new Dialog(getActivity());
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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


        if(session.getCurrentScreen().equals("copy")){
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
                    createDelivery("1");
                } else if (check2.isChecked()) {
                    mDialog.dismiss();
                    user_Group = "2";
                    createDelivery("2");
                } else if (check3.isChecked()) {
                    mDialog.dismiss();
                    user_Group = "3";
                    createDelivery("3");
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

    public void createDelivery(String user_Group) {
        if (user_Group.contentEquals("1") || user_Group.contentEquals("2"))
            getfavourites();
        else
            submitShipment("sendall");
    }

    public String getBackImage(String deliveryItem) {
        String image = "";
        try {
            JSONArray jsonArray = new JSONArray(deliveryItem);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject JsonObj = jsonArray.optJSONObject(i);
                image = JsonObj.optString("item_photo");
                if (!image.contentEquals(""))
                    return image;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void submitShipment(String user_Groupp) {
        String url = Config.SERVER_URL + Config.DELIVERIES;
        HashMap<String, String> params = new HashMap<>();
        com.grabid.models.ShipmentAddress address = session.getShipmentAddress();
        Shipment shipment = session.getShipment();
        params.put(Keys.USER_GROUP, user_Group);
//        params.put(Keys.ITEM_PHOTO, item_pic);
        String delitem = shipment.getDeliveryItem();
        String img = StorePath.ITEM_IMAGE;
//        params.put(Keys.ITEM_PHOTO, StorePath.ITEM_IMAGE);
        params.put(Keys.ITEM_PHOTO, getBackImage(shipment.getDeliveryItem()));
        params.put(Keys.DROPOFF_ADDRESS, address.getDoAddress());
        params.put(Keys.AUCTION_BID, address.getAuctionBid() ? "1" : "0");
        if (address.getAuctionBid()) {
            params.put(Keys.AUCTION_START_TIME, address.getStartDate());
            params.put(Keys.AUCTION_END_TIME, address.getEndDate());
            params.put(Keys.MAX_AUCTION_BID, address.getMaxBid());
        } else {
            params.put(Keys.FIXED_OFFER, address.getFixedOffer());
        }
        params.put(Keys.PICKUP_DAY_TYPE, address.getPuDateType());
        params.put(Keys.PICKUP_DAY, address.getPuDate());
        if (address.getPuDateType().equals("4"))
            params.put(Keys.PICKUP_END_DAY, address.getPuEndDate());
        params.put(Keys.PICKUP_COUNTRY, address.getPuCountry());
        params.put(Keys.PICKUP_STATE, address.getPuState());
        params.put(Keys.PICKUP_CITY, address.getPuCity());
        params.put(Keys.PICKUP_ADDRESS, address.getPuAddress());
        params.put(Keys.PICKUP_CONTACT_PERSON, address.getPuContact());
        params.put(Keys.PICKUP_MOBILE, address.getPuMobile());
        params.put(Keys.PICKUP_PHONE, address.getPuMobile());
        params.put(Keys.PICKUP_BUILD_TYPE, puBuild);
        if (puBuild != null && puBuild.equals("1"))
            params.put(Keys.PICKUP_BUILD_COMPANYNAME, mPickUpComVal.getText().toString());
        params.put(Keys.PICKUP_INDUCTION_REQUIRE, puInduct);
        params.put(Keys.PICKUP_SPECIAL_RESTRICTION, puSpecialRestriction.getText().toString());
        params.put(Keys.DROPOFF_INDUCTION_REQUIRE, doInduct);
        params.put(Keys.DROPOFF_SPECIAL_RESTRICTION, doSpecialRestriction.getText().toString());
        if (user_Groupp != null && !user_Groupp.contentEquals("sendall"))
            params.put(Keys.FAVOURITE_USER_GROUP_IDS, user_Groupp);

        if (!suitableIdBuilder.equals("")) {
            try {
                String val = suitableIdBuilder.substring(0, suitableIdBuilder.length() - 1);
                params.put(Keys.SUITABLE_VEHICLE, val);
            } catch (Exception e) {
                e.toString();
            }
        }

        params.put(Keys.PICKUP_LIFT_EQUIPMENT, puLift);
        params.put(Keys.DROPOFF_BUILD_TYPE, doBuild);

        if (doBuild != null && doBuild.equals("1"))
            params.put(Keys.DROPOFF_BUILD_COMPANYNAME, mdropoffComVal.getText().toString());


        if (puLift.equals("1")) {
            try {
                String puvalId="";
                if(puLiftIdBuilder.contains(",")) {
                    puvalId = puLiftIdBuilder.substring(0, puLiftIdBuilder.length() - 1);
                }else{
                    puvalId = puLiftIdBuilder.substring(0, puLiftIdBuilder.length() - 0);
                }
                PreviewField.setPickUpLiftingEquipmentAvIds(puvalId);
                PreviewField.setPickUpLiftingEquipmentNeededIds("");
                String puval="";
                try {
                    puval = puLiftNameBuilder.substring(0, puLiftNameBuilder.length() - 1);
                }catch(Exception ex){
                    puval="";
                }
                if(puvalId.length()>0){
                    if(puval.length()==0) {
                        for (int i = 0; i < puLiftCategoryData.size(); i++) {
                            if (puLiftCategoryData.get(i).isSelected()) {
                                puLiftNameBuilder += puLiftCategoryData.get(i).getName() + ",";
                            }
                        }
                        puval = puLiftNameBuilder.substring(0, puLiftNameBuilder.length() - 1);
                    }
                }

                params.put(Keys.PICKUP_LIFT_EQUIP_AVAILABLE, puvalId);
                params.put(Keys.PICKUP_LIFT_EQUIP_AVAILABLE_TEXT,puval);
                params.put(Keys.PICKUP_LIFT_EQUIP_NEEDED_TEXT, "");

            } catch (Exception e) {
                e.toString();
            }
        } else if (puLift.equals("0")) {
            try {
                String puvalId="";
                if(puLiftIdBuilder.contains(",")) {
                    puvalId = puLiftIdBuilder.substring(0, puLiftIdBuilder.length() - 1);
                }else{
                    puvalId = puLiftIdBuilder.substring(0, puLiftIdBuilder.length() - 0);
                }
                PreviewField.setPickUpLiftingEquipmentNeededIds(puvalId);
                PreviewField.setPickUpLiftingEquipmentAvIds("");
                String puval="";
                try {
                    puval = puLiftNameBuilder.substring(0, puLiftNameBuilder.length() - 1);
                }catch(Exception ex){
                    puval="";
                }
                if(puvalId.length()>0){
                    if(puval.length()==0) {
                        for (int i = 0; i < puLiftCategoryData.size(); i++) {
                            if (puLiftCategoryData.get(i).isSelected()) {
                                puLiftNameBuilder += puLiftCategoryData.get(i).getName() + ",";
                            }
                        }
                        puval = puLiftNameBuilder.substring(0, puLiftNameBuilder.length() - 1);
                    }
                }
                params.put(Keys.PICKUP_LIFT_EQUIP_NEEDED, puvalId);
                params.put(Keys.PICKUP_LIFT_EQUIP_AVAILABLE_TEXT,"");
                params.put(Keys.PICKUP_LIFT_EQUIP_NEEDED_TEXT, puval);
            } catch (Exception e) {
                e.toString();
            }
        } else {
            params.put(Keys.PICKUP_LIFT_EQUIP_AVAILABLE_TEXT, "");
            params.put(Keys.PICKUP_LIFT_EQUIP_NEEDED_TEXT, "");
            PreviewField.setPickUpLiftingEquipmentNeededIds("");
            PreviewField.setPickUpLiftingEquipmentAvIds("");

        }
        if (doLift.equals("1")) {
            try {
                String dovalId="";
                if(doLiftIdBuilder.contains(",")) {
                    dovalId = doLiftIdBuilder.substring(0, doLiftIdBuilder.length() - 1);
                }else{
                    dovalId = doLiftIdBuilder.substring(0, doLiftIdBuilder.length() - 0);
                }

                PreviewField.setDropOffLiftingEquipmentAvIds(dovalId);
                PreviewField.setDropOffLiftingEquipmentNeddedIds("");
                String doval="";
                try {
                    doval = doLiftNameBuilder.substring(0, doLiftNameBuilder.length() - 1);
                }catch(Exception ex){
                    doval="";
                }
                if(dovalId.length()>0){
                    if(doval.length()==0) {
                        for (int i = 0; i < doLiftCategoryData.size(); i++) {
                            if (doLiftCategoryData.get(i).isSelected()) {
                                doLiftNameBuilder += doLiftCategoryData.get(i).getName() + ",";
                            }
                        }
                        doval = doLiftNameBuilder.substring(0, doLiftNameBuilder.length() - 1);
                    }
                }

                params.put(Keys.DROPOFF_LIFT_EQUIP_AVAILABLE, dovalId);
                params.put(Keys.DROPOFF_LIFT_EQUIP_AVAILABLE_TEXT, doval);
                params.put(Keys.DROPOFF_LIFT_EQUIP_NEEDED_TEXT, "");



            } catch (Exception e) {
                e.toString();
            }
        } else if (doLift.equals("0")) {
            try {
                String dovalId="";
                if(doLiftIdBuilder.contains(",")) {
                    dovalId = doLiftIdBuilder.substring(0, doLiftIdBuilder.length() - 1);
                }else{
                    dovalId = doLiftIdBuilder.substring(0, doLiftIdBuilder.length() - 0);
                }
                PreviewField.setDropOffLiftingEquipmentAvIds("");
                PreviewField.setDropOffLiftingEquipmentNeddedIds(dovalId);
                String doval="";
                try {
                    doval = doLiftNameBuilder.substring(0, doLiftNameBuilder.length() - 1);
                }catch(Exception ex){
                    doval="";
                }
                if(dovalId.length()>0){
                    if(doval.length()==0) {
                        for (int i = 0; i < doLiftCategoryData.size(); i++) {
                            if (doLiftCategoryData.get(i).isSelected()) {
                                doLiftNameBuilder += doLiftCategoryData.get(i).getName() + ",";
                            }
                        }
                        doval = doLiftNameBuilder.substring(0, doLiftNameBuilder.length() - 1);
                    }
                }
                params.put(Keys.DROPOFF_LIFT_EQUIP_NEEDED, dovalId);
                params.put(Keys.DROPOFF_LIFT_EQUIP_AVAILABLE_TEXT, "");
                params.put(Keys.DROPOFF_LIFT_EQUIP_NEEDED_TEXT, doval);

            } catch (Exception e) {
                e.toString();
            }

        } else {
            params.put(Keys.DROPOFF_LIFT_EQUIP_AVAILABLE_TEXT, "");
            params.put(Keys.DROPOFF_LIFT_EQUIP_NEEDED_TEXT, "");
            PreviewField.setDropOffLiftingEquipmentAvIds("");
            PreviewField.setDropOffLiftingEquipmentNeddedIds("");

        }


        params.put(Keys.DROPOFF_DAY_TYPE, address.getDoDateType());
        params.put(Keys.DROPOFF_DAY, address.getDoDate());
        if (address.getDoDateType().equals("4"))
            params.put(Keys.DROPOFF_END_DAY, address.getDoEndDate());
        params.put(Keys.DROPOFF_COUNTRY, address.getDoCountry());
        params.put(Keys.DROPOFF_STATE, address.getDoState());
        params.put(Keys.DROPOFF_CITY, address.getDoCity());
        params.put(Keys.DROPOFF_ADDRESS, address.getDoAddress());
        params.put(Keys.DROPOFF_CONTACT, address.getDoContact());
        params.put(Keys.DROPOFF_MOBILE, address.getDoMobile());
        params.put(Keys.DROPOFF_PHONE, address.getDoMobile());

        params.put(Keys.DROPOFF_LIFT_EQUIPMENT, doLift);
        params.put(Keys.DROPOFF_CALL, doCallRequire);
        params.put(Keys.DROPOFF_APPOINTMENT, doAppointment);
        params.put(Keys.ITEM_DELIVERY_TITLE, address.getTitle());
        params.put(Keys.PICKUP_LATITUDE, address.getPuLat());
        params.put(Keys.PICKUP_LONGITUDE, address.getPuLang());
        params.put(Keys.DROPOFF_LATITUDE, address.getDoLat());
        params.put(Keys.DROPOFF_LONGITUDE, address.getDoLang());
        params.put(Keys.DELIVERY_TYPE_ID, shipment.getDeliveryTypeId());
        params.put(Keys.ITEM_QTY, shipment.getQty());
        params.put(Keys.SPECIAL_PERMIT, specialPermit);

        if (specialPermit.equals("1"))
            params.put(Keys.SPECIAL_PERMIT_DETAIL, specialPermit);
        params.put(Keys.DELIVERY_ITEM, shipment.getDeliveryItem());
        params.put(Keys.ITEM_DELIVERY_TYPE_SUB_ID, shipment.getDeliverySubTypeID());

        if (shipment.getDeliveryTypeId().equals("2"))
            params.put(Keys.ITEM_EQUIPMENT_TYPE_ID, shipment.getDeliverySubTypeID());
        if (shipment.getDeliveryTypeId().equals("1")) {
            if (shipment.getDeliverySubTypeID().equals("18"))
                params.put(Keys.ITEM_DELIVAR_OTHER, shipment.getOther());

        }
        if (shipment.getDeliveryTypeId().equals("2")) {
            if (shipment.getDeliverySubTypeID().equals("10"))
                params.put(Keys.ITEM_DELIVAR_OTHER, shipment.getOther());
        }
        if (shipment.getDeliveryTypeId().equals("3")) {
            if (shipment.getDeliverySubTypeID().equals("32"))
                params.put(Keys.ITEM_DELIVAR_OTHER, shipment.getOther());
        }
        if (shipment.getDeliveryTypeId().equals("4")) {
            if (shipment.getDeliverySubTypeID().equals("33"))
                params.put(Keys.ITEM_DELIVAR_OTHER, shipment.getOther());
            else
                params.put(Keys.ITEM_DELIVERY_TYPE_SUB_SUB_ID, shipment.getDeliverySubSubTypeID());
            if (shipment.getDeliverySubSubTypeID().equals("38") ||
                    shipment.getDeliverySubSubTypeID().equals("43"))
                params.put(Keys.ITEM_DELIVAR_OTHER, shipment.getOther());
        }
        params.put(Keys.GEO_ZONE, isGEOZoneEnable ? "1" : "0");
        if (isGEOZoneEnable)
            params.put(Keys.RADIUS, geo.getText().toString());
        Log.d(Config.TAG, params.toString());
        PreviewField.setSuiotableVehicles(suitableVehicle.getText().toString());
        if (session.getCurrentScreen().equals("edit") || session.getCurrentScreen().equals("editRelisting")) {
            PreviewDelivery(params, deliveryID);
        } else {
            session.saveCurrentScreen("copy");
            PreviewDelivery(params, "");
        }
    }

    public void PreviewDelivery(HashMap<String, String> params, String id) {
        String backStateName = this.getClass().getName();
        Bundle bundle = new Bundle();
        bundle.putSerializable("hashmap", params);
        bundle.putString("id", id);
        Fragment fragment = new PreviewDelivery();
        fragment.setArguments(bundle);
        getActivity().getFragmentManager().beginTransaction().add(R.id.container, fragment, backStateName)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void updateShipment(String id) {
        String url = Config.SERVER_URL + Config.DELIVERIES + "/" + id;
        Log.d(Config.TAG, url);
        HashMap<String, String> params = new HashMap<>();

        com.grabid.models.ShipmentAddress address = session.getShipmentAddress();
        Shipment shipment = session.getShipment();

        params.put(Keys.DROPOFF_ADDRESS, address.getDoAddress());
        params.put(Keys.AUCTION_BID, address.getAuctionBid() ? "1" : "0");
        if (address.getAuctionBid()) {
            params.put(Keys.AUCTION_START_TIME, address.getStartDate());
            params.put(Keys.AUCTION_END_TIME, address.getEndDate());
            params.put(Keys.MAX_AUCTION_BID, address.getMaxBid());
        } else {
            params.put(Keys.FIXED_OFFER, address.getFixedOffer());
        }
        params.put(Keys.PICKUP_DAY, address.getPuDate());
        params.put(Keys.PICKUP_COUNTRY, "India");
        params.put(Keys.PICKUP_STATE, "Chandigarh");
        params.put(Keys.PICKUP_CITY, "");
        params.put(Keys.PICKUP_ADDRESS, address.getPuAddress());
        params.put(Keys.PICKUP_CONTACT_PERSON, address.getPuContact());
        params.put(Keys.PICKUP_MOBILE, address.getPuMobile());
        params.put(Keys.PICKUP_PHONE, address.getPuMobile());
        params.put(Keys.PICKUP_BUILD_TYPE, puBuild);
        params.put(Keys.PICKUP_LIFT_EQUIPMENT, puLift);
        params.put(Keys.DROPOFF_DAY, address.getDoDate());
        params.put(Keys.DROPOFF_COUNTRY, "India");
        params.put(Keys.DROPOFF_STATE, "Delhi");
        params.put(Keys.DROPOFF_CITY, "");
        params.put(Keys.DROPOFF_ADDRESS, address.getDoAddress());
        params.put(Keys.DROPOFF_CONTACT, address.getDoContact());
        params.put(Keys.DROPOFF_MOBILE, address.getDoMobile());
        params.put(Keys.DROPOFF_PHONE, address.getDoMobile());
        params.put(Keys.DROPOFF_BUILD_TYPE, doBuild);
        params.put(Keys.DROPOFF_LIFT_EQUIPMENT, doLift);
        params.put(Keys.DROPOFF_CALL, doCallRequire);
        params.put(Keys.DROPOFF_APPOINTMENT, doAppointment);
        params.put(Keys.ITEM_DELIVERY_TITLE, address.getTitle());
        params.put(Keys.PICKUP_LATITUDE, address.getPuLat());
        params.put(Keys.PICKUP_LONGITUDE, address.getPuLang());
        params.put(Keys.DROPOFF_LATITUDE, address.getDoLat());
        params.put(Keys.DROPOFF_LONGITUDE, address.getDoLang());
        params.put(Keys.DELIVERY_TYPE_ID, shipment.getDeliveryTypeId());
        if (shipment.getDeliveryTypeId().equals("1")) {
            params.put(Keys.ITEM_DELIVERY_TYPE_SUB_ID, shipment.getDeliverySubTypeID());
            params.put(Keys.ITEM_WEIGHT, shipment.getWeight());
            params.put(Keys.ITEM_LENGTH, shipment.getLength());
            params.put(Keys.ITEM_WIDTH, shipment.getWidth());
            params.put(Keys.ITEM_HEIGHT, shipment.getHeight());
            params.put(Keys.ITEM_STACKABLE, shipment.getStackable());
            params.put(Keys.ITEM_HAZARDOUS, shipment.getHazardous());
            if (shipment.getDeliverySubTypeID().equals("28")) {
                params.put(Keys.ITEM_DELIVAR_OTHER, shipment.getOther());
            }
        } else if (shipment.getDeliveryTypeId().equals("2")) {
            params.put(Keys.ITEM_EQUIPMENT_TYPE_ID, shipment.getDeliverySubTypeID());
            params.put(Keys.ITEM_QTY, shipment.getQty());
            params.put(Keys.ITEM_MORE, shipment.getMore());
            if (shipment.getDeliverySubTypeID().equals("10"))
                params.put(Keys.ITEM_DELIVAR_OTHER, shipment.getOther());
        } else if (shipment.getDeliveryTypeId().equals("3")) {
            params.put(Keys.ITEM_DELIVERY_TYPE_SUB_ID, shipment.getDeliverySubTypeID());
            params.put(Keys.ITEM_QTY, shipment.getQty());
            params.put(Keys.ITEM_MORE, shipment.getMore());
            if (shipment.getDeliverySubTypeID().equals("32")) {
                params.put(Keys.ITEM_DELIVAR_OTHER, shipment.getOther());
            } else if (shipment.getDeliverySubTypeID().equals("25") ||
                    shipment.getDeliverySubTypeID().equals("26") ||
                    shipment.getDeliverySubTypeID().equals("28") ||
                    shipment.getDeliverySubTypeID().equals("29") ||
                    shipment.getDeliverySubTypeID().equals("30")) {
                params.put(Keys.ITEM_WEIGHT, shipment.getWeight());
                params.put(Keys.ITEM_LENGTH, shipment.getLength());
                params.put(Keys.ITEM_WIDTH, shipment.getWidth());
                params.put(Keys.ITEM_HEIGHT, shipment.getHeight());
            }
        } else if (shipment.getDeliveryTypeId().equals("4")) {
            params.put(Keys.ITEM_DELIVERY_TYPE_SUB_ID, shipment.getDeliverySubTypeID());
            params.put(Keys.ITEM_ANIMAL_NAME, shipment.getName());
            params.put(Keys.ITEM_ANIMAL_BREED, shipment.getBreed());
            if (shipment.getVaccination().equalsIgnoreCase("yes"))
                params.put(Keys.ITEM_CURRENT_VACCINATIONS, "1");
            else params.put(Keys.ITEM_CURRENT_VACCINATIONS, "0");
            params.put(Keys.ITEM_QTY, shipment.getQty());
            params.put(Keys.ITEM_WEIGHT, shipment.getWeight());
            params.put(Keys.ITEM_MORE, shipment.getMore());
            if (shipment.getDeliverySubTypeID().equals("16")) {
                if (shipment.getCarrier().equalsIgnoreCase("yes"))
                    params.put(Keys.ITEM_ANIMAL_CARRIER, "1");
                else params.put(Keys.ITEM_ANIMAL_CARRIER, "0");
                params.put(Keys.ITEM_DELIVERY_TYPE_SUB_SUB_ID, shipment.getDeliverySubSubTypeID());
            } else if (shipment.getDeliverySubTypeID().equals("17")) {
                params.put(Keys.ITEM_DELIVERY_TYPE_SUB_SUB_ID, shipment.getDeliverySubSubTypeID());
                if (shipment.getDeliverySubSubTypeID().equals("38") ||
                        shipment.getDeliverySubSubTypeID().equals("43"))
                    params.put(Keys.ITEM_DELIVAR_OTHER, shipment.getOther());
            }
        }

        params.put(Keys.SPECIAL_PERMIT, specialPermit);
        params.put(Keys.GEO_ZONE, isGEOZoneEnable ? "1" : "0");
        if (isGEOZoneEnable)
            params.put(Keys.RADIUS, geo.getText().toString());

        Log.d(Config.TAG, params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall apiCall = new RestAPICall(getActivity(), HTTPMethods.PUT, this, params);
            apiCall.execute(url, session.getToken());
        } else {
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
        }
    }

    public void showGrabidDialog(final int type, final int subtype) {
        final Dialog mDialog = new Dialog(getActivity(), R.style.GrabidDialog);
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
        if (type == 1)
            title.setText("Select Build Type");
        else if (type == 2)
            title.setText("Select Lifting Equipment");
        else
            title.setText("Select Freight Delivery Type");
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                try {
                    dialog.dismiss();
                } catch (Exception e) {

                }
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                dialog.dismiss();
            }
        });

        final ListView dialog_ListView = (ListView) mDialog.findViewById(R.id.list);
        ArrayAdapter<String> adapter = null;

        if (type == 1)
            adapter = new ArrayAdapter<>(getActivity(),
                    R.layout.dialog_textview, R.id.textItem, getBuildList());
        else if (type == 2)
            adapter = new ArrayAdapter<>(getActivity(),
                    R.layout.dialog_textview, R.id.textItem, getEquipmentList());
        else
            adapter = new ArrayAdapter<>(getActivity(),
                    R.layout.dialog_textview, R.id.textItem, getFreightType());

        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (type == 1) {
                    if (subtype == 1) {
                        puBuildType.setText(parent.getItemAtPosition(position).toString());
                    } else {
                        doBuildType.setText(parent.getItemAtPosition(position).toString());
                    }
                } else if (type == 2) {
                    String value = parent.getItemAtPosition(position).toString();
                    Log.d("result", value);
                    if (subtype == 1) {
                        liftType = 1;
                        puLiftEquipment.setText(value);
                        puLiftCategory.setText("");
                        puLiftIdBuilder = "";
                        puLiftNameBuilder = "";
                        if (value.equalsIgnoreCase("Manual Handling")) {
                            txtPULiftCategory.setVisibility(View.GONE);
                            puLiftCategory.setVisibility(View.GONE);
                        } else if (value.contains("Not available")) {
                            liftSubType = 2;
                            getLiftEquipment("");
                        } else {
                            liftSubType = 1;
                            getLiftEquipment("");
                        }
                    } else {
                        liftType = 2;
                        doLiftEquipment.setText(value);
                        doLiftCategory.setText("");
                        doLiftIdBuilder = "";
                        doLiftNameBuilder = "";
                        if (value.equalsIgnoreCase("Manual Handling")) {
                            txtDOLiftCategory.setVisibility(View.GONE);
                            doLiftCategory.setVisibility(View.GONE);
                        } else if (value.contains("Not available")) {
                            liftSubType = 2;
                            getLiftEquipment("");
                        } else {
                            liftSubType = 1;
                            getLiftEquipment("");
                        }
                    }
                } else {
                    freightDelType.setText(parent.getItemAtPosition(position).toString());
                }
                setupValues(type, subtype, parent.getItemAtPosition(position).toString());
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void setupValues(int type, int subType, String s) {
        if (type == 1) {
            if (subType == 1) {
                if (s.equals("Commercial")) {
                    puBuild = "1";
                    mPickComTitle.setVisibility(View.VISIBLE);
                    mPickUpComVal.setVisibility(View.VISIBLE);
                } else if (s.equals("Residential")) {
                    puBuild = "2";
                    mPickComTitle.setVisibility(View.GONE);
                    mPickUpComVal.setVisibility(View.GONE);
                }
            } else {
                if (s.equals("Commercial")) {
                    doBuild = "1";
                    mdropComTitle.setVisibility(View.VISIBLE);
                    mdropoffComVal.setVisibility(View.VISIBLE);
                } else if (s.equals("Residential")) {
                    doBuild = "2";
                    mdropComTitle.setVisibility(View.GONE);
                    mdropoffComVal.setVisibility(View.GONE);
                }
            }
        } else {
            if (subType == 1) {
                if (s.equals("Available on site"))
                    puLift = "1";
                else if (s.equals("Not available on site")) puLift = "0";
                else if (s.equals("Manual Handling")) puLift = "2";
            } else {
                if (s.equals("Available on site"))
                    doLift = "1";
                else if (s.equals("Not available on site")) doLift = "0";
                else if (s.equals("Manual Handling")) doLift = "2";
            }
        }
    }

    public String[] getBuildList() {
        return getActivity().getResources().getStringArray(R.array.build_type);
    }

    public String[] getEquipmentList() {
        return getActivity().getResources().getStringArray(R.array.life_equipment);
    }

    public String[] getFreightType() {
        return getActivity().getResources().getStringArray(R.array.freight_type);
    }

    String puData = "";

    private void getLiftEquipment(String puData) {
        type = 1;
        this.puData = puData;
        String url = Config.SERVER_URL + Config.LIFTING_EQUIPMENT;
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

    String puLiftIdBuilder = "";
    String puLiftNameBuilder = "";
    String doLiftIdBuilder = "";
    String doLiftNameBuilder = "";
    String suitableIdBuilder = "";
    JSONArray puLiftArray = new JSONArray();
    JSONArray doLiftArray = new JSONArray();
    JSONArray suitableArray = new JSONArray();

    private class CategoryAdapter extends BaseAdapter {
        Context ctx;
        int type;

        CategoryAdapter(Context ctx, int type) {
            this.ctx = ctx;
            this.type = type;
            cattype = type;
        }

        @Override
        public int getCount() {
            if (type == 3)
                return suitableVehicleData.size();
            else if (type == 2)
                return doLiftCategoryData.size();
            else
                return puLiftCategoryData.size();
        }

        @Override
        public Object getItem(int i) {
            if (type == 3)
                return suitableVehicleData.get(i);
            else if (type == 2)
                return doLiftCategoryData.get(i);
            else
                return puLiftCategoryData.get(i);
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

            if (type == 3) {
                if (position == suitableVehicleData.size() - 1) {
                    done.setVisibility(View.VISIBLE);
                } else done.setVisibility(View.GONE);
            } else if (type == 2) {
                if (position == doLiftCategoryData.size() - 1) {
                    done.setVisibility(View.VISIBLE);
                } else done.setVisibility(View.GONE);
            } else {
                if (position == puLiftCategoryData.size() - 1) {
                    done.setVisibility(View.VISIBLE);
                } else done.setVisibility(View.GONE);
            }
            name.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (check.isChecked())
                        check.setChecked(false);
                    else
                        check.setChecked(true);
                    check.setChecked(true);
                    if (type == 3) {
                        Category country = suitableVehicleData.get((int) name.getTag());
                        country.setSelected(check.isChecked());
                        suitableVehicleData.set((int) name.getTag(), country);
                    } else if (type == 1) {
                        Category country = puLiftCategoryData.get((int) name.getTag());
                        country.setSelected(check.isChecked());
                        puLiftCategoryData.set((int) name.getTag(), country);
                    } else {
                        Category country = doLiftCategoryData.get((int) name.getTag());
                        country.setSelected(check.isChecked());
                        doLiftCategoryData.set((int) name.getTag(), country);
                    }
                    //notifyDataSetChanged();
                }
            });
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (type == 3) {
                        Category country = suitableVehicleData.get((int) name.getTag());
                        country.setSelected(isChecked);
                        suitableVehicleData.set((int) name.getTag(), country);
                    } else if (type == 1) {
                        Category country = puLiftCategoryData.get((int) name.getTag());
                        country.setSelected(isChecked);
                        puLiftCategoryData.set((int) name.getTag(), country);
                    } else {
                        Category country = doLiftCategoryData.get((int) name.getTag());
                        country.setSelected(isChecked);
                        doLiftCategoryData.set((int) name.getTag(), country);
                    }
                }
            });
            done.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    done(cattype);
                }
            });
            name.setText(category.getName());
            check.setChecked(category.isSelected());
            return convertView;
        }
    }

    public void done(int type) {
        String builder = "";
        try {
            if (type == 3) {
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
            } else if (type == 2) {
                doLiftIdBuilder = "";
                doLiftNameBuilder = "";
                for (int i = 0; i < doLiftCategoryData.size(); i++) {
                    if (doLiftCategoryData.get(i).isSelected()) {
                        builder += doLiftCategoryData.get(i).getName() + ", ";
                        doLiftIdBuilder += doLiftCategoryData.get(i).getId() + ",";
                        doLiftNameBuilder += doLiftCategoryData.get(i).getName() + ",";
                        JSONObject object = new JSONObject();
                        object.put("name", doLiftCategoryData.get(i).getName());
                        object.put("id", doLiftCategoryData.get(i).getId());
                        if (type == 1)
                            puLiftArray.put(object);
                        else doLiftArray.put(object);
                    }
                }
            } else {
                puLiftIdBuilder = "";
                puLiftNameBuilder = "";
                for (int i = 0; i < puLiftCategoryData.size(); i++) {
                    if (puLiftCategoryData.get(i).isSelected()) {
                        builder += puLiftCategoryData.get(i).getName() + ", ";
                        puLiftIdBuilder += puLiftCategoryData.get(i).getId() + ",";
                        puLiftNameBuilder += puLiftCategoryData.get(i).getName() + ",";
                        JSONObject object = new JSONObject();
                        object.put("name", puLiftCategoryData.get(i).getName());
                        object.put("id", puLiftCategoryData.get(i).getId());
                        if (type == 1)
                            puLiftArray.put(object);
                        else doLiftArray.put(object);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (builder.equals("")) {
            if (type == 1)
                showMessage("Please select pickup category");
            else if (type == 2)
                showMessage("Please select dropoff category");
            else
                showMessage("Please select carrier category");
        } else {
            if (type == 1)
                puLiftCategory.setText(builder.substring(0, builder.length() - 2));
            else if (type == 2)
                doLiftCategory.setText(builder.substring(0, builder.length() - 2));
            else
                suitableVehicle.setText(builder.substring(0, builder.length() - 2));
            if (mDialog != null && mDialog.isShowing())
                mDialog.dismiss();
        }
    }


    Dialog mDialog;

    public void showLiftEquipmentDialog(final int type) {
        mDialog = new Dialog(getActivity(), R.style.GrabidDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list_new);
        mDialog.setCanceledOnTouchOutside(false);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        ImageView close = (ImageView) mDialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done(cattype);
                /*if (type == 1 || type == 2) {
                    if (mDialog != null && mDialog.isShowing())
                        mDialog.dismiss();
                } else
                    done(cattype);*/
            }
        });
        if (type == 1)
            title.setText("Select Pick Up Category");
        else if (type == 2)
            title.setText("Select Drop Off Category");
        else title.setText("Select suitable vehicle");

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
        CategoryAdapter catAdapter = new CategoryAdapter(getActivity(), type);
        dialog_ListView.setAdapter(catAdapter);

        mDialog.show();
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
        submitShipment(builder);

    }
}
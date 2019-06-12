package com.bookmyride.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.adapters.FleetAdapter;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.models.Fleets;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vinod on 2017-01-20.
 */
public class DriverFleets extends AppCompatActivity implements AsyncTaskCompleteListener {
    ListView list;
    SessionHandler session;
    FleetAdapter adapter;
    ArrayList<Fleets> data = new ArrayList<Fleets>();
    String categoryId = "";
    boolean hasFleet = false;
    TextView noData, title;
    EditText search;
    String selectedVehicleId, selectedBrand, selectedNumber, selectedModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_fleets);
        init();
        categoryId = getIntent().getStringExtra("categoryId");
        if (getIntent().hasExtra("hasFleet")) {
            hasFleet = getIntent().getBooleanExtra("hasFleet", false);
        }
        getAvailableFleets();
    }

    private void init() {
        session = new SessionHandler(this);
        list = (ListView) findViewById(R.id.list);
        adapter = new FleetAdapter(this, data, getResources());
        noData = (TextView) findViewById(R.id.no_data);
        title = (TextView) findViewById(R.id.title);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Fleets selectedFleet = data.get(pos);
                selectedBrand = selectedFleet.getBrand();
                selectedVehicleId = selectedFleet.getVehicleId();
                selectedModel = selectedFleet.getModel();
                selectedNumber = selectedFleet.getVehicleNumber();
                goOnline(selectedFleet.getVehicleId(), selectedFleet.getModel(), selectedFleet.getVehicleNumber());
            }
        });
        search = (EditText) findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                adapter.filter(cs.toString());
                list.invalidate();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    int type;

    private void goOnline(String vehicleId, String model, String number) {
        type = 1;
        HashMap<String, String> params = new HashMap<>();
        params.put(Key.STATUS, "1");
        params.put("driverCategory_id", categoryId);
        params.put(Key.VEHICLE_NUM, number);
        params.put("vehicle_model", model);
        params.put(Key.VEHICLE_ID, vehicleId);
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.PUT, this, params);
            apiHandler.execute(Config.DRIVER_AVAILABLITY + "1", session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    public void onBack(View view) {
        //onBackPressed();
        Intent i = new Intent(this, DriverTypes.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        DriverFleets.this.finish();
    }

    private void getAvailableFleets() {
        type = 0;
        if (Internet.hasInternet(DriverFleets.this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this, null);
            apiHandler.execute(Config.FLEET + categoryId, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(DriverFleets.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
        mDialog.show();
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                if (type == 0) {
                    data.clear();
                    JSONObject dataObj = outJson.getJSONObject(Key.DATA);
                    if (dataObj.has("fleet_name"))
                        title.setText("Fleet: " + dataObj.getString("fleet_name"));
                    JSONArray fleetArray = dataObj.getJSONArray(Key.FLEET_VEHICLES);
                    for (int i = 0; i < fleetArray.length(); i++) {
                        JSONObject fleetObj = fleetArray.getJSONObject(i);
                        Fleets fleet = new Fleets();
                        fleet.setVehicleId(fleetObj.getString(Key.VEHICLE_ID));
                        fleet.setBrand(fleetObj.getString(Key.BRAND_NAME));
                        fleet.setModel(fleetObj.getString(Key.MODEL_NAME));
                        fleet.setVehicleNumber(fleetObj.getString(Key.VEHICLE_NUM));
                        fleet.setStatus(fleetObj.getString(Key.STATUS));
                        fleet.setCategoryId(categoryId);
                        if (fleetObj.getString(Key.STATUS).equals("1"))
                            data.add(fleet);
                    }
                    if (data.size() > 0) {
                        noData.setVisibility(View.GONE);
                        list.setVisibility(View.VISIBLE);
                        adapter = new FleetAdapter(this, this.data, getResources());
                        list.setAdapter(adapter);
                    } else {
                        list.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                    }
                } else if (type == 1) {
                    session.saveLastCategory(categoryId);
                    Bundle bundle = new Bundle();
                    bundle.putString("CategoryID", categoryId);
                    bundle.putBoolean("hasFleet", true);
                    bundle.putString("selectedNumber", selectedNumber);
                    bundle.putString("selectedVehicleId", selectedVehicleId);
                    bundle.putString("selectedModel", selectedModel);
                    bundle.putString("CategoryID", categoryId);
                    displayDriverMap(bundle);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
            } else if (outJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                Alert("Alert!", outJson.getString(Key.MESSAGE));
            } else {
                Alert("Alert!", outJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayDriverMap(Bundle bundle) {
        Fragment fragment = new com.bookmyride.fragments.DriverMapActivity();
        fragment.setArguments(bundle);
        FragmentTransaction ft = DriverHome.fm.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commitAllowingStateLoss();
    }
}


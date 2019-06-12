package com.bookmyride.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;


import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.adapters.DriverTypeAdapter;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.models.Cars;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vinod on 2017-01-20.
 */
public class DriverTypes extends AppCompatActivity implements AsyncTaskCompleteListener {
    TextView skip;
    ListView list;
    SessionHandler session;
    DriverTypeAdapter adapter;
    ArrayList<Cars> data = new ArrayList<Cars>();
    TextView done;
    boolean hasFleet = false;
    String fleetID;

    /*@Override
    protected void onStart() {
        super.onStart();
        for(int i=0; i<data.size(); i++){
            Log.e(""+i, ""+data.get(i).isFleetSelected());
        }
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_types);
        init();
        if (getIntent().hasExtra("hasFleet")) {
            hasFleet = getIntent().getBooleanExtra("hasFleet", false);
            fleetID = getIntent().getStringExtra("fleetID");
        }
        checkServiceAvailable();
    }

    private void init() {
        session = new SessionHandler(this);
        skip = (TextView) findViewById(R.id.skip);
        list = (ListView) findViewById(R.id.list);
        done = (TextView) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goOnline();
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        adapter = new DriverTypeAdapter(this, data);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                String id = data.get(pos).getId();
                categoryId = id;
                int selectedIndex = getIndex(id);
                Cars car = new Cars();
                car.setDefault(true);
                car.setIcon(data.get(pos).getIcon());
                car.setId(data.get(pos).getId());
                car.setSelectedIcon(data.get(pos).getSelectedIcon());
                car.setName(data.get(pos).getName());
                car.setFleetSelected(data.get(pos).isFleetSelected());
                data.set(selectedIndex, car);
                /*CategoryID = id;
                data.clear();
                adapter.notifyDataSetChanged();
                data = getCars(id);*/
                adapter.notifyDataSetChanged();

                /*if(hasFleet && fleetID.equals("null")) {
                    Alert("Alert!", "No fleets are assigned to you. Please contact with administrator.");
                } else if(hasFleet && !fleetID.equals("null")){
                    Intent intent = new Intent(DriverTypes.this, DriverFleets.class);
                    intent.putExtra("categoryId", categoryId);
                    intent.putExtra("hasFleet", hasFleet);
                    startActivity(intent);
                }*/
                if (data.get(pos).isFleetSelected()) {
                    Intent intent = new Intent(DriverTypes.this, DriverFleets.class);
                    intent.putExtra("categoryId", categoryId);
                    intent.putExtra("hasFleet", hasFleet);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } else {
                    goOnline();
                }
            }
        });
    }

    String categoryId = "1";
    int type;

    private void goOnline() {
        type = 1;
        HashMap<String, String> params = new HashMap<>();
        params.put(Key.STATUS, "1");
        params.put("driverCategory_id", categoryId);
        if (hasFleet) {
            Alert("", "You have no registered vehicles. Please choose the category and select fleets" +
                    " for continue services with BookMyRide.");
            return;
        } else {
            params.put(Key.VEHICLE_NUM, "0");
            params.put(Key.VEHICLE_ID, "0");
        }
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.PUT, this, params);
            apiHandler.execute(Config.DRIVER_AVAILABLITY + "1", session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private int getIndex(String id) {
        int selectedIndex = -1;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(id))
                selectedIndex = i;
            else {
                Cars car = new Cars();
                car.setDefault(false);
                car.setIcon(data.get(i).getIcon());
                car.setId(data.get(i).getId());
                car.setSelectedIcon(data.get(i).getSelectedIcon());
                car.setName(data.get(i).getName());
                car.setFleetSelected(data.get(i).isFleetSelected());
                data.set(i, car);
            }
        }
        return selectedIndex;
    }

    public void onBack(View view) {
        onBackPressed();
    }

    private void checkServiceAvailable() {
        type = 0;
        if (Internet.hasInternet(DriverTypes.this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this, null);
            apiHandler.execute(Config.SERVICE_AVAILABLES + "?driver_id=" + session.getUserID(), session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(DriverTypes.this, true);
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
                    JSONArray itemArray = dataObj.getJSONArray(Key.ITEMS);
                    for (int i = 0; i < itemArray.length(); i++) {
                        JSONObject itemObj = itemArray.getJSONObject(i);
                        Cars cars = new Cars();
                        cars.setId(itemObj.get(Key.ID).toString());
                        cars.setName(itemObj.getString(Key.NAME));
                        cars.setIcon(itemObj.getString(Key.ICON));
                        cars.setSelectedIcon(itemObj.getString(Key.SELECTED_ICON));
                        cars.setDefault((i == 0) ? true : false);
                        if(itemObj.has(Key.IS_FLEET_SELECTED))
                        cars.setFleetSelected(itemObj.get(Key.IS_FLEET_SELECTED).toString().equals("1") ? true : false);
                        else cars.setFleetSelected(false);
                        data.add(cars);
                        if (i == 0) {
                            categoryId = itemObj.get(Key.ID).toString();
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else if (type == 1) {
                    session.saveLastCategory(categoryId);
                    Bundle bundle = new Bundle();
                    bundle.putString("CategoryID", categoryId);
                    displayDriverMap(bundle);
                    finish();
                }
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

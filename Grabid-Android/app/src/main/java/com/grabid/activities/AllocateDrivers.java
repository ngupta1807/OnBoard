package com.grabid.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.models.AvailableDriver;
import com.grabid.models.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Vinod on 22/11/17.
 */

public class AllocateDrivers extends AppCompatActivity {
    View view;
    HashMap<Integer, String> vehicleTypeID = new HashMap<>();
    ArrayList<AvailableDriver> availableDrivers;
    ArrayList<AvailableDriver> arraylist;
    Category[] suitableVehicleData1;
    int index;
    String val = "", strvalue = "";
    String vehicleTypeIdStr = "";
    EditText searchET;
    SessionManager session;
    TextView mNoData;

    public void clickme(View view) {
        switch (view.getId()) {
            case R.id.back:
                cancelresult();
                break;
            case R.id.done:

                break;
        }
    }

    String deliveryID = "", promptMsg = "";

    private void getAvailableDrivers() {
        if (Internet.hasInternet(this)) {
            RestAPICall mobileAPI = new RestAPICall(this, HTTPMethods.GET, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    Log.e("res", result);
                    try {
                        JSONObject outerObj = new JSONObject(result);
                        if (Integer.parseInt(outerObj.getString(Config.STATUS)) == APIStatus.SUCCESS) {
                            JSONArray outerArray = outerObj.getJSONArray("data");
                            for (int i = 0; i < outerArray.length(); i++) {
                                JSONObject innerObj = outerArray.getJSONObject(i);
                                AvailableDriver driver = new AvailableDriver();
                                driver.setId(innerObj.getString("id"));
                                driver.setUsername(innerObj.getString("username"));
                                driver.setMoible(innerObj.getString("mobile"));
                                driver.setDriverRating(innerObj.get("driver_rating").toString());
                                availableDrivers.add(driver);
                            }
                            if (catAdapter != null)
                                catAdapter.notifyDataSetChanged();
                            mListView.setVisibility(View.VISIBLE);
                            mNoData.setVisibility(View.GONE);
                        } else if (Integer.parseInt(outerObj.getString(Config.STATUS)) == APIStatus.UNPROCESSABLE) {
                            if (!outerObj.getString(Config.MESSAGE).contentEquals(""))
                                AlertManager.messageDialog(AllocateDrivers.this, "Alert!", outerObj.getString(Config.MESSAGE));
                            else
                                AlertManager.messageDialog(AllocateDrivers.this, "Alert!", outerObj.getJSONArray("data").getJSONObject(0).getString(Config.MESSAGE));
                            mListView.setVisibility(View.GONE);
                            mNoData.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
            mobileAPI.execute(Config.GET_AVAILABLE_DRIVERS + deliveryID, session.getToken());
        } else
            AlertManager.messageDialog(this, "Alert!", getResources().getString(R.string.no_internet));
    }

    ListView mListView;
    CategoryAdapter catAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            e.toString();
        }
        setContentView(R.layout.available_driver_list);
        session = new SessionManager(this);
        deliveryID = getIntent().getStringExtra("delivery_id");
        if (getIntent().getStringExtra("incomingType").equals("chauffeur"))
            promptMsg = getResources().getString(R.string.allocatetransfer);
        else
            promptMsg = getResources().getString(R.string.allocatedelivery);
        availableDrivers = new ArrayList<AvailableDriver>();
        this.arraylist = new ArrayList<AvailableDriver>();
        mListView = (ListView) findViewById(R.id.list);
        mNoData = (TextView) findViewById(R.id.no_data);
        catAdapter = new CategoryAdapter(AllocateDrivers.this);
        mListView.setAdapter(catAdapter);
        searchET = (EditText) findViewById(R.id.search_et);
        // Capture Text in EditText
        searchET.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = searchET.getText().toString().toLowerCase(Locale.getDefault());
                catAdapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });
        getAvailableDrivers();
    }


    public void sendData() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("value", val);
        returnIntent.putExtra("index", index);
        returnIntent.putExtra("strvalue", strvalue);
        returnIntent.putExtra("suitableidbuilder", suitableIdBuilder);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void cancelresult() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    String suitableIdBuilder = "";

    private class CategoryAdapter extends BaseAdapter {
        Context ctx;

        CategoryAdapter(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public int getCount() {
            return availableDrivers.size();
        }

        @Override
        public Object getItem(int i) {
            return availableDrivers.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater vi = (LayoutInflater) getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.searchaddlayout, null);
            AvailableDriver driver = (AvailableDriver) getItem(position);
            final TextView nameLevel = (TextView) convertView.findViewById(R.id.del);
            final TextView name = (TextView) convertView.findViewById(R.id.title);
            final TextView mobileLevel = (TextView) convertView.findViewById(R.id.del1);
            final TextView mobile = (TextView) convertView.findViewById(R.id.mobile);
            final TextView allocate = (TextView) convertView.findViewById(R.id.addfav);
            final RatingBar rating = (RatingBar) convertView.findViewById(R.id.rating);
            name.setTag(position);
            allocate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    /*Intent intent = new Intent();
                    intent.putExtra("name", availableDrivers.get((Integer) name.getTag()).getUsername());
                    intent.putExtra("id", availableDrivers.get((Integer) name.getTag()).getId());
                    setResult(RESULT_OK, intent);
                    AllocateDrivers.this.finish();*/
                    allocationPrompt("Alert!", promptMsg, availableDrivers.get((Integer) name.getTag()).getId());
                    //allocationDriver(availableDrivers.get((Integer) name.getTag()).getId());
                }
            });
            name.setText(driver.getUsername());
            //nameLevel.setText(driver.getUsername());
            //mobileLevel.setText(driver.getUsername());
            mobile.setText(driver.getMoible());
            allocate.setText("ALLOCATE");
            rating.setRating(Float.valueOf(driver.getDriverRating()));
            rating.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            return convertView;
        }

        public void allocationPrompt(String title, String message, final String driverID) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AllocateDrivers.this);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    allocationDriver(driverID);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            Dialog d = builder.create();
            d.show();
        }


        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            availableDrivers.clear();
            if (charText.length() == 0) {
                availableDrivers.addAll(arraylist);
            } else {
                for (AvailableDriver wp : arraylist) {
                    if (wp.getUsername().toLowerCase(Locale.getDefault()).contains(charText)) {
                        availableDrivers.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    private void allocationDriver(String driverID) {
        HashMap<String, String> params = new HashMap<>();
        params.put("delivery_id", deliveryID);
        params.put("allocated_driver_id", driverID);
        if (Internet.hasInternet(this)) {
            RestAPICall mobileAPI = new RestAPICall(AllocateDrivers.this, HTTPMethods.POST, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    try {
                        JSONObject outJson = new JSONObject(result);
                        if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                            messageDialog("Success!", outJson.getString("message"));
                        } else {
                            messageDialog("Alert!", outJson.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, params);
            mobileAPI.execute(Config.ALLOCATE_DRIVER, session.getToken());
        } else
            AlertManager.messageDialog(this, "Alert!", getResources().getString(R.string.no_internet));
    }

    public void messageDialog(final String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AllocateDrivers.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (title.equalsIgnoreCase("Success!")) {
                    Intent intent = new Intent();
                    intent.putExtra("allocated", "1");
                    setResult(RESULT_OK, intent);
                    AllocateDrivers.this.finish();
                } else {

                }
            }
        });
        Dialog d = builder.create();
        d.show();
    }

}
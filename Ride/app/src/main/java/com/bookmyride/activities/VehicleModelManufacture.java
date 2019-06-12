package com.bookmyride.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.adapters.ModelManufactureAdapter;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.Internet;
import com.bookmyride.models.Country;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vinod on 2017-01-08.
 */
public class VehicleModelManufacture extends AppCompatActivity implements AsyncTaskCompleteListener{
    EditText search;
    ListView mList;
    Context mcom;
    String type, category;
    TextView title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);
        init();
    }
    public void onBack(View view){
        onBackPressed();
    }
    private void init() {
        mcom = this;
        title = (TextView) findViewById(R.id.title);
        search = (EditText) findViewById(R.id.search);
        mList = (ListView) findViewById(R.id.list);
        getListData(getIntent().getStringExtra("endPoint"));
        category = getIntent().getStringExtra("category");
        type =  getIntent().getStringExtra("type");
        title.setText(getIntent().getStringExtra("title"));
        SearchData();
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("name", data.get(i).getName());
                intent.putExtra("id", data.get(i).getId());
                //intent.putExtra("url", data.get(i).getCode());
                intent.putExtra("category", getIntent().getStringExtra("category"));
                setResult(Activity.RESULT_OK,intent);
                VehicleModelManufacture.this.finish();
            }
        });
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void SearchData() {
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                adapter.filter(cs.toString());
                mList.invalidate();
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

    // *********** used to load alerts *********//*
    public void getListData(String endPoint) {
        if(Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this, null);
            apiHandler.execute(endPoint, "");
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));

    }

    ArrayList<Country> data = new ArrayList<>();
    ModelManufactureAdapter adapter;

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(VehicleModelManufacture.this, true);
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

    public void handleResponse(String result) {
        try {
            JSONObject data =new JSONObject(result);
            JSONArray countries = data.getJSONArray(Key.DATA);
            for (int i = 0; i < countries.length(); i++) {
                JSONObject obj = (JSONObject) countries.get(i);
                Country cm = new Country();
                cm.setId(obj.getString("id"));
                cm.setName(obj.getString("name"));
                //cm.setCode(obj.getString("url"));
                this.data.add(cm);
            }
            Country cm = new Country();
            if(type.equals("1")) {
                cm.setId("Other Manufacturer");
                cm.setName("Other Manufacturer");
            } else {
                cm.setId("Other Model");
                cm.setName("Other Model");
            }
            this.data.add(this.data.size(),cm);
            if(this.data.size()>0) {
                mList.setVisibility(View.VISIBLE);
                adapter = new ModelManufactureAdapter(mcom, this.data, type,
                        mcom.getResources());
                mList.setAdapter(adapter);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

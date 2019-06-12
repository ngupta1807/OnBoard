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

import com.bookmyride.R;
import com.bookmyride.adapters.CountryAdapter;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
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
public class CountryList extends AppCompatActivity implements AsyncTaskCompleteListener {
    EditText search;
    ListView countrydata;
    Context mcom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);
        init();
    }

    public void onBack(View view) {
        onBackPressed();
    }

    private void init() {
        mcom = this;
        search = (EditText) findViewById(R.id.search);
        countrydata = (ListView) findViewById(R.id.countrydata);
        getCountryList();
        SearchData();
        countrydata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("name", acm.get(i).getName());
                intent.putExtra("id", acm.get(i).getId());
                intent.putExtra("code", acm.get(i).getCode());
                setResult(Activity.RESULT_OK, intent);
                CountryList.this.finish();
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
                countryadapter.filter(cs.toString());
                countrydata.invalidate();
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
    public void getCountryList() {
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this, null);
            apiHandler.execute(Config.COUNTRY_LIST, "");
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    ArrayList<Country> acm = new ArrayList<>();
    CountryAdapter countryadapter;

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(CountryList.this, true);
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
            JSONObject data = new JSONObject(result);
            JSONArray countries = data.getJSONArray(Key.DATA);
            for (int i = 0; i < countries.length(); i++) {
                JSONObject obj = (JSONObject) countries.get(i);
                Country cm = new Country();
                cm.setId(obj.getString("id"));
                cm.setName(obj.getString("country"));
                cm.setCode(obj.getString("dialCode"));
                acm.add(cm);
            }
            if (acm.size() > 0) {
                countrydata.setVisibility(View.VISIBLE);
                countryadapter = new CountryAdapter(mcom, acm,
                        mcom.getResources());
                countrydata.setAdapter(countryadapter);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

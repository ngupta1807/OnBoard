package com.grabid.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.grabid.BuildConfig;
import com.grabid.R;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.RestAPICall;
import com.grabid.common.Internet;

import org.json.JSONObject;

public class TermsAndConditions extends AppCompatActivity implements View.OnClickListener, AsyncTaskCompleteListener {
    String type;
    ImageView nav;
    private WebView mWebview;
    TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            e.toString();
        }
        setContentView(R.layout.activity_terms_and_conditions);
        type = getIntent().getStringExtra("type");
        mWebview = (WebView) findViewById(R.id.textterm);
        mTitle = (TextView) findViewById(R.id.title);
        if (type.equals("1"))
            mTitle.setText(getResources().getString(R.string.termsconditions));
        else
            mTitle.setText(getResources().getString(R.string.privacypolicy));
        getData();
        //  mWebview.loadUrl("file:///android_asset/privacypolicy.html");
        nav = (ImageView) findViewById(R.id.nav);
        nav.setOnClickListener(this);

    }

    public void getData() {
        try {
            String url = "";
            if (BuildConfig.logistic)
                url = Config.SERVER_URL + Config.FAQTERM;
            else
                url = Config.SERVER_URL + Config.FAQCHAUFFER;
            if (Internet.hasInternet(TermsAndConditions.this)) {
                RestAPICall mobileAPI = new RestAPICall(TermsAndConditions.this, HTTPMethods.GET, this, null);
                mobileAPI.execute(url, "");
            }
        } catch (Exception e) {
            e.toString();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav: {
                // onBackPressed();
                finish();
            }
            break;
        }
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            JSONObject data = outJson.optJSONObject(Config.DATA);
            String description = "";
            if (type.equals("1"))
                description = data.optString("term-and-conditions");
            else
                description = data.optString("privacy-and-policy");
            if (description != null && !description.contentEquals("")) {
                mWebview.loadData(description, "text/html; charset=utf-8", "UTF-8");
            }

        } catch (Exception e) {
            e.toString();
        }
    }
}
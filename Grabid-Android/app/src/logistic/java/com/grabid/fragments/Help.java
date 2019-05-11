package com.grabid.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.adapters.HelpAdapter;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.models.HelpM;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class Help extends Fragment implements AsyncTaskCompleteListener, View.OnClickListener {
    SessionManager session;
    ListView mHelpList;
    ArrayList<HelpM> categories = new ArrayList<>();
    HelpAdapter mAdapter;
    TextView noData, mSupport;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.title.setText(getResources().getString(R.string.help));
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        View view = inflater.inflate(R.layout.help, null);
        // ((WebView) view.findViewById(R.id.HelpM)).loadUrl("file:///android_asset/about.html");
        init(view);
        return view;
    }

    public void UpdateDesign() {
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
    }


    public void init(View view) {
        session = new SessionManager(getActivity());
        mHelpList = (ListView) view.findViewById(R.id.helpList);
        noData = (TextView) view.findViewById(R.id.no_data);
        mSupport = (TextView) view.findViewById(R.id.support);
        mAdapter = new HelpAdapter(getActivity(), categories);
        mHelpList.setSmoothScrollbarEnabled(true);
        mHelpList.setAdapter(mAdapter);
        mSupport.setOnClickListener(this);
        getFaq();
    }

    public void sendEmailMesage() {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "support@grabid.com.au"));
            //  emailIntent.putExtra(Intent.EXTRA_SUBJECT, deliveryData.getTitle());
            try {
                startActivity(Intent.createChooser(emailIntent, "Support"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.toString();
        }
    }

    private void getFaq() {
        String url = Config.SERVER_URL + Config.FAQ;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else AlertManager.messageDialog(getActivity(), "Alert!", "");
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        if (categories.size() > 0)
            categories.clear();
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                JSONArray jsonArray = outJson.optJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObj = jsonArray.optJSONObject(i);
                    JSONArray childJson = jObj.optJSONArray("faqlist");
                    if (childJson.length() > 0) {
                        Log.v("elements", childJson.toString());
                        for (int j = 0; j < childJson.length(); j++) {
                            JSONObject jObjchild = childJson.optJSONObject(j);
                            HelpM helpmb = new HelpM();
                            helpmb.setCategoryType(jObj.optString("name"));
                            helpmb.setCategoryTitle(jObjchild.optString("title"));
                            helpmb.setCategorydescription(jObjchild.optString("description"));
                            helpmb.setIsselected(false);
                            if (j >= 1)
                                helpmb.setCategory(false);
                            else
                                helpmb.setCategory(true);
                            categories.add(helpmb);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                if (categories.size() == 0) {
                    mHelpList.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                } else {
                    mHelpList.setVisibility(View.VISIBLE);
                    noData.setVisibility(View.GONE);
                }
            } else if (outJson.getInt(Config.STATUS) == APIStatus.UNPROCESSABLE) {
                AlertManager.messageDialog(getActivity(), "Alert!", outJson.getString(Config.MESSAGE));
            } else
                AlertManager.messageDialog(getActivity(), "Alert!", "Error");

            // Log.v("", outJson.toString());

        } catch (Exception e) {
            e.toString();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.support:
                sendEmailMesage();
                break;
        }
    }
}
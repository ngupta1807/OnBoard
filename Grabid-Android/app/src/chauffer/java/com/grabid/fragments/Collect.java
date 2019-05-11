package com.grabid.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.adapters.CollectAdapter;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.models.CollectJob;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by graycell on 30/3/18.
 */

public class Collect extends Fragment implements AsyncTaskCompleteListener {
    SessionManager session;
    ListView list;
    CollectAdapter mAdapter;
    TextView noData;
    ArrayList<CollectJob> collectData = new ArrayList<CollectJob>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.collect, null);
        session = new SessionManager(getActivity());
        initTopBar();
        init(view);
        getCollectInfo();
        return view;
    }

    public void init(View view) {
        list = (ListView) view.findViewById(R.id.list);
        mAdapter = new CollectAdapter(getActivity(), collectData);
        noData = view.findViewById(R.id.no_data);
        list.setAdapter(mAdapter);
    }

    public void initTopBar() {
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.title.setText(getResources().getString(R.string.collect));
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.edit.setBackgroundResource(R.drawable.edit_top);
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
    }

    @Override
    public void onTaskComplete(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            JSONArray innerObject = outJson.getJSONArray(Config.DATA);
            for (int i = 0; i < innerObject.length(); i++) {
                JSONObject cardObj = innerObject.getJSONObject(i);
                CollectJob cljob = new CollectJob();
                cljob.setAmount(cardObj.getString(Keys.AMOUNT));
                cljob.setAmountcollected(cardObj.getString(Keys.AMOUNT_COLLECTED));
                cljob.setCreatedAt(cardObj.getString(Keys.CREATED_AT));
                try {
                    cljob.setDeliveryId(cardObj.getString(Keys.KEY_JOB_ID));
                }catch (Exception ex){
                    cljob.setDeliveryId("");
                }
               // cljob.setDeliveryId(cardObj.getString(Keys.KEY_JOB_ID));
                cljob.setDeliveryStatus(cardObj.getString(Keys.DELIVERY_STATUS));
                cljob.setPaymenttype(cardObj.getString(Keys.PAYMENT_TYPE));
                cljob.setPaymodeid(cardObj.getString(Keys.PAYMENT_MODE));
                cljob.setItemdeliverytitle(cardObj.getString(Keys.ITEM_DELIVERY_TITLE));
                collectData.add(cljob);
            }
            list.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
            if (collectData.size() == 0) {
                noData.setVisibility(View.VISIBLE);
                list.setVisibility(View.GONE);
            } else {
                noData.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void getCollectInfo() {
        String url = Config.SERVER_URL + Config.COLLECT_DETAIL;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage(TITLE, getResources().getString(R.string.no_internet));
    }

    String TITLE = "Alert!";

    private void showMessage(String title, String message) {
        AlertManager.messageDialog(getActivity(), title, message);
    }

}

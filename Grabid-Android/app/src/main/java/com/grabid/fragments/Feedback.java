package com.grabid.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.adapters.FeedbackAdapter;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vinod on 10/14/2016.
 */
public class Feedback extends Fragment implements View.OnClickListener,
        AsyncTaskCompleteListener {
    ListView list;
    TextView noData;
    FeedbackAdapter adapter;
    SessionManager session;
    ArrayList<com.grabid.models.Feedback> feedbackData = new ArrayList<com.grabid.models.Feedback>();
    String incomingType;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    int page = 1;
    int totalCount = 1;
    boolean loadingMore = false;
    int type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        View view = inflater.inflate(R.layout.feedback, null);
        init(view);
        if (page == 1) {
            type = getArguments().getInt("type");
            getFeedbacks(getArguments().getInt("type"));
        }

        if (getArguments().getInt("type") == 1) {
            HomeActivity.title.setText(getResources().getString(R.string.shipper_feedback));
//            incomingType = "shipper";
            incomingType = "feedback";
        } else {
            HomeActivity.title.setText(getResources().getString(R.string.driver_feedback));
            incomingType = "feedback";
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(ratings, new IntentFilter("sendRatings"));
    }

    private BroadcastReceiver ratings = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String str = intent.getAction();
            try {
                if (str != null && str.contentEquals("sendRatings")) {
                    page = 1;
                    getFeedbacks(type);

                }
            } catch (Exception e) {
                e.toString();
            }

        }
    };

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(ratings);
    }

    private void init(View view) {
        session = new SessionManager(getActivity());
        list = (ListView) view.findViewById(R.id.list);
        noData = (TextView) view.findViewById(R.id.no_data);
        adapter = new FeedbackAdapter(getActivity(), feedbackData);
        list.setSmoothScrollbarEnabled(true);
        list.setAdapter(adapter);
        list.setItemsCanFocus(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String backStateName = this.getClass().getName();
                Bundle bundle = new Bundle();
                bundle.putString("delivery_id", feedbackData.get(i).getDeliveryID());
                bundle.putString("incoming_type", incomingType);
                bundle.putSerializable("incoming_delivery_type", "");
                Fragment fragment = new DeliveryInfo();
                fragment.setArguments(bundle);
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        });
        list.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView view,
                                 int firstVisibleItemm, int visibleItemCountt,
                                 int totalItemCountt) {
                firstVisibleItem = firstVisibleItemm;
                visibleItemCount = visibleItemCountt;
                totalItemCount = totalItemCountt;


            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (firstVisibleItem > 0 && lastItem == totalItemCount && scrollState == SCROLL_STATE_IDLE) {
                    if (!loadingMore) {
                        if (totalCount >= page) {
                            loadingMore = true;
                            getFeedbacks(getArguments().getInt("type"));
                        }
                    }

                    //  new AsyncTask().execute();

                    //get next 10-20 items(your choice)items

                }
            }
        });

    }

    @Override
    public void onTaskComplete(String result) {
        Log.d(Config.TAG, result);
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                JSONObject dataObj = outJson.getJSONObject(Config.DATA);
                JSONArray feedbackArray = dataObj.getJSONArray(Config.FEEDBACK);
                if (page == 1)
                    feedbackData.clear();
                ++page;
                loadingMore = false;
                for (int i = 0; i < feedbackArray.length(); i++) {
                    JSONObject feedbackObj = feedbackArray.getJSONObject(i);
                    com.grabid.models.Feedback feedback = new com.grabid.models.Feedback();
                    feedback.setId(feedbackObj.getString(Keys.KEY_ID));
                    feedback.setUserID(feedbackObj.getString(Keys.KEY_ID));
                    feedback.setDeliveryTitle(feedbackObj.getString(Keys.DELIVERY_TITLE));
                    feedback.setDeliveryID(feedbackObj.getString(Keys.DELIVERY_ID));
                    feedback.setFeedback(feedbackObj.getString(Keys.FEEDBACK));
                    feedback.setRating(feedbackObj.getString(Keys.RATING));
                    feedback.setGivenBy(feedbackObj.getString(Keys.GIVEN_BY));
                    feedback.setUpdatedBy(feedbackObj.getString(Keys.UPDATED_BY));
                    feedbackData.add(feedback);
                }
                adapter.notifyDataSetChanged();
                if (feedbackData.size() == 0) {
                    list.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                } else {
                    list.setVisibility(View.VISIBLE);
                    noData.setVisibility(View.GONE);
                }
                try {
                    if (dataObj.has("_meta")) {
                        JSONObject metaCount = dataObj.getJSONObject("_meta");
                        totalCount = Integer.parseInt(metaCount.optString("pageCount"));
                        Log.v("totalcount", String.valueOf(totalCount));

                    }
                } catch (Exception e) {
                    e.toString();
                }
            } else {
                AlertManager.messageDialog(getActivity(), "Alert!", "Error");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            list.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {

    }

    private void getFeedbacks(int type) {
        if (page == 1)
            feedbackData.clear();
        String url;
        if (type == 1)
            url = Config.SERVER_URL + Config.FEEDBACK_SHIPPER;
        else url = Config.SERVER_URL + Config.FEEDBACKS;

        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }
}
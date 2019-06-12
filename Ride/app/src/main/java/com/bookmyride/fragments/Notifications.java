package com.bookmyride.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.adapters.NotificationAdapter;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.api.OnLoadMoreListener;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.models.Notification;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vinod on 6/2/2017.
 */
public class Notifications extends Fragment implements AsyncTaskCompleteListener {
    ListView list;
    TextView noData;
    SessionHandler session;
    NotificationAdapter adapter;
    ArrayList<com.bookmyride.models.Notification> data = new ArrayList<>();
    int pageCount, currentPage = 0;
    public static boolean hasMore = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notifications, null);
        init(view);
        getNotifications();
        return view;
    }

    private OnLoadMoreListener loadMore = new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (hasMore)
                getNotifications();
        }
    };
    int selectedPos = -1;

    private void init(View view) {
        session = new SessionHandler(getActivity());
        list = (ListView) view.findViewById(R.id.list);
        noData = (TextView) view.findViewById(R.id.no_data);
        adapter = new NotificationAdapter(getActivity(), data, loadMore);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!data.get(i).isRead()) {
                    selectedPos = i;
                    readNotifications(data.get(i).getId());
                }
            }
        });
    }

    private void getNotifications() {
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET, this, null);
            apiHandler.execute(Config.NOTIFICATION + "?page=" + (++currentPage), session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                if (data.size() > 0 && !hasMore)
                    data.clear();
                JSONArray jsonArray;
                if (outJson.get(Key.DATA) instanceof JSONArray) {
                    jsonArray = outJson.getJSONArray(Key.DATA);
                } else {
                    jsonArray = outJson.getJSONObject(Key.DATA).getJSONArray(Key.ITEMS);
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject innerObj = jsonArray.getJSONObject(i);
                    com.bookmyride.models.Notification notification = new com.bookmyride.models.Notification();
                    if (innerObj.has("details")) {
                        //if(innerObj.get("details") instanceof JSONObject) {
                        String msg;
                        String detail = innerObj.get("details").toString();
                        try {
                            JSONObject detailObj = new JSONObject(detail);
                            msg = detailObj.getString("msg");
                        } catch (JSONException e) {
                            msg = innerObj.getString("details");
                            e.printStackTrace();
                        }
                        notification.setMessage(msg);
                        notification.setId(innerObj.getString("id"));
                        //notification.setTitle("");
                        notification.setRead(innerObj.get("is_read").toString().equals("1") ? true : false);
                        if (!msg.equals("") && !msg.equals("null"))
                            data.add(notification);
                        //}
                    }
                }
                adapter.notifyDataSetChanged();
                if (data.size() > 0) {
                    noData.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                } else {
                    list.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                }

                pageCount = outJson.getJSONObject(Key.DATA).getJSONObject("_meta").getInt("pageCount");
                currentPage = outJson.getJSONObject(Key.DATA).getJSONObject("_meta").getInt("currentPage");

                if (currentPage < pageCount)
                    hasMore = true;
                else hasMore = false;
            } else
                Alert("Alert!", outJson.getString(Key.MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(getActivity(), true);
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

    private void readNotifications(String id) {
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    try {
                        JSONObject outerJson = new JSONObject(result);
                        if (outerJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                            updateStatus();
                            adapter.notifyDataSetChanged();
                        } else {
                            Alert("Alert!", outerJson.getString(Key.MESSAGE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
            apiHandler.execute(Config.NOTIFICATION + "/read?id=" + id, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void updateStatus() {
        Notification msg = data.get(selectedPos);
        Notification newMsg = new Notification();
        newMsg.setMessage(msg.getMessage());
        newMsg.setRead(true);
        newMsg.setId(msg.getId());
        data.set(selectedPos, newMsg);
    }

    private int getPosition(String id) {
        int index = -1;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(id)) {
                return i;
            }
        }
        return index;
    }
}

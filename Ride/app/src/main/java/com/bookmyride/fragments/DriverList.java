package com.bookmyride.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.adapters.DriverAdapter;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.api.OnLoadMoreListener;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.models.Drivers;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DriverList extends Fragment implements AsyncTaskCompleteListener,
        View.OnClickListener {
    LinearLayout allRides, completed;
    TextView allView, favouriteView;
    ListView rideList;
    SessionHandler session;
    DriverAdapter adapter;
    ArrayList<Drivers> allData = new ArrayList<>();
    ArrayList<Drivers> favouriteData = new ArrayList<>();
    int selectedType;
    TextView noData;
    EditText search;
    int pageCount, currentPage;
    public static boolean hasMore = false;
    String userType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.driver_list, null);
        init(view);
        updateUI(1);
        return view;
    }

    private void init(View view) {
        session = new SessionHandler(getActivity());
        userType = session.getUserType();
        allRides = (LinearLayout) view.findViewById(R.id.all);
        allRides.setOnClickListener(this);
        completed = (LinearLayout) view.findViewById(R.id.favourite);
        completed.setOnClickListener(this);
        allView = (TextView) view.findViewById(R.id.h_view);
        favouriteView = (TextView) view.findViewById(R.id.d_view);
        rideList = (ListView) view.findViewById(R.id.driver_list);
        noData = (TextView) view.findViewById(R.id.no_data);
        search = (EditText) view.findViewById(R.id.search);
    }

    private OnLoadMoreListener loadMore = new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (hasMore)
                getDrivers("" + selectedType);
        }
    };

    private void setListAdapter(final ArrayList<Drivers> rideData) {
        adapter = new DriverAdapter(getActivity(), rideData, loadMore);
        rideList.setAdapter(adapter);
        /*rideList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Drivers selected = rideData.get(position);
                startActivity(new Intent(getActivity(), RideDetail.class)
                .putExtra("rideDetail", (Serializable) selected));
            }
        }); */
        if (rideData.size() > 0) {
            noData.setVisibility(View.GONE);
            rideList.setVisibility(View.VISIBLE);
        } else {
            rideList.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.all:
                updateUI(1);
                break;
            case R.id.favourite:
                updateUI(2);
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
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                JSONArray items;
                if (outJson.get(Key.DATA) instanceof JSONArray) {
                    items = outJson.getJSONArray(Key.DATA);
                } else {
                    items = outJson.getJSONObject(Key.DATA).getJSONArray(Key.ITEMS);
                }
                if (selectedType == 1) {
                    if (items.length() > 0 && !hasMore)
                        allData.clear();
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject obj = items.getJSONObject(i).getJSONObject("driverdata");
                        Drivers driver = new Drivers();
                        driver.setId(obj.getString("id"));
                        driver.setFullName(obj.getString("fullName"));
                        driver.setImgUrl(obj.getString("image"));
                        String profileDataObj = obj.get("profileData").toString();
                        if (!profileDataObj.equals("") && !profileDataObj.equals("null")) {
                            JSONObject profileObj = new JSONObject(profileDataObj);
                            String address = profileObj.get("address").toString();
                            JSONObject addressObj = new JSONObject(address);
                            driver.setAddress(addressObj.getString("address"));
                            driver.setRating(profileObj.getDouble("rating"));
                        } else {
                            driver.setAddress("");
                            driver.setRating(0);
                        }

                        allData.add(driver);
                    }

                } else if (selectedType == 2) {
                    if (items.length() > 0 && !hasMore)
                        favouriteData.clear();
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject obj = items.getJSONObject(i).getJSONObject("driverdata");
                        Drivers driver = new Drivers();
                        driver.setId(obj.getString("driver_id"));
                        driver.setFullName("Demo");
                        driver.setImgUrl("");
                        driver.setAddress(obj.getString("address"));
                        //driver.setFullName(obj.getString("fullName"));
                        //driver.setImgUrl(obj.getString("image"));
                        favouriteData.add(driver);
                    }
                }

                adapter.notifyDataSetChanged();
                if (adapter.getCount() > 0)
                    noData.setVisibility(View.GONE);
                else noData.setVisibility(View.VISIBLE);

                if (outJson.has("_meta")) {
                    pageCount = outJson.getJSONObject(Key.DATA).getJSONObject("_meta").getInt("pageCount");
                    currentPage = outJson.getJSONObject(Key.DATA).getJSONObject("_meta").getInt("currentPage");

                    if (currentPage < pageCount)
                        hasMore = true;
                    else hasMore = false;
                } else hasMore = false;
            } else {
                Alert("Alert!", outJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getDrivers(String status) {
        /*String type;
        if(userType.equals("3"))
            type = "0";
        else
            type = "1";
        String endPoint;
        if(status.equals("1"))
            endPoint = Config.DRIVERS_LIST + "?page=" +(++currentPage);
        else*/
        String endPoint = Config.FAVOURITE_DRIVERS_LIST + "?type=0&expand=driverdata&page=" + (++currentPage);
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.POST, this, null);
            apiHandler.execute(endPoint, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
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

    private void updateUI(int type) {
        selectedType = type;
        if (type == 1) {
            currentPage = 0;
            setListAdapter(allData);
            getDrivers("1");
            search.setVisibility(View.GONE);
            rideList.setVisibility(View.VISIBLE);
            allView.setBackgroundResource(R.color.driver_color);
            favouriteView.setBackgroundResource(R.color.white);
        } else if (type == 2) {
            currentPage = 0;
            setListAdapter(favouriteData);
            getDrivers("2");
            search.setVisibility(View.GONE);
            rideList.setVisibility(View.VISIBLE);
            allView.setBackgroundResource(R.color.white);
            favouriteView.setBackgroundResource(R.color.driver_color);
        }
    }
}

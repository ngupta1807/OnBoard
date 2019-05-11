package com.grabid.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.models.Shipper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vinod on 10/14/2016.
 */
public class ShipperInfo extends Fragment implements View.OnClickListener,
        AsyncTaskCompleteListener {
    SessionManager session;
    TextView company, address, suburb, state, country, userName;
    RatingBar shipperRating, driverRating;
    String shipperID;
    ImageView mImg;
    boolean IsShipper = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        View view = inflater.inflate(R.layout.shipper_info, null);
        init(view);
        if (getArguments().containsKey("shipper_id")) {
            shipperID = getArguments().getString("shipper_id");
            IsShipper = getArguments().getBoolean("isShipper");
        }
        if (IsShipper)
            HomeActivity.title.setText(getResources().getString(R.string.senderdetail));
        else
            HomeActivity.title.setText(getResources().getString(R.string.driverdetail));
        getShipperInfo();
        return view;
    }

    private void init(View view) {
        session = new SessionManager(getActivity());
        company = (TextView) view.findViewById(R.id.company_name);
        address = (TextView) view.findViewById(R.id.address);
        suburb = (TextView) view.findViewById(R.id.suburb);
        state = (TextView) view.findViewById(R.id.state);
        country = (TextView) view.findViewById(R.id.country);
        userName = (TextView) view.findViewById(R.id.user);
        shipperRating = (RatingBar) view.findViewById(R.id.rating_shipper);
        driverRating = (RatingBar) view.findViewById(R.id.rating_driver);
        mImg = (ImageView) view.findViewById(R.id.imageView);
    }

    private void appendData(com.grabid.models.Shipper shipper) {
        company.setText(shipper.getCompany());
        address.setText(shipper.getAddress());
        suburb.setText(shipper.getSuburb());
        state.setText(shipper.getState());
        country.setText(shipper.getCountry());
        userName.setText(shipper.getName());
        Log.v("rat","getDriverRating"+shipper.getShipperRating());
        Log.v("rat","getDriverRating"+shipper.getDriverRating());
        shipperRating.setRating(Float.valueOf(shipper.getDriverRating())); //Float.valueOf(shipper.getShipperRating())
        driverRating.setRating(Float.valueOf(shipper.getDriverRating()));
        shipperRating.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        driverRating.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        try {
            if (shipper.getProfileimg() != null && !shipper.getProfileimg().equals("") && !shipper.getProfileimg().contentEquals("null"))
                Picasso.with(getActivity()).load(shipper.getProfileimg()).into(mImg);
        } catch (Exception e) {
            e.toString();
        }
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
                Shipper shipper = new Shipper();
                JSONObject data = outJson.getJSONObject(Config.DATA);
                JSONObject user = data.getJSONObject(Config.USER);
                shipper.setId(user.getString(Keys.KEY_ID));
                shipper.setName(user.getString(Keys.USERNAME));
                shipper.setShipperRating(user.getString(Keys.SHIPPER_RATING));
                shipper.setDriverRating(user.getString(Keys.DRIVER_RATING));
                shipper.setProfileimg(user.optString(Keys.KEY_IMAGE));
                String key = data.get(Config.COMPANY).toString();
                if (!key.equals("null")) {
                    JSONObject company = data.getJSONObject(Config.COMPANY);
                    shipper.setCompany(company.getString(Keys.NAME));
                    shipper.setAddress(company.getString(Keys.ADDRESS));
                    shipper.setSuburb(company.getString(Keys.SUBURB));
                    shipper.setState(company.getString(Keys.STATE_NAME));
                    shipper.setCountry(company.getString(Keys.COUNTRY_NAME));
                }
                appendData(shipper);
            } else {
                AlertManager.messageDialog(getActivity(), "Alert!", "Error");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

    }

    private void getShipperInfo() {
        String url = Config.SERVER_URL + Config.USER + Config.PROFILE_VIEW + shipperID;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }
}
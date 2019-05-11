package com.grabid.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;

/**
 * Created by vinod on 10/14/2016.
 */
public class Settings extends Fragment implements View.OnClickListener {
    // TextView changePassword;
    ImageView gps;
    TextView mChangePermissions;
    RelativeLayout mPermissionLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.title.setText("Settings");
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        View view = inflater.inflate(R.layout.settings, null);
        init(view);

        //changePassword = (TextView) view.findViewById(R.id.change_password);
        // changePassword.setOnClickListener(this);
        return view;
    }

    public void init(View view) {
        gps = (ImageView) view.findViewById(R.id.gps_switch);
        mChangePermissions = (TextView) view.findViewById(R.id.permissions_switch);
        mPermissionLayout = (RelativeLayout) view.findViewById(R.id.permissionlayout);
        if (checkOS())
            mPermissionLayout.setVisibility(View.VISIBLE);
        gps.setOnClickListener(this);
        mChangePermissions.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        checkAndSetTags();
    }

    private void checkAndSetTags() {
        if (isGPSEnagle()) {
            gps.setTag("1");
            gps.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.on_btn));
        } else {
            gps.setTag("0");
            gps.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.off_btn));
        }
    }

    private boolean isGPSEnagle() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gps_switch:
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivity(intent);
                break;
            case R.id.permissions_switch:
                Intent intentP = new Intent();
                intentP.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intentP.setData(uri);
                //  intentP.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //intentP.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intentP, 1);
                break;
        }

    }

    public boolean checkOS() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return true;
        else
            return false;
    }
}
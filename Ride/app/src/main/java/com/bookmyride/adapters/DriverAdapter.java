package com.bookmyride.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.api.OnLoadMoreListener;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.fragments.RideSummary;
import com.bookmyride.models.Drivers;
import com.bookmyride.util.ImageLoader;
import com.bookmyride.views.AlertDialog;
import com.bookmyride.views.RideDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vinod on 2017-01-07.
 */
public class DriverAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    ArrayList<Drivers> data;
    OnLoadMoreListener loadingData;
    ImageLoader imgLoader;
    SessionHandler session;

    public DriverAdapter(Context ctx, ArrayList<Drivers> data, OnLoadMoreListener loadMore) {
        this.ctx = ctx;
        this.data = data;
        this.loadingData = loadMore;
        this.session = new SessionHandler(ctx);
        this.inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imgLoader = new ImageLoader(ctx);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Drivers getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        //New design
        View view;
        ViewHolder mHolder;
        if (convertView == null) {
            view = inflater.inflate(R.layout.driver_info, null);
            mHolder = new ViewHolder();
            mHolder.icon = (ImageView) view.findViewById(R.id.icon);
            mHolder.name = (TextView) view.findViewById(R.id.name);
            mHolder.address = (TextView) view.findViewById(R.id.address);
            mHolder.addRemove = (TextView) view.findViewById(R.id.add_remove);
            mHolder.rating = (RatingBar) view.findViewById(R.id.rating);
            view.setTag(mHolder);
        } else {
            view = convertView;
            mHolder = (ViewHolder) view.getTag();
        }
        final Drivers driver = getItem(i);

        mHolder.name.setText(driver.getFullName());
        if (!driver.getImgUrl().equals("") && !driver.getImgUrl().equals("null"))
            imgLoader.DisplayImage(driver.getImgUrl(), mHolder.icon);
        mHolder.rating.setRating((float) driver.getRating());
        mHolder.address.setText(driver.getAddress());

        mHolder.addRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog mDialog = new AlertDialog((Activity) ctx, false);
                mDialog.setDialogTitle("Alert!");
                mDialog.setDialogMessage("Do you want to make unfavourite this driver?");
                mDialog.setNegativeButton("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        if (Internet.hasInternet(ctx)) {
                            HashMap<String, String> params = new HashMap<>();
                            //params.put("fav_type", session.getUserType().equals("4")?"1":"0");
                            params.put("type", "0");
                            params.put("id", driver.getId());
                            APIHandler apiCall = new APIHandler(ctx, HTTPMethods.PUT, new AsyncTaskCompleteListener() {
                                @Override
                                public void onTaskComplete(String result) {
                                    try {
                                        JSONObject outJson = new JSONObject(result);
                                        if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                                            data.remove(i);
                                            notifyDataSetChanged();
                                            Alert("Success!", outJson.getString(Key.MESSAGE));
                                        } else {
                                            Alert("Alert!", outJson.getString(Key.MESSAGE));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, params);
                            apiCall.execute(Config.REMOVE_FAVOURITE, new SessionHandler(ctx).getToken());
                        } else
                            Alert("Alert!", ctx.getResources().getString(R.string.no_internet));
                    }
                });
                mDialog.setPositiveButton("No", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
                mDialog.show();
            }
        });

        if (data.size() > 15) {
            if (i == data.size() - 1 && RideSummary.hasMore) {
                //progressBar.setVisibility(View.VISIBLE);
                loadingData.onLoadMore();
            } //else progressBar.setVisibility(View.GONE);
        }

        return view;
    }

    private void Alert(final String title, String message) {
        final RideDialog mDialog = new RideDialog((Activity) ctx, false, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(ctx.getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (title.equals("Success!")) {

                }
            }
        });
        mDialog.show();
    }

    public class ViewHolder {
        private TextView name, address, addRemove;
        private ImageView icon;
        private RatingBar rating;
    }
}

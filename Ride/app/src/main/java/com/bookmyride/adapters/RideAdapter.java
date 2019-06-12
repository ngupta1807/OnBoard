package com.bookmyride.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.api.OnLoadMoreListener;
import com.bookmyride.fragments.RideSummary;
import com.bookmyride.models.Ride;

import java.util.ArrayList;

/**
 * Created by vinod on 2017-01-07.
 */
public class RideAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    ArrayList<Ride> data;
    OnLoadMoreListener loadingData;

    public RideAdapter(Context ctx, ArrayList<Ride> data, OnLoadMoreListener loadMore) {
        this.ctx = ctx;
        this.data = data;
        this.loadingData = loadMore;
        this.inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Ride getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = inflater.inflate(R.layout.ride_info, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) view.findViewById(R.id.icon);
            holder.ride_id = (TextView) view.findViewById(R.id.ride_id);
            holder.puAddress = (TextView) view.findViewById(R.id.pu_address);
            holder.doAddress = (TextView) view.findViewById(R.id.do_address);
            holder.puDay = (TextView) view.findViewById(R.id.pu_date);
            holder.puTime = (TextView) view.findViewById(R.id.pu_time);
            holder.rideStatus = (TextView) view.findViewById(R.id.status);
            holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        Ride ride = getItem(i);

        if (ride.getDriverCategory().equals("1")) {
            holder.icon.setImageResource(R.drawable.taxi_rides);
        } else if (ride.getDriverCategory().equals("2")) {
            holder.icon.setImageResource(R.drawable.economy_rides);
        } else if (ride.getDriverCategory().equals("3")) {
            holder.icon.setImageResource(R.drawable.premium_rides);
        } else if (ride.getDriverCategory().equals("4")) {
            holder.icon.setImageResource(R.drawable.motor_bike_rides);
        }

        holder.puAddress.setText(ride.getPickup());
        if (ride.getDropoff().equals(""))
            holder.doAddress.setText("Not Selected");
        else
            holder.doAddress.setText(ride.getDropoff());

        String puDateTime = ride.getPuDate();
        if (!puDateTime.equals("null") && !puDateTime.equals("")) {
            String day = puDateTime.substring(0, puDateTime.indexOf(" "));
            String time = puDateTime.substring(puDateTime.indexOf(" ") + 1, puDateTime.length());
            holder.puDay.setText(day);
            holder.puTime.setText(time);
        }
        holder.rideStatus.setText(ride.getStatus());
        holder.ride_id.setText("RIDE_ID: " + ride.getId());

        String status = ride.getStatusId();
        switch (status) {
            case "0":
                holder.rideStatus.setBackgroundColor(Color.parseColor("#f6b803"));
                break;
            case "1":
                holder.rideStatus.setBackgroundColor(Color.parseColor("#79ad2e"));
                break;
            case "3":
                holder.rideStatus.setBackgroundColor(Color.parseColor("#0066b0"));
                break;
            case "4":
                holder.rideStatus.setBackgroundColor(Color.parseColor("#e46c0b"));
                break;
            case "5":
                holder.rideStatus.setBackgroundColor(Color.parseColor("#990066"));
                break;
            case "6":
                holder.rideStatus.setBackgroundColor(Color.parseColor("#000066"));
                break;
            case "7":
                holder.rideStatus.setBackgroundColor(Color.parseColor("#d52c15"));
                break;
            case "10":
                holder.rideStatus.setBackgroundColor(Color.parseColor("#006666"));
                break;
        }

        if (data.size() > 15) {
            if (i == data.size() - 1 && RideSummary.hasMore) {
                holder.progressBar.setVisibility(View.VISIBLE);
                loadingData.onLoadMore();
            } else holder.progressBar.setVisibility(View.GONE);
        }
        return view;
    }

    public class ViewHolder {
        private ImageView icon;
        private TextView ride_id, puAddress, doAddress, puDay, puTime, rideStatus;
        private ProgressBar progressBar;
    }
}

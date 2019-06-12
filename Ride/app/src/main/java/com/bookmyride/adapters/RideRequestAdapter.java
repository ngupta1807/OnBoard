package com.bookmyride.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.models.Ride;

import java.util.ArrayList;

/**
 * Created by vinod on 1/19/2017.
 */
public class RideRequestAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    ArrayList<Ride> data;
    public RideRequestAdapter(Context ctx, ArrayList<Ride> data){
        this.ctx = ctx;
        this.data = data;
        this.inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rideView = inflater.inflate(R.layout.ride_request, null);
        TextView title = (TextView) rideView.findViewById(R.id.title);
        TextView detail = (TextView) rideView.findViewById(R.id.detail);
        Ride ride = data.get(i);
        title.setText(ride.getPickup());
        detail.setText(ride.getPuDate());
        return rideView;
    }
}

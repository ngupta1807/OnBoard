package com.bookmyride.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.api.OnLoadMoreListener;
import com.bookmyride.fragments.Notifications;
import com.bookmyride.models.ReferralAmount;

import java.util.ArrayList;

public class ReferralAdapter extends BaseAdapter {
    private ArrayList<ReferralAmount> data;
    private LayoutInflater mInflater;
    private Context context;
    OnLoadMoreListener loadingData;

    public ReferralAdapter(Context c, ArrayList<ReferralAmount> d, OnLoadMoreListener loadingData) {
        context = c;
        mInflater = LayoutInflater.from(context);
        data = d;
        this.loadingData = loadingData;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    public class ViewHolder {
        private TextView date, time, amount, type, bookingID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.adapter_referral_copy, parent, false);
            holder = new ViewHolder();
            holder.date = (TextView) view.findViewById(R.id.date);
            holder.time = (TextView) view.findViewById(R.id.time);
            holder.type = (TextView) view.findViewById(R.id.type);
            holder.amount = (TextView) view.findViewById(R.id.amount);
            holder.bookingID = (TextView) view.findViewById(R.id.booking_id);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        String dateTime = data.get(position).getStatus();
        String date = dateTime.substring(0, dateTime.indexOf(" "));
        String time = dateTime.substring(dateTime.indexOf(" ") + 1, dateTime.length());
        holder.date.setText(date);
        holder.time.setText(time);
        holder.type.setText(data.get(position).getType());
        holder.amount.setText(data.get(position).getReferralAmount());
        holder.bookingID.setText(data.get(position).getBookingid());
        if (data.get(position).getType().equalsIgnoreCase("Loyalty")) {
            holder.type.setBackgroundColor(Color.parseColor("#f9d214"));
        } else {
            holder.type.setBackgroundColor(Color.parseColor("#d52c15"));
        }
        if (data.size() > 15) {
            if (position == data.size() - 1 && Notifications.hasMore) {
                loadingData.onLoadMore();
            }
        }
        return view;
    }
}




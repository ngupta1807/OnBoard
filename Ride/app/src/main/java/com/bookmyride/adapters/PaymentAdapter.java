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
import com.bookmyride.fragments.PayMe;
import com.bookmyride.models.Payment;

import java.util.ArrayList;

public class PaymentAdapter extends BaseAdapter {
    private ArrayList<Payment> data;
    private LayoutInflater mInflater;
    private Context context;
    OnLoadMoreListener loadMore;

    public PaymentAdapter(Context ctx, ArrayList<Payment> d, OnLoadMoreListener loadMore) {
        context = ctx;
        mInflater = LayoutInflater.from(context);
        data = d;
        this.loadMore = loadMore;
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
        private TextView date, time, amount, bookingID, type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_referral, parent, false);
            holder = new ViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.type = (TextView) convertView.findViewById(R.id.type);
            holder.bookingID = (TextView) convertView.findViewById(R.id.booking_id);
            holder.amount = (TextView) convertView.findViewById(R.id.amount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Payment payment = data.get(position);
        if (!payment.getDate().equals("null") && payment.getDate().contains(" ")) {
            String date = payment.getDate();
            String day = date.substring(0, date.indexOf(" "));
            String time = date.substring(date.indexOf(" "), date.length());
            holder.date.setText(day);
            holder.time.setText(time);
        }

        holder.bookingID.setText(payment.getBookingID());
        //holder.type.setText(payment.getType());
        //holder.amount.setText(payment.getAmount());
        if (data.get(position).getType().equalsIgnoreCase("Debit")) {
            holder.type.setBackgroundColor(Color.parseColor("#d52c15"));
            holder.amount.setText("-" + payment.getAmount());
            holder.type.setText(" " + payment.getType().toUpperCase());
        } else {
            //holder.type.setBackgroundColor(Color.parseColor("#78ad2c"));
            holder.amount.setText(payment.getAmount());
            holder.type.setText(payment.getType());
            if (payment.getPaidStatus().equals("2")) {
                holder.type.setText("ON HOLD");
                holder.type.setBackgroundColor(Color.parseColor("#f9d214"));
            } else if (payment.getType().equalsIgnoreCase("wallet")) {
                holder.type.setBackgroundColor(Color.parseColor("#ff3737"));
            } else if (payment.getType().equalsIgnoreCase("westpac") ||
                    payment.getType().equalsIgnoreCase("pinpay") ||
                    payment.getType().equalsIgnoreCase("SAVED CARD")) {
                holder.type.setBackgroundColor(Color.parseColor("#980642"));
            } else if (payment.getType().equalsIgnoreCase("TERMINAL")) {
                holder.type.setBackgroundColor(Color.parseColor("#0a4d6d"));
            } else {
                holder.type.setBackgroundColor(Color.parseColor("#ff3737"));
            }
        }
        if (data.size() > 15) {
            if (position == data.size() - 1 && PayMe.hasMore) {
                loadMore.onLoadMore();
            }
        }
        return convertView;
    }
}




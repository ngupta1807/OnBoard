package com.bookmyride.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.activities.WalletHistory;
import com.bookmyride.api.OnLoadMoreListener;
import com.bookmyride.fragments.ChargeMe;
import com.bookmyride.models.Payment;

import java.util.ArrayList;


public class TransactionAdapter extends BaseAdapter {
    private ArrayList<Payment> data;
    private LayoutInflater mInflater;
    private Context context;
    int type;
    OnLoadMoreListener loadingData;

    public TransactionAdapter(Context c, ArrayList<Payment> d, int type, OnLoadMoreListener loadingData) {
        context = c;
        mInflater = LayoutInflater.from(context);
        data = d;
        this.type = type;
        this.loadingData = loadingData;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Payment getItem(int position) {
        return data.get(position);
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
        private TextView txtId, date, time, amount, type, bookingID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.adapter_transaction, parent, false);
            holder = new ViewHolder();
            holder.date = (TextView) view.findViewById(R.id.date);
            holder.time = (TextView) view.findViewById(R.id.time);
            holder.type = (TextView) view.findViewById(R.id.type);
            holder.amount = (TextView) view.findViewById(R.id.amount);
            holder.bookingID = (TextView) view.findViewById(R.id.booking_id);
            holder.txtId = (TextView) view.findViewById(R.id.txt_id);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        Payment payment = getItem(position);
        String dateTime = payment.getDate();
        String date = dateTime.substring(0, dateTime.indexOf(" "));
        String time = dateTime.substring(dateTime.indexOf(" ") + 1, dateTime.length());
        holder.date.setText(date);
        holder.time.setText(time);
        holder.type.setText(payment.getGateway());
        holder.amount.setText(payment.getAmount());
        holder.bookingID.setText(payment.getBookingID());
        if (type == 2)
            holder.txtId.setText("Invoice ID:");
        //holder.type.setTextColor(Color.parseColor("#000000"));
        if (payment.getGateway().equalsIgnoreCase("wallet")) {
            holder.type.setBackgroundColor(Color.parseColor("#ff3737"));
        } else if (payment.getGateway().equalsIgnoreCase("westpac") ||
                payment.getGateway().equalsIgnoreCase("pinpay")||
                payment.getGateway().equalsIgnoreCase("SAVED CARD")) {
            holder.type.setBackgroundColor(Color.parseColor("#980642"));
        }  else if (payment.getGateway().equalsIgnoreCase("TERMINAL")) {
            holder.type.setBackgroundColor(Color.parseColor("#0a4d6d"));
        } else {
            holder.type.setBackgroundColor(Color.parseColor("#ff3737"));
        }
        if (data.size() > 15) {
            if (type == 2) {
                if (position == data.size() - 1 && WalletHistory.hasMore) {
                    loadingData.onLoadMore();
                }
            } else {
                if (position == data.size() - 1 && ChargeMe.hasMore) {
                    loadingData.onLoadMore();
                }
            }
        }
        return view;
    }
}




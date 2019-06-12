package com.bookmyride.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.models.PaymentHistory;

import java.util.ArrayList;


/**
 * Created by vinod on 10/27/2016.
 */
public class HistoryAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    ArrayList<PaymentHistory> historyData;
    String type = "";

    public HistoryAdapter(Context ctx, ArrayList<PaymentHistory> notificationData, String type) {
        this.ctx = ctx;
        this.historyData = notificationData;
        this.type = type;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return historyData.size();
    }

    @Override
    public PaymentHistory getItem(int i) {
        return historyData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.adapter_notification, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            convertView.setTag(holder);
        }
        PaymentHistory data = getItem(i);
        holder.title.setText("Delivery: " + data.getDeliveryTitle());
        if (type.equals("bank")) {
            if (data.getPayable() == 0)
                holder.message.setText("Amount payable: $" + data.getAmount());
            else
                holder.message.setText("Amount paid: $" + data.getAmount());
        } else
            holder.message.setText("Amount charged: $" + data.getAmount());
        return convertView;
    }

    public class ViewHolder {
        private TextView title, message;
    }
}

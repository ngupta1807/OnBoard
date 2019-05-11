package com.grabid.adapters;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.models.PaymentHistory;

import java.util.ArrayList;

import static com.grabid.R.id.amounttype;

/**
 * Created by vinod on 10/27/2016.
 */
public class EarningAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    ArrayList<PaymentHistory> historyData;
    String type = "";

    public EarningAdapter(Context ctx, ArrayList<PaymentHistory> notificationData, String type) {
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
    public Object getItem(int i) {
        return historyData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View mView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        PaymentHistory data = (PaymentHistory) getItem(i);
        if (mView == null) {
            mView = inflater.inflate(R.layout.adapter_earning, null);
            viewHolder = new ViewHolder();
            viewHolder.date = (TextView) mView.findViewById(R.id.date);
            viewHolder.amount = (TextView) mView.findViewById(R.id.amount);
            viewHolder.type = (TextView) mView.findViewById(R.id.type);
            viewHolder.bookingid = (TextView) mView.findViewById(R.id.bookingidval);
            viewHolder.amounttype = (TextView) mView.findViewById(amounttype);
            viewHolder.relative = (RelativeLayout) mView.findViewById(R.id.shipmentrelative);
            viewHolder.shipmentId = (TextView) mView.findViewById(R.id.shipment_id);
            viewHolder.vieww = mView.findViewById(R.id.view);
            mView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) mView.getTag();
        }

        try {
            viewHolder.amount.setText("$ " + data.getAmount());

            viewHolder.bookingid.setText(data.getDeliveryId());
            if (data.getType().contains("Referral")) {
                viewHolder.amounttype.setText("Referral Dollars Earned");
                viewHolder.type.setBackgroundColor(ctx.getResources().getColor(R.color.darkblue));
                viewHolder.type.setText("Referral");
            } else {
                viewHolder.amounttype.setText("Loyalty Dollars");
                viewHolder.type.setBackgroundColor(ctx.getResources().getColor(R.color.seagreen));
                viewHolder.type.setText("Loyalty");
            }
            try {
                SpannableString text = new SpannableString(data.getDate());
                text.setSpan(new ForegroundColorSpan(ctx.getResources().getColor(R.color.litegray)), 0, 10, 0);
                viewHolder.date.setText(text, TextView.BufferType.SPANNABLE);
            } catch (Exception e) {
                e.toString();
            }
            try {
                if (data.ispaid()) {
                    viewHolder.type.setText(data.getType());
                    viewHolder.amounttype.setText("Amount");
                    viewHolder.relative.setVisibility(View.VISIBLE);
                    viewHolder.vieww.setVisibility(View.VISIBLE);
                    if (data.getType() != null && data.getType().contentEquals("Cashout")) {
                        viewHolder.shipmentId.setText("Pay Via");
                        viewHolder.bookingid.setText(data.getType());
                    }
                } else {
                    viewHolder.relative.setVisibility(View.VISIBLE);
                    viewHolder.vieww.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.toString();
            }

        } catch (Exception e) {
            e.toString();
        }
        return mView;
    }

    static class ViewHolder {
        TextView date;
        TextView amount;
        TextView type;
        TextView bookingid;
        TextView amounttype;
        RelativeLayout relative;
        View vieww;
        TextView shipmentId;
    }
}

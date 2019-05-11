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
import com.grabid.models.CollectJob;

import java.util.ArrayList;

import static com.grabid.R.id.messageval;

/**
 * Created by vinod on 10/27/2016.
 */
public class CollectAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    ArrayList<CollectJob> collectData = new ArrayList<CollectJob>();
    String type = "";

    public CollectAdapter(Context ctx, ArrayList<CollectJob> notificationData) {
        this.ctx = ctx;
        this.collectData = notificationData;
        this.type = type;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return collectData.size();
    }

    @Override
    public Object getItem(int i) {
        return collectData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View mView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        CollectJob data = (CollectJob) getItem(i);
        if (mView == null) {
            mView = inflater.inflate(R.layout.collectdatadapter, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) mView.findViewById(R.id.title);
            viewHolder.message = (TextView) mView.findViewById(R.id.message);
            viewHolder.messageval = (TextView) mView.findViewById(messageval);
            viewHolder.dateval = (TextView) mView.findViewById(R.id.datevalue);
            viewHolder.date = (TextView) mView.findViewById(R.id.date);
            viewHolder.paymentvia = (TextView) mView.findViewById(R.id.paymentvia);
            viewHolder.paymentviarlt = mView.findViewById(R.id.rltpaymentvia);
            viewHolder.delstatus = (RelativeLayout) mView.findViewById(R.id.delstatus);
            viewHolder.delstatusval = (TextView) mView.findViewById(R.id.delstatusval);
            viewHolder.paymentviatitle = (TextView) mView.findViewById(R.id.paymentviatitle);
            viewHolder.amountchargetodriverval = (TextView) mView.findViewById(R.id.amountchargetodriverval);
            viewHolder.amtcollected = mView.findViewById(R.id.amtcollected);
            mView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) mView.getTag();
        }
        try {
            SpannableString text = new SpannableString(data.getCreatedAt());
            text.setSpan(new ForegroundColorSpan(ctx.getResources().getColor(R.color.litegray)), 0, 10, 0);
            viewHolder.dateval.setText(text, TextView.BufferType.SPANNABLE);
        } catch (Exception e) {
            e.toString();
        }
        TextView shipmentvalue = (TextView) mView.findViewById(R.id.shipmentvalue);
        viewHolder.title.setText(data.getItemdeliverytitle());
        shipmentvalue.setText(data.getDeliveryId());
        if (data.getPaymenttype() != null && data.getPaymenttype().contentEquals("1")) {
            viewHolder.messageval.setText("$" + data.getAmount());
            viewHolder.amtcollected.setVisibility(View.VISIBLE);
            viewHolder.paymentviarlt.setVisibility(View.GONE);
        } else{
            viewHolder.amountchargetodriverval.setText("$" + data.getAmount());
            viewHolder.paymentviarlt.setVisibility(View.VISIBLE); // colcted
            viewHolder.amtcollected.setVisibility(View.VISIBLE);
            viewHolder.messageval.setText("$" + data.getAmountcollected());
        }

        if (data.getDeliveryStatus() != null && data.getDeliveryStatus().equals("1"))
            viewHolder.delstatusval.setText("Assigned");
        else if (data.getDeliveryStatus() != null && data.getDeliveryStatus().equals("2"))
            viewHolder.delstatusval.setText("In-Transit");
        else if (data.getDeliveryStatus() != null && data.getDeliveryStatus().equals("3"))
            viewHolder.delstatusval.setText("Completed");
        else if (data.getDeliveryStatus() != null && data.getDeliveryStatus().equals("4"))
            viewHolder.delstatusval.setText("Cancelled");
        else
            viewHolder.delstatus.setVisibility(View.GONE);

        viewHolder.paymentvia.setText(data.getPaymodeid());

        return mView;
    }

    static class ViewHolder {
        TextView title;
        TextView message;
        TextView messageval;
        TextView dateval;
        TextView date;
        TextView paymentvia;
        RelativeLayout paymentviarlt;
        RelativeLayout delstatus;
        TextView delstatusval;
        TextView amountchargetodriverval;
        TextView paymentviatitle;
        RelativeLayout amtcollected;
    }
}

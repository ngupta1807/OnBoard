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

import static com.grabid.R.id.messageval;

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
            mView = inflater.inflate(R.layout.bank_adapter_notification, null);
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
            mView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) mView.getTag();
        }
        try {
            SpannableString text = new SpannableString(data.getDate());
            text.setSpan(new ForegroundColorSpan(ctx.getResources().getColor(R.color.litegray)), 0, 10, 0);
            viewHolder.dateval.setText(text, TextView.BufferType.SPANNABLE);
        } catch (Exception e) {
            e.toString();
        }
        TextView shipmentvalue = (TextView) mView.findViewById(R.id.shipmentvalue);
        viewHolder.title.setText(data.getDeliveryTitle());
        shipmentvalue.setText(data.getDeliveryId());
        if (data.getPaymentMethodName() != null) {
            viewHolder.paymentviarlt.setVisibility(View.VISIBLE);
            viewHolder.paymentvia.setText(data.getPaymentMethodName());
        } else
            viewHolder.paymentviarlt.setVisibility(View.GONE);
        if (type.equals("penality")) {
            if (data.getUserType() != null && data.getUserType().equals("1"))
                viewHolder.delstatusval.setText("Canceled");
            else if (data.getUserType() != null && data.getUserType().equals("2"))
                viewHolder.delstatusval.setText("Withdrawn");
            else
                viewHolder.delstatus.setVisibility(View.GONE);
        }
        else if (type.equals("bank")) {
            viewHolder.delstatus.setVisibility(View.GONE);
        } else{
            if (data.getDelStatus() != null && data.getDelStatus().equals("1"))
                viewHolder.delstatusval.setText("Assigned");
            else if (data.getDelStatus() != null && data.getDelStatus().equals("2"))
                viewHolder.delstatusval.setText("In-Transit");
            else if (data.getDelStatus() != null && data.getDelStatus().equals("3"))
                viewHolder.delstatusval.setText("Completed");
            else if (data.getDelStatus() != null && data.getDelStatus().equals("4"))
                viewHolder.delstatusval.setText("Cancelled");
            else
                viewHolder.delstatus.setVisibility(View.GONE);
        }

        if (type.equals("bank")) {
            if (data.getPayable() == 0) {
                viewHolder.message.setText("Amount Payable");
                viewHolder.messageval.setText("$" + data.getPayToDriverAmount());
            } else {
                viewHolder.message.setText("Amount Paid");
                viewHolder.messageval.setText("$" + data.getPayToDriverAmount());
            }
        } else {
            viewHolder.paymentviatitle.setText("Payment via");
            if (data.getPayable() == 0) {
                viewHolder.message.setText("Amount");
                viewHolder.date.setText("Charged on");
                viewHolder.messageval.setText("$" + data.getAmount());
            } else {
                viewHolder.message.setText("Amount Charged");
                viewHolder.date.setText("Charged on");
                if (data.getChargedAmount() != null && !data.getChargedAmount().contentEquals(""))
                    viewHolder.messageval.setText("$" + data.getChargedAmount());
                else
                    viewHolder.messageval.setText("$" + data.getAmount());
            }
        }

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
        TextView paymentviatitle;
    }
}

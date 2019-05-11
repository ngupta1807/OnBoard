package com.grabid.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.common.SessionManager;
import com.grabid.models.CompanyInfo;
import com.grabid.models.Delivery;
import com.grabid.models.UserInfo;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import static com.grabid.R.id.bid_color;
import static com.grabid.R.id.desc;
import static com.grabid.R.id.lay_top;
import static com.grabid.R.id.status;

/**
 * Created by vinod on 10/26/2016.
 */
public class ListAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    ArrayList<Delivery> data;
    int type;
    UserInfo userInfo;
    SessionManager session;
    CompanyInfo company;
    Target target;
    Picasso mPicasso;


    public ListAdapter(Context ctx, ArrayList<Delivery> deliveryData, int type) {
        this.ctx = ctx;
        this.data = deliveryData;
        this.type = type;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        session = new SessionManager(this.ctx);
        userInfo = session.getUserDetails();
        company = session.getCompanyInfo();
        mPicasso = Picasso.with(ctx);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View mView, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        Delivery deliveryData = (Delivery) getItem(i);
        if (mView == null) {
            mView = inflater.inflate(R.layout.adapter_delivery_new, null);
            viewHolder = new ViewHolder();
            viewHolder.itemImg = (ImageView) mView.findViewById(R.id.profile_pic);
            viewHolder.name = (TextView) mView.findViewById(R.id.name);
            viewHolder.desc = (TextView) mView.findViewById(desc);
            viewHolder.shipment_id = (TextView) mView.findViewById(R.id.shipment_id);
            viewHolder.type = (TextView) mView.findViewById(R.id.type);
            //  viewHolder.dtype = (TextView) mView.findViewById(R.id.d_type);
            viewHolder.bid = (TextView) mView.findViewById(R.id.bid);
            viewHolder.status = (TextView) mView.findViewById(status);
            viewHolder.lay_top = (RelativeLayout) mView.findViewById(lay_top);
            viewHolder.bid_color = (LinearLayout) mView.findViewById(bid_color);
            viewHolder.puDate = (TextView) mView.findViewById(R.id.pu_date_time);
            //   viewHolder.doDate = (TextView) mView.findViewById(R.id.do_date_time);
            viewHolder.bidPrice = (TextView) mView.findViewById(R.id.bid_price);
            mView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) mView.getTag();
        }
        String path = deliveryData.getCompanyLogo();
        if (path != null && !path.contentEquals("") && !path.contentEquals("null"))
            Picasso.with(this.ctx).load(path).into(viewHolder.itemImg);
        String text = deliveryData.getTitle();
        if (text != null)
            viewHolder.name.setText(text);
        viewHolder.shipment_id.setText(deliveryData.getJob_ID());
        viewHolder.desc.setText(deliveryData.getPickupSpecialRestriction());
        viewHolder.desc.setVisibility(View.GONE);
            if (deliveryData.getItemtype().equals("1")) {
            viewHolder.type.setText("Offload Transfer");
            viewHolder.type.setCompoundDrawablesWithIntrinsicBounds(R.drawable.offload, 0, 0, 0);
        } else {
            viewHolder.type.setText("Collect Transfer");
            viewHolder.type.setCompoundDrawablesWithIntrinsicBounds(R.drawable.collect_job, 0, 0, 0);
        }

        LayerDrawable bgDrawable = (LayerDrawable) viewHolder.lay_top.getBackground();
        final GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.gradientDrawble);

        if (deliveryData.getDeliveryStatus().equalsIgnoreCase("0")) {
            if (deliveryData.getUserID().equals(userInfo.getId())) {
                viewHolder.status.setText("AVAILABLE");
                viewHolder.status.setBackgroundColor(ContextCompat.getColor(ctx, R.color.available_color));
                viewHolder.bid_color.setBackgroundColor(ContextCompat.getColor(ctx, R.color.available_color));
                shape.setColor(ContextCompat.getColor(ctx, R.color.text_color_black));
            } else {
                if (!deliveryData.getBidStatus().equals("null")) {
                    viewHolder.status.setText("PENDING");
                    viewHolder.status.setBackgroundColor(ContextCompat.getColor(ctx, R.color.pending_color));
                    viewHolder.bid_color.setBackgroundColor(ContextCompat.getColor(ctx, R.color.pending_color));
                    shape.setColor(ContextCompat.getColor(ctx, R.color.pending_color));
                } else {
                    viewHolder.status.setText("AVAILABLE");
                    viewHolder.status.setBackgroundColor(ContextCompat.getColor(ctx, R.color.available_color));
                    viewHolder.bid_color.setBackgroundColor(ContextCompat.getColor(ctx, R.color.available_color));
                    shape.setColor(ContextCompat.getColor(ctx, R.color.text_color_black));
                }
            }
        }

        if (deliveryData.getDeliveryStatus().equalsIgnoreCase("1")) {
            viewHolder.status.setText("UPCOMING");
            viewHolder.status.setBackgroundColor(ContextCompat.getColor(ctx, R.color.upcoming_color));
            viewHolder.bid_color.setBackgroundColor(ContextCompat.getColor(ctx, R.color.upcoming_color));
            shape.setColor(ContextCompat.getColor(ctx, R.color.upcoming_color));
        }
        if (deliveryData.getDeliveryStatus().equalsIgnoreCase("2")) {
            viewHolder.status.setText("IN-TRANSIT");
            viewHolder.status.setBackgroundColor(ContextCompat.getColor(ctx, R.color.intransit_color));
            viewHolder.bid_color.setBackgroundColor(ContextCompat.getColor(ctx, R.color.intransit_color));
            shape.setColor(ContextCompat.getColor(ctx, R.color.intransit_color));
        }
        if (deliveryData.getDeliveryStatus().equalsIgnoreCase("3")) {
            viewHolder.status.setText("COMPLETED");
            viewHolder.status.setBackgroundColor(ContextCompat.getColor(ctx, R.color.completed_color));
            viewHolder.bid_color.setBackgroundColor(ContextCompat.getColor(ctx, R.color.completed_color));
            shape.setColor(ContextCompat.getColor(ctx, R.color.completed_color));
        }
        if (deliveryData.getDeliveryStatus().equalsIgnoreCase("4")) {
            viewHolder.status.setText("CANCELLED");
            viewHolder.status.setBackgroundColor(ContextCompat.getColor(ctx, R.color.cancelled_color));
            viewHolder.bid_color.setBackgroundColor(ContextCompat.getColor(ctx, R.color.cancelled_color));
            shape.setColor(ContextCompat.getColor(ctx, R.color.cancelled_color));
        }

        viewHolder.puDate.setText(deliveryData.getPickUpDate());
        //  viewHolder.doDate.setText(deliveryData.getDropOffDate());
      /*  if (!deliveryData.getMaxOpeningBid().equals("null")) {
            viewHolder.bidPrice.setText("OPENING BID");
            viewHolder.bid.setText("$" + deliveryData.getMaxOpeningBid());
        } else {
            viewHolder.bidPrice.setText("FIXED PRICE");
            viewHolder.bid.setText("$" + deliveryData.getFixedOffer());
        }*/
        if (deliveryData.getAllocateDriverID() != null && deliveryData.getAllocateDriverID().equalsIgnoreCase(session.getUserDetails().getId())) {
            viewHolder.bidPrice.setText("ALLOCATED TRANSFER");
            viewHolder.bid.setText("");
        } else if (deliveryData.getAuctionBid().equals("2")) {
            viewHolder.bidPrice.setText("OPENING BID");
            viewHolder.bid.setText("$" + deliveryData.getMaxOpeningBid());
        } else {
            viewHolder.bidPrice.setText("FIXED PRICE");
            viewHolder.bid.setText("$" + deliveryData.getFixedOffer());
        }

        return mView;
    }

    static class ViewHolder {
        ImageView itemImg;
        TextView name;
        TextView desc;
        TextView shipment_id;
        TextView type;
        // TextView dtype;
        TextView bid;
        TextView status;
        RelativeLayout lay_top;
        LinearLayout bid_color;
        TextView puDate;
        // TextView doDate;
        TextView bidPrice;
    }
}

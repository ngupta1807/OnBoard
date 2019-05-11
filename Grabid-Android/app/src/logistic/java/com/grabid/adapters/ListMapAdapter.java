package com.grabid.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.common.SessionManager;
import com.grabid.models.HomeData;
import com.grabid.models.UserInfo;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import static com.grabid.R.id.desc;
import static com.grabid.R.id.lay_top;
import static com.grabid.R.id.status;

/**
 * Created by vinod on 10/26/2016.
 */
public class ListMapAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    ArrayList<HomeData> data;
    int type;
    UserInfo userInfo;
    SessionManager session;
    Target target;

    public ListMapAdapter(Context ctx, ArrayList<HomeData> deliveryData) {
        this.ctx = ctx;
        this.data = deliveryData;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        session = new SessionManager(this.ctx);
        userInfo = session.getUserDetails();
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
        HomeData homeData = (HomeData) getItem(i);
        if (mView == null) {
            mView = inflater.inflate(R.layout.adapter_delivery_new, null);
            viewHolder = new ViewHolder();
            viewHolder.itemImg = (ImageView) mView.findViewById(R.id.profile_pic);
            viewHolder.lay_top = (RelativeLayout) mView.findViewById(lay_top);
            viewHolder.name = (TextView) mView.findViewById(R.id.name);
            viewHolder.desc = (TextView) mView.findViewById(desc);
            viewHolder.shipment_id = (TextView) mView.findViewById(R.id.shipment_id);
            viewHolder.type = (TextView) mView.findViewById(R.id.type);

            viewHolder.bid = (TextView) mView.findViewById(R.id.bid);
            viewHolder.status = (TextView) mView.findViewById(status);
            viewHolder.puDate = (TextView) mView.findViewById(R.id.pu_date_time);
            viewHolder.doDate = (TextView) mView.findViewById(R.id.do_date_time);
            viewHolder.bidPrice = (TextView) mView.findViewById(R.id.bid_price);
            viewHolder.d_type = (TextView) mView.findViewById(R.id.d_type);
            mView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) mView.getTag();
        }
        // imgLoader.DisplayImage(homeData.getItemPhoto(), viewHolder.itemImg);
        //Picasso.with(this.ctx).load(homeData.getItemPhoto()).into(viewHolder.itemImg);

        if (homeData.getItemPhoto() != null && !homeData.getItemPhoto().equals("") && !homeData.getItemPhoto().contentEquals("null"))
            Picasso.with(this.ctx).load(homeData.getItemPhoto()).into(viewHolder.itemImg);


        viewHolder.d_type.setVisibility(View.GONE);
        String text = homeData.getItemDeliveryTitle();
        if (text != null)
            viewHolder.name.setText(text);
//        viewHolder.shipment_id.setText(Html.fromHtml("<font color='#d2d2d2'>" + homeData.getId() + "</font>"));
        viewHolder.shipment_id.setText(homeData.getJob_ID());
        viewHolder.desc.setText("Loreum ipsum is simply dummy text");

        if (homeData.getLiftequipement().equals("1")) {
            viewHolder.type.setVisibility(View.VISIBLE);
            viewHolder.type.setText("Commercial");
            viewHolder.type.setCompoundDrawablesWithIntrinsicBounds(R.drawable.commerical_icon, 0, 0, 0);
        } else if (homeData.getLiftequipement().equals("2")) {
            viewHolder.type.setVisibility(View.VISIBLE);
            viewHolder.type.setText("Residential");
            viewHolder.type.setCompoundDrawablesWithIntrinsicBounds(R.drawable.residental_icon, 0, 0, 0);
        } else {
            viewHolder.type.setVisibility(View.GONE);
        }
        LayerDrawable bgDrawable = (LayerDrawable) viewHolder.lay_top.getBackground();
        final GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.gradientDrawble);
        if (homeData.getDeliveryStatus().equalsIgnoreCase("0")) {
            viewHolder.status.setText("AVAILABLE");
            viewHolder.status.setBackgroundColor(ContextCompat.getColor(ctx, R.color.available_color));
            shape.setColor(ContextCompat.getColor(ctx, R.color.text_color_black));
        }
        /*if (homeData.getDeliveryStatus().equalsIgnoreCase("0")) {
            if (homeData.getUserID().equals(userInfo.getId())) {
                viewHolder.status.setText("AVAILABLE");
                viewHolder.status.setBackgroundColor(ContextCompat.getColor(ctx, R.color.black));
                shape.setColor(ContextCompat.getColor(ctx, R.color.black));
            } else {
                if (!homeData.getBidStatus().equals("null")) {
                    viewHolder.status.setText("PENDING");
                    viewHolder.status.setBackgroundColor(ContextCompat.getColor(ctx, R.color.pending_color));
                    shape.setColor(ContextCompat.getColor(ctx, R.color.pending_color));
                } else {
                    viewHolder.status.setText("AVAILABLE");
                    viewHolder.status.setBackgroundColor(ContextCompat.getColor(ctx, R.color.black));
                    shape.setColor(ContextCompat.getColor(ctx, R.color.black));
                }
            }
        }
*/
        if (homeData.getDeliveryStatus().equalsIgnoreCase("1")) {
            viewHolder.status.setText("UPCOMING");
            viewHolder.status.setBackgroundColor(Color.YELLOW);
            shape.setColor(Color.YELLOW);
        }
        if (homeData.getDeliveryStatus().equalsIgnoreCase("2")) {
            viewHolder.status.setText("IN TRANSIT");
            viewHolder.status.setBackgroundColor(Color.CYAN);
            shape.setColor(Color.CYAN);
        }
        if (homeData.getDeliveryStatus().equalsIgnoreCase("3")) {
            viewHolder.status.setText("COMPLETED");
            viewHolder.status.setBackgroundColor(Color.GREEN);
            shape.setColor(Color.GREEN);
        }
        if (homeData.getDeliveryStatus().equalsIgnoreCase("4")) {
            viewHolder.status.setText("CANCELLED");
            viewHolder.status.setBackgroundColor(Color.RED);
            shape.setColor(Color.RED);
        }

        viewHolder.puDate.setText(homeData.getPickupDay());
        viewHolder.doDate.setText(homeData.getDropoffDay());
        if (!homeData.getMaximumOpeningBid().equals("null")) {
            viewHolder.bidPrice.setText("OPENING BID");
            viewHolder.bid.setText("$" + homeData.getMaximumOpeningBid());
        } else {
            viewHolder.bidPrice.setText("FIXED PRICE");
            viewHolder.bid.setText("$" + homeData.getFixedOffer());
        }


        return mView;
    }

    static class ViewHolder {
        ImageView itemImg;
        RelativeLayout lay_top;
        TextView name;
        TextView desc;
        TextView shipment_id;
        TextView type;
        TextView bid;
        TextView status;
        TextView puDate;
        TextView doDate;
        TextView bidPrice;
        TextView d_type;
    }
}

package com.grabid.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.models.Notification;

import java.util.ArrayList;

/**
 * Created by vinod on 10/27/2016.
 */
public class NotificationAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    ArrayList<Notification> notificationData;
    Typeface normal, bold;

    public NotificationAdapter(Context ctx, ArrayList<Notification> notificationData) {
        this.ctx = ctx;
        normal = Typeface.createFromAsset(ctx.getAssets(), "fonts/Raleway_Regular.ttf");
        bold = Typeface.createFromAsset(ctx.getAssets(), "fonts/Raleway_Bold.ttf");
        this.notificationData = notificationData;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return notificationData.size();
    }

    @Override
    public Object getItem(int i) {
        return notificationData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View mView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        Notification data = (Notification) getItem(i);
        if (mView == null) {
            mView = inflater.inflate(R.layout.adapter_notification, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) mView.findViewById(R.id.title);
            viewHolder.message = (TextView) mView.findViewById(R.id.message);
            viewHolder.counter = (TextView) mView.findViewById(R.id.notification_counter);
            viewHolder.card_background = (RelativeLayout) mView.findViewById(R.id.card_layout);
            mView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) mView.getTag();
        }

        LayerDrawable bgDrawable = (LayerDrawable) viewHolder.card_background.getBackground();
        if (data.getReadStatus().equals("0")) {
            //title.setTypeface(bold);
            viewHolder.message.setTypeface(bold);
            /*final GradientDrawable shapeall = (GradientDrawable)   bgDrawable.findDrawableByLayerId(R.id.gradientDrawbleall);
            shapeall.setColor(ContextCompat.getColor(ctx, R.color.notify_read));*/

        } else {
           /* final GradientDrawable shapeall = (GradientDrawable)   bgDrawable.findDrawableByLayerId(R.id.gradientDrawbleall);
            shapeall.setColor(ContextCompat.getColor(ctx, R.color.white));*/
            //title.setTypeface(normal);
            viewHolder.message.setTypeface(normal);
        }
        if (i % 2 == 0) {
            //card_background.setBackgroundResource(R.drawable.colored_blue_sidebar);
            viewHolder.counter.setBackgroundResource(R.drawable.triangle_blue_corner);
            try {
                final GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.gradientDrawble);
                shape.setColor(ContextCompat.getColor(ctx, R.color.pending_color));
            } catch (Exception e) {
                e.toString();
            }
        } else if (i % 2 != 0) {
            //card_background.setBackgroundResource(R.drawable.colored_green_sidebar);
            viewHolder.counter.setBackgroundResource(R.drawable.triangle_green_corner);
            final GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.gradientDrawble);
            shape.setColor(ContextCompat.getColor(ctx, R.color.completed_color));
        }
        viewHolder.counter.setText("" + (i + 1));
        if (data.getType() != null && (data.getType().contentEquals("33") || data.getType().contentEquals("34") || data.getType().contentEquals("66")))
            viewHolder.title.setText(data.getTitle());
        else
            viewHolder.title.setText(ctx.getResources().getString(R.string.not_title) + " " + data.getDeliveryTitle());
        viewHolder.message.setText(data.getMessage());
        return mView;
    }

    static class ViewHolder {
        TextView title;
        TextView message;
        TextView counter;
        RelativeLayout card_background;
    }
}
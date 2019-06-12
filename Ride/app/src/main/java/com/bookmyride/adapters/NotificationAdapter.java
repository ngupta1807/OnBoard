package com.bookmyride.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.api.OnLoadMoreListener;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.fragments.Notifications;
import com.bookmyride.models.Notification;

import java.util.ArrayList;

/**
 * Created by vinod on 6/2/2017.
 */
public class NotificationAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context ctx;
    ArrayList<Notification> data;
    OnLoadMoreListener loadingData;
    Typeface normal, bold;
    SessionHandler session;

    public NotificationAdapter(Context ctx, ArrayList<Notification> data, OnLoadMoreListener loadMore) {
        this.ctx = ctx;
        this.data = data;
        this.session = new SessionHandler(ctx);
        normal = Typeface.createFromAsset(ctx.getAssets(), "fonts/Roboto-Regular.ttf");
        bold = Typeface.createFromAsset(ctx.getAssets(), "fonts/Roboto-Bold.ttf");
        this.loadingData = loadMore;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Notification getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.notification, null);
            holder = new ViewHolder();
            //holder.title = (TextView) view.findViewById(R.id.title);
            holder.message = (TextView) view.findViewById(R.id.message);
            view.setTag(holder);
        }
        final Notification notification = getItem(i);

        //title.setText(notification.getTitle());
        if (notification.isRead())
            holder.message.setTypeface(normal);
        else holder.message.setTypeface(bold);

        holder.message.setText(notification.getMessage());

        if (data.size() > 15) {
            if (i == data.size() - 1 && Notifications.hasMore) {
                loadingData.onLoadMore();
            }
        }
        return view;
    }

    private class ViewHolder {
        private TextView message;
    }

}

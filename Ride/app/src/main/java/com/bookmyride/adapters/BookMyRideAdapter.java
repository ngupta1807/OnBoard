package com.bookmyride.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.models.Cars;
import com.bookmyride.util.ImageLoader;

import java.util.ArrayList;


public class BookMyRideAdapter extends BaseAdapter {

    private ArrayList<Cars> data;
    private ImageLoader imageLoader;
    private LayoutInflater mInflater;
    private Activity context;

    public BookMyRideAdapter(Activity c, ArrayList<Cars> d) {
        context = c;
        mInflater = LayoutInflater.from(context);
        data = d;
        imageLoader = new ImageLoader(context);
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
        private ImageView icon;
        private TextView name;
        private LinearLayout layout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.bookmyride_single, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.icon = (ImageView) view.findViewById(R.id.icon);
            holder.layout = (LinearLayout) view.findViewById(R.id.layout);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.name.setText(data.get(position).getName());
        if(data.get(position).isDefault())
            imageLoader.DisplayImage(String.valueOf(data.get(position).getSelectedIcon()), holder.icon);
        else
            imageLoader.DisplayImage(String.valueOf(data.get(position).getIcon()), holder.icon);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        ViewGroup.LayoutParams params = holder.layout.getLayoutParams();
        params.width = (displaymetrics.widthPixels / data.size()) - 10;
        holder.layout.setLayoutParams(params);

        if(data.get(position).getId().equals("1"))
            holder.name.setTextColor(Color.parseColor("#f9d214"));
        else if(data.get(position).getId().equals("2"))
            holder.name.setTextColor(Color.parseColor("#78ad2c"));
        else if(data.get(position).getId().equals("3"))
            holder.name.setTextColor(Color.parseColor("#D42C15"));
        else if(data.get(position).getId().equals("4"))
            holder.name.setTextColor(Color.parseColor("#0066b0"));

        return view;
    }
}


package com.bookmyride.adapters;


import android.content.Context;
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


public class DriverTypeAdapter extends BaseAdapter {

    private ArrayList<Cars> data;
    private ImageLoader imageLoader;
    private LayoutInflater mInflater;
    private Context context;

    public DriverTypeAdapter(Context c, ArrayList<Cars> carsData) {
        context = c;
        mInflater = LayoutInflater.from(context);
        data = carsData;
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
        private ImageView image;
        private TextView name;
        LinearLayout background;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.type_adapter, parent, false);
            holder = new ViewHolder();
            holder.background = (LinearLayout) view.findViewById(R.id.background);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.image = (ImageView) view.findViewById(R.id.car_image);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.name.setText(data.get(position).getName());

        if(data.get(position).isDefault()) {
            //holder.background.setBackgroundColor(Color.parseColor("#d3d3d3"));
            //holder.name.setTextColor(Color.parseColor("#FFFFFF"));
            imageLoader.DisplayImage(String.valueOf(data.get(position).getSelectedIcon()), holder.image);
        } else {
            //holder.background.setBackgroundColor(Color.parseColor("#FFFFFF"));
            //holder.name.setTextColor(Color.parseColor("#000000"));
            imageLoader.DisplayImage(String.valueOf(data.get(position).getIcon()), holder.image);
        }
        return view;
    }
}


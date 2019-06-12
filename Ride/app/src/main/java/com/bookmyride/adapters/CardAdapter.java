package com.bookmyride.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bookmyride.R;
import com.bookmyride.models.Cards;

import java.util.ArrayList;


public class CardAdapter extends BaseAdapter {

    private ArrayList<Cards> data;
    private LayoutInflater mInflater;
    private Context context;

    public CardAdapter(Context c, ArrayList<Cards> d) {
        context = c;
        mInflater = LayoutInflater.from(context);
        data = d;
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
        private TextView name, type;
        private ImageView icon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.adapter_cards, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.type = (TextView) view.findViewById(R.id.type);
            holder.icon = (ImageView) view.findViewById(R.id.icon);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(data.get(position).getName());
        holder.type.setText(data.get(position).getGateway());

        /*if (data.get(position).getSelected_Cat().equalsIgnoreCase(data.get(position).getCat_id())) {
            holder.dot_image.setVisibility(View.VISIBLE);
        } else {
            holder.dot_image.setVisibility(View.INVISIBLE);
        }*/

        return view;
    }
}


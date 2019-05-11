package com.grabid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grabid.R;

/**
 * Created by graycell on 2/8/17.
 */

public class CountryCodeAdapter extends BaseAdapter {
    Context context;
    String countrycode[];
    int[] flags;
    LayoutInflater inflter;


    public CountryCodeAdapter(Context applicationContext, int[] flags, String[] countrycode) {
        this.context = applicationContext;
        this.countrycode = countrycode;
        this.flags = flags;
        inflter = (LayoutInflater.from(applicationContext));

    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.countrycodes, null);
        //   TextView names = (TextView) view.findViewById(R.id.textView);
        TextView code = (TextView) view.findViewById(R.id.countrycode);
        ImageView img = (ImageView) view.findViewById(R.id.imageView);
        LinearLayout countrylinear = (LinearLayout) view.findViewById(R.id.linrearcountrycode);
        img.setImageResource(flags[i]);
        code.setText(countrycode[i]);

        return view;
    }


}
package com.grabid.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.fragments.HelpDetail;
import com.grabid.models.HelpM;

import java.util.ArrayList;

/**
 * Created by graycell on 3/11/17.
 */

public class HelpAdapter extends BaseAdapter {
    Activity ctx;
    LayoutInflater inflater;
    ArrayList<HelpM> categories = new ArrayList<>();

    public HelpAdapter(Activity context, ArrayList<HelpM> categories) {
        this.ctx = context;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.categories = categories;


    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        final HelpM help = categories.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.helpadapter, null);
            viewHolder = new ViewHolder();
            viewHolder.categorytitle = convertView.findViewById(R.id.categorytitle);
            viewHolder.categoryques = convertView.findViewById(R.id.categoryvalue);
            viewHolder.view = convertView.findViewById(R.id.view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (help.isCategory()) {
            viewHolder.categorytitle.setText("" + help.getCategoryType());
            viewHolder.categorytitle.setVisibility(View.VISIBLE);
        } else {
            viewHolder.categorytitle.setVisibility(View.GONE);
        }
        viewHolder.categoryques.setText(help.getCategoryTitle());
        viewHolder.categoryques.setId(position);

        viewHolder.categoryques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String backStateName = this.getClass().getName();
                Fragment fragment = new HelpDetail();
                Bundle bundle = new Bundle();
                bundle.putString("question", help.getCategoryTitle());
                bundle.putString("answer", help.getCategorydescription());
                fragment.setArguments(bundle);
                ctx.getFragmentManager().beginTransaction().add(R.id.container, fragment, backStateName)
                        .addToBackStack(null)
                        //.commit();
                        .commitAllowingStateLoss();


            }
        });

        return convertView;

    }

    static class ViewHolder {
        TextView categorytitle;
        TextView categoryques;
        View view;

    }
}

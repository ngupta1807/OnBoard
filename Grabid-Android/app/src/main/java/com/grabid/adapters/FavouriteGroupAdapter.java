package com.grabid.adapters;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.fragments.FavouriteGroups;
import com.grabid.models.FavouriteGroupModel;

import java.util.ArrayList;

/**
 * Created by graycell on 12/2/18.
 */

public class FavouriteGroupAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<FavouriteGroupModel> feedackData;
    LayoutInflater inflater;
    boolean IsUser;
    Fragment fragment;

    public FavouriteGroupAdapter(Context ctx, ArrayList<FavouriteGroupModel> feedackData, boolean IsUser, Fragment fragment) {
        this.ctx = ctx;
        this.feedackData = feedackData;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.IsUser = IsUser;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return feedackData.size();
    }

    @Override
    public Object getItem(int i) {
        return feedackData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View mView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        final FavouriteGroupModel feedback = (FavouriteGroupModel) getItem(i);
        if (mView == null) {
            mView = inflater.inflate(R.layout.favouritegrouplayout, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) mView.findViewById(R.id.title);
            viewHolder.viewtxt = (TextView) mView.findViewById(R.id.viewtxt);
            viewHolder.rename = (TextView) mView.findViewById(R.id.rename);
            viewHolder.remove = (TextView) mView.findViewById(R.id.remove);
            viewHolder.bottomlinear = (LinearLayout) mView.findViewById(R.id.bottomlinear);
            mView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) mView.getTag();
        }
        viewHolder.title.setText("" + feedback.getName());
        viewHolder.rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FavouriteGroups) fragment).RenameFavouriteDialog(feedback.getId());
            }
        });
        viewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FavouriteGroups) fragment).Remove(feedback.getId());

            }
        });
        viewHolder.viewtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((FavouriteGroups) fragment).viewFavourities(feedback.getId(), feedback.getName());

            }
        });
        if (IsUser)
            viewHolder.bottomlinear.setVisibility(View.GONE);
        else
            viewHolder.bottomlinear.setVisibility(View.VISIBLE);


        return mView;
    }

    static class ViewHolder {
        TextView title;
        TextView rename;
        TextView viewtxt, remove;
        LinearLayout bottomlinear;
    }
}


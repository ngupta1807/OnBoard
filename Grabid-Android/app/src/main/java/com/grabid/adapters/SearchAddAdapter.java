package com.grabid.adapters;

import android.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.fragments.Favourite;
import com.grabid.fragments.SearchAddFavourite;
import com.grabid.models.SearchAddFavModel;

import java.util.ArrayList;

/**
 * Created by graycell on 13/12/17.
 */

public class SearchAddAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<SearchAddFavModel> feedackData;
    LayoutInflater inflater;
    boolean IsSearch;
    Fragment fragment;
    String isHome="";
    public SearchAddAdapter(Context ctx, ArrayList<SearchAddFavModel> feedackData,String isHome, boolean IsSearch, Fragment fragment) {
        this.ctx = ctx;
        this.feedackData = feedackData;
        this.isHome = isHome;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.IsSearch = IsSearch;
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
        final SearchAddFavModel feedback = (SearchAddFavModel) getItem(i);
        if (mView == null) {
            mView = inflater.inflate(R.layout.searchaddlayout, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) mView.findViewById(R.id.title);
            viewHolder.mobile = (TextView) mView.findViewById(R.id.mobile);
            viewHolder.rating = (RatingBar) mView.findViewById(R.id.rating);
            viewHolder.addfav = (TextView) mView.findViewById(R.id.addfav);
            viewHolder.linear_user_group = (LinearLayout) mView.findViewById(R.id.linear_user_group);
            mView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) mView.getTag();
        }
        if (IsSearch)
            viewHolder.addfav.setText("ADD");
        else
            viewHolder.addfav.setText("REMOVE");
        viewHolder.title.setText("" + feedback.getUserName());
        viewHolder.mobile.setText(feedback.getMobile());
        viewHolder.rating.setRating(Float.valueOf(feedback.getDriver_rating()));
        viewHolder.rating.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        viewHolder.addfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsSearch)
                    ((SearchAddFavourite) fragment).Add(feedback.getId());
                else
                    ((Favourite) fragment).Remove(feedback.getId());

            }
        });
        viewHolder.linear_user_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsSearch){
                    //((SearchAddFavourite) fragment).FavouriteGroups(feedback.getId());
                }
                else {
                    if(isHome.equals("no"))
                        Log.v("isHome","isHome"+isHome);
                    else{
                        ((Favourite) fragment).FavouriteGroups(feedback.getId());
                    }
                }

            }
        });

        return mView;
    }

    static class ViewHolder {
        TextView title;
        TextView mobile;
        RatingBar rating;
        TextView addfav;
        LinearLayout linear_user_group;
    }
}

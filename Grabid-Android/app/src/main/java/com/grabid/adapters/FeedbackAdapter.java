package com.grabid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.models.Feedback;

import java.util.ArrayList;

/**
 * Created by vinod on 11/7/2016.
 */
public class FeedbackAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<Feedback> feedackData;
    LayoutInflater inflater;

    public FeedbackAdapter(Context ctx, ArrayList<Feedback> feedackData) {
        this.ctx = ctx;
        this.feedackData = feedackData;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        Feedback feedback = (Feedback) getItem(i);
        if (mView == null) {
            mView = inflater.inflate(R.layout.adapter_feedback, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) mView.findViewById(R.id.title);
            viewHolder.feed_back = (TextView) mView.findViewById(R.id.feedback);
            viewHolder.rating = (RatingBar) mView.findViewById(R.id.rating);
            mView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) mView.getTag();
        }
        viewHolder.title.setText("" + feedback.getDeliveryTitle());
        viewHolder.feed_back.setText(feedback.getFeedback());
        viewHolder.rating.setRating(Float.valueOf(feedback.getRating()));
        viewHolder.rating.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        return mView;
    }

    static class ViewHolder {
        TextView title;
        TextView feed_back;
        RatingBar rating;
    }
}

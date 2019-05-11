package com.grabid.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;

/**
 * Created by graycell on 11/12/17.
 */

public class HelpDetail extends Fragment {
    TextView mCategoryQues, mCategoryAnswer;
    String mQuestion, mAnswer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.helpdetail, null);
        mQuestion = getArguments().getString("question");
        mAnswer = getArguments().getString("answer");
        init(view);
        appendData();
        return view;
    }

    public void init(View view) {
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.title.setText(getResources().getString(R.string.help));
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        mCategoryAnswer = view.findViewById(R.id.categoryanswer);
        mCategoryQues = view.findViewById(R.id.categoryques);
    }

    public void appendData() {
        mCategoryQues.setText(mQuestion);
        mCategoryAnswer.setText(Html.fromHtml(mAnswer));
    }
}

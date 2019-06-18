package com.tipbox.app.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.tipbox.app.MainActivity;
import com.tipbox.app.R;
import com.tipbox.app.interfce.FragmentCallback;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    FragmentCallback fc;
    private LinkedHashMap<String, List<String>> expandableListDetail;
    static int[] subiconsetting = { R.drawable.menu_payment_methods_icon, R.drawable.menu_start_receiving_tips_icon,R.drawable.menu_logout_icon};

    static int[] subiconabout = {R.drawable.menu_privacy_policy_icon, R.drawable.menu_terms_of_use_icon,
            R.drawable.menu_help_icon};

    static int[] hicon = {R.drawable.menu_notification_icon,R.drawable.menu_dashboard_settings_icon, R.drawable.about_icon};
    public CustomExpandableListAdapter(Context context, List<String> expandableListTitle,
                                       LinkedHashMap<String, List<String>> expandableListDetail,FragmentCallback fc) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        this.fc=fc;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expand_item, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.expandedListItem);
        expandedListTextView.setClickable(false);
        ImageView img = (ImageView) convertView
                .findViewById(R.id.img);
        img.setClickable(false);
        LinearLayout lv = (LinearLayout) convertView
                .findViewById(R.id.lv);
        lv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc.getFragmentName(expandedListText);
            }
        });
        if(listPosition==1) {
            img.setBackgroundResource(subiconsetting[expandedListPosition]);
        }else if(listPosition==2) {
            img.setBackgroundResource(subiconabout[expandedListPosition]);
        }
        expandedListTextView.setText(expandedListText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(final int listPosition, boolean isExpanded,
                             View convertView, final ViewGroup parent) {
        final String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expand_group, null);
        }
        final TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        final LinearLayout lvgroup = (LinearLayout) convertView
                .findViewById(R.id.lvgroup);
        final ImageView img = (ImageView) convertView
                .findViewById(R.id.img);
        lvgroup.setClickable(true);
        listTitleTextView.setClickable(false);
        img.setClickable(false);
        lvgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listTitle.equals("Notifications")){
                    fc.getFragmentName(listTitle);
                }else{
                    ExpandableListView mExpandableListView = (ExpandableListView) parent;
                    if(!mExpandableListView.isGroupExpanded(listPosition))
                        mExpandableListView.expandGroup(listPosition);
                    else
                        mExpandableListView.collapseGroup(listPosition);
                }
            }
        });
        img.setBackgroundResource(hicon[listPosition]);
        //listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
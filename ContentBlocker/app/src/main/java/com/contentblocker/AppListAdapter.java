package com.contentblocker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by graycell on 5/30/2017.
 */
public class AppListAdapter extends BaseAdapter {

    Context context;
    ArrayList<AppListModel> appList = new ArrayList<AppListModel>();
    ArrayList<String> blockList = new ArrayList<String>();

    public AppListAdapter(Context activity, ArrayList<AppListModel> applist, ArrayList<String> blockList) {
        context = activity;
        appList = applist;
        this.blockList = blockList;
    }

    @Override
    public int getCount() {
        return appList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        vi = inflater.inflate(R.layout.applist_adapter, null);

        final AppListModel dataList = appList.get(position);
        TextView appName = (TextView) vi.findViewById(R.id.appName);
        RelativeLayout image = (RelativeLayout) vi.findViewById(R.id.appLogo);
        final CheckBox checkBox = (CheckBox) vi.findViewById(R.id.checkbox);

        /*if (dataList.getAppPackage().equalsIgnoreCase("com.android.settings")) {
            checkBox.setChecked(true);
        }*/

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               /* if (dataList.getAppPackage().equalsIgnoreCase("com.android.settings")) {
                    checkBox.setChecked(true);
                } else {*/
                    if (isChecked) {
                        if (!blockList.contains(dataList.getAppPackage())) {
                            blockList.add(dataList.getAppPackage());
                            Log.v("blocked", "blocked");
                        }
                    } else {
                        Log.v("Unblocked", "Unblocked");
                        blockList.remove(dataList.getAppPackage());
                    }
               /* }*/
            }
        });

        appName.setText(dataList.getAppName());
        image.setBackgroundDrawable(dataList.getImageLogo());

        return vi;
    }


}

package com.grabid.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.models.Items;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vinod on 10/14/2016.
 */
public class Preferences extends Fragment implements AsyncTaskCompleteListener {
    private LinearLayout mLinearListView;
    SessionManager session;
    String type = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.title.setText(getResources().getString(R.string.preference));
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));

       /* RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) HomeActivity.track_delivery.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        HomeActivity.track_delivery.setLayoutParams(params);*/
        //HomeActivity.track_delivery.setText("Save");
        //HomeActivity.track_delivery.setPadding(0,10,0,0);
        //HomeActivity.track_delivery.setTextColor(Color.BLACK);

        View view = inflater.inflate(R.layout.preferences, null);
        init(view);
        getPreferences();
        return view;
    }

    boolean isCategorySelected = false;

    public String printJson() throws JSONException {
        int count = 0;
        JSONArray arr = new JSONArray();
        for (int j = 0; j < mainList.size(); j++) {
            if (mainList.get(j).isChecked()) {
                ++count;
            }
            ArrayList<Items.ItemList> itemList = mainList.get(j).getmItemListArray();
            JSONArray subArr = new JSONArray();
            for (int k = 0; k < itemList.size(); k++) {
                JSONObject subObj = new JSONObject();
                subObj.put(Keys.KEY_ID, itemList.get(k).getId());
                subObj.put(Keys.KEY_NAME, itemList.get(k).getItemName());
                subObj.put(Keys.KEY_IS_SELECTED, (itemList.get(k).isChecked() == true) ? "1" : "0");
                subArr.put(subObj);
            }
            JSONObject ob = new JSONObject();
            ob.put(Keys.KEY_ID, mainList.get(j).getId());
            ob.put(Keys.KEY_NAME, mainList.get(j).getCatName());
            ob.put(Keys.KEY_SUB_PREFERENCES, subArr);
            ob.put(Keys.KEY_IS_SELECTED, (mainList.get(j).isChecked() == true) ? "1" : "0");
            arr.put(ob);
        }

        if (count >= 2) {
            isCategorySelected = true;
        }
        return arr.toString();
    }

    private void savePreferences() {
        type = "2";
        try {
            Log.v("printJson", "printJson" + printJson());
            HashMap<String, String> params = new HashMap<>();
            params.put("data", printJson());
            String url = Config.SERVER_URL + Config.PREFERENCES + "/0";
            if (Internet.hasInternet(getActivity())) {
                RestAPICall api = new RestAPICall(getActivity(), HTTPMethods.PUT, this, params);
                api.execute(url, session.getToken());
            } else
                AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
        } catch (Exception ex) {

        }
    }

    private void getPreferences() {
        type = "1";
        String url = Config.SERVER_URL + Config.PREFERENCES;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall api = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            api.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void init(View view) {
        session = new SessionManager(getActivity());
        mLinearListView = (LinearLayout) view.findViewById(R.id.linear_ListView);
        TextView save = (TextView) view.findViewById(R.id.save);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
            }
        });
    }

    boolean isFirstViewClick = false;
    private ArrayList<Items> mainList = new ArrayList<Items>();

    public void appendData() {
        mainList = getUserPreferences();
        addSecondLevelViews();
    }

    public void addSecondLevelViews() {
        for (int j = 0; j < mainList.size(); j++) {
            LayoutInflater inflater2 = null;
            inflater2 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View mLinearView2 = inflater2.inflate(R.layout.row_second, null);
            TextView mSubItemName = (TextView) mLinearView2.findViewById(R.id.textViewTitle);
            final RelativeLayout mLinearSecondArrow = (RelativeLayout) mLinearView2.findViewById(R.id.linearSecond);
            final ImageView mImageArrowSecond = (ImageView) mLinearView2.findViewById(R.id.imageSecondArrow);
            final LinearLayout mLinearScrollThird = (LinearLayout) mLinearView2.findViewById(R.id.linear_scroll_third);
            final CheckBox chkBox2 = (CheckBox) mLinearView2.findViewById(R.id.checkBox);
            chkBox2.setTag(j);
            final Items item = mainList.get(j);
            isFirstViewClick = item.isVisible();

            if (isFirstViewClick == false) {
                mLinearScrollThird.setVisibility(View.GONE);
                mImageArrowSecond.setBackgroundResource(R.drawable.plus_icn);
            } else {
                mLinearScrollThird.setVisibility(View.VISIBLE);
                mImageArrowSecond.setBackgroundResource(R.drawable.minus_icn);
            }

            mLinearSecondArrow.setTag(j);
            mLinearScrollThird.setVisibility((item.isVisible() ? View.VISIBLE : View.GONE));

            if (item.isVisible())
                addThirdLevelView(mLinearScrollThird, mainList.get((int) chkBox2.getTag()));

            mLinearSecondArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFirstViewClick == false) {
                        isFirstViewClick = true;
                        mImageArrowSecond.setBackgroundResource(R.drawable.minus_icn);
                        mLinearScrollThird.setVisibility(View.VISIBLE);

                        if (((LinearLayout) mLinearScrollThird).getChildCount() > 0)
                            ((LinearLayout) mLinearScrollThird).removeAllViews();

                        mainList.set((int) chkBox2.getTag(), new Items(item.getId(), item.getCatName(), item.getmItemListArray(), item.isChecked(), isFirstViewClick));
                        addThirdLevelView(mLinearScrollThird, mainList.get((int) chkBox2.getTag()));
                    } else {
                        isFirstViewClick = false;
                        mImageArrowSecond.setBackgroundResource(R.drawable.plus_icn);
                        mLinearScrollThird.setVisibility(View.GONE);
                        mainList.set((int) chkBox2.getTag(), new Items(item.getId(), item.getCatName(), item.getmItemListArray(), item.isChecked(), isFirstViewClick));
                    }
                }
            });

            if (item.getmItemListArray().size() == 0)
                mImageArrowSecond.setVisibility(View.GONE);

            mSubItemName.setText(item.getCatName());
            chkBox2.setChecked(item.isChecked());

            chkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    ArrayList<Items.ItemList> itemList = new ArrayList<>();

                    if (((LinearLayout) mLinearScrollThird).getChildCount() > 0)
                        ((LinearLayout) mLinearScrollThird).removeAllViews();

                    for (int j = 0; j < item.getmItemListArray().size(); j++) {
                        Items.ItemList il = item.getmItemListArray().get(j);
                        itemList.add(item.getmItemListArray().indexOf(il), new Items.ItemList(il.getId(), il.getItemName(), isChecked));
                    }

                    /*boolean isCheckTop = false;

                    for (int i = 0; i < mainList.get((int) chkBox2.getTag()).getmItemListArray().size(); i++) {
                        if (mainList.get((int) chkBox2.getTag()).getmItemListArray().get(i).isChecked()) {
                            isCheckTop = true;
                            break;
                        }
                    }*/
                    //mainList.set((int) chkBox2.getTag(), new Items(item.getId(), item.getCatName(), item.getmItemListArray(), isCheckTop, item.isVisible()));
                    mainList.set((int) chkBox2.getTag(), new Items(item.getId(), item.getCatName(), itemList, isChecked, isChecked));

                    if (isChecked) {
                        isFirstViewClick = true;
                        mImageArrowSecond.setBackgroundResource(R.drawable.minus_icn);
                        mLinearScrollThird.setVisibility(View.VISIBLE);
                        //final ArrayList<SubCategory> subCatData2 = mainList.get((int) chkBox2.getTag()).getmSubCategoryList();
                        addThirdLevelView(mLinearScrollThird, mainList.get((int) chkBox2.getTag()));
                        //addThirdLevelView(mLinearScrollThird, mainList.get((int) chkBox2.getTag()), subCatData2.get((int) mLinearSecondArrow.getTag()));
                    } else {
                        isFirstViewClick = true;
                        mImageArrowSecond.setBackgroundResource(R.drawable.plus_icn);
                        mLinearScrollThird.setVisibility(View.GONE);
                    }

                    mLinearListView.removeAllViews();
                    addSecondLevelViews();
                }
            });

            mLinearListView.addView(mLinearView2);
        }
    }

    public void addThirdLevelView(final LinearLayout mLinearScrollThird, final Items items) {
        final ArrayList<Items.ItemList> data = items.getmItemListArray();
        for (int k = 0; k < data.size(); k++) {
            LayoutInflater inflater3 = null;
            inflater3 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View mLinearView3 = inflater3.inflate(R.layout.row_third, null);
            final TextView mItemName = (TextView) mLinearView3.findViewById(R.id.textViewItemName);
            final CheckBox chkBox3 = (CheckBox) mLinearView3.findViewById(R.id.checkBox);
            final Items.ItemList il = data.get(k);
            mItemName.setText(il.getItemName());
            mItemName.setTag(data.indexOf(il));
            mLinearView3.setTag(mainList.indexOf(items));
            chkBox3.setChecked(il.isChecked());
            chkBox3.setTag(items.getmItemListArray().indexOf(items));

            chkBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    data.set(((int) mItemName.getTag()), new Items.ItemList(il.getId(), il.getItemName(), isChecked));
                    boolean isCheckTop = false;

                    for (int i = 0; i < items.getmItemListArray().size(); i++) {
                        if (items.getmItemListArray().get(i).isChecked()) {
                            isCheckTop = true;
                            break;
                        }
                    }
                    mainList.set((int) mLinearView3.getTag(), new Items(items.getId(), items.getCatName(), items.getmItemListArray(), isCheckTop, true));
                    mLinearListView.removeAllViews();
                    addSecondLevelViews();
                }
            });
            mLinearScrollThird.addView(mLinearView3);
        }
    }

    ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

    public ArrayList<Items> getUserPreferences() {
        ArrayList<Items> listDataHeader = new ArrayList<Items>();
        ArrayList<Items.ItemList> subArrayListItem = null;
        Log.d(Config.DATA, data.toString());
        for (int i = 0; i < data.size(); i++) {
            JSONArray outterArr;
            try {
                outterArr = new JSONArray(data.get(i).get(Keys.KEY_SUB_PREFERENCES));
                subArrayListItem = new ArrayList<>();
                for (int k = 0; k < outterArr.length(); k++) {
                    JSONObject inob = outterArr.getJSONObject(k);
                    subArrayListItem.add(k, new Items.ItemList(inob.getString(Keys.KEY_ID), inob.getString(Keys.KEY_NAME), (inob.getString(Keys.KEY_IS_SELECTED).equals("1")) ? true : false));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listDataHeader.add(i, new Items(data.get(i).get(Keys.KEY_ID), data.get(i).get(Keys.KEY_NAME), subArrayListItem, (data.get(i).get(Keys.KEY_IS_SELECTED).equals("1")) ? true : false, false));
        }

        return listDataHeader;
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    JSONObject outterJson;

    private void handleResponse(String result) {
        try {
            outterJson = new JSONObject(result);
            if (outterJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                if (type.equals("1")) {
                    JSONArray outterArray = outterJson.getJSONArray(Config.DATA);

                    for (int i = 0; i < outterArray.length(); i++) {
                        JSONObject innerObject = outterArray.getJSONObject(i);

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(Keys.KEY_ID, innerObject.getString(Keys.KEY_ID));
                        map.put(Keys.KEY_NAME, innerObject.getString(Keys.KEY_NAME));
                        map.put(Keys.KEY_IS_SELECTED, innerObject.getString(Keys.KEY_IS_SELECTED));
                        map.put(Keys.KEY_SUB_PREFERENCES, innerObject.get(Keys.KEY_SUB_PREFERENCES).toString());
                        data.add(map);
                    }
                    appendData();
                } else {
                    showSuccessMessage("Your preferences have been saved successfully.");
                }
            } else if (outterJson.getInt(Config.STATUS) == APIStatus.UNPROCESSABLE) {
                if (outterJson.getString(Config.MESSAGE).equals(""))
                    showMessage("Error", outterJson.getJSONArray(Config.DATA).getJSONObject(0).getString(Config.MESSAGE));
                else
                    showMessage("Error", outterJson.getString(Config.MESSAGE));
            } else {
                showMessage("Error", getResources().getString(R.string.no_response));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showSuccessMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Success!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (type.equals("choose")) {
                    getActivity().getFragmentManager().popBackStack();
                } else {
                    Fragment fragment = new HomeMap();
                    String backStateName = "com.grabid.activities.HomeActivity";
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                }
            }
        });
        builder.show();
    }

    private void showMessage(String title, String message) {
        AlertManager.messageDialog(getActivity(), title, message);
    }

}
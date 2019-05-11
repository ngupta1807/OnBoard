package com.grabid.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.models.Category;
import com.grabid.models.FavouriteGroupModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FavoriteGroupSelectionList extends AppCompatActivity implements View.OnClickListener, AsyncTaskCompleteListener {
    ArrayList<Category> favouritegroupsdata = new ArrayList<>();
    SessionManager session;
    int page = 1;
    int totalCount = 1;
    boolean loadingMore = false;
    int type;
    CategoryAdapter adapter;
    TextView mTitle;
    ImageView close;
    ListView list;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    TextView noData;
    boolean IsPageNation = true;
    String selected_fav_user_id = "";
    TextView mDone;
    TextView mAdd;
String hasData="";
    public void clickme(View view) {
        switch (view.getId()) {
            case R.id.back:
                cancelresult();
                break;
            case R.id.done:
                if (mDone.getText().toString().equalsIgnoreCase("DONE")) {
                    for (int i = 0; i < favouritegroupsdata.size(); i++) {
                        if (favouritegroupsdata.get(i).isSelected()) {
                            hasData="yes";
                            break;
                        }else{
                            hasData="no";
                        }
                    }
                    if (hasData.equals("no")) {
                        displayAlert();
                    } else {
                        done();
                    }
                }
                else
                    AddMore();
                break;
            case R.id.add:
                AddMore();
                break;
        }
    }

    private void displayAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FavoriteGroupSelectionList.this);
        builder.setTitle("Alert!");
        builder.setMessage(getResources().getString(R.string.selectgroupalert));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void AddMore() {
        final Dialog alertDialog = new Dialog(FavoriteGroupSelectionList.this);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.addmoregroups);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final EditText subEditText = (EditText) alertDialog.findViewById(R.id.group_name);
        TextView save = (TextView) alertDialog.findViewById(R.id.save);
        TextView cancel = (TextView) alertDialog.findViewById(R.id.cancel);
        TextView Title = (TextView) alertDialog.findViewById(R.id.favtitle);
        Title.setText("Add Favourite Group");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (subEditText.getText().toString().length() > 0) {
                    AddFavourite(subEditText.getText().toString());
                } else
                    AlertManager.messageDialog(FavoriteGroupSelectionList.this, "Alert!", "Please enter group name.");
            }

        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    public void AddFavourite(String groupname) {
        type = 2;
        HashMap<String, String> params = new HashMap<>();
        String url = Config.SERVER_URL + Config.CREATE_FAV_USERGROUP;
        params.put("name", groupname);
        Log.d("end", params.toString());
        if (Internet.hasInternet(FavoriteGroupSelectionList.this)) {
            RestAPICall mobileAPI = new RestAPICall(FavoriteGroupSelectionList.this, HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(FavoriteGroupSelectionList.this, "Alert!", getResources().getString(R.string.no_internet));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            e.toString();
        }
        setContentView(R.layout.activity_multiple_groups);
        Bundle extras = getIntent().getExtras();
        if (extras.containsKey("IsPageNation"))
            IsPageNation = extras.getBoolean("IsPageNation");
        if (extras.containsKey("data"))
            selected_fav_user_id = extras.getString("data");
        init();

    }

    public void sendData(String val) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("value", val);
        if (favouritegroupsdata.size() == 0)
            returnIntent.putExtra("hasGroups", false);
        else
            returnIntent.putExtra("hasGroups", true);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void cancelresult() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    public void init() {
        session = new SessionManager(FavoriteGroupSelectionList.this);
        adapter = new CategoryAdapter(FavoriteGroupSelectionList.this);
        mTitle = findViewById(R.id.title);
        close = findViewById(R.id.close);
        list = findViewById(R.id.list);
        mDone = findViewById(R.id.done);
        mAdd = findViewById(R.id.add);
        list.setSmoothScrollbarEnabled(true);
        list.setAdapter(adapter);
        noData = findViewById(R.id.no_data);

        // list.setItemsCanFocus(true);

        list.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView view,
                                 int firstVisibleItemm, int visibleItemCountt,
                                 int totalItemCountt) {
                firstVisibleItem = firstVisibleItemm;
                visibleItemCount = visibleItemCountt;
                totalItemCount = totalItemCountt;


            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (firstVisibleItem > 0 && lastItem == totalItemCount && scrollState == SCROLL_STATE_IDLE) {
                    if (!loadingMore) {
                        if (totalCount >= page) {
                            loadingMore = true;
                            getMultipleGroups();
                        }
                    }
                }
            }
        });
        getMultipleGroups();
    }

    public void getMultipleGroups() {
        type = 0;
        String url = "";
        page = 1;
        if (IsPageNation)
            url = Config.SERVER_URL + Config.GET_FAVOURITEGROUPS + "&page=" + page;
        else
            url = Config.SERVER_URL + Config.GET_FAVOURITE_GROUP_LIST;
        if (Internet.hasInternet(FavoriteGroupSelectionList.this)) {
            RestAPICall mobileAPI = new RestAPICall(FavoriteGroupSelectionList.this, HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(FavoriteGroupSelectionList.this, "Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onTaskComplete(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                if (type == 2) {
                    getMultipleGroups();
                } else {
                    if (type == 0) {
                        if (page == 1)
                            favouritegroupsdata.clear();
                        ++page;
                        loadingMore = false;
                        if (IsPageNation) {
                            JSONObject dataObj = outJson.getJSONObject(Config.DATA);
                            JSONArray favouriteArray = dataObj.getJSONArray(Config.FAVUSER);
                            for (int i = 0; i < favouriteArray.length(); i++) {
                                JSONObject carrierObj = favouriteArray.getJSONObject(i);
                                FavouriteGroupModel fav = new FavouriteGroupModel();
                                Category cat = new Category();
                                cat.setName(carrierObj.getString(Keys.NAME));
                                cat.setId(carrierObj.getString(Keys.ID));
                                cat.setSelected(false);
                                favouritegroupsdata.add(cat);
                            }
                            adapter.notifyDataSetChanged();
                            if (favouritegroupsdata.size() == 0) {
                                list.setVisibility(View.GONE);
                                noData.setVisibility(View.VISIBLE);
                                mDone.setText("ADD");
                                mAdd.setVisibility(View.GONE);


                            } else {
                                noData.setVisibility(View.GONE);
                                list.setVisibility(View.VISIBLE);
                                mDone.setText("DONE");
                                mAdd.setVisibility(View.VISIBLE);
                                mAdd.setText("ADD");

                            }
                            try {
                                if (dataObj.has("_meta")) {
                                    JSONObject metaCount = dataObj.getJSONObject("_meta");
                                    totalCount = Integer.parseInt(metaCount.optString("pageCount"));
                                    Log.v("totalcount", String.valueOf(totalCount));

                                }
                            } catch (Exception e) {
                                e.toString();
                            }
                        } else {
                            JSONArray dataObj = outJson.getJSONArray(Config.DATA);
                            if (dataObj.length() < 1) {
                                favouritegroupsdata.clear();
                                adapter.notifyDataSetChanged();
                                list.setVisibility(View.GONE);
                                noData.setVisibility(View.VISIBLE);
                                mDone.setText("DONE");

                            } else {
                                mDone.setText("DONE");
                                for (int i = 0; i < dataObj.length(); i++) {
                                    JSONObject carrierObj = dataObj.getJSONObject(i);
                                    FavouriteGroupModel fav = new FavouriteGroupModel();
                                    Category cat = new Category();
                                    cat.setName(carrierObj.getString(Keys.NAME));
                                    cat.setId(carrierObj.getString(Keys.ID));
                                    cat.setSelected(false);
                                    favouritegroupsdata.add(cat);
                                }
                                if (getIntent().getExtras().containsKey("data")) {
                                    try {
                                        if (selected_fav_user_id.length() > 0) {
                                            String fav_user[] = selected_fav_user_id.split(",");
                                            for (int j = 0; j < fav_user.length; j++) {
                                                for (int i = 0; i < dataObj.length(); i++) {
                                                    JSONObject innerJson = dataObj.getJSONObject(i);
                                                    Category cat = new Category();
                                                    if (fav_user[j].equals(innerJson.get(Keys.KEY_ID).toString())) {
                                                        cat.setName(innerJson.getString(Keys.KEY_NAME));
                                                        cat.setId(innerJson.get(Keys.KEY_ID).toString());
                                                        cat.setSelected(true);
                                                        // suitableSelectedVehicleData.add(cat);
                                                        favouritegroupsdata.set((int) i, cat);
                                                    }

                                                }
                                            }

                                        }
                                    } catch (Exception e) {
                                        e.toString();
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                if (favouritegroupsdata.size() == 0) {
                                    list.setVisibility(View.GONE);
                                    noData.setVisibility(View.VISIBLE);
                                    mDone.setText("DONE");
                                } else {
                                    noData.setVisibility(View.GONE);
                                    list.setVisibility(View.VISIBLE);
                                    mDone.setText("DONE");
                                }

                            }
                        }
                    }
                }
            } else if (outJson.getInt(Config.STATUS) == APIStatus.UNPROCESSABLE) {
                list.setVisibility(View.GONE);
                noData.setVisibility(View.VISIBLE);
                if (IsPageNation) {
                    mDone.setText("ADD");
                    mAdd.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.toString();
        }
    }

    private class CategoryAdapter extends BaseAdapter {
        Context ctx;

        CategoryAdapter(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public int getCount() {
            return favouritegroupsdata.size();
        }

        @Override
        public Object getItem(int i) {
            return favouritegroupsdata.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater vi = (LayoutInflater) getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.simple_lit_check1, null);
            Category category = (Category) getItem(position);
            final TextView name = (TextView) convertView.findViewById(R.id.textItem);
            final CheckBox check = (CheckBox) convertView.findViewById(R.id.check);
            name.setTag(position);
            name.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (check.isChecked())
                        check.setChecked(false);
                    else
                        check.setChecked(true);
                    check.setChecked(true);
                    Category country = new Category();
                    country = favouritegroupsdata.get((int) name.getTag());
                    country.setSelected(check.isChecked());
                    favouritegroupsdata.set((int) name.getTag(), country);
                }
                //notifyDataSetChanged();

            });
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    Category country = new Category();
                    country = favouritegroupsdata.get((int) name.getTag());
                    country.setSelected(isChecked);
                    favouritegroupsdata.set((int) name.getTag(), country);
                }
            });

            name.setText(category.getName());
            check.setChecked(category.isSelected());
            return convertView;
        }

    }

    String suitableIdBuilder = "";

    public void done() {
        String builder = "";
        for (int i = 0; i < favouritegroupsdata.size(); i++) {
            if (favouritegroupsdata.get(i).isSelected()) {
                try {
                    builder += favouritegroupsdata.get(i).getName() + ", ";
                    suitableIdBuilder += favouritegroupsdata.get(i).getId() + ",";
                    builder = suitableIdBuilder.substring(0, suitableIdBuilder.length() - 1);
                    Log.v("abc", suitableIdBuilder);
                } catch (Exception e) {
                    e.toString();
                }
            }
        }
        sendData(builder);

    }
}

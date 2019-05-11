package com.grabid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.activities.FavoriteGroupSelectionList;
import com.grabid.adapters.SearchAddAdapter;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.models.Category;
import com.grabid.models.SearchAddFavModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.grabid.common.AlertManager.messageDialog;

/**
 * Created by graycell on 13/12/17.
 */

public class SearchAddFavourite extends Fragment implements AsyncTaskCompleteListener {
    EditText mSearchedt;
    SessionManager session;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    int page = 1;
    int totalCount = 1;
    boolean loadingMore = false;
    ListView list;
    TextView noData;
    ArrayList<SearchAddFavModel> favourite = new ArrayList<SearchAddFavModel>();
    ArrayList<Category> favouritegroupsdata = new ArrayList<>();
    SearchAddAdapter adapter;
    String mSearchtxt;
    int type;
    String mfav_user_id;
    String isHome="";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.searchaddfavourite, null);
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.title.setText(R.string.searchfavourite);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        init(view);
        return view;
    }

    public void init(View view) {
        session = new SessionManager(getActivity());
        list = (ListView) view.findViewById(R.id.list);
        noData = (TextView) view.findViewById(R.id.no_data);
        mSearchedt = (EditText) view.findViewById(R.id.search_et);
        mSearchedt.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mSearchedt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == 12) {
                    mSearchtxt = mSearchedt.getText().toString();
                    page = 1;
                    getDrivers(mSearchtxt);
                }
                return false;
            }
        });
        if (getArguments() != null) {
            if (getArguments().containsKey("group_id")) {
                isHome = "no";
            }
        }
        adapter = new SearchAddAdapter(getActivity(), favourite,isHome, true, this);
        list.setSmoothScrollbarEnabled(true);
        list.setAdapter(adapter);
        list.setItemsCanFocus(true);
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
                            getDrivers(mSearchtxt);
                        }
                    }
                }
            }
        });
    }

    public void SuccessDialog(Context ctx, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getActivity().onBackPressed();
                /*if (getArguments() != null && getArguments().containsKey("group_id")) {
                    getActivity().onBackPressed();
                } else {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra("favourite", "1");
                    if (getArguments() != null && getArguments().containsKey("group_id"))
                        intent.putExtra("group_id", getArguments().getString("group_id"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }*/

            }
        });
        Dialog d = builder.create();
        d.show();
    }

    private void showMessage(String title, String message) {
        messageDialog(getActivity(), title, message);
    }

    public void FavouriteGroups(String user_id) {
        String backStateName = this.getClass().getName();
        Fragment fragment = new FavouriteGroups();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", user_id);
        fragment.setArguments(bundle);
        getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                .addToBackStack(null)
                .commitAllowingStateLoss();

    }

    public void Add(String fav_user_id) {
        type = 1;
        String url = "";
        HashMap<String, String> params = new HashMap<>();
        if (getArguments() != null && getArguments().containsKey("group_id")) {
            url = Config.SERVER_URL + Config.ADD_FAVOURIE_PARICULAR_GROUP;
            params.put("group_id", getArguments().getString("group_id"));
            //url = Config.SERVER_URL + Config.ADD_FAVOURITE;
            params.put("fav_user_id", fav_user_id);
            Log.d("end", params.toString());
            if (Internet.hasInternet(getActivity())) {
                RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
                mobileAPI.execute(url, session.getToken());
            } else
                showMessage("Alert!", getResources().getString(R.string.no_internet));
        } else
            showADDialog(fav_user_id);
    }

    public void showADDialog(final String fav_user_id) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // builder.setCancelable(false);
        //  builder.setTitle(getResources().getString(R.string.allorselected));
        builder.setMessage(getResources().getString(R.string.allorselected));
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              /*  type = 1;
                String url = "";
                HashMap<String, String> params = new HashMap<>();
                url = Config.SERVER_URL + Config.ADD_FAVOURIE_PARICULAR_GROUP;
                params.put("group_id", "all");
                params.put("fav_user_id", fav_user_id);
                Log.d("end", params.toString());
                call(params, url);*/
            }
        });
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // getDelivery();
                mfav_user_id = fav_user_id;
                getfavourites();

            }
        });
        builder.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 16 && resultCode == -1) {
            try {
                String val = data.getStringExtra("value");
                Log.v("val", val);
                String url = "";
                type = 1;
                HashMap<String, String> params = new HashMap<>();
                url = Config.SERVER_URL + Config.ADD_FAVOURIE_PARICULAR_GROUP;
                params.put("group_id", val);
                params.put("fav_user_id", mfav_user_id);
                Log.d("end", params.toString());
                if (Internet.hasInternet(getActivity())) {
                    RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
                    mobileAPI.execute(url, session.getToken());
                } else
                    showMessage("Alert!", getResources().getString(R.string.no_internet));
            } catch (Exception e) {
                e.toString();
            }
        }
    }

    public void getfavourites() {
        Intent intent = new Intent(getActivity(), FavoriteGroupSelectionList.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("IsPageNation", true);
        intent.putExtras(bundle);
        startActivityForResult(intent, 16);
    }

    public void call(HashMap<String, String> params, String url) {
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage("Alert!", getResources().getString(R.string.no_internet));
    }

    public void getDrivers(String searchtxt) {
        type = 0;
        String url;
        if (getArguments() != null && getArguments().containsKey("group_id"))
            url = Config.SERVER_URL + Config.SEARCH_FAVOURITIES + searchtxt + "&group_id=" + getArguments().getString("group_id") + "&page=" + page;
        else
            url = Config.SERVER_URL + Config.SEARCH_FAVOURITIES + searchtxt + "&page=" + page;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public void onTaskComplete(String result) {
        Log.v("", result.toString());
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                if (type == 0) {
                    if (page == 1)
                        favourite.clear();
                    ++page;
                    loadingMore = false;
                    JSONObject dataObj = outJson.getJSONObject(Config.DATA);
                    JSONArray favouriteArray = dataObj.getJSONArray(Config.FAVUSER);
                    for (int i = 0; i < favouriteArray.length(); i++) {
                        JSONObject carrierObj = favouriteArray.getJSONObject(i);
                        SearchAddFavModel fav = new SearchAddFavModel();
                        fav.setId(carrierObj.getString(Keys.ID));
                        //fav.setFirstName(carrierObj.getString(Keys.FIRST_NAME));
                        //fav.setLastName(carrierObj.getString(Keys.LAST_NAME));
                        fav.setUserName(carrierObj.getString(Keys.USERNAME));
                        fav.setEmail(carrierObj.getString(Keys.EMAIL));
                        fav.setMobile(carrierObj.getString(Keys.MOBILE));
                        fav.setDriver_rating(carrierObj.getString(Keys.DRIVER_RATING));
                        fav.setShipper_rating(carrierObj.getString(Keys.SHIPPER_RATING));
                        favourite.add(fav);
                    }
                    adapter.notifyDataSetChanged();
                    if (favourite.size() == 0) {
                        list.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                    } else {
                        noData.setVisibility(View.GONE);
                        list.setVisibility(View.VISIBLE);
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
                } else if (type == 1) {
                    SuccessDialog(getActivity(), "Success!", outJson.getString(Config.MESSAGE));
                }
            } else if (outJson.getInt(Config.STATUS) == APIStatus.UNPROCESSABLE) {
                if (type == 0) {
                    page = 1;
                    loadingMore = false;
                    favourite.clear();
                    adapter.notifyDataSetChanged();
                    list.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                    messageDialog(getActivity(), "Alert!", outJson.getString(Config.MESSAGE));
                } else
                    messageDialog(getActivity(), "Alert!", outJson.getString(Config.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

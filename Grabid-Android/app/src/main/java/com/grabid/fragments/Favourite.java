package com.grabid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.adapters.SearchAddAdapter;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.models.SearchAddFavModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by graycell on 13/12/17.
 */

public class Favourite extends Fragment implements AsyncTaskCompleteListener {
    ListView list;
    TextView noData;
    SessionManager session;
    SearchAddAdapter adapter;
    ArrayList<SearchAddFavModel> favourite = new ArrayList<SearchAddFavModel>();
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    int page = 1;
    int totalCount = 1;
    boolean loadingMore = false;
    int type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favourite, null);
        page = 1;
        if (getArguments() != null && getArguments().containsKey("group_id")) {
            if (getArguments().containsKey("incoming_type"))
                HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
            else
                HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
        } else
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);

        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));

        HomeActivity.addmore.setVisibility(View.VISIBLE);
        if (getArguments() != null && getArguments().containsKey("name"))
            HomeActivity.title.setText(getArguments().getString("name"));
        else
            HomeActivity.title.setText(R.string.favourite);

        HomeActivity.addmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMore();
            }
        });
        init(view);
        if (page == 1) {
            getfavourites();
        }
        return view;
    }

    public void AddMore() {
        String backStateName = this.getClass().getName();
        HomeActivity.addmore.setVisibility(View.GONE);
        Fragment fragment = new SearchAddFavourite();
        if (getArguments() != null && getArguments().containsKey("group_id")) {
            Bundle bundle = new Bundle();
            bundle.putString("group_id", getArguments().getString("group_id"));
            if (getArguments().containsKey("name"))
                bundle.putString("name", getArguments().getString("name"));

            if (getArguments() != null) {
                if (getArguments().containsKey("group_id")) {
                    bundle.putString("group_id", getArguments().getString("group_id"));
                }
            }
            fragment.setArguments(bundle);
        }
        getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }


   /* @Override
    public void onStop() {
        super.onStop();
        HomeActivity.addmore.setVisibility(View.GONE);
    }*/
String isHome="";
    public void init(View view) {
        session = new SessionManager(getActivity());
        list = (ListView) view.findViewById(R.id.list);
        noData = (TextView) view.findViewById(R.id.no_data);
        if (getArguments() != null) {
            if (getArguments().containsKey("group_id")) {
                isHome = "no";
            }
        }
        adapter = new SearchAddAdapter(getActivity(), favourite,isHome, false, this);
        list.setSmoothScrollbarEnabled(true);
        list.setAdapter(adapter);
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
                            getfavourites();
                        }
                    }
                }
            }
        });

    }

    public void getfavourites() {
        String url;
        if (getArguments() != null && getArguments().containsKey("group_id")) {
            url = Config.SERVER_URL + Config.FAVOURITE_USER_GROUP_DETAILS + getArguments().getString("group_id") + "&page=" + page;
            type = 2;
        } else {
            url = Config.SERVER_URL + Config.GET_FAVOURITIES + "?page=" + page;
            type = 0;
        }
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    public void FavouriteGroups(String user_id) {
        String backStateName = this.getClass().getName();
        HomeActivity.addmore.setVisibility(View.GONE);
        Fragment fragment = new FavouriteGroups();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", user_id);
        fragment.setArguments(bundle);
        getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                .addToBackStack(null)
                .commitAllowingStateLoss();

    }


    public void Remove(String fav_user_id) {
        if (getArguments() != null && getArguments().containsKey("group_id")) {
            showRemoveDialog(getActivity(), "Alert!", getResources().getString(R.string.removeuserfavgroup), fav_user_id, true);
        } else {
            showRemoveDialog(getActivity(), "Alert!", getResources().getString(R.string.suretoremove), fav_user_id, false);
        }
          /*  url = Config.SERVER_URL + Config.REMOVE_FAVOURITE;
            HashMap<String, String> params = new HashMap<>();
            params.put("fav_user_id", fav_user_id);
            Log.d("end", params.toString());
            if (Internet.hasInternet(getActivity())) {
                RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
                mobileAPI.execute(url, session.getToken());
            } else
                AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
        }*/
    }

   /* public void Remove(String fav_user_id) {
        type = 1;
        String url = "";
        HashMap<String, String> params = new HashMap<>();
        if (getArguments() != null && getArguments().containsKey("group_id")) {
            url = Config.SERVER_URL + Config.REMOVE_USER_FROM_GROUP;
            params.put("group_id", getArguments().getString("group_id"));
        } else
            url = Config.SERVER_URL + Config.REMOVE_FAVOURITE;
        params.put("fav_user_id", fav_user_id);
        Log.d("end", params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }*/

    public void SuccessDialog(Context ctx, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                page = 1;
                getfavourites();
            }
        });
        Dialog d = builder.create();
        d.show();
    }

    public void showRemoveDialog(Context ctx, String title, String message, final String fav_user_id, final boolean IsGroupUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (IsGroupUser)
                    RemoveFavouritegroupUser(fav_user_id);
                else
                    RemoveUser(fav_user_id);
            }

        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });
        Dialog d = builder.create();
        d.show();
    }

    public void RemoveFavouritegroupUser(String fav_user_id) {
        type = 1;
        String url = "";
        url = Config.SERVER_URL + Config.REMOVEFAVOURIE_GET + fav_user_id;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));

    }

    public void RemoveUser(String fav_user_id) {
        type = 1;
        String url = "";
        url = Config.SERVER_URL + Config.REMOVE_FAVOURITE;
        HashMap<String, String> params = new HashMap<>();
        params.put("fav_user_id", fav_user_id);
        Log.d("end", params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public void onTaskComplete(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                if (type == 2) {
                    if (page == 1)
                        favourite.clear();
                    ++page;
                    loadingMore = false;
                    JSONObject dataObj = outJson.getJSONObject(Config.DATA);
                    JSONArray favouriteArray = dataObj.getJSONArray(Config.FAVUSER);
                    for (int i = 0; i < favouriteArray.length(); i++) {
                        JSONObject carrierObj = favouriteArray.getJSONObject(i);
                        JSONArray childArray = carrierObj.getJSONArray(Config.FAVUSERS);
                        for (int j = 0; j < childArray.length(); j++) {
                            JSONObject childObj = childArray.getJSONObject(j);
                            SearchAddFavModel fav = new SearchAddFavModel();
                            fav.setId(childObj.getString(Keys.ID));
                            fav.setFavouriteuserid(childObj.getString(Keys.FAVOURITE_USER_ID));
                            fav.setFirstName(childObj.getString(Keys.FULL_NAME));
                            fav.setLastName(childObj.getString(Keys.FULL_NAME));
                            fav.setUserName(childObj.getString(Keys.FULL_NAME));
                            fav.setEmail("");
                            fav.setMobile(childObj.getString(Keys.MOBILE));
                            fav.setDriver_rating(childObj.getString(Keys.RATING));
                            fav.setShipper_rating(childObj.getString(Keys.RATING));
                            favourite.add(fav);
                        }
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
                }
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
                        fav.setFavouriteuserid(carrierObj.getString(Keys.ID));
                        fav.setFirstName(carrierObj.getString(Keys.FIRST_NAME));
                        fav.setLastName(carrierObj.getString(Keys.LAST_NAME));
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
            } else {
                page = 1;
                loadingMore = false;
                favourite.clear();
                adapter.notifyDataSetChanged();
                list.setVisibility(View.GONE);
                noData.setVisibility(View.VISIBLE);
                //  AlertManager.messageDialog(getActivity(), "Alert!", outJson.getString(Config.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            noData.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        }

    }
}

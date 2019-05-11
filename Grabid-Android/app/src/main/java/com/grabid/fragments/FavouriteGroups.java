package com.grabid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.adapters.FavouriteGroupAdapter;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.models.FavouriteGroupModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by graycell on 9/2/18.
 */

public class FavouriteGroups extends Fragment implements AsyncTaskCompleteListener {
    ListView list;
    TextView noData;
    SessionManager session;
    FavouriteGroupAdapter adapter;
    ArrayList<FavouriteGroupModel> favourite = new ArrayList<FavouriteGroupModel>();
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    int page = 1;
    int totalCount = 1;
    boolean loadingMore = false;
    int type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favourite, null);
        if (getArguments() != null && getArguments().containsKey("user_id")) {
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
            HomeActivity.addmore.setVisibility(View.GONE);
        } else {
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
            HomeActivity.addmore.setVisibility(View.VISIBLE);
        }

        //    HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));

        if (getArguments() != null && getArguments().containsKey("user_id"))
            HomeActivity.addmore.setVisibility(View.GONE);
        else
            HomeActivity.addmore.setVisibility(View.VISIBLE);
        HomeActivity.title.setText(R.string.favouritegroups);

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


    private void AddMore() {
        final Dialog alertDialog = new Dialog(getActivity());
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
                    AlertManager.messageDialog(getActivity(), "Alert!", "Please enter group name.");
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

    public void RenameFavouriteDialog(final String id) {
        final Dialog alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.addmoregroups);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final EditText subEditText = (EditText) alertDialog.findViewById(R.id.group_name);
        TextView save = (TextView) alertDialog.findViewById(R.id.save);
        TextView cancel = (TextView) alertDialog.findViewById(R.id.cancel);
        TextView Title = (TextView) alertDialog.findViewById(R.id.favtitle);
        Title.setText("Rename Favourite Group");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (subEditText.getText().toString().length() > 0) {
                    RenameFavourite(subEditText.getText().toString(), id);
                } else
                    AlertManager.messageDialog(getActivity(), "Alert!", "Please enter group name.");
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



    public void init(View view) {
        session = new SessionManager(getActivity());
        list = (ListView) view.findViewById(R.id.list);
        noData = (TextView) view.findViewById(R.id.no_data);
        if (getArguments() != null && getArguments().containsKey("user_id"))
            adapter = new FavouriteGroupAdapter(getActivity(), favourite, true, this);
        else
            adapter = new FavouriteGroupAdapter(getActivity(), favourite, false, this);
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

    public void viewFavourities(String id, String name) {
        String backStateName = this.getClass().getName();
        Bundle bundle = new Bundle();
        bundle.putString("group_id", id);
        bundle.putString("name", name);
        Fragment fragment = new Favourite();
        fragment.setArguments(bundle);
        getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void getfavourites() {
        type = 0;
        String url = "";
        if (getArguments() != null && getArguments().containsKey("user_id"))
            url = Config.SERVER_URL + Config.GET_FAVOURITE_USERS_GROUP + getArguments().getString("user_id") + "&page=" + page;
        else
            url = Config.SERVER_URL + Config.GET_FAVOURITEGROUPS + "&page=" + page;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    public void AddFavourite(String groupname) {
        type = 2;
        HashMap<String, String> params = new HashMap<>();
        String url = Config.SERVER_URL + Config.CREATE_FAV_USERGROUP;
        params.put("name", groupname);
        Log.d("end", params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    public void RenameFavourite(String groupname, String id) {
        type = 4;
        HashMap<String, String> params = new HashMap<>();
        String url = Config.SERVER_URL + Config.RENAME_FAV_GROUP;
        params.put("name", groupname);
        params.put("id", id);
        Log.d("end", params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    public void showRemoveDialog(Context ctx, String title, String message, final String group_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                RemoveGrp(group_id);
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

    public void Remove(String group_id) {
        showRemoveDialog(getActivity(), "Alert!", getResources().getString(R.string.removegrp), group_id);

    }

    public void RemoveGrp(String group_id) {
        type = 1;
        String url;
        url = Config.SERVER_URL + Config.REMOVE_USER_GROUP + group_id;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));

    }

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

    @Override
    public void onTaskComplete(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                if (type == 2 || type == 1 || type == 4)
                    SuccessDialog(getActivity(), "Success!", outJson.getString(Config.MESSAGE));
                if (type == 0) {
                    if (page == 1)
                        favourite.clear();
                    ++page;
                    loadingMore = false;
                    if (getArguments() != null && getArguments().containsKey("user_id")) {
                        JSONArray favouriteArray = outJson.getJSONArray(Config.DATA);
                        for (int i = 0; i < favouriteArray.length(); i++) {
                            JSONObject carrierObj = favouriteArray.getJSONObject(i);
                            FavouriteGroupModel fav = new FavouriteGroupModel();
                            fav.setId(carrierObj.getString(Keys.ID));
                            fav.setName(carrierObj.getString(Keys.NAME));
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

                    } else {
                        JSONObject dataObj = outJson.getJSONObject(Config.DATA);
                        JSONArray favouriteArray = dataObj.getJSONArray(Config.FAVUSER);
                        for (int i = 0; i < favouriteArray.length(); i++) {
                            JSONObject carrierObj = favouriteArray.getJSONObject(i);
                            FavouriteGroupModel fav = new FavouriteGroupModel();
                            fav.setId(carrierObj.getString(Keys.ID));
                            fav.setName(carrierObj.getString(Keys.NAME));
                            fav.setUser_id(carrierObj.getString(Keys.KEY_USER_ID));
                            //fav.setApp_id(carrierObj.getString(Keys.APP_ID));
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
                    }
                }
            } else if (outJson.getInt(Config.STATUS) == APIStatus.UNPROCESSABLE) {
                if (!outJson.getString(Config.MESSAGE).contentEquals("")) {
                    if (type == 0) {
                        // if (outJson.getString(Config.MESSAGE).contentEquals("No data found")) {
                        page = 1;
                        loadingMore = false;
                        favourite.clear();
                        adapter.notifyDataSetChanged();
                        list.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                        //  }
                    }
                    //    AlertManager.messageDialog(getActivity(), "Alert!", outJson.getString(Config.MESSAGE));
                } else
                    AlertManager.messageDialog(getActivity(), "Alert!", outJson.getJSONArray(Config.DATA).getJSONObject(0).optString(Config.MESSAGE));
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
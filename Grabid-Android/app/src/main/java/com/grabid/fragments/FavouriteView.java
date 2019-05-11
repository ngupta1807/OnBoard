package com.grabid.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.grabid.BuildConfig;
import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.api.Config;
import com.grabid.common.SessionManager;
import com.grabid.models.UserInfo;

/**
 * Created by graycell on 27/12/17.
 */

public class FavouriteView extends Fragment {
    WebView webview;
    SessionManager session;
    UserInfo userInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favouritelocations, null);
        init(view);
        return view;

    }

    public void init(View view) {
        session = new SessionManager(getActivity());
        userInfo = session.getUserDetails();
        webview = (WebView) view.findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new MyWebViewClient());
        openURL();
    }

    private void openURL() {
        //  String url = "http://dev.grabid.com.au/chauffer/web/favourite-user/map/?app_token=" + userInfo.getApp_token();
        if (BuildConfig.logistic)
            webview.loadUrl(Config.BASE_URL + "/logistics/favourite-user/map/?app_token=" + userInfo.getApp_token());
        else
            webview.loadUrl(Config.BASE_URL + "/chauffeur/favourite-user/map/?app_token=" + userInfo.getApp_token());
        webview.requestFocus();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        HomeActivity.addmore.setVisibility(View.GONE);

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}

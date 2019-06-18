package com.contentblocker;

import android.graphics.drawable.Drawable;

/**
 * Created by graycell on 5/30/2017.
 */
public class AppListModel {

    String appName;
    String appPackage;
    Drawable imageLogo;

    public AppListModel(String appName, String appPackage, Drawable imageLogo) {
        this.appName = appName;
        this.appPackage = appPackage;
        this.imageLogo = imageLogo;
    }

    public Drawable getImageLogo() {
        return imageLogo;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppPackage() {
        return appPackage;
    }
}

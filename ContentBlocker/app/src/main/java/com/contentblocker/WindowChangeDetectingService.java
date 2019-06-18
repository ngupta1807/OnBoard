package com.contentblocker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by graycell on 6/15/2017.
 */
public class WindowChangeDetectingService extends AccessibilityService {

    ArrayList<String> blockList = new ArrayList<String>();

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        //Configure these here for compatibility with API 13 and below.
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        if (Build.VERSION.SDK_INT >= 16)
            //Just in case this helps
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.getPackageName() != null && event.getClassName() != null) {
                ComponentName componentName = new ComponentName(
                        event.getPackageName().toString(),
                        event.getClassName().toString()
                );
                blockList = getPreferences(getApplicationContext());

                ActivityInfo activityInfo = tryGetActivity(componentName);
                boolean isActivity = activityInfo != null;
                //if (isActivity)

                String pkg_name = componentName.getPackageName();
                Log.i("CurrentActivity:",""+ pkg_name);
                if (blockList != null) {
                    for(int i=0;i<blockList.size();i++){
                        if (blockList.get(i).equals(pkg_name)) {
                            Log.i("CurrentActivity:","found");
                            startActivity(new Intent(WindowChangeDetectingService.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY));
                            break;
                        }else{
                            Log.i("CurrentActivity","Not found");
                        }
                    }
                }
            }
        }
    }

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public void onInterrupt() {
    }

    public static ArrayList<String> getPreferences(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String json = sp.getString("blocklist", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> arrayList = gson.fromJson(json, type);

        return arrayList;
    }
}
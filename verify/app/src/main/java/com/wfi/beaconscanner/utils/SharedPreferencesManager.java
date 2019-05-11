package com.wfi.beaconscanner.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created : Nisha Developed by : GraycellTechnologies
 * Used : To save/get data of local variable.
 */
public class SharedPreferencesManager {
    Context ctx;

    SharedPreferences prefs;

    public SharedPreferencesManager(Context ctx) {
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences(ctx.getPackageName(),
                Context.MODE_PRIVATE);
    }

    public void saveLongValues(String key, Long value) {
        Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public Long getLongValues(String key) {
        if (key.equals("difference")) {
            return prefs.getLong(key, 0l);
        }
        return prefs.getLong(key, Long.valueOf("01234"));
    }

    public void saveStringValues(String key, String value) {
        Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void saveIntValues(String key, int value) {
        Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getIntValues(String key) {
        return prefs.getInt(key, 1);
    }

    public String getStringValues(String key) {

        return prefs.getString(key, "");

    }

}

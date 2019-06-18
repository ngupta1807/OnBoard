package com.contentblocker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class AppListActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<AppListModel> appList = new ArrayList<AppListModel>();
    public static ArrayList<String> blockList = new ArrayList<String>();
    String packageName, appName;
    Drawable logo;
    ListView list;
    Button block;
    AppListAdapter adapter;
    PolicyManager policyManager;

    @Override
    protected void onResume() {
        super.onResume();

       /* //activate device admin to restrict user from app uninstall
        if (isDeviceAdminActive()) {
            //activate app accessibility feature to trace other apps foreground status
            if (isAccessibilityEnabled(this, "com.contentblocker/.WindowChangeDetectingService")) {
                adapter.notifyDataSetChanged();
            } else {
                showDialog();
            }
        } else {
            ActivateDeviceAdmin();
        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applist);

        list = (ListView) findViewById(R.id.appsList);
        block = (Button) findViewById(R.id.block);
        getInstalledAppsList();
        adapter = new AppListAdapter(this, appList, blockList);
        list.setAdapter(adapter);

        policyManager = new PolicyManager(this);
        block.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.block) {
            /*if (!blockList.contains("com.android.settings")) {
                blockList.add("com.android.settings");
            }*/
            savePreferences(AppListActivity.this, "blocklist", blockList);
            if (isDeviceAdminActive()) {
                //activate app accessibility feature to trace other apps foreground status
                /*if (isAccessibilityEnabled(this, "com.android.settings/.WindowChangeDetectingService")) {
                    adapter.notifyDataSetChanged();
                } else {
                    showDialog();
                }*/
            } else {
                ActivateDeviceAdmin();
            }
            Log.v("blockList","blockList::"+blockList);
                startBlockService(); //start apps foreground tracking service
            }
    }

    /*****
     * get list of installed apps in phone
     *****/
    public void getInstalledAppsList() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);

        for (int i = 0; i < pkgAppsList.size(); i++) {
            packageName = pkgAppsList.get(i).activityInfo.packageName;
            Log.v("AppsList", "PackageName : " + packageName);
            Log.v("AppsList", "AppsName : " + pkgAppsList.get(i).activityInfo.name);
            PackageManager packageManager = getApplicationContext().getPackageManager();

            try {
                appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
                logo = packageManager.getApplicationIcon(packageName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            appList.add(new AppListModel(appName, packageName, logo));
         }
    }

    public static boolean isAccessibilityEnabled(Context context, String id) {

       /* AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo service : runningServices) {
            if (id.equals(service.getId())) {
                return true;
            }
        }*/

        return false;
    }

/*    public static void logInstalledAccessiblityServices(Context context) {

        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getInstalledAccessibilityServiceList();
        for (AccessibilityServiceInfo service : runningServices) {
            Log.i("service ID", service.getId());
        }
    }*/

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder((this));
        builder.setCancelable(false);
        builder.setTitle("Alert!");
        builder.setMessage("Please ON Accessbility Settings for ContentBlocker");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              /*  Settings.Secure.putString(getContentResolver(),
                        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                        "package_name");*/
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(callGPSSettingIntent, 1);
            }
        });
        builder.show();
    }

    public void startBlockService() {
        startService(new Intent(this, WindowChangeDetectingService.class));
    }


    public static void savePreferences(Context context, String key,
                                       ArrayList<String> value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();

        String json = gson.toJson(value);
        editor.putString(key, json);
        editor.apply();
    }

    public void ActivateDeviceAdmin() {
        Intent activateDeviceAdmin = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        activateDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                policyManager.getAdminComponent());
        activateDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "After activating admin, you will be able to block application uninstallation.");
        startActivityForResult(activateDeviceAdmin,
                PolicyManager.DPM_ACTIVATION_REQUEST_CODE);

    }

    public boolean isDeviceAdminActive() {
        if (policyManager.isAdminActive()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == Activity.RESULT_OK && requestCode == PolicyManager.DPM_ACTIVATION_REQUEST_CODE) {
            // handle code for successfull enable of admin
            // startService(new Intent(this, MasterService.class));
            Log.v("Device Admin ", "Activated");
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            Log.v("Device Admin ", "Deactivated");
        }

    }
}


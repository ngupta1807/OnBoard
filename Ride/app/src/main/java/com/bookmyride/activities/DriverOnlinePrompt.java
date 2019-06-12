package com.bookmyride.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.bookmyride.R;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.api.ServiceHandlerInBack;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.fcm.AlarmReceiver;
import com.bookmyride.models.DriverCategory;
import com.bookmyride.views.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vinod on 2017-01-21.
 */
public class DriverOnlinePrompt extends AppCompatActivity {
    SessionHandler session;
    Timer timer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        super.onCreate(savedInstanceState);
        session = new SessionHandler(this);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                goOffline();
            }
        }, 15 * 60 * 1000);
        showNoActionPrompt();
    }

    Toast mToast;
    AlertDialog noActionDialog;

    private void showNoActionPrompt() {
        noActionDialog = new AlertDialog(DriverOnlinePrompt.this, true);
        noActionDialog.setDialogTitle("Alert!");
        noActionDialog.setDialogMessage("You haven't engaged with BookMyRide for 30 minutes. Click on GO OFFLINE if you don't want to get any rides now.");
        noActionDialog.setNegativeButton("STAY ONLINE",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //timerCount = 0;
                        if (timer != null)
                            timer.cancel();
                        noActionDialog.dismiss();
                        DriverOnlinePrompt.this.finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
        noActionDialog.setPositiveButton("GO OFFLINE",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //noActionDialog.dismiss();
                        if (timer != null)
                            timer.cancel();
                        goOffline();
                    }
                });
        noActionDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null)
            timer.cancel();
        DriverOnlinePrompt.this.finish();
    }

    @Override
    protected void onDestroy() {
        if (timer != null)
            timer.cancel();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void goOffline() {
        DriverCategory dc = session.getOnlineDriverData();
        HashMap<String, String> params = new HashMap<>();
        params.put(Key.STATUS, "0");
        params.put("driverCategory_id", dc.getDriverCateogry());
        params.put(Key.VEHICLE_NUM, dc.getVehicleNum());
        params.put(Key.VEHICLE_ID, dc.getVehicleId());

        if (Internet.hasInternet(this)) {
            ServiceHandlerInBack apiHandler = new ServiceHandlerInBack(this, HTTPMethods.PUT, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    try {
                        JSONObject outJson = new JSONObject(result);
                        if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                            if (noActionDialog != null && noActionDialog.isShowing())
                                noActionDialog.dismiss();
                            session.saveOnlineStatus(false);
                            session.saveDriverDategory("", "", "");
                            stopAlarm();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    displayDriverMap(new Bundle());
                                    DriverOnlinePrompt.this.finish();
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                }
                            });
                        } else {
                            Alert("Alert!", outJson.getString(Key.MESSAGE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, params);
            apiHandler.execute(Config.DRIVER_AVAILABLITY + "0", session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void displayDriverMap(Bundle bundle) {
        Fragment fragment = new com.bookmyride.fragments.DriverDashboard();
        fragment.setArguments(bundle);
        FragmentTransaction ft = DriverHome.fm.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commitAllowingStateLoss();
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(DriverOnlinePrompt.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
        mDialog.show();
    }

    private void stopAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this,
                1, intent, 0);

        // And cancel the alarm.
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert am != null;
        am.cancel(sender);

        // Tell the user about what we did.
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, "Repeating Alarm Unscheduled.", Toast.LENGTH_LONG);
        //mToast.show();
    }
}
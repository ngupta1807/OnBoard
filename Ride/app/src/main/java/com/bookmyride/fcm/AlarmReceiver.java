package com.bookmyride.fcm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.bookmyride.R;
import com.bookmyride.activities.DriverOnlinePrompt;
import com.bookmyride.activities.SplashActivity;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.api.ServiceHandlerInBack;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.models.DriverCategory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vinod on 2/6/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {
    Toast mToast;
    SessionHandler session;
    public static Timer timer;
    @Override
    public void onReceive(Context context, Intent intent) {
        session = new SessionHandler(context);
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(context, "Alarm Received.", Toast.LENGTH_LONG);
        //mToast.show();
        if (!NotificationUtils.isAppIsInBackground(context)) {
            context.startActivity(new Intent(context, DriverOnlinePrompt.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            session.saveDriverDialogOpen(true);
            createLocalNotification(context);
            playNotificationSound(context);
        }
    }

    private void createLocalNotification(final Context context) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                goOffline(context);
            }
        }, 15 * 60 * 1000);
        Intent notificationIntent = new Intent(context, SplashActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(SplashActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        String message = "You haven't engaged with BookMyRide for 30 minutes. Click on GO OFFLINE if you don't want to get any rides now.";
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Notification notification = builder.setSmallIcon(R.drawable.app_icon_new).setTicker("BookMyRide").setWhen(0)
                .setAutoCancel(true)
                .setContentTitle("BookMyRide")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentText(message).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    public void playNotificationSound(Context ctx) {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + ctx.getPackageName() + "/raw/ring");
            //Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(ctx, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goOffline(final Context context) {
        DriverCategory dc = session.getOnlineDriverData();
        HashMap<String, String> params = new HashMap<>();
        params.put(Key.STATUS, "0");
        params.put("driverCategory_id", dc.getDriverCateogry());
        params.put(Key.VEHICLE_NUM, dc.getVehicleNum());
        params.put(Key.VEHICLE_ID, dc.getVehicleId());
        ServiceHandlerInBack apiHandler = new ServiceHandlerInBack(context, HTTPMethods.PUT, new AsyncTaskCompleteListener() {
            @Override
            public void onTaskComplete(String result) {
                try {
                    JSONObject outJson = new JSONObject(result);
                    if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                        session.saveOnlineStatus(false);
                        session.saveDriverDialogOpen(false);
                        session.saveDriverDategory("", "", "");
                        stopAlarm(context);
                        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
        apiHandler.execute(Config.DRIVER_AVAILABLITY + "0", session.getToken());
    }
    private void stopAlarm(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context,
                1, intent, 0);

        // And cancel the alarm.
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert am != null;
        am.cancel(sender);

        // Tell the user about what we did.
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, "Repeating Alarm Unscheduled.", Toast.LENGTH_LONG);
        //mToast.show();
    }
}

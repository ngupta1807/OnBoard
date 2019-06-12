package com.bookmyride.fcm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.bookmyride.R;
import com.bookmyride.activities.BeginRide;
import com.bookmyride.activities.BookingTimer;
import com.bookmyride.activities.DriverArrived;
import com.bookmyride.activities.EndRide;
import com.bookmyride.activities.FareBreakdown;
import com.bookmyride.activities.FareBreakup;
import com.bookmyride.activities.RatingToPassenger;
import com.bookmyride.activities.RideRequestList;
import com.bookmyride.activities.SplashActivity;
import com.bookmyride.activities.TrackDetail;
import com.bookmyride.api.Config;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.fragments.AcceptPayment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by vinod on 01/17/2017.
 */
public class RideFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = RideFirebaseMessagingService.class.getSimpleName();
    private static final String NOTIFICATION_ID_KEY = "NOTIFICATION_ID";
    private static final String CALL_SID_KEY = "CALL_SID";
    NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Log.e("notification", remoteMessage.getNotification().getBody());
        if (remoteMessage == null)
            return;
        handleDataMessage(remoteMessage.getData());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void handleDataMessage(Map<String, String> json) {
        Log.e("notification", json.toString());
        JSONObject rideData = new JSONObject();
        /*try {
            rideData.put("message", message);
        } catch (JSONException e){
            e.printStackTrace();
        }*/
        for (Map.Entry<String, String> entry : json.entrySet()) {
            //Log.d("push", entry.getKey() + "-" + entry.getValue());
            if (entry.getKey().equals("bookingId")) {
                new SessionHandler(getApplicationContext()).saveCurrentBookingId(entry.getValue());
            }
            try {
                rideData.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            String status = "";
            try {
                if (rideData.has("status"))
                    status = rideData.getString("status");
                if (new SessionHandler(getApplicationContext()).getUserType().equals("4") && (status.equals("") || status.equals("0")))
                    new SessionHandler(getApplicationContext()).saveRideData(rideData.toString());
                Log.e("rideData-not", new SessionHandler(getApplicationContext()).getRideData());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            createNotification("BookMyRide", rideData);
            playNotificationSound();
        } else {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            boolean driverOnline = cn.getClassName().equalsIgnoreCase("com.ride247app.activities.DriverMapActivity");
            if (new SessionHandler(getApplicationContext()).getUserType().equals("4")) {
                if (cn.getClassName().equalsIgnoreCase("com.bookmyride.activities.RideRequestList")
                        || cn.getClassName().equalsIgnoreCase("com.bookmyride.activities.BeginRide")
                        || cn.getClassName().equalsIgnoreCase("com.bookmyride.activities.DriverArrived")) {
                    driverOnline = false;
                } else {
                    driverOnline = true;
                    /*Fragment currentFragment = DriverHome.fm.findFragmentById(R.id.content_frame);
                    if (currentFragment instanceof DriverMapActivity) {
                        driverOnline = true;
                    } else driverOnline = false;*/
                }
            } else driverOnline = false;
            if (new SessionHandler(getApplicationContext()).getUserType().equals("4")) {
                boolean rideRequestScreen = cn.getClassName().equalsIgnoreCase("com.ride247app.activities.RideRequestList");
                String status = "";
                String type = "";
                String confirmation = "";
                String msg = "";
                try {
                    if (rideData.has("status"))
                        status = rideData.getString("status");
                    if (rideData.has("type"))
                        type = rideData.getString("type");
                    if (rideData.has("confirmation"))
                        confirmation = rideData.getString("confirmation");
                    if (rideData.has("message"))
                        msg = rideData.getString("message");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("status", status);
                Log.e("confirmation", confirmation);
                Log.e("driverOnline", "" + driverOnline);
                Log.e("type", "" + type);
                if (status.equals("302")) {
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("rideData", rideData.toString());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                    playNotificationSound();
                } else if (status.equals("202")) {
                    Intent pushNotification = new Intent(NotificationFilters.PAYMENT_REQEUST);
                    pushNotification.putExtra("rideData", rideData.toString());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                    playNotificationSound();
                } else if (driverOnline /*&& type.equals("0")*/ && !status.equals("204") && confirmation.equals("")) {
                    Log.e("online", "IN");
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("rideData", rideData.toString());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                    //playNotificationSound();
                } else {
                    Log.e("req", msg);
                    if (msg.startsWith("New booking") || msg.startsWith("Newbooking") || (type.equals("1") && !confirmation.equals(""))) {
                        Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                        pushNotification.putExtra("rideData", rideData.toString());
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                        playNotificationSound();
                    } /*else if (rideRequestScreen && status.equals("204")) {
                        Intent pushNotification = new Intent(NotificationFilters.REQUEST_CANCELLED);
                        pushNotification.putExtra("rideData", rideData.toString());
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                        playNotificationSound();
                    } */ else if (status.equals("0") || status.equals("204")) {
                        Intent pushNotification = new Intent(NotificationFilters.REQUEST_CANCELLED);
                        pushNotification.putExtra("rideData", rideData.toString());
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                        playNotificationSound();
                    }
                }
            } else {
                boolean trackScreen = cn.getClassName().equalsIgnoreCase("com.ride247app.activities.TrackDetail");
                String status = "";
                String statusCode = "";
                try {
                    if (rideData.has("status"))
                        status = rideData.getString("status");
                    if (rideData.has("statusCode"))
                        statusCode = rideData.getString("statusCode");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status.equals("202")) {
                    Intent pushNotification = new Intent(NotificationFilters.PAYMENT_REQEUST);
                    pushNotification.putExtra("rideData", rideData.toString());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                    playNotificationSound();
                } else if ((status.equals("204") || statusCode.equals("204")) && trackScreen) {
                    Intent pushNotification = new Intent(NotificationFilters.REQUEST_CANCELLED);
                    pushNotification.putExtra("rideData", rideData.toString());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                    playNotificationSound();
                } else if ((status.equals("3") || status.equals("4") ||
                        status.equals("5") || status.equals("6")) && trackScreen) {
                    Intent pushNotification = new Intent(NotificationFilters.UPDATE_STATUS);
                    pushNotification.putExtra("rideData", rideData.toString());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                    playNotificationSound();
                } else {
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("rideData", rideData.toString());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                    playNotificationSound();
                }
            }
        }
    }

    private void createNotification(String title, JSONObject rideData) {
        String status = "";
        String bookingID = "";
        String type = "";
        String driverCategory = "";
        String driverId = "";
        String confirmation = "";
        String asap = "";
        try {
            Intent intent = new Intent(this, SplashActivity.class);
            if (rideData.has("status"))
                status = rideData.getString("status");
            if (rideData.has("type"))
                type = rideData.getString("type");
            if (rideData.has("bookingId"))
                bookingID = rideData.getString("bookingId");
            if (rideData.has("driverCategory_id"))
                driverCategory = rideData.getString("driverCategory_id");
            if (rideData.has("driver_id"))
                driverId = rideData.getString("driver_id");
            if (rideData.has("confirmation"))
                confirmation = rideData.getString("confirmation");
            if (rideData.has("asap_pickup"))
                asap = rideData.getString("asap_pickup");

            if (new SessionHandler(getApplicationContext()).getUserType().equals("4")) {
                if (status.equals("302")) {
                    intent = new Intent(this, SplashActivity.class);
                    intent.putExtra("rideData", rideData.toString());
                } else if (type.equals("0") && !status.equals("204")) {
                    intent = new Intent(this, RideRequestList.class);
                    intent.putExtra("rideData", rideData.toString());
                } else if (type.equals("1") && confirmation.equalsIgnoreCase("driver")) {
                    //Handle Confirmation notification by driver
                    intent = new Intent(this, SplashActivity.class);
                    intent.putExtra("rideData", rideData.toString());
                } else if (type.equals("1") && status.equals("204")) {
                    intent = new Intent(this, SplashActivity.class);
                    intent.putExtra("rideData", rideData.toString());
                } else if (type.equals("1") && driverId.equals("")) {
                    intent = new Intent(this, RideRequestList.class);
                    intent.putExtra("rideData", rideData.toString());
                } else if (type.equals("1") && !driverId.equals("")) {
                    intent = new Intent(this, SplashActivity.class);
                    intent.putExtra("rideData", rideData.toString());
                } else if (status.equals("1")) {
                    intent = new Intent(this, DriverArrived.class);
                    //intent = new Intent(this, StartRiding.class);
                    intent.putExtra("bookingId", bookingID);
                    intent.putExtra("type", rideData.getString("type"));
                    intent.putExtra("payment_status", rideData.getString("paymentStatus"));
                    intent.putExtra("pickUp", rideData.getString("pickUp"));
                    intent.putExtra("driverCategory", rideData.getString("driverCategory_id"));
                    intent.putExtra("dropOff", rideData.getString("dropOff"));
                } else if (status.equals("12")) {
                    intent = new Intent(this, DriverArrived.class);
                    intent.putExtra("bookingId", bookingID);
                    intent.putExtra("type", rideData.getString("type"));
                    intent.putExtra("payment_status", rideData.getString("paymentStatus"));
                    intent.putExtra("pickUp", rideData.getString("pickUp"));
                    intent.putExtra("driverCategory", rideData.getString("driverCategory_id"));
                    intent.putExtra("dropOff", rideData.getString("dropOff"));
                } else if (status.equals("3")) {
                    intent = new Intent(this, BeginRide.class);
                    intent.putExtra("bookingId", bookingID);
                    intent.putExtra("type", rideData.getString("type"));
                    intent.putExtra("payment_status", rideData.getString("paymentStatus"));
                    intent.putExtra("pickUp", rideData.getString("pickUp"));
                    intent.putExtra("driverCategory", rideData.getString("driverCategory_id"));
                    intent.putExtra("dropOff", rideData.getString("dropOff"));
                } else if (status.equals("4")) {
                    intent = new Intent(this, EndRide.class);
                    intent.putExtra("bookingId", bookingID);
                    intent.putExtra("type", rideData.getString("type"));
                    intent.putExtra("payment_status", rideData.getString("paymentStatus"));
                    intent.putExtra("pickUp", rideData.getString("pickUp"));
                    intent.putExtra("driverCategory", rideData.getString("driverCategory_id"));
                    intent.putExtra("dropOff", rideData.getString("dropOff"));
                } else if (status.equals("5")) {
                    intent = new Intent(this, AcceptPayment.class);
                    intent.putExtra("bookingId", bookingID);
                    intent.putExtra("type", rideData.getString("type"));
                } else if (status.equals("0") || status.equals("204")) {
                    intent = new Intent(this, SplashActivity.class);
                    intent.putExtra("rideData", rideData.toString());
                } else if (status.equals("202")) {
                    intent = new Intent(this, RatingToPassenger.class);
                    intent.putExtra("show", "");
                    intent.putExtra("bookingId", bookingID);
                    intent.putExtra("rideData", rideData.toString());
                }
            } else if (new SessionHandler(getApplicationContext()).getUserType().equals("3")) {
                if (type.equalsIgnoreCase("0") && !status.equals("202")) {
                    if (BookingTimer.timer != null) {
                        BookingTimer.timer.finish();
                    }
                    intent = new Intent(this, TrackDetail.class);
                    intent.putExtra("status", status);
                    intent.putExtra("bookingID", bookingID);
                    intent.putExtra("driverCategory", driverCategory);
                } else if (type.equalsIgnoreCase("1") && confirmation.equalsIgnoreCase("passenger")) {
                    if (BookingTimer.timer != null) {
                        BookingTimer.timer.finish();
                    }
                    //Handle confirmation notification by passenger.
                    intent = new Intent(this, SplashActivity.class);
                    intent.putExtra("rideData", rideData.toString());
                } else if (type.equalsIgnoreCase("1") && asap.equalsIgnoreCase("0")) {
                    intent = new Intent(this, SplashActivity.class);
                    intent.putExtra("rideData", rideData.toString());
                } else if (status.equalsIgnoreCase("4") || status.equalsIgnoreCase("3")) {
                    intent = new Intent(this, TrackDetail.class);
                    intent.putExtra("status", status);
                    intent.putExtra("bookingID", bookingID);
                    intent.putExtra("driverCategory", driverCategory);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                } else if (status.equalsIgnoreCase("5") || status.equalsIgnoreCase("6")) {
                    intent = new Intent(this, FareBreakup.class);
                    intent.putExtra("status", status);
                    intent.putExtra("bookingID", bookingID);
                    intent.putExtra("driverCategory", driverCategory);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                } else if (status.equals("202")) {
                    intent = new Intent(this, FareBreakdown.class);
                    intent.putExtra("bookingId", bookingID);
                } else if (status.equals("204")) {
                    intent = new Intent(this, SplashActivity.class);
                    intent.putExtra("bookingId", bookingID);
                } else {
                    intent = new Intent(this, SplashActivity.class);
                }
            } else {
                intent = new Intent(this, SplashActivity.class);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent resultIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            Notification notification = mBuilder.setSmallIcon(R.drawable.app_icon_new).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(rideData.getString("message")))
                    .setContentIntent(resultIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentText(rideData.getString("message")).build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void playNotificationSound() {
        /*try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + getPackageName() + "/raw/ring");
            //Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(this, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
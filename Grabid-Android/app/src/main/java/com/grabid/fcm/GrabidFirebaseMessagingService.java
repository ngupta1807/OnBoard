package com.grabid.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.grabid.R;
import com.grabid.activities.DialogActivity;
import com.grabid.activities.HomeActivity;
import com.grabid.activities.Splash;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.models.BankDetail;
import com.grabid.models.Card;

import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

import me.leolin.shortcutbadger.ShortcutBadger;


/**
 * Created by vinod on 12/19/2016.
 */
public class GrabidFirebaseMessagingService extends FirebaseMessagingService implements AsyncTaskCompleteListener {
    // private static final String TAG = GrabidFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;
    private static final String TAG = "Hello World";
    SessionManager sessionManager;
    String type = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "Data: " + remoteMessage.getData());
        String data = remoteMessage.getData().toString();
        sessionManager = new SessionManager(getApplicationContext());
        Map<String, String> json = remoteMessage.getData();
        String body = json.get("body");
        if (remoteMessage == null)
            return;


        if (remoteMessage.getData().size() > 0) {
            /*if (json.get("type").equals("61") || json.get("type").equals("62") ){
                handleNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(),json.get("type"));
            }
            else*/
            String type = "";
            if (json.containsKey("type"))
                type = json.get("type");
            if (type != null && !type.contentEquals("") && (type.equals("74") || (type.equals("75")))) {
                if (type.contentEquals("74")) {
                    if (NotificationUtils.isAppIsInBackground(getApplicationContext()) || !HomeActivity.IsCreditCard) {
                        getSavedCreditCard();
                    } else
                        sendCreditInfo();
                }
                if (type.contentEquals("75")) {
                    if (NotificationUtils.isAppIsInBackground(getApplicationContext()) || !HomeActivity.IsBankDetail) {
                        getBankInfo();
                    } else
                        sendBankInfo();
                }
            } else if (type != null && !type.contentEquals("") && (type.equals("70"))) {
                showDeliveryInfo(json.get("type"), json.get("delivery_id"));
            } else if (type != null && !type.contentEquals("") && (type.equals("55"))) {
                if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                    sessionManager.saveRatings(json.get("shipper_rating"), json.get("driver_rating"));
                } else
                    sendBroadcast(json.get("shipper_rating"), json.get("driver_rating"));
                handleDataMessage(remoteMessage.getData());
            } else {
                handleDataMessage(remoteMessage.getData());
                Log.d("handlecalleddata", "handleNotification");
            }

        } else if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
        try {
            if (json.containsKey("type")) {
                String type = json.get("type");
                if (type != null && type.equals("33"))
                    sessionManager.saveAdminStatus("1");
                else if (type != null && type.equals("34"))
                    sessionManager.saveEmailStatus("1");
            }
        } catch (Exception e) {

        }
    }

    private void getBankInfo() {
        this.type = "GET_BANK_CARD";
        String url = Config.SERVER_URL + Config.BANK_DETAIL + "/0";
        if (Internet.hasInternet(getApplicationContext())) {
            RestAPICall mobileAPI = new RestAPICall(getApplicationContext(), HTTPMethods.GET, this, null, false);
            mobileAPI.execute(url, sessionManager.getToken());
        }
    }

    private void getSavedCreditCard() {
        this.type = "GET_CREDIT_CARD";
        String url = Config.SERVER_URL + Config.CREDIT_CARD + "/0";
        if (Internet.hasInternet(getApplicationContext())) {
            RestAPICall mobileAPI = new RestAPICall(getApplicationContext(), HTTPMethods.GET, this, null, false);
            mobileAPI.execute(url, sessionManager.getToken());
        }
    }

    public void sendBankInfo() {
        Intent intent = new Intent("getbankinfo");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void sendCreditInfo() {
        Intent intent = new Intent("getcreditinfo");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void sendBroadcast(String sRating, String dRating) {
        Intent intent = new Intent("sendRatings");
        intent.putExtra("shipperRating", sRating);
        intent.putExtra("driverRating", dRating);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void sendBroadcast(String count) {
        Intent intent = new Intent("sendCount");
        intent.putExtra("count", count);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleNotification(String title, String message) {
        Log.v("handlenotificaioncalled", "not deliveryid");
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            Intent resultIntent = new Intent(this, DialogActivity.class);
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("message", message);
            resultIntent.putExtra("delivery_id", "");
            resultIntent.putExtra("type", "");
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(resultIntent);
            playNotificationSound();
        } else {
            createNotification(title, message, "", "", "");
            playNotificationSound();
        }
    }

    private void handleNotification(String title, String message, String type) {
        Log.v("handlenotificaioncalled", "not deliveryid");
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            Intent resultIntent = new Intent(this, DialogActivity.class);
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("message", message);
            resultIntent.putExtra("delivery_id", "");
            resultIntent.putExtra("type", type);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(resultIntent);
            playNotificationSound();
        } else {
            createNotification(title, message, "", "", "");
            playNotificationSound();
        }
    }

    private void showDeliveryInfo(String type, String deliveryID) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext()) && HomeActivity.IsDeliveryInfo) {
            Intent intent = new Intent("updatedelivery");
            intent.putExtra("delivery_id", deliveryID);
            intent.putExtra("type", type);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

           /* Intent intent;
            intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra("delivery_id", deliveryID);
            intent.putExtra("type", type);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
        }
    }

    private void handleDataMessage(Map<String, String> json) {
        try {
            final String type = json.get("type");
            final String deliveryID = json.get("delivery_id");
            final String title = json.get("title");
            final String body = json.get("body");
            String count = "";
            try {
                count = json.get("countNotification");
                sessionManager.saveCount(count);
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext()))
                    sendBroadcast(count);
            } catch (Exception e) {
                e.toString();
            }
            String imageUrl = "";
            String timestamp = "";
            Log.e(TAG, "type: " + type);
            Log.e(TAG, "deliveryID: " + deliveryID);
            Log.e(TAG, "title: " + title);
            Log.e(TAG, "body: " + body);
            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                Log.v("foreground", "foreground");
                playNotificationSound();
                Intent resultIntent = new Intent(this, DialogActivity.class);
                resultIntent.putExtra("title", title);
                resultIntent.putExtra("message", body);
                resultIntent.putExtra("delivery_id", deliveryID);
                resultIntent.putExtra("type", type);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(resultIntent);
            } else {

                if (type != null && !type.contentEquals("") && (type.equals("62")))
                    createNotification(title, title, type, deliveryID, count);
                else
                    createNotification(title, body, type, deliveryID, count);
                playNotificationSound();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
    public static final String NOTIFICATION_CHANNEL_ID = "10002";
    private void createNotification(String title, String messageBody, String type, String deliveryID, String count) {
        Intent intent;
        if (type != null && (type.contentEquals("66"))) {
            intent = new Intent(getApplicationContext(), HomeActivity.class);
            // String xId = "456";
            String no = new Random().nextInt(50) + "_action";
            intent.setAction(no);
            intent.putExtra("title", title);
            intent.putExtra("message", messageBody);
            intent.putExtra("type", type);
            intent.putExtra("creditcarddecline", type);
        } else if (TextUtils.isEmpty(deliveryID) || (type != null && type.contentEquals("65")))
            intent = new Intent(this, Splash.class);
        else {
            intent = new Intent(getApplicationContext(), HomeActivity.class);
            // String xId = "456";
            String no = new Random().nextInt(50) + "_action";
            intent.setAction(no);
            intent.putExtra("title", title);
            intent.putExtra("message", messageBody);
            intent.putExtra("delivery_id", deliveryID);
            intent.putExtra("type", type);
            if (type != null && (type.contentEquals("64")))
                intent.putExtra("dialogactivity", type);

        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent resultIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true).setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(messageBody))
                .setSound(notificationSoundURI)
                .setContentIntent(resultIntent).setNumber(10);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert notificationManager != null;
            mNotificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(0, mNotificationBuilder.build());
        if (count != null && !count.contentEquals(""))
            try {
                int countint = Integer.parseInt(count);
                ShortcutBadger.applyCount(getApplicationContext(), countint);
                Log.v("count", count);
            } catch (Exception e) {
                e.toString();
            }

    }

    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + getPackageName() + "/raw/ring");
            //Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(this, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskComplete(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                if (type.contentEquals("GET_BANK_CARD")) {
                    JSONObject innerObject = outJson.getJSONObject(Config.DATA);
                    if (innerObject.get(Config.BANKDETAIL) instanceof JSONObject) {
                        JSONObject cardObj = innerObject.getJSONObject(Config.BANKDETAIL);
                        BankDetail card = new BankDetail();
                        card.setId(cardObj.getString(Keys.KEY_ID));
                        card.setUserID(cardObj.getString(Keys.KEY_USER_ID));
                        card.setHolderName(cardObj.getString(Keys.HOLDER_NAME));
                        card.setBankName(cardObj.getString(Keys.BANK_NAME));
                        card.setBranchCode(cardObj.getString(Keys.BRANCH_CODE));
                        //card.setHashNumber(cardObj.getString(Keys.HASH_ACCOUNT_NUMBER));
                        //card.setSwiftCode(cardObj.getString(Keys.SWIFT_CODE));
                        card.setAccountNumber(cardObj.getString(Keys.ACCOUNT_NUMBER));

                        try {
                            sessionManager.saveBankDetail(outJson.optJSONObject(Config.DATA).toString());
                        } catch (Exception e) {
                            e.toString();
                        }
                    }

                } else if (type.equals("GET_CREDIT_CARD")) {
                    JSONObject innerObject = outJson.getJSONObject(Config.DATA);
                    JSONObject cardObj = innerObject.getJSONObject(Config.CREDITCARD);
                    Card card = new Card();
                    card.setId(cardObj.getString(Keys.KEY_ID));
                    card.setNameOnCard(cardObj.getString(Keys.NAME_ON_CARD));
                    card.setCardNumber(cardObj.getString(Keys.CARD_NUMBER));
                    card.setCardType(cardObj.getString(Keys.CARD_TYPE));
                    card.setExpiry(cardObj.getString(Keys.EXPIRY));
                    card.setCvv("000");
                    card.setCardToken(cardObj.getString(Keys.CARD_TOKEN));
                    try {
                        sessionManager.saveCreditCard(outJson.optJSONObject(Config.DATA).toString());
                    } catch (Exception e) {
                        e.toString();
                    }
                }
            }
        } catch (Exception e) {
            e.toString();
        }
    }
}
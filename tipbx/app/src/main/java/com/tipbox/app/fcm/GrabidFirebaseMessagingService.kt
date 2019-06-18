package com.tipbox.app.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tipbox.app.MainActivity
import com.tipbox.app.R


/**
 * Created by nisha
 */
class GrabidFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d(TAG, "From: " + remoteMessage!!.data)
        handleNotification(remoteMessage!!.data.get("body"))
    }
    private fun handleNotification( message: String?) {
        if (!NotificationUtils.isAppIsInBackground(applicationContext)) {
            /*var broadcaster = LocalBroadcastManager.getInstance(getBaseContext());
            var intent =  Intent("001");
            intent.putExtra("data",message)
            broadcaster.sendBroadcast(intent);*/
            createNotification( message)
        } else {
            createNotification(message)
        }
    }

    private fun createNotification( messageBody: String?) {
        val intent: Intent
        intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("activity", "6")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        val resultIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val mNotificationBuilder = NotificationCompat.Builder(this)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mNotificationBuilder.setSmallIcon(com.tipbox.app.R.mipmap.app_icon_t)
            mNotificationBuilder.setColor(getResources().getColor(R.color.colorPrimary))
            mNotificationBuilder.setContentTitle(getString(com.tipbox.app.R.string.app_name))
        }else{
            mNotificationBuilder.setSmallIcon(com.tipbox.app.R.mipmap.app_icon_t)
            mNotificationBuilder.setColor(getResources().getColor(R.color.colorPrimary))
            //mNotificationBuilder.setContentTitle(getString(com.tipbox.app.R.string.app_name))
        }
        mNotificationBuilder
           // .setContentTitle(getString(com.tipbox.app.R.string.app_name))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().setBigContentTitle(getString(com.tipbox.app.R.string.app_name)).bigText(messageBody))
            .setSound(notificationSoundURI)
            .setContentIntent(resultIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel =
                NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            assert(notificationManager != null)
            mNotificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(0, mNotificationBuilder.build())
    }


    companion object {
        private val NOTIFICATION_CHANNEL_ID = "1"
        private val TAG = "Hello World"
    }


}
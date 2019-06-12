package com.example.servicetypes;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.*;
import android.util.Log;
import android.widget.Toast;

public class BoundServiceUsingMessanger extends Service {

    public static final int JOB_1 = 1;
    public static final int JOB_RESPONSE_1 = 2;

    Messenger messenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            Message message;
            Bundle bundle = new Bundle();
            String messageText;

            switch (msg.what) {
                case JOB_1:
                    messageText = msg.getData().getString("message");
                    message = Message.obtain(null, JOB_RESPONSE_1);
                    Toast.makeText(getApplicationContext(),messageText , Toast.LENGTH_SHORT).show();
                    bundle.putString("message_res", messageText.toUpperCase());
                    message.setData(bundle);
                    Messenger activityMessenger = msg.replyTo;
                    try {
                        activityMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    }
}
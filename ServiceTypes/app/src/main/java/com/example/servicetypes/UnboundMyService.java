package com.example.servicetypes;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/*An application component starts the service, and it would continue to run in the background,
        even if the original component that started it is destroyed.
        You will use the startService() method to start an unbound service.*/

public class UnboundMyService extends Service {
    MediaPlayer myPlayer;
    public UnboundMyService() {
    }
    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        myPlayer.start();
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onCreate() {
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
        //playFile(R.raw.song);
        downloadFile("https://static.pexels.com/photos/4825/red-love-romantic-flowers.jpg");
    }

    public void playFile(int urlStr) {
        myPlayer = MediaPlayer.create(this,urlStr);
        myPlayer.setLooping(false); // Set looping
    }

    public void downloadFile(final String urlStr) {
        BackTask backTask = new BackTask();
        backTask.execute(urlStr);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
        //myPlayer.stop();
    }
}
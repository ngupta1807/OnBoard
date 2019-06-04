package com.example.servicetypes;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
    An Android component may bind itself to a Service using bindService ()
    to perform interactivity and inter process communication.
    When the component binding to the bound service is destroyed, the service stops.
*/

public class BoundService extends Service {
    MediaPlayer myPlayer;
    public BoundService() {
    }

    private Binder binder = new LocalBinder();
    public class LocalBinder extends Binder {
        BoundService getService() {
            return BoundService.this;
        }
    }
    @Override
    public void onCreate() {
        Log.v("App","Service Created");
    }

    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.v("App","Service Started");
        return binder;
    }


    public void downloadFile(final String urlStr) {
        BackTask backTask = new BackTask();
        backTask.execute(urlStr);
    }

    public void playFile() {
        Log.v("App","Service playFile");
        myPlayer = MediaPlayer.create(this,R.raw.song);
        myPlayer.start();
        myPlayer.setLooping(false); // Set looping
    }

    @Override
    public void onDestroy() {
        Log.v("App","Service Stopped");
        //myPlayer.stop();
    }
}
package com.example.servicetypes;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BackTask extends AsyncTask<String, Integer, Void> {
    protected void onPreExecute() {
    }

    protected Void doInBackground(String... params) {
        URL url;
        int count;
        HttpURLConnection con = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            url = new URL(params[0]);
            try {
                // Open connection
                con = (HttpURLConnection) url.openConnection();
                // read stream
                is = con.getInputStream();
                String pathr = url.getPath();
                // output file path
                String filename = pathr.substring(pathr.lastIndexOf('/') + 1);
                File f=new File(Environment.getExternalStorageDirectory(),"/edoc_download");
                f.mkdirs();
                //String path = Environment.getExternalStorageDirectory() + "/edoc_download/" + filename;
                File path = new File(f, filename);
                Log.i("App", "" + path);
                if (path.exists())
                    path.delete();
                //write to file
                fos = new FileOutputStream(path);
                int lenghtOfFile = con.getContentLength();
                byte data[] = new byte[1024];
                while ((count = is.read(data)) != -1) {
                    fos.write(data, 0, count);
                }
                fos.flush();


            } catch (Exception e) {
                e.printStackTrace();
                String error_Message = e.getMessage();
                if (error_Message != null) {
                    Log.e("BoundService",error_Message);
                }

            } finally {
                if (is != null)
                    try {
                        is.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                if (fos != null)
                    try {
                        fos.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
            }


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void result) {
        Log.v("App","Download finished");
    }

}

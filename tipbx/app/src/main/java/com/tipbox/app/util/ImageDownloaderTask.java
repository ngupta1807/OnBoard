package com.tipbox.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.tipbox.app.interfce.BitmapCallback;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    private final MemoryCache memoryCache;
    private Context context;
    private String url;
    BitmapCallback callback;
    public ImageDownloaderTask(BitmapCallback callback, String url, Context context) {
        memoryCache = new MemoryCache() {
            @Override
            public long getCurrentSize() {
                return 0;
            }

            @Override
            public long getMaxSize() {
                return 0;
            }

            @Override
            public void setSizeMultiplier(float multiplier) {

            }

            @Nullable
            @Override
            public Resource<?> remove(@NonNull Key key) {
                return null;
            }

            @Nullable
            @Override
            public Resource<?> put(@NonNull Key key, @Nullable Resource<?> resource) {
                return null;
            }

            @Override
            public void setResourceRemovedListener(@NonNull ResourceRemovedListener listener) {

            }

            @Override
            public void clearMemory() {

            }

            @Override
            public void trimMemory(int level) {

            }
        };
        this.url = url;
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return downloadBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }
        callback.result(bitmap);
    }

    private Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode != 200) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            Log.d("URLCONNECTIONERROR", e.toString());
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();

            }
        }
        return null;
    }
}

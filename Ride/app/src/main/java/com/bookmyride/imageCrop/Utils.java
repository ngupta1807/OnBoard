package com.bookmyride.imageCrop;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.bookmyride.BuildConfig;

import java.io.File;

public class Utils {

    public static Uri getImageUri(Context ctx, String path) {
        //return Uri.fromFile(new File(path));
        Uri photoURI = FileProvider.getUriForFile(ctx,
                BuildConfig.APPLICATION_ID + ".provider",
                new File(path));
        return photoURI;
    }
}

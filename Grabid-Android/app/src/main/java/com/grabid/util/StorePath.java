package com.grabid.util;

import android.os.Environment;

/**
 * Created by vinod on 11/24/2016.
 */
public interface StorePath {
    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/GRABiD/";
    String USER_SIGN = DIRECTORY  + "user_sign.png";
    String USER_IMAGE = DIRECTORY  + "user_sign.png";
    String DRIVER_LICENCE = DIRECTORY  + "user_sign.png";
    String ITEM_IMAGE = DIRECTORY  + "user_sign.png";
}

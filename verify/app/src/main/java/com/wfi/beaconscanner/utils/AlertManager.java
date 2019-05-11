package com.wfi.beaconscanner.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.Html;

/**
 * Created by nisha.
 */
public class AlertManager {
    public static void messageDialog(Context ctx, String title, String message) {
        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        if (Build.VERSION.SDK_INT >= 24) {
            builder.setTitle(Html.fromHtml("<font color='#ff0000'>"+title+"</font>",Html.FROM_HTML_MODE_LEGACY));
            builder.setMessage(Html.fromHtml("<font color='#000000'>Please check internet is working on your device.</font>",Html.FROM_HTML_MODE_LEGACY));
        } else {
            builder.setTitle(title);
            builder.setMessage("Please check internet is working on your device.");
        }
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                System.exit(0);
            }
        });
        builder.show();
    }

    // *********** show alert box with OK button *********//**//*
    public void showDialog(final Context mcon, final String message, boolean isCancelable, final String closeapp) {
        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(mcon);
        builder.setCancelable(isCancelable);
        if (Build.VERSION.SDK_INT >= 24) {
            builder.setTitle(Html.fromHtml("<font color='#ff0000'>Alert!</font>",Html.FROM_HTML_MODE_LEGACY));
            builder.setMessage(Html.fromHtml("<font color='#000000'>"+message+"</font>",Html.FROM_HTML_MODE_LEGACY));
        } else {
            builder.setTitle("Alert!");
            builder.setMessage(message);
        }
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(closeapp.equals("1")) {
                    System.exit(0);
                }
            }
        });
        builder.show();
    }
}

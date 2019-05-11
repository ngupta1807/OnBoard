package com.grabid.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.grabid.R;
import com.grabid.fragments.ChargeMe;
import com.grabid.fragments.PayMe;

/**
 * Created by vinod on 10/14/2016.
 */
public class AlertManager {
    public static void messageDialog(Context ctx, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog d = builder.create();
        d.show();
    }

    public static void BankmessageDialog(final Activity ctx, String title, final String message, final String backstackname, final String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //   if (message.contentEquals("Please save your bank detail and credit card information before take now") || message.contentEquals("Please save your bank detail before take now") || message.contentEquals("Please save your bank detail and credit card information before bid") || message.contentEquals("Please save your bank detail before bid")) {
//                message.contentEquals("Please save your  credit card information before take now") || message.contentEquals("Please save your  credit card information before bid"
                if (type != null && !type.isEmpty()) {
                    if (type.contentEquals("3")) {
                        {
                            String backStateName = backstackname;
                            Fragment fragment = new PayMe();
                            Bundle bundle = new Bundle();
                            bundle.putString("UITYPE", "3");
                            fragment.setArguments(bundle);
                            ctx.getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName).addToBackStack(null).commitAllowingStateLoss();
                            Log.v("", message);
                        }
                    } else if (type.contentEquals("2") || type.contentEquals("5")) {
                        String backStateName = backstackname;
                        Fragment fragment = new ChargeMe();
                        Bundle bundle = new Bundle();
                        bundle.putString("UITYPE", type);
                        fragment.setArguments(bundle);
                        ctx.getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                                .addToBackStack(null)
                                .commitAllowingStateLoss();
                        Log.v("", message);
                    }
                }
                dialog.dismiss();
            }
        });
        Dialog d = builder.create();
        d.show();
    }

    public static void BankmessageDialogAdd(final Activity ctx, String title, final String message, final String backstackname, final String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //   if (message.contentEquals("Please save your bank detail and credit card information before take now") || message.contentEquals("Please save your bank detail before take now") || message.contentEquals("Please save your bank detail and credit card information before bid") || message.contentEquals("Please save your bank detail before bid")) {
//                message.contentEquals("Please save your  credit card information before take now") || message.contentEquals("Please save your  credit card information before bid"
                if (type != null && !type.isEmpty()) {
                    if (type.contentEquals("3")) {
                        {
                            String backStateName = backstackname;
                            Fragment fragment = new PayMe();
                            Bundle bundle = new Bundle();
                            bundle.putString("UITYPE", "3");
                            fragment.setArguments(bundle);
                            ctx.getFragmentManager().beginTransaction().add(R.id.container, fragment, backStateName).addToBackStack(null).commitAllowingStateLoss();
                            Log.v("", message);
                        }
                    } else if (type.contentEquals("2") || type.contentEquals("5")) {
                        String backStateName = backstackname;
                        Fragment fragment = new ChargeMe();
                        Bundle bundle = new Bundle();
                        bundle.putString("UITYPE", type);
                        fragment.setArguments(bundle);
                        ctx.getFragmentManager().beginTransaction().add(R.id.container, fragment, backStateName)
                                .addToBackStack(null)
                                .commitAllowingStateLoss();
                        Log.v("", message);
                    }
                }
                dialog.dismiss();
            }
        });
        Dialog d = builder.create();
        d.show();
    }

}

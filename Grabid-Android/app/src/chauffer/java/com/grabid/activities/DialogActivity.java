package com.grabid.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.grabid.R;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.RestAPICall;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.models.DeliveryUpdate;

import java.util.HashMap;

/**
 * Created by nisha on 2/17/2017.
 */

public class DialogActivity extends Activity implements AsyncTaskCompleteListener {
    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle in = getIntent().getExtras();
        session = new SessionManager(DialogActivity.this);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("GRABID");
        alertDialog.setMessage(in.getString("message"));
        alertDialog.setIcon(R.mipmap.app_icon);
        String type = in.getString("type");
        if (type != null && (type.contentEquals("64"))) {
            alertDialog.setButton2("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    cancelDelivery(in.getString("delivery_id"));
                }
            });
            alertDialog.setButton(" AWARD", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent;
                    if (TextUtils.isEmpty(in.getString("delivery_id")))
                        intent = new Intent(getApplicationContext(), Splash.class);
                    else {
                        intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.putExtra("message", in.getString("message"));
                        intent.putExtra("delivery_id", in.getString("delivery_id"));
                        intent.putExtra("type", in.getString("type"));
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });
        } else if (type != null && (type.contentEquals("33") || type.contentEquals("34") || type.contentEquals("65") || type.contentEquals("55"))) {
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
        } else if (type != null && type.contentEquals("63")) {
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (in.containsKey("delivery_id")) {
                        if (HomeActivity.IsDeliveryInfo && DeliveryUpdate.getDeliveryId() != null && in.getString("delivery_id").contentEquals(DeliveryUpdate.getDeliveryId())) {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        } else {
                            dialog.dismiss();
                            finish();
                        }
                    } else {
                        dialog.dismiss();
                        finish();
                    }
                }
            });

        } else if (type != null && (type.contentEquals("66"))) {
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent;
                    intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("message", in.getString("message"));
                    intent.putExtra("type", in.getString("type"));
                    intent.putExtra("creditcarddecline", in.getString("type"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();


                }
            });
        }
        //Added by VK
        else if (type != null && ((type.contentEquals("68")) || type.contentEquals("71"))) {
            alertDialog.setButton("VIEW", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    showDeliveryInfo(in.getString("type"), in.getString("delivery_id"));
                }
            });

        } else if (type != null && (type.contentEquals("69"))) {
            alertDialog.setButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    showDeliveryInfo(in.getString("type"), in.getString("delivery_id"));
                }
            });
            alertDialog.setButton2("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    showDeliveryInfo(in.getString("type"), in.getString("delivery_id"));
                }
            });
        }
        else if (type != null && (type.contentEquals("10")) ) {
            alertDialog.setButton("SEARCH", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent;
                    if (TextUtils.isEmpty(in.getString("delivery_id")))
                        intent = new Intent(getApplicationContext(), Splash.class);
                    else {
                        intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.putExtra("message", in.getString("message"));
                        intent.putExtra("delivery_id", in.getString("delivery_id"));
                        intent.putExtra("type", in.getString("type"));
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });
            alertDialog.setButton2("IGNORE", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
        }
        //VK end
        else {
            alertDialog.setButton("SHOW", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent;
                    if (TextUtils.isEmpty(in.getString("delivery_id")))
                        intent = new Intent(getApplicationContext(), Splash.class);
                    else {
                        intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.putExtra("message", in.getString("message"));
                        intent.putExtra("delivery_id", in.getString("delivery_id"));
                        intent.putExtra("type", in.getString("type"));
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });
            alertDialog.setButton2("IGNORE", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
        }

        alertDialog.show();
    }

    //Added by VK
    //open delivery info page
    private void showDeliveryInfo(String type, String deliveryID) {
        Intent intent;
        intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("delivery_id", deliveryID);
        intent.putExtra("type", type);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        DialogActivity.this.finish();
    }

    //VK end
    private void cancelDelivery(String deliveryID) {
        String url = Config.SERVER_URL + Config.JOBS + "/" + deliveryID;
        Log.d("url", url);
        HashMap<String, String> params = new HashMap<>();
        params.put("delivery_id", deliveryID);
        if (Internet.hasInternet(DialogActivity.this)) {
//            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            RestAPICall mobileAPI = new RestAPICall(DialogActivity.this, HTTPMethods.DELETE, this, null);
            mobileAPI.execute(url, session.getToken());
        } else {
            messageDialog(DialogActivity.this, "Alert!", getResources().getString(R.string.no_internet), false);
        }
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    public void handleResponse(String result) {
        if (Integer.parseInt(result) == APIStatus.SUCCESS) {
            messageDialog(DialogActivity.this, "Success!", "Your transfer has been cancelled successfully.", true);
        } else {
            messageDialog(DialogActivity.this, "Error!", getResources().getString(R.string.fail), false);
        }
    }

    public void messageDialog(Context ctx, String title, String message, final boolean IsSuccess) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle(title);
            builder.setMessage(message);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (HomeActivity.IsDeliveryInfo && IsSuccess) {
                        Intent intent = new Intent(DialogActivity.this, HomeActivity.class);
                        intent.putExtra("signaturetype", "1");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    DialogActivity.this.finish();
                }
            });
            Dialog d = builder.create();
            d.show();
        } catch (Exception e) {
            e.toString();
        }
    }

}
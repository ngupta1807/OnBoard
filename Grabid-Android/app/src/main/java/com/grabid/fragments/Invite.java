package com.grabid.fragments;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.common.AlertManager;
import com.grabid.common.SessionManager;
import com.grabid.models.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class Invite extends Fragment implements View.OnClickListener,
        AsyncTaskCompleteListener {
    TextView referralCode;
    LinearLayout whatsApp, facebook, email, sms;
    UserInfo userInfo;
    SessionManager session;
    String facebookAppPkg = "com.facebook.android";
    String whatsAppPkg = "com.whatsapp";
    String shareMsg;
    //    String appLinkUrl = "https://play.google.com/store/apps/details?id=";
    String appLinkUrl = "XXX";
    String part2 = "";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.title.setText(getResources().getString(R.string.in_vite));
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        View view = inflater.inflate(R.layout.invite, null);
        init(view);
        return view;
    }

    private void init(View view) {
        session = new SessionManager(getActivity());
        userInfo = session.getUserDetails();
        referralCode = (TextView) view.findViewById(R.id.referral_code);
        whatsApp = (LinearLayout) view.findViewById(R.id.whatsapp);
        facebook = (LinearLayout) view.findViewById(R.id.facebook);
        sms = (LinearLayout) view.findViewById(R.id.sms);
        email = (LinearLayout) view.findViewById(R.id.email);
//        shareMsg = "Join GRABiD today using my referral code:\"" + userInfo.getReferCode() + "\" Download and sign up using this link:" + appLinkUrl;

        referralCode.setOnClickListener(this);
        try {
            String string = userInfo.getReferCode();
            String[] parts = string.split("=");
            String part1 = parts[0]; // 004
            part2 = parts[1];
            referralCode.setText(part2);
        } catch (Exception e) {
            e.toString();
        }
//        shareMsg = "Join GRABiD today using my referral code: " + part2 + ". Download and sign up using this link:" + userInfo.getReferCode();
        shareMsg = getResources().getString(R.string.joingrabid) + " " + part2 + ". Download and sign up using this link:" + userInfo.getReferCode();


        whatsApp.setOnClickListener(this);
        facebook.setOnClickListener(this);
        email.setOnClickListener(this);
        sms.setOnClickListener(this);
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
            } else {
                AlertManager.messageDialog(getActivity(), "Alert!", "Error");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.whatsapp) {
            sendMessagewhatsApp();
        } else if (view.getId() == R.id.facebook) {
            //   shareApp(facebookAppPkg);

            //sendMessageFacebook();
            onShareResult(view);

        } else if (view.getId() == R.id.email) {
            sendEmail();
        } else if (view.getId() == R.id.sms) {
            sendMessage();
        }
        /*Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "I use GRABiD to bid for shipments and also order shipments. Please use my Referral Code " + userInfo.getReferCode() + " to earn extra.";
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));*/
    }


    public void sendMessage() {
        try {
            Uri uri = Uri.parse("smsto:");
            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
            it.putExtra("sms_body", shareMsg);
            startActivity(it);
           /* Intent sendIntent = new Intent(Intent.ACTION_VIEW,uri);
            sendIntent.putExtra("sms_body", shareMsg);
            sendIntent.setType("vnd.android-dir/mms-sms");
            startActivity(sendIntent);*/
        } catch (Exception e) {
            e.toString();
        }
    }

    public void sendMessagewhatsApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareMsg);
        sendIntent.setType("text/plain");
        // Do not forget to add this to open whatsApp App specifically
        boolean isAppInstalled = checkAppInstall("com.whatsapp");
        if (isAppInstalled) {
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        } else {
            AlertManager.messageDialog(getActivity(), "Alert!", "Your device has no WhatsApp app installed.");

/*
            Toast.makeText(getActivity(),
                    "Installed application first", Toast.LENGTH_LONG).show();
*/
        }

    }

    public void sendFacebookMessage() {


    }

    public void sendMessageFacebook() {
        boolean isAppInstalled = checkAppInstall("com.facebook.katana");
        if (isAppInstalled) {
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            // shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMsg);
            PackageManager pm = getActivity().getPackageManager();
            List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
            ShareLinkContent content =
                    new ShareLinkContent.Builder()
                            .setContentUrl(
                                    Uri.parse("https://developers.facebook.com/docs/android/devices"))
                            .build();

            for (final ResolveInfo app : activityList) {
                if ((app.activityInfo.name).contains("facebook")) {
                    final ActivityInfo activity = app.activityInfo;
                    final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                    shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    shareIntent.setComponent(name);

                    startActivity(shareIntent);
                    break;
                }
            }
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", "Your device has no Facebook app installed.");
            /*Toast.makeText(getActivity(),
                "Installed application first", Toast.LENGTH_LONG).show();*/
    }

    String LOG_TAG = "facebook";
    private CallbackManager callbackManager;

    public void onShareResult(View view) {
        FacebookSdk.sdkInitialize(getActivity());
        callbackManager = CallbackManager.Factory.create();
        final ShareDialog shareDialog = new ShareDialog(getActivity());

        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d(LOG_TAG, "success");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(LOG_TAG, "error");
            }

            @Override
            public void onCancel() {
                Log.d(LOG_TAG, "cancel");
            }
        });


        if (shareDialog.canShow(ShareLinkContent.class)) {
            String url = userInfo.getReferCode();
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("GRABiD Share")
                    // .setContentDescription("My new highscore is " + "1234" + "!!")
                    .setContentUrl(Uri.parse(url))
                    //.setImageUrl(Uri.parse("android.resource://de.ginkoboy.flashcards/" + R.drawable.logo_flashcards_pro))
                    // .setImageUrl(Uri.parse("http://bagpiper-andy.de/bilder/dudelsack%20app.png"))
                    .build();

            shareDialog.show(linkContent);
        }


    }

  /*  private void inviteViaFacebook() {
        boolean isAppInstalled = checkAppInstall("com.facebook.katana");
        if (isAppInstalled) {
            final String appPackageName = getActivity().getPackageName();
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
            PackageManager pm = getActivity().getPackageManager();
            List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
            for (final ResolveInfo app : activityList) {
                if ((app.activityInfo.name).contains("facebook")) {
                    final ActivityInfo activity = app.activityInfo;
                    final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                    shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    shareIntent.setComponent(name);
                    getActivity().startActivity(shareIntent);
                    break;
                }
            }
        } else {
//            Alert("Alert!", "Facebook application is not installed.");
        }
    }*/

    public void shareApp(String application) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, shareMsg);

        boolean installed = checkAppInstall(application);
        if (installed) {
            intent.setPackage(application);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(),
                    "Installed application first", Toast.LENGTH_LONG).show();
        }

    }

    private boolean checkAppInstall(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }


    public void sendEmail() {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, shareMsg);
        try {
            Intent mailer = Intent.createChooser(intent, null);
            getActivity().startActivity(mailer);
        } catch (android.content.ActivityNotFoundException ex) {
            AlertManager.messageDialog(getActivity(), "Alert!", "There are no email clients installed.");

//            Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
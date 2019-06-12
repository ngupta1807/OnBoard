package com.bookmyride.fragments;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.views.AlertDialog;

import java.util.List;

public class InviteAndEarn extends Fragment implements View.OnClickListener {
    SessionHandler session;

    TextView referralCode;
    LinearLayout whatsApp, facebook, sms, email;
    String msg = "";
    String referral_code = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.invite_earn, null);
        init(view);
        return view;
    }

    /*@Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("INVITE AND EARN");
    }*/

    private void init(View view) {
        session = new SessionHandler(getActivity());
        referralCode = (TextView) view.findViewById(R.id.referral_code);
        whatsApp = (LinearLayout) view.findViewById(R.id.whatsapp);
        whatsApp.setOnClickListener(this);
        facebook = (LinearLayout) view.findViewById(R.id.facebook);
        facebook.setOnClickListener(this);
        sms = (LinearLayout) view.findViewById(R.id.sms);
        sms.setOnClickListener(this);
        email = (LinearLayout) view.findViewById(R.id.email);
        email.setOnClickListener(this);
        /*submit = (TextView) view.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSharing();
            }
        });*/
        String referral = session.getReferralCode();
        referral_code = referral.substring(referral.indexOf("=") + 1, referral.length());
        msg = "Join BookMyRide today using my referral code: " + referral_code + ".\n" +
                //"Sign up using this link for Android: www.ride247.com or this link for iOS: www.ride247.com";
                "Sign up using this link: " + referral;
        referralCode.setText(referral_code);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.whatsapp:
                inviteViaWhatsapp();
                break;
            case R.id.facebook:
                inviteViaFacebook();
                break;
            case R.id.sms:
                inviteViaSMS();
                break;
            case R.id.email:
                inviteViaEmail();
                break;
        }
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(getActivity(), true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void inviteViaWhatsapp() {
        final String appPackageName = getContext().getPackageName();
        boolean isAppInstalled = isAppInstalled("com.whatsapp");

        if (isAppInstalled) {
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            /*whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Hi,\n" +
                    "Please install Ride24:7 app at: \nhttps://play.google.com/store/apps/details?id=" + appPackageName + "\nand use '" + session.getReferralCode() + "' referral code while registration.");*/
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, msg);
            try {
                startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Alert("Alert!", "Whatsapp have not been installed.");
            }
        } else {
            Alert("Alert!", "Whatsapp application is not installed.");
        }
    }

    private void inviteViaFacebook() {
        boolean isAppInstalled = isAppInstalled("com.facebook.katana");
        if (isAppInstalled) {
            final String appPackageName = getContext().getPackageName();
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "BookMyRide Referral Code.");
            /*shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hi,\n" +
                    "Please install Ride24:7 app at: \nhttps://play.google.com/store/apps/details?id=" + appPackageName + "\nand use '" + session.getReferralCode() + "' referral code while registration.");*/
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg);
            PackageManager pm = getContext().getPackageManager();
            List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
            for (final ResolveInfo app : activityList) {
                if ((app.activityInfo.name).contains("facebook")) {
                    final ActivityInfo activity = app.activityInfo;
                    final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                    shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    shareIntent.setComponent(name);
                    getContext().startActivity(shareIntent);
                    break;
                }
            }
        } else {
            Alert("Alert!", "Facebook application is not installed.");
        }
    }

    private void inviteViaSMS() {
        try {
            final String appPackageName = getContext().getPackageName();
            String mText = "Hi,\n" + "Please install BookMyRide app at: \nhttps://play.google.com/store/apps/details?id=" + appPackageName + "\nand use '" + session.getReferralCode() + "' referral code while registration.";
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(getActivity());
                sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                //sendIntent.putExtra(Intent.EXTRA_TEXT, mText);
                sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                // Can be null in case that there is no default, then the user would be able to choose
                // any app that support this intent.
                if (defaultSmsPackageName != null) {
                    sendIntent.setPackage(defaultSmsPackageName);
                }
                startActivity(sendIntent);
            } else {
                sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setType("vnd.android-dir/mms-sms");
                //sendIntent.putExtra("sms_body", mText);
                sendIntent.putExtra("sms_body", msg);
                if (sendIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(sendIntent);
                } else {
                    Alert("Alert!", "Activity not found.");
                }
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void inviteViaEmail() {
        final String appPackageName = getContext().getPackageName();
        try {
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "BookMyRide Referral Code.");
            /*emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hi,\n" +
                    "Please install Ride24:7 app at: \nhttps://play.google.com/store/apps/details?id=" + appPackageName + "\nand use '" + session.getReferralCode() + "' referral code while registration.");*/
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg);
            emailIntent.setType("plain/text"); // This is incorrect MIME, but Gmail is one of the only apps that responds to it - this might need to be replaced with text/plain for Facebook
            final PackageManager pm = getContext().getPackageManager();
            final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
            ResolveInfo best = null;
            for (final ResolveInfo info : matches)
                if (info.activityInfo.packageName.endsWith(".gm") ||
                        info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
            if (best != null)
                emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
            startActivity(emailIntent);
        } catch (Exception e) {
            Alert("Alert!", "Gmail Application not found.");
        }
    }

    private boolean isAppInstalled(String uri) {
        PackageManager pm = getContext().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
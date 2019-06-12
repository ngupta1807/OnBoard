package com.bookmyride.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;

import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bookmyride.R;
import com.bookmyride.activities.register.Location;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.fcm.AlarmReceiver;
import com.bookmyride.fcm.NotificationFilters;
import com.bookmyride.fragments.About;
import com.bookmyride.fragments.ChangePassword;
import com.bookmyride.fragments.ChargeMe;
import com.bookmyride.fragments.DriverDashboard;
import com.bookmyride.fragments.DriverList;
import com.bookmyride.fragments.DriverMapActivity;
import com.bookmyride.fragments.InviteAndEarn;
import com.bookmyride.fragments.Notifications;
import com.bookmyride.fragments.PayMe;
import com.bookmyride.fragments.PaymentSummary;
import com.bookmyride.fragments.PreBookingRide;
import com.bookmyride.fragments.RechargeWallet;
import com.bookmyride.fragments.ReferralAmount;
import com.bookmyride.fragments.RideMap;
import com.bookmyride.fragments.RideSummary;
import com.bookmyride.services.LocationService;
import com.bookmyride.services.RouteService;
import com.bookmyride.util.ImageLoader;
import com.bookmyride.views.CustomTypefaceSpan;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class DriverHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SessionHandler session;
    ImageLoader imgLoader;
    public static TextView mTitle;
    public static ImageView userImg;
    public static TextView filter;
    public static TextView availableRides;
    public static FragmentManager fm;
    private BroadcastReceiver mReceiver;
    String preValue = "1";
    public static String noOfRides = "0";

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        init();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission())
                loadFragments();
        } else
            loadFragments();

        deleteFiles(session.getTaxiImgPath());
        deleteFiles(session.getLicenceImgPath());
        deleteFiles(session.getProfileImgPath());
    }

    String rideData;

    private void loadFragments() {
        /*if(getIntent().hasExtra("pre")) {
            preValue = getIntent().getStringExtra("pre");
            displaySelectedScreen(R.id.pending);
        } else*/
        displaySelectedScreen(R.id.home);
        startService(new Intent(getApplicationContext(), LocationService.class));
        startService(new Intent(getApplicationContext(), RouteService.class));
        if (getIntent().hasExtra("rideData")) {
            rideData = getIntent().getStringExtra("rideData");
            getIntent().removeExtra("rideData");
            String confirmation = "";
            try {
                JSONObject jObj = new JSONObject(rideData);
                if (jObj.has("confirmation"))
                    confirmation = jObj.getString("confirmation");
            } catch (JSONException e) {
                e.printStackTrace();
                confirmation = "";
            }
            if (!confirmation.equals(""))
                confirmationPopup();
            else showCancelledMessage();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        freeMemory();
    }

    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    private void deleteFiles(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("file Deleted :" + path);
            } else {
                System.out.println("file not Deleted :" + path);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(NotificationFilters.AVAILABLE_RIDE));
    }

    @Override
    protected void onPause() {
        if (mReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onPause();
    }

    private void startAlarm() {
        Intent intent = new Intent(DriverHome.this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(DriverHome.this,
                1, intent, 0);

        // Schedule the alarm for half an hour!
        long interval = 30 * 60 * 1000;
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert am != null;
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + interval,
                interval, sender);
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(DriverHome.this, "Alarm Registered.", Toast.LENGTH_LONG);
        //mToast.show();
    }

    @Override
    protected void onDestroy() {
        if (session.isDriverOnline()) {
            startAlarm();
        } else stopAlarm();
        if (mReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public void updateTheTextView(final String noOfRides) {
        runOnUiThread(new Runnable() {
            public void run() {
                availableRides.setText(noOfRides);
            }
        });
    }

    private void init() {
        session = new SessionHandler(this);
        imgLoader = new ImageLoader(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.menu);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        filter = (TextView) toolbar.findViewById(R.id.filter);
        availableRides = (TextView) toolbar.findViewById(R.id.rides);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        disableNavigationViewScrollbars(navigationView);
        View hView = navigationView.getHeaderView(0);
        RelativeLayout topBar = (RelativeLayout) hView.findViewById(R.id.lay_top);
        TextView navUser = (TextView) hView.findViewById(R.id.title);
        TextView phone = (TextView) hView.findViewById(R.id.phone);
        userImg = (ImageView) hView.findViewById(R.id.pic);
        if (!session.getUserImg().equals("") && !session.getUserImg().equals("null")) {
            imgLoader.DisplayImage(session.getUserImg(), userImg);
        }
        phone.setText(session.getUserNo());
        navUser.setText("HELLO " + session.getUser().toUpperCase());
        topBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (session.isDriverOnline()) {
                    Alert("Alert!", "Please go offline, if you want to update your profile.");
                } else {
                    startActivity(new Intent(getApplicationContext(), Location.class)
                            .putExtra("LoginData", DriverDashboard.response));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });

        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

        Menu menu = navigationView.getMenu();
        //Change text color of Driver title
        MenuItem driver = menu.findItem(R.id.menu_driver);
        applyFontToMenuHeaderItem(driver);
        SpannableString s = new SpannableString(driver.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length(), 0);
        driver.setTitle(s);

        //Change text color of Passenger title
        MenuItem passenger = menu.findItem(R.id.menu_passenger);
        applyFontToMenuHeaderItem(passenger);
        SpannableString s2 = new SpannableString(passenger.getTitle());
        s2.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, s2.length(), 0);
        passenger.setTitle(s2);

        //Change text color of Wallet title
        MenuItem wallet = menu.findItem(R.id.menu_wallet);
        applyFontToMenuHeaderItem(wallet);
        SpannableString s3 = new SpannableString(wallet.getTitle());
        s3.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, s3.length(), 0);
        wallet.setTitle(s3);

        //Change text color of Wallet title
        MenuItem account = menu.findItem(R.id.menu_account);
        applyFontToMenuHeaderItem(account);
        SpannableString s5 = new SpannableString(account.getTitle());
        s5.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, s5.length(), 0);
        account.setTitle(s5);

        /*if (session.isPassenger().equals("1")) {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.menu_passenger).setVisible(true);
            Menu menu_charge = navigationView.getMenu();
            menu_charge.findItem(R.id.charge_me).setVisible(true);
        } else {*/
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.menu_passenger).setVisible(false);
        Menu menu_charge = navigationView.getMenu();
        menu_charge.findItem(R.id.charge_me).setVisible(false);
        //}*/

        if (session.isBothTypeUser()) {
            Menu upgrade = navigationView.getMenu();
            upgrade.findItem(R.id.upgrade).setVisible(false);
        } else {
            Menu upgrade = navigationView.getMenu();
            upgrade.findItem(R.id.upgrade).setVisible(true);
        }

        navigationView.getMenu().getItem(0).setChecked(true);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NotificationFilters.AVAILABLE_RIDE)) {
                    noOfRides = intent.getStringExtra("noOfRides");
                    try {
                        if (getVisibleFragment()) {
                            if (!noOfRides.equals("null")) {
                                //if(Integer.parseInt(noOfRides) > 0 && getVisibleFragment())
                                availableRides.setVisibility(View.VISIBLE);
                                //else availableRides.setVisibility(View.GONE);
                                updateTheTextView("Available\nRides: " + noOfRides);
                            } else {
                                noOfRides = "0";
                                availableRides.setVisibility(View.VISIBLE);
                                updateTheTextView("Available\nRides: " + noOfRides);
                            }
                        } else availableRides.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        availableRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!noOfRides.equals("0") && !noOfRides.equals("") && !noOfRides.equals("null")) {
                    //preValue = "2";
                    //displaySelectedScreen(R.id.pending);
                    startActivity(new Intent(getApplicationContext(), AvailableRides.class)
                            .putExtra("preValue", "3"));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });
    }

    private boolean getVisibleFragment() {
        boolean isOnlineScreenVisible;
        Fragment currentFragment = DriverHome.fm.findFragmentById(R.id.content_frame);
        if (currentFragment instanceof DriverMapActivity) {
            isOnlineScreenVisible = true;
        } else isOnlineScreenVisible = false;
        return isOnlineScreenVisible;
    }

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", face), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void applyFontToMenuHeaderItem(MenuItem mi) {
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", face), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showExitDialog();
            //super.onBackPressed();
        }
    }

    @SuppressLint("SetTextI18n")
    private void displaySelectedScreen(int itemId) {
        Fragment fragment = null;
        switch (itemId) {
            case R.id.upgrade:
                upgradeAccountPrompt();
                break;
            case R.id.favorite_driver:
                mTitle.setText("Drivers");
                fragment = new DriverList();
                break;
            case R.id.notification:
                mTitle.setText("Notifications");
                fragment = new Notifications();
                break;
            case R.id.pending:
                Bundle bundle = new Bundle();
                mTitle.setText("Pre Booked Rides");
                fragment = new PreBookingRide();
                bundle.putInt("preValue", Integer.parseInt(preValue));
                fragment.setArguments(bundle);
                break;
            case R.id.charge_me:
                mTitle.setText("Charge Me");
                fragment = new ChargeMe();
                break;
            case R.id.info_payment_driver:
                mTitle.setText("Payment History");
                fragment = new PaymentSummary();
                break;
            case R.id.info_payment_customer:
                mTitle.setText("Payment History");
                fragment = new PaymentSummary();
                break;
            case R.id.book_ride:
                mTitle.setText("Book My Ride");
                fragment = new RideMap();
                break;
            case R.id.home:
                mTitle.setText("Dashboard");
                fragment = new DriverDashboard();
                break;
            case R.id.trip_summary:
                mTitle.setText("My Rides");
                fragment = new RideSummary();
                break;
            case R.id.my_rides:
                mTitle.setText("My Rides");
                fragment = new RideSummary();
                break;
            case R.id.bank_account:
                mTitle.setText("Bank Details");
                fragment = new PayMe();
                break;
            case R.id.accept_payment:
                mTitle.setText("Accept Payment");
                //fragment = new AcceptPayment();
                break;
            case R.id.view_wallet:
                mTitle.setText("BookMyRide Wallet");
                fragment = new ReferralAmount();
                break;
            case R.id.recharge_wallet:
                mTitle.setText("Recharge Wallet");
                fragment = new RechargeWallet();
                break;
            case R.id.change_pass:
                mTitle.setText("Change Password");
                fragment = new ChangePassword();
                break;
            case R.id.invite_earn:
                mTitle.setText("Invite & Earn");
                fragment = new InviteAndEarn();
                break;
            case R.id.settings:
                //mTitle.setText("SETTINGS");
                //fragment = new Settings();
                deactivateAccountDialog();
                break;
            case R.id.about:
                mTitle.setText("About BookMyRide");
                fragment = new About();
                break;
            case R.id.logout:
                logoutDialog();
                break;
        }
        if (fragment != null) {
            fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commitAllowingStateLoss();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //calling the method display selected screen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void logoutDialog() {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(DriverHome.this, true);
        mDialog.setDialogTitle("BookMyRide");
        mDialog.setDialogMessage(getResources().getString(R.string.profile_lable_logout_message));
        mDialog.setNegativeButton(getResources().getString(R.string.profile_lable_logout_yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                APIHandler apiHandler = new APIHandler(DriverHome.this, HTTPMethods.POST, new AsyncTaskCompleteListener() {
                    @Override
                    public void onTaskComplete(String result) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            if (obj.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                                session.saveCardExist(false);
                                session.saveToken("");
                                session.clearProfile();
                                session.clearLocation();
                                session.clearAddress();
                                session.saveOnlineStatus(false);
                                session.saveUserActualType("");
                                Location.addressSelectedData = "";
                                Location.locationSelectedData = "";
                                Location.driverSelectedData = "";
                                Location.addressSelectedData = "";
                                Location.vehicalSelectedData = "";
                                Location.selectedRefferelCode = "";
                                Location.isPremium = "";
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        stopAlarm();
                                        stopService(new Intent(getApplicationContext(), RouteService.class));
                                        stopService(new Intent(getApplicationContext(), LocationService.class));
                                        Intent intent = new Intent(getApplicationContext(), SignIn.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        DriverHome.this.finish();
                                    }
                                });
                            } else
                                Alert("Alert!", obj.getString(Key.MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, null);
                apiHandler.execute(Config.LOGOUT, session.getToken());
            }
        });
        mDialog.setPositiveButton(getResources().getString(R.string.profile_lable_logout_no), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    Toast mToast;

    private void stopAlarm() {
        Intent intent = new Intent(DriverHome.this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(DriverHome.this,
                1, intent, 0);

        // And cancel the alarm.
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert am != null;
        am.cancel(sender);
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(this, "Alarm Stopped.", Toast.LENGTH_LONG);
        //mToast.show();
    }

    private void upgradeAccountPrompt() {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(DriverHome.this, true);
        mDialog.setDialogTitle("BookMyRide");
        mDialog.setDialogMessage("Do you want to upgrade your account?");
        mDialog.setNegativeButton(getResources().getString(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                startActivity(new Intent(getApplicationContext(), PassengerSignup.class)
                        .putExtra("profile", "")
                        .putExtra("upgrade", ""));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        mDialog.setPositiveButton(getResources().getString(R.string.no), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void Alert(final String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(DriverHome.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (title.equalsIgnoreCase("Success!")) {
                    session.saveCardExist(false);
                    session.saveUserActualType("");
                    session.saveToken("");
                    session.clearProfile();
                    session.clearLocation();
                    session.clearAddress();
                    session.saveOnlineStatus(false);
                    Intent intent = new Intent(getApplicationContext(), SignIn.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    DriverHome.this.finish();
                }
            }
        });
        mDialog.show();
    }

    private void deactivateAccountDialog() {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(DriverHome.this, true);
        mDialog.setDialogTitle("Deactivate Account");
        mDialog.setDialogMessage(getResources().getString(R.string.del_profile_msg));
        mDialog.setNegativeButton(getResources().getString(R.string.profile_lable_logout_yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                APIHandler apiHandler = new APIHandler(DriverHome.this, HTTPMethods.PUT, new AsyncTaskCompleteListener() {
                    @Override
                    public void onTaskComplete(String result) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            if (obj.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                                Alert("Success!", obj.getString(Key.MESSAGE));
                            } else
                                Alert("Alert!", obj.getString(Key.MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, null);
                apiHandler.execute(Config.DEACTIVATE + session.getUserID(), session.getToken());
            }
        });
        mDialog.setPositiveButton(getResources().getString(R.string.profile_lable_logout_no), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void showExitDialog() {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(DriverHome.this, true);
        mDialog.setDialogTitle("BookMyRide");
        mDialog.setDialogMessage("Do you want to quit the application?");
        mDialog.setNegativeButton(getResources().getString(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                DriverHome.this.finish();
                System.exit(0);
            }
        });
        mDialog.setPositiveButton(getResources().getString(R.string.no), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void showCancelledMessage() {
        Intent intent = new Intent(this, NotificationDialogs.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("rideData", rideData);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void confirmationPopup() {
        Intent intent = new Intent(this, Confirmation.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("rideData", rideData);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 919;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        loadFragments();
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                    DriverHome.this.finish();
                }
                break;
        }
    }
}

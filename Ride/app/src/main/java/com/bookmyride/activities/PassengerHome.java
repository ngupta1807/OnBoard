package com.bookmyride.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
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
import com.bookmyride.fragments.*;
import com.bookmyride.fragments.ChangePassword;
import com.bookmyride.services.LocationService;
import com.bookmyride.services.RouteService;
import com.bookmyride.util.ImageLoader;
import com.bookmyride.views.CustomTypefaceSpan;

import org.json.JSONException;
import org.json.JSONObject;

public class PassengerHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SessionHandler session;
    ImageLoader imgLoader;
    public static TextView mTitle;
    public static TextView filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission())
                loadFragments();
        } else
            loadFragments();
    }

    String rideData;

    private void loadFragments() {
        displaySelectedScreen(R.id.book_ride);
        startService(new Intent(getApplicationContext(), LocationService.class));
        if (getIntent().hasExtra("rideData")) {
            rideData = getIntent().getStringExtra("rideData");
            getIntent().removeExtra("rideData");
            String confirmation = "";
            String asap = "";
            String msg = "";
            String type = "";
            try {
                JSONObject jObj = new JSONObject(rideData);
                if (jObj.has("confirmation"))
                    confirmation = jObj.getString("confirmation");
                if (jObj.has("confirmation"))
                    asap = jObj.getString("confirmation");
                if (jObj.has("message"))
                    msg = jObj.getString("message");
                if (jObj.has("type"))
                    type = jObj.getString("type");
            } catch (JSONException e) {
                e.printStackTrace();
                confirmation = "";
            }
            if (!confirmation.equals(""))
                confirmationPopup();
            else if (asap.equals("0") && type.equals("1"))
                acceptPopup(msg);
        } else if (getIntent().hasExtra("bookingId")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(PassengerHome.this, RideDetail.class);
                    intent.putExtra("booking_id", getIntent().getStringExtra("bookingId"));
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        }
    }

    private void init() {
        session = new SessionHandler(this);
        imgLoader = new ImageLoader(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        filter = (TextView) toolbar.findViewById(R.id.filter);
        //toolbar.setNavigationIcon(R.drawable.menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setHomeAsUpIndicator(R.drawable.menu);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        disableNavigationViewScrollbars(navigationView);
        View hView = navigationView.getHeaderView(0);
        RelativeLayout topBar = (RelativeLayout) hView.findViewById(R.id.lay_top);
        TextView navUser = (TextView) hView.findViewById(R.id.title);
        TextView phone = (TextView) hView.findViewById(R.id.phone);
        if (!session.getUserImg().equals("") && !session.getUserImg().equals("null")) {
            Log.e("pass_img", session.getUserImg());
            ImageView userImg = (ImageView) hView.findViewById(R.id.pic);
            imgLoader.DisplayImage(session.getUserImg(), userImg);
        }
        navUser.setText("HELLO " + session.getUser().toUpperCase());
        phone.setText(session.getUserNo());
        topBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayProfile();
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

        if (session.isBothTypeUser()) {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.upgrade).setVisible(false);
        } else {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.upgrade).setVisible(true);
        }
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

    private void upgradeAccountPrompt() {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(PassengerHome.this, true);
        mDialog.setDialogTitle("BookMyRide");
        mDialog.setDialogMessage("Do you want to upgrade your account?");
        mDialog.setNegativeButton(getResources().getString(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                startActivity(new Intent(getApplicationContext(), Location.class)
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

    private void displaySelectedScreen(int itemId) {
        Fragment fragment = null;
        switch (itemId) {
            case R.id.favorite_driver:
                mTitle.setText("Favourite Drivers");
                fragment = new DriverList();
                break;
            case R.id.upgrade:
                upgradeAccountPrompt();
                break;
            case R.id.notification:
                mTitle.setText("Notifications");
                fragment = new Notifications();
                break;
            case R.id.book_ride:
                mTitle.setText("BookMyRide");
                fragment = new RideMap();
                break;
            case R.id.payment_info:
                mTitle.setText("Payment History");
                fragment = new PaymentSummary();
                break;
            case R.id.my_rides:
                mTitle.setText("My Rides");
                fragment = new RideSummary();
                break;
            case R.id.my_card:
                //startActivity(new Intent(this, MyCard.class));
                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                mTitle.setText("Charge Me");
                fragment = new ChargeMe();
                break;
            case R.id.view_wallet:
                mTitle.setText("BookMyRide Wallet");
                fragment = new ReferralAmount();
                break;
            case R.id.recharge_wallet:
                mTitle.setText("Recharge Wallet");
                fragment = new RechargeWallet();
                break;
            case R.id.invite_earn:
                mTitle.setText("Invite & Earn");
                fragment = new InviteAndEarn();
                break;
            case R.id.emergency_contact:
                mTitle.setText("Emergency Contact");
                fragment = new EmergencyContact();
                break;
            case R.id.change_pass:
                mTitle.setText("Change Password");
                fragment = new ChangePassword();
                break;
            case R.id.settings:
                //mTitle.setText("Settings");
                //fragment = new Settings();
                deactivateAccountDialog();
                break;
            case R.id.about:
                mTitle.setText("About BookMyRide");
                fragment = new About();
                break;
            case R.id.logout:
                quitDialog();
                //displayProfile();
                break;
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commitAllowingStateLoss();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void deactivateAccountDialog() {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(PassengerHome.this, true);
        mDialog.setDialogTitle("Deactivate Account");
        mDialog.setDialogMessage(getResources().getString(R.string.del_profile_msg));
        mDialog.setNegativeButton(getResources().getString(R.string.profile_lable_logout_yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                APIHandler apiHandler = new APIHandler(PassengerHome.this, HTTPMethods.PUT, new AsyncTaskCompleteListener() {
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

    private void displayProfile() {
        startActivity(new Intent(this, PassengerSignup.class).putExtra("profile", ""));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //calling the method display selected screen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }

    private void quitDialog() {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(PassengerHome.this, true);
        mDialog.setDialogTitle("BookMyRide");
        mDialog.setDialogMessage(getResources().getString(R.string.profile_lable_logout_message));
        mDialog.setNegativeButton(getResources().getString(R.string.profile_lable_logout_yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                APIHandler apiHandler = new APIHandler(PassengerHome.this, HTTPMethods.POST, new AsyncTaskCompleteListener() {
                    @Override
                    public void onTaskComplete(String result) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            if (obj.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                                session.saveCardExist(false);
                                session.saveUserActualType("");
                                session.saveToken("");
                                session.clearProfile();
                                session.clearLocation();
                                session.clearAddress();
                                session.saveOnlineStatus(false);
                                stopService(new Intent(PassengerHome.this, LocationService.class));
                                stopService(new Intent(PassengerHome.this, RouteService.class));
                                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                PassengerHome.this.finish();
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

    private void Alert(final String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(PassengerHome.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (title.equals("Success!")) {
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
                    PassengerHome.this.finish();
                }
            }
        });
        mDialog.show();
    }

    private void showExitDialog() {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(PassengerHome.this, true);
        mDialog.setDialogTitle("BookMyRide");
        mDialog.setDialogMessage("Do you want to quit the application?");
        mDialog.setNegativeButton(getResources().getString(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                PassengerHome.this.finish();
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

    private void confirmationPopup() {
        Intent intent = new Intent(this, Confirmation.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("rideData", rideData);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void acceptPopup(String message) {
        Intent intent = new Intent(this, RideCancelledDialog.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("message", message);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 929;

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
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    PassengerHome.this.finish();
                }
                break;
        }
    }
}

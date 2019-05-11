package com.grabid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.common.Utils;
import com.grabid.models.Delivery;
import com.grabid.models.PreviewField;
import com.grabid.models.UserInfo;
import com.grabid.util.FileOperation;


import com.grabid.util.StorePath;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.grabid.common.AlertManager.messageDialog;

/**
 * Created by graycell on 7/2/18.
 */

public class PreviewDelivery extends Fragment implements View.OnClickListener,
        AsyncTaskCompleteListener {
    EditText holderName, cardNumber, cvv;
    TextView auctionStatus, shipperName;
    TextView deliveryTitle, puContactPerson, puMobile, puDateTime, doContactPerson,
            doMobile, doDateTime, auctionStart, auctionEnd, maxOpeningBid,
            puAddress, doAddress, submit, puBuildType, puLiftEquipment, mRelist,
            doBuildType, doCall, doAppoint, doLiftEquipment, delivery, deliveryType,
            senderName, receiverName, bidTitle, suitableVehicle, totalQty;
    LinearLayout layShipper, laySender, layreceiver, layBids, layBid, layQty,
            laySuitableVehicle, layItems;
    LinearLayout layAuction;
    SessionManager session;
    String type, deliveryID, bookmarkID, shipperID, deliveryTypeID;
    RatingBar shipperRating;
    ImageView itemImage, detail, receiverSign, senderSign, bookmark, msg, call;
    String incomingType = "";
    String incoming_delivery_type = "";
    Delivery deliveryData;
    UserInfo userInfo;
    ImageView copy_delivery;
    RelativeLayout shipperPic;
    TextView pu_date_type, do_date_type;
    String auctionDateTime = "";
    int auctionType;
    boolean mFirstUpdate = true;
    boolean mIsAuctionEnded = false;
    LinearLayout sBidInfo;
    TextView sBidderPrice;
    boolean mAuction = false;
    TextView mExport;
    TextView mWithDraw, mAddFavourite;
    Target target;
    Date currentDatee;
    PowerManager.WakeLock wakeLock;
    String pickUpMobileStr = "", dropoffMobileStr = "";
    //int px;
    ProgressDialog dialog;
    String encoded_image = null;
    HashMap<String, String> mMap = new HashMap<String, String>();


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void UpdateDesign() {
        if (this.getArguments().containsKey("hashmap") && this.getArguments().getSerializable("hashmap") != null) {
            HomeActivity.edit.setVisibility(View.VISIBLE);
            HomeActivity.title.setText(getResources().getString(R.string.detail));
            HomeActivity.track_delivery.setVisibility(View.VISIBLE);
            HomeActivity.markread.setVisibility(View.GONE);
            HomeActivity.filter.setVisibility(View.GONE);
            HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.seagreen));
            submit.setBackgroundColor(getResources().getColor(R.color.blue));
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
            HomeActivity.title.setTextColor(getResources().getColor(R.color.white));
            HomeActivity.track_delivery.setVisibility(View.GONE);
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US);
                Date systemDate = Calendar.getInstance().getTime();
                String myDate = dateFormat.format(systemDate);
                currentDatee = dateFormat.parse(myDate);
            } catch (Exception e) {
                e.toString();
            }
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("", "");
        currentDatee = new Date();
        try {
            PowerManager powerManager = (PowerManager) getActivity().getSystemService(getActivity().POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "MyWakelockTag");
            wakeLock.acquire();
        } catch (Exception e) {
            e.toString();
        }
       /* Resources r = getActivity().getResources();
        px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                5,
                r.getDisplayMetrics()
        );*/
    }


    @Override
    public void onStart() {
        super.onStart();
        //   HomeActivity.IsDeliveryInfo = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        // HomeActivity.IsDeliveryInfo = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HomeActivity.edit.setVisibility(View.VISIBLE);
        HomeActivity.title.setText(getResources().getString(R.string.detail));
        HomeActivity.track_delivery.setVisibility(View.VISIBLE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        mAuction = true;
        View view = inflater.inflate(R.layout.delivery_info, null);
        init(view);
        Bundle b = this.getArguments();
        if (b.containsKey("hashmap") && b.getSerializable("hashmap") != null) {
            deliveryID = getArguments().getString("id");
            mMap = (HashMap<String, String>) b.getSerializable("hashmap");
            DeliveryObject(mMap);
            if (session.getCurrentScreen().equals("edit") || session.getCurrentScreen().equals("editRelisting")) {
                submit.setText("UPDATE DELIVERY DETAILS");
            } else
                submit.setText("SUBMIT DELIVERY DETAILS");
            submit.setVisibility(View.VISIBLE);
            copy_delivery.setVisibility(View.GONE);
            HomeActivity.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    HashMap<String, Delivery> data = new HashMap<String, Delivery>();
                    data.put("data", deliveryData);
                    intent.putExtra("data", data);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        } else {
            if (getArguments().containsKey("delivery_id")) {
                deliveryID = getArguments().getString("delivery_id");
                incomingType = getArguments().getString("incoming_type");
                getDelivery();
            }
            if (getArguments().containsKey("data")) {
                HashMap<String, Delivery> map = (HashMap<String, Delivery>) getArguments().getSerializable("data");
                deliveryData = map.get("data");
                incomingType = getArguments().getString("incoming_type");
                incoming_delivery_type = getArguments().getString("incoming_delivery_type");
                deliveryID = deliveryData.getId();
                getDelivery();
            } else if (getArguments().containsKey("delivery_id")) {
                deliveryID = getArguments().getString("delivery_id");
                incomingType = getArguments().getString("incoming_type");
                incoming_delivery_type = getArguments().getString("incoming_delivery_type");
                //getDelivery();
            }
            if (incomingType.equals("bookmark") || incomingType.equals("search")) {
                if (!deliveryData.getUserID().equals(userInfo.getId())) {
                    HomeActivity.edit.setVisibility(View.GONE);
                    HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
                    submit.setBackgroundColor(getResources().getColor(R.color.seagreen));
                    HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
                    HomeActivity.title.setTextColor(getResources().getColor(R.color.black));
                    HomeActivity.track_delivery.setTextColor(getResources().getColor(R.color.blue));
                    if (deliveryData.getDeliveryStatus().equals("0") || deliveryData.getDeliveryStatus().equals("4")) { //pending
                        HomeActivity.track_delivery.setVisibility(View.GONE);
                    } else {
                        if (deliveryData.getDeliveryStatus().equals("3"))
                            HomeActivity.track_delivery.setText("Track Delivery");
                        else
                            HomeActivity.track_delivery.setText("Propose Directions");
                        HomeActivity.track_delivery.setVisibility(View.VISIBLE);
                    }
                } else {
                    HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.seagreen));
                    submit.setBackgroundColor(getResources().getColor(R.color.blue));
                    HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
                    HomeActivity.title.setTextColor(getResources().getColor(R.color.white));
                    HomeActivity.track_delivery.setTextColor(getResources().getColor(R.color.blue));
                    if (deliveryData.getDeliveryStatus().equals("4"))
                        HomeActivity.track_delivery.setVisibility(View.GONE);
                    if (deliveryData.getDeliveryStatus().equals("0")) { //pending
                        HomeActivity.edit.setVisibility(View.VISIBLE);
                        HomeActivity.edit.setBackgroundResource(R.drawable.edit_white);
                        HomeActivity.track_delivery.setVisibility(View.GONE);
                    } else {
                        HomeActivity.edit.setVisibility(View.GONE);
                        HomeActivity.track_delivery.setText("Track Delivery");
                        HomeActivity.track_delivery.setVisibility(View.VISIBLE);
                    }
                }
            }

            if (incomingType.equals("shipper")) {
                HomeActivity.edit.setBackgroundResource(R.drawable.edit_white);
                copy_delivery.setVisibility(View.VISIBLE);
                bookmark.setVisibility(View.INVISIBLE);
                call.setVisibility(View.GONE);
                msg.setVisibility(View.GONE);
                HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.seagreen));
                submit.setBackgroundColor(getResources().getColor(R.color.blue));
                HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
                HomeActivity.title.setTextColor(getResources().getColor(R.color.white));
                HomeActivity.track_delivery.setTextColor(getResources().getColor(R.color.white));

                if (incoming_delivery_type.equals("0")) { //pending
                    HomeActivity.edit.setVisibility(View.VISIBLE);
                    HomeActivity.track_delivery.setVisibility(View.GONE);
                    try {
                        String auctionstatusstr = auctionStatus.getText().toString();
                        if (auctionstatusstr != null) {
                            if (auctionstatusstr.contains("AUCTION ENDS IN") || auctionstatusstr.contains("AUCTION HAS ENDED")) {
                                HomeActivity.edit.setVisibility(View.GONE);
                            }
                        }
                    } catch (Exception e) {
                        e.toString();
                    }
                } else {
                    HomeActivity.track_delivery.setText("Track Delivery");
                    HomeActivity.track_delivery.setVisibility(View.VISIBLE);
                    HomeActivity.edit.setVisibility(View.GONE);
                    if (incoming_delivery_type.equals("4")) {
                        HomeActivity.track_delivery.setVisibility(View.GONE);
                        HomeActivity.edit.setVisibility(View.GONE);
                    }
                }
            } else if (incomingType.equals("driver")) {
                copy_delivery.setVisibility(View.GONE);
                if (deliveryData.getDeliveryStatus().equals("3")) {
                    bookmark.setVisibility(View.INVISIBLE);
                    mExport.setVisibility(View.VISIBLE);
                } else {
                    bookmark.setVisibility(View.VISIBLE);
                    mExport.setVisibility(View.GONE);
                }
                HomeActivity.edit.setVisibility(View.GONE);
                HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
                submit.setBackgroundColor(getResources().getColor(R.color.seagreen));
                HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
                HomeActivity.title.setTextColor(getResources().getColor(R.color.black));
                HomeActivity.track_delivery.setTextColor(getResources().getColor(R.color.blue));
                if (incoming_delivery_type.equals("0") || incoming_delivery_type.equals("4")) { //pending
                    HomeActivity.track_delivery.setVisibility(View.GONE);
                } else {
                    if (incoming_delivery_type.equals("3"))
                        HomeActivity.track_delivery.setText("Track Delivery");
                    else
                        HomeActivity.track_delivery.setText("Propose Directions");
                    HomeActivity.track_delivery.setVisibility(View.VISIBLE);
                }
            } else {
                try {
                    if (incoming_delivery_type != null && !incoming_delivery_type.contentEquals("null")) {
                        if (incoming_delivery_type.equals("10")) {
                            try {
                                if (deliveryData != null) {
                                    if (deliveryData.getDeliveryStatus().equals("0") || deliveryData.getDeliveryStatus().equals("4")) { //pending
                                        HomeActivity.track_delivery.setVisibility(View.GONE);
                                    } else {
                                        if (deliveryData.getDeliveryStatus().equals("3"))
                                            HomeActivity.track_delivery.setText("Track Delivery");
                                        else
                                            HomeActivity.track_delivery.setText("Propose Directions");
                                        HomeActivity.track_delivery.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    copy_delivery.setVisibility(View.GONE);
                                    HomeActivity.track_delivery.setVisibility(View.GONE);
                                    HomeActivity.edit.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.toString();
                            }
                        }
                    } else {
                        copy_delivery.setVisibility(View.GONE);
                        HomeActivity.track_delivery.setVisibility(View.GONE);
                        HomeActivity.edit.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.toString();
                }
            }
            HomeActivity.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        handler.removeCallbacks(runnable);
                    } catch (Exception ex) {
                    }
                    session.saveCurrentScreen("edit");
                    String backStateName = this.getClass().getName();
                    Bundle bundle = new Bundle();
                    Fragment fragment = new SubmitStepOne();
                    HashMap<String, Delivery> data = new HashMap<String, Delivery>();
                    data.put("data", deliveryData);
                    bundle.putSerializable("data", data);
                    fragment.setArguments(bundle);
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                }
            });
            HomeActivity.track_delivery.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View view) {
                    String intype = "";
                    if (deliveryData.getUserID().equals(userInfo.getId()))
                        intype = "shipper";
                    else
                        intype = "driver";
                    String backStateName = this.getClass().getName();
                    Bundle bundle = new Bundle();
                    HashMap<String, Delivery> data = new HashMap<String, Delivery>();
                    data.put("data", deliveryData);
                    bundle.putSerializable("deliveryID", deliveryID);
                    bundle.putSerializable("incoming_type", intype);
                    bundle.putSerializable("incoming_delivery_type", deliveryData.getDeliveryStatus());
                    Fragment fragment = new TrackDelivery();
                    fragment.setArguments(bundle);
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName).addToBackStack(null)
                            //.commit();
                            .commitAllowingStateLoss();
                }
            });
        }
        return view;
    }

    public void showGrabidDialog(String message) {
        final Dialog mDialog = new Dialog(getActivity());
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //   mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.reasonmessage);
        mDialog.setCanceledOnTouchOutside(true);
        final TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        TextView ok = (TextView) mDialog.findViewById(R.id.ok);
        TextView cancel = (TextView) mDialog.findViewById(R.id.cancel);
        final EditText reason = (EditText) mDialog.findViewById(R.id.reason);
        title.setText(message);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reasonstr = reason.getText().toString();
                if (reasonstr != null && !reasonstr.contentEquals("")) {
                    mDialog.dismiss();
                    cancelDeliveryUpcoming(reasonstr);
                } else
                    showMessage("Alert!", "Please Enter Reason.");
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });


        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        mDialog.show();
    }

    private void composeEmail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
        i.putExtra(Intent.EXTRA_SUBJECT, "Auto generated subject.");
        i.putExtra(Intent.EXTRA_TEXT, "This is auto generated content.");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            showMessage("Opps!!!", "There are no email clients installed.");
        }
    }

    LinearLayout actual_date, actual_do_date;
    TextView pu_actual_date, do_actual_date;

    private void init(View view) {
        try {
            session = new SessionManager(getActivity());
            userInfo = session.getUserDetails();
            bookmark = (ImageView) view.findViewById(R.id.bookmark);
            bookmark.setOnClickListener(this);
            call = (ImageView) view.findViewById(R.id.call);
            call.setOnClickListener(this);
            msg = (ImageView) view.findViewById(R.id.msg);
            msg.setOnClickListener(this);
            shipperName = (TextView) view.findViewById(R.id.shipper_name);
            sBidInfo = (LinearLayout) view.findViewById(R.id.sbidinfo);
            sBidderPrice = (TextView) view.findViewById(R.id.sbid_price);
            auctionStatus = (TextView) view.findViewById(R.id.auction_status);
            deliveryTitle = (TextView) view.findViewById(R.id.title);
            puContactPerson = (TextView) view.findViewById(R.id.pu_contact);
            puMobile = (TextView) view.findViewById(R.id.pu_mobile);
            puAddress = (TextView) view.findViewById(R.id.pu_address);
            puDateTime = (TextView) view.findViewById(R.id.pu_date);
            doContactPerson = (TextView) view.findViewById(R.id.do_contact);
            doAddress = (TextView) view.findViewById(R.id.do_address);
            doMobile = (TextView) view.findViewById(R.id.do_mobile);
            mWithDraw = (TextView) view.findViewById(R.id.needtowithdraw);
            mAddFavourite = (TextView) view.findViewById(R.id.addfavourite);
            doDateTime = (TextView) view.findViewById(R.id.do_date);
            auctionStart = (TextView) view.findViewById(R.id.auction_start);
            auctionEnd = (TextView) view.findViewById(R.id.auction_end);
            maxOpeningBid = (TextView) view.findViewById(R.id.max_bid_price);
            puBuildType = (TextView) view.findViewById(R.id.pu_build_type);
            puLiftEquipment = (TextView) view.findViewById(R.id.pu_lift_equip);
            doBuildType = (TextView) view.findViewById(R.id.do_build_type);
            doCall = (TextView) view.findViewById(R.id.do_call);
            doAppoint = (TextView) view.findViewById(R.id.do_appointment);
            doLiftEquipment = (TextView) view.findViewById(R.id.do_lift_equip);
            delivery = (TextView) view.findViewById(R.id.delivery);
            deliveryType = (TextView) view.findViewById(R.id.delivery_type);
            receiverName = (TextView) view.findViewById(R.id.receiver_name);
            senderName = (TextView) view.findViewById(R.id.sender_name);
            bidTitle = (TextView) view.findViewById(R.id.bid_title);
            suitableVehicle = (TextView) view.findViewById(R.id.suitable_vehicle);
            totalQty = (TextView) view.findViewById(R.id.total_qty);
            senderSign = (ImageView) view.findViewById(R.id.sender_sign);
            receiverSign = (ImageView) view.findViewById(R.id.receiver_sign);
            shipperPic = (RelativeLayout) view.findViewById(R.id.shipper_pic);
            layShipper = (LinearLayout) view.findViewById(R.id.lay_shipper);
            layAuction = (LinearLayout) view.findViewById(R.id.lay_auction);
            laySender = (LinearLayout) view.findViewById(R.id.lay_sender);
            layreceiver = (LinearLayout) view.findViewById(R.id.lay_receiver);
            layBids = (LinearLayout) view.findViewById(R.id.lay_bids);
            layBid = (LinearLayout) view.findViewById(R.id.lay_bid);
            layQty = (LinearLayout) view.findViewById(R.id.lay_qty);
            laySuitableVehicle = (LinearLayout) view.findViewById(R.id.lay_sutiable_vehicle);
            layItems = (LinearLayout) view.findViewById(R.id.lay_items);
            submit = (TextView) view.findViewById(R.id.submit);
            mRelist = (TextView) view.findViewById(R.id.relist);
            submit.setOnClickListener(this);
            mRelist.setOnClickListener(this);
            shipperRating = (RatingBar) view.findViewById(R.id.rating_shipper);
            detail = (ImageView) view.findViewById(R.id.detail);
            itemImage = (ImageView) view.findViewById(R.id.background);
            copy_delivery = (ImageView) view.findViewById(R.id.copy_delivery);
            pu_date_type = (TextView) view.findViewById(R.id.pu_date_type);
            do_date_type = (TextView) view.findViewById(R.id.do_date_type);
            mExport = (TextView) view.findViewById(R.id.export);
            actual_date = (LinearLayout) view.findViewById(R.id.actual_date);
            pu_actual_date = (TextView) view.findViewById(R.id.pu_actual_date);

            actual_do_date = (LinearLayout) view.findViewById(R.id.actual_do_date);
            do_actual_date = (TextView) view.findViewById(R.id.do_actual_date);

            mExport.setOnClickListener(this);
            detail.setOnClickListener(this);
            copy_delivery.setOnClickListener(this);
            mWithDraw.setOnClickListener(this);
            mAddFavourite.setOnClickListener(this);
            target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if (bitmap != null) {
                        try {
                            shipperPic.setBackground(new BitmapDrawable(bitmap));
                            // mainLayout.setBackground(new BitmapDrawable(context.getResources(), bitmap));
                        } catch (Exception e) {
                            e.toString();
                        }
                    }
                    // mainLayout.setBackground(new BitmapDrawable(context.getResources(), bitmap));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.d("TAG", "FAILED");

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Log.d("TAG", "Prepare Load");
                }
            };
            puMobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  doCallMessage(1, deliveryData.getPuMobile());
                    //     if (!deliveryData.getUserID().equals(userInfo.getId()))
                    showCallDialog(pickUpMobileStr);

                }
            });
            doMobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  doCallMessage(1, deliveryData.getDoMobile());
                    // if (!deliveryData.getUserID().equals(userInfo.getId()))
                    showCallDialog(dropoffMobileStr);

                }
            });
        } catch (Exception e) {
            e.toString();
        }
    }

    @Override
    public void onTaskComplete(String result) {
        Log.d(Config.TAG, result);
        handleResponse(result);
    }


    public void DeliveryObject(HashMap<String, String> mMap) {
        JSONObject deliveryObj = new JSONObject(mMap);
        try {
            Delivery delivery = new Delivery();
            delivery.setPaymentAmount("null");
            delivery.setBookmarked(false);
            delivery.setId(deliveryID);
            delivery.setBidStatus(deliveryObj.optString(Keys.BID_STATUS));
            delivery.setTitle(deliveryObj.optString(Keys.ITEM_DELIVERY_TITLE));
            delivery.setBidID(deliveryObj.optString(Keys.BID_ID));
            delivery.setDeliveryStatus("0");
            delivery.setDropoffAdress(deliveryObj.optString(Keys.DROPOFF_ADDRESS));
            delivery.setDriverID("null");
            delivery.setItemPhoto(deliveryObj.optString(Keys.ITEM_PHOTO));

            delivery.setObjData(deliveryObj.toString());
            delivery.setCompletedAt("null");
            delivery.setPickUpAddress(deliveryObj.optString(Keys.PICKUP_ADDRESS));
            delivery.setUserID(userInfo.getId());
            delivery.setPickUpDate(deliveryObj.optString(Keys.PICKUP_DATE));
            delivery.setDropOffDate(deliveryObj.optString(Keys.DROPOFF_DATE));
            delivery.setWidth(deliveryObj.optString(Keys.ITEM_WIDTH));
            delivery.setStatus(deliveryObj.optString(Keys.STATUS));
            delivery.setStackable(deliveryObj.optString(Keys.ITEM_STACKABLE));
            delivery.setReceiver("null");
            delivery.setReceiverSign("null");
            delivery.setSender("null");
            delivery.setSenderSign("null");
            delivery.setQty(deliveryObj.optString(Keys.ITEM_QTY));
            delivery.setWeight(deliveryObj.optString(Keys.ITEM_WEIGHT));
            delivery.setPuMobile(deliveryObj.optString(Keys.PICKUP_MOBILE));
            delivery.setPuContactPerson(deliveryObj.optString(Keys.PICKUP_CONTACT_PERSON));
            delivery.setDoMobile(deliveryObj.optString(Keys.DROPOFF_MOBILE));
            delivery.setPuLng(deliveryObj.optString(Keys.PICKUP_LONGITUDE));
            delivery.setPuLat(deliveryObj.optString(Keys.PICKUP_LATITUDE));
            delivery.setDoContactPerson(deliveryObj.optString(Keys.DROPOFF_CONTACT));
            delivery.setDoLat(deliveryObj.optString(Keys.DROPOFF_LATITUDE));
            delivery.setDoLng(deliveryObj.optString(Keys.DROPOFF_LONGITUDE));
            delivery.setPuLiftEquipment(deliveryObj.optString(Keys.PICKUP_LIFT_EQUIPMENT));
            delivery.setAnimalName(deliveryObj.optString(Keys.ITEM_ANIMAL_NAME));
            delivery.setBreed(deliveryObj.optString(Keys.ITEM_ANIMAL_BREED));
            delivery.setPuBuildType(deliveryObj.optString(Keys.PICKUP_BUILD_TYPE));
            delivery.setDoBuildType(deliveryObj.optString(Keys.DROPOFF_BUILD_TYPE));
            delivery.setDoLiftEquipment(deliveryObj.optString(Keys.DROPOFF_LIFT_EQUIPMENT));
            delivery.setFixedOffer(deliveryObj.optString(Keys.FIXED_OFFER));
            delivery.setAuctionStart(deliveryObj.optString(Keys.AUCTION_START_TIME));
            delivery.setAuctionEnd(deliveryObj.optString(Keys.AUCTION_END_TIME));
            delivery.setAuctionBid(deliveryObj.optString(Keys.AUCTION_BID));
            delivery.setGeo(deliveryObj.optString(Keys.GEO_ZONE));
            delivery.setRadius(deliveryObj.optString(Keys.RADIUS));
            delivery.setMaxOpeningBid(deliveryObj.optString(Keys.MAX_AUCTION_BID));
            delivery.setDoAppoint(deliveryObj.optString(Keys.DROPOFF_APPOINTMENT));
            delivery.setDoCall(deliveryObj.optString(Keys.DROPOFF_CALL));
            delivery.setMore(deliveryObj.optString(Keys.ITEM_MORE));
            try {
                delivery.setRelistNotification(deliveryObj.optString(Keys.RELIST_NOTIFICATION));
                delivery.setItem_delivery_other(deliveryObj.optString(Keys.ITEM_DELIVAR_OTHER));
                delivery.setPickUpComName(deliveryObj.optString(Keys.PICKUP_BUILD_COMPANYNAME));
                delivery.setDropOffComName(deliveryObj.optString(Keys.DROPOFF_BUILD_COMPANYNAME));
            } catch (Exception e) {
                e.toString();
            }
            if (!deliveryObj.has((Keys.ITEM_EQUIPMENT_TYPE_ID)) && deliveryObj.optString(Keys.ITEM_EQUIPMENT_TYPE_ID).equals("null")) {
                delivery.setDeliveryTypeSubID(deliveryObj.optString(Keys.ITEM_EQUIPMENT_TYPE_ID));
                delivery.setDeliverySubName(PreviewField.getDeliverySubType());
            } else {
                delivery.setDeliveryTypeSubID(deliveryObj.optString(Keys.ITEM_DELIVERY_TYPE_SUB_ID));
                delivery.setDeliverySubName(PreviewField.getDeliverySubType());
            }
            try {
                delivery.setFav_user_id(deliveryObj.optString(Keys.FAVOURITE_USER_GROUP_IDS));
            } catch (Exception e) {
                e.toString();
            }
            /*if (deliveryObj.get(Config.BOOKMARK).toString().equals("null")) {
            } else {
                delivery.setBookmarkID(deliveryObj.getJSONObject(Config.BOOKMARK).getString(Keys.KEY_ID));
            }*/
            delivery.setUser_Group(deliveryObj.optString(Keys.USER_GROUP));
            delivery.setDeliveryName(PreviewField.getDeliveryName());
            delivery.setDeliveryStatus("0");
            delivery.setDeliveryTypeID(deliveryObj.optString(Keys.DELIVERY_TYPE_ID));
            delivery.setDeliveryTypeSubSubID(deliveryObj.optString(Keys.ITEM_DELIVERY_TYPE_SUB_SUB_ID));
            delivery.setDeliveryTypeSubSubName(deliveryObj.optString(Keys.ITEM_DELIVERY_TYPE_SUB_SUB_NAME));
            delivery.setCurrentVaccination(deliveryObj.optString(Keys.ITEM_CURRENT_VACCINATIONS));
            delivery.setAnimalCarrier(deliveryObj.optString(Keys.ITEM_ANIMAL_CARRIER));
            delivery.setShipperName(userInfo.getUserName());
            delivery.setShipperImage(userInfo.getProfileImage());
            delivery.setShipperRating(userInfo.getShipperRating());
            delivery.setShipperID(userInfo.getId());
            try {
                //  if (deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).has(Keys.MOBILE))
                delivery.setSender_Mobile(userInfo.getMobile());
            } catch (Exception e) {
                e.toString();
            }
            delivery.setBidArray(deliveryObj.optString(Keys.BIDS));
            delivery.setChoosedBidsArray(deliveryObj.optString(Keys.BID_CHOOSED));
            // delivery.setHasDriverFeedback(deliveryObj.optString(Keys.FEEDBACK_TO_DRIVER).toString().equals("null") ? false : true);
            //  delivery.setHasShipperFeedback(deliveryObj.get(Keys.FEEDBACK_TO_SHIPPER).toString().equals("null") ? false : true);
            delivery.setItemsData(deliveryObj.opt(Keys.DELIVERY_ITEM).toString());
            delivery.setSuitableVehicles(PreviewField.getSuiotableVehicles());
            delivery.setDropoffDateType(deliveryObj.optString(Keys.DROPOFF_DAY_TYPE));
            delivery.setPickupDateType(deliveryObj.optString(Keys.PICKUP_DAY_TYPE));
            delivery.setDropOffEndDate(deliveryObj.optString(Keys.DROPOFF_END_DAY));
            delivery.setPickupEndDate(deliveryObj.optString(Keys.PICKUP_END_DAY));
            delivery.setPickupCountry(deliveryObj.optString(Keys.PICKUP_COUNTRY));
            delivery.setPickupState(deliveryObj.optString(Keys.PICKUP_STATE));
            delivery.setPickupCity(deliveryObj.optString(Keys.PICKUP_CITY));
            delivery.setDropoffCountry(deliveryObj.optString(Keys.DROPOFF_COUNTRY));
            delivery.setDropoffState(deliveryObj.optString(Keys.DROPOFF_STATE));
            delivery.setDropoffCity(deliveryObj.optString(Keys.DROPOFF_CITY));
            delivery.setPickupinductionRequire(deliveryObj.optString(Keys.PICKUP_INDUCTION_REQUIRE));
            delivery.setPickupSpecialRestriction(deliveryObj.optString(Keys.PICKUP_SPECIAL_RESTRICTION));
            delivery.setDropoffinductionRequire(deliveryObj.optString(Keys.DROPOFF_INDUCTION_REQUIRE));
            delivery.setDropoffSpecialRestriction(deliveryObj.optString(Keys.DROPOFF_SPECIAL_RESTRICTION));
            delivery.setSpecialPermit(deliveryObj.optString(Keys.SPECIAL_PERMIT));
            delivery.setSpecialPermitDetail(deliveryObj.optString(Keys.SPECIAL_PERMIT_DETAIL));
            delivery.setSuitabelVehicle(deliveryObj.optString(Keys.SUITABLE_VEHICLE));
            try {
                delivery.setPickUpLiftEquiAvailableIds(PreviewField.getPickUpLiftingEquipmentAvIds());
                delivery.setDropOffLiftEquiAvailableIds(PreviewField.getDropOffLiftingEquipmentAvIds());
                delivery.setPickUpLiftEquiNeededIds(PreviewField.getPickUpLiftingEquipmentNeededIds());
                delivery.setDropOffLiftEquiNeededIds(PreviewField.getDropOffLiftingEquipmentNeddedIds());
                delivery.setPuLiftEquipmentText(deliveryObj.optString(Keys.PICKUP_LIFT_EQUIP_AVAILABLE_TEXT));
                delivery.setPuLiftEquipmentNeededText(deliveryObj.optString(Keys.PICKUP_LIFT_EQUIP_NEEDED_TEXT));
                delivery.setDoLiftEquipmentText(deliveryObj.optString(Keys.DROPOFF_LIFT_EQUIP_AVAILABLE_TEXT));
                delivery.setDoLiftEquipmentNeededText(deliveryObj.optString(Keys.DROPOFF_LIFT_EQUIP_NEEDED_TEXT));
                delivery.setFromPickUpAt(deliveryObj.optString("from_pick_up_at"));
                delivery.setCompleted_at(deliveryObj.optString("completed_at"));

            } catch (Exception e) {
                e.toString();

            }
            try {
                delivery.setPickUpBarcode(deliveryObj.optString(Keys.PICK_UPBARCODE));
                delivery.setDropOffBarCode(deliveryObj.optString(Keys.DROP_OFFBARCODE));
            } catch (Exception e) {
                e.toString();
            }
                        /*delivery.setFromPickUpAt(deliveryObj.getString("from_pick_up_at"));
                        delivery.setCompleted_at(deliveryObj.getString("completed_at"));*/
            /*try {
                if (!delivery.getDeliveryStatus().equals("0")) {
                    delivery.setDriver_name(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.USERNAME));
                    delivery.setDriver_image(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.PROFILE_IMAGE));
                    delivery.setDriver_rating(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.RATING));
                    delivery.setDriver_id(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.KEY_ID));
                    delivery.setIsFavouriteUser(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.ISFAVOURITE_USER));

                }
            } catch (Exception e) {
                e.toString();
            }*/
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US);
                Date systemDate = Calendar.getInstance().getTime();
                String myDate = dateFormat.format(systemDate);
                delivery.setUser_time(myDate);
                currentDatee = dateFormat.parse(myDate);

            } catch (Exception e) {
                e.toString();
            }
            delivery.setIsDriver(false);
            this.deliveryData = delivery;
            if (!deliveryData.getAuctionStart().equals("") && !deliveryData.getAuctionStart().equals("0") && !deliveryData.getAuctionStart().equals("null")) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
                    Date endDate = dateFormat.parse(deliveryData.getAuctionEnd());
                    if (!(endDate.after(currentDatee))) {
                        mIsAuctionEnded = true;
                    }

                } catch (Exception e) {
                    e.toString();
                }
            }
           /* if (incomingType.equals("notification") || incomingType.equals("home") || incomingType.equals("bank") || incomingType.equals("card")
                    || incomingType.equals("feedback")) {

                Log.v("_Id", "_Id" + new SessionManager(getActivity()).getUserDetails().getId());
                Log.v("KEY_USER_ID", "KEY_USER_ID" + deliveryObj.getString(Keys.KEY_USER_ID));
                Log.v("getIsDriver", "getIsDriver" + deliveryData.getIsDriver());
                Log.v("getDeliveryStatus", "getDeliveryStatus" + deliveryData.getDeliveryStatus());*/
            if (deliveryData.getIsDriver()) {
                copy_delivery.setVisibility(View.GONE);
                HomeActivity.edit.setVisibility(View.GONE);
                bookmark.setVisibility(View.VISIBLE);
                HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
                submit.setBackgroundColor(getResources().getColor(R.color.seagreen));
                HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
                HomeActivity.title.setTextColor(getResources().getColor(R.color.black));
                HomeActivity.track_delivery.setTextColor(getResources().getColor(R.color.blue));
                if (deliveryData.getDeliveryStatus().equals("0") || deliveryData.getDeliveryStatus().equals("4")) { //pending
                    HomeActivity.track_delivery.setVisibility(View.GONE);
                } else {
                    try {
                        if (deliveryData.getDeliveryStatus().equals("1")) {
                            HomeActivity.track_delivery.setText("Propose Directions");
                            HomeActivity.track_delivery.setVisibility(View.VISIBLE);
                        } else if (deliveryData.getDriverID() != null && !deliveryData.getDriverID().equals(userInfo.getId())) {
                            HomeActivity.track_delivery.setVisibility(View.GONE);
                        } else {
                            if (deliveryData.getDeliveryStatus().equals("3"))
                                HomeActivity.track_delivery.setText("Track Delivery");
                            else
                                HomeActivity.track_delivery.setText("Propose Directions");
                            HomeActivity.track_delivery.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.toString();
                    }
                }

            } else {
                bookmark.setVisibility(View.INVISIBLE);
                HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.seagreen));
                submit.setBackgroundColor(getResources().getColor(R.color.blue));
                HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
                HomeActivity.title.setTextColor(getResources().getColor(R.color.white));
                HomeActivity.track_delivery.setTextColor(getResources().getColor(R.color.blue));
                if (deliveryData.getDeliveryStatus().equals("4"))
                    HomeActivity.edit.setVisibility(View.VISIBLE);

                if (deliveryData.getDeliveryStatus().equals("0")) { //pending
                    HomeActivity.edit.setVisibility(View.VISIBLE);
                    HomeActivity.edit.setBackgroundResource(R.drawable.edit_white);
                    HomeActivity.track_delivery.setVisibility(View.GONE);
                } else {
                    HomeActivity.edit.setVisibility(View.GONE);
                    HomeActivity.track_delivery.setText("Track Delivery");
                    HomeActivity.track_delivery.setVisibility(View.VISIBLE);
                    if (deliveryData.getDeliveryStatus().equals("4")) {
                        HomeActivity.track_delivery.setVisibility(View.GONE);
                    }
                }
            }
            //  }
            appendData();
        } catch (Exception e) {
            e.toString();
        }

    }

    private void handleResponse(String result) {
        Log.v("result", "result info:.." + result);
        if (type.equals("addfav")) {
            try {
                JSONObject outJson = new JSONObject(result);

                if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                    showSuccessMessage(outJson.getString(Config.MESSAGE));
                }
            } catch (Exception e) {
                e.toString();
            }
        }
        if (type.equals("delete")) {
            if (Integer.parseInt(result) == APIStatus.SUCCESS) {
                bookmark.setTag("0");
                showSuccessMessage("Delivery has been unbookmarked successfully");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    bookmark.setImageDrawable(getResources().getDrawable(R.drawable.banner_icon, getActivity().getTheme()));
                } else {
                    bookmark.setImageDrawable(getResources().getDrawable(R.drawable.banner_icon));
                }
            } else
                showMessage("Error!", getResources().getString(R.string.fail));
        } else if (type.equals("change_notification"))

        {
            try {
                JSONObject outJson = new JSONObject(result);
                if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                    JSONObject dataObj = outJson.optJSONObject(Config.DATA);
                    if (dataObj.has("count")) {
                        String count = dataObj.optString("count");
                        if (dataObj.optString("count") != null) {
                            session.saveCount(count);
                            if (count.contentEquals("0")) {
                                HomeActivity.badge.hide();
                            } else {
                                HomeActivity.badge.setText(count);
                                HomeActivity.badge.show();
                            }
                        }
                    }
                }
               /* if (session.getReadStatus().contentEquals("0")) {
                    if (session.getCount() != null && !session.getCount().contentEquals("")) {
                        int count = Integer.parseInt(session.getCount());
                        if (count > 0) {
                            count = count - 1;
                            String countstr = String.valueOf(count);
                            session.saveCount(countstr);
                            if (countstr.contentEquals("0")) {
                                HomeActivity.badge.hide();
                            } else {
                                HomeActivity.badge.setText(countstr);
                                HomeActivity.badge.show();
                            }
                        }
                    }
                }*/
                Log.v("result", "result" + result);
            } catch (Exception e) {

            }

        } else {
            try {
                JSONObject outJson = new JSONObject(result);
                if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                    if (type.equalsIgnoreCase("submitDelivery"))
                        showSuccessMessage("Delivery Details have been submitted.");
                    if (type.equalsIgnoreCase("updateDelivery"))
                        showSuccessMessage("Delivery Details have been updated.");

                    if (type.equals("add")) {
                        bookmark.setTag("1");
                        showSuccessMessage("Delivery has been bookmarked successfully");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            bookmark.setImageDrawable(getResources().getDrawable(R.drawable.banner_icon_active, getActivity().getTheme()));
                        } else {
                            bookmark.setImageDrawable(getResources().getDrawable(R.drawable.banner_icon_active));
                        }
                        try {
                            JSONObject dataObj = outJson.getJSONObject(Config.DATA);
                            bookmarkID = dataObj.optString("id");
                        } catch (Exception e) {
                            e.toString();
                        }
                    } else if (type.equals("cancel")) {
                        showSuccessMessage("Your delivery has been cancelled successfully.");
                    } else if (type.equals("cancelupcoming")) {
                        if (deliveryData.getUserID().equals(userInfo.getId()))
                            showSuccessMessage("Your delivery has been cancelled successfully.");
                        else
                            showSuccessMessage("You have successfully withdraw from this delivery.");
                    } else if (type.equals("bid")) {
                        showSuccessMessage("Your bid has been submitted successfully.");
                    } else if (type.equals("choose")) {
                        showSuccessMessage(outJson.getString(Config.MESSAGE));
                    } else if (type.equals("take_now")) {
//                        showMessageDialog("Your request is submitted successfully.");
                        showMessageDialog(outJson.getString(Config.MESSAGE));
                    } else if (type.equals("get")) {
                        JSONObject dataObj = outJson.getJSONObject(Config.DATA);
                        JSONObject deliveryObj = dataObj.getJSONObject(Config.DELIVERY);
                        Delivery delivery = new Delivery();
                        delivery.setPaymentAmount(deliveryObj.getString(Keys.PAYMENT_AMOUNT));
                        delivery.setBookmarked((deliveryObj.get(Config.BOOKMARK).toString().equals("null")) ? false : true);
                        delivery.setId(deliveryObj.getString(Keys.KEY_ID));
                        delivery.setBidStatus(deliveryObj.get(Keys.BID_STATUS).toString());
                        delivery.setTitle(deliveryObj.getString(Keys.ITEM_DELIVERY_TITLE));
                        delivery.setBidID(deliveryObj.getString(Keys.BID_ID));
                        delivery.setDeliveryStatus(deliveryObj.getString(Keys.DELIVERY_STATUS));
                        delivery.setDropoffAdress(deliveryObj.getString(Keys.DROPOFF_ADDRESS));
                        delivery.setDriverID(deliveryObj.getString(Keys.DRIVER_ID));
                        delivery.setItemPhoto(deliveryObj.getString(Keys.ITEM_PHOTO));

                        delivery.setObjData(deliveryObj.toString());
                        delivery.setCompletedAt(deliveryObj.getString(Keys.COMPLETED_AT));
                        delivery.setPickUpAddress(deliveryObj.getString(Keys.PICKUP_ADDRESS));
                        delivery.setUserID(deliveryObj.getString(Keys.KEY_USER_ID));
                        delivery.setPickUpDate(deliveryObj.getString(Keys.PICKUP_DATE));
                        delivery.setDropOffDate(deliveryObj.getString(Keys.DROPOFF_DATE));
                        delivery.setWidth(deliveryObj.getString(Keys.ITEM_WIDTH));
                        delivery.setStatus(deliveryObj.getString(Keys.STATUS));
                        delivery.setStackable(deliveryObj.getString(Keys.ITEM_STACKABLE));
                        delivery.setReceiver(deliveryObj.getString(Keys.RECEIVER_NAME));
                        delivery.setReceiverSign(deliveryObj.getString(Keys.RECEIVER_SIGN));
                        delivery.setSender(deliveryObj.getString(Keys.FROM_PICKUP_NAME));
                        delivery.setSenderSign(deliveryObj.getString(Keys.FROM_PICKUP_SIGN));
                        delivery.setQty(deliveryObj.getString(Keys.ITEM_QTY));
                        delivery.setWeight(deliveryObj.getString(Keys.ITEM_WEIGHT));
                        delivery.setPuMobile(deliveryObj.getString(Keys.PICKUP_MOBILE));
                        delivery.setPuContactPerson(deliveryObj.getString(Keys.PICKUP_CONTACT_PERSON));
                        delivery.setDoMobile(deliveryObj.getString(Keys.DROPOFF_MOBILE));
                        delivery.setPuLng(deliveryObj.getString(Keys.PICKUP_LONGITUDE));
                        delivery.setPuLat(deliveryObj.getString(Keys.PICKUP_LATITUDE));
                        delivery.setDoContactPerson(deliveryObj.getString(Keys.DROPOFF_CONTACT));
                        delivery.setDoLat(deliveryObj.getString(Keys.DROPOFF_LATITUDE));
                        delivery.setDoLng(deliveryObj.getString(Keys.DROPOFF_LONGITUDE));
                        delivery.setPuLiftEquipment(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIPMENT));
                        delivery.setAnimalName(deliveryObj.getString(Keys.ITEM_ANIMAL_NAME));
                        delivery.setBreed(deliveryObj.getString(Keys.ITEM_ANIMAL_BREED));
                        delivery.setPuBuildType(deliveryObj.getString(Keys.PICKUP_BUILD_TYPE));
                        delivery.setDoBuildType(deliveryObj.getString(Keys.DROPOFF_BUILD_TYPE));
                        delivery.setDoLiftEquipment(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIPMENT));
                        delivery.setFixedOffer(deliveryObj.getString(Keys.FIXED_OFFER));
                        delivery.setAuctionStart(deliveryObj.getString(Keys.AUCTION_START_TIME));
                        delivery.setAuctionEnd(deliveryObj.getString(Keys.AUCTION_END_TIME));
                        delivery.setAuctionBid(deliveryObj.getString(Keys.AUCTION_BID));
                        delivery.setGeo(deliveryObj.getString(Keys.GEO_ZONE));
                        delivery.setRadius(deliveryObj.getString(Keys.RADIUS));
                        delivery.setMaxOpeningBid(deliveryObj.getString(Keys.MAX_AUCTION_BID));
                        delivery.setDoAppoint(deliveryObj.getString(Keys.DROPOFF_APPOINTMENT));
                        delivery.setDoCall(deliveryObj.getString(Keys.DROPOFF_CALL));
                        delivery.setMore(deliveryObj.getString(Keys.ITEM_MORE));
                        delivery.setUser_Group(deliveryObj.getString(Keys.USER_GROUP));
                        try {
                            delivery.setRelistNotification(deliveryObj.getString(Keys.RELIST_NOTIFICATION));
                            delivery.setItem_delivery_other(deliveryObj.getString(Keys.ITEM_DELIVAR_OTHER));
                            delivery.setPickUpComName(deliveryObj.getString(Keys.PICKUP_BUILD_COMPANYNAME));
                            delivery.setDropOffComName(deliveryObj.getString(Keys.DROPOFF_BUILD_COMPANYNAME));
                        } catch (Exception e) {
                            e.toString();
                        }
                        if (!deliveryObj.getString(Keys.ITEM_EQUIPMENT_TYPE_ID).equals("null")) {
                            delivery.setDeliveryTypeSubID(deliveryObj.getString(Keys.ITEM_EQUIPMENT_TYPE_ID));
                            delivery.setDeliverySubName(deliveryObj.getString(Keys.ITEM_EQUIPMENT_TYPE_NAME));
                        } else {
                            delivery.setDeliveryTypeSubID(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_ID));
                            delivery.setDeliverySubName(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_NAME));
                        }
                        if (deliveryObj.get(Config.BOOKMARK).toString().equals("null")) {
                        } else {
                            delivery.setBookmarkID(deliveryObj.getJSONObject(Config.BOOKMARK).getString(Keys.KEY_ID));
                        }
                        try {
                            delivery.setFav_user_id(deliveryObj.getString(Keys.FAVOURITE_USER_GROUP_IDS));
                        } catch (Exception e) {
                            e.toString();
                        }
                        delivery.setDeliveryName(deliveryObj.getString(Keys.DELIVERY_TYPE_NAME));
                        delivery.setDeliveryStatus(deliveryObj.getString(Keys.DELIVERY_STATUS));
                        delivery.setDeliveryTypeID(deliveryObj.getString(Keys.DELIVERY_TYPE_ID));
                        delivery.setDeliveryTypeSubSubID(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_SUB_ID));
                        delivery.setDeliveryTypeSubSubName(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_SUB_NAME));
                        delivery.setCurrentVaccination(deliveryObj.getString(Keys.ITEM_CURRENT_VACCINATIONS));
                        delivery.setAnimalCarrier(deliveryObj.getString(Keys.ITEM_ANIMAL_CARRIER));
                        delivery.setShipperName(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.USERNAME));
                        delivery.setShipperImage(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.PROFILE_IMAGE));
                        delivery.setShipperRating(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.RATING));
                        delivery.setShipperID(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.KEY_ID));
                        try {
                            if (deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).has(Keys.MOBILE))
                                delivery.setSender_Mobile(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.MOBILE));
                        } catch (Exception e) {
                            e.toString();
                        }
                        delivery.setBidArray(deliveryObj.get(Keys.BIDS).toString());
                        delivery.setChoosedBidsArray(deliveryObj.get(Keys.BID_CHOOSED).toString());
                        delivery.setHasDriverFeedback(deliveryObj.get(Keys.FEEDBACK_TO_DRIVER).toString().equals("null") ? false : true);
                        delivery.setHasShipperFeedback(deliveryObj.get(Keys.FEEDBACK_TO_SHIPPER).toString().equals("null") ? false : true);
                        delivery.setItemsData(deliveryObj.get(Keys.DELIVERY_ITEMS).toString());
                        delivery.setSuitableVehicles(deliveryObj.get(Keys.SUITABLE_VEHICLE_TEXT).toString());
                        delivery.setDropoffDateType(deliveryObj.getString(Keys.DROPOFF_DAY_TYPE));
                        delivery.setPickupDateType(deliveryObj.getString(Keys.PICKUP_DAY_TYPE));
                        delivery.setDropOffEndDate(deliveryObj.getString(Keys.DROPOFF_END_DAY));
                        delivery.setPickupEndDate(deliveryObj.getString(Keys.PICKUP_END_DAY));
                        delivery.setPickupCountry(deliveryObj.getString(Keys.PICKUP_COUNTRY));
                        delivery.setPickupState(deliveryObj.getString(Keys.PICKUP_STATE));
                        delivery.setPickupCity(deliveryObj.getString(Keys.PICKUP_CITY));
                        delivery.setDropoffCountry(deliveryObj.getString(Keys.DROPOFF_COUNTRY));
                        delivery.setDropoffState(deliveryObj.getString(Keys.DROPOFF_STATE));
                        delivery.setDropoffCity(deliveryObj.getString(Keys.DROPOFF_CITY));
                        delivery.setPickupinductionRequire(deliveryObj.getString(Keys.PICKUP_INDUCTION_REQUIRE));
                        delivery.setPickupSpecialRestriction(deliveryObj.getString(Keys.PICKUP_SPECIAL_RESTRICTION));
                        delivery.setDropoffinductionRequire(deliveryObj.getString(Keys.DROPOFF_INDUCTION_REQUIRE));
                        delivery.setDropoffSpecialRestriction(deliveryObj.getString(Keys.DROPOFF_SPECIAL_RESTRICTION));
                        delivery.setSpecialPermit(deliveryObj.getString(Keys.SPECIAL_PERMIT));
                        delivery.setSpecialPermitDetail(deliveryObj.getString(Keys.SPECIAL_PERMIT_DETAIL));
                        delivery.setSuitabelVehicle(deliveryObj.getString(Keys.SUITABLE_VEHICAL_IDS));
                        try {
                            delivery.setPickUpLiftEquiAvailableIds(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_AVAILABLE_IDS));
                            delivery.setDropOffLiftEquiAvailableIds(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_AVAILABLE_IDS));
                            delivery.setPickUpLiftEquiNeededIds(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_NEEDED_IDS));
                            delivery.setDropOffLiftEquiNeededIds(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_NEEDED_IDS));
                            delivery.setPuLiftEquipmentText(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_AVAILABLE_TEXT));
                            delivery.setPuLiftEquipmentNeededText(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_NEEDED_TEXT));
                            delivery.setDoLiftEquipmentText(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_AVAILABLE_TEXT));
                            delivery.setDoLiftEquipmentNeededText(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_NEEDED_TEXT));
                            delivery.setFromPickUpAt(deliveryObj.getString("from_pick_up_at"));
                            delivery.setCompleted_at(deliveryObj.getString("completed_at"));

                        } catch (Exception e) {
                            e.toString();

                        }
                        try {
                            delivery.setPickUpBarcode(deliveryObj.getString(Keys.PICK_UPBARCODE));
                            delivery.setDropOffBarCode(deliveryObj.getString(Keys.DROP_OFFBARCODE));
                        } catch (Exception e) {
                            e.toString();
                        }
                        /*delivery.setFromPickUpAt(deliveryObj.getString("from_pick_up_at"));
                        delivery.setCompleted_at(deliveryObj.getString("completed_at"));*/
                        try {
                            if (!delivery.getDeliveryStatus().equals("0")) {
                                delivery.setDriver_name(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.USERNAME));
                                delivery.setDriver_image(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.PROFILE_IMAGE));
                                delivery.setDriver_rating(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.RATING));
                                delivery.setDriver_id(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.KEY_ID));
                                delivery.setIsFavouriteUser(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.ISFAVOURITE_USER));

                            }
                        } catch (Exception e) {
                            e.toString();
                        }
                        try {
                            delivery.setUser_time(deliveryObj.getString(Keys.USER_TIME));
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US);
                            currentDatee = dateFormat.parse(delivery.getUser_time());
                        } catch (Exception e) {
                            e.toString();
                        }
                        if (deliveryObj.getString(Keys.KEY_USER_ID).equals(new SessionManager(getActivity()).getUserDetails().getId())) {
                            delivery.setIsDriver(false);
                        } else {
                            delivery.setIsDriver(true);
                        }
                        this.deliveryData = delivery;
                        if (!deliveryData.getAuctionStart().equals("0") && !deliveryData.getAuctionStart().equals("null")) {
                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
                                Date endDate = dateFormat.parse(deliveryData.getAuctionEnd());
                                if (!(endDate.after(currentDatee))) {
                                    mIsAuctionEnded = true;
                                }

                            } catch (Exception e) {
                                e.toString();
                            }
                        }
                        if (incomingType.equals("notification") || incomingType.equals("home") || incomingType.equals("bank") || incomingType.equals("card")
                                || incomingType.equals("feedback")) {
                            copy_delivery.setVisibility(View.GONE);
                            Log.v("_Id", "_Id" + new SessionManager(getActivity()).getUserDetails().getId());
                            Log.v("KEY_USER_ID", "KEY_USER_ID" + deliveryObj.getString(Keys.KEY_USER_ID));
                            Log.v("getIsDriver", "getIsDriver" + deliveryData.getIsDriver());
                            Log.v("getDeliveryStatus", "getDeliveryStatus" + deliveryData.getDeliveryStatus());
                            if (deliveryData.getIsDriver()) {
                                HomeActivity.edit.setVisibility(View.GONE);
                                bookmark.setVisibility(View.VISIBLE);
                                HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
                                submit.setBackgroundColor(getResources().getColor(R.color.seagreen));
                                HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
                                HomeActivity.title.setTextColor(getResources().getColor(R.color.black));
                                HomeActivity.track_delivery.setTextColor(getResources().getColor(R.color.blue));
                                if (deliveryData.getDeliveryStatus().equals("0") || deliveryData.getDeliveryStatus().equals("4")) { //pending
                                    HomeActivity.track_delivery.setVisibility(View.GONE);
                                } else {
                                    try {
                                        if (deliveryData.getDeliveryStatus().equals("1")) {
                                            HomeActivity.track_delivery.setText("Propose Directions");
                                            HomeActivity.track_delivery.setVisibility(View.VISIBLE);
                                        } else if (deliveryData.getDriverID() != null && !deliveryData.getDriverID().equals(userInfo.getId())) {
                                            HomeActivity.track_delivery.setVisibility(View.GONE);
                                        } else {
                                            if (deliveryData.getDeliveryStatus().equals("3"))
                                                HomeActivity.track_delivery.setText("Track Delivery");
                                            else
                                                HomeActivity.track_delivery.setText("Propose Directions");
                                            HomeActivity.track_delivery.setVisibility(View.VISIBLE);
                                        }
                                    } catch (Exception e) {
                                        e.toString();
                                    }
                                }

                            } else {
                                bookmark.setVisibility(View.INVISIBLE);
                                HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.seagreen));
                                submit.setBackgroundColor(getResources().getColor(R.color.blue));
                                HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
                                HomeActivity.title.setTextColor(getResources().getColor(R.color.white));
                                HomeActivity.track_delivery.setTextColor(getResources().getColor(R.color.blue));
                                if (deliveryData.getDeliveryStatus().equals("4"))
                                    HomeActivity.edit.setVisibility(View.VISIBLE);

                                if (deliveryData.getDeliveryStatus().equals("0")) { //pending
                                    HomeActivity.edit.setVisibility(View.VISIBLE);
                                    HomeActivity.edit.setBackgroundResource(R.drawable.edit_white);
                                    HomeActivity.track_delivery.setVisibility(View.GONE);
                                } else {
                                    HomeActivity.edit.setVisibility(View.GONE);
                                    HomeActivity.track_delivery.setText("Track Delivery");
                                    HomeActivity.track_delivery.setVisibility(View.VISIBLE);
                                    if (deliveryData.getDeliveryStatus().equals("4")) {
                                        HomeActivity.track_delivery.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                        appendData();
                    } else if (type.equals("driver_amount")) {
                        JSONObject dataObj = outJson.getJSONObject(Config.DATA);
                        String message;
                        String SubmiText = submit.getText().toString();
                        if (SubmiText != null && !SubmiText.isEmpty()) {
                            if (SubmiText.contentEquals("TAKE NOW"))
                                //   if (incomingType.equals("map"))
                                message = "Are you sure you want to accept this Take Now offer for this delivery? " +
                                        "\nReminder: if successful your payment for this delivery will be " + dataObj.getString("driver_amount");
                            else
                                message = "Are you sure you want to bid for this delivery? " +
                                        "\nReminder: if successful your payment for this delivery will be " + dataObj.getString("driver_amount");
                            showCancelDialog(message, 1);
                        }
                    }
                } else if (outJson.getInt(Config.STATUS) == APIStatus.UNPROCESSABLE) {
                    if (type.equals("bid") || type.equals("take_now")) {
                        try {
                            String message = outJson.getString(Config.MESSAGE);
                            JSONObject inJson = outJson.optJSONObject("data");
                            if (inJson != null && inJson.has("type")) {
                                try {
                                    String inJsonType = inJson.optString("type");
                                    AlertManager.BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", outJson.getString(Config.MESSAGE), this.getClass().getName(), inJsonType);
                                } catch (Exception e) {
                                    e.toString();
                                }
                            } else {
                                if (message != null && !message.contentEquals("")) {
                                    showMessage("Error", message);
                                }
                            }

                        } catch (Exception e) {
                            e.toString();
                        }
                       /* try {
                            String message = outJson.getString(Config.MESSAGE);
                            showMessage("Error", message);
                        } catch (Exception e) {
                            e.toString();
                        }*/
                    } else if (outJson.getString(Config.MESSAGE).equals("")) {
                        try {
                            String field = outJson.getJSONArray(Config.DATA).getJSONObject(0).getString(Config.FIELD);
                            if (field != null && field.contentEquals("auction_start_time"))
                                ShipmentAddressDialog(outJson.getJSONArray(Config.DATA).getJSONObject(0).getString(Config.MESSAGE));
                            else
                                showMessage("Alert!", outJson.getJSONArray(Config.DATA).getJSONObject(0).getString(Config.MESSAGE));
                        } catch (Exception e) {
                            e.toString();
                        }
                    } else if (!outJson.getString(Config.MESSAGE).equals("")) {
                        showMessage("Alert!", outJson.getString(Config.MESSAGE));
                    } else {
                        //showMessage("Error", outJson.getString(Config.MESSAGE));
                        try {
                            JSONObject inJson = outJson.optJSONObject("data");
                            String inJsonType = inJson.optString("type");
                            AlertManager.BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", outJson.getString(Config.MESSAGE), this.getClass().getName(), inJsonType);
                        } catch (Exception e) {
                            e.toString();
                        }
                    }
                } else if (outJson.getInt(Config.STATUS) == APIStatus.INVALID_CARD) {
                    try {
                        AlertManager.BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", outJson.getString(Config.MESSAGE), this.getClass().getName(), "5");
                    } catch (Exception e) {
                        e.toString();
                    }
                } else {
                    showMessage("Alert!", getResources().getString(R.string.no_response));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (incomingType.equals("notification")) {
                if (session.getReadStatus().contentEquals("0"))
                    changeNotificationStatus();
            }
        }
    }


    private void doGetCardTypes() {
        String url = Config.SERVER_URL + Config.CREDIT_CARD;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage(TITLE, getResources().getString(R.string.no_internet));
    }

    private boolean checktimings(boolean isAfter) {
        if (isAfter) {
            try {
                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());
                SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
//            String c_datetime = df1.format(c.getTime());
                String c_datetime = df1.format(currentDatee.getTime());
                String a_datetime = deliveryData.getAuctionEnd();
                String pattern = "yyyy-MM-dd hh:mm a";
                SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
                try {
                    Date date1 = sdf.parse(c_datetime);
                    Date date2 = sdf.parse(a_datetime);
                    if (date1.before(date2)) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.toString();
            }
        } else {
            try {
                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());
                SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
//            String c_datetime = df1.format(c.getTime());
                String c_datetime = df1.format(currentDatee.getTime());
                String a_datetime = deliveryData.getAuctionStart();
                String pattern = "yyyy-MM-dd hh:mm a";
                SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
                try {
                    Date date1 = sdf.parse(c_datetime);
                    Date date2 = sdf.parse(a_datetime);
                    if (date1.before(date2)) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.toString();
            }
        }
        return false;
    }

    public void doCallMessage(int type, String mobile) {
        try {
            if (type == 1) {
                String number = "tel:" + mobile;
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(number));
                startActivity(callIntent);
            } else {
                sendEmailMesage();
                /*Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:" + Uri.encode(mobileno)));
                startActivity(intent);*/

            }
        } catch (Exception e) {
            e.toString();
        }
    }

    /*public void sendEmail() {
        String link = "http://demo.grabidnow.com/frontend/web/delivery/pdf?id=" + deliveryData.getId();
        String message = "Hi \n" +
                "\n" +
                " Please download the pdf file of complete details for delivery" + deliveryData.getDeliveryName() + " by click on the below mentioned link. \n\n" + link;
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + ""));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, deliveryData.getDeliveryName());
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        // emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, link); //If you are using HTML in your body text
        startActivity(Intent.createChooser(emailIntent, "Share Pdf"));
    }*/
    public void sendEmail() {
        try {
//           String link = "http://dev.grabid.com.au/frontend/web/delivery/pdf?id=" + deliveryData.getId();
            String link = Config.BASE_URL + "/frontend/web/delivery/pdf?id=" + deliveryData.getId();
            if (deliveryData.getUserID().equals(userInfo.getId()))
                link = link + "&type=1";
            else
                link = link + "&type=2";
            String message = "Hi \n" +
                    "\n" +
                    " Please click on the link below to download the complete delivery details for \"" + deliveryData.getTitle() + "\"" + ": \n\n" + link + "\n\n" + "Thanks" + "\n" + "The GRABiD Team";
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + ""));
//        emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, deliveryData.getTitle());
            emailIntent.putExtra(Intent.EXTRA_TEXT, message);
            // emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, link); //If you are using HTML in your body text
            try {
                startActivity(Intent.createChooser(emailIntent, "Share Pdf"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.toString();
        }
    }

    public void sendEmailMesage() {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + ""));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, deliveryData.getTitle());
            try {
                startActivity(Intent.createChooser(emailIntent, "Share Message"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.toString();
        }
    }

    public void Add(String fav_user_id) {
        type = "addfav";
        HashMap<String, String> params = new HashMap<>();
        String url = Config.SERVER_URL + Config.ADD_FAVOURITE;
        params.put("fav_user_id", fav_user_id);
        Log.d("end", params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage("Alert!", getResources().getString(R.string.no_internet));
    }

    String item_pic = "";

    public void AsyncImageBase64() {

        new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(getActivity());
                dialog.setMessage("Please wait...");
                dialog.setCancelable(false);
                dialog.show();

            }

            @Override
            protected JSONArray doInBackground(Void... params) {
                JSONArray array = new JSONArray();
                try {
                    com.grabid.models.Shipment shipment = session.getShipment();
                    JSONArray jarray = new JSONArray(shipment.getDeliveryItem());
                    for (int i = 0; i < jarray.length(); i++) {
                        final JSONObject obj = (JSONObject) jarray.get(i);
                        try {
                            item_pic = "";
                            if (!obj.getString("item_photo").equals("")) {
                                if (obj.getString("item_photo").contains("http")) {
                                    Log.v("", "");
                                    URL newurl = new URL(obj.getString("item_photo"));

//                    URL newurl = new URL("http://demo.grabidnow.com/frontend/web/images/uploads/delivery/2a847c6e78ee338e3d678287e0d1e890.jpg");
                                    //   img_value = new URL(obj.getString("item_photo"));
                                    HttpURLConnection connection = (HttpURLConnection) newurl.openConnection();
                                    connection.setDoInput(true);
                                    connection.connect();
                                    InputStream input = connection.getInputStream();
                                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                                    encoded_image = Utils.encodeTobase64(myBitmap);
                                    Log.v("", encoded_image.toString());
                                    item_pic = encoded_image;
//                                    array.put(i, obj.getString("item_photo"));
                                    array.put(i, item_pic);
                                } else {
                                    item_pic = FileOperation.encodeFileToBase64Binary(obj.getString("item_photo"));
//                                    array.put(i, obj.getString("item_photo"));
                                    array.put(i, item_pic);
                                }
                            } else {
                                array.put(i, item_pic);
                            }

                        } catch (Exception ex) {
                            Log.v("data", "delivery item of more detail:" + ex.getMessage());
                        }
                    }


                } catch (Exception e) {
                    e.toString();
                }
                return array;
            }

            @Override
            protected void onPostExecute(JSONArray aVoid) {
                // update the UI (this is executed on UI thread)
                super.onPostExecute(aVoid);
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
//                                    item_pic = FileOperation.sendImg(obj.getString("item_photo"));
//                item_pic = aVoid;
                item_pic = "";
               /* if (type == 3)
                    submitShipment(aVoid);
                else
                    submitShipment(aVoid, deliveryID);*/
                //   Log.v("", aVoid);
                submitDelivery(aVoid, deliveryID);
            }
        }.execute();
    }

    public String getUpdatedShipment(String deliveryItem, JSONArray Jsonarray) {
        try {
            JSONArray jarray = new JSONArray(deliveryItem);
            Log.v("jarray", "jarray" + jarray.length());
            for (int i = 0; i < jarray.length(); i++) {
                final JSONObject obj = (JSONObject) jarray.get(i);
                /*try {
                    obj.put("id", obj.get("id"));
                    obj.put("item_quantity", obj.get("item_quantity"));
                    obj.put("item_weight", obj.get("item_weight"));
                    obj.put("item_length", obj.get("item_length"));
                    obj.put("item_width", obj.get("item_width"));
                    obj.put("item_height", obj.get("item_height"));
                    obj.put("item_stackable", obj.get("item_stackable"));
                    obj.put("item_hazardous", obj.get("item_hazardous"));
                } catch (Exception ex) {
                    Log.v("data", "delivery item of more detail:" + ex.getMessage());
                }
                obj.put("item_more_details", obj.get("item_more_details"));
                Log.v("item_photo", "item_photo" + obj.getString("item_photo"));*/
                if (!obj.getString("item_photo").equals("")) {
                    item_pic = (String) Jsonarray.get(i);
                    /*try {
                        if (obj.getString("item_photo").contains("http")) {
                            Log.v("", "");
                            item_pic = (String) Jarray.get(i);
                        } else
                            item_pic = (String) Jarray.get(i);
//                            item_pic = FileOperation.encodeFileToBase64Binary(obj.getString("item_photo"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                    obj.put("item_photo", item_pic);
                } else
                    obj.put("item_photo", "");

                jarray.put(i, obj);
            }
            return jarray.toString();
        } catch (Exception ex) {
            Log.v("Error:", "Error:" + ex.getMessage());
            return "";
        }

    }

    public void submitDelivery(JSONArray JsonPic, String id) {

        String url = "";
        if (submit.getText().toString().contains("SUBMIT")) {
            url = Config.SERVER_URL + Config.DELIVERIES;
            type = "submitDelivery";
        } else {
            type = "updateDelivery";
            try {
                if (session.getCurrentScreen().equals("editRelisting"))
                    url = Config.SERVER_URL + Config.DELIVERYRELIST + "/?id=" + id;
                else
                    url = Config.SERVER_URL + Config.DELIVERIES + "/" + id;
            } catch (Exception e) {
                e.toString();
            }
        }
        com.grabid.models.Shipment shipment = session.getShipment();
        mMap.remove(Keys.DELIVERY_ITEM);
        mMap.put(Keys.DELIVERY_ITEM, getUpdatedShipment(shipment.getDeliveryItem(), JsonPic));
        String item_picc = "";
        try {
            item_picc = FileOperation.encodeFileToBase64Binary(StorePath.ITEM_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMap.remove(Keys.ITEM_PHOTO);
        mMap.put(Keys.ITEM_PHOTO, item_picc);

        Log.d(Config.TAG, mMap.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall apiCall;
            if (submit.getText().toString().contains("SUBMIT"))
                apiCall = new RestAPICall(getActivity(), HTTPMethods.POST, this, mMap);
            else
                apiCall = new RestAPICall(getActivity(), HTTPMethods.PUT, this, mMap);
            apiCall.execute(url, session.getToken());
        } else {
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.needtowithdraw: {
                showCancelDialog("Are you sure you want to withdraw from the delivery?", 4);
                break;
            }
            case R.id.addfavourite:
                Add(deliveryData.getDriver_id());
                break;
            case R.id.export:
                sendEmail();
                break;
            case R.id.call:
                try {
                    StringBuilder str = new StringBuilder(deliveryData.getSender_Mobile());
                    str.insert(3, " ");
                    showCallDialog(str.toString());
                } catch (Exception e) {
                    e.toString();
                }
//                doCallMessage(1, deliveryData.getSender_Mobile());
                break;
            case R.id.msg:
                doCallMessage(2, "");
//                composeEmail();
                break;
            case R.id.detail:
                showShipperInfo();
                break;
            case R.id.submit:
                if (submit.getText().toString().equalsIgnoreCase("SUBMIT DELIVERY DETAILS") || submit.getText().toString().equalsIgnoreCase("UPDATE DELIVERY DETAILS")) {
                    AsyncImageBase64();
                }
                if (submit.getText().toString().equalsIgnoreCase("Take Now")) {
                    try {
                        if (userInfo.getBankDetail() == null)
                            AlertManager.BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savebankdetails), this.getClass().getName(), "3");
                        else if (userInfo.getBankDetail().contentEquals("null"))
                            AlertManager.BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savebankdetails), this.getClass().getName(), "3");
                        else if (userInfo.getCreditCard() == null)
                            AlertManager.BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savecreditdetails), this.getClass().getName(), "2");
                        else if (userInfo.getCreditCard().contentEquals("null"))
                            AlertManager.BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savecreditdetails), this.getClass().getName(), "2");
                        else
                            getDriverPrice(deliveryData.getFixedOffer());
                    } catch (Exception e) {
                        e.toString();
                    }

                    //showCancelDialog("Are you sure you want to accept this Take Now offer for this delivery?", 1);
                } else if (submit.getText().toString().equalsIgnoreCase("Cancel Delivery")) {
                    try {
                        if (deliveryData != null && deliveryData.getDeliveryStatus().equals("1"))
                            showCancelDialog("Are you sure you want to cancel this delivery?", 3);
                        else
                            showCancelDialog("Are you sure you want to cancel this delivery?", 0);
                    } catch (Exception e) {
                        e.toString();
                    }
                } else if (submit.getText().toString().equalsIgnoreCase("Bid Now")) {
                    if (checktimings(false)) {
                        showAuctionNotStart("This auction is not yet active.");
                        return;
                    }
                    try {
                        if (userInfo.getBankDetail() == null)
                            AlertManager.BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savebankdetailsbidnow), this.getClass().getName(), "3");
                        else if (userInfo.getBankDetail().contentEquals("null"))
                            AlertManager.BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savebankdetailsbidnow), this.getClass().getName(), "3");
                        else if (userInfo.getCreditCard() == null)
                            AlertManager.BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savecreditdetailsbidnow), this.getClass().getName(), "2");
                        else if (userInfo.getCreditCard().contentEquals("null"))
                            AlertManager.BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savecreditdetailsbidnow), this.getClass().getName(), "2");
                        else if (deliveryData.getAuctionBid().equals("0")) {
                            //bidNow("");
                            getDriverPrice(deliveryData.getFixedOffer());
                        } else
                            showBidNowDialog();
                    } catch (Exception e) {
                        e.toString();
                    }
                } else if (submit.getText().toString().equalsIgnoreCase("Confirm Delivery Pick up")) {
                    //showConfirmDialog("Are you sure you want to update delivery status to In-transit?");
                    showConfirmPickUp();
                } else if (submit.getText().toString().equalsIgnoreCase("Confirm Delivery Drop Off")) {
                    showConfirmDialog("Are you sure you want to update delivery status to Complete?");
                } else if (submit.getText().toString().equalsIgnoreCase("RATE SENDER")) {
                    addFeedback();
                } else if (submit.getText().toString().equalsIgnoreCase("RATE CARRIER")) {
                    addFeedback();
                }
                break;
            case R.id.bookmark:
                if (bookmark.getTag().toString().equals("0")) {
                    type = "add";
                    addToBookmark(deliveryID);
                } else {
                    type = "delete";
                    removeBookmark(bookmarkID);
                }
                break;

            case R.id.copy_delivery:
                try {
                    handler.removeCallbacks(runnable);
                } catch (Exception ex) {
                }
                session.saveCurrentScreen("copy");
                String backStateName = this.getClass().getName();
                Bundle bundle = new Bundle();
                Fragment fragment = new SubmitStepOne();
                HashMap<String, Delivery> data = new HashMap<String, Delivery>();
                data.put("data", deliveryData);
                bundle.putSerializable("data", data);
                fragment.setArguments(bundle);
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
                break;
            case R.id.relist:
                try {
                    handler.removeCallbacks(runnable);
                } catch (Exception ex) {
                }
                session.saveCurrentScreen("editRelisting");
                String backStateNamee = this.getClass().getName();
                Bundle bundlee = new Bundle();
                Fragment fragmentt = new SubmitStepOne();
                HashMap<String, Delivery> dataa = new HashMap<String, Delivery>();
                dataa.put("data", deliveryData);
                bundlee.putSerializable("data", dataa);
                fragmentt.setArguments(bundlee);
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragmentt, backStateNamee)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
                break;
        }


    }

    public Boolean checkZero(String val) {
        try {
            if (val.matches("[0]+"))
                return false;
            if (Double.parseDouble(val) > Double.parseDouble(deliveryData.getMaxOpeningBid()))
                return false;
        } catch (Exception e) {
            e.toString();
        }
        return true;
    }


    private void showBidNowDialog() {
        final Dialog alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_layout);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final EditText subEditText = (EditText) alertDialog.findViewById(R.id.bid_price);
        TextView proceed = (TextView) alertDialog.findViewById(R.id.proceed);
        TextView cancel = (TextView) alertDialog.findViewById(R.id.cancel);
        TextView bidTitle = (TextView) alertDialog.findViewById(R.id.bidtitle);
        bidTitle.setText("Maximum Opening Bid - $" + deliveryData.getMaxOpeningBid());
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subEditText.getText().toString().length() > 0) {
                    try {
                        if (checkZero(subEditText.getText().toString())) {
                            alertDialog.dismiss();
                            //bidNow(subEditText.getText().toString());
                            driverAmount = subEditText.getText().toString();
                            getDriverPrice(subEditText.getText().toString());
                        } else {
                            showMessage("Error!", "Please enter valid amount");
                        }
                    } catch (Exception e) {
                        e.toString();
                    }
                } else
                    showMessage("Alert!", "Please enter bid price.");
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private void showConfirmPickUp() {
        final Dialog alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.confirmpickup);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView proceed = (TextView) alertDialog.findViewById(R.id.proceed);
        TextView cancel = (TextView) alertDialog.findViewById(R.id.cancel);
        WebView Webview = (WebView) alertDialog.findViewById(R.id.textterm);
        Webview.loadUrl("file:///android_asset/pickupinstruction.html");
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                showConfirmDialog("Are you sure you want to update delivery status to In-transit?");
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                showCancelDialog("Are you sure you want to withdraw from the delivery?", 4);
            }
        });
        alertDialog.show();

    }

   /* private void showBidNowDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.dialog_layout, null);
        final EditText subEditText = (EditText) subView.findViewById(R.id.bid_price);
        TextView proceed = (TextView) subView.findViewById(R.id.proceed);
        TextView cancel = (TextView) subView.findViewById(R.id.cancel);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(subView);
        final AlertDialog alertDialog = builder.create();
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subEditText.getText().toString().length() > 0) {
                    alertDialog.dismiss();
                    //bidNow(subEditText.getText().toString());
                    driverAmount = subEditText.getText().toString();
                    getDriverPrice(subEditText.getText().toString());
                } else
                    showMessage("Alert!", "Please enter bid price.");
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }*/

    private void addFeedback() {
        String type = "shipper";
        String backStateName = this.getClass().getName();
        Bundle bundle = new Bundle();
        Fragment fragment = new AddFeedback();
        String submittext = submit.getText().toString();
        if (submittext != null && !submittext.isEmpty()) {
            if (submittext.contentEquals("RATE CARRIER")) {
                type = "shipper";
            } else {
                type = "driver";
            }
        }
        bundle.putString("delivery_id", deliveryID);
        bundle.putString("incoming_type", type);
        bundle.putString("backstack", "delivery_info");
        fragment.setArguments(bundle);
        getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private void showShipperInfo() {
        String backStateName = this.getClass().getName();
        Bundle bundle = new Bundle();
        Fragment fragment = new ShipperInfo();
        if (((deliveryData.getUserID().equals(userInfo.getId())) && (!deliveryData.getDeliveryStatus().equals("0")) && (!deliveryData.getDeliveryStatus().equals("4")))) {
            bundle.putString("shipper_id", deliveryData.getDriver_id());
            bundle.putBoolean("isShipper", false);
        } else {
            bundle.putString("shipper_id", shipperID);
            bundle.putBoolean("isShipper", true);
        }

        fragment.setArguments(bundle);
        getActivity().getFragmentManager().beginTransaction().add(R.id.container, fragment, backStateName)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private void showBidderInfo(String bidderid) {
        String backStateName = this.getClass().getName();
        Bundle bundle = new Bundle();
        Fragment fragment = new BidderInfo();
        bundle.putString("bidderid", bidderid);
        fragment.setArguments(bundle);
        getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private void showSignature() {
        String backStateName = this.getClass().getName();
        Bundle bundle = new Bundle();
        Fragment fragment = new Signature();
        bundle.putString("delivery_id", deliveryID);
        if (deliveryData.getDeliveryStatus().equals("1"))
            bundle.putString("contact_person", deliveryData.getPuContactPerson());
        else
            bundle.putString("contact_person", deliveryData.getDoContactPerson());
        bundle.putString("delivery_status", deliveryData.getDeliveryStatus());
        fragment.setArguments(bundle);
        getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private void showImages() {
        String backStateName = this.getClass().getName();
        HashMap<String, Delivery> data = new HashMap<String, Delivery>();
        data.put("data", deliveryData);
        Bundle bundle = new Bundle();
        Fragment fragment = new Images();
        bundle.putString("delivery_id", deliveryID);
        bundle.putSerializable("data", data);
        if (deliveryData.getDeliveryStatus().equals("1"))
            bundle.putString("contact_person", deliveryData.getPuContactPerson());
        else
            bundle.putString("contact_person", deliveryData.getDoContactPerson());
        bundle.putString("delivery_status", deliveryData.getDeliveryStatus());
        fragment.setArguments(bundle);
        getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private void appendData() {
        deliveryID = deliveryData.getId();
        deliveryTypeID = deliveryData.getDeliveryTypeID();
        bookmarkID = deliveryData.getBookmarkID();
        if (deliveryData.isBookmarked()) {
            bookmark.setTag("1");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bookmark.setImageDrawable(getResources().getDrawable(R.drawable.banner_icon_active, getActivity().getTheme()));
            } else {
                bookmark.setImageDrawable(getResources().getDrawable(R.drawable.banner_icon_active));
            }
        } else {
            bookmark.setTag("0");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bookmark.setImageDrawable(getResources().getDrawable(R.drawable.banner_icon, getActivity().getTheme()));
            } else {
                bookmark.setImageDrawable(getResources().getDrawable(R.drawable.banner_icon));
            }
        }
        shipperID = deliveryData.getShipperID();


        if (deliveryData.getItemPhoto() != null && !deliveryData.getItemPhoto().equals("") && !deliveryData.getItemPhoto().contentEquals("null")) {
            try {
                String path = deliveryData.getItemPhoto();
                //  cropImageView.setImageUri(Uri.fromFile(new File(jsonArray.getJSONObject(currentPos).getString("item_photo"))));
                //final Bitmap croppedImage = cropImageView.getCroppedImage();

                if (path != null && path.contains("http"))
                    Picasso.with(getActivity()).load(path).into(itemImage);
                else {
                    final Bitmap croppedImage = BitmapFactory.decodeFile(path);
                    itemImage.setImageBitmap(croppedImage);
                }
            } catch (Exception e) {
                e.toString();
            }
//            Picasso.with(getActivity()).load(deliveryData.getItemPhoto()).into(itemImage);
        }


        deliveryTitle.setText(deliveryData.getTitle());
        puContactPerson.setText(deliveryData.getPuContactPerson());
        try {
            StringBuilder str = new StringBuilder(deliveryData.getPuMobile());
            str.insert(3, " ");
            if (str.toString().substring(4).startsWith("0"))
                str.deleteCharAt(4);
            pickUpMobileStr = str.toString();
            puMobile.setText(pickUpMobileStr);
        } catch (Exception e) {
            e.toString();
        }
        puAddress.setText(deliveryData.getPickUpAddress());
        if (deliveryData.getPickupDateType().equals("4"))
            puDateTime.setText(deliveryData.getPickUpDate() + "  To  " + deliveryData.getPickupEndDate());
        else
            puDateTime.setText(deliveryData.getPickUpDate());
        doContactPerson.setText(deliveryData.getDoContactPerson());
        doAddress.setText(deliveryData.getDropoffAdress());
        try {
            StringBuilder str = new StringBuilder(deliveryData.getDoMobile());
            str.insert(3, " ");
            if (str.toString().substring(4).startsWith("0"))
                str.deleteCharAt(4);
            dropoffMobileStr = str.toString();
            doMobile.setText(dropoffMobileStr);
        } catch (Exception e) {
            e.toString();
        }
        if (deliveryData.getDropoffDateType() != null && !deliveryData.getDropoffDateType().contentEquals("") && deliveryData.getDropoffDateType().equals("4"))
            doDateTime.setText(deliveryData.getDropOffDate() + "  To  " + deliveryData.getDropOffEndDate());
        else
            doDateTime.setText(deliveryData.getDropOffDate());
        totalQty.setText(deliveryData.getQty());
        try {
            String suitvehicles = deliveryData.getSuitableVehicles();
            if (deliveryData.getSuitableVehicles() != null && deliveryData.getSuitableVehicles().contains(","))
                suitableVehicle.setText(deliveryData.getSuitableVehicles().replace(',', '\n'));
            else
                suitableVehicle.setText(deliveryData.getSuitableVehicles());
        } catch (Exception e) {
            e.toString();
        }

        appendItemsData(deliveryData.getItemsData());
        shipperRating.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        if (deliveryData.getPickupDateType().equals("1"))
            pu_date_type.setText("On ");
        else if (deliveryData.getPickupDateType().equals("2"))
            pu_date_type.setText("After ");
        else if (deliveryData.getPickupDateType().equals("3"))
            pu_date_type.setText("Before ");
        else if (deliveryData.getPickupDateType().equals("4"))
            pu_date_type.setText("Between ");

        if (deliveryData.getDropoffDateType().equals("1"))
            do_date_type.setText("On ");
        else if (deliveryData.getDropoffDateType().equals("2"))
            do_date_type.setText("After ");
        else if (deliveryData.getDropoffDateType().equals("3"))
            do_date_type.setText("Before ");
        else if (deliveryData.getDropoffDateType().equals("4"))
            do_date_type.setText("Between ");


        if (deliveryData.getAuctionBid().equals("1")) {
            layAuction.setVisibility(View.VISIBLE);
            auctionStart.setText(deliveryData.getAuctionStart());
            auctionEnd.setText(deliveryData.getAuctionEnd());
            if (deliveryData.getDeliveryStatus().equals("0") ||
                    deliveryData.getDeliveryStatus().equals("4")) {
                if (!deliveryData.getBidStatus().equals("null")) {
                    try {
                        String delivery = deliveryData.getBidStatus();
                        JSONObject bidstatus = new JSONObject(deliveryData.getBidStatus());
                        maxOpeningBid.setText("Offered Bid - $" + bidstatus.optString("bid_price"));
                        sBidInfo.setVisibility(View.VISIBLE);
                        sBidderPrice.setText(" " + "$" + bidstatus.optString("bid_price"));
                        Log.v("", "");

                    } catch (Exception e) {
                        e.toString();
                    }
                } else
                    maxOpeningBid.setText("Maximum Opening Bid - $" + deliveryData.getMaxOpeningBid());
            } else
                maxOpeningBid.setText("Accepted Bid - $" + deliveryData.getPaymentAmount());
        } else {
            layAuction.setVisibility(View.GONE);
            maxOpeningBid.setText("Fixed Offer - $" + deliveryData.getFixedOffer());
        }

        try {
            if (deliveryData.getFromPickUpAt() != null && !deliveryData.getFromPickUpAt().contentEquals("") && !deliveryData.getFromPickUpAt().equals("null")) {
              /*  String inputText = deliveryData.getFromPickUpAt();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.ENGLISH);

                df.setTimeZone(TimeZone.getTimeZone("UTC"));

                SimpleDateFormat outputFormat =
                        new SimpleDateFormat("yyyy-MM-dd h:mm a");
                // Adjust locale and zone appropriately
                Date date = null;
                try {
                    date = df.parse(inputText);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String outputText = outputFormat.format(date);
                System.out.println(outputText);*/

                actual_date.setVisibility(View.VISIBLE);
                pu_actual_date.setText(deliveryData.getFromPickUpAt());
            } else {
                actual_date.setVisibility(View.GONE);
            }
            if (deliveryData.getCompleted_at() != null && !deliveryData.getCompleted_at().contentEquals("") && !deliveryData.getCompleted_at().equals("null")) {
               /* String inputText = deliveryData.getCompleted_at();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.ENGLISH);

                df.setTimeZone(TimeZone.getTimeZone("UTC"));

                SimpleDateFormat outputFormat =
                        new SimpleDateFormat("yyyy-MM-dd h:mm a");
                // Adjust locale and zone appropriately
                Date date = null;
                try {
                    date = df.parse(inputText);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String outputText = outputFormat.format(date);
                System.out.println(outputText);*/
                actual_do_date.setVisibility(View.VISIBLE);
                do_actual_date.setText(deliveryData.getCompleted_at());
            } else {
                actual_do_date.setVisibility(View.GONE);
            }
        } catch (Exception ex) {

        }

        puBuildType.setText(deliveryData.getPuBuildType());

        if (deliveryData.getPuLiftEquipment().equals("1"))
            puLiftEquipment.setText("Lifting Equipment (Available on site) - " + deliveryData.getPuLiftEquipmentText());
        else if (deliveryData.getDoLiftEquipment().equals("0"))
            puLiftEquipment.setText("Lifting Equipment (Not available on site) - " + deliveryData.getPuLiftEquipmentNeededText());
        else puLiftEquipment.setText("Lifting Equipment - Manual Handling");

        if (deliveryData.getPuBuildType().equals("1"))
            puBuildType.setText("Commercial");
        else puBuildType.setText("Residential");

        if (deliveryData.getDoBuildType().equals("1"))
            doBuildType.setText("Commercial");
        else doBuildType.setText("Residential");

        if (deliveryData.getDoCall().equals("1"))
            doCall.setText("Call Required - Yes");
        else doCall.setText("Call Required - No");

        if (deliveryData.getDoAppoint().equals("1"))
            doAppoint.setText("Appointment - Yes");
        else doAppoint.setText("Appointment - No");

        if (deliveryData.getDoLiftEquipment().equals("1"))
            doLiftEquipment.setText("Lifting Equipment (Available on site) - " + deliveryData.getDoLiftEquipmentText());
        else if (deliveryData.getDoLiftEquipment().equals("0"))
            doLiftEquipment.setText("Lifting Equipment (Not available on site) - " + deliveryData.getDoLiftEquipmentNeededText());
        else doLiftEquipment.setText("Lifting Equipment - Manual Handling");

        this.delivery.setText(deliveryData.getDeliveryName());
        deliveryType.setText(deliveryData.getDeliverySubName());

        /*if(deliveryData.getFixedOffer().equals("null"))
            layfixedPrice.setVisibility(View.GONE);
        else {
            fixedPrice.setText("$"+deliveryData.getFixedOffer());
            laymaxOpeningBid.setVisibility(View.GONE);
            layauctionStart.setVisibility(View.GONE);
            layauctionEnd.setVisibility(View.GONE);
        }*/

        if (!deliveryData.getPickUpDate().equals("0") && !deliveryData.getPickUpDate().equals("null")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
            Date strDate = null;
            try {
                strDate = sdf.parse(deliveryData.getPickUpDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //Log.d("now", ""+new Date());
            //Log.d("pickup", ""+strDate);
            /* if (new Date().after(strDate)) {
                HomeActivity.edit.setVisibility(View.GONE);
            } else {
                if(incomingType.equals("shipper"))
                    HomeActivity.edit.setVisibility(View.VISIBLE);
                else
                    HomeActivity.edit.setVisibility(View.GONE);
            }*/
        }

        //Sender/Receiver Name and Sign
        Log.v("getSign", "getSign:" + deliveryData.getReceiverSign());
        if (deliveryData.getReceiver().equals("null")) {
            layreceiver.setVisibility(View.GONE);
        } else {
            layreceiver.setVisibility(View.VISIBLE);
            receiverName.setText(deliveryData.getReceiver());
            //  imgLoader.DisplayImage(deliveryData.getReceiverSign(), receiverSign);
            if (deliveryData.getReceiverSign() != null && !deliveryData.getReceiverSign().equals("") && !deliveryData.getReceiverSign().contentEquals("null"))
                Picasso.with(getActivity()).load(deliveryData.getReceiverSign()).into(receiverSign);

        }
        Log.v("getSign", "getSign:" + deliveryData.getSenderSign());
        if (deliveryData.getSender().equals("null")) {
            laySender.setVisibility(View.GONE);
        } else {
            laySender.setVisibility(View.VISIBLE);
            senderName.setText(deliveryData.getSender());
            // imgLoader.DisplayImage(deliveryData.getSenderSign(), senderSign);
            if (deliveryData.getSenderSign() != null && !deliveryData.getSenderSign().equals("") && !deliveryData.getSenderSign().contentEquals("null"))
                Picasso.with(getActivity()).load(deliveryData.getSenderSign()).into(senderSign);
        }
        if (!deliveryData.getAuctionStart().contentEquals("") && !deliveryData.getAuctionStart().equals("0") && !deliveryData.getAuctionStart().equals("null")) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
                Date endDate = dateFormat.parse(deliveryData.getAuctionEnd());
                if (!(endDate.after(currentDatee))) {
                    mIsAuctionEnded = true;
                }

            } catch (Exception e) {
                e.toString();
            }
        }
        refreshAuction();
        //Bids and Choose Bids
        if (incomingType.equals("shipper") || deliveryData.getUserID().equals(userInfo.getId())) {
            if (deliveryData.getBidArray() != null && !deliveryData.getBidArray().equals("") && !deliveryData.getBidArray().equals("null")) {
                try {
                    if (!deliveryData.getDeliveryStatus().equals("1") && !deliveryData.getDeliveryStatus().equals("2") && !deliveryData.getDeliveryStatus().equals("3") && !deliveryData.getDeliveryStatus().equals("4")) {
                        final JSONArray bidArray;
                    /*if(deliveryData.getDeliveryStatus().equals("1"))
                        else if(deliveryData.getDeliveryStatus().equals("2"))
                    else if(deliveryData.getDeliveryStatus().equals("3"))*/
                        if (deliveryData.getDeliveryStatus().equals("1") ||
                                deliveryData.getDeliveryStatus().equals("2") ||
                                deliveryData.getDeliveryStatus().equals("3")) {
                            bidTitle.setText("Selected Carrier:");
                            bidArray = new JSONArray(deliveryData.getChoosedBidsArray());
                        } else bidArray = new JSONArray(deliveryData.getBidArray());
                        try {
                            if (layBid != null && layBid.getChildCount() >= 1)
                                layBid.removeAllViews();
                        } catch (Exception e) {
                            e.toString();
                        }

                        for (int i = 0; i < bidArray.length(); i++) {
                            final JSONObject bidObj = bidArray.getJSONObject(i);
                            final View view = getActivity().getLayoutInflater().inflate(R.layout.bid, null);
                            LinearLayout.LayoutParams layoutParams = new
                                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(0, 0, 0, 10);
                            view.setLayoutParams(layoutParams);

                            final TextView bidderID = (TextView) view.findViewById(R.id.bidder_id);
                            final TextView bidPrice = (TextView) view.findViewById(R.id.bid_price);
                            final RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.bidinfo);
                            bidderID.setText(" " + bidObj.getString("bidder_id"));
                            if (bidObj.getString("bid_price").equals("null"))
                                bidPrice.setText(" Fixed Price");
                            else
                                bidPrice.setText(" " + "$" + bidObj.getString("bid_price"));
                            final TextView accept = (TextView) view.findViewById(R.id.accept);
                        /* if(deliveryData.getDeliveryStatus().equals("1"))
                                accept.setVisibility(View.GONE);
                        else
                            accept.setVisibility(View.VISIBLE);

                        if(incomingType.equals("shipper")
                                && deliveryData.getDeliveryStatus().equals("3")
                                && !deliveryData.hasDriverFeedback())
                            accept.setText("Feedback");
                        else accept.setVisibility(View.GONE);*/

                            if (deliveryData.getDeliveryStatus().equals("0"))
                                accept.setVisibility(View.VISIBLE);
                            else if (deliveryData.getDeliveryStatus().equals("1"))
                                accept.setVisibility(View.GONE);
                            else if ((incomingType.equals("shipper") || deliveryData.getUserID().equals(userInfo.getId()))
                                    && deliveryData.getDeliveryStatus().equals("3")
                                    && !deliveryData.hasDriverFeedback()) {
                                accept.setText("Feedback");
                                accept.setVisibility(View.VISIBLE);
                            } else accept.setVisibility(View.GONE);

                            accept.setTag(i);
                            //  bidderID.setTag(i);
                            bidderID.setTag(i);
                            bidPrice.setTag(i);
                            relativeLayout.setTag(i);
                            relativeLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.v("", "");
                                    try {
                                        showBidderInfo(bidArray.getJSONObject((int) relativeLayout.getTag()).getString("driver_id"));
                                        JSONObject json = bidArray.getJSONObject((int) relativeLayout.getTag());
                                        Log.v("", json.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                      /*  view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.v("", "");
                                try {
                                    showBidderInfo(bidArray.getJSONObject((int) view.getTag()));
                                    JSONObject json = bidArray.getJSONObject((int) view.getTag());
                                    Log.v("", json.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //showShipperInfo();
                                *//*bidArray.getJSONObject(2)

                             JSONObject json =    bidArray.getJSONObject(8);
                                showBidderInfo(bidArray.getJSONObject((int) view.getTag());*//*
                                Log.v("", "");
                            }
                        });*/
                            if (checktimings(true))
                                accept.setVisibility(View.GONE);
                            else
                                accept.setVisibility(View.VISIBLE);
                            accept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (accept.getText().equals("Feedback")) {
                                        addFeedback();
                                    } else {
                                        try {
                                            showAcceptDialog(bidArray.getJSONObject((int) accept.getTag()).getString("id"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                            layBid.addView(view);
                        }
                        if (bidArray.length() > 0) {
                            layBids.setVisibility(View.VISIBLE);
                        } else layBids.setVisibility(View.GONE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (incomingType.equals("driver")) {

        }
        Log.e("LoggedUser", userInfo.getId());
        if (incomingType.equals("shipper") || deliveryData.getUserID().equals(userInfo.getId())) {
            updateShipper();
        } else if (incomingType.equals("driver")) {
            updateDriver();
        } else if (incomingType.equals("notification")
                || incomingType.equals("feedback")
                || incomingType.equals("home")
                || incomingType.equals("search")
                || incomingType.equals("bookmark") || incomingType.equals("bank") || incomingType.equals("card")) {
            if (deliveryData.getUserID().equals(userInfo.getId())) {
                updateShipper();

            } else {
                updateDriver();

            }
        } else if (incomingType.equals("map")) {
            if (deliveryData.getDeliveryStatus().equals("0")) {
                //if(isBidDateTimeValid(deliveryData.getAuctionStart(), deliveryData.getAuctionEnd())) {
                submit.setVisibility(View.VISIBLE);
                submit.setText("TAKE NOW");
                submit.setOnClickListener(this);
                //}
            }
        }

    }

    /*  private void appendItemsData(String itemData) {
          try {
              JSONArray jsonArray = new JSONArray(itemData);
              LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
              for (int i = 0; i < jsonArray.length(); i++) {
                  View view = inflater.inflate(R.layout.items, null);
                  TextView itemNo = (TextView) view.findViewById(R.id.item_no);
                  TextView dimensions = (TextView) view.findViewById(R.id.dimension);
                  TextView weight = (TextView) view.findViewById(R.id.weight);
                  ImageView image = (ImageView) view.findViewById(R.id.image);
                  JSONObject itemObj = jsonArray.getJSONObject(i);
                  itemNo.setText("Item " + (i + 1) + ":");
                  if (deliveryTypeID.equals("1")) {
                      dimensions.setText("Dimensions: " + itemObj.getString(Keys.ITEM_WIDTH) + "cm (w) x "
                              + itemObj.getString(Keys.ITEM_HEIGHT) + "cm (h) x "
                              + itemObj.getString(Keys.ITEM_LENGTH) + "cm (l)");
                      weight.setText("Weight: " + itemObj.getString(Keys.ITEM_WEIGHT) + "kg\n" +
                              "Stackable: " + (itemObj.getString(Keys.ITEM_STACKABLE).equals("1") ? "YES" : "NO") + "\n" +
                              "Hazardous: " + (itemObj.getString(Keys.ITEM_HAZARDOUS).equals("1") ? "YES" : "NO"));


                  } else if (deliveryTypeID.equals("2") || deliveryTypeID.equals("3")) {
                      dimensions.setText("Quantity: " + itemObj.getString(Keys.ITEM_QUANTITY));
                      weight.setText("More Details: " + itemObj.getString(Keys.ITEM_MORE));
                  } else if (deliveryTypeID.equals("4")) {
                      dimensions.setText("Quantity: " + itemObj.getString(Keys.ITEM_QUANTITY) + "\n"
                              + "Animal Name: " + itemObj.getString(Keys.ITEM_ANIMAL_NAME) + "\n"
                              + "Animal Breed: " + itemObj.getString(Keys.ITEM_ANIMAL_BREED) + "\n"
                              + "Current Vaccination: " + (itemObj.getString(Keys.ITEM_CURRENT_VACCINATIONS).equals("1") ? "YES" : "NO"));
                      weight.setText("More Details: " + itemObj.getString(Keys.ITEM_MORE));
                  }
                  layItems.addView(view);
              }
          } catch (JSONException e) {
              e.printStackTrace();
          }
      }*/
    private void appendItemsData(String itemData) {
        try {
            layItems.removeAllViews();
            JSONArray jsonArray = new JSONArray(itemData);
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < jsonArray.length(); i++) {
                View view = inflater.inflate(R.layout.items, null);
                TextView itemNo = (TextView) view.findViewById(R.id.item_no);
                TextView dimensions = (TextView) view.findViewById(R.id.dimension);
                TextView dimensionsh = (TextView) view.findViewById(R.id.dimensionh);
                TextView weight = (TextView) view.findViewById(R.id.weight);
                TextView weighth = (TextView) view.findViewById(R.id.weighth);
                TextView text2 = (TextView) view.findViewById(R.id.text2);
                TextView text3 = (TextView) view.findViewById(R.id.text3);
                TextView text4 = (TextView) view.findViewById(R.id.text4);
                TextView text2h = (TextView) view.findViewById(R.id.text2h);
                TextView text3h = (TextView) view.findViewById(R.id.text3h);
                TextView text4h = (TextView) view.findViewById(R.id.text4h);

                LinearLayout linear1 = (LinearLayout) view.findViewById(R.id.linear1);
                LinearLayout linear2 = (LinearLayout) view.findViewById(R.id.linear2);
                LinearLayout linear3 = (LinearLayout) view.findViewById(R.id.linear3);
                LinearLayout linearImage = (LinearLayout) view.findViewById(R.id.linearimage);

                JSONObject itemObj = jsonArray.getJSONObject(i);
                itemNo.setText("Item " + (i + 1) + ":");
                if (deliveryTypeID.equals("1")) {
                    dimensionsh.setText("Dimensions ");
                    dimensions.setText(itemObj.getString(Keys.ITEM_WIDTH) + "cm (w) x "
                            + itemObj.getString(Keys.ITEM_HEIGHT) + "cm (h) x "
                            + itemObj.getString(Keys.ITEM_LENGTH) + "cm (l)");
                    weight.setText(itemObj.getString(Keys.ITEM_WEIGHT) + "kg");
                    weighth.setText("Weight ");
                    linear1.setVisibility(View.VISIBLE);
                    linear2.setVisibility(View.VISIBLE);
                    text2h.setText("Stackable ");
                    text3h.setText("Hazardous ");
                    text2.setText((itemObj.getString(Keys.ITEM_STACKABLE).equals("1") ? "YES" : "NO"));
                    text3.setText((itemObj.getString(Keys.ITEM_HAZARDOUS).equals("1") ? "YES" : "NO"));
                    linear3.setVisibility(View.GONE);


                   /* weight.setText("Weight: " + itemObj.getString(Keys.ITEM_WEIGHT) + "kg\n" +
                            "Stackable: " + (itemObj.getString(Keys.ITEM_STACKABLE).equals("1") ? "YES" : "NO") + "\n" +
                            "Hazardous: " + (itemObj.getString(Keys.ITEM_HAZARDOUS).equals("1") ? "YES" : "NO"));*/
                    if (itemObj.getString(Keys.ITEM_PHOTO) != null && !itemObj.getString(Keys.ITEM_PHOTO).contentEquals("") && !itemObj.getString(Keys.ITEM_PHOTO).contentEquals("null")) {
                        linearImage.setVisibility(View.VISIBLE);
                        ImageView image = (ImageView) view.findViewById(R.id.image);
                        //  if (itemObj.getString(Keys.ITEM_PHOTO) != null && !itemObj.getString(Keys.ITEM_PHOTO).equals("") && !itemObj.getString(Keys.ITEM_PHOTO).contentEquals("null"))
//                        Picasso.with(getActivity()).load(Config.DeliveryImgURL + itemObj.getString(Keys.ITEM_PHOTO)).into(image);
                        try {
                            String path = itemObj.getString(Keys.ITEM_PHOTO);
                            //  cropImageView.setImageUri(Uri.fromFile(new File(jsonArray.getJSONObject(currentPos).getString("item_photo"))));
                            //final Bitmap croppedImage = cropImageView.getCroppedImage();

                            if (path != null && path.contains("http"))
                                Picasso.with(getActivity()).load(path).into(image);
                            else {
                                final Bitmap croppedImage = BitmapFactory.decodeFile(path);
                                image.setImageBitmap(croppedImage);
                            }
                        } catch (Exception e) {
                            e.toString();
                        }

                        // Picasso.with(getActivity()).load(itemObj.getString(Keys.ITEM_PHOTO)).into(image);
                    } else {
                        linearImage.setVisibility(View.GONE);
                    }

                } else if (deliveryTypeID.equals("2") || deliveryTypeID.equals("3")) {
                    dimensionsh.setText("Quantity ");
                    dimensions.setText(itemObj.getString(Keys.ITEM_QUANTITY));
                    weighth.setText("More Details ");
                    weight.setText(itemObj.getString(Keys.ITEM_MORE));
                    linear1.setVisibility(View.GONE);
                    linear2.setVisibility(View.GONE);
                    linear3.setVisibility(View.GONE);
                    String deliverySubID = deliveryData.getDeliveryTypeSubID();
                    if (deliveryTypeID.equals("3") && deliverySubID != null) {
                        try {
                            if (deliverySubID.equals("25") || deliverySubID.equals("26")
                                    || deliverySubID.equals("28") || deliverySubID.equals("29")
                                    || deliverySubID.equals("30")) {
                                dimensionsh.setText("Dimensions ");
                                dimensions.setText(itemObj.getString(Keys.ITEM_WIDTH) + "cm (w) x "
                                        + itemObj.getString(Keys.ITEM_HEIGHT) + "cm (h) x "
                                        + itemObj.getString(Keys.ITEM_LENGTH) + "cm (l)");
                                weight.setText(itemObj.getString(Keys.ITEM_WEIGHT) + "kg");
                                weighth.setText("Weight ");
                                linear1.setVisibility(View.VISIBLE);
                                text2h.setText("More Details ");
                                text2.setText(itemObj.getString(Keys.ITEM_MORE));
                            }
                        } catch (Exception e) {
                            e.toString();
                        }
                    }
                    if (itemObj.getString(Keys.ITEM_PHOTO) != null && !itemObj.getString(Keys.ITEM_PHOTO).contentEquals("") && !itemObj.getString(Keys.ITEM_PHOTO).contentEquals("null")) {
                        linearImage.setVisibility(View.VISIBLE);
                        ImageView image = (ImageView) view.findViewById(R.id.image);
                        // String photo_url = itemObj.getString(Keys.ITEM_PHOTO);
                        //  if (itemObj.getString(Keys.ITEM_PHOTO) != null && !itemObj.getString(Keys.ITEM_PHOTO).equals("") && !itemObj.getString(Keys.ITEM_PHOTO).contentEquals("null"))
                        try {
                            String path = itemObj.getString(Keys.ITEM_PHOTO);
                            //  cropImageView.setImageUri(Uri.fromFile(new File(jsonArray.getJSONObject(currentPos).getString("item_photo"))));
                            //final Bitmap croppedImage = cropImageView.getCroppedImage();

                            if (path != null && path.contains("http"))
                                Picasso.with(getActivity()).load(path).into(image);
                            else {
                                final Bitmap croppedImage = BitmapFactory.decodeFile(path);
                                image.setImageBitmap(croppedImage);
                            }
                        } catch (Exception e) {
                            e.toString();
                        }

                        // Picasso.with(getActivity()).load(itemObj.getString(Keys.ITEM_PHOTO)).into(image);
                    } else {
                        linearImage.setVisibility(View.GONE);
                    }
                } else if (deliveryTypeID.equals("4")) {
                    linear1.setVisibility(View.VISIBLE);
                    linear2.setVisibility(View.VISIBLE);
                    linear3.setVisibility(View.GONE);

                    dimensionsh.setText("Quantity ");
                    dimensions.setText(itemObj.getString(Keys.ITEM_QUANTITY));
                    weighth.setText("More Details");
                    weight.setText(itemObj.getString(Keys.ITEM_MORE));
                    //  text2h.setText("Animal Name");
                    //  text2.setText(itemObj.getString(Keys.ITEM_ANIMAL_NAME));
                    // text2h.setVisibility(View.GONE);
                    //  text2.setVisibility(View.GONE);
                    if (itemObj.getString(Keys.ITEM_ANIMAL_BREED) != null && !itemObj.getString(Keys.ITEM_ANIMAL_BREED).contentEquals("") && !itemObj.getString(Keys.ITEM_ANIMAL_BREED).contentEquals("null")) {
                        text2h.setText("Animal Breed ");
                        text2.setText(itemObj.getString(Keys.ITEM_ANIMAL_BREED));
                        linear1.setVisibility(View.VISIBLE);
                    } else
                        linear1.setVisibility(View.GONE);


                    text3h.setText("Current Vaccination ");
                    text3.setText((itemObj.getString(Keys.ITEM_CURRENT_VACCINATIONS).equals("1") ? "YES" : "NO"));
                   /* dimensions.setText("Quantity: " + itemObj.getString(Keys.ITEM_QUANTITY) + "\n"
                            + "Animal Name: " + itemObj.getString(Keys.ITEM_ANIMAL_NAME) + "\n"
                            + "Animal Breed: " + itemObj.getString(Keys.ITEM_ANIMAL_BREED) + "\n"
                            + "Current Vaccination: " + (itemObj.getString(Keys.ITEM_CURRENT_VACCINATIONS).equals("1") ? "YES" : "NO"));*/
                    weight.setText(itemObj.getString(Keys.ITEM_MORE));
                    if (itemObj.getString(Keys.ITEM_PHOTO) != null && !itemObj.getString(Keys.ITEM_PHOTO).contentEquals("") && !itemObj.getString(Keys.ITEM_PHOTO).contentEquals("null")) {
                        linearImage.setVisibility(View.VISIBLE);
                        ImageView image = (ImageView) view.findViewById(R.id.image);
                        //  if (itemObj.getString(Keys.ITEM_PHOTO) != null && !itemObj.getString(Keys.ITEM_PHOTO).equals("") && !itemObj.getString(Keys.ITEM_PHOTO).contentEquals("null"))
                        try {
                            String path = itemObj.getString(Keys.ITEM_PHOTO);
                            //  cropImageView.setImageUri(Uri.fromFile(new File(jsonArray.getJSONObject(currentPos).getString("item_photo"))));
                            //final Bitmap croppedImage = cropImageView.getCroppedImage();

                            if (path != null && path.contains("http"))
                                Picasso.with(getActivity()).load(path).into(image);
                            else {
                                final Bitmap croppedImage = BitmapFactory.decodeFile(path);
                                image.setImageBitmap(croppedImage);
                            }
                        } catch (Exception e) {
                            e.toString();
                        }

                        // Picasso.with(getActivity()).load(itemObj.getString(Keys.ITEM_PHOTO)).into(image);
                    } else {
                        linearImage.setVisibility(View.GONE);
                    }
                }
                layItems.addView(view);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void refreshAuction() {
        if (deliveryData.getDeliveryStatus().equals("0")) {
            if (!deliveryData.getAuctionStart().contentEquals("") && !deliveryData.getAuctionStart().equals("0") && !deliveryData.getAuctionStart().equals("null")) {
                Date strDate = null;
                Date endDate = null;
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
                    strDate = dateFormat.parse(deliveryData.getAuctionStart());
                    endDate = dateFormat.parse(deliveryData.getAuctionEnd());
                    Date systemDate = Calendar.getInstance().getTime();
                    String myDate = dateFormat.format(systemDate);
                    Log.d("now", "Date" + myDate);
                    Log.d("start", "Date" + strDate);
                    Log.d("end", "Date" + endDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (strDate.after(currentDatee)) {
                    Log.v("date", "Date1");
                    auctionStatus.setVisibility(View.VISIBLE);
                    auctionDateTime = deliveryData.getAuctionStart();
                    auctionType = 1;
                    countDownStart(deliveryData.getAuctionStart(), 1);
                } else if (!(strDate.after(currentDatee))) {
                    if (!deliveryData.getAuctionEnd().equals("0") && !deliveryData.getAuctionEnd().equals("null")) {
                        if ((endDate.after(currentDatee))) {
                            Log.v("date", "Date2");
                            auctionStatus.setVisibility(View.VISIBLE);
                            auctionDateTime = deliveryData.getAuctionEnd();
                            auctionType = 0;
                            countDownStart(deliveryData.getAuctionEnd(), 0);
                        } else if (!(endDate.after(currentDatee))) {
                            Log.v("date", "Date3");
                            if (deliveryData.getDeliveryStatus().equals("0")) {
                                auctionStatus.setVisibility(View.VISIBLE);
                                auctionStatus.setText("AUCTION HAS ENDED");
                                // HomeActivity.edit.setVisibility(View.GONE);
                                if (mAuction) {
                                    mAuction = false;
                                    /*try {
                                        if (deliveryData != null && userInfo != null) {
                                            if (deliveryData.getUserID().equals(userInfo.getId()))
                                                HomeActivity.edit.setVisibility(View.GONE);
                                        }
                                    } catch (Exception e) {
                                        e.toString();
                                    }*/
                                }

                                if (!mIsAuctionEnded && mFirstUpdate && deliveryData.getUserID().equals(userInfo.getId())) {
                                    //    UpdateFragment();
                                    if (deliveryID != null && !deliveryID.contentEquals("")) {
                                        //  getDelivery();
                                        mFirstUpdate = false;
                                        mIsAuctionEnded = true;

                                    }

                                }
                            } else {
                                // auctionStatus.setVisibility(View.GONE);
                            }
                            submit.setVisibility(View.VISIBLE);
                           /* if (submit.getText().toString().equalsIgnoreCase("BID SUBMITTED")) {
                                submit.setVisibility(View.VISIBLE);
                            } else
                                submit.setVisibility(View.GONE);
//                        HomeActivity.edit.setVisibility(View.GONE);
                            if (incomingType.equals("driver") || incomingType.equals("search") || !deliveryData.getUserID().equals(userInfo.getId())) {
                                if (submit.getText().toString().equalsIgnoreCase("BID SUBMITTED")) {
                                    submit.setVisibility(View.VISIBLE);

                                }
                                //Need To Test
                                *//*else
                                    submit.setVisibility(View.GONE);*//*
                                //HomeActivity.edit.setVisibility(View.GONE);
                            } else if (incomingType.equals("search") &&
                                    !deliveryData.getUserID().equals(userInfo.getId())) {
                                if (submit.getText().toString().equalsIgnoreCase("BID SUBMITTED")) {
                                    submit.setVisibility(View.VISIBLE);
                                } else
                                    submit.setVisibility(View.GONE);
                            }
                            if (!deliveryData.getUserID().equals(userInfo.getId())) {
                                if (deliveryData.getDeliveryStatus().equals("0") ||
                                        deliveryData.getDeliveryStatus().equals("4")) {
                                    if (!deliveryData.getBidStatus().equals("null")) {
                                        submit.setText("BID SUBMITTED");
                                        submit.setVisibility(View.VISIBLE);
                                    }
                                }
                            }*/
                        }
                    }

                }
            }
        }
        /*if (mFirstUpdate) {
            mFirstUpdate = false;
        }*/
    }


    private void updateShipper() {
        mWithDraw.setVisibility(View.GONE);
        mRelist.setVisibility(View.GONE);
        String auctionstatusstr = auctionStatus.getText().toString();
        bookmark.setVisibility(View.INVISIBLE);
        call.setVisibility(View.GONE);
        msg.setVisibility(View.GONE);
        if (deliveryData.getDeliveryStatus().equals("0") || deliveryData.getDeliveryStatus().equals("4")) {
            shipperRating.setRating(Float.valueOf(deliveryData.getShipperRating()));
            shipperName.setText(deliveryData.getShipperName());
            //imgLoaderr.DisplayImage(deliveryData.getShipperImage(), shipperPic);
            if (deliveryData.getShipperImage() != null && !deliveryData.getShipperImage().equals("") && !deliveryData.getShipperImage().contentEquals("null"))
                Picasso.with(getActivity()).load(deliveryData.getShipperImage()).into(target);
          /*  Picasso.with(getActivity()).load(deliveryData.getShipperImage()).into(new Target() {

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    shipperPic.setBackground(new BitmapDrawable(bitmap));
                    // mainLayout.setBackground(new BitmapDrawable(context.getResources(), bitmap));
                }

                @Override
                public void onBitmapFailed(final Drawable errorDrawable) {
                    Log.d("TAG", "FAILED");
                }

                @Override
                public void onPrepareLoad(final Drawable placeHolderDrawable) {
                    Log.d("TAG", "Prepare Load");
                }
            });*/
        } else {
            shipperRating.setRating(Float.valueOf(deliveryData.getDriver_rating()));
            String name = deliveryData.getDriver_name();
            shipperName.setText(deliveryData.getDriver_name());
            //imgLoaderr.DisplayImage(deliveryData.getShipperImage(), shipperPic);
            if (deliveryData.getDriver_image() != null && !deliveryData.getDriver_image().equals("") && !deliveryData.getDriver_image().contentEquals("null"))
                Picasso.with(getActivity()).load(deliveryData.getDriver_image()).into(target);
           /* Picasso.with(getActivity()).load(deliveryData.getDriver_image()).into(new Target() {

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    shipperPic.setBackground(new BitmapDrawable(bitmap));
                    // mainLayout.setBackground(new BitmapDrawable(context.getResources(), bitmap));
                }

                @Override
                public void onBitmapFailed(final Drawable errorDrawable) {
                    Log.d("TAG", "FAILED");
                }

                @Override
                public void onPrepareLoad(final Drawable placeHolderDrawable) {
                    Log.d("TAG", "Prepare Load");
                }
            });*/
        }


        HomeActivity.edit.setBackgroundResource(R.drawable.edit_white);
        if (deliveryData.getDeliveryStatus().equals("0"))
            auctionStatus.setVisibility(View.VISIBLE);
        if (deliveryData.getDeliveryStatus().equals("1")) {
            auctionStatus.setVisibility(View.VISIBLE);
            auctionStatus.setText("DELIVERY ASSIGNED");
            layAuction.setVisibility(View.GONE);
        } else if (deliveryData.getDeliveryStatus().equals("2")) {
            auctionStatus.setVisibility(View.VISIBLE);
            auctionStatus.setText("DELIVERY IN-TRANSIT");
            layAuction.setVisibility(View.GONE);

        } else if (deliveryData.getDeliveryStatus().equals("3")) {
            auctionStatus.setVisibility(View.VISIBLE);
            auctionStatus.setText("DELIVERY COMPLETED");
            layAuction.setVisibility(View.GONE);
            mExport.setVisibility(View.VISIBLE);
            bookmark.setVisibility(View.INVISIBLE);
            if (deliveryData.getIsFavouriteUser() != null && deliveryData.getIsFavouriteUser().equals("0")) {
                if (deliveryData.hasDriverFeedback()) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 0, 0, 0);
                    mAddFavourite.setLayoutParams(params);
                }
                mAddFavourite.setVisibility(View.VISIBLE);

            } else
                mAddFavourite.setVisibility(View.GONE);


        } else if (deliveryData.getDeliveryStatus().equals("4")) {
            auctionStatus.setVisibility(View.VISIBLE);
            auctionStatus.setText("DELIVERY CANCELLED");
            HomeActivity.edit.setVisibility(View.GONE);
            // layAuction.setVisibility(View.GONE);

        }
        if (deliveryData.getDeliveryStatus().equals("0") ||
                deliveryData.getDeliveryStatus().equals("1")) {
            submit.setVisibility(View.VISIBLE);
            submit.setText("CANCEL DELIVERY");
            submit.setOnClickListener(this);
        } else if (deliveryData.getDeliveryStatus().equals("3") && !deliveryData.hasDriverFeedback()) {
            submit.setVisibility(View.VISIBLE);
            submit.setText("RATE CARRIER");
            submit.setOnClickListener(this);
            // auctionStatus.setVisibility(View.VISIBLE);
            //  auctionStatus.setText("Delivery completed");
        }
        try {
            Date endDate = null;
            String dropoffdate = deliveryData.getDropOffDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
            endDate = dateFormat.parse(deliveryData.getDropOffDate());
            if (!(endDate.after(currentDatee))) {
                String submittext = submit.getText().toString();
                if (submittext != null && !submittext.isEmpty()) {
                    if (submittext.contentEquals("CANCEL DELIVERY")) {
                        //    submit.setVisibility(View.GONE);
                        //    HomeActivity.edit.setVisibility(View.GONE);
                    }
                }
            }
            if (auctionstatusstr != null && !auctionstatusstr.isEmpty()) {
                if (auctionstatusstr.contains("AUCTION ENDS IN")) {
                    HomeActivity.edit.setVisibility(View.GONE);
                }
                if (auctionstatusstr.contentEquals("AUCTION HAS ENDED")) {
                    //  submit.setVisibility(View.GONE);

                    HomeActivity.edit.setVisibility(View.GONE);
                }
            }

            if (deliveryData.getDeliveryStatus().equals("0")) {
                if (deliveryData.getRelistNotification() != null && deliveryData.getRelistNotification().equals("1")) {
                    mRelist.setVisibility(View.VISIBLE);
                   /* LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 0, px, 0);
                    submit.setLayoutParams(params);
                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params1.setMargins(px, 0, 0, 0);
                    mRelist.setLayoutParams(params1);*/

                }

            }
            if (deliveryData.getDeliveryStatus().equals("4")) {
                submit.setVisibility(View.GONE);
                mRelist.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 0);
                mRelist.setLayoutParams(params);
            }

//            mRelist.setLa

        } catch (Exception e) {
            e.toString();
        }
    }

    private void updateDriver() {
        mRelist.setVisibility(View.GONE);
        if (deliveryData.getDeliveryStatus().equals("1")) {
            mWithDraw.setVisibility(View.VISIBLE);
        } else
            mWithDraw.setVisibility(View.GONE);
        //   mWithDraw.setVisibility(View.VISIBLE);
        shipperRating.setRating(Float.valueOf(deliveryData.getShipperRating()));
        shipperName.setText(deliveryData.getShipperName());
        //imgLoaderr.DisplayImage(deliveryData.getShipperImage(), shipperPic);
        if (deliveryData.getShipperImage() != null && !deliveryData.getShipperImage().equals("") && !deliveryData.getShipperImage().contentEquals("null"))
            Picasso.with(getActivity()).load(deliveryData.getShipperImage()).into(target);
       /* Picasso.with(getActivity()).load(deliveryData.getShipperImage()).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                shipperPic.setBackground(new BitmapDrawable(bitmap));
                // mainLayout.setBackground(new BitmapDrawable(context.getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(final Drawable errorDrawable) {
                Log.d("TAG", "FAILED");
            }

            @Override
            public void onPrepareLoad(final Drawable placeHolderDrawable) {
                Log.d("TAG", "Prepare Load");
            }
        });*/
        String userId = userInfo.getId();
        String driverid = deliveryData.getDriverID();
        String auctionstatusstr = auctionStatus.getText().toString();
        HomeActivity.edit.setVisibility(View.GONE);
        copy_delivery.setVisibility(View.GONE);
        if (deliveryData.getDeliveryStatus().equals("1") || deliveryData.getDeliveryStatus().equals("2") || deliveryData.getDeliveryStatus().equals("3")) {
            call.setVisibility(View.VISIBLE);
            msg.setVisibility(View.VISIBLE);
        }
        if (deliveryData.getDeliveryStatus().equals("1")) {
            auctionStatus.setVisibility(View.VISIBLE);
            auctionStatus.setText("DELIVERY ASSIGNED");
            layAuction.setVisibility(View.GONE);

        } else if (deliveryData.getDeliveryStatus().equals("2")) {
            auctionStatus.setVisibility(View.VISIBLE);
            auctionStatus.setText("DELIVERY IN-TRANSIT");
            layAuction.setVisibility(View.GONE);

        } else if (deliveryData.getDeliveryStatus().equals("3")) {
            auctionStatus.setVisibility(View.VISIBLE);
            auctionStatus.setText("DELIVERY COMPLETED");
            layAuction.setVisibility(View.GONE);
            mExport.setVisibility(View.VISIBLE);
            bookmark.setVisibility(View.INVISIBLE);

        } else if (deliveryData.getDeliveryStatus().equals("4")) {
            auctionStatus.setVisibility(View.VISIBLE);
            auctionStatus.setText("DELIVERY CANCELLED");

        }

        Log.v("deliveryData", "deliveryData::" + deliveryData.getDeliveryStatus());
        if (deliveryData.getDeliveryStatus().equals("0") &&
                deliveryData.getBidStatus().equals("null")) {
            //if(isBidDateTimeValid(deliveryData.getAuctionStart(), deliveryData.getAuctionEnd())) {
            if (deliveryData.getAuctionBid().equals("1")) {
                submit.setText("BID NOW");
            } else if (deliveryData.getAuctionBid().equals("0")) {
                submit.setText("TAKE NOW");
            }
            submit.setVisibility(View.VISIBLE);
            submit.setOnClickListener(this);
            //}
        } else if (deliveryData.getDeliveryStatus().equals("1") &&
                deliveryData.getDriverID().equals(userInfo.getId())) {
            submit.setVisibility(View.VISIBLE);
            submit.setText("CONFIRM DELIVERY PICK UP");
            submit.setOnClickListener(this);
        } else if (deliveryData.getDeliveryStatus().equals("2") &&
                deliveryData.getDriverID().equals(userInfo.getId())) {
            submit.setVisibility(View.VISIBLE);
            submit.setText("CONFIRM DELIVERY DROP OFF");
            submit.setOnClickListener(this);
        } else if (deliveryData.getDeliveryStatus().equals("3")
                && !deliveryData.hasShipperFeedback() &&
                deliveryData.getDriverID().equals(userInfo.getId())) {
            submit.setVisibility(View.VISIBLE);
            submit.setText("RATE SENDER");
            submit.setOnClickListener(this);
            // auctionStatus.setVisibility(View.VISIBLE);
            // auctionStatus.setText("Delivery has been completed");
        } else if (deliveryData.getDeliveryStatus().equals("4")) {
            //submit.setVisibility(View.VISIBLE);
            //submit.setText("BID SUBMITTED");
            //submit.setOnClickListener(null);
        }

        if (deliveryData.getDeliveryStatus().equals("0")) {
            //  puAddress.setVisibility(View.GONE);
            puMobile.setVisibility(View.GONE);
            //doAddress.setVisibility(View.GONE);
            doMobile.setVisibility(View.GONE);
            // if( deliveryData.getPickupCity())
            String PickUpAddress = "";
            String dropOffAddress = "";

            if (deliveryData.getPickupCity() != null && !deliveryData.getPickupCity().contentEquals("null") && !deliveryData.getPickupCity().contentEquals("")) {
                PickUpAddress = deliveryData.getPickupCity();
            }
            if (deliveryData.getPickupState() != null && !deliveryData.getPickupState().contentEquals("null") && !deliveryData.getPickupState().contentEquals("")) {
                PickUpAddress = PickUpAddress + " ," + deliveryData.getPickupState();
            }
            if (deliveryData.getPickupCountry() != null && !deliveryData.getPickupCountry().contentEquals("null") && !deliveryData.getPickupCountry().contentEquals("")) {
                PickUpAddress = PickUpAddress + " , " + deliveryData.getPickupCountry();
            }
            if (deliveryData.getDropoffCity() != null && !deliveryData.getDropoffCity().contentEquals("null") && !deliveryData.getDropoffCity().contentEquals("")) {
                dropOffAddress = deliveryData.getDropoffCity();
            }
            if (deliveryData.getDropoffState() != null && !deliveryData.getDropoffState().contentEquals("null") && !deliveryData.getDropoffState().contentEquals("")) {
                dropOffAddress = dropOffAddress + " ," + deliveryData.getDropoffState();
            }
            if (deliveryData.getDropoffCountry() != null && !deliveryData.getDropoffCountry().contentEquals("null") && !deliveryData.getDropoffCountry().contentEquals("")) {
                dropOffAddress = dropOffAddress + " , " + deliveryData.getDropoffCountry();
            }
            puAddress.setText(PickUpAddress);
            doAddress.setText(dropOffAddress);
            Log.v("", "");
            //  puAddress.setText();
        } else if (deliveryData.getDriverID() != null && !deliveryData.getDriverID().equals(userInfo.getId())) {
            String PickUpAddress = "";
            String dropOffAddress = "";

            if (deliveryData.getPickupCity() != null && !deliveryData.getPickupCity().contentEquals("null") && !deliveryData.getPickupCity().contentEquals("")) {
                PickUpAddress = deliveryData.getPickupCity();
            }
            if (deliveryData.getPickupState() != null && !deliveryData.getPickupState().contentEquals("null") && !deliveryData.getPickupState().contentEquals("")) {
                PickUpAddress = PickUpAddress + "," + deliveryData.getPickupState();
            }
            if (deliveryData.getPickupCountry() != null && !deliveryData.getPickupCountry().contentEquals("null") && !deliveryData.getPickupCountry().contentEquals("")) {
                PickUpAddress = PickUpAddress + "," + deliveryData.getPickupCountry();
            }
            if (deliveryData.getDropoffCity() != null && !deliveryData.getDropoffCity().contentEquals("null") && !deliveryData.getDropoffCity().contentEquals("")) {
                dropOffAddress = deliveryData.getDropoffCity();
            }
            if (deliveryData.getDropoffState() != null && !deliveryData.getDropoffState().contentEquals("null") && !deliveryData.getDropoffState().contentEquals("")) {
                dropOffAddress = dropOffAddress + "," + deliveryData.getDropoffState();
            }
            if (deliveryData.getDropoffCountry() != null && !deliveryData.getDropoffCountry().contentEquals("null") && !deliveryData.getDropoffCountry().contentEquals("")) {
                dropOffAddress = dropOffAddress + "," + deliveryData.getDropoffCountry();
            }
            puAddress.setText(PickUpAddress);
            doAddress.setText(dropOffAddress);
            Log.v("", "");
            //  puAddress.setVisibility(View.GONE);
            puMobile.setVisibility(View.GONE);
            //  doAddress.setVisibility(View.GONE);
            doMobile.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
            call.setVisibility(View.GONE);
            msg.setVisibility(View.GONE);
            HomeActivity.track_delivery.setVisibility(View.GONE);
            mWithDraw.setVisibility(View.GONE);

        } else {
            puMobile.setVisibility(View.VISIBLE);
            doMobile.setVisibility(View.VISIBLE);
        }
        if (auctionstatusstr != null && !auctionstatusstr.isEmpty()) {
            if (auctionstatusstr.contentEquals("AUCTION HAS ENDED")) {
                if (submit.getText().toString().equalsIgnoreCase("BID SUBMITTED")) {
                    submit.setVisibility(View.VISIBLE);
                } else
                    submit.setVisibility(View.GONE);
                //  HomeActivity.edit.setVisibility(View.GONE);
            }
        }
    }

    public void showMessageDialog(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Success!");
        builder.setMessage(message);
        builder.setPositiveButton("Ignore", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("signaturetype", "1");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
        builder.setNegativeButton("Show", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getDelivery();
            }
        });
        builder.show();
    }

    public void showCallDialog(final String mobile) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("CALL");
        builder.setMessage(mobile);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                doCallMessage(1, mobile);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.show();
    }

    private void showSuccessMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Success!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (type.equals("addfav")) {
                    getDelivery();
                } else if (type.equals("bid")) {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra("signaturetype", "1");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else if (type.equals("choose")) {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra("signaturetype", "2");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //getActivity().getFragmentManager().popBackStack();
                } else {
                    Fragment fragment = new HomeMap();
                    String backStateName = "com.grabid.activities.HomeActivity";
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                }
            }
        });
        builder.show();
    }

    private void showMessage(String title, String message) {
        messageDialog(getActivity(), title, message);
    }

    public void ShipmentAddressDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                //  Bundle bundle = new Bundle();
                HashMap<String, Delivery> data = new HashMap<String, Delivery>();
                data.put("data", deliveryData);
                // bundle.putSerializable("data", data);
                intent.putExtra("SubmitStepTwo", data);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });
        Dialog d = builder.create();
        d.show();

    }

    String TITLE = "Alert!";

    private boolean isValidate() {
        if (holderName.getText().toString().trim().isEmpty()
                || cardNumber.getText().toString().trim().isEmpty()
                ) {
            showMessage(TITLE, "Please complete all fields.");
            return false;
        }
        /*else if(isCardExpiryDateValid(
                Integer.parseInt(v3),
				Integer.parseInt(expiry.getText().toString())))
		{
			alertManager.showAlert("Alert!","Please select valid card expiry date.");
			return false;
		}*/
        return true;
    }

    private void getSavedCreditCard() {
        String url = Config.SERVER_URL + Config.CREDIT_CARD + "/0";
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage(TITLE, getResources().getString(R.string.no_internet));
    }

    ArrayList<HashMap<String, String>> cardData = new ArrayList<HashMap<String, String>>();

    public String getCardTypeID(String name) {
        String id = null;

        for (int i = 0; i < cardData.size(); i++) {
            if (name.equalsIgnoreCase(cardData.get(i).get(Keys.KEY_NAME)))
                id = cardData.get(i).get(Keys.KEY_ID);
        }
        return id;
    }

    public void updateUI(String cardType) {
        if (cardType.equalsIgnoreCase("American Express")) {
            cardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
            cvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            //cardNumber.setText("");
            //cvv.setText("");
        } else {
            cardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
            cvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
            //cardNumber.setText("");
            //cvv.setText("");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (wakeLock != null)
                wakeLock.release();
            wakeLock = null;
        } catch (Exception e) {
            e.toString();
        }
        //  HomeActivity.edit.setVisibility(View.GONE);
      /*  HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.title.setTextColor(getResources().getColor(R.color.black));
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.white));*/
    }

    private void addToBookmark(String deliveryID) {
        HashMap<String, String> params = new HashMap<>();
        String url = Config.SERVER_URL + Config.BOOKMARKS;
        params.put("delivery_id", deliveryID);
        Log.d("end", params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage(TITLE, getResources().getString(R.string.no_internet));
    }

    private void removeBookmark(String bookmarkID) {
        String url = Config.SERVER_URL + Config.BOOKMARKS + "/" + bookmarkID;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.DELETE, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage(TITLE, getResources().getString(R.string.no_internet));
    }

    private void getDelivery() {
        type = "get";
        String url = Config.SERVER_URL + Config.DELIVERIES + "/" + deliveryID;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void chooseBid(String ID) {
        type = "choose";
        String url = Config.SERVER_URL + Config.CHOOSE_BID + ID;
        Log.d("URL", url);
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void cancelDelivery() {
        type = "cancel";
        String url = Config.SERVER_URL + Config.DELIVERY + Config.CANCEL_DELIVERY + deliveryID;
        Log.d("url", url);
        HashMap<String, String> params = new HashMap<>();
        params.put("delivery_id", deliveryID);
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void cancelDeliveryUpcoming(String message) {
        type = "cancelupcoming";
        String url = Config.SERVER_URL + Config.DELIVERY + Config.CANCEL_DELIVERYUPCOMING;
        // String url = "http://192.168.100.47/grabit/wwwroot" + Config.SERVER_URL + Config.DELIVERY + Config.CANCEL_DELIVERY + deliveryID;
        Log.d("url", url);
        HashMap<String, String> params = new HashMap<>();
        params.put("reason", message);
        params.put("delivery_id", deliveryID);
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void bidNow(String bidPrice) {

        String url = "";
        Log.v("bidPrice", "bidPrice" + bidPrice);
        if (bidPrice.equals("")) {
            type = "take_now";
            url = Config.SERVER_URL + Config.DELIVERY + "/" + Config.TAKE_NOW + "/?id=" + deliveryID;
        } else {
            type = "bid";
            url = Config.SERVER_URL + Config.BIDS + "/" + deliveryID;
        }
        Log.d("url", url);
        HashMap<String, String> params = new HashMap<>();
        params.put("bid_price", bidPrice);
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI;
            if (bidPrice.equals(""))
                mobileAPI = new RestAPICall(getActivity(), HTTPMethods.PUT, this, null);
            else
                mobileAPI = new RestAPICall(getActivity(), HTTPMethods.PUT, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    String driverAmount = "";

    private void showCancelDialog(String msg, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert!");
        builder.setMessage(msg);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                if (type == 4)
                    showGrabidDialog(getResources().getString(R.string.canceldriver));
                else if (type == 3)
                    showGrabidDialog(getResources().getString(R.string.cancelshipper));
                else if (type == 0)
                    cancelDelivery();
                else {
                    if (deliveryData.getAuctionBid().equals("0")) {
                        bidNow("");
                    } else {
                        bidNow(driverAmount);
                    }
                }
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAcceptDialog(final String id) {
        if (checktimings(true)) {
            showAuctionNotStart("You can not choose carrier before auction end time.");
            return;
        } else if (userInfo.getBankDetail() == null)
            AlertManager.BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savebankdetailsaccept), this.getClass().getName(), "3");
        else if (userInfo.getBankDetail().contentEquals("null"))
            AlertManager.BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savebankdetailsaccept), this.getClass().getName(), "3");
        else if (userInfo.getCreditCard() == null)
            AlertManager.BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savecreditdetailsaccept), this.getClass().getName(), "2");
        else if (userInfo.getCreditCard().contentEquals("null"))
            AlertManager.BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savecreditdetailsaccept), this.getClass().getName(), "2");
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Alert!");
            builder.setMessage("Are you sure you want to accept that carrier bid for this delivery?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    dialog.dismiss();
                    chooseBid(id);
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void showAuctionNotStart(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert!");
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                //showSignature();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showConfirmDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert!");
        builder.setMessage(msg);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                //showSignature();
                if (deliveryData.getDeliveryStatus().equals("1")) {
                    if (deliveryData.getPickUpBarcode() != null && deliveryData.getPickUpBarcode().equals("0"))
                        showImages();
                    else
                        showSignature();
                } else if (deliveryData.getDeliveryStatus().equals("2")) {
                    if (!deliveryData.getReceiver().equals("null")) {
                        String backStateName = this.getClass().getName();
                        Bundle bundle = new Bundle();
                        bundle.putString("delivery_id", deliveryID);
                        bundle.putString("backStack", "deliveryinfo");
                        Fragment fragment = new ProofOfDelivery();
                        fragment.setArguments(bundle);
                        getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                                .addToBackStack(null)
                                .commitAllowingStateLoss();
                    } else if (deliveryData.getDropOffBarCode() != null && deliveryData.getDropOffBarCode().equals("0"))
                        showImages();
                    else
                        showSignature();
                } else
                    showImages();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static Handler handler;
    public static Runnable runnable;

    public void countDownStart(final String dateTime, final int type) {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
                    // Here Set your Event Date
                    Date eventDate = dateFormat.parse(auctionDateTime);
                    Date currentDate = currentDatee;
                    // Date abc = new Date();
                    currentDatee.setTime(currentDatee.getTime() + 1000);
                    long diff = 0;
                    long time1 = eventDate.getTime();
                    long time2 = currentDate.getTime();
                    diff = eventDate.getTime() - currentDate.getTime();
                    System.out.println("different : " + diff);
                    Log.v("event end Date", "event end Date:" + eventDate);
                    Log.v("currentDate Date", "currentDate Date:" + currentDate);
                    if (diff > 0) {
                        long days = diff / (24 * 60 * 60 * 1000);
                        diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;
                        String value;
                        if (auctionType == 1) {
                            value = "AUCTION STARTS IN - ";
                            if (incomingType.equals("driver"))
                                submit.setVisibility(View.GONE);
                        } else {
                            value = "AUCTION ENDS IN - ";
                            //   HomeActivity.edit.setVisibility(View.GONE);
                            if (mAuction) {
                                mAuction = false;
                                /*try {
                                    if (deliveryData != null && userInfo != null) {
                                        if (deliveryData.getUserID().equals(userInfo.getId()))
                                            HomeActivity.edit.setVisibility(View.GONE);
                                    }
                                } catch (Exception e) {
                                    e.toString();
                                }*/
                            }
                            if (deliveryData.getBidStatus().equals("null")) {
                                submit.setVisibility(View.VISIBLE);
                            } else {
                                if (!deliveryData.getUserID().equals(userInfo.getId())) {
                                   /* submit.setVisibility(View.VISIBLE);
                                    submit.setText("BID SUBMITTED");
                                    submit.setOnClickListener(null);*/
                                }
                            }
                        }
                        if (days > 0)
                            value = value + String.format("%02d", days) + ":";
                        if (hours > 0)
                            value = value + String.format("%02d", hours) + ":";
                        auctionStatus.setText(value
                                + String.format("%02d", minutes) + ":"
                                + String.format("%02d", seconds));
                    } else {
                        refreshAuction();
                        handler.removeCallbacks(runnable);
                        handler.removeMessages(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    private void getDriverPrice(String bidPrice) {
        type = "driver_amount";
        String url = Config.SERVER_URL + Config.DRIVER_AMOUNT /*+ "/" + deliveryID*/;
        Log.d("url", url);
        HashMap<String, String> params = new HashMap<>();
        params.put("amount", bidPrice);
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI;
            mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void changeNotificationStatus() {
        type = "change_notification";
        Log.v("id:", "id:" + session.getNotificationID());
        String url = Config.SERVER_URL + Config.NOTIFICATION + Config.READ_STATUS + "?id=" + session.getNotificationID();
        Log.d("url", url);
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI;
            mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }


}
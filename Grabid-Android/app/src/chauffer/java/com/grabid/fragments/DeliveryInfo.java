package com.grabid.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grabid.R;
import com.grabid.activities.AllocateDrivers;
import com.grabid.activities.FavoriteGroupSelectionList;
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
import com.grabid.models.Delivery;
import com.grabid.models.DeliveryUpdate;
import com.grabid.models.UserInfo;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.grabid.common.AlertManager.messageDialog;

/**
 * Created by vinod on 10/14/2016.
 */
public class DeliveryInfo extends Fragment implements View.OnClickListener,
        AsyncTaskCompleteListener {
    EditText holderName, cardNumber, cvv;
    TextView auctionStatus, shipperName;
    TextView deliveryTitle, puContactPerson, puMobile, puDateTime, auctionStart, auctionEnd, maxOpeningBid,
            puAddress, doAddress, submit, mRelist, delivery, deliveryType, accept, reject, allocateDriver,
            senderName, receiverName, bidTitle, suitableVehicle, totalQty;
    LinearLayout layShipper, laySender, layreceiver, layBids, layBid, layQty,
            laySuitableVehicle, lay_drop_off;
    LinearLayout layAuction;
    SessionManager session;
    String type, deliveryID, bookmarkID, shipperID, deliveryTypeID;
    RatingBar shipperRating;
    ImageView detail, receiverSign, senderSign, bookmark, msg, call;
    //    ImageView itemImage;
    String incomingType = "";
    String incoming_delivery_type = "";
    Delivery deliveryData;
    UserInfo userInfo;


    ImageView copy_delivery;
    RelativeLayout shipperPic;
    //TextView pu_date_type, do_date_type;
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
    String pickUpMobileStr = "";
    String mfav_user_id;
    LinearLayout mPaymentTypesLnr;
    TextView mPaymentType,note;


    public void UpdateDesign() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentDatee = new Date();
        try {
            PowerManager powerManager = (PowerManager) getActivity().getSystemService(getActivity().POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "MyWakelockTag");
            wakeLock.acquire();
        } catch (Exception e) {
            e.toString();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updatedelivery, new IntentFilter("updatedelivery"));

    }

    private BroadcastReceiver updatedelivery = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String str = intent.getAction();
            String del_id = "";
            try {
                if (str != null && str.contentEquals("updatedelivery")) {
                    if (intent.hasExtra("delivery_id")) {
                        del_id = intent.getStringExtra("delivery_id");
                    }
                    if (del_id.contentEquals(deliveryID))
                        getDelivery();
                    //page = 1;
                    //getFeedbacks(type);

                }
            } catch (Exception e) {
                e.toString();
            }

        }
    };

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updatedelivery);

    }

    @Override
    public void onStart() {
        super.onStart();
        HomeActivity.IsDeliveryInfo = true;
        if (!deliveryID.contentEquals(""))
            DeliveryUpdate.setDeliveryId(deliveryID);
        //  getDelivery();
    }

    @Override
    public void onStop() {
        super.onStop();
        HomeActivity.IsDeliveryInfo = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HomeActivity.edit.setVisibility(View.VISIBLE);
        HomeActivity.title.setText(getResources().getString(R.string.jobdetail));
        HomeActivity.track_delivery.setVisibility(View.VISIBLE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        mAuction = true;
        View view = inflater.inflate(R.layout.delivery_info, null);
        init(view);
        if (getArguments().containsKey("delivery_id")) {
            deliveryID = getArguments().getString("delivery_id");
            incomingType = getArguments().getString("incoming_type");
            //  getDelivery();
        }
        if (getArguments().containsKey("data")) {
            HashMap<String, Delivery> map = (HashMap<String, Delivery>) getArguments().getSerializable("data");
            deliveryData = map.get("data");
            incomingType = getArguments().getString("incoming_type");
            incoming_delivery_type = getArguments().getString("incoming_delivery_type");
            deliveryID = deliveryData.getId();
            // getDelivery();
           /* if (deliveryData.getUserID().equals(userInfo.getId())) {
                if (deliveryData.getDeliveryStatus().equals("3")) {
                    deliveryID = deliveryData.getId();
                    getDelivery();
                } else
                    appendData();
            } else {
                appendData();
            }*/

        } else if (getArguments().containsKey("delivery_id")) {
            deliveryID = getArguments().getString("delivery_id");
            incomingType = getArguments().getString("incoming_type");
            incoming_delivery_type = getArguments().getString("incoming_delivery_type");
            //getDelivery();
        }
        if (deliveryID != null && !deliveryID.contentEquals(""))
            getDelivery();
        if (incomingType.equals("bookmark") || incomingType.equals("search")) {
            if (!deliveryData.getUserID().equals(userInfo.getId())) {
                HomeActivity.edit.setVisibility(View.GONE);
                HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
                submit.setBackgroundColor(getResources().getColor(R.color.seagreen));
                HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
                HomeActivity.title.setTextColor(getResources().getColor(R.color.white));
                HomeActivity.track_delivery.setTextColor(getResources().getColor(R.color.seagreen));
                if (deliveryData.getDeliveryStatus().equals("0") || deliveryData.getDeliveryStatus().equals("4")) { //pending
                    HomeActivity.track_delivery.setVisibility(View.GONE);
                } else {
                    if (deliveryData.getDeliveryStatus().equals("3"))
                        HomeActivity.track_delivery.setText("Track Transfer");
                    else
                        HomeActivity.track_delivery.setText("Propose Directions");
                    HomeActivity.track_delivery.setVisibility(View.VISIBLE);
                }
            } else {
                HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.seagreen));
                submit.setBackgroundColor(getResources().getColor(R.color.blue));
                HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
                HomeActivity.title.setTextColor(getResources().getColor(R.color.white));
                HomeActivity.track_delivery.setTextColor(getResources().getColor(R.color.white));
                if (deliveryData.getDeliveryStatus().equals("4"))
                    HomeActivity.track_delivery.setVisibility(View.GONE);
                if (deliveryData.getDeliveryStatus().equals("0")) { //pending
                    HomeActivity.edit.setVisibility(View.VISIBLE);
                    HomeActivity.edit.setBackgroundResource(R.drawable.edit_white);
                    HomeActivity.track_delivery.setVisibility(View.GONE);
                } else {
                    HomeActivity.edit.setVisibility(View.GONE);
                    HomeActivity.track_delivery.setText("Track Transfer");
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
                HomeActivity.track_delivery.setText("Track Transfer");
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
            HomeActivity.title.setTextColor(getResources().getColor(R.color.white));
            HomeActivity.track_delivery.setTextColor(getResources().getColor(R.color.seagreen));
            if (incoming_delivery_type.equals("0") || incoming_delivery_type.equals("4")) { //pending
                HomeActivity.track_delivery.setVisibility(View.GONE);
            } else {
                if (incoming_delivery_type.equals("3"))
                    HomeActivity.track_delivery.setText("Track Transfer");
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
                                        HomeActivity.track_delivery.setText("Track Transfer");
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
        HomeActivity.edit.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                //   UpdateFragment();
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
                bundle.putBoolean("isBack", true);
                fragment.setArguments(bundle);
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        });
        HomeActivity.track_delivery.setOnClickListener(new View.OnClickListener() {
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

        return view;
    }

    // modified by VK - type to differentiate for reject allocation and withdraw
    public void showGrabidDialog(String message, final String type) {
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
                    // Added by VK
                    if (type.equalsIgnoreCase("reject"))
                        rejectAllocation(reason.getText().toString());
                    else //VK end
                        cancelDeliveryUpcoming(reasonstr);
                } else
                    showMessage("Alert!", getActivity().getResources().getString(R.string.enterreason));
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


    LinearLayout actual_date;//, actual_do_date;
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
            mAddFavourite = (TextView) view.findViewById(R.id.addfavourite);
            doAddress = (TextView) view.findViewById(R.id.do_address);
            mWithDraw = (TextView) view.findViewById(R.id.needtowithdraw);
            mPaymentTypesLnr = (LinearLayout) view.findViewById(R.id.lay_paymenttypes);
            mPaymentType = (TextView) view.findViewById(R.id.payment_type);
            auctionStart = (TextView) view.findViewById(R.id.auction_start);
            auctionEnd = (TextView) view.findViewById(R.id.auction_end);
            maxOpeningBid = (TextView) view.findViewById(R.id.max_bid_price);
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
            submit = (TextView) view.findViewById(R.id.submit);
            mRelist = (TextView) view.findViewById(R.id.relist);
            submit.setOnClickListener(this);
            mRelist.setOnClickListener(this);
            accept = (TextView) view.findViewById(R.id.accept);
            accept.setOnClickListener(this);
            reject = (TextView) view.findViewById(R.id.reject);
            reject.setOnClickListener(this);
            allocateDriver = (TextView) view.findViewById(R.id.allocate_driver);
            allocateDriver.setOnClickListener(this);
            shipperRating = (RatingBar) view.findViewById(R.id.rating_shipper);
            detail = (ImageView) view.findViewById(R.id.detail);
            //    itemImage = (ImageView) view.findViewById(R.id.background);
            copy_delivery = (ImageView) view.findViewById(R.id.copy_delivery);
            mExport = (TextView) view.findViewById(R.id.export);
            actual_date = (LinearLayout) view.findViewById(R.id.actual_date);
            pu_actual_date = (TextView) view.findViewById(R.id.pu_actual_date);
            // actual_do_date = (LinearLayout) view.findViewById(R.id.actual_do_date);
            do_actual_date = (TextView) view.findViewById(R.id.do_actual_date);
            lay_drop_off = (LinearLayout) view.findViewById(R.id.lay_dropoff);
            note = (TextView) view.findViewById(R.id.note);
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
//                    if (!deliveryData.getUserID().equals(userInfo.getId()))
                    showCallDialog(pickUpMobileStr);

                }
            });
        } catch (Exception e) {
            e.toString();
        }
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

    @Override
    public void onTaskComplete(String result) {
        Log.d(Config.TAG, result);
        handleResponse(result);
    }

    public void validateVehicle() {
        type = "validatevehicle";
        String url;
        HashMap<String, String> params = new HashMap<>();
        url = Config.SERVER_URL + Config.VELIDATE_VEHICLE_DATA;
        params.put("delivery_id", deliveryData.getId());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI;
            mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));

    }

    private void handleResponse(String result) {
        Log.v("result", "result info:.." + result);
        if (type.equals("driverPenality")) {
            try {
                JSONObject outJson = new JSONObject(result);
                if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                    showSuccessMessage(outJson.getString(Config.MESSAGE));
                    return;
                }
            } catch (Exception e) {
                e.toString();
            }
        }

        if (type.equals("shipperPenality")) {
            try {
                JSONObject outJson = new JSONObject(result);
                if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                    showSuccessMessage(outJson.getString(Config.MESSAGE));
                    return;
                }
            } catch (Exception e) {
                e.toString();
            }
        }
        if (type.equals("addfav")) {
            try {
                JSONObject outJson = new JSONObject(result);
                if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                    showSuccessMessage(outJson.getString(Config.MESSAGE));
                    return;
                }
            } catch (Exception e) {
                e.toString();
            }
        }
        if (type.equals("validatevehicle")) {
            try {
                JSONObject outJson = new JSONObject(result);
                if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                    try {
                        if (userInfo.getBankDetail() == null)
                            AlertManager.BankmessageDialog((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savebankdetailsbidnow), this.getClass().getName(), "3");
                        else if (userInfo.getBankDetail().contentEquals("null"))
                            AlertManager.BankmessageDialog((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savebankdetailsbidnow), this.getClass().getName(), "3");
                        else if (userInfo.getCreditCard() == null)
                            AlertManager.BankmessageDialog((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savecreditdetailsbidnow), this.getClass().getName(), "2");
                        else if (userInfo.getCreditCard().contentEquals("null"))
                            AlertManager.BankmessageDialog((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savecreditdetailsbidnow), this.getClass().getName(), "2");
                        else if (deliveryData.getAuctionBid().equals("3")) {
                            //bidNow("");
                            getDriverPrice(deliveryData.getFixedOffer());
                        } else
                            showBidNowDialog();
                    } catch (Exception e) {
                        e.toString();
                    }
                }/* else if (outJson.getInt(Config.STATUS) == APIStatus.UNPROCESSABLE) {
                    AlertManager.messageDialog(getActivity(), "Alert!", outJson.getString("message"));
                }
                return;*/
            } catch (Exception e) {
                e.toString();
            }
        }
        if (type.equals("cancel")) {
            if (Integer.parseInt(result) == APIStatus.SUCCESS) {
                showSuccessMessage("Your transfer has been cancelled successfully.");
            } else
                showMessage("Error!", getResources().getString(R.string.fail));
            return;
        }
        if (type.equals("delete")) {
            if (Integer.parseInt(result) == APIStatus.SUCCESS) {
                bookmark.setTag("0");
                showSuccessMessage(getActivity().getResources().getString(R.string.unbookmarksuccessfully));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    bookmark.setImageDrawable(getResources().getDrawable(R.drawable.banner_icon, getActivity().getTheme()));
                } else {
                    bookmark.setImageDrawable(getResources().getDrawable(R.drawable.banner_icon));
                }
            } else
                showMessage("Error!", getResources().getString(R.string.fail));
        } else if (type.equals("change_notification")) {
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
                Log.v("result", "result" + result);
            } catch (Exception e) {

            }

        } else {
            try {
                JSONObject outJson = new JSONObject(result);
                if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                    // Added by VK
                    if (type.equals("accept")) {
                        accept.setVisibility(View.GONE);
                        reject.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);
                        submit.setText("CONFIRM TRANSFER PICK UP");
                        submit.setOnClickListener(this);
                        mWithDraw.setVisibility(View.GONE);
                        call.setVisibility(View.VISIBLE);
                        msg.setVisibility(View.VISIBLE);
                        HomeActivity.track_delivery.setVisibility(View.VISIBLE);
                        puMobile.setVisibility(View.VISIBLE);
                        AlertManager.messageDialog(getActivity(), "Success!", outJson.getString("message"));
                    } else if (type.equals("reject")) {
                        rejectSuccess(outJson.getString("message"));
                    } else
                        //VK end
                        if (type.equals("add")) {
                            bookmark.setTag("1");
                            showSuccessMessage(getActivity().getResources().getString(R.string.bookmarksuccessfully));
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
                        } else if (type.equals("change_status_intransiet")) {
                            showSuccessMessage(outJson.getString(Config.MESSAGE));
                        } else if (type.equals("change_status_complete")) {
                            showSuccessMessage(outJson.getString(Config.MESSAGE));
                        } else if (type.equals("cancel")) {
                            showSuccessMessage(outJson.getString(Config.MESSAGE));
                        } else if (type.equals("cancelupcoming")) {
                            if (deliveryData.getUserID().equals(userInfo.getId()))
                                showSuccessMessage(outJson.getString(Config.MESSAGE));
                            else
                                showSuccessMessage(outJson.getString(Config.MESSAGE));
                        } else if (type.equals("bid")) {
                            showSuccessMessage(getActivity().getResources().getString(R.string.bidsubmitsuccess));
                        } else if (type.equals("choose")) {
                            showSuccessMessage(outJson.getString(Config.MESSAGE));
                        } else if (type.equals("take_now")) {
                            showMessageDialog(outJson.getString(Config.MESSAGE));
                        } else if (type.equals("get")) {
                            JSONObject dataObj = outJson.getJSONObject(Config.DATA);
                            JSONObject deliveryObj = dataObj.getJSONObject(Config.JOB);
                            Delivery delivery = new Delivery();
                            Log.v("data", deliveryObj.toString());
                            delivery.setPaymentAmount(deliveryObj.getString(Keys.PAYMENT_AMOUNT));
                            delivery.setBookmarked((deliveryObj.get(Config.BOOKMARK).toString().equals("null")) ? false : true);
                            delivery.setId(deliveryObj.getString(Keys.KEY_ID));

                            delivery.setAsap((deliveryObj.get(Config.ASAP).toString().equals("1")) ? true : false);
                            delivery.setRoundTrip((deliveryObj.get(Config.ROUNDTRIP).toString().equals("1")) ? true : false);
                            delivery.setItemtype(deliveryObj.getString(Config.ITEMTYPE));

                            delivery.setTitle(deliveryObj.getString(Keys.ITEM_DELIVERY_TITLE));
                            delivery.setBidID(deliveryObj.getString(Keys.BID_ID));
                            delivery.setBidStatus(deliveryObj.get(Keys.BID_STATUS).toString());
                            delivery.setDeliveryStatus(deliveryObj.getString(Keys.DELIVERY_STATUS));
                            delivery.setDropoffAdress(deliveryObj.getString(Keys.DROPOFF_ADDRESS));
                            delivery.setDriverID(deliveryObj.getString(Keys.DRIVER_ID));
                            //delivery.setItemPhoto(deliveryObj.getString(Keys.ITEM_PHOTO));

                            //Added By VK
                            delivery.setIsAbleToAllocate(deliveryObj.getBoolean(Config.IS_ABLE_TO_ALLOCATE_DRIVER));
                            delivery.setAllocateDriverID(deliveryObj.get(Config.ALLOCATE_DRIVER_ID).toString());
                            delivery.setAllocationStatus(deliveryObj.get(Config.ALLOCATION_STATUS).toString());
                            //VK end

                            delivery.setObjData(deliveryObj.toString());
                            delivery.setCompletedAt(deliveryObj.getString(Keys.COMPLETED_AT));
                            delivery.setPickUpAddress(deliveryObj.getString(Keys.PICKUP_ADDRESS));
                            delivery.setUserID(deliveryObj.getString(Keys.KEY_USER_ID));
                            delivery.setPickUpDate(deliveryObj.getString(Keys.PICKUP_DATE));

                            delivery.setPickupCountry(deliveryObj.getString(Keys.PICKUP_COUNTRY));
                            delivery.setPickupState(deliveryObj.getString(Keys.PICKUP_STATE));
                            delivery.setPickupCity(deliveryObj.getString(Keys.PICKUP_CITY));
                            delivery.setDropoffCountry(deliveryObj.getString(Keys.DROPOFF_COUNTRY));
                            delivery.setDropoffState(deliveryObj.getString(Keys.DROPOFF_STATE));
                            delivery.setDropoffCity(deliveryObj.getString(Keys.DROPOFF_CITY));

                            delivery.setStatus(deliveryObj.getString(Keys.STATUS));

                            delivery.setReceiver(deliveryObj.getString(Keys.RECEIVER_NAME));
                            delivery.setReceiverSign(deliveryObj.getString(Keys.RECEIVER_SIGN));
                            delivery.setSender(deliveryObj.getString(Keys.FROM_PICKUP_NAME));
                            delivery.setSenderSign(deliveryObj.getString(Keys.FROM_PICKUP_SIGN));
                            delivery.setQty(deliveryObj.getString(Keys.ITEM_QTY));

                            delivery.setPuMobile(deliveryObj.getString(Keys.PICKUP_MOBILE));
                            delivery.setPuContactPerson(deliveryObj.getString(Keys.PICKUP_CONTACT_PERSON));

                            delivery.setPuLat(deliveryObj.getString(Keys.PICKUP_LATITUDE));  //plz chk
                            delivery.setPuLng(deliveryObj.getString(Keys.PICKUP_LONGITUDE));
                            delivery.setDoContactPerson(deliveryObj.getString(Keys.DROPOFF_CONTACT));
                            delivery.setDoLat(deliveryObj.getString(Keys.DROPOFF_LATITUDE)); //plz chk
                            delivery.setDoLng(deliveryObj.getString(Keys.DROPOFF_LONGITUDE));

                            delivery.setFixedOffer(deliveryObj.getString(Keys.FIXED_OFFER));
                            delivery.setAuctionStart(deliveryObj.getString(Keys.AUCTION_START_TIME));
                            delivery.setAuctionEnd(deliveryObj.getString(Keys.AUCTION_END_TIME));
                            delivery.setMaxOpeningBid(deliveryObj.getString(Keys.MAX_AUCTION_BID));
                            delivery.setAuctionBid(deliveryObj.getString(Keys.AUCTION_BID));
                            //delivery.setDoAppoint(deliveryObj.getString(Keys.DROPOFF_APPOINTMENT));
                            //delivery.setMore(deliveryObj.getString(Keys.ITEM_MORE));
                            delivery.setDoCall(deliveryObj.getString(Keys.DROPOFF_CALL));
                            delivery.setUser_Group(deliveryObj.getString(Keys.USER_GROUP));
                            delivery.setPayment_mode(deliveryObj.getString(Keys.PAYMENT_MODE));
                            try {
                                delivery.setRelistNotification(deliveryObj.getString(Keys.RELIST_NOTIFICATION));
                            } catch (Exception e) {
                                e.toString();
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
                            delivery.setGeo(deliveryObj.getString(Keys.GEO_ZONE));
                            delivery.setRadius(deliveryObj.getString(Keys.RADIUS));

                            delivery.setDeliveryStatus(deliveryObj.getString(Keys.DELIVERY_STATUS)); //added
                            delivery.setDeliveryName(deliveryObj.getString(Keys.DELIVERY_TYPE_NAME));

                            delivery.setShipperName(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.USERNAME));
                            delivery.setShipperImage(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.PROFILE_IMAGE));
                            delivery.setShipperRating(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.RATING));
                            delivery.setShipperID(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.KEY_ID));

                            delivery.setBidArray(deliveryObj.get(Keys.BIDS).toString());
                            delivery.setChoosedBidsArray(deliveryObj.get(Keys.BID_CHOOSED).toString());
                            delivery.setHasDriverFeedback(deliveryObj.get(Keys.FEEDBACK_TO_DRIVER).toString().equals("null") ? false : true);
                            delivery.setHasShipperFeedback(deliveryObj.get(Keys.FEEDBACK_TO_SHIPPER).toString().equals("null") ? false : true);

                            delivery.setSuitableVehicles(deliveryObj.get(Keys.SUITABLE_VEHICLE_TEXT).toString());

                            delivery.setPickupSpecialRestriction(deliveryObj.getString(Keys.PICKUP_SPECIAL_RESTRICTION));

                            delivery.setSpecialPermitDetail(deliveryObj.getString(Keys.SPECIAL_PERMIT_DETAIL));
                            delivery.setSuitabelVehicle(deliveryObj.getString(Keys.SUITABLE_VEHICAL_IDS));

                            delivery.setFromPickUpAt(deliveryObj.getString("from_pick_up_at"));
                            delivery.setCompleted_at(deliveryObj.getString("completed_at"));
                            delivery.setCompanyLogo(deliveryObj.getString(Keys.COMPANY_LOGO));
                            try {
                                if (deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).has(Keys.MOBILE)) {
                                    delivery.setSender_Mobile(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.MOBILE));
                                    delivery.setSender_email(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.EMAIL));
                                }
                            } catch (Exception e) {
                                e.toString();
                            }
                            try {
                                if (!delivery.getDeliveryStatus().equals("0")) {
                                    delivery.setDriver_name(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.USERNAME));
                                    delivery.setDriver_image(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.PROFILE_IMAGE));
                                    delivery.setDriver_rating(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.RATING));
                                    delivery.setDriver_id(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.KEY_ID));
                                    delivery.setIsFavouriteUser(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.ISFAVOURITE_USER));
                                    delivery.setDriver_mobile(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.MOBILE));
                                    delivery.setDriver_email(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.EMAIL));

                                }
                            } catch (Exception e) {
                                e.toString();
                            }
                            try {
                                if (deliveryObj.getString(Keys.ALLOCATED_DEIVER_DETAIL) != null && !deliveryObj.getString(Keys.ALLOCATED_DEIVER_DETAIL).contentEquals("null")) {
                                    delivery.setAloMobile(deliveryObj.getJSONArray(Keys.ALLOCATED_DEIVER_DETAIL).getJSONObject(0).getString(Keys.MOBILE));
                                    delivery.setAloemail(deliveryObj.getJSONArray(Keys.ALLOCATED_DEIVER_DETAIL).getJSONObject(0).getString(Keys.EMAIL));
                                    delivery.setAldriver_name(deliveryObj.getJSONArray(Keys.ALLOCATED_DEIVER_DETAIL).getJSONObject(0).getString(Keys.USERNAME));
                                    delivery.setAldriver_image(deliveryObj.getJSONArray(Keys.ALLOCATED_DEIVER_DETAIL).getJSONObject(0).getString(Keys.PROFILE_IMAGE));
                                    delivery.setAldriver_rating(deliveryObj.getJSONArray(Keys.ALLOCATED_DEIVER_DETAIL).getJSONObject(0).getString(Keys.RATING));
                                    delivery.setAldriver_id(deliveryObj.getJSONArray(Keys.ALLOCATED_DEIVER_DETAIL).getJSONObject(0).getString(Keys.KEY_ID));

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
                                    HomeActivity.title.setTextColor(getResources().getColor(R.color.white));
                                    HomeActivity.track_delivery.setTextColor(getResources().getColor(R.color.seagreen));
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
                                                    HomeActivity.track_delivery.setText("Track Transfer");
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
                                    HomeActivity.track_delivery.setTextColor(getResources().getColor(R.color.white));
                                    if (deliveryData.getDeliveryStatus().equals("4"))
                                        HomeActivity.edit.setVisibility(View.VISIBLE);

                                    if (deliveryData.getDeliveryStatus().equals("0")) { //pending
                                        HomeActivity.edit.setVisibility(View.VISIBLE);
                                        HomeActivity.edit.setBackgroundResource(R.drawable.edit_white);
                                        HomeActivity.track_delivery.setVisibility(View.GONE);
                                    } else {
                                        HomeActivity.edit.setVisibility(View.GONE);
                                        HomeActivity.track_delivery.setText("Track Transfer");
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
                                    message = "Are you sure you want to accept this Take Now offer for this transfer? " +
                                            "\nReminder: if successful your payment for this transfer will be " + dataObj.getString("driver_amount");
                                else
                                    message = "Are you sure you want to bid for this transfer? " +
                                            "\nReminder: if successful your payment for this transfer will be " + dataObj.getString("driver_amount");
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
                                    AlertManager.BankmessageDialog((HomeActivity) getActivity(), "Alert!", outJson.getString(Config.MESSAGE), this.getClass().getName(), inJsonType);
                                } catch (Exception e) {
                                    e.toString();
                                }
                            } else if (inJson != null && inJson.has("delivery_status")) {
                                if (inJson.optString("delivery_status").contentEquals("1"))
                                    CancelStatus(message);
                            } else {
                                if (message != null && !message.contentEquals("")) {
                                    showMessage("Alert!", message);
                                }
                            }

                        } catch (Exception e) {
                            e.toString();
                        }

                    } else if (outJson.getString(Config.MESSAGE).equals(""))
                        showMessage("Alert!", outJson.getJSONArray(Config.DATA).getJSONObject(0).getString(Config.MESSAGE));
                    else if (!outJson.getString(Config.MESSAGE).equals("")) {
                        showMessage("Alert!", outJson.getString(Config.MESSAGE));
                    } else {

                        try {
                            JSONObject inJson = outJson.optJSONObject("data");
                            String inJsonType = inJson.optString("type");
                            AlertManager.BankmessageDialog((HomeActivity) getActivity(), "Alert!", outJson.getString(Config.MESSAGE), this.getClass().getName(), inJsonType);
                        } catch (Exception e) {
                            e.toString();
                        }
                    }
                } else if (outJson.getInt(Config.STATUS) == APIStatus.INVALID_CARD) {
                    try {
                        AlertManager.BankmessageDialog((HomeActivity) getActivity(), "Alert!", outJson.getString(Config.MESSAGE), this.getClass().getName(), "5");
                    } catch (Exception e) {
                        e.toString();
                    }
                } else {
                    showMessage("Error", getResources().getString(R.string.no_response));
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

    public void sendEmailMesage() {
        try {
            String email = "";
            if (!deliveryData.getUserID().equals(userInfo.getId()))
                email = deliveryData.getSender_email();
            else {
             /*   if (deliveryData.getAllocateDriverID() != null && !deliveryData.getAllocateDriverID().contentEquals("") && !deliveryData.getAllocateDriverID().contentEquals("null")) {
                    if (deliveryData.getAloMobile() != null && !deliveryData.getAloMobile().contentEquals("null"))
                        email = deliveryData.getAloemail();
                    else
                        email = deliveryData.getDriver_email();
                } else*/
                email = deliveryData.getDriver_email();
            }
//            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + ""));
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", email, null));
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

    public void sendEmail() {
        try {
//            String link = "http://dev.grabid.com.au/chauffer/web/job/pdf?id=" + deliveryData.getId();
            String link = Config.BASE_URL + "/chauffeur/job/pdf?id=" + deliveryData.getId();
            if (deliveryData.getUserID().equals(userInfo.getId()))
                link = link + "&type=1";
            else
                link = link + "&type=2";
            String message = "Hi \n" +
                    "\n" +
                    " Please click on the link below to download the complete transfer details for \"" + deliveryData.getTitle() + "\"" + ": \n\n" + link + "\n\n" + "Thanks" + "\n" + "The GRABiD Team";
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 18 && resultCode == -1) {
            try {
                String val = data.getStringExtra("value");
                Log.v("val", val);
                String url = "";
                type = "addfav";
                HashMap<String, String> params = new HashMap<>();
                url = Config.SERVER_URL + Config.ADD_FAVOURIE_PARICULAR_GROUP;
                params.put("group_id", val);
                params.put("fav_user_id", mfav_user_id);
                Log.d("end", params.toString());
                if (Internet.hasInternet(getActivity())) {
                    RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
                    mobileAPI.execute(url, session.getToken());
                } else
                    showMessage("Alert!", getResources().getString(R.string.no_internet));
            } catch (Exception e) {
                e.toString();
            }
        } else if (requestCode == 19 && resultCode == Activity.RESULT_OK) {
            String allocated = data.getStringExtra("allocated");
            Log.e("data", allocated);
            getDelivery();
        }
    }

    public void showADDialog(final String fav_user_id) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // builder.setCancelable(false);
        //  builder.setTitle(getResources().getString(R.string.allorselected));
        builder.setMessage(getResources().getString(R.string.allorselected));
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              /*  type = "addfav";
                String url = "";
                HashMap<String, String> params = new HashMap<>();
                url = Config.SERVER_URL + Config.ADD_FAVOURIE_PARICULAR_GROUP;
                params.put("group_id", "all");
                params.put("fav_user_id", fav_user_id);
                Log.d("end", params.toString());
                call(params, url);*/
            }
        });
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // getDelivery();
                mfav_user_id = fav_user_id;
                getfavourites();

            }
        });
        builder.show();

    }

    public void call(HashMap<String, String> params, String url) {
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage("Alert!", getResources().getString(R.string.no_internet));
    }

    public void getfavourites() {
        Intent intent = new Intent(getActivity(), FavoriteGroupSelectionList.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("IsPageNation", true);
        intent.putExtras(bundle);
        startActivityForResult(intent, 18);
    }

    public void Add(String fav_user_id) {
      /*  type = "addfav";
        HashMap<String, String> params = new HashMap<>();
        String url = Config.SERVER_URL + Config.ADD_FAVOURITE;
        params.put("fav_user_id", fav_user_id);
        Log.d("end", params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage("Alert!", getResources().getString(R.string.no_internet));
*/
    }

    // Added by VK
    private void acceptAllocation() {
        type = "accept";
        HashMap<String, String> params = new HashMap<>();
        params.put("delivery_id", deliveryID);
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(Config.ACCEPT_ALLOCATION, session.getToken());
        } else
            showMessage(TITLE, getResources().getString(R.string.no_internet));
    }

    private void rejectAllocation(String reason) {
        type = "reject";
        HashMap<String, String> params = new HashMap<>();
        params.put("delivery_id", deliveryID);
        params.put("reason", reason);
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(Config.REJECT_ALLOCATION, session.getToken());
        } else
            showMessage(TITLE, getResources().getString(R.string.no_internet));
    }

    public void getAvailableDriver() {
        Intent intent = new Intent(getActivity(), AllocateDrivers.class);
        intent.putExtra("delivery_id", deliveryID);
        intent.putExtra("incomingType", "chauffeur");
        startActivityForResult(intent, 19);
    }

    //VK end
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Added by VK
            case R.id.allocate_driver:
                getAvailableDriver();
                break;
            case R.id.accept:
                acceptAllocation();
                //acceptAllocationPrompt("Alert!", "Are you sure to accept allocation for this delivery?");
                break;
            case R.id.reject:
                showCancelDialog(getActivity().getResources().getString(R.string.rejectallocatedtransfer), 8);
                break;
            // VK end
            case R.id.needtowithdraw: {
                showCancelDialog(getActivity().getResources().getString(R.string.withdrawtransfer), 4);
                break;
            }
            case R.id.addfavourite:
                //Add(deliveryData.getDriver_id());
                showADDialog(deliveryData.getDriver_id());
                break;
            case R.id.export:
                sendEmail();
                break;
            case R.id.call:
                try {
                    StringBuilder str;
                    if (!deliveryData.getUserID().equals(userInfo.getId()))
                        str = new StringBuilder(deliveryData.getSender_Mobile());
                    else {
                     /*   if (deliveryData.getAllocateDriverID() != null && !deliveryData.getAllocateDriverID().contentEquals("") && !deliveryData.getAllocateDriverID().contentEquals("null")) {
                            if (deliveryData.getAloMobile() != null && !deliveryData.getAloMobile().contentEquals("null"))
                                str = new StringBuilder(deliveryData.getAloMobile());
                            else
                                str = new StringBuilder(deliveryData.getDriver_mobile());
                        } else*/
                        str = new StringBuilder(deliveryData.getDriver_mobile());
                    }
                    str.insert(3, " ");
                    if (str.toString().substring(4).startsWith("0"))
                        str.deleteCharAt(4);
                    String mob = str.toString();
                    showCallDialog(mob);
                } catch (Exception e) {
                    e.toString();
                }

                // doCallMessage(1, deliveryData.getSender_Mobile());
                break;
            case R.id.msg:
                doCallMessage(2, "");
//                composeEmail();
                break;
            case R.id.detail:
                showShipperInfo();
                break;
            case R.id.submit:
                if (submit.getText().toString().equalsIgnoreCase("Take Now")) {
                    try {
                        if (userInfo.getBankDetail() == null)
                            AlertManager.BankmessageDialog((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savebankdetails), this.getClass().getName(), "3");
                        else if (userInfo.getBankDetail().contentEquals("null"))
                            AlertManager.BankmessageDialog((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savebankdetails), this.getClass().getName(), "3");
                        else if (userInfo.getCreditCard() == null)
                            AlertManager.BankmessageDialog((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savecreditdetails), this.getClass().getName(), "2");
                        else if (userInfo.getCreditCard().contentEquals("null"))
                            AlertManager.BankmessageDialog((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savecreditdetails), this.getClass().getName(), "2");
                        else
                            getDriverPrice(deliveryData.getFixedOffer());
                    } catch (Exception e) {
                        e.toString();
                    }

                    //showCancelDialog("Are you sure you want to accept this Take Now offer for this delivery?", 1);
                } else if (submit.getText().toString().equalsIgnoreCase("Cancel Transfer")) {
                    try {
                        if (deliveryData != null && deliveryData.getDeliveryStatus().equals("1"))
                            showCancelDialog(getActivity().getResources().getString(R.string.canceltransfer), 3);
                        else
                            showCancelDialog(getActivity().getResources().getString(R.string.canceltransfer), 0);
                    } catch (Exception e) {
                        e.toString();
                    }
                } else if (submit.getText().toString().equalsIgnoreCase("Bid Now")) {
                    if (checktimings(false)) {
                        showAuctionNotStart(getActivity().getResources().getString(R.string.auctionnotyetactive));
                        return;
                    }
                    validateVehicle();

                } else if (submit.getText().toString().equalsIgnoreCase("Confirm Transfer Pick up")) {
                    showConfirmDialog(getActivity().getResources().getString(R.string.transferstatustointransiet), true);
                } else if (submit.getText().toString().equalsIgnoreCase("Confirm Transfer Drop Off")) {
                    showConfirmDialog(getActivity().getResources().getString(R.string.transferstatustocomplete), false);
                } else if (submit.getText().toString().equalsIgnoreCase("Rate Dispatcher")) {
                    addFeedback(true);
                } else if (submit.getText().toString().equalsIgnoreCase("Rate Driver")) {
                    addFeedback(true);
                }
                break;
            case R.id.layOver:
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
                bundle.putBoolean("isBack", true);
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
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        proceed.setLayoutParams(layoutParams);
        TextView bidTitle = (TextView) alertDialog.findViewById(R.id.bidtitle);
        bidTitle.setText("Maximum Opening Bid - $" + deliveryData.getMaxOpeningBid());
        cancel.setVisibility(View.GONE);
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
                            showMessage("Error!", getActivity().getResources().getString(R.string.entervalidamount));
                        }
                    } catch (Exception e) {
                        e.toString();
                    }
                } else
                    showMessage("Alert!", getActivity().getResources().getString(R.string.enterbidprice));
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


    private void addFeedback(Boolean isDeliveryInfoBack) {
        String type = "shipper";
        String backStateName = this.getClass().getName();
        Bundle bundle = new Bundle();
        Fragment fragment = new AddFeedback();
        String submittext = submit.getText().toString();
        if (submittext != null && !submittext.isEmpty()) {
            if (submittext.contentEquals("RATE DRIVER")) {
                type = "shipper";
            } else {
                type = "driver";
            }
        }
        bundle.putString("delivery_id", deliveryID);
        bundle.putString("incoming_type", type);
        bundle.putBoolean("isDeliveryInfoBack", isDeliveryInfoBack);
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
           /* if (deliveryData.getAllocateDriverID() != null && !deliveryData.getAllocateDriverID().contentEquals("") && !deliveryData.getAllocateDriverID().contentEquals("null"))
                bundle.putString("shipper_id", deliveryData.getAldriver_id());
            else*/
            bundle.putString("shipper_id", deliveryData.getDriver_id());
            bundle.putBoolean("isShipper", false);
        } else {
            bundle.putString("shipper_id", shipperID);
            bundle.putBoolean("isShipper", true);
        }
        fragment.setArguments(bundle);
        getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
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

    private void appendData() {
        deliveryID = deliveryData.getId();
        deliveryTypeID = deliveryData.getDeliveryTypeID();
        bookmarkID = deliveryData.getBookmarkID();
        note.setText(deliveryData.getPickupSpecialRestriction());

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
        //   shipperName.setText(deliveryData.getShipperName());
        //imgLoaderr.DisplayImage(deliveryData.getShipperImage(), shipperPic);
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
        // imgLoader.DisplayImage(deliveryData.getItemPhoto(), itemImage);
      /*  if (deliveryData.getCompanyLogo() != null)
            Picasso.with(getActivity()).load(deliveryData.getCompanyLogo()).into(itemImage);*/
           /* Glide.with(getActivity())
                    .load(deliveryData.getCompanyLogo())
                    .into(itemImage);*/
        //  shipperRating.setRating(Float.valueOf(deliveryData.getShipperRating()));
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
        if (deliveryData.getDeliveryStatus().equals("0") || deliveryData.getDeliveryStatus().equals("1") || deliveryData.getDeliveryStatus().equals("4"))
            puDateTime.setText(deliveryData.getPickUpDate());
        else if (deliveryData.getDeliveryStatus().equals("2") || deliveryData.getDeliveryStatus().equals("3"))
            puDateTime.setText(deliveryData.getPickUpDate());

        doAddress.setText(deliveryData.getDropoffAdress());
        totalQty.setText(deliveryData.getQty());
        // suitableVehicle.setText(deliveryData.getSuitableVehicles());
        try {
            String suitvehicles = deliveryData.getSuitableVehicles();
            if (deliveryData.getSuitableVehicles() != null && deliveryData.getSuitableVehicles().contains(","))
                suitableVehicle.setText(deliveryData.getSuitableVehicles().replace(',', '\n'));
            else
                suitableVehicle.setText(deliveryData.getSuitableVehicles());
        } catch (Exception e) {
            e.toString();
        }
        shipperRating.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        if (deliveryData.getAuctionBid().equals("2")) {
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
// Added By VK
        if (deliveryData.getAllocateDriverID() != null && deliveryData.getAllocateDriverID().equalsIgnoreCase(session.getUserDetails().getId()))
            maxOpeningBid.setText("Allocated Transfer");
        // VK end

        try {
            if (!deliveryData.getFromPickUpAt().equals("null")) {
                actual_date.setVisibility(View.GONE);
                pu_actual_date.setText(deliveryData.getFromPickUpAt());
            } else {
                actual_date.setVisibility(View.GONE);
            }
            if (!deliveryData.getCompleted_at().equals("null")) {
                do_actual_date.setText(deliveryData.getCompleted_at());
            } else {
                //  actual_do_date.setVisibility(View.GONE);
            }
        } catch (Exception ex) {

        }


        if (deliveryData.getItemtype().equals("1")) {
            this.delivery.setText("Offload");
            mPaymentTypesLnr.setVisibility(View.GONE);
            mPaymentType.setVisibility(View.GONE);
        } else {
            this.delivery.setText("Collect");
            mPaymentTypesLnr.setVisibility(View.VISIBLE);
            mPaymentType.setVisibility(View.VISIBLE);
            setmPay_Mode();

        }
        if (deliveryData.isRoundTrip())
            deliveryType.setText("Y");
        else
            deliveryType.setText("N");


        if (!deliveryData.getPickUpDate().equals("0") && !deliveryData.getPickUpDate().equals("null")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
            Date strDate = null;
            try {
                strDate = sdf.parse(deliveryData.getPickUpDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        //Sender/Receiver Name and Sign
        Log.v("getSign", "getSign:" + deliveryData.getReceiverSign());
        if (deliveryData.getReceiver().equals("null")) {
            layreceiver.setVisibility(View.GONE);
        } else {
            layreceiver.setVisibility(View.GONE);
            receiverName.setText(deliveryData.getReceiver());
            //  imgLoader.DisplayImage(deliveryData.getReceiverSign(), receiverSign);
            if (deliveryData.getReceiverSign() != null && !deliveryData.getReceiverSign().contentEquals("") && !deliveryData.getReceiverSign().contentEquals("null"))
                Picasso.with(getActivity()).load(deliveryData.getReceiverSign()).into(receiverSign);

        }
        Log.v("getSign", "getSign:" + deliveryData.getSenderSign());
        if (deliveryData.getSender().equals("null")) {
            laySender.setVisibility(View.GONE);
        } else {
            laySender.setVisibility(View.GONE);
            senderName.setText(deliveryData.getSender());
            // imgLoader.DisplayImage(deliveryData.getSenderSign(), senderSign);
            if (deliveryData.getSenderSign() != null && !deliveryData.getSenderSign().contentEquals("") && !deliveryData.getSenderSign().contentEquals("null"))
                Picasso.with(getActivity()).load(deliveryData.getSenderSign()).into(senderSign);
        }
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
        refreshAuction();
        //Bids and Choose Bids
        if (incomingType.equals("shipper") || deliveryData.getUserID().equals(userInfo.getId())) {
            if (!deliveryData.getBidArray().equals("null")) {
                try {
                    if (!deliveryData.getDeliveryStatus().equals("1") && !deliveryData.getDeliveryStatus().equals("2") && !deliveryData.getDeliveryStatus().equals("3") && !deliveryData.getDeliveryStatus().equals("4")) {
                        final JSONArray bidArray;
                    /*if(deliveryData.getDeliveryStatus().equals("1"))
                        else if(deliveryData.getDeliveryStatus().equals("2"))
                    else if(deliveryData.getDeliveryStatus().equals("3"))*/
                        if (deliveryData.getDeliveryStatus().equals("1") ||
                                deliveryData.getDeliveryStatus().equals("2") ||
                                deliveryData.getDeliveryStatus().equals("3")) {
                            bidTitle.setText("Selected Driver:");
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
                                        addFeedback(true);
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
        if (incomingType.equals("shipper")) {
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

    public void setmPay_Mode() {
        String mPayment_mode = "";
        if (deliveryData.getPayment_mode().contains("1"))
            mPayment_mode = "Cash";
        if (deliveryData.getPayment_mode().contains("2")) {
            if (!mPayment_mode.contentEquals(""))
                mPayment_mode = mPayment_mode + ", Credit Card";
            else
                mPayment_mode = "Credit Card";
        }
        if (deliveryData.getPayment_mode().contains("3")) {
            if (!mPayment_mode.contentEquals(""))
                mPayment_mode = mPayment_mode + ",Cabcharge";
            else
                mPayment_mode = "Cabcharge";
        }
        mPaymentType.setText(mPayment_mode);
    }

    private void refreshAuction() {
        if (deliveryData.getAuctionBid().equals("2")) {
            if (deliveryData.getDeliveryStatus().equals("0")) {
                if (!deliveryData.getAuctionStart().equals("0") && !deliveryData.getAuctionStart().equals("null")) {
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
                        try {
                            handler.removeCallbacks(runnable);
                        }catch (Exception ex){
                            handler = new Handler();
                        }
                        countDownStart(deliveryData.getAuctionStart(), 1);
                    } else if (!(strDate.after(currentDatee))) {
                        if (!deliveryData.getAuctionEnd().equals("0") && !deliveryData.getAuctionEnd().equals("null")) {
                            if ((endDate.after(currentDatee))) {
                                Log.v("date", "Date2");
                                auctionStatus.setVisibility(View.VISIBLE);
                                auctionDateTime = deliveryData.getAuctionEnd();
                                auctionType = 0;
                                handler = new Handler();
                                countDownStart(deliveryData.getAuctionEnd(), 0);
                            } else if (!(endDate.after(currentDatee))) {
                                Log.v("date", "Date3");
                                if (deliveryData.getDeliveryStatus().equals("0")) {
                                    auctionStatus.setVisibility(View.VISIBLE);
                                    auctionStatus.setText("AUCTION HAS ENDED");
                                    // HomeActivity.edit.setVisibility(View.GONE);
                                    if (mAuction) {
                                        mAuction = false;
                                        try {
                                            if (deliveryData != null && userInfo != null) {
                                                if (deliveryData.getUserID().equals(userInfo.getId()))
                                                    HomeActivity.edit.setVisibility(View.GONE);
                                            }
                                        } catch (Exception e) {
                                            e.toString();
                                        }
                                    }

                                    if (!mIsAuctionEnded && mFirstUpdate && deliveryData.getUserID().equals(userInfo.getId())) {
                                        //    UpdateFragment();
                                        if (deliveryID != null && !deliveryID.contentEquals("")) {
                                            getDelivery();
                                            mFirstUpdate = false;
                                            mIsAuctionEnded = true;

                                        }

                                    }
                                } else {
                                    auctionStatus.setVisibility(View.GONE);
                                }
                                if (submit.getText().toString().equalsIgnoreCase("BID SUBMITTED")) {
                                    submit.setVisibility(View.VISIBLE);
                                } else
                                    submit.setVisibility(View.GONE);
//                        HomeActivity.edit.setVisibility(View.GONE);
                                if (incomingType.equals("driver") || incomingType.equals("search") || !deliveryData.getUserID().equals(userInfo.getId())) {
                                    if (submit.getText().toString().equalsIgnoreCase("BID SUBMITTED")) {
                                        submit.setVisibility(View.VISIBLE);
                                    } else
                                        submit.setVisibility(View.GONE);
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
                                }
                            }
                        }

                    }
                }
            }
        /*if (mFirstUpdate) {
            mFirstUpdate = false;
        }*/
        }
    }

    public void UpdateFragment() {
        try {
            FragmentTransaction ftr = getActivity().getFragmentManager().beginTransaction();
            ftr.detach(DeliveryInfo.this).attach(DeliveryInfo.this).commit();
        } catch (Exception e) {
            e.toString();
        }
    }

    private void updateShipper() {
        mWithDraw.setVisibility(View.GONE);
        mRelist.setVisibility(View.GONE);
        String auctionstatusstr = auctionStatus.getText().toString();
        copy_delivery.setVisibility(View.VISIBLE);
        bookmark.setVisibility(View.INVISIBLE);
        /*call.setVisibility(View.GONE);
        msg.setVisibility(View.GONE);*/
        if (deliveryData.getDeliveryStatus().equals("1") || deliveryData.getDeliveryStatus().equals("2") || deliveryData.getDeliveryStatus().equals("3")) {
            call.setVisibility(View.VISIBLE);
            msg.setVisibility(View.VISIBLE);
        }
        lay_drop_off.setVisibility(View.VISIBLE);
        if (deliveryData.getDeliveryStatus().equals("0") || deliveryData.getDeliveryStatus().equals("4")) {
            if (deliveryData.getShipperRating() != null)
                shipperRating.setRating(Float.valueOf(deliveryData.getShipperRating()));
            shipperName.setText(deliveryData.getShipperName());
            //imgLoaderr.DisplayImage(deliveryData.getShipperImage(), shipperPic);
            if (deliveryData.getShipperImage() != null && !deliveryData.getShipperImage().contentEquals("") && !deliveryData.getShipperImage().contentEquals("null"))
                Picasso.with(getActivity()).load(deliveryData.getShipperImage()).into(target);

        } else {
            if (deliveryData.getDriver_rating() != null)
                shipperRating.setRating(Float.valueOf(deliveryData.getDriver_rating()));
            String name = deliveryData.getDriver_name();
            shipperName.setText(deliveryData.getDriver_name());
            //imgLoaderr.DisplayImage(deliveryData.getShipperImage(), shipperPic);
            if (deliveryData.getDriver_image() != null && !deliveryData.getDriver_image().contentEquals("") && !deliveryData.getDriver_image().contentEquals("null"))
                Picasso.with(getActivity()).load(deliveryData.getDriver_image()).into(target);

           /* try {
                if (deliveryData.getAllocateDriverID() != null && !deliveryData.getAllocateDriverID().contentEquals("") && !deliveryData.getAllocateDriverID().contentEquals("null")) {
                    if (deliveryData.getAloMobile() != null && !deliveryData.getAloMobile().contentEquals("null")) {
                        shipperRating.setRating(Float.valueOf(deliveryData.getAldriver_rating()));
                        String name = deliveryData.getAldriver_name();
                        shipperName.setText(deliveryData.getAldriver_name());
                        //imgLoaderr.DisplayImage(deliveryData.getShipperImage(), shipperPic);
                        if (deliveryData.getAldriver_image() != null && !deliveryData.getAldriver_image().contentEquals("") && !deliveryData.getAldriver_image().contentEquals("null"))
                            Picasso.with(getActivity()).load(deliveryData.getAldriver_image()).into(target);

                    } else {
                        shipperRating.setRating(Float.valueOf(deliveryData.getDriver_rating()));
                        String name = deliveryData.getDriver_name();
                        shipperName.setText(deliveryData.getDriver_name());
                        //imgLoaderr.DisplayImage(deliveryData.getShipperImage(), shipperPic);
                        if (deliveryData.getDriver_image() != null && !deliveryData.getDriver_image().contentEquals("") && !deliveryData.getDriver_image().contentEquals("null"))
                            Picasso.with(getActivity()).load(deliveryData.getDriver_image()).into(target);
                    }
                } else {

                }

            } catch (Exception e) {
                e.toString();
            }*/
        }
        HomeActivity.edit.setBackgroundResource(R.drawable.edit_white);
        if (deliveryData.getDeliveryStatus().equals("0"))
            auctionStatus.setVisibility(View.VISIBLE);
        if (deliveryData.getDeliveryStatus().equals("1")) {
            auctionStatus.setVisibility(View.VISIBLE);
            auctionStatus.setText("TRANSFER ASSIGNED");
            layAuction.setVisibility(View.GONE);
        } else if (deliveryData.getDeliveryStatus().equals("2")) {
            auctionStatus.setVisibility(View.VISIBLE);
            auctionStatus.setText("TRANSFER IN-TRANSIT");
            layAuction.setVisibility(View.GONE);

        } else if (deliveryData.getDeliveryStatus().equals("3")) {
            auctionStatus.setVisibility(View.VISIBLE);
            auctionStatus.setText("TRANSFER COMPLETED");
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
            auctionStatus.setText("TRANSFER CANCELLED");
            HomeActivity.edit.setVisibility(View.GONE);
            // layAuction.setVisibility(View.GONE);

        }
        if (deliveryData.getDeliveryStatus().equals("0") ||
                deliveryData.getDeliveryStatus().equals("1")) {
            submit.setVisibility(View.VISIBLE);
            submit.setText("CANCEL TRANSFER");
            submit.setOnClickListener(this);
        } else if (deliveryData.getDeliveryStatus().equals("3") && !deliveryData.hasDriverFeedback()) {
            submit.setVisibility(View.VISIBLE);
            submit.setText("RATE DRIVER");
            submit.setOnClickListener(this);
            // auctionStatus.setVisibility(View.VISIBLE);
            //  auctionStatus.setText("Delivery completed");
        }
        try {

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

        } catch (Exception e) {
            e.toString();
        }
        if (mRelist.getVisibility() == View.VISIBLE)
            HomeActivity.edit.setVisibility(View.GONE);

    }

    private void updateDriver() {
        mRelist.setVisibility(View.GONE);
        if (deliveryData.getDeliveryStatus().equals("1")) {
            mWithDraw.setVisibility(View.VISIBLE);
        } else
            mWithDraw.setVisibility(View.GONE);
        //   mWithDraw.setVisibility(View.VISIBLE);
        if (deliveryData.getShipperRating() != null)
            shipperRating.setRating(Float.valueOf(deliveryData.getShipperRating()));
        shipperName.setText(deliveryData.getShipperName());
        //imgLoaderr.DisplayImage(deliveryData.getShipperImage(), shipperPic);
        if (deliveryData.getShipperImage() != null && !deliveryData.getShipperImage().contentEquals("") && !deliveryData.getShipperImage().contentEquals("null"))
            Picasso.with(getActivity()).load(deliveryData.getShipperImage()).into(target);
        // GlideCommon.ViewTarget viewTarget = new GlideCommon.ViewTarget(shipperPic);
        //  Glide.with(getActivity()).load(deliveryData.getShipperImage()).fitCenter().centerCrop().into(viewTarget);

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
        String userId = userInfo.getId();
        String driverid = deliveryData.getDriverID();
        String auctionstatusstr = auctionStatus.getText().toString();
        HomeActivity.edit.setVisibility(View.GONE);
        copy_delivery.setVisibility(View.GONE);
        lay_drop_off.setVisibility(View.VISIBLE);
        if (deliveryData.getDeliveryStatus().equals("1") || deliveryData.getDeliveryStatus().equals("2") || deliveryData.getDeliveryStatus().equals("3")) {
            call.setVisibility(View.VISIBLE);
            msg.setVisibility(View.VISIBLE);
        }
        if (deliveryData.getDeliveryStatus().equals("0"))
            auctionStatus.setVisibility(View.VISIBLE);
        if (deliveryData.getDeliveryStatus().equals("1")) {
            auctionStatus.setVisibility(View.VISIBLE);
            auctionStatus.setText("TRANSFER ASSIGNED");
            layAuction.setVisibility(View.GONE);
            lay_drop_off.setVisibility(View.VISIBLE);

        } else if (deliveryData.getDeliveryStatus().equals("2")) {
            auctionStatus.setVisibility(View.VISIBLE);
            auctionStatus.setText("TRANSFER IN-TRANSIT");
            layAuction.setVisibility(View.GONE);
            lay_drop_off.setVisibility(View.VISIBLE);

        } else if (deliveryData.getDeliveryStatus().equals("3")) {
            auctionStatus.setVisibility(View.VISIBLE);
            auctionStatus.setText("TRANSFER COMPLETED");
            layAuction.setVisibility(View.GONE);
            mExport.setVisibility(View.VISIBLE);
            bookmark.setVisibility(View.INVISIBLE);
            lay_drop_off.setVisibility(View.VISIBLE);

        } else if (deliveryData.getDeliveryStatus().equals("4")) {
            auctionStatus.setVisibility(View.VISIBLE);
            auctionStatus.setText("TRANSFER CANCELLED");
            lay_drop_off.setVisibility(View.VISIBLE);

        }

        Log.v("deliveryData", "deliveryData::" + deliveryData.getDeliveryStatus());
        if (deliveryData.getDeliveryStatus().equals("0") &&
                deliveryData.getBidStatus().equals("null")) {
            //if(isBidDateTimeValid(deliveryData.getAuctionStart(), deliveryData.getAuctionEnd())) {
            if (deliveryData.getAuctionBid().equals("2")) {
                submit.setText("BID NOW");
            } else if (deliveryData.getAuctionBid().equals("3")) {
                submit.setText("TAKE NOW");
            }
            submit.setVisibility(View.VISIBLE);
            submit.setOnClickListener(this);
            //}
        }
        // Added/Modified by VK
        /*else if (deliveryData.getDeliveryStatus().equals("1") &&
                deliveryData.getDriverID().equals(userInfo.getId())) {
            submit.setVisibility(View.VISIBLE);
            submit.setText("CONFIRM TRANSFER PICK UP");
            submit.setOnClickListener(this);
        }*/
        //VK end
        if (deliveryData.getDeliveryStatus().equals("0")) {
            //  puAddress.setVisibility(View.GONE);
            puMobile.setVisibility(View.GONE);
            //doAddress.setVisibility(View.GONE);
            //  doMobile.setVisibility(View.GONE);
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
            //     doMobile.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
            call.setVisibility(View.GONE);
            msg.setVisibility(View.GONE);
            HomeActivity.track_delivery.setVisibility(View.GONE);
            mWithDraw.setVisibility(View.GONE);

        } else
            puMobile.setVisibility(View.VISIBLE);
        if (auctionstatusstr != null && !auctionstatusstr.isEmpty()) {
            if (auctionstatusstr.contentEquals("AUCTION HAS ENDED")) {
                if (submit.getText().toString().equalsIgnoreCase("BID SUBMITTED")) {
                    submit.setVisibility(View.VISIBLE);
                } else
                    submit.setVisibility(View.GONE);
                //  HomeActivity.edit.setVisibility(View.GONE);
            }
        }
        // Added/Modified by VK
        if (deliveryData.getDeliveryStatus().equals("1")) {
            if (deliveryData.getDriverID().equals(userInfo.getId())) {
                submit.setVisibility(View.VISIBLE);
                submit.setText("CONFIRM TRANSFER PICK UP");
                submit.setOnClickListener(this);
                mWithDraw.setVisibility(View.VISIBLE);
            }
            if (deliveryData.getAllocateDriverID().equalsIgnoreCase("null")) {
                deliveryData.setAllocateDriverID("");
            }
            if (deliveryData.getIsAbleToAllocate() &&
                    deliveryData.getAllocateDriverID().equalsIgnoreCase("")) {
                allocateDriver.setVisibility(View.VISIBLE);
            } else if (deliveryData.getIsAbleToAllocate() &&
                    !deliveryData.getAllocateDriverID().equalsIgnoreCase("")) {
                allocateDriver.setVisibility(View.GONE);
                submit.setVisibility(View.GONE);
            }
            //allocated driver conditions
            if (deliveryData.getAllocateDriverID().equals(userInfo.getId())) {
                if (deliveryData.getAllocationStatus().equalsIgnoreCase("1")) {
                    submit.setVisibility(View.VISIBLE);
                    submit.setText("CONFIRM TRANSFER PICK UP");
                    submit.setOnClickListener(this);
                    mWithDraw.setVisibility(View.GONE);
                    call.setVisibility(View.VISIBLE);
                    msg.setVisibility(View.VISIBLE);
                    accept.setVisibility(View.GONE);
                    reject.setVisibility(View.GONE);
                    HomeActivity.track_delivery.setVisibility(View.VISIBLE);
                    puMobile.setVisibility(View.VISIBLE);
                } else if (deliveryData.getAllocationStatus().equalsIgnoreCase("2")) {

                } else {
                    accept.setVisibility(View.VISIBLE);
                    reject.setVisibility(View.VISIBLE);
                }
            }
        } else if (deliveryData.getDeliveryStatus().equals("2")) {
            if (deliveryData.getAllocateDriverID().equals(userInfo.getId())) {
                submit.setVisibility(View.VISIBLE);
                submit.setText("CONFIRM TRANSFER DROP OFF");
                submit.setOnClickListener(this);
                HomeActivity.track_delivery.setVisibility(View.VISIBLE);
                puMobile.setVisibility(View.VISIBLE);
            }
            if (deliveryData.getAllocateDriverID().equals("") || deliveryData.getAllocateDriverID().equals("null")) {
                if (deliveryData.getDriverID().equals(userInfo.getId())) {
                    submit.setVisibility(View.VISIBLE);
                    submit.setText("CONFIRM TRANSFER DROP OFF");
                    submit.setOnClickListener(this);
                }
            }
        } else if (deliveryData.getDeliveryStatus().equals("3")
                && !deliveryData.hasShipperFeedback() &&
                deliveryData.getDriverID().equals(userInfo.getId())) {
            submit.setVisibility(View.VISIBLE);
            submit.setText("RATE DISPATCHER");
            submit.setOnClickListener(this);
            // auctionStatus.setVisibility(View.VISIBLE);
            // auctionStatus.setText("Delivery has been completed");
        } else if (deliveryData.getDeliveryStatus().equals("4")) {
            //submit.setVisibility(View.VISIBLE);
            //submit.setText("BID SUBMITTED");
            //submit.setOnClickListener(null);
        }

        //VK end
    }

    public void showMessageDialog(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Success!");
        builder.setMessage(message);
        builder.setNegativeButton("IGNORE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("signaturetype", "1");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
        builder.setPositiveButton("SHOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getDelivery();
            }
        });
        builder.show();
    }

    public void rejectSuccess(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Success!");
        builder.setMessage(message);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("signaturetype", "1");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        builder.show();
    }

    public void CancelStatus(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Alert!");
        builder.setMessage(message);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("SearchDel", "1");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        builder.show();
    }

    private void showSuccessMessage(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Success!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (type.equals("driverPenality")) {
                    showGrabidDialog(getResources().getString(R.string.canceldrivernew), "");
                } else if (type.equals("shipperPenality")) {
                    showGrabidDialog(getResources().getString(R.string.cancelshippernew), "");
                } else if (type.equals("addfav")) {
                    getDelivery();
                } else if (type.equals("delete") || type.equals("add")) {

                } else if (type.equals("choose")) {
//                    getActivity().getFragmentManager().popBackStack();
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra("signaturetype", "2");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
                } else if (type.equals("change_status_intransiet") || type.equals("bid")) {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra("signaturetype", "1");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
                } else if (type.equals("change_status_complete")) {
                    //Modified by VK
                    if (deliveryData.getAllocateDriverID().equals(session.getUserDetails().getId())) {
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        intent.putExtra("signaturetype", "1");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity().finish();
                    } else // VK end
                        addFeedback(false);
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
       /* try {
            if (wakeLock != null)
                wakeLock.release();
            wakeLock = null;
        } catch (Exception e) {
            e.toString();
        }*/
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
//        String url = Config.SERVER_URL + Config.DELIVERIES + "/" + deliveryID;
        String url = Config.SERVER_URL + Config.JOBS + "/" + deliveryID;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void chooseBid(String ID) {
        type = "choose";
        // String url = Config.SERVER_URL + Config.CHOOSE_BID + ID;
        String url = Config.SERVER_URL + Config.CHOOSE_JOB_BID + ID;
        Log.d("URL", url);
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void cancelDelivery() {
        type = "cancel";
//        String url = Config.SERVER_URL + Config.DELIVERY + Config.CANCEL_DELIVERY + deliveryID;
        String url = Config.SERVER_URL + Config.JOBS + "/" + deliveryID;
        Log.d("url", url);
        HashMap<String, String> params = new HashMap<>();
        params.put("delivery_id", deliveryID);
        if (Internet.hasInternet(getActivity())) {
//            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.DELETE, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void cancelDeliveryUpcoming(String message) {
        type = "cancelupcoming";
//        String url = Config.SERVER_URL + Config.DELIVERY + Config.CANCEL_DELIVERYUPCOMING;
        String url = Config.SERVER_URL + Config.JOB + Config.JOB_CANCEL;
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
        HashMap<String, String> params = new HashMap<>();
        String url = "";
        Log.v("bidPrice", "bidPrice" + bidPrice);
        if (bidPrice.equals("")) {
            type = "take_now";
            url = Config.SERVER_URL + Config.JOB + "/" + Config.TAKE_NOW + "/?id=" + deliveryID;
        } else {
            type = "bid";
//            url = Config.SERVER_URL + Config.BIDS + "/" + deliveryID;
            url = Config.SERVER_URL + Config.BID_ON_JOB;
            Log.d("url", url);
            params.put("bid_price", bidPrice);
            params.put("delivery_id", deliveryID);
        }
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI;
            if (bidPrice.equals(""))
                mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            else
//                mobileAPI = new RestAPICall(getActivity(), HTTPMethods.PUT, this, params);
                mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
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
                if (type == 8)
                    showGrabidDialog(getActivity().getResources().getString(R.string.enterreasonfortransferrejection), "reject");
                else if (type == 4) {
                    getPanalty("driverPenality");
                } else if (type == 3) {
                    getPanalty("shipperPenality");
                } else if (type == 0)
                    cancelDelivery();
                else {
                    if (deliveryData.getAuctionBid().equals("3")) {
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

    private void getPanalty(String type) {
        this.type = type;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(Config.GETPANALITY + deliveryID, session.getToken());
        }

    }

    private void showAcceptDialog(final String id) {
        if (checktimings(true)) {
            showAuctionNotStart(getActivity().getResources().getString(R.string.cannotchoosedriver));
            return;
        } else if (userInfo.getBankDetail() == null)
            AlertManager.BankmessageDialog((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savebankdetailsaccept), this.getClass().getName(), "3");
        else if (userInfo.getBankDetail().contentEquals("null"))
            AlertManager.BankmessageDialog((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savebankdetailsaccept), this.getClass().getName(), "3");
        else if (userInfo.getCreditCard() == null)
            AlertManager.BankmessageDialog((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savecreditdetailsaccept), this.getClass().getName(), "2");
        else if (userInfo.getCreditCard().contentEquals("null"))
            AlertManager.BankmessageDialog((HomeActivity) getActivity(), "Alert!", getResources().getString(R.string.savecreditdetailsaccept), this.getClass().getName(), "2");
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Alert!");
            builder.setMessage(getActivity().getResources().getString(R.string.acceptdriverbidfortransfer));
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

    private void showConfirmDialog(String msg, final boolean Intransiet) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert!");
        builder.setMessage(msg);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                //showSignature();
                // showImages();
                changeJobStatus(Intransiet);
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

        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
                    // Here Set your Event Date
                    Date eventDate = dateFormat.parse(auctionDateTime);
                    Date currentDate = currentDatee;
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
                                //Delivery editable while auction is live
                                try {
                                    if (deliveryData != null && userInfo != null) {
                                        if (deliveryData.getUserID().equals(userInfo.getId()))
                                            HomeActivity.edit.setVisibility(View.GONE);
                                    }
                                } catch (Exception e) {
                                    e.toString();
                                }
                            }
                            if (deliveryData.getBidStatus().equals("null")) {
                                submit.setVisibility(View.VISIBLE);
                            } else {
                                submit.setVisibility(View.VISIBLE);
                                submit.setText("BID SUBMITTED");
                                //  submit.setOnClickListener(null);
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
        params.put("delivery_id", deliveryID);
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI;
            mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void changeJobStatus(boolean Intransiet) {
        String url = "";
        if (Intransiet) {
            type = "change_status_intransiet";
            url = Config.SERVER_URL + Config.JOB + Config.DELIVERY_STATUS_IN_TRANSIT;
        } else {
            type = "change_status_complete";
            url = Config.SERVER_URL + Config.JOB + Config.DELIVERY_STATUS_IN_COMPLETE;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(Keys.DELIVERY_ID, deliveryID);
        //  String url = Config.SERVER_URL + Config.NOTIFICATION + Config.READ_STATUS + "?id=" + session.getNotificationID();
        Log.d("url", url);
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

    public void acceptAllocationPrompt(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                acceptAllocation();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog d = builder.create();
        d.show();
    }
}
package com.grabid.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import com.grabid.models.Delivery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.grabid.R.color.top_bar_title_color;
import static com.grabid.common.AlertManager.messageDialog;

/**
 * Created by vinod on 10/14/2016.
 */
public class SubmitStepOne extends Fragment implements View.OnClickListener,
        AsyncTaskCompleteListener {
    String deliveryID = "";
    TextView JobType;
    LinearLayout layTotalqty;
    EditText totalQty;
    TextView done;
    SessionManager session;
    Delivery delivery = null;
    JSONArray jsonArray;
    RadioGroup mRadioAsap, mRadioIsRadioTrip;
    boolean IsCopy = false;
    boolean IsAppendData = false;
    int type;
    boolean IsNotification = false;
    String mRadioASAStr = "", mRadioTripStr = "", JobTypeStr = "";
    RadioButton mAsapYes, mAsapNo, mRoundTripYes, mRoundTripNo;
    public static Boolean mCreditCardDetail = true;
    LinearLayout mPay_Mode;
    String mPayment_mode = "";
    CheckBox mCheckcash, mCheckCredit, mCheckCab;
    boolean isBack = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.title.setText(getResources().getString(R.string.submitjobs));
        if (getArguments() != null && getArguments().containsKey("isBack"))
            isBack = getArguments().getBoolean("isBack");
        View view = inflater.inflate(R.layout.shipment, null);
        init(view);
        getDuration();
        return view;
    }

    private void changeNotificationStatus() {
        type = 14;
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

    private void getDelivery(String deliveryID) {
        type = 9;
        // String url = Config.SERVER_URL + Config.DELIVERIES + "/" + deliveryID;
        String url = Config.SERVER_URL + Config.JOBS + "/" + deliveryID;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void getDuration() {
        type = 5;
        String url = Config.SERVER_URL + Config.DURATION;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    public void UpdateDesign() {
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.title.setText(getResources().getString(R.string.submitjobs));
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.seagreen));
        HomeActivity.title.setTextColor(getResources().getColor(top_bar_title_color));
        if (IsCopy) {
            try {
                int backCount = getActivity().getFragmentManager().getBackStackEntryCount();
                if (isBack)
                    HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
                else
                    HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_white);

            } catch (Exception e) {
                e.toString();
                HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
            }
        } else
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_white);
    }

    @Override
    public void onStart() {
        super.onStart();
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.seagreen));
        HomeActivity.title.setTextColor(getResources().getColor(top_bar_title_color));
        if (IsCopy) {
            try {
                int backCount = getActivity().getFragmentManager().getBackStackEntryCount();
                if (isBack)
                    HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
                else
                    HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_white);
            } catch (Exception e) {
                e.toString();
                HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
            }
        } else
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_white);

    }

    @Override
    public void onStop() {
        super.onStop();
        //    HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        //  HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        //HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
    }

    String itemData = "";
    JSONArray jsonArrayData = null;


    private void appendData() {
        HashMap<String, Delivery> map = (HashMap<String, Delivery>) getArguments().getSerializable("data");
        Delivery delivery = map.get("data");
        try {
            if (getArguments().containsKey("incoming_type")) {
                String incomingType = getArguments().getString("incoming_type");
                if (incomingType != null && (incomingType.contentEquals("notificationshipment")))
                    session.saveCurrentScreen("editRelisting");
            }
        } catch (Exception e) {
            e.toString();
        }
        this.delivery = delivery;
        if (delivery.getItemtype().equals("1")) {
            JobType.setText("Offload Transfer");
            JobTypeStr = "1";
            mPay_Mode.setVisibility(View.GONE);
        } else {
            JobType.setText("Collect Transfer");
            JobTypeStr = "2";
            mPay_Mode.setVisibility(View.VISIBLE);
        }
        if (delivery.isAsap())
            mAsapYes.setChecked(true);
        else
            mAsapNo.setChecked(true);

        if (delivery.isRoundTrip())
            mRoundTripYes.setChecked(true);
        else
            mRoundTripNo.setChecked(true);
        totalQty.setText(delivery.getQty());
        if (IsNotification) {
            if (session.getReadStatus().contentEquals("0"))
                changeNotificationStatus();
        }
        if (delivery.getItemtype().equals("2")) {
            if (delivery.getPayment_mode() != null) {
                if (delivery.getPayment_mode().contains("1"))
                    mCheckcash.setChecked(true);
                if (delivery.getPayment_mode().contains("2"))
                    mCheckCredit.setChecked(true);
                if (delivery.getPayment_mode().contains("3"))
                    mCheckCab.setChecked(true);
            }
        }


    }

    private void appendEditData() {
        session.saveCurrentScreen("editRelisting");
        Delivery delivery = this.delivery;
        if (delivery.getItemtype().equals("1")) {
            JobType.setText("Offload Transfer");
            JobTypeStr = "1";
        } else {
            JobType.setText("Collect Transfer");
            JobTypeStr = "2";
        }
        if (delivery.isAsap())
            mAsapYes.setChecked(true);
        else
            mAsapNo.setChecked(true);

        if (delivery.isRoundTrip())
            mRoundTripYes.setChecked(true);
        else
            mRoundTripNo.setChecked(true);
        totalQty.setText(delivery.getQty());

    }

    private void init(View view) {
        session = new SessionManager(getActivity());
        if (getArguments().containsKey("delivery_id") || (getArguments().containsKey("data")))
            IsCopy = true;
        JobType = (TextView) view.findViewById(R.id.d_type);
        JobType.setOnClickListener(this);
        layTotalqty = (LinearLayout) view.findViewById(R.id.lay_totalqty);
        totalQty = (EditText) view.findViewById(R.id.qty);
        done = (TextView) view.findViewById(R.id.qty_done);
        done.setOnClickListener(this);
        mRadioAsap = (RadioGroup) view.findViewById(R.id.radio);
        mRadioIsRadioTrip = (RadioGroup) view.findViewById(R.id.radio1);
        mAsapYes = (RadioButton) view.findViewById(R.id.radioyes);
        mAsapNo = (RadioButton) view.findViewById(R.id.radiono);
        mRoundTripYes = (RadioButton) view.findViewById(R.id.radioyes1);
        mRoundTripNo = (RadioButton) view.findViewById(R.id.radiono1);
        mPay_Mode = (LinearLayout) view.findViewById(R.id.pay_mode);
        mCheckcash = (CheckBox) view.findViewById(R.id.radiocash);
        mCheckCredit = (CheckBox) view.findViewById(R.id.radiocredicard);
        mCheckCab = (CheckBox) view.findViewById(R.id.radiocabcharge);
        totalQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    totalQty.clearFocus();
                }
                return false;
            }
        });
        mRadioAsap.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.radioyes:
                        mRadioASAStr = "1";
                        break;
                    case R.id.radiono:
                        mRadioASAStr = "2";
                        break;
                }
            }
        });
        mRadioIsRadioTrip.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.radioyes1:
                        mRadioTripStr = "1";
                        break;
                    case R.id.radiono1:
                        mRadioTripStr = "2";
                        break;
                }
            }
        });
    }

    public String[] getJobTpes() {
        return getResources().getStringArray(R.array.jobypes);
    }

    public void showGrabidDialog(final int type) {
        final Dialog mDialog = new Dialog(getActivity(), R.style.GrabidDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list_new);
        mDialog.setCanceledOnTouchOutside(true);
        final TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        ImageView close = (ImageView) mDialog.findViewById(R.id.close);
        if (type == 1)
            title.setText("Please Select");
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog.isShowing())
                    mDialog.dismiss();
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        final ListView dialog_ListView = (ListView) mDialog.findViewById(R.id.list);
        ArrayAdapter<String> adapter = null;
        if (type == 1)
            adapter = new ArrayAdapter<>(getActivity(),
                    R.layout.dialog_textview, R.id.textItem, getJobTpes());
        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (type == 1) {
                    JobType.setText(parent.getItemAtPosition(position).toString());
                    if (parent.getItemAtPosition(position).toString().contentEquals("Offload Transfer")) {
                        JobTypeStr = "1";
                        mPay_Mode.setVisibility(View.GONE);
                    } else {
                        JobTypeStr = "2";
                        mPay_Mode.setVisibility(View.VISIBLE);
                    }
                    jsonArray = new JSONArray();
                    layTotalqty.setVisibility(View.VISIBLE);

                        /*resetFields();*/

                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    @Override
    public void onTaskComplete(String result) {
        Log.d(Config.TAG, result);
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outterJson = new JSONObject(result);
            if (type == 5) {
                JSONObject dataObj = outterJson.getJSONObject(Config.DATA);
                mCreditCardDetail = dataObj.optBoolean(Keys.CREDITCARDDETAIL);

                if (getArguments().containsKey("delivery_id")) {
                    IsCopy = true;
                    try {
                        if (getArguments().containsKey("incoming_type")) {
                            String incomingType = getArguments().getString("incoming_type");
                            if (incomingType != null && incomingType.contentEquals("notificationshipment")) {
                                IsNotification = true;
                                appendData();


                            } else {
//                        String delivery_id = getArguments().getString("delivery_id");
                                getDelivery(getArguments().getString("delivery_id"));
                            }
                        } else {
                            getDelivery(getArguments().getString("delivery_id"));
                        }
                    } catch (Exception e) {

                    }
                    // appendData();
                    // IsCopy = true;
                } else if (getArguments().containsKey("data")) {
                    appendData();
                    IsCopy = true;
                }
                if (!mCreditCardDetail)
                    BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", outterJson.getString(Config.MESSAGE), this.getClass().getName(), "5");
            } else {
                if (Integer.parseInt(outterJson.getString(Config.STATUS)) == APIStatus.SUCCESS) {

                    if (type == 9) {
                        delivery = new Delivery();
                        try {
                            JSONObject dataObj = outterJson.getJSONObject(Config.DATA);
                            JSONObject deliveryObj = dataObj.getJSONObject(Config.JOB);
                            Delivery delivery = new Delivery();
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
                            delivery.setDoCall(deliveryObj.getString(Keys.DROPOFF_CALL));
                            delivery.setUser_Group(deliveryObj.getString(Keys.USER_GROUP));
                            delivery.setPayment_mode(deliveryObj.getString(Keys.PAYMENT_MODE));
                            delivery.setFromPickUpAt(deliveryObj.getString("from_pick_up_at"));
                            delivery.setCompleted_at(deliveryObj.getString("completed_at"));
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
                            delivery.setSuitableVehicles(deliveryObj.get(Keys.SUITABLE_VEHICLE_TEXT).toString());
                            delivery.setPickupSpecialRestriction(deliveryObj.getString(Keys.PICKUP_SPECIAL_RESTRICTION));
                            delivery.setSpecialPermitDetail(deliveryObj.getString(Keys.SPECIAL_PERMIT_DETAIL));
                            delivery.setSuitabelVehicle(deliveryObj.getString(Keys.SUITABLE_VEHICAL_IDS));
                            delivery.setFromPickUpAt(deliveryObj.getString("from_pick_up_at"));
                            delivery.setCompleted_at(deliveryObj.getString("completed_at"));
                            delivery.setCompanyLogo(deliveryObj.getString(Keys.COMPANY_LOGO));
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
                            this.delivery = delivery;
                            appendEditData();
                        } catch (Exception e) {
                        }
                    }
                    if (type == 14) {
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
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void BankmessageDialogAdd(final Activity ctx, String title, final String message, final String backstackname, final String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("UPDATE CARD", new DialogInterface.OnClickListener() {
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


    int currentPos = 0;

    public void setmPay_Mode() {
        mPayment_mode = "";
        if (mCheckcash.isChecked())
            mPayment_mode = "1";
        if (mCheckCredit.isChecked()) {
            if (!mPayment_mode.contentEquals(""))
                mPayment_mode = mPayment_mode + ",2";
            else
                mPayment_mode = "2";
        }
        if (mCheckCab.isChecked()) {
            if (!mPayment_mode.contentEquals(""))
                mPayment_mode = mPayment_mode + ",3";
            else
                mPayment_mode = "3";
        }
    }

    public boolean isValid() {
        setmPay_Mode();
        String text = JobType.getText().toString();
        if (JobType.getText().toString().contentEquals("") && mRadioASAStr.contentEquals("")
                && totalQty.getText().toString().trim().length() == 0 && mRadioTripStr.contentEquals("")) {
            showMessage(getActivity().getResources().getString(R.string.completeallfield));
            return false;
        }
        if (JobType.getText().toString().contentEquals("")) {
            showMessage(getActivity().getResources().getString(R.string.selecttransfertype));
            return false;
        } else if (mRadioASAStr.contentEquals("")) {
            showMessage(getActivity().getResources().getString(R.string.isthisasap));
            return false;
        } else if (totalQty.getText().toString().trim().length() == 0) {
            showMessage(getActivity().getResources().getString(R.string.totalpassenger));
            return false;
        } else if (mRadioTripStr.contentEquals("")) {
            showMessage(getActivity().getResources().getString(R.string.isthisroundtrip));
            return false;
        }
        int length = totalQty.getText().length();
        if (length != 0 && !checkZero(totalQty.getText().toString())) {
            showMessage(getActivity().getResources().getString(R.string.passengergreaterthenzero));
            return false;
        }
        if (JobTypeStr.contentEquals("2"))
            if (mPayment_mode.contentEquals("")) {
                showMessage(getActivity().getResources().getString(R.string.selectpaymentmode));
                return false;
            }

        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.qty_done:
                if (isValid()) {
                    session.saveJob(JobTypeStr, mRadioASAStr, mRadioTripStr, totalQty.getText().toString(), mPayment_mode);
                    String backStateName = this.getClass().getName();
                    Fragment fragment = new SubmitStepTwo();
                    Bundle bundle = new Bundle();
                    if (delivery != null) {
                        HashMap<String, Delivery> data = new HashMap<String, Delivery>();
                        data.put("data", delivery);
                        bundle.putSerializable("data", data);
                    }
                    fragment.setArguments(bundle);
                    getActivity().getFragmentManager().beginTransaction().add(R.id.container, fragment, backStateName)
                            .addToBackStack(null)
                            //.commit();
                            .commitAllowingStateLoss();
                }
                break;
            case R.id.d_type:
                showGrabidDialog(1);
                break;
        }
    }

    private void showMessage(String message) {
        messageDialog(getActivity(), "Alert!", message);
    }

    public Boolean checkZero(String val) {
        if (val.matches("[0]+"))
            return false;


        return true;
    }
}
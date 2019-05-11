package com.grabid.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.imageCrop.Constants;
import com.grabid.imageCrop.ImageCropActivity;
import com.grabid.imageCrop.PicModeSelectDialogFragment;
import com.grabid.models.Delivery;
import com.grabid.models.PreviewField;
import com.grabid.util.StorePath;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.grabid.R.id.lay_img;
import static com.grabid.common.AlertManager.messageDialog;

/**
 * Created by vinod on 10/14/2016.
 */
public class SubmitStepOne extends Fragment implements View.OnClickListener,
        AsyncTaskCompleteListener, PicModeSelectDialogFragment.IPicModeSelectListener {
    String deliveryID = "", deliverySubID, deliverySubSubID = "";
    TextView delivry, deliveryType, stackable, hazardous, animalType, animalCarrier, currentVaccination, mAnimalCarrierTitle, mItemDetailTxt;
    LinearLayout layDeliverySub, layNote, layAnimalNote, layAnimalType, layQty,
            layPhoto, layMore, layAnimal, layHWLW, layStackHaz, layTotalqty;
    EditText totalQty, qty, weight, length, width, height, more, breed, animalWeight, hazadours_good_value;
    //EditText animalName;
    TextView submit, cancel, done, notes, previous, next, addMore, saveItem, qtyDone, back, same, hazadours_good_title;
    ImageView pic;
    SessionManager session;
    CropImageView cropImageView;
    RelativeLayout layImg;
    RelativeLayout cropLay;
    ScrollView scroll;
    ImageView img, edit_img;
    Delivery delivery = null;
    JSONArray jsonArray;
    LinearLayout topLayout, btmLayout;
    TextView dot1, dot2;
    boolean IsCopy = false;
    boolean IsAppendData = false;
    int type;
    boolean IsNotification = false;
    public static Boolean mCreditCardDetail = true;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //
        View view = inflater.inflate(R.layout.shipment, null);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.seagreen));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.text_color_white));
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.title.setText(getResources().getString(R.string.submitdelivery));
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_white);
        //
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
        String url = Config.SERVER_URL + Config.DELIVERIES + "/" + deliveryID;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void getDuration() {
        type = 10;
        String url = Config.SERVER_URL + Config.DURATION;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    public void UpdateDesign() {
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.seagreen));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.text_color_white));
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.title.setText(getResources().getString(R.string.submitdelivery));
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_white);
        if (getArguments().containsKey("delivery_id") || getArguments().containsKey("data"))
            IsCopy = true;
        if (IsCopy) {
            try {
                int backCount = getActivity().getFragmentManager().getBackStackEntryCount();
                if (backCount == 1)
                    HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_white);
                else
                    HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
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
        HomeActivity.title.setTextColor(getResources().getColor(R.color.text_color_white));
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_white);
        if (IsCopy) {
            try {
                int backCount = getActivity().getFragmentManager().getBackStackEntryCount();
                if (backCount == 1)
                    HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_white);
                else
                    HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_white);
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
        //   HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        //HomeActivity.title.setTextColor(getResources().getColor(R.color.text_color_white));
        // HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
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
        Log.d("Edit", delivery.getDeliveryTypeID());
        Log.d("Edit", delivery.getDeliveryTypeSubID());
        Log.d("Edit", delivery.getQty());
        Log.d("getItemsData", delivery.getItemsData());
        this.delivery = delivery;
        jsonArray = new JSONArray();
        //updateUI();
        layDeliverySub.setVisibility(View.VISIBLE);
        itemData = delivery.getItemsData();
        //dropdown 1
        deliveryID = delivery.getDeliveryTypeID();
        delivry.setText(delivery.getDeliveryName());
        topLayout.setVisibility(View.VISIBLE);
        //doGetStates(deliveryID, 2);
        deliverySubID = delivery.getDeliveryTypeSubID();   //dropdown 2
        deliveryType.setText(delivery.getDeliverySubName());
        if (deliveryID.equals("4")) {
            IsAppendData = true;
            deliverySubSubID = delivery.getDeliveryTypeSubSubID();
            //  doGetStates(deliverySubID, 3);
        } else {
            layTotalqty.setVisibility(View.VISIBLE);
            //resetFields();
        }
        //working
        if (deliveryID != null && deliveryID.equals("1")) {
            if (deliverySubID != null && (deliverySubID.equals("18"))) {
                layNote.setVisibility(View.VISIBLE);
                notes.setText(delivery.getItem_delivery_other());
            }

        } else if (deliveryID != null && deliveryID.equals("2")) {
            if (deliverySubID != null && (deliverySubID.equals("10"))) {
                layNote.setVisibility(View.VISIBLE);
                notes.setText(delivery.getItem_delivery_other());
            }

        } else if (deliveryID != null && deliveryID.equals("3")) {
            if (deliverySubID != null && (deliverySubID.equals("32"))) {
                layNote.setVisibility(View.VISIBLE);
                notes.setText(delivery.getItem_delivery_other());
            }

        } else if (deliveryID != null && deliveryID.equals("4")) {
            if (deliverySubID != null && (deliverySubID.equals("33"))) {
                layNote.setVisibility(View.VISIBLE);
                notes.setText(delivery.getItem_delivery_other());
                layAnimalType.setVisibility(View.GONE);
            } else {
                if (deliverySubSubID.equals("38") || deliverySubSubID.equals("43")) {
                    layAnimalType.setVisibility(View.VISIBLE);
                    layNote.setVisibility(View.VISIBLE);
                    notes.setText(delivery.getItem_delivery_other());
                } else {
                    layNote.setVisibility(View.GONE);
                    layAnimalType.setVisibility(View.VISIBLE);
                }
            }
            if (deliverySubID.equals("16")) {
                animalCarrier.setVisibility(View.VISIBLE);
                mAnimalCarrierTitle.setVisibility(View.VISIBLE);
            } else {
                animalCarrier.setVisibility(View.GONE);
                mAnimalCarrierTitle.setVisibility(View.GONE);
            }
        }
        //working
        //String animalName = delivery.getAnimalName();
        //dropdown 3
        //  deliverySubSubID = getDeliverySubSubId(delivery.getDeliveryTypeSubSubID());
        /*if (deliveryID.equals("4")) {
            //   String name = getDeliverySubSubName(delivery.getDeliveryTypeSubSubID());
            animalType.setText("");
            if (animalType.getText().toString().equalsIgnoreCase("other")) {
                layAnimalType.setVisibility(View.GONE);
                layAnimalNote.setVisibility(View.VISIBLE);
            } else {
                layAnimalNote.setVisibility(View.GONE);
                layAnimalType.setVisibility(View.VISIBLE);
            }
        }*/
        layTotalqty.setVisibility(View.VISIBLE);
        totalQty.setText(delivery.getQty());

        try {
            jsonArrayData = (JSONArray) new JSONTokener(itemData).nextValue();
            Log.v("jsonArr", "jsonArr" + jsonArrayData);
        } catch (Exception ex) {
            Log.v("error:", "error:" + ex.getMessage());
        }
    }

    private void appendEditData() {
        // HashMap<String, Delivery> map = (HashMap<String, Delivery>) getArguments().getSerializable("data");
        //this.delivery = delivery;

        session.saveCurrentScreen("editRelisting");
        Delivery delivery = this.delivery;
        Log.d("Edit", delivery.getDeliveryTypeID());
        Log.d("Edit", delivery.getDeliveryTypeSubID());
        Log.d("Edit", delivery.getQty());
        Log.d("getItemsData", delivery.getItemsData());
        jsonArray = new JSONArray();
        //updateUI();
        layDeliverySub.setVisibility(View.VISIBLE);
        itemData = delivery.getItemsData();
        //dropdown 1
        deliveryID = delivery.getDeliveryTypeID();
        delivry.setText(delivery.getDeliveryName());
        topLayout.setVisibility(View.VISIBLE);
        //doGetStates(deliveryID, 2);
        deliverySubID = delivery.getDeliveryTypeSubID();   //dropdown 2
        deliveryType.setText(delivery.getDeliverySubName());
        if (deliveryID.equals("4")) {
            IsAppendData = true;
            deliverySubSubID = delivery.getDeliveryTypeSubSubID();
            //  doGetStates(deliverySubID, 3);
        } else {
            layTotalqty.setVisibility(View.VISIBLE);
            //resetFields();
        }
        if (deliveryID != null && deliveryID.equals("1")) {
            if (deliverySubID != null && (deliverySubID.equals("18"))) {
                layNote.setVisibility(View.VISIBLE);
                notes.setText(delivery.getItem_delivery_other());
            }

        } else if (deliveryID != null && deliveryID.equals("2")) {
            if (deliverySubID != null && (deliverySubID.equals("10"))) {
                layNote.setVisibility(View.VISIBLE);
                notes.setText(delivery.getItem_delivery_other());
            }

        } else if (deliveryID != null && deliveryID.equals("3")) {
            if (deliverySubID != null && (deliverySubID.equals("32"))) {
                layNote.setVisibility(View.VISIBLE);
                notes.setText(delivery.getItem_delivery_other());
            }

        } else if (deliveryID != null && deliveryID.equals("4")) {
            if (deliverySubID != null && (deliverySubID.equals("33"))) {
                layNote.setVisibility(View.VISIBLE);
                notes.setText(delivery.getItem_delivery_other());
                layAnimalType.setVisibility(View.GONE);
            } else {
                if (deliverySubSubID.equals("38") || deliverySubSubID.equals("43")) {
                    layAnimalType.setVisibility(View.VISIBLE);
                    layNote.setVisibility(View.VISIBLE);
                    notes.setText(delivery.getItem_delivery_other());
                } else {
                    layNote.setVisibility(View.GONE);
                    layAnimalType.setVisibility(View.VISIBLE);
                }
            }
            if (deliverySubID.equals("16")) {
                animalCarrier.setVisibility(View.VISIBLE);
                mAnimalCarrierTitle.setVisibility(View.VISIBLE);
            } else {
                animalCarrier.setVisibility(View.GONE);
                mAnimalCarrierTitle.setVisibility(View.GONE);
            }
        }
        //  String animalName = delivery.getAnimalName();
        //dropdown 3
        //  deliverySubSubID = getDeliverySubSubId(delivery.getDeliveryTypeSubSubID());
        /*if (deliveryID.equals("4")) {
            //   String name = getDeliverySubSubName(delivery.getDeliveryTypeSubSubID());
            animalType.setText("");
            if (animalType.getText().toString().equalsIgnoreCase("other")) {
                layAnimalType.setVisibility(View.GONE);
                layAnimalNote.setVisibility(View.VISIBLE);
            } else {
                layAnimalNote.setVisibility(View.GONE);
                layAnimalType.setVisibility(View.VISIBLE);
            }
        }*/
        layTotalqty.setVisibility(View.VISIBLE);
        totalQty.setText(delivery.getQty());

        try {
            jsonArrayData = (JSONArray) new JSONTokener(itemData).nextValue();
            Log.v("jsonArr", "jsonArr" + jsonArrayData);
        } catch (Exception ex) {
            Log.v("error:", "error:" + ex.getMessage());
        }

    }

    private void init(View view) {
        session = new SessionManager(getActivity());
        delivry = (TextView) view.findViewById(R.id.delivery);
        delivry.setOnClickListener(this);
        deliveryType = (TextView) view.findViewById(R.id.d_type);
        deliveryType.setOnClickListener(this);
        stackable = (TextView) view.findViewById(R.id.stackable);
        stackable.setOnClickListener(this);
        hazardous = (TextView) view.findViewById(R.id.hazardous);
        hazardous.setOnClickListener(this);
        layDeliverySub = (LinearLayout) view.findViewById(R.id.lay_deliverysub);
        layQty = (LinearLayout) view.findViewById(R.id.lay_qty);
        layNote = (LinearLayout) view.findViewById(R.id.lay_note);
        layAnimalNote = (LinearLayout) view.findViewById(R.id.lay_animal_note);
        layAnimalType = (LinearLayout) view.findViewById(R.id.lay_animal_type);
        layPhoto = (LinearLayout) view.findViewById(R.id.lay_photo);
        layMore = (LinearLayout) view.findViewById(R.id.lay_more);
        more = (EditText) view.findViewById(R.id.more);
        more.setImeOptions(EditorInfo.IME_ACTION_DONE);
        more.setRawInputType(InputType.TYPE_CLASS_TEXT);
        layAnimal = (LinearLayout) view.findViewById(R.id.lay_animal);
        layHWLW = (LinearLayout) view.findViewById(R.id.lay_hwlw);
        layStackHaz = (LinearLayout) view.findViewById(R.id.lay_haz_stack);
        layTotalqty = (LinearLayout) view.findViewById(R.id.lay_totalqty);
        dot1 = (TextView) view.findViewById(R.id.dot1);
        dot2 = (TextView) view.findViewById(R.id.dot2);
        totalQty = (EditText) view.findViewById(R.id.qty);
        qtyDone = (TextView) view.findViewById(R.id.qty_done);
        qtyDone.setOnClickListener(this);
        qty = (EditText) view.findViewById(R.id.item_qty);
        weight = (EditText) view.findViewById(R.id.weight);
        length = (EditText) view.findViewById(R.id.length);
        width = (EditText) view.findViewById(R.id.width);
        height = (EditText) view.findViewById(R.id.height);
        pic = (ImageView) view.findViewById(R.id.image);
        submit = (TextView) view.findViewById(R.id.submit);
        submit.setOnClickListener(this);
        pic.setOnClickListener(this);
        layImg = (RelativeLayout) view.findViewById(lay_img);
        img = (ImageView) view.findViewById(R.id.img);
        edit_img = (ImageView) view.findViewById(R.id.edit_img);
        cropLay = (RelativeLayout) view.findViewById(R.id.crop_lay);
        scroll = (ScrollView) view.findViewById(R.id.scrollView);
        cropImageView = (CropImageView) view.findViewById(R.id.CropImageView);
        cancel = (TextView) view.findViewById(R.id.cancel);
        done = (TextView) view.findViewById(R.id.done);
        previous = (TextView) view.findViewById(R.id.previous);
        next = (TextView) view.findViewById(R.id.next);
        same = (TextView) view.findViewById(R.id.same);
        same.setOnClickListener(this);
        // addMore = (TextView) view.findViewById(R.id.add_more);
        saveItem = (TextView) view.findViewById(R.id.save);
        cancel.setOnClickListener(this);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        //addMore.setOnClickListener(this);
        saveItem.setOnClickListener(this);
        done.setOnClickListener(this);
        edit_img.setOnClickListener(this);
        animalType = (TextView) view.findViewById(R.id.animal_type);
        animalType.setOnClickListener(this);
        animalCarrier = (TextView) view.findViewById(R.id.animal_carrier);
        animalCarrier.setOnClickListener(this);
        currentVaccination = (TextView) view.findViewById(R.id.current_vaccination);
        currentVaccination.setOnClickListener(this);
        // animalName = (EditText) view.findViewById(R.id.name);
        breed = (EditText) view.findViewById(R.id.animal_breed);
        animalWeight = (EditText) view.findViewById(R.id.animal_weight);
        notes = (EditText) view.findViewById(R.id.notes);
        topLayout = (LinearLayout) view.findViewById(R.id.toplayout);
        btmLayout = (LinearLayout) view.findViewById(R.id.btmlayout);
        back = (TextView) view.findViewById(R.id.back);
        back.setOnClickListener(this);
        mAnimalCarrierTitle = (TextView) view.findViewById(R.id.animalcarriertitle);
        mItemDetailTxt = (TextView) view.findViewById(R.id.itemdetailtxt);
        hazadours_good_title = (TextView) view.findViewById(R.id.hazardous_good_title);
        hazadours_good_value = (EditText) view.findViewById(R.id.hazardous_good_value);
        more.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });

      /*  totalQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        // code to execute when EditText loses focus
                        topLayout.setVisibility(View.GONE);
                        btmLayout.setVisibility(View.VISIBLE);
                        updateUI();
                        updateSubUI();
                        saveItem.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.toString();
                    }
                    //updateForms();
                }
            }
        });*/
        /*qty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String qtyStatus = qtyStatus();
                    Log.v("qtyStatus","qtyStatus:"+qtyStatus);
                    if(qtyStatus.equals("less")){
                        saveItem.setVisibility(View.VISIBLE);
                        submit.setVisibility(View.GONE);
                    } else if(qtyStatus.equals("greater")){
                        showMessage("Items quantity must equal to total quantity.");
                    } else if(qtyStatus.equals("equal")){
                        //saveItem.setText("Update Item");
                        //saveItem.setVisibility(View.VISIBLE);
                        saveItem.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);
                    }
                }
            }
        });*/
        /*weight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });*/

        totalQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    totalQty.clearFocus();
                }
                return false;
            }
        });
    }

    public void showGrabidDialog(final int type) {
        final Dialog mDialog = new Dialog(getActivity(), R.style.GrabidDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list_new);
        mDialog.setCanceledOnTouchOutside(false);
        final TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        ImageView close = (ImageView) mDialog.findViewById(R.id.close);
        if (type == 1)
            title.setText("Please Select");
        else if (type == 2)
            title.setText("Please Select");
        else if (type == 3)
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
                    R.layout.dialog_textview, R.id.textItem, getDeliveryList());
        else if (type == 2)
            adapter = new ArrayAdapter<>(getActivity(),
                    R.layout.dialog_textview, R.id.textItem, getDeliverySubList());
        else if (type == 3)
            adapter = new ArrayAdapter<>(getActivity(),
                    R.layout.dialog_textview, R.id.textItem, getAnimalTyes());

        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (type == 1) {
                    deliveryID = "";
                    deliverySubID = "";
                    deliverySubSubID = "";
                    delivry.setText(parent.getItemAtPosition(position).toString());
                    hideAll();
                    jsonArray = new JSONArray();
                    deliveryID = getDeliveryId(parent.getItemAtPosition(position).toString());
                    layDeliverySub.setVisibility(View.VISIBLE);
                    topLayout.setVisibility(View.VISIBLE);
                    doGetStates(deliveryID, 2);
                } else if (type == 2) {
                    deliverySubID = "";
                    deliverySubSubID = "";
                    animalType.setText("");
                    deliveryType.setText(parent.getItemAtPosition(position).toString());
                    deliverySubID = getDeliverySubId(parent.getItemAtPosition(position).toString());
                    jsonArray = new JSONArray();
                    Log.v("deliveryID:", "deliveryID:" + deliveryID);
                    if (!deliverySubID.equals("33") && deliveryID.equals("4")) {
                        doGetStates(deliverySubID, 3);
                    } else {
                        layTotalqty.setVisibility(View.VISIBLE);


                        /*resetFields();*/
                    }


                    if (deliveryID != null && deliveryID.equals("1")) {
                        if (deliverySubID != null && (deliverySubID.equals("18")))
                            layNote.setVisibility(View.VISIBLE);
                        else
                            layNote.setVisibility(View.GONE);
                    } else if (deliveryID != null && deliveryID.equals("2")) {
                        if (deliverySubID != null && (deliverySubID.equals("10")))
                            layNote.setVisibility(View.VISIBLE);
                        else
                            layNote.setVisibility(View.GONE);
                    } else if (deliveryID != null && deliveryID.equals("3")) {
                        if (deliverySubID != null && (deliverySubID.equals("32")))
                            layNote.setVisibility(View.VISIBLE);
                        else
                            layNote.setVisibility(View.GONE);
                    } else if (deliveryID != null && deliveryID.equals("4")) {
                        if (deliverySubID != null && (deliverySubID.equals("33"))) {
                            //  layAnimalNote.setVisibility(View.VISIBLE);
                            layAnimalType.setVisibility(View.GONE);
                            layNote.setVisibility(View.VISIBLE);
                        } else {
                            //layAnimalNote.setVisibility(View.GONE);
                            layAnimalType.setVisibility(View.VISIBLE);
                            layNote.setVisibility(View.GONE);
                        }
                        if (deliverySubID != null && deliverySubID.equals("16")) {
                            mAnimalCarrierTitle.setVisibility(View.VISIBLE);
                            animalCarrier.setVisibility(View.VISIBLE);
                        } else {
                            mAnimalCarrierTitle.setVisibility(View.GONE);
                            animalCarrier.setVisibility(View.GONE);
                        }

                    } else
                        layNote.setVisibility(View.GONE);

                } else if (type == 3) {
                    deliverySubSubID = "";
                    animalType.setText(parent.getItemAtPosition(position).toString());
                    deliverySubSubID = getDeliverySubSubId(parent.getItemAtPosition(position).toString());
                    jsonArray = new JSONArray();
                    if (deliverySubSubID.equals("38") || deliverySubSubID.equals("43")) {
                        layAnimalType.setVisibility(View.VISIBLE);
                        layNote.setVisibility(View.VISIBLE);
                    } else {
                        layNote.setVisibility(View.GONE);
                        layAnimalType.setVisibility(View.VISIBLE);
                    }

                   /* if (parent.getItemAtPosition(position).toString().equalsIgnoreCase("other")) {
                        layAnimalType.setVisibility(View.GONE);
                        layAnimalNote.setVisibility(View.VISIBLE);
                    } else {
                        layAnimalNote.setVisibility(View.GONE);
                        layAnimalType.setVisibility(View.VISIBLE);
                    }*/
                    layTotalqty.setVisibility(View.VISIBLE);
                    resetFields();
                    /* resetFields();*/


                    //updateUI();
                    //updateSubUI();
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void hideAll() {
        deliveryType.setText("");
        animalType.setText("");
        deliverySubID = "";
        deliverySubSubID = "";
        totalQty.setText("");
        layMore.setVisibility(View.GONE);
        layTotalqty.setVisibility(View.GONE);
        layQty.setVisibility(View.GONE);
        layNote.setVisibility(View.GONE);
        layHWLW.setVisibility(View.GONE);
        layStackHaz.setVisibility(View.GONE);
        layAnimal.setVisibility(View.GONE);
        layPhoto.setVisibility(View.GONE);
    }

    private void updateSubUI() {
        if (deliverySubID != null && (deliverySubID.equals("18") || deliverySubID.equals("10") ||
                deliverySubID.equals("32"))) {
            layNote.setVisibility(View.VISIBLE);
        } else
            layNote.setVisibility(View.GONE);
        if (deliveryID.equals("4")) {
            if (deliverySubID != null && deliverySubID.equals("33")) {
                layAnimalType.setVisibility(View.GONE);
                //layAnimalNote.setVisibility(View.VISIBLE);
                layNote.setVisibility(View.VISIBLE);
            } else {
                if (deliverySubSubID != null && (deliverySubSubID.equals("38") || deliverySubSubID.equals("43"))) {
                    layAnimalType.setVisibility(View.VISIBLE);
                    layNote.setVisibility(View.VISIBLE);
                } else {
                    layNote.setVisibility(View.GONE);
                    layAnimalType.setVisibility(View.VISIBLE);
                }
            }
        }


    }

    public String[] getDeliveryList() {
        String[] listContent = new String[deliveryData.size()];
        for (int i = 0; i < deliveryData.size(); i++) {
            listContent[i] = deliveryData.get(i).get(Keys.KEY_NAME);
        }
        return listContent;
    }

    public String getDeliveryId(String countryName) {
        for (int i = 0; i < deliveryData.size(); i++) {
            if (deliveryData.get(i).get(Keys.KEY_NAME).equalsIgnoreCase(countryName))
                return deliveryData.get(i).get(Keys.KEY_ID);
        }
        return "";
    }

    public String getDeliverySubId(String stateName) {
        for (int i = 0; i < deliverySubData.size(); i++) {
            if (deliverySubData.get(i).get(Keys.KEY_NAME).equalsIgnoreCase(stateName))
                return deliverySubData.get(i).get(Keys.KEY_ID);
        }
        return "";
    }

    public String getDeliverySubSubId(String stateName) {
        for (int i = 0; i < animalTypeData.size(); i++) {
            if (animalTypeData.get(i).get(Keys.KEY_NAME).equalsIgnoreCase(stateName))
                return animalTypeData.get(i).get(Keys.KEY_ID);
        }
        return "";
    }

    public String getDeliverySubSubName(String id) {
        for (int i = 0; i < animalTypeData.size(); i++) {
            if (animalTypeData.get(i).get(Keys.KEY_ID).equalsIgnoreCase(id))
                return animalTypeData.get(i).get(Keys.KEY_NAME);
        }
        return "";
    }

    public String[] getDeliverySubList() {
        String[] listContent = new String[deliverySubData.size()];
        for (int i = 0; i < deliverySubData.size(); i++) {
            listContent[i] = deliverySubData.get(i).get(Keys.KEY_NAME);
        }
        return listContent;
    }

    public String[] getAnimalTyes() {
        String[] listContent = new String[animalTypeData.size()];
        for (int i = 0; i < animalTypeData.size(); i++) {
            listContent[i] = animalTypeData.get(i).get(Keys.KEY_NAME);
        }
        return listContent;
    }

    //  int type;
    ArrayList<HashMap<String, String>> deliveryData = new ArrayList<>();
    ArrayList<HashMap<String, String>> deliverySubData = new ArrayList<>();
    ArrayList<HashMap<String, String>> animalTypeData = new ArrayList<>();

    private void doGetStates(String delivery_id, int type) {
        this.type = type;
        String url;
        if (delivery_id.equals("2")) {
            url = Config.SERVER_URL + Config.EQUIPMENT_TYPE;
        } else
            url = Config.SERVER_URL + Config.DELIVERY_TYPE + "/subcat?id=" + delivery_id;
        Log.d(Config.TAG, url);
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void getDeliveryTypes() {
        type = 1;
        String url = Config.SERVER_URL + Config.DELIVERY_TYPE;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public void onTaskComplete(String result) {
        Log.d(Config.TAG, result);
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outterJson = new JSONObject(result);
            if (type == 10) {
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
                                getDeliveryTypes();

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
                    getDeliveryTypes();
                } else {
                    getDeliveryTypes();
                }
                if (!mCreditCardDetail)
                    BankmessageDialogAdd((HomeActivity) getActivity(), "Alert!", outterJson.getString(Config.MESSAGE), this.getClass().getName(), "5");
            } else {
                if (Integer.parseInt(outterJson.getString(Config.STATUS)) == APIStatus.SUCCESS) {
                    if (type == 9) {
                        delivery = new Delivery();
                        try {
                            JSONObject dataObj = outterJson.getJSONObject(Config.DATA);
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
                            getDeliveryTypes();
                            //IsCopy = true;

                        } catch (Exception e) {

                        }
                    } else {
                        if (type == 1)
                            deliveryData.clear();
                        else if (type == 2)
                            deliverySubData.clear();
                        else
                            animalTypeData.clear();
                        JSONArray outterArray = outterJson.getJSONArray("data");
                        for (int i = 0; i < outterArray.length(); i++) {
                            JSONObject innerJson = outterArray.getJSONObject(i);

                            if (type == 1) {
                                HashMap<String, String> map = new HashMap<>();
                                map.put(Keys.KEY_ID, innerJson.get(Keys.KEY_ID).toString());
                                map.put(Keys.KEY_NAME, innerJson.getString(Keys.KEY_NAME));
                                map.put(Keys.KEY_IS_SELECTED, innerJson.getString(Keys.KEY_IS_SELECTED));
                                deliveryData.add(map);
                            } else if (type == 2) {
                                HashMap<String, String> map = new HashMap<>();
                                map.put(Keys.KEY_ID, innerJson.get(Keys.KEY_ID).toString());
                                map.put(Keys.KEY_NAME, innerJson.getString(Keys.KEY_NAME));
                                if (innerJson.has(Keys.KEY_IS_SELECTED))
                                    map.put(Keys.KEY_IS_SELECTED, innerJson.getString(Keys.KEY_IS_SELECTED));
                                deliverySubData.add(map);
                            } else if (type == 3) {
                                HashMap<String, String> map = new HashMap<>();
                                map.put(Keys.KEY_ID, innerJson.get(Keys.KEY_ID).toString());
                                map.put(Keys.KEY_NAME, innerJson.getString(Keys.KEY_NAME));
                                if (innerJson.has(Keys.KEY_IS_SELECTED))
                                    map.put(Keys.KEY_IS_SELECTED, innerJson.getString(Keys.KEY_IS_SELECTED));
                                animalTypeData.add(map);
                                if (IsNotification) {
                                    if (session.getReadStatus().contentEquals("0"))
                                        changeNotificationStatus();
                                }
                            } else if (type == 14) {
                                IsNotification = false;
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
                                /*if (session.getReadStatus().contentEquals("0")) {
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
                            }
                        }
                        if (IsAppendData && type == 2 && !deliveryID.equals("") && !deliveryID.equals("null") && deliveryID.equals("4")) {
                            if (!deliverySubID.equals("") && !deliverySubID.equals("null")) {
                                doGetStates(deliverySubID, 3);
                                IsAppendData = false;
                            } else if (type == 2) {
                                if (IsNotification) {
                                    if (session.getReadStatus().contentEquals("0"))
                                        changeNotificationStatus();
                                }
                            }
                        } else if (type == 2) {
                            if (IsNotification) {
                                if (session.getReadStatus().contentEquals("0"))
                                    changeNotificationStatus();
                            }
                        }

                        if (type == 1) {
                            if (!deliveryID.equals("") && !deliveryID.equals("null"))
                                doGetStates(deliveryID, 2);
                        } else if (type == 3) {
                            if (deliverySubID != null && (deliverySubID.equals("33"))) {
                                layNote.setVisibility(View.VISIBLE);
                                layAnimalType.setVisibility(View.GONE);
                            } else {
                                layAnimalType.setVisibility(View.VISIBLE);
                            }
//                        layAnimalType.setVisibility(View.VISIBLE);
                            if (IsCopy) {
                                if (deliveryID.equals("4")) {
                                    try {
                                        if (deliverySubID != null && (deliverySubID.equals("33"))) {
                                            layNote.setVisibility(View.VISIBLE);
                                            if (delivery != null)
                                                notes.setText(delivery.getItem_delivery_other());
                                        } else {
                                            String name = getDeliverySubSubName(delivery.getDeliveryTypeSubSubID());
                                            animalType.setText(getDeliverySubSubName(delivery.getDeliveryTypeSubSubID()));
                                            if (deliverySubSubID.equals("38") || deliverySubSubID.equals("43")) {
                                                layAnimalType.setVisibility(View.VISIBLE);
                                                layNote.setVisibility(View.VISIBLE);
                                            } else {
                                                layNote.setVisibility(View.GONE);
                                                layAnimalType.setVisibility(View.VISIBLE);
                                            }

                                        }
                                   /* if (animalType.getText().toString().equalsIgnoreCase("other")) {
                                        layAnimalType.setVisibility(View.GONE);
                                        layAnimalNote.setVisibility(View.VISIBLE);
                                    } else {
                                        layAnimalNote.setVisibility(View.GONE);
                                        layAnimalType.setVisibility(View.VISIBLE);
                                    }*/
                                    } catch (Exception e) {
                                        e.toString();
                                    }
                                }
                            }
                        } else layAnimalType.setVisibility(View.GONE);
                    }
                } else {
                    try {
                        String message = null;
                        JSONArray jsonArray = outterJson.optJSONArray(Config.DATA);
                        if (jsonArray.length() > 0) {
                            message = outterJson.optJSONArray("data").optJSONObject(0).optString(Config.MESSAGE);
                        } else if (message == null) {
                            message = outterJson.optString("message");
                            Log.v("", message);
                        }
                        showMessage(message);
                    } catch (Exception e) {
                        e.toString();
                    }
                    //AlertManager.messageDialog(getActivity(), "Alert!", outterJson.getJSONArray("data").getJSONObject(0).getString(Config.MESSAGE));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateNextPrevious() {
       /* if (layImg.getVisibility() == View.GONE)
            pic.setVisibility(View.VISIBLE);
        else
            pic.setVisibility(View.GONE);*/
        try {
            int pos = currentPos + 1;
            mItemDetailTxt.setText("ITEM " + pos);
        } catch (Exception e) {
            e.toString();
        }

        if (currentPos >= 1) {
            previous.setVisibility(View.VISIBLE);
            back.setVisibility(View.GONE);
        } else {
            previous.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);
            saveItem.setVisibility(View.VISIBLE);
            submit.setVisibility(View.GONE);
        }

        if (jsonArray.length() - 1 > currentPos && jsonArray.length() > 1) {
            next.setVisibility(View.VISIBLE);
            same.setVisibility(View.GONE);
        } else {
            next.setVisibility(View.GONE);
        }
        int jsonlength = jsonArray.length();
        /*if (jsonArray.length() - 1 == currentPos && jsonArray.length() > 1) {
            saveItem.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);
        }*/
        String qtyStatus = qtyStatus();
        try {
            if (currentPos < jsonArray.length() - 1 || qtyStatus.equals("less")) {
                saveItem.setVisibility(View.VISIBLE);
                submit.setVisibility(View.GONE);

            } else if (qtyStatus.equals("equal") || currentPos == jsonArray.length() - 1) {
                saveItem.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.toString();
        }

        Log.v("", "");
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

    private void showItemDetail(JSONArray jsonArray, String url) {
        Log.d("size", "" + jsonArray.length());
        Log.d("pos", "" + currentPos);
        Log.d("deliveryID", "" + deliveryID);

        if (currentPos >= 0) {
            try {
                Log.d("item_photo", "item_photo::" + url + "" + jsonArray.getJSONObject(currentPos).getString("item_photo"));
                switch (deliveryID) {
                    case "1":
                        if (!jsonArray.getJSONObject(currentPos).getString("item_quantity").equals("null"))
                            qty.setText(jsonArray.getJSONObject(currentPos).getString("item_quantity"));
                        if (!jsonArray.getJSONObject(currentPos).getString("item_weight").equals("null"))
                            weight.setText(jsonArray.getJSONObject(currentPos).getString("item_weight"));
                        if (!jsonArray.getJSONObject(currentPos).getString("item_length").equals("null"))
                            length.setText(jsonArray.getJSONObject(currentPos).getString("item_length"));
                        if (!jsonArray.getJSONObject(currentPos).getString("item_width").equals("null"))
                            width.setText(jsonArray.getJSONObject(currentPos).getString("item_width"));
                        if (!jsonArray.getJSONObject(currentPos).getString("item_height").equals("null"))
                            height.setText(jsonArray.getJSONObject(currentPos).getString("item_height"));
                        if (!jsonArray.getJSONObject(currentPos).getString("item_stackable").equals("null")) {
                            if (jsonArray.getJSONObject(currentPos).getString("item_stackable").equals("1"))
                                stackable.setText("YES");
                            else
                                stackable.setText("NO");
                        }
                        if (!jsonArray.getJSONObject(currentPos).getString("item_hazardous").equals("null")) {
                            if (jsonArray.getJSONObject(currentPos).getString("item_hazardous").equals("1")) {
                                hazardous.setText("YES");
                                hazadours_good_value.setVisibility(View.VISIBLE);
                                hazadours_good_title.setVisibility(View.VISIBLE);
                                if (jsonArray.getJSONObject(currentPos).has("hazardous_goods_code_type"))
                                    hazadours_good_value.setText(jsonArray.getJSONObject(currentPos).getString("hazardous_goods_code_type"));
                            } else {
                                hazardous.setText("NO");
                                hazadours_good_value.setVisibility(View.GONE);
                                hazadours_good_title.setVisibility(View.GONE);
                            }
                        }
                        /*if (!jsonArray.getJSONObject(currentPos).getString("item_hazardous").equals("null")) {
                            notes.setText(jsonArray.getJSONObject(currentPos).getString("item_more_details"));
                        }*/
                        if (jsonArray.getJSONObject(currentPos).getString("item_photo") != null && !jsonArray.getJSONObject(currentPos).getString("item_photo").equals("") && !jsonArray.getJSONObject(currentPos).getString("item_photo").equals("null")) {
                         /*   if (!url.equals("")) {
                                // imgloader.DisplayImage(url + "" + jsonArray.getJSONObject(currentPos).getString("item_photo"), img);
                                //   if (jsonArray.getJSONObject(currentPos).getString("item_photo") != null && !jsonArray.getJSONObject(currentPos).getString("item_photo").contentEquals("") && !jsonArray.getJSONObject(currentPos).getString("item_photo").contentEquals("null"))
                                Picasso.with(getActivity()).load(jsonArray.getJSONObject(currentPos).getString("item_photo")).into(img);
                                layImg.setVisibility(View.VISIBLE);
                                pic.setVisibility(View.GONE);
                            } else {*/
                            String path = jsonArray.getJSONObject(currentPos).getString("item_photo");
                            //  cropImageView.setImageUri(Uri.fromFile(new File(jsonArray.getJSONObject(currentPos).getString("item_photo"))));
                            //final Bitmap croppedImage = cropImageView.getCroppedImage();

                            if (path != null && path.contains("http")) {
                                // imgloader.DisplayImage((path), img);
                                Picasso.with(getActivity()).load(path).into(img);
                                layImg.setVisibility(View.VISIBLE);
                                imagePath = path;
                                pic.setVisibility(View.GONE);
                            } else {
                                final Bitmap croppedImage = BitmapFactory.decodeFile(path);
                                layImg.setVisibility(View.VISIBLE);
                                img.setImageBitmap(croppedImage);
                                new SavePhotoTask(StorePath.ITEM_IMAGE, croppedImage).execute();
                                imagePath = jsonArray.getJSONObject(currentPos).getString("item_photo");
                                pic.setVisibility(View.GONE);
                            }
                            // }
                        } else {
                            imagePath = "";
                            layImg.setVisibility(View.GONE);
                            pic.setVisibility(View.VISIBLE);
                        }

                        break;
                    case "2":
                        if (!jsonArray.getJSONObject(currentPos).getString("item_quantity").equals("null"))
                            qty.setText(jsonArray.getJSONObject(currentPos).getString("item_quantity"));
                        if (!jsonArray.getJSONObject(currentPos).getString("item_more_details").equals("null"))
                            more.setText(jsonArray.getJSONObject(currentPos).getString("item_more_details"));
                       /* if (!jsonArray.getJSONObject(currentPos).getString("item_more_details").equals("null"))
                            notes.setText(jsonArray.getJSONObject(currentPos).getString("item_more_details"));*/
                        if (jsonArray.getJSONObject(currentPos).getString("item_photo") != null && !jsonArray.getJSONObject(currentPos).getString("item_photo").equals("") && !jsonArray.getJSONObject(currentPos).getString("item_photo").equals("null")) {
                           /* if (!url.equals("")) {
                                layImg.setVisibility(View.VISIBLE);
//                                imgloader.DisplayImage(url + "" + jsonArray.getJSONObject(currentPos).getString("item_photo"), img);
                                // if (jsonArray.getJSONObject(currentPos).getString("item_photo") != null && !jsonArray.getJSONObject(currentPos).getString("item_photo").contentEquals("") && !jsonArray.getJSONObject(currentPos).getString("item_photo").contentEquals("null"))
//                                Picasso.with(getActivity()).load(url + "" + jsonArray.getJSONObject(currentPos).getString("item_photo")).into(img);
                                Picasso.with(getActivity()).load(jsonArray.getJSONObject(currentPos).getString("item_photo")).into(img);
                                pic.setVisibility(View.GONE);
                            } else {*/
                            try {
                                String path = jsonArray.getJSONObject(currentPos).getString("item_photo");
                                // cropImageView.setImageUri(Uri.fromFile(new File(jsonArray.getJSONObject(currentPos).getString("item_photo"))));
//                                    final Bitmap croppedImage = cropImageView.getCroppedImage();
                                if (path != null && path.contains("http")) {
                                    //imgloader.DisplayImage((path), img);
                                    Picasso.with(getActivity()).load(path).into(img);
                                    layImg.setVisibility(View.VISIBLE);
                                    imagePath = path;
                                    pic.setVisibility(View.GONE);
                                } else {
                                    final Bitmap croppedImage = BitmapFactory.decodeFile(path);
                                    layImg.setVisibility(View.VISIBLE);
                                    img.setImageBitmap(croppedImage);
                                    new SavePhotoTask(StorePath.ITEM_IMAGE, croppedImage).execute();
                                    imagePath = jsonArray.getJSONObject(currentPos).getString("item_photo");
                                    pic.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.toString();
                            }
                            // }
                        } else {
                            imagePath = "";
                            layImg.setVisibility(View.GONE);
                            pic.setVisibility(View.VISIBLE);
                        }

                        break;
                    case "3":
                        if (!jsonArray.getJSONObject(currentPos).getString("item_quantity").equals("null"))
                            qty.setText(jsonArray.getJSONObject(currentPos).getString("item_quantity"));
                        if (!jsonArray.getJSONObject(currentPos).getString("item_more_details").equals("null"))
                            more.setText(jsonArray.getJSONObject(currentPos).getString("item_more_details"));
                        /*if (!jsonArray.getJSONObject(currentPos).getString("item_more_details").equals("null"))
                            notes.setText(jsonArray.getJSONObject(currentPos).getString("item_more_details"));*/
                        if (deliverySubID != null && deliverySubID.equals("25") || deliverySubID.equals("26")
                                || deliverySubID.equals("28") || deliverySubID.equals("29")
                                || deliverySubID.equals("30")) {
                            if (!jsonArray.getJSONObject(currentPos).getString("item_weight").equals("null"))
                                weight.setText(jsonArray.getJSONObject(currentPos).getString("item_weight"));
                            if (!jsonArray.getJSONObject(currentPos).getString("item_length").equals("null"))
                                length.setText(jsonArray.getJSONObject(currentPos).getString("item_length"));
                            if (!jsonArray.getJSONObject(currentPos).getString("item_width").equals("null"))
                                width.setText(jsonArray.getJSONObject(currentPos).getString("item_width"));
                            if (!jsonArray.getJSONObject(currentPos).getString("item_height").equals("null"))
                                height.setText(jsonArray.getJSONObject(currentPos).getString("item_height"));
                        }
                        if (jsonArray.getJSONObject(currentPos).getString("item_photo") != null && !jsonArray.getJSONObject(currentPos).getString("item_photo").equals("") && !jsonArray.getJSONObject(currentPos).getString("item_photo").equals("null")) {
                          /*  if (!url.equals("")) {
                                layImg.setVisibility(View.VISIBLE);
                                // imgloader.DisplayImage(url + "" + jsonArray.getJSONObject(currentPos).getString("item_photo"), img);
                                //  if (jsonArray.getJSONObject(currentPos).getString("item_photo") != null && !jsonArray.getJSONObject(currentPos).getString("item_photo").contentEquals("") && !jsonArray.getJSONObject(currentPos).getString("item_photo").contentEquals("null"))
                                Picasso.with(getActivity()).load(jsonArray.getJSONObject(currentPos).getString("item_photo")).into(img);
                                pic.setVisibility(View.GONE);
                            } else {*/
                            String path = jsonArray.getJSONObject(currentPos).getString("item_photo");
                            //   cropImageView.setImageUri(Uri.fromFile(new File(jsonArray.getJSONObject(currentPos).getString("item_photo"))));
                            // final Bitmap croppedImage = cropImageView.getCroppedImage();
                            if (path != null && path.contains("http")) {
                                //  imgloader.DisplayImage((path), img);
                                Picasso.with(getActivity()).load(path).into(img);
                                layImg.setVisibility(View.VISIBLE);
                                imagePath = path;
                                pic.setVisibility(View.GONE);
                            } else {
                                final Bitmap croppedImage = BitmapFactory.decodeFile(path);
                                layImg.setVisibility(View.VISIBLE);
                                img.setImageBitmap(croppedImage);
                                new SavePhotoTask(StorePath.ITEM_IMAGE, croppedImage).execute();
                                imagePath = jsonArray.getJSONObject(currentPos).getString("item_photo");
                                pic.setVisibility(View.GONE);
                            }
                            // }
                        } else {
                            imagePath = "";
                            layImg.setVisibility(View.GONE);
                            pic.setVisibility(View.VISIBLE);
                        }
                        break;
                    case "4":
                        if (!jsonArray.getJSONObject(currentPos).getString("item_quantity").equals("null"))
                            qty.setText(jsonArray.getJSONObject(currentPos).getString("item_quantity"));
                        if (deliverySubID != null && deliverySubID.equals("16")) {
                            if (!jsonArray.getJSONObject(currentPos).getString("item_animal_carrier").equals("null")) {
                                if (jsonArray.getJSONObject(currentPos).getString("item_animal_carrier").equals("1"))
                                    animalCarrier.setText("YES");
                                else
                                    animalCarrier.setText("NO");
                            }
                        }
                     /*   if (!jsonArray.getJSONObject(currentPos).getString("item_animal_name").equals("null"))
                            animalName.setText(jsonArray.getJSONObject(currentPos).getString("item_animal_name"));
                     */
                        if (jsonArray.getJSONObject(currentPos).getString("item_animal_breed") != null && !jsonArray.getJSONObject(currentPos).getString("item_animal_breed").equals("null"))
                            breed.setText(jsonArray.getJSONObject(currentPos).getString("item_animal_breed"));
                        if (!jsonArray.getJSONObject(currentPos).getString("item_weight").equals("null"))
                            animalWeight.setText(jsonArray.getJSONObject(currentPos).getString("item_weight"));
                        if (!jsonArray.getJSONObject(currentPos).getString("item_current_vaccinations").equals("null")) {
                            if (jsonArray.getJSONObject(currentPos).getString("item_current_vaccinations").equals("1"))
                                currentVaccination.setText("YES");
                            else
                                currentVaccination.setText("NO");
                        }

                        if (!jsonArray.getJSONObject(currentPos).getString("item_more_details").equals("null"))
                            more.setText(jsonArray.getJSONObject(currentPos).getString("item_more_details"));
                      /*  if (!jsonArray.getJSONObject(currentPos).getString("item_more_details").equals("null"))
                            notes.setText(jsonArray.getJSONObject(currentPos).getString("item_more_details"));*/
                        if (jsonArray.getJSONObject(currentPos).getString("item_photo") != null && !jsonArray.getJSONObject(currentPos).getString("item_photo").equals("") && !jsonArray.getJSONObject(currentPos).getString("item_photo").equals("null")) {
                           /* if (!url.equals("")) {
                                layImg.setVisibility(View.VISIBLE);
                                //  imgloader.DisplayImage(url + "" + jsonArray.getJSONObject(currentPos).getString("item_photo"), img);
                                // if (jsonArray.getJSONObject(currentPos).getString("item_photo") != null && !jsonArray.getJSONObject(currentPos).getString("item_photo").contentEquals("") && !jsonArray.getJSONObject(currentPos).getString("item_photo").contentEquals("null"))
                                Picasso.with(getActivity()).load(jsonArray.getJSONObject(currentPos).getString("item_photo")).into(img);
                                pic.setVisibility(View.GONE);
                            } else {*/
                            try {
                                String path = jsonArray.getJSONObject(currentPos).getString("item_photo");
                                //  cropImageView.setImageUri(Uri.fromFile(new File(jsonArray.getJSONObject(currentPos).getString("item_photo"))));
                                //final Bitmap croppedImage = cropImageView.getCroppedImage();

                                if (path != null && path.contains("http")) {
                                    //imgloader.DisplayImage((path), img);
                                    Picasso.with(getActivity()).load(path).into(img);
                                    layImg.setVisibility(View.VISIBLE);
                                    imagePath = path;
                                    pic.setVisibility(View.GONE);
                                } else {
                                    final Bitmap croppedImage = BitmapFactory.decodeFile(path);
                                    layImg.setVisibility(View.VISIBLE);
                                    img.setImageBitmap(croppedImage);
                                    new SavePhotoTask(StorePath.ITEM_IMAGE, croppedImage).execute();
                                    imagePath = jsonArray.getJSONObject(currentPos).getString("item_photo");
                                    pic.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.toString();
                            }
                            //  }
                        } else {
                            imagePath = "";
                            layImg.setVisibility(View.GONE);
                            pic.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String qtyStatus() {
        String status = "";
        int itemQtyUsed;
        Log.v("qty", "qty:" + qty.getText().toString());
        if (qty.getText().toString().length() > 0)
            itemQtyUsed = Integer.parseInt(qty.getText().toString());
        else
            itemQtyUsed = 0;
        Log.v("itemQtyUsed", "itemQtyUsed:" + itemQtyUsed);
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                if (i != currentPos)
                    itemQtyUsed += Integer.parseInt(jsonArray.getJSONObject(i).getString("item_quantity"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("itemQtyUsed", "itemQtyUsed:" + itemQtyUsed);


      /*  if (totalQty.getText().length() 0= 0 && ){

        }*/
        if (totalQty.getText().length() > 0) {
            if (itemQtyUsed < Integer.parseInt(totalQty.getText().toString())) {
                status = "less";
            } else if (itemQtyUsed > Integer.parseInt(totalQty.getText().toString())) {
                status = "greater";
            } else {
                status = "equal";
            }
        }
        Log.d("itemQtyUsed", "itemQtyUsed" + itemQtyUsed);
        Log.d("totalQty", "Qty" + totalQty.getText().toString());
        Log.d("qty_status", status);
        return status;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                topLayout.setVisibility(View.VISIBLE);
                btmLayout.setVisibility(View.GONE);
                dot1.setBackgroundResource(R.drawable.active_dotblue);
                dot2.setBackgroundResource(R.drawable.dotblue);
                saveItem.setVisibility(View.GONE);
                submit.setVisibility(View.GONE);
                break;
            case R.id.save:
                qty.setText("1");
                if (isValid()) {
                    if (saveItem.getText().toString().contains("SAVE")) {
                        String qtyStatus = qtyStatus();
                        if (qtyStatus.equals("less") || qtyStatus.equals("equal")) {
                            saveJsonData();
                            if (currentPos >= jsonArray.length() - 1)
                                currentPos = jsonArray.length() - 1;
                            if (qtyStatus.equals("less")) {
                                saveItem.setVisibility(View.VISIBLE);
                            }
                            ++currentPos;
                            updateNextPrevious();
                            Log.v("saveItem", "saveItem:" + saveItem);
                            if (!itemData.equals("")) {
                                if (getArguments().containsKey("data") || getArguments().containsKey("delivery_id")) {
                                    if (jsonArray.length() - 1 >= currentPos) {
                                        showItemDetail(jsonArray, "");
                                    } else if (jsonArrayData.length() > currentPos)
                                        showItemDetail(jsonArrayData, Config.DeliveryImgURL);
                                    else
                                        resetFields();
                                }

                            } else if (jsonArray != null && jsonArray.length() > 0 && jsonArray.length() > currentPos) {
                                showItemDetail(jsonArray, "");
                            } else
                                resetFields();
                            validateButton(false);

                        } else if (qtyStatus().equals("greater")) {
                            showMessage("Item quantity is greater than total quantity. Items quantity must equal to total quantity.");
                        }
                    } else {
                        //addMore.setVisibility(View.GONE);
                        saveItem.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.previous:
                if (currentPos > 0) {
                    --currentPos;
                    same.setVisibility(View.GONE);
                    updateNextPrevious();
                    showItemDetail(jsonArray, "");
                }
                break;
            case R.id.next:
                if (jsonArray.length() > currentPos) {
                    ++currentPos;
                    updateNextPrevious();
                    showItemDetail(jsonArray, "");
                }
                break;
          /*  case R.id.add_more:
                ++currentPos;
                updateNextPrevious();
                resetFields();
                break;*/
            case R.id.qty_done:
              /*  if (!mCreditCardDetail) {
                    AlertManager.BankmessageDialog((HomeActivity) getActivity(), "Alert!", getActivity().getResources().getString(R.string.creditcarddecline), this.getClass().getName(), "5");
                    return;
                }*/
                if (delivry.getText().toString().contentEquals("Choose Delivery")) {
                    showMessage("Please Select Delivery Type.");
                    return;
                }
                if (deliveryID != null && deliveryID.contentEquals("4")) {
                    if (deliverySubSubID != null && deliverySubSubID.contentEquals("") && !deliverySubID.equals("33")) {
                        showMessage("Please Select Animal Type.");
                        return;
                    }
                }
                if (deliveryID != null && deliveryID.equals("1")) {
                    if (deliverySubID != null && (deliverySubID.equals("18")))
                        if (notes.getText().toString().trim().length() == 0) {
                            showMessage("Please enter item detail.");
                            return;
                        }

                } else if (deliveryID != null && deliveryID.equals("2")) {
                    if (deliverySubID != null && (deliverySubID.equals("10")))
                        if (notes.getText().toString().trim().length() == 0) {
                            showMessage("Please enter item detail.");
                            return;
                        }

                } else if (deliveryID != null && deliveryID.equals("3")) {
                    if (deliverySubID != null && (deliverySubID.equals("32")))
                        if (notes.getText().toString().trim().length() == 0) {
                            showMessage("Please enter item detail.");
                            return;
                        }

                } else if (deliveryID != null && deliveryID.equals("4")) {
                    if (deliverySubID != null && (deliverySubID.equals("33")))
                        if (notes.getText().toString().trim().length() == 0) {
                            showMessage("Please enter item detail.");
                            return;
                        }
                    if (deliverySubSubID != null && !deliverySubSubID.contentEquals("")) {
                        if (deliverySubSubID.equals("38") || deliverySubSubID.equals("43")) {
                            if (notes.getText().toString().trim().length() == 0) {
                                showMessage("Please enter item detail.");
                                return;
                            }
                        }
                    }
                }

                int length = totalQty.getText().length();
                if (length != 0 && !checkZero(totalQty.getText().toString())) {
                    showMessage("Quantity must be greater than zero.");
                    return;
                }
                if (length != 0 && length > 4) {
                    showMessage("Please enter qty max 4 digit.");
                    return;
                }

                if (totalQty.getText().length() > 0) {
                    currentPos = 0;
                    if (IsCopy) {
                        try {
                            int totalQ = Integer.parseInt(totalQty.getText().toString());
                            {
                                if (jsonArrayData != null && totalQ < jsonArrayData.length()) {
                                    remove_shipmentAlert(getActivity().getResources().getString(R.string.removeshipment), 1);
//                                    remove_copyshipment(totalQ);
                                    return;
                                }
                            }

                        } catch (Exception e) {
                            e.toString();
                        }
                    }
                    try {
                        int totalQ = Integer.parseInt(totalQty.getText().toString());
                        {
                            if (jsonArray != null && totalQ < jsonArray.length()) {
                                remove_shipmentAlert(getActivity().getResources().getString(R.string.removeshipment), 0);
                                return;
                                // remove_shipment(totalQ);
                            }
                        }
                    } catch (Exception e) {
                        e.toString();
                    }
                    topLayout.setVisibility(View.GONE);
                    btmLayout.setVisibility(View.VISIBLE);
                    Log.v("deliveryID:", "deliveryID::" + deliveryID);
                    updateUI();
                    updateSubUI();
                    saveItem.setVisibility(View.VISIBLE);
                    if (!saveItem.equals("")) {
                        if (getArguments().containsKey("data") || getArguments().containsKey("delivery_id")) {
                            if (jsonArrayData.length() > currentPos)
                                showItemDetail(jsonArrayData, Config.DeliveryImgURL);
                            else
                                resetFields();
                        }
                    }

                    validateButton(true);
                } else showMessage("Please enter total quantity.");
                break;
            case R.id.delivery:
                showGrabidDialog(1);
                break;
            case R.id.d_type:
                showGrabidDialog(2);
                break;
            case R.id.animal_type:
                showGrabidDialog(3);
                break;
            case R.id.submit:
                //submitShipment();
                qty.setText("1");
                if (isValid()) {
                    String qtyStatus = qtyStatus();
                    if (qtyStatus.equals("less") || qtyStatus.equals("equal")) {
                        saveJsonData();
                        if (currentPos >= jsonArray.length() - 1)
                            currentPos = jsonArray.length() - 1;
                        if (qtyStatus.equals("less")) {
                            saveItem.setVisibility(View.VISIBLE);
                        }
                    } else if (qtyStatus().equals("greater")) {
                        showMessage("Item quantity is greater than total quantity. Items quantity must equal to total quantity.");
                    }
                    int itemQtyUsed = 0;
                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            itemQtyUsed += Integer.parseInt(jsonArray.getJSONObject(i).getString("item_quantity"));
                        }
                        Log.v("itemQtyUsed", "itemQtyUsed" + itemQtyUsed);
                        Log.v("totalQty", "totalQty" + totalQty.getText().toString());
                        if (itemQtyUsed != Integer.parseInt(totalQty.getText().toString())) {
                            showMessage("Items quantity must equal to total quantity.");
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    addShipment();
                }
                break;
            case R.id.image:
//                selectImage();
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US).format(new Date());
                ImageCropActivity.TEMP_PHOTO_FILE_NAME = "shippment_photo" + timeStamp + ".jpg";
                showAddProfilePicDialog();
                break;
            case R.id.cancel:
                onCancel();
                break;
            case R.id.done:
                //  onDone();
                break;
            case R.id.hazardous:
                showSelctionDialog(2);
                break;
            case R.id.stackable:
                showSelctionDialog(1);
                break;
            case R.id.current_vaccination:
                showSelctionDialog(3);
                break;
            case R.id.animal_carrier:
                showSelctionDialog(4);
                break;
            case R.id.edit_img:
//                selectImage();
                String timeStampp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US).format(new Date());
                ImageCropActivity.TEMP_PHOTO_FILE_NAME = "shippment_photo" + timeStampp + ".jpg";
                showAddProfilePicDialog();
                /*String timeStampp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                ImageCropActivity.TEMP_PHOTO_FILE_NAME = "shippment_photo" + timeStampp + ".jpg";
                //    ImageCropActivity.TEMP_PHOTO_FILE_NAME = "shippment_photo.jpg";
                showAddProfilePicDialog();*/
                break;

            case R.id.same:
                fillData();
                same.setVisibility(View.GONE);
                break;

        }
    }

    public void remove_shipmentAlert(String message, final int val) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                try {
                    int totalQ = Integer.parseInt(totalQty.getText().toString());
                    if (val == 1) {
                        remove_copyshipment(totalQ);
                        if (jsonArray != null && totalQ < jsonArray.length()) {
                            remove_shipment(totalQ);
                        }
                    } else {
                        remove_shipment(totalQ);
                    }
                    topLayout.setVisibility(View.GONE);
                    btmLayout.setVisibility(View.VISIBLE);
                    Log.v("deliveryID:", "deliveryID::" + deliveryID);
                    updateUI();
                    updateSubUI();
                    saveItem.setVisibility(View.VISIBLE);
                    if (!saveItem.equals("")) {
                        if (getArguments().containsKey("data") || getArguments().containsKey("delivery_id")) {
                            if (jsonArrayData.length() > currentPos)
                                showItemDetail(jsonArrayData, Config.DeliveryImgURL);
                            else
                                resetFields();
                        }
                    }

                    validateButton(true);
                } catch (Exception e) {
                    e.toString();
                }

                //   remove_vehicle(Integer.parseInt(totalVehicle.getText().toString()));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog d = builder.create();
        d.show();
    }

    public Boolean checkZero(String val) {
        if (val.matches("[0]+"))
            return false;


        return true;
    }

    public void validateButton(boolean Isdone) {
        if (!Isdone)
            imagePath = "";
        qty.setText("1");
        String qtyStatus = qtyStatus();
        Log.v("qtyStatus", "qtyStatus:" + qtyStatus);
        if (qtyStatus.equals("less")) {
            saveItem.setVisibility(View.VISIBLE);
            submit.setVisibility(View.GONE);
        } else if (qtyStatus.equals("greater")) {
            showMessage("Items quantity must equal to total quantity.");
        } else if (qtyStatus.equals("equal")) {
            //saveItem.setText("Update Item");
            //saveItem.setVisibility(View.VISIBLE);
            saveItem.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);
        }
        //
        try {
            int len = jsonArray.length() - 1;
            if (currentPos < jsonArray.length() - 1 || qtyStatus.equals("less")) {
                saveItem.setVisibility(View.VISIBLE);
                submit.setVisibility(View.GONE);

            } else if (qtyStatus.equals("equal") || currentPos == jsonArray.length() - 1) {
                saveItem.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.toString();
        }
    }

    public void remove_copyshipment(int totalqty) {
        try {
            for (int i = jsonArrayData.length(); i >= totalqty; i--) {
                jsonArrayData.remove(i);
            }
        } catch (Exception e) {
            e.toString();
        }
    }

    public void remove_shipment(int totalqty) {
        try {
            for (int i = jsonArray.length(); i >= totalqty; i--) {
                jsonArray.remove(i);
            }
            if (jsonArray != null && !(jsonArray.length() > 1))
                next.setVisibility(View.GONE);

            Log.v("", "");
        } catch (Exception e) {
            e.toString();
        }

    }

    private void saveJsonData() {
       /* if (!itemData.equals("")) {
            if (getArguments().containsKey("data")) {
                if (jsonArrayData.length() > currentPos) {
                    try {
                        if (jsonArrayData.getJSONObject(currentPos).getString("item_photo") != null)
                            imagePath = Config.DeliveryImgURL + "" + jsonArrayData.getJSONObject(currentPos).getString("item_photo");
                        String value = imagePath;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }*/
        if (IsCopy) {
            if (imagePath.contentEquals("")) {
                try {
                    if (jsonArray.length() - 1 >= currentPos) {
                        imagePath = jsonArray.getJSONObject(currentPos).getString("item_photo");
                        Log.v("", imagePath);
                    } else if (jsonArrayData.length() - 1 >= currentPos) {
                        String url = jsonArrayData.optJSONObject(currentPos).getString("item_photo");
                        if (url != null && !url.contentEquals("null") && !url.contentEquals("")) {
                            // imagePath = url;
                            //   imagePath = Config.DeliveryImgURL + url;
                            imagePath = url;
                            Log.v("", url);
                        } else {
                            imagePath = "";
                        }
                    }
                } catch (Exception e) {
                    e.toString();
                }

            }
        }

        if (imagePath.contentEquals("")) {
            try {
                if (jsonArray.length() - 1 >= currentPos) {
                    imagePath = jsonArray.getJSONObject(currentPos).getString("item_photo");
                    Log.v("", imagePath);
                }
            } catch (Exception e) {
                e.toString();
            }

        }
        String moreStr = "";
        switch (deliveryID) {
            case "1":
                saveFrightItems("", qty.getText().toString(), weight.getText().toString()
                        , length.getText().toString(), width.getText().toString()
                        , height.getText().toString(), stackable.getText().toString().equalsIgnoreCase("yes") ? "1" : "0"
                        , hazardous.getText().toString().equalsIgnoreCase("yes") ? "1" : "0", hazardous.getText().toString().equalsIgnoreCase("yes") ? hazadours_good_value.getText().toString() : "0", imagePath);
                break;
            case "2":
                moreStr = more.getText().toString();
                saveApplianceItems("", qty.getText().toString(), more.getText().toString(), imagePath);
                break;
            case "3":
                saveVehicleItems("", qty.getText().toString(), weight.getText().toString()
                        , length.getText().toString(), width.getText().toString()
                        , height.getText().toString(), more.getText().toString(), imagePath);
                break;
            case "4":
                if (deliverySubID != null && deliverySubID.equals("16")) {
                    saveAnimalItems("", qty.getText().toString(), animalCarrier.getText().toString().equalsIgnoreCase("yes") ? "1" : "0"
                            , "", breed.getText().toString()
                            , animalWeight.getText().toString()
                            , hasCurrentVaccination ? "1" : "0", more.getText().toString(), imagePath);

                } else {
                    saveAnimalItems("", qty.getText().toString(), "0"
                            , "", breed.getText().toString()
                            , animalWeight.getText().toString()
                            , hasCurrentVaccination ? "1" : "0", more.getText().toString(), imagePath);
                }
                break;

        }
    }

    public void fillData() {
        switch (deliveryID) {
            case "1":
                try {
                    if (!jsonArray.getJSONObject(currentPos - 1).getString("item_quantity").equals("null"))
                        qty.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_quantity"));
                    if (!jsonArray.getJSONObject(currentPos - 1).getString("item_weight").equals("null"))
                        weight.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_weight"));
                    if (!jsonArray.getJSONObject(currentPos - 1).getString("item_length").equals("null"))
                        length.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_length"));
                    if (!jsonArray.getJSONObject(currentPos - 1).getString("item_width").equals("null"))
                        width.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_width"));
                    if (!jsonArray.getJSONObject(currentPos - 1).getString("item_height").equals("null"))
                        height.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_height"));
                    if (!jsonArray.getJSONObject(currentPos - 1).getString("item_stackable").equals("null")) {
                        if (jsonArray.getJSONObject(currentPos - 1).getString("item_stackable").equals("1"))
                            stackable.setText("YES");
                        else
                            stackable.setText("NO");
                    }
                    if (!jsonArray.getJSONObject(currentPos - 1).getString("item_hazardous").equals("null")) {
                        if (jsonArray.getJSONObject(currentPos - 1).getString("item_hazardous").equals("1")) {
                            hazardous.setText("YES");
                            hazadours_good_value.setVisibility(View.VISIBLE);
                            hazadours_good_title.setVisibility(View.VISIBLE);
                            if (jsonArray.getJSONObject(currentPos - 1).has("hazardous_goods_code_type"))
                                hazadours_good_value.setText(jsonArray.getJSONObject(currentPos - 1).getString("hazardous_goods_code_type"));
                        } else {
                            hazardous.setText("NO");
                            hazadours_good_value.setVisibility(View.GONE);
                            hazadours_good_title.setVisibility(View.GONE);
                        }
                    }
                    /*if (!jsonArray.getJSONObject(currentPos - 1).getString("item_hazardous").equals("null")) {
                        notes.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_more_details"));
                    }*/

                    if (!jsonArray.getJSONObject(currentPos - 1).getString("item_photo").equals("") && !jsonArray.getJSONObject(currentPos - 1).getString("item_photo").equals("null")) {
                        String path = jsonArray.getJSONObject(currentPos - 1).getString("item_photo");
                        if (path != null && path.contains("http")) {
                            //  imgloader.DisplayImage((path), img);
                            Picasso.with(getActivity()).load(path).into(img);
                            layImg.setVisibility(View.VISIBLE);
                            imagePath = path;
                            pic.setVisibility(View.GONE);
                        } else if (path != null && !path.contentEquals("")) {
                            final Bitmap croppedImage = BitmapFactory.decodeFile(path);
                            layImg.setVisibility(View.VISIBLE);
                            img.setImageBitmap(croppedImage);
                            new SavePhotoTask(StorePath.ITEM_IMAGE, croppedImage).execute();
                            imagePath = jsonArray.getJSONObject(currentPos - 1).getString("item_photo");
                            pic.setVisibility(View.GONE);
                        }

                    } else {
                        imagePath = "";
                        layImg.setVisibility(View.GONE);
                        pic.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "2":
                try {
                    if (!jsonArray.getJSONObject(currentPos - 1).getString("item_quantity").equals("null"))
                        qty.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_quantity"));
                    if (!jsonArray.getJSONObject(currentPos - 1).getString("item_more_details").equals("null"))
                        more.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_more_details"));
                    /*if (!jsonArray.getJSONObject(currentPos - 1).getString("item_more_details").equals("null"))
                        notes.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_more_details"));*/
                    if (!jsonArray.getJSONObject(currentPos - 1).getString("item_photo").equals("") && !jsonArray.getJSONObject(currentPos - 1).getString("item_photo").equals("null")) {

                        try {
                            String path = jsonArray.getJSONObject(currentPos - 1).getString("item_photo");
                            // cropImageView.setImageUri(Uri.fromFile(new File(jsonArray.getJSONObject(currentPos).getString("item_photo"))));
//                                    final Bitmap croppedImage = cropImageView.getCroppedImage();
                            if (path != null && path.contains("http")) {
//                                imgloader.DisplayImage((path), img);
                                Picasso.with(getActivity()).load(path).into(img);
                                layImg.setVisibility(View.VISIBLE);
                                imagePath = path;
                                pic.setVisibility(View.GONE);
                            } else {
                                final Bitmap croppedImage = BitmapFactory.decodeFile(path);
                                layImg.setVisibility(View.VISIBLE);
                                img.setImageBitmap(croppedImage);
                                new SavePhotoTask(StorePath.ITEM_IMAGE, croppedImage).execute();
                                imagePath = jsonArray.getJSONObject(currentPos - 1).getString("item_photo");
                                pic.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.toString();
                        }

                    } else {
                        imagePath = "";
                        layImg.setVisibility(View.GONE);
                        pic.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.toString();
                }
                break;
            case "3":
                try {
                    if (!jsonArray.getJSONObject(currentPos - 1).getString("item_quantity").equals("null"))
                        qty.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_quantity"));
                    if (!jsonArray.getJSONObject(currentPos - 1).getString("item_more_details").equals("null"))
                        more.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_more_details"));
                    /*if (!jsonArray.getJSONObject(currentPos - 1).getString("item_more_details").equals("null"))
                        notes.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_more_details"));*/
                    if (deliverySubID != null && deliverySubID.equals("25") || deliverySubID.equals("26")
                            || deliverySubID.equals("28") || deliverySubID.equals("29")
                            || deliverySubID.equals("30")) {
                        if (!jsonArray.getJSONObject(currentPos - 1).getString("item_weight").equals("null"))
                            weight.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_weight"));
                        if (!jsonArray.getJSONObject(currentPos - 1).getString("item_length").equals("null"))
                            length.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_length"));
                        if (!jsonArray.getJSONObject(currentPos - 1).getString("item_width").equals("null"))
                            width.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_width"));
                        if (!jsonArray.getJSONObject(currentPos - 1).getString("item_height").equals("null"))
                            height.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_height"));
                    }
                    if (!jsonArray.getJSONObject(currentPos - 1).getString("item_photo").equals("") && !jsonArray.getJSONObject(currentPos - 1).getString("item_photo").equals("null")) {

                        String path = jsonArray.getJSONObject(currentPos - 1).getString("item_photo");
                        //   cropImageView.setImageUri(Uri.fromFile(new File(jsonArray.getJSONObject(currentPos).getString("item_photo"))));
                        // final Bitmap croppedImage = cropImageView.getCroppedImage();
                        if (path != null && path.contains("http")) {
                            //imgloader.DisplayImage((path), img);
                            Picasso.with(getActivity()).load(path).into(img);
                            layImg.setVisibility(View.VISIBLE);
                            imagePath = path;
                            pic.setVisibility(View.GONE);
                        } else {
                            final Bitmap croppedImage = BitmapFactory.decodeFile(path);
                            layImg.setVisibility(View.VISIBLE);
                            img.setImageBitmap(croppedImage);
                            new SavePhotoTask(StorePath.ITEM_IMAGE, croppedImage).execute();
                            imagePath = jsonArray.getJSONObject(currentPos - 1).getString("item_photo");
                            pic.setVisibility(View.GONE);
                        }

                    } else {
                        imagePath = "";
                        layImg.setVisibility(View.GONE);
                        pic.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.toString();
                }
                break;
            case "4":
                try {
                    if (!jsonArray.getJSONObject(currentPos - 1).getString("item_quantity").equals("null"))
                        qty.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_quantity"));
                    if (deliverySubID != null && deliverySubID.equals("16")) {
                        if (!jsonArray.getJSONObject(currentPos - 1).getString("item_animal_carrier").equals("null")) {
                            if (jsonArray.getJSONObject(currentPos - 1).getString("item_animal_carrier").equals("1"))
                                animalCarrier.setText("YES");
                            else
                                animalCarrier.setText("NO");
                        }
                    }
               /*     if (!jsonArray.getJSONObject(currentPos - 1).getString("item_animal_name").equals("null"))
                        animalName.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_animal_name"));
               */
                    if (jsonArray.getJSONObject(currentPos - 1).getString("item_animal_breed") != null && !jsonArray.getJSONObject(currentPos - 1).getString("item_animal_breed").equals("null"))
                        breed.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_animal_breed"));
                    if (!jsonArray.getJSONObject(currentPos - 1).getString("item_weight").equals("null"))
                        animalWeight.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_weight"));
                    if (!jsonArray.getJSONObject(currentPos - 1).getString("item_current_vaccinations").equals("null")) {
                        if (jsonArray.getJSONObject(currentPos - 1).getString("item_current_vaccinations").equals("1"))
                            currentVaccination.setText("YES");
                        else
                            currentVaccination.setText("NO");
                    }

                    if (!jsonArray.getJSONObject(currentPos - 1).getString("item_more_details").equals("null"))
                        more.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_more_details"));
                    /*if (!jsonArray.getJSONObject(currentPos - 1).getString("item_more_details").equals("null"))
                        notes.setText(jsonArray.getJSONObject(currentPos - 1).getString("item_more_details"));*/
                    if (!jsonArray.getJSONObject(currentPos - 1).getString("item_photo").equals("") && !jsonArray.getJSONObject(currentPos - 1).getString("item_photo").equals("null")) {

                        try {
                            String path = jsonArray.getJSONObject(currentPos - 1).getString("item_photo");
                            //  cropImageView.setImageUri(Uri.fromFile(new File(jsonArray.getJSONObject(currentPos).getString("item_photo"))));
                            //final Bitmap croppedImage = cropImageView.getCroppedImage();

                            if (path != null && path.contains("http")) {
                                //  imgloader.DisplayImage((path), img);
                                Picasso.with(getActivity()).load(path).into(img);
                                layImg.setVisibility(View.VISIBLE);
                                imagePath = path;
                                pic.setVisibility(View.GONE);

                            } else {
                                final Bitmap croppedImage = BitmapFactory.decodeFile(path);
                                layImg.setVisibility(View.VISIBLE);
                                img.setImageBitmap(croppedImage);
                                new SavePhotoTask(StorePath.ITEM_IMAGE, croppedImage).execute();
                                imagePath = jsonArray.getJSONObject(currentPos - 1).getString("item_photo");
                                pic.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.toString();
                        }

                    } else {
                        imagePath = "";
                        layImg.setVisibility(View.GONE);
                        pic.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.toString();
                }
                break;
        }
    }

    private void resetFields() {
        //addMore.setVisibility(View.GONE);
        Log.v("resetFields", "resetFields" + deliveryID);
        int len = jsonArray.length();
        if (jsonArray.length() >= 1)
            same.setVisibility(View.VISIBLE);
        weight.requestFocus();
        switch (deliveryID) {
            case "1":
                qty.setText("");
                weight.setText("");
                length.setText("");
                width.setText("");
                height.setText("");
                stackable.setText("");
                hazardous.setText("");
                hazadours_good_value.setText("");
                hazadours_good_value.setVisibility(View.GONE);
                hazadours_good_title.setVisibility(View.GONE);
                //   notes.setText("");
                layImg.setVisibility(View.GONE);
                pic.setVisibility(View.VISIBLE);
                break;
            case "2":
                qty.setText("");
                more.setText("");
                //  notes.setText("");
                layImg.setVisibility(View.GONE);
                pic.setVisibility(View.VISIBLE);
                break;
            case "3":
                qty.setText("");
                more.setText("");
                //  notes.setText("");
                if (deliverySubID.equals("25") || deliverySubID.equals("26")
                        || deliverySubID.equals("28") || deliverySubID.equals("29")
                        || deliverySubID.equals("30")) {
                    weight.setText("");
                    length.setText("");
                    width.setText("");
                    height.setText("");
                }
                layImg.setVisibility(View.GONE);
                pic.setVisibility(View.VISIBLE);
                break;
            case "4":
                qty.setText("");
                animalCarrier.setText("");
                //  animalName.setText("");
                breed.setText("");
                animalWeight.setText("");
                currentVaccination.setText("");
                more.setText("");
                //  notes.setText("");
                layImg.setVisibility(View.GONE);
                pic.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showMessage(String message) {
        messageDialog(getActivity(), "Alert!", message);
    }

    public void addShipment() {
        String itemWeight, itemQty;
        if (deliveryID.equals("4")) {
            itemWeight = animalWeight.getText().toString();
            itemQty = qty.getText().toString();
        } else {
            itemQty = qty.getText().toString();
            itemWeight = weight.getText().toString();
        }
        session.saveShipMent(deliveryID, deliverySubID, totalQty.getText().toString(),
                itemWeight, length.getText().toString(),
                width.getText().toString(), height.getText().toString(),
                stackable.getText().toString(), hazardous.getText().toString(),
                more.getText().toString(), animalCarrier.getText().toString(),
                "", breed.getText().toString(),
                currentVaccination.getText().toString(), notes.getText().toString()
                , deliverySubSubID, jsonArray.toString());

        PreviewField.setDeliveryName(delivry.getText().toString());
        PreviewField.setDeliverySubType(deliveryType.getText().toString());
        if (!deliverySubSubID.contentEquals(""))
            PreviewField.setDeliverySubSubType(animalType.getText().toString());
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

    // Fright Delivery Items
    private void saveFrightItems(String id, String itemQty, String weight, String length, String width,
                                 String height, String stackable, String hazardous, String hazadourscodetype, String photo) {

        Log.v("currentPos:", "currentPos:" + currentPos);
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", id);
            obj.put("item_quantity", itemQty);
            obj.put("item_weight", weight);
            obj.put("item_length", length);
            obj.put("item_width", width);
            obj.put("item_height", height);
            obj.put("item_stackable", stackable);
            obj.put("item_hazardous", hazardous);
            if (hazardous.equalsIgnoreCase("1"))
                obj.put("hazardous_goods_code_type", hazadourscodetype);
            obj.put("item_more_details", "");
            obj.put("item_photo", photo);
            jsonArray.put(currentPos, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("save", jsonArray.toString());
    }

    //Appliance Delivery Items
    private void saveApplianceItems(String id, String itemQty, String more, String photo) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", id);
            obj.put("item_quantity", itemQty);
            obj.put("item_more_details", more);
            obj.put("item_photo", photo);
            //obj.put("item_more_details", "");
            jsonArray.put(currentPos, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("save", jsonArray.toString());
    }

    //Vehicle  Delivery Items
    private void saveVehicleItems(String id, String itemQty, String weight, String length, String width,
                                  String height, String more, String photo) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", id);
            obj.put("item_quantity", itemQty);
            if (deliverySubID.equals("25") || deliverySubID.equals("26")
                    || deliverySubID.equals("28") || deliverySubID.equals("29")
                    || deliverySubID.equals("30")) {
                obj.put("item_weight", weight);
                obj.put("item_length", length);
                obj.put("item_width", width);
                obj.put("item_height", height);
            }
            obj.put("item_more_details", more);
            obj.put("item_photo", photo);
            jsonArray.put(currentPos, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("save", jsonArray.toString());
    }

    //for Animal
    private void saveAnimalItems(String id, String itemQty, String animalCarrier, String name, String breed, String width,
                                 String vaccination, String more, String photo) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", id);
            obj.put("item_quantity", itemQty);
            if (deliverySubID != null && deliverySubID.equals("16"))
                obj.put("item_animal_carrier", animalCarrier);
            //  obj.put("item_animal_name", name);
            obj.put("item_animal_breed", breed);
            obj.put("item_weight", width);
            obj.put("item_current_vaccinations", vaccination);
            obj.put("item_more_details", more);
            obj.put("item_photo", photo);
            jsonArray.put(currentPos, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("save", jsonArray.toString());
    }

    private void updateUI() {
        try {
            int pos = currentPos + 1;
            mItemDetailTxt.setText("ITEM " + pos);
        } catch (Exception e) {
            e.toString();
        }
        dot1.setBackgroundResource(R.drawable.dotblue);
        dot2.setBackgroundResource(R.drawable.active_dotblue);
        switch (deliveryID) {
            case "1":
                layMore.setVisibility(View.GONE);
                layAnimal.setVisibility(View.GONE);
                layTotalqty.setVisibility(View.VISIBLE);
                layQty.setVisibility(View.VISIBLE);
                layHWLW.setVisibility(View.VISIBLE);
                layStackHaz.setVisibility(View.VISIBLE);
                layPhoto.setVisibility(View.VISIBLE);
                break;
            case "2":
                layHWLW.setVisibility(View.GONE);
                layAnimal.setVisibility(View.GONE);
                layMore.setVisibility(View.VISIBLE);
                layQty.setVisibility(View.VISIBLE);
                layPhoto.setVisibility(View.VISIBLE);

                break;
            case "3":
                layHWLW.setVisibility(View.GONE);
                layAnimal.setVisibility(View.GONE);
                layMore.setVisibility(View.VISIBLE);
                layQty.setVisibility(View.VISIBLE);
                layPhoto.setVisibility(View.VISIBLE);

                if (deliverySubID.equals("25") || deliverySubID.equals("26")
                        || deliverySubID.equals("28") || deliverySubID.equals("29")
                        || deliverySubID.equals("30")) {
                    layHWLW.setVisibility(View.VISIBLE);
                } else layHWLW.setVisibility(View.GONE);

                break;
            case "4":
                layHWLW.setVisibility(View.GONE);
                layQty.setVisibility(View.VISIBLE);
                layAnimal.setVisibility(View.VISIBLE);
                layMore.setVisibility(View.VISIBLE);
                layPhoto.setVisibility(View.VISIBLE);

                break;
        }
    }

    private boolean isValid() {
        switch (deliveryID) {
            case "1":
                if (weight.getText().toString().trim().length() == 0
                        && length.getText().toString().trim().length() == 0 && width.getText().toString().trim().length() == 0
                        && height.getText().toString().trim().length() == 0 && stackable.getText().toString().trim().length() == 0
                        && hazardous.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.completeallfield));
                    return false;
                }
              /*  if (qty.getText().toString().trim().length() == 0) {
                    showMessage("Please enter item qty.");
                    return false;
                } else*/
                if (weight.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.item_weight));
                    return false;
                } else if (!checkZero(weight.getText().toString())) {
                    showMessage(getActivity().getResources().getString(R.string.wgreaterzero));
                    return false;
                } else if (length.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.itemlength));
                    return false;
                } else if (!checkZero(length.getText().toString())) {
                    showMessage(getActivity().getResources().getString(R.string.lgreaterzero));
                    return false;
                } else if (width.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.itemwidh));
                    return false;
                } else if (!checkZero(width.getText().toString())) {
                    showMessage(getActivity().getResources().getString(R.string.widthgreaterzero));
                    return false;
                } else if (height.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.itemheight));
                    return false;
                } else if (!checkZero(height.getText().toString())) {
                    showMessage(getActivity().getResources().getString(R.string.heightgreaterzero));
                    return false;
                } else if (stackable.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.itemstackable));
                    return false;
                } else if (hazardous.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.itemhazardous));
                    return false;
                } else if (hazardous.getText().toString().trim().equalsIgnoreCase("yes")) {
                    if (hazadours_good_value.getText().toString().trim().length() == 0) {
                        showMessage(getActivity().getResources().getString(R.string.hazardousgoodtype));
                        return false;
                    }
                }
                if (deliverySubID.equals("18")) {
                    if (notes.getText().toString().trim().length() == 0) {
                        showMessage(getActivity().getResources().getString(R.string.item_detaill));
                        return false;
                    }
                }
                break;
            case "2":
                if (more.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.completeallfield));
                    return false;
                }
             /*   if (qty.getText().toString().trim().length() == 0) {
                    showMessage("Please enter quantity.");
                    return false;
                } else*/
                if (more.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.item_detaill));
                    return false;
                } else if (deliverySubID.equals("10")) {
                    if (notes.getText().toString().trim().length() == 0) {
                        showMessage(getActivity().getResources().getString(R.string.item_detaill));
                        return false;
                    }
                }
                break;
            case "3":
                if (deliverySubID.equals("25") || deliverySubID.equals("26")
                        || deliverySubID.equals("28") || deliverySubID.equals("29")
                        || deliverySubID.equals("30")) {
                    if (weight.getText().toString().trim().length() == 0
                            && length.getText().toString().trim().length() == 0 && width.getText().toString().trim().length() == 0
                            && height.getText().toString().trim().length() == 0) {
                        showMessage(getActivity().getResources().getString(R.string.completeallfield));
                        return false;
                    }
                }
                if (!(deliverySubID.equals("25") || deliverySubID.equals("26")
                        || deliverySubID.equals("28") || deliverySubID.equals("29")
                        || deliverySubID.equals("30"))) {
                    if (more.getText().toString().trim().length() == 0) {
                        showMessage(getActivity().getResources().getString(R.string.completeallfield));
                        return false;
                    }

                }

                /*if (qty.getText().toString().trim().length() == 0) {
                    showMessage("Please enter quantity.");
                    return false;
                } *//*else if (more.getText().toString().trim().length() == 0) {
                    showMessage("Please enter item detail.");
                    return false;
                }*/
                else if (deliverySubID.equals("25") || deliverySubID.equals("26")
                        || deliverySubID.equals("28") || deliverySubID.equals("29")
                        || deliverySubID.equals("30")) {
                    if (weight.getText().toString().trim().length() == 0) {
                        showMessage(getActivity().getResources().getString(R.string.item_weight));
                        return false;
                    } else if (!checkZero(weight.getText().toString())) {
                        showMessage(getActivity().getResources().getString(R.string.wgreaterzero));
                        return false;
                    } else if (length.getText().toString().trim().length() == 0) {
                        showMessage(getActivity().getResources().getString(R.string.itemlength));
                        return false;
                    } else if (!checkZero(length.getText().toString())) {
                        showMessage(getActivity().getResources().getString(R.string.lgreaterzero));
                        return false;
                    } else if (width.getText().toString().trim().length() == 0) {
                        showMessage(getActivity().getResources().getString(R.string.itemwidh));
                        return false;
                    } else if (!checkZero(width.getText().toString())) {
                        showMessage(getActivity().getResources().getString(R.string.widthgreaterzero));
                        return false;
                    } else if (height.getText().toString().trim().length() == 0) {
                        showMessage(getActivity().getResources().getString(R.string.itemheight));
                        return false;
                    } else if (!checkZero(height.getText().toString())) {
                        showMessage(getActivity().getResources().getString(R.string.heightgreaterzero));
                        return false;
                    }
                } else if (deliverySubID.equals("32")) {
                    if (notes.getText().toString().trim().length() == 0) {
                        showMessage(getActivity().getResources().getString(R.string.item_detaill));
                        return false;
                    }
                }
                if (!(deliverySubID.equals("25") || deliverySubID.equals("26")
                        || deliverySubID.equals("28") || deliverySubID.equals("29")
                        || deliverySubID.equals("30"))) {
                    if (more.getText().toString().trim().length() == 0) {
                        showMessage(getActivity().getResources().getString(R.string.item_detaill));
                        return false;
                    }
                }
                break;
            case "4":
                if (animalWeight.getText().toString().trim().length() == 0
                        && currentVaccination.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.completeallfield));
                    return false;
                }

              /*  if (qty.getText().toString().trim().length() == 0) {
                    showMessage("Please enter quantity.");
                    return false;
                }*/
                if (deliverySubID != null && deliverySubID.equals("16")) {
                    if (animalCarrier.getText().toString().trim().length() == 0) {
                        showMessage(getActivity().getResources().getString(R.string.animalcarrier));
                        return false;
                    }
                }
                if (animalWeight.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.animalweight));
                    return false;
                } else if (!checkZero(animalWeight.getText().toString())) {
                    showMessage(getActivity().getResources().getString(R.string.animalwegreaterthezero));
                    return false;
                } else if (currentVaccination.getText().toString().trim().length() == 0) {
                    showMessage(getActivity().getResources().getString(R.string.current_vaccination));
                    return false;
                } /*else if (more.getText().toString().trim().length() == 0) {
                    showMessage("Please enter item more detail.");
                    return false;
                }*/ else if (deliverySubID.equals("33")) {
                    if (notes.getText().toString().trim().length() == 0) {
                        showMessage(getActivity().getResources().getString(R.string.item_detaill));
                        return false;
                    }
                }
                break;
        }
        return true;
    }

    private void showAddProfilePicDialog() {
        PicModeSelectDialogFragment dialogFragment = new PicModeSelectDialogFragment();
        dialogFragment.setiPicModeSelectListener(this);
        dialogFragment.show(getFragmentManager(), "picModeSelector");
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_CODE);
                        } else if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        } else if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_CODE);
                        } else
                            requestOpenCamera();
                    } else
                        requestOpenCamera();
                } else if (items[item].equals("Choose from Library")) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        } else
                            openGallery();
                    } else
                        openGallery();
                } else if (items[item].equals("Cancel"))
                    dialog.dismiss();
            }
        });
        builder.show();
    }

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_CHOOSE_PHOTO = 2;

    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        i.setType("image/*");

        startActivityForResult(i, REQUEST_CHOOSE_PHOTO);
    }

    int CAMERA_PERMISSION_CODE = 115;
    int STORAGE_PERMISSION_CODE = 116;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestOpenCamera();
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    String imagePath = "";

    public void SaveCapturedImagePath(String path) {
        Log.v("path", "path:" + path);
        imagePath = path;
        SharedPreferences myPrefs = getActivity().getSharedPreferences(Config.PREF_NAME, 0);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putString("camera_path", path);
        prefsEditor.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Log.v("imagePath", "imagePath onSaveInstanceState" + imagePath);
        if (!imagePath.equals("")) {
            bundle.putParcelable("img_uri", uriSavedImage);
            bundle.putString("file_name", file_name);
        }
    }

    /*@Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Log.v("imagePath", "imagePath onSaveInstanceState" + imagePath);
        bundle.putBoolean("restoreState", true);
        //if (!imagePath.equals("")) {
        if (uriSavedImage != null) {
            bundle.putParcelable("img_uri", uriSavedImage);
            bundle.putString("file_name", file_name);
            bundle.putString("img_urii", uriSavedImage.toString());
        }
        //  }
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        // if (!imagePath.equals("")) {
        if (bundle != null) {
            if (bundle.containsKey("img_urii")) {
                try {
                    uriSavedImage = Uri.parse(bundle.getString("img_urii"));
                    Log.v("", uriSavedImage.toString());
                } catch (Exception e) {
                    e.toString();
                }
            }
        }
        //   bundle.putParcelable("img_uri", uriSavedImage);
        //    bundle.putString("file_name", file_name);
        // }
    }
*/
    public void requestOpenCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        String iconsStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Temp/";
        sdIconStorageDir = new File(iconsStoragePath);
        if (!sdIconStorageDir.exists()) {
            sdIconStorageDir.mkdirs();
        }
        file_name = System.currentTimeMillis() + ".jpg";
        File file = new File(sdIconStorageDir, file_name);
        file.setWritable(true);
        if (file.exists()) file.delete();
        uriSavedImage = Uri.fromFile(file);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
        //getActivity().startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_UPDATE_PIC) {
                String imagePath = result.getStringExtra(Constants.IntentExtras.IMAGE_PATH);
//                showCroppedImage(imagePath);
                //  SaveCapturedImagePath(imagePath);
                //proceed(imagePath, 0);
                showCroppedImage(imagePath);
            }
        } else {
            if (requestCode == REQUEST_CODE_UPDATE_PIC) {
                try {
                    String errorMsg = result.getStringExtra(ImageCropActivity.ERROR_MSG);
                    //   Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.toString();
                }
            }
        }

    }

    /*   @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == getActivity().RESULT_OK) {
                if (requestCode == REQUEST_TAKE_PHOTO) {
                    try {
                        //  if (data.getData() == null) {
                        String path = uriSavedImage.getPath();
                        //   Object pathobj = data.getExtras().get("data");
                        //  Log.v("", path.toString());
                        // } else {
                        SaveCapturedImagePath(uriSavedImage.getPath());
                        proceed(uriSavedImage.getPath(), 0);
                        // }
                    } catch (Exception e) {
                        e.toString();
                    }
                } else if (requestCode == REQUEST_CHOOSE_PHOTO) {
                    String path = null;
                    try {
                        Uri selectedImage = data.getData();
                        Cursor c = getActivity().getContentResolver().query(
                                Uri.parse(selectedImage.toString()), null, null, null, null);
                        //   c.moveToNext();
                        if (c == null) {
                            path = selectedImage.getPath();
                        } else {
                            //  c.moveToFirst();
                            // int idx = cursor.getColumnIndex(MediaStore.ImagesModel.ImageColumns.DATA);
                            // result = cursor.getString(idx);
                            c.moveToNext();
                            path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
                            c.close();
                        }
                        // String path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
                        //c.close();
                        SaveCapturedImagePath(path);
                        proceed(path, 1);
                    } catch (Exception e) {
                        Log.v("exception", e.toString());
                    }
                }
            } else if (resultCode == getActivity().RESULT_CANCELED) {
            }
        }*/
    private void showCroppedImage(String mImagePath) {
        if (mImagePath != null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(mImagePath);
            SaveCapturedImagePath(mImagePath);
            // img.setImageBitmap(myBitmap);
            // licence_layout.setVisibility(View.VISIBLE);
            //btn_licence_image.setVisibility(View.GONE);
           /* scroll.setVisibility(View.GONE);
            HomeActivity.title.setText("CROP PICTURE");
            cropLay.setVisibility(View.VISIBLE);
            Log.v("selectedImagePath", "selectedImagePath:" + mImagePath);
            cropImageView.setImageUri(Uri.fromFile(new File(mImagePath)));*/
            onDone(mImagePath);
        }
    }

    public void proceed(String selectedImagePath, int type) {
        ExifInterface exif;
        try {
            exif = new ExifInterface(selectedImagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(0);
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        scroll.setVisibility(View.GONE);
        HomeActivity.title.setText("CROP PICTURE");
        cropLay.setVisibility(View.VISIBLE);
        Log.v("selectedImagePath", "selectedImagePath:" + selectedImagePath);
        cropImageView.setImageUri(Uri.fromFile(new File(selectedImagePath)));
    }

    public void onCancel() {
        cropLay.setVisibility(View.GONE);
        scroll.setVisibility(View.VISIBLE);
        HomeActivity.title.setText(getResources().getString(R.string.submitdelivery));
    }

    /*public void onDone() {
        cropLay.setVisibility(View.GONE);
        scroll.setVisibility(View.VISIBLE);
        HomeActivity.title.setText("SUBMIT SHIPMENT");
        final Bitmap croppedImage = cropImageView.getCroppedImage();
        layImg.setVisibility(View.VISIBLE);
        img.setImageBitmap(croppedImage);
        new SavePhotoTask(StorePath.ITEM_IMAGE, croppedImage).execute();

    }*/
    public void onDone(String mImagePath) {
        cropLay.setVisibility(View.GONE);
        scroll.setVisibility(View.VISIBLE);
        HomeActivity.title.setText(getResources().getString(R.string.submitdelivery));
        // final Bitmap croppedImage = cropImageView.getCroppedImage();
        Bitmap myBitmap = BitmapFactory.decodeFile(mImagePath);
        layImg.setVisibility(View.VISIBLE);
        img.setImageBitmap(myBitmap);
        pic.setVisibility(View.GONE);
        new SavePhotoTask(StorePath.ITEM_IMAGE, myBitmap).execute();

    }


    String file_name;
    File sdIconStorageDir;
    private Uri uriSavedImage;

    @Override
    public void onPicModeSelected(String mode) {
        String action = mode.equalsIgnoreCase(Constants.PicModes.CAMERA) ? Constants.IntentExtras.ACTION_CAMERA : Constants.IntentExtras.ACTION_GALLERY;
        actionProfilePic(action);
    }

    public static final int REQUEST_CODE_UPDATE_PIC = 0x1;

    private void actionProfilePic(String action) {
        Intent intent = new Intent(getActivity(), ImageCropActivity.class);
        intent.putExtra("ACTION", action);
        intent.putExtra("rect", "");
        startActivityForResult(intent, REQUEST_CODE_UPDATE_PIC);
    }


    public class SavePhotoTask extends AsyncTask<String, String, String> {
        String fileName;
        Bitmap bitmap;

        public SavePhotoTask(String fileName, Bitmap bitmap) {
            this.fileName = fileName;
            this.bitmap = bitmap;
        }

        @Override
        protected String doInBackground(String... jpeg) {
            String path = storeImage(bitmap, fileName);
            return path;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            img.setTag(result);
        }
    }

    private String storeImage(Bitmap imageData, String filename) {
        File file;
        try {
            if (imageData != null) {
                file = new File(filename);
                if (file.exists())
                    file.delete();
                FileOutputStream fileOutputStream = new FileOutputStream(file.getPath());
                BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

                imageData.compress(Bitmap.CompressFormat.PNG, 90, bos);
                bos.flush();
                bos.close();
            } else
                return "";
        } catch (FileNotFoundException e) {
            e.getMessage();
            return "";
        } catch (IOException e) {
            e.getMessage();
            return "";
        }
        return file.getPath();
    }

    boolean isStackable = false;
    boolean hasAnimalCarrier = false;
    boolean hasCurrentVaccination = false;
    boolean isHazardous = false;

    public void showSelctionDialog(final int type) {
        final Dialog mDialog = new Dialog(getActivity(), R.style.GrabidDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_choose_new);
        mDialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);

        title.setText("        Please select        ");
        ImageView close = (ImageView) mDialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog.isShowing())
                    mDialog.dismiss();
            }
        });

        TextView YES = (TextView) mDialog.findViewById(R.id.yes);
        TextView NO = (TextView) mDialog.findViewById(R.id.no);
        YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == 1) {
                    isStackable = true;
                    stackable.setText("YES");
                } else if (type == 2) {
                    isHazardous = true;
                    hazardous.setText("YES");
                    hazadours_good_title.setVisibility(View.VISIBLE);
                    hazadours_good_value.setVisibility(View.VISIBLE);
                } else if (type == 3) {
                    hasCurrentVaccination = true;
                    currentVaccination.setText("YES");
                } else {
                    hasAnimalCarrier = true;
                    animalCarrier.setText("YES");
                }
                mDialog.dismiss();
            }
        });
        NO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == 1) {
                    isStackable = false;
                    stackable.setText("NO");
                } else if (type == 2) {
                    isHazardous = false;
                    hazardous.setText("NO");
                    hazadours_good_title.setVisibility(View.GONE);
                    hazadours_good_value.setVisibility(View.GONE);
                } else if (type == 3) {
                    hasCurrentVaccination = false;
                    currentVaccination.setText("NO");
                } else {
                    hasAnimalCarrier = false;
                    animalCarrier.setText("NO");
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
        ;
    }

}
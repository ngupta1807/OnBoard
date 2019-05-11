package com.grabid.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grabid.R;
import com.grabid.activities.BarCodeScanner;
import com.grabid.activities.HomeActivity;
import com.grabid.adapters.BarCodeAdapter;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by graycell on 6/11/17.
 */

public class Images extends Fragment implements View.OnClickListener, AsyncTaskCompleteListener {
    String imagePath = "";
    ProgressDialog dialog;
    SessionManager sessionManager;
    String encoded_image = null;
    TextView mAddphotos, mConfirm, mCancel;
    ArrayList<String> captureBarcode = new ArrayList<>();
    ArrayList<String> mBarcodes = new ArrayList<>();
    ListView mBarcode;
    BarCodeAdapter mAdapter;
    int type;
    String[] permissionsRequired = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA};
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 218;
    private boolean sentToSettings = false;
    int REQUEST_PERMISSION_SETTING = 203;
    Delivery deliveryData;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.images, null);
        init(view);
        return view;
    }

    public void init(View view) {
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.title.setText(getResources().getString(R.string.getphotos));
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        HomeActivity.markread.setVisibility(View.GONE);
        sessionManager = new SessionManager(getActivity());
        mAddphotos = (TextView) view.findViewById(R.id.addphotos);
        mConfirm = (TextView) view.findViewById(R.id.confirm);
        mCancel = (TextView) view.findViewById(R.id.cancel);
        mBarcode = (ListView) view.findViewById(R.id.barcodeList);
        mBarcode.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mBarcode.setSmoothScrollbarEnabled(true);
        mAdapter = new BarCodeAdapter(getActivity(), captureBarcode, Images.this);
        mBarcode.setSmoothScrollbarEnabled(true);
        mBarcode.setAdapter(mAdapter);
        mCancel.setOnClickListener(this);
        mAddphotos.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        if (getArguments().containsKey("data")) {
            HashMap<String, Delivery> map = (HashMap<String, Delivery>) getArguments().getSerializable("data");
            deliveryData = map.get("data");
        }
       /* if (getArguments().getString("delivery_status").equals("1")) {
            mConfirm.setText("Confirm Delivery Pick up");
        } else
            mConfirm.setText("Confirm Delivery Drop Off");*/
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mBarcode.setSelection(mAdapter.getCount() - 1);
            }
        });

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void CameraGallery() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity(), permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissionsRequired[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissionsRequired[1])
                        ) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getActivity().getResources().getString(R.string.cameraandphoto));
                    builder.setMessage(getActivity().getResources().getString(R.string.sharephototochoose));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(getActivity(), permissionsRequired, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    });
                    builder.setNegativeButton("Don’t Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else if (new SessionManager(getActivity()).getCamera()) {
                    //Previously Permission Request was cancelled with 'Dont Ask Again',
                    // Redirect to Settings after showing Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getActivity().getResources().getString(R.string.cameraandphoto));
                    builder.setMessage(getActivity().getResources().getString(R.string.sharephototochoose));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            sentToSettings = true;
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.gotopermissionandgrant), Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNegativeButton("Don’t Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    //just request the permission
                    ActivityCompat.requestPermissions(getActivity(), permissionsRequired, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
                new SessionManager(getActivity()).setCamera(true);
            } else {
                Intent intent = new Intent(getActivity(), BarCodeScanner.class);
                startActivityForResult(intent, 1);

            }
        } else {
            Intent intent = new Intent(getActivity(), BarCodeScanner.class);
            startActivityForResult(intent, 1);
        }
    }

    private void showSignature() {
        String backStateName = this.getClass().getName();
        Bundle bundle = new Bundle();
        Fragment fragment = new Signature();
        //Added by VK
        if (getArguments().containsKey("allocateDriverID"))
            bundle.putString("allocateDriverID", getArguments().getString("allocateDriverID"));
        //VK end
        if (getArguments().containsKey("delivery_id")) {
            bundle.putString("delivery_id", getArguments().getString("delivery_id"));
            if (getArguments().getString("delivery_status").equals("1"))
                bundle.putString("contact_person", getArguments().getString("contact_person"));
            else
                bundle.putString("contact_person", getArguments().getString("contact_person"));
            bundle.putString("delivery_status", getArguments().getString("delivery_status"));
            fragment.setArguments(bundle);
            getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addphotos:
                if (mBarcodes.size() < Integer.parseInt(deliveryData.getQty())) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        CameraGallery();
                    } else {
                        Intent intent = new Intent(getActivity(), BarCodeScanner.class);
                        startActivityForResult(intent, 1);
                    }
                } else {
//                    AlertManager.messageDialog(getActivity(), "Alert!", "Barcodes should be equal to delivery item quantity" + " " + mBarcodes.size() + ".");
                    AlertManager.messageDialog(getActivity(), "Alert!", "No further barcodes to capture. Number of barcodes captured equals number of delivery items.");
                }
                break;
            case R.id.confirm:
                if (mBarcodes.size() > 0) {
                    StringBuilder commaSepValueBuilder = new StringBuilder();
                    for (int i = 0; i < mBarcodes.size(); i++) {
                        commaSepValueBuilder.append(mBarcodes.get(i));
                        if (i != mBarcodes.size() - 1) {
                            commaSepValueBuilder.append(", ");
                        }
                    }
                    sendPhoto(getArguments().getString("delivery_id"), getArguments().getString("delivery_status").equals("1") ? "1" : "2", commaSepValueBuilder.toString());
                } else
                    sendPhoto(getArguments().getString("delivery_id"), getArguments().getString("delivery_status").equals("1") ? "1" : "2", "");
                // showSignature();
                break;
            case R.id.cancel:
                showSignature();
                // getActivity().getFragmentManager().popBackStack();
                break;

        }
    }

    public void sendPhoto(String deliveryid, String docType, String doc_name) {
        type = 2;
        String url = Config.SERVER_URL + Config.DELIVERY_DOCS;
        HashMap<String, String> params = new HashMap<>();
        params.put(Keys.DELIVERY_ID, deliveryid);
        params.put(Keys.DOCTYPE, docType);
        params.put(Keys.DOCNAME, doc_name);
        Log.d(Config.TAG, params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall apiCall = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            apiCall.execute(url, sessionManager.getToken());
        } else {
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                CameraGallery();
            }
        } else if (requestCode == 1 && resultCode == -1) {
            try {
                String val = result.getStringExtra("value");
                // Toast.makeText(getActivity(), val, Toast.LENGTH_SHORT).show();
                Log.v("scanresult", val);
                mBarcodes.add(val);
                getPhotos(val);
            } catch (Exception e) {
                e.toString();
            }
        }
    }

    public void removeBarCode(int id) {
        try {
            mBarcodes.remove(id);
        } catch (Exception e) {
            e.toString();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                boolean allgranted = false;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        allgranted = true;
                    } else {
                        allgranted = false;
                        break;
                    }
                }

                if (allgranted) {
                    Intent intent = new Intent(getActivity(), BarCodeScanner.class);
                    startActivityForResult(intent, 1);
                }
                /*if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    proceedToCrop(null);
                }*/
                else {
                    Toast.makeText(getActivity(), "No permission granted to access the external storage.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void getPhotos(String val) {
        type = 1;
        String url = Config.SERVER_URL + Config.GET_BARCODES + val;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall api = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            api.execute(url, sessionManager.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    public void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                if (type == 1) {
                    captureBarcode.add(outJson.optString(Config.DATA));
                    mAdapter.notifyDataSetChanged();
                } else if (type == 2) {
                    showSignature();
                }
            } else if (outJson.getInt(Config.STATUS) == APIStatus.UNPROCESSABLE) {
                if (outJson.getString(Config.MESSAGE).equals(""))
                    showMessage("Error", outJson.getJSONArray(Config.DATA).getJSONObject(0).getString(Config.MESSAGE));
                else
                    showMessage("Error", outJson.getString(Config.MESSAGE));
            } else {
                showMessage("Error", getResources().getString(R.string.no_response));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String title, String message) {
        AlertManager.messageDialog(getActivity(), title, message);
    }


}

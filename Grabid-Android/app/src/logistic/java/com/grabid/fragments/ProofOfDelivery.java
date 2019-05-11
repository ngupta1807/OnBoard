package com.grabid.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.adapters.PodAdapter;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.BackPressed;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.imageCrop.Constants;
import com.grabid.imageCrop.ImageActivity;
import com.grabid.imageCrop.PicModeSelectDialogFragment;
import com.grabid.util.FileOperation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by graycell on 15/12/17.
 */

public class ProofOfDelivery extends Fragment implements View.OnClickListener, PicModeSelectDialogFragment.IPicModeSelectListener, AsyncTaskCompleteListener, BackPressed {
    String imagePath = "";
    ProgressDialog dialog;
    SessionManager sessionManager;
    String encoded_image = null;
    //   ImageView mImage;
    TextView mAddphotos, mConfirm, mCancel;
    String deliveryId, allocateDriverID = "";
    ArrayList<String> mImages = new ArrayList<>();
    ListView mPod;
    PodAdapter mAdapter;
    String backStack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.proofofdelivery, null);
        if (getArguments().containsKey("delivery_id")) {
            deliveryId = getArguments().getString("delivery_id");
            if (getArguments().containsKey("backStack"))
                backStack = getArguments().getString("backStack");
        }
        // Added by VK
        if (getArguments().containsKey("allocateDriverID"))
            allocateDriverID = getArguments().getString("allocateDriverID");
        //VK end
        init(view);
        return view;
    }

    public void SaveCapturedImagePath(String path) {
        imagePath = path;
        Bitmap myBitmap = BitmapFactory.decodeFile(path);
        // mImage.setImageBitmap(myBitmap);
        // mImage.setVisibility(View.VISIBLE);
        mImages.add(imagePath);
        mAdapter.notifyDataSetChanged();
    }

    public void init(View view) {
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.title.setText(getResources().getString(R.string.podtitle));
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        HomeActivity.markread.setVisibility(View.GONE);
        sessionManager = new SessionManager(getActivity());
        mAddphotos = (TextView) view.findViewById(R.id.addphotos);
        mConfirm = (TextView) view.findViewById(R.id.confirm);
        // mImage = (ImageView) view.findViewById(R.id.image);
        mPod = (ListView) view.findViewById(R.id.podList);
        mPod.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mPod.setSmoothScrollbarEnabled(true);
        mAdapter = new PodAdapter(getActivity(), mImages);
        mPod.setSmoothScrollbarEnabled(true);
        mPod.setAdapter(mAdapter);
        mCancel = (TextView) view.findViewById(R.id.cancel);
        mCancel.setOnClickListener(this);
        mAddphotos.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mPod.setSelection(mAdapter.getCount() - 1);
            }
        });
       /* HomeActivity.navDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deliveryId != null && backStack != null) {
                    if (backStack.contentEquals("list")) {
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        intent.putExtra("signaturetype", "1");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity().finish();

                    } else {
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        intent.putExtra("delivery_id", deliveryId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity().finish();
                    }
                } else {
                    startActivity(new Intent(getActivity(), HomeActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    getActivity().finish();
                }

            }
        });*/

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addphotos:
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US).format(new Date());
                ImageActivity.TEMP_PHOTO_FILE_NAME = "pod" + timeStamp + ".jpg";
                showAddProfilePicDialog();
                break;
            case R.id.confirm:
                if (mImages.size() > 0)
                    AsyncImageBase64();
                else
                    CompleteDelivery(deliveryId, "");
                break;
            case R.id.cancel:
                CompleteDelivery(deliveryId, "");
                break;

        }
    }

    public void CompleteDelivery(String deliveryID, String doc_name) {
        String url;
        url = Config.SERVER_URL + Config.DELIVERY_POD;
        HashMap<String, String> params = new HashMap<>();
        params.put(Keys.DELIVERY_ID, deliveryID);
        params.put("doc_name", doc_name);
        if (Internet.hasInternet(getActivity())) {
            RestAPICall apiCall = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            apiCall.execute(url, sessionManager.getToken());
        } else {
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
        }
    }

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
                    try {
                        for (int i = 0; i < mImages.size(); i++) {
                            encoded_image = FileOperation.encodeFileToBase64Binary(mImages.get(i));
                            Log.v("", encoded_image.toString());
                            JSONObject Jobj = new JSONObject();
                            Jobj.put("item_image", encoded_image);
                            array.put(i, Jobj);
                        }
                    } catch (Exception ex) {
                        ex.toString();
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
                CompleteDelivery(deliveryId, aVoid.toString());
            }
        }.execute();
    }

    private void showAddProfilePicDialog() {
        PicModeSelectDialogFragment dialogFragment = new PicModeSelectDialogFragment();
        dialogFragment.setiPicModeSelectListener(this);
        dialogFragment.show(getFragmentManager(), "picModeSelector");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_UPDATE_PIC) {
                String imagePath = result.getStringExtra(Constants.IntentExtras.IMAGE_PATH);
                showCroppedImage(imagePath);
            }
        } else {
            if (requestCode == REQUEST_CODE_UPDATE_PIC) {
                String errorMsg = result.getStringExtra(ImageActivity.ERROR_MSG);
                // Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showCroppedImage(String mImagePath) {
        if (mImagePath != null) {
            SaveCapturedImagePath(mImagePath);
        }
    }

    @Override
    public void onPicModeSelected(String mode) {
        String action = mode.equalsIgnoreCase(Constants.PicModes.CAMERA) ? Constants.IntentExtras.ACTION_CAMERA : Constants.IntentExtras.ACTION_GALLERY;
        actionProfilePic(action);
    }

    public static final int REQUEST_CODE_UPDATE_PIC = 0x1;

    private void actionProfilePic(String action) {
        Intent intent = new Intent(getActivity(), ImageActivity.class);
        intent.putExtra("ACTION", action);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_PIC);
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void showSuccessMessage(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Success!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //Added by VK
                if (allocateDriverID.equalsIgnoreCase(sessionManager.getUserDetails().getId())) {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra("signaturetype", "1");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                //VK end
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra("addfeedbackval", deliveryId);
                    intent.putExtra("incoming_type", "driver");
                    intent.putExtra("backstack", "listingpage");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        builder.show();

    }

    public void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                showSuccessMessage(outJson.getString(Config.MESSAGE));
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

    @Override
    public boolean onBackPressed() {
        if (deliveryId != null && backStack != null) {
            if (backStack.contentEquals("list")) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("signaturetype", "1");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
                return true;

            } else {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("delivery_id", deliveryId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
                return true;
            }
        } else {
            startActivity(new Intent(getActivity(), HomeActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            getActivity().finish();
            return true;
        }
        //return true;
    }
}

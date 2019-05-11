package com.grabid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.grabid.common.BackPressed;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.util.StorePath;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by vinod on 11/21/2016.
 */
public class Signature extends Fragment implements AsyncTaskCompleteListener, View.OnClickListener, BackPressed {
    View view;
    signature mSignature;
    Bitmap bitmap;
    File file;
    Dialog dialog;
    TextView getSign, mClear, mGetSign, mCancel, to, submit, contact, title;
    RelativeLayout mContent;
    ImageView sign;
    SessionManager session;
    int type;
    String deliveryID, deliveryStatus, receiverName;
    String savedPath = "";
    TextView dateTime;
    String[] permissionsRequired = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA};
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 218;
    private boolean sentToSettings = false;
    int REQUEST_PERMISSION_SETTING = 203;
    String allocateDriverID = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        View view = inflater.inflate(R.layout.take_signature, null);
        session = new SessionManager(getActivity());
        if (getArguments().containsKey("allocateDriverID"))
            allocateDriverID = getArguments().getString("allocateDriverID");
        getSign = (TextView) view.findViewById(R.id.signature);
        title = (TextView) view.findViewById(R.id.title);
        contact = (TextView) view.findViewById(R.id.contact);
        to = (TextView) view.findViewById(R.id.to);
        submit = (TextView) view.findViewById(R.id.submit);
        submit.setVisibility(View.GONE);
        sign = (ImageView) view.findViewById(R.id.sign);
        file = new File(StorePath.USER_SIGN);
        if (!file.exists()) {
            file.mkdir();
        }

        getSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    CameraGallery();

                } else {
                    dialog_action();
                }


            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showReceiverDialog();
                submitSign();
            }
        });

        if (getArguments().containsKey("delivery_id")) {
            to.setText(getArguments().getString("contact_person"));
            deliveryID = getArguments().getString("delivery_id");
            deliveryStatus = getArguments().getString("delivery_status");
            Log.d("del_status", deliveryStatus);
            if (deliveryStatus.equals("1")) {
                type = 1;
//                title.setText(getResources().getString(R.string.title_pu));
                HomeActivity.title.setText(getResources().getString(R.string.p_detail));
                title.setText(getResources().getString(R.string.title_pending));
                contact.setText(getResources().getString(R.string.contact_pu));
            } else {
                type = 2;
                HomeActivity.title.setText(getResources().getString(R.string.c_detail));
                title.setText(getResources().getString(R.string.title_do));
                contact.setText(getResources().getString(R.string.contact_do));
            }
        }


        return view;
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
                    builder.setTitle("GRABiD Logistics would like to access your camera and Photos.");
                    builder.setMessage("GRABiD Logistics will only upload and share the photo you choose.");
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
                    builder.setTitle("GRABiD Logistics would like to access your camera and Photos.");
                    builder.setMessage("GRABiD Logistics will only upload and share the photo you choose.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            sentToSettings = true;
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            Toast.makeText(getActivity(), "Go to Permissions to Grant Camera and Storage", Toast.LENGTH_LONG).show();
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
                dialog_action();

            }
        } else {
            dialog_action();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                CameraGallery();
            }
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
                    dialog_action();
                }
                /*if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    proceedToCrop(null);
                }*/
                else {
                    Toast.makeText(getActivity(), "No permission granted to access the external storage", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onTaskComplete(String result) {
        Log.d(Config.TAG, result);
        handleResponse(result);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public boolean onBackPressed() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.putExtra("delivery_id", deliveryID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
        return true;
    }

    public class signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();
        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public String save(View v, String StoredPath) {
            File file = null;
            Log.v("StoredPath", "StoredPath: " + StoredPath);
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                File checkDir = new File(StoredPath);
                if (!checkDir.exists())
                    checkDir.mkdir();
                file = new File(StoredPath, "usersign.png");
                Log.v("path:.", "path:." + file.getAbsolutePath());
                if (file.exists()) file.delete();
                FileOutputStream mFileOutStream = new FileOutputStream(file);
                //FileOutputStream mFileOutStream = new FileOutputStream(StoredPath );
                v.draw(canvas);
                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }
            return file.getAbsolutePath();
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {
            Log.v("log_tag", string);
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    // Function for Digital Signature
    public void dialog_action() {
        // Dialog Function
        dialog = new Dialog(getActivity());
        // Removing the features of Normal Dialogs
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_signature);
        dialog.setCancelable(true);
        mContent = (RelativeLayout) dialog.findViewById(R.id.linearLayout);
        mSignature = new signature(getActivity(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        final View viewlayout = getActivity().getLayoutInflater().inflate(R.layout.datetime, null);
        RelativeLayout.LayoutParams layoutParams = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, 0, 0, 10);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        viewlayout.setLayoutParams(layoutParams);
        dateTime = (TextView) viewlayout.findViewById(R.id.date_time_set);
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mContent.addView(viewlayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = (TextView) dialog.findViewById(R.id.clear);
        mGetSign = (TextView) dialog.findViewById(R.id.getsign);
        mGetSign.setEnabled(false);
        mCancel = (TextView) dialog.findViewById(R.id.cancel);
        view = mContent;

        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });

        mGetSign.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.v("log_tag", "Panel Saved");
               /* view.setDrawingCacheEnabled(true);
                savedPath = mSignature.save(view, StorePath.DIRECTORY);
                dialog.dismiss();
                Toast.makeText(getActivity(), "Successfully Saved", Toast.LENGTH_SHORT).show();
                setSign(savedPath);
                submit.setVisibility(View.VISIBLE);*/
                showReceiverDialog();
                // Calling the same class
                //getActivity().recreate();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Canceled");
                dialog.dismiss();
                // Calling the same class
                //getActivity().recreate();
            }
        });
        dialog.show();
    }

    private void setSign(String path) {
        sign.setImageBitmap(BitmapFactory.decodeFile(path));
    }

    private void submitSign() {
        String url;
        if (type == 1)
            url = Config.SERVER_URL + Config.DELIVERIES + Config.DELIVERY_STATUS_IN_TRANSIT;
        else
            url = Config.SERVER_URL + Config.DELIVERIES + Config.DELIVERY_STATUS_IN_COMPLETE;
        HashMap<String, String> params = new HashMap<>();
        params.put(Keys.DELIVERY_ID, deliveryID);
        if (type == 1) {
            params.put(Keys.FROM_PICKUP_NAME, receiverName);
            String userSign = "";
            try {
                userSign = encodeFileToBase64Binary(savedPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            params.put(Keys.FROM_PICKUP_SIGN, userSign);
        } else {
            params.put(Keys.RECEIVER_NAME, receiverName);
            String userSign = "";
            try {
                userSign = encodeFileToBase64Binary(savedPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            params.put(Keys.RECEIVER_SIGN, userSign);
        }
        Log.d(Config.TAG, url);
        Log.d(Config.TAG, params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall apiCall = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            apiCall.execute(url, session.getToken());
        } else {
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
        }
    }

    private String encodeFileToBase64Binary(String path) throws IOException, FileNotFoundException {
        File file = new File(path);
        byte[] bytes = loadFile(file);
        byte[] encoded = org.apache.commons.codec.binary.Base64.encodeBase64(bytes);
        String encodedString = new String(encoded);
        return encodedString;
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }

    private void showReceiverDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.dialog_recieve, null);
        final EditText subEditText = (EditText) subView.findViewById(R.id.bid_price);
        TextView proceed = (TextView) subView.findViewById(R.id.proceed);
        TextView cancel = (TextView) subView.findViewById(R.id.cancel);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(subView);
        final AlertDialog alertDialog = builder.create();
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vieww) {
                if (subEditText.getText().toString().length() > 0) {
                    alertDialog.dismiss();
                    receiverName = subEditText.getText().toString();
                    /*final View viewlayout = getActivity().getLayoutInflater().inflate(R.layout.datetime, null);
                    RelativeLayout.LayoutParams layoutParams = new
                            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 0, 0, 10);
                    viewlayout.setLayoutParams(layoutParams);
                    TextView dateTime = (TextView) viewlayout.findViewById(R.id.date_time_set);
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
                        String currentDateandTime = sdf.format(new Date());
                        dateTime.setText(currentDateandTime);
                    } catch (Exception e) {
                        e.toString();
                    }*/
                    //  mContent.addView(viewlayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    //
                    // mContent.addView(viewlayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    //  mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    String currentDateandTime = "";
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.US);
                        currentDateandTime = sdf.format(new Date());

                    } catch (Exception e) {
                        e.toString();
                    }
                    dateTime.setText("By: " + receiverName + ", " + currentDateandTime);
                    // view = mContent;
                    view.setDrawingCacheEnabled(true);
                    savedPath = mSignature.save(view, StorePath.DIRECTORY);
                    dialog.dismiss();
                    //Toast.makeText(getActivity(), "Successfully Saved", Toast.LENGTH_SHORT).show();
                    setSign(savedPath);
                    submit.setVisibility(View.VISIBLE);
                    //  submitSign();
                } else
                    showMessage("Alert!", "Please enter your name.");
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

    private void showMessage(String title, String message) {
        AlertManager.messageDialog(getActivity(), title, message);
    }

    private void showSuccessMessage(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Success!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //  getActivity().getFragmentManager().popBackStack();
                // getActivity().getFragmentManager().popBackStack();
                //  startActivity(new Intent(getActivity(), HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                //   getActivity().finish();
                dialog.dismiss();
                //Modified by VK
                if (type == 3 && !allocateDriverID.equalsIgnoreCase(session.getUserDetails().getId())) {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra("addfeedbackval", deliveryID);
                    intent.putExtra("incoming_type", "driver");
                    intent.putExtra("backstack", "listingpage");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra("signaturetype", "1");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        builder.show();

    }

    private void showPodMessage(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Success!");
        builder.setMessage(message);
        builder.setNegativeButton("UPLOAD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String backStateName = this.getClass().getName();
                Bundle bundle = new Bundle();
                bundle.putString("delivery_id", getArguments().getString("delivery_id"));
                bundle.putString("backStack", "list");
                // Added by VK
                bundle.putString("allocateDriverID", allocateDriverID);
                //VK end
                Fragment fragment = new ProofOfDelivery();
                fragment.setArguments(bundle);
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();

            }
        });
        builder.setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                CompleteDelivery(deliveryID, "");
            }
        });
        builder.show();

    }

    public void CompleteDelivery(String deliveryID, String doc_name) {
        type = 3;
        String url;
        url = Config.SERVER_URL + Config.DELIVERY_POD;
        HashMap<String, String> params = new HashMap<>();
        params.put(Keys.DELIVERY_ID, deliveryID);
        params.put("doc_name", doc_name);
        if (Internet.hasInternet(getActivity())) {
            RestAPICall apiCall = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            apiCall.execute(url, session.getToken());
        } else {
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
        }
    }

    private void handleResponse(String result) {

        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                if (type == 1 || type == 3)
                    showSuccessMessage(outJson.getString(Config.MESSAGE));
                else if (type == 2)
                    showPodMessage(outJson.getString(Config.MESSAGE));
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
}

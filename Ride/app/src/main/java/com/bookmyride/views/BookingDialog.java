package com.bookmyride.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bookmyride.R;


/**
 * Created by Vinod on 07/01/2017.
 */
public class BookingDialog {
    private ImageView cross;
    private Context mContext;
    private TextView Bt_action, Bt_dismiss;
    private TextView alert_title, alert_message;
    EditText note;
    private RelativeLayout Rl_button;
    private Dialog dialog;
    private View view;
    private boolean isPositiveAvailable = false;
    private boolean isNegativeAvailable = false;

    public BookingDialog(Context context, boolean show, boolean cancellable) {
        this.mContext = context;
        this.crossVisible = show;
        this.cancellable = cancellable;
        //--------Adjusting Dialog width-----
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.85);//fill only 85% of the screen

        view = View.inflate(mContext, R.layout.dialog_note, null);
        dialog = new Dialog(mContext, R.style.rideDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCancelable(cancellable);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        alert_title = (TextView) view.findViewById(R.id.title);
        note = (EditText) view.findViewById(R.id.note);
        alert_message = (TextView) view.findViewById(R.id.msg);
        Bt_action = (TextView) view.findViewById(R.id.now);
        Bt_dismiss = (TextView) view.findViewById(R.id.later);
        cross = (ImageView) view.findViewById(R.id.cross);
        if(crossVisible)
            cross.setVisibility(View.VISIBLE);
        else cross.setVisibility(View.GONE);
        Rl_button = (RelativeLayout) view.findViewById(R.id.lay_bottom);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }
    boolean crossVisible = false;
    boolean cancellable = false;

    public void show() {

        /* Enable or Disable positive Button */
        if (isPositiveAvailable) {
            Bt_action.setVisibility(View.VISIBLE);
        } else {
            Bt_action.setVisibility(View.GONE);
        }

        /* Enable or Disable Negative Button */
        if (isNegativeAvailable) {
            Bt_dismiss.setVisibility(View.VISIBLE);
        } else {
            Bt_dismiss.setVisibility(View.GONE);
        }
        /* Changing color for Button Layout */
        /*if (isPositiveAvailable && isNegativeAvailable) {
            Rl_button.setBackgroundColor(mContext.getResources().getColor(R.color.white_color));
        } else {
            Rl_button.setBackgroundColor(mContext.getResources().getColor(R.color.driver_color));
        }*/
        if(dialog != null) {
            try {
                dialog.show();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void setDialogTitle(String title) {
        alert_title.setText(title);
    }

    public void setDialogMessage(String message) {
        alert_message.setText(message);
    }

    public void setCancelOnTouchOutside(boolean value) {
        dialog.setCanceledOnTouchOutside(value);
    }

    /*Action Button for Dialog*/
    public void setPositiveButton(String text, final View.OnClickListener listener) {
        isPositiveAvailable = true;
        Bt_action.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Bold.ttf"));
        Bt_action.setText(text);
        Bt_action.setOnClickListener(listener);
    }

    public void setNegativeButton(String text, final View.OnClickListener listener) {
        isNegativeAvailable = true;
        Bt_dismiss.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Bold.ttf"));
        Bt_dismiss.setText(text);
        Bt_dismiss.setOnClickListener(listener);
    }
}

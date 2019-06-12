package com.bookmyride.util;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bookmyride.R;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author GT
 */
public class Utils {

    public static Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    public static void showPopup(final Activity ctx, String msg) {
        final Dialog mDialog = new Dialog(ctx, R.style.rideDialog);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_payment);
        TextView title = (TextView) mDialog.findViewById(R.id.txt_msg);
        TextView message = (TextView) mDialog.findViewById(R.id.msg);
        RelativeLayout Rl_ok = (RelativeLayout) mDialog.findViewById(R.id.ok);

        message.setText(msg);
        title.setText("Alert!");
        Rl_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                try {
                    ctx.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mDialog.show();
        /*if(getIntent().hasExtra("end"))
            dismissDialog();*/
    }
}

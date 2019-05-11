package com.grabid.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.grabid.R;

/**
 * Created by vinod on 10/14/2016.
 */
public class BoldTextView extends TextView {
    public BoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public BoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BoldTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
//        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway_Bold.ttf");
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway_SemiBold.ttf");

        this.setTypeface(face);
        setHintTextColor(context.getResources().getColor(R.color.form_title));
    }
}

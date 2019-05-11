package com.grabid.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by vinod on 10/14/2016.
 */
public class SemiBoldTextView extends TextView {
    public SemiBoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SemiBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SemiBoldTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway_SemiBold.ttf");
        this.setTypeface(face);
    }
}

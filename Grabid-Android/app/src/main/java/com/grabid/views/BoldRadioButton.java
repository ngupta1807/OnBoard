package com.grabid.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * Created by vinod on 10/14/2016.
 */
public class BoldRadioButton extends RadioButton {
    public BoldRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public BoldRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public BoldRadioButton(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        //  Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway_Bold.ttf");
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway_SemiBold.ttf");

        this.setTypeface(face);
    }
}

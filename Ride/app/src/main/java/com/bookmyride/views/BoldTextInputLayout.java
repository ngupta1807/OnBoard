package com.bookmyride.views;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;

/**
 * Created by vinod on 10/14/2016.
 */
public class BoldTextInputLayout extends TextInputLayout {
    public BoldTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public BoldTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BoldTextInputLayout(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        this.setTypeface(face);
    }
}

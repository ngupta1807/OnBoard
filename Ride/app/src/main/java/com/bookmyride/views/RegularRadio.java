package com.bookmyride.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * Created by vinod on 10/14/2016.
 */
public class RegularRadio extends RadioButton {
    public RegularRadio(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public RegularRadio(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RegularRadio(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
        this.setTypeface(face);
    }
}

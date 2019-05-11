package com.grabid.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by vinod on 10/14/2016.
 */
public class LightTextView extends TextView {
    public LightTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public LightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LightTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway_Light.ttf");
        this.setTypeface(face);
        //this.getBackground().setColorFilter(context.getResources().getColor(R.color.darkblue), PorterDuff.Mode.SRC_IN);

    }

}

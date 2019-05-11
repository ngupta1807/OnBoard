package com.grabid.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by vinod on 10/14/2016.
 */
public class RegularTextView extends TextView {
    public RegularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public RegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RegularTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        // Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway_Regular.ttf");
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway_SemiBold.ttf");
        this.setTypeface(face);
        //this.getBackground().setColorFilter(context.getResources().getColor(R.color.darkblue), PorterDuff.Mode.SRC_IN);

    }

}

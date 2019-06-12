package com.bookmyride.views;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by vinod on 10/14/2016.
 */
public class NormalEditText extends EditText {
    public NormalEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public NormalEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NormalEditText(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
        TypefaceSpan typefaceText= new HelveticaLightSpan(face);
        SpannableString spannableText = new SpannableString("");
        spannableText.setSpan(typefaceText, 0, spannableText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        this.setText(spannableText);
    }
}

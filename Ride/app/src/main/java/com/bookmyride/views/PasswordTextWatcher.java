package com.bookmyride.views;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by vinod on 1/24/2017.
 */
public class PasswordTextWatcher implements TextWatcher {
    private EditText mEditText;
    Typeface normal;
    Typeface light;
    Context ctx;
    public PasswordTextWatcher(EditText e, Context ctx) {
        this.ctx = ctx;
        mEditText = e;
        normal = Typeface.createFromAsset(ctx.getAssets(), "fonts/Roboto-Light.ttf");
        light = Typeface.createFromAsset(ctx.getAssets(), "fonts/Roboto-Light.ttf");
        mEditText.setTypeface(light);
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //mEditText.setTypeface(Typeface.DEFAULT);
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //mEditText.setTypeface(Typeface.MONOSPACE);
    }

    public void afterTextChanged(Editable s) {
        if(s.length() <= 0){
            mEditText.setTypeface(light);
        } else {
            mEditText.setTypeface(normal);
        }
    }
}
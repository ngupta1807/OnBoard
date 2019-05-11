package com.grabid.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import com.grabid.R;

import java.lang.reflect.Field;

/**
 * Created by vinod on 10/14/2016.
 */
public class BoldEditText extends EditText {
    public BoldEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public BoldEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BoldEditText(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
//        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway_Bold.ttf");
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway_SemiBold.ttf");
        this.setTypeface(face);
        //this.getBackground().setColorFilter(context.getResources().getColor(R.color.darkblue), PorterDuff.Mode.SRC_IN);
        setCursorDrawableColor(this, context.getResources().getColor(R.color.darkblue));
        setHintTextColor(context.getResources().getColor(R.color.form_title));
    }

    public static void setCursorDrawableColor(EditText editText, int color) {
        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);
            Drawable[] drawables = new Drawable[2];
            drawables[0] = editText.getContext().getResources().getDrawable(mCursorDrawableRes);
            drawables[1] = editText.getContext().getResources().getDrawable(mCursorDrawableRes);
            drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);
        } catch (Throwable ignored) {
            ignored.toString();
        }
    }
}

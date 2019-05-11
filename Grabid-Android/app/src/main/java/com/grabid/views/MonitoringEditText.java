package com.grabid.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.grabid.R;
import com.grabid.common.EditTextListener;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by graycell on 27/3/18.
 */

public class MonitoringEditText extends EditText {

    private final Context context;
    ArrayList<EditTextListener> listeners;

    /*
        Just the constructors to create a new EditText...
     */
    public MonitoringEditText(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public MonitoringEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public MonitoringEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init(context);
    }

    /**
     * <p>This is where the "magic" happens.</p>
     * <p>The menu used to cut/copy/paste is a normal ContextMenu, which allows us to
     * overwrite the consuming method and react on the different events.</p>
     *
     * @see <a href="http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/2.3_r1/android/widget/TextView.java#TextView.onTextContextMenuItem%28int%29">Original Implementation</a>
     */
    @Override
    public boolean onTextContextMenuItem(int id) {
        // Do your thing:
        boolean consumed = super.onTextContextMenuItem(id);
        // React:
        switch (id) {
            case android.R.id.cut:
                onTextCut();
                break;
            case android.R.id.paste:
                onTextPaste();
                break;
            case android.R.id.copy:
                onTextCopy();
        }
        return consumed;
    }

    /**
     * Text was cut from this EditText.
     */
    public void onTextCut() {
      //  Toast.makeText(context, "Cut!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Text was copied from this EditText.
     */
    public void onTextCopy() {
     //   Toast.makeText(context, "Copy!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Text was pasted into the EditText.
     */
    public void onTextPaste() {
       // Toast.makeText(context, "Paste!", Toast.LENGTH_SHORT).show();
        for (EditTextListener listener : listeners) {
            listener.onUpdate();
        }
    }

    private void init(Context context) {
        listeners = new ArrayList<>();
        // Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway_Regular.ttf");
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway_SemiBold.ttf");

        this.setTypeface(face);
        //   this.getBackground().setColorFilter(context.getResources().getColor(R.color.darkblue), PorterDuff.Mode.SRC_IN);
        setCursorDrawableColor(this, context.getResources().getColor(R.color.darkblue));
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

    public void addListener(EditTextListener listener) {
        try {
            listeners.add(listener);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
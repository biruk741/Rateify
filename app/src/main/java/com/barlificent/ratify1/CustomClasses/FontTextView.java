package com.barlificent.ratify1.CustomClasses;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by 2015 on 1/5/2018.
 */
public class FontTextView extends TextView {
    private static final String asset = "fonts/avenir.ttf"/*"fonts/avenir.ttf"*/;
    public FontTextView(Context context) {
        super(context);
        setCustomFont(context,asset);
    }
    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, asset);
    }
    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, asset);
    }
    public void setCustomFont(Context c, String asset){
        this.setTypeface(Typeface.createFromAsset(c.getAssets(),asset));
    }
}

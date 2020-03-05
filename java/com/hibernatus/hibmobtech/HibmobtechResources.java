package com.hibernatus.hibmobtech;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by tgo on 01/12/15.
 */
public class HibmobtechResources {
    Typeface hibmobtechFont;

    public HibmobtechResources(Context context) {
        this.hibmobtechFont = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");
    }

    public Typeface getHibmobtechFont() {
        return hibmobtechFont;
    }
}

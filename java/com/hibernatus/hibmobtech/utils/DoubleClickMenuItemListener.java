package com.hibernatus.hibmobtech.utils;

import android.view.MenuItem;

/**
 * Created by Eric on 24/02/2017.
 */

public abstract class DoubleClickMenuItemListener implements MenuItem.OnMenuItemClickListener {

    //private static final long DOUBLE_CLICK_TIME_DELTA = 300; //milliseconds
    private static final long DOUBLE_CLICK_TIME_DELTA = 500; //milliseconds --> 1/2 s
    //private static final long DOUBLE_CLICK_TIME_DELTA = 1000;  //milliseconds --> 1s

    long lastClickTime = 0;

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        long clickTime = System.currentTimeMillis();
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
            onDoubleMenuItemClick(item);
            lastClickTime = 0;
        } else {
            onSingleMenuItemClick(item);
        }
        lastClickTime = clickTime;
        return true;
    }

    public abstract void onSingleMenuItemClick(MenuItem item);
    public abstract void onDoubleMenuItemClick(MenuItem item);
}

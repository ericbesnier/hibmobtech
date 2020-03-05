package com.hibernatus.hibmobtech.model;

import android.util.Log;

/**
 * Created by Eric on 21/01/2016.
 */

public class Cause extends BasicRefEntity {
    public static final String TAG = Cause.class.getSimpleName();

    public static final String MRO_CAUSE_CODE = "mroCauseCode";
    public Cause() {}
    public Cause(String description) {
        super(description);
    }

    public void setId(Object id) {
        Long l = Long.valueOf(id.toString());
        Log.d(TAG, "setId: id=" + l);

        this.id = l.longValue();
    }
}
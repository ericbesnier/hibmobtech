package com.hibernatus.hibmobtech.sparepart;

import android.util.Log;

import com.hibernatus.hibmobtech.model.SparePart;

/**
 * Created by Eric on 15/01/2016.
 */
public class SparePartCurrent {
    public static final String TAG = SparePartCurrent.class.getSimpleName();
    private SparePart sparePartCurrent;

    private SparePartCurrent() {}

    private static SparePartCurrent INSTANCE = new SparePartCurrent();

    public void initSparePartCurrent() {
        Log.i(TAG, "initSparePartCurrent");
        sparePartCurrent = null;
    }

    public boolean isCurrentSparePart() {
        return sparePartCurrent != null;
    }

    public static SparePartCurrent getInstance()
    {
        return INSTANCE;
    }

    public SparePart getSparePartCurrent() {
        Log.i(TAG, "getSparePart");
        if (sparePartCurrent != null) {
            Log.i(TAG, "getSparePart:currentRequest: id="
                    + sparePartCurrent.getId()
                    + " getSparePart.getDescription="
                    + sparePartCurrent.getDescription());
        }
        return sparePartCurrent;
    }

    public void setSparePartCurrent(SparePart sparePartCurrent) {
        Log.i(TAG, "setSparePart");
        this.sparePartCurrent = sparePartCurrent;
        if (sparePartCurrent != null) {
            Log.i(TAG, "setSparePart:currentRequest: id="
                    + this.sparePartCurrent.getId()
                    + " setSparePart.getDescription="
                    + this.sparePartCurrent.getDescription());
        }
    }
}


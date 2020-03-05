package com.hibernatus.hibmobtech.utils;

import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

/**
 * Created by Eric on 16/12/2016.
 */

public class ClickSpan extends ClickableSpan {
    public final String TAG = ClickSpan.class.getSimpleName();

    private OnClickListener mListener;

    public ClickSpan(OnClickListener listener) {
        Log.e(TAG, "Construtor");

        mListener = listener;
    }

    @Override
    public void onClick(View widget) {
        Log.e(TAG, "onClick");

        if (mListener != null) mListener.onClick();
    }

    public interface OnClickListener {
        void onClick();
    }
}

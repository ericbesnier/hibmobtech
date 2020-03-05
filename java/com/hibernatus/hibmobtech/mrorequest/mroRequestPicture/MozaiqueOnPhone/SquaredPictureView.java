package com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.MozaiqueOnPhone;

/**
 * Created by Eric on 20/02/2017.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/** An image view which always remains square with respect to its width. */
public final class SquaredPictureView extends ImageView {
    public SquaredPictureView(Context context) {
        super(context);
    }

    public SquaredPictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}

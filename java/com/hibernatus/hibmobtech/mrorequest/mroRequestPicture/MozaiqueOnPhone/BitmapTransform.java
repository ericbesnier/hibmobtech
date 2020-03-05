package com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.MozaiqueOnPhone;

/**
 * Created by Eric on 20/02/2017.
 */

import android.graphics.Bitmap;
import android.util.Log;

import com.squareup.picasso.Transformation;

// B i t m a p T r a n s f o r m
//
// Transformate the loaded image to avoid OutOfMemoryException
//
// ---------------------------------------------------------------------------


public class BitmapTransform implements Transformation {
    public static final String TAG = BitmapTransform.class.getSimpleName();

    private final int maxWidth;
    private final int maxHeight;

    public BitmapTransform(int maxWidth, int maxHeight) {
        Log.d(TAG, "BitmapTransform");
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        Log.d(TAG, "transform: bitmap.getByteCount()=" + bitmap.getByteCount());

        int targetWidth, targetHeight;
        double aspectRatio;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            targetWidth = maxWidth;
            aspectRatio = (double) bitmap.getHeight() / (double) bitmap.getWidth();
            targetHeight = (int) (targetWidth * aspectRatio);
        } else {
            targetHeight = maxHeight;
            aspectRatio = (double) bitmap.getWidth() / (double) bitmap.getHeight();
            targetWidth = (int) (targetHeight * aspectRatio);
        }

        Bitmap result = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);
        if (result != bitmap) {
            bitmap.recycle();
        }
        Log.d(TAG, "transform: result.getByteCount()=" + result.getByteCount());

        return result;
    }

    @Override
    public String key() {

        String ret = maxWidth + "x" + maxHeight;
        Log.d(TAG, "key=" + ret);
        return ret;
    }
}

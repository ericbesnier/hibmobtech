package com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.ThumbnailBandeau;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory;

import static com.hibernatus.hibmobtech.HibmobtechApplication.NUM_OF_COLUMNS_PER_SCREEN;


/**
 * Created by Eric on 13/01/2017.
 */


public class ThumbnailViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = ThumbnailViewHolder.class.getSimpleName();
    public ImageView imageView;
    Context context;

    public ThumbnailViewHolder(Context context, View itemView) {
        super(itemView);
        Log.d(TAG, "ThumbnailViewHolder");
        this.context = context;
        imageView = (ImageView) itemView.findViewById(R.id.mroRequestThumbnailImageView);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        if (imageView != null){
            imageView.setLayoutParams(new RecyclerView.LayoutParams(width / NUM_OF_COLUMNS_PER_SCREEN,
                    RecyclerView.LayoutParams.MATCH_PARENT));
            imageView.setMinimumHeight(300); //set minimum height of view here
        }
    }

    public void setData(Cursor cursor) {
        byte[] imageByteArray = cursor.getBlob(cursor.getColumnIndex(SQLiteBaseDaoFactory.COL_THUMBNAIL));
        if (imageByteArray != null) {
            Log.d(TAG, "setData: cursor=" + cursor + " image size=" + imageByteArray.length
                    + " bytes (" +  imageByteArray.length / 1024 + " Ko)");
            Bitmap bitmapThumbnail = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
            imageView.setImageBitmap(bitmapThumbnail);
        }
    }
}
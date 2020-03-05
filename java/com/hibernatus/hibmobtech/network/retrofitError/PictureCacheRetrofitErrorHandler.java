package com.hibernatus.hibmobtech.network.retrofitError;


import android.content.Context;
import android.util.Log;

import com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoPictureCache;
import com.hibernatus.hibmobtech.model.Picture;

import retrofit.RetrofitError;

// c l a s s   P i c t u r e C a c h e R e t r o f i t E r r o r H a n d l e r
// ---------------------------------------------------------------------------
public class PictureCacheRetrofitErrorHandler extends RetrofitErrorHandler {
    private static final String TAG = PictureCacheRetrofitErrorHandler.class.getSimpleName();

    protected Picture picture;
    protected Context context;

    public PictureCacheRetrofitErrorHandler(Context context) {
        this.context = context;
    }

    @Override
    public void on500(RetrofitError retrofitError){
        super.on500(retrofitError);
        Log.e(TAG, "on500: Erreur interne du serveur");
        if(picture != null) {
            picture.setResendNumber(picture.getResendNumber() + 1);
            SQLiteBaseDaoPictureCache sqLiteBaseDaoPictureCache = new SQLiteBaseDaoPictureCache(context);
            sqLiteBaseDaoPictureCache.open();
            sqLiteBaseDaoPictureCache.updatePicture(picture);
            sqLiteBaseDaoPictureCache.close();
        }
    }

    public void managePictureRetrofitError(Picture picture, RetrofitError retrofitError) {
        Log.e(TAG, "managePictureRetrofitError");
        this.picture = picture;
        manageRetrofitError(retrofitError);
    }
}


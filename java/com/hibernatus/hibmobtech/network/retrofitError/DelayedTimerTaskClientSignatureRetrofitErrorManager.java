package com.hibernatus.hibmobtech.network.retrofitError;


import android.content.Context;
import android.util.Log;

import com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoSignatureCache;
import com.hibernatus.hibmobtech.model.ClientSignature;

import retrofit.RetrofitError;

// c l a s s   D e l a y e d T i m e r T a s k C l i e n t S i g n a t u r e R e t r o f i t E r r o r M a n a g e r
// -----------------------------------------------------------------------------------------------------------------
public class DelayedTimerTaskClientSignatureRetrofitErrorManager extends RetrofitErrorHandler {
    private static final String TAG = DelayedTimerTaskClientSignatureRetrofitErrorManager.class.getSimpleName();

    protected ClientSignature ClientSignature;
    protected Context context;

    public DelayedTimerTaskClientSignatureRetrofitErrorManager(Context context) {
        this.context = context;
    }

    @Override
    public void on500(RetrofitError retrofitError){
        super.on500(retrofitError);
        Log.d(TAG, "on500");
        if(ClientSignature != null) {
            ClientSignature.setResendNumber(ClientSignature.getResendNumber() + 1);
            SQLiteBaseDaoSignatureCache SQLiteBaseDaoSignatureCache = new SQLiteBaseDaoSignatureCache(context);
            SQLiteBaseDaoSignatureCache.open();
            SQLiteBaseDaoSignatureCache.updateSignature(ClientSignature);
            SQLiteBaseDaoSignatureCache.close();
        }
    }

    public void manageClientSignature(ClientSignature ClientSignature, RetrofitError retrofitError) {
        this.ClientSignature = ClientSignature;
        manageRetrofitError(retrofitError);
    }
}


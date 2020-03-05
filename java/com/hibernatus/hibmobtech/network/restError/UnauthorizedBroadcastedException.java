package com.hibernatus.hibmobtech.network.restError;

import android.content.Context;

import retrofit.RetrofitError;

/**
 * Created by tgo on 02/12/15.
 */
public class UnauthorizedBroadcastedException extends BroadcastedException {
    public static final String TAG = "UnauthorizedBroadcastedException";

    public UnauthorizedBroadcastedException(Context ctx, RetrofitError cause) {
        super(ctx, cause, EXCEPTION_TYPE.UNAUTHORIZED);
        //process unauthorized error - logout user/force to login again?
        // . . .
/*        Response r = cause.getResponse();
        if(r != null) {
            AndroidUtils.showToast(ctx, "Erreur" + r.getStatus() + " réseau ! Autorisation invalide");
        }*/
    }
}
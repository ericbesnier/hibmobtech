package com.hibernatus.hibmobtech.network.restError;

import android.content.Context;

import retrofit.RetrofitError;

/**
 * Created by tgo on 02/12/15.
 */
public class ForbiddenBroadcastedException extends BroadcastedException {
    public static final String TAG = "ForbiddenBroadcastedException";

    public ForbiddenBroadcastedException(Context ctx, RetrofitError cause) {
        super(ctx, cause, EXCEPTION_TYPE.FORBIDDEN);
        //process forbidden error - inform user that (s)he can’t access this content?
        // . . .

    }
}
package com.hibernatus.hibmobtech.network.restError;

import android.content.Context;

import retrofit.RetrofitError;

/**
 * Created by tgo on 02/12/15.
 */
public class InternalServerErrorBroadcastedException extends BroadcastedException {
    public static final String TAG = InternalServerErrorBroadcastedException.class.getSimpleName();

    public InternalServerErrorBroadcastedException(Context ctx, RetrofitError cause) {
        super(ctx, cause, EXCEPTION_TYPE.INTERNAL_SERVER);
        //process internal server error - inform user that server is unable to process request due to internal error?
        // . . .
    }
}
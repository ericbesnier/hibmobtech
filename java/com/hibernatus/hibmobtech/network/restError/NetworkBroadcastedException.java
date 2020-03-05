package com.hibernatus.hibmobtech.network.restError;

import android.content.Context;

import retrofit.RetrofitError;

/**
 * Created by tgo on 02/12/15.
 */
public class NetworkBroadcastedException extends BroadcastedException {
    public static final String TAG = "NetworkBroadcastedException";

    public NetworkBroadcastedException(Context ctx, RetrofitError cause) {
        super(ctx, cause, EXCEPTION_TYPE.NETWORK);
    }
}
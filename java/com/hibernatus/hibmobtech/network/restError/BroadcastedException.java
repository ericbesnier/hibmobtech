package com.hibernatus.hibmobtech.network.restError;

import android.content.Context;
import android.content.Intent;

import retrofit.RetrofitError;

/**
 * Created by tgo on 02/12/15.
 */
public abstract class BroadcastedException extends Throwable {
    public static final String TAG = BroadcastedException.class.getSimpleName();

    public static final String ACTION_BROADCASTED_EXCEPTION = "action_broadcasted_exception";
    public static final String EXTRA_TYPE = "broadcasted_exception_type";
    public static final String EXTRA_MESSAGE = "broadcasted_exception_message";

    public static enum EXCEPTION_TYPE {
        UNKNOWN, NETWORK, UNAUTHORIZED, FORBIDDEN, INTERNAL_SERVER;

        public static EXCEPTION_TYPE getByOrdinal(int o) {
            for(EXCEPTION_TYPE e : EXCEPTION_TYPE.values()) {
                if(e.ordinal() == o) return e;
            }
            return UNKNOWN;
        }
    }

    protected EXCEPTION_TYPE mExceptionType;

    public BroadcastedException(Context ctx, RetrofitError cause, EXCEPTION_TYPE type) {
        super(cause);
        mExceptionType = type;
        Intent bcast = new Intent(ACTION_BROADCASTED_EXCEPTION);
        bcast.putExtra(EXTRA_TYPE, mExceptionType.ordinal());
        bcast.putExtra(EXTRA_MESSAGE, cause.toString());
        ctx.sendBroadcast(bcast);
    }

}
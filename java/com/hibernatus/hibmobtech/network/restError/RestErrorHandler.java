package com.hibernatus.hibmobtech.network.restError;

import android.content.Context;
import android.util.Log;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by tgo on 02/12/15.
 */
public class RestErrorHandler implements ErrorHandler {
    public static final String TAG = RestErrorHandler.class.getSimpleName();

    private Context mContext;

    public RestErrorHandler(Context ctx) {
        mContext = ctx;
    }

    @Override
    public Throwable handleError(RetrofitError cause) {
        Log.e(TAG, "Rest error: " + cause.toString());
        if(cause != null) {
            switch(cause.getKind()) {
                case NETWORK:
                    //AndroidUtils.showToast(context, "Erreur NETWORK");
                    return new NetworkBroadcastedException(mContext, cause);
                default:
                    Response r = cause.getResponse();
                    if(r == null) {
                        return cause;
                    }
                    if (r.getStatus() == 401) {
                        return new UnauthorizedBroadcastedException(mContext, cause);
                    } else if(r.getStatus() == 403) {
                        //AndroidUtils.showToast(context, "Erreur" + r.getStatus() + " réseau ! Limite API quotidienne dépassé");
                        return new ForbiddenBroadcastedException(mContext, cause);
                    } else if(r.getStatus() >= 500) {
                        //AndroidUtils.showToast(context, "Erreur serveur " + r.getStatus());
                        return new InternalServerErrorBroadcastedException(mContext, cause);
                    } else if(r.getStatus() == 404) {
                        //AndroidUtils.showToast(context, "Erreur" + r.getStatus() + " réseau inaccessible !");
                        Log.e(TAG, "error 404");
                        return cause;
                    }
            }
        }
        return cause;
    }
}

package com.hibernatus.hibmobtech.network;

import android.util.Log;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by tgo on 22/11/15.
 */
public class LoggingInterceptor implements Interceptor, ClockHeader {

    static final String TAG = LoggingInterceptor.class.getSimpleName();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();

        Response response = chain.proceed(request);

        long elapsed = System.nanoTime() - t1;

        Log.i(TAG, String.format("Received response for %s in %.3fms",
                response.request().url(), elapsed / 1e6d));

        return response.newBuilder()
                .header(X_HIBBIZ_TOTAL_ELAPSED_TIME, Long.toString(elapsed))
                .build();
    }
}
package com.hibernatus.hibmobtech.tracking;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by tgo on 24/12/15.
 */
public class TrackingApiKeyInterceptor implements Interceptor {

    static final String API_KEY = "X-Hibbiz-Tracking-Key";
    static final String API_KEY_VALUE = "oFN4Ac98f79oDj91ObyN5TPO3nktKocS";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        builder.addHeader(API_KEY, API_KEY_VALUE);
        return chain.proceed(builder.build());
    }
}

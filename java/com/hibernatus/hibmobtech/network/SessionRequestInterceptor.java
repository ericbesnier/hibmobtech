package com.hibernatus.hibmobtech.network;

import android.util.Log;

import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.List;


/**
 * Created by tgo on 25/10/15.
 */
public class SessionRequestInterceptor implements Interceptor {

    private static final String TAG = SessionRequestInterceptor.class.getName();

    public final static String XSRF_TOKEN = "XSRF-TOKEN";
    public final static String X_XSRF_TOKEN = "X-XSRF-TOKEN";
    public final static String X_AUTH_TOKEN = "x-auth-token";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        HttpCookie xsrf = null;
        CookieStore store = HibmobtechApplication.getCookieStore();
        List<HttpCookie> cookies = store.getCookies();
        for (HttpCookie c : cookies) {
            Log.d(TAG, "Cookie: " + c.toString());
            if (XSRF_TOKEN.equalsIgnoreCase(c.getName())) {
                xsrf = c;
            }
        }

        Request.Builder builder = request.newBuilder();
        if (xsrf != null) {
            Log.d(TAG, "Adding header: " + X_XSRF_TOKEN + ": " + xsrf.getValue());
            builder.addHeader(X_XSRF_TOKEN, xsrf.getValue());
        }
        // depends of Spring configuration: cookie or header for session token
        String authToken = HibmobtechApplication.getDataStore().getAuthToken();
        if (authToken != null) {
            Log.d(TAG, "Adding header: " + X_AUTH_TOKEN + ": " + authToken);
            builder.addHeader(X_AUTH_TOKEN, authToken);
        }

        return chain.proceed(builder.build());
    }
}

package com.hibernatus.hibmobtech.authenticator;

import android.util.Base64;
import android.util.Log;

import java.util.List;

import retrofit.client.Header;

/**
 * Created by tgo on 04/12/15.
 */
public class AuthenticatorUtils {
    public static String getBasicAuthHeader(String login, String password) {
        String creds = String.format("%s:%s", login, password);
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
        return auth;
    }

    public static void logAuthHeaders(String tag, List<Header> headers) {
        for (Header h : headers) {
            Log.v(tag, h.toString());
        }
    }

}

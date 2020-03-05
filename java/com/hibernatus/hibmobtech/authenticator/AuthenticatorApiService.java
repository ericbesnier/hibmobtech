package com.hibernatus.hibmobtech.authenticator;



import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by tgo on 24/10/15.
 */
public interface AuthenticatorApiService {

    final String BASIC_AUTH_HEADER = "Authorization";
    final String X_AUTH_TOKEN = "x-auth-token";
    final String X_HIBBIZ_DEVICE_ID = "X-Hibbiz-Device-Id";
    final String X_HIBBIZ_APP_VERSION = "X-Hibbiz-App-Version";
    /**
     * Asynchronous authentication services
     * @param authorization
     * @param response
     */

    @GET("/user")
    void authenticate(@Header(BASIC_AUTH_HEADER) String authorization,
                      @Header(X_HIBBIZ_DEVICE_ID) String deviceId,
                      @Header(X_HIBBIZ_APP_VERSION) String appVersion,
                      Callback<User> response);
    /**
     * Synchronous authentication
     * @param authorization
     * @return
     */

    @GET("/user")
    User authenticate(@Header(BASIC_AUTH_HEADER) String authorization,
                      @Header(X_HIBBIZ_DEVICE_ID) String deviceId,
                      @Header(X_HIBBIZ_APP_VERSION) String appVersion);

    @GET("/user")
    void whoami(Callback<User> response);

    @POST("/logout")
    void logout(@Body String dummy, Callback<Response> response);

    @GET("/hibbiz-api/ping")
    void ping(Callback<Map<String, String>> callback);
}

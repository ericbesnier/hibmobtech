package com.hibernatus.hibmobtech.tracking;


import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by tgo on 09/12/15.
 */
public interface TrackingApiService {

    /**
     * Synchronous push to the tracking api.
     * Should be used by background service which is running on its own thread.
     *
     * @param deviceId
     * @param segment
     * @return
     */
    @POST("/hibbiz-api/tracking/{deviceId}/tracks")
    TrackingResult pushLocations(@Path("deviceId") String deviceId,
                                 @Body TrackingSegment segment);

    /**
     * Asynchronous push to the tracking api.
     * To be used for single push request.
     *
     * @param deviceId
     * @param segment
     * @param callback
     */
    @POST("/hibbiz-api/tracking/{deviceId}/tracks")
    void pushLocations(@Path("deviceId") String deviceId,
                       @Body TrackingSegment segment,
                       Callback<TrackingResult> callback);
}

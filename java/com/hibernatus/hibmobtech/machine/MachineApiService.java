package com.hibernatus.hibmobtech.machine;


import com.hibernatus.hibmobtech.model.Machine;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by tgo on 24/10/15.
 */
public interface MachineApiService {

    @GET("/hibbiz-api/api/machines/{machineId}")
    void getOneMachine(@Path("machineId") Long machineId,
                       Callback<Machine> callback);

    @GET("/hibbiz-api/api/sites/{currentSiteId}/machines/{machineId}")
    void getSiteMachine(@Path("currentSiteId") Long siteId,
                        @Path("machineId") Long machineId,
                        Callback<Machine> callback);

    @GET("/hibbiz-api/api/machines/")
    void getMachine(@Query("serial") String serialNumber,
                    Callback<List<Machine>> callback);

    @DELETE("/hibbiz-api/api/sites/{currentSiteId}/machines/{machineId}")
    void deleteMachine(@Path("currentSiteId") Long siteId,
                       @Path("machineId") Long machineId,
                       Callback<Void> callback);

    @POST("/hibbiz-api/api/sites/{currentSiteId}/machines")
    void createMachine(@Path("currentSiteId") Long siteId,
                       @Body Machine machine,
                       Callback<Machine> callback);

    @PUT("/hibbiz-api/api/sites/{siteId}/machines/{machineId}")
    void updateMachine(@Path("siteId") Long siteId,
                       @Path("machineId") Long machineId,
                       @Body Machine machine,
                       Callback<Machine> callback);
}

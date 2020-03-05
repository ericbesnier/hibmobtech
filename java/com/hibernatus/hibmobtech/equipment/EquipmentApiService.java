package com.hibernatus.hibmobtech.equipment;

import com.bleau.hibernatus.mob.util.Page;
import com.hibernatus.hibmobtech.model.Equipment;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by tgo on 24/10/15.
 */
public interface EquipmentApiService {

    @GET("/hibbiz-api/api/equipments/{equipmentId}")
    void getOneEquipment(@Path("equipmentId") Long equipmentId,
                         Callback<Equipment> callback);

    @GET("/hibbiz-api/api/equipments")
    void getEquipments(@Query("page") Integer page,
                       @Query("size") Integer size,
                       Callback<Page<Equipment>> callback);

    @GET("/hibbiz-api/api/equipments/search")
    void searchEquipments(@Query("page") Integer page,
                          @Query("size") Integer size,
                          @Query("sdesc") String desc,
                          Callback<Page<Equipment>> callback);
}

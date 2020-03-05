package com.hibernatus.hibmobtech.sparepart;

import com.bleau.hibernatus.mob.util.Page;
import com.hibernatus.hibmobtech.model.SparePart;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.EncodedQueryMap;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by tgo on 24/10/15.
 */
public interface SparePartApiService {

    @GET("/hibbiz-api/api/spareparts/search")
    void searchSpareParts(@Query("page")Integer page,
                          @Query("size")Integer size,
                          @Query("sort")String sort,
                          @EncodedQueryMap Map<String, String> sdesc,
                          Callback<Page<SparePart>> callback);

    @GET("/hibbiz-api/api/spareparts")
    void getSpareParts(@Query("page") Integer page,
                       @Query("size") Integer size,
                       @Query("sort")String sort,
                       Callback<Page<SparePart>> callback);
}

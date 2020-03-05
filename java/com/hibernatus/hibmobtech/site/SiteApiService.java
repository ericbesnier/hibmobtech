package com.hibernatus.hibmobtech.site;



import com.bleau.hibernatus.mob.util.Page;
import com.hibernatus.hibmobtech.model.Invoice;
import com.hibernatus.hibmobtech.model.Machine;
import com.hibernatus.hibmobtech.model.Site;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by tgo on 24/10/15.
 */
public interface SiteApiService {

    @GET("/hibbiz-api/api/sites/{currentSiteId}")
    void getOneSite(@Path("currentSiteId") Long siteId,
                    Callback<Site> callback);

    @GET("/hibbiz-api/api/sites/{currentSiteId}/machines")
    void getSiteMachines(@Path("currentSiteId") Long siteId,
                         Callback<List<Machine>> callback);


    @GET("/hibbiz-api/api/sites/{currentSiteId}/invoices")
    void getSiteInvoices(@Path("currentSiteId") Long siteId,
                         Callback<List<Invoice>> callback);

    // http://52.17.181.31:8080/hibbiz-api/api/sites/2/invoices?p=unpaid
/*    @GET("/hibbiz-api/api/mro-causes")
    void searchCauses(@Query("search") String search,
                      Callback<List<Cause>> callback);*/

    @GET("/hibbiz-api/api/sites/{currentSiteId}/invoices")
    void getSiteUnpaidInvoices(@Path("currentSiteId") Long siteId,
                               @Query("p") String search,
                         Callback<List<Invoice>> callback);

    @GET("/hibbiz-api/api/sites")
    void getSites(@Query("page") Integer page,
                  @Query("size") Integer size,
                  Callback<Page<Site>> callback);

    @GET("/hibbiz-api/api/sites/search")
    void searchSites(@Query("page") Integer page,
                     @Query("size") Integer size,
                     @Query("name") String name,
                     Callback<Page<Site>> callback);

}

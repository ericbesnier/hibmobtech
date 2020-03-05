package com.hibernatus.hibmobtech.mrorequest;


import com.hibernatus.hibmobtech.model.Cause;
import com.hibernatus.hibmobtech.model.ClientSignature;
import com.hibernatus.hibmobtech.model.MroRequest;
import com.hibernatus.hibmobtech.model.Picture;
import com.hibernatus.hibmobtech.model.Task;
import com.hibernatus.hibmobtech.utils.TypedJsonString;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;


/**
 * Created by tgo on 24/10/15.
 */
public interface MroRequestApiService {

    @GET("/hibbiz-api/api/users/{userId}/mro-requests")
    public void getUserMroRequests(@Path("userId") Long userId,
                                   Callback<List<MroRequest>> callback);

    @PUT("/hibbiz-api/api/mro-requests/{mroRequestId}")
    public void putMroRequest(@Path("mroRequestId") Long mroRequestId,
                              @Body MroRequest client,
                              Callback<MroRequest> callback);

    @GET("/hibbiz-api/api/mro-requests/{mroRequestId}")
    public void getMroRequest(@Path("mroRequestId") Long mroRequestId,
                              Callback<MroRequest> callback);

    // /api/sites/{siteId}/mro-requests?status=opened
    @GET("/hibbiz-api/api/users/{userId}/mro-requests")
    public void getMroRequestByStatus(@Path("userId") Long userId,
                                      @Query("status") String status,
                                      Callback<List<MroRequest>> callback);

    @GET("/hibbiz-api/api/mro-requests")
    public void getMroRequestByStatus(@Query("status") String status,
                                      Callback<List<MroRequest>> callback);

    @GET("/hibbiz-api/api/mro-causes")
    public void searchCauses(@Query("search") String search,
                             Callback<List<Cause>> callback);

    @GET("/hibbiz-api/api/mro-tasks")
    public void searchTasks(@Query("search") String search,
                            Callback<List<Task>> callback);

    @GET("/hibbiz-api/api/sites/{currentSiteId}/machines/{machineId}/mro-requests")
    public void getMachineMroRequests(@Path("currentSiteId") Long siteId,
                                      @Path("machineId") Long machineId,
                                      Callback<List<MroRequest>> callback);

    @PUT("/hibbiz-api/api/mro-requests/{mroRequestId}/start")
    public void startMroRequest(@Path("mroRequestId") Long mroRequestId,
                                @Body MroRequest client,
                                Callback<MroRequest> callback);

    @PUT("/hibbiz-api/api/mro-requests/{mroRequestId}/finish")
    public void finishMroRequest(@Path("mroRequestId") Long mroRequestId,
                                 @Body MroRequest client,
                                 Callback<MroRequest> callback);

    @PUT("/hibbiz-api/api/mro-requests/{mroRequestId}/take")
    public void takeMroRequest(@Path("mroRequestId") Long mroRequestId,
                               @Body MroRequest client,
                               Callback<MroRequest> callback);

    @Multipart
    @POST("/hibbiz-api/api/mro-requests/{mroRequestId}/signature_old")
    public void uploadSignatureOld(
            @Path("mroRequestId") Long mroRequestId,
            @Part("name") TypedString name,
            @Part("image") TypedFile image,
            Callback<Void> callback);

    // /api/mro-requests/{mroRequestId}/signature
    @GET("/hibbiz-api/api/mro-requests/{mroRequestId}/signature")
    public void getMroRequestSignature(@Path("mroRequestId") Long mroRequestId,
                                          Callback<ClientSignature> callback);

    // /api/mro-requests/{mroRequestId}/signature/image?type={'jpeg' | 'png'}
    @GET("/hibbiz-api/api/mro-requests/{mroRequestId}/signature/image")
    public void getMroRequestImageFromSignature(@Path("mroRequestId") Long mroRequestId,
                                                @Query("type") String type,
                                                Callback<Response> callback);

    @GET("/hibbiz-api/api/mro-requests/{mroRequestId}/pictures")
    public void getMroRequestPicturesList(@Path("mroRequestId") Long mroRequestId,
                                          Callback<List<Picture>> callback);

    @GET("/hibbiz-api/api/mro-requests/{mroRequestId}/pictures/{pictureId}/image")
    public void getMroRequestImageFromPicture(@Path("mroRequestId") Long mroRequestId,
                                              @Path("pictureId") Long pictureId,
                                              Callback<Response> callback);

    @GET("/hibbiz-api/api/mro-requests/{mroRequestId}/pictures/{pictureId}/thumbnail")
    public void getMroRequestThumbnail(@Path("mroRequestId") Long mroRequestId,
                                       @Path("pictureId") Long pictureId,
                                       Callback<Response> callback);

    /**
     * u p l o a d P i c t u r e
     *
     * Ajouter une photo à une intervention. Le contenu posté est de type "multipart" avec une première partie
     * qui est le nom ou la description de la photo (String), la 2ème partie est la photo (byte[]).
     * Le serveur calcule et stocke une miniature de la photo (thumbnail).
     *
     * /api/mro-requests/{mroRequestId}/pictures
     */

    @Multipart
    @POST("/hibbiz-api/api/mro-requests/{mroRequestId}/pictures_old")
    void uploadPictureWithRetrofit19(
            @Path("mroRequestId") Long mroRequestId,
            @Part("name") TypedString name,
            @Part("file") TypedFile file,
            Callback<Picture> callback);

    // TODO ne fonctionne pas...
    @Multipart
    @Headers({"Content-MroType: multipart/mixed; boundary=hibmob"})
    @POST("/hibbiz-api/api/mro-requests/{mroRequestId}/pictures")
    void uploadPictureWithRetrofit20(
            @Path("mroRequestId") Long mroRequestId,
            @Part(value = "fileinfos", encoding = "UTF-8") TypedJsonString fileinfos,
            @Part("file") TypedFile file,
            Callback<Picture> callback);
}

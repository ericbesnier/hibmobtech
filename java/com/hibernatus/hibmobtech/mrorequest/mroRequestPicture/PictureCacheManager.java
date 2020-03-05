package com.hibernatus.hibmobtech.mrorequest.mroRequestPicture;

import android.content.Context;
import android.util.Log;

import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoPictureCache;
import com.hibernatus.hibmobtech.model.Picture;
import com.hibernatus.hibmobtech.mrorequest.MroRequestApiService;
import com.hibernatus.hibmobtech.network.retrofitError.PictureCacheRetrofitErrorHandler;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import static com.hibernatus.hibmobtech.HibmobtechApplication.MAX_RESEND;

/**
 * Created by Eric on 10/03/2017.
 */

public class PictureCacheManager {
    public static final String TAG = PictureCacheManager.class.getSimpleName();

    protected Context context;
    protected PictureCacheRetrofitErrorHandler pictureCacheRetrofitErrorHandler;

    public PictureCacheManager(Context context, PictureCacheRetrofitErrorHandler pictureCacheRetrofitErrorHandler) {
        this.context = context;
        this.pictureCacheRetrofitErrorHandler = pictureCacheRetrofitErrorHandler;
    }

    public void postAllDataBasePictureCache() throws IOException, ClassNotFoundException {
        Log.e(TAG, "postAllDataBasePictureCache");
        MroRequestApiService service = HibmobtechApplication.getRestClient().getMroRequestService();
        final SQLiteBaseDaoPictureCache sqLiteBaseDaoPictureCache = new SQLiteBaseDaoPictureCache(context);
        sqLiteBaseDaoPictureCache.open();

        ArrayList<Picture> pictureArrayList = new ArrayList<>(sqLiteBaseDaoPictureCache.getAllTablePictures());
        Log.e(TAG, "postAllDataBasePictureCache: pictureArrayList=" + pictureArrayList
        + " pictureArrayList.size()=" + pictureArrayList.size());
        int i = 1;
        for (final Picture picture : pictureArrayList) {
            Log.e(TAG, "postAllDataBasePictureCache:Thread.currentThread()=" + Thread.currentThread()
                    + " picture No [" + i + "]"
                    + " picture.getId()=" + picture.getId()
                    + " picture.getResendNumber()=" + picture.getResendNumber());

            if(picture.getResendNumber() < MAX_RESEND) {
                String pictureString = "postAllDataBasePictureCache: picture=" + picture
                        + " Photo de l'intervention No " + picture.getIdMroRequest()
                        + " description=" + picture.getInfos().getTitle();
                Log.e(TAG, "postAllDataBasePictureCache: " + pictureString);

                byte[] imageByteArray = picture.getImage();
                Log.e(TAG, "postAllDataBasePictureCache: imageByteArray size=" + imageByteArray.length + " bytes (" + imageByteArray.length / 1024 + " Ko)");

                File tempFile = File.createTempFile("_" + picture.getId() + "_", "tmp", null);
                FileUtils.writeByteArrayToFile(tempFile, imageByteArray);

                String titleString = picture.getInfos().getTitle();
                TypedString titleTypedString = new TypedString(new String (titleString.getBytes ("UTF-8"), "iso-8859-1"));
                service.uploadPictureWithRetrofit19(
                        picture.getIdMroRequest(),
                        titleTypedString,
                        //new TypedFile("imageByteArray/jpeg", tempFile),
                        new TypedFile("image/jpeg", tempFile),
                        new Callback<Picture>() {
                            @Override
                            public void success(Picture pictureResponse, Response response) {
/*                                if(!((Activity) context).isFinishing())
                                    Toast.makeText(context,
                                        "Photo transmise au serveur avec succès",
                                        Toast.LENGTH_SHORT).show();*/
                                sqLiteBaseDaoPictureCache.deletePicture(picture);
                            }

                            @Override
                            public void failure(RetrofitError retrofitError) {
                                Log.e(TAG, "postAllDataBasePictureCache: Erreur lors de transmission de la photo au serveur : "
                                        + retrofitError.toString());
                                if(pictureCacheRetrofitErrorHandler != null)
                                pictureCacheRetrofitErrorHandler.managePictureRetrofitError(picture, retrofitError);
                            }
                        });
            }
            else { // after 3 try of sending picture, delete the picture in database
                Log.e(TAG, "postAllDataBasePictureCache: suppression de la photo en erreur: picture.getId()="
                        + picture.getId());
                sqLiteBaseDaoPictureCache.deletePicture(picture);
            }
            i++;
        }
        sqLiteBaseDaoPictureCache.close();
    }
}

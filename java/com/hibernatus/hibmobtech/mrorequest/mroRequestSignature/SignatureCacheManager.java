package com.hibernatus.hibmobtech.mrorequest.mroRequestSignature;

import android.content.Context;
import android.util.Log;

import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoSignatureCache;
import com.hibernatus.hibmobtech.model.ClientSignature;
import com.hibernatus.hibmobtech.mrorequest.MroRequestApiService;
import com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.PictureUtils;
import com.hibernatus.hibmobtech.network.retrofitError.DelayedTimerTaskClientSignatureRetrofitErrorManager;

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
import static com.hibernatus.hibmobtech.HibmobtechApplication.SIGNATURE_WEIGHT;

/**
 * Created by Eric on 11/03/2017.
 */

public class SignatureCacheManager {

    public static final String TAG = SignatureCacheManager.class.getSimpleName();

    protected Context context;
    protected DelayedTimerTaskClientSignatureRetrofitErrorManager delayedTimerTaskClientSignatureRetrofitErrorManager;

    public SignatureCacheManager(Context context, DelayedTimerTaskClientSignatureRetrofitErrorManager delayedTimerTaskClientSignatureRetrofitErrorManager) {
        this.context = context;
        this.delayedTimerTaskClientSignatureRetrofitErrorManager = delayedTimerTaskClientSignatureRetrofitErrorManager;
    }

    public void postAllDataBaseSignatureCache() throws IOException, ClassNotFoundException {
        Log.d(TAG, "postAllDataBaseSignatureCache");
        MroRequestApiService service = HibmobtechApplication.getRestClient().getMroRequestService();
        final SQLiteBaseDaoSignatureCache sqLiteBaseDaoSignatureCache = new SQLiteBaseDaoSignatureCache(context);
        sqLiteBaseDaoSignatureCache.open();

        ArrayList<ClientSignature> clientSignatureArrayList = new ArrayList<>(sqLiteBaseDaoSignatureCache.getAllTableSignatures());
        Log.d(TAG, "postAllDataBaseSignatureCache: signatureArrayList=" + clientSignatureArrayList);
        for (final ClientSignature clientSignature : clientSignatureArrayList) {
            if(clientSignature.getResendNumber() < MAX_RESEND) {
                String signatureString = "postAllDataBaseSignatureCache: signature=" + clientSignature
                        + " Signature de l'intervention No " + clientSignature.getIdMroRequest()
                        + " Nom client=" + clientSignature.getInfos().getSignerName();
                Log.d(TAG, "postAllDataBaseSignatureCache: " + signatureString);

                byte[] signatureByteArray = clientSignature.getSignatureFile();
                Log.d(TAG, "postAllDataBaseSignatureCache: signatureByteArray size="
                        + signatureByteArray.length + " bytes (" + signatureByteArray.length / 1024 + " Ko)");

                File signatureFile = File.createTempFile("_" + clientSignature.getId() + "_", "tmp", null);
                FileUtils.writeByteArrayToFile(signatureFile, signatureByteArray);

                PictureUtils pictureUtils = new PictureUtils(context);
                File signatureCompressedFile = pictureUtils.compressImageFileToImageFile(signatureFile,
                        SIGNATURE_WEIGHT);

                String signerName = clientSignature.getInfos().getSignerName();
                TypedString signerNameTypedString = new TypedString(new String (signerName.getBytes ("UTF-8"), "iso-8859-1"));
                service.uploadSignatureOld(
                        clientSignature.getIdMroRequest(),
                        signerNameTypedString,
                        new TypedFile("signatureByteArray/jpeg", signatureCompressedFile),
                        new Callback<Void>() {
                            @Override
                            public void success(Void aVoid, Response response) {
                                sqLiteBaseDaoSignatureCache.deleteSignature(clientSignature);
                            }

                            @Override
                            public void failure(RetrofitError retrofitError) {
                                delayedTimerTaskClientSignatureRetrofitErrorManager.manageClientSignature(clientSignature, retrofitError);
                            }
                        });
            }
            else{  // after 3 try of sending Signature, delete the Signature in database
                Log.e(TAG, "postAllDataBaseSignatureCache: suppression de la signature en erreur: clientSignature.getId()="
                        + clientSignature.getId());
                sqLiteBaseDaoSignatureCache.deleteSignature(clientSignature);
            }
        }
        sqLiteBaseDaoSignatureCache.close();
    }
}

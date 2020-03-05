package com.hibernatus.hibmobtech.mrorequest.mroRequestLoader;

import android.util.Log;

import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.model.MroRequest;
import com.hibernatus.hibmobtech.mrorequest.MroRequestApiService;

import java.util.List;

import retrofit.Callback;

/**
 * Created by Eric on 23/09/2016.
 */
public class MroRequestFinishedLoader implements MroRequestLoader {
    public static final String TAG = MroRequestFinishedLoader.class.getSimpleName();

    @Override
    public void loadMroRequest(Callback<List< MroRequest >> callback) {
        Log.d(TAG, "loadMroRequest: FINISHED");
        MroRequestApiService service = HibmobtechApplication.getRestClient().getMroRequestService();
        Long userId = HibmobtechApplication.getDataStore().getUserId();
        service.getMroRequestByStatus(userId, "FINISHED", callback);
    }
}

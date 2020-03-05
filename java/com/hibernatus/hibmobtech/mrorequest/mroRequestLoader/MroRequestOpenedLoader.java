package com.hibernatus.hibmobtech.mrorequest.mroRequestLoader;

import android.util.Log;

import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.model.MroRequest;
import com.hibernatus.hibmobtech.mrorequest.MroRequestApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Eric on 23/09/2016.
 */
public class MroRequestOpenedLoader implements MroRequestLoader {
    public static final String TAG = MroRequestOpenedLoader.class.getSimpleName();

    @Override
    public void loadMroRequest(final Callback<List<MroRequest>> callback) {
        Log.d(TAG, "loadMroRequest: OPENED");
        Callback<List<MroRequest>> filteredcallback = new Callback<List<MroRequest>>() {
            @Override
            public void success(List<MroRequest> mroRequests, Response response) {
                if(mroRequests == null){
                    Log.e(TAG, "loadMroRequest: OPENED: ERROR mroRequests == null");
                    return;
                }
                List<MroRequest> mroRequestsFiltered = new ArrayList<>();
                for (MroRequest request : mroRequests){
                    if(request.getRealizedQuotationNumber() == null || request.getRealizedQuotationNumber().isEmpty()){
                        mroRequestsFiltered.add(request);
                    }
                }
                Log.d(TAG, "loadMroRequest: filtered=" + mroRequestsFiltered.size() + "/" + mroRequests.size());
                callback.success(mroRequestsFiltered, response);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "failure : " + error.toString());
                callback.failure(error);
            }
        };
        MroRequestApiService service = HibmobtechApplication.getRestClient().getMroRequestService();
        service.getMroRequestByStatus("OPENED", filteredcallback);
    }
}
package com.hibernatus.hibmobtech.observer;

import android.util.Log;

import com.hibernatus.hibmobtech.network.retrofitError.DisplayRetrofitErrorManager;
import com.hibernatus.hibmobtech.observable.NetworkObservable;

import java.util.Observable;
import java.util.Observer;

import retrofit.RetrofitError;

/**
 * Created by Eric on 03/11/2016.
 */

public class NetworkObserver implements Observer {
    protected static final String TAG = NetworkObserver.class.getSimpleName();
    DisplayRetrofitErrorManager displayRetrofitErrorManager;

    public NetworkObserver(DisplayRetrofitErrorManager displayRetrofitErrorManager) {
        Log.d(TAG, "Constructor: NetworkObserver");
        this.displayRetrofitErrorManager = displayRetrofitErrorManager;
    }

    @Override
    public void update(Observable observable, Object data) {
        if(observable instanceof NetworkObservable){
            RetrofitError retrofitError = ((NetworkObservable) observable).getRetrofitError();
            Log.d(TAG, "update: retrofitError=" + retrofitError);
            displayRetrofitErrorManager.manageRetrofitError(retrofitError);
        }
    }
}

package com.hibernatus.hibmobtech.observable;

import java.util.Observable;

import retrofit.RetrofitError;

/**
 * Created by Eric on 01/11/2016.
 */

public class NetworkObservable extends Observable {
    protected static final String TAG = NetworkObservable.class.getSimpleName();

    private RetrofitError retrofitError;

    public RetrofitError getRetrofitError() {
        return retrofitError;
    }

    public void setRetrofitError(RetrofitError retrofitError) {
        this.retrofitError = retrofitError;
        setChanged();
        notifyObservers(this.retrofitError);
    }
}

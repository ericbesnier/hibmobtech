package com.hibernatus.hibmobtech.network;

import com.hibernatus.hibmobtech.network.restError.RestError;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by tgo on 25/10/15.
 */
public abstract class RestCallback<T> implements Callback<T> {
    public abstract void failure(RestError restError);

    @Override
    public void failure(RetrofitError error) {
        RestError restError = (RestError) error.getBodyAs(RestError.class);

        if (restError != null) {
            failure(restError);
        }
        else {
            failure(new RestError(error.getMessage()));
        }
    }
}
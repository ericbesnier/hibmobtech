package com.hibernatus.hibmobtech.mrorequest.mroRequestLoader;

import com.hibernatus.hibmobtech.model.MroRequest;

import java.util.List;

import retrofit.Callback;

/**
 * Created by Eric on 22/09/2016.
 */

public interface MroRequestLoader
{
    void loadMroRequest(Callback<List<MroRequest>> callback);
}

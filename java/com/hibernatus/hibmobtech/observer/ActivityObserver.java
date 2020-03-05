package com.hibernatus.hibmobtech.observer;

import android.content.Context;
import android.util.Log;

import com.hibernatus.hibmobtech.observable.ActivityObservable;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Eric on 03/11/2016.
 */

public class ActivityObserver implements Observer {
    protected static final String TAG = ActivityObserver.class.getSimpleName();

    Context context;

    public ActivityObserver(Context context) {
        Log.d(TAG, "Constructor: ActivityObserver");
        this.context = context;
    }

    @Override
    public void update(Observable observable, Object data) {
        if(observable instanceof ActivityObservable){
            Log.d(TAG, "update: activity=" +((ActivityObservable) observable).getActivity()
                    + " activityState=" + ((ActivityObservable) observable).getActivityState());
        }
    }
}

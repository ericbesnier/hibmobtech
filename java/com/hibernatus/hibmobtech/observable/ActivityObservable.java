package com.hibernatus.hibmobtech.observable;

import android.app.Activity;

import java.util.Observable;

/**
 * Created by Eric on 01/11/2016.
 */

public class ActivityObservable extends Observable {
    protected static final String TAG = ActivityObservable.class.getSimpleName();

    public enum State {
        ON_CREATE,
        ON_START,
        ON_RESTART,
        ON_RESUME,
        ON_PAUSE,
        ON_STOP,
        ON_DESTROY
    }

    protected Activity activity;
    protected State activityState;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        setChanged();
        notifyObservers(this.activity);
    }

    public State getActivityState() {
        return activityState;
    }

    public void setActivityState(State activityState) {
        this.activityState = activityState;
        setChanged();
        notifyObservers(this.activityState);
    }

    public String toString(){
        return "Activity (activity=" + activity + " activityObservable=" + activityState +")";
    }
}

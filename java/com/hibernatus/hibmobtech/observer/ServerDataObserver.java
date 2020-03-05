package com.hibernatus.hibmobtech.observer;

/**
 * Created by Eric on 14/06/2017.
 */

public interface ServerDataObserver {
    // method to update the observer, used by subject
    public void update();

    // attach with subject to observe
    public void attachServerDataObservable(ServerDataObservable observable);
}

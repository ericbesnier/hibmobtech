package com.hibernatus.hibmobtech.observer;

/**
 * Created by Eric on 14/06/2017.
 */

public interface ServerDataObservable {
    // methods to register and unregister observers
    public void register(ServerDataObserver obj);
    public void unregister(ServerDataObserver obj);

    // method to notify observers of change
    public void notifyObservers();

    // method to get updates from subject
    public Object getUpdate(ServerDataObserver obj);
}

package com.hibernatus.hibmobtech.observable;

import java.util.Observable;

/**
 * Created by Eric on 03/11/2016.
 */

// c l a s s   S e t t i n g s O b s e r v a b l e
// -----------------------------------------------------

public class SettingsObservable extends Observable {
    protected static final String TAG = SettingsObservable.class.getSimpleName();
    boolean locationEnabled = true;
    boolean airPlaneModeOn = false;

    public boolean isLocationEnabled() {
        return locationEnabled;
    }

    public void setLocationEnabled(boolean locationEnabled) {
        this.locationEnabled = locationEnabled;
        setChanged();
        notifyObservers(this.locationEnabled);
    }

    public boolean isAirPlaneModeOn() {
        return airPlaneModeOn;
    }

    public void setAirPlaneModeOn(boolean airPlaneModeOn) {
        this.airPlaneModeOn = airPlaneModeOn;
        setChanged();
        notifyObservers(this.locationEnabled);
    }
}

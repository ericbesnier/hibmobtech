package com.hibernatus.hibmobtech.tracking;

import android.location.Location;

import com.hibernatus.hibmobtech.model.TrackingLocation;

/**
 * Created by tgo on 01/01/16.
 */
public class Tracking {
    public static TrackingLocation fromLocation(Location location) {
        return new TrackingLocation(location.getLatitude(), location.getLongitude(), location.getTime(),
                location.getAltitude(), location.getAccuracy(), location.getSpeed(), location.getBearing());
    }
}

package com.hibernatus.hibmobtech.tracking;


import com.hibernatus.hibmobtech.model.TrackingLocation;

import java.util.Collections;
import java.util.List;

/**
 * Created by tgo on 23/12/15.
 */
public class TrackingSegment {

    String deviceId;
    List<TrackingLocation> locations = Collections.emptyList();
    int numberOfLocations;

    public TrackingSegment() {
    }

    public TrackingSegment(String deviceId, List<TrackingLocation> locations) {
        this.deviceId = deviceId;
        this.locations = locations;
        this.numberOfLocations = locations.size();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<TrackingLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<TrackingLocation> locations) {
        this.locations = locations;
    }

    public int getNumberOfLocations() {
        return numberOfLocations;
    }

    public void setNumberOfLocations(int numberOfLocations) {
        this.numberOfLocations = numberOfLocations;
    }
}

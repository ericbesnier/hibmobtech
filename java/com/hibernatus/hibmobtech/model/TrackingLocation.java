package com.hibernatus.hibmobtech.model;

/**
 * Created by tgo on 23/12/15.
 */
public class TrackingLocation {
    double latitude;
    double longitude;
    long time;
    double altitude;
    float accuracy;
    float speed;
    float bearing;

    public TrackingLocation() {
    }

    public TrackingLocation(double latitude, double longitude, long time,
                            double altitude, float accuracy, float speed, float bearing) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.altitude = altitude;
        this.accuracy = accuracy;
        this.speed = speed;
        this.bearing = bearing;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }
}

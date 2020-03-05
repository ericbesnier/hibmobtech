package com.hibernatus.hibmobtech.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by Eric on 09/11/2015.
 */
public class LatLng implements Parcelable {
    public static final String TAG = LatLng.class.getSimpleName();
    private double lat;
    private double lng;

    public LatLng() {
    }

    protected LatLng(Parcel in) {
        lat = in.readDouble();
        lng = in.readDouble();
    }

    public static final Creator<LatLng> CREATOR = new Creator<LatLng>() {
        @Override
        public LatLng createFromParcel(Parcel in) {
            return new LatLng(in);
        }

        @Override
        public LatLng[] newArray(int size) {
            return new LatLng[size];
        }
    };

    public double getLat() {
        Log.i(TAG, "getLat: lat=" + lat);
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        Log.i(TAG, "getLng: lng=" + lng);
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }
}

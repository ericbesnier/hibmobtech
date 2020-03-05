package com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.MozaiqueOnPhone;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Eric on 20/02/2017.
 */

public class PicassoPhonePicture implements Comparable<PicassoPhonePicture>{
    protected static final String TAG = PicassoPhonePicture.class.getSimpleName();

    private int id;
    private String albumName;
    private String photoUri;
    private String timeStamp;

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName( String name ) {
        this.albumName = name;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri( String photoUri ) {
        this.photoUri = photoUri;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public int compareTo(PicassoPhonePicture another) {
        Log.d(TAG, "compareTo: this.getTimeStamp()=" + this.getTimeStamp()
                + " another.getTimeStamp()=" + another.getTimeStamp());
        // exple: Sat Feb 11 11:47:51 GMT+01:00 2017
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.US);
        Date start = null;
        Date end = null;
        try {
            if(this.getTimeStamp() != null)
                start = sdf.parse(this.getTimeStamp());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            if(another.getTimeStamp() != null)
                end = sdf.parse(another.getTimeStamp());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return end.compareTo(start);
    }
}
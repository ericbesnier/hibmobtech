package com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.MozaiqueOnPhone;

import java.util.Vector;

/**
 * Created by Eric on 20/02/2017.
 */

public class PicassoPhoneAlbum {

    private int id;
    private String name;
    private String coverUri;
    private Vector<PicassoPhonePicture> albumPhotos;

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getCoverUri() {
        return coverUri;
    }

    public void setCoverUri( String albumCoverUri ) {
        this.coverUri = albumCoverUri;
    }

    public Vector<PicassoPhonePicture> getAlbumPhotos() {
        if ( albumPhotos == null ) {
            albumPhotos = new Vector<>();
        }
        return albumPhotos;
    }

    public void setAlbumPhotos( Vector<PicassoPhonePicture> albumPhotos ) {
        this.albumPhotos = albumPhotos;
    }
}

package com.hibernatus.hibmobtech.model;

import android.util.Log;

/**
 * Created by Eric on 01/06/2016.
 */
public class Picture {
    public static final String TAG = Picture.class.getSimpleName();

    public static final String MRO_PICTURE_CODE = "mroPictureCode";

    private Long id;
    private byte[] thumbnail;
    private byte[] image;
    private Long idMroRequest;
    private PictureInfos infos;
    private String parentKey;
    private String pathHdpi;
    private int resendNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Long getIdMroRequest() {
        return idMroRequest;
    }

    public void setIdMroRequest(Long idMroRequest) {
        this.idMroRequest = idMroRequest;
    }

    public PictureInfos getInfos() {
        return infos;
    }

    public void setInfos(PictureInfos infos) {
        this.infos = infos;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public String getPathHdpi() {
        return pathHdpi;
    }

    public void setPathHdpi(String pathHdpi) {
        this.pathHdpi = pathHdpi;
    }

    public int getResendNumber() {
        return resendNumber;
    }

    public void setResendNumber(int resendNumber) {
        Log.d(TAG, "setResendNumber: id="+ id + " resendNumber=" + resendNumber);
        this.resendNumber = resendNumber;
    }

    /*
DataBase bean
    INTEGER PRIMARY KEY ID;
    INTEGER ID_MRO_REQUEST;
    BLOB IMAGE;
    BLOB THUMBNAIL;
    TEXT TITLE;
    TEXT DESCRIPTION;
    INTEGER TIME;

Server bean
Picture
    private long id;
    private String parentKey;
    private PictureInfos infos;
    private byte[] image;
    private byte[] thumbnail;
PictureInfos
    private String title;
    private String description;
    private Long time;
    */


}

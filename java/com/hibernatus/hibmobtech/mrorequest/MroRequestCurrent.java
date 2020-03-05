package com.hibernatus.hibmobtech.mrorequest;

import android.util.Log;
import android.widget.ImageView;

import com.hibernatus.hibmobtech.model.Cause;
import com.hibernatus.hibmobtech.model.MroRequest;
import com.hibernatus.hibmobtech.model.Picture;
import com.hibernatus.hibmobtech.model.SpareParts;
import com.hibernatus.hibmobtech.model.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Eric on 15/01/2016.
 */
public class MroRequestCurrent {
    public static final String TAG = MroRequestCurrent.class.getSimpleName();
    private MroRequest mroRequestCurrent;
    private MroRequestCurrent(){}
    private ImageView signatureImageView = null;
    private boolean isMroRequestCheckable = false;
    private Cause additionalCause = null;
    private Task additionalTask = null;
    private SpareParts additionalSpareParts = null;
    private Picture additionalPicture = null;
    private File signatureFile;

    /** Instance unique pré-initialisée */
    private static MroRequestCurrent INSTANCE = new MroRequestCurrent();



    // O t h e r s   m e t h o d s
    // ---------------------------

    public static MroRequestCurrent getInstance()
    {
        Log.d(TAG, "getInstance INSTANCE=" + INSTANCE);
        return INSTANCE;
    }

    public Picture getPicture(String path) {
        Log.d(TAG, "getPictureBitmap:path=" + path);
        ArrayList<Picture> pictureArrayList = mroRequestCurrent.getOperation().getPictures();
        Iterator<Picture> iterator = pictureArrayList.iterator();
        Picture picture = null;
        while (iterator.hasNext()) {
            picture = iterator.next();
            if(picture.getPathHdpi().equals(path)){
                break;
            }
        }
        return picture;
    }

    public boolean isMroRequestCheckable() {
        Log.d(TAG, "isMroRequestCheckable=" + isMroRequestCheckable);
        return isMroRequestCheckable;
    }

    public boolean isCurrentRequest() {
        return mroRequestCurrent != null;
    }


    // G e t t e r s   a n d   S e t t e r s
    //
    public File getSignatureFile() {
        return signatureFile;
    }

    public void setSignatureFile(File signatureFile) {
        this.signatureFile = signatureFile;
    }

    public SpareParts getAdditionalSpareParts() {
        return additionalSpareParts;
    }

    public void setAdditionalSpareParts(SpareParts additionalSpareParts) {
        this.additionalSpareParts = additionalSpareParts;
    }

    public Picture getAdditionalPicture() {
        return additionalPicture;
    }

    public void setAdditionalPicture(Picture additionalPicture) {
        this.additionalPicture = additionalPicture;
    }

    public Task getAdditionalTask() {
        return additionalTask;
    }

    public void setAdditionalTask(Task additionalTask) {
        this.additionalTask = additionalTask;
    }

    public Cause getAdditionalCause() {
        return additionalCause;
    }

    public void setAdditionalCause(Cause additionalCause) {
        this.additionalCause = additionalCause;
    }

    public void setIsMroRequestCheckable(boolean isMroRequestCheckable) {
        Log.d(TAG, "setIsMroRequestCheckable:isMroRequestCheckable=" + isMroRequestCheckable);
        this.isMroRequestCheckable = isMroRequestCheckable;
    }

    public ImageView getSignatureImageView() {
        return signatureImageView;
    }

    public void setSignatureImageView(ImageView signatureImageView) {
        this.signatureImageView = signatureImageView;
    }

    public MroRequest getMroRequestCurrent() {
        if(mroRequestCurrent == null){
            Log.e(TAG, "getMroRequestCurrent: mroRequestCurrent is NULL");
            return null;
        }
        else {
            Log.d(TAG, "getMroRequestCurrent:currentRequest: id="
                    + mroRequestCurrent.getId()
                    + " machineDetailSiteNameTextView="
                    + mroRequestCurrent.getSite().getName()
                    + " isMroRequestCheckable="
                    + isMroRequestCheckable);
            return mroRequestCurrent;
        }
    }

    public void setMroRequestCurrent(MroRequest mroRequestCurrent) {
        Log.d(TAG, "setMroRequestCurrent: mroRequestCurrent" + mroRequestCurrent);
        this.mroRequestCurrent = mroRequestCurrent;
        if(mroRequestCurrent == null){
            Log.e(TAG, "setMroRequestCurrent: mroRequestCurrent is NULL");
            return;
        }
        Log.d(TAG, "setMroRequestCurrent:currentRequest: id="
                + this.mroRequestCurrent.getId()
        + " machineDetailSiteNameTextView="
        + this.mroRequestCurrent.getSite().getName()
                + " isMroRequestCheckable="
        + isMroRequestCheckable);
    }
}


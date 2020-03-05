package com.hibernatus.hibmobtech.site;

import android.util.Log;

import com.hibernatus.hibmobtech.model.Site;

/**
 * Created by Eric on 15/01/2016.
 */
public class SiteCurrent {
    public static final String TAG = SiteCurrent.class.getSimpleName();
    private Site siteCurrent;
    private SiteCurrent(){}
    private boolean isSiteCheckable = false;

    private static SiteCurrent INSTANCE = new SiteCurrent();

    public void initSiteCurrent() {
        Log.i(TAG, "initSiteCurrent");
        siteCurrent = null;
        isSiteCheckable = false;
    }

    public boolean isSiteCheckable() {
        Log.i(TAG, "isSiteCheckable=" + isSiteCheckable);
        return isSiteCheckable;
    }

    public void setIsSiteCheckable(boolean isSiteCheckable) {
        Log.i(TAG, "setIsSiteCheckable:isSiteCheckable=" + isSiteCheckable);
        this.isSiteCheckable = isSiteCheckable;
    }

    public boolean isCurrentSite() {
        return siteCurrent != null;
    }

    public static SiteCurrent getInstance()
    {	return INSTANCE;
    }

    public Site getSiteCurrent() {
        if(siteCurrent != null)
            Log.i(TAG, "getSiteCurrent:currentRequest: id="
                    + siteCurrent.getId()
                    + " isSiteCheckable="
                    + isSiteCheckable);
        else{
            Log.i(TAG, "getSiteCurrent:currentRequest == null !!!!!!!!!!");
        }
        return siteCurrent;
    }

    public void setSiteCurrent(Site siteCurrent) {
        Log.d(TAG, "!!!! setSiteCurrent:siteCurrent=" + siteCurrent);
        this.siteCurrent = siteCurrent;
        if(siteCurrent != null)
            Log.d(TAG, "setSiteCurrent:currentRequest: id="
                    + this.siteCurrent.getId()
                    + " isSiteCheckable="
                    + isSiteCheckable);
    }
}


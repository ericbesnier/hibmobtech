package com.hibernatus.hibmobtech.observer;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.hibernatus.hibmobtech.mrorequest.MroRequestActivity;

/**
 * Created by Eric on 25/01/2017.
 */

public class HibmobtechContentObserver extends ContentObserver {

    public static final String TAG = MroRequestActivity.class.getSimpleName();

    private ContentObserverCallback contentObserverCallback;

    public HibmobtechContentObserver(ContentObserverCallback contentObserverCallback) {
        super(null); // null is totally fine here
        this.contentObserverCallback = contentObserverCallback;
    }

    public HibmobtechContentObserver(Handler handler) {
        super(handler);
        Log.d(TAG, "HibmobtechContentObserver");
    }

    @Override
    public void onChange(boolean selfChange) {
        Log.d(TAG, "HibmobtechContentObserver : onChange : selfChange=" + selfChange);
        this.onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Log.d(TAG, "HibmobtechContentObserver : onChange : selfChange=" + selfChange + " uri=" + uri);
        // this is NOT UI thread, this is a BACKGROUND thread
        contentObserverCallback.onUpdatedContent();
    }
}

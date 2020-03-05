package com.hibernatus.hibmobtech.mrorequest;

import android.util.Log;

/**
 * Created by Eric on 16/11/2016.
 */

public class MroRequestsFragment {
    public static final String TAG = MroRequestsFragment.class.getSimpleName();

    public MroRequestListFragment fragment;
    public String title;
    public String fragmentId;

    public MroRequestsFragment(MroRequestListFragment fragment, String title, String fragmentId) {
        Log.d(TAG, "MroRequestsFragment: fragment=" + fragment + " title=" + title + " fragmentType=" + fragmentId);
        this.fragment = fragment;
        this.title = title;
        this.fragmentId = fragmentId;
    }
}


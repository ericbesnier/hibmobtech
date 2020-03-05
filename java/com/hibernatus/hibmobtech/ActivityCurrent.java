package com.hibernatus.hibmobtech;

import android.app.Activity;
import android.util.Log;


/**
 * Created by Eric on 15/01/2016.
 */
public class ActivityCurrent {
    public static final String TAG = ActivityCurrent.class.getSimpleName();
    private Activity activityCurrent;
    private String activityName;
    private ActivityCurrent(){}
    private boolean isCurrentActivity = false;


    /** Instance unique pré-initialisée */
    private static ActivityCurrent INSTANCE = new ActivityCurrent();

    public void initActivityCurrent() {
        Log.i(TAG, "initActivityCurrent");
        activityCurrent = null;
        isCurrentActivity = false;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public boolean isCurrentActivity() {
        return isCurrentActivity;
    }

    public void setIsCurrentActivity(boolean isCurrentActivity) {
        this.isCurrentActivity = isCurrentActivity;
    }

    /** Point d'accès pour l'instance unique du singleton */
    public static ActivityCurrent getInstance()
    {	return INSTANCE;
    }

    public Activity getActivityCurrent() {
        if(activityCurrent != null) {
            if (activityCurrent != null) {
                Log.i(TAG, "getActivityCurrent:currentActivity="
                        + this.activityCurrent);
            }
        }
        return activityCurrent;
    }

    public void setActivityCurrent(Activity activityCurrent) {
        this.activityCurrent = activityCurrent;
        if (activityCurrent != null) {
            Log.i(TAG, "setActivityCurrent:currentActivity="
                    + this.activityCurrent);
        }
    }
}


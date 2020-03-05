package com.hibernatus.hibmobtech.machine;

import android.util.Log;

import com.hibernatus.hibmobtech.model.Machine;

/**
 * Created by Eric on 15/01/2016.
 */
public class MachineCurrent {
    public static final String TAG = MachineCurrent.class.getSimpleName();
    private Machine machineCurrent;
    private MachineCurrent(){}

    private static MachineCurrent INSTANCE = new MachineCurrent();

    public void initMachineCurrent() {
        Log.i(TAG, "initMachineCurrent");
        machineCurrent = null;
    }

    public boolean isCurrentMachine() {
        return machineCurrent != null;
    }

    public static MachineCurrent getInstance()
    {	return INSTANCE;
    }

    public Machine getMachineCurrent() {
        if(machineCurrent != null) {
            Log.i(TAG, "getMachineCurrent:currentRequest: id="
                    + machineCurrent.getId()
                    + " machineDetailSiteNameTextView="
                    + machineCurrent.getSite().getName());
        }
        return machineCurrent;
    }

    public void setMachineCurrent(Machine machineCurrent) {
        this.machineCurrent = machineCurrent;
        if (machineCurrent != null) {
            Log.i(TAG, "setMachineCurrent:currentRequest: id="
                    + this.machineCurrent.getId()
                    + " machineDetailSiteNameTextView="
                    + this.machineCurrent.getSite().getName());
        }
    }
}


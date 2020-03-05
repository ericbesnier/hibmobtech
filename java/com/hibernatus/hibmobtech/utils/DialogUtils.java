package com.hibernatus.hibmobtech.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.hibernatus.hibmobtech.HibmobtechApplication;

/**
 * Created by Eric on 04/11/2016.
 */

public class DialogUtils {
    protected static final String TAG = DialogUtils.class.getSimpleName();

    public AlertDialog createAlertDialogOKCancel(String message,
                                                 DialogInterface.OnClickListener okListener,
                                                 DialogInterface.OnClickListener cancelListener) {
        Log.d(TAG, "createAlertDialogOKCancel: message=" + message);

        AlertDialog alertDialog = null;
        Activity currentActivity = HibmobtechApplication.getInstance().getCurrentActivity();

        if (currentActivity != null) {
            Log.d(TAG, "createAlertDialogOKCancel: création de la boîte de dialogue");
            AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
            builder.setMessage(message);
            builder.setPositiveButton("OK", okListener);
            builder.setNegativeButton("Cancel", cancelListener);
            alertDialog = builder.create();
        }
        else{
            Log.e(TAG, "currentActivity == null");
        }
        return alertDialog;
    }

    public AlertDialog createAlertDialogOK(String message, DialogInterface.OnClickListener okListener) {
        Log.d(TAG, "createAlertDialogOK: message=" + message);

        AlertDialog alertDialog = null;
        Activity currentActivity = HibmobtechApplication.getInstance().getCurrentActivity();

        if (currentActivity != null) {
            Log.d(TAG, "createAlertDialogOK: création de la boîte de dialogue");
            AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
            builder.setMessage(message);
            builder.setPositiveButton("OK", okListener);
            alertDialog = builder.create();
        }
        else{
            Log.e(TAG, "currentActivity == null");
        }
        return alertDialog;
    }

    public AlertDialog createAlertDialogOK(Context context, String message, DialogInterface.OnClickListener okListener) {
        Log.d(TAG, "createAlertDialogOK: création de la boîte de dialogue");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton("OK", okListener);
        return builder.create();
    }
}

package com.hibernatus.hibmobtech.observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.hibernatus.hibmobtech.observable.SettingsObservable;
import com.hibernatus.hibmobtech.utils.DialogUtils;
import com.hibernatus.hibmobtech.utils.NotificationUtils;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Eric on 03/11/2016.
 */

public class SettingsObserver implements Observer {
    protected static final String TAG = SettingsObserver.class.getSimpleName();

    protected Context context;
    protected DialogUtils dialogUtils;
    protected NotificationUtils notificationUtils;
    protected AlertDialog alertDialog;

    public SettingsObserver(Context context) {
        Log.d(TAG, "Constructor: SettingsObserver");
        this.context = context;
        dialogUtils = new DialogUtils();
        notificationUtils = new NotificationUtils();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof SettingsObservable) {

            // on vérifie la localisation
            if(((SettingsObservable) observable).isLocationEnabled()) { // localisation activée
                Log.d(TAG, "onUpdatedContent: localisation activée !!!");
                notificationUtils.launchNotification(context);

            }
            else { // localisation désactivée
                Log.d(TAG, "onUpdatedContent: localisation désactivée !!!");
                notificationUtils.cancelNotification(context);
            }

            // on vérifie le mode avion
            if(((SettingsObservable) observable).isAirPlaneModeOn()) { // Mode avion activé
                Log.d(TAG, "onUpdatedContent: Mode avion activé !!!");
                String message = "Pour permettre le bon fonctionnement de l'application, vous devez désactiver le mode avion";

                if (context != null) {
                    alertDialog = createAlertDialog(context, message);
                    if (!alertDialog.isShowing()) {
                        Log.d(TAG, "onUpdatedContent: affichage de la boîte de dialogue");
                        if(!((Activity) context).isFinishing())
                            alertDialog.show();
                    }
                }
            }
            else { // Mode avion désactivé
                Log.d(TAG, "onUpdatedContent: Mode avion désactivé !!!");
            }
        }
    }

    public AlertDialog createAlertDialog(Context context, String message) {
        Log.d(TAG, "createAlertDialog");

        return alertDialog = dialogUtils.createAlertDialogOKCancel(message,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Log.d(TAG, "onUpdatedContent: onOKListener");
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        SettingsObserver.this.context.startActivity(intent);
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Log.d(TAG, "onUpdatedContent: onCancelListener");
                    }
                });
    }
}

package com.hibernatus.hibmobtech.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.hibernatus.hibmobtech.HibmobtechApplication.LOCATION_NOTIFICATION_ID;

/**
 * Created by Eric on 04/11/2016.
 */

public class NotificationUtils {
    protected static final String TAG = NotificationUtils.class.getSimpleName();

    public void launchNotification(Context context) {
        Log.d(TAG, "launchNotification");
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("' le' dd/MM/yyyy ' à ' HH:mm:ss");
        String currentDateAndTime = sdf.format(new Date());

        Notification notification = new Notification.Builder(context)
                .setContentTitle("Vous êtes géolocalisé")
                .setContentText(currentDateAndTime)
                .setSmallIcon(R.drawable.ic_settings_input_antenna_white_18px)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(new Notification.BigTextStyle().bigText(currentDateAndTime)).build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        HibmobtechApplication.getInstance().getNotificationManager().notify(LOCATION_NOTIFICATION_ID, notification);
    }

    public void cancelNotification(Context context) {
        Log.d(TAG, "cancelNotification");
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(LOCATION_NOTIFICATION_ID);
    }
}

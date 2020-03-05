package com.hibernatus.hibmobtech.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;

import com.hibernatus.hibmobtech.HibmobtechApplication;

/**
 * Created by Eric on 31/01/2016.
 */


public class AlarmUtils {
    public static final String TAG = AlarmUtils.class.getSimpleName();

    //private final static Logger LOGGER = LoggerUtils.getLogger(AlarmUtils.class);

    public static void scheduleRtcWakeUpAlarm(long triggerAtTime, PendingIntent operation) {
        getAlarmManager().set(AlarmManager.RTC_WAKEUP, triggerAtTime, operation);
        Log.i(TAG, "scheduleRtcWakeUpAlarm: RTC_WAKEUP " + triggerAtTime);
    }

    public static void scheduleRtcAlarm(long triggerAtTime, PendingIntent operation) {
        getAlarmManager().set(AlarmManager.RTC, triggerAtTime, operation);
        log("RTC", triggerAtTime);
    }

    public static void scheduleElapsedRealtimeWakeUpAlarm(long triggerAtTime, PendingIntent operation) {
        getAlarmManager().set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, operation);
        log("ELAPSED_REALTIME_WAKEUP", triggerAtTime);
    }

    public static void scheduleElapsedRealtimeAlarm(long triggerAtTime, PendingIntent operation) {
        getAlarmManager().set(AlarmManager.ELAPSED_REALTIME, triggerAtTime, operation);
        log("ELAPSED_REALTIME", triggerAtTime);
    }

    private static void log(String alarmType, long triggerAtTime) {
/*        log("Created " + alarmType + " alarm for "
                + DateUtils.formatDateTime(DateUtils.getDate(triggerAtTime)));*/
    }

    public static void cancelAlarm(PendingIntent operation) {
        getAlarmManager().cancel(operation);
    }

    private static AlarmManager getAlarmManager() {
        Context context = HibmobtechApplication.getInstance();
        return (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    }
}


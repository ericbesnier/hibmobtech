package com.hibernatus.hibmobtech.utils;

import android.util.Log;

/**
 * Created by Eric on 16/06/2017.
 */

public class ThreadUtils {
    public static final String TAG = ThreadUtils.class.getSimpleName();

    public static long getThreadId() {
        Thread t = Thread.currentThread();
        return t.getId();
    }

    public static String getThreadSignature() {
        Thread thread = Thread.currentThread();
        long id = thread.getId();
        String name = thread.getName();
        long priority = thread.getPriority();
        String groupName = thread.getThreadGroup().getName();
        return ("[Thread:" + name + "/" + id + "/" + priority + "/" + groupName + "]");
    }

    public static void logThreadSignature() {
        String threadSignature = getThreadSignature();
        Log.d(TAG, "logThreadSignature: threadSignature=" +  threadSignature);
    }

    public static void sleepForInSecs(int secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException x) {
            throw new RuntimeException("interrupted", x);
        }
    }
}

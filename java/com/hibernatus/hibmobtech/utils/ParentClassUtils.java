package com.hibernatus.hibmobtech.utils;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.Stack;

/**
 * Created by Eric on 20/11/2015.
 */
public class ParentClassUtils {
    public static final String TAG = "ParentClassUtils";

    public static void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup)child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }

    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

    public static Stack<Class<?>> parentClassStack = new Stack<Class<?>>();

    public static void displayParentStack(){
        int i = 0;
        int sizeOfStack = parentClassStack.size();
        while (i < sizeOfStack){
            Log.i(TAG, "displayParentStack: class=" + parentClassStack.get(i));
            i++;
        }
    }

    public static Class searchableParentClass;

}

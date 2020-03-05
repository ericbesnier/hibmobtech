package com.hibernatus.hibmobtech;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Eric on 18/02/2017.
 */

public class HibmobtechBroadcastReceiver extends BroadcastReceiver{
    public static final String TAG = HibmobtechBroadcastReceiver.class.getSimpleName();

    public HibmobtechBroadcastReceiver() {
        Log.d(TAG, "HibmobtechBroadcastReceiver");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: intent.getAction()="
                + intent.getAction());
        if(!((Activity) context).isFinishing())
            Toast.makeText(context, TAG + "onReceive: intent.getAction()="
                    + intent.getAction(), Toast.LENGTH_SHORT).show();
    }

}

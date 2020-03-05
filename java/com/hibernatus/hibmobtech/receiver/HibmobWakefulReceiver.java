package com.hibernatus.hibmobtech.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.hibernatus.hibmobtech.tracking.HibmobTrackingService;

/**
 * Created by Eric on 28/06/2017.
 */

public class HibmobWakefulReceiver extends WakefulBroadcastReceiver {
    protected static final String TAG = "HibmobWakefulReceiver";

    public static final String PROCESS_RESPONSE = "HibmobWakefulReceiver.PROCESS_RESPONSE";

    @Override
    public void onReceive(Context context, Intent intent) {

        String responseString = intent.getStringExtra(HibmobTrackingService.RESPONSE_STRING);
        String reponseMessage = intent.getStringExtra(HibmobTrackingService.RESPONSE_MESSAGE);

        // Start the service, keeping the device awake while the service is
        // launching. This is the Intent to deliver to the service.
        // HERE IS WHERE YOU RECEIVE THE INFORMATION FROM THE INTENTSERVICE, FROM HERE YOU CAN START AN ACTIVITY OR WHATEVER YOU AIM
        Log.d(TAG, "onReceive: responseString=" + responseString
                + "reponseMessage=" + reponseMessage);

        Toast.makeText(context, "HibmobWakefulReceiver: onReceive: responseString=" + responseString
                + "reponseMessage=" + reponseMessage, Toast.LENGTH_SHORT).show();

        Intent service = new Intent(context, HibmobTrackingService.class);
        startWakefulService(context, service);
    }
}

package com.marcosdiez.spectrumanalyzer.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.marcosdiez.spectrumanalyzer.Globals;

/**
 * Created by Marcos on 05-May-15.
 */
public class BootReceiver extends BroadcastReceiver {

    public static final String TAG = "XB-BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // just make sure we are getting the right intent (better safe than sorry)
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.d(TAG, "DataPublishedBackgroundService.onReceive");
            if(!Globals.working_for_real){
                Log.d(TAG, "DataPublishedBackgroundService.onReceive -> DISABLED");
                return;
            }
            DataPublishedBackgroundService.startMeAsAService(context);
            Log.d(TAG, "Service Started!");
        } else {
            Log.e(TAG, "Received unexpected intent " + intent.toString());
        }
    }
}
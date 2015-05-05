package com.marcosdiez.spectrumanalyzer.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.marcosdiez.spectrumanalyzer.Settings;

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
            if(!Settings.working_for_real){
                Log.d(TAG, "DataPublishedBackgroundService.onReceive -> DISABLED");
                return;
            }
            Intent serviceIntent = new Intent(context, DataPublishedBackgroundService.class);
            context.startService(serviceIntent);
            Log.d(TAG, "Service Started!");
        } else {
            Log.e(TAG, "Received unexpected intent " + intent.toString());
        }
    }
}
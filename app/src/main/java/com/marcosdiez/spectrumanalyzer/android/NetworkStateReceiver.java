package com.marcosdiez.spectrumanalyzer.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.marcosdiez.spectrumanalyzer.Globals;


/**
 * Created by Marcos on 17-Jan-15.
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    private static final String TAG = "XB-NetworkStateReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Globals.offline = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE);
        if (Globals.offline) {
            Log.d(TAG, "We are offline.");
        } else {
            Log.d(TAG, "We are connected again. Let's send data to the server.");
            // do something()


        }
    }
}
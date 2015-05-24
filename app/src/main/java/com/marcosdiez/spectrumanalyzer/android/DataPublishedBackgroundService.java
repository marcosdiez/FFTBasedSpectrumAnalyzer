package com.marcosdiez.spectrumanalyzer.android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.marcosdiez.spectrumanalyzer.Globals;
import com.marcosdiez.spectrumanalyzer.util.SendToServer;


public class DataPublishedBackgroundService extends Service {
    private static String TAG = "XB-DataPublishedBackgroundService";

    public static void startMeAsAService(Context packageContext)    {
        Intent serviceIntent = new Intent(packageContext, DataPublishedBackgroundService.class);
        packageContext.startService(serviceIntent);
    }


    public DataPublishedBackgroundService() {
    }

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        void sleeper(int seconds) {
            try {
                Thread.sleep(seconds * 1000);
            } catch (InterruptedException e) {
            }
        }

        @Override
        public void handleMessage(Message msg) {
            SendToServer serverSender = new SendToServer();
            Globals.there_is_data_to_be_sent = true;
            if(Globals.working_for_real) {
                while (true) {
                    serverSender.publishData();
                    sleeper(Globals.seconds_to_sleep_between_publish_attempt);
                }
            }
//                // Stop the service using the startId, so that we don't stop
//                // the service in the middle of handling another job
//                stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        if (Globals.isContextNull()) {
            Globals.setContext(this);
        }
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "service starting");
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}
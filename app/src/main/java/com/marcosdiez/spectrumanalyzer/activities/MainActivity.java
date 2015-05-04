package com.marcosdiez.spectrumanalyzer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.marcosdiez.spectrumanalyzer.Globals;
import com.marcosdiez.spectrumanalyzer.R;
import com.marcosdiez.spectrumanalyzer.android.DataPublishedBackgroundService;
import com.marcosdiez.spectrumanalyzer.util.GpsStuff;
import com.marcosdiez.spectrumanalyzer.util.SaveToDatabase;
import com.marcosdiez.spectrumanalyzer.util.SendToServer;

public class MainActivity extends Activity {
    private static final String TAG = "XB-MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Globals.setContext(this);

        Log.d(TAG, "INIT");

        GpsStuff.getMyGpsStuff().refreshLocation();

        SaveToDatabase x = new SaveToDatabase();
        x.insertSensorIsDisabled();
        x.insertSensorIsEnabled();
//        x.insertEvent("marcos", "diez");
//
//        x.insertEvent("marcos2", "diez2");
//
//        x.insertEvent("marcos", "diez");
//
//        x.insertEvent("marcos3", "diez3");
//        x.insertEvent("marcos", "diez");
        Log.d(TAG, "Data Created");

        SendToServer xx = new SendToServer();
        xx.publishData();
        Log.d(TAG, "now the background service");

        Intent intent = new Intent(this, DataPublishedBackgroundService.class);
        startService(intent);

        Log.d(TAG, "Done");
    }



}

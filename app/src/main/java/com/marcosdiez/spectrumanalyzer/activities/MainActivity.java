package com.marcosdiez.spectrumanalyzer.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.marcosdiez.spectrumanalyzer.Globals;
import com.marcosdiez.spectrumanalyzer.R;
import com.marcosdiez.spectrumanalyzer.util.GpsStuff;
import com.marcosdiez.spectrumanalyzer.util.SaveToDatabase;

public class MainActivity extends Activity {
    private static final String TAG = "XB-MainActivity";

    private Button getButton(int id, View.OnClickListener c) {
        Button theButton = (Button) findViewById(id);
        theButton.setOnClickListener(c);
        return theButton;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Globals.setContext(this);

        Log.d(TAG, "INIT");

        GpsStuff.getMyGpsStuff().refreshLocation();

        final SaveToDatabase std = new SaveToDatabase();
        getButton(R.id.buttonSensorDisable, new View.OnClickListener() {
            public void onClick(View v) {
                std.insertSensorIsDisabled();
            }
        });

        getButton(R.id.buttonSensorEnabled, new View.OnClickListener() {
            public void onClick(View v) {
                std.insertSensorIsEnabled();
            }
        });



//        SaveToDatabase x = new SaveToDatabase();
//        x.insertSensorIsDisabled();
//        x.insertSensorIsEnabled();
////        x.insertEvent("marcos", "diez");
////
////        x.insertEvent("marcos2", "diez2");
////
////        x.insertEvent("marcos", "diez");
////
////        x.insertEvent("marcos3", "diez3");
////        x.insertEvent("marcos", "diez");
//        Log.d(TAG, "Data Created");
//
//        SendToServer xx = new SendToServer();
//        xx.publishData();
//        Log.d(TAG, "now the background service");
////
//        Intent intent = new Intent(this, DataPublishedBackgroundService.class);
//        startService(intent);
//
        Log.d(TAG, "Done");
    }



}

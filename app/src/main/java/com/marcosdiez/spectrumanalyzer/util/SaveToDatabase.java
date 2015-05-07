package com.marcosdiez.spectrumanalyzer.util;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.marcosdiez.spectrumanalyzer.db.DatabaseManager;
import com.marcosdiez.spectrumanalyzer.db.SignalsDbHelper;


/**
 * Created by Marcos on 17-Jan-15.
 */
public class SaveToDatabase {
    private static final String TAG = "XB-SignalParser";
    public static final String PORTA = "porta";

    public void sendAudioFrequency(int value){
        insertEvent(PORTA, value);
    }

    public void insertSensorIsEnabled() {
        insertEvent(PORTA, 1);
    }

    public void insertSensorIsDisabled() {
        insertEvent(PORTA, 0);
    }

    private void insertEvent(String theEventName, int eventValue) {
        insertEvent(theEventName, eventValue + "");
    }


    private void insertEvent(String theEventName, String eventValue) {
        long unixTime = System.currentTimeMillis() / 1000L;
        double lat = GpsStuff.getMyGpsStuff().lat;
        double lng = GpsStuff.getMyGpsStuff().lng;

        PowerInformation p = new PowerInformation();

        ContentValues values = new ContentValues();
        values.put(SignalsDbHelper.SIGNALS_ROW_EVENT_NAME, theEventName);
        values.put(SignalsDbHelper.SIGNALS_ROW_EVENT_VALUE, eventValue);
        values.put(SignalsDbHelper.SIGNALS_ROW_TIMESTAMP_EVENT_RECEIVED, unixTime);
        values.put(SignalsDbHelper.SIGNALS_ROW_LAT, lat);
        values.put(SignalsDbHelper.SIGNALS_ROW_LNG, lng);
        values.put(SignalsDbHelper.SIGNALS_ROW_BATTERY_LEVEL, p.getBatteryPercent());
        values.put(SignalsDbHelper.SIGNALS_ROW_IS_CHARGING, p.isCharging());

        values.put(SignalsDbHelper.SIGNALS_ROW_SENT_TO_SERVER, false);

        Log.d(TAG, "event_name=[" + theEventName + "] event_value=[" + eventValue +
                "] lat=[" + lat + "] lng=[" + lng + "] is_charging=[" + p.isCharging() +
                "] batt=[" + p.getBatteryPercent() + "]");

        saveEventInTheDatabase(values);
    }

    private void saveEventInTheDatabase(ContentValues values) {
        Log.d(TAG, "saveEvent");
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.insert(SignalsDbHelper.SIGNALS_DATA_TABLE_NAME,
                null,
                values
        );
        DatabaseManager.getInstance().closeDatabase();
    }

}

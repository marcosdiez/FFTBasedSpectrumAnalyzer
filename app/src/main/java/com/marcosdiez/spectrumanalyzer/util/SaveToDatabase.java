package com.marcosdiez.spectrumanalyzer.util;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.marcosdiez.spectrumanalyzer.Globals;
import com.marcosdiez.spectrumanalyzer.db.DatabaseManager;
import com.marcosdiez.spectrumanalyzer.db.SignalsDbHelper;


/**
 * Created by Marcos on 17-Jan-15.
 */
public class SaveToDatabase {
    private static final String TAG = "XB-SignalParser";

//    public void parse(String msg) {
//        Log.d(TAG, "SingalParse.parse [" + msg + "]: TODO");
//    }


    public void insertSensorIsEnabled(){
        insertEvent("porta", 0);

    }

    public void insertSensorIsDisabled(){
        insertEvent("porta", 1);
    }

    private void insertEvent(String theEventName, int eventValue){
        insertEvent(theEventName, eventValue + "");
    }

    private void insertEvent(String theEventName, String eventValue) {
        long unixTime = System.currentTimeMillis() / 1000L;
        double lat = GpsStuff.getMyGpsStuff().lat;
        double lng = GpsStuff.getMyGpsStuff().lng;

        ContentValues values = new ContentValues();
        values.put(SignalsDbHelper.SIGNALS_ROW_EVENT_NAME, theEventName);
        values.put(SignalsDbHelper.SIGNALS_ROW_EVENT_VALUE, eventValue);
        values.put(SignalsDbHelper.SIGNALS_ROW_TIMESTAMP_EVENT_RECEIVED, unixTime);
        values.put(SignalsDbHelper.SIGNALS_ROW_LAT, lat);
        values.put(SignalsDbHelper.SIGNALS_ROW_LNG, lng);
        values.put(SignalsDbHelper.SIGNALS_ROW_SENT_TO_SERVER, false);

        Log.d(TAG, "event_name=[" + theEventName + "] event_value=[" + eventValue + "] lat=[" + lat + "] lng=[" + lng + "]");
        saveEventInTheDatabase(values);
        Globals.there_is_data_to_be_sent=true;
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

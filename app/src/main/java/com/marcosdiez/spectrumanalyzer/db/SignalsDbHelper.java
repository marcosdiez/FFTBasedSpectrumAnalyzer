package com.marcosdiez.spectrumanalyzer.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.marcosdiez.spectrumanalyzer.Globals;


/**
 * Created by Marcos on 12/19/13.
 */
public class SignalsDbHelper extends SQLiteOpenHelper {
    public static final String SIGNALS_DATA_TABLE_NAME = "SignalData";


    public static final String SIGNALS_ROW_ID = "id";
    public static final String SIGNALS_ROW_EVENT_NAME = "event_name";
    public static final String SIGNALS_ROW_EVENT_VALUE = "event_value";
    public static final String SIGNALS_ROW_TIMESTAMP_EVENT_RECEIVED = "timestamp_event_received";
    public static final String SIGNALS_ROW_LAT = "lat";
    public static final String SIGNALS_ROW_LNG = "lng";
    public static final String SIGNALS_ROW_SENT_TO_SERVER = "sent_to_server";


    private static final String SIGNALS_DATA_TABLE_CREATE =

            "CREATE TABLE '" + SIGNALS_DATA_TABLE_NAME + "' " +
                    "(" +
                    " '" + SIGNALS_ROW_ID + "' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
                    ", '" + SIGNALS_ROW_EVENT_NAME + "' TEXT NOT NULL" +
                    ", '" + SIGNALS_ROW_EVENT_VALUE + "' TEXT NOT NULL" +
                    ", '" + SIGNALS_ROW_TIMESTAMP_EVENT_RECEIVED + "' INTEGER NOT NULL default '0'" +
                    ", '" + SIGNALS_ROW_LAT + "' REAL NOT NULL default '0'" +
                    ", '" + SIGNALS_ROW_LNG + "' REAL NOT NULL default '0'" +
                    ", '" + SIGNALS_ROW_SENT_TO_SERVER + "' BOOLEAN NOT NULL default 0 CHECK (sent_to_server  IN (0,1) )" +
//                    "',timestamp_sent_to_server' INTEGER NOT NULL default '0'" +
                    ");";
    private static final String TAG = "XB-SignalsDbHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "signals.sqlite3";


    public SignalsDbHelper() {
        super(Globals.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SIGNALS_DATA_TABLE_CREATE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading SQLite DB from " + oldVersion + " to " + newVersion);
        int currentVersion = oldVersion;


//        if(currentVersion < 10){
//            currentVersion = 10;
//            Log.d(TAG, "Upgrading SQLite DB to " + 10);
//            sqLiteDatabase.execSQL("ALTER TABLE PortalData ADD COLUMN 'address' TEXT default NULL;");
//        }
//
//
//        Log.d(TAG, "Upgrading SQLite DB to " + DATABASE_VERSION);
//        sqLiteDatabase.execSQL("UPDATE PortalData SET 'address' = null WHERE address = '';");

        Log.d(TAG, "Upgrading SQLite DB completed...");
    }
}

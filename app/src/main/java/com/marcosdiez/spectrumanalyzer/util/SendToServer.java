package com.marcosdiez.spectrumanalyzer.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.marcosdiez.spectrumanalyzer.Globals;
import com.marcosdiez.spectrumanalyzer.Settings;
import com.marcosdiez.spectrumanalyzer.db.DatabaseManager;
import com.marcosdiez.spectrumanalyzer.db.SignalsDbHelper;


/**
 * Created by Marcos on 17-Jan-15.
 */
public class SendToServer {
    private static final String TAG = "XB-SendToServer";

    public synchronized void publishData() {
        if (!Globals.there_is_data_to_be_sent) {
            Log.d(TAG, "There is no data to be sent... bailing.");
            return;
        }
        if (Globals.offline) {
            Log.d(TAG, "We are offline. There is no reason to try to in publish data.");
            return;
        }
        Log.d(TAG, "Starting to publish data to Server.");
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor queryCursor = queryForNonPublishedItems(db);
        int numRows = queryCursor.getCount();
        Log.d(TAG, "There are " + numRows + " rows to send to server");
        sendCursorDataToServer(db, queryCursor, numRows);
        queryCursor.close();
        DatabaseManager.getInstance().closeDatabase();
        Log.d(TAG, "End of publishData()");
    }

    private void sendCursorDataToServer(SQLiteDatabase db, Cursor queryCursor, int numRows) {
        queryCursor.moveToFirst();
        int sentCounter = 0;
        while (queryCursor.isAfterLast() == false) {
            if (Globals.offline) {
                Log.d(TAG, "Aborting sending " + numRows + "rows to the server.");
                return;
            }
            if (publish(queryCursor)) {
                sentCounter++;
                updateDb(db, queryCursor);
            }
            queryCursor.moveToNext();
        }
        Log.d(TAG, "I sent " + sentCounter + " of " + numRows + " to the server.");
    }

    private boolean publish(Cursor queryCursor) {
        String url = generateServerUrl(queryCursor);
        if (Settings.working_for_real) {
            boolean result = Misc.makeHttpRequest(url);
            Log.d(TAG, "ServerURL: " + result + " [" + url + "]");
            return result;
        } else {
            Log.d(TAG, "ServerURL: WE_ARE_OFFLINE [" + url + "]");
            return true;
        }
    }

    private String generateServerUrl(Cursor queryCursor) {
        StringBuilder output = new StringBuilder(500);

        output.append(Settings.server_header);

        // event_name = event_value
        output.append("?__device=" + Misc.getAndroidId());
        output.append("&" + queryCursor.getString(1) + "=" + queryCursor.getString(2));
        output.append("&__time=" + Misc.epochToDate(queryCursor.getLong(3)));
        output.append("&lat=" + queryCursor.getDouble(4));
        output.append("&long=" + queryCursor.getDouble(5));
        output.append("&batt=" + queryCursor.getInt(6));
        output.append("&charging=" + queryCursor.getInt(7));

        String outputUrl = output.toString();
        return outputUrl;
    }

    private Cursor queryForNonPublishedItems(SQLiteDatabase db) {

        String[] db_FROM = {
                SignalsDbHelper.SIGNALS_ROW_ID, // needed to later mark as viewed
                SignalsDbHelper.SIGNALS_ROW_EVENT_NAME,
                SignalsDbHelper.SIGNALS_ROW_EVENT_VALUE,
                SignalsDbHelper.SIGNALS_ROW_TIMESTAMP_EVENT_RECEIVED,
                SignalsDbHelper.SIGNALS_ROW_LAT,
                SignalsDbHelper.SIGNALS_ROW_LNG,
                SignalsDbHelper.SIGNALS_ROW_BATTERY_LEVEL,
                SignalsDbHelper.SIGNALS_ROW_IS_CHARGING
        };

        String where = "sent_to_server=?";
        String[] whereArgs = new String[]{"0"};

        String sortOrder = "id ASC";

        Cursor queryCursor = db.query(SignalsDbHelper.SIGNALS_DATA_TABLE_NAME, db_FROM, where, whereArgs, null, null, sortOrder);
        return queryCursor;
    }

    private void updateDb(SQLiteDatabase db, Cursor queryCursor) {
        int row_id = queryCursor.getInt(0);

        ContentValues values = new ContentValues();
        values.put(SignalsDbHelper.SIGNALS_ROW_SENT_TO_SERVER, true);

        // updating row
        String where = SignalsDbHelper.SIGNALS_ROW_ID + "=?";
        String[] whereValues = new String[]{String.valueOf(row_id)};

        db.update(SignalsDbHelper.SIGNALS_DATA_TABLE_NAME, values, where, whereValues);
    }
}

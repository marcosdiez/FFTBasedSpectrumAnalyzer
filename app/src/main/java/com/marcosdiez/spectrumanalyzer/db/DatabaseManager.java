package com.marcosdiez.spectrumanalyzer.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Marcos on 17-Jan-15.
 */
public class DatabaseManager {
    // https://github.com/dmytrodanylyk/dmytrodanylyk/blob/gh-pages/articles/Concurrent%20Database%20Access.md

    private AtomicInteger mOpenCounter = new AtomicInteger();

    private static DatabaseManager instance;
    private static SQLiteOpenHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    private static synchronized void initializeInstance(SQLiteOpenHelper helper) {
        instance = new DatabaseManager();
        mDatabaseHelper = helper;
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            initializeInstance(new SignalsDbHelper());
        }

        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        if(mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        if(mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            mDatabase.close();
        }
    }
}
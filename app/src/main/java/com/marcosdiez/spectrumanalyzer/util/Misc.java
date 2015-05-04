package com.marcosdiez.spectrumanalyzer.util;

import android.util.Log;

import com.marcosdiez.spectrumanalyzer.Globals;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Marcos on 04-May-15.
 */
public class Misc {
    // all the orphan functions with nowhere to go...


    public static final String TAG = "XB-Misc";

    public static boolean makeHttpRequest(String URL) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(URL));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                String responseString = out.toString();
                out.close();
                //..more logic
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch( java.io.IOException e){
            Log.d(TAG, e.toString());
            return false;
        }
        return true;
    }

    public static String epochToDate(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date theDate = new Date(timestamp*1000);
        String dateString = dateFormat.format(theDate);

        return dateString;
    }

    public static String getAndroidId() {
        return android.provider.Settings.Secure.getString(Globals.getContext().getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }
}

package com.marcosdiez.spectrumanalyzer;

import android.content.Context;
import android.os.Environment;

//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationServices;

/**
 * Created by Marcos Diez on 2015-01-17.
 */
public class Globals {
    public static boolean offline = false;
    public static boolean there_is_data_to_be_sent = false;

    private static Context context = null;

    public static final int frequency_limit = 4000; // Hz
    public static int min_frequency = 1000;    // Hz
    public static int max_frequency = 3000;   // Hz
    public static int time_of_generated_sound = 1000; // ms - miliseconds
    public static final int time_of_generated_sound_max = 5000; // ms - miliseconds

    public static int words = 4;
    public static final int words_max = 10;

    public static int num_samples = 50;
    public static final int num_samples_max = 100;

    public static int minumum_audio_volume_to_be_considered = 0; // no unity
    public static final int minumum_audio_volume_to_be_considered_max = 10; // no unity


    public static String toastMsg = null; // for background tasks to send Toasts.


    // boilerplate

    public static Context getContext() {
        if (context == null) {
            throw new NullPointerException("Context");
        }
        return context;
    }

    public static void setContext(Context context) {
        Globals.context = context;
    }

    public static boolean isContextNull() {
        return context == null;
    }


// private static GoogleApiClient googleApiClient = null;
//    public synchronized static GoogleApiClient getGoogleApiClient(){
//        if(googleApiClient == null){
//            googleApiClient = new GoogleApiClient.Builder(getContext())
//                    .addApi(LocationServices.API)
//                    .build();
//            googleApiClient.disconnect();
//
//        }
//        return googleApiClient;
//    }


    public static String getPublicWritableFolder() {
        return Environment.getExternalStorageDirectory() + "/Android/data/" +
                Globals.getContext().getPackageName();
    }
}

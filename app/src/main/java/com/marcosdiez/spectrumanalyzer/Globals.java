package com.marcosdiez.spectrumanalyzer;

import android.content.Context;
import android.os.Environment;

import com.marcosdiez.spectrumanalyzer.audio.Interpreter;

//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationServices;

/**
 * Created by Marcos Diez on 2015-01-17.
 */
public class Globals {


    // settings
    // dev stuff
    public static final boolean working_for_real = false;


    // Server Stuff
    // http://freesense.no-ip.org:8080/ScadaBR/httpds?__device=blah&porta=1
    public static final String server_protocol = "http";
    public static final String server = "freesense.no-ip.org";
    public static final int server_port = 8080;
    public static final String server_path = "ScadaBR/httpds";
    public static final String server_header = server_protocol + "://" + server + ":" + server_port + "/" + server_path;
    public static final int seconds_to_sleep_between_publish_attempt = 5;


    public static boolean offline = false;
    public static boolean there_is_data_to_be_sent = false;

    private static Context context = null;

    public static final long miliseconds_between_beeps_for_end_of_message =2000;
    public static final int frequency_limit = 4000; // Hz
    public static int min_frequency = 1000;    // Hz
    public static int max_frequency = 3000;   // Hz
    public static int time_of_generated_sound = 1000; // ms - miliseconds
    public static final int time_of_generated_sound_max = 5000; // ms - miliseconds

    public static int words = 5;
    public static final int words_max = 10;
    public static int smallascii_words_per_character = 3; // this should be automatic

    public static int num_samples = 4;
    public static final int num_samples_max = 100;

    public static int minumum_audio_volume_to_be_considered = 2; // no unity
    public static final int minumum_audio_volume_to_be_considered_max = 10; // no unity


    public static Interpreter interpreter = new Interpreter();
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


    public static String getPublicWritableFolder() {
        return Environment.getExternalStorageDirectory() + "/Android/data/" +
                Globals.getContext().getPackageName();
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
}

package com.marcosdiez.spectrumanalyzer.audio.Player;

import android.util.Log;

import com.marcosdiez.spectrumanalyzer.Globals;
import com.marcosdiez.spectrumanalyzer.audio.Communication;

/**
 * Created by Marcos on 29-Mar-15.
 */
public class AudioIoPlayer implements Runnable {
    final static String TAG = "XB-AudioIoPlayer";
    TonePlayer tonePlayer = new TonePlayer();
    String message = null;

    public void setMessage(String msg) {
        this.message = msg;
    }

    public void dumpState() {
        Log.d(TAG, "Freq(Hz): " + Globals.min_frequency + "/" + Globals.max_frequency +
                " time(Ms): " + Globals.time_of_generated_sound + " words: " + Globals.words);
    }

    public void run() {
        if (message == null) {
            sendAllBeep();
        } else {
            Communication.player(message, tonePlayer);
        }
    }

    public void sendAllBeep() {
        dumpState();

        // Communication.player("hello;", this);
        int delta = Math.abs(Globals.max_frequency - Globals.min_frequency) / Globals.words;
        int frequency = Globals.min_frequency;
        for (int i = 0; i <= Globals.words; i++) {
            tonePlayer.processFrequency(frequency);
            frequency += delta;
        }
    }

}

package com.marcosdiez.spectrumanalyzer.audio;

import android.util.Log;

import com.marcosdiez.spectrumanalyzer.Globals;

/**
 * Created by Marcos on 29-Mar-15.
 */
public class AudioIoPlayer implements Runnable, Communication.Beeper {
    final static String TAG = "AudioIoPlayer";
    TonePlayer tonePlayer = new TonePlayer();

    public void dumpState() {
        Log.d(TAG, "Freq(Hz): " + Globals.min_frequency + "/" + Globals.max_frequency +
                " time(Ms): " + Globals.time_of_generated_sound + " words: " + Globals.words);
    }

    public void run() {
        send();
    }

    public void send() {
        dumpState();

        Communication.player("hello;", this);
//        int delta = Math.abs(maxFrequencyHz - minFrequencyHz);
//        int frequencyIncrement = delta / words;
//        int frequency = minFrequencyHz;
//        for (int i = 0; i < words; i++) {
//            playSound(frequency, timeMs);
//            frequency += frequencyIncrement;
//        }
    }

    public void beepChar(char c) {
        int delta = Math.abs(Globals.max_frequency - Globals.min_frequency);
        int frequencyIncrement = delta / Globals.words;

        int beep_value = Integer.parseInt(""+c);

        int frequency = Globals.min_frequency + frequencyIncrement * beep_value;
        playSound(frequency, Globals.time_of_generated_sound);
    }

    public void beepWordSeparator() {
        playSound(100, Globals.time_of_generated_sound);
    }

    void playSound(int frequency, int playTimeInMilliseconds) {
        String msg = "Freq (Hz): " + frequency + " time (ms):" + playTimeInMilliseconds;
        Log.d(TAG, msg);
        tonePlayer.play(playTimeInMilliseconds, frequency);
    }



}

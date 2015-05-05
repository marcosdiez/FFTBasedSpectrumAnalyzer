package com.marcosdiez.spectrumanalyzer.audio;

import android.util.Log;

import com.marcosdiez.spectrumanalyzer.R;

/**
 * Created by Marcos on 29-Mar-15.
 */
public class AudioIoPlayer implements Runnable, Communication.Beeper {
    final static String TAG = "AudioIoPlayer";
    int minFrequencyHz;
    int maxFrequencyHz;
    int timeMs;
    int words;
    TonePlayer tonePlayer = new TonePlayer();

    public void dumpState() {
        Log.d(TAG, "Freq(Hz): " + minFrequencyHz + "/" + maxFrequencyHz +
                " time(Ms): " + timeMs + " words: " + words);
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
        int delta = Math.abs(maxFrequencyHz - minFrequencyHz);
        int frequencyIncrement = delta / words;

        int beep_value = Integer.parseInt(""+c);

        int frequency = minFrequencyHz + frequencyIncrement * beep_value;
        playSound(frequency, timeMs);
    }

    public void beepWordSeparator() {
        playSound(100, timeMs);
//        try {
//            Thread.sleep(timeMs);
//        } catch (InterruptedException e) {
//        }
    }

    void playSound(int frequency, int playTimeInMilliseconds) {
        String msg = "Freq (Hz): " + frequency + " time (ms):" + playTimeInMilliseconds;
        Log.d(TAG, msg);
        tonePlayer.play(playTimeInMilliseconds, frequency);
    }

    public int getMinFrequencyHz() {
        return minFrequencyHz;
    }

    public void setMinFrequencyHz(int minFrequencyHz) {
        this.minFrequencyHz = minFrequencyHz;
    }

    public int getMaxFrequencyHz() {
        return maxFrequencyHz;
    }

    public void setMaxFrequencyHz(int maxFrequencyHz) {
        this.maxFrequencyHz = maxFrequencyHz;
    }

    public int getTimeMs() {
        return timeMs;
    }

    public void setTimeMs(int timeMs) {
        this.timeMs = timeMs;
    }

    public int getWords() {
        return words;
    }

    public void setWords(int words) {
        if (words == 0) {
            words = 1;
        }
        this.words = words;
    }


    public void setSeekerValue(int id, int value) {
        // guess why we do this ? Java does not have function pointers

        switch (id) {
            case (R.id.seek_min_frequency):
                setMinFrequencyHz(value);
                break;
            case (R.id.seek_max_frequency):
                setMaxFrequencyHz(value);
                break;
            case (R.id.seek_time):
                setTimeMs(value);
                break;
            case (R.id.seek_words):
                setWords(value);
                break;
            case (R.id.seek_filter):
            case (R.id.seek_iteration):
                // we the player does not use neither this values
                break;
            default:
                Log.d(TAG, "setSeekerValue -> Invalid ID:" + id);
                // I should raise an exception, but that would
                // be TOO hard and involve 50 try/catches
                break;
        }
    }


}

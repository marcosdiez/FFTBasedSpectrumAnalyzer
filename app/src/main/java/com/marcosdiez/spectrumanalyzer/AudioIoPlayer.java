package com.marcosdiez.spectrumanalyzer;

import android.util.Log;

/**
 * Created by Marcos on 29-Mar-15.
 */
public class AudioIoPlayer implements Runnable {
    final static String TAG = "AudioIoPlayer";
    // private static final ExecutorService threadPool = Executors.newCachedThreadPool();
    int minFrequencyHz;
    int maxFrequencyHz;
    int timeMs;
    int words;
    int filter;
    int iterations;
    TonePlayer tonePlayer = new TonePlayer();

    public void dumpState() {
        Log.d(TAG, "Freq(Hz): " + minFrequencyHz + "/" + maxFrequencyHz +
                " time(Ms): " + timeMs + " words: " + words +
                " filter :" + filter + " iterations: " + iterations);
    }

    public void run() {
        send();
    }

    public void send() {
        dumpState();
        int delta = Math.abs(maxFrequencyHz - minFrequencyHz);
        int frequencyIncrement = delta / words;
        int frequency = minFrequencyHz;
        for (int i = 0; i < words; i++) {
            playSound(frequency, timeMs);
            frequency += frequencyIncrement;
        }
    }

    void playSound(int frequency, int playTimeInMiliseconds) {
        String msg = "Freq (Hz): " + frequency + " time (ms):" + playTimeInMiliseconds;
        Log.d(TAG, msg);
        tonePlayer.play(playTimeInMiliseconds, frequency);
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

    public int getFilter() {
        return filter;
    }

    public void setFilter(int filter) {
        this.filter = filter;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
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
                setFilter(value);
                break;
            case (R.id.seek_iteration):
                setIterations(value);
                break;
            default:
                Log.d(TAG, "setSeekerValue -> Invalid ID:" + id);
                // I should raise an exception, but that would
                // be TOO hard and involve 50 try/catches
                break;
        }
    }
}
